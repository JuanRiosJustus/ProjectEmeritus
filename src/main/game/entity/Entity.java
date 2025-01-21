package main.game.entity;

import main.game.components.Component;
import main.game.components.IdentityComponent;
import main.game.components.tile.Tile;


import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class Entity implements Serializable {

    protected Map<Class<? extends Component>, Component> mComponents = new LinkedHashMap<>();

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
        IdentityComponent identityComponent = get(IdentityComponent.class);
        if (tile != null) {
            return tile.getBasicIdentityString();
        } else if (identityComponent != null) {
            return identityComponent.toString();
        } else {
            return "Entity...";
        }
    }
}
