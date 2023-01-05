package game.stores.pools.ability;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import game.stores.pools.JsonValidation;

import java.util.Map;

public class AbilityScalar {
    public final int base;
    public final Map<String, Float> scaling;
    public final Map<String, Float> percent;
    public AbilityScalar(JsonObject map) {
        base = map.getInteger(Jsoner.mintJsonKey("base", -1));
        scaling = JsonValidation.getStringToFloatMap(map, "scaling");
        percent = JsonValidation.getStringToFloatMap(map, "percent");
    }
}
