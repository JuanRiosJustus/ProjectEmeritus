package main.game.systems;

import main.constants.Pair;
import main.constants.Tuple;
import main.constants.Vector3f;
import main.game.components.*;
import main.game.components.behaviors.Behavior;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathing.lineofsight.PathingAlgorithms;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.action.ActionDatabase;
import main.game.stores.pools.action.ActionEvent;
import main.game.systems.actions.ActionHandler;
import main.game.systems.actions.behaviors.AggressiveBehavior;
import main.game.systems.actions.behaviors.RandomnessBehavior;
import main.game.systems.combat.CombatReport;
import main.game.systems.texts.FloatingText;
import main.input.InputController;
import main.input.Mouse;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.StringUtils;

import java.awt.Color;
import java.util.*;

public class ActionSystem extends GameSystem {
    private final SplittableRandom mRandom = new SplittableRandom();
    private final ELogger mLogger = ELoggerFactory.getInstance().getELogger(ActionSystem.class);
    private final PathingAlgorithms algorithm = new PathingAlgorithms() {
    };
    private final AggressiveBehavior mAggressiveBehavior = new AggressiveBehavior();
    private final RandomnessBehavior mRandomnessBehavior = new RandomnessBehavior();

    private static final String GYRATE = "gyrate";
    private static final String TO_TARGET_AND_BACK = "to_target_and_back";
    private static final String SHAKE = "shake";
    private static final int DEFAULT_VISION_RANGE = 8;

    private static final String ACTION_SYSTEM = "ACTION_SYSTEM";
    private ActionHandler actionHandler = new ActionHandler();

    @Override
    public void update(GameModel model, Entity unitEntity) {
        actionHandler.finishAction(model);
//        finishAction();

        Behavior behavior = unitEntity.get(Behavior.class);

        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        if (actionComponent.hasActed()) { return; }

        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        if (animationComponent.hasPendingAnimations()) { return; }


        if (behavior.isUserControlled()) {
            updateUser(model, unitEntity);
        } else {
            updateAI(model, unitEntity);
        }
    }

    public void updateUser(GameModel model, Entity unitEntity) {
        boolean isActionPanelOpen = model.getGameState().isActionPanelOpen();
        if (!isActionPanelOpen) { return; }

        if (model.getSpeedQueue().peek() != unitEntity) { return; }

        Mouse mouse = InputController.getInstance().getMouse();
        Entity mousedAt = model.tryFetchingTileMousedAt();

        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        // Check that the unit actually has the ability, not sure why we need to check, but keeping for now
        String action = actionComponent.getAction();
        if (action == null) { return; }

        // Execute the action
        boolean acted = act(model, unitEntity, action, mousedAt, mouse.isPressed());
        actionComponent.setActed(acted);
        if (!acted) { return;  }
        model.getGameState().setAutomaticallyGoToHomeControls(true);
    }

    public void updateAI(GameModel model, Entity unitEntity) {
        if (model.getSpeedQueue().peek() != unitEntity) { return; }

        Behavior behavior = unitEntity.get(Behavior.class);
        if (behavior.shouldWait()) { return; }

        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        if (actionComponent.hasActed()) { return; }
        // Check that the unit actually has the ability, not sure why we need to check, but keeping for now
        Pair<Entity, String> toActOn = mRandomnessBehavior.toActOn(model, unitEntity);
        if (toActOn != null) {
            act(model, unitEntity, toActOn.second, toActOn.first, true);
        }
        actionComponent.setActed(true);
    }

    public boolean act(GameModel model, Entity unitEntity, String action, Entity target, boolean commit) {
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        Entity currentTile = movementComponent.getCurrentTile();;
        int range = ActionDatabase.getInstance().getRange(action);
        int area = ActionDatabase.getInstance().getArea(action);

        boolean isUpdated = actionComponent.isUpdated("action_range", currentTile, range);
        if (isUpdated) {
            Set<Entity> rng = algorithm.computeAreaOfSight(model, currentTile, range);
            actionComponent.stageRange(rng);
        }

        isUpdated = actionComponent.isUpdated("action_line_of_sight", currentTile, target);
        if (isUpdated) {
            Set<Entity> los = algorithm.computeLineOfSight(model, currentTile, target);
            actionComponent.stageLineOfSight(los);
        }

        isUpdated = actionComponent.isUpdated("action_area_of_effect", target, area);
        if (isUpdated) {
            Set<Entity> aoe = algorithm.computeAreaOfSight(model, target, area);
            actionComponent.stageAreaOfEffect(aoe);
        }

        // Below may or may not be used
        boolean showVision = model.getGameState().shouldShowActionRanges();
        isUpdated = actionComponent.isUpdated("action_vision_range", currentTile, DEFAULT_VISION_RANGE, showVision);
        if (isUpdated) {
//            Queue<Entity> tilesWithinVision = mPathBuilder.getTilesInActionRange(model, currentTile, DEFAULT_VISION_RANGE);
//            actionComponent.stageVision(tilesWithinVision);
        }

        actionComponent.stageTarget(target);
        actionComponent.stageAction(action);

        // try executing action only if specified
        // - Target is not null
        // - Target is within range
        // - We are not in preview mode
        if (target == null || !commit || !actionComponent.isValidTarget() || actionComponent.hasActed()) {
            return false;
        }
        actionComponent.commit();

//        ActionDatabase.getInstance().useAbility()
        return actionHandler.startAction(model, unitEntity, action, actionComponent.getStagedTileAreaOfEffect());

//        return startAction(
//                        model,
//                        unitEntity,
//                        action,
//                        actionComponent.getStagedTileAreaOfEffect()
//                );
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
