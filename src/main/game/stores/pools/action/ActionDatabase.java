package main.game.stores.pools.action;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import main.constants.Constants;
import main.constants.Quadruple;
import main.constants.Tuple;
import main.game.components.StatisticsComponent;
import main.game.entity.Entity;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.MathUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class ActionDatabase {
    private static ActionDatabase instance = null;
    private final Map<String, JSONObject> mActionsMap = new HashMap<>();
    private final Map<String, Float> mDebugMap = new HashMap<>();
    private static final String COST_KEY = "cost";
    private static final String DAMAGE_KEY = "damage";

    public static ActionDatabase getInstance() {
        if (instance == null) {
            instance = new ActionDatabase();
        }
        return instance;
    }

    private ActionDatabase() {
        ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        try {
            JSONArray actions = new JSONArray(Files.readString(Path.of(Constants.ACTION_DATABASE)));
            for (int index = 0; index < actions.length(); index++) {
                JSONObject unit = actions.getJSONObject(index);
                mActionsMap.put(unit.getString("action"), unit);
            }
            logger.info("Successfully initialized {}", getClass().getSimpleName());
        } catch (Exception ex) {
            logger.info("Error parsing ability: " + ex.getMessage());
            System.exit(-1);
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public boolean execute(String ability, Entity caster, List<Entity> targets) {
        JSONObject data = mActionsMap.get(ability);
        for (Entity target : targets) {

        }
        return false;
    }

    public int getArea(String action) {
        JSONObject data = mActionsMap.get(action);
        int result = data.getInt("area");
        return result;
    }

    public int getRange(String action) {
        JSONObject data = mActionsMap.get(action);
        int result = data.getInt("range");
        return result;
    }

    public int getAccuracy(String action) {
        JSONObject data = mActionsMap.get(action);
        int result = data.getInt("accuracy");
        return result;
    }


    public Map<String, Float> getDamageV2(Entity actorUnitEntity, String action, Entity actedOnUnitEntity) {
//        JSONObject data = mActionsMap.get(action);
//        var rrrrrrrrr = data.toMap()
//                .entrySet()
//                .stream()
//                .filter(e -> e.getKey().contains(DAMAGE_KEY))
//                .map(e -> Map.Entry(e.getKey().substring(0, e.getKey().indexOf(".")), e.getValue()))
//                .collect(Collectors.toSet())
        return null;
    }

//    public List<String> getResourcesToDamage(String action) {
//        JSONObject data = mActionsMap.get(action);
//        List<String> rrrrrr = data.toMap()
//                .entrySet()
//                .stream()
//                .filter(e -> e.getKey().contains(DAMAGE_KEY))
//                .toList()
//                .stream().map(e -> e.getKey().substring(0, e.getKey().indexOf(".")))
//                .toList();
//        return null;
//    }

    public Map<String, Float> getDamage(Entity actorUnitEntity, String action, Entity actedOnUnitEntity) {
        JSONObject data = mActionsMap.get(action);


//        return null;
//        CsvRow actionRow = ActionPool.getInstance().getAction(action);
//        Map<String, Float> resultMap = new HashMap<>();
//        Map<String, Float> damageMap = actionRow.getNumberMap("Damage_Formula");
//        for (Map.Entry<String, Float> entry : damageMap.entrySet()) {
//            String[] data = entry.getKey().split(" ");
//            if (data.length != 4) { continue; }
//            String target = data[0];
//            String modifier = data[1];
//            String attribute = data[2];
//            String resource = data[3];
//            Float value = entry.getValue();
//            int damage = getValue(
//                    actorUnitEntity,
//                    actedOnUnitEntity,
//                    target,
//                    modifier,
//                    attribute,
//                    value
//            );
//            resultMap.put(resource, resultMap.getOrDefault(resource, 0f) + damage);
//        }
//        return resultMap;
        return null;
    }


    public List<Map.Entry<String, String>> getResourceToCostScaling(String action) {
        JSONObject data = mActionsMap.get(action);
        List<Map.Entry<String, String>> list = data.toMap()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().contains(COST_KEY))
                .filter(e -> e.getKey().contains(BASE_KEY) ||
                        e.getKey().contains(TOTAL_PERCENTAGE_KEY) ||
                        e.getKey().contains(CURRENT_PERCENTAGE_KEY) ||
                        e.getKey().contains(MISSING_PERCENTAGE_KEY))
                .filter(e -> e.getKey().contains(HEALTH_KEY) ||
                        e.getKey().contains(STAMINA_KEY) ||
                        e.getKey().contains(MANA_KEY))
                .map(e -> {
                    String resourceName = null;
                    if (e.getKey().startsWith(HEALTH_KEY)) {
                        resourceName = HEALTH_KEY;
                    } else if (e.getKey().startsWith(MANA_KEY)) {
                        resourceName = MANA_KEY;
                    } else if (e.getKey().startsWith(STAMINA_KEY)) {
                        resourceName = STAMINA_KEY;
                    }

                    String costMultiplier = null;
                    if (e.getKey().contains(BASE_KEY)) {
                        costMultiplier = BASE_KEY;
                    } else if (e.getKey().contains(TOTAL_PERCENTAGE_KEY)) {
                        costMultiplier = TOTAL_PERCENTAGE_KEY;
                    } else if (e.getKey().contains(MISSING_PERCENTAGE_KEY)) {
                        costMultiplier = MISSING_PERCENTAGE_KEY;
                    } else if (e.getKey().contains(CURRENT_PERCENTAGE_KEY)) {
                        costMultiplier = CURRENT_PERCENTAGE_KEY;
                    }

                    Map.Entry<String, String> resourceToCostMultiplier = null;
                    if (resourceName != null && costMultiplier != null) {
                        resourceToCostMultiplier = Map.entry(resourceName, costMultiplier);
                    }
                    return resourceToCostMultiplier;
                })
                .filter(Objects::nonNull)
                .toList();
        return list;
    }


//    public
//    public List<Tuple<String, String, Float>> getResourceCosts(String action) {
//        JSONObject data = mActionsMap.get(action);
//        List<Tuple<String, String, Float>> list = data.toMap()
//                .entrySet()
//                .stream()
//                .filter(e -> e.getKey().contains(COST_KEY))
//                .filter(e -> e.getKey().contains(BASE_KEY) ||
//                        e.getKey().contains(TOTAL_PERCENTAGE_KEY) ||
//                        e.getKey().contains(CURRENT_PERCENTAGE_KEY) ||
//                        e.getKey().contains(MISSING_PERCENTAGE_KEY))
//                .filter(e -> e.getKey().contains(HEALTH_KEY) ||
//                        e.getKey().contains(STAMINA_KEY) ||
//                        e.getKey().contains(MANA_KEY))
//                .map(e -> {
//                    String resource = null;
//                    if (e.getKey().startsWith(HEALTH_KEY)) {
//                        resource = HEALTH_KEY;
//                    } else if (e.getKey().startsWith(MANA_KEY)) {
//                        resource = MANA_KEY;
//                    } else if (e.getKey().startsWith(STAMINA_KEY)) {
//                        resource = STAMINA_KEY;
//                    }
//
//                    String multiplier = null;
//                    if (e.getKey().contains(BASE_KEY)) {
//                        multiplier = BASE_KEY;
//                    } else if (e.getKey().contains(TOTAL_PERCENTAGE_KEY)) {
//                        multiplier = TOTAL_PERCENTAGE_KEY;
//                    } else if (e.getKey().contains(MISSING_PERCENTAGE_KEY)) {
//                        multiplier = MISSING_PERCENTAGE_KEY;
//                    } else if (e.getKey().contains(CURRENT_PERCENTAGE_KEY)) {
//                        multiplier = CURRENT_PERCENTAGE_KEY;
//                    }
//
//                    float value = data.getFloat(e.getKey());
//
//                    Tuple<String, String, Float> tuple = null;
//                    if (resource != null && multiplier != null && value != 0) {
//                        tuple = new Tuple<>(resource, multiplier, value);
//                    }
//
//                    return tuple;
//                })
//                .filter(Objects::nonNull)
//                .toList();
//        return list;
//    }
//    public Map<String, Float> getResourceCosts(Entity unitEntity, String action) {
//        JSONObject data = mActionsMap.get(action);
//        Map<String, Float> costMap = new HashMap<>();
//        Map<String, Float> costKeys = actionRow.getNumberMap("Cost_Formula");
//        for (Map.Entry<String, Float> entry : costKeys.entrySet()) {
//            String[] key = entry.getKey().split(" ");
//            Float value = entry.getValue();
//            String calculation = key[0];
//            String attribute = key[1];
//            float cost = getValue(unitEntity, null, "Self", calculation, attribute, value);
//            costMap.put(attribute, cost);
//        }
//        return costMap;
//    }

    private int getValue(Entity actor, Entity acted, String target, String calculation, String attribute, float value) {
        StatisticsComponent stats = null;
        float total = 0;
        if (target.contains("Self")) {
            stats = actor.get(StatisticsComponent.class);
        } else if (target.contains("Other")) {
            stats = acted.get(StatisticsComponent.class);
        }

        if (stats != null) {
            switch (calculation) {
                case "Flat" ->  total = value;
                case "Base" ->  total = stats.getBase(attribute) * value;
                case "Modified" -> total = stats.getModified(attribute) * value;
                case "Total", "Percent_Max" -> total = stats.getTotal(attribute) * value;
                case "Percent_Missing" -> total = (stats.getTotal(attribute) - stats.getCurrent(attribute)) * value;
                case "Percent_Current" -> total = stats.getCurrent(attribute) * value;
            }
        }

        return (int) total;
    }
    public boolean isSuccessful(String action) {
        float successChance = ActionDatabase.getInstance().getAccuracy(action);
        return MathUtils.passesChanceOutOf100(successChance);
    }

    public boolean shouldUsePhysicalDefense(String action) {
        int range = ActionDatabase.instance.getRange(action);
        return range <= 1;
    }

    public boolean hasSameTypeAttackBonus(Entity actorUnitEntity, String action) {
        return false;
//        CsvRow actionRow = ActionPool.getInstance().getAction(action);
//        return !Collections.disjoint(
//                actorUnitEntity.get(StatisticsComponent.class).getType(),
//                actionRow.getList("Type"));
    }

//    public boolean isDamagingAbility(String action) {
//        CsvRow actionRow = ActionPool.getInstance().getAction(action);
//        Map<String, Float> damageMap = actionRow.getNumberMap("Damage_Formula");
//        for (Map.Entry<String, Float> entry : damageMap.entrySet()) {
//            if (entry.getValue() > 0) { return true; }
//        }
//        return false;
//    }
    private static final String HEALTH_KEY = "health";
    private static final String MANA_KEY = "mana";
    private static final String STAMINA_KEY = "stamina";
    private static final String BASE_KEY = "base";
    private static final String TOTAL_PERCENTAGE_KEY = "total_percentage";
    private static final String CURRENT_PERCENTAGE_KEY = "current_percentage";
    private static final String MISSING_PERCENTAGE_KEY = "missing_percentage";


    public List<Quadruple<String, String, String, Float>> getDamageScaling(String action) {
        JSONObject data = mActionsMap.get(action);
        List<Quadruple<String, String, String, Float>> entry = data.toMap()
                .entrySet()
                .stream()
                .filter(o -> o.getKey().contains(DAMAGE_KEY))
                .map(e -> {
                    String key = e.getKey();
                    String damageTo = key.substring(0, key.indexOf("."));

                    String scalingType = key.substring(key.lastIndexOf(".") + 1);
                    key = key.substring(0, key.lastIndexOf("."));
                    String scalingStat = key.substring(key.lastIndexOf(".") + 1);
                    Float value = Float.valueOf(e.getValue().toString());
                    return new Quadruple<>(damageTo, scalingStat, scalingType, value);
                })
                .toList();
        return entry;
    }

    public List<Tuple<String, String, Float>> getDamageAttributeMagnitudeAndValue(String action, String resource) {
        JSONObject data = mActionsMap.get(action);
        List<Tuple<String, String, Float>> result = data.toMap()
                .entrySet()
                .stream()
                // Validate the key format "{RESOURCE}.damage.Base|{ScalingAttribute.{Base|Modified|Total}}
                .filter(e -> e.getKey().startsWith(resource))
                .filter(e -> e.getKey().contains(DAMAGE_KEY))
                // Get the scaling value for the action
                .map(e -> {
                    String key = e.getKey();
                    String scalingAttribute = key.substring(key.lastIndexOf(".") + 1);
                    key = key.substring(0, key.lastIndexOf("."));
                    String scalingMagnitude = key.substring(key.lastIndexOf(".") + 1);
                    if (scalingAttribute.equalsIgnoreCase(BASE_KEY)) {
                        scalingMagnitude = null;
                    }
                    Float value = Float.valueOf(e.getValue().toString());
                    return new Tuple<>(scalingAttribute, scalingMagnitude, value);
                })
                .toList();
        return result;
    }

    public List<Map.Entry<String, String>> getDamageMapping(String action, String targetResource, String scaling) {
        JSONObject data = mActionsMap.get(action);
        List<Map.Entry<String, String>> list = data.toMap()
                .keySet()
                .stream()
                .filter(o -> o.startsWith(targetResource)) // Find the resource this ability is damaging
                .filter(o -> o.contains(DAMAGE_KEY))
                .filter(e -> e.endsWith(scaling))
                .map(key -> {

                    String scalingType = key.substring(key.lastIndexOf(".") + 1);
                    key = key.substring(0, key.lastIndexOf("."));
                    String scalingStat = key.substring(key.lastIndexOf(".") + 1);
                    Map.Entry<String, String> entry = Map.entry(scalingType, scalingStat);

                    return entry;

                })
                .toList();

        return list;
    }

    public int getBaseCost(String action, String resource, int current, int total) {
        int resourceCost = getResourceCost(action, resource, BASE_KEY, current, total);
        return resourceCost;
    }

    public int getTotalPercentageCost(String action, String resource, int current, int total) {
        int resourceCost = getResourceCost(action, resource, TOTAL_PERCENTAGE_KEY, current, total);
        return resourceCost;
    }

    public int getCurrentPercentageCost(String action, String resource, int current, int total) {
        int resourceCost = getResourceCost(action, resource, CURRENT_PERCENTAGE_KEY, current, total);
        return resourceCost;
    }

    public int getMissingPercentageCost(String action, String resource, int current, int total) {
        int resourceCost = getResourceCost(action, resource, MISSING_PERCENTAGE_KEY, current, total);
        return resourceCost;
    }

    public Set<String> getResourcesToUse(String action) {
        JSONObject data = mActionsMap.get(action);
        Set<String> resources = data.toMap()
                .keySet()
                .stream()
                .filter(e -> e.contains(COST_KEY))
                .map(e -> e.substring(0, e.indexOf(".")))
                .collect(Collectors.toSet());
        return resources;
    }

    public Set<String> getResourcesToDamage(String action) {
        JSONObject data = mActionsMap.get(action);
        Set<String> result = data.toMap()
                .keySet()
                .stream()
                .filter(e -> e.contains(DAMAGE_KEY))
                .map(e -> e.substring(0, e.indexOf(".")))
                .collect(Collectors.toSet());
        return result;
    }

    public Map<String, Object> getDamageMap(String action) {
        JSONObject data = mActionsMap.get(action);
        Map<String, Object> resources = data.toMap()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().contains(DAMAGE_KEY))
                .map(e -> Map.entry(e.getKey().substring(0, e.getKey().indexOf(".")), e.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return null;
    }
    public int getResourceCost(String action, String resource, String scalingType, int current, int total) {
        JSONObject actionData = mActionsMap.get(action);
        Map.Entry<String, Object> node = getResourceCostNode(action, resource, scalingType);
        float result = 0;
        if (node != null) {
            float value = actionData.getFloat(node.getKey());
            result = getValueFromScalingType(scalingType, value, current, total);
        }
        return (int) result;
    }

    public int getResourceDamage(String action, String damaging, String scalingType, int current, int total) {
        JSONObject actionData = mActionsMap.get(action);
//        Map.Entry<String, Object> node = getResourceCostNode(action, resource, scalingType);
        float result = 0;
//        if (node != null) {
////            float value = actionData.getFloat(node.getKey());
////            result = getValueFromScalingType(scalingType, value, current, total);
//        }
        return (int) result;
    }

    private float getValueFromScalingType(String scalingType, float cost, int current, int total) {
        float result = 0;
        switch (scalingType) {
            case BASE_KEY -> { result = cost; }
            case TOTAL_PERCENTAGE_KEY -> { result = total * cost; }
            case CURRENT_PERCENTAGE_KEY -> { result = current * cost; }
            case MISSING_PERCENTAGE_KEY -> { result = (total - current) * cost; }
            default -> {}
        }

        return result;
    }

    private Map.Entry<String, Object> getResourceCostNode(String action, String resource, String mod) {
        JSONObject data = mActionsMap.get(action);
        Map.Entry<String, Object> entry = data.toMap()
                .entrySet()
                .stream()
                // Validate the node is the correct resource, is a cost, and modifier
                .filter(e -> e.getKey().contains(resource))
                .filter(e -> e.getKey().contains(COST_KEY))
                .filter(e -> e.getKey().contains(mod))
                .findFirst()
                .orElse(null);

        return entry;
    }

    public boolean isDamagingAbility(String action) {
        JSONObject data = mActionsMap.get(action);
        double damage = data.toMap()
                .keySet()
                .stream()
                .filter(o -> o.contains(DAMAGE_KEY))
                .mapToDouble(o -> {
                    double value = data.getDouble(o);
                    return value;
                }).sum();
        return damage > 0;
    }
}
