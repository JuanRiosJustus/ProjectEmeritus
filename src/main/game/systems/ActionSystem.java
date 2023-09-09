package main.game.systems;


import main.constants.ColorPalette;
import main.constants.Constants;

import main.constants.Direction;
import main.constants.Settings;
import main.game.components.Animation;
import main.game.components.MovementManager;
import main.game.components.Summary;
import main.game.components.Tags;
import main.game.components.Tile;
import main.game.components.Track;
import main.game.components.Vector;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stats.node.StatsNode;
import main.game.stores.pools.AssetPool;
import main.game.stores.pools.action.Action;
import main.game.systems.combat.CombatEvent;
import main.game.systems.combat.DamageReport;
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
        if (!action.hasTag(Action.CAN_FRIENDLY_FIRE)) {
            attackAt.remove(user.get(MovementManager.class).currentTile);
        }
        if (attackAt.isEmpty()) { return false; }

        // 1. Check that unit has resources for ability
        if (action.canNotPayCosts(user)) { return false; }

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

        applyEffects(model, event.actor, event, event.action.tagsToUserMap.entrySet());

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
        Summary stats = attacker.get(Summary.class);
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
        Summary defendingSummary = defender.get(Summary.class);
        Vector defendingVector = defender.get(Animation.class).getVector();
        Summary attackingSummary = attacker.get(Summary.class);
        Vector attackingVector = attacker.get(Animation.class).getVector();

        // 1. Calculate damage
        DamageReport report = new DamageReport(model, attacker, event.action, defender);
        int healthDamage = report.getFinalHealthDamage();
        int energyDamage = report.getFinalEnergyDamage();

        if (healthDamage != 0) {
            defendingSummary.addResources(Constants.HEALTH, -healthDamage);
            model.logger.log(
                    ColorPalette.getHtmlColor(attacker.toString(), ColorPalette.HEX_CODE_GREEN),
                    StringFormatter.format(
                            "uses {} {} {}",
                            ColorPalette.getHtmlColor(String.valueOf(event.action), ColorPalette.HEX_CODE_CREAM),
                            defender == attacker ? "" : "on " + ColorPalette.getHtmlColor(defender.toString(), ColorPalette.HEX_CODE_RED),
                            healthDamage > 0 ?
                                    ColorPalette.getHtmlColor("dealing " + Math.abs(healthDamage) + " DMG", ColorPalette.HEX_CODE_LIGHT_RED) :
                                    ColorPalette.getHtmlColor("recovering " + Math.abs(healthDamage) + " HP", ColorPalette.HEX_CODE_LIGHT_GREEN)
                    )
            );
            if (energyDamage == 0) {
                defendingSummary.addResources(Constants.ENERGY, random.nextInt(1, 5));
            }
        }
        if (energyDamage != 0) {
            defendingSummary.addResources(Constants.ENERGY, -energyDamage);
            model.logger.log(
                    ColorPalette.getHtmlColor(attacker.toString(), ColorPalette.HEX_CODE_GREEN),
                    StringFormatter.format(
                            "uses {} {} {}",
                            ColorPalette.getHtmlColor(String.valueOf(event.action), ColorPalette.HEX_CODE_CREAM),
                            defender == attacker ? "" : "on " + ColorPalette.getHtmlColor(defender.toString(), ColorPalette.HEX_CODE_RED),
                            energyDamage > 0 ?
                                    ColorPalette.getHtmlColor("dealing " + Math.abs(energyDamage) + " DMG", ColorPalette.HEX_CODE_LIGHT_PURPLE) :
                                    ColorPalette.getHtmlColor("recovering " + Math.abs(energyDamage) + " EP", ColorPalette.HEX_CODE_LIGHT_BLUE)
                    )
            );
        }

        if (energyDamage == 0 && healthDamage == 0) {
            model.logger.log(
                    ColorPalette.getHtmlColor(attacker.toString(), ColorPalette.HEX_CODE_GREEN),
                    "uses " + event.action
            );
        }

        // Draw the correct combat animations
    //    applyAnimationsBasedOnAbility(model, event.ability, defender, health, energy, buffValue);

        // 2. If the defender has no more health, just remove
        if (model.speedQueue.removeIfNoCurrentHealth(defender)) {
            announceWithStationaryText(model, "Dead!", defender, ColorPalette.GREY);
            return;
        }

        // 3. apply status effects to target 
        applyEffects(model, defender, event, event.action.tagsToTargetsMap.entrySet());

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
        Summary summary = target.get(Summary.class);
        for (Map.Entry<String, Float> entry : statuses) {
            // If the stat chance passes, handle
            float statusChance = Math.abs(entry.getValue());
            if (statusChance < random.nextFloat()) { continue; }
            // Check if the status effect increases a stat
            String status = entry.getKey();
            StatsNode node = summary.getStatsNode(status);

            if (status.endsWith("Knockback")) {
                // get direction user is attacking from
                float distance = Float.MAX_VALUE;
                Entity bestTileEntity = null;
                Direction bestDirection = null;
                Entity userLocation = event.actor.get(MovementManager.class).currentTile;
                Tile tile = userLocation.get(Tile.class);
                for (Direction direction : Direction.values()) {
                    Entity adjLoc = model.tryFetchingTileAt(tile.row + direction.y, tile.column + direction.x);
                    if (adjLoc == null) { continue; }
                    Tile newLoc = adjLoc.get(Tile.class);
                    float newDistance = (float) tile.distance(newLoc);
                    if (newDistance < distance) {
                        distance = newDistance;
                        bestTileEntity = adjLoc;
                        bestDirection = direction;
                    }
                }
                if (bestTileEntity != null) {
                    // move target in that direction if possible
                    Tile bestTile = bestTileEntity.get(Tile.class);
                    if (!bestTile.isGreaterStructure()) {
                        MovementManager mm = target.get(MovementManager.class);
                        mm.moved = false;
                        int toMove = 2;
                        Entity toMoveTo =
                                model.tryFetchingTileAt(tile.row + (bestDirection.y * toMove),
                                        tile.column + (bestDirection.x * toMove));
                        MovementManager.move(model, target, toMoveTo, true);
                    }
                }
            } else if (node == null) {
                target.get(Tags.class).add(status, event.action);
            }
            Color c = ColorPalette.getColorOfAbility(event.action);
            if (node != null) {
                node.add(event.action, Constants.PERCENT, entry.getValue() <  0 ? -.5f : .5f);
                model.logger.log(target + "'s " + status + " " +
                        (entry.getValue() <  0 ? "decreased" : "increased"));

                announceWithFloatingText(gameModel,
                        (entry.getValue() <  0 ? "-" : "+") +
                                StringUtils.spaceByCapitalization(node.getName()),
                        target, c
                );
            } else {
//                announceWithFloatingText(gameModel, StringUtils.capitalize(status) + "'d", target, c);
                announceWithFloatingText(gameModel, StringUtils.capitalize(status) + "'d", target, c);
                model.logger.log(target + " was inflicted with " + status);
            }
            logger.info("{} has {}", target, status);
        }
    }

    public void payAbilityCosts(CombatEvent event) {
        Entity unit = event.actor;
        Action action = event.action;

        // Get the costs of the ability
        int healthCost = action.getHealthCost(unit);
        int energyCost = action.getEnergyCost(unit);

        if (healthCost != 0) { logger.debug("{} paying {} health for {}", unit, healthCost, action.name); }
        if (energyCost != 0) { logger.debug("{} paying {} energy for {}", unit, energyCost, action.name); }

        // Deduct the cost from the user
        Summary summary = unit.get(Summary.class);
        summary.addResources(Constants.HEALTH, -healthCost);
        summary.addResources(Constants.ENERGY, -energyCost);
    }

    public boolean canPayAbilityCosts(Entity unit, Action action) {
        Summary summary = unit.get(Summary.class);
        boolean canPayHealthCosts = summary.getStatCurrent(Constants.HEALTH) >= action.getHealthCost(unit);
        boolean canPayEnergyCosts = summary.getStatCurrent(Constants.ENERGY) >= action.getEnergyCost(unit);
        return canPayHealthCosts && canPayEnergyCosts;
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
