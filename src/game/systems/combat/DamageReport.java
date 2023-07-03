package game.systems.combat;

import constants.ColorPalette;
import constants.Constants;
import game.components.Animation;
import game.components.StatusEffects;
import game.components.Types;
import game.components.statistics.Energy;
import game.components.statistics.Health;
import game.components.statistics.Summary;
import game.components.statistics.*;
import game.entity.Entity;
import game.main.GameModel;
import game.stats.node.ScalarNode;
import game.stores.pools.ability.Ability;
import logging.Logger;
import logging.LoggerFactory;
import utils.EmeritusUtils;
import utils.MathUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DamageReport {

    private static final String physicalTypes = "Slash Pierce Blunt Normal";
    private static final String magicalTypes = "Light Air Water Dark Fire Earth";

    private float totalDamage = 0;
    private float BaseDamage = 0;
    private float stabBonus = 0;
    private float stdpPenalty = 0;
    private float criticalBonus = 0;
    private float physicalBonus = 0;
    private float counterPenalty = 0;

    private final static Logger logger = LoggerFactory.instance().logger(DamageReport.class);

    public DamageReport(GameModel model, Entity attacker, Ability ability, Entity defender, boolean toHealth) {

        Summary defenderStats = defender.get(Summary.class);
        Health defenderHealth = defender.get(Health.class);
        Energy defenderEnergy = defender.get(Energy.class);

        BaseDamage = getAbilityDamage(attacker, ability, defender, toHealth);
        if (BaseDamage == 0) { return; }
        totalDamage = BaseDamage;

        // 2. Reward units using attacks that are same type
        if (hasSameType(attacker, ability)) {
            stabBonus = totalDamage * .5f;
            totalDamage += stabBonus;
            logger.info("+{0} from STAB", stabBonus);
        }

        // 3. Penalize using attacks against units of same type
        if (hasSameType(defender, ability)) {
            stdpPenalty = totalDamage * .5f;
            totalDamage -= stdpPenalty;
            logger.info("-{0} from STDP", stdpPenalty);
        }

        // 4 bonus to using physical type attack
        if (isPhysicalType(ability.type) && isMagicalType(defender)) {
            physicalBonus = totalDamage * .25f;
            totalDamage += physicalBonus;
            logger.info("+{0} from PHYS", physicalBonus);
        }

        // 4.5 determine if the attack is critical
        if (MathUtils.passesChanceOutOf100(.05f)) {
            criticalBonus = totalDamage * 2;
            totalDamage += criticalBonus;
            logger.info("+{0} from CRIT", criticalBonus);
        }

        if (defender.get(StatusEffects.class).remove(Constants.NEGATE)) {
            counterPenalty = totalDamage * .75f;
            totalDamage -= counterPenalty;
            logger.info("-{0} from NGTE", counterPenalty);
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

            logger.info("[Base DMG: {0}] -> [Rng DMG: {1}] -> [Final DMG: {2}]",
                    BaseDamage, preDefenseDamage, finalDamage);
            logger.info("{0}''s {1} deals {2} {3} Damage to {4}",
                    attacker, ability.name, finalDamage, (toHealth ? "Health" : "Energy"), defender);
            logger.info("{0}''s {1}: from {2} -> {3}",
                    defender, (toHealth ? "Health" : "Energy"), starting, ending);
            model.system.floatingText.floater(
                    (criticalBonus > 0 ? "!" : "") +
                            (finalDamage <  0 ? "+" : "") +
                            Math.abs(finalDamage), defender.get(Animation.class).position,
                    ColorPalette.getColorBasedOnAbility(ability));
        }
    }

    public static float getAbilityScalingDamage(Entity user, Ability ability, boolean toHealth) {
        Map<String, Float> ratios = (toHealth ? ability.healthDamageScaling : ability.energyDamageScaling);
        if (ratios.isEmpty()) { return 0; }

        Summary statistics = user.get(Summary.class);
        float damage = 0;

        for (Map.Entry<String, Float> entry : ratios.entrySet()) {
            float subtotal = statistics.getScalarNode(entry.getKey()).getTotal() * entry.getValue();
            damage += subtotal;
            String abbreviation = EmeritusUtils.getAbbreviation(entry.getKey());
            String percentage = MathUtils.floatToPercent(entry.getValue());
            logger.info("{0} deals {1} additional damage ({2} of {3})", ability.name, subtotal, percentage, abbreviation);
        }

        return damage;
    }

    public static float getAbilityPercentageDamage(Entity defender, Ability ability, boolean toHealth) {
        Map<String, Float> ratios = toHealth ? ability.healthDamagePercent : ability.energyDamagePercent;
        if (ratios.isEmpty()) { return 0; }

        Summary statistics = defender.get(Summary.class);
        Health health = defender.get(Health.class);
        Energy energy = defender.get(Energy.class);

        float damage = 0;
        String nodeType = toHealth ? Constants.HEALTH : Constants.ENERGY;
        ScalarNode node = statistics.getScalarNode(nodeType);
        int current = toHealth ? health.current : energy.current;

        for (Map.Entry<String, Float> entry : ratios.entrySet()) {
            String key = entry.getKey();
            float value = entry.getValue();
            float subtotal = 0;
            switch (key) {
                case Constants.MISSING -> subtotal += (node.getTotal() - current) * value;
                case Constants.CURRENT -> subtotal += value * current;
                case Constants.MAX -> subtotal += value * node.getTotal();
                default -> logger.info("Unsupported percentage type");
            }
            damage += subtotal;
            logger.info("{0} deals {1} additional damage ({2} of {3})", ability.name, subtotal, value, key);
        } 

        return damage;
    }


    public static float getAbilityDamage(Entity attacker, Ability ability, Entity defender, boolean toHealth) {

        // calculate the flat/base damage
        int base = (toHealth ? ability.healthDamageBase : ability.energyDamageBase);

        // calculate the scaling damage based on the attackers stats
        float scaling = getAbilityScalingDamage(attacker, ability, toHealth);

        // calculate percentage damage based on the defenders stats, Missing, Current, and Max
        float percentage = getAbilityPercentageDamage(defender, ability, toHealth);

        float total = (base + scaling + percentage);

        if (total != 0) {
            logger.info("{3}(Total Damage) = {0}(Base) + {1}(Scaling) + {2}(Percentage)", base, scaling, percentage, total);
        }

        return total;
    }

    private float getDefense(Entity entity, Ability ability) {
        boolean isMagicalType = ability.type.stream().allMatch(type -> magicalTypes.contains(type));
        Summary defendingStats = entity.get(Summary.class);
        float total = 1;
        if (isMagicalType) {
            total = defendingStats.getScalarNode(Constants.MAGICAL_DEFENSE).getTotal();
        } else {
            total = defendingStats.getScalarNode(Constants.PHYSICAL_DEFENSE).getTotal();
        }
        return total;
    }

    public int getTotalDamage() {
        return (int) totalDamage;
    }

    private static boolean isMagicalType(Entity entity) {
        return entity.get(Summary.class).getTypes().stream().anyMatch(magicalTypes::contains);
//        return Arrays.stream(entity.get(Statistics.class).getStringNode(Constants.TYPE).value.split("\\s+"))
//                .anyMatch(magicalTypes::contains);
    }

    private static boolean isMagicalType(Set<String> types) {
        return types.stream().anyMatch(magicalTypes::contains);
    }

    private static boolean isPhysicalType(Set<String> types) {
        return types.stream().anyMatch(physicalTypes::contains);
    }

//    private static boolean hasSameType(Entity entity, Ability ability) {
//        return ability.type.retainAll()//UnitPool.instance().getUnit(entity.get(Statistics.class).getStringNode("name").value)//Arrays.stream(entity.get(Statistics.class).getStringNode(Constants.TYPE).value.split("\\s+")).anyMatch()
//    }
    private static boolean hasSameType(Entity entity, Ability ability) {
        return entity.get(Summary.class).getAbilities().retainAll(ability.type);
    }
}
