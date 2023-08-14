package main.game.stores.pools.ability;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import main.constants.Constants;
import main.game.components.Statistics;
import main.game.entity.Entity;
import main.game.stats.node.ResourceNode;
import main.game.stats.node.StatsNode;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class Ability {

    public static final String
            IGNORE_DEFENSES = "IgnoreDefenses",
            CAN_FRIENDLY_FIRE = "CanFriendlyFire";

    public final String name;
    public final String description;
    public final float accuracy;
    public final int range;
    public final int area;
    public final String impact;
    private final Set<String> type;
    public final String animation;

    private final Map<String, Float> cost;
    private final Map<String, Float> damage;
    public final Map<String, Float> tagsToTargets;
    public final Map<String, Float> tagsToUser;

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

        cost = toScalarFloatMap(dao.get("Cost"));
        damage = toScalarFloatMap(dao.get("Damage"));

        tagsToUser = toScalarFloatMap(dao.get("TagsToUser"));
        tagsToTargets = toScalarFloatMap(dao.get("TagsToTargets"));
    }

    public boolean hasTag(String tag) { return traits.contains(tag); }
    public Set<String> getTypes() { return type; }
    public String toString() { return name; }

    public boolean isHealthDamaging() {
        float base = 0;
        float totalScalingDamage = 0;
        for (Map.Entry<String, Float> entry : damage.entrySet()) {
            if (!entry.getKey().contains(Constants.HEALTH)) { continue;  }
            totalScalingDamage += entry.getValue();
            if (base != 0 || entry.getKey().toLowerCase().contains("base")) { continue; }
            base = entry.getValue();
        }
        return totalScalingDamage > 0 && base > 0;
    }

    public boolean isEnergyDamaging() {
        float base = 0;
        float totalScalingDamage = 0;
        for (Map.Entry<String, Float> entry : damage.entrySet()) {
            if (!entry.getKey().contains(Constants.ENERGY)) { continue;  }
            totalScalingDamage += entry.getValue();
            if (base != 0 || entry.getKey().toLowerCase().contains("base")) { continue; }
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

    public int getHealthCost(Entity user) { return getCost(user, Constants.HEALTH); }
    public int getEnergyCost(Entity user) { return getCost(user, Constants.ENERGY); }
    private int getCost(Entity user, String resourceToCheck) {


        Statistics statistics = user.get(Statistics.class);
        ResourceNode resource = statistics.getResourceNode(resourceToCheck);

        float total = 0;
        for (Map.Entry<String, Float> cost : cost.entrySet()) {

            String token = cost.getKey();
            float value = 0;

            if (!token.startsWith(resourceToCheck)) { continue; }

            if (token.contains("Base")) {
                value = cost.getValue();
            } else if (token.contains("Percent")) {
                if (token.contains("Max")) {
                    value = (int) (resource.getTotal() * cost.getValue());
                } else if (token.contains("Missing")) {
                    value = (int) ((resource.getTotal() - resource.getCurrent()) * cost.getValue());
                } else if (token.contains("Current")) {
                    value = (int) (resource.getCurrent() * cost.getValue());
                }
            }
            total += value;
        }
        return (int)total;
    }

    public float getHealthDamage(Entity user) { return getDamage(user, Constants.HEALTH); }
    public float getEnergyDamage(Entity user) { return getDamage(user, Constants.ENERGY); }
    private float getDamage(Entity user, String resourceToCheck) {

        Statistics statistics = user.get(Statistics.class);
        Set<String> nodes = statistics.getStatNodeNames();

        float total = 0;
        for (Map.Entry<String, Float> cost : damage.entrySet()) {

            String token = cost.getKey();
            float value = 0;

            if (!token.startsWith(resourceToCheck)) { continue; }

            // Base damage is special case
            if (token.contains("Base")) {
                value = cost.getValue();
            } else {
                String referenced = nodes.stream()
                        .filter(node -> token.toLowerCase().contains(node))
                        .findFirst()
                        .orElse(null);
                if (referenced == null) { continue; }
                StatsNode node = statistics.getStatsNode(referenced);
                if (token.contains("Total")) {
                    value = node.getTotal() * cost.getValue();
                } else if (token.contains("Modified")) {
                    value = node.getModified() * cost.getValue();
                } else if (token.contains("Base")) {
                    value = node.getBase() * cost.getValue();
                }
            }
            total += value;
        }
        return (int)total;
    }
}
