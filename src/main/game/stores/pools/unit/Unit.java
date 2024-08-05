package main.game.stores.pools.unit;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;

import java.math.BigDecimal;
import java.util.*;

public class Unit {
    private final JsonObject mRawJson;

    public Unit(JsonObject dao) {
        mRawJson = dao;
    }
    public String getStringValue(String key) { return (String) mRawJson.get(key); }

    @SuppressWarnings("unchecked")
    public <T> List<T> getListValue(String key) {
        JsonArray list = (JsonArray) mRawJson.get(key);
        return list.stream().map(item -> (T)item).toList();
    }
    public Map<String, Float> getScalarKeysWithTag(String tag) {
        Map<String, Float> result = new HashMap<>();
        for (String key : mRawJson.keySet()) {
            if (!key.contains("(" + tag + ")")) { continue; }
            BigDecimal value = (BigDecimal) mRawJson.get(key);
            result.put(key.substring(0, key.indexOf("(")), value.floatValue());
        }
        return result;
    }
}
