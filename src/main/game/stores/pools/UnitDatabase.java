package main.game.stores.pools;

import main.game.components.statistics.StatisticsComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import main.constants.Constants;
import main.game.components.*;
import main.game.components.behaviors.Behavior;
import main.game.entity.Entity;
import main.game.stores.factories.EntityStore;
import main.logging.EmeritusLogger;

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
        EmeritusLogger logger = EmeritusLogger.create(getClass());
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
}
