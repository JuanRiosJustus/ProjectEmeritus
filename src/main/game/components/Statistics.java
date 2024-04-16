package main.game.components;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
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
    private final Map<String, List<String>> mSetMap = new HashMap<>();
    private final Map<String, StatNode> mStatsNodeMap = new HashMap<>();
    private final Map<String, String> mMetaDataMap = new HashMap<>();
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
        mSetMap.put(TYPES, new ArrayList<>(unit.types));
        mSetMap.put(ABILITIES, new ArrayList<>(unit.abilities));
        mSetMap.put(TAGS, new ArrayList<>());
//        mSetMap.put(SKILLS, new HashSet<>(Set.of("Intimidate", "Bluff", "Listen", "Search",  "Concentrate", "Reason")));
        mSetMap.put(SKILLS, new ArrayList<>());


        // Set up stat nodes
        for (Map.Entry<String, Integer> entry : unit.attributes.entrySet()) {
            String key = entry.getKey().substring(entry.getKey().lastIndexOf(".") + 1);
            Integer value = entry.getValue();
            mStatsNodeMap.put(key, new StatNode(key, value));
        }

        for (Map.Entry<String, Integer> entry : unit.resources.entrySet()) {
            String key = entry.getKey().substring(entry.getKey().lastIndexOf(".") + 1);
            Integer value = entry.getValue();
            mStatsNodeMap.put(key, new ResourceNode(key, value));
        }

        mStatsNodeMap.put(LEVEL, new StatNode(LEVEL, 1));

        StatNode levelNode = getStatNode(LEVEL);
        levelNode.setBase(level);
        levelNode.setModified(experience);

        mMetaDataMap.put(SPECIES, unit.name);
        mMetaDataMap.put(VOCATION, vocation);
        mMetaDataMap.put(ABILITIES, String.join(",", unit.abilities));
        mMetaDataMap.put(TYPES, String.join(",", unit.types));
    }

    public void putMetaData(String key, String value) { mMetaDataMap.put(key, value); }
//    public Set<String> getAbilities() { return new HashSet<>(mSetMap.get(ABILITIES)); }
    public Set<String> getAbilities() {
        return new HashSet<>(Arrays.stream(mMetaDataMap.get(ABILITIES).split(",")).toList());
    }
    public Set<String> getType() {
        return new HashSet<>(Arrays.stream(mMetaDataMap.get(TYPES).split(",")).toList());
    }

//    public Set<String> getType() { return new HashSet<>(mSetMap.get(TYPES)); }
    public Set<String> getSkills() { return new HashSet<>(mSetMap.get(SKILLS)); }
    public boolean setContains(String set, String value) { return mSetMap.get(set).contains(value); }
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
    public String getVocation() { return mMetaDataMap.get(VOCATION); }

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
    public String getSpecies() { return mMetaDataMap.get(SPECIES); }

//    public int getModificationCount() {
//        int total = 0;
//        for (Map.Entry<String, StatNode> entry : mStatsMap.entrySet()) {
//            total += entry.getValue().getModifications().size();
//        }
//        return total;
//    }

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

    @Override
    public JsonObject toJsonObject(JsonObject toWriteTo) {
        JsonObject stats = new JsonObject();
        mStatsNodeMap.forEach((key, value) -> stats.put(key, value.getBase()));
        toWriteTo.put("statsMap", stats);

        JsonObject other = new JsonObject();
        mSetMap.forEach((key, value) -> other.put(key, new JsonArray(value)));
        toWriteTo.put("otherMap", other);

        toWriteTo.put("species", mMetaDataMap.get(SPECIES));

        return toWriteTo;
    }
}
