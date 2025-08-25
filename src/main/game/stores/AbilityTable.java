package main.game.stores;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import main.constants.Constants;
import main.logging.EmeritusLogger;

import main.utils.MathUtils;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

public class AbilityTable {
    private static AbilityTable instance = null;
    private final Map<String, JSONObject> mAbilities = new HashMap<>();
    private final Map<String, Float> mDebugMap = new HashMap<>();
    private static final String ATTRIBUTE_KEY = "attribute_key";
    private static final String ATTRIBUTE_SCALING = "attribute_scaling";
    private static final String ATTRIBUTE_VALUE = "attribute_value";

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
            String raw = Files.readString(Paths.get(Constants.ABILITY_DATABASE_PATH));
            JSONArray rows = JSONArray.parse(raw);
            for (int i = 0; i < rows.size(); i++) {
                JSONObject data = rows.getJSONObject(i);
                String ability = data.getString("ability");
                mAbilities.put(ability.toLowerCase(Locale.ROOT), data);
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

    public boolean exists(String name) {
        return mAbilities.containsKey(name);
    }

    public int getArea(String ability) {
        JSONObject data = getOrCacheResult(ability);
        int result = data.getIntValue("area", 0);
        return result;
    }

    public int getRange(String ability) {
        JSONObject data = getOrCacheResult(ability);
        int result = data.getIntValue("range", 0);
        return result;
    }

    public float getAccuracy(String ability) {
        JSONObject data = getOrCacheResult(ability);
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

    public String getType(String ability) {
        JSONObject data = getOrCacheResult(ability);
        return data.getString("type");
    }

//    public boolean shouldUsePhysicalDefense(String action) {
//        int range = AbilityTable.instance.getRange(action);
//        return range <= 1;
//    }
//
//    public boolean hasSameTypeAttackBonus(Entity actorUnitEntity, String action) {
//        return false;
////        CsvRow actionRow = ActionPool.getInstance().getAction(action);
////        return !Collections.disjoint(
////                actorUnitEntity.get(StatisticsComponent.class).getType(),
////                actionRow.getList("Type"));
//    }

    private static final String HEALTH_KEY = "health";
    private static final String MANA_KEY = "mana";
    private static final String STAMINA_KEY = "stamina";
    private static final String BASE_KEY = "base";
    private static final String SCALING_KEY = "scaling";


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


//    public boolean isDamagingAbility(String action) {
//        JSONObject data = mActionsMap.get(action);
////        if (data == null) { return false; }
////        double damage = data.toMap()
////                .keySet()
////                .stream()
////                .filter(o -> o.contains(DAMAGE_KEY))
////                .mapToDouble(o -> {
////                    double value = data.getDouble(o);
////                    return value;
////                }).sum();
////        return damage > 0;
//        return true;
//    }



    public List<String> getStatisticKeysV1(String ability) {
        JSONObject data = mAbilities.get(ability);

        List<String> keys = data.keySet()
                .stream()
                .filter(e -> e.startsWith("statistic"))
                .filter(e -> e.contains("."))
                .map(e -> e.substring(e.lastIndexOf(".") + 1))
                .toList();
        return keys;
    }

    public Map<String, String> getStatisticKeys(String ability) {
        JSONObject data = mAbilities.get(ability);

        if (data == null) { return new HashMap<>(); }

        Map<String, String> keys = data.keySet()
                .stream()
                .filter(e -> e.startsWith("statistic"))
                .filter(e -> e.contains("."))
                .map(k -> Map.entry(k.substring(k.lastIndexOf(".") + 1), k))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return keys;
    }

    public boolean getMakesPhysicalContact(String action) {
        JSONObject data = mAbilities.get(action);
        boolean makesContact = data.getBoolean("makes_physical_contact");
        return makesContact;
    }

    public JSONObject getAbilityData(String ability) {
        JSONObject data = mAbilities.get(ability);
        if (data == null) { return null; }

        JSONObject result = new JSONObject();

        result.put("description", getDescription(ability));
        result.put("range", getRange(ability));
        result.put("area", getArea(ability));
        result.put("accuracy", getAccuracy(ability));
        result.put("type", getType(ability));

        return result;

    }
    public String getDescription(String ability) {
        JSONObject data = mAbilities.get(ability);
        if (data == null) {
            System.out.println(
                    "toko"
            );
        }
        String description = data.getString("description");
        return description;
    }

    public Set<String> getKeys(String ability) {
        JSONObject data = getOrCacheResult(ability);
        return data.keySet();
    }

    public Object getValue(String ability, String key) {
        JSONObject data = getOrCacheResult(ability);
        return data.get(key);
    }

    public Float getFloat(String ability, String key) {
        JSONObject data = getOrCacheResult(ability);
        return data.getFloatValue(key);
    }

    public boolean isPercentageKey(String ability, String key) {
        JSONObject data = getOrCacheResult(ability);
        Object value = data.get(key);
        if (value instanceof String str) { return str.endsWith("%"); }
        return false;
    }


    public JSONObject getResourcesToCostOrDamage(String ability, boolean isCost) {
        JSONObject data = getOrCacheResult(ability);
        String indicator = isCost ? ".cost" : ".damage";

        JSONObject results = new JSONObject();
        for (String key : data.keySet()) {
            // Ensure the key is correctly formatted and has the cost indicator
            if (!key.contains(indicator)) { continue; }

            int index = key.indexOf(indicator);
            if (index == -1) { continue; }

            String resource = key.substring(0, key.indexOf("."));

            results.put(resource, resource);
        }
        return results;
    }


    public JSONObject getResourcesToCostOrDamageVariables(String ability, String resource, boolean isCost) {
        JSONObject data = getOrCacheResult(ability);
        String indicator = isCost ? ".cost" : ".damage";

        JSONObject result = new JSONObject();
        for (String key : data.keySet()) {
            // Ensure the key is correctly formatted and has the cost indicator
            int index = key.indexOf(indicator);
            if (index == -1) { continue; }

            boolean isRelevant = key.startsWith(resource);
            if (!isRelevant) { continue; }

            String statistic = key.substring(index + indicator.length());
            if (statistic.startsWith(".")) { statistic = statistic.substring(1); }

            float value = data.getFloatValue(key);
            result.put(statistic, value);
        }
        return result;
    }


    private static final String COST_INDICATOR = ".cost";
    public JSONArray getStatisticsToCost(String ability) {
        JSONObject data = getOrCacheResult(ability);
        JSONObject result = new JSONObject();

        for (String key : data.keySet()) {
            int indicatorIndex = key.indexOf(COST_INDICATOR);
            boolean isRelatedKey = indicatorIndex >= 0;
            if (!isRelatedKey) { continue; }

            String statistic = key.substring(0, key.indexOf("."));
            if (statistic.startsWith(".")) { statistic = statistic.substring(1); }

            float value = data.getFloatValue(key);
            result.put(statistic, value);
        }

        return new JSONArray();
    }
    public JSONObject getCostOrDamageConstants(String ability, boolean isCost) {
        JSONObject data = getOrCacheResult(ability);
        String indicator = isCost ? ".cost" : ".damage";

        JSONObject result = new JSONObject();
        for (String key : data.keySet()) {
            int indicatorIndex = key.indexOf(indicator);
            boolean isRelatedKey = indicatorIndex >= 0;
            if (!isRelatedKey) { continue; }

            String statistic = key.substring(0, key.indexOf("."));
            float value = data.getFloatValue(key);
            result.put(statistic, value);
        }
        return result;
    }

    public JSONObject getCostOrDamageStatisticsToImpactReferences(String ability, String resource, boolean isCost) {
        JSONObject data = getOrCacheResult(ability);
        String indicator = isCost ? ".cost." : ".damage.";

        JSONObject result = new JSONObject();
        for (String key : data.keySet()) {
            int indicatorIndex = key.indexOf(indicator);
            boolean isRelatedKey = indicatorIndex >= 0;
            if (!isRelatedKey) { continue; }
            boolean isResource = key.startsWith(resource);
            if (!isResource) { continue; }
            String scaling = key.substring(indicatorIndex + indicator.length());
            double value = data.getDoubleValue(key);
            result.put(scaling, value);
        }
        return result;
    }

    public JSONObject getCost(String ability) {
        JSONObject result = getOrCacheResult(ability);
        return result.getJSONObject("cost");
    }
    public String getAttribute(JSONObject object) {
        return object.getString("attribute");
    }

    public String getEquation(JSONObject resourceObject) {
        return resourceObject.getString("equation");
    }

    public String getScalingAttributeScaling(JSONObject attrMod) { return attrMod.getString(ATTRIBUTE_SCALING); }
    public boolean isBaseScaling(JSONObject attrMod) { return attrMod.getString(ATTRIBUTE_SCALING).equals("base"); }
    public String getScalingAttributeKey(JSONObject attrMod) { return attrMod.getString(ATTRIBUTE_KEY); }
    public float getScalingAttributeValue(JSONObject attrMod) { return attrMod.getFloat(ATTRIBUTE_VALUE); }

//    public JSONArray getDamage(String ability) {
//        JSONObject result = getOrCacheResult(ability);
//        return result.getJSONArray("damage");
//    }

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
    public int getTargetTagObjectDuration(JSONObject targetTag) { return targetTag.getIntValue("duration"); }

    public JSONArray getPassiveAttributes(String ability) {
        JSONObject result = getOrCacheResult(ability);
        if (result == null) {
            System.out.println("tookok");
        }
        JSONArray attributes = result.getJSONArray("passive_attributes");
        return attributes;
    }
}
