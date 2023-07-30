package main.game.components;

import main.game.entity.Entity;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ActionManager extends Component {

    public Entity targeting = null;
    public boolean acted = false;

    public final Set<Entity> withinRange = ConcurrentHashMap.newKeySet();
    public final Set<Entity> areaOfEffect = ConcurrentHashMap.newKeySet();
    public final Set<Entity> lineOfSight = ConcurrentHashMap.newKeySet();

    public void addAreaOfEffect(Set<Entity> set) {
        areaOfEffect.clear();
        areaOfEffect.addAll(set);
    }

    public void addLineOfSight(Set<Entity> set) {
        lineOfSight.clear();
        lineOfSight.addAll(set);
    }

    public void addWithinRange(Set<Entity> set) {
        withinRange.clear();
        withinRange.addAll(set);
    }

    public void reset() {
        acted = false;
    }
}
