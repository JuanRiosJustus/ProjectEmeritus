package main.game.stores.pools;

import main.game.components.statistics.StatisticsComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import main.constants.Constants;
import main.game.components.*;
import main.game.components.behaviors.Behavior;
import main.game.entity.Entity;
import main.game.stores.factories.EntityFactory;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.RandomUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class UnitDatabase {

    private static UnitDatabase mInstance = null;
    public static UnitDatabase getInstance() { if (mInstance == null) { mInstance = new UnitDatabase(); } return mInstance; }

//    private final Map<String, Unit> mUnitTemplateMap = new HashMap<>();
    private final Map<String, Entity> mLiveUnitMap = new HashMap<>();

    private final Map<String, JSONObject> mUnitMap = new HashMap<>();
    private final static String RESOURCES_KEY = "resources";
    private final static String ATTRIBUTES_KEY = "attribute";

    private UnitDatabase() {
        ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        try {
            JSONArray units = new JSONArray(Files.readString(Path.of(Constants.UNITS_DATABASE)));
            for (int index = 0; index < units.length(); index++) {
                JSONObject unit = units.getJSONObject(index);
                mUnitMap.put(unit.getString("unit"), unit);
            }
            logger.info("Successfully initialized {}", getClass().getSimpleName());
        } catch (Exception ex) {
            logger.error("Error parsing Unit: " + ex.getMessage());
            System.exit(-1);
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public Collection<String> getAllPossibleUnits() { return mUnitMap.keySet(); }
    public Entity get(String uuid) {
        return mLiveUnitMap.get(uuid);
    }

    public JSONObject save(Entity entity) {
        JSONObject JSONObject = new JSONObject();

        IdentityComponent identityComponent = entity.get(IdentityComponent.class);
        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
        InventoryComponent inventoryComponent = entity.get(InventoryComponent.class);

        JSONObject.put("name", identityComponent.getNickname());
        JSONObject.put("uuid", identityComponent.getID());

        JSONObject.put("species", statisticsComponent.getUnit());
        JSONObject.put("level", statisticsComponent.getLevel());
//        JSONObject.put("experience", statistics.getExperience());

        JSONObject.put("items", new JSONArray());

        return JSONObject;
    }

    public String getRandomUnit(boolean controlled) {
        List<String> unitTemplates = new ArrayList<>(mUnitMap.keySet().stream().toList());
        Collections.shuffle(unitTemplates);
        String randomUnit = unitTemplates.get(0);

        String nickname = RandomUtils.createRandomName(3, 6);
        return create(randomUnit, nickname, UUID.randomUUID().toString(), controlled);
    }


    public String create(String unit, String nickname, boolean control) {
        return create(unit, nickname, UUID.randomUUID().toString(), control);
    }

    public String create(String unit, String nickname, String uuid, boolean control) {

        // The unit with this uuid is already loaded.
        if (mLiveUnitMap.containsKey(uuid)) {
            return uuid;
        }

        Entity entity = EntityFactory.getInstance().createBaseEntity(nickname, uuid);

        entity.add(new Behavior(control));
        entity.add(new ActionComponent());
        entity.add(new MovementComponent());
        entity.add(new AnimationComponent());
        entity.add(new Overlay());
        entity.add(new TagComponent());
        entity.add(new InventoryComponent());
        entity.add(new History());
        entity.add(new DirectionComponent());
        entity.add(new AssetComponent());

//        entity.add(new StatisticsComponent(unit));
        mLiveUnitMap.put(uuid, entity);

        return uuid;
    }


    public List<String> getType(String unit) {
        return mUnitMap.get(unit)
                .getJSONArray("type")
                .toList()
                .stream()
                .map(Object::toString)
                .toList();
    }

    public String getUnitName(String unit) {
        return mUnitMap.get(unit).getString("unit");
    }

    public List<String> getActions(String unit) {
        return mUnitMap.get(unit)
                .getJSONArray("actions")
                .toList()
                .stream()
                .map(Object::toString)
                .toList();
    }

    private static final String STATS_ATTRIBUTES = "stats_attributes";
    public Map<String, Integer> getAttributes(String unit) {
        String specialKey = STATS_ATTRIBUTES;
        Map<String, Integer> result =  mUnitMap.get(unit)
                .toMap()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().contains(specialKey))
                .map(e -> Map.entry(e.getKey().substring(specialKey.length() + 1), e.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        stringObjectEntry -> Integer.parseInt(stringObjectEntry.getValue().toString())
                ));
        return result;
    }

    private static final String STAT_RESOURCES = "stats_resources";
    public Map<String, Integer> getResources(String unit) {
        String specialKey = STAT_RESOURCES;
        Map<String, Integer> result =  mUnitMap.get(unit)
                .toMap()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().contains(specialKey))
                .map(e -> Map.entry(e.getKey().substring(specialKey.length() + 1), e.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        stringObjectEntry -> Integer.parseInt(stringObjectEntry.getValue().toString())
                ));
        return result;
    }
}
