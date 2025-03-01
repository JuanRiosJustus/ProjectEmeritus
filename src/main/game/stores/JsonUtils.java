package main.game.stores;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class JsonUtils {

    public static Object getValueFromJsonArray(JSONArray jsonArray, String path) {
        if (jsonArray == null || path == null || path.isEmpty()) {
            return null;
        }

        String[] pathing = path.split("\\.");
        Object currentObjectOrArray = jsonArray;

        for (String key : pathing) {
            if (currentObjectOrArray instanceof JSONObject obj) {
                if (obj.has(key)) {
                    currentObjectOrArray = obj.get(key);
                } else {
                    return null;
                }
            } else if (currentObjectOrArray instanceof JSONArray arr) {
                try {
                    int index = Integer.parseInt(key);
                    if (index >= arr.length()) {
                        return null;
                    }
                    currentObjectOrArray = arr.get(index);
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    return null;
                }
            } else {
                return null;
            }
        }

        return currentObjectOrArray;
    }

    public static Object getValueFromJsonObject(JSONObject jsonObject, String path) {
        if (jsonObject == null || path == null || path.isEmpty()) {
            return null;
        }

        String[] pathing = path.split("\\.");
        Object currentObjectOrArray = jsonObject;

        for (String key : pathing) {
            if (currentObjectOrArray instanceof JSONObject obj) {
                if (obj.has(key)) {
                    currentObjectOrArray = obj.get(key);
                } else {
                    return null;
                }
            } else if (currentObjectOrArray instanceof JSONArray arr) {
                try {
                    int index = Integer.parseInt(key);
                    if (index >= arr.length()) {
                        return null;
                    }
                    currentObjectOrArray = arr.get(index);
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    return null;
                }
            } else {
                return null;
            }
        }

        return currentObjectOrArray;
    }

    public static List<String> getStringList(JSONArray array) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            String value = array.getString(i);
            list.add(value);
        }
        return list;
    }

    public static List<Float> getFloatList(JSONArray array) {
        List<Float> map = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            float value = array.getFloat(i);
            map.add(value);
        }
        return map;
    }

    public static List<Integer> getIntegerList(JSONArray array) {
        List<Integer> map = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            int value = array.getInt(i);
            map.add(value);
        }
        return map;
    }


    public static Map<String, String> getStringMap(JSONObject jsonObject) {
        Map<String, String> map = new HashMap<>();
        for (String key : jsonObject.keySet()) {
            String value = jsonObject.getString(key);
            map.put(key, value);
        }
        return map;
    }

    public static Map<String, Float> getFloatMap(JSONObject jsonObject) {
        Map<String, Float> map = new HashMap<>();
        for (String key : jsonObject.keySet()) {
            float value = jsonObject.getFloat(key);
            map.put(key, value);
        }
        return map;
    }

    public static Map<String, Integer> getIntegerMap(JSONObject jsonObject) {
        Map<String, Integer> map = new HashMap<>();
        for (String key : jsonObject.keySet()) {
            int value = jsonObject.getInt(key);
            map.put(key, value);
        }
        return map;
    }
}