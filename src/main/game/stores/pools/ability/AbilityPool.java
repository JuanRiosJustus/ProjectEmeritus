package main.game.stores.pools.ability;

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

    private final Map<String, Ability> mAbilityMap = new HashMap<>();
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
            FileReader reader = new FileReader(Constants.ABILITIES_DATABASE);
            JsonArray objects = (JsonArray) Jsoner.deserialize(reader);
            for (Object object : objects) {
                JsonObject jsonObject = (JsonObject) object;
                Ability ability = new Ability(jsonObject);
                mAbilityMap.put(ability.name.toLowerCase(Locale.ROOT), ability);
            }
        } catch (Exception ex) {
            logger.info("Error parsing ability: " + ex.getMessage());
            System.exit(-1);
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public Ability getAbility(String name) {
        return mAbilityMap.get(name.toLowerCase());
    }
}
