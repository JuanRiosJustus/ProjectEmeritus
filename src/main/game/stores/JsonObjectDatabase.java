package main.game.stores;

import main.constants.Constants;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import org.json.JSONArray;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonObjectDatabase {


    private final Map<String, JsonObjectTable> mTables = new LinkedHashMap<>();

    private static JsonObjectDatabase mInstance = null;
    public static JsonObjectDatabase getInstance() {
        if (mInstance == null) {
            mInstance = new JsonObjectDatabase();
        }
        return mInstance;
    }
    private JsonObjectDatabase() {
        ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        String[] databases = new String[] {
                Constants.UNITS_DATABASE,
                Constants.ABILITIES_DATABASE,
                Constants.TAGS_DATABASE
        };

        try {
            // Consider all the databases in the given path
            for (String database : databases) {
                JSONArray rows = new JSONArray(Files.readString(Path.of(database)));
                // The database
                JsonObjectTable jsonObjectTable = new JsonObjectTable(rows);
                String name = database.substring(database.lastIndexOf("/") + 1, database.lastIndexOf(".json"));
                mTables.put(name, jsonObjectTable);
            }
            logger.info("Successfully initialized {}", getClass().getSimpleName());
        } catch (Exception ex) {
            logger.error("Error parsing Unit: " + ex.getMessage());
            System.exit(-1);
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public JsonObjectTable get(String table) { return mTables.get(table); }
}
