package main.game.systems;


import main.constants.ColorPalette;
import main.constants.Constants;
import main.constants.GameStateKey;

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
        boolean isFastForwarding = model.state.getBoolean(GameStateKey.UI_SETTINGS_FAST_FORWARD_TURNS); //engine.model.ui.settings.fastForward.isSelected();
        MovementTrack movementTrack = unit.get(MovementTrack.class);
        if (!isFastForwarding && movementTrack.isMoving()) { return; }

        // 3. Finish the combat by applying the damage to the defending units. Remove from queue
        finishCombat(model, unit, event);
        queue.remove(unit);
    }

    public void startCombat(GameModel model, Entity actor, Ability ability, Set<Entity> attackAt) {

        // 0. if the ability can't affect the user, remove if available
        if (!ability.canFriendlyFire) { attackAt.remove(actor.get(MovementManager.class).currentTile); }
        if (attackAt.isEmpty()) { return; }

        // 1. Check that unit has resources for ability
        if (!canPayAbilityCosts(actor, ability)) { return; }

        // 2.5 This can happen if player chooses empty square
        if (attackAt.isEmpty()) { return; }

        // 3. Animate based on the abilities range
        animateBasedOnAbilityRange(actor, ability, attackAt);

        // 4. Draw ability name to screen
        announceAbility(model, ability, actor);

        // 5. Cache the combat state...
        queue.put(actor, new CombatEvent(actor, ability, attackAt));

        // model.logger.log(actor, "uses " + ability.name);
    }

    private void finishCombat(GameModel model, Entity attacker, CombatEvent event) {
        logger.debug("{} initiates combat", attacker);

        // 0. Pay the ability costs
        payAbilityCosts(event);

        // 1.5. apply buffs and debuffs to user
//        int value = tryApplyingBuffsToUser(event.ability, event.attacker, event.ability.statusToUser);
//
//        // 2 Draw the correct combat animations
//        if (value != 0) { model.system.combatAnimation.apply(attacker, (value > 0 ? "Buff" : "Debuff")); }

        // tryApplyingStatusToUser(model, event.ability, event.actor);

        applyAnimationsToTargets(model, event.ability, event.tiles);

        // 3. Execute a hit on all selected defenders
        for (Entity entity : event.tiles) {
            Tile tile = entity.get(Tile.class);

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
        stats.gainExperience(4);

        logger.debug("{} finishes combat", attacker);
    }

    private  void tryApplyingStatusToUser(GameModel model, Ability ability, Entity attacker) {
        for (Map.Entry<String, Float> entry : ability.statusToUser.entrySet()) {
            if (!MathUtils.passesChanceOutOf100(entry.getValue())) { continue; }
            String statusToApply = entry.getKey();
            logger.info("Applying {} to {}", statusToApply, attacker);
            attacker.get(StatusEffects.class).add(statusToApply, ability);
            model.system.floatingText.floater(statusToApply,
                    attacker.get(Animation.class).position, ColorPalette.getColorOfAbility(ability));

        }
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
        int healthDamage = report.getTotalHealthDamage();
        int energyDamage = report.getTotalEnergyDamage();

        if (healthDamage != 0) {
            ResourceNode health = defendingSummary.getResourceNode(Constants.HEALTH);
            health.apply(-healthDamage);
            model.logger.log(attacker, " uses " + event.ability + " on " + defender + " for " + healthDamage + " Damage");
        }
        if (energyDamage != 0) {
            ResourceNode energy = defendingSummary.getResourceNode(Constants.ENERGY);
            energy.apply(-energyDamage);
            model.logger.log(defender, " uses " + event.ability + " on " + defender + " for " + energyDamage + " Damage");
        }

        // get total buffOrDebuff and apply
//        int buffValue = tryApplyingBuffsToTargets(event.ability, defender);
//        String type = EmeritusUtils.getAbilityTypes(event.ability);

        // Draw the correct combat animations
    //    applyAnimationsBasedOnAbility(model, event.ability, defender, health, energy, buffValue);

        // 2. If the defender has no more health, just remove
        if (model.speedQueue.removeIfNoCurrentHealth(defender)) {
            announce(model, "Dead!", defender, ColorPalette.GREY);
            // model.system.floatingText.floater("Dead!",
            //         defender.get(Animation.class).position, ColorPalette.getColorBasedOnAbility(event.ability));
            return;
        }

        // 3. apply status effects to target 
        applyStatusEffects2(model, defender, event.ability);
       
        // 4. apply buff effects to target 
        // applyBuffs(model, defender, event.ability.buffToTargets);

        // don't move if already performing some action
        MovementTrack movementTrack = defender.get(MovementTrack.class);
        if (movementTrack.isMoving()) { return; }

        // defender has already queued an attack/is the attacker, don't animate
        if (queue.containsKey(defender)) { return; }

        movementTrack.wiggle(defender);
    }

    private void applyAnimationsToTargets(GameModel model, Ability ability, Set<Entity> targets) {

        // Animation animation = AssetPool.instance().getAbilityAnimation(ability.name);

        // if (animation == null) {
        //     String type = EmeritusUtils.getAbilityTypes(ability);
        //     animation = AssetPool.instance().getAbilityAnimation(type);;
        // } else {
        //     // animation.elongate();
        // }

        String type = EmeritusUtils.getAbilityTypes(ability);
        Animation animation = AssetPool.getInstance().getAbilityAnimation(type);
        
        if (animation == null) { logger.info("TODO, why are some animations returning null?"); }

        // animation.lengthenAnimation();

        model.system.combatAnimation.apply(targets, animation);
    }

    // private void applyBuffs(GameModel model, Entity defender, Map<String, Map.Entry<Float, Float>> buffToTargets) {
    //     // 3. apply buff effects to target 
    //     for (Map.Entry<String, Map.Entry<Float, Float>> entry : buffToTargets.entrySet()) {
    //         float buffChance = entry.getValue().getKey();
    //         float buffValue = entry.getValue().getValue();
    //         String buff = entry.getKey();
    //         if (buffChance < random.nextFloat()) { continue; }
    //         String scalarType = (buffValue % 1 == 0 ? Constants.FLAT : Constants.PERCENT);
    //         // Add status effect to 
    //         defender.get(Summary.class).getStatsNode(buff).add(buff, scalarType, buffValue);

    //         // defender.get(StatusEffects.class).add(buff);
    //         logger.info("{} has {}", defender, buff);
    //         // model.system.floatingText.floater(status, defendingVector, ColorPalette.PURPLE);
    //         model.logger.log(defender, "'s " + buff + " changed");
    //     }
    // }

    private void applyStatusEffects2(GameModel model, Entity target, Ability ability) {
        // Go through all of the different status effects and their probability
        Statistics statistics = target.get(Statistics.class);
        for (Map.Entry<String, Float> entry : ability.statusToTargets.entrySet()) {
            // If the stat chance passes, handle
            float statusChance = Math.abs(entry.getValue());
            if (statusChance < random.nextFloat()) { continue; }
            // Check if the status effect increases a stat
            String status = entry.getKey();
            StatsNode node = null;
            switch (status) {
                case "Health" -> node = statistics.getStatsNode(Constants.HEALTH);
                case "MagicalAttack" -> node = statistics.getStatsNode(Constants.MAGICAL_ATTACK);
                case "MagicalDefense" -> node = statistics.getStatsNode(Constants.MAGICAL_DEFENSE);
                case "Energy" -> node = statistics.getStatsNode(Constants.ENERGY);
                case "PhysicalAttack" -> node = statistics.getStatsNode(Constants.PHYSICAL_ATTACK);
                case "PhysicalDefense" -> node = statistics.getStatsNode(Constants.PHYSICAL_DEFENSE);
                case "Speed" -> node = statistics.getStatsNode(Constants.SPEED);
                case "Climb" -> node = statistics.getStatsNode(Constants.CLIMB);
                case "Move" -> node = statistics.getStatsNode(Constants.MOVE);
                default -> target.get(StatusEffects.class).add(status, ability);
            }

            Vector location = target.get(MovementManager.class).currentTile.get(Vector.class).copy();
            Color c = ColorPalette.getColorOfAbility(ability);
            if (node != null) {
                node.add(ability, Constants.PERCENT, entry.getValue() <  0 ? -.5f : .5f);
                model.logger.log(target, "'s " + status + " " + 
                    (entry.getValue() <  0 ? "decreased" : "increased"));
                    
                model.system.floatingText.floater((entry.getValue() <  0 ? "-" : "+") + StringUtils.capitalize(node.getName()), location, c);
            } else {
                model.system.floatingText.floater(StringUtils.capitalize(status) + "'d", location, c);
                model.logger.log(target, " was " + status + "'d");
            }
            logger.info("{} has {}", target, status);

            // target.get(StatusEffects.class).add(status);
            // logger.log("{0} has {1}", target, status);
            // if (target.get(Vector.class) == null) {
            //     System.out.println("ERROR?!?!?!");
            // }

            // model.system.floatingText.floater(status, target.get(MovementManager.class).currentTile.get(Vector.class).copy(), ColorPalette.PURPLE);
            // model.uiLogQueue.add(target.get(Name.class).value + " was " + status + "'d");
        }
    }

    // private void applyStatusEffects(GameModel model, Entity target, Ability ability) {
    //     // Go through all of the different status effects and their probability
    //     Summary statistics = target.get(Summary.class);
    //     for (Map.Entry<String, Float> entry : ability.statusToTargets.entrySet()) {
    //         // If the stat chance passes, handle
    //         float statusChance = Math.abs(entry.getValue());
    //         if (statusChance < random.nextFloat()) { continue; }
    //         // Check if the status effect increases a stat
    //         String status = entry.getKey();
    //         StatsNode node = null;
    //         switch (status) {
    //             case "health" -> node = statistics.getStatsNode(Constants.HEALTH);
    //             case "magicalAttack" -> node = statistics.getStatsNode(Constants.MAGICAL_ATTACK);
    //             case "magicalDefense" -> node = statistics.getStatsNode(Constants.MAGICAL_DEFENSE);
    //             case "energy" -> node = statistics.getStatsNode(Constants.ENERGY);
    //             case "physicalAttack" -> node = statistics.getStatsNode(Constants.PHYSICAL_ATTACK);
    //             case "physicalDefense" -> node = statistics.getStatsNode(Constants.PHYSICAL_DEFENSE);
    //             default -> target.get(StatusEffects.class).add(status);
    //         }

    //         Vector location = target.get(MovementManager.class).currentTile.get(Vector.class).copy();
    //         if (node != null) {
    //             node.add(ability, Constants.PERCENT, entry.getValue() <  0 ? -.5f : .5f);
    //             model.uiLogQueue.add(target.get(Summary.class).getName() + "'s " + status + " " + 
    //                 (entry.getValue() <  0 ? "decreased" : "increased"));
                    
    //             model.system.floatingText.floater((entry.getValue() <  0 ? "-" : "+") + StringUtils.capitalize(node.getName()), location, ColorPalette.PURPLE);
    //         } else {

    //             // model.system.floatingText.floater("444444", location, ColorPalette.PURPLE);
    //             model.system.floatingText.floater(StringUtils.capitalize(status) + "'d", location, ColorPalette.PURPLE);
    //             model.uiLogQueue.add(target.get(Summary.class).getName()+ " was " + status + "'d");
    //         }
    //         logger.info("{0} has {1}", target, status);

    //         // target.get(StatusEffects.class).add(status);
    //         // logger.log("{0} has {1}", target, status);
    //         // if (target.get(Vector.class) == null) {
    //         //     System.out.println("ERROR?!?!?!");
    //         // }

    //         // model.system.floatingText.floater(status, target.get(MovementManager.class).currentTile.get(Vector.class).copy(), ColorPalette.PURPLE);
    //         // model.uiLogQueue.add(target.get(Name.class).value + " was " + status + "'d");
    //     }
    // }
//    private  void handleCounterStatusEffect(EngineController engine, Entity attacker, CombatEvent event,
//                                                  Entity defender, Statistics defendingStats,
//                                                  Statistics attackingStats, DamageReport health, DamageReport energy) {
////                                                  Statistics attackingStats, int healthDamage, int energyDamage) {
//        // handle all the damage dealt back to the attacker at quarter power
//        int healthDamageToDeal = 0, healthDamageToHeal = 0, energyDamageToDeal = 0, energyDamageToHeal = 0;
//        int healthDamage = health.getTotalDamage(), energyDamage = energy.getTotalDamage();
//        if (energyDamage != 0) {
//            energyDamageToDeal = (int) (energyDamage * .25);
//            logger.log("{0} countered {1} for {2} health damage", defender, attacker, energyDamageToDeal);
//            attackingStats.toEnergy(energyDamageToDeal);
//        }
//        if (healthDamage != 0) {
//            healthDamageToDeal = (int) (healthDamage * .25);
//            logger.log("{0} countered {1} for {2} health damage", defender, attacker, healthDamageToDeal);
//            attackingStats.toHealth(healthDamageToDeal);
//        }
//        // This should be hit most of the time
//        if (healthDamageToDeal != 0 || energyDamageToDeal != 0) {
//            Movement movement = defender.get(Movement.class);
//            movement.gyrate(engine, defender);
//            FloatingTextSystem.floater("Countered!",
//                    defender.get(SpriteAnimation.class).position, ColorPalette.getColorBasedOnAbility(event.ability));
//
//            // Only show one type of damage dealt
//            if (healthDamageToDeal != 0) {
//                FloatingTextSystem.floater(String.valueOf(healthDamageToDeal),
//                        attacker.get(SpriteAnimation.class).position,
//                        ColorPalette.getColorBasedOnAbility(event.ability));
//            } else {
//                FloatingTextSystem.floater(String.valueOf(energyDamageToDeal),
//                        attacker.get(SpriteAnimation.class).position,
//                        ColorPalette.getColorBasedOnAbility(event.ability));
//            }
//        }
//    }

//     private  int tryApplyingBuffsToTargets(Ability ability, Entity target, float chance) {
//         Statistics stats = target.get(Statistics.class);
//         if (chance < random.nextFloat()) { return 0; }

//         Set<Map.Entry<String, Float>> entrySet;

//         if (ability.statusToUser != null) {
//             entrySet = ability.statusToUser.entrySet();
//         } else if (ability.statusToTargets != null) {
//             entrySet = ability.statusToTargets.entrySet();
//         } else {
//             return 0;
//         }

//         float totalValueDifference = 0;
//         for (Map.Entry<String, Float> entry : entrySet) {
//             String key = entry.getKey();
//             float value = entry.getValue();
//             // This means buffs and debuffs to stats can't ever be more than 100%...
//             // This also means you can have a flat and percent debuff from the same ability
//             boolean isFlatAmount = value > 1 || value < -1;
//             String buffOrDebuffType = (isFlatAmount ? Constants.FLAT : Constants.PERCENT);
//             int from = stats.getStatsNode(key).getTotal();
//             stats.getStatsNode(key).add(ability, buffOrDebuffType, value);
//             int to = stats.getStatsNode(key).getTotal();

//             totalValueDifference += value;
//             // Visual/logging confirmation
//             logger.info(target + " [" + key + " " +
//                     (value > 0 ? Constants.UP : Constants.DOWN) + " " +
//                     (isFlatAmount ? (int)value : MathUtils.floatToPercent(value)) + "]");
//             logger.info("{0}''s {1} went from {2} to {3}", target, key, from, to);
// //            logger.log(target + "'s " + key + " went From " + from + " to " + to);
//             gameModel.system.floatingText.floater(EmeritusUtils.getAbbreviation(key) + (value >= 0 ? "+" : "-"),
//                     target.get(Animation.class).position, getColorOfAbility(ability));
//         }

//         return (int) totalValueDifference;
//     }

    public  void payAbilityCosts(CombatEvent event) {
        Entity unit = event.actor;
        Ability ability = event.ability;

        // Get the costs of the ability
        int healthCost = (int) ability.getHealthCost(unit);
        int energyCost = (int) ability.getEnergyCost(unit); 

        if (healthCost != 0) { logger.debug("{} paying {} health for {}", unit, healthCost, ability.name); }
        if (energyCost != 0) { logger.debug("{} paying {} energy for {}", unit, energyCost, ability.name); }

        // Deduct the cost from the user
        Statistics summary = unit.get(Statistics.class);
        ResourceNode health = summary.getResourceNode(Constants.HEALTH);
        health.apply(-healthCost);
        ResourceNode energy = summary.getResourceNode(Constants.ENERGY);
        energy.apply(-energyCost);
    }

    public boolean canPayAbilityCosts(Entity unit, Ability ability) {
        Statistics stats = unit.get(Statistics.class);
        ResourceNode health = stats.getResourceNode(Constants.HEALTH);
        ResourceNode energy = stats.getResourceNode(Constants.ENERGY);
        boolean canPayHealthCosts = health.current >= ability.getHealthCost(unit);
        boolean canPayEnergyCosts = energy.current >= ability.getEnergyCost(unit);
        return canPayHealthCosts && canPayEnergyCosts;
    }

    public  void animateBasedOnAbilityRange(Entity actor, Ability ability, Set<Entity> targets) {
        MovementTrack movementTrack = actor.get(MovementTrack.class);
        if (ability.range <= 1 && ability.area <= 1) {
            Entity tile = targets.iterator().next();
            movementTrack.forwardsThenBackwards(actor, tile);
        } else {
            movementTrack.gyrate(actor);
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
    
    private void announce(GameModel model, String announcement, Entity user, Color color) {        
        model.system.floatingText
            .stationary(
                announcement, 
                user.get(Animation.class).position, 
                color
            );
    }

    private void announceAbility(GameModel model, Ability ability, Entity user) {
        announce(model, ability.name, user, ColorPalette.getColorOfAbility(ability));
    }
}
