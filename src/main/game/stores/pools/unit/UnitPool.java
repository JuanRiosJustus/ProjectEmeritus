package main.game.stores.pools.unit;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import main.constants.Constants;
import main.constants.csv.CsvTable;
import main.constants.csv.CsvRow;
import main.game.components.*;
import main.game.components.behaviors.AiBehavior;
import main.game.components.behaviors.Behavior;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.stores.factories.EntityFactory;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.JsonParser;
import main.utils.RandomUtils;

import java.io.FileReader;
import java.util.*;

public class UnitPool {

    private static UnitPool mInstance = null;
    public static UnitPool getInstance() { if (mInstance == null) { mInstance = new UnitPool(); } return mInstance; }

//    private final Map<String, Unit> mUnitTemplateMap = new HashMap<>();
    private CsvTable mUnitData = null;
    private final Map<String, Entity> mUnitMap = new HashMap<>();

    private UnitPool() {
        ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        try {
            mUnitData = new CsvTable(Constants.UNITS_DATABASE.replace(".json", ".csv"), "Unit");
        } catch (Exception ex) {
            logger.error("Error parsing Unit: " + ex.getMessage());
            System.exit(-1);
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    private CsvRow getUnit(String unit) {
        return mUnitData.get(unit);
    }

    public Entity get(String uuid) {
        return mUnitMap.get(uuid);
    }

    public JsonObject save(Entity entity) {
        JsonObject jsonObject = new JsonObject();

        IdentityComponent identityComponent = entity.get(IdentityComponent.class);
        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
        InventoryComponent inventoryComponent = entity.get(InventoryComponent.class);

        jsonObject.put("name", identityComponent.getName());
        jsonObject.put("uuid", identityComponent.getUuid());

        jsonObject.put("species", statisticsComponent.getUnit());
        jsonObject.put("level", statisticsComponent.getLevel());
//        jsonObject.put("experience", statistics.getExperience());

        jsonObject.put("items", new JsonArray());

        return jsonObject;
    }

    public String getRandomUnit() {
        return getRandomUnit(false);
    }

    public String getRandomUnit(boolean controlled) {
        List<String> unitTemplates = new ArrayList<>(mUnitData.keySet().stream().toList());
        Collections.shuffle(unitTemplates);
        String randomUnit = unitTemplates.get(0);

        String nickname = RandomUtils.createRandomName(3, 6);
        return create(randomUnit, nickname, UUID.randomUUID().toString(), controlled);
    }

    public String create(String unit, String name, String uuid, boolean controlled) {
        return create(unit, name, uuid, 1, 0, controlled);
    }

    public String create(String unit, String nickname, String uuid, int lvl, int xp, boolean control) {

        // The unit with this uuid is already loaded.
        if (mUnitMap.containsKey(uuid)) {
            return uuid;
        }

        Entity entity = EntityFactory.create(nickname, uuid);

        if (control) {
            entity.add(new UserBehavior());
        } else {
            entity.add(new AiBehavior());
        }

        entity.add(new Behavior(control));
        entity.add(new ActionComponent());
        entity.add(new MovementComponent());
        entity.add(new TrackComponent());
        entity.add(new Overlay());
        entity.add(new TagComponent());
        entity.add(new InventoryComponent());
        entity.add(new History());
        entity.add(new DirectionComponent());
        entity.add(new AssetComponent());

        entity.add(new StatisticsComponent(unit, lvl, xp));
        mUnitMap.put(uuid, entity);

        return uuid;
    }

    public String create(JsonObject unitJson, boolean controlled) {
        String finalUuid = null;
        try {
            String species = (String) unitJson.get("species");
            String name = (String) unitJson.get("name");
            String uuid = (String) unitJson.get("uuid");
            finalUuid = create(species, name, uuid,  controlled);
        } catch (Exception ex) {
            System.err.println("LOGGER FAILED - " + ex);
        }
        return finalUuid;
    }

    public List<String> getType(String unit) { return getUnit(unit).getList("Type"); }
    public String getUnitName(String unit) { return getUnit(unit).get("Unit"); }
    public List<String> getActions(String unit) { return getUnit(unit).getList("Actions"); }
    public List<String> getColumnsLike(String unit, String like) { return getUnit(unit).getColumnsLike(like); }
    public int getValueAsInt(String unit, String column) { return getUnit(unit).getInt(column); }
}
