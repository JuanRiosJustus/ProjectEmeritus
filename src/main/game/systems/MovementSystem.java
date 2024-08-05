package main.game.systems;

import main.constants.Constants;
import main.constants.Direction;
import main.game.components.*;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.PathBuilder;

import java.util.LinkedList;
import java.util.Set;

public class MovementSystem extends GameSystem {
    @Override
    public void update(GameModel model, Entity unit) {

    }

    public boolean move(GameModel model, Entity unitEntity, Entity toMoveTo, boolean execute) {
        // Get the ranges of the movement
        Statistics statistics = unitEntity.get(Statistics.class);
        int move = statistics.getStatTotal(Constants.MOVE);
        int climb = statistics.getStatTotal(Constants.CLIMB);

        return move(model, unitEntity, toMoveTo, move, climb, execute);
    }

    private boolean move(GameModel model, Entity unitEntity, Entity destination, int move, int climb, boolean execute) {
        MovementManager movementManager = unitEntity.get(MovementManager.class);
        if (movementManager.hasMoved() || (movementManager.shouldNotUpdate(model, destination) && !execute)) {
            return false;
        }

        // if the unit was not placed, it cannot move.
        if (movementManager.currentTile == null) { return false; }

        // Get the ranges and paths
        Entity start = movementManager.getCurrentTile();
        Set<Entity> withinRange = PathBuilder.newBuilder().inMovementRange(
                model,
                start,
                move,
                climb
        );

        LinkedList<Entity> withinPath = PathBuilder.newBuilder().inMovementPath(
                model,
                start,
                destination,
                move,
                climb
        );

        movementManager.setRange(withinRange);
        movementManager.setPath(withinPath);
        if (destination != null) {
            DirectionalFace directionalFace = unitEntity.get(DirectionalFace.class);
            directionalFace.setPotentialDirection(getDirection(start, destination));
        }

        // move unit if tile selected and is within movement range and path
        if (!execute) { return false; }
        if (destination == null || destination == movementManager.currentTile) { return false; }
        if (!movementManager.tilesInPath.contains(destination)) { return false; }
        if (!movementManager.tilesInRange.contains(destination)) { return false; }

        // do the animation for the tile
        setAnimationTrack(model, unitEntity, withinPath);
        DirectionalFace directionalFace = unitEntity.get(DirectionalFace.class);
        directionalFace.setDirection(getDirection(start, destination));

        History history = unitEntity.get(History.class);
        history.log("Moved to " + destination);
        history = destination.get(History.class);
        history.log("Traversed by " + unitEntity);

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

    private void setAnimationTrack(GameModel model, Entity unit, LinkedList<Entity> tilesInPath) {
        MovementTrack track = unit.get(MovementTrack.class);
        MovementManager manager = unit.get(MovementManager.class);
        manager.setPreviousTile(manager.currentTile);
        if (manager.shouldUseTrack()) {
            track.move2(model, tilesInPath);
        }
        Tile tile = tilesInPath.getLast().get(Tile.class);
        tile.setUnit(unit);

        manager.setMoved(true);
    }

    public void undo(GameModel model, Entity unit) {
        MovementManager movementManager = unit.get(MovementManager.class);
        movementManager.moved = false;

        Entity previous = movementManager.previousTile;

        movementManager.useTrack = false;
        move(model, unit, previous, true);
        movementManager.useTrack = true;
        movementManager.previousTile = null;

        // handle waiting tile selection state
        movementManager.moved = false;
    }

    public boolean forceMove(GameModel model, Entity unit, Entity toMoveTo) {
        MovementManager movementManager = unit.get(MovementManager.class);
        boolean hasMoved = movementManager.moved;
        boolean moved = move(model, unit, toMoveTo, -1, -1, true);
        return moved;
    }


}
