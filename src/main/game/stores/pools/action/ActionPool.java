package main.game.stores.pools.action;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import main.constants.Constants;
import main.game.stores.pools.unit.Unit;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.JsonParser;

public class ActionPool {

    private final Map<String, Action> mActionsMap = new HashMap<>();
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

//         CsvParser parser = new CsvParser(Constants.ABILITY_DATA_FILE_CSV);
//        JsonParser parser = new JsonParser(Constants.ABILITY_DATA_FILE_JSON);
//
//        for (int index = 0; index < parser.getRecordCount(); index++) {
//            Map<String, String> record = parser.getRecord(index);
//            Action action = new Action(record);
//            mActionsMap.put(action.name.toLowerCase(), action);
//        }

        try {
            FileReader reader = new FileReader(Constants.ABILITY_DATA_FILE_JSON);
            JsonArray array = (JsonArray) Jsoner.deserialize(reader);
            for (Object object : array) {
                Action action = new Action((JsonObject) object);
                mActionsMap.put(action.name.toLowerCase(Locale.ROOT), action);
            }
        } catch (Exception ex) {
            logger.info("Error parsing prototype: " + ex.getMessage());
            ex.printStackTrace();
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public Action get(String name) {
        return mActionsMap.get(name.toLowerCase());
    }
}
