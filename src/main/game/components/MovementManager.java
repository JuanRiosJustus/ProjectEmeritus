package main.game.components;

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
    public final Deque<Entity> path = new ConcurrentLinkedDeque<>();
    public final Set<Entity> range = ConcurrentHashMap.newKeySet();

    public void setPath(Deque<Entity> deque) {
        path.clear();
        path.addAll(deque);
    }
 
    public void setRange(Set<Entity> deque) {
        range.clear();
        range.addAll(deque);
    }

    public void reset() {
        path.clear();
        range.clear();
        moved = false;
    }

    public void move(GameModel model, Entity toMoveTo) {
        MovementTrack track = owner.get(MovementTrack.class);
        previousTile = currentTile;
        if (useTrack) {
            track.move(model, owner, toMoveTo);
        } else {
            track.set(model, owner, toMoveTo);
        }
        moved = true;
    }
}
