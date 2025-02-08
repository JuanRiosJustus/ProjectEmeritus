package main.game.systems;

import main.constants.Direction;
import main.constants.StateLock;
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
    private final StateLock mStateLock = new StateLock();
//    @Override
//    public void update(GameModel model, Entity unitEntity) {
//        // Only move if its entities turn
//        if (model.getSpeedQueue().peek() != unitEntity) { return; }
//
//        // Only move if not already moved
//        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//        if (movementComponent.hasMoved()) { return; }
//
//        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
//        if (animationComponent.hasPendingAnimations()) { return; }
//        // Handle user and AI separately
//        Behavior behavior = unitEntity.get(Behavior.class);
//        if (behavior.isUserControlled()) {
////            updateUser(model, unitEntity, InputController.getInstance());
//        } else {
////            updateAi(model, unitEntity);
//        }
//    }

    public void update(GameModel model, String unitID) {
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

        // Get the moused at tile
        String mousedAtTileID = model.tryFetchingMousedAtTileID();

        // Execute the movement
        move(model, unitID, mousedAtTileID, false);

        Entity unitEntity = EntityStore.getInstance().get(unitID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Mouse mouse = controller.getMouse();
        if (!mouse.isPressed() || movementComponent.hasMoved()) { return; }

        boolean moved = move(model, unitID, mousedAtTileID, true);

        movementComponent.setMoved(moved);

        if (!moved) { return; }
        model.getGameState().setAutomaticallyGoToHomeControls(true);
    }

//    private void updateUser(GameModel model, String unitID, InputController controller) {
//        boolean isMovementPanelBeingUsed = model.getGameState().isMovementPanelOpen();
//        if (!isMovementPanelBeingUsed) { return; }
//
//        // Get the moused at tile
//        Mouse mouse = controller.getMouse();
//        Entity mousedAtTileEntity = model.tryFetchingTileMousedAt();
//        if (mousedAtTileEntity == null) { return; }
//
//        IdentityComponent mousedAtTileEntityIdentityComponent = mousedAtTileEntity.get(IdentityComponent.class);
//        String tileID = mousedAtTileEntityIdentityComponent.getID();
//        Entity unitEntity = EntityStore.getInstance().get(unitID);
//        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//
//        // Execute the movement
//        move(model, unitID, tileID, false);
//
//        if (!mouse.isPressed() || movementComponent.hasMoved()) { return; }
//
//        boolean moved = move(model, unitID, tileID, true);
////        String currentID = movementComponent.getCurrentTile();
////        String nextID = mousedAtTileEntityIdentityComponent.getID();
//
////        Tile current = movementComponent.getCurrentTileV1().get(Tile.class);
////        Tile next = mousedAt.get(Tile.class);
////        mLogger.info("Moving from {} to {}", current.getBasicIdentityString(), next.getBasicIdentityString());
////        mLogger.info("Moving from {} to {}");
//
//        movementComponent.setMoved(moved);
//
//        if (!moved) { return; }
//        model.getGameState().setAutomaticallyGoToHomeControls(true);
//    }

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
        Entity unitEntity = getEntityWithID(unitID);
        Behavior unitBehavior = unitEntity.get(Behavior.class);
        if (unitBehavior.shouldWait()) { return; }

        MovementComponent unitMovement = unitEntity.get(MovementComponent.class);

        String tileToMoveToID = mRandomnessBehavior.toMoveTo(model, unitID);
        if (tileToMoveToID == null) {  unitMovement.setMoved(true); return; }

        boolean moved = move(model, unitID, tileToMoveToID, true);
        unitMovement.setMoved(moved);
    }

    private boolean move(GameModel model, String unitToMoveID, String tileToMoveUnitToID, boolean commit) {
        Entity unitEntity = getEntityWithID(unitToMoveID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        int move = statisticsComponent.getTotalMovement();
        int climb = statisticsComponent.getTotalClimb();

        String toMoveFromTileID = movementComponent.getCurrentTileID();
        Entity toMoveFromTileEntity = getEntityWithID(toMoveFromTileID);
        Entity toMoveToTileEntity = getEntityWithID(tileToMoveUnitToID);

        // This can be flood the console, statelock to prevent flooding, probably not necessary
        boolean shouldUpdateLogger = mStateLock.isUpdated("planning_to_move_logger", toMoveFromTileEntity, toMoveToTileEntity);
        if (shouldUpdateLogger) {
            mLogger.info("{} is planning to move from {} to {}", unitEntity, toMoveFromTileEntity, toMoveToTileEntity);
        }

        // Only update then the tile to move from has changed, or the units move or climb stat changed
        boolean isUpdated = movementComponent.isUpdatedState("movement_range", toMoveFromTileID, move, climb);
        if (isUpdated) {
            Set<Entity> area = algorithm.computeMovementArea(model, toMoveFromTileEntity, move);
            movementComponent.stageMovementRange(area);
            mLogger.info("Updated movement area for {}({})", unitToMoveID, unitEntity);
        }

        // Only update when the tile to move to has changed, or the units move or climb stat changed
        isUpdated = movementComponent.isUpdatedState("movement_path", tileToMoveUnitToID, move, climb);
        if (isUpdated) {
            Set<Entity> path = algorithm.computeMovementPath(model, toMoveFromTileEntity, toMoveToTileEntity);
            movementComponent.stageMovementPath(path);
            mLogger.info("Updated movement path for {}({})", unitToMoveID, unitEntity);
        }

        movementComponent.stageTarget(toMoveToTileEntity);

        // try executing action only if specified
        // - Target is not null
        // - Target is within range
        // - We are not in preview mode
        // - We are targeting the current tile were one
        if (!commit) { return false; }
        if (toMoveToTileEntity == null) { return false; }
        if (toMoveToTileEntity == toMoveFromTileEntity) { return false; }
        if (!movementComponent.isValidMovementPath()) { return false; }

        movementComponent.commit();

        // do the animation for the tile
        setAnimationTrack(model, unitEntity, movementComponent.getStagedTilePath());

        Tile tile = toMoveToTileEntity.get(Tile.class);
        tile.setUnit(unitToMoveID);

//        DirectionComponent directionComponent = unitEntity.get(DirectionComponent.class);
//        directionComponent.setDirection(getDirection(currentTile, tileToMoveTo));


        mLogger.info("Moved from {} to {}", toMoveFromTileEntity, toMoveToTileEntity);
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
