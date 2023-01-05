package game.stores.pools;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import game.stores.pools.ability.AbilityScalar;

import java.util.*;

public class JsonValidation {

    public static Map<String, Float> getStringToFloatMap(JsonObject json, String key) {
        if (json.isEmpty()) { return null; }

        JsonObject object = (JsonObject) json.get(key);
        if (object == null) { return null; }

        Map<String, Float> map = new HashMap<>();
        for (String str : object.keySet()) {
            map.put(str, object.getFloat(Jsoner.mintJsonKey(str, 0f)));
        }
        return map;
    }

    public static AbilityScalar getAbilityScalar(JsonObject json, String key) {
        if (json.isEmpty()) { return null; }

        JsonObject object = (JsonObject) json.get(key);
        if (object == null) { return null; }

        return new AbilityScalar(object);
    }
}
