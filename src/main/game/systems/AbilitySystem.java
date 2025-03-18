package main.game.systems;

import main.constants.Checksum;
import main.constants.Pair;
import main.game.components.*;
import main.game.components.behaviors.Behavior;
import main.game.components.statistics.StatisticsComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathing.lineofsight.PathingAlgorithms;
import main.game.stores.factories.EntityStore;
import main.game.stores.pools.action.AbilityDatabase;
import main.game.stores.pools.action.ActionEvent;
import main.game.systems.actions.ActionHandler;
import main.game.systems.actions.behaviors.AggressiveBehavior;
import main.game.systems.actions.behaviors.RandomnessBehavior;
import main.input.InputController;
import main.input.Mouse;
import main.logging.EmeritusLogger;


import java.util.*;
import java.util.stream.Collectors;

public class AbilitySystem extends GameSystem {
    private final SplittableRandom mRandom = new SplittableRandom();
    private final EmeritusLogger mLogger = EmeritusLogger.create(AbilitySystem.class);
    private final Checksum mChecksum = new Checksum();
    private final PathingAlgorithms algorithm = new PathingAlgorithms() {
    };
    private final AggressiveBehavior mAggressiveBehavior = new AggressiveBehavior();
    private final RandomnessBehavior mRandomnessBehavior = new RandomnessBehavior();
    private static final int DEFAULT_VISION_RANGE = 8;
    private ActionHandler actionHandler = new ActionHandler();

//    @Override
//    public void update(GameModel model, Entity unitEntity) {
////        actionHandler.finishAction(model);
////        finishAction();
//
//        Behavior behavior = unitEntity.get(Behavior.class);
//
//        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
//        if (abilityComponent.hasActed()) { return; }
//
//        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
//        if (animationComponent.hasPendingAnimations()) { return; }
//
//
//        if (behavior.isUserControlled()) {
//            updateUser(model, unitEntity);
//        } else {
//            updateAI(model, unitEntity);
//        }
//    }

    public void update(GameModel model, String unitID) {
        Entity unitEntity = EntityStore.getInstance().get(unitID);
        Behavior behavior = unitEntity.get(Behavior.class);

        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
        if (abilityComponent.hasActed()) { return; }

        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        if (animationComponent.hasPendingAnimations()) { return; }


        if (behavior.isUserControlled()) {
            updateUser(model, unitID);
        } else {
            updateAI(model, unitID);
        }
    }

    public void updateUser(GameModel model, String unitID) {
        boolean isAbilityPanelOpen = model.getGameState().isAbilityPanelOpen();
        if (!isAbilityPanelOpen) { return; }

        Entity unitEntity = EntityStore.getInstance().get(unitID);
        String currentTurnsUnitID = model.getSpeedQueue().peekV2();
        if (currentTurnsUnitID == null || !currentTurnsUnitID.equals(unitID)) { return; }

//        if (mousedAtTileID == null) { return; }
        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);

        // Check that the unit actually has the ability, not sure why we need to check, but keeping for now
        String ability = abilityComponent.getAbility();
        if (ability == null) { return; }

        // Execute the action
        Mouse mouse = InputController.getInstance().getMouse();
        String mousedAtTileID = model.tryFetchingMousedAtTileID();;
        boolean acted = actV2(model, unitID, ability, mousedAtTileID, mouse.isPressed());
        abilityComponent.setActed(acted);
        if (!acted) { return;  }
        model.getGameState().setAutomaticallyGoToHomeControls(true);
    }


//    public void updateUserV2(GameModel model, String unitID) {
//        boolean isAbilityPanelOpen = model.getGameState().isAbilityPanelOpen();
//        if (!isAbilityPanelOpen) { return; }
//
//        Entity unitEntity = EntityStore.getInstance().get(unitID);
//        String currentTurnsUnitID = model.getSpeedQueue().peekV2();
//        if (currentTurnsUnitID == null || !currentTurnsUnitID.equals(unitID)) { return; }
//
//        Mouse mouse = InputController.getInstance().getMouse();
//        Entity mousedAtTileEntity = model.tryFetchingMousedAtTileEntity();
//        if (mousedAtTileEntity == null) { return; }
//        IdentityComponent mousedAtTileIdentity = mousedAtTileEntity.get(IdentityComponent.class);
//        String mousedAtTileEntityID = mousedAtTileIdentity.getID();
//        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
//
//        // Check that the unit actually has the ability, not sure why we need to check, but keeping for now
//        String ability = abilityComponent.getAbility();
//        if (ability == null) { return; }
//
//        // Execute the action
//        boolean acted = actV2(model, unitID, ability, mousedAtTileEntityID, mouse.isPressed());
//        abilityComponent.setActed(acted);
//        if (!acted) { return;  }
//        model.getGameState().setAutomaticallyGoToHomeControls(true);
//    }


    public void updateAI(GameModel model, String unitID) {
        String currentTurnsUnit = model.getSpeedQueue().peekV2();
        if (currentTurnsUnit == null || !currentTurnsUnit.equalsIgnoreCase(unitID)) { return; }

        Entity unitEntity = EntityStore.getInstance().get(unitID);
        Behavior behavior = unitEntity.get(Behavior.class);
        if (behavior.shouldWait()) { return; }

        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
        if (abilityComponent.hasActed()) { return; }
        // Check that the unit actually has the ability, not sure why we need to check, but keeping for now
        Pair<String, String> toActOn = mRandomnessBehavior.toActOnV2(model, unitEntity);
        if (toActOn != null) {
            actV2(model, unitID, toActOn.getSecond(), toActOn.getFirst(), true);
        }
        abilityComponent.setActed(true);
    }

    public boolean actV2(GameModel model, String actingUnitID, String ability, String targetedTileID, boolean commit) {

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
            Set<Entity> rng = algorithm.computeAreaOfSight(model, currentTileEntity, range);
            abilityComponent.stageRange(rng);
            mLogger.info("Updated area of sight for {}, viewing {} tiles", unitEntity, rng.size());
        }

        isUpdated = isUpdated("action_line_of_sight", currentTileID, targetedTileID);
        if (isUpdated) {
            Set<Entity> los = algorithm.computeLineOfSight(model, currentTileEntity, targetedTileEntity);
            abilityComponent.stageLineOfSight(los);
            mLogger.info("Updated line of sight for {}, viewing {} tiles", unitEntity, los.size());
        }

        isUpdated = isUpdated("action_area_of_effect", targetedTileID, area);
        if (isUpdated) {
            Set<Entity> aoe = algorithm.computeAreaOfSight(model, targetedTileEntity, area);
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

        abilityComponent.stageTarget(targetedTileEntity);
        abilityComponent.stageAbility(ability);

        // try executing action only if specified
        // - Target is not null
        // - Target is within range
        // - We are not in preview mode
        if (targetedTileID == null || !commit || !abilityComponent.isValidTarget() || abilityComponent.hasActed()) {
            return false;
        }
        abilityComponent.commit();

        Set<Entity> targets = abilityComponent.getStagedTileAreaOfEffect();
        Set<String> targetTileIDs = targets.stream().map(e -> e.get(IdentityComponent.class).getID()).collect(Collectors.toSet());

//        boolean success = AbilityDatabase.getInstance().use(model, unitEntity, ability, targets);
        boolean success = AbilityDatabase.getInstance().useV2(model, actingUnitID, ability, targetTileIDs);


        mLogger.info("Used from {} on {}", ability, targetedTileEntity);

        return success;
    }

    private void applyEffects(GameModel model, Entity target, ActionEvent event, Set<Map.Entry<String, Float>> statuses) {
        // Go through all the different status effects and their probability
        StatisticsComponent statisticsComponent = target.get(StatisticsComponent.class);
        for (Map.Entry<String, Float> entry : statuses) {
            // If the stat chance passes, handle
            float statusChance = Math.abs(entry.getValue());
            if (statusChance < mRandom.nextFloat()) { continue; }
            // Check if the status effect increases a stat
            String status = entry.getKey();
//            StatNode node = statistics.getStatsNode(status);


            if (status.endsWith("Knockback")) {
//                handleKnockback(model, target, event);
            } else {
                target.get(TagComponent.class).add(status, event.getAction());
            }
//            Color c = ColorPalette.getColorOfAbility(event.action);

//            if (node != null) {
//                statistics.modify(status,
//                        event.ability, StatNode.MULTIPLICATIVE, (int) (entry.getValue() <  0 ? -.5f : .5f));
//                model.logger.log(target + "'s " + status + " " +
//                        (entry.getValue() <  0 ? "decreased" : "increased"));
//
//                announceWithFloatingText(gameModel, (entry.getValue() <  0 ? "-" : "+") +
//                                StringUtils.spaceByCapitalization(status), target, c);
//            } else {
//                announceWithFloatingText(gameModel,
//                        StringUtils.spaceByCapitalization(status) + "'d", target, c);
//                model.logger.log(target + " was inflicted with " + StringUtils.spaceByCapitalization(status));
//            }
            mLogger.info("{} has {}", target, status);
        }
    }

//    public void applyAnimation(GameModel model, Entity unitEntity, String animation, Entity target) {
//        if (unitEntity == null) { return; }
//        AnimationSystem animationSystem = model.getSystems().getAnimationSystem();
//        switch (animation) {
//            case TO_TARGET_AND_BACK -> animationSystem.executeToTargetAndBackAnimation(model, unitEntity, target);
//            case GYRATE -> animationSystem.executeGyrateAnimation(model, unitEntity);
//            case SHAKE -> animationSystem.executeShakeAnimation(model, unitEntity);
//        }
//    }
}
