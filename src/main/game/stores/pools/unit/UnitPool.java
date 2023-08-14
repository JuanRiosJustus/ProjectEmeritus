package main.game.stores.pools.unit;

import main.constants.Constants;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.JsonParser;

import java.util.*;

public class UnitPool {

    private static UnitPool instance = null;
    public static UnitPool getInstance() { if (instance == null) { instance = new UnitPool(); } return instance; }

    private final Map<String, Unit> map = new HashMap<>();

    private UnitPool() {
        ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        // CsvParser parser = new CsvParser(Constants.UNITS_DATA_FILE_CSV);
        JsonParser parser = new JsonParser(Constants.UNITS_DATA_FILE_JSON);
        
        for (int index = 0; index < parser.getRecordCount(); index++) {
            Map<String, String> record = parser.getRecord(index);
            Unit unit = new Unit(record);
            map.put(unit.unit.toLowerCase(), unit);
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public Unit getUnit(String unit) {
        return map.get(unit.toLowerCase());
    }
}
