package main.game.stores.factories;

import main.game.components.Identity;
import main.game.entity.Entity;

public class EntityFactory {
    public static Entity create() { return create(""); }
    public static Entity create(String name) { return create(name, null); }

    public static Entity create(String name, String uuid) {
        Entity entity = new Entity();
        entity.add(new Identity(name, uuid));
        return entity;
    }
}
