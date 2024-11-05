package main.game.systems.combat;

import main.game.components.StatisticsComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.action.ActionPool;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;

public class CombatReport {

    private static final String physicalTypes = "Slash Pierce Blunt Normal";
    private static final String magicalTypes = "Light Air Water Dark Fire Earth";
    private final Map<String, Float> mResourceToDamageMap = new HashMap<>();
    private final Map<String, Float> mDamagePropertiesMap = new HashMap<>();
    private final Map<String, Integer> mDamageMap = new HashMap<>();
    private final String CRIT_BONUS = "Critical";
    private final static ELogger logger = ELoggerFactory.getInstance().getELogger(CombatReport.class);
    private final Entity mActorUnitEntity;
    private final String mAction;
    private final Entity mActedOnUnitEntity;

    public CombatReport(GameModel model, Entity actorUnitEntity, String action, Entity actedOnUnitEntity) {
        mActorUnitEntity = actorUnitEntity;
        mAction = action;
        mActedOnUnitEntity = actedOnUnitEntity;
    }


    public Map<String, Integer> calculate() {
        Map<String, Float> rawDamageMap = ActionPool.getInstance().getDamage(
                mActorUnitEntity,
                mAction,
                mActedOnUnitEntity
        );
        StatisticsComponent statisticsComponent = mActedOnUnitEntity.get(StatisticsComponent.class);
        for (String nodeName : statisticsComponent.getStatNodeKeys()) {
            if (!rawDamageMap.containsKey(nodeName)) { continue; }
            float rawDamage = rawDamageMap.get(nodeName);
            float bonusDamage = getDamageAfterBonuses(mActorUnitEntity, mAction, mActedOnUnitEntity, rawDamage);
            int finalDamage = (int) (getDamageAfterDefenses(mActorUnitEntity, mAction, mActedOnUnitEntity, bonusDamage) * -1);
            mDamageMap.put(nodeName, finalDamage);
        }
        return mDamageMap;
    }

    public Map<String, Integer> calculate1() {
        Map<String, Float> rawDamageMap = ActionPool.getInstance().getDamage(
                mActorUnitEntity,
                mAction,
                mActedOnUnitEntity
        );
        StatisticsComponent statisticsComponent = mActedOnUnitEntity.get(StatisticsComponent.class);
        for (String nodeName : statisticsComponent.getStatNodeKeys()) {
            if (!rawDamageMap.containsKey(nodeName)) { continue; }
            float rawDamage = rawDamageMap.get(nodeName);
            float bonusDamage = getDamageAfterBonuses(mActorUnitEntity, mAction, mActedOnUnitEntity, rawDamage);
            int finalDamage = (int) (getDamageAfterDefenses(mActorUnitEntity, mAction, mActedOnUnitEntity, bonusDamage) * -1);
            mDamageMap.put(nodeName, finalDamage);
        }
        return mDamageMap;
    }


    private float getDamageAfterDefenses(Entity actorUnitEntity, String action, Entity actedOnUnitEntity, float damage) {

        float finalDamage = damage;
//
        float defenderDefense = getDefense(actedOnUnitEntity, action);

//        if (action.hasTag(TagComponent.IGNORE_DEFENSES)) { defenderDefense = 0; }

        finalDamage = finalDamage * (100 / (100 + defenderDefense));

        return finalDamage;
    }

    private float getDamageAfterBonuses(Entity actorUnitEntity, String action, Entity actedOnUnitEntity, float damage) {
        if (damage == 0) { return 0; }

        float finalDamage = damage;

        logger.debug("Base Damage: {}", finalDamage);
        // 2. Reward units using attacks that are same type as themselves
        boolean isSameTypeAttackBonus = ActionPool.getInstance().hasSameTypeAttackBonus(actorUnitEntity, action);
        if (isSameTypeAttackBonus) {
            float stabBonus = finalDamage * .5f;
            finalDamage += stabBonus;
        }

//        // 3. Penalize using attacks against units that share the type as the attack
//        if (hasSameTypeAttackBonus(actedOnUnitEntity, action)) {
//            float stdp = finalDamage * .5f;
//            logger.debug("{}(Current) - {}({}) = {}", finalDamage, stdp, STDP_PENALTY, (finalDamage - stdp));
////            mDamagePropertiesMap.put(resource + "_" + STDP_PENALTY, stdp);
//            finalDamage -= stdp;
//        }

//        if (isAverseToAbilityType(defender, action)) {
//            float aversion = finalDamage * .5f;
//            logger.debug("{}(Current) + {}({}) = {}", finalDamage, aversion, AVERSION_BONUS, (finalDamage + aversion));
////            mDamagePropertiesMap.put(resource + "_" + AVERSION_BONUS, aversion);
//            finalDamage += aversion;
//        }

        // 4.5 determine if the attack is critical
        boolean isCrit = MathUtils.passesChanceOutOf100(.05f);
        if (isCrit) {
            float cridDamage = finalDamage * 2;
//            mDamagePropertiesMap.put(resource + "_" + CRIT_BONUS, crit);
            finalDamage += cridDamage;
        }

//        if (defender.get(TagComponent.class).contains(Constants.NEGATE)) {
//            float ngte = finalDamage * .9f;
//            logger.debug("{}(Current) - {}({}) = {}", finalDamage, ngte, Constants.NEGATE, (finalDamage - ngte));
////            mDamagePropertiesMap.put(resource + "_" + Constants.NEGATE, ngte);
//            finalDamage -= ngte;
//        }
        return finalDamage;
    }

    private float getDefense(Entity entity, String action) {
        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
        boolean isNormal = ActionPool.getInstance().shouldUsePhysicalDefense(action);
        float total = 1;
        if (isNormal) {
            total = statisticsComponent.getTotal(StatisticsComponent.PHYSICAL_DEFENSE);
        } else {
            total = statisticsComponent.getTotal(StatisticsComponent.RESISTANCE);
        }
        return total;
    }
}
