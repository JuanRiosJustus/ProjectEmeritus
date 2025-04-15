package main.game.systems.combat;

import main.game.components.AbilityComponent;
import main.game.components.AnimationComponent;
import main.game.components.IdentityComponent;
import main.game.components.animation.AnimationTrack;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.TileComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.AbilityTable;
import main.game.systems.AnimationSystem;
import main.game.systems.GameSystem;
import main.game.systems.SystemContext;
import main.game.systems.texts.FloatingTextSystem;
import main.logging.EmeritusLogger;
import main.utils.MathUtils;
import main.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CombatSystem extends GameSystem {
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
        if (announcement.isEmpty()) {
            announcement = StringUtils.convertSnakeCaseToCapitalized(ability);
        }
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



            // Trigger an animation for the user
            // Add a listener to notify when the animation completes
            AnimationComponent actorComponent = actorEntity.get(AnimationComponent.class);
            AnimationTrack track = AnimationSystem.executeShakeAnimation(mGameModel, actedOnEntityID);
            actorComponent.addOnCompleteListener(() -> {
                Entity actedOnEntity = getEntityWithID(actedOnEntityID);
                if (actedOnEntity == null) { return; }
                AnimationComponent actedOnComponent = actedOnEntity.get(AnimationComponent.class);
                actedOnComponent.addTrack(track);
                mEventBus.publish(CombatSystem.COMBAT_END_EVENT, CombatSystem.createCombatEndEvent(
                        actorEntityID, ability, actedOnEntityID
                ));
            });
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

        Entity actedOnEntity = getEntityWithID(actedOnEntityID);
        if (actedOnEntity == null) { return; }

        AbilityDamageReport adr = new AbilityDamageReport(actorEntityID, ability, actedOnEntityID);
        Map<String, Float> damageMap = adr.getFinalDamageMap();
        StatisticsComponent actedOnStatisticsComponent = actedOnEntity.get(StatisticsComponent.class);
        mLogger.info("Started dealing damage with {} to {} ", ability, actedOnEntity);

        for (Map.Entry<String, Float> entry : damageMap.entrySet()) {
            String attribute = entry.getKey();
            int finalDamage = entry.getValue().intValue();

            actedOnStatisticsComponent.toResource(attribute, -finalDamage);

            String displayText = (finalDamage < 0 ? "+" : "") + Math.abs(finalDamage);
            mEventBus.publish(FloatingTextSystem.FLOATING_TEXT_EVENT, FloatingTextSystem.createFloatingTextEvent(
                    displayText, actedOnEntityID
            ));
        }
    }

    @Override
    public void update(GameModel model, SystemContext systemContext) { }



    public static Map<String, Float> getCostMapping(String userUnitID, String ability) {
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


    private static float getDamageAfterDefenses(String actedOnEntityID, String ability, float damage) {
        if (damage == 0) { return 0; }
        float currentDamage = damage;

        Entity actedOnEntity = getEntityWithID(actedOnEntityID);
        StatisticsComponent statisticsComponent = actedOnEntity.get(StatisticsComponent.class);
        IdentityComponent identityComponent = actedOnEntity.get(IdentityComponent.class);
        String actedOnNickname = identityComponent.getNickname();

        mLogger.info("Started calculating damage after defenses for {}", actedOnNickname);
        mLogger.info("{} Raw damage after defenses for {}", currentDamage, ability);

        if (hasSameTypeAttackBonus(actedOnEntityID, ability)) {
            float sameTypeDefenseBonus = currentDamage * .75f;
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


        mLogger.info("Finished calculating damage after defenses for {}", actedOnNickname);
        mLogger.info("{} damage after defenses for {}", currentDamage, ability);

        return currentDamage;
    }

    private static float getDamageAfterModifiers(String actorEntityID, String ability, float damage) {
        if (damage == 0) { return 0; }
        float currentDamage = damage;

        Entity actorEntity = getEntityWithID(actorEntityID);
        IdentityComponent actorIdentity = actorEntity.get(IdentityComponent.class);
        String actorNickname = actorIdentity.getNickname();

        mLogger.info("Started calculating damage after modifiers for {}", actorNickname);
        mLogger.info("{} Raw damage before modifiers for {}", currentDamage, ability);

        // 3. Penalize using attacks against units that share the type as the attack
        if (hasSameTypeAttackBonus(actorEntityID, ability)) {
            float sameTypeAttackBonus = currentDamage * 1.5f;
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
        if (isCriticalHit) {
            float criticalDamage = currentDamage * 1.8f;
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

    private static boolean hasSameTypeAttackBonus(String entityID, String ability) {
        Entity entity = getEntityWithID(entityID);
        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
        JSONArray unitTypes = statisticsComponent.getType();
        JSONArray abilityTypes = AbilityTable.getInstance().getType(ability);
        for (int i = 0; i < abilityTypes.length(); i++) {
            String type = abilityTypes.getString(i);
            boolean sharesType = unitTypes.toList().contains(type);
            if (!sharesType) { continue; }
            return true;
        }

        return false;
    }

    public static Map<String, Float> getDamageMapping(String actorID, String ability, String actedID) {
        Entity actorEntity = getEntityWithID(actorID);
        StatisticsComponent actorStats = actorEntity.get(StatisticsComponent.class);

        Map<String, Float> damageMap = new LinkedHashMap<>();
        JSONArray rawDamages = AbilityTable.getInstance().getDamage(ability);
        mLogger.info("Started constructing damage mappings for {} to use {}", actorEntity, ability);

        // Calculate the raw damage from the ability
        for (int i = 0; i < rawDamages.length(); i++) {
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

        // if acted is not null, calculate the defenses
        mLogger.info("Finished constructing damage mappings for {} to use {}", actorEntity, ability);
        if (actedID == null) { return damageMap; }
        mLogger.info("Started constructing damage mappings after defense {} to use {}", actorEntity, ability);

        Map<String, Float> basicDamageMap = new HashMap<>();
        for (Map.Entry<String, Float> damageEntry : damageMap.entrySet()) {
            String damageType = damageEntry.getKey();
            float rawDamage = damageEntry.getValue();
            float damageAfterModifiers = getDamageAfterModifiers(actorID, ability, rawDamage);
            float damageAfterDefenses = getDamageAfterDefenses(actedID, ability, damageAfterModifiers);
            float finalDamage = damageAfterDefenses;
            basicDamageMap.put(damageType, finalDamage);
        }

        mLogger.info("Finished constructing damage mappings after for {} to use {}", actorEntity, ability);
        return basicDamageMap;
    }
}
