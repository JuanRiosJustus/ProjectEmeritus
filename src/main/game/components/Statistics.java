package main.game.components;

import main.constants.Constants;
import main.game.components.tile.Gem;
import main.game.stats.Resource;
import main.game.stats.Stat;
import main.game.stores.pools.unit.Unit;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.*;

public class Statistics extends Component {

    public static final String HEALTH = "Health";
    public static final String MANA = "Mana";
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
    public static final String RESISTANCE = "Resistance";
    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(Statistics.class);
    private final Map<String, Stat> mStatsMap = new HashMap<>();
    private String mUnit = "";
    public Statistics() { }
    public Statistics(Map<String, Integer> dao) {
        for (Map.Entry<String, Integer> entry : dao.entrySet()) {
            putStatsNode(entry.getKey(), entry.getValue());
        }
    }
    public Statistics(Unit unit) {
        this(unit.stats);
        mUnit = unit.name;
        putResourceNode(HEALTH, unit.stats.get(HEALTH), false);
        putResourceNode(MANA, unit.stats.get(MANA), false);
        putResourceNode(STAMINA, unit.stats.get(STAMINA), false);
        putResourceNode(LEVEL, 1, false);
        putResourceNode(EXPERIENCE, getExperienceNeeded(1), true);
    }

    public Stat getStatsNode(String key) { return mStatsMap.get(key); }

    private void putStatsNode(String key, int value) {
        mStatsMap.put(key, new Stat(key, value));
    }
    private void putResourceNode(String key, int value, boolean zero) {
        mStatsMap.put(key, new Resource(key, value, zero));
    }
    public void addModification(String node, Object source, String type, int value) {
        Stat stat = mStatsMap.get(node);
        stat.add(source, type, value);
    }
    public void toResources(String node, int amount) {
        Resource resource = (Resource) mStatsMap.get(node);
        resource.add(amount);
    }
    public int addTotalAmountToResource(String node, float amount) {
        Resource resource = (Resource) mStatsMap.get(node);
        int total = (int) (resource.getTotal() * amount);
        resource.add(total);
        return total;
    }
    public int addMissingAmountToResource(String node, float amount) {
        Resource resource = (Resource) mStatsMap.get(node);
        int missing = (int) ((resource.getTotal() - resource.getCurrent()) * amount);
        resource.add(missing);
        return missing;
    }
    public int addCurrentAmountToResource(String node, float amount) {
        Resource resource = (Resource) mStatsMap.get(node);
        int current = (int) (resource.getCurrent() * amount);
        resource.add(current);
        return current;
    }
    public void clearModifications(String node) {
        Stat stat = mStatsMap.get(node);
        stat.clear();
    }
    public int getStatModified(String node) { return mStatsMap.get(node).getModified(); }
    public int getStatTotal(String node) { return mStatsMap.get(node).getTotal(); }
    public int getStatBase(String node) { return mStatsMap.get(node).getBase(); }
    public int getStatCurrent(String node) {
        Stat stat = mStatsMap.get(node);
        if (stat instanceof Resource resource) {
            return resource.getCurrent();
        }
        return stat.getTotal();
    }
    
    public Resource getResourceNode(String name) {
        return (Resource) mStatsMap.get(name);
    }
    public Set<String> getResourceKeys() {
        Set<String> result = new HashSet<>();
        for (Map.Entry<String, Stat> entry : mStatsMap.entrySet()) {
            if (!(entry.getValue() instanceof Resource)) { continue; }
            result.add(entry.getKey());
        }
        return result;
    }
    public boolean toExperience(int amount) {
        Stat level = getStatsNode(LEVEL);
        Resource experience = getResourceNode(EXPERIENCE);
        boolean leveledUp = false;
        while (amount > 0) {
            int toLevelUp = experience.getMissing();
            if (toLevelUp > amount) {
                // fill up to the experience to the required amount
                experience.add(amount);
                amount = 0;
            } else {
                // fill up with the rest
                experience.add(toLevelUp);
                amount -= toLevelUp;
            }
            if (experience.getMissing() > 0) { continue; }
            level.add("Level Up", "flat", 1);
            putResourceNode(EXPERIENCE, getExperienceNeeded(level.getTotal()), true);
            experience = getResourceNode(EXPERIENCE);
            experience.add(Integer.MIN_VALUE);
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
    public String getSpecies() { return mUnit; }
    public int getModificationCount() {
        int total = 0;
        for (Map.Entry<String, Stat> entry : mStatsMap.entrySet()) {
            total += entry.getValue().getModifications().size();
        }
        return total;
    }

    public Set<String> getStatNodeNames() { return mStatsMap.keySet(); }

    public void addGem(Gem gem) {
        switch (gem) {
            case RESET -> {
                owner.get(Tags.class).clear();
                clear();
            }
            case HEALTH_RESTORE -> {
                Resource node = getResourceNode(Constants.HEALTH);
                node.add((int) (node.getTotal() * .2));
            }
            case ENERGY_RESTORE -> {
                Resource node = getResourceNode(Constants.ENERGY);
                node.add((int) (node.getTotal() * .2));
            }
            case PHYSICAL_BUFF -> {
                Stat node = getStatsNode(Constants.PHYSICAL_ATTACK);
                node.add(gem, "percent", .25f);
                node = getStatsNode(Constants.PHYSICAL_DEFENSE);
                node.add(gem, "percent", .25f);
            }
            case MAGICAL_BUFF -> {
                Stat node = getStatsNode(Constants.MAGICAL_ATTACK);
                node.add(gem, "percent", .25f);
                node = getStatsNode(Constants.MAGICAL_DEFENSE);
                node.add(gem, "percent", .25f);
            }
            case SPEED_BUFF -> {
                Stat node = getStatsNode(Constants.SPEED);
                node.add(gem, "percent", .5f);
            }
            case CRITICAL_BUFF -> {
//                node = getStatsNode(Constants.SPEED);
            }
            default -> logger.info("Unsupported gem type {}", gem);
        }

//        if (node != null) {  node.add(gem, Constants.PERCENT, Constants.PERCENT_PER_STAGE); }
    }
}
