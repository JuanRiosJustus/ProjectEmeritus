package main.game.systems;

import com.alibaba.fastjson2.JSONArray;
import main.game.components.statistics.StatisticsComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathing.lineofsight.PathingAlgorithms;
import main.game.stores.AbilityTable;
import main.logging.EmeritusLogger;
import com.alibaba.fastjson2.JSONObject;

import java.util.*;

public class AbilitySystem extends GameSystem {
    private final SplittableRandom mRandom = new SplittableRandom();
    private static final EmeritusLogger mLogger = EmeritusLogger.create(AbilitySystem.class);
    private final PathingAlgorithms algorithm = new PathingAlgorithms();
    private final Map<String, Integer> mState = new LinkedHashMap<>();
    private static final int DEFAULT_VISION_RANGE = 8;
    private static final String PASSIVE = "PASSIVE";


//
//    public AbilitySystem() { }
    public AbilitySystem(GameModel gameModel) {
        super(gameModel);

//        mEventBus.subscribe(USE_ABILITY_EVENT, this::handleUsingAbility);
    }

//    private void handleUsingAbility(JSONObject event) {
//        String unitUsingAbilityID = event.getString(USE_ABILITY_EVENT_UNIT_USING_ABILITY_ID);
//        String abilityBeingUsed = event.getString(USE_ABILITY_EVENT_ABILITY);
//        String targetedTileID = event.getString(USE_ABILITY_EVENT_TARGET_TILE_ID);
//        boolean commit = event.getBoolean(USE_ABILITY_EVENT_COMMIT);
//
//        if (unitUsingAbilityID == null || abilityBeingUsed == null) { return; }
//
//        Entity unitEntity = getEntityWithID(unitUsingAbilityID);
//        if (unitEntity == null) { return; }
//
//        ActionsComponent actionsComponent = unitEntity.get(ActionsComponent.class);
//        if (actionsComponent.hasFinishedUsingAbility()) { return; }
//
//
//        // Execute the action
////        System.out.println("Trying to use " + abilityBeingUsed);
//        boolean wasUsed = useAbility(mGameModel, unitUsingAbilityID, abilityBeingUsed, targetedTileID, commit);
//        if (!wasUsed) { return;  }
//        actionsComponent.setHasFinishedUsingAbility(true);
////        mGameModel.getGameState().setAutomaticallyGoToHomeControls(true);
//    }

//    public boolean useAbility(GameModel model, String actingUnitID, String ability, String targetedTileID, boolean commit) {
//
//        Entity unitEntity = getEntityWithID(actingUnitID);
//        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
//        ActionsComponent actionsComponent = unitEntity.get(ActionsComponent.class);
//        String currentTileID = movementComponent.getCurrentTileID();
//        int range = AbilityTable.getInstance().getRange(ability);
//        int area = AbilityTable.getInstance().getArea(ability);
//
//
//        boolean shouldUpdateLogger = isUpdated("planning_to_act_logger", unitEntity, ability, targetedTileID);
//        if (shouldUpdateLogger) {
//            mLogger.info("{} is planning to use {} on {}", unitEntity, ability, targetedTileID);
//        }
//
//        boolean isUpdated = isUpdated("action_range", currentTileID, range);
//        if (isUpdated) {
//            // Setup request
//            JSONArray request = model.getTilesInAreaOfSight(GameModel.createGetTilesInAreaOfSightRequest(
//                    currentTileID, range, true
//            ));
//            // Process response
//            List<String> aos = new ArrayList<>();
//            for (int i = 0; i < request.size(); i++) { aos.add(request.getString(i)); }
//            // Apply processing
//            abilityComponent.stageRange(aos);
//            mLogger.info("Updated area of sight for {}, viewing {} tiles", unitEntity, aos.size());
//        }
//
//        isUpdated = isUpdated("action_line_of_sight", currentTileID, targetedTileID);
//        if (isUpdated) {
//            // Setup request
//            JSONArray request = model.getTilesInLineOfSight(GameModel.createGetTilesInLineOfSightRequest(
//                    currentTileID, targetedTileID, true
//            ));
//            // Process response
//            List<String> los = new ArrayList<>();
//            for (int i = 0; i < request.size(); i++) { los.add(request.getString(i)); }
//            // Apply processing
//            abilityComponent.stageLineOfSight(los);
//            mLogger.info("Updated line of sight for {}, viewing {} tiles", unitEntity, los.size());
//        }
//
//        isUpdated = isUpdated("action_area_of_effect", targetedTileID, area);
//        if (isUpdated) {
//            // Setup request
//            JSONArray request = model.getTilesInAreaOfSight(GameModel.createGetTilesInAreaOfSightRequest(
//                    targetedTileID, area - 1, true
//            ));
//            // Process response
//            List<String> aoe = new ArrayList<>();
//            for (int i = 0; i < request.size(); i++) { aoe.add(request.getString(i)); }
//            // Apply processing
//            abilityComponent.stageAreaOfEffect(aoe);
//            mLogger.info("Updated area of sight for {}, viewing {} tiles", unitEntity, aoe.size());
////            List<String> aoe = algorithm.computeAreaOfEffect(model, targetedTileID, area);
////            abilityComponent.stageAreaOfEffect(aoe);
////            mLogger.info("Updated area of effect for {} viewing {} tiles", unitEntity, aoe.size());
//        }
//
//        // Below may or may not be used
////        boolean showVision = model.getGameState().shouldShowActionRanges();
////        isUpdated = isUpdated("action_vision_range", currentTileEntity, DEFAULT_VISION_RANGE, showVision);
////        if (isUpdated) {
//////            Queue<Entity> tilesWithinVision = mPathBuilder.getTilesInActionRange(model, currentTile, DEFAULT_VISION_RANGE);
//////            actionComponent.stageVision(tilesWithinVision);
////        }
//
//        abilityComponent.stageTarget(targetedTileID);
//        abilityComponent.stageAbility(ability);
//
//        // try executing action only if specified
//        // - Target is not null
//        // - Target is within range
//        // - We are not in preview mode
//        if (targetedTileID == null) { return false; }
//        if (actionsComponent.hasFinishedUsingAbility()) { return false; }
//        if (!abilityComponent.isValidTarget()) { return false; }
//        if (!commit) { return  false; }
//
//        abilityComponent.commit();
//
//        mEventBus.publish(CombatSystem.createCombatStartEvent(actingUnitID, ability));
//        mLogger.info("Used from {} on {}", ability, targetedTileID);
//
//        return true;
//    }

    @Override
    public void update(GameModel model, SystemContext systemContext) {
        systemContext.getAllUnitEntityIDs().forEach(entityID -> {
            Entity entity = getEntityWithID(entityID);
            StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
            int hashCode = statisticsComponent.hashCode();

            int currentHash = mState.getOrDefault(entityID, -1);
            if (currentHash == hashCode) { return; }
            mLogger.info("Started updating {} because of ability");

//            removePassiveAbility(entityID);
//            initializePassiveAbility(entityID);
            mState.put(entityID, hashCode);

            mLogger.info("Finished update updating {} because of ability");
        });
    }

    private void removePassiveAbility(String entityID) {
        Entity entity = getEntityWithID(entityID);
        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
        statisticsComponent.removeModification(PASSIVE);
    }

    private void initializePassiveAbility(String entityID) {
        Entity entity = getEntityWithID(entityID);
        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
        String passiveAbility = statisticsComponent.getTraitAbility();
        if (passiveAbility == null) { return; }
        JSONArray attributeModifiers = AbilityTable.getInstance().getPassiveAttributes(passiveAbility);
        if (attributeModifiers == null) { return; }

        if (!attributeModifiers.isEmpty()) {
            for (int i = 0; i < attributeModifiers.size(); i++) {
                JSONObject attributeModifier = attributeModifiers.getJSONObject(i);
                String scalingType = AbilityTable.getInstance().getScalingAttributeScaling(attributeModifier);
                String scalingAttribute = AbilityTable.getInstance().getScalingAttributeKey(attributeModifier);
                float scalingMagnitude = AbilityTable.getInstance().getScalingAttributeValue(attributeModifier);
                boolean isBaseScaling = AbilityTable.getInstance().isBaseScaling(attributeModifier);


                float baseModifiedTotalMissingCurrent = statisticsComponent.getScaling(scalingAttribute, scalingType);
                float value = baseModifiedTotalMissingCurrent * scalingMagnitude;
                if (isBaseScaling) {
                    value = scalingMagnitude;
                }

                statisticsComponent.addFlatBonus(passiveAbility, passiveAbility, scalingAttribute, value);
            }

            statisticsComponent.addTag(passiveAbility);
        }
    }
}
