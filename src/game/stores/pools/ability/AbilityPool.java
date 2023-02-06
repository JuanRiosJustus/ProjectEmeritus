package game.stores.pools.ability;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import constants.Constants;
import logging.Logger;
import logging.LoggerFactory;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class AbilityPool {

    private final Map<String, Ability> map = new HashMap<>();
    private static AbilityPool instance = null;
    public static AbilityPool instance() { if (instance == null) { instance = new AbilityPool(); } return instance; }

    private AbilityPool() {
        Logger logger = LoggerFactory.instance().logger(getClass());
        logger.banner("Started initializing " + getClass().getSimpleName());

        try {

            // Load all the abilities from the CSV file
            String abilityDataFile = Constants.ABILITY_DATA_FILE;
            Reader fileReader = Files.newBufferedReader(Paths.get(abilityDataFile));
            JsonObject file = (JsonObject) Jsoner.deserialize(fileReader);
            JsonArray array = (JsonArray) file.get("abilities");

            logger.log("Loaded all abilities from {0}", abilityDataFile);

            for (Object object : array) {
                Ability ability = new Ability((JsonObject) object);
                map.put(ability.name, ability);
            }
        } catch (Exception e) {
            logger.log("Exception with ability Store - " + e.getMessage());
            System.err.println("Exception with Ability Store - " + e.getMessage());
            e.printStackTrace();
        }
        logger.banner("Finished initializing {0}", getClass().getSimpleName());
    }

    public Ability getAbility(String name) {
//        Ability ability = map.get(name);
//        if (ability == null) {
//            System.err.println("Unable to parse " + name);
//        }
//        return ability;
        return map.get(name);
    }
}
