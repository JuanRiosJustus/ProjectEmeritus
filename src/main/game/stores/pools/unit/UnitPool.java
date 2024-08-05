package main.game.stores.pools.unit;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import main.constants.Constants;
import main.game.components.*;
import main.game.components.behaviors.AiBehavior;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.stores.factories.EntityFactory;
import main.game.stores.pools.asset.AssetPool;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.RandomUtils;

import java.io.FileReader;
import java.util.*;

import static main.game.stores.pools.asset.AssetPool.STRETCH_Y_ANIMATION;
import static main.game.stores.pools.asset.AssetPool.UNITS_SPRITEMAP;

public class UnitPool {

    private static UnitPool mInstance = null;
    public static UnitPool getInstance() { if (mInstance == null) { mInstance = new UnitPool(); } return mInstance; }

    private final Map<String, Unit> mUnitTemplateMap = new HashMap<>();
    private final Map<String, Entity> mUnitMap = new HashMap<>();

    private UnitPool() {
        ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        try {
            FileReader reader = new FileReader(Constants.UNITS_DATABASE);
            JsonObject objects = (JsonObject) Jsoner.deserialize(reader);
            for (String key : objects.keySet()) {
                JsonObject jsonObject = (JsonObject) objects.get(key);
                Unit unit = new Unit(jsonObject);
                mUnitTemplateMap.put(key.toLowerCase(Locale.ROOT), unit);
            }

        } catch (Exception ex) {
            logger.error("Error parsing Unit: " + ex.getMessage());
            System.exit(-1);
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    private Unit getUnitTemplate(String unit) {
        return mUnitTemplateMap.get(unit.toLowerCase());
    }

    public Entity get(String uuid) {
        return mUnitMap.get(uuid);
    }

    public JsonObject save(Entity entity) {
        JsonObject jsonObject = new JsonObject();

        Identity identity = entity.get(Identity.class);
        Statistics statistics = entity.get(Statistics.class);
        Inventory inventory = entity.get(Inventory.class);

        jsonObject.put("name", identity.getName());
        jsonObject.put("uuid", identity.getUuid());

        jsonObject.put("species", statistics.getUnit());
        jsonObject.put("level", statistics.getLevel());
//        jsonObject.put("experience", statistics.getExperience());

        jsonObject.put("items", new JsonArray());

        return jsonObject;
    }

    public String getRandomUnit() {
        return getRandomUnit(false);
    }

    public String getRandomUnit(boolean controlled) {
        List<String> unitTemplates = new ArrayList<>(mUnitTemplateMap.keySet().stream().toList());
        Collections.shuffle(unitTemplates);
        String randomUnit = mUnitTemplateMap.get(unitTemplates.get(0)).getStringValue("Unit");

        String nickname = RandomUtils.createRandomName(3, 6);
        return create(randomUnit, nickname, UUID.randomUUID().toString(), controlled);
    }

    public String create(String species, String name, String uuid, boolean controlled) {
        return create(species, name, uuid, "N/A", 1, 0, controlled);
    }

    public String create(String species, String name, String uuid, String vocation, int lvl, int xp, boolean control) {

        // The unit with this uuid is already loaded.
        if (mUnitMap.containsKey(uuid)) {
            return uuid;
        }
//        Entity entity = new Entity();
//        entity.add(new Identity(name, uuid));

        Entity entity = EntityFactory.create(name, uuid);

        if (control) {
            entity.add(new UserBehavior());
        } else {
            entity.add(new AiBehavior());
        }

        entity.add(new ActionManager());
        entity.add(new MovementManager());
        entity.add(new MovementTrack());
        entity.add(new Overlay());
        entity.add(new Tags());
        entity.add(new Inventory());
        entity.add(new History());
        entity.add(new DirectionalFace());

        Unit unit = getUnitTemplate(species);
        entity.add(new Statistics(unit, vocation, lvl, xp));

        String simplified = (unit.getStringValue("named") == null ? species : unit.getStringValue("named"))
                .replace(" ", "")
                .replace("_", "");

        entity.add(new Assets(Assets.UNIT_ASSET, uuid, UNITS_SPRITEMAP, simplified, 0, STRETCH_Y_ANIMATION));

        String id = AssetPool.getInstance().createAsset(UNITS_SPRITEMAP, simplified, 0, STRETCH_Y_ANIMATION);
        entity.add(AssetPool.getInstance().getAnimationWithId(id));

        String unitUuid = entity.get(Identity.class).getUuid();
        mUnitMap.put(unitUuid, entity);

        return unitUuid;
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
}
