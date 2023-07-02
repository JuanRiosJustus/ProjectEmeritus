package game.stores.pools.ability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

public class Ability {

    public final String name;
    public final String description;
    public final boolean friendlyFire;
    public final float accuracy;
    public final int range;
    public final int area;
    public final String impact;
    public final Set<String> type;

    public final int healthCostBase;
    public final Map<String, Float> healthCostScaling;
    public final Map<String, Float> healthCostPercent;

    public final int healthDamageBase;
    public final Map<String, Float> healthDamageScaling;
    public final Map<String, Float> healthDamagePercent;

    public final int energyCostBase;
    public final Map<String, Float> energyCostScaling;
    public final Map<String, Float> energyCostPercent;

    public final int energyDamageBase;
    public final Map<String, Float> energyDamageScaling;
    public final Map<String, Float> energyDamagePercent;

    public final Map<String, Float> statusToTargets;
    public final Map<String, Float> statusToUser;

    public Ability(JsonObject dao) {
        name = dao.getString(Jsoner.mintJsonKey("name", null));
        description = dao.getString(Jsoner.mintJsonKey("description", null));
        accuracy = dao.getFloat(Jsoner.mintJsonKey("accuracy", null));
        range = dao.getInteger(Jsoner.mintJsonKey("range", null));
        area = dao.getInteger(Jsoner.mintJsonKey("area", null));
        type = new HashSet<>(dao.getCollection(Jsoner.mintJsonKey("type", null)));
        impact = dao.getString(Jsoner.mintJsonKey("impact", null));
        friendlyFire = dao.getBoolean(Jsoner.mintJsonKey("friendlyFire", null));

        healthCostBase = dao.getInteger(Jsoner.mintJsonKey("healthCostBase", null));
        healthCostScaling = toFloatMap(dao, "healthCostScaling");
        healthCostPercent = toFloatMap(dao, "healthCostPercent");

        energyCostBase = dao.getInteger(Jsoner.mintJsonKey("energyCostBase", null));
        energyCostScaling = toFloatMap(dao, "energyCostScaling");
        energyCostPercent = toFloatMap(dao, "energyCostPercent");

        healthDamageBase = dao.getInteger(Jsoner.mintJsonKey("healthDamageBase", null));
        healthDamageScaling = toFloatMap(dao, "healthDamageScaling");
        healthDamagePercent = toFloatMap(dao, "healthDamagePercent");

        energyDamageBase = dao.getInteger(Jsoner.mintJsonKey("energyDamageBase", null));
        energyDamageScaling = toFloatMap(dao, "energyDamageScaling");
        energyDamagePercent = toFloatMap(dao, "energyDamagePercent");

        statusToUser = toFloatMap(dao, "statusToUser");
        statusToTargets = toFloatMap(dao, "statusToTargets");
    }

    private static Map<String, Float> toFloatMap(JsonObject object, String key) {
        Map<String, Float> map = new HashMap<>();
        JsonObject dao = object.getMap(Jsoner.mintJsonKey(key, null));
        for (String entry : dao.keySet()) {
            Number number = (Number) dao.get(entry);
            map.put(entry, number.floatValue());
        }
        return map;
    }

    public String toString() { return name; }
}
