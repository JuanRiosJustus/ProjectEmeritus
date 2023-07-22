package game.entity;

import game.components.Component;
import game.components.NameTag;
import game.components.Statistics;
import game.components.Tile;

import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes.Name;


public class Entity {

    protected Map<Class<? extends Component>, Object> mapping = new HashMap<>();

    public static Entity newBuilder() {
        return new Entity();
    }

    public <T> void remove(Class<T> component) {
        mapping.remove(component);
    }

    public Entity add(Component component) {
        component.setOwner(this);
        mapping.put(component.getClass(), component);
        return this;
    }

    public <T> T get(Class<T> component) { return component.cast(mapping.get(component)); }

    public String toString() {
        Tile tile = get(Tile.class);
        NameTag nameTag = get(NameTag.class);
        if (tile != null) {
            return tile.toString();
        } else if (nameTag != null) {
            return nameTag.toString();
            // return "[" + summary.toString() + " (" +  System.identityHashCode(this) + ")]";
        } else {
            return "Entity...";
        }
    }
}
