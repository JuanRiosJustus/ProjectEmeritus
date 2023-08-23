package main.game.components;

import main.game.entity.Entity;
import main.game.stores.pools.ability.Ability;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ActionManager extends Component {

    public Entity targeting = null;
    public boolean acted = false;
    public Ability action = null;

    public final Set<Entity> range = ConcurrentHashMap.newKeySet();
    public final Set<Entity> area = ConcurrentHashMap.newKeySet();
    public final Set<Entity> sight = ConcurrentHashMap.newKeySet();

    public void addTilesInAreaOfEffect(Set<Entity> set) {
        area.clear();
        area.addAll(set);
    }

    public void addTilesInLineOfSight(Set<Entity> set) {
        sight.clear();
        sight.addAll(set);
    }

    public void addTilesInRange(Set<Entity> set) {
        range.clear();
        range.addAll(set);
    }

    public void reset() {
        area.clear();
        sight.clear();
        range.clear();
        acted = false;
        action = null;
    }
}
