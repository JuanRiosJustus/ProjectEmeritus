package main.game.systems;

import main.constants.Constants;
import main.constants.Direction;
import main.game.components.*;
import main.game.components.behaviors.Behavior;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.PathBuilder;
import main.game.queue.SpeedQueue;
import main.game.systems.actions.behaviors.AggressiveBehavior;
import main.game.systems.actions.behaviors.RandomnessBehavior;
import main.input.InputController;
import main.input.Mouse;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.LinkedList;
import java.util.Set;

public class MovementSystem extends GameSystem {
    private final ELogger mLogger = ELoggerFactory.getInstance().getELogger(MovementSystem.class);
    private final AggressiveBehavior mAggressiveBehavior = new AggressiveBehavior();
    private final RandomnessBehavior mRandomnessBehavior = new RandomnessBehavior();
    @Override
    public void update(GameModel model, Entity unitEntity) {
        // Only move if its the entities turn
        if (model.getSpeedQueue().peek() != unitEntity) { return; }
        // Only move if not already moved
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        if (movementComponent.hasMoved()) { return; }
        MovementTrackComponent movementTrackComponent = unitEntity.get(MovementTrackComponent.class);
        if (movementTrackComponent.isMoving()) { return; }
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
        boolean moved = move(model, unitEntity, mousedAt, true);
        if (mouse.isPressed() && !movementComponent.hasMoved()) {
            moved = move(model, unitEntity, mousedAt, false);
            mLogger.info("Moving from {} to {}", movementComponent.getCurrentTile(), mousedAt);
            movementComponent.setMoved(moved);
            if (moved) {
                model.getGameState().setControllerToHomeScreen(true);
            }
        }
    }

    private void updateAi(GameModel model, Entity unitEntity) {
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity toMoveTo = mRandomnessBehavior.toMoveTo(model, unitEntity);
        mLogger.info("Moving from {} to {}", movementComponent.getCurrentTile(), toMoveTo);
        boolean moved = move(model, unitEntity, toMoveTo, false);
        movementComponent.setMoved(moved);
    }

    public boolean move(GameModel model, Entity unitEntity, Entity toMoveTo, boolean isPreview) {
        // Get the ranges of the movement
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        int move = statisticsComponent.getStatTotal(Constants.MOVE);
        int climb = statisticsComponent.getStatTotal(Constants.CLIMB);

        return move(model, unitEntity, toMoveTo, move, climb, isPreview);
    }

    private boolean move(GameModel model, Entity unitEntity, Entity target, int move, int climb, boolean isPreview) {
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

        movementComponent.setRange(withinRange);
        movementComponent.setPath(withinPath);

        // try executing action only if specified
        // - Target is not null
        // - Target is within range
        // - We are not in preview mode
        // - We are targeting the current tile were one
        if (target == movementComponent.getCurrentTile()) { return false; }
        if (target == null || isPreview || !withinRange.contains(target)) { return false; }

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

    private void setAnimationTrack(GameModel model, Entity unitEntity, LinkedList<Entity> path) {
        MovementTrackComponent movementTrackComponent = unitEntity.get(MovementTrackComponent.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        movementComponent.setPreviousTile(movementComponent.currentTile);
        if (movementComponent.shouldUseTrack()) {
            movementTrackComponent.move(model, path);
        }
        Entity tileEntity = path.peekLast();
        if (tileEntity == null) { return; }
        Tile tile = tileEntity.get(Tile.class);
        tile.setUnit(unitEntity);

        movementComponent.setMoved(true);
    }

    public void undo(GameModel model, Entity unit) {
        MovementComponent movementComponent = unit.get(MovementComponent.class);
        movementComponent.moved = false;

        Entity previous = movementComponent.previousTile;

        movementComponent.useTrack = false;
        move(model, unit, previous, true);
        movementComponent.useTrack = true;
        movementComponent.previousTile = null;

        // handle waiting tile selection state
        movementComponent.moved = false;
    }

    public boolean forceMove(GameModel model, Entity unit, Entity toMoveTo) {
        MovementComponent movementComponent = unit.get(MovementComponent.class);
        boolean hasMoved = movementComponent.moved;
        boolean moved = move(model, unit, toMoveTo, -1, -1, true);
        return moved;
    }


}
