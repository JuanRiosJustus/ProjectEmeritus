package main.game.stores.pools.action;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import main.constants.Constants;
import main.constants.Tuple;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.MathUtils;
import main.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class ActionDatabase {
    private static ActionDatabase instance = null;
    private final Map<String, JSONObject> mActionsMap = new HashMap<>();
    private final Map<String, Action> mActionsMapsV2 = new LinkedHashMap<>();
    private final Map<String, Float> mDebugMap = new HashMap<>();
    private static final String COST_KEY = "cost";
    private static final String DAMAGE_KEY = "damage";
    private static final String DAMAGE_FROM_USER_KEY = "damage_from_user";
    private static final String DAMAGE_FORMULA = "damage_formula";
    private static final String COST_FORMULA = "cost_formula";
//    private static final String HEALTH_DAMAGE_FORMULA = "_damage_formula";
    private static final String COST_FROM_USER_KEY = "cost_from_user";
    private static final String UNDERSCORE_DELIMITER = "_";
    private static final String EQUAL_DELIMITER = "=";

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
                JSONObject action = actions.getJSONObject(index);
                mActionsMap.put(action.getString("action"), action);

                mActionsMapsV2.put(action.getString("action"), new Action(action));
            }
            logger.info("Successfully initialized {}", getClass().getSimpleName());
        } catch (Exception ex) {
            logger.info("Error parsing ability: " + ex.getMessage());
            logger.info("Example: " + ex.getMessage());
            ex.printStackTrace();
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
        if (data == null) {
            System.out.println("teoe");
        }
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

    public Map<String, Float> getResourceDamage(Entity actorUnitEntity, String action, Entity actedOnUnitEntity) {
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
                .entrySet()
                .stream()
                .filter(e -> e.getKey().endsWith(COST_FORMULA))
                .filter(e -> e.getValue() != null)
                .map(e -> e.getKey().substring(0, e.getKey().indexOf(COST_FORMULA) - 1))
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

    private static final String HEALTH_KEY = "health";
    private static final String MANA_KEY = "mana";
    private static final String STAMINA_KEY = "stamina";
    private static final String BASE_KEY = "base";
    private static final String SCALING_KEY = "scaling";

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


    public List<Tuple<String, String, Float>> getScalingCostFromUser(String action, String targetResource) {

        JSONObject data = mActionsMap.get(action);
        JSONObject scalings = data.toMap()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(targetResource))
                .filter(e -> e.getKey().endsWith(COST_FORMULA))
                .filter(e -> e.getValue() != null)
                .filter(e -> !e.getValue().toString().isEmpty())
                .map(e -> {
                    return new JSONObject(String.valueOf(e.getValue()));
                })
                .filter(e -> !e.isEmpty())
                .findFirst()
                .orElse(null);

        List<Tuple<String, String, Float>> results = new ArrayList<>();

        if (scalings != null) {
            for (String key : scalings.keySet()) {
                Tuple<String, String, Float> result = null;
                float value = scalings.getFloat(key);
                if (key.startsWith(BASE_KEY) && key.endsWith(BASE_KEY)) {
                    result = new Tuple<>(key, null, value);
                } else {
                    String magnitude = key.substring(0, key.indexOf(UNDERSCORE_DELIMITER));
                    String attribute = key.substring(key.indexOf(UNDERSCORE_DELIMITER) + 1);
                    result = new Tuple<>(magnitude, attribute, value);
                }
                results.add(result);
            }
        }
        return results;


//        JSONObject data = mActionsMap.get(action);
//        List<Tuple<String, String, Float>> result = data.toMap()
//                .entrySet()
//                .stream()
//                .filter(e -> e.getKey().startsWith(resource))
//                .filter(e -> e.getKey().contains(COST_FROM_USER_KEY))
//                .filter(e -> {
//                    // For all stat nodes
//                    boolean usesBase = e.getKey().contains(BASE_KEY);
//                    boolean usesModified = e.getKey().contains(MODIFIED_KEY);
//                    boolean usesTotal = e.getKey().contains(TOTAL_KEY);
//                    // For resource nodes
//                    boolean usesCurrent = e.getKey().contains(CURRENT_KEY);
//                    boolean usesMissing = e.getKey().contains(MISSING_KEY);
//                    boolean usesMax = e.getKey().contains(MAX_KEY);
//                    return usesBase || usesModified || usesTotal || usesCurrent || usesMissing || usesMax;
//                })
//                // Get the scaling value for the action
//                .map(e -> {
//
//                    int scalingStart = e.getKey().indexOf(COST_FROM_USER_KEY) + COST_FROM_USER_KEY.length() + 1;
//                    String scalingData = e.getKey().substring(scalingStart);
//                    String magnitude = scalingData.substring(0, scalingData.indexOf(UNDERSCORE_DELIMITER));
//                    String attribute = scalingData.substring(scalingData.indexOf(UNDERSCORE_DELIMITER) + 1);
//                    Float value = Float.valueOf(e.getValue().toString());
//
//                    return new Tuple<>(magnitude, attribute, value);
//                })
//                .toList();
//        return result;
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

    public String getCostFormula(String action, String resource) {
        JSONObject data = mActionsMap.get(action);
        Map.Entry<String, String> entry = data.toMap()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(resource))
                .filter(e -> e.getKey().endsWith(COST_FORMULA))
                .map(e -> Map.entry(e.getKey(), String.valueOf(e.getValue())))
                .findFirst()
                .orElse(null);

        String result = "None";
        if (entry != null) {
            result = entry.getValue();
        }
        return result;
    }

    public String getPrettyCostFormula(String action, String resource) {
        String costFormula = getCostFormula(action, resource);

        StringBuilder sb = new StringBuilder();
        String[] costFormulaNodes = costFormula.split(",");
        for (String costFormulaNode : costFormulaNodes) {
            String value = costFormulaNode.substring(costFormulaNode.indexOf(EQUAL_DELIMITER) + 1);
            costFormulaNode = costFormulaNode.substring(0, costFormulaNode.indexOf(EQUAL_DELIMITER));

            if (!sb.isEmpty()) { sb.append(System.lineSeparator()); }

            if (costFormulaNode.contains(BASE_KEY)) {
                sb.append(value).append(" Base");
            } else {
                String magnitude = costFormulaNode.substring(0, costFormulaNode.indexOf(UNDERSCORE_DELIMITER));
                costFormulaNode = costFormulaNode.substring(costFormulaNode.indexOf(UNDERSCORE_DELIMITER) + 1);
                String attribute = costFormulaNode;

                String percent = StringUtils.floatToPercentage(Float.parseFloat(value));
                String prettyMagnitude = StringUtils.convertSnakeCaseToCapitalized(magnitude);
                String prettyAttribute = StringUtils.convertSnakeCaseToCapitalized(attribute);
                sb.append(percent)
                        .append(" ")
                        .append(prettyMagnitude)
                        .append(" ")
                        .append(prettyAttribute);
            }
        }

        return sb.toString();
    }


    public String getDamageFormula(String action, String resource) {
        JSONObject data = mActionsMap.get(action);
        Map.Entry<String, String> entry = data.toMap()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(resource))
                .filter(e -> e.getKey().endsWith(DAMAGE_FORMULA))
                .map(e -> Map.entry(e.getKey(), String.valueOf(e.getValue())))
                .findFirst()
                .orElse(null);

        String result = "None";
        if (entry != null) {
            result = entry.getValue();
        }
        return result;
    }

    public String getDamageFormulaV2(String action, String resource) {
        JSONObject data = mActionsMap.get(action);
        Map.Entry<String, String> entry = data.toMap()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(resource))
                .filter(e -> e.getKey().endsWith(DAMAGE_FORMULA))
                .map(e -> Map.entry(e.getKey(), String.valueOf(e.getValue())))
                .findFirst()
                .orElse(null);

        String result = "None";
        if (entry != null) {
            result = entry.getValue();
        }
        return result;
    }

    public String getPrettyDamageFormula(String action, String resource) {
        String damageFormula = getDamageFormula(action, resource);

        StringBuilder sb = new StringBuilder();
        String[] damageFormulaNodes = damageFormula.split(",");
        for (String damageFormulaNode : damageFormulaNodes) {
            String value = damageFormulaNode.substring(damageFormulaNode.indexOf(EQUAL_DELIMITER) + 1);
            damageFormulaNode = damageFormulaNode.substring(0, damageFormulaNode.indexOf(EQUAL_DELIMITER));

            if (!sb.isEmpty()) { sb.append(System.lineSeparator()); }

            if (damageFormulaNode.startsWith(BASE_KEY)) {
                sb.append(value).append(" Base");
            } else {
                String source = damageFormulaNode.substring(0, damageFormulaNode.indexOf(UNDERSCORE_DELIMITER));
                damageFormulaNode = damageFormulaNode.substring(damageFormulaNode.indexOf(UNDERSCORE_DELIMITER) + 1);
                String magnitude = damageFormulaNode.substring(0, damageFormulaNode.indexOf(UNDERSCORE_DELIMITER));
                damageFormulaNode = damageFormulaNode.substring(damageFormulaNode.indexOf(UNDERSCORE_DELIMITER) + 1);
                String attribute = damageFormulaNode;

                String percent = StringUtils.floatToPercentage(Float.parseFloat(value));
                String prettyMagnitude = StringUtils.convertSnakeCaseToCapitalized(magnitude);
                String prettyAttribute = StringUtils.convertSnakeCaseToCapitalized(attribute);
                sb.append(percent)
                        .append(" ")
                        .append(prettyMagnitude)
                        .append(" ")
                        .append(prettyAttribute);
            }
        }

        return sb.toString();
    }

    public List<Tuple<String, String, Float>> getScalingCostV2(String action, String targetResource) {
        JSONObject data = mActionsMap.get(action);
        JSONObject scalingDamage = data.toMap()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(targetResource))
                .filter(e -> e.getKey().endsWith(COST_FORMULA))
                .map(e -> new JSONObject(String.valueOf(e.getValue())))
                .filter(e -> !e.isEmpty())
                .findFirst()
                .orElse(null);

        List<Tuple<String, String, Float>> results = new ArrayList<>();

        if (scalingDamage != null) {
            for (String key : scalingDamage.keySet()) {
                Tuple<String, String, Float> result = null;
                float value = scalingDamage.getFloat(key);
                if (key.startsWith(BASE_KEY) && key.endsWith(BASE_KEY)) {
                    result = new Tuple<>(key, null, value);
                } else {
                    String magnitude = key.substring(0, key.indexOf(UNDERSCORE_DELIMITER));
                    String attribute = key.substring(key.indexOf(UNDERSCORE_DELIMITER) + 1);
                    result = new Tuple<>(magnitude, attribute, value);
                }
                results.add(result);
            }
        }
        return results;
    }

//    public List<Tuple<String, String, Float>> getScalingDamageV2(String action, String targetResource) {
//        JSONObject data = mActionsMap.get(action);
//        JSONObject scalings = data.toMap()
//                .entrySet()
//                .stream()
//                .filter(e -> e.getKey().startsWith(targetResource))
//                .filter(e -> e.getKey().endsWith(DAMAGE_FORMULA))
//                .map(e -> new JSONObject(String.valueOf(e.getValue())))
//                .filter(e -> !e.isEmpty())
//                .findFirst()
//                .orElse(null);
//
//        List<Tuple<String, String, Float>> results = new ArrayList<>();
//
//        if (scalings != null) {
//            for (String key : scalings.keySet()) {
//                Tuple<String, String, Float> result = null;
//                float value = scalings.getFloat(key);
//                if (key.startsWith(BASE_KEY) && key.endsWith(BASE_KEY)) {
//                    result = new Tuple<>(key, null, value);
//                } else {
//                    String magnitude = key.substring(0, key.indexOf(UNDERSCORE_DELIMITER));
//                    String attribute = key.substring(key.indexOf(UNDERSCORE_DELIMITER) + 1);
//                    result = new Tuple<>(magnitude, attribute, value);
//                }
//                results.add(result);
//            }
//        }
//        return results;
//    }

    public boolean use(GameModel model, String name, Entity user, Set<Entity> targets) {
        Action action = mActionsMapsV2.get(name);
        if (action == null) { return false; }
        boolean isValid = action.validateEffects(model, user, targets);
        if (isValid) {
            action.applyEffects(model, user, targets);
        }
        return isValid;
    }

    public List<Tuple<String, String, Float>> getResourceDamage(String action, String targetResource) {
        JSONObject data = mActionsMap.get(action);
        JSONObject scalings = data.toMap()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(targetResource))
                .filter(e -> e.getKey().endsWith(DAMAGE_FORMULA))
                .map(e -> {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(String.valueOf(e.getValue()));
                    } catch (Exception exception) {
                        System.out.println(exception.toString());
                        System.out.println(exception.getMessage());
                        System.out.println(exception.fillInStackTrace());
                    }
                    return jsonObject;

                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        List<Tuple<String, String, Float>> results = new ArrayList<>();

        if (scalings != null) {
            Tuple<String, String, Float> result = null;
            String key = BASE_KEY;
            if (scalings.has(key)) {
                float value = scalings.getFloat(key);
                result = new Tuple<>(key, null, value);
                results.add(result);
            }
            key = SCALING_KEY;
            JSONObject scaling = scalings.getJSONObject(key);
            for (String scalingKey : scaling.keySet()) {
                String magnitude = scalingKey.substring(0, scalingKey.indexOf(UNDERSCORE_DELIMITER));
                String attribute = scalingKey.substring(scalingKey.indexOf(UNDERSCORE_DELIMITER) + 1);
                float value = scaling.getFloat(scalingKey);
                result = new Tuple<>(magnitude, attribute, value);
                results.add(result);
            }
        }
        return results;
    }


    public Set<String> getResourcesToDamage(String action) {
        JSONObject data = mActionsMap.get(action);
        Set<String> result = data.toMap()
                .keySet()
                .stream()
                .filter(e -> e.contains(DAMAGE_KEY))
                .map(e -> e.substring(0, e.indexOf(UNDERSCORE_DELIMITER)))
                .collect(Collectors.toSet());
        return result;
    }

    public List<Tuple<String, String, Float>> getResourceCost(String action, String targetResource) {
        JSONObject data = mActionsMap.get(action);
        JSONObject scalings = data.toMap()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(targetResource))
                .filter(e -> e.getKey().endsWith(COST_FORMULA))
                .filter(e -> e.getValue() != null)
                .map(e -> {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(String.valueOf(e.getValue()));
                    } catch (Exception exception) {
                        System.out.println(exception.toString());
                        System.out.println(exception.getMessage());
                        System.out.println(exception.fillInStackTrace());
                    }
                    return jsonObject;

                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        List<Tuple<String, String, Float>> results = new ArrayList<>();

        if (scalings != null) {
            Tuple<String, String, Float> result = null;
            String key = BASE_KEY;
            if (scalings.has(key)) {
                float value = scalings.getFloat(key);
                result = new Tuple<>(key, null, value);
                results.add(result);
            }
            key = SCALING_KEY;
            JSONObject scaling = scalings.getJSONObject(key);
            for (String scalingKey : scaling.keySet()) {
                String magnitude = scalingKey.substring(0, scalingKey.indexOf(UNDERSCORE_DELIMITER));
                String attribute = scalingKey.substring(scalingKey.indexOf(UNDERSCORE_DELIMITER) + 1);
                float value = scaling.getFloat(scalingKey);
                result = new Tuple<>(magnitude, attribute, value);
                results.add(result);
            }
        }
        return results;
    }
    public boolean isDamagingAbility(String action) {
        JSONObject data = mActionsMap.get(action);
//        if (data == null) { return false; }
//        double damage = data.toMap()
//                .keySet()
//                .stream()
//                .filter(o -> o.contains(DAMAGE_KEY))
//                .mapToDouble(o -> {
//                    double value = data.getDouble(o);
//                    return value;
//                }).sum();
//        return damage > 0;
        return true;
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

            List<Tuple<String, String, Float>> scalings = getResourceDamage(action, resource);
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
