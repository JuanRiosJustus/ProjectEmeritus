package main.utils;

import java.util.*;

import main.logging.EmeritusLogger;
import org.json.JSONArray;
import org.json.JSONObject;


public class JsonParser {

    private final List<Map<String, String>> mRecords = new ArrayList<>();
    private static final EmeritusLogger logger = EmeritusLogger.create(JsonParser.class);
    private List<JSONObject> mRecords2 = new ArrayList<>();

    public JsonParser(String path) {
//        init(path);
    }

    private List<Object> parseArray(JSONArray array) {
        List<Object> result = new ArrayList<>();
        for (Object jsonEntry : array) {
            if (jsonEntry instanceof JSONArray value) {
                result.add(parseArray(value));
            } else if (jsonEntry instanceof JSONObject value) {
                result.add(parseObject(value));
            } else {
                result.add(jsonEntry);
            }
        }
        return result;
    }

    private Map<String, Object> parseObject(JSONObject object) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> jsonEntry : object.toMap().entrySet()) {
            if (jsonEntry instanceof JSONArray value) {
                result.put(jsonEntry.getKey(), parseArray(value));
            } else if (jsonEntry instanceof JSONObject value) {
                result.put(jsonEntry.getKey(), parseObject(value));
            } else {
                result.put(jsonEntry.getKey(), jsonEntry.getValue());
            }
        }
        return result;
    }

    private void init(String path) {
//        try {
//            logger.info("Starting processing {}", path);
//
//            FileReader reader = new FileReader(path);
//            JSONArray array = (JSONArray) Jsoner.deserialize(reader);
////            JSONArray array = (JSONArray) Jsoner.deserialize(reader);
//            for (Object o : array) {
//                Map<String, String> record = new HashMap<>();
//                JSONObject object = (JSONObject) o;
//                for (String key : object.keySet()) {
//                    Object data = object.get(key);
//                    record.put(key, String.valueOf(data));
//                }
//                mRecords.add(record);
//            }
//
//            logger.info("Completed processing {}", path);
//        } catch (Exception ex) {
//            logger.error("Failed processing {} because {}", path, ex.getMessage());
//        }
    }
    public Map<String, String> getRecord(int index) {
        return mRecords.get(index);
    }

    public int getRecordCount() { return mRecords.size(); }
}
