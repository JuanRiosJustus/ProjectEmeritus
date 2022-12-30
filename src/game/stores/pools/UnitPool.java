package game.stores.pools;

import constants.Constants;
import game.stores.pools.ability.AbilityPool;
import logging.Logger;
import logging.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class UnitPool {

    private static UnitPool instance = null;
    public static UnitPool instance() { if (instance == null) { instance = new UnitPool(); } return instance; }
    private final Map<String, Map<String, String>> map = new HashMap<>();

    private UnitPool() {
        Logger logger = LoggerFactory.instance().logger(getClass());
        logger.banner("Started initializing " + getClass().getSimpleName());
        try {
            Path fileName = Path.of(Constants.UNITS_DATA_FILE);
            List<String> records = Files.readAllLines(fileName);
            String[] header = records.get(0).replaceAll("\\s", "").split(",");
            logger.log("Loading CSV from " + fileName);
//            logger.log("Header: " + Arrays.toString(header));

            // Make a template for every unit
            for (int row = 1; row < records.size(); row++) {

                // Get unit/template properties
                Map<String, String> template = new LinkedHashMap<>();
                String[] record = records.get(row).split(",");
                for (int column = 0; column < record.length; column++) {
                    String sanitized = record[column].strip();
                    if (sanitized.isEmpty()) { continue; }
                    template.put(header[column], sanitized);
                }
                String name = template.get(header[0]);
                map.put(name, template);
            }
        } catch (Exception e) {
            logger.log(e.getMessage());
        }
        logger.banner("Finished initializing {0}", getClass().getSimpleName());
    }

    public Map<String, String> getStatisticsTemplate(String unitName) {
        return new HashMap<>(map.get(unitName));
    }

}
