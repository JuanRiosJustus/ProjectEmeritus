package main.game.entity;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.game.components.Component;
import main.game.components.Identity;
import main.game.components.Statistics;
import main.game.components.tile.Tile;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class Entity implements Serializable {

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
        Identity identity = get(Identity.class);
        if (tile != null) {
            return tile.toString();
        } else if (identity != null) {
            return identity.toString();
            // return "[" + summary.toString() + " (" +  System.identityHashCode(this) + ")]";
        } else {
            return "Entity...";
        }
    }

    public String toJson() {
        JsonObject object = new JsonObject();
        Statistics statistics = get(Statistics.class);
        object.put("unit", statistics.getSpecies());
        Identity identity = get(Identity.class);
        object.put("nickname", identity.toString());
        return object.toJson();
    }
}
