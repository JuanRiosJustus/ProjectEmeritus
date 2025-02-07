package main.game.systems;

import main.constants.Direction;
import main.game.components.*;
import main.game.components.behaviors.Behavior;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathing.lineofsight.PathingAlgorithms;
import main.game.stores.factories.EntityStore;
import main.game.systems.actions.behaviors.AggressiveBehavior;
import main.game.systems.actions.behaviors.RandomnessBehavior;
import main.input.InputController;
import main.input.Mouse;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.RandomUtils;

import java.util.Set;

public class MovementSystem extends GameSystem {
    private final ELogger mLogger = ELoggerFactory.getInstance().getELogger(MovementSystem.class);
    private final AggressiveBehavior mAggressiveBehavior = new AggressiveBehavior();
    private final RandomnessBehavior mRandomnessBehavior = new RandomnessBehavior();
    private final PathingAlgorithms algorithm = new PathingAlgorithms();
    @Override
    public void update(GameModel model, Entity unitEntity) {
        // Only move if its entities turn
        if (model.getSpeedQueue().peek() != unitEntity) { return; }

        // Only move if not already moved
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        if (movementComponent.hasMoved()) { return; }

        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        if (animationComponent.hasPendingAnimations()) { return; }
        // Handle user and AI separately
        Behavior behavior = unitEntity.get(Behavior.class);
        if (behavior.isUserControlled()) {
//            updateUser(model, unitEntity, InputController.getInstance());
        } else {
//            updateAi(model, unitEntity);
        }
    }

    public void updateV2(GameModel model, String unitID) {
        // Only move if its entities turn
        String unitOfCurrentTurnID = model.getSpeedQueue().peekV2();
        if (unitOfCurrentTurnID != null && !unitOfCurrentTurnID.equalsIgnoreCase(unitID)) { return; }

        // Only move if not already moved
        Entity unitEntity = EntityStore.getInstance().get(unitID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        if (movementComponent.hasMoved()) { return; }

        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        if (animationComponent.hasPendingAnimations()) { return; }
        // Handle user and AI separately
        Behavior behavior = unitEntity.get(Behavior.class);
        if (behavior.isUserControlled()) {
            updateUser(model, unitID, InputController.getInstance());
        } else {
            updateAi(model, unitID);
        }
    }

    private void updateUser(GameModel model, String unitID, InputController controller) {
        boolean isMovementPanelBeingUsed = model.getGameState().isMovementPanelOpen();
        if (!isMovementPanelBeingUsed) { return; }


        Mouse mouse = controller.getMouse();
        Entity mousedAtTileEntity = model.tryFetchingTileMousedAt();
        if (mousedAtTileEntity == null) { return; }

        IdentityComponent mousedAtTileEntityIdentityComponent = mousedAtTileEntity.get(IdentityComponent.class);
        String tileID = mousedAtTileEntityIdentityComponent.getID();
        Entity unitEntity = EntityStore.getInstance().get(unitID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);

        // Execute the movement
        move(model, unitID, tileID, false);

        if (!mouse.isPressed() || movementComponent.hasMoved()) { return; }

        boolean moved = move(model, unitID, tileID, true);
//        String currentID = movementComponent.getCurrentTile();
//        String nextID = mousedAtTileEntityIdentityComponent.getID();

//        Tile current = movementComponent.getCurrentTileV1().get(Tile.class);
//        Tile next = mousedAt.get(Tile.class);
//        mLogger.info("Moving from {} to {}", current.getBasicIdentityString(), next.getBasicIdentityString());
//        mLogger.info("Moving from {} to {}");

        movementComponent.setMoved(moved);

        if (!moved) { return; }
        model.getGameState().setAutomaticallyGoToHomeControls(true);
    }

//    private void updateUser(GameModel model, Entity unitEntity, InputController controller) {
//        boolean isMovementPanelBeingUsed = model.getGameState().isMovementPanelOpen();
//        if (!isMovementPanelBeingUsed) { return; }
//
//
//        Mouse mouse = controller.getMouse();
//        Entity mousedAt = model.tryFetchingTileMousedAt();
//        if (mousedAt == null) { return; }
//        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//
//        // Execute the movement
//        boolean moved = move(model, unitEntity.get(IdentityComponent.class).getID(), mousedAt.get(IdentityComponent.class).getID(), false);
//        if (!mouse.isPressed() || movementComponent.hasMoved()) { return; }
//
//        moved = move(model, unitEntity.get(IdentityComponent.class).getID(), mousedAt.get(IdentityComponent.class).getID(), true);
//        String currentID = movementComponent.getCurrentTile();
//        IdentityComponent mousedAtID = mousedAt.get(IdentityComponent.class);
//        String nextID = mousedAtID.getID();
//
//        Tile current = movementComponent.getCurrentTileV1().get(Tile.class);
//        Tile next = mousedAt.get(Tile.class);
//        mLogger.info("Moving from {} to {}", current.getBasicIdentityString(), next.getBasicIdentityString());
//
//        movementComponent.setMoved(moved);
//
//        if (!moved) { return; }
//        model.getGameState().setAutomaticallyGoToHomeControls(true);
//    }

    private void updateAi(GameModel model, String unitID) {
        Entity unitEntity = EntityStore.getInstance().get(unitID);
        Behavior unitBehavior = unitEntity.get(Behavior.class);
        if (unitBehavior.shouldWait()) { return; }

        MovementComponent unitMovement = unitEntity.get(MovementComponent.class);

        Entity toMoveToTileEntity = mRandomnessBehavior.toMoveTo(model, unitID);
        if (toMoveToTileEntity == null) {  unitMovement.setMoved(true); return; }
        IdentityComponent toMoveToTileID = toMoveToTileEntity.get(IdentityComponent.class);

        String tileID = toMoveToTileID.getID();

        String currentTileID = unitMovement.getCurrentTileID();
        Entity currentTileEntity = EntityStore.getInstance().get(currentTileID);

        boolean moved = move(model, unitID, tileID, true);
        unitMovement.setMoved(moved);
    }

    private boolean move(GameModel model, String unitToMoveID, String tileToMoveUnitToID, boolean commit) {
        Entity unitEntity = EntityStore.getInstance().get(unitToMoveID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        int move = statisticsComponent.getTotalMovement();
        int climb = statisticsComponent.getTotalClimb();

        String currentTileID = movementComponent.getCurrentTileID();
        Entity currentTileEntity = EntityStore.getInstance().get(currentTileID);
        Entity toMoveToTileEntity = EntityStore.getInstance().get(tileToMoveUnitToID);

        mLogger.info("Planning to move from {} to {}", currentTileEntity, toMoveToTileEntity);

        boolean isUpdated = movementComponent.isUpdatedState("movement_range", currentTileID, move, climb);
        if (isUpdated) {
            Set<Entity> area = algorithm.computeMovementArea(model, currentTileEntity, move);
            movementComponent.stageMovementRange(area);
        }

        isUpdated = movementComponent.isUpdatedState("movement_path", tileToMoveUnitToID, move, climb);
        if (isUpdated) {
            Set<Entity> path = algorithm.computeMovementPath(model, currentTileEntity, toMoveToTileEntity);
            movementComponent.stageMovementPath(path);
        }

        movementComponent.stageTarget(toMoveToTileEntity);

        // try executing action only if specified
        // - Target is not null
        // - Target is within range
        // - We are not in preview mode
        // - We are targeting the current tile were one
        if (!commit) { return false; }
        if (toMoveToTileEntity == null) { return false; }
        if (toMoveToTileEntity == currentTileEntity) { return false; }
        if (!movementComponent.isValidMovementPath()) { return false; }

        movementComponent.commit();

        // do the animation for the tile
        setAnimationTrack(model, unitEntity, movementComponent.getStagedTilePath());

        Tile tile = toMoveToTileEntity.get(Tile.class);
//        tile.setUnit(unitEntity);
        tile.setUnit(unitToMoveID);

//        DirectionComponent directionComponent = unitEntity.get(DirectionComponent.class);
//        directionComponent.setDirection(getDirection(currentTile, tileToMoveTo));
        return true;
    }


//    private boolean move(GameModel model, Entity unitEntity, Entity tileToMoveTo, boolean commit) {
//        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
//        int move = statisticsComponent.getTotalMovement();
//        int climb = statisticsComponent.getTotalClimb();
//
//        Entity currentTile = movementComponent.getCurrentTile();
//        boolean isUpdated = movementComponent.isUpdatedState("movement_range", currentTile, move, climb);
//        if (isUpdated) {
//            Set<Entity> area = algorithm.computeMovementArea(model, currentTile, move);
//            movementComponent.stageMovementRange(area);
//        }
//
//        isUpdated = movementComponent.isUpdatedState("movement_path", currentTile, tileToMoveTo, move, climb);
//        if (isUpdated) {
//            Set<Entity> path = algorithm.computeMovementPath(model, currentTile, tileToMoveTo);
//            movementComponent.stageMovementPath(path);
//        }
//
//        movementComponent.stageTarget(tileToMoveTo);
//
//        // try executing action only if specified
//        // - Target is not null
//        // - Target is within range
//        // - We are not in preview mode
//        // - We are targeting the current tile were one
//        if (tileToMoveTo == null || tileToMoveTo == currentTile || !commit || !movementComponent.isValidMovementPath()) {
//            return false;
//        }
//
//        movementComponent.commit();
//
//        // do the animation for the tile
//        setAnimationTrack(model, unitEntity, movementComponent.getStagedTilePath());
//
//        Tile tile = tileToMoveTo.get(Tile.class);
//        tile.setUnit(unitEntity);
//
//        DirectionComponent directionComponent = unitEntity.get(DirectionComponent.class);
//        directionComponent.setDirection(getDirection(currentTile, tileToMoveTo));
////
////        History history = unitEntity.get(History.class);
////        history.log("Moved to " + destination);
////        history = destination.get(History.class);
////        history.log("Traversed by " + unitEntity);
//
//        return true;
//    }

    public Direction getDirection(Entity start, Entity end) {
        Tile startTile = start.get(Tile.class);
        Tile endTile = end.get(Tile.class);

        // top left tile is 0,0 - top right is 0,N, bottom right is N,N
        // Thus, a positive number indicates movement north
        int verticalDirectionMagnitude = startTile.getRow() - endTile.getRow();
        Direction vertidalDirection = Direction.South;
        if (verticalDirectionMagnitude > 0) {
            vertidalDirection = Direction.North;
        }

        // a positive number indicates movement west
        int horizontalDirectionMagnitude = startTile.getColumn() - endTile.getColumn();
        Direction horizontalDirection = Direction.East;
        if (horizontalDirectionMagnitude > 0) {
            horizontalDirection = Direction.West;
        }

        Direction mostObviousDirection = vertidalDirection;
        if (Math.abs(horizontalDirectionMagnitude) > Math.abs(verticalDirectionMagnitude)) {
            mostObviousDirection = horizontalDirection;
        }

        return mostObviousDirection;
    }

    private void setAnimationTrack(GameModel model, Entity unitEntity, Set<Entity> pathing) {
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        if (movementComponent.shouldUseTrack()) {
            model.getSystems().getAnimationSystem().executeMoveAnimation(model, unitEntity, pathing);
        }
    }

//    public void undo(GameModel model, Entity unit) {
//        MovementComponent movementComponent = unit.get(MovementComponent.class);
//        movementComponent.moved = false;
//
//        Entity previous = movementComponent.previousTile;
//
//        movementComponent.useTrack = false;
//        move(model, unit, previous, true);
//        movementComponent.useTrack = true;
//        movementComponent.previousTile = null;
//
//        // handle waiting tile selection state
//        movementComponent.moved = false;
//    }

//    public boolean forceMove(GameModel model, Entity unit, Entity toMoveTo) {
//        MovementComponent movementComponent = unit.get(MovementComponent.class);
//        boolean hasMoved = movementComponent.mHasMoved;
//        boolean moved = move(model, unit, toMoveTo, true);
//        return moved;
//    }

    public int getSpeed(GameModel model, int speed1, int speed2) {
        int spriteWidth = model.getGameState().getSpriteWidth();
        int spriteHeight = model.getGameState().getSpriteHeight();
        float spriteSize = (float) (spriteWidth + spriteHeight) / 2;
        return (int) (spriteSize * RandomUtils.getRandomNumberBetween(speed1, speed2));
    }
}
