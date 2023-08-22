package main.game.components;

import main.game.entity.Entity;
import main.game.stores.pools.ability.Ability;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ActionManager extends Component {

    public Entity targeting = null;
    public boolean acted = false;
    public Ability action = null;

    public final Set<Entity> actionRange = ConcurrentHashMap.newKeySet();
    public final Set<Entity> actionAreaOfEffect = ConcurrentHashMap.newKeySet();
    public final Set<Entity> actionLineOfSight = ConcurrentHashMap.newKeySet();

    public void addTilesInAreaOfEffect(Set<Entity> set) {
        actionAreaOfEffect.clear();
        actionAreaOfEffect.addAll(set);
    }

    public void addTilesInLineOfSight(Set<Entity> set) {
        actionLineOfSight.clear();
        actionLineOfSight.addAll(set);
    }

    public void addTilesInRange(Set<Entity> set) {
        actionRange.clear();
        actionRange.addAll(set);
    }

    public void reset() {
        actionAreaOfEffect.clear();
        actionLineOfSight.clear();
        actionRange.clear();
        acted = false;
        action = null;
    }
}
