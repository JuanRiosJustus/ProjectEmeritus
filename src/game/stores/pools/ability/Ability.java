package game.stores.pools.ability;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import game.stores.pools.JsonValidation;

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
    public final Set<String> type;
    public final Map<String, Float> statusToUser;
    public final AbilityScalar healthCost;
    public final AbilityScalar healthDamage;
    public final AbilityScalar energyDamage;
    public final AbilityScalar energyCost;
    public final Map<String, Float> statusToTargets;

    public Ability(JsonObject jsonObject) {
        name = jsonObject.getString(Jsoner.mintJsonKey("name", null));
        description = jsonObject.getString(Jsoner.mintJsonKey("description", null));
        accuracy = jsonObject.getFloat(Jsoner.mintJsonKey("accuracy", -1f));
        range = jsonObject.getInteger(Jsoner.mintJsonKey("range", -1f));
        area = jsonObject.getInteger(Jsoner.mintJsonKey("area", -1f));
        type = new HashSet<>(jsonObject.getCollection(Jsoner.mintJsonKey("type", null)));
        friendlyFire = jsonObject.getBoolean(Jsoner.mintJsonKey("friendlyFire", true));

        healthCost = JsonValidation.getAbilityScalar(jsonObject, "healthCost");
        healthDamage = JsonValidation.getAbilityScalar(jsonObject, "healthDamage");
        energyCost = JsonValidation.getAbilityScalar(jsonObject, "energyCost");
        energyDamage = JsonValidation.getAbilityScalar(jsonObject, "energyDamage");

        statusToTargets = JsonValidation.getStringToFloatMap(jsonObject, "statusToTargets");
        statusToUser = JsonValidation.getStringToFloatMap(jsonObject, "statusToUser");
    }

    public String toString() { return name; }
}
