package game.components;

import game.entity.Entity;
import game.main.GameModel;

import java.util.Deque;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MovementManager extends Component {

    public boolean moved = false;
    public Entity currentTile = null;
    public Entity previousTile = null;

    public boolean useTrack = true;
    public final Deque<Entity> tilesWithinMovementPath = new ConcurrentLinkedDeque<>();
    public final Set<Entity> tilesWithinMovementRange = ConcurrentHashMap.newKeySet();

    public void reset() {
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
