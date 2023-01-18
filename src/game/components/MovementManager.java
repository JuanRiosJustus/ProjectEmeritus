package game.components;

import game.entity.Entity;

import java.util.Deque;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MovementManager extends Component {

    public boolean moved = false;
    public Entity tile = null;
    public Entity lastPathed = null;

    public final Deque<Entity> tilesWithinMovementPath = new ConcurrentLinkedDeque<>();
    public final Set<Entity> tilesWithinMovementRange = ConcurrentHashMap.newKeySet();

    public void reset() {
        moved = false;
    }
}
