package main.game.stores.pools.action;

import java.util.HashMap;
import java.util.Map;

import main.constants.Constants;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.JsonParser;

public class ActionPool {

    private final Map<String, Action> map = new HashMap<>();
    private static ActionPool instance = null;
    public static ActionPool getInstance() {
        if (instance == null) {
            instance = new ActionPool();
        }
        return instance;
    }

    private ActionPool() {
        ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        // CsvParser parser = new CsvParser(Constants.ABILITY_DATA_FILE_CSV);
        JsonParser parser = new JsonParser(Constants.ABILITY_DATA_FILE_JSON);

        for (int index = 0; index < parser.getRecordCount(); index++) {
            Map<String, String> record = parser.getRecord(index);
            Action action = new Action(record);
            map.put(action.name.toLowerCase(), action);
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public Action get(String name) {
        return map.get(name.toLowerCase());
    }
}
