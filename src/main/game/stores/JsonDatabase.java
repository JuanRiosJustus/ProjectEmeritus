package main.game.stores;

import main.constants.Constants;
import main.logging.EmeritusLogger;

import org.json.JSONArray;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonDatabase {


    private final Map<String, JsonTable> mTables = new LinkedHashMap<>();

    private static JsonDatabase mInstance = null;
    public static JsonDatabase getInstance() {
        if (mInstance == null) {
            mInstance = new JsonDatabase();
        }
        return mInstance;
    }
    private JsonDatabase() {
        EmeritusLogger logger = EmeritusLogger.create(getClass());
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
                JsonTable jsonTable = new JsonTable(rows);
                String name = database.substring(database.lastIndexOf("/") + 1, database.lastIndexOf(".json"));
                mTables.put(name, jsonTable);
            }
            logger.info("Successfully initialized {}", getClass().getSimpleName());
        } catch (Exception ex) {
            logger.error("Error parsing Unit: " + ex.getMessage());
            System.exit(-1);
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public JsonTable get(String table) { return mTables.get(table); }
}
