package game.stores.pools.unit;

import constants.Constants;
import logging.ELogger;
import logging.ELoggerFactory;
import utils.CsvParser;

import java.util.*;

public class UnitPool {

    private static UnitPool instance = null;
    public static UnitPool instance() { if (instance == null) { instance = new UnitPool(); } return instance; }

    private final Map<String, Unit> map = new HashMap<>();

    private UnitPool() {
        ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        CsvParser parser = new CsvParser(Constants.UNITS_DATA_FILE_CSV);
        
        for (int index = 0; index < parser.getRecordCount(); index++) {
            Map<String, String> record = parser.getRecord(index);
            Unit unit = new Unit(record);
            map.put(unit.name.toLowerCase(), unit);
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public Unit getUnit(String name) {
        return map.get(name);
    }
}
