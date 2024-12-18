package main.game.systems;


import main.game.components.behaviors.Behavior;
import main.game.components.behaviors.UserBehavior;
import main.game.components.*;
import main.game.components.behaviors.AiBehavior;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.systems.actions.BehaviorSystem;
import main.game.systems.texts.FloatingTextSystem;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.awt.image.BufferedImage;

public class UpdateSystem {

    public UpdateSystem() { }
    private boolean endTurn = false;
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private final HandleEndOfTurnSystem mHandleEndOfTurnSystem = new HandleEndOfTurnSystem();
    public final AnimationSystem mAnimationSystem = new AnimationSystem();
    public final OverlaySystem mOverlaySystem = new OverlaySystem();
    public final FloatingTextSystem mFloatingTextSystem = new FloatingTextSystem();
    private final GemSpawnerSystem gemSpawnerSystem = new GemSpawnerSystem();
    private final TileVisualsSystem mTileVisualsSystem = new TileVisualsSystem();
    private final UnitVisualsSystem mUnitVisualsSystem = new UnitVisualsSystem();
    private final ActionSystem mActionSystem = new ActionSystem();
    private final MovementSystem mMovementSystem = new MovementSystem();
    private final BehaviorSystem mBehaviorSystem = new BehaviorSystem();

    public void update(GameModel model) {
        // update all tiles and units
        for (int row = 0; row < model.getRows(); row++) {
            for (int column = 0; column < model.getColumns(); column++) {
                Entity entity = model.tryFetchingEntityAt(row, column);
                mTileVisualsSystem.update(model, entity);
                Tile tile = entity.get(Tile.class);
                updateUnit(model, tile.getUnit());
            }
        }

        mOverlaySystem.update(model, null);
        mFloatingTextSystem.update(model, null);

        Entity currentActiveUnitEntity = model.getSpeedQueue().peek();

        boolean shouldAutoEndTurn = model.getGameState().shouldAutomaticallyEndControlledTurns();
        if (shouldAutoEndTurn) {
            endTurn();


//            model.getGameState().setEndCurrentUnitsTurn(false);
//            model.getGameState().setShouldEndTheTurn(false);
            if (currentActiveUnitEntity.get(UserBehavior.class) == null) {
//                model.setGameState(GameState.CHANGE_BATTLE_UI_TO_HOME_SCREEN, true);
                model.getGameState().setAutomaticallyGoToHomeControls(true);
            }
        }

        if (endTurn) {
            mHandleEndOfTurnSystem.update(model, currentActiveUnitEntity);
            gemSpawnerSystem.update(model, currentActiveUnitEntity);
            endTurn = false;
//            endTurn(model, current);
        }

        boolean newRound = model.getSpeedQueue().update();
        if (newRound) { model.mLogger.log("New Round"); }


        mTileVisualsSystem.createBackgroundImageWallpaper(model);
    }

    private void updateUnit(GameModel model, Entity unitEntity) {
        if (unitEntity == null) { return; }

        mBehaviorSystem.update(model, unitEntity);
        mAnimationSystem.update(model, unitEntity);
        mUnitVisualsSystem.update(model, unitEntity);

//        if (model.getGameState().isUnitDeploymentMode()) { return; }

        Behavior behavior = unitEntity.get(Behavior.class);
        if (behavior.isUserControlled()) {
            updateUser(model, unitEntity);
        } else {
            updateAi(model, unitEntity);
        }

        handleAutoEndTurn(model, unitEntity);
    }

    private void updateUser(GameModel model, Entity unitEntity) {
        mActionSystem.update(model, unitEntity);
        mMovementSystem.update(model, unitEntity);
    }

    private void updateAi(GameModel model, Entity unitEntity) {
        Behavior behavior = unitEntity.get(Behavior.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);

        if (behavior.shouldMoveFirst()) {
            if (!movementComponent.hasMoved()) {
                mMovementSystem.update(model, unitEntity);
            } else if (!actionComponent.hasActed()) {
                mActionSystem.update(model, unitEntity);
            }
        } else {
            if (!actionComponent.hasActed()) {
                mActionSystem.update(model, unitEntity);
            } else if (!movementComponent.hasMoved()) {
                mMovementSystem.update(model, unitEntity);
            }
        }
    }

    private void handleAutoEndTurn(GameModel model, Entity unitEntity) {
        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        Behavior behavior = unitEntity.get(Behavior.class);

        // If the unit has moved, were allowed to end the turn;
        if (!movementComponent.hasMoved()) { return; }
        // If the unit has acted, were allowed to end the turn
        if (!actionComponent.hasActed()) { return; }
        // if the unit is waiting for some reason, do not end the turn
        if (behavior.shouldWait()) { return; }
        // if the unit has pending animations, do not end the turn
        if (animationComponent.hasPendingAnimations()) { return; }

        endTurn();
    }

    public void endTurn() { endTurn = true; }

    private void endTurn(GameModel model, Entity unit) {
        TagComponent tagComponent = unit.get(TagComponent.class);
        model.getSpeedQueue().dequeue();
        if (tagComponent.contains(TagComponent.YIELD)) {
            model.getSpeedQueue().requeue(unit);
        }

        Entity turnStarter = model.getSpeedQueue().peek();
        if (turnStarter != null) { model.mLogger.log(turnStarter.get(IdentityComponent.class) + "'s turn starts"); }

        logger.info("Starting new Turn");

        ActionComponent actionComponent = unit.get(ActionComponent.class);
        actionComponent.reset();

        MovementComponent movementComponent = unit.get(MovementComponent.class);
        movementComponent.reset();

        Behavior behavior = unit.get(AiBehavior.class);
//        if (behavior == null) { behavior = unit.get(UserBehavior.class); }
//        behavior.reset();

//        Tags tags = unit.get(Tags.class);
        TagComponent.handleEndOfTurn(model, unit);
        tagComponent.reset();

//        Passives passives = unit.get(Passives.class);
//        if (passives.contains(Passives.MANA_REGEN_I)) {
//            Summary summary = unit.get(Summary.class);
//            int amount = summary.addTotalAmountToResource(Summary.MANA, .05f);
//            Animation animation = unit.get(Animation.class);
//            model.system.floatingText.floater("+" + amount + "EP", animation.getVector(), ColorPalette.WHITE);
//        }

        gemSpawnerSystem.update(model, unit);
        endTurn = false;
    }

    public FloatingTextSystem getFloatingTextSystem() { return mFloatingTextSystem; }
    public ActionSystem getActionSystem() { return mActionSystem; }
    public MovementSystem getMovementSystem() { return mMovementSystem; }
    public AnimationSystem getAnimationSystem() { return mAnimationSystem; }
    public BufferedImage getBackgroundWallpaper() {
        return mTileVisualsSystem.getBackgroundWallpaper();
    }
}
