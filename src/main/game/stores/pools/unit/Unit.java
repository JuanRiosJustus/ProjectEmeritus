package main.game.stores.pools.unit;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.*;

public class Unit {
    private final JSONObject mRawJson;

    public Unit(JSONObject dao) {
        mRawJson = dao;
    }
    public String getStringValue(String key) { return (String) mRawJson.get(key); }

    @SuppressWarnings("unchecked")
    public <T> List<T> getListValue(String key) {
        JSONArray list = (JSONArray) mRawJson.get(key);
        return list.toList().stream().map(item -> (T)item).toList();
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
