package main.game.stores.pools.ability;

import java.util.HashMap;
import java.util.Map;

import main.constants.Constants;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.CsvParser;
import main.utils.JsonParser;

public class AbilityPool {

    private final Map<String, Ability> map = new HashMap<>();
    private static AbilityPool instance = null;
    public static AbilityPool getInstance() { if (instance == null) { instance = new AbilityPool(); } return instance; }

    private AbilityPool() {
        ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        // CsvParser parser = new CsvParser(Constants.ABILITY_DATA_FILE_CSV);
        JsonParser parser = new JsonParser(Constants.ABILITY_DATA_FILE_JSON);

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