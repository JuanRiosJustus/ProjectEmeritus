package game.stores.pools.ability;

import constants.Constants;
import logging.Logger;
import logging.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class AbilityPool {

    private final Map<String, Ability> map = new HashMap<>();
    public static final AbilityPool instance = new AbilityPool();

    private AbilityPool() {
        Logger logger = LoggerFactory.instance().logger(getClass());
        logger.banner("Started initializing " + getClass().getSimpleName());

        try {
            // Load all of the abilities from the CSV file
            Path fileName = Path.of(Constants.ABILITY_DATA_FILE);
            List<String> records = Files.readAllLines(fileName);
            logger.log("Loaded all abilities from {0}", fileName);

            String[] header = records.get(0).replaceAll("\\s", "").split(",");
            logger.log("Columns loaded from {0}", Arrays.toString(header));

            // Get the properties for each ability, skip first row because it's the header
            for (int row = 1; row < records.size(); row++) {

                Map<String, String> template = new HashMap<>();

                // Split the line by commas, if the
                String[] record = records.get(row).split(",");
                for (int column = 0; column < record.length; column++) {
                    String columnKey = header[column];
                    String columnValue = record[column];
                    columnValue = columnValue.trim();
                    if (columnValue.isBlank()) { columnValue = null; }
                    template.put(columnKey, columnValue);
                }

                Ability ability = new Ability(template);
                map.put(ability.name, ability);
//                logger.log("Finished loading {0}", template);
            }

        } catch (Exception e) {
            logger.log("Exception with ability Store - " + e.getMessage());
            System.err.println("Exception with Ability Store - " + e.getMessage());
            e.printStackTrace();
        }
        logger.banner("Finished initializing {0}", getClass().getSimpleName());
    }

//    public void sanitize(Map<String, String> map) throws Exception {
//
//        var name = IOSanitizer.parseString(map.get("Name"));
//        var description = IOSanitizer.parseString(map.get("Description"));
//        var accuracy = IOSanitizer.parseFloat(map.get("Accuracy"));
//        var range = IOSanitizer.parseInt(map.get("Range"));
//        var areaOfEffect = IOSanitizer.parseInt(map.get("AreaOfEffect"));
//        var types = new HashSet<>(Arrays.stream(IOSanitizer.parseString(map.get("Type")).split("\\s+")).toList());
//        var canHitUser = IOSanitizer.parseBoolean(map.get("CanHitUser"));
//        var defendingStat = IOSanitizer.parseString(map.get("DefendingStat"));
//
//        var baseHealthDamage = IOSanitizer.parseInt(map.get("BaseHealthDamage"));
//        var scalingHealthDamage = IOSanitizer.parseKeyValueMap(map.get("ScalingHealthDamage"));
//        var percentHealthDamage = IOSanitizer.parseKeyValueMap(map.get("PercentHealthDamage"));
//
//        var baseEnergyDamage = IOSanitizer.parseInt(map.get("BaseEnergyDamage"));
//        var scalingEnergyDamage = IOSanitizer.parseKeyValueMap(map.get("ScalingEnergyDamage"));
//        var percentEnergyDamage = IOSanitizer.parseKeyValueMap(map.get("PercentEnergyDamage"));
//
//        var statusToTargets = IOSanitizer.parseKeyValueMap(map.get("StatusToTargets"));
//        var statusToUser = IOSanitizer.parseKeyValueMap(map.get("StatusToUser"));
//
//        var baseEnergyCost = IOSanitizer.parseInt(map.get("BaseEnergyCost"));
//        var percentEnergyCost = IOSanitizer.parseKeyValueMap(map.get("PercentEnergyCost"));
//
//        var baseHealthCost = IOSanitizer.parseInt(map.get("BaseHealthCost"));
//        var percentHealthCost = IOSanitizer.parseKeyValueMap(map.get("PercentHealthCost"));
//
//        var buffOrDebuffsToUserChance = IOSanitizer.parseFloat(map.get("BuffOrDebuffsToUserChance"));
//        var buffOrDebuffsToUser = IOSanitizer.parseKeyValueMap(map.get("BuffOrDebuffsToUser"));
//
//        var buffOrDebuffsToTargetsChance = IOSanitizer.parseFloat(map.get("BuffOrDebuffsToTargetsChance"));
//        var buffOrDebuffsToTargets = IOSanitizer.parseKeyValueMap(map.get("BuffOrDebuffsToTargets"));
//
//        return new Ability(name, description, canHitUser, accuracy, range, areaOfEffect, types, defendingStat,
//                baseHealthDamage, scalingHealthDamage, percentHealthDamage, baseEnergyDamage, scalingEnergyDamage,
//                percentEnergyDamage, statusToTargets, statusToUser, baseEnergyCost, percentEnergyCost, baseHealthCost,
//                percentHealthCost, buffOrDebuffsToUserChance, buffOrDebuffsToUser, buffOrDebuffsToTargetsChance,
//                buffOrDebuffsToTargets);
//    }

    public static AbilityPool instance() { return instance; }

    public Ability getAbility(String name) {
//        Ability ability = map.get(name);
//        if (ability == null) {
//            System.err.println("Unable to parse " + name);
//        }
//        return ability;
        return map.get(name);
    }
}
