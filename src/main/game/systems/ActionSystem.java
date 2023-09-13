package main.game.systems;


import main.constants.ColorPalette;
import main.constants.Constants;

import main.constants.Direction;
import main.constants.Settings;
import main.game.components.*;
import main.game.components.Statistics;
import main.game.components.Vector;
import main.game.components.tile.Tile;
import main.game.components.tile.TileUtils;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stats.Stat;
import main.game.stores.pools.AssetPool;
import main.game.stores.pools.action.Action;
import main.game.systems.combat.CombatEvent;
import main.game.systems.combat.DamageCalculator;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.EmeritusUtils;
import main.utils.MathUtils;
import main.utils.StringFormatter;
import main.utils.StringUtils;

import java.awt.Color;
import java.util.*;

public class ActionSystem extends GameSystem {

    private final Map<Entity, CombatEvent> queue = new HashMap<>();
    private final SplittableRandom random = new SplittableRandom();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(ActionSystem.class);
    private GameModel gameModel;

    @Override
    public void update(GameModel model, Entity unit) {
        // 1. if the current unit is not in queue, skip
        CombatEvent event = queue.get(unit);
        gameModel = model;
        if (event == null) { return; }

        // 2. wait next loop to check if attacker has finished animating
        boolean isFastForwarding = Settings.getInstance().getBoolean(Settings.GAMEPLAY_FAST_FORWARD_TURNS);
        Track track = unit.get(Track.class);
        if (!isFastForwarding && track.isMoving()) { return; }

        // 3. Finish the combat by applying the damage to the defending units. Remove from queue
        finishCombat(model, unit, event);
        queue.remove(unit);
    }

    public boolean startCombat(GameModel model, Entity user, Action action, Set<Entity> attackAt) {

        // 0. if the ability can't affect the user, remove if available
        if (!action.hasTag(Tags.CAN_FRIENDLY_FIRE)) {
            attackAt.remove(user.get(MovementManager.class).currentTile);
        }
        if (attackAt.isEmpty()) { return false; }

        // 1. Check that unit has resources for ability
        if (action.cantPayCosts(user)) { return false; }

        // 2. Animate based on the abilities range
        applyAnimationToUser(user, action, attackAt);

        // 3. Draw ability name to screen
        announceWithFloatingText(model, action.name, user, ColorPalette.getColorOfAbility(action));

        // 4. Cache the combat state...
        queue.put(user, new CombatEvent(user, action, attackAt));

        return true;
    }

    private void finishCombat(GameModel model, Entity attacker, CombatEvent event) {
        logger.debug("{} initiates combat", attacker);

        // 0. Pay the ability costs
        payAbilityCosts(event);

//        applyEffects(model, event.actor, event, event.action.tagsToUserMap.entrySet());

        applyAnimationsToTargets(model, event.action, event.tiles);

        // 3. Execute a hit on all selected defenders
        for (Entity entity : event.tiles) {
            Tile tile = entity.get(Tile.class);

            if (tile.isStructure()) { tile.removeStructure(); }
            if (tile.unit == null) { continue; }

            boolean hit = MathUtils.passesChanceOutOf100(event.action.accuracy);
            logger.debug("{} uses {} on {}", attacker, event.action.name, entity);

            // 4. Attack if possible
            if (hit) {
                executeHit(model, attacker, event, tile.unit);
            } else {
                executeMiss(model, attacker, event, tile.unit);
            }
        }
        Statistics stats = attacker.get(Statistics.class);
        if (stats.toExperience(random.nextInt(1, 5))) {
            announceWithFloatingText(gameModel, "Lvl Up!", attacker, Color.WHITE);
        }

        logger.debug("{} finishes combat", attacker);
    }

    private  void executeMiss(GameModel model, Entity attacker, CombatEvent event, Entity defender) {
        Vector vector = attacker.get(Animation.class).getVector();
        model.system.floatingText.floater("Missed!", vector, ColorPalette.getColorOfAbility(event.action));
        logger.info("{} misses {}", attacker, defender);
    }

    private  void executeHit(GameModel model, Entity attacker, CombatEvent event, Entity defender) {

        // 0. Setup
        Statistics defendingStatistics = defender.get(Statistics.class);
        Vector defendingVector = defender.get(Animation.class).getVector();
        Statistics attackingStatistics = attacker.get(Statistics.class);
        Vector attackingVector = attacker.get(Animation.class).getVector();

        // 1. Calculate damage
        DamageCalculator report = new DamageCalculator(model, attacker, event.action, defender);

        for (String resource : report.getDamageKeys()) {
            int damage = (int) report.getDamage(resource);
            int critical = (int) report.getCritical(resource);
            defendingStatistics.toResources(resource, -damage);
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
                                ColorPalette.getHtmlColor(String.valueOf(event.action), ColorPalette.HEX_CODE_CREAM),
                                defender == attacker ? "" : "on " + defender,
                                damage > 0 ?
                                        ColorPalette.getHtmlColor("dealing " + Math.abs(damage) + " Damage", negative) :
                                        ColorPalette.getHtmlColor("recovering " + Math.abs(damage) + resource, positive)
                        )
                );
                model.system.floatingText.floater((critical != 0 ? "!" : "") + (damage <  0 ? "+" : "") +
                                Math.abs(damage) + "", defender.get(Animation.class).getVector(),
                        ColorPalette.getColorOfAbility(event.action));
            } else {
                model.logger.log(
                        ColorPalette.getHtmlColor(attacker.toString(), ColorPalette.HEX_CODE_GREEN), "uses " + event.action
                );
            }
        }

        // Draw the correct combat animations
    //    applyAnimationsBasedOnAbility(model, event.ability, defender, health, energy, buffValue);

        // 2. If the defender has no more health, just remove
        if (model.speedQueue.removeIfNoCurrentHealth(defender)) {
            announceWithStationaryText(model, "Dead!", defender, ColorPalette.GREY);
            return;
        }

        // 3. apply status effects to target 
//        applyEffects(model, defender, event, event.action.tagsToTargetsMap.entrySet());

        // don't move if already performing some action
        Track track = defender.get(Track.class);
        if (track.isMoving()) { return; }

        // defender has already queued an attack/is the attacker, don't animate
        if (queue.containsKey(defender)) { return; }

        track.wiggle(defender);
    }

    private void applyAnimationsToTargets(GameModel model, Action action, Set<Entity> targets) {


        String type = EmeritusUtils.getAbilityTypes(action);
        Animation animation = AssetPool.getInstance().getAbilityAnimation(type);
        
        if (animation == null) { logger.info("TODO, why are some animations returning null?"); }

        // animation.lengthenAnimation();

        model.system.combatAnimation.apply(targets, animation);
    }

    private void applyEffects(GameModel model, Entity target, CombatEvent event, Set<Map.Entry<String, Float>> statuses) {
        // Go through all the different status effects and their probability
        Statistics statistics = target.get(Statistics.class);
        for (Map.Entry<String, Float> entry : statuses) {
            // If the stat chance passes, handle
            float statusChance = Math.abs(entry.getValue());
            if (statusChance < random.nextFloat()) { continue; }
            // Check if the status effect increases a stat
            String status = entry.getKey();
            Stat node = statistics.getStatsNode(status);

            if (status.endsWith("Knockback")) {
                handleKnockback(model, target, event);
            } else if (node == null) {
                target.get(Tags.class).add(status, event.action);
            }
            Color c = ColorPalette.getColorOfAbility(event.action);
            if (node != null) {
                node.add(event.action, Constants.PERCENT, entry.getValue() <  0 ? -.5f : .5f);
                model.logger.log(target + "'s " + status + " " +
                        (entry.getValue() <  0 ? "decreased" : "increased"));

                announceWithFloatingText(gameModel, (entry.getValue() <  0 ? "-" : "+") +
                                StringUtils.spaceByCapitalization(node.getName()), target, c);
            } else {
                announceWithFloatingText(gameModel, StringUtils.capitalize(status) + "'d", target, c);
                model.logger.log(target + " was inflicted with " + status);
            }
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
            if (location.isWall() || location.isGreaterStructure()) { continue; }
            toMoveTo = toCheck;
        }
        if (toMoveTo == null) { return; }
        MovementManager.forceMove(model, target, toMoveTo);
    }

    public void payAbilityCosts(CombatEvent event) {
        Entity unit = event.actor;
        Action action = event.action;

        // Deduct the cost from the user
        Statistics statistics = unit.get(Statistics.class);
        for (String key : statistics.getResourceKeys()) {
            int cost = action.getCost(unit, key);

            statistics.toResources(key, -cost);
        }
    }

    public  void applyAnimationToUser(Entity actor, Action action, Set<Entity> targets) {
        Track track = actor.get(Track.class);
        if (action.animation.contains("Ranged")) {
            track.gyrate(actor);
        } else {
            Entity tile = targets.iterator().next();
            track.forwardsThenBackwards(actor, tile);
        }
    }

    public Set<Entity> extractUnitsFromTiles(Action action, List<Entity> tiles) {
        Set<Entity> set = new HashSet<>();
        for (Entity tile : tiles) {
            Tile details = tile.get(Tile.class);
            if (details.unit == null) { continue; }
            set.add(details.unit);
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
