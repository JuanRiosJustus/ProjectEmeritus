package main.utils;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class JsonParser {


    private List<Map<String, String>> records = new ArrayList<>();
    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(JsonParser.class);

    public JsonParser(String path) {
        try {
            logger.info("Starting processing {}", path);

            FileReader reader = new FileReader(path);
            JsonArray array = (JsonArray) Jsoner.deserialize(reader);
            for (Object o : array) {
                Map<String, String> record = new HashMap<>();
                JsonObject object = (JsonObject) o;
                for (String key : object.keySet()) {
                    Object data = object.get(key);
                    record.put(key, String.valueOf(data));
                }
                records.add(record);
            }

            logger.info("Completed processing {}", path);
        } catch (Exception ex) {
            logger.error("Failed processing {} because {}", path, ex.getMessage());
        }
    }

    public Map<String, String> getRecord(int index) {
        return records.get(index);
    }

    public int getRecordCount() { return records.size(); }
}
