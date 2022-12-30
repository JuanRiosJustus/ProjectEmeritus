package game.systems;


import constants.ColorPalette;
import constants.Constants;
import game.GameModel;
import game.components.SpriteAnimation;
import game.components.*;
import game.components.statistics.Energy;
import game.components.statistics.Health;
import game.components.statistics.Resource;
import game.components.statistics.Statistics;
import game.components.Vector;
import game.components.Tile;
import game.entity.Entity;
import game.components.Movement;
import game.stores.pools.ability.Ability;
import game.systems.combat.CombatEvent;
import game.systems.combat.DamageReport;
import logging.Logger;
import logging.LoggerFactory;
import utils.EmeritusUtils;
import utils.MathUtils;

import java.util.*;
import java.util.List;

public class CombatSystem extends GameSystem {

    private final Map<Entity, CombatEvent> queue = new HashMap<>();
    private final SplittableRandom random = new SplittableRandom();
    private final Logger logger = LoggerFactory.instance().logger(CombatSystem.class);
    private final String physicalTypes = "Slash Pierce Blunt Normal";
    private final String magicalTypes = "Light Water Dark Fire Earth";
    private GameModel gameModel;

    @Override
    public void update(GameModel model, Entity unit) {
        // 1. if the current unit is not in queue, skip
        CombatEvent event = queue.get(unit);
        gameModel = model;
        if (event == null) { return; }

        // 2. wait next loop to check if attacker has finished animating
        boolean isFastForwarding = model.ui.getBoolean(Constants.SETTINGS_UI_FASTFORWARDTURNS); //engine.model.ui.settings.fastForward.isSelected();
        Movement movement = unit.get(Movement.class);
        if (!isFastForwarding && movement.isMoving()) { return; }

        // 3. Finish the combat by applying the damage to the defending units. Remove from queue
        finishCombat(model, unit);
        queue.remove(unit);
    }

    public void startCombat(GameModel model, Entity attacker, Ability ability, List<Entity> attackAt) {

        // 0. if the ability can't affect the user, remove if available
        if (!ability.canHitUser) { attackAt.remove(attacker.get(ActionManager.class).tileOccupying); }
        if (attackAt.isEmpty()) { return; }

        // 1. Check that unit has resources for ability
        if (!canPayAbilityCosts(attacker, ability)) { return; }

        // 2. Extract the units from the given selection of tiles
        Set<Entity> defenders = extractUnitsFromTiles(ability, attackAt);

        // 2.5 This can happen if player chooses empty square
        if (defenders.isEmpty()) { return; }

        // 3. Animate based on the abilities range
        animateBasedOnAbilityRange(attacker, ability, defenders);

        // 4. Draw ability name to screen
        model.system.floatingText
                .dialogue(ability.name, attacker.get(SpriteAnimation.class).position, ColorPalette.getColorBasedOnAbility(ability));

        // 5. Cache the combat state...
        queue.put(attacker, new CombatEvent(attacker, ability, defenders));
    }

    private void finishCombat(GameModel model, Entity attacker) {
        CombatEvent event = queue.get(attacker);

        logger.banner("{0} starts combat", attacker);

        // 0. Pay the ability costs
        payAbilityCosts(event);

        // 1.5. apply buffs and debuffs to user
        int value = tryApplyingBuffsToUser(event.ability, event.attacker, event.ability.buffsToUserChance);

        // 2 Draw the correct combat animations
        if (value != 0) { model.system.combatAnimation.apply(attacker, (value > 0 ? "Buff" : "Debuff")); }

        tryApplyingStatusToUser(model, event.ability, event.attacker);

        // 3. Execute a hit on all selected defenders
        for (Entity defender : event.defenders) {
            boolean hit = MathUtils.passesChanceOutOf100(event.ability.accuracy);
            logger.log("{0} uses {1} on {2}", attacker, event.ability.name, defender);

            // 4. Attack if possible
            if (hit) {
                executeHit(model, attacker, event, defender);
            } else {
                executeMiss(model, attacker, event, defender);
            }
            logger.log("===========================================================");
        }
        logger.banner("{0} finishes combat", attacker);
    }

    private  void tryApplyingStatusToUser(GameModel model, Ability ability, Entity attacker) {
        for (Map.Entry<String, Float> entry : ability.statusToUser.entrySet()) {
            if (!MathUtils.passesChanceOutOf100(entry.getValue())) { continue; }
            String statusToApply = entry.getKey();
            logger.log("Applying {0} to {1}", statusToApply, attacker);
            attacker.get(StatusEffects.class).add(statusToApply);
            model.system.floatingText.floater(statusToApply,
                    attacker.get(SpriteAnimation.class).position, ColorPalette.getColorBasedOnAbility(ability));

        }
    }

    private  void executeMiss(GameModel model, Entity attacker, CombatEvent event, Entity defender) {
        Vector vector = attacker.get(SpriteAnimation.class).position;
        model.system.floatingText.floater("Missed!", vector, ColorPalette.getColorBasedOnAbility(event.ability));
        logger.log("{0} misses {1}", attacker, defender);
    }

    private  void executeHit(GameModel model, Entity attacker, CombatEvent event, Entity defender) {

        // 0. Setup
        Statistics defendingStats = defender.get(Statistics.class);
        Vector defendingVector = defender.get(SpriteAnimation.class).position;
        Statistics attackingStats = attacker.get(Statistics.class);
        Vector attackingVector = attacker.get(SpriteAnimation.class).position;

        // 1. Calculate damage
        DamageReport health = new DamageReport(model, attacker, event.ability, defender, true);
        int healthDamage = health.getTotalDamage();

        DamageReport energy = new DamageReport(model, attacker, event.ability, defender, false);
        int energyDamage = energy.getTotalDamage();

        // get total buffOrDebuff and apply
        int buffValue = tryApplyingBuffsToTargets(event.ability, defender, event.ability.buffsToTargetsChance);
//        String type = EmeritusUtils.getAbilityTypes(event.ability);

        // Draw the correct combat animations
        applyAnimationsBasedOnAbility(model, event.ability, defender, health, energy, buffValue);

        // 2. If the defender has no more health, just remove
        if (model.queue.removeIfNoCurrentHealth(defender)) {
            model.system.floatingText.floater("Dead!",
                    defender.get(SpriteAnimation.class).position, ColorPalette.getColorBasedOnAbility(event.ability));
            return;
        }

        // 3. apply status effects tp target
        for (Map.Entry<String, Float> entry : event.ability.statusToTargets.entrySet()) {
            float statusChance = entry.getValue();
            if (statusChance < random.nextFloat()) { continue; }
            String status = entry.getKey();
            defender.get(StatusEffects.class).add(status);
            logger.log("{0} has {1}", defender, status);
        }

        // don't move if already performing some action
        Movement movement = defender.get(Movement.class);
        if (movement.isMoving()) { return; }

        // defender has already queued an attack/is the attacker, don't animate
        if (queue.containsKey(defender)) { return; }

        movement.wiggle(defender);
    }

    private  void applyAnimationsBasedOnAbility(GameModel model, Ability ability, Entity defender,
                                                DamageReport health, DamageReport energy, int buffValue) {

        int healthDamage = health.getTotalDamage();
        int energyDamage = energy.getTotalDamage();

        ArrayList<String> animationsToApply = new ArrayList<>();

        String type = EmeritusUtils.getAbilityTypes(ability);

        if (type != null) { animationsToApply.add(type); }

        if (buffValue != 0) { animationsToApply.add((buffValue > 0 ? "Buff" : "Debuff")); }

        if (animationsToApply.size() == 0) { return; }

        model.system.combatAnimation.apply(defender, animationsToApply.toArray(new String[0]));
    }

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

    private  int tryApplyingBuffsToUser(Ability ability, Entity target, float chance) {
        return tryApplyingBuffsToTargets(ability, target, chance, true);
    }

    private  int tryApplyingBuffsToTargets(Ability ability, Entity target, float chance) {
        return tryApplyingBuffsToTargets(ability, target, chance, false);
    }

    private  int tryApplyingBuffsToTargets(Ability ability, Entity target, float chance, boolean targetIsUser) {
        Statistics stats = target.get(Statistics.class);
        if (chance < random.nextFloat()) { return 0; }

        Set<Map.Entry<String, Float>> entrySet = (targetIsUser ?
                ability.buffsToUser.entrySet() : ability.buffsToTargets.entrySet());

        float totalValueDifference = 0;
        for (Map.Entry<String, Float> entry : entrySet) {
            String key = entry.getKey();
            float value = entry.getValue();
            // This means buffs and debuffs to stats can't ever be more than 100%...
            // This also means you can have a flat and percent debuff from the same ability
            boolean isFlatAmount = value > 1 || value < -1;
            String buffOrDebuffType = (isFlatAmount ? Constants.FLAT : Constants.PERCENT);
            int from = stats.getScalarNode(key).getTotal();
            stats.getScalarNode(key).add(ability, buffOrDebuffType, value);
            int to = stats.getScalarNode(key).getTotal();

            totalValueDifference += value;
            // Visual/logging confirmation
            logger.log(target + " [" + key + " " +
                    (value > 0 ? Constants.UP : Constants.DOWN) + " " +
                    (isFlatAmount ? (int)value : MathUtils.floatToPercent(value)) + "]");
            logger.log("{0}''s {1} went from {2} to {3}", target, key, from, to);
//            logger.log(target + "'s " + key + " went From " + from + " to " + to);
            gameModel.system.floatingText.floater(EmeritusUtils.getAbbreviation(key) + (value >= 0 ? "+" : "-"),
                    target.get(SpriteAnimation.class).position, ColorPalette.getColorBasedOnAbility(ability));
        }

        return (int) totalValueDifference;
    }

    public  void payAbilityCosts(CombatEvent event) {
        Entity unit = event.attacker;
        Ability ability = event.ability;

        // Get the costs of the ability
        int healthCost = calculateCosts(unit, ability, Constants.HEALTH);
        int energyCost = calculateCosts(unit, ability, Constants.ENERGY);

        event.energyCost = energyCost;
        event.healthCost = healthCost;

        if (healthCost != 0) { logger.log("{0} paying {1} health for {2}", unit, healthCost, ability.name); }
        if (energyCost != 0) { logger.log("{0} paying {1} energy for {2}", unit, energyCost, ability.name); }

        // Deduct the cost from the user
        unit.get(Health.class).apply(-healthCost);
        unit.get(Energy.class).apply(-energyCost);
    }

//    private  int calculateEnergyCosts(Entity unit, Ability ability, String costType) {
//        float energyCost = ability.baseEnergyCost;
//        // If mapping has entries, then must have percentage health costs
//        if (ability.percentEnergyCost.isEmpty()) { return (int) energyCost; }
//        Statistics stats = unit.get(Statistics.class);
//        for (Map.Entry<String, Float> entry : ability.percentEnergyCost.entrySet()) {
//            String key = entry.getKey();
//            float value = entry.getValue();
//            float percentageEnergyCost = 0;
//            switch (key) {
//                case Constants.MISSING -> {
//                    int missing = stats.getScalarNode(Constants.ENERGY).getTotal() - stats.getCurrentEnergy();
//                    percentageEnergyCost = missing * value;
//                }
//                case Constants.CURRENT -> percentageEnergyCost = stats.getCurrentEnergy() * value;
//                case Constants.MAX -> percentageEnergyCost = stats.getScalarNode(Constants.ENERGY).getTotal() * value;
//                default -> logger.log("Unsupported percentage type");
//            }
//            energyCost += percentageEnergyCost;
//        }
//
//        return (int) energyCost;
//    }

    private  int calculateCosts(Entity unit, Ability ability, String costType) {
        costType = costType.toLowerCase(Locale.ROOT);
        // Get the base cost
        float cost = (costType.equals("health") ? ability.baseHealthCost : ability.baseEnergyCost);
        // If no percentage costs, return the base
        Map<String, Float> typeToCostMap = (costType.equals("health") ?
                ability.percentHealthCost : ability.percentEnergyCost);
        if (typeToCostMap.isEmpty()) { return (int) cost; }
        // Get the percentage costs to calculate
        Statistics stats = unit.get(Statistics.class);
        String nodeToCalculate = (costType.equals("health") ? Constants.HEALTH : Constants.ENERGY);
        Resource resource = (costType.equals("health") ? unit.get(Health.class) : unit.get(Energy.class));

        for (Map.Entry<String, Float> typeAndCost : typeToCostMap.entrySet()) {
            String key = typeAndCost.getKey();
            float value = typeAndCost.getValue();
            float percentCost = 0;
            switch (key) {
                case Constants.MISSING -> {
                    float missing = (stats.getScalarNode(nodeToCalculate).getTotal() - resource.current) * value;
                    percentCost = missing * value;
                }
                case Constants.CURRENT -> percentCost = resource.current * value;
                case Constants.MAX -> percentCost = stats.getScalarNode(nodeToCalculate).getTotal() * value;
                default -> logger.log("Unsupported percentage type");
            }
            cost += percentCost;
        }
        return (int) cost;
    }

//    private  int calculateHealthCosts(Entity unit, Ability ability) {
//        float healthCost = ability.baseHealthCost;
//        // If mapping has entries, then must have percentage health costs
//        if (ability.percentHealthCost.isEmpty()) { return (int) healthCost; }
//        Statistics stats = unit.get(Statistics.class);
//        for (Map.Entry<String, Float> entry : ability.percentHealthCost.entrySet()) {
//            String key = entry.getKey();
//            float value = entry.getValue();
//            float percentageHealthCost = 0;
//            switch (key) {
//                case Constants.MISSING -> {
//                    int missing = stats.getScalarNode(Constants.HEALTH).getTotal() - stats.getCurrentHealth();
//                    percentageHealthCost = missing * value;
//                }
//                case Constants.CURRENT -> percentageHealthCost = stats.getCurrentHealth() * value;
//                case Constants.MAX -> percentageHealthCost = stats.getScalarNode(Constants.HEALTH).getTotal() * value;
//                default -> logger.log("Unsupported percentage type");
//            }
//            healthCost += percentageHealthCost;
//        }
//
//        return (int) healthCost;
//    }

    private  boolean canPayAbilityCosts(Entity unit, Ability ability) {
        Statistics stats = unit.get(Statistics.class);
        Health health = unit.get(Health.class);
        Energy energy = unit.get(Energy.class);
        boolean canPayHealthCosts = health.current >= calculateCosts(unit, ability, "Health");
        boolean canPayEnergyCosts = energy.current >= calculateCosts(unit, ability, "Energy");
        return canPayHealthCosts && canPayEnergyCosts;
    }

    public  void animateBasedOnAbilityRange(Entity unit, Ability ability, Set<Entity> targets) {
        Movement movement = unit.get(Movement.class);
        if (ability.range == 1) {
            ActionManager tracker = targets.iterator().next().get(ActionManager.class);
            Entity tile = tracker.tileOccupying;
            movement.forwardsThenBackwards(unit, tile);
        } else {
            movement.gyrate(unit);
        }
    }

    public  Set<Entity> extractUnitsFromTiles(Ability ability, List<Entity> tiles) {
        Set<Entity> set = new HashSet<>();
        for (Entity tile : tiles) {
            Tile details = tile.get(Tile.class);
            if (details.unit == null) { continue; }
            set.add(details.unit);
        }
        return set;
    }


    /*
     *   ██████╗ ██████╗ ███╗   ███╗██████╗  █████╗ ████████╗     ██████╗ █████╗ ██╗      ██████╗██╗   ██╗██╗      █████╗ ████████╗██╗ ██████╗ ███╗   ██╗███████╗
     *  ██╔════╝██╔═══██╗████╗ ████║██╔══██╗██╔══██╗╚══██╔══╝    ██╔════╝██╔══██╗██║     ██╔════╝██║   ██║██║     ██╔══██╗╚══██╔══╝██║██╔═══██╗████╗  ██║██╔════╝
     *  ██║     ██║   ██║██╔████╔██║██████╔╝███████║   ██║       ██║     ███████║██║     ██║     ██║   ██║██║     ███████║   ██║   ██║██║   ██║██╔██╗ ██║███████╗
     *  ██║     ██║   ██║██║╚██╔╝██║██╔══██╗██╔══██║   ██║       ██║     ██╔══██║██║     ██║     ██║   ██║██║     ██╔══██║   ██║   ██║██║   ██║██║╚██╗██║╚════██║
     *  ╚██████╗╚██████╔╝██║ ╚═╝ ██║██████╔╝██║  ██║   ██║       ╚██████╗██║  ██║███████╗╚██████╗╚██████╔╝███████╗██║  ██║   ██║   ██║╚██████╔╝██║ ╚████║███████║
     *   ╚═════╝ ╚═════╝ ╚═╝     ╚═╝╚═════╝ ╚═╝  ╚═╝   ╚═╝        ╚═════╝╚═╝  ╚═╝╚══════╝ ╚═════╝ ╚═════╝ ╚══════╝╚═╝  ╚═╝   ╚═╝   ╚═╝ ╚═════╝ ╚═╝  ╚═══╝╚══════╝
    */

}
