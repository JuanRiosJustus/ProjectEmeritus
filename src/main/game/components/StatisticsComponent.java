package main.game.components;

import main.game.components.tile.Gem;
import main.game.stats.StatNode;
import main.game.stores.pools.UnitDatabase;
import org.json.JSONArray;

import java.util.*;

public class StatisticsComponent extends Component {

    public static final String HEALTH = "health";
    public static final String MANA = "mana";
    public static final String STAMINA = "stamina";
    public static final String LEVEL = "level", EXPERIENCE = "experience";
    public static final String PHYSICAL_ATTACK = "PhysicalAttack", PHYSICAL_DEFENSE = "PhysicalDefense";
    public static final String MAGICAL_ATTACK = "MagicalAttack", MAGICAL_DEFENSE = "MagicalDefense";
    public static final String MOVE = "move";
    public static final String CLIMB = "climb";
    public static final String SPEED = "speed";
    public static final String RESISTANCE = "Resistance";
    public static final String SPECIES = "Species", VOCATION = "Vocation";
    public static final String SKILLS = "Skills",
            TYPES = "Type",
            ABILITIES = "Abilities",
            PASSIVES = "Passives",
            TAGS = "Tags";

    private static final String ID_KEY = "id";
    private static final String NICKNAME_KEY = "nickname";
    private static final String UNIT_KEY = "unit";
    private final Map<String, StatNode> mStatsNodeMap = new HashMap<>();
    private List<String> mType = new ArrayList<>();
    private List<String> mActions = new ArrayList<>();
    private String mUnit = "";
    private Set<String> mTags = new HashSet<>();
    private StatNode mLevelNode = null;
    private StatNode mExperienceNode = null;
    public StatisticsComponent(Map<String, Integer> dao) {
        for (Map.Entry<String, Integer> entry : dao.entrySet()) {
            mStatsNodeMap.put(entry.getKey(), new StatNode(entry.getKey(), entry.getValue()));
        }
    }

    public StatisticsComponent(String unit) { this(UUID.randomUUID().toString(), unit, ""); }
    public StatisticsComponent(String id, String unit, String nickname) {

        mType = UnitDatabase.getInstance().getType(unit);
        put("Type", new JSONArray(mType));

        mActions = UnitDatabase.getInstance().getActions(unit);
        put("Actions", new JSONArray(mActions));

        mUnit = UnitDatabase.getInstance().getUnitName(unit);
        put(UNIT_KEY, mUnit);

        put(ID_KEY, id);
        put(NICKNAME_KEY, nickname);

//        mSetMap.put(TAGS, new ArrayList<>());
//        mSetMap.put(SKILLS, new HashSet<>(Set.of("Intimidate", "Bluff", "Listen", "Search",  "Concentrate", "Reason")));


        StatNode statNode = null;
        Map<String, Integer> attributes = UnitDatabase.getInstance().getAttributes(unit);
        for (Map.Entry<String, Integer> entry : attributes.entrySet()) {
            String name = entry.getKey();
            int value = entry.getValue();
            statNode = new StatNode(name, value);
            mStatsNodeMap.put(name, statNode);
        }

        Map<String, Integer> resources = UnitDatabase.getInstance().getResources(unit);
        for (Map.Entry<String, Integer> entry : resources.entrySet()) {
            String name = entry.getKey();
            int value = entry.getValue();
            statNode = new StatNode(name, value);
            statNode.setCurrent(value);
            mStatsNodeMap.put(name, statNode);
        }

//        statNode = mStatsNodeMap.get("Mana");
//        statNode.setCurrent(0);

        mLevelNode = new StatNode(LEVEL);
        mLevelNode.setBase(1);
        mStatsNodeMap.put(LEVEL, mLevelNode);

        mExperienceNode = new StatNode(EXPERIENCE);
        mExperienceNode.setBase(0);
        mStatsNodeMap.put(EXPERIENCE, mExperienceNode);
    }

    public Set<String> getAbilities() { return new HashSet<>(mActions); }
    public List<String> getActions() { return new ArrayList<>(mActions); }
    public Set<String> getType() { return new HashSet<>(mType); }
    private StatNode getStatNode(String key) { return mStatsNodeMap.get(key); }
    public int getBaseModifiedOrTotal(String node, String value) {
        return mStatsNodeMap.get(node).getBaseModifiedOrTotal(value);
    }

    public void modify(String node, int value) { modify(node, null, null, value); }
    public void modify(String node, Object source, String type, int value) {
        StatNode statNode = mStatsNodeMap.get(node);
        if (statNode == null) { return; }

//        if (statNode instanceof ResourceNode resourceNode) {
//            resourceNode.modify(value);
//        } else {
//            statNode.modify(source, type, value);
//        }
    }

    public String getUnit() { return getString(UNIT_KEY); }
    public String getID() { return getString(ID_KEY); }
    public String getNickname() { return getString(NICKNAME_KEY); }

    public int getTotalClimb() { return mStatsNodeMap.get(StatisticsComponent.CLIMB).getTotal(); }
    public int getTotalMovement() { return mStatsNodeMap.get(StatisticsComponent.MOVE).getTotal(); }
    public int getTotalSpeed() { return mStatsNodeMap.get(StatisticsComponent.SPEED).getTotal(); }
    public int getCurrentHealth() { return mStatsNodeMap.get(StatisticsComponent.HEALTH).getCurrent(); }
    public int getTotalHealth() { return mStatsNodeMap.get(StatisticsComponent.HEALTH).getTotal(); }

    public int getModified(String node) { return mStatsNodeMap.get(node).getModified(); }
    public int getTotal(String node) { return mStatsNodeMap.get(node).getTotal(); }
    public int getBase(String node) { return mStatsNodeMap.get(node).getBase(); }
    public int getCurrent(String node) { return mStatsNodeMap.get(node).getCurrent(); }
    public void reduceResource(String node, int value) {
        mStatsNodeMap.get(node).setCurrent(mStatsNodeMap.get(node).getCurrent() - Math.abs(value));
    }
    public void addResource(String node, int value) {
        mStatsNodeMap.get(node).setCurrent(mStatsNodeMap.get(node).getCurrent() + Math.abs(value));
    }

    public Set<String> getKeySet() { return mStatsNodeMap.keySet(); }
    public int getLevel() { return getStatNode(LEVEL).getTotal(); }
//    public int getExperience() { return getResourceNode(EXPERIENCE).getCurrent(); }

    public boolean toExperience(int amount) {
        StatNode level = getStatNode(LEVEL);
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
//            putResourceNode(EXPERIENCE, getExperienceNeeded(level.getTotal()), true);
//            experience = getResourceNode(EXPERIENCE);
//            experience.modify(Integer.MIN_VALUE);
//            leveledUp = true;
//        }

        return leveledUp;
    }

    public static int getExperienceNeeded(int level) {
//        double x = Math.pow(level, 3);
//        double y = Math.pow(level, 2);
//        return (int)Math.round( 0.04 * x + 0.8 * y + 2 * level);

//        return (int) Math.round((4 * Math.pow(level, 3)) / 5);

        double exponent = 2.1;
        double baseXP = 10;
        return (int) Math.floor(baseXP * Math.pow(level, exponent));
    }


//    private void clear() { mStatsNodeMap.forEach((k, v) -> { v.clear(); }); }

    public Set<String> getStatNodeKeys() { return mStatsNodeMap.keySet(); }

    public void addGem(Gem gem) {
//        switch (gem) {
//            case RESET -> {
//                mOwner.get(TagComponent.class).clear();
//                clear();
//            }
//            case HEALTH_RESTORE -> {
//                ResourceNode node = getResourceNode(Constants.HEALTH);
//                node.modify((int) (node.getTotal() * .2));
//            }
//            case ENERGY_RESTORE -> {
//                ResourceNode node = getResourceNode(Constants.ENERGY);
//                node.modify((int) (node.getTotal() * .2));
//            }
//            case PHYSICAL_BUFF -> {
//                StatNode node = getStatNode(Constants.PHYSICAL_ATTACK);
//                node.modify(gem, "percent", .25f);
//                node = getStatNode(Constants.PHYSICAL_DEFENSE);
//                node.modify(gem, "percent", .25f);
//            }
//            case MAGICAL_BUFF -> {
//                StatNode node = getStatNode(Constants.MAGICAL_ATTACK);
//                node.modify(gem, "percent", .25f);
//                node = getStatNode(Constants.MAGICAL_DEFENSE);
//                node.modify(gem, "percent", .25f);
//            }
//            case SPEED_BUFF -> {
//                StatNode node = getStatNode(Constants.SPEED);
//                node.modify(gem, "percent", .5f);
//            }
//            case CRITICAL_BUFF -> {
////                node = getStatsNode(Constants.SPEED);
//            }
//            default -> mLogger.info("Unsupported gem type {}", gem);
//        }
//
////        if (node != null) {  node.add(gem, Constants.PERCENT, Constants.PERCENT_PER_STAGE); }
    }
}
