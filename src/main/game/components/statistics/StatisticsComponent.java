package main.game.components.statistics;

import main.constants.HashSlingingSlasher;
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
    private static final String TAGS = "tags";

    private static final String ID_KEY = "id";
    private static final String NICKNAME_KEY = "nickname";
    private static final String UNIT = "unit";
    private final HashSlingingSlasher mHashSlingingSlasher = new HashSlingingSlasher();
    private final Map<String, Attribute> mAttributeMap = new LinkedHashMap<>();
//    private final List<>
    private final JSONObject mTags = new JSONObject();
    private int mHashCode = 0;
    public StatisticsComponent() { }

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


    public String getUnit() { return getString(UNIT); }
    public String getID() { return getString(ID_KEY); }
    public String getNickname() { return getString(NICKNAME_KEY); }



    private static final String TYPE = "type";
    public void putType(JSONArray type) { put(TYPE, type); }
    public JSONArray getType() { return getJSONArray(TYPE); }
//    public Set<String> getType() {
//        JSONArray type = getJSONArray(TYPE);
//        Set<String> result = new LinkedHashSet<>();
//        for (int index = 0; index < type.length(); index++) {
//            String value = type.getString(index);
//            result.add(value);
//        }
//        return result;
//    }


    private static final String BASIC_ABILITY = "basic_ability";
    public void putBasicAbility(String basic) { put(BASIC_ABILITY, basic); }
    public String getBasicAbility() { return optString(BASIC_ABILITY); }
    private static final String PASSIVE_ABILITY = "passive_ability";
    public void putPassiveAbility(String passive) { put(PASSIVE_ABILITY, passive); }
    public String getPassiveAbility() { return optString(PASSIVE_ABILITY); }
    private static final String OTHER_ABILITY = "other_ability";
    public void putOtherAbility(JSONArray other) { put(OTHER_ABILITY, other); }
    public Set<String> getOtherAbility() {
        JSONArray otherAbility = optJSONArray(OTHER_ABILITY, new JSONArray());
        Set<String> result = new LinkedHashSet<>();
        for (int i = 0; i < otherAbility.length(); i++) {
            String value = otherAbility.getString(i);
            result.add(value);
        }
        return result;
    }

    public Set<String> getAttributes() { return mAttributeMap.keySet(); }
    public void putAttributes(JSONObject attributes) {
        Map<String, Attribute> updatedMap = mAttributeMap;
        updatedMap.clear();

        for (String key : attributes.keySet()) {
            float value = attributes.getFloat(key);

            Attribute attribute = new Attribute(key, value);
            updatedMap.put(key, attribute);
        }
        recalculateCheckSum();
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
        Attribute statisticNode = (Attribute) mAttributeMap.get(attribute);
        return statisticNode.getModified();
    }
    public int getTotal(String attribute) {
        Attribute statisticNode = (Attribute) mAttributeMap.get(attribute);
        return statisticNode.getTotal();
    }
    public int getBase(String attribute) {
        Attribute statisticNode = (Attribute) mAttributeMap.get(attribute);
        return statisticNode.getBase();
    }
    public int getCurrent(String attribute) {
        Attribute statisticNode = (Attribute) mAttributeMap.get(attribute);
        return statisticNode.getCurrent();
    }
    public float getScaling(String attribute, String type) {
        Attribute statisticNode = (Attribute) mAttributeMap.get(attribute);
        return statisticNode.getScaling(type);
    }

    public void toResource(String attribute, float value) {
        Attribute attributeNode = (Attribute) mAttributeMap.get(attribute);
        attributeNode.setCurrent(attributeNode.getCurrent() + value);
        recalculateCheckSum();
    }

//    public void putModification(String attribute, String mod, String source, float value, int lifetime) {
//        Attribute node = (Attribute) mAttributesMap.get(attribute);
//        node.putModification(mod, source, value, lifetime);
//        recalculateCheckSum();
//    }
//
//    public void putAdditiveModification(String attribute, String source, float value, int lifetime) {
//        Attribute node = (Attribute) mAttributesMap.get(attribute);
//        node.putAdditiveModification(source, value, lifetime);
//        recalculateCheckSum();
//    }
//    public void putMultiplicativeModification(String attribute, String source, float value, int lifetime) {
//        Attribute node = (Attribute) mAttributesMap.get(attribute);
//        node.putMultiplicativeModification(source, value, lifetime);
//        recalculateCheckSum();
//    }

    private void recalculateCheckSum() {
        Set<Integer> hashCodes = new HashSet<>();
        for (Map.Entry<String, Attribute> entry : mAttributeMap.entrySet()) {
            String key = entry.getKey();
            Attribute attribute = entry.getValue();
            int hashcode = attribute.hashCode();
            hashCodes.add(hashcode);
        }
        mHashCode = hashCodes.hashCode();
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

    public Set<String> getTagKeys() { return mTags.keySet(); }
//    public JSONArray getTags() { return mTags; }

    public JSONObject getTag(String key) { return mTags.getJSONObject(key); }

    public int hashCode() { return mHashCode; }
}
