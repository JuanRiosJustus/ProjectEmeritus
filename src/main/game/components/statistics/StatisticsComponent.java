package main.game.components.statistics;

import main.constants.Checksum;
import main.game.components.Component;
import main.game.stats.Attribute;
import main.game.stats.Tag;
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
    private final Checksum mChecksum = new Checksum();
    private final JSONObject mAttributesMap = new JSONObject();
    private final JSONObject mTags = new JSONObject();
    public StatisticsComponent() { }

    public StatisticsComponent(Map<String, Float> attributes) {
        for (Map.Entry<String, Float> entry : attributes.entrySet()) {
            String key = entry.getKey();
            Float value = entry.getValue();

            Attribute attribute = new Attribute(key, value);
            mAttributesMap.put(key, attribute);
        }

        put("statistics", mAttributesMap);
        put("tags", mTags);

        recalculateCheckSum();
    }

    public void addTag(String tag) {
        mTags.put(tag, new Tag("????", tag, -1));
        recalculateCheckSum();
    }

    public void addTag(String source, String tag, int duration) {
        mTags.put(tag, new Tag(source, tag, duration));
        recalculateCheckSum();
    }

    public void removeTag(String tag) {
        mTags.remove(tag);
        recalculateCheckSum();
    }

//    public Map<String, Integer> getTags() {
//
//        Map<String, Integer> result = new LinkedHashMap<>();
//        for (String key : tags.keySet()) {
//            int value = tags.getInt(key);
//            result.put(key, value);
//        }
//        return result;
//    }



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
    public int getModified(String attribute) {
        Attribute statisticNode = (Attribute) mAttributesMap.get(attribute);
        return statisticNode.getModified();
    }
    public int getTotal(String attribute) {
        Attribute statisticNode = (Attribute) mAttributesMap.get(attribute);
        return statisticNode.getTotal();
    }
    public int getBase(String attribute) {
        Attribute statisticNode = (Attribute) mAttributesMap.get(attribute);
        return statisticNode.getBase();
    }
    public int getCurrent(String attribute) {
        Attribute statisticNode = (Attribute) mAttributesMap.get(attribute);
        return statisticNode.getCurrent();
    }
    public float getScaling(String attribute, String type) {
        Attribute statisticNode = (Attribute) mAttributesMap.get(attribute);
        return statisticNode.getScaling(type);
    }

    public void toResource(String node, int value) {
        Attribute attribute = (Attribute) mAttributesMap.get(node);
        attribute.setCurrent(attribute.getCurrent() + value);
        recalculateCheckSum();
    }

    public void putModification(String attribute, String mod, String source, float value, int lifetime) {
        Attribute node = (Attribute) mAttributesMap.get(attribute);
        node.putModification(mod, source, value, lifetime);
        recalculateCheckSum();
    }

    public void putAdditiveModification(String attribute, String source, float value, int lifetime) {
        Attribute node = (Attribute) mAttributesMap.get(attribute);
        node.putAdditiveModification(source, value, lifetime);
        recalculateCheckSum();
    }
    public void putMultiplicativeModification(String attribute, String source, float value, int lifetime) {
        Attribute node = (Attribute) mAttributesMap.get(attribute);
        node.putMultiplicativeModification(source, value, lifetime);
        recalculateCheckSum();
    }

    private void recalculateCheckSum() {
        StringBuilder sb = new StringBuilder();
        Set<String> keys = mAttributesMap.keySet();
        for (String key : keys) {
            Attribute attribute = (Attribute) mAttributesMap.get(key);
            sb.append(attribute.getCheckSum());
        }

        keys = mTags.keySet();
        for (String key : keys) {
            Tag tag = (Tag) mTags.get(key);
            sb.append(tag.toString());
        }

        mChecksum.set(sb.toString());
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


    public Set<String> getAttributeKeys() { return mAttributesMap.keySet(); }
    public Set<String> getTagKeys() { return mTags.keySet(); }

    public JSONObject getTag(String key) { return mTags.getJSONObject(key); }

    public int getChecksum() { return mChecksum.get(); }
}
