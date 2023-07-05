package game.stores.pools.ability;

import java.util.HashMap;
import java.util.Map;

import constants.Constants;
import logging.ELogger;
import logging.ELoggerFactory;
import utils.CsvParser;

public class AbilityPool {

    private final Map<String, Ability> map = new HashMap<>();
    private static AbilityPool instance = null;
    public static AbilityPool getInstance() { if (instance == null) { instance = new AbilityPool(); } return instance; }

    private AbilityPool() {
        ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        CsvParser parser = new CsvParser(Constants.ABILITY_DATA_FILE_CSV);

        for (int index = 0; index < parser.getRecordCount(); index++) {
            Map<String, String> record = parser.getRecord(index);
            Ability ability = new Ability(record);
            map.put(ability.name.toLowerCase(), ability);
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public Ability getAbility(String name) {
        return map.get(name.toLowerCase());
    }
}
