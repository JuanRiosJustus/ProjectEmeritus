package game.stores.pools.ability;

import java.util.Arrays;
import java.util.HashMap;
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
    public final String impact;
    public final Set<String> type;
    public final Map<String, Float> statusToTargets = new HashMap<>();
    public final Map<String, Float> statusToUser = new HashMap<>();

    public final int healthCostBase;
    public final Map<String, Float> healthCostScaling = new HashMap<>();
    public final Map<String, Float> healthCostPercent = new HashMap<>();

    public final int healthDamageBase;
    public final Map<String, Float> healthDamageScaling = new HashMap<>();
    public final Map<String, Float> healthDamagePercent = new HashMap<>();

    public final int energyCostBase;
    public final Map<String, Float> energyCostScaling = new HashMap<>();
    public final Map<String, Float> energyCostPercent = new HashMap<>();

    public final int energyDamageBase;
    public final Map<String, Float> energyDamageScaling = new HashMap<>();
    public final Map<String, Float> energyDamagePercent = new HashMap<>();

    public final Map<String, Map.Entry<Float, Float>> buffToUser = new HashMap<>();
    public final Map<String, Map.Entry<Float, Float>> buffToTargets = new HashMap<>();

    public Ability(Map<String, String> row) {
        name = row.get("name");
        description = row.get("description");
        accuracy = Float.parseFloat(row.get("accuracy"));
        range = Integer.parseInt(row.get("range"));
        area = Integer.parseInt(row.get("area"));
        type = new HashSet<>(Arrays.asList(row.get("types").split("\\|")));
        impact = row.get("impact");
        friendlyFire = Boolean.parseBoolean(row.get("can_friendly_fire"));

        healthCostBase = Integer.parseInt(row.get("health_cost_base"));
        setupScalingMap(row.get("health_cost_scaling"), healthCostScaling);
        setupScalingMap(row.get("health_cost_percent"), healthCostPercent);

        healthDamageBase = Integer.parseInt(row.get("health_damage_base"));
        setupScalingMap(row.get("health_damage_scaling"), healthDamageScaling);
        setupScalingMap(row.get("health_damage_percent"), healthDamagePercent);

        energyCostBase = Integer.parseInt(row.get("energy_cost_base"));
        setupScalingMap(row.get("energy_cost_scaling"), energyCostScaling);
        setupScalingMap(row.get("energy_cost_percent"), energyCostPercent);

        energyDamageBase = Integer.parseInt(row.get("energy_damage_base"));
        setupScalingMap(row.get("energy_damage_scaling"), energyDamageScaling);
        setupScalingMap(row.get("energy_damage_percent"), energyDamagePercent);

        setupScalingMap(row.get("status_to_user"), statusToUser);
        setupScalingMap(row.get("status_to_targets"), statusToTargets);

        setupSpecialBuffMap(row.get("buff_to_user"), buffToUser);
        setupSpecialBuffMap(row.get("buff_to_targets"), buffToTargets);

    }

    private void setupScalingMap(String column, Map<String, Float> toInsertInto) {
        if (column == null || column.isEmpty()) { return; }
        Arrays.stream(column.split("\\|"))
                .map(token -> token.split("="))
                .filter(token -> token.length > 1)
                .forEach(entry -> toInsertInto.put(entry[0], Float.valueOf(entry[1])));
    }

    private void setupSpecialBuffMap(String column, Map<String, Map.Entry<Float, Float>> toInsertInto) {
        if (column == null || column.isEmpty()) { return; }
        Arrays.stream(column.split("\\|"))
                .map(token -> token.split("="))
                .filter(token -> token.length > 1)
                .forEach(entry -> toInsertInto.put(entry[0],
                        Map.entry(Float.valueOf(entry[1]), Float.valueOf(entry[2]))));
    }

    public String toString() { return name; }
}
