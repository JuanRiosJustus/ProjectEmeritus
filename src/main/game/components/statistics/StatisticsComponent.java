package main.game.components.statistics;

import main.game.components.Component;
import main.game.stats.Statistic;
import main.game.stats.Tag;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.*;

public class StatisticsComponent extends Component {

    private static final String HEALTH = "health";
    private static final String MANA = "mana";
    private static final String STAMINA = "stamina";
    private static final String LEVEL = "level", EXPERIENCE = "experience";
    private static final String PHYSICAL_ATTACK = "physical_attack", PHYSICAL_DEFENSE = "physical_defense";
    private static final String MAGICAL_ATTACK = "magical_attack", MAGICAL_DEFENSE = "magical_defense";
    private static final String MOVE = "move", CLIMB = "climb", SPEED = "speed";
    private static final String UNIT = "unit";
    private final Map<String, Statistic> mAttributeMap = new LinkedHashMap<>();
    private final Map<String, Tag> mTagMap = new LinkedHashMap<>();
    private final String STATISTICS = "statistics";
    private JSONObject mStatisticsMap = new JSONObject();

    private final String TAGS = "tags";
    private JSONObject mTagsMap = new JSONObject();

//    private final List<>
    private int mHashCode = 0;
    public StatisticsComponent() {

        mStatisticsMap = new JSONObject();
        put(STATISTICS, mStatisticsMap);

        mTagsMap = new JSONObject();
        put(TAGS, mTagsMap);
    }


    public JSONObject getTags() { return getJSONObject(TAGS); }
    public JSONObject getStatistics() { return getJSONObject(STATISTICS); }

    public void putStatistics(JSONArray statsList) {
        JSONArray stats = getJSONArray(STATISTICS);
        if (stats != null) {
            return;
        }
        put(STATISTICS, statsList);
    }

//    public JSONArray getStatistics() {
//        return getJSONArray(STATISTICS);
//    }



    public void addTag(String key) {
        Tag tag = new Tag(key, "????", -1);
        mTagMap.put(key, tag);
        recalculateHash();
    }

    public void addTag(String key, String source, int duration) {
        Tag tag = new Tag(key, source, duration);
        mTagMap.put(key, tag);
        recalculateHash();
    }

    public void removeTag(String tag) {
        mTagMap.remove(tag);
        recalculateHash();
    }

    public String getUnit() { return getString(UNIT); }

    private static final String TYPE = "type";
    public void putType(JSONArray type) { put(TYPE, type); }

    public JSONArray getType() { return getJSONArray(TYPE); }


    private static final String BASIC_ABILITY = "basic_ability";
    public void putBasicAbility(String basic) { put(BASIC_ABILITY, basic); }
    public String getBasicAbility() { return getString(BASIC_ABILITY); }
    private static final String PASSIVE_ABILITY = "passive_ability";
    public void putPassiveAbility(String passive) { put(PASSIVE_ABILITY, passive); }
    public String getPassiveAbility() { return getString(PASSIVE_ABILITY); }
    private static final String OTHER_ABILITY = "other_ability";
    public void putOtherAbility(JSONArray other) { put(OTHER_ABILITY, other); }
    public Set<String> getOtherAbility() {
        JSONArray otherAbility = getJSONArray(OTHER_ABILITY);
        if (otherAbility == null) { otherAbility = new JSONArray(); }
        Set<String> result = new LinkedHashSet<>();
        for (int i = 0; i < otherAbility.size(); i++) {
            String value = otherAbility.getString(i);
            result.add(value);
        }
        return result;
    }

    public Set<String> getAttributes() { return mStatisticsMap.keySet(); }
    public void putAttributes(JSONObject attributes) {
        JSONObject updatedMap = mStatisticsMap;

        updatedMap.clear();

        for (String key : attributes.keySet()) {
            float value = attributes.getFloat(key);

            Statistic statistic = new Statistic(key, value);
            updatedMap.put(key, statistic);
        }
        recalculateHash();
    }

    public void setStatistic(String key, int value) {
        JSONObject updatedMap = mStatisticsMap;

        if (value <= 0) {
            value = 0;
        }

        Statistic statistic = new Statistic(key, value);
        updatedMap.put(key, statistic);

        recalculateHash();
    }

    public void getStatChanges(JSONObject attributes) {
        Map<String, Statistic> updatedMap = mAttributeMap;
        updatedMap.clear();

        for (String key : attributes.keySet()) {
            float value = attributes.getFloat(key);

            Statistic statistic = new Statistic(key, value);
            updatedMap.put(key, statistic);
        }
        recalculateHash();
    }



    public void putUnit(String unit) { put(StatisticsComponent.UNIT, unit); }
    public int getTotalClimb() { return getTotal(StatisticsComponent.CLIMB); }
    public int getTotalMovement() { return getTotal(StatisticsComponent.MOVE); }
    public int getTotalSpeed() { return getTotal(StatisticsComponent.SPEED); }
    public int getCurrentHealth() { return getCurrent(StatisticsComponent.HEALTH); }
    public int getTotalHealth() { return getTotal(StatisticsComponent.HEALTH); }
    public int getCurrentMana() { return getCurrent(StatisticsComponent.MANA); }
    public int getTotalMana() { return getTotal(StatisticsComponent.MANA); }
    public int getCurrentStamina() { return getCurrent(StatisticsComponent.STAMINA); }
    public int getTotalStamina() { return getTotal(StatisticsComponent.STAMINA); }
    public int getLevel() { return getTotal(StatisticsComponent.LEVEL); }
    public int getCurrentExperience() { return getCurrent(StatisticsComponent.EXPERIENCE); }
    public int getTotalMaxExperience() { return getTotal(StatisticsComponent.EXPERIENCE); }
    public int getTotalPhysicalAttack() { return getTotal(StatisticsComponent.PHYSICAL_ATTACK); }
    public int getTotalMagicalAttack() { return getTotal(StatisticsComponent.MAGICAL_ATTACK); }
    public int getTotalPhysicalDefense() { return getTotal(StatisticsComponent.PHYSICAL_DEFENSE); }
    public int getTotalMagicalDefense() { return getTotal(StatisticsComponent.MAGICAL_DEFENSE); }

    private Statistic createOrGetNode(String statistic) {
        Statistic node = (Statistic) mStatisticsMap.getJSONObject(statistic);
        if (node == null) {
            node = new Statistic(statistic, 0);
            mStatisticsMap.put(statistic, node);
        }
        return node;
    }
    public int getBonus(String statistic) {
        Statistic node = createOrGetNode(statistic);
        return node.getBonus();
    }
    public int getTotal(String statistic) {
        Statistic node = createOrGetNode(statistic);
        return node.getTotal();
    }
    public int getBase(String statistic) {
        Statistic node = createOrGetNode(statistic);
        return node.getBase();
    }
    public int getCurrent(String statistic) {
        Statistic node = createOrGetNode(statistic);
        return node.getCurrent();
    }
    public int getMissing(String statistic) {
        Statistic node = createOrGetNode(statistic);
        return node.getMissing();
    }

    public float getScaling(String statistic, String type) {
        Statistic node = createOrGetNode(statistic);
        return node.getScaling(type);
    }

    public void toResource(String statistic, float value) {
        Statistic node = (Statistic) mStatisticsMap.getJSONObject(statistic);
        node.setCurrent(node.getCurrent() + value);
        recalculateHash();
    }

    public void addToResource(String statistic, double value) {
        Statistic node = (Statistic) mStatisticsMap.getJSONObject(statistic);
        node.setCurrent(node.getCurrent() + value);
        recalculateHash();
    }

    public void removeFromResource(String statistic, double value) {
        Statistic node = (Statistic) mStatisticsMap.getJSONObject(statistic);
        node.setCurrent(node.getCurrent() - value);
        recalculateHash();
    }




    public void addFlatBonus(String source, String name, String statistic, float value) {
        Statistic node = (Statistic) mStatisticsMap.getJSONObject(statistic);
        node.addFlatBonus(source, name, value);
        recalculateHash();
    }

    public void addPercentBonus(String source, String name, String statistic, float value) {
        Statistic node = (Statistic) mStatisticsMap.getJSONObject(statistic);
        node.addPercentBonus(source, name, value);
        recalculateHash();
    }

    public void fillResource(String statistic) {
        Statistic node = (Statistic) mStatisticsMap.getJSONObject(statistic);
        node.fill();
        recalculateHash();
    }

    public void putSimpleModification(String source, String name, String attribute, float value) {
        Statistic node = mAttributeMap.get(attribute);
        node.addPercentBonus(source, name, value);
        recalculateHash();
    }


    public void removeModification(String source) {
        Map<String, Statistic> attributes = mAttributeMap;
        for (Map.Entry<String, Statistic> entry : attributes.entrySet()) {
            Statistic statistic = entry.getValue();
            statistic.removeModification(source);
        }
    }
    private void recalculateHash() {
        Set<Integer> hashCodes = new HashSet<>();
        for (Map.Entry<String, Statistic> entry : mAttributeMap.entrySet()) {
            String key = entry.getKey();
            Statistic statistic = entry.getValue();
            int hashcode = statistic.hashCode();
            hashCodes.add(hashcode);
        }

        mHashCode = 17;
        mHashCode = mHashCode * 31 + mAttributeMap.hashCode();
        mHashCode = mHashCode * 31 + (getBasicAbility() == null ? -1 : getBasicAbility().hashCode());
        mHashCode = mHashCode * 31 + (getOtherAbility() == null ? - 1 : getOtherAbility().hashCode());
        mHashCode = mHashCode * 31 + (getPassiveAbility() == null ? -1 : getPassiveAbility().hashCode());
    }

//    public int getExperience() { return getResourceNode(EXPERIENCE); }



    public boolean toExperience(int amount) {
//        StatNode level = getStatNode(LEVEL);
//        ResourceNode experience = getResourceNode(EXPERIENCE);
        boolean leveledUp = false;
//        while (amount > 0) {
//            int toLevelUp = 0; //experience.getMissing();
//            if (toLevelUp > amount) {
//                // fill up to the experience to the required amount
//                experience.modify(amount);
//                amount = 0;
//            } else {
//                // fill up with the rest
//                experience.modify(toLevelUp);
//                amount -= toLevelUp;
//            }
//            if (experience.getMissing() > 0) { continue; }
//            level.modify("Level Up", "flat", 1);
//            putResourceNode(EXPERIENCE, getExperienceNeeded(level), true);
//            experience = getResourceNode(EXPERIENCE);
//            experience.modify(Integer.MIN_VALUE);
//            leveledUp = true;
//        }

        return leveledUp;
    }

//    public int getHashState() {
//
//    }

    public static int getExperienceNeeded(int level) {
//        double x = Math.pow(level, 3);
//        double y = Math.pow(level, 2);
//        return (int)Math.round( 0.04 * x + 0.8 * y + 2 * level);

//        return (int) Math.round((4 * Math.pow(level, 3)) / 5);

        double exponent = 2.1;
        double baseXP = 10;
        return (int) Math.floor(baseXP * Math.pow(level, exponent));
    }

    public int getTagDuration(String tag) { return mTagMap.get(tag).getDuration(); }


    public Set<String> getTagNames() { return mTagMap.keySet(); }
    public void putTag(String key) {
        JSONObject tags = getTags();

        JSONObject data = tags.getJSONObject(key);
        if (data == null) {
            data = new JSONObject()
                    .fluentPut("name", key)
                    .fluentPut("duration", 2)
                    .fluentPut("stacks", 1);
        } else {
            data.fluentPut("stacks", data.getIntValue("stacks") + 1);
        }
    }

    public JSONObject getTagDetails(String key) {
        JSONObject tags = getTags();
        JSONObject tagData = tags.getJSONObject(key);
        JSONObject result = new JSONObject();
        for (String tagDataKey : tagData.keySet()) {
            Object value = tagData.get(tagDataKey);
            if (value instanceof JSONObject || value instanceof JSONArray) { continue; }
            result.put(tagDataKey, value);
        }
        return result;

    }
    public int hashCode() { return mHashCode; }
}
