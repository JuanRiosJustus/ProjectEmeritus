package game.stores.pools.ability;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.util.HashMap;
import java.util.Map;

public class AbilityScalar {

    public final int base;
    public final Map<String, Float> scaling = new HashMap<>();
    public final Map<String, Float> percent = new HashMap<>();
    public AbilityScalar(JsonObject object) {

        base = object.getInteger(Jsoner.mintJsonKey("base", 0f));

        JsonObject temp = (JsonObject) object.get("scaling");
        for (String s : temp.keySet()) {
            scaling.put(s, temp.getFloat(Jsoner.mintJsonKey(s, 0f)));
        }

        temp = (JsonObject) object.get("percent");
        for (String s : temp.keySet()) {
            percent.put(s, temp.getFloat(Jsoner.mintJsonKey(s, 0f)));
        }
    }
    
    public boolean isEmpty() { return base == 0 && scaling.isEmpty() && percent.isEmpty(); }

}
