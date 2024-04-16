package main.game.stores.pools.ability;

import java.util.*;
import java.util.stream.Collectors;

import main.game.components.Statistics;
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
    public final Map<String, Float> stats = new HashMap<>();
    public String damageExpression;
    public String costExpression;

    public Ability(Map<String, String> map) {
        name = map.get("Name");
        description = map.getOrDefault("Description", "N/A");
        accuracy = Integer.parseInt(map.getOrDefault("Accuracy", "0"));
        range =  Integer.parseInt(map.getOrDefault("Range", "0"));
        area =  Integer.parseInt(map.getOrDefault("Area", "0"));
        travel = map.get("Travel");
        damageExpression = map.get("Damage");
        costExpression = map.get("Cost");
        types.addAll(List.of(map.get("Type").split(" ")));

        impact = map.getOrDefault("Impact", "");
        animation = map.getOrDefault("Animation", "N/A");
    }

    public boolean hasTag(String tag) { return traits.contains(tag); }
    public Set<String> getTypes() { return types; }
    public String toString() { return name; }

    public boolean canNotPayCosts(Entity user) {
        Statistics stats = user.get(Statistics.class);

        Set<String> costKeys = getCostKeys();
        for (String key : costKeys) {
            int cost = getCost(user, key);
            if (cost == 0) { continue; }
            if (cost > 0 && stats.getStatCurrent(key) >= cost) { continue; }
            return true;
        }
        return false;
    }

    public Set<String> getCostKeys() {
        // Damage keys are int the form of "damage.resourceToDamage.{base, nodeBasedOn.modifier}"

        return Arrays.stream(costExpression.split("\\+"))
                .map(e -> {
                    String[] coefficientParts = e.split(" ");
                    return coefficientParts[coefficientParts.length - 1];
                }).collect(Collectors.toSet());
    }

    public float getHealthDamage(Entity entity) { return getDamage(entity, Statistics.HEALTH); }
    public float getManaDamage(Entity entity) { return getDamage(entity, Statistics.MANA); }
    public float getStaminaDamage(Entity entity) { return getDamage(entity, Statistics.STAMINA); }

    public int getDamage(Entity entity, String damageType) {
        String[] damageCoefficients = damageExpression.split("\\+");

        Statistics userStats = entity.get(Statistics.class);

        float total = 0;

        for (String damageToken : damageCoefficients) {

            if (!damageToken.contains(damageType)) {
                continue;
            }

            String[] datum = damageToken.trim().split(" ");
            String resource = datum[2];
            String scalingType = datum[1];
            String value = datum[0];
            boolean isPercentage = value.contains(".");
            float scalar = Float.parseFloat(value);
            float localTotal = 0;

            switch (scalingType) {
                case "Base" -> {
                    if (isPercentage) {
                        localTotal += userStats.getStatBase(resource) * scalar;
                    } else {
                        localTotal += Float.parseFloat(value);
                    }
                }
                case "Modified" -> {
                    if (isPercentage) { localTotal += userStats.getStatModified(resource) * scalar; }
                }
                case "Missing" -> {
                    if (isPercentage) {
                        localTotal += (userStats.getStatTotal(resource) - userStats.getStatCurrent(resource)) * scalar;
                    }
                }
                case "Current" -> {
                    if (isPercentage) {
                        localTotal += userStats.getStatCurrent(resource) * scalar;
                    }
                }
                case "Total" -> {
                    if (isPercentage) {
                        localTotal += userStats.getStatTotal(resource) * scalar;
                    }
                }
            }

            total += localTotal;
        }

        return (int) total;
    }

    public int getCost(Entity entity, String costType) {
        if (costExpression.isEmpty()) { return 0; }

        String[] costCoefficients = costExpression.split("\\+");

        Statistics stats = entity.get(Statistics.class);

        float total = 0;

        // If the value is a floating point, were using a percentage
        for (String costToken : costCoefficients) {

            if (!costToken.contains(costType)) { continue; }

            String[] datum = costToken.trim().split(" ");
            String resource = datum[2];
            String scalingType = datum[1];
            String value = datum[0];
            float scalar = Float.parseFloat(value);
            float localTotal = 0;
            boolean isPercentage = value.contains(".");

            switch (scalingType) {
                case "Base" -> {
                    if (isPercentage) {
                        localTotal += stats.getStatBase(resource) * scalar;
                    } else {
                        localTotal += Float.parseFloat(value);
                    }
                }
                case "Modified" -> {
                    if (isPercentage) {
                        localTotal += stats.getStatModified(resource) * scalar;
                    }
                }
                case "Missing" -> {
                    if (isPercentage) {
                        localTotal += (stats.getStatTotal(resource) - stats.getStatCurrent(resource)) * scalar;
                    }
                }
                case "Current" -> {
                    if (isPercentage) {
                        localTotal += stats.getStatCurrent(resource) * scalar;
                    }
                }
                case "Total" -> {
                    if (isPercentage) {
                        localTotal += stats.getStatTotal(resource) * scalar;
                    }
                }
            }
            total += localTotal;
        }

        return (int) total;

    }

    public Set<String> getDamageKeys() {
        return Arrays.stream(damageExpression.split("\\+"))
                .map(e -> {
                    String[] coefficientParts = e.split(" ");
                    return coefficientParts[coefficientParts.length - 1];
                }).collect(Collectors.toSet());
    }
}
