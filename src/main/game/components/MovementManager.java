package main.game.components;

import main.constants.Constants;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.PathBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MovementManager extends Component {

    public boolean moved = false;
    public Entity currentTile = null;
    public Entity previousTile = null;
    public boolean useTrack = true;
    public final Deque<Entity> path = new ConcurrentLinkedDeque<>();
    public final Set<Entity> range = ConcurrentHashMap.newKeySet();

    private void setPath(Deque<Entity> deque) {
        path.clear();
        path.addAll(deque);
    }
 
    private void setRange(Set<Entity> deque) {
        range.clear();
        range.addAll(deque);
    }

    private void move(GameModel model, Entity toMoveTo) {
        AnimationMovementTrack track = mOwner.get(AnimationMovementTrack.class);
        previousTile = currentTile;
        if (useTrack) {
            track.move(model, mOwner, toMoveTo);
        } else {
            track.set(model, mOwner, toMoveTo);
        }
        moved = true;
    }

    public void reset() {
        path.clear();
        range.clear();
        previouslyTargeting = null;
        moved = false;
    }

    public static MovementManager project(GameModel model, Entity start, int move, int climb, Entity toMoveTo) {
        if (start == null) { return null; }
        MovementManager result = new MovementManager();
        result.setRange(PathBuilder.newBuilder()
                .setModel(model)
                .setRange(move)
                .setClimb(climb)
                .setStart(start)
                .getTilesInMovementRange());

        if (toMoveTo == null) { return result; }
        result.setPath(PathBuilder.newBuilder()
                .setModel(model)
                .setRange(move)
                .setClimb(climb)
                .setStart(start)
                .setEnd(toMoveTo)
                .getTilesInMovementPath());

        return result;
    }

    private Entity previouslyTargeting = null;
    private boolean shouldNotUpdate(GameModel model, Entity targeting) {
        boolean isSameTarget = previouslyTargeting == targeting;
        if (!isSameTarget) {
//            System.out.println("Waiting for user movement input... " + previouslyTargeting + " vs " + targeting);
        }
        previouslyTargeting = targeting;
        return isSameTarget && mOwner.get(UserBehavior.class) != null;
    }

//    public static boolean move(GameModel model, Entity unit, Entity toMoveTo, boolean execute) {
//        MovementManager movementManager = unit.get(MovementManager.class);
//        if (movementManager.moved || (movementManager.shouldNotUpdate(model, toMoveTo) && !execute)) { return false; }
//
//        // Get the ranges of the movement
//        Statistics statistics = unit.get(Statistics.class);
//        int move = statistics.getStatTotal(Constants.MOVE);
//        int climb = statistics.getStatTotal(Constants.CLIMB);
//        MovementManager projection = project(model, movementManager.currentTile, move, climb, toMoveTo);
//        movementManager.setRange(projection.range);
//        movementManager.setPath(projection.path);
//
//        // move unit if tile selected and is within movement range and path
//        if (!execute) { return false; }
//        if (toMoveTo == null || toMoveTo == movementManager.currentTile) { return false; }
//        if (!movementManager.path.contains(toMoveTo)) { return false; }
//        if (!movementManager.range.contains(toMoveTo)) { return false; }
//
//        // try committing movement track
//        movementManager.move(model, toMoveTo);
//        movementManager.moved = true;
//        return true;
//    }


    public static boolean move(GameModel model, Entity unit, Entity toMoveTo, boolean execute) {
        // Get the ranges of the movement
        Statistics statistics = unit.get(Statistics.class);
        int move = statistics.getStatTotal(Constants.MOVE);
        int climb = statistics.getStatTotal(Constants.CLIMB);

        return move(model, unit, toMoveTo, move, climb, execute);
    }
    private static boolean move(GameModel model, Entity unit, Entity toMoveTo, int move, int climb, boolean execute) {
        MovementManager movementManager = unit.get(MovementManager.class);
        if (movementManager.moved || (movementManager.shouldNotUpdate(model, toMoveTo) && !execute)) { return false; }

        // if the unit was not placed, it cannot move.
        if (movementManager.currentTile == null) { return false; }

        // Get the ranges of the movement
        MovementManager projection = project(model, movementManager.currentTile, move, climb, toMoveTo);
        movementManager.setRange(projection.range);
        movementManager.setPath(projection.path);

        // move unit if tile selected and is within movement range and path
        if (!execute) { return false; }
        if (toMoveTo == null || toMoveTo == movementManager.currentTile) { return false; }
        if (!movementManager.path.contains(toMoveTo)) { return false; }
        if (!movementManager.range.contains(toMoveTo)) { return false; }

        // try committing movement track
        movementManager.move(model, toMoveTo);
        movementManager.moved = true;

        History history = unit.get(History.class);
        history.log("Moved to " + toMoveTo);
        history = toMoveTo.get(History.class);
        history.log("Traversed by " + unit);

        return true;
    }


    public static boolean forceMove(GameModel model, Entity unit, Entity toMoveTo) {
        MovementManager movementManager = unit.get(MovementManager.class);
        boolean hasMoved = movementManager.moved;
        boolean moved = move(model, unit, toMoveTo, -1, -1, true);
        movementManager.moved = hasMoved;
        return moved;
    }

    public static void undo(GameModel model, Entity unit) {
        MovementManager movementManager = unit.get(MovementManager.class);
        movementManager.moved = false;

        Entity previous = movementManager.previousTile;

        movementManager.useTrack = false;
        MovementManager.move(model, unit, previous, true);
        movementManager.useTrack = true;
        movementManager.previousTile = null;

        // handle waiting tile selection state
        movementManager.moved = false;
    }
}
