package main.game.entity;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.game.components.Component;
import main.game.components.Identity;
import main.game.components.Summary;
import main.game.components.tile.Tile;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class Entity implements Serializable {

    protected Map<Class<? extends Component>, Object> mComponents = new HashMap<>();

    public <T> void remove(Class<T> component) {
        mComponents.remove(component);
    }

    public Entity add(Component component) {
        component.setOwner(this);
        mComponents.put(component.getClass(), component);
        return this;
    }

    public <T> T get(Class<T> component) { return component.cast(mComponents.get(component)); }

    public String toString() {
        Tile tile = get(Tile.class);
        Identity identity = get(Identity.class);
        if (tile != null) {
            return tile.toString();
        } else if (identity != null) {
            return identity.toString();
        } else {
            return "Entity...";
        }
    }

//    public String toJson() {
//        JsonObject object = new JsonObject();
//        Summary summary = get(Summary.class);
//        object.put("unit", summary.getName());
//        Identity identity = get(Identity.class);
//        object.put("nickname", identity.toString());
//        return object.toJson();
//    }
}
