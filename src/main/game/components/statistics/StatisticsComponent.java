package main.game.components.statistics;

import main.constants.StateLock;
import main.game.components.Component;
import main.game.stats.StatisticNode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class StatisticsComponent extends Component {

    private static final String HEALTH = "health";
    private static final String MANA = "mana";
    private static final String STAMINA = "stamina";
    private static final String LEVEL = "level", EXPERIENCE = "experience";
    private static final String PHYSICAL_ATTACK = "physical_attack", PHYSICAL_DEFENSE = "physical_defense";
    private static final String MAGICAL_ATTACK = "magical_attack", MAGICAL_DEFENSE = "magical_defense";
    private static final String MOVE = "move", CLIMB = "climb", SPEED = "speed";
    private static final String ABILITIES = "abilities";
    private static final String TYPE = "type";
    private static final String TAGS = "tags";

    private static final String ID_KEY = "id";
    private static final String NICKNAME_KEY = "nickname";
    private static final String UNIT = "unit";
    private final Map<String, StatisticNode> mStatsNodeMap = new HashMap<>();
    private Map<String, Integer> mTags = new LinkedHashMap<>();
    private StateLock mStateLock = new StateLock();
    public StatisticsComponent() { }

    public StatisticsComponent(Map<String, Float> attributes) {
        // Setup Resources
        for (Map.Entry<String, Float> entry : attributes.entrySet()) {
            String key = entry.getKey();
            Float value = entry.getValue();
            StatisticNode node = new StatisticNode(key, value);
            put(key, node);

            mStateLock.isUpdated(key, node.getTotal());
        }

    }

    public void addTag(String tag) {
        JSONObject tags = optJSONObject(TAGS, new JSONObject());
        put(TAGS, tags);

        int currentCount = tags.optInt(tag, 0);
        int newCount = currentCount + 1;
        tags.put(tag, newCount);

        mStateLock.isUpdated(tag, newCount);
    }

    public void removeTag(String tag) {
        JSONObject tags = optJSONObject(TAGS, new JSONObject());
        put(TAGS, tags);

        int currentCount = tags.optInt(tag, 0);
        int newCount = currentCount - 1;
        if (newCount <= 0) {
            tags.remove(tag);
        } else {
            tags.put(tag, newCount);
        }

        mStateLock.isUpdated(tag, newCount);
    }

    public int getTag(String tag) { return mTags.getOrDefault(tag, 0); }
    public Map<String, Integer> getTags() {
        JSONObject tags = optJSONObject(TAGS, new JSONObject());
        put(TAGS, tags);

        Map<String, Integer> result = new LinkedHashMap<>();
        for (String key : tags.keySet()) {
            int value = tags.getInt(key);
            result.put(key, value);
        }
        return result;
    }



    public int getScaling(String attribute, String value) {
        StatisticNode statisticNode = (StatisticNode) get(attribute);
        return statisticNode.getScaling(value);
    }
    public String getUnit() { return getString(UNIT); }
    public String getID() { return getString(ID_KEY); }
    public String getNickname() { return getString(NICKNAME_KEY); }



    public void putType(List<String> type) { put(StatisticsComponent.TYPE, type); }
    public Set<String> getType() {
        JSONArray type = getJSONArray("type");
        Set<String> result = new LinkedHashSet<>();
        for (int index = 0; index < type.length(); index++) {
            String value = type.getString(index);
            result.add(value);
        }
        return result;
    }



    public void putAbilities(List<String> abilities) { put(StatisticsComponent.ABILITIES, abilities); }
    public Set<String> getAbilities() {
        JSONArray abilities = getJSONArray(StatisticsComponent.ABILITIES);
        Set<String> result = new LinkedHashSet<>();
        for (int index = 0; index < abilities.length(); index++) {
            String value = abilities.getString(index);
            result.add(value);
        }
        return result;
    }


    public void putUnit(String unit) { put(StatisticsComponent.UNIT, unit); }
    public int getTotalClimb() {return getTotal(StatisticsComponent.CLIMB); }
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
    public int getModified(String node) {
        StatisticNode statisticNode = (StatisticNode) get(node);
        return statisticNode.getModified();
    }
    public int getTotal(String node) {
        StatisticNode statisticNode = (StatisticNode) get(node);
        return statisticNode.getTotal();
    }
    public int getBase(String node) {
        StatisticNode statisticNode = (StatisticNode) get(node);
        return statisticNode.getBase();
    }
    public int getCurrent(String node) {
        StatisticNode statisticNode = (StatisticNode) get(node);
        return statisticNode.getCurrent();
    }




//    public void reduceResource(String node, int value) {
//        get(node).setCurrent(get(node) - Math.abs(value));
//    }
//    public void addResource(String node, int value) {
//        get(node).setCurrent(get(node) + Math.abs(value));
//    }

    public void toResource(String node, int value) {
        StatisticNode statisticNode = (StatisticNode) get(node);
        statisticNode.setCurrent(statisticNode.getCurrent() + value);
        mStateLock.isUpdated(node, statisticNode.getCurrent());
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


    public Set<String> getStatisticNodeKeys() {
        Set<String> keys = new LinkedHashSet<>();
        for (String key : keySet()) {
            Object value = get(key);

            if (value instanceof StatisticNode) {
                keys.add(key);
            }
        }
        return keys;
    }

    public int getHashState() { return mStateLock.getHashState(); }
}
