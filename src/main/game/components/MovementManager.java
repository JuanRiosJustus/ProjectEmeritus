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
        Track track = owner.get(Track.class);
        previousTile = currentTile;
        if (useTrack) {
            track.move(model, owner, toMoveTo);
        } else {
            track.set(model, owner, toMoveTo);
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
        return isSameTarget && owner.get(UserBehavior.class) != null;
    }

    public static boolean move(GameModel model, Entity unit, Entity toMoveTo, boolean execute) {
        MovementManager movementManager = unit.get(MovementManager.class);
        if (movementManager.moved || (movementManager.shouldNotUpdate(model, toMoveTo) && !execute)) { return false; }

        // Get the ranges of the movement
        Summary summary = unit.get(Summary.class);
        int move = summary.getStatTotal(Constants.MOVE);
        int climb = summary.getStatTotal(Constants.CLIMB);
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
        return true;
    }

    public static boolean forceMove(GameModel model, Entity unit, Entity toMoveTo) {
        MovementManager movementManager = unit.get(MovementManager.class);

        // Get the ranges of the movement
        Summary summary = unit.get(Summary.class);
        int move = summary.getStatTotal(Constants.MOVE);
        int climb = summary.getStatTotal(Constants.CLIMB);
        MovementManager projection = project(model, movementManager.currentTile, move, climb, toMoveTo);
        movementManager.setRange(projection.range);
        movementManager.setPath(projection.path);

        // move unit if tile selected and is within movement range and path
        if (toMoveTo == null || toMoveTo == movementManager.currentTile) { return false; }
        if (!movementManager.path.contains(toMoveTo)) { return false; }
        if (!movementManager.range.contains(toMoveTo)) { return false; }

        // try committing movement track
        movementManager.move(model, toMoveTo);
        return true;
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
