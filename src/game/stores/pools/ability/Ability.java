package game.stores.pools.ability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import constants.Constants;
import game.components.statistics.Energy;
import game.components.statistics.Health;
import game.components.statistics.Resource;
import game.components.statistics.Summary;
import game.entity.Entity;
import game.stats.node.ResourceNode;
import game.stats.node.StatsNode;
import logging.ELogger;
import logging.ELoggerFactory;
import logging.LoggerFactory;
import utils.EmeritusUtils;
import utils.MathUtils;

public class Ability {

    public final String name;
    public final String description;
    public final boolean canFriendlyFire;
    public final float accuracy;
    public final int range;
    public final int area;
    public final String impact;
    public final Set<String> type;

    public final int baseHealthCost;
    public final Map<String, Float> percentHealthCost;

    public final int baseEnergyCost;
    public final Map<String, Float> percentEnergyCost;

    public final int baseHealthDamage;
    public final Map<String, Float> scalingHealthDamage;

    public final int baseEnergyDamage;
    public final Map<String, Float> scalingEnergyDamage;

    public final Map<String, Float> statusToTargets;
    public final Map<String, Float> statusToUser;

    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(Ability.class);

    public Ability(Map<String, String> dao) {
        name = dao.get("Name");
        description = dao.get("Description");
        accuracy = Float.parseFloat(dao.get("Accuracy"));
        range = Integer.parseInt(dao.get("Range"));
        area = Integer.parseInt(dao.get("Area"));
        type = new HashSet<>(Arrays.asList(dao.get("Type").split(",")));
        impact = dao.get("Impact");
        canFriendlyFire = Boolean.parseBoolean(dao.get("CanFriendlyFire"));

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

    public String toString() { return name; }

    public float getHealthCost(Entity user) {

        Summary userSummary = user.get(Summary.class);
        ResourceNode health = userSummary.getResourceNode(Constants.HEALTH);

        float base = baseHealthCost;

        float percentage = getPercentageTotal(health, percentHealthCost);

        float total = base + percentage;

        if (total != 0) {
            logger.debug(
                "{}'s {}: {}(base) + {}(percentage) = {}(health cost total)",
                type.toString(), name, base, percentage, total
            );
        }

        return total;
    }

    public float getEnergyCost(Entity user) {

        Summary userSummary = user.get(Summary.class);
        ResourceNode energy = userSummary.getResourceNode(Constants.ENERGY);

        float base = baseEnergyCost;

        float percentage = getPercentageTotal(energy, percentEnergyCost);

        float total = base + percentage;

        if (total != 0) {
            logger.debug(
                "{}'s {}: {}(base) + {}(percentage) = {}(energy cost total)",
                type.toString(), name, base, percentage, total     
            );
        }

        return total;
    }

    /**
     * Gets the abilities health damage based on the user's stat totals
     * @param user
     * @param scalingMap
     * @return
     */
    public float getHealthDamage(Entity user) {
        Summary userSummary = user.get(Summary.class);

        float base = baseHealthDamage;

        float scaling = getScalingTotal(userSummary, scalingHealthDamage);

        float total = base + scaling;

        if (total != 0) {
            logger.debug(
                "{}'s {}: {}(base) + {}(scaling) = {}(health damage total)",
                type.toString(), name, base, scaling, total    
            );
        }

        return total;
    }

    /**
     * Gets the abilities energy damage based on the user's stat totals
     */
    public float getEnergyDamage(Entity user) {
        Summary userSummary = user.get(Summary.class);

        float base = baseEnergyDamage;

        float scaling = getScalingTotal(userSummary, scalingEnergyDamage);

        float total = base + scaling;

        if (total != 0) {
            logger.debug(
                "{}'s {} : {}(base) + {}(scaling) = {}(energy damage total)",
                type.toString(), name, base, scaling, total
            );
        }

        return total;
    }

    private float getPercentageTotal(ResourceNode node, Map<String, Float> damagePercentage) {
        float total = 0;
        for (Map.Entry<String, Float> entry : damagePercentage.entrySet()) {
            String key = entry.getKey();
            float value = entry.getValue();
            switch (key) {
                case Constants.MISSING -> total += (node.getTotal() - node.current) * value;
                case Constants.CURRENT -> total += value * node.current;
                case Constants.MAX -> total += value * node.getTotal();
                default -> logger.warn("Unsupported percentage type when calculating");
            }
        }
        return total;
    }

    private float getScalingTotal(Summary summary, Map<String, Float> damageScaling) {
        float total = 0;
        for (Map.Entry<String, Float> entry : damageScaling.entrySet()) {
            StatsNode node = summary.getStatsNode(entry.getKey());
            float subtotal = node.getTotal() * entry.getValue();
            total += subtotal;
        }
        return total;
    }
}
