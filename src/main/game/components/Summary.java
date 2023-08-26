package main.game.components;

import main.constants.Constants;
import main.game.components.tile.Gem;
import main.game.stats.node.ResourceNode;
import main.game.stats.node.StatsNode;
import main.game.stores.pools.unit.Unit;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.*;

public class Summary extends Component {

    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(Summary.class);
    private final Map<String, StatsNode> statsMap = new HashMap<>();
    private String species = "";
    public Summary() { }
    public Summary(Map<String, Integer> dao) {
        for (Map.Entry<String, Integer> entry : dao.entrySet()) {
            putStatsNode(entry.getKey(), entry.getValue());
        }
    }
    public Summary(Unit unit) {
        this(unit.stats);
        species = unit.species;

        putResourceNode(Constants.HEALTH, unit.stats.get(Constants.HEALTH), false);
        putResourceNode(Constants.ENERGY, unit.stats.get(Constants.ENERGY), false);
        putResourceNode(Constants.LEVEL, 1, false);
        putResourceNode(Constants.EXPERIENCE, getExperienceNeeded(1), true);


    }

    public StatsNode getStatsNode(String key) { return statsMap.get(key); }

    private void putStatsNode(String key, int value) {
        statsMap.put(key, new StatsNode(key, value));
    }
    private void putResourceNode(String key, int value, boolean zero) {
        statsMap.put(key, new ResourceNode(key, value, zero));
    }
    public void addModification(String node, Object source, String type, int value) {
        StatsNode statNode = statsMap.get(node);
        statNode.add(source, type, value);
    }
    public void addResources(String node, int amount) {
        ResourceNode resourceNode = (ResourceNode) statsMap.get(node);
        resourceNode.add(amount);
    }
    public void clearModifications(String node) {
        StatsNode statNode = statsMap.get(node);
        statNode.clear();
    }
    public int getStatModifications(String node) { return statsMap.get(node).getModified(); }
    public int getStatTotal(String node) { return statsMap.get(node).getTotal(); }
    public int getStatBase(String node) { return statsMap.get(node).getBase(); }
    public int getStatCurrent(String node) {
        StatsNode statsNode = statsMap.get(node);
        if (statsNode instanceof ResourceNode resourceNode) {
            return resourceNode.getCurrent();
        }
        return statsNode.getTotal();
    }
    
    public ResourceNode getResourceNode(String name) {
        return (ResourceNode) statsMap.get(name);
    }

    public boolean toExperience(int amount) {
        StatsNode level = getStatsNode(Constants.LEVEL);
        ResourceNode experience = getResourceNode(Constants.EXPERIENCE);
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
            putResourceNode(Constants.EXPERIENCE, getExperienceNeeded(level.getTotal()), true);
            experience = getResourceNode(Constants.EXPERIENCE);
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

    private void clear() { statsMap.forEach((k, v) -> { v.clear(); }); }
    public String getSpecies() { return species; }
    public int getModificationCount() {
        int total = 0;
        for (Map.Entry<String, StatsNode> entry : statsMap.entrySet()) {
            total += entry.getValue().getModifications().size();
        }
        return total;
    }

    public Set<String> getStatNodeNames() { return statsMap.keySet(); }

    public void addGem(Gem gem) {
        switch (gem) {
            case RESET -> {
                owner.get(Tags.class).clear();
                clear();
            }
            case HEALTH_RESTORE -> {
                ResourceNode node = getResourceNode(Constants.HEALTH);
                node.add((int) (node.getTotal() * .2));
            }
            case ENERGY_RESTORE -> {
                ResourceNode node = getResourceNode(Constants.ENERGY);
                node.add((int) (node.getTotal() * .2));
            }
            case PHYSICAL_BUFF -> {
                StatsNode node = getStatsNode(Constants.PHYSICAL_ATTACK);
                node.add(gem, "percent", .25f);
                node = getStatsNode(Constants.PHYSICAL_DEFENSE);
                node.add(gem, "percent", .25f);
            }
            case MAGICAL_BUFF -> {
                StatsNode node = getStatsNode(Constants.MAGICAL_ATTACK);
                node.add(gem, "percent", .25f);
                node = getStatsNode(Constants.MAGICAL_DEFENSE);
                node.add(gem, "percent", .25f);
            }
            case SPEED_BUFF -> {
                StatsNode node = getStatsNode(Constants.SPEED);
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
