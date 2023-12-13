package main.game.stores.pools.action;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import main.constants.Constants;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class AbilityPool {

    private final Map<String, Ability> mActionsMap = new HashMap<>();
    private static AbilityPool instance = null;
    public static AbilityPool getInstance() {
        if (instance == null) {
            instance = new AbilityPool();
        }
        return instance;
    }

    private AbilityPool() {
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
                Ability ability = new Ability((JsonObject) object);
                mActionsMap.put(ability.name.toLowerCase(Locale.ROOT), ability);
            }
        } catch (Exception ex) {
            logger.info("Error parsing prototype: " + ex.getMessage());
            ex.printStackTrace();
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public Ability get(String name) {
        return mActionsMap.get(name.toLowerCase());
    }
}
