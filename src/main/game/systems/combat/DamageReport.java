package main.game.systems.combat;

import main.constants.ColorPalette;
import main.constants.Constants;
import main.game.components.*;
import main.game.components.Summary;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stats.node.ResourceNode;
import main.game.stores.pools.action.Action;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.MathUtils;

import java.util.Set;

public class DamageReport {

    private static final String physicalTypes = "Slash Pierce Blunt Normal";
    private static final String magicalTypes = "Light Air Water Dark Fire Earth";

    private float finalHealthDamage = 0;
    private float finalEnergyDamage = 0;
    private float aversion = 0;
    private float stabBonus = 0;
    private float stdpPenalty = 0;
    private float criticalBonus = 0;
    private float physicalBonus = 0;
    private float counterPenalty = 0;

    private final static ELogger logger = ELoggerFactory.getInstance().getELogger(DamageReport.class);

    public DamageReport(GameModel model, Entity attacker, Action action, Entity defender) {

        Summary defenderSummary = defender.get(Summary.class);
        ResourceNode defenderHealth = defenderSummary.getResourceNode(Constants.HEALTH);
        ResourceNode defenderEnergy = defenderSummary.getResourceNode(Constants.ENERGY);

        float baseHealthDamage = action.getHealthDamage(attacker);
        float baseEnergyDamage = action.getEnergyDamage(attacker);

        if (baseHealthDamage == 0 && baseEnergyDamage == 0) { return; }

        finalHealthDamage = baseHealthDamage;
        finalEnergyDamage = baseEnergyDamage;

        float multiplier = baseHealthDamage != 0 ? baseHealthDamage : baseEnergyDamage;
        // 2. Reward units using attacks that are same type as themselves
        if (hasSameTypeAttackBonus(attacker, action)) {
            stabBonus = multiplier * 1.1f;
            finalHealthDamage += (baseHealthDamage > 0 ? stabBonus : 0);
            finalEnergyDamage += (baseEnergyDamage > 0 ? stabBonus : 0);
            logger.debug("+{} damage from SameTypeAttackBonus", stabBonus);
        }

        // 3. Penalize using attacks against units that share the type as the attack
        if (hasSameTypeAttackBonus(defender, action)) {
            stdpPenalty = multiplier * .1f;
            finalHealthDamage -= (baseHealthDamage > 0 ? stdpPenalty : 0);
            finalEnergyDamage -= (baseEnergyDamage > 0 ? stdpPenalty : 0);
            logger.debug("-{} damage from SameTypeDefenderPenalty", stdpPenalty);
        }

        if (isAverseToAbilityType(defender, action)) {
            aversion = multiplier * .25f;
            finalHealthDamage += (baseHealthDamage > 0 ? aversion : 0);
            finalEnergyDamage += (baseEnergyDamage > 0 ? aversion : 0);
            logger.debug("-{} damage from Aversion", aversion);
        }

        // 4.5 determine if the attack is critical
        if (MathUtils.passesChanceOutOf100(.05f)) {
            criticalBonus = multiplier * 2;
            finalHealthDamage += (baseHealthDamage > 0 ? criticalBonus : 0);
            finalEnergyDamage += (baseEnergyDamage > 0 ? criticalBonus : 0);
            logger.debug("+{} damage from CRIT", criticalBonus);
        }

        if (defender.get(Tags.class).contains(Constants.NEGATE)) {
            counterPenalty = multiplier * .75f;
            finalHealthDamage -= (baseHealthDamage > 0 ? counterPenalty : 0);
            finalEnergyDamage -= (baseEnergyDamage > 0 ? counterPenalty : 0);
            logger.debug("-{} damage because NGTE", counterPenalty);
        }

        float preDefenseHealthDamage = finalHealthDamage;
        float preDefenseEnergyDamage = finalEnergyDamage;
        // float preDefenseDamage

        // 5. calculate the actual damage by getting defense
        float defenderDefense = getDefense(defenderSummary, action);;
        if (action.hasTag(Action.IGNORE_DEFENSES)) {
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
                                Math.abs((int)finalHealthDamage), defender.get(Animation.class).getVector(),
                        ColorPalette.getColorOfAbility(action));
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
                                Math.abs((int)finalEnergyDamage) + " EP", defender.get(Animation.class).getVector(),
                        ColorPalette.getColorOfAbility(action));
            }
        }
    }

    private float getDefense(Summary summary, Action action) {
        boolean isNormal = action.getTypes().contains(Constants.NORMAL);
        float total = 1;
        if (isNormal) {
            total = summary.getStatTotal(Constants.CONSTITUTION);
        } else {
            total = summary.getStatTotal(Constants.RESISTANCE);
        }
        return total;
    }

    public int getFinalHealthDamage() { return (int) finalHealthDamage; }
    public int getFinalEnergyDamage() { return (int) finalEnergyDamage; }

    private static boolean isMagicalType(Entity entity) {
        return entity.get(Types.class).getTypes().stream().anyMatch(magicalTypes::contains);
    }

    private static boolean isMagicalType(Set<String> types) {
        return types.stream().anyMatch(magicalTypes::contains);
    }

    private static boolean isPhysicalType(Set<String> types) {
        return types.stream().anyMatch(physicalTypes::contains);
    }

    private static boolean hasSameTypeAttackBonus(Entity entity, Action action) {
        return entity.get(Actions.class)
                .getAbilities()
                .retainAll(action.getTypes());
    }

    private static boolean isAverseToAbilityType(Entity entity, Action action) {
        Tags tags = entity.get(Tags.class);
        for (String type : action.getTypes()) {
            if (!tags.contains(type + " Averse")) { continue; }
            return true;
        }
        return false;
    }
}
