package main.game.components;

import main.constants.Constants;
import main.game.components.tile.Gem;
import main.game.stats.node.ResourceNode;
import main.game.stats.node.StatsNode;
import main.game.stores.pools.unit.Unit;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.*;

public class Statistics extends Component {

    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(Statistics.class);
    private final Map<String, StatsNode> statsMap = new HashMap<>();
    private String unit = "";
    
    public Statistics() { setup(); }
    public Statistics(Unit template) { setup(); initialize(template); }
    private void initialize(Unit template) {

        unit = template.unit;

        putResourceNode(Constants.HEALTH, template.health);
        putResourceNode(Constants.ENERGY, template.energy);
//        getResourceNode(Constants.ENERGY).add(Integer.MIN_VALUE);

        putStatsNode(Constants.LEVEL, 1);

        putResourceNode(Constants.EXPERIENCE, getExperienceNeeded(1));
        getResourceNode(Constants.EXPERIENCE).add(Integer.MIN_VALUE);

        putStatsNode(Constants.PHYSICAL_ATTACK, template.physicalAttack);
        putStatsNode(Constants.PHYSICAL_DEFENSE, template.physicalDefense);
        putStatsNode(Constants.MAGICAL_ATTACK, template.magicalAttack);
        putStatsNode(Constants.MAGICAL_DEFENSE, template.magicalDefense);

        putStatsNode(Constants.SPEED, template.speed);
        putStatsNode(Constants.MOVE, template.move);
        putStatsNode(Constants.CLIMB, template.climb);
    }

    private void setup() {
        putResourceNode(Constants.HEALTH, 0);
        putResourceNode(Constants.ENERGY, 0);

        putStatsNode(Constants.LEVEL, 1);

        putResourceNode(Constants.EXPERIENCE, getExperienceNeeded(1));
        getResourceNode(Constants.EXPERIENCE).add(Integer.MIN_VALUE);

        putStatsNode(Constants.PHYSICAL_ATTACK, 0);
        putStatsNode(Constants.PHYSICAL_DEFENSE, 0);
        putStatsNode(Constants.MAGICAL_ATTACK, 0);
        putStatsNode(Constants.MAGICAL_DEFENSE, 0);

        putStatsNode(Constants.SPEED, 0);
        putStatsNode(Constants.MOVE, 0);
        putStatsNode(Constants.CLIMB, 0);
    }

    public static Statistics builder() {
        return new Statistics();
    }

    public StatsNode getStatsNode(String key) { return statsMap.get(key); }

    public Statistics putStatsNode(String name, int value) {
        statsMap.put(name, new StatsNode(name, value));
        return this;
    }

    public Statistics putResourceNode(String name, int value) {
        statsMap.put(name, new ResourceNode(name, value));
        return this;
    }

    public int getStatTotal(String name) {
        return statsMap.get(name).getTotal();
    }
    public int getResourceCurrent(String name) {
        ResourceNode node = (ResourceNode) statsMap.get(name);
        return node.getCurrent();
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
            putResourceNode(Constants.EXPERIENCE, getExperienceNeeded(level.getTotal()));
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
    public String getUnit() { return unit; }
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