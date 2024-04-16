package main.game.stores.pools.ability;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import main.constants.Constants;
import main.game.stores.core.CsvReader;
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
            CsvReader reader = new CsvReader(Constants.ABILITIES_DATABASE);
            System.out.println(reader.getHeader().toString());
            for (Map<String, String> dao : reader.getRows()) {
                Ability ability = new Ability(dao);
                mActionsMap.put(ability.name.toLowerCase(Locale.ROOT), ability);
            }
        } catch (Exception ex) {
            logger.info("Error parsing prototype: " + ex.getMessage());
            ex.printStackTrace();
        }

        try {
//            FileReader reader = new FileReader(Constants.ABILITY_DATA_FILE_JSON);
//            JsonObject objects = (JsonObject) Jsoner.deserialize(reader);
//            for (Object object : objects.values()) {
//                Ability ability = new Ability((JsonObject) object);
//                mActionsMap.put(ability.name.toLowerCase(Locale.ROOT), ability);
//            }
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
