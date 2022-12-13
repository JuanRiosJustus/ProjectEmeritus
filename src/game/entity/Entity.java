package game.entity;

import game.components.Component;
import game.components.Name;
import game.components.Tile;

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
        Name tag = get(Name.class);
        if (tile != null) {
            return tile.toString();
        } else if (tag != null) {
            return "[" + tag.value + " (" +  System.identityHashCode(this) + ")]";
        } else {
            return "Entity...";
        }
    }
}
