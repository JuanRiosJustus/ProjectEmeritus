package main.game.stores;

import main.constants.EmeritusDatabase;
import main.game.components.*;
import main.game.components.ActionsComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.StructureComponent;
import main.game.components.tile.TileComponent;
import main.game.entity.Entity;
import main.graphics.AnimationPool;
import main.utils.RandomUtils;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.*;

public class EntityStore {
    private static final String TILE_ENTITY = "tile";
    private static final String UNIT_ENTITY = "unit";
    private static final String STRUCTURE_ENTITY = "structure";
    private static final String EQUIPMENT_ENTITY = "equipment";
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


    public String createTile(int row, int column) {
        String id = createUUID("ROW", row, "COLUMN", column);
        Entity newEntity = createBaseEntity(id, id, TILE_ENTITY);

        newEntity.add(new AssetComponent());
        newEntity.add(new TileComponent(row, column));
        newEntity.add(new Overlay());
        newEntity.add(new PositionComponent());
        newEntity.add(new History());

        return id;
    }

    public String createEquipment(String name) {
        String id = createUUID("NAME", name);
        Entity newEntity = createBaseEntity(id, name, EQUIPMENT_ENTITY);

        StatisticsComponent statisticsComponent = new StatisticsComponent();

        JSONArray data = EmeritusDatabase.getInstance().getEquipment(name);
        JSONObject equipment = data.getJSONObject(0);
        JSONArray equipmentStats = equipment.getJSONArray("statistics");

        statisticsComponent.putStatistics(equipmentStats);

        newEntity.add(statisticsComponent);

        return id;
    }


    public String createUnit(boolean isAI) {
        List<String> units = new ArrayList<>(UnitTable.getInstance().getAllUnits());
        Collections.shuffle(units);
        String randomUnit = units.getFirst();
        String nickname = RandomUtils.createRandomName(3, 6);
        return createUnit(randomUnit, nickname, isAI);
    }
    public String createUnit(String unit, String nickname, boolean isAI) {
        String id = createUUID("UNIT", unit, "NICKNAME", nickname);
        Entity newEntity = createBaseEntity(id, nickname, UNIT_ENTITY);

        newEntity.add(new ActionsComponent());
        newEntity.add(new AIComponent(isAI));
        newEntity.add(new AbilityComponent());
        newEntity.add(new MovementComponent());
        newEntity.add(new PositionComponent());
        newEntity.add(new AnimationComponent());
        newEntity.add(new Overlay());
        newEntity.add(new TagComponent());
        newEntity.add(new EquipmentComponent());
        newEntity.add(new History());
        newEntity.add(new DirectionComponent());
        newEntity.add(new AssetComponent());
        newEntity.add(new TimerComponent());

        unit = unit.toLowerCase();

        JSONObject statistics = UnitTable.getInstance().getStatistics(unit);
        JSONArray type = UnitTable.getInstance().getType(unit);

        String basicAbility = UnitTable.getInstance().getBasicAbility(unit);
        String passiveAbility = UnitTable.getInstance().getPassiveAbility(unit);
        JSONArray otherAbility = UnitTable.getInstance().getOtherAbility(unit);


        StatisticsComponent statisticsComponent = new StatisticsComponent();
        statisticsComponent.putAttributes(statistics);
        statisticsComponent.putType(type);

        statisticsComponent.putBasicAbility(basicAbility);
        statisticsComponent.putPassiveAbility(passiveAbility);
        statisticsComponent.putOtherAbility(otherAbility);

        statisticsComponent.putUnit(unit);


        newEntity.add(statisticsComponent);

        return id;
    }


    public String createStructure() { return createStructure(null); }
    public String createStructure(String structure) {
        if (structure == null || structure.isEmpty()) {
            List<String> structures = AnimationPool.getInstance().getStructureTileSets();
            structure = structures.get(new Random().nextInt(structures.size()));
        }

        structure = structure.substring(structure.lastIndexOf("/") + 1, structure.lastIndexOf("."));
        String id = createUUID("STRUCTURE", structure);
        Entity newEntity = createBaseEntity(id, structure, STRUCTURE_ENTITY);

        newEntity.add(new StatisticsComponent());
        newEntity.add(new AssetComponent());
        newEntity.add(new StructureComponent(structure));
        newEntity.add(new PositionComponent());

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

    private static String createUUID(Object... args) {
        StringBuilder sb = new StringBuilder();
        if (args.length > 0 && args.length % 2 == 0) {
            for (int i = 0; i < args.length; i += 2) {
                Object key = String.valueOf(args[i]);
                Object value = String.valueOf(args[i + 1]);
                sb.append(key).append("_").append(value).append("___");
            }
        }
        return sb + "UUID_" + UUID.randomUUID();
    }
}
