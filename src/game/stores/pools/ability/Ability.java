package game.stores.pools.ability;

import com.github.cliftonlabs.json_simple.JsonKey;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Ability {

    public final String name;
    public final String description;
    public final boolean friendlyFire;
    public final float accuracy;
    public final int range;
    public final int area;
    public final String impact;
    public final Set<String> type;
    public final AbilityScalar healthCost;
    public final AbilityScalar healthDamage;
    public final AbilityScalar energyDamage;
    public final AbilityScalar energyCost;
    public final Map<String, Float> statusToTargets = new HashMap<>();
    public final Map<String, Float> statusToUser = new HashMap<>();

    public Ability(JsonObject object) {
        name = object.getString(Jsoner.mintJsonKey("name", ""));
        description = object.getString(Jsoner.mintJsonKey("description", ""));
        accuracy = object.getFloat(Jsoner.mintJsonKey("accuracy", -1f));
        range = object.getInteger(Jsoner.mintJsonKey("range", -1f));
        area = object.getInteger(Jsoner.mintJsonKey("area", -1f));
        type = new HashSet<>(object.getCollection(Jsoner.mintJsonKey("type", null)));
        impact = object.getString(Jsoner.mintJsonKey("impact", ""));
        friendlyFire = object.getBoolean(Jsoner.mintJsonKey("friendlyFire", true));

        healthCost = new AbilityScalar((JsonObject) object.get("healthCost"));
        healthDamage =  new AbilityScalar((JsonObject) object.get("healthDamage"));
        energyCost =  new AbilityScalar((JsonObject) object.get("energyCost"));
        energyDamage =  new AbilityScalar((JsonObject) object.get("energyDamage"));

        JsonObject temp = (JsonObject) object.get("statusToTargets");
        for (String s : temp.keySet()) {
            statusToTargets.put(s, temp.getFloat(Jsoner.mintJsonKey(s, 0f)));
        }

        temp = (JsonObject) object.get("statusToUser");
        for (String s : temp.keySet()) {
            statusToTargets.put(s, temp.getFloat(Jsoner.mintJsonKey(s, 0f)));
        }
    }
    public String toString() { return name; }
}
