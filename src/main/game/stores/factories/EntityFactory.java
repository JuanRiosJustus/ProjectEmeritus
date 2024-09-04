package main.game.stores.factories;

import main.game.components.IdentityComponent;
import main.game.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityFactory {
    private static final Map<String, Entity> mEntityMap = new HashMap<>();
    private static final Map<String, Entity> mEntityMap2 = new HashMap<>();
    public static Entity create() { return create(""); }
    public static Entity create(String name) { return create(name, null); }

    public static Entity create(String name, String uuid) {
        Entity entity = new Entity();
        entity.add(new IdentityComponent(name, uuid));

        mEntityMap.put(name, entity);
        mEntityMap2.put(uuid, entity);

        return entity;
    }

    public static List<Entity> getPool() { return new ArrayList<>(mEntityMap.values()); }
}
