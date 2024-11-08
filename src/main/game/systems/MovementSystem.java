package main.game.systems;

import main.constants.Constants;
import main.constants.Direction;
import main.constants.Vector3f;
import main.game.components.*;
import main.game.components.behaviors.Behavior;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.PathBuilder;
import main.game.systems.actions.behaviors.AggressiveBehavior;
import main.game.systems.actions.behaviors.RandomnessBehavior;
import main.input.InputController;
import main.input.Mouse;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.RandomUtils;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;

public class MovementSystem extends GameSystem {
    private final ELogger mLogger = ELoggerFactory.getInstance().getELogger(MovementSystem.class);
    private final AggressiveBehavior mAggressiveBehavior = new AggressiveBehavior();
    private final RandomnessBehavior mRandomnessBehavior = new RandomnessBehavior();
    private final PathBuilder mPathBuilder = new PathBuilder();
    @Override
    public void update(GameModel model, Entity unitEntity) {
        // Only move if its entities turn
        if (model.getSpeedQueue().peek() != unitEntity) { return; }

        // Only move if not already moved
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        if (movementComponent.hasMoved()) { return; }

        TrackComponent trackComponent = unitEntity.get(TrackComponent.class);
        if (trackComponent.isMoving()) { return; }
        // Handle user and AI separately
        Behavior behavior = unitEntity.get(Behavior.class);
        if (behavior.isUserControlled()) {
            updateUser(model, unitEntity, InputController.getInstance());
        } else {
            updateAi(model, unitEntity);
        }
    }

    private void updateUser(GameModel model, Entity unitEntity, InputController controller) {
        boolean isMovementPanelBeingUsed = model.getGameState().isMovementPanelOpen();
        if (!isMovementPanelBeingUsed) { return; }

        Mouse mouse = controller.getMouse();
        Entity mousedAt = model.tryFetchingTileMousedAt();
        if (mousedAt == null) { return; }
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);

        // Execute the movement
        boolean moved = move(model, unitEntity, mousedAt, false);
        if (mouse.isPressed() && !movementComponent.hasMoved()) {
            moved = move(model, unitEntity, mousedAt, true);
            mLogger.info("Moving from {} to {}", movementComponent.getCurrentTile(), mousedAt);
            movementComponent.setMoved(moved);
            if (moved) {
                model.getGameState().setControllerToHomeScreen(true);
            }
        }
    }

    private void updateAi(GameModel model, Entity unitEntity) {
        Behavior behavior = unitEntity.get(Behavior.class);
        if (behavior.shouldWait()) { return; }
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity toMoveTo = mRandomnessBehavior.toMoveTo(model, unitEntity);
        mLogger.info("Moving from {} to {}", movementComponent.getCurrentTile(), toMoveTo);
        boolean moved = move(model, unitEntity, toMoveTo, true);
        movementComponent.setMoved(moved);
    }

    public boolean move(GameModel model, Entity unitEntity, Entity toMoveTo, boolean commit) {
        // Get the ranges of the movement
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        int move = statisticsComponent.getTotal(Constants.MOVE);
        int climb = statisticsComponent.getTotal(Constants.CLIMB);

        return move(model, unitEntity, toMoveTo, move, climb, commit);
    }

    private boolean move(GameModel model, Entity unitEntity, Entity target, int move, int climb, boolean commit) {
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);

        Entity currentTile = movementComponent.getCurrentTile();
        boolean isUpdated = movementComponent.isUpdatedState("movement_range", currentTile, move, climb);
        if (isUpdated) {
            Set<Entity> withinRange = mPathBuilder.getMovementRange(model, currentTile, move, climb);
            movementComponent.stageRange(withinRange);
        }

        isUpdated = movementComponent.isUpdatedState("movement_path", currentTile, target, move, climb);
        if (isUpdated) {
            LinkedList<Entity> withinPath = mPathBuilder.getMovementPath(model, currentTile, target, move, climb);
            movementComponent.stagePath(withinPath);
        }

        // try executing action only if specified
        // - Target is not null
        // - Target is within range
        // - We are not in preview mode
        // - We are targeting the current tile were one
        if (target == null || target == currentTile || !commit || !movementComponent.isValidPath(target)) {
            return false;
        }

        movementComponent.commit();

        // do the animation for the tile
        setAnimationTrack(model, unitEntity, movementComponent.getTilesInFinalPath());
        DirectionComponent directionComponent = unitEntity.get(DirectionComponent.class);
        directionComponent.setDirection(getDirection(currentTile, target));
//
//        History history = unitEntity.get(History.class);
//        history.log("Moved to " + destination);
//        history = destination.get(History.class);
//        history.log("Traversed by " + unitEntity);

        return true;
    }

    private boolean moveV1(GameModel model, Entity unitEntity, Entity target, int move, int climb, boolean commit) {
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);


        // Get the ranges and paths
        Set<Entity> withinRange = PathBuilder.newBuilder().getMovementRange(
                model,
                movementComponent.getCurrentTile(),
                move,
                climb
        );

        LinkedList<Entity> withinPath = PathBuilder.newBuilder().getMovementPath(
                model,
                movementComponent.getCurrentTile(),
                target,
                move,
                climb
        );

        movementComponent.stageRange(withinRange);
        movementComponent.stagePath(withinPath);

        // try executing action only if specified
        // - Target is not null
        // - Target is within range
        // - We are not in preview mode
        // - We are targeting the current tile were one
        if (target == movementComponent.getCurrentTile()) { return false; }
        if (target == null || commit || !withinRange.contains(target)) { return false; }

        movementComponent.commit();

        // do the animation for the tile
        setAnimationTrack(model, unitEntity, withinPath);
        DirectionComponent directionComponent = unitEntity.get(DirectionComponent.class);
        directionComponent.setDirection(getDirection(movementComponent.getCurrentTile(), target));
//
//        History history = unitEntity.get(History.class);
//        history.log("Moved to " + destination);
//        history = destination.get(History.class);
//        history.log("Traversed by " + unitEntity);

        return true;
    }

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

    private void setAnimationTrack(GameModel model, Entity unitEntity, Deque<Entity> pathing) {
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        if (movementComponent.shouldUseTrack()) {
            model.getSystems().getTrackSystem().executeMoveAnimation(model, unitEntity, pathing);
        }
        Entity tileEntity = pathing.getLast();
        Tile tile = tileEntity.get(Tile.class);
        tile.setUnit(unitEntity);
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

    public boolean forceMove(GameModel model, Entity unit, Entity toMoveTo) {
        MovementComponent movementComponent = unit.get(MovementComponent.class);
        boolean hasMoved = movementComponent.mHasMoved;
        boolean moved = move(model, unit, toMoveTo, -1, -1, true);
        return moved;
    }

    public int getSpeed(GameModel model, int speed1, int speed2) {
        int spriteWidth = model.getSettings().getSpriteWidth();
        int spriteHeight = model.getSettings().getSpriteHeight();
        float spriteSize = (float) (spriteWidth + spriteHeight) / 2;
        return (int) (spriteSize * RandomUtils.getRandomNumberBetween(speed1, speed2));
    }
}
