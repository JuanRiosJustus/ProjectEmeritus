package game.stores.pools.unit;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UnitTemplate {
    public final String name;
    public final boolean unique;
    public final Set<String> types;
    public final Set<String> abilities;
    public final Map<String, Integer> stats = new HashMap<>();
    public UnitTemplate(JsonObject jsonObject) {
        name = jsonObject.getString(Jsoner.mintJsonKey("name", null));
        types = new HashSet<>(jsonObject.getCollection(Jsoner.mintJsonKey("type", null)));
        unique = jsonObject.getBoolean(Jsoner.mintJsonKey("unique", null));
        abilities = new HashSet<>(jsonObject.getCollection(Jsoner.mintJsonKey("abilities", null)));

        JsonObject statsJson = jsonObject.getMap(Jsoner.mintJsonKey("stats", null));
        for (String key : statsJson.keySet()) {
            stats.put(key, statsJson.getIntegerOrDefault(Jsoner.mintJsonKey(key, null)));
        }
    }
}
