package main.game.stores;

import main.logging.ELogger;
import main.logging.ELoggerFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.zip.Inflater;

public class JsonTable {
    private final Map<String, JSONObject> mRows = new LinkedHashMap<>();
    private static final String ID_FIELD = "id";
    public JsonTable(JSONArray table) {
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


//    public Map<String, V>  getString(String id, String path, Class< defaultValue) { return (String) getOrDefault(id, path, defaultValue); }
//    public String getString(String id, String path) { return (String) getOrDefault(id, path, null); }

//    public List<String> getListAsStrings(String id, String[] path) {
//        JSONObject listData = (JSONObject) get(id, path);
//        JSONObject data = mRows.get(id);
//        return JsonUtils.get(data, path, Object.class);
//
//        JSONObject listData = (JSONObject) get(id, path);
//        List<String> result = new ArrayList<>();
//
//        if (listData != null) {
//            for (String key : listData.keySet()) {
//                String value = listData.getString(key);
//                result.add(value);
//            }
//        }
//        return result;
//    }

//    public List<Float> getListAsNumbers(String id, String[] path) {
//        JSONObject listData = (JSONObject) get(id, path);
//        List<Float> result = new ArrayList<>();
//
//        if (listData != null) {
//            for (String key : listData.keySet()) {
//                float value = listData.getFloat(key);
//                result.add(value);
//            }
//        }
//        return result;
//    }

//    public Map<String, String> getMapAsStrings(String id, String[] path) {
//        JSONObject listData = (JSONObject) get(id, path);
//        Map<String, String> result = new LinkedHashMap<>();
//
//        if (listData != null) {
//            for (String key : listData.keySet()) {
//                String value = listData.getString(key);
//                result.put(key, value);
//            }
//        }
//        return result;
//    }

//    public Map<String, Float> getMapAsFloats(String id, String path) {
//        JSONObject listData = (JSONObject) getOrDefault(id, path);
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

//    public Map<String, Float> getMapAsFloats(String id, String[] path) {
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

    public List<Float> getFloatList(String id, String path) {
        JSONObject row = mRows.get(id);
        JSONArray array = (JSONArray) JsonUtils.getValueFromJsonObject(row, path);
        List<Float> result = JsonUtils.getFloatList(array);
        return result;
    }

    public Map<String, Float> getFloatMap(String id, String path) {
        JSONObject row = mRows.get(id);
        JSONObject object = (JSONObject) JsonUtils.getValueFromJsonObject(row, path);
        Map<String, Float> result = JsonUtils.getFloatMap(object);
        return result;
    }

    public List<String> getStringList(String id, String path) {
        JSONObject row = mRows.get(id);
        JSONArray array = (JSONArray) JsonUtils.getValueFromJsonObject(row, path);
        List<String> result = JsonUtils.getStringList(array);
        return result;
    }


    public int count() { return mRows.size(); }
    public Set<String> keySet() { return mRows.keySet(); }
}
