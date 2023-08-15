package main.game.systems;


import main.constants.ColorPalette;
import main.constants.Constants;
import main.ui.GameState;

import main.game.components.*;
import main.game.components.Vector;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stats.node.ResourceNode;
import main.game.stats.node.StatsNode;
import main.game.stores.pools.AssetPool;
import main.game.stores.pools.ability.Ability;
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

public class CombatSystem extends GameSystem {

    private final Map<Entity, CombatEvent> queue = new HashMap<>();
    private final SplittableRandom random = new SplittableRandom();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(CombatSystem.class);
    private final String physicalTypes = "Slash Pierce Blunt Normal";
    private final String magicalTypes = "Light Air Water Dark Fire Nature";
    private final String[] statKeys = new String[]{ 
        "Health", "MagicalAttack", "MagicalDefense", 
        "Energy", "PhysicalAttack", "PhysicalDefense"
    };

    private GameModel gameModel;

    @Override
    public void update(GameModel model, Entity unit) {
        // 1. if the current unit is not in queue, skip
        CombatEvent event = queue.get(unit);
        gameModel = model;
        if (event == null) { return; }

        // 2. wait next loop to check if attacker has finished animating
        boolean isFastForwarding = model.gameState.getBoolean(GameState.UI_SETTINGS_FAST_FORWARD_TURNS); //engine.model.ui.settings.fastForward.isSelected();
        MovementTrack movementTrack = unit.get(MovementTrack.class);
        if (!isFastForwarding && movementTrack.isMoving()) { return; }

        // 3. Finish the combat by applying the damage to the defending units. Remove from queue
        finishCombat(model, unit, event);
        queue.remove(unit);
    }

    public boolean startCombat(GameModel model, Entity user, Ability ability, Set<Entity> attackAt) {

        // 0. if the ability can't affect the user, remove if available
        if (!ability.hasTag(Ability.CAN_FRIENDLY_FIRE)) {
            attackAt.remove(user.get(MovementManager.class).currentTile);
        }
        if (attackAt.isEmpty()) { return false; }

        // 1. Check that unit has resources for ability
        if (!ability.canPayCosts(user)) { return false; }

        // 2. Animate based on the abilities range
        applyAnimationToUser(user, ability, attackAt);

        // 3. Draw ability name to screen
        announceWithFloatingText(model, ability.name, user, ColorPalette.getColorOfAbility(ability));

        // 4. Cache the combat state...
        queue.put(user, new CombatEvent(user, ability, attackAt));

        return true;
    }

    private void finishCombat(GameModel model, Entity attacker, CombatEvent event) {
        logger.debug("{} initiates combat", attacker);

        // 0. Pay the ability costs
        payAbilityCosts(event);

        applyEffects(model, event.actor, event.ability, event.ability.tagsToUser.entrySet());

        applyAnimationsToTargets(model, event.ability, event.tiles);

        // 3. Execute a hit on all selected defenders
        for (Entity entity : event.tiles) {
            Tile tile = entity.get(Tile.class);

            if (tile.isStructure()) { tile.removeStructure(); }
            if (tile.unit == null) { continue; }

            boolean hit = MathUtils.passesChanceOutOf100(event.ability.accuracy);
            logger.debug("{} uses {} on {}", attacker, event.ability.name, entity);

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
        Vector vector = attacker.get(Animation.class).position;
        model.system.floatingText.floater("Missed!", vector, ColorPalette.getColorOfAbility(event.ability));
        logger.info("{} misses {}", attacker, defender);
    }

    private  void executeHit(GameModel model, Entity attacker, CombatEvent event, Entity defender) {

        // 0. Setup
        Statistics defendingSummary = defender.get(Statistics.class);
        Vector defendingVector = defender.get(Animation.class).position;
        Statistics attackingSummary = attacker.get(Statistics.class);
        Vector attackingVector = attacker.get(Animation.class).position;

        // 1. Calculate damage
        DamageReport report = new DamageReport(model, attacker, event.ability, defender);
        int healthDamage = report.getFinalHealthDamage();
        int energyDamage = report.getFinalEnergyDamage();

        if (healthDamage != 0) {
            ResourceNode health = defendingSummary.getResourceNode(Constants.HEALTH);
            health.add(-healthDamage);
            model.logger.log(
                    ColorPalette.getHtmlColor(attacker.toString(), ColorPalette.HEX_CODE_GREEN),
                    StringFormatter.format(
                            "uses {} on {} {}",
                            ColorPalette.getHtmlColor(String.valueOf(event.ability), ColorPalette.HEX_CODE_CREAM),
                            defender == attacker ? "themselves" : defender,
                            healthDamage > 0 ?
                                    ColorPalette.getHtmlColor("dealing " + healthDamage + " DMG", ColorPalette.HEX_CODE_RED) :
                                    ColorPalette.getHtmlColor("recovering " + Math.abs(healthDamage) + " HP", ColorPalette.HEX_CODE_GREEN)
                    )
            );
            if (energyDamage == 0) {
                ResourceNode energy = defendingSummary.getResourceNode(Constants.ENERGY);
                energy.add(random.nextInt(1, 5));
            }
        }
        if (energyDamage != 0) {
            ResourceNode energy = defendingSummary.getResourceNode(Constants.ENERGY);
            energy.add(-energyDamage);
            model.logger.log(
                    ColorPalette.getHtmlColor(attacker.toString(), ColorPalette.HEX_CODE_GREEN),
                    StringFormatter.format(
                            "uses {} on {} {}",
                            ColorPalette.getHtmlColor(String.valueOf(event.ability), ColorPalette.HEX_CODE_CREAM),
                            defender == attacker ? "themselves" : defender,
                            energyDamage > 0 ?
                                    ColorPalette.getHtmlColor("dealing " + energyDamage + " DMG", ColorPalette.HEX_CODE_PURPLE) :
                                    ColorPalette.getHtmlColor("recovering " + Math.abs(energyDamage) + " EP", ColorPalette.HEX_CODE_BLUE)
                    )
            );
        }

        if (energyDamage == 0 && healthDamage == 0) {
            model.logger.log(
                    ColorPalette.getHtmlColor(attacker.toString(), ColorPalette.HEX_CODE_GREEN),
                    "uses " + event.ability
            );
        }

        // Draw the correct combat animations
    //    applyAnimationsBasedOnAbility(model, event.ability, defender, health, energy, buffValue);

        // 2. If the defender has no more health, just remove
        if (model.speedQueue.removeIfNoCurrentHealth(defender)) {
            announceWithStationaryText(model, "Dead!", defender, ColorPalette.GREY);
            // model.system.floatingText.floater("Dead!",
            //         defender.get(Animation.class).position, ColorPalette.getColorBasedOnAbility(event.ability));
            return;
        }

        // 3. apply status effects to target 
        applyEffects(model, defender, event.ability, event.ability.tagsToTargets.entrySet());

        // don't move if already performing some action
        MovementTrack movementTrack = defender.get(MovementTrack.class);
        if (movementTrack.isMoving()) { return; }

        // defender has already queued an attack/is the attacker, don't animate
        if (queue.containsKey(defender)) { return; }

        movementTrack.wiggle(defender);
    }

    private void applyAnimationsToTargets(GameModel model, Ability ability, Set<Entity> targets) {


        String type = EmeritusUtils.getAbilityTypes(ability);
        Animation animation = AssetPool.getInstance().getAbilityAnimation(type);
        
        if (animation == null) { logger.info("TODO, why are some animations returning null?"); }

        // animation.lengthenAnimation();

        model.system.combatAnimation.apply(targets, animation);
    }

    private void applyEffects(GameModel model, Entity target, Ability ability, Set<Map.Entry<String, Float>> statuses) {
        // Go through all the different status effects and their probability
        Statistics statistics = target.get(Statistics.class);
        for (Map.Entry<String, Float> entry : statuses) {
            // If the stat chance passes, handle
            float statusChance = Math.abs(entry.getValue());
            if (statusChance < random.nextFloat()) { continue; }
            // Check if the status effect increases a stat
            String status = entry.getKey();
            StatsNode node = statistics.getStatsNode(status);

            if (node == null) { target.get(Tags.class).add(status, ability); }
            Color c = ColorPalette.getColorOfAbility(ability);
            if (node != null) {
                node.add(ability, Constants.PERCENT, entry.getValue() <  0 ? -.5f : .5f);
                model.logger.log(target + "'s " + status + " " +
                        (entry.getValue() <  0 ? "decreased" : "increased"));

                announceWithFloatingText(gameModel,
                        (entry.getValue() <  0 ? "-" : "+") +
                                StringUtils.spaceByCapitalization(node.getName()),
                        target, c
                );
            } else {
//                announceWithFloatingText(gameModel, StringUtils.capitalize(status) + "'d", target, c);
                announceWithFloatingText(gameModel, StringUtils.capitalize(status), target, c);
                model.logger.log(target + " was inflicted with " + status);
            }
            logger.info("{} has {}", target, status);
        }
    }

    public  void payAbilityCosts(CombatEvent event) {
        Entity unit = event.actor;
        Ability ability = event.ability;

        // Get the costs of the ability
        int healthCost = ability.getHealthCost(unit);
        int energyCost = ability.getEnergyCost(unit);

        if (healthCost != 0) { logger.debug("{} paying {} health for {}", unit, healthCost, ability.name); }
        if (energyCost != 0) { logger.debug("{} paying {} energy for {}", unit, energyCost, ability.name); }

        // Deduct the cost from the user
        Statistics summary = unit.get(Statistics.class);
        ResourceNode health = summary.getResourceNode(Constants.HEALTH);
        health.add(-healthCost);
        ResourceNode energy = summary.getResourceNode(Constants.ENERGY);
        energy.add(-energyCost);
    }

    public boolean canPayAbilityCosts(Entity unit, Ability ability) {
        Statistics stats = unit.get(Statistics.class);
        ResourceNode health = stats.getResourceNode(Constants.HEALTH);
        ResourceNode energy = stats.getResourceNode(Constants.ENERGY);
        boolean canPayHealthCosts = health.getCurrent() >= ability.getHealthCost(unit);
        boolean canPayEnergyCosts = energy.getCurrent() >= ability.getEnergyCost(unit);
        return canPayHealthCosts && canPayEnergyCosts;
    }

    public  void applyAnimationToUser(Entity actor, Ability ability, Set<Entity> targets) {
        MovementTrack movementTrack = actor.get(MovementTrack.class);
        if (ability.animation.equals("Magic")) {
            movementTrack.gyrate(actor);
        } else {
            Entity tile = targets.iterator().next();
            movementTrack.forwardsThenBackwards(actor, tile);
        }
    }

    public Set<Entity> extractUnitsFromTiles(Ability ability, List<Entity> tiles) {
        Set<Entity> set = new HashSet<>();
        for (Entity tile : tiles) {
            Tile details = tile.get(Tile.class);
            if (details.unit == null) { continue; }
            set.add(details.unit);
        }
        return set;
    }
    
    private void announceWithStationaryText(GameModel model, String announcement, Entity user, Color color) {
        model.system.floatingText
            .stationary(
                announcement, 
                user.get(Animation.class).position, 
                color
            );
    }

    private void announceWithFloatingText(GameModel model, String announcement, Entity user, Color color) {
        model.system.floatingText
                .floater(
                        announcement,
                        user.get(Animation.class).position,
                        color
                );
    }
}
