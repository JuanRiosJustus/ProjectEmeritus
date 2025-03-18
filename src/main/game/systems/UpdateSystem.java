package main.game.systems;


import javafx.scene.image.Image;
import main.game.components.behaviors.Behavior;
import main.game.components.behaviors.UserBehavior;
import main.game.components.*;
import main.game.components.behaviors.AiBehavior;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;
import main.game.systems.actions.BehaviorSystem;
import main.game.systems.texts.FloatingTextSystem;
import main.logging.EmeritusLogger;


import java.awt.image.BufferedImage;

public class UpdateSystem {

    public UpdateSystem() { }
    private boolean endTurn = false;
    private final EmeritusLogger logger = EmeritusLogger.create(getClass());
    private final HandleEndOfTurnSystem mHandleEndOfTurnSystem = new HandleEndOfTurnSystem();
    public final AnimationSystem mAnimationSystem = new AnimationSystem();
    public final OverlaySystem mOverlaySystem = new OverlaySystem();
    public final FloatingTextSystem mFloatingTextSystem = new FloatingTextSystem();
    private final GemSpawnerSystem gemSpawnerSystem = new GemSpawnerSystem();
    private final VisualsSystem mVisualsSystem = new VisualsSystem();
    private final UnitVisualsSystem mUnitVisualsSystem = new UnitVisualsSystem();
    private final AbilitySystem mAbilitySystem = new AbilitySystem();
    private final MovementSystem mMovementSystem = new MovementSystem();
    private final BehaviorSystem mBehaviorSystem = new BehaviorSystem();

    public void update(GameModel model) {
        // update all tiles and units
        for (int row = 0; row < model.getRows(); row++) {
            for (int column = 0; column < model.getColumns(); column++) {
                Entity entity = model.tryFetchingEntityAt(row, column);
                IdentityComponent identityComponent = entity.get(IdentityComponent.class);
                String entityID = identityComponent.getID();
                mVisualsSystem.update(model, entityID);

                Tile tile = entity.get(Tile.class);
                String unitID = tile.getUnitID();
                if (unitID.isBlank()) { continue; }

                updateUnit(model, unitID);
            }
        }

        mOverlaySystem.update(model, null);
        mFloatingTextSystem.update(model, null);

        String currentTurnsEntityID = model.getSpeedQueue().peekV2();

        boolean shouldAutoEndTurn = model.getGameState().shouldAutomaticallyEndControlledTurns();
        boolean shouldEndTurn = model.getGameState().shouldEndTheTurn();
        if (shouldAutoEndTurn) {
            endTurn();


//            model.getGameState().setEndCurrentUnitsTurn(false);
//            model.getGameState().setShouldEndTheTurn(false);

            Entity currentActiveUnitEntity = EntityStore.getInstance().get(currentTurnsEntityID);

            if (currentActiveUnitEntity != null && currentActiveUnitEntity.get(UserBehavior.class) == null) {
//                model.setGameState(GameState.CHANGE_BATTLE_UI_TO_HOME_SCREEN, true);
                model.getGameState().setAutomaticallyGoToHomeControls(true);
            }
        }

        if ((endTurn || shouldEndTurn) && !mAnimationSystem.hasPendingAnimations()) {
            mHandleEndOfTurnSystem.update(model, currentTurnsEntityID);
//            gemSpawnerSystem.update(model, currentActiveUnitEntity);
            endTurn = false;
//            endTurn(model, current);
        }

        boolean newRound = model.getSpeedQueue().update();
        if (newRound) { model.mLogger.log("New Round"); }


        mVisualsSystem.createBackgroundImageWallpaper(model);
    }


    private void updateUnit(GameModel model, String unitID) {
        if (unitID == null) { return; }

//        mBehaviorSystem.update(model, unitEntity);
        mBehaviorSystem.update(model, unitID);

//        mAnimationSystem.update(model, unitEntity);
        mAnimationSystem.update(model, unitID);

//        mUnitVisualsSystem.update(model, unitEntity);
        mUnitVisualsSystem.update(model, unitID);

//        if (model.getGameState().isUnitDeploymentMode()) { return; }

        Entity unitEntity = EntityStore.getInstance().get(unitID);
        Behavior behavior = unitEntity.get(Behavior.class);

        if (behavior.isUserControlled()) {
            updateUser(model, unitID);
        } else {
            updateAi(model, unitID);
        }

        handleAutoEndTurn(model, unitEntity);
    }

    private void updateUser(GameModel model, String unitID) {
        mAbilitySystem.update(model, unitID);
        mMovementSystem.update(model, unitID);
    }

    private void updateAi(GameModel model, String unitID) {
        Entity unitEntity = EntityStore.getInstance().get(unitID);
        Behavior behavior = unitEntity.get(Behavior.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);

        if (behavior.shouldMoveFirst()) {
            if (!movementComponent.hasMoved()) {
                mMovementSystem.update(model, unitID);
            } else if (!abilityComponent.hasActed()) {
                mAbilitySystem.update(model, unitID);
            }
        } else {
            if (!abilityComponent.hasActed()) {
                mAbilitySystem.update(model, unitID);
            } else if (!movementComponent.hasMoved()) {
                mMovementSystem.update(model, unitID);
            }
        }
    }

    private void handleAutoEndTurn(GameModel model, Entity unitEntity) {
        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
        Behavior behavior = unitEntity.get(Behavior.class);

        // If the unit has moved, were allowed to end the turn;
        if (!movementComponent.hasMoved()) { return; }
        // If the unit has acted, were allowed to end the turn
        if (!abilityComponent.hasActed()) { return; }
        // if the unit is waiting for some reason, do not end the turn
        if (behavior.shouldWait()) { return; }
        // if the unit has pending animations, do not end the turn
        if (animationComponent.hasPendingAnimations()) { return; }

        endTurn();
    }

    public void endTurn() { endTurn = true; }

    private void endTurn(GameModel model, Entity unit) {
//        TagComponent tagComponent = unit.get(TagComponent.class);
////        model.getSpeedQueue().dequeue();
//        if (tagComponent.contains(TagComponent.YIELD)) {
////            model.getSpeedQueue().requeue(unit);
//        }
//
//        Entity turnStarter = model.getSpeedQueue().peek();
//        if (turnStarter != null) { model.mLogger.log(turnStarter.get(IdentityComponent.class) + "'s turn starts"); }
//
//        logger.info("Starting new Turn");
//
//        AbilityComponent abilityComponent = unit.get(AbilityComponent.class);
//        abilityComponent.reset();
//
//        MovementComponent movementComponent = unit.get(MovementComponent.class);
//        movementComponent.reset();
//
//        Behavior behavior = unit.get(AiBehavior.class);
////        if (behavior == null) { behavior = unit.get(UserBehavior.class); }
////        behavior.reset();
//
////        Tags tags = unit.get(Tags.class);
//        TagComponent.handleEndOfTurn(model, unit);
//        tagComponent.reset();
//
////        Passives passives = unit.get(Passives.class);
////        if (passives.contains(Passives.MANA_REGEN_I)) {
////            Summary summary = unit.get(Summary.class);
////            int amount = summary.addTotalAmountToResource(Summary.MANA, .05f);
////            Animation animation = unit.get(Animation.class);
////            model.system.floatingText.floater("+" + amount + "EP", animation.getVector(), ColorPalette.WHITE);
////        }
//
//        gemSpawnerSystem.update(model, unit);
//        endTurn = false;
    }

    public FloatingTextSystem getFloatingTextSystem() { return mFloatingTextSystem; }
    public AbilitySystem getActionSystem() { return mAbilitySystem; }
    public MovementSystem getMovementSystem() { return mMovementSystem; }
    public AnimationSystem getAnimationSystem() { return mAnimationSystem; }
    public Image getBackgroundWallpaper() {
        return mVisualsSystem.getBackgroundWallpaper();
    }
}
