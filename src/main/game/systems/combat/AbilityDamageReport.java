package main.game.systems.combat;

import main.game.components.IdentityComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.entity.Entity;
import main.game.stores.AbilityTable;
import main.game.stores.EntityStore;
import main.game.systems.texts.FloatingTextSystem;
import main.logging.EmeritusLogger;
import main.utils.MathUtils;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AbilityDamageReport  {
    private static final EmeritusLogger mLogger = EmeritusLogger.create(AbilityDamageReport.class);
    private Map<String, Float> mRawDamageMap = new HashMap<>();
    private Map<String, Float> mLowerDamageMap = new HashMap<>();
    private Map<String, Float> mUpperDamageMap = new HashMap<>();
    private Map<String, Float> mFinalDamageMap = new HashMap<>();
    private Map<String, Float> mTagsToUserChanceMap = new HashMap<>();
    private Map<String, Float> mTagsToTargetChanceMap = new HashMap<>();
    public AbilityDamageReport(String actorEntityID, String ability, String actedOnEntityID) {

        Entity actorEntity = getEntityWithID(actorEntityID);
        IdentityComponent actorID = actorEntity.get(IdentityComponent.class);
        String actorNickname = actorID.getNickname();

        mLogger.info("Started constructing damage mappings for {} to use {}", actorNickname, ability);
        mRawDamageMap = getRawDamageMap(actorEntityID, ability);
        mTagsToUserChanceMap = getUserTagsChanceMap(ability);
        mTagsToTargetChanceMap = getTargetTagsChanceMap(ability);
        mLogger.info("Finished constructing damage mappings for {} to use {}", actorEntity, ability);
        if (actedOnEntityID != null) {
            mLogger.info("Started constructing damage mappings after defense {} to use {}", actorEntity, ability);
            mFinalDamageMap = getFinalDamageMap(actorEntityID, ability, actedOnEntityID);
            mLogger.info("Finished constructing damage mappings after for {} to use {}", actorEntity, ability);
        }
    }

    private Map<String, Float> getTargetTagsChanceMap(String ability) {
        JSONArray userTags = AbilityTable.getInstance().getTargetTagObjects(ability);
        Map<String, Float> chanceMap = new HashMap<>();
        for (int i = 0; i < userTags.size(); i++) {
            JSONObject targetTag = userTags.getJSONObject(i);
            float chance = AbilityTable.getInstance().getTargetTagObjectChance(targetTag);
            String name = AbilityTable.getInstance().getTargetTagObjectName(targetTag);
            chanceMap.put(name, chance);
        }
        return chanceMap;
    }

    private Map<String, Float> getUserTagsChanceMap(String ability) {
        JSONArray userTags = AbilityTable.getInstance().getUserTagObjects(ability);
        Map<String, Float> chanceMap = new HashMap<>();
        for (int i = 0; i < userTags.size(); i++) {
            JSONObject targetTag = userTags.getJSONObject(i);
            float chance = AbilityTable.getInstance().getTargetTagObjectChance(targetTag);
            String name = AbilityTable.getInstance().getTargetTagObjectName(targetTag);
            chanceMap.put(name, chance);
        }
        return chanceMap;
    }

    public Map<String, Float> getFinalDamageMap(String actorEntityID, String ability, String actedOnEntityID) {
        // Calculate the raw damage from the ability
        Map<String, Float> finalDamageMap = new HashMap<>();
        for (Map.Entry<String, Float> damageEntry : mRawDamageMap.entrySet()) {
            String damageType = damageEntry.getKey();
            float rawDamage = damageEntry.getValue();
            float damageAfterModifiers = getDamageAfterModifiers(actorEntityID, ability, rawDamage, damageType);
            float damageAfterDefenses = getDamageAfterDefenses(actedOnEntityID, ability, damageAfterModifiers, damageType);
            float finalDamage = damageAfterDefenses;
            finalDamageMap.put(damageType, finalDamage);
        }

        return finalDamageMap;
    }




    public Map<String, Float> getRawDamageMap(String actorEntityID, String ability) {
        // Calculate the raw damage from the ability
        Entity actorEntity = getEntityWithID(actorEntityID);
        StatisticsComponent actorStats = actorEntity.get(StatisticsComponent.class);
        JSONArray rawDamages = AbilityTable.getInstance().getDamage(ability);
        Map<String, Float> damageMap = new LinkedHashMap<>();
        for (int i = 0; i < rawDamages.size(); i++) {
            JSONObject damage = rawDamages.getJSONObject(i);
            String targetAttribute = AbilityTable.getInstance().getTargetAttribute(damage);
            String scalingAttribute = AbilityTable.getInstance().getScalingAttribute(damage);
            String scalingType = AbilityTable.getInstance().getScalingType(damage);
            float scalingValue = AbilityTable.getInstance().getScalingMagnitude(damage);
            boolean isBaseScaling = AbilityTable.getInstance().isBaseScaling(damage);

            // Get previous calculations if available
            float currentAccruedDamage = damageMap.getOrDefault(targetAttribute, 0f);
            float additionalCost = 0;
            if (isBaseScaling) {
                additionalCost += scalingValue;
            } else {
                float baseModifiedTotalMissingCurrent = actorStats.getScaling(scalingAttribute, scalingType);
                additionalCost = baseModifiedTotalMissingCurrent * scalingValue;
            }

            float newAccruedDamage = currentAccruedDamage + additionalCost;
            damageMap.put(targetAttribute, newAccruedDamage);
        }
        return damageMap;
    }

    private float getDamageAfterDefenses(String actedOnEntityID, String ability, float damage, String damageType) {
        if (damage == 0) { return 0; }
        float currentDamage = damage;

        Entity actedOnEntity = getEntityWithID(actedOnEntityID);
        StatisticsComponent statisticsComponent = actedOnEntity.get(StatisticsComponent.class);
        IdentityComponent identityComponent = actedOnEntity.get(IdentityComponent.class);
        String actedOnNickname = identityComponent.getNickname();

        mLogger.info("Started calculating damage after defenses for {}", actedOnNickname);
        mLogger.info("{} Raw damage after defenses for {}", currentDamage, ability);


        boolean hasSTDB = hasSameTypeAttackBonus(actedOnEntityID, ability);
        float sameTypeDefenseBonus = currentDamage * .7f;
        updateLowerDamage(damageType, currentDamage - sameTypeDefenseBonus);

        if (hasSTDB) {
            currentDamage -= sameTypeDefenseBonus;
            mLogger.info(
                    "{} defends with Same-Type-Defense-Bonus from {} for {}",
                    actedOnNickname,
                    ability,
                    sameTypeDefenseBonus
            );
        }

        float defenderDefense = 0;
        boolean usesPhysicalDefense = AbilityTable.getInstance().makesPhysicalContact(ability);
        if (usesPhysicalDefense) {
            defenderDefense = statisticsComponent.getTotalPhysicalDefense();
        } else {
            defenderDefense = statisticsComponent.getTotalMagicalDefense();
        }
        currentDamage = currentDamage * (100 / (100 + defenderDefense));
        updateLowerDamage(damageType, currentDamage);


        mLogger.info("Finished calculating damage after defenses for {}", actedOnNickname);
        mLogger.info("{} damage after defenses for {}", currentDamage, ability);

        return currentDamage;
    }

    private float getDamageAfterModifiers(String actorEntityID, String ability, float damage, String damageType) {
        if (damage == 0) { return 0; }
        float currentDamage = damage;

        Entity actorEntity = getEntityWithID(actorEntityID);
        IdentityComponent actorIdentity = actorEntity.get(IdentityComponent.class);
        String actorNickname = actorIdentity.getNickname();

        mLogger.info("Started calculating damage after modifiers for {}", actorNickname);
        mLogger.info("{} Raw damage before modifiers for {}", currentDamage, ability);

        // 3. Penalize using attacks against units that share the type as the attack
        boolean hasSTAB = hasSameTypeAttackBonus(actorEntityID, ability);
        float sameTypeAttackBonus = currentDamage * 1.2f;
        updateUpperDamage(damageType, currentDamage + sameTypeAttackBonus);

        if (hasSTAB) {
            currentDamage += sameTypeAttackBonus;
            mLogger.info(
                    "{} lands a Same-Type-Attack-Bonus with {}, for {}",
                    actorNickname,
                    ability,
                    sameTypeAttackBonus
            );
        }

        // 4.5 determine if the attack is critical
        boolean isCriticalHit = MathUtils.passesChanceOutOf100(.05f);
        float criticalDamage = currentDamage * 1.5f;
//        updateUpperDamage(damageType, currentDamage + criticalDamage);

        if (isCriticalHit) {
            currentDamage += criticalDamage;
            mLogger.info(
                    "{} lands a CRIT bonus with {} for {}",
                    actorNickname,
                    ability,
                    criticalDamage
            );
        }

        mLogger.info("{} damage after modifiers for {}", currentDamage, ability);
        mLogger.info("Finished calculating damage after modifiers for {}", actorNickname);
        return currentDamage;
    }

    private boolean hasSameTypeAttackBonus(String entityID, String ability) {
        Entity entity = getEntityWithID(entityID);
        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
        JSONArray unitTypes = statisticsComponent.getType();
        JSONArray abilityTypes = AbilityTable.getInstance().getType(ability);
        for (int i = 0; i < abilityTypes.size(); i++) {
            String type = abilityTypes.getString(i);
            boolean sharesType = unitTypes.contains(type);
            if (!sharesType) { continue; }
            return true;
        }

        return false;
    }

    private void updateLowerDamage(String type, float damage) {
        float previousLowerDamage = mLowerDamageMap.getOrDefault(type, Float.MAX_VALUE);
        mLowerDamageMap.put(type, Math.min(previousLowerDamage, damage));
    }

    private void updateUpperDamage(String type, float damage) {
        float previousUpperDamage = mUpperDamageMap.getOrDefault(type, Float.MIN_VALUE);
        mUpperDamageMap.put(type, Math.max(previousUpperDamage, damage));
    }

    private Entity getEntityWithID(String entityID) { return EntityStore.getInstance().get(entityID); }

    public Map<String, Float> getRawDamageMap() { return mRawDamageMap; }
    public Map<String, Float> getLowerDamageMap() { return mLowerDamageMap; }
    public Map<String, Float> getUpperDamageMap() { return mUpperDamageMap; }
    public Map<String, Float> getFinalDamageMap() { return mFinalDamageMap; }

    public Map<String, Float> getTagsToUserMap() { return mTagsToUserChanceMap; }
    public Map<String, Float> getTagsToTargetMap() { return mTagsToTargetChanceMap; }
}
