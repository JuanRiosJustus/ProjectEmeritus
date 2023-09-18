package main.game.systems.combat;

import main.constants.Constants;
import main.game.components.*;
import main.game.components.Statistics;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.action.Action;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.MathUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DamageCalculator {

    private static final String physicalTypes = "Slash Pierce Blunt Normal";
    private static final String magicalTypes = "Light Air Water Dark Fire Earth";
    private final Map<String, Float> mResourceToDamageMap = new HashMap<>();
    private final Map<String, Float> mDamagePropertiesMap = new HashMap<>();
    private final String CRIT_BONUS = "Critical";
    private final String STAB_BONUS = "Same Type Attack Bonus";
    private final String STDP_PENALTY = "Same Type Defender Penalty";
    private final String AVERSION_BONUS = "Aversion";
    private final String NGTE_TAG = "Negate";
    private final static ELogger logger = ELoggerFactory.getInstance().getELogger(DamageCalculator.class);

    public DamageCalculator(GameModel model, Entity actor, Action action, Entity defender) {
        Statistics statistics = actor.get(Statistics.class);
        for (String resource : statistics.getResourceKeys()) {
            float damage = calculateDamage(actor, action, defender, resource);
            if (damage == 0) { continue; }
            mResourceToDamageMap.put(resource, damage);
        }
    }

    private float calculateDamage(Entity actor, Action action, Entity defender, String resource) {

        float baseDamage = action.getDamage(actor, resource);
        if (baseDamage == 0) { return 0; }

        float finalDamage = baseDamage;


        logger.debug("Base Damage: {}", finalDamage);
        // 2. Reward units using attacks that are same type as themselves
        if (hasSameTypeAttackBonus(actor, action)) {
            float stab = finalDamage * .5f;
            logger.debug("{}(Current) + {}({}) = {}", finalDamage, stab, STAB_BONUS, (finalDamage + stab));
            mDamagePropertiesMap.put(resource + "_" + STAB_BONUS, stab);
            finalDamage += stab;
        }

        // 3. Penalize using attacks against units that share the type as the attack
        if (hasSameTypeAttackBonus(defender, action)) {
            float stdp = finalDamage * .5f;
            logger.debug("{}(Current) - {}({}) = {}", finalDamage, stdp, STDP_PENALTY, (finalDamage - stdp));
            mDamagePropertiesMap.put(resource + "_" + STDP_PENALTY, stdp);
            finalDamage -= stdp;
        }

        if (isAverseToAbilityType(defender, action)) {
            float aversion = finalDamage * .5f;
            logger.debug("{}(Current) + {}({}) = {}", finalDamage, aversion, AVERSION_BONUS, (finalDamage + aversion));
            mDamagePropertiesMap.put(resource + "_" + AVERSION_BONUS, aversion);
            finalDamage += aversion;
        }

        // 4.5 determine if the attack is critical
        if (MathUtils.passesChanceOutOf100(.05f)) {
            float crit = finalDamage * 2;
            logger.debug("{}(Current) + {}({}) = {}", finalDamage, crit, CRIT_BONUS, (finalDamage + crit));
            mDamagePropertiesMap.put(resource + "_" + CRIT_BONUS, crit);
            finalDamage += crit;
        }

        if (defender.get(Tags.class).contains(Constants.NEGATE)) {
            float ngte = finalDamage * .9f;
            logger.debug("{}(Current) - {}({}) = {}", finalDamage, ngte, Constants.NEGATE, (finalDamage - ngte));
            mDamagePropertiesMap.put(resource + "_" + Constants.NEGATE, ngte);
            finalDamage -= ngte;
        }

        float preDefenseDamage = finalDamage;

        // 5. calculate the actual damage by getting defense
        float defenderDefense = getDefense(defender, action);

        if (action.hasTag(Tags.IGNORE_DEFENSES)) { defenderDefense = 0; }

        finalDamage = preDefenseDamage * (100 / (100 + defenderDefense));

        logger.debug("{}(Before Defense) - {}(After Defense)", preDefenseDamage, finalDamage);
        return finalDamage;
    }

    private float getDefense(Entity entity, Action action) {
        Statistics statistics = entity.get(Statistics.class);
        boolean isNormal = action.getTypes().contains(Constants.NORMAL);
        float total = 1;
        if (isNormal) {
            total = statistics.getStatTotal(Statistics.CONSTITUTION);
        } else {
            total = statistics.getStatTotal(Statistics.RESISTANCE);
        }
        return total;
    }

    public Set<String> getDamageKeys() { return mResourceToDamageMap.keySet(); }
    public float getDamage(String key) { return mResourceToDamageMap.get(key); }
    public float getCritical(String key) {
        return mDamagePropertiesMap.getOrDefault(key + "_" + CRIT_BONUS, 0f);
    }

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
        return !Collections.disjoint(entity.get(Types.class).getTypes(), action.getTypes());
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
