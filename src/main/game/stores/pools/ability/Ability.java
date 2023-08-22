package main.game.stores.pools.ability;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import main.constants.Constants;
import main.game.components.Summary;
import main.game.entity.Entity;
import main.game.stats.node.StatsNode;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class Ability {

    public static final String
            IGNORE_DEFENSES = "IgnoreDefenses",
            CAN_FRIENDLY_FIRE = "CanFriendlyFire";

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
    private final Set<String> type;
    public final String animation;

    private final Map<String, Float> costMap;
    private final Map<String, Float> damageMap;
    public final Map<String, Float> tagsToTargetsMap;
    public final Map<String, Float> tagsToUserMap;
    private final Set<String> traits;
    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(Ability.class);

    public Ability(Map<String, String> dao) {
        name = dao.get("Name");
        description = dao.get("Description");
        accuracy = Float.parseFloat(dao.get("Accuracy"));
        range = Integer.parseInt(dao.get("Range"));
        area = Integer.parseInt(dao.get("Area"));
        type = new HashSet<>(Arrays.asList(dao.get("Type").split(",")));
        impact = dao.get("Impact");
        animation = dao.get("Animation");

        traits = new HashSet<>(Arrays.asList(dao.get("Traits").split(",")));

        costMap = toScalarFloatMap(dao.get("Cost"));
        damageMap = toScalarFloatMap(dao.get("Damage"));

        tagsToUserMap = toScalarFloatMap(dao.get("TagsToUser"));
        tagsToTargetsMap = toScalarFloatMap(dao.get("TagsToTargets"));
    }

    public boolean hasTag(String tag) { return traits.contains(tag); }
    public Set<String> getTypes() { return type; }
    public String toString() { return name; }

    public boolean isHealthDamaging() {
        float base = 0;
        float totalScalingDamage = 0;
        for (Map.Entry<String, Float> entry : damageMap.entrySet()) {
            if (!entry.getKey().contains(HEALTH)) { continue;  }
            totalScalingDamage += entry.getValue();
            if (base != 0 || entry.getKey().contains(BASE)) { continue; }
            base = entry.getValue();
        }
        return totalScalingDamage > 0 && base > 0;
    }

    public boolean isEnergyDamaging() {
        float base = 0;
        float totalScalingDamage = 0;
        for (Map.Entry<String, Float> entry : damageMap.entrySet()) {
            if (!entry.getKey().contains(ENERGY)) { continue;  }
            totalScalingDamage += entry.getValue();
            if (base != 0 || entry.getKey().contains(BASE)) { continue; }
            base = entry.getValue();
        }
        return totalScalingDamage > 0 && base > 0;
    }

    private static Map<String, Float> toScalarFloatMap(String token) {
        Map<String, Float> map = new HashMap<>();
        if (token == null || token.isEmpty()) { return map; }
        String[] tokens = token.split(",");

        // each key value pair with be in the form of someAttribute=99.99
        for (String keyValue : tokens) {
            String key = keyValue.substring(0, keyValue.indexOf("="));
            String value = keyValue.substring(keyValue.indexOf("=") + 1);
            map.put(key.trim(), Float.parseFloat(value));
        }

        return map;
    }

    public boolean canNotPayCosts(Entity user) {
        Summary stats = user.get(Summary.class);
        boolean canNotPayHealthCost = stats.getStatCurrent(Constants.HEALTH) < getHealthCost(user);
        boolean canNotPayEnergyCost = stats.getStatCurrent(Constants.ENERGY) < getEnergyCost(user);
        return canNotPayHealthCost || canNotPayEnergyCost;
    }

    public int getHealthCost(Entity entity) { return getCost(entity, HEALTH); }
    public int getEnergyCost(Entity entity) { return getCost(entity, ENERGY); }
    private int getCost(Entity entity, String resource) {

        Summary summary = entity.get(Summary.class);
        int total = summary.getStatTotal(resource);
        int current = summary.getStatCurrent(resource);

        float result = 0;
        for (Map.Entry<String, Float> cost : costMap.entrySet()) {

            String token = cost.getKey();
            float value = 0;

            if (!token.endsWith(resource)) { continue; }

            if (token.contains(BASE)) {
                value = cost.getValue();
            } else if (token.contains(PERCENT)) {
                if (token.contains(MAX)) {
                    value = total * cost.getValue();
                } else if (token.contains(MISSING)) {
                    value =  (total - current) * cost.getValue();
                } else if (token.contains(CURRENT)) {
                    value = current * cost.getValue();
                }
            }
            result += value;
        }
        return (int) result;
    }

    public float getHealthDamage(Entity entity) { return getDamage(entity, HEALTH); }
    public float getEnergyDamage(Entity entity) { return getDamage(entity, ENERGY); }
    private float getDamage(Entity entity, String resource) {

        Summary summary = entity.get(Summary.class);
        Set<String> nodes = summary.getStatNodeNames();

        float total = 0;
        for (Map.Entry<String, Float> damage : damageMap.entrySet()) {

            String token = damage.getKey();
            float value = 0;

            if (!token.endsWith(resource)) { continue; }

            // Base damage is special case
            if (token.contains(BASE)) {
                value = damage.getValue();
            } else {
                String referenced = nodes.stream()
                        .filter(node -> token.toLowerCase().contains(node))
                        .findFirst()
                        .orElse(null);
                if (referenced == null) { continue; }
                StatsNode node = summary.getStatsNode(referenced);
                if (token.contains(TOTAL)) {
                    value = node.getTotal() * damage.getValue();
                } else if (token.contains(MODIFIED)) {
                    value = node.getModified() * damage.getValue();
                } else if (token.contains(BASE)) {
                    value = node.getBase() * damage.getValue();
                }
            }
            total += value;
        }
        return (int)total;
    }
}
