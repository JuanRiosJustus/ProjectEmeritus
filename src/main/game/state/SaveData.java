package main.game.state;

import main.logging.EmeritusLogger;
import com.alibaba.fastjson2.JSONObject;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

public abstract class SaveData extends JSONObject {
    private static final EmeritusLogger logger = EmeritusLogger.create(SaveData.class);
    protected String mSavedDataFilePath = null;
    protected JSONObject getOrLoadData(String filePath) {
        // Don't load again  if already have user data loaded
//        if (mJsonData != null && !mJsonData.isEmpty()) {
//            return mJsonData;
//        }
        try {
            logger.info("Reading save file from {}", filePath);
            FileReader reader = new FileReader(filePath);
//            mJsonData = (JSONObject) Jsoner.deserialize(reader);
            mSavedDataFilePath = filePath;
            logger.info("Successfully loaded file from {}", filePath);
        } catch (Exception ex) {
//            mJsonData = new JSONObject();
            mSavedDataFilePath = filePath;
            logger.warn("Failed to load file from {} {}", filePath, ex);
        }

//        return mJsonData;
        return null;
    }

    public void save(String key, JSONObject value, String... path) {
        JSONObject loadedData = load(path);
        key = key.replace(" ", "_");
        // If this key exists, replace
        loadedData.put(key, value);
//        saveData(mSavedDataFilePath, Jsoner.prettyPrint(mJsonData.toJson()).replaceAll("\\\\", ""));
    }

    public JSONObject load(String... path) {
        JSONObject loadedData = getOrLoadData(mSavedDataFilePath);
        int index = 0;
        JSONObject currentNode = loadedData;
        // Could this be improved to handle indexing as well
        while (true) {
            String key = path[index];
            currentNode = (JSONObject) currentNode.get(key);
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

    private JSONObject saveData(String filePath, String data) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath, false), true)) {
            out.write(data);
        } catch (Exception ex) {
            logger.error("Unable to save user data {}", ex.toString());
        }
        return null;
    }
}
