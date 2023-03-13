package game.stores.pools.ability;

import constants.Constants;
import game.stores.core.CsvReader;
import logging.Logger;
import logging.LoggerFactory;

import java.util.*;

public class AbilityPool {

    private final Map<String, Ability> map = new HashMap<>();
    private static AbilityPool instance = null;
    public static AbilityPool instance() { if (instance == null) { instance = new AbilityPool(); } return instance; }

    private AbilityPool() {
        Logger logger = LoggerFactory.instance().logger(getClass());
        logger.banner("Started initializing {0}", getClass().getSimpleName());

        try {

            // Load all the abilities from the CSV file
            CsvReader reader = new CsvReader(Constants.ABILITY_DATA_FILE_CSV, ",");
            logger.log("Finished parsing CSV from {0}", Constants.ABILITY_DATA_FILE_CSV);

            for (int row = 1; row < reader.getSize(); row++) {
                Map<String, String> rowData = reader.getRow(row);
                Ability ability = new Ability(rowData);
                map.put(ability.name, ability);
            }

            logger.log("Finished mapping Ability CSV to Ability Map");
        } catch (Exception e) {
            logger.log("Exception with ability Store - " + e.getMessage());
            System.err.println("Exception with Ability Store - " + e.getMessage());
            e.printStackTrace();
        }
        logger.banner("Finished initializing {0}", getClass().getSimpleName());
    }

    public Ability getAbility(String name) {
        return map.get(name);
    }
}
