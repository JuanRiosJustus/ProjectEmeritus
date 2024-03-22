package main.game.stores.pools.action;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

        try {
            FileReader reader = new FileReader(Constants.ABILITY_DATA_FILE_JSON);
            JsonObject objects = (JsonObject) Jsoner.deserialize(reader);
            for (Object object : objects.values()) {
                Ability ability = new Ability((JsonObject) object);
                mActionsMap.put(ability.name.toLowerCase(Locale.ROOT), ability);
            }
        } catch (Exception ex) {
            logger.info("Error parsing prototype: " + ex.getMessage());
            ex.printStackTrace();
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public Ability getAbility(String name) {
        return mActionsMap.get(name.toLowerCase());
    }
}
