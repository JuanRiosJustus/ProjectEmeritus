package main.game.stores;

import main.game.components.*;
import main.game.components.ActionsComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.StructureComponent;
import main.game.components.tile.TileComponent;
import main.game.entity.Entity;
import main.utils.RandomUtils;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

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




        // Add any passive effects from the abilities to the units
//        for (int index = 0; index < otherAbility.size(); index++) {
//            String ability = otherAbility.getString(index);
//            JSONArray attributeModifiers = AbilityTable.getInstance().getAttributes(ability);
//            if (!attributeModifiers.isEmpty()) {
//                for (int i = 0; i < attributeModifiers.size(); i++) {
//                    JSONObject attributeModifier = attributeModifiers.getJSONObject(i);
//                    String modifier = AbilityTable.getInstance().getScalingType(attributeModifier);
//                    String attribute = AbilityTable.getInstance().getScalingAttribute(attributeModifier);
//                    float magnitude = AbilityTable.getInstance().getScalingMagnitude(attributeModifier);
//                }
//                System.out.println("toto");
//            }
//        }

//        JSONArray attributeModifiers = AbilityTable.getInstance().getAttributes(passiveAbility);
//        if (!attributeModifiers.isEmpty()) {
//            for (int i = 0; i < attributeModifiers.size(); i++) {
//                JSONObject attributeModifier = attributeModifiers.getJSONObject(i);
//                String scalingType = AbilityTable.getInstance().getScalingType(attributeModifier);
//                String scalingAttribute = AbilityTable.getInstance().getScalingAttribute(attributeModifier);
//                float scalingMagnitude = AbilityTable.getInstance().getScalingMagnitude(attributeModifier);
//                boolean isBaseScaling = AbilityTable.getInstance().isBaseScaling(attributeModifier);
//
//
//                float baseModifiedTotalMissingCurrent = statisticsComponent.getScaling(scalingAttribute, scalingType);
//                float value = baseModifiedTotalMissingCurrent * scalingMagnitude;
//                if (isBaseScaling) {
//                    value = scalingMagnitude;
//                }
//
//                statisticsComponent.putAdditiveModification(scalingAttribute, passiveAbility, value, 2);
//            }
//
//            statisticsComponent.addTag(passiveAbility);
//            System.out.println("toto");
//        }





        newEntity.add(statisticsComponent);

        return id;
    }


    public String getOrCreateStructure(String structure) {
        return getOrCreateStructure(null, structure);
    }
    public String getOrCreateStructure(String id, String structure) {

        if (mEntityMap.containsKey(id)) { return id; }
        if (id == null) { id = UUID.randomUUID().toString(); }
        if (structure == null) { structure = id; }

        Entity newEntity = createBaseEntity(id, structure, STRUCTURE_ENTITY);

        newEntity.add(new StatisticsComponent());
        newEntity.add(new AssetComponent());
        newEntity.add(new StructureComponent(structure));

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
