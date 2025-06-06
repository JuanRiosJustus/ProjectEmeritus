package main.game.stores;

import java.util.*;

import main.constants.EmeritusDatabase;
import main.game.entity.Entity;
import main.logging.EmeritusLogger;

import main.utils.MathUtils;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

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
            JSONArray rows = EmeritusDatabase.getInstance().execute(
                    "SELECT * FROM " + EmeritusDatabase.ABILITY_DATABASE
            );
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
        int result = data.getIntValue("area");
        return result;
    }

    public int getRange(String ability) {
        JSONObject data = getOrCacheResult(ability);
        if (data == null) {
            System.err.println("tttlgmt " + ability);
        }
        int result = data.getIntValue("range");
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

    public JSONArray getDamages(String ability) {
        JSONObject result = getOrCacheResult(ability);
        return result.getJSONArray("damages");
    }
    public JSONArray getCosts(String ability) {
        JSONObject result = getOrCacheResult(ability);
        return result.getJSONArray("costs");
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
    public int getTargetTagObjectDuration(JSONObject targetTag) { return targetTag.getIntValue("duration"); }

    public JSONArray getPassiveAttributes(String ability) {
        JSONObject result = getOrCacheResult(ability);
        JSONArray attributes = result.getJSONArray("passive_attributes");
        return attributes;
    }
}
