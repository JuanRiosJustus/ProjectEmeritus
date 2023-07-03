package game.stores.pools.ability;

import constants.Constants;
import game.stores.core.CsvReader;
import logging.Logger;
import logging.LoggerFactory;

import java.io.FileReader;
import java.util.*;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

public class AbilityPool {

    private final Map<String, Ability> map = new HashMap<>();
    private static AbilityPool instance = null;
    public static AbilityPool getInstance() { if (instance == null) { instance = new AbilityPool(); } return instance; }

    private AbilityPool() {
        Logger logger = LoggerFactory.instance().logger(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        try (FileReader reader = new FileReader(Constants.ABILITY_DATA_FILE_JSON)) {
            // Parse the JSON file content
            Object obj = Jsoner.deserialize(reader);
            JsonObject dao = (JsonObject) obj;
            JsonArray data = (JsonArray) dao.get("abilities");

            for (int index = 0; index < data.size(); index++) {
                dao = (JsonObject) data.get(index);
                Ability ability = new Ability(dao);
                map.put(ability.name, ability);
            }
            logger.info("Finished initializing {}", getClass().getSimpleName());
        } catch (Exception e) {
            logger.error("Failed initializing {} because {}", getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public Ability getAbility(String name) {
        return map.get(name);
    }
}
