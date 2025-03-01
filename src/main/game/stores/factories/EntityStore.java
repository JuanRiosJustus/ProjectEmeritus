package main.game.stores.factories;

import main.game.components.*;
import main.game.components.behaviors.Behavior;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.stores.JsonDatabase;
import main.game.stores.JsonTable;
import main.game.stores.pools.UnitDatabase;
import main.utils.RandomUtils;
import org.json.JSONObject;

import java.util.*;

public class EntityStore {
    private static final String TILE_ENTITY = "tile";
    private static final String UNIT_ENTITY = "unit";
    private static final String STRUCTURE_ENTITY = "structure";
    private final Map<String, Entity> mEntityMap = new HashMap<>();

    private static EntityStore mInstance = null;

    private EntityStore() { }

    public static EntityStore getInstance() {
        if (mInstance == null) {
            mInstance = new EntityStore();
        } return mInstance;
    }

    public Entity get(String id) { return mEntityMap.get(id); }

    public Entity createBaseEntity(String id, String nickname, String type) {
        Entity entity = new Entity();
        entity.add(new IdentityComponent(id, nickname, type));
        mEntityMap.put(id, entity);
        return entity;
    }


    public String getOrCreateTile(int row, int column) { return getOrCreateTile(new Tile(row, column)); }
    public String getOrCreateTile(JSONObject jsonObject) { return getOrCreateTile(null, null, jsonObject); }
    public String getOrCreateTile(String id, String nickname, JSONObject tileObject) {

        if (mEntityMap.containsKey(nickname)) { return nickname; }
        if (id == null) { id = UUID.randomUUID().toString(); }
        if (nickname == null) { nickname = id; }


        Entity newEntity = createBaseEntity(id, nickname, TILE_ENTITY);

        newEntity.add(new AssetComponent());
        newEntity.add(new Tile(tileObject));
        newEntity.add(new Overlay());
        newEntity.add(new History());

        mEntityMap.put(nickname, newEntity);

        return nickname;
    }


    public String getOrCreateUnit(boolean controlled) {
        List<String> units = new ArrayList<>(UnitDatabase.getInstance().getAllPossibleUnits());
        Collections.shuffle(units);
        String randomUnit = units.get(0);
        String nickname = RandomUtils.createRandomName(3, 6);
        return getOrCreateUnit(UUID.randomUUID().toString(), randomUnit, nickname, controlled);
    }
    public String getOrCreateUnit(String id, String unit, String nickname, boolean control) {

        if (mEntityMap.containsKey(id)) { return id; }
        if (id == null) { id = UUID.randomUUID().toString(); }
        if (nickname == null) { nickname = id; }

        Entity newEntity = createBaseEntity(id, nickname, UNIT_ENTITY);

        newEntity.add(new Behavior(control));
        newEntity.add(new AbilityComponent());
        newEntity.add(new MovementComponent());
        newEntity.add(new AnimationComponent());
        newEntity.add(new Overlay());
        newEntity.add(new TagComponent());
        newEntity.add(new InventoryComponent());
        newEntity.add(new History());
        newEntity.add(new DirectionComponent());
        newEntity.add(new AssetComponent());


        JsonTable unitsTable = JsonDatabase.getInstance().get("units");

        Map<String, Float> attributes = unitsTable.getFloatMap(unit, "attributes");
        List<String> abilities = unitsTable.getStringList(unit, "abilities");
        List<String> type = unitsTable.getStringList(unit, "type");

        StatisticsComponent statisticsComponent = new StatisticsComponent(attributes);
        statisticsComponent.putType(type);
        statisticsComponent.putAbilities(abilities);
        statisticsComponent.putUnit(unit);

        newEntity.add(statisticsComponent);

        return id;
    }


    public String getOrCreateStructure(String nickname) {
        return getOrCreateStructure(null, nickname);
    }
    public String getOrCreateStructure(String id, String nickname) {

        if (mEntityMap.containsKey(id)) { return id; }
        if (id == null) { id = UUID.randomUUID().toString(); }
        if (nickname == null) { nickname = id; }

        Entity newEntity = createBaseEntity(id, nickname, STRUCTURE_ENTITY);

        newEntity.add(new StatisticsComponent());
        newEntity.add(new AssetComponent());

        return id;
    }

    public boolean isStructureEntity(String id) {
        Entity entity = mEntityMap.get(id);

        boolean result = false;
        if (entity != null) {
            IdentityComponent identityComponent = entity.get(IdentityComponent.class);
            result = identityComponent.getType().equalsIgnoreCase(STRUCTURE_ENTITY);
        }

        return result;
    }

    public boolean isUnitEntity(String id) {
        Entity entity = mEntityMap.get(id);

        boolean result = false;
        if (entity != null) {
            IdentityComponent identityComponent = entity.get(IdentityComponent.class);
            result = identityComponent.getType().equalsIgnoreCase(UNIT_ENTITY);
        }

        return result;
    }

    public boolean isTileEntity(String id) {
        Entity entity = mEntityMap.get(id);

        boolean result = false;
        if (entity != null) {
            IdentityComponent identityComponent = entity.get(IdentityComponent.class);
            result = identityComponent.getType().equalsIgnoreCase(TILE_ENTITY);
        }

        return result;
    }
}
