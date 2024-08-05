package main.game.components;

import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;


public class MovementManager extends Component {

    public boolean moved = false;
    public Entity currentTile = null;
    public Entity previousTile = null;
    public boolean useTrack = true;
    public final Deque<Entity> tilesInPath = new ConcurrentLinkedDeque<>();
    public final Set<Entity> tilesInRange = ConcurrentHashMap.newKeySet();
    public void setMoved(boolean hasMoved) { moved = hasMoved; }
    public boolean shouldUseTrack() {
        return useTrack;
    }
    public void setCurrentTile(Entity tile) {
        currentTile = tile;
    }
    public void setPreviousTile(Entity tile) {
        previousTile = tile;
    }

    public void setPath(List<Entity> deque) {
        tilesInPath.clear();
        tilesInPath.addAll(deque);
    }

    public void setRange(Set<Entity> deque) {
        tilesInRange.clear();
        tilesInRange.addAll(deque);
    }

    public void reset() {
        tilesInPath.clear();
        tilesInRange.clear();
        previouslyTargeting = null;
        moved = false;
    }

    private Entity previouslyTargeting = null;
    public boolean shouldNotUpdate(GameModel model, Entity targeting) {
        boolean isSameTarget = previouslyTargeting == targeting;
        if (!isSameTarget) {
//            System.out.println("Waiting for user movement input... " + previouslyTargeting + " vs " + targeting);
        }
        previouslyTargeting = targeting;
        return isSameTarget && mOwner.get(UserBehavior.class) != null;
    }
    public Entity getCurrentTile() {
        return currentTile;
    }
    public boolean hasMoved() { return moved; }
}
