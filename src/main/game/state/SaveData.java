package main.game.state;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import main.json.JsonSerializable;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

public abstract class SaveData extends JsonObject {
    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(SaveData.class);
    protected String mSavedDataFilePath = null;
    protected JsonObject getOrLoadData(String filePath) {
        // Don't load again  if already have user data loaded
//        if (mJsonData != null && !mJsonData.isEmpty()) {
//            return mJsonData;
//        }
        try {
            logger.info("Reading save file from {}", filePath);
            FileReader reader = new FileReader(filePath);
//            mJsonData = (JsonObject) Jsoner.deserialize(reader);
            mSavedDataFilePath = filePath;
            logger.info("Successfully loaded file from {}", filePath);
        } catch (Exception ex) {
//            mJsonData = new JsonObject();
            mSavedDataFilePath = filePath;
            logger.warn("Failed to load file from {} {}", filePath, ex);
        }

//        return mJsonData;
        return null;
    }

    public void save(String key, JsonObject value, String... path) {
        JsonObject loadedData = load(path);
        key = key.replace(" ", "_");
        // If this key exists, replace
        loadedData.put(key, value);
//        saveData(mSavedDataFilePath, Jsoner.prettyPrint(mJsonData.toJson()).replaceAll("\\\\", ""));
    }

    public JsonObject load(String... path) {
        JsonObject loadedData = getOrLoadData(mSavedDataFilePath);
        int index = 0;
        JsonObject currentNode = loadedData;
        // Could this be improved to handle indexing as well
        while (true) {
            String key = path[index];
            currentNode = (JsonObject) currentNode.get(key);
            // If we are at the last index of the array, return
            boolean isTerminal = index == path.length - 1;
            if (isTerminal) {
                break;
            } else {
                index++;
            }
        }
        return currentNode;
    }

    private JsonObject saveData(String filePath, String data) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath, false), true)) {
            out.write(data);
        } catch (Exception ex) {
            logger.error("Unable to save user data {}", ex.toString());
        }
        return null;
    }
}
