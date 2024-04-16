package main.game.systems;


import main.game.stores.pools.ColorPalette;

import main.constants.Direction;
import main.constants.Settings;
import main.game.components.*;
import main.game.components.Statistics;
import main.game.components.Vector;
import main.game.components.tile.Tile;
import main.game.components.tile.TileUtils;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.asset.AssetPool;
import main.game.stores.pools.ability.Ability;
import main.game.systems.combat.CombatEvent;
import main.game.systems.combat.DamageCalculator;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.EmeritusUtils;
import main.utils.MathUtils;
import main.utils.StringFormatter;

import java.awt.Color;
import java.util.*;

public class CombatSystem extends GameSystem {

    private final Map<Entity, CombatEvent> mQueue = new HashMap<>();
    private final SplittableRandom mRandom = new SplittableRandom();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(CombatSystem.class);
    private GameModel gameModel;

    @Override
    public void update(GameModel model, Entity unit) {
        // 1. if the current unit is not in queue, skip
        CombatEvent event = mQueue.get(unit);
        gameModel = model;
        if (event == null) { return; }

        // 2. wait next loop to check if attacker has finished animating
        boolean isFastForwarding = Settings.getInstance().getBoolean(Settings.GAMEPLAY_FAST_FORWARD_TURNS);
        AnimationMovementTrack track = unit.get(AnimationMovementTrack.class);
        if (!isFastForwarding && track.isMoving()) { return; }

        // 3. Finish the combat by applying the damage to the defending units. Remove from queue
        finishCombat(model, unit, event);
        mQueue.remove(unit);
    }

    public boolean startCombat(GameModel model, Entity user, Ability ability, Set<Entity> attackAt) {

        // 0. if the ability can't affect the user, remove if available
        if (!ability.hasTag(Tags.CAN_FRIENDLY_FIRE)) {
            attackAt.remove(user.get(MovementManager.class).currentTile);
        }
        if (attackAt.isEmpty()) { return false; }

        // 1. Check that unit has resources for ability
        if (ability.canNotPayCosts(user)) { return false; }

        // 2. Animate based on the abilities range
        applyAnimationToUser(user, ability, attackAt);

        // 3. Draw ability name to screen
        announceWithFloatingText(model, ability.name, user, ColorPalette.getColorOfAbility(ability));

        // 4. Cache the combat state...
        mQueue.put(user, new CombatEvent(user, ability, attackAt));

        return true;
    }

    private void finishCombat(GameModel model, Entity attacker, CombatEvent event) {
        logger.debug("{} initiates combat", attacker);

        // 0. Pay the ability costs
        payAbilityCosts(event);

//        applyEffects(model, event.actor, event, event.action.tagsToUserMap.entrySet());

        applyAnimationsToTargets(model, event.ability, event.tiles);

        // 3. Execute a hit on all selected defenders
        for (Entity entity : event.tiles) {
            Tile tile = entity.get(Tile.class);

            if (tile.isNotNavigable()) { tile.removeStructure(); }
            if (tile.mUnit == null) { continue; }

            boolean hit = MathUtils.passesChanceOutOf100(event.ability.accuracy);
            logger.debug("{} uses {} on {}", attacker, event.ability.name, entity);

            // 4. Attack if possible
            if (hit) {
                executeHit(model, attacker, event, tile.mUnit);
            } else {
                executeMiss(model, attacker, event, tile.mUnit);
            }
        }
        Statistics stats = attacker.get(Statistics.class);
//        if (stats.toExperience(random.nextInt(1, 5))) {
//            announceWithFloatingText(gameModel, "Lvl Up!", attacker, Color.WHITE);
//        }

        logger.debug("{} finishes combat", attacker);
    }

    private  void executeMiss(GameModel model, Entity attacker, CombatEvent event, Entity defender) {
        Vector vector = attacker.get(Animation.class).getVector();
        model.system.floatingText.floater("Missed!", vector, ColorPalette.getColorOfAbility(event.ability));
        logger.info("{} misses {}", attacker, defender);
    }

    private  void executeHit(GameModel model, Entity attacker, CombatEvent event, Entity defender) {

        // 0. Setup
        Statistics defendingStatistics = defender.get(Statistics.class);
        Vector defendingVector = defender.get(Animation.class).getVector();
        Statistics attackingStatistics = attacker.get(Statistics.class);
        Vector attackingVector = attacker.get(Animation.class).getVector();

        // 1. Calculate damage
        DamageCalculator report = new DamageCalculator(model, attacker, event.ability, defender);

        for (String resource : report.getDamageKeys()) {
            int damage = (int) report.getDamage(resource);
            int critical = (int) report.getCritical(resource);
            defendingStatistics.modify(resource, -damage);
            String negative = "", positive = "";
            switch (resource) {
                case Statistics.HEALTH -> {
                    negative = ColorPalette.HEX_CODE_RED;
                    positive = ColorPalette.HEX_CODE_GREEN;
                }
                case Statistics.MANA -> {
                    negative = ColorPalette.HEX_CODE_PURPLE;
                    positive = ColorPalette.HEX_CODE_BLUE;
                }
                case Statistics.STAMINA -> {
                    negative = ColorPalette.HEX_CODE_CREAM;
                    positive = ColorPalette.HEX_CODE_GREEN;
                }
            }
            if (damage != 0) {
                model.logger.log(
                        ColorPalette.getHtmlColor(attacker.toString(), ColorPalette.HEX_CODE_GREEN),
                        StringFormatter.format(
                                "uses {} {} {}",
                                ColorPalette.getHtmlColor(String.valueOf(event.ability), ColorPalette.HEX_CODE_CREAM),
                                defender == attacker ? "" : "on " + defender,
                                damage > 0 ?
                                        ColorPalette.getHtmlColor("dealing " + Math.abs(damage) + " Damage", negative) :
                                        ColorPalette.getHtmlColor("recovering " + Math.abs(damage) + resource, positive)
                        )
                );
                model.system.floatingText.floater((critical != 0 ? "!" : "") + (damage <  0 ? "+" : "") +
                                Math.abs(damage) + "", defender.get(Animation.class).getVector(),
                        ColorPalette.getColorOfAbility(event.ability));
            } else {
                model.logger.log(
                        ColorPalette.getHtmlColor(attacker.toString(), ColorPalette.HEX_CODE_GREEN), "uses " + event.ability
                );
            }
        }

        // Draw the correct combat animations
    //    applyAnimationsBasedOnAbility(model, event.ability, defender, health, energy, buffValue);

        // 2. If the defender has no more health, just remove
        if (model.speedQueue.removeIfNoCurrentHealth(defender)) {
            announceWithStationaryText(model, "Dead!", defender, ColorPalette.NORMAL_TYPE);
            return;
        }

        // 3. apply status effects to target 
        applyEffects(model, defender, event, event.ability.conditionsToTargetsChances.entrySet());

        // don't move if already performing some action
        AnimationMovementTrack track = defender.get(AnimationMovementTrack.class);
        if (track.isMoving()) { return; }

        // defender has already queued an attack/is the attacker, don't animate
        if (mQueue.containsKey(defender)) { return; }

        track.wiggle(defender);
    }

    private void applyAnimationsToTargets(GameModel model, Ability ability, Set<Entity> targets) {


        String type = EmeritusUtils.getAbilityTypes(ability);
        Animation animation = AssetPool.getInstance().getAbilityAnimation(type);
        
        if (animation == null) { logger.error("Some reason, the animation returned null"); }

        // animation.lengthenAnimation();

        model.system.combatAnimation.apply(targets, animation);
    }

    private void applyEffects(GameModel model, Entity target, CombatEvent event, Set<Map.Entry<String, Float>> statuses) {
        // Go through all the different status effects and their probability
        Statistics statistics = target.get(Statistics.class);
        for (Map.Entry<String, Float> entry : statuses) {
            // If the stat chance passes, handle
            float statusChance = Math.abs(entry.getValue());
            if (statusChance < mRandom.nextFloat()) { continue; }
            // Check if the status effect increases a stat
            String status = entry.getKey();
//            StatNode node = statistics.getStatsNode(status);


            if (status.endsWith("Knockback")) {
                handleKnockback(model, target, event);
            } else {
                target.get(Tags.class).add(status, event.ability);
            }
            Color c = ColorPalette.getColorOfAbility(event.ability);

//            if (node != null) {
//                statistics.modify(status,
//                        event.ability, StatNode.MULTIPLICATIVE, (int) (entry.getValue() <  0 ? -.5f : .5f));
//                model.logger.log(target + "'s " + status + " " +
//                        (entry.getValue() <  0 ? "decreased" : "increased"));
//
//                announceWithFloatingText(gameModel, (entry.getValue() <  0 ? "-" : "+") +
//                                StringUtils.spaceByCapitalization(status), target, c);
//            } else {
//                announceWithFloatingText(gameModel,
//                        StringUtils.spaceByCapitalization(status) + "'d", target, c);
//                model.logger.log(target + " was inflicted with " + StringUtils.spaceByCapitalization(status));
//            }
            logger.info("{} has {}", target, status);
        }
    }

    private static void handleKnockback(GameModel model, Entity target, CombatEvent event) {
        Tile actorTile = event.actor.get(MovementManager.class).currentTile.get(Tile.class);
        Tile targetTile = target.get(MovementManager.class).currentTile.get(Tile.class);
        Direction direction = TileUtils.getDirectionFrom(targetTile, actorTile);
        Entity toMoveTo = null;
        for (int i = 1; i < 3; i++) {
            Entity toCheck = model.tryFetchingTileAt(targetTile.row + (direction.y * i),
                    targetTile.column + (direction.x * i));
            if (toCheck == null) { break; }
            Tile location = toCheck.get(Tile.class);
            if (location.isWall() || location.isNotNavigable()) { continue; }
            toMoveTo = toCheck;
        }
        if (toMoveTo == null) { return; }
        MovementManager.forceMove(model, target, toMoveTo);
    }

    public void payAbilityCosts(CombatEvent event) {
        Entity unit = event.actor;
        Ability ability = event.ability;

        // Deduct the cost from the user
        Statistics statistics = unit.get(Statistics.class);
        for (String key : statistics.getResourceKeys()) {
            int cost = ability.getCost(unit, key);

            statistics.modify(key,  -cost);
        }
    }

    public  void applyAnimationToUser(Entity actor, Ability ability, Set<Entity> targets) {
        AnimationMovementTrack track = actor.get(AnimationMovementTrack.class);
        if (ability.travel.contains("Melee")) {
            Entity tile = targets.iterator().next();
            track.forwardsThenBackwards(actor, tile);
        } else {
            track.gyrate(actor);
        }
    }

    public Set<Entity> extractUnitsFromTiles(Ability ability, List<Entity> tiles) {
        Set<Entity> set = new HashSet<>();
        for (Entity tile : tiles) {
            Tile details = tile.get(Tile.class);
            if (details.mUnit == null) { continue; }
            set.add(details.mUnit);
        }
        return set;
    }
    
    private void announceWithStationaryText(GameModel model, String announcement, Entity user, Color color) {
        model.system.floatingText.stationary(announcement, user.get(Animation.class).getVector(), color);
    }

    private void announceWithFloatingText(GameModel model, String announcement, Entity user, Color color) {
        model.system.floatingText.floater(announcement, user.get(Animation.class).getVector(), color);
    }
}
