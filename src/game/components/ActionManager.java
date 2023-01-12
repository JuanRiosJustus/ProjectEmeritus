package game.components;

import game.entity.Entity;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ActionManager extends Component {
    public Entity targeting = null;
    public boolean acted = false;

    public final Set<Entity> tilesWithinActionRange = ConcurrentHashMap.newKeySet();
    public final Set<Entity> tilesWithinActionAOE = ConcurrentHashMap.newKeySet();
    public final Set<Entity> tilesWithinActionLOS = ConcurrentHashMap.newKeySet();

    public void reset() {
        acted = false;
    }
}
