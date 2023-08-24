package main.game.components;

import main.constants.Constants;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.PathBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Movement extends Component {

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

    public static Movement project(GameModel model, Entity start, int move, int climb, Entity toMoveTo) {
        if (start == null) { return null; }
        Movement result = new Movement();
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
        Movement movement = unit.get(Movement.class);
        if (movement.moved || (movement.shouldNotUpdate(model, toMoveTo) && !execute)) { return false; }

        // Get the ranges of the movement
        Summary summary = unit.get(Summary.class);
        int move = summary.getStatTotal(Constants.MOVE);
        int climb = summary.getStatTotal(Constants.CLIMB);
        Movement projection = project(model, movement.currentTile, move, climb, toMoveTo);
        movement.setRange(projection.range);
        movement.setPath(projection.path);

        // move unit if tile selected and is within movement range and path
        if (!execute) { return false; }
        if (toMoveTo == null || toMoveTo == movement.currentTile) { return false; }
        if (!movement.path.contains(toMoveTo)) { return false; }
        if (!movement.range.contains(toMoveTo)) { return false; }

        // try committing movement track
        movement.move(model, toMoveTo);
        movement.moved = true;
        return true;
    }

    public static void undo(GameModel model, Entity unit) {
        Movement movement = unit.get(Movement.class);
        movement.moved = false;

        Entity previous = movement.previousTile;

        movement.useTrack = false;
        Movement.move(model, unit, previous, true);
        movement.useTrack = true;
        movement.previousTile = null;

        // handle waiting tile selection state
        movement.moved = false;
    }
}
