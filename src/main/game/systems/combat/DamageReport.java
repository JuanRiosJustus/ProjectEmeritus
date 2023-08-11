package main.game.systems.combat;

import main.constants.ColorPalette;
import main.constants.Constants;
import main.game.components.Abilities;
import main.game.components.Animation;
import main.game.components.Statistics;
import main.game.components.StatusEffects;
import main.game.components.Type;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stats.node.ResourceNode;
import main.game.stores.pools.ability.Ability;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.MathUtils;

import java.util.Set;

public class DamageReport {

    private static final String physicalTypes = "Slash Pierce Blunt Normal";
    private static final String magicalTypes = "Light Air Water Dark Fire Earth";

    private float finalHealthDamage = 0;
    private float finalEnergyDamage = 0;
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

        finalHealthDamage = baseHealthDamage;
        finalEnergyDamage = baseEnergyDamage;

        // 2. Reward units using attacks that are same type
        if (hasSameTypeAttackBonus(attacker, ability)) {
            stabBonus = finalHealthDamage * .5f;
            finalHealthDamage += (baseHealthDamage > 0 ? stabBonus : 0);
            finalEnergyDamage += (baseEnergyDamage > 0 ? stabBonus : 0);
            logger.debug("+{} damage from STAB", stabBonus);
        }

        // 3. Penalize using attacks against units of same type
        if (hasSameTypeAttackBonus(defender, ability)) {
            stdpPenalty = finalHealthDamage * .5f;
            finalHealthDamage -= (baseHealthDamage > 0 ? stdpPenalty : 0);
            finalEnergyDamage -= (baseEnergyDamage > 0 ? stdpPenalty : 0);
            logger.debug("-{} damage from STDP", stdpPenalty);
        }

        // 4 bonus to using physical type attack
        if (isPhysicalType(ability.getTypes()) && isMagicalType(defender)) {
            physicalBonus = finalHealthDamage * .25f;
            finalHealthDamage += (baseHealthDamage > 0 ? physicalBonus : 0);
            finalEnergyDamage += (baseEnergyDamage > 0 ? physicalBonus : 0);
            logger.debug("+{} damage from PTAB", physicalBonus);
        }

        // 4.5 determine if the attack is critical
        if (MathUtils.passesChanceOutOf100(.05f)) {
            criticalBonus = finalHealthDamage * 2;
            finalHealthDamage += (baseHealthDamage > 0 ? criticalBonus : 0);
            finalEnergyDamage += (baseEnergyDamage > 0 ? criticalBonus : 0);
            logger.debug("+{} damage from CRIT", criticalBonus);
        }

        if (defender.get(StatusEffects.class).contains(Constants.NEGATE)) {
            counterPenalty = finalHealthDamage * .75f;
            finalHealthDamage -= (baseHealthDamage > 0 ? counterPenalty : 0);
            finalEnergyDamage -= (baseEnergyDamage > 0 ? counterPenalty : 0);
            logger.debug("-{} damage because NGTE", counterPenalty);
        }

        float preDefenseHealthDamage = finalHealthDamage;
        float preDefenseEnergyDamage = finalEnergyDamage;
        // float preDefenseDamage

        // 5. calculate the actual damage by getting defense
        float defenderDefense = getDefense(defender, ability);;
        if (ability.hasTag(Ability.IGNORE_DEFENSES)) {
            defenderDefense = 0;
        }
        finalHealthDamage = finalHealthDamage * (100 / (100 + defenderDefense));
        finalEnergyDamage = finalEnergyDamage * (100 / (100 + defenderDefense));

        // Commit the damage to the defender
        if (finalHealthDamage != 0 || finalEnergyDamage != 0) {
            if (finalHealthDamage != 0) {
                logger.debug(
                    "({}(base) + {}{rng}) - {}(def) = {}(final hp) -> {}", 
                    baseHealthDamage, preDefenseHealthDamage - baseHealthDamage, 
                    finalHealthDamage - preDefenseHealthDamage, finalHealthDamage,
                    defenderHealth.getCurrent()
                );
                model.system.floatingText.floater(
                        (criticalBonus > 0 ? "!" : "") +
                                (finalHealthDamage <  0 ? "+" : "") +
                                Math.abs((int)finalHealthDamage), defender.get(Animation.class).position,
                        ColorPalette.getColorOfAbility(ability));
            }
            if (finalEnergyDamage != 0) {
                logger.debug(
                    "({}(base) + {}{rng}) - {}(def) = {}(final ep) -> {}",
                    baseEnergyDamage, preDefenseEnergyDamage - baseEnergyDamage, 
                    finalEnergyDamage - preDefenseEnergyDamage, finalEnergyDamage,
                    defenderEnergy.getCurrent()
                );
                model.system.floatingText.floater(
                        (criticalBonus > 0 ? "!" : "") +
                                (finalEnergyDamage <  0 ? "+" : "") +
                                Math.abs((int)finalEnergyDamage) + " EP", defender.get(Animation.class).position,
                        ColorPalette.getColorOfAbility(ability));
            }
        }
    }

    private float getDefense(Entity entity, Ability ability) {
        boolean isMagicalType = ability.getTypes()
                .stream()
                .allMatch(type -> magicalTypes.contains(type));
        Statistics defendingStats = entity.get(Statistics.class);
        float total = 1;
        if (isMagicalType) {
            total = defendingStats.getStatsNode(Constants.MAGICAL_DEFENSE).getTotal();
        } else {
            total = defendingStats.getStatsNode(Constants.PHYSICAL_DEFENSE).getTotal();
        }
        return total;
    }

    public int getFinalHealthDamage() { return (int) finalHealthDamage; }
    public int getFinalEnergyDamage() { return (int) finalEnergyDamage; }

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
        return entity.get(Abilities.class)
                .getAbilities()
                .retainAll(ability.getTypes());
    }
}
