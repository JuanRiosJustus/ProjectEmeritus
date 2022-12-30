package game.systems.combat;

import constants.ColorPalette;
import constants.Constants;
import game.GameModel;
import game.components.SpriteAnimation;
import game.components.StatusEffects;
import game.components.statistics.Energy;
import game.components.statistics.Health;
import game.components.statistics.Statistics;
import game.entity.Entity;
import game.stats.node.ScalarNode;
import game.stores.pools.ability.Ability;
import logging.Logger;
import logging.LoggerFactory;
import utils.EmeritusUtils;
import utils.MathUtils;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class DamageReport {

    private static final String physicalTypes = "Slash Pierce Blunt Normal";
    private static final String magicalTypes = "Light Water Dark Fire Earth";

    private float totalDamage = 0;
    private float BaseDamage = 0;
    private float stabBonus = 0;
    private float stdpPenalty = 0;
    private float criticalBonus = 0;
    private float physicalBonus = 0;
    private float counterPenalty = 0;

    private final Logger logger = LoggerFactory.instance().logger(getClass());

    public DamageReport(GameModel model, Entity attacker, Ability ability, Entity defender, boolean toHealth) {

        Statistics defenderStats = defender.get(Statistics.class);
        Health defenderHealth = defender.get(Health.class);
        Energy defenderEnergy = defender.get(Energy.class);

        BaseDamage = getAbilityDamage(attacker, ability, defender, toHealth);
        if (BaseDamage == 0) { return; }
        totalDamage = BaseDamage;

        // 2. Reward units using attacks that are same type
        if (hasSameType(attacker, ability)) {
            stabBonus = totalDamage * .5f;
            totalDamage += stabBonus;
            logger.log("+{0} from STAB", stabBonus);
        }

        // 3. Penalize using attacks against units of same type
        if (hasSameType(defender, ability)) {
            stdpPenalty = totalDamage * .5f;
            totalDamage -= stdpPenalty;
            logger.log("-{0} from STDP", stdpPenalty);
        }

        // 4 bonus to using physical type attack
        if (isPhysicalType(ability.types) && isMagicalType(defender)) {
            physicalBonus = totalDamage * .25f;
            totalDamage += physicalBonus;
            logger.log("+{0} from PHYS", physicalBonus);
        }

        // 4.5 determine if the attack is critical
        if (MathUtils.passesChanceOutOf100(.05f)) {
            criticalBonus = totalDamage * 2;
            totalDamage += criticalBonus;
            logger.log("+{0} from CRIT", criticalBonus);
        }

        if (defender.get(StatusEffects.class).remove(Constants.NEGATE)) {
            counterPenalty = totalDamage * .75f;
            totalDamage -= counterPenalty;
            logger.log("-{0} from NGTE", counterPenalty);
        }

        float preDefenseDamage = totalDamage;

        // 5. calculate the actual damage by getting defense
        float defenderDefense = getDefense(defender, ability);
        totalDamage = totalDamage * (100 / (100 + defenderDefense));
        int finalDamage = (int) totalDamage;

        // Commit the damage to the defender
        if (finalDamage != 0) {

            int starting = (toHealth ? defenderHealth.current : defenderEnergy.current);
            if (toHealth) { defenderHealth.apply(-finalDamage); } else { defenderEnergy.apply(-finalDamage); }
            int ending = (toHealth ? defenderHealth.current : defenderEnergy.current);

            logger.log("[Base DMG: {0}] -> [Rng DMG: {1}] -> [Final DMG: {2}]",
                    BaseDamage, preDefenseDamage, finalDamage);
            logger.log("{0}''s {1} deals {2} {3} Damage to {4}",
                    attacker, ability.name, finalDamage, (toHealth ? "Health" : "Energy"), defender);
            logger.log("{0}''s {1}: from {2} -> {3}",
                    defender, (toHealth ? "Health" : "Energy"), starting, ending);
            model.system.floatingText.floater(
                    (criticalBonus > 0 ? "!" : "") +
                            (finalDamage <  0 ? "+" : "") +
                            Math.abs(finalDamage), defender.get(SpriteAnimation.class).position,
                    ColorPalette.getColorBasedOnAbility(ability));
        }
    }

    private float getAbilityDamage(Entity attacker, Ability ability, Entity defender, boolean toHealth) {
        Statistics attackerStats = attacker.get(Statistics.class);
        Statistics defenderStats = defender.get(Statistics.class);

        // calculate the flat/base damage
        int baseDamage = (toHealth ? ability.baseHealthDamage : ability.baseEnergyDamage);

        // calculate the scaling damage based on the attackers stats
        StringBuilder scalingMonitor = new StringBuilder();
        float scalingDamage = 0;
        Map<String, Float> scalingRatios = (toHealth ? ability.scalingHealthDamage : ability.scalingEnergyDamage);

        // If there are scaling ratios, calculate, get the total to add to the base. These values between 0 <= x <= 1
        if (scalingRatios.size() > 0) {
            for (Map.Entry<String, Float> entry : scalingRatios.entrySet()) {
                scalingDamage += attackerStats.getScalarNode(entry.getKey()).getTotal() * entry.getValue();
                scalingMonitor.append(MessageFormat.format("({0}: {1})",
                        EmeritusUtils.getAbbreviation(entry.getKey()), MathUtils.floatToPercent(entry.getValue())));
            }
        }

        // calculate percentage damage: Missing, Current and Max
        StringBuilder percentMonitor = new StringBuilder();
        float percentageDamage = 0;
        Map<String, Float> percentDamages = (toHealth ? ability.percentHealthDamage : ability.percentEnergyDamage);
        if (percentDamages.size() > 0) {
            String nodeType = (toHealth ? Constants.HEALTH : Constants.ENERGY);
            Health defenderHealth = defender.get(Health.class);
            Energy defenderEnergy = defender.get(Energy.class);
            for (Map.Entry<String, Float> entry : percentDamages.entrySet()) {
                String key = entry.getKey();
                float value = entry.getValue();
                switch (key) {
                    case Constants.MISSING -> {
                        int missing = defenderStats.getScalarNode(nodeType).getTotal() -
                                (toHealth ? defenderHealth.current : defenderEnergy.current);
                        percentageDamage += missing * value;
                    }
                    case Constants.CURRENT -> percentageDamage += value *
                            (toHealth ? defenderHealth.current : defenderEnergy.current);
                    case Constants.MAX -> percentageDamage += value * attackerStats.getScalarNode(nodeType).getTotal();
                    default -> logger.log("Unsupported percentage type");
                }
                percentMonitor.append(MessageFormat.format("({0}: {1})", key, MathUtils.floatToPercent(value)));
            }
        }

        float total = (baseDamage + scalingDamage + percentageDamage);

        if (total != 0) {
            logger.log(
                    "{0}''s Damage calculations: [BaseDamage: {1}] [ScalingDamage: {2}] [PercentDamage: {3}]",
                    ability.name, baseDamage,
                    (scalingMonitor.length() <= 0 ? 0 :
                            MessageFormat.format("{0} {1}", scalingDamage, scalingMonitor)),
                    (percentMonitor.length() <= 0 ? 0 :
                            MessageFormat.format("{0} {1}", percentageDamage, percentMonitor))
            );
        }

        return total;
    }

    private float getDefense(Entity entity, Ability ability) {
        if (ability.defendingStats.isEmpty()) { return 0; }
        float total = 0;
        for (Map.Entry<String, Float> defendingStat : ability.defendingStats.entrySet()) {
            ScalarNode node = entity.get(Statistics.class).getScalarNode(defendingStat.getKey());
            if (node == null) { System.err.println("Unable to parse key " + defendingStat.getKey()); return 0; }
            total += node.getTotal() * defendingStat.getValue();
        }
        return total;
    }

    public int getTotalDamage() {
        return (int) totalDamage;
    }

    private static boolean isMagicalType(Entity entity) {
        return Arrays.stream(entity.get(Statistics.class).getStringNode(Constants.TYPE).value.split("\\s+"))
                .anyMatch(magicalTypes::contains);
    }

    private static boolean isMagicalType(Set<String> types) {
        return types.stream().anyMatch(magicalTypes::contains);
    }

    private static boolean isPhysicalType(Set<String> types) {
        return types.stream().anyMatch(physicalTypes::contains);
    }

    private static boolean hasSameType(Entity entity, Ability ability) {
        return Arrays.stream(entity.get(Statistics.class).getStringNode(Constants.TYPE).value.split("\\s+"))
                .anyMatch(ability.types::contains);
    }
}
