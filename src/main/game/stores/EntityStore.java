package main.game.stores;

import main.game.components.*;
import main.game.components.ActionsComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.TileComponent;
import main.game.entity.Entity;
import main.utils.RandomUtils;
import org.json.JSONArray;
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


    public String getOrCreateTile(int row, int column, int elevation) {
        String id = row + "_" + column + "_" + UUID.randomUUID();
        Entity newEntity = createBaseEntity(id, id, TILE_ENTITY);

        newEntity.add(new AssetComponent());
        newEntity.add(new TileComponent(row, column, elevation));
        newEntity.add(new Overlay());
        newEntity.add(new History());

        return id;
    }


    public String getOrCreateUnit(boolean isAI) {
        List<String> units = new ArrayList<>(UnitTable.getInstance().getAllUnits());
        Collections.shuffle(units);
        String randomUnit = units.get(0);
        String nickname = RandomUtils.createRandomName(3, 6);
        return getOrCreateUnit(randomUnit, nickname, isAI);
    }
    public String getOrCreateUnit(String unit, String nickname, boolean isAI) {

        String id = unit + "_" + nickname + "_" + UUID.randomUUID();
        Entity newEntity = createBaseEntity(id, nickname, UNIT_ENTITY);

        newEntity.add(new ActionsComponent());
        newEntity.add(new AIComponent(isAI));
        newEntity.add(new AbilityComponent());
        newEntity.add(new MovementComponent());
        newEntity.add(new AnimationComponent());
        newEntity.add(new Overlay());
        newEntity.add(new TagComponent());
        newEntity.add(new InventoryComponent());
        newEntity.add(new History());
        newEntity.add(new DirectionComponent());
        newEntity.add(new AssetComponent());
        newEntity.add(new TimerComponent());

        unit = unit.toLowerCase();

        JSONObject attributes = UnitTable.getInstance().getAttributes(unit);
        JSONArray type = UnitTable.getInstance().getType(unit);

        String basicAbility = UnitTable.getInstance().getBasicAbility(unit);
        String passiveAbility = UnitTable.getInstance().getPassiveAbility(unit);
        JSONArray otherAbility = UnitTable.getInstance().getOtherAbility(unit);


        StatisticsComponent statisticsComponent = new StatisticsComponent();
        statisticsComponent.putAttributes(attributes);
        statisticsComponent.putType(type);

        statisticsComponent.putBasicAbility(basicAbility);
        statisticsComponent.putPassiveAbility(passiveAbility);
        statisticsComponent.putOtherAbility(otherAbility);

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


    public JSONObject getUnitSaveData(String id) {
        Entity unitEntity = get(id);
        if (unitEntity == null) { return null; }
        IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);

        JSONObject data = new JSONObject();
        data.put("id", identityComponent.getID());
        data.put("nickname", identityComponent.getNickname());
        data.put("level", statisticsComponent.getLevel());
        data.put("experience", statisticsComponent.getCurrentExperience());

        return data;
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
