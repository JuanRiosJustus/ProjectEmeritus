package main.game.stores;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import main.constants.Constants;
import main.logging.EmeritusLogger;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TagTable {
    private static TagTable instance = null;
    public static TagTable getInstance() {
        if (instance == null) {
            instance = new TagTable();
        }
        return instance;
    }

    private Map<String, JSONObject> mTags = new HashMap<>();

    private TagTable() {
        EmeritusLogger logger = EmeritusLogger.create(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        try {
            String raw = Files.readString(Paths.get(Constants.TAGS_DATABASE_PATH));
            JSONArray rows = JSONArray.parse(raw);
            for (int i = 0; i < rows.size(); i++) {
                JSONObject data = rows.getJSONObject(i);
                String tag = data.getString("tag");
                mTags.put(tag.toLowerCase(Locale.ROOT), data);
            }
        } catch (Exception ex) {
            logger.info("Error parsing tag: " + ex.getMessage());
            logger.info("Example: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(-1);
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }
}
