package main.game.stores.pools;

import main.constants.JSONTable;
import org.json.JSONArray;
import org.json.JSONObject;
import main.constants.Constants;
import main.game.entity.Entity;
import main.logging.EmeritusLogger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class UnitDatabase extends JSONTable {

    private static UnitDatabase mInstance = null;
    public static UnitDatabase getInstance() {
        if (mInstance == null) {
            mInstance = new UnitDatabase();
        }
        return mInstance;
    }
    private String mTableName = "";
    private final Map<String, Entity> mLiveUnitMap = new HashMap<>();

    private final Map<String, JSONObject> mCached = new LinkedHashMap<>();
    private final Map<String, JSONObject> mUnitMap = new HashMap<>();
    private final static String RESOURCES_KEY = "resources";
    private final static String ATTRIBUTES_KEY = "attribute";

    private UnitDatabase() {
        EmeritusLogger logger = EmeritusLogger.create(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        try {
            String jsonData = Files.readString(Path.of(Constants.UNITS_DATABASE_RESOURCE_PATH));
            mTable = new JSONArray(jsonData);
            mTableName = "UNIT_TABLE";
            logger.info("Successfully initialized {}", getClass().getSimpleName());
        } catch (Exception ex) {
            logger.info("Error parsing unit: " + ex.getMessage());
            logger.info("Example: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(-1);
        }
        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

//    public String


    private JSONObject getOrCacheResult(String name) {
        JSONObject result = mCached.getOrDefault(name, null);
        if (result == null) {
            JSONArray query = executeQuery(
                    "SELECT * FROM " + mTableName + " WHERE ability = '" + name + "'"
            );
            result = query.getJSONObject(0);
            mCached.put(name, result);
        }
        return result;
    }

    public Collection<String> getAllPossibleUnits() {
        JSONArray results = executeQuery("SELECT unit FROM " + mTableName);
        List<String> result = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            JSONObject row = results.getJSONObject(i);
            String unit = row.getString("unit");
            result.add(unit);
        }
        return result;
    }
}
