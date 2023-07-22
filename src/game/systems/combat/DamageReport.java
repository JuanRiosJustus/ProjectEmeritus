package game.systems.combat;

import constants.ColorPalette;
import constants.Constants;
import game.components.Abilities;
import game.components.Animation;
import game.components.Statistics;
import game.components.StatusEffects;
import game.components.Type;
import game.components.statistics.*;
import game.entity.Entity;
import game.main.GameModel;
import game.stats.node.ResourceNode;
import game.stats.node.StatsNode;
import game.stores.pools.ability.Ability;
import logging.ELogger;
import logging.ELoggerFactory;
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

    private float totalHealthDamage = 0;
    private float totalEnergyDamage = 0;
    private float stabBonus = 0;
    private float stdpPenalty = 0;
    private float criticalBonus = 0;
    private float physicalBonus = 0;
    private float counterPenalty = 0;

    private final static ELogger logger = ELoggerFactory.getInstance().getELogger(DamageReport.class);

    public DamageReport(GameModel model, Entity attacker, Ability ability, Entity defender) {

        Statistics defenderSummary = defender.get(Statistics.class);
        ResourceNode defenderHealth = defenderSummary.getResourceNode(Constants.HEALTH);
        ResourceNode defenderEnergy = defenderSummary.getResourceNode(Constants.ENERGY);

        float baseHealthDamage = ability.getHealthDamage(attacker);
        float baseEnergyDamage = ability.getEnergyDamage(attacker);

        if (baseHealthDamage == 0 && baseEnergyDamage == 0) { return; }

        totalHealthDamage = baseHealthDamage;
        totalEnergyDamage = baseEnergyDamage;

        // 2. Reward units using attacks that are same type
        if (hasSameTypeAttackBonus(attacker, ability)) {
            stabBonus = totalHealthDamage * .5f;
            totalHealthDamage += (baseHealthDamage > 0 ? stabBonus : 0);
            totalEnergyDamage += (baseEnergyDamage > 0 ? stabBonus : 0);
            logger.debug("+{} damage from STAB", stabBonus);
        }

        // 3. Penalize using attacks against units of same type
        if (hasSameTypeAttackBonus(defender, ability)) {
            stdpPenalty = totalHealthDamage * .5f;
            totalHealthDamage -= (baseHealthDamage > 0 ? stdpPenalty : 0);
            totalEnergyDamage -= (baseEnergyDamage > 0 ? stdpPenalty : 0);
            logger.debug("-{} damage from STDP", stdpPenalty);
        }

        // 4 bonus to using physical type attack
        if (isPhysicalType(ability.type) && isMagicalType(defender)) {
            physicalBonus = totalHealthDamage * .25f;
            totalHealthDamage += (baseHealthDamage > 0 ? physicalBonus : 0);
            totalEnergyDamage += (baseEnergyDamage > 0 ? physicalBonus : 0);
            logger.debug("+{} damage from PTAB", physicalBonus);
        }

        // 4.5 determine if the attack is critical
        if (MathUtils.passesChanceOutOf100(.05f)) {
            criticalBonus = totalHealthDamage * 2;
            totalHealthDamage += (baseHealthDamage > 0 ? criticalBonus : 0);
            totalEnergyDamage += (baseEnergyDamage > 0 ? criticalBonus : 0);
            logger.debug("+{} damage from CRIT", criticalBonus);
        }

        if (defender.get(StatusEffects.class).contains(Constants.NEGATE)) {
            counterPenalty = totalHealthDamage * .75f;
            totalHealthDamage -= (baseHealthDamage > 0 ? counterPenalty : 0);
            totalEnergyDamage -= (baseEnergyDamage > 0 ? counterPenalty : 0);
            logger.debug("-{} damage because NGTE", counterPenalty);
        }

        float preDefenseHealthDamage = totalHealthDamage;
        float preDefenseEnergyDamage = totalEnergyDamage;
        // float preDefenseDamage

        // 5. calculate the actual damage by getting defense
        float defenderDefense = getDefense(defender, ability);
        totalHealthDamage = totalHealthDamage * (100 / (100 + defenderDefense));
        totalEnergyDamage = totalEnergyDamage * (100 / (100 + defenderDefense));
    
        int finalHealthDamage = (int) totalHealthDamage;
        int finalEnergyDamage = (int) totalEnergyDamage;

        // Commit the damage to the defender
        if (finalHealthDamage != 0 || finalEnergyDamage != 0) {

            if (baseHealthDamage > 0) {
                logger.debug(
                    "({}(base) + {}{rng}) - {}(def) = {}(final hp) -> {}", 
                    baseHealthDamage, preDefenseHealthDamage - baseHealthDamage, 
                    totalHealthDamage - preDefenseHealthDamage,totalHealthDamage,
                    defenderHealth.current
                );       
            }
            if (baseEnergyDamage > 0) {
                logger.debug(
                    "({}(base) + {}{rng}) - {}(def) = {}(final nrg) -> {}",
                    baseEnergyDamage, preDefenseEnergyDamage - baseEnergyDamage, 
                    totalEnergyDamage - preDefenseEnergyDamage,totalEnergyDamage,
                    defenderEnergy.current
                );
            }

            model.system.floatingText.floater(
                    (criticalBonus > 0 ? "!" : "") +
                            (finalHealthDamage <  0 ? "+" : "") +
                            Math.abs(finalHealthDamage), defender.get(Animation.class).position,
                    ColorPalette.getColorBasedOnAbility(ability));
        }
    }

    private float getDefense(Entity entity, Ability ability) {
        boolean isMagicalType = ability.type.stream().allMatch(type -> magicalTypes.contains(type));
        Statistics defendingStats = entity.get(Statistics.class);
        float total = 1;
        if (isMagicalType) {
            total = defendingStats.getStatsNode(Constants.MAGICAL_DEFENSE).getTotal();
        } else {
            total = defendingStats.getStatsNode(Constants.PHYSICAL_DEFENSE).getTotal();
        }
        return total;
    }

    public int getTotalHealthDamage() { return (int) totalHealthDamage; }
    public int getTotalEnergyDamage() { return (int) totalEnergyDamage; }

    private static boolean isMagicalType(Entity entity) {
        return entity.get(Type.class).getTypes().stream().anyMatch(magicalTypes::contains);
    }

    private static boolean isMagicalType(Set<String> types) {
        return types.stream().anyMatch(magicalTypes::contains);
    }

    private static boolean isPhysicalType(Set<String> types) {
        return types.stream().anyMatch(physicalTypes::contains);
    }

    private static boolean hasSameTypeAttackBonus(Entity entity, Ability ability) {
        return entity.get(Abilities.class).getAbilities().retainAll(ability.type);
    }
}
