package main.game.stores.pools.action;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import main.constants.Constants;
import main.constants.Tuple;
import main.game.entity.Entity;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.MathUtils;
import main.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class ActionDatabase {
    private static ActionDatabase instance = null;
    private final Map<String, JSONObject> mActionsMap = new HashMap<>();
    private final Map<String, Float> mDebugMap = new HashMap<>();
    private static final String COST_KEY = "cost";
    private static final String DAMAGE_KEY = "damage";
    private static final String DAMAGE_FROM_USER_KEY = "damage_from_user";
    private static final String COST_FROM_USER_KEY = "cost_from_user";
    private static final String DELIMITER = "_";

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

    public int getTimesToTrigger(String action) {
        JSONObject data = mActionsMap.get(action);
        int result = data.getInt("times_to_trigger");
        return result;
    }
//    times_to_trigger

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

    public Set<String> getResourcesToCost(String action) {
        JSONObject data = mActionsMap.get(action);
        Set<String> resources = data.toMap()
                .keySet()
                .stream()
                .filter(o -> o.contains(COST_KEY))
                .map(o -> o.substring(0, o.indexOf(DELIMITER)))
                .collect(Collectors.toSet());
        return resources;
    }

    public boolean isSuccessful(String action) {
        float successChance = ActionDatabase.getInstance().getAccuracy(action);
        return MathUtils.passesChanceOutOf100(successChance);
    }

    public List<String> getType(String action) {
        JSONObject data = mActionsMap.get(action);
        List<String> result = new ArrayList<>();
        data.getJSONArray("type").forEach(e -> result.add(e.toString()));
        return result;
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
    private static final String MODIFIED_KEY = "modified";
    private static final String TOTAL_KEY = "total";
    private static final String CURRENT_KEY = "current";
    private static final String MISSING_KEY = "missing";
    private static final String MAX_KEY = "max";
    private static final String TOTAL_PERCENTAGE_KEY = "total_percentage";
    private static final String CURRENT_PERCENTAGE_KEY = "current_percentage";
    private static final String MISSING_PERCENTAGE_KEY = "missing_percentage";

    public int getBaseCost(String action, String resource) {
        JSONObject data = mActionsMap.get(action);
        int result = 0;
        Map.Entry<String, Integer> base = data.toMap()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(resource))
                .filter(e -> e.getKey().contains(COST_KEY))
                .filter(e -> e.getKey().endsWith(BASE_KEY))
                .map(e -> Map.entry(e.getKey(), Integer.parseInt(e.getValue().toString())))
                .findFirst()
                .orElse(null);

        if (base != null) { result = base.getValue(); }

        return result;
    }

    public List<Tuple<String, String, Float>> getScalingCostFromUser(String action, String resource) {
        JSONObject data = mActionsMap.get(action);
        List<Tuple<String, String, Float>> result = data.toMap()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(resource))
                .filter(e -> e.getKey().contains(COST_FROM_USER_KEY))
                .filter(e -> !e.getKey().endsWith(BASE_KEY))
                .filter(e -> {
                    // For all stat nodes
                    boolean usesBase = e.getKey().contains(BASE_KEY);
                    boolean usesModified = e.getKey().contains(MODIFIED_KEY);
                    boolean usesTotal = e.getKey().contains(TOTAL_KEY);
                    // For resource nodes
                    boolean usesCurrent = e.getKey().contains(CURRENT_KEY);
                    boolean usesMissing = e.getKey().contains(MISSING_KEY);
                    boolean usesMax = e.getKey().contains(MAX_KEY);
                    return usesBase || usesModified || usesTotal || usesCurrent || usesMissing || usesMax;
                })
                // Get the scaling value for the action
                .map(e -> {

                    int scalingStart = e.getKey().indexOf(COST_FROM_USER_KEY) + COST_FROM_USER_KEY.length() + 1;
                    String scalingData = e.getKey().substring(scalingStart);
                    String magnitude = scalingData.substring(0, scalingData.indexOf(DELIMITER));
                    String attribute = scalingData.substring(scalingData.indexOf(DELIMITER) + 1);
                    Float value = Float.valueOf(e.getValue().toString());

                    return new Tuple<>(magnitude, attribute, value);
                })
                .toList();
        return result;
    }

    public int getBaseDamage(String action, String resource) {
        JSONObject data = mActionsMap.get(action);
        int result = 0;
        Map.Entry<String, Integer> base = data.toMap()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(resource))
                .filter(e -> e.getKey().contains(DAMAGE_KEY))
                .filter(e -> e.getKey().endsWith(BASE_KEY))
                .map(e -> Map.entry(e.getKey(), Integer.parseInt(e.getValue().toString())))
                .findFirst()
                .orElse(null);

        if (base != null) { result = base.getValue(); }

        return result;
    }

    public List<Tuple<String, String, Float>> getScalingDamageFromUser(String action, String resourceToDamage) {
        JSONObject data = mActionsMap.get(action);
        List<Tuple<String, String, Float>> result = data.toMap()
                .entrySet()
                .stream()
                // Validate the key format "{RESOURCE}.damage.Base|{ScalingAttribute.{Base|Modified|Total}}
                .filter(e -> e.getKey().startsWith(resourceToDamage))
                .filter(e -> e.getKey().contains(DAMAGE_FROM_USER_KEY))
                .filter(e -> !e.getKey().endsWith(BASE_KEY))
                .filter(e -> {
                    // For all stat nodes
                    boolean usesBase = e.getKey().contains(BASE_KEY);
                    boolean usesModified = e.getKey().contains(MODIFIED_KEY);
                    boolean usesTotal = e.getKey().contains(TOTAL_KEY);
                    // For resource nodes
                    boolean usesCurrent = e.getKey().contains(CURRENT_KEY);
                    boolean usesMissing = e.getKey().contains(MISSING_KEY);
                    boolean usesMax = e.getKey().contains(MAX_KEY);
                    return usesBase || usesModified || usesTotal || usesCurrent || usesMissing || usesMax;
                })
                // Get the scaling value for the action
                .map(e -> {

                    int scalingStart = e.getKey().indexOf(DAMAGE_FROM_USER_KEY) + DAMAGE_FROM_USER_KEY.length() + 1;
                    String scalingData = e.getKey().substring(scalingStart);
                    String magnitude = scalingData.substring(0, scalingData.indexOf(DELIMITER));
                    String attribute = scalingData.substring(scalingData.indexOf(DELIMITER) + 1);
                    Float value = Float.valueOf(e.getValue().toString());

                    return new Tuple<>(magnitude, attribute, value);
                })
                .toList();
        return result;
    }


    public Set<String> getResourcesToDamage(String action) {
        JSONObject data = mActionsMap.get(action);
        Set<String> result = data.toMap()
                .keySet()
                .stream()
                .filter(e -> e.contains(DAMAGE_KEY))
                .map(e -> e.substring(0, e.indexOf(DELIMITER)))
                .collect(Collectors.toSet());
        return result;
    }

    public boolean isDamagingAbility(String action) {
        JSONObject data = mActionsMap.get(action);
        if (data == null) { return false; }
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


    public String getResourceCalculations(String action) {
        StringBuilder sb = new StringBuilder();
        Set<String> resources = getResourcesToCost(action);
        for (String resource : resources) {
            int baseCost = getBaseCost(action, resource);

            sb.append(baseCost).append(" (Base Cost)");

            List<Tuple<String, String, Float>> scalings = getScalingCostFromUser(action, resource);
            for (Tuple<String, String, Float> scaling : scalings) {
                String magnitude = scaling.getFirst();
                String attribute = scaling.getSecond();
                Float value = scaling.getThird();

                String prettyMagnitude = StringUtils.convertSnakeCaseToCapitalized(magnitude);
                String prettyAttribute = StringUtils.convertSnakeCaseToCapitalized(attribute);
                String prettyPercent = StringUtils.floatToPercentage(value);

                sb.append("\n+")
                        .append(prettyPercent)
                        .append(" ")
                        .append(prettyMagnitude)
                        .append(" ")
                        .append(prettyAttribute);
            }
        }
        return sb.toString();
    }

    public String getDamageCalculations(String action) {
        StringBuilder sb = new StringBuilder();
        Set<String> resources = getResourcesToDamage(action);
        for (String resource : resources) {
            int baseDamage = getBaseDamage(action, resource);

            sb.append(baseDamage).append(" (Base Damage)");

            List<Tuple<String, String, Float>> scalings = getScalingDamageFromUser(action, resource);
            for (Tuple<String, String, Float> scaling : scalings) {
                String magnitude = scaling.getFirst();
                String attribute = scaling.getSecond();
                Float value = scaling.getThird();

                String prettyMagnitude = StringUtils.convertSnakeCaseToCapitalized(magnitude);
                String prettyAttribute = StringUtils.convertSnakeCaseToCapitalized(attribute);
                String prettyPercent = StringUtils.floatToPercentage(value);

                sb.append("\n+")
                        .append(prettyPercent)
                        .append(" ")
                        .append(prettyMagnitude)
                        .append(" ")
                        .append(prettyAttribute);
            }

        }
        return sb.toString();
    }

    public boolean getMakesPhysicalContact(String action) {
        JSONObject data = mActionsMap.get(action);
        boolean makesContact = data.getBoolean("makes_physical_contact");
        return makesContact;
    }

    public String getDescription(String action) {
        JSONObject data = mActionsMap.get(action);
        String description = data.getString("description");
        return description;
    }
}
