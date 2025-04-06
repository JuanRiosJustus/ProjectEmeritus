package main.game.systems;

import main.constants.Pair;
import main.game.components.*;
import main.game.components.behaviors.Behavior;
import main.game.components.statistics.StatisticsComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathing.lineofsight.PathingAlgorithms;
import main.game.stores.factories.EntityStore;
import main.game.stores.pools.AbilityDatabase;
import main.game.systems.actions.behaviors.AggressiveBehavior;
import main.game.systems.actions.behaviors.RandomnessBehavior;
import main.logging.EmeritusLogger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class AbilitySystem extends GameSystem {
    private final SplittableRandom mRandom = new SplittableRandom();
    private static final EmeritusLogger mLogger = EmeritusLogger.create(AbilitySystem.class);
    private final PathingAlgorithms algorithm = new PathingAlgorithms() {
    };
    private final AggressiveBehavior mAggressiveBehavior = new AggressiveBehavior();
    private final RandomnessBehavior mRandomnessBehavior = new RandomnessBehavior();
    private static final int DEFAULT_VISION_RANGE = 8;


    public static final String USE_ABILITY_EVENT = "use_ability_event";
//
//    public AbilitySystem() { }
    public AbilitySystem(GameModel gameModel) {
        super(gameModel);

        mEventBus.subscribe(USE_ABILITY_EVENT, this::tryUsingAbility);
    }

    public static JSONObject createUsingAbilityEvent(String unitID, String ability, String targetTileID, boolean commit) {
        JSONObject event = new JSONObject();
        event.put("unit_using_ability_id", unitID);
        event.put("ability_being_used", ability);
        event.put("targeted_tile_id", targetTileID);
        event.put("commit", commit);
        return event;
    }

    public void tryUsingAbility(JSONObject event) {
        String unitUsingAbilityID = event.optString("unit_using_ability_id", null);
        String abilityBeingUsed = event.optString("ability_being_used", null);
        String targetedTileID = event.optString("targeted_tile_id", null);
        boolean commit = event.getBoolean("commit");

        if (unitUsingAbilityID == null || abilityBeingUsed == null) { return; }

        Entity unitEntity = EntityStore.getInstance().get(unitUsingAbilityID);
        if (unitEntity == null) { return; }

        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
        if (abilityComponent.hasActed()) { return; }

        // Execute the action
        boolean acted = act(mGameModel, unitUsingAbilityID, abilityBeingUsed, targetedTileID, commit);
        if (!acted) { return;  }
        abilityComponent.setActed(acted);
        mGameModel.getGameState().setAutomaticallyGoToHomeControls(true);
    }

    public void update(GameModel model, SystemContext systemContext) {

        systemContext.getNonControlledUnitIDs().forEach(unitID -> {
            Entity unitEntity = EntityStore.getInstance().get(unitID);
            // Don't act if still waiting for an animation to finish
            AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
            if (animationComponent.hasPendingAnimations()) { return; }
            // Ignore if the unit has already acted
            AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
            if (abilityComponent.hasActed()) { return; }
            // Wait according to configured state
            Behavior behavior = unitEntity.get(Behavior.class);
            float unitWaitTimeBetweenActivities = model.getGameState().getUnitWaitTimeBetweenActivities();
            if (behavior.shouldWait(unitWaitTimeBetweenActivities)) { return; }

            // Check that the unit actually has the ability, not sure why we need to check, but keeping for now
            Pair<String, String> toActOn = mRandomnessBehavior.toActOnV2(model, unitID);
            if (toActOn != null) {
                boolean acted = act(model, unitID, toActOn.getSecond(), toActOn.getFirst(), true);
                abilityComponent.setActed(acted);
            } else {
                abilityComponent.setActed(true);
            }

            if (abilityComponent.hasActed()) {
                mLogger.info("{} Triggered ability system", unitEntity);
            }
        });
    }

    public boolean act(GameModel model, String actingUnitID, String ability, String targetedTileID, boolean commit) {

        Entity unitEntity = EntityStore.getInstance().get(actingUnitID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
        String currentTileID = movementComponent.getCurrentTileID();
        Entity currentTileEntity = EntityStore.getInstance().get(currentTileID);
        Entity targetedTileEntity = EntityStore.getInstance().get(targetedTileID);
        int range = AbilityDatabase.getInstance().getRange(ability);
        int area = AbilityDatabase.getInstance().getArea(ability);


        boolean shouldUpdateLogger = isUpdated("planning_to_act_logger", unitEntity, ability, targetedTileEntity);
        if (shouldUpdateLogger) {
            mLogger.info("{} is planning to use {} on {}", unitEntity, ability, targetedTileEntity);
        }

        boolean isUpdated = isUpdated("action_range", currentTileID, range);
        if (isUpdated) {
            List<String> rng = algorithm.computeAreaOfSightV2(model, currentTileEntity, range);
            abilityComponent.stageRange(rng);
            mLogger.info("Updated area of sight for {}, viewing {} tiles", unitEntity, rng.size());
        }

        isUpdated = isUpdated("action_line_of_sight", currentTileID, targetedTileID);
        if (isUpdated) {
            List<String> los = algorithm.computeLineOfSightV2(model, currentTileEntity, targetedTileEntity);
            abilityComponent.stageLineOfSight(los);
            mLogger.info("Updated line of sight for {}, viewing {} tiles", unitEntity, los.size());
        }

        isUpdated = isUpdated("action_area_of_effect", targetedTileID, area);
        if (isUpdated) {
            List<String> aoe = algorithm.computeAreaOfSightV2(model, targetedTileEntity, area);
            abilityComponent.stageAreaOfEffect(aoe);
            mLogger.info("Updated area of effect for {} viewing {} tiles", unitEntity, aoe.size());
        }

        // Below may or may not be used
        boolean showVision = model.getGameState().shouldShowActionRanges();
        isUpdated = isUpdated("action_vision_range", currentTileEntity, DEFAULT_VISION_RANGE, showVision);
        if (isUpdated) {
//            Queue<Entity> tilesWithinVision = mPathBuilder.getTilesInActionRange(model, currentTile, DEFAULT_VISION_RANGE);
//            actionComponent.stageVision(tilesWithinVision);
        }

        abilityComponent.stageTarget(targetedTileID);
        abilityComponent.stageAbility(ability);

        // try executing action only if specified
        // - Target is not null
        // - Target is within range
        // - We are not in preview mode
        if (targetedTileID == null) { return false; }
        if (abilityComponent.hasActed()) { return false; }
        if (!abilityComponent.isValidTarget()) { return false; }
        if (!commit) { return  false; }

        abilityComponent.commit();

        mEventBus.publish(CombatSystem.COMBAT_START_EVENT, CombatSystem.createCombatStartEvent(
                actingUnitID, ability
        ));

        mLogger.info("Used from {} on {}", ability, targetedTileEntity);

        return true;
    }





    public Map<String, Float> getCostMapping(String userUnitID, String ability) {
        Entity userEntity = getEntityWithID(userUnitID);
        StatisticsComponent statisticsComponent = userEntity.get(StatisticsComponent.class);
        Map<String, Float> costMap = new LinkedHashMap<>();
        JSONArray costs = AbilityDatabase.getInstance().getCosts(ability);
        mLogger.info("Started constructing cost mappings for {} to use {}", userEntity, ability);

        for (int i = 0; i < costs.length(); i++) {
            JSONObject cost = costs.getJSONObject(i);
            String targetAttribute = AbilityDatabase.getInstance().getTargetAttribute(cost);
            String scalingAttribute = AbilityDatabase.getInstance().getScalingAttribute(cost);
            String scalingType = AbilityDatabase.getInstance().getScalingType(cost);
            float scalingValue = AbilityDatabase.getInstance().getScalingMagnitude(cost);

            boolean isBaseScaling = AbilityDatabase.getInstance().isBaseScaling(cost);

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



//    private void applyEffects(GameModel model, Entity target, ActionEvent event, Set<Map.Entry<String, Float>> statuses) {
//        // Go through all the different status effects and their probability
//        StatisticsComponent statisticsComponent = target.get(StatisticsComponent.class);
//        for (Map.Entry<String, Float> entry : statuses) {
//            // If the stat chance passes, handle
//            float statusChance = Math.abs(entry.getValue());
//            if (statusChance < mRandom.nextFloat()) { continue; }
//            // Check if the status effect increases a stat
//            String status = entry.getKey();
////            StatNode node = statistics.getStatsNode(status);
//
//
//            if (status.endsWith("Knockback")) {
////                handleKnockback(model, target, event);
//            } else {
//                target.get(TagComponent.class).add(status, event.getAction());
//            }
////            Color c = ColorPalette.getColorOfAbility(event.action);
//
////            if (node != null) {
////                statistics.modify(status,
////                        event.ability, StatNode.MULTIPLICATIVE, (int) (entry.getValue() <  0 ? -.5f : .5f));
////                model.logger.log(target + "'s " + status + " " +
////                        (entry.getValue() <  0 ? "decreased" : "increased"));
////
////                announceWithFloatingText(gameModel, (entry.getValue() <  0 ? "-" : "+") +
////                                StringUtils.spaceByCapitalization(status), target, c);
////            } else {
////                announceWithFloatingText(gameModel,
////                        StringUtils.spaceByCapitalization(status) + "'d", target, c);
////                model.logger.log(target + " was inflicted with " + StringUtils.spaceByCapitalization(status));
////            }
//            mLogger.info("{} has {}", target, status);
//        }
//    }
}
