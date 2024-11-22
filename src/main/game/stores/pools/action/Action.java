package main.game.stores.pools.action;

import java.util.*;
import java.util.stream.Collectors;

import main.game.components.StatisticsComponent;
import main.game.entity.Entity;

public class Action {

    public String name;
    public String description;
    public float accuracy;
    public int range;
    public int area;
    public String impact;
    public String travel;
    private final Set<String> types = new HashSet<>();
    public String animation;
    private final Set<String> traits = new HashSet<>();
    public final Map<String, Float> conditionsToUserChances = new HashMap<>();
    public final Map<String, Float> conditionsToTargetsChances = new HashMap<>();
    public final Map<String, Float> stats = new HashMap<>();
    public String damageExpression;
    public String costExpression;
    public Action(Map<String, String> map) {
//        name = (String) map.get("Name");
//        description = (String) map.getOrDefault("Description", "N/A");
//        accuracy = ((BigDecimal) map.getOrDefault("Accuracy", 0)).floatValue();
//        range =  ((BigDecimal) map.getOrDefault("Range", 0)).intValue();
//        area =  ((BigDecimal) map.getOrDefault("Area", 0)).intValue();
//        travel = (String) map.get("Travel");
//        damageExpression = (String) map.get("Damage");
//        costExpression = (String) map.get("Cost");
//
//        String temp = (String) map.get("Type");
//        types.addAll(List.of(temp.split(",")));
//
//        impact = (String) map.getOrDefault("Impact", "");
//        animation = (String) map.getOrDefault("Animation", "N/A");
    }
//    public Action(JSONObject map) {
//        name = (String) map.get("Name");
//        description = (String) map.getOrDefault("Description", "N/A");
//        accuracy = ((BigDecimal) map.getOrDefault("Accuracy", 0)).floatValue();
//        range =  ((BigDecimal) map.getOrDefault("Range", 0)).intValue();
//        area =  ((BigDecimal) map.getOrDefault("Area", 0)).intValue();
//        travel = (String) map.get("Travel");
//        damageExpression = (String) map.get("Damage");
//        costExpression = (String) map.get("Cost");
//
//        String temp = (String) map.get("Type");
//        types.addAll(List.of(temp.split(",")));
//
//        impact = (String) map.getOrDefault("Impact", "");
//        animation = (String) map.getOrDefault("Animation", "N/A");
//    }

    public boolean hasTag(String tag) { return traits.contains(tag); }
    public Set<String> getTypes() { return types; }
    public String toString() { return name; }

    public boolean canNotPayCosts(Entity user) {
        StatisticsComponent stats = user.get(StatisticsComponent.class);

        Set<String> costKeys = getCostKeys();
        for (String key : costKeys) {
            int cost = getCost(user, key);
            if (cost == 0) { continue; }
            if (cost > 0 && stats.getCurrent(key) >= cost) { continue; }
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

    public float getHealthDamage(Entity entity) { return getDamage(entity, StatisticsComponent.HEALTH); }
    public float getManaDamage(Entity entity) { return getDamage(entity, StatisticsComponent.MANA); }
    public float getStaminaDamage(Entity entity) { return getDamage(entity, StatisticsComponent.STAMINA); }

    public int getDamage(Entity entity, String damageType) {
        String[] damageCoefficients = damageExpression.split("\\+");

        StatisticsComponent userStats = entity.get(StatisticsComponent.class);

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
                        localTotal += userStats.getBase(resource) * scalar;
                    } else {
                        localTotal += Float.parseFloat(value);
                    }
                }
                case "Modified" -> {
                    if (isPercentage) { localTotal += userStats.getModified(resource) * scalar; }
                }
                case "Missing" -> {
                    if (isPercentage) {
                        localTotal += (userStats.getTotal(resource) - userStats.getCurrent(resource)) * scalar;
                    }
                }
                case "Current" -> {
                    if (isPercentage) {
                        localTotal += userStats.getCurrent(resource) * scalar;
                    }
                }
                case "Total" -> {
                    if (isPercentage) {
                        localTotal += userStats.getTotal(resource) * scalar;
                    }
                }
            }

            total += localTotal;
        }

        return (int) total;
    }

    public int getHealthCost(Entity entity) { return getCost(entity, StatisticsComponent.HEALTH); }
    public int getManaCost(Entity entity) { return getCost(entity, StatisticsComponent.MANA); }
    public int getStaminaCost(Entity entity) { return getCost(entity, StatisticsComponent.STAMINA); }
    public int getCost(Entity entity, String resourceKey) {
        if (costExpression.isEmpty()) { return 0; }

        String[] costCoefficients = costExpression.split("\\+");

        StatisticsComponent stats = entity.get(StatisticsComponent.class);

        float total = 0;

        // If the value is a floating point, were using a percentage
        for (String costToken : costCoefficients) {
            // if the cost token is not related, skip
            if (!costToken.contains(resourceKey)) { continue; }
            // Since the cost token can be split into pieces
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
                        localTotal += stats.getBase(resource) * scalar;
                    } else {
                        localTotal += Float.parseFloat(value);
                    }
                }
                case "Modified" -> {
                    if (isPercentage) {
                        localTotal += stats.getModified(resource) * scalar;
                    }
                }
                case "Missing" -> {
                    if (isPercentage) {
                        localTotal += (stats.getTotal(resource) - stats.getCurrent(resource)) * scalar;
                    }
                }
                case "Current" -> {
                    if (isPercentage) {
                        localTotal += stats.getCurrent(resource) * scalar;
                    }
                }
                case "Total" -> {
                    if (isPercentage) {
                        localTotal += stats.getTotal(resource) * scalar;
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
