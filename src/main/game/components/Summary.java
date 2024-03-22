package main.game.components;

import main.constants.Constants;
import main.game.components.tile.Gem;
import main.game.stats.ResourceNode;
import main.game.stats.StatNode;
import main.game.stores.pools.unit.Unit;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.*;

public class Summary extends Component {

    public static final String HEALTH = "Health";
    public static final String MANA = "Mana";
    public static final String ENERGY = "Energy";
    public static final String STAMINA = "Stamina";
    public static final String LEVEL = "Level";
    public static final String EXPERIENCE = "Experience";
    public static final String STRENGTH = "Strength";
    public static final String INTELLIGENCE = "Intelligence";
    public static final String DEXTERITY = "Dexterity";
    public static final String WISDOM = "Wisdom";
    public static final String CONSTITUTION = "Constitution";
    public static final String CHARISMA = "Charisma";
    public static final String LUCK = "Luck";
    public static final String MOVE = "Move";
    public static final String CLIMB = "Climb";
    public static final String SPEED = "Speed";
    public static final String RESISTANCE = "Resistance";
    public static final String SKILLS = "Skills",
            TYPES = "Type",
            ABILITIES = "Abilities",
            PASSIVES = "Passives",
            TAGS = "Tags";

//    public static final String[] STAT_KEYS = {
//            HEALTH, MANA, STAMINA, STRENGTH, INTELLIGENCE, DEXTERITY, WISDOM, CONSTITUTION, CHARISMA
//    };

    private static final Set<String> mStatKeys = new HashSet<>();
    private static final ELogger mLogger = ELoggerFactory.getInstance().getELogger(Summary.class);
    private final Map<String, List<String>> mSetMap = new HashMap<>();
    private final Map<String, StatNode> mStatsMap = new HashMap<>();
    private String mName = "";
    public Summary() { }
    public Summary(Map<String, Integer> dao) {
        for (Map.Entry<String, Integer> entry : dao.entrySet()) {
            putStatsNode(entry.getKey(), entry.getValue());
            mStatKeys.add(entry.getKey());
        }
    }
    public Summary(Unit unit) {
//        addAllStats(unit, this);

        mSetMap.put(TYPES, new ArrayList<>(unit.types));
//        mSetMap.put(PASSIVES, new HashSet<>(unit.));
        mSetMap.put(ABILITIES, new ArrayList<>(unit.abilities));
//        mSetMap.get(ABILITIES).addAll(Set.of("Attack", "Defend"));
        mSetMap.put(TAGS, new ArrayList<>());
//        mSetMap.put(SKILLS, new HashSet<>(Set.of("Intimidate", "Bluff", "Listen", "Search",  "Concentrate", "Reason")));
        mSetMap.put(SKILLS, new ArrayList<>());


        // Set up stat nodes
        for (Map.Entry<String, Integer> entry : unit.attributes.entrySet()) {
            String key = entry.getKey().substring(entry.getKey().lastIndexOf(".") + 1);
            Integer value = entry.getValue();
            mStatsMap.put(key, new StatNode(key, value));
        }

        for (Map.Entry<String, Integer> entry : unit.resources.entrySet()) {
            String key = entry.getKey().substring(entry.getKey().lastIndexOf(".") + 1);
            Integer value = entry.getValue();
            mStatsMap.put(key, new ResourceNode(key, value));
        }

        mStatsMap.put(EXPERIENCE, new ResourceNode(EXPERIENCE, getExperienceNeeded(getLevel() + 1), true));
        mName = unit.name;
    }

    public static Set<String> getStatKeys() { return new HashSet<>(mStatKeys); }
    public Set<String> getAbilities() { return new HashSet<>(mSetMap.get(ABILITIES)); }
    public Set<String> getType() { return new HashSet<>(mSetMap.get(TYPES)); }
    public Set<String> getSkills() { return new HashSet<>(mSetMap.get(SKILLS)); }
    public boolean setContains(String set, String value) { return mSetMap.get(set).contains(value); }
    public StatNode getStatsNode(String key) { return mStatsMap.get(key); }

    private void putStatsNode(String key, int value) {
        mStatsMap.put(key, new StatNode(key, value));
    }
    private void putResourceNode(String key, int value, boolean zero) {
        mStatsMap.put(key, new ResourceNode(key, value, zero));
    }
    public void modify(String node, int value) { modify(node, null, null, value); }
    public void modify(String node, Object source, String type, int value) {
        StatNode statNode = mStatsMap.get(node);
        if (statNode instanceof ResourceNode resourceNode && source == null && type == null) {
            resourceNode.modify(value);
        } else {
            statNode.modify(source, type, value);
        }
    }

    public int getHashState(String key) {
        StatNode node = mStatsMap.get(key);
        if (node == null) { return 0; }
        return node.hashState();
    }
    public void clearModifications(String node) {
        StatNode stat = mStatsMap.get(node);
        stat.clear();
    }
    public int getStatModified(String node) { return mStatsMap.get(node).getModified(); }
    public int getStatTotal(String node) { return mStatsMap.get(node).getTotal(); }
    public int getStatBase(String node) { return mStatsMap.get(node).getBase(); }
    public int getStatCurrent(String node) {
        StatNode stat = mStatsMap.get(node);
        if (stat instanceof ResourceNode resource) {
            return resource.getCurrent();
        }
        return stat.getTotal();
    }

    public Map<String, Float> getStatModSummary(String node) {
        StatNode stat = mStatsMap.get(node);
        return stat.getSummary();
    }
    public <T> boolean isOfType(String key, Class<T> type) {
        StatNode node = mStatsMap.get(key);
        if (node == null) { return false; }
        return node.getClass().getSimpleName().equals(type.getSimpleName());
    }
    public ResourceNode getResourceNode(String name) {
        return (ResourceNode) mStatsMap.getOrDefault(name, null);
    }
    public Set<String> getResourceKeys() {
        Set<String> result = new HashSet<>();
        for (Map.Entry<String, StatNode> entry : mStatsMap.entrySet()) {
            if (!(entry.getValue() instanceof ResourceNode)) { continue; }
            result.add(entry.getKey());
        }
        return result;
    }

    public Set<String> getKeySet() { return mStatsMap.keySet(); }
    public int getLevel() { return getStatsNode(LEVEL).getTotal(); }
    public int getExperience() { return getResourceNode(EXPERIENCE).getCurrent(); }

    public boolean toExperience(int amount) {
        StatNode level = getStatsNode(LEVEL);
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
        double x = Math.pow(level, 3);
        double y = Math.pow(level, 2);
        return (int)Math.round( 0.04 * x + 0.8 * y + 2 * level);
    }

    private void clear() { mStatsMap.forEach((k, v) -> { v.clear(); }); }
    public String getName() { return mName; }
//    public int getModificationCount() {
//        int total = 0;
//        for (Map.Entry<String, StatNode> entry : mStatsMap.entrySet()) {
//            total += entry.getValue().getModifications().size();
//        }
//        return total;
//    }

    public Set<String> getStatNodeKeys() { return mStatsMap.keySet(); }

    public void addGem(Gem gem) {
        switch (gem) {
            case RESET -> {
                owner.get(Tags.class).clear();
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
                StatNode node = getStatsNode(Constants.PHYSICAL_ATTACK);
                node.modify(gem, "percent", .25f);
                node = getStatsNode(Constants.PHYSICAL_DEFENSE);
                node.modify(gem, "percent", .25f);
            }
            case MAGICAL_BUFF -> {
                StatNode node = getStatsNode(Constants.MAGICAL_ATTACK);
                node.modify(gem, "percent", .25f);
                node = getStatsNode(Constants.MAGICAL_DEFENSE);
                node.modify(gem, "percent", .25f);
            }
            case SPEED_BUFF -> {
                StatNode node = getStatsNode(Constants.SPEED);
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
