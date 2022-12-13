package game.stores.pools.ability;

import utils.IOSanitizer;

import java.util.*;

public class Ability {

    public final String name;
    public final String description;
    public final boolean canHitUser;
    public final float accuracy;
    public final int range;
    public final int areaOfEffect;
    public final Set<String> types;

    public final Map<String, Float> defendingStats;

    public final int baseEnergyCost;
    public final Map<String, Float> percentEnergyCost;

    public final int baseHealthCost;
    public final Map<String, Float> percentHealthCost;

    public final int baseHealthDamage;
    public final Map<String, Float> scalingHealthDamage;
    public final Map<String, Float> percentHealthDamage;

    public final int baseEnergyDamage;
    public final Map<String, Float> scalingEnergyDamage;
    public final Map<String, Float> percentEnergyDamage;

    public final Map<String, Float> statusToTargets;
    public final Map<String, Float> statusToUser;

    public final float buffsToUserChance;
    public final Map<String, Float> buffsToUser;

    public final float buffsToTargetsChance;
    public final Map<String, Float> buffsToTargets;

    public Ability(Map<String, String> map) throws Exception {
        name = IOSanitizer.parseString(map.get("Name"));
        description = IOSanitizer.parseString(map.get("Description"));
        accuracy = IOSanitizer.parseFloat(map.get("Accuracy"));
        range = IOSanitizer.parseInt(map.get("Range"));
        areaOfEffect = IOSanitizer.parseInt(map.get("AreaOfEffect"));
        types = new HashSet<>(Arrays.stream(IOSanitizer.parseString(map.get("Type")).split("\\s+")).toList());
        canHitUser = IOSanitizer.parseBoolean(map.get("CanHitUser"));

        defendingStats = IOSanitizer.parseKeyValueMap(map.get("DefendingStats"));

        baseHealthDamage = IOSanitizer.parseInt(map.get("BaseHealthDamage"));
        scalingHealthDamage = IOSanitizer.parseKeyValueMap(map.get("ScalingHealthDamage"));
        percentHealthDamage = IOSanitizer.parseKeyValueMap(map.get("PercentHealthDamage"));

        baseEnergyDamage = IOSanitizer.parseInt(map.get("BaseEnergyDamage"));
        scalingEnergyDamage = IOSanitizer.parseKeyValueMap(map.get("ScalingEnergyDamage"));
        percentEnergyDamage = IOSanitizer.parseKeyValueMap(map.get("PercentEnergyDamage"));

        statusToTargets = IOSanitizer.parseKeyValueMap(map.get("StatusToTargets"));
        statusToUser = IOSanitizer.parseKeyValueMap(map.get("StatusToUser"));

        baseEnergyCost = IOSanitizer.parseInt(map.get("BaseEnergyCost"));
        percentEnergyCost = IOSanitizer.parseKeyValueMap(map.get("PercentEnergyCost"));

        baseHealthCost = IOSanitizer.parseInt(map.get("BaseHealthCost"));
        percentHealthCost = IOSanitizer.parseKeyValueMap(map.get("PercentHealthCost"));

        buffsToUserChance = IOSanitizer.parseFloat(map.get("BuffsToUserChance"));
        buffsToUser = IOSanitizer.parseKeyValueMap(map.get("BuffsToUser"));

        buffsToTargetsChance = IOSanitizer.parseFloat(map.get("BuffsToTargetsChance"));
        buffsToTargets = IOSanitizer.parseKeyValueMap(map.get("BuffsToTargets"));
    }

    public String toString() { return name; }
}
