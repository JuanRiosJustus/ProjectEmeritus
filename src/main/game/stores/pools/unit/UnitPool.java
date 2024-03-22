package main.game.stores.pools.unit;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import main.constants.Constants;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.io.FileReader;
import java.util.*;

public class UnitPool {

    private static UnitPool mInstance = null;
    public static UnitPool getInstance() { if (mInstance == null) { mInstance = new UnitPool(); } return mInstance; }

    private final Map<String, Unit> map = new HashMap<>();

    private UnitPool() {
        ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        try {
            FileReader reader = new FileReader(Constants.UNITS_DATA_FILE_JSON);
            JsonObject objects = (JsonObject) Jsoner.deserialize(reader);
            for (Object object : objects.values()) {
                JsonObject dao = (JsonObject) object;
                Unit unit = new Unit(dao);
                map.put(unit.name.toLowerCase(Locale.ROOT), unit);
            }
        } catch (Exception ex) {
            logger.error("Error parsing prototype: " + ex.getMessage());
            ex.printStackTrace();
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public Unit getUnit(String unit) {
        return map.get(unit.toLowerCase());
    }
}
