package game.stores.pools.unit;

import constants.Constants;
import game.stores.core.CsvReader;
import logging.Logger;
import logging.LoggerFactory;

import java.util.*;

public class UnitPool {

    private static UnitPool instance = null;
    public static UnitPool instance() { if (instance == null) { instance = new UnitPool(); } return instance; }
    private final Map<String, Map<String, String>> map1 = new HashMap<>();

    private final Map<String, Unit> map = new HashMap<>();

    private UnitPool() {
        Logger logger = LoggerFactory.instance().logger(getClass());
        logger.banner("Started initializing {0}", getClass().getSimpleName());
        try {

            CsvReader reader = new CsvReader(Constants.UNITS_DATA_FILE_CSV, ",");
            logger.log("Finished parsing CSV from {0}", Constants.UNITS_DATA_FILE_CSV);

            for (int index = 1; index < reader.getSize(); index++) {
                Map<String, String> row = reader.getRow(index);
                Unit template = new Unit(row);
                map.put(template.name, template);
            }
            
            logger.log("Finished mapping Units CSV to Units Map");
        } catch (Exception e) {
            logger.log(e.getMessage());
        }
        logger.banner("Finished initializing {0}", getClass().getSimpleName());
    }

    public Unit getUnit(String name) {
        return map.get(name);
    }
    public Map<String, String> getStatisticsTemplate(String unitName) {
        return new HashMap<>(map1.get(unitName));
    }

}
