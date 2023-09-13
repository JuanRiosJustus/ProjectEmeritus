package main.utils;

import java.io.FileReader;
import java.util.*;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class JsonParser {

    private final List<Map<String, String>> mRecords = new ArrayList<>();
    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(JsonParser.class);
    private List<JsonObject> mRecords2 = new ArrayList<>();

    public JsonParser(String path) {
        init(path);
    }

    private List<Object> parseArray(JsonArray array) {
        List<Object> result = new ArrayList<>();
        for (Object jsonEntry : array) {
            if (jsonEntry instanceof JsonArray value) {
                result.add(parseArray(value));
            } else if (jsonEntry instanceof JsonObject value) {
                result.add(parseObject(value));
            } else {
                result.add(jsonEntry);
            }
        }
        return result;
    }

    private Map<String, Object> parseObject(JsonObject object) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> jsonEntry : object.entrySet()) {
            if (jsonEntry instanceof JsonArray value) {
                result.put(jsonEntry.getKey(), parseArray(value));
            } else if (jsonEntry instanceof JsonObject value) {
                result.put(jsonEntry.getKey(), parseObject(value));
            } else {
                result.put(jsonEntry.getKey(), jsonEntry.getValue());
            }
        }
        return result;
    }

    private void init(String path) {
        try {
            logger.info("Starting processing {}", path);

            FileReader reader = new FileReader(path);
            JsonArray array = (JsonArray) Jsoner.deserialize(reader);
//            JsonArray array = (JsonArray) Jsoner.deserialize(reader);
            for (Object o : array) {
                Map<String, String> record = new HashMap<>();
                JsonObject object = (JsonObject) o;
                for (String key : object.keySet()) {
                    Object data = object.get(key);
                    record.put(key, String.valueOf(data));
                }
                mRecords.add(record);
            }

            logger.info("Completed processing {}", path);
        } catch (Exception ex) {
            logger.error("Failed processing {} because {}", path, ex.getMessage());
        }
    }
    public Map<String, String> getRecord(int index) {
        return mRecords.get(index);
    }

    public int getRecordCount() { return mRecords.size(); }
}
