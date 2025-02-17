package main.game.stores;

import main.logging.ELogger;
import main.logging.ELoggerFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class JsonObjectTable {
    private final Map<String, JSONObject> mRows = new LinkedHashMap<>();
    private static final String ID_FIELD = "id";
    public JsonObjectTable(JSONArray table) {
        ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        try {
            for (int index = 0; index < table.length(); index++) {
                JSONObject row = table.getJSONObject(index);
                String id = row.getString(ID_FIELD);
                mRows.put(id, row);
            }

            logger.info("Successfully initialized {}", getClass().getSimpleName());
        } catch (Exception ex) {
            logger.error("Error parsing Unit: " + ex.getMessage());
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public JSONObject getRow(String id) {
        return mRows.get(id);
    }

    private Object get(String id, String[] path) {
        JSONObject current = mRows.get(id);

        // Traverse the JSON path
        for (int i = 0; i < path.length; i++) {
            String key = path[i];

            // Check if the key exists at the current level
            if (!current.has(key)) { return null; }

            // If we're at the terminal position, return the value
            if (i == path.length - 1) { return current.get(key); }

            // Move to the next nested JSONObject / JSONArray
            if (current.get(key) instanceof JSONObject) {
                current = current.getJSONObject(key);
            } else {
                return null; // Path leads to a non-JSONObject before the end
            }
        }

        return null; // Default return (should not be reached)
    }

    public List<String> getListAsStrings(String id, String[] path) {
        JSONObject listData = (JSONObject) get(id, path);
        List<String> result = new ArrayList<>();

        if (listData != null) {
            for (String key : listData.keySet()) {
                String value = listData.getString(key);
                result.add(value);
            }
        }
        return result;
    }

    public List<Float> getListAsNumbers(String id, String[] path) {
        JSONObject listData = (JSONObject) get(id, path);
        List<Float> result = new ArrayList<>();

        if (listData != null) {
            for (String key : listData.keySet()) {
                float value = listData.getFloat(key);
                result.add(value);
            }
        }
        return result;
    }

    public Map<String, String> getMapAsStrings(String id, String[] path) {
        JSONObject listData = (JSONObject) get(id, path);
        Map<String, String> result = new LinkedHashMap<>();

        if (listData != null) {
            for (String key : listData.keySet()) {
                String value = listData.getString(key);
                result.put(key, value);
            }
        }
        return result;
    }

    public Map<String, Float> getMapAsFloats(String id, String[] path) {
        JSONObject listData = (JSONObject) get(id, path);
        Map<String, Float> result = new LinkedHashMap<>();

        if (listData != null) {
            for (String key : listData.keySet()) {
                float value = listData.getFloat(key);
                result.put(key, value);
            }
        }
        return result;
    }
//    public int get getMapAsFloats(String id, String[] path) {
//        JSONObject listData = (JSONObject) get(id, path);
//        Map<String, Float> result = new LinkedHashMap<>();
//
//        if (listData != null) {
//            for (String key : listData.keySet()) {
//                float value = listData.getFloat(key);
//                result.put(key, value);
//            }
//        }
//        return result;
//    }

    public int count() { return mRows.size(); }
    public Set<String> keySet() { return mRows.keySet(); }
}
