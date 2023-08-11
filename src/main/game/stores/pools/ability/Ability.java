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

    public final int baseHealthCost;
    private final Map<String, Float> percentHealthCost;

    public final int baseEnergyCost;
    private final Map<String, Float> percentEnergyCost;

    public final int baseHealthDamage;
    private final Map<String, Float> scalingHealthDamage;

    public final int baseEnergyDamage;
    private final Map<String, Float> scalingEnergyDamage;

    public final Map<String, Float> statusToTargets;
    public final Map<String, Float> statusToUser;

    private final Set<String> tags;
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

        tags = new HashSet<>(Arrays.asList(dao.get("Tags").split(",")));

        baseHealthCost = Integer.parseInt(dao.get("BaseHealthCost"));
        percentHealthCost = toScalarFloatMap(dao.get("PercentHealthCost"));

        baseEnergyCost = Integer.parseInt(dao.get("BaseEnergyCost"));
        percentEnergyCost = toScalarFloatMap(dao.get("PercentEnergyCost"));

        baseHealthDamage = Integer.parseInt(dao.get("BaseHealthDamage"));
        scalingHealthDamage = toScalarFloatMap(dao.get("ScalingHealthDamage"));

        baseEnergyDamage = Integer.parseInt(dao.get("BaseEnergyDamage"));
        scalingEnergyDamage = toScalarFloatMap(dao.get("ScalingEnergyDamage"));

        statusToUser = toScalarFloatMap(dao.get("StatusToUser"));
        statusToTargets = toScalarFloatMap(dao.get("StatusToTargets"));
    }

    public boolean hasTag(String tag) { return tags.contains(tag); }
    public Set<String> getTypes() { return type; }
    public String toString() { return name; }

    public boolean isHealthDamaging() {
        boolean hasBaseHealthDamage = baseHealthDamage > 0;
        float totalScalingDamage = 0;
        for (Map.Entry<String, Float> entry : scalingHealthDamage.entrySet()) {
            totalScalingDamage += entry.getValue();
        }
        return hasBaseHealthDamage && totalScalingDamage > 0;
    }

    public boolean isEnergyDamaging() {
        boolean hasBaseEnergyDamage = baseEnergyDamage > 0;
        float totalScalingDamage = 0;
        for (Map.Entry<String, Float> entry : scalingEnergyDamage.entrySet()) {
            totalScalingDamage += entry.getValue();
        }
        return hasBaseEnergyDamage && totalScalingDamage > 0;
    }


    private static Map<String, Float> toScalarFloatMap(String token) {
        Map<String, Float> map = new HashMap<>();
        if (token == null || token.length() <= 0) { return map; }
        String[] tokens = token.split(",");

        // each key value pair witl be in the form of someAttribute=99.99
        for (String keyValue : tokens) {
            String key = keyValue.substring(0, keyValue.indexOf("="));
            String value = keyValue.substring(keyValue.indexOf("=") + 1);
            map.put(key, Float.parseFloat(value));
        }

        return map;
    }

    public float getHealthCost(Entity user) {

        Statistics userSummary = user.get(Statistics.class);
        ResourceNode health = userSummary.getResourceNode(Constants.HEALTH);

        float base = baseHealthCost;

        float percentage = getPercentageTotal(health, percentHealthCost);

        float total = base + percentage;

        return total;
    }

    public float getEnergyCost(Entity user) {
        Statistics stats = user.get(Statistics.class);
        ResourceNode energy = stats.getResourceNode(Constants.ENERGY);

        float base = baseEnergyCost;
        float percentage = getPercentageTotal(energy, percentEnergyCost);
        float total = base + percentage;

        return total;
    }


    public float getHealthDamage(Entity user) {
        Statistics stats = user.get(Statistics.class);

        float base = baseHealthDamage;
        float scaling = getScalingTotal(stats, scalingHealthDamage);
        float total = base + scaling;

        return total;
    }

    /**
     * Gets the abilities energy damage based on the user's stat totals
     */
    public float getEnergyDamage(Entity user) {
        Statistics stats = user.get(Statistics.class);

        float base = baseEnergyDamage;
        float scaling = getScalingTotal(stats, scalingEnergyDamage);
        float total = base + scaling;

        return total;
    }

    private float getPercentageTotal(ResourceNode node, Map<String, Float> damagePercentage) {
        float total = 0;
        for (Map.Entry<String, Float> entry : damagePercentage.entrySet()) {
            String key = entry.getKey();
            float value = entry.getValue();
            switch (key) {
                case Constants.MISSING -> total += (node.getTotal() - node.getCurrent()) * value;
                case Constants.CURRENT -> total += value * node.getCurrent();
                case Constants.MAX -> total += value * node.getTotal();
                default -> logger.warn("Unsupported percentage type when calculating");
            }
        }
        return total;
    }

    private float getScalingTotal(Statistics summary, Map<String, Float> damageScaling) {
        float total = 0;
        for (Map.Entry<String, Float> entry : damageScaling.entrySet()) {
            StatsNode node = summary.getStatsNode(entry.getKey());
            float subtotal = node.getTotal() * entry.getValue();
            total += subtotal;
        }
        return total;
    }
}
