package main.game.systems;

import main.game.components.AbilityComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.TileComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.AbilityTable;
import main.game.systems.texts.FloatingTextSystem;
import main.logging.EmeritusLogger;
import main.utils.MathUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CombatSystem extends GameSystem  {
    private static final EmeritusLogger mLogger = EmeritusLogger.create(CombatSystem.class);
    public CombatSystem(GameModel gameModel) {
        super(gameModel);

        mEventBus.subscribe(COMBAT_START_EVENT, this::handleCombatStartEvent);
        mEventBus.subscribe(COMBAT_END_EVENT, this::handleCombatEndEvent);
    }

    public static final String COMBAT_START_EVENT = "combat_start_event";
    private static final String COMBAT_START_EVENT_ACTOR_ENTITY_ID = "actor_entity_id";
    private static final String COMBAT_START_EVENT_ABILITY = "ability";
    public static JSONObject createCombatStartEvent(String actorEntityID, String ability) {
        JSONObject event = new JSONObject();
        event.put(COMBAT_START_EVENT_ACTOR_ENTITY_ID, actorEntityID);
        event.put(COMBAT_START_EVENT_ABILITY, ability);
        return event;
    }
    public void handleCombatStartEvent(JSONObject event) {
        String actorEntityID = event.getString(COMBAT_START_EVENT_ACTOR_ENTITY_ID);
        String ability = event.getString(COMBAT_START_EVENT_ABILITY);

        boolean canPayCosts = canPayCosts(actorEntityID, ability);
        if (!canPayCosts) { return; }

        payCosts(actorEntityID, ability);

        String announcement = AbilityTable.getInstance().getAnnouncement(ability);
        mGameModel.getEventBus().publish(FloatingTextSystem.FLOATING_TEXT_EVENT, FloatingTextSystem.createFloatingTextEvent(
                announcement, actorEntityID
        ));

        Entity actorEntity = getEntityWithID(actorEntityID);
        AbilityComponent abilityComponent = actorEntity.get(AbilityComponent.class);


        String targetTile = abilityComponent.getFinalTileTargeted();
        String userAnimation = AbilityTable.getInstance().getUserAnimation(ability);
        mGameModel.getEventBus().publish(AnimationSystem.ANIMATION_EVENT, AnimationSystem.createAnimationEvent(
                actorEntityID, userAnimation, List.of(targetTile)
        ));


        abilityComponent.getTilesInFinalAreaOfEffect().forEach(iteratedTargetTileID -> {
            Entity tileEntity = getEntityWithID(iteratedTargetTileID);
            TileComponent tile = tileEntity.get(TileComponent.class);
            String actedOnEntityID = tile.getUnitID();
            if (actedOnEntityID == null) { return; }

            boolean hitsTarget = AbilityTable.getInstance().isSuccessful(ability);
            if (!hitsTarget) { return; }

            mEventBus.publish(AnimationSystem.DAMAGE_TAKEN_ANIMATION_EVENT, AnimationSystem.createDamageTakenAnimationEvent(
                    actorEntityID, ability, actedOnEntityID
            ));
        });
    }

    public static final String COMBAT_END_EVENT = "combat_end_event";
    private static final String COMBAT_END_EVENT_ACTOR_ENTITY_ID = "combat_end_event_actor_entity_id";
    private static final String COMBAT_END_EVENT_ABILITY = "combat_end_event_ability";
    private static final String COMBAT_END_EVENT_ACTED_ON_ENTITY_ID = "combat_end_event_acted_on_entity_id";
    public static JSONObject createCombatEndEvent(String actorEntityID, String ability, String actedOnEntityID) {
        JSONObject event = new JSONObject();
        event.put(COMBAT_END_EVENT_ACTOR_ENTITY_ID, actorEntityID);
        event.put(COMBAT_END_EVENT_ABILITY, ability);
        event.put(COMBAT_END_EVENT_ACTED_ON_ENTITY_ID, actedOnEntityID);
        return event;
    }
    public void handleCombatEndEvent(JSONObject event) {
        String actorEntityID = event.getString(COMBAT_END_EVENT_ACTOR_ENTITY_ID);
        String ability = event.getString(COMBAT_END_EVENT_ABILITY);
        String actedOnEntityID = event.getString(COMBAT_END_EVENT_ACTED_ON_ENTITY_ID);

        Map<String, Float> damageMap = getDamageMapping(actorEntityID, ability);
        Entity targetEntity = getEntityWithID(actedOnEntityID);
        if (targetEntity == null) { return; }
        StatisticsComponent statisticsComponent = targetEntity.get(StatisticsComponent.class);
        mLogger.info("Started dealing damage with {} to {} ", ability, targetEntity);

        for (Map.Entry<String, Float> entry : damageMap.entrySet()) {
            String attribute = entry.getKey();

            float rawDamage = entry.getValue();
            float damageAfterModifiers = getDamageAfterModifiers(actorEntityID, ability, actedOnEntityID, rawDamage);
            float damageAfterDefenses = getDamageAfterDefenses(actorEntityID, ability, actedOnEntityID, damageAfterModifiers);
            int finalDamage = (int) damageAfterDefenses;

            statisticsComponent.toResource(attribute, -finalDamage);

            String displayText = (finalDamage < 0 ? "+" : "") + Math.abs(finalDamage);
            mEventBus.publish(FloatingTextSystem.FLOATING_TEXT_EVENT, FloatingTextSystem.createFloatingTextEvent(
                    displayText, actedOnEntityID
            ));
        }
    }

    @Override
    public void update(GameModel model, SystemContext systemContext) { }



    public Map<String, Float> getCostMapping(String userUnitID, String ability) {
        Entity userEntity = getEntityWithID(userUnitID);
        StatisticsComponent statisticsComponent = userEntity.get(StatisticsComponent.class);
        Map<String, Float> costMap = new LinkedHashMap<>();
        JSONArray costs = AbilityTable.getInstance().getCosts(ability);
        mLogger.info("Started constructing cost mappings for {} to use {}", userEntity, ability);

        for (int i = 0; i < costs.length(); i++) {
            JSONObject cost = costs.getJSONObject(i);
            String targetAttribute = AbilityTable.getInstance().getTargetAttribute(cost);
            String scalingAttribute = AbilityTable.getInstance().getScalingAttribute(cost);
            String scalingType = AbilityTable.getInstance().getScalingType(cost);
            float scalingValue = AbilityTable.getInstance().getScalingMagnitude(cost);

            boolean isBaseScaling = AbilityTable.getInstance().isBaseScaling(cost);

            float currentAccruedCost = costMap.getOrDefault(targetAttribute, 0f);
            float additionalCost = 0;
            if (isBaseScaling) {
                additionalCost += scalingValue;
            } else {
                float baseModifiedTotalMissingCurrent = statisticsComponent.getScaling(scalingAttribute, scalingType);
                additionalCost = baseModifiedTotalMissingCurrent * scalingValue;
            }

            float newAccruedCost = currentAccruedCost + additionalCost;
            costMap.put(targetAttribute,newAccruedCost);
        }


        mLogger.info("Finished constructing cost mappings for {} to use {}", userEntity, ability);
        return costMap;
    }

    public boolean canPayCosts(String userUnitID, String ability) {
        Map<String, Float> costMap = getCostMapping(userUnitID, ability);
        Entity userEntity = getEntityWithID(userUnitID);
        StatisticsComponent statisticsComponent = userEntity.get(StatisticsComponent.class);
        mLogger.info("Checking cost requirements for {} to use {}", userEntity, ability);

        for (Map.Entry<String, Float> entry : costMap.entrySet()) {
            String attribute = entry.getKey();
            float cost = entry.getValue();

            int currentValue = statisticsComponent.getCurrent(attribute);
            if (currentValue >= cost) { continue; }
            mLogger.info("{} is unable to pay for {} because of {}", userEntity, ability, attribute);
            return false;
        }

        mLogger.info("{} is able to pay for {}", userEntity, ability);
        return true;
    }

    public void payCosts(String userUnitID, String ability) {
        Map<String, Float> costMap = getCostMapping(userUnitID, ability);
        Entity userEntity = getEntityWithID(userUnitID);
        StatisticsComponent statisticsComponent = userEntity.get(StatisticsComponent.class);
        mLogger.info("Paying cost requirements for {} using {}", userEntity, ability);

        for (Map.Entry<String, Float> entry : costMap.entrySet()) {
            String attribute = entry.getKey();
            float cost = entry.getValue();
            float currentTotal = statisticsComponent.getTotal(attribute);

            mLogger.info("Paying {} {} out of {} {} to use {}", cost, attribute, currentTotal, attribute, ability);
            statisticsComponent.toResource(attribute, -cost);
        }
    }


    private float getDamageAfterDefenses(String actorEntityID, String ability, String actedOnEntityID, float damage) {

        float finalDamage = damage;

        Entity actedOnEntity = getEntityWithID(actorEntityID);
        StatisticsComponent statisticsComponent = actedOnEntity.get(StatisticsComponent.class);


        float defenderDefense = 0;
        boolean usesPhysicalDefense = AbilityTable.getInstance().usesPhysicalDefense(ability);
        if (usesPhysicalDefense) {
            defenderDefense = statisticsComponent.getTotalPhysicalDefense();
        } else {
            defenderDefense = statisticsComponent.getTotalMagicalDefense();
        }

        finalDamage = finalDamage * (100 / (100 + defenderDefense));


        mLogger.info("{} = Damage after defenses", (int) finalDamage);
        return finalDamage;
    }

    private float getDamageAfterModifiers(String actorEntityID, String ability, String actedOnEntityID, float rawDamage) {
        if (rawDamage == 0) { return 0; }

        float currentDamage = rawDamage;

        mLogger.info("{} = Raw Damage", (int) currentDamage);
//
        Entity actorEntity = getEntityWithID(actorEntityID);
//        StatisticsComponent statisticsComponent = actorEntity.get(StatisticsComponent.class);
//        logger.debug("Base Damage: {}", finalDamage);
//        // 2. Reward units using attacks that are same type as themselves
//        boolean isSameTypeAttackBonus = AbilityDatabase.getInstance().hasSameTypeAttackBonus(actorUnitEntity, action);
//        if (isSameTypeAttackBonus) {
//            float stabBonus = finalDamage * .5f;
//            finalDamage += stabBonus;
//        }

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
        boolean isCriticalHit = MathUtils.passesChanceOutOf100(.05f);
        if (isCriticalHit) {
            float criticalDamage = currentDamage * 1.8f;
            currentDamage += criticalDamage;
            mLogger.info("{} lands a critical hit! for {}", actorEntity.toString(), criticalDamage);
        }

        mLogger.info("{} = Damage after modifiers", (int) currentDamage);
        return currentDamage;
    }

    public Map<String, Float> getDamageMapping(String actorEntityID, String ability) {
        Entity userEntity = getEntityWithID(actorEntityID);
        StatisticsComponent statisticsComponent = userEntity.get(StatisticsComponent.class);
        Map<String, Float> damageMap = new LinkedHashMap<>();
        JSONArray damages = AbilityTable.getInstance().getDamage(ability);
        mLogger.info("Started constructing damage mappings for {} to use {}", userEntity, ability);

        for (int i = 0; i < damages.length(); i++) {
            JSONObject damage = damages.getJSONObject(i);
            String targetAttribute = AbilityTable.getInstance().getTargetAttribute(damage);
            String scalingAttribute = AbilityTable.getInstance().getScalingAttribute(damage);
            String scalingType = AbilityTable.getInstance().getScalingType(damage);
            float scalingValue = AbilityTable.getInstance().getScalingMagnitude(damage);

            boolean isBaseScaling = AbilityTable.getInstance().isBaseScaling(damage);

            float currentAccruedDamage = damageMap.getOrDefault(targetAttribute, 0f);
            float additionalCost = 0;
            if (isBaseScaling) {
                additionalCost += scalingValue;
            } else {
                float baseModifiedTotalMissingCurrent = statisticsComponent.getScaling(scalingAttribute, scalingType);
                additionalCost = baseModifiedTotalMissingCurrent * scalingValue;
            }

            float newAccruedDamage = currentAccruedDamage + additionalCost;
            damageMap.put(targetAttribute, newAccruedDamage);
        }

        mLogger.info("Finished constructing damage mappings for {} to use {}", userEntity, ability);
        return damageMap;
    }
}
