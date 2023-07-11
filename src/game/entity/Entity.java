package game.entity;

import game.components.Component;
import game.components.Tile;
import game.components.statistics.Summary;

import java.util.HashMap;
import java.util.Map;


public class Entity {

    protected Map<Class<? extends Component>, Object> mapping = new HashMap<>();

    public <T> void remove(Class<T> component) {
        mapping.remove(component);
    }

    public void add(Component component) {
        component.setOwner(this);
        mapping.put(component.getClass(), component);
    }

    public <T> T get(Class<T> component) { return component.cast(mapping.get(component)); }

    public String toString() {
        Tile tile = get(Tile.class);
        Summary summary = get(Summary.class);
        if (tile != null) {
            return tile.toString();
        } else if (summary != null) {
            return summary.toString();
            // return "[" + summary.toString() + " (" +  System.identityHashCode(this) + ")]";
        } else {
            return "Entity...";
        }
    }
}
