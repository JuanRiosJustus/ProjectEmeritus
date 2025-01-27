package main.game.stores.factories;

import main.game.components.*;
import main.game.components.behaviors.Behavior;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.stores.JsonObjectDatabase;
import main.game.stores.JsonObjectTable;
import main.game.stores.pools.UnitDatabase;
import main.utils.RandomUtils;
import org.json.JSONObject;

import java.util.*;

public class EntityFactory {
    private final Map<String, Entity> mEntityMap = new HashMap<>();

    private static EntityFactory mInstance = null;
    private EntityFactory() { }
    public static EntityFactory getInstance() {
        if (mInstance == null) {
            mInstance = new EntityFactory();
        } return mInstance;
    }

    public Entity get(String id) { return mEntityMap.get(id); }

    public Entity createBaseEntity(String nickname) { return createBaseEntity(UUID.randomUUID().toString(), nickname); }
    public Entity createBaseEntity(String id, String nickname) {
        Entity entity = new Entity();
        entity.add(new IdentityComponent(id, nickname));
        mEntityMap.put(id, entity);
        return entity;
    }

    public String createTile(int row, int column) { return createTile(UUID.randomUUID().toString(), new Tile(row, column)); }
    public String createTile(JSONObject jsonObject) { return createTile(UUID.randomUUID().toString(), jsonObject); }
    public String createTile(String id, JSONObject tileObject) {

        if (mEntityMap.containsKey(id)) { return id; }

        Entity newEntity = createBaseEntity(id);

        newEntity.add(new AssetComponent());
        newEntity.add(new Tile(tileObject));
        newEntity.add(new Overlay());
        newEntity.add(new History());

        mEntityMap.put(id, newEntity);

        return id;
    }


    public String createUnit(boolean controlled) {
        List<String> units = new ArrayList<>(UnitDatabase.getInstance().getAllPossibleUnits());
        Collections.shuffle(units);
        String randomUnit = units.get(0);
        String nickname = RandomUtils.createRandomName(3, 6);
        return createUnit(UUID.randomUUID().toString(), randomUnit, nickname, controlled);
    }
    public String createUnit(String id, String unit, String nickname, boolean control) {

        if (mEntityMap.containsKey(id)) { return id; }

        Entity newEntity = createBaseEntity(id, nickname);

        newEntity.add(new Behavior(control));
        newEntity.add(new ActionComponent());
        newEntity.add(new MovementComponent());
        newEntity.add(new AnimationComponent());
        newEntity.add(new Overlay());
        newEntity.add(new TagComponent());
        newEntity.add(new InventoryComponent());
        newEntity.add(new History());
        newEntity.add(new DirectionComponent());
        newEntity.add(new AssetComponent());


        JsonObjectTable unitsTable = JsonObjectDatabase.getInstance().get("units");
        Map<String, Float> attributes = unitsTable.getMapAsFloats(unit, new String[]{ "attributes" });
        List<String> abilities = unitsTable.getListAsStrings(unit, new String[]{ "abilities"});
        List<String> type = unitsTable.getListAsStrings(unit, new String[]{ "type" });

        StatisticsComponent statisticsComponent = new StatisticsComponent(attributes);
        statisticsComponent.putType(type);
        statisticsComponent.putAbilities(abilities);
        statisticsComponent.putUnit(unit);

        newEntity.add(statisticsComponent);

        return id;
    }


    public String createStructure(String name) {
        return createStructure(UUID.randomUUID().toString(), name);
    }
    public String createStructure(String id, String name) {

        if (mEntityMap.containsKey(id)) { return id; }

        Entity newEntity = createBaseEntity(id, name);

        newEntity.add(new StatisticsComponent());
        newEntity.add(new AssetComponent());

        return id;
    }
}
