package main.game.stores.pools.unit;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import main.constants.Constants;
import main.game.components.*;
import main.game.components.behaviors.AiBehavior;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.stores.core.CsvReader;
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
            JsonArray objects = (JsonArray) Jsoner.deserialize(reader);
            for (Object object : objects) {
                JsonObject jsonObject = (JsonObject) object;
                Unit unit = new Unit(jsonObject);
                mUnitTemplateMap.put(unit.name.toLowerCase(Locale.ROOT), unit);
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

    public List<Entity> getEntityList() {
        return new ArrayList<>(mUnitMap.values());
    }

    public JsonObject save(Entity entity) {
        JsonObject jsonObject = new JsonObject();

        Identity identity = entity.get(Identity.class);
        Statistics statistics = entity.get(Statistics.class);
        Inventory inventory = entity.get(Inventory.class);

        jsonObject.put("name", identity.getName());
        jsonObject.put("uuid", identity.getUuid());

        jsonObject.put("species", statistics.getSpecies());
        jsonObject.put("vocation", statistics.getVocation());

        jsonObject.put("level", statistics.getLevel());
        jsonObject.put("experience", statistics.getExperience());

        jsonObject.put("items", new JsonArray());

        return jsonObject;
    }


    public String getRandomUnit() {
        List<String> unitTemplates = new ArrayList<>(mUnitTemplateMap.keySet().stream().toList());
        Collections.shuffle(unitTemplates);
        String randomUnit = mUnitTemplateMap.get(unitTemplates.get(0)).name;

        String nickname = RandomUtils.createRandomName(3, 6);
        return create(randomUnit, nickname + (randomUnit.isBlank() ? "": " the " + randomUnit), UUID.randomUUID().toString(), false);
    }

    public String getTestUnit(int id) {
        return create("", "test unit " + id, "1337" + id, true);
    }

    public String create(String species, String name, String uuid, boolean controlled) {
        return create(species, name, uuid, "N/A", 1, 0, controlled);
    }

    public String create(String species, String name, String uuid, String vocation, int lvl, int xp, boolean control) {

        // The unit with this uuid is already loaded.
        if (mUnitMap.containsKey(uuid)) {
            return uuid;
        }

        Entity entity = new Entity();
        entity.add(new Identity(name, uuid));

        if (control) {
            entity.add(new UserBehavior());
        } else {
            entity.add(new AiBehavior());
        }

        entity.add(new AbilityManager());
        entity.add(new MovementManager());
        entity.add(new AnimationMovementTrack());
        entity.add(new Overlay());
        entity.add(new Tags());
        entity.add(new Inventory());
        entity.add(new History());

        Unit unit = getUnitTemplate(species);
        entity.add(new Statistics(unit, vocation, lvl, xp));

        String simplified = (unit.named.isBlank() ? species : unit.named)
                .replaceAll(" ", "")
                .replaceAll("_", "");

        String id = AssetPool.getInstance().createAsset(UNITS_SPRITEMAP, simplified, 0, STRETCH_Y_ANIMATION);
        entity.add(AssetPool.getInstance().getAnimation(id));
        // TODO maybe we can find a way to wrap animation such that we can grab new animation if the sprite size changes
        // We coudl use the animation above as just vector 2f and remove the animation (buffered iumages) from it
        entity.add(new AssetWrapper(UNITS_SPRITEMAP, simplified, 0, STRETCH_Y_ANIMATION));

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
