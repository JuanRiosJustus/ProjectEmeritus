package game.stores.pools.unit;

import constants.Constants;
import logging.Logger;
import logging.LoggerFactory;

import java.io.FileReader;
import java.util.*;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

public class UnitPool {

    private static UnitPool instance = null;
    public static UnitPool instance() { if (instance == null) { instance = new UnitPool(); } return instance; }
    private final Map<String, Map<String, String>> map1 = new HashMap<>();

    private final Map<String, Unit> map = new HashMap<>();

    private UnitPool() {
        Logger logger = LoggerFactory.instance().logger(getClass());
        logger.banner("Started initializing {0}", getClass().getSimpleName());

        try (FileReader reader = new FileReader(Constants.UNITS_DATA_FILE_JSON)) {
            // Parse the JSON file content
            Object obj = Jsoner.deserialize(reader);
            JsonObject dao = (JsonObject) obj;
            JsonArray data = (JsonArray) dao.get("units");

            for (int index = 0; index < data.size(); index++) {
                dao = (JsonObject) data.get(index);
                Unit unit = new Unit(dao);
                map.put(unit.name, unit);
            }
        } catch (Exception e) {
            logger.log("Exception with unit Store - " + e.getMessage());
            e.printStackTrace();
        }
        
        logger.banner("Finished initializing {0}", getClass().getSimpleName());
    }

    public Unit getUnit(String name) {
        return map.get(name);
    }
}
