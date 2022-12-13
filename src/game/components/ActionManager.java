package game.components;

import game.entity.Entity;

import java.util.Deque;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ActionManager extends Component {

    public Entity tileOccupying = null;
    public Entity tileToMoveTo = null;
    public Entity tileToAttackAt = null;

    public boolean attacked = false;
    public boolean moved = false;

    public final Deque<Entity> tilesWithinMovementRangePath = new ConcurrentLinkedDeque<>();
    public final Set<Entity> tilesWithinMovementRange = ConcurrentHashMap.newKeySet();
    public final Set<Entity> tilesWithinAbilityRange = ConcurrentHashMap.newKeySet();
    public final Set<Entity> tilesWithinAreaOfEffect = ConcurrentHashMap.newKeySet();

    public void reset() {
        attacked = false;
        moved = false;
    }
}
