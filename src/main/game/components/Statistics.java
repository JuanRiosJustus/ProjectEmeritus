package main.game.components;

import main.constants.Constants;
import main.game.components.tile.Gem;
import main.game.stats.ResourceNode;
import main.game.stats.StatNode;
import main.game.stores.pools.unit.Unit;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.*;

public class Statistics extends Component {

    public static final String HEALTH = "Health";
    public static final String MANA = "Mana";
    public static final String ENERGY = "Energy";
    public static final String STAMINA = "Stamina";
    public static final String LEVEL = "Level", EXPERIENCE = "Experience";
    public static final String PHYSICAL_ATTACK = "PhysicalAttack", PHYSICAL_DEFENSE = "PhysicalDefense";
    public static final String MAGICAL_ATTACK = "MagicalAttack", MAGICAL_DEFENSE = "MagicalDefense";
    public static final String CHARISMA = "Charisma";
    public static final String LUCK = "Luck";
    public static final String MOVE = "Move";
    public static final String CLIMB = "Climb";
    public static final String SPEED = "Speed";
    public static final String RESISTANCE = "Resistance";
    public static final String SPECIES = "Species", VOCATION = "Vocation";
    public static final String SKILLS = "Skills",
            TYPES = "Type",
            ABILITIES = "Abilities",
            PASSIVES = "Passives",
            TAGS = "Tags";

    private static final ELogger mLogger = ELoggerFactory.getInstance().getELogger(Statistics.class);
    private final Map<String, StatNode> mStatsNodeMap = new HashMap<>();
    private List<String> mType = new ArrayList<>();
    private List<String> mActions = new ArrayList<>();
    private String mUnit = "";
    private String mUnitFileName = "";
    private Set<String> mTags = new HashSet<>();
    public Statistics() { }
    public Statistics(Map<String, Integer> dao) {
        for (Map.Entry<String, Integer> entry : dao.entrySet()) {
            mStatsNodeMap.put(entry.getKey(), new StatNode(entry.getKey(), entry.getValue()));
        }
    }

    public Statistics(Unit unit) {
        this(unit, "", 1, 0);
    }

    public Statistics(Unit unit, String vocation, int level, int experience) {
        mType = unit.getListValue("Type");
        mActions = unit.getListValue("Abilities");
        mUnit = unit.getStringValue("Unit");
        mUnitFileName = mUnit.replace(' ', '_');

//        mTags = new
//        mSetMap.put(TAGS, new ArrayList<>());
//        mSetMap.put(SKILLS, new HashSet<>(Set.of("Intimidate", "Bluff", "Listen", "Search",  "Concentrate", "Reason")));


        for (Map.Entry<String, Float> entry : unit.getScalarKeysWithTag("attribute").entrySet()) {
            mStatsNodeMap.put(entry.getKey(), new StatNode(entry.getKey(), entry.getValue()));
        }

        for (Map.Entry<String, Float> entry : unit.getScalarKeysWithTag("resource").entrySet()) {
            mStatsNodeMap.put(entry.getKey(), new ResourceNode(entry.getKey(), entry.getValue()));
        }


        mStatsNodeMap.put(LEVEL, new StatNode(LEVEL, 1));
//        mStatsNodeMap.put(EXPERIENCE, getExperienceNeeded(level.getTotal()))

        StatNode levelNode = getStatNode(LEVEL);
        levelNode.setBase(level);
        levelNode.setModified(experience);

//        mMetaDataMap.put(ABILITIES, String.join(",", unit.abilities));
//        mMetaDataMap.put(TYPES, String.join(",", unit.types));
    }

    public Set<String> getAbilities() { return new HashSet<>(mActions); }
    public Set<String> getType() { return new HashSet<>(mType); }
    public Set<String> getSkills() { return new HashSet<>(mActions); }
    public boolean setContains(String set, String value) { return false; }
    public boolean hasAbility(String abilityName) { return mActions.contains(abilityName); }
    private StatNode getStatNode(String key) { return mStatsNodeMap.get(key); }

    private void putStatsNode(String key, int value) {
        mStatsNodeMap.put(key, new StatNode(key, value));
    }
    private void putResourceNode(String key, int value, boolean zero) {
        mStatsNodeMap.put(key, new ResourceNode(key, value, zero));
    }
    public void modify(String node, int value) { modify(node, null, null, value); }
    public void modify(String node, Object source, String type, int value) {
        StatNode statNode = mStatsNodeMap.get(node);
        if (statNode == null) { return; }

        if (statNode instanceof ResourceNode resourceNode) {
            resourceNode.modify(value);
        } else {
            statNode.modify(source, type, value);
        }
    }

    public int getHashState(String key) {
        StatNode node = mStatsNodeMap.get(key);
        if (node == null) { return 0; }
        return node.hashState();
    }
    public void clearModifications(String node) {
        StatNode stat = mStatsNodeMap.get(node);
        stat.clear();
    }
    public int getStatModified(String node) { return mStatsNodeMap.get(node).getModified(); }
    public int getStatTotal(String node) { return mStatsNodeMap.get(node).getTotal(); }
    public int getStatBase(String node) { return mStatsNodeMap.get(node).getBase(); }
    public int getStatCurrent(String node) {
        StatNode stat = mStatsNodeMap.get(node);
        if (stat instanceof ResourceNode resource) {
            return resource.getCurrent();
        }
        return stat.getTotal();
    }

    public Map<String, Float> getStatModSummary(String node) {
        StatNode stat = mStatsNodeMap.get(node);
        return stat.getSummary();
    }
    public <T> boolean isOfType(String key, Class<T> type) {
        StatNode node = mStatsNodeMap.get(key);
        if (node == null) { return false; }
        return node.getClass().getSimpleName().equals(type.getSimpleName());
    }
    public ResourceNode getResourceNode(String name) {
        return (ResourceNode) mStatsNodeMap.getOrDefault(name, null);
    }
    public Set<String> getResourceKeys() {
        Set<String> result = new HashSet<>();
        for (Map.Entry<String, StatNode> entry : mStatsNodeMap.entrySet()) {
            if (!(entry.getValue() instanceof ResourceNode)) { continue; }
            result.add(entry.getKey());
        }
        return result;
    }

    public Set<String> getKeySet() { return mStatsNodeMap.keySet(); }
    public int getLevel() { return getStatNode(LEVEL).getTotal(); }
    public int getExperience() { return getResourceNode(EXPERIENCE).getCurrent(); }

    public boolean toExperience(int amount) {
        StatNode level = getStatNode(LEVEL);
        ResourceNode experience = getResourceNode(EXPERIENCE);
        boolean leveledUp = false;
        while (amount > 0) {
            int toLevelUp = experience.getMissing();
            if (toLevelUp > amount) {
                // fill up to the experience to the required amount
                experience.modify(amount);
                amount = 0;
            } else {
                // fill up with the rest
                experience.modify(toLevelUp);
                amount -= toLevelUp;
            }
            if (experience.getMissing() > 0) { continue; }
            level.modify("Level Up", "flat", 1);
            putResourceNode(EXPERIENCE, getExperienceNeeded(level.getTotal()), true);
            experience = getResourceNode(EXPERIENCE);
            experience.modify(Integer.MIN_VALUE);
            leveledUp = true;
        }

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


    private void clear() { mStatsNodeMap.forEach((k, v) -> { v.clear(); }); }
    public String getUnit() { return mUnit; }
    public String getUnitFileName() { return mUnitFileName; }

    public Set<String> getStatNodeKeys() { return mStatsNodeMap.keySet(); }

    public void addGem(Gem gem) {
        switch (gem) {
            case RESET -> {
                mOwner.get(Tags.class).clear();
                clear();
            }
            case HEALTH_RESTORE -> {
                ResourceNode node = getResourceNode(Constants.HEALTH);
                node.modify((int) (node.getTotal() * .2));
            }
            case ENERGY_RESTORE -> {
                ResourceNode node = getResourceNode(Constants.ENERGY);
                node.modify((int) (node.getTotal() * .2));
            }
            case PHYSICAL_BUFF -> {
                StatNode node = getStatNode(Constants.PHYSICAL_ATTACK);
                node.modify(gem, "percent", .25f);
                node = getStatNode(Constants.PHYSICAL_DEFENSE);
                node.modify(gem, "percent", .25f);
            }
            case MAGICAL_BUFF -> {
                StatNode node = getStatNode(Constants.MAGICAL_ATTACK);
                node.modify(gem, "percent", .25f);
                node = getStatNode(Constants.MAGICAL_DEFENSE);
                node.modify(gem, "percent", .25f);
            }
            case SPEED_BUFF -> {
                StatNode node = getStatNode(Constants.SPEED);
                node.modify(gem, "percent", .5f);
            }
            case CRITICAL_BUFF -> {
//                node = getStatsNode(Constants.SPEED);
            }
            default -> mLogger.info("Unsupported gem type {}", gem);
        }

//        if (node != null) {  node.add(gem, Constants.PERCENT, Constants.PERCENT_PER_STAGE); }
    }
}
