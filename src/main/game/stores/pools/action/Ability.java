package main.game.stores.pools.action;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import main.game.components.Summary;
import main.game.entity.Entity;

public class Ability {

    private static final String BASE = "Base";
    private static final String TOTAL = "Total";
    private static final String MODIFIED = "Modified";
    private static final String PERCENT = "Percent";
    private static final String MISSING = "Missing";
    private static final String CURRENT = "Current";
    private static final String MAX = "Max";

    public final String name;
    public final String description;
    public final float accuracy;
    public final int range;
    public final int area;
    public final String impact;
    public final String travel;
    private final Set<String> types = new HashSet<>();
    public final String animation;
    private final Set<String> traits = new HashSet<>();
    public final Map<String, Float> conditionsToUserChances = new HashMap<>();
    public final Map<String, Float> conditionsToTargetsChances = new HashMap<>();
//    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(Action.class);
    public final Map<String, Float> stats = new HashMap<>();

    private final JsonObject mDao;
    public Ability(JsonObject dao) {
        name = (String) dao.get("Name");
        description = (String) dao.getOrDefault("Description", "N/A");
        accuracy = ((BigDecimal)dao.getOrDefault("Accuracy", 0)).intValue();
        range = ((BigDecimal)dao.getOrDefault("Range", 0)).intValue();
        area = ((BigDecimal)dao.getOrDefault("Area", 0)).intValue();
        travel = (String) dao.get("Travel");

        for (Map.Entry<String, Object> entry : dao.entrySet()) {
            if (!(entry.getValue() instanceof BigDecimal value)) { continue; }
            stats.put(entry.getKey(), value.floatValue());
        }

        JsonArray array;
        if (dao.containsKey("Types")) {
            array = (JsonArray) dao.get("Types");
            types.addAll(array.stream().map(Object::toString).collect(Collectors.toSet()));
        }

        if (dao.containsKey("Traits")) {
            array = (JsonArray) dao.get("Traits");
            traits.addAll(array.stream().map(Object::toString).collect(Collectors.toSet()));
        }

        for (Map.Entry<String, Object> entry : dao.entrySet()) {
            if (entry.getKey().contains("Tags.To.Targets")) {
                String key = entry.getKey().substring(entry.getKey().lastIndexOf(".") + 1);
                BigDecimal value = (BigDecimal) entry.getValue();
                conditionsToTargetsChances.put(key, value.floatValue());
            } else if (entry.getKey().contains("Tags.To.User")) {
                String key = entry.getKey().substring(entry.getKey().lastIndexOf(".") + 1);
                BigDecimal value = (BigDecimal) entry.getValue();
                conditionsToUserChances.put(key, value.floatValue());
            }
        }

        impact = (String)dao.getOrDefault("Impact", "");
        animation = (String) dao.getOrDefault("Animation", "N/A");

        mDao = dao;
    }

    public boolean hasTag(String tag) { return traits.contains(tag); }
    public Set<String> getTypes() { return types; }
    public String toString() { return name; }
    public boolean cantPayCosts(Entity user) {
        Summary stats = user.get(Summary.class);
        Set<String> resourceKeys = stats.getResourceKeys();
        for (String key : resourceKeys) {
            int cost = getCost(user, key);
            if (cost == 0) { continue; }
            if (cost > 0 && stats.getStatCurrent(key) >= cost) { continue; }
            return true;
        }
        return false;
    }
    public Set<String> getCostKeys() {
        // Damage keys are int the form of "damage.resourceToDamage.{base, nodeBasedOn.modifier}"
        return mDao.keySet().stream()
                .filter(e -> e.startsWith("Cost"))
                .filter(e -> e.split("\\.").length < 3)
                .map(e -> {
                    String[] damageKeys = e.split("\\.");
                    return damageKeys[1];
                })
                .collect(Collectors.toSet());
    }
    public int getCost(Entity entity, String resource) {

        Map<String, Float> cost = mDao.entrySet().stream()
                .filter(entry -> entry.getKey().contains("Cost"))
                .filter(entry -> entry.getKey().contains(resource))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> ((BigDecimal)entry.getValue()).floatValue()));

        Summary summary = entity.get(Summary.class);
        int total = summary.getStatTotal(resource);
        int current = summary.getStatCurrent(resource);

        float result = 0;

        for (Map.Entry<String, Float> entry : cost.entrySet()) {
            float value = 0;
            if (entry.getKey().contains(BASE)) {
                value = entry.getValue();
            } else if (entry.getKey().contains(PERCENT)) {
                if (entry.getKey().contains(MAX)) {
                    value = total * entry.getValue();
                } else if (entry.getKey().contains(MISSING)) {
                    value =  (total - current) * entry.getValue();
                } else if (entry.getKey().contains(CURRENT)) {
                    value = current * entry.getValue();
                }
            }
            result += value;
        }
        return (int) result;
    }

    public Set<String> getDamageKeys() {
        // Damage keys are int the form of "damage.resourceToDamage.{base, nodeBasedOn.modifier}"
        return mDao.keySet().stream()
                .filter(e -> e.contains("Damage"))
                .filter(e -> e.split("\\.").length > 2)
                .map(e -> {
                    String[] damageKeys = e.split("\\.");
                    return damageKeys[1];
                })
                .collect(Collectors.toSet());
    }
    public float getHealthDamage(Entity entity) { return getDamage(entity, Summary.HEALTH); }
    public float getManaDamage(Entity entity) { return getDamage(entity, Summary.MANA); }
    public float getStaminaDamage(Entity entity) { return getDamage(entity, Summary.STAMINA); }
    public float getDamage(Entity entity, String resource) {

        Summary summary = entity.get(Summary.class);
        Map<String, Float> damage = mDao.entrySet().stream()
                .filter(entry -> entry.getKey().contains("Damage"))
                .filter(entry -> entry.getKey().contains(resource))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> ((BigDecimal)entry.getValue()).floatValue()));

        float total = 0;
        for (Map.Entry<String, Float> entry : damage.entrySet()) {

            float value = 0;
            String[] keys = entry.getKey().split("\\.");

            if (entry.getKey().endsWith(BASE)) {
                value = entry.getValue();
            } else {
                String node = keys[2];
                String modifier = keys[3];
                switch (modifier) {
                    case TOTAL -> value = summary.getStatTotal(node) * entry.getValue();
                    case MODIFIED -> value = summary.getStatModified(node) * entry.getValue();
                    case BASE -> value = summary.getStatBase(node) * entry.getValue();
                }
            }

            total += value;
        }
        return (int)total;
    }
}
