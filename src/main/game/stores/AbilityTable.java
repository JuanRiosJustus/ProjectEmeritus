package main.game.stores;

import java.util.*;
import java.util.stream.Collectors;

import main.constants.EmeritusDatabase;
import main.constants.Tuple;
import main.game.entity.Entity;
import main.logging.EmeritusLogger;

import main.utils.MathUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class AbilityTable {
    private static AbilityTable instance = null;
    private final Map<String, JSONObject> mActionsMap = new HashMap<>();
    private final Map<String, JSONObject> mCached = new LinkedHashMap<>();
    private final Map<String, JSONObject> mAbilities = new HashMap<>();
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

    public static AbilityTable getInstance() {
        if (instance == null) {
            instance = new AbilityTable();
        }
        return instance;
    }

    private AbilityTable() {
        EmeritusLogger logger = EmeritusLogger.create(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        try {
            JSONArray rows = EmeritusDatabase.getInstance().executeQuery(
                    "SELECT * FROM " + EmeritusDatabase.ABILITY_DATABASE
            );
            for (int i = 0; i < rows.length(); i++) {
                JSONObject data = rows.getJSONObject(i);
                String ability = data.getString("ability");
                mAbilities.put(ability, data);
            }
        } catch (Exception ex) {
            logger.info("Error parsing ability: " + ex.getMessage());
            logger.info("Example: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(-1);
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    private JSONObject getOrCacheResult(String name) {
        JSONObject result = mAbilities.get(name);
        return result;
    }

    public int getArea(String ability) {
        JSONObject data = getOrCacheResult(ability);
        int result = data.getInt("area");
        return result;
    }

    public int getRange(String ability) {
        JSONObject data = getOrCacheResult(ability);
        int result = data.getInt("range");
        return result;
    }

    public float getAccuracy(String action) {
        JSONObject data = getOrCacheResult(action);
        float result = data.getFloat("accuracy");
        return result;
    }
//
//    public int getTimesToTrigger(String action) {
//        JSONObject data = mActionsMap.get(action);
//        int result = data.getInt("times_to_trigger");
//        return result;
//    }
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


    public boolean isSuccessful(String ability) {
        float successChance = AbilityTable.getInstance().getAccuracy(ability);
        return MathUtils.passesChanceOutOf100(successChance);
    }

    public boolean makesPhysicalContact(String ability) {
        JSONObject data = getOrCacheResult(ability);
        return data.getBoolean("makes_physical_contact");
    }

    public JSONArray getType(String ability) {
        JSONObject data = getOrCacheResult(ability);
        JSONArray type = data.getJSONArray("type");
        return type;
    }

    public boolean shouldUsePhysicalDefense(String action) {
        int range = AbilityTable.instance.getRange(action);
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

//    public int getTotalDamage(Entity user, String action, String resource) {
//        Ability abilityData = mAbilityMapV2.get(action);
//        int totalDamage = abilityData.getTotalDamage(user, resource);
//        return totalDamage;
//    }
//    public String getTotalDamageFormula(Entity user, String action, String resource) {
//        Ability abilityData = mAbilityMapV2.get(action);
//        List<String> damageFormula = abilityData.getTotalDamageFormula(user, resource);
//        StringBuilder sb = new StringBuilder();
//        for (String formula : damageFormula) {
//            if (!sb.isEmpty()) { sb.append(System.lineSeparator()); }
//            sb.append(formula);
//        }
//
//        return sb.toString().trim();
//    }

//    public String getTotalDamageFormula(String unitEntityID, String action, String resource) {
//        Ability abilityData = mAbilityMapV2.get(action);
//        List<String> damageFormula = abilityData.getTotalDamage(unitEntityID, resource);
//        StringBuilder sb = new StringBuilder();
//        for (String formula : damageFormula) {
//            if (!sb.isEmpty()) { sb.append(System.lineSeparator()); }
//            sb.append(formula);
//        }
//
//        return sb.toString().trim();
//    }


//    public String getTotalCostFormula(Entity user, String action, String resource) {
//        Ability abilityData = mAbilityMapV2.get(action);
//        List<String> costFormula = abilityData.getTotalCostFormula(user, resource);
//        StringBuilder sb = new StringBuilder();
//        for (String formula : costFormula) {
//            if (!sb.isEmpty()) { sb.append(System.lineSeparator()); }
//            sb.append(formula);
//        }
//
//        return sb.toString().trim();
//    }

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

//    public String getCostFormula(String action, String resource) {
//        JSONObject data = mActionsMap.get(action);
//        Map.Entry<String, String> entry = data.toMap()
//                .entrySet()
//                .stream()
//                .filter(e -> e.getKey().startsWith(resource))
//                .filter(e -> e.getKey().endsWith(COST_FORMULA))
//                .map(e -> Map.entry(e.getKey(), String.valueOf(e.getValue())))
//                .findFirst()
//                .orElse(null);
//
//        String result = "None";
//        if (entry != null) {
//            result = entry.getValue();
//        }
//        return result;
//    }

//    public String getPrettyCostFormula(String action, String resource) {
//        String costFormula = getCostFormula(action, resource);
//
//        StringBuilder sb = new StringBuilder();
//        String[] costFormulaNodes = costFormula.split(",");
//        for (String costFormulaNode : costFormulaNodes) {
//            String value = costFormulaNode.substring(costFormulaNode.indexOf(EQUAL_DELIMITER) + 1);
//            costFormulaNode = costFormulaNode.substring(0, costFormulaNode.indexOf(EQUAL_DELIMITER));
//
//            if (!sb.isEmpty()) { sb.append(System.lineSeparator()); }
//
//            if (costFormulaNode.contains(BASE_KEY)) {
//                sb.append(value).append(" Base");
//            } else {
//                String magnitude = costFormulaNode.substring(0, costFormulaNode.indexOf(UNDERSCORE_DELIMITER));
//                costFormulaNode = costFormulaNode.substring(costFormulaNode.indexOf(UNDERSCORE_DELIMITER) + 1);
//                String attribute = costFormulaNode;
//
//                String percent = StringUtils.floatToPercentage(Float.parseFloat(value));
//                String prettyMagnitude = StringUtils.convertSnakeCaseToCapitalized(magnitude);
//                String prettyAttribute = StringUtils.convertSnakeCaseToCapitalized(attribute);
//                sb.append(percent)
//                        .append(" ")
//                        .append(prettyMagnitude)
//                        .append(" ")
//                        .append(prettyAttribute);
//            }
//        }
//
//        return sb.toString();
//    }
//
//
//    public String getDamageFormula(String action, String resource) {
//        JSONObject data = mActionsMap.get(action);
//        Map.Entry<String, String> entry = data.toMap()
//                .entrySet()
//                .stream()
//                .filter(e -> e.getKey().startsWith(resource))
//                .filter(e -> e.getKey().endsWith(DAMAGE_FORMULA))
//                .map(e -> Map.entry(e.getKey(), String.valueOf(e.getValue())))
//                .findFirst()
//                .orElse(null);
//
//        String result = "None";
//        if (entry != null) {
//            result = entry.getValue();
//        }
//        return result;
//    }

//    public boolean useV2(GameModel model, String userID, String ability, Set<String> targetTileIDs) {
//        Ability abilityData = mAbilityMapV2.get(ability);
//
//        if (abilityData == null) { return false; }
//
//        boolean isValid = abilityData.validateEffectsV2(model, userID, targetTileIDs);
//        if (isValid) { abilityData.applyEffectsV2(model, userID, targetTileIDs); }
//
//        return isValid;
//    }



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

//    public List<String> getDamageFormula(String action) {
//        Action actionData = mActionsMapsV2.get(action);
//        List<String> resources = actionData.getDamageFormula(null, )
//        return resources;
//    }


    public Set<String> getResourcesToDamageV1(String action) {
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

    public JSONArray getCosts(String ability) {
        JSONObject result = getOrCacheResult(ability);
        return result.getJSONArray("costs");
    }
    public String getTargetAttribute(JSONObject resourceObject) {
        return resourceObject.getString("target_attribute");
    }
    public String getScalingType(JSONObject resourceObject) {
        return resourceObject.getString("scaling_type");
    }
    public boolean isBaseScaling(JSONObject resourceObject) {return resourceObject.getString("scaling_type").equals("base"); }
    public String getScalingAttribute(JSONObject resourceObject) {return resourceObject.optString("scaling_attribute", null); }
    public float getScalingMagnitude(JSONObject resourceObject) {
        return resourceObject.getFloat("scaling_magnitude");
    }

    public JSONArray getDamage(String ability) {
        JSONObject result = getOrCacheResult(ability);
        return result.getJSONArray("damages");
    }

    public String getUserAnimation(String ability) {
        JSONObject result = getOrCacheResult(ability);
        return result.getString("user_animation");
    }

    public String getAnnouncement(String ability) {
        JSONObject result = getOrCacheResult(ability);
        return result.getString("announcement");
    }


    public JSONArray getUserTagObjects(String ability) {
        JSONObject result = getOrCacheResult(ability);
        JSONArray tags = result.getJSONArray("user_tags");
        return tags;
    }
    public JSONArray getTargetTagObjects(String ability) {
        JSONObject result = getOrCacheResult(ability);
        JSONArray tags = result.getJSONArray("target_tags");
        return tags;
    }
    public float getTargetTagObjectChance(JSONObject targetTag) { return targetTag.getFloat("chance"); }
    public String getTargetTagObjectName(JSONObject targetTag) { return targetTag.getString("tag"); }
    public int getTargetTagObjectDuration(JSONObject targetTag) { return targetTag.getInt("duration"); }


//    public JSONArray getCostObject(String ability) {
//        JSONObject result = getOrCacheResult(ability);
//        return result.getJSONArray("costs");
//    }
}
