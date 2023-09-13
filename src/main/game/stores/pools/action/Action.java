package main.game.stores.pools.action;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import main.game.components.Statistics;
import main.game.entity.Entity;

public class Action {

    private static final String BASE = "Base";
    private static final String TOTAL = "Total";
    private static final String MODIFIED = "Modified";
    private static final String PERCENT = "Percent";
    private static final String MISSING = "Missing";
    private static final String CURRENT = "Current";
    private static final String MAX = "Max";
    private static final String HEALTH = "Health";
    private static final String ENERGY = "Energy";

    public final String name;
    public final String description;
    public final float accuracy;
    public final int range;
    public final int area;
    public final String impact;
    private final Set<String> types = new HashSet<>();
    public final String animation;
    private final Set<String> traits = new HashSet<>();
//    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(Action.class);

    private final JsonObject mDao;
    public Action(JsonObject dao) {
        name = (String) dao.get("Name");
        description = (String) dao.getOrDefault("Description", "N/A");
        accuracy = ((BigDecimal)dao.getOrDefault("Accuracy", 0)).intValue();
        range = ((BigDecimal)dao.getOrDefault("Range", 0)).intValue();
        area = ((BigDecimal)dao.getOrDefault("Area", 0)).intValue();

        JsonArray array;
        if (dao.containsKey("Types")) {
            array = (JsonArray) dao.get("Types");
            types.addAll(array.stream().map(Object::toString).collect(Collectors.toSet()));
        }

        if (dao.containsKey("Traits")) {
            array = (JsonArray) dao.get("Traits");
            traits.addAll(array.stream().map(Object::toString).collect(Collectors.toSet()));
        }

        impact = (String)dao.getOrDefault("Impact", "");
        animation = (String) dao.getOrDefault("Animation", "N/A");

        mDao = dao;
    }

    public boolean hasTag(String tag) { return traits.contains(tag); }
    public Set<String> getTypes() { return types; }
    public String toString() { return name; }
    public boolean cantPayCosts(Entity user) {
        Statistics stats = user.get(Statistics.class);
        Set<String> resourceKeys = stats.getResourceKeys();
        for (String key : resourceKeys) {
            int cost = getCost(user, key);
            if (cost == 0) { continue; }
            if (cost > 0 && stats.getStatCurrent(key) >= cost) { continue; }
            return true;
        }
        return false;
    }
    public int getCost(Entity entity, String resource) {

        Map<String, Float> cost = mDao.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("Cost"))
                .filter(entry -> entry.getKey().contains(resource))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> ((BigDecimal)entry.getValue()).floatValue()));

        Statistics statistics = entity.get(Statistics.class);
        int total = statistics.getStatTotal(resource);
        int current = statistics.getStatCurrent(resource);

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

    public float getHealthDamage(Entity entity) { return getDamage(entity, HEALTH); }
    public float getEnergyDamage(Entity entity) { return getDamage(entity, ENERGY); }
    public float getDamage(Entity entity, String resource) {

        Statistics statistics = entity.get(Statistics.class);
        Map<String, Float> damage = mDao.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("Damage"))
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
                    case TOTAL -> value = statistics.getStatTotal(node) * entry.getValue();
                    case MODIFIED -> value = statistics.getStatModified(node) * entry.getValue();
                    case BASE -> value = statistics.getStatBase(node) * entry.getValue();
                }
            }

            total += value;
        }
        return (int)total;
    }
}
