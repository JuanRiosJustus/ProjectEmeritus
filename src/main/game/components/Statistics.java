package main.game.components;

import main.constants.Constants;
import main.game.collectibles.Gem;
import main.game.stats.node.ResourceNode;
import main.game.stats.node.StatsNode;
import main.game.stores.pools.unit.Unit;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.*;

public class Statistics extends Component {

    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(Statistics.class);

    private final Map<String, StatsNode> statsMap = new HashMap<>();
    
    public Statistics() { }
    
    public Statistics(Unit template) { initialize(template); }
    
    private void initialize(Unit template) {

        statsMap.put(Constants.HEALTH, new ResourceNode(Constants.HEALTH, template.health));
        statsMap.put(Constants.ENERGY, new ResourceNode(Constants.ENERGY, template.energy));

        statsMap.put(Constants.LEVEL, new StatsNode(Constants.LEVEL, 1));
        statsMap.put(Constants.EXPERIENCE_NEEDED, new StatsNode(Constants.EXPERIENCE_NEEDED, 0));
        statsMap.put(Constants.EXPERIENCE_THRESHOLD, new StatsNode(Constants.EXPERIENCE_THRESHOLD, getExperienceThreshold(1)));

        statsMap.put(Constants.PHYSICAL_ATTACK, new StatsNode(Constants.PHYSICAL_ATTACK, template.physicalAttack));
        statsMap.put(Constants.PHYSICAL_DEFENSE, new StatsNode(Constants.PHYSICAL_DEFENSE ,template.physicalDefense));
        statsMap.put(Constants.MAGICAL_ATTACK, new StatsNode(Constants.MAGICAL_ATTACK ,template.magicalAttack));
        statsMap.put(Constants.MAGICAL_DEFENSE, new StatsNode(Constants.MAGICAL_DEFENSE ,template.magicalDefense));

        statsMap.put(Constants.SPEED, new StatsNode(Constants.SPEED, template.speed));
        statsMap.put(Constants.MOVE, new StatsNode(Constants.MOVE, template.move));
        statsMap.put(Constants.CLIMB, new StatsNode(Constants.CLIMB, template.climb));
    }

    public static Statistics builder() {
        return new Statistics();
    }

    public StatsNode getStatsNode(String key) { return (StatsNode) statsMap.get(key); }
    public Statistics putStatsNode(String name, int value) { 
        statsMap.put(name, new StatsNode(name, value)); 
        return this; 
    }
    
    public ResourceNode getResourceNode(String key) { return (ResourceNode) statsMap.get(key); }
    public Statistics putResourceNode(String name, int value) {
        statsMap.put(name, new ResourceNode(name, value));
        return this;
    }

    public void gainExperience(int amount) {
        StatsNode level = getStatsNode(Constants.LEVEL);
        StatsNode current = getStatsNode(Constants.EXPERIENCE_NEEDED);
        StatsNode threshhold = getStatsNode(Constants.EXPERIENCE_THRESHOLD);
        while (amount > 0) {
            int amountToFillLevel = threshhold.getTotal() - current.getTotal();
            int xpGainingThisLevel = 0;
            if (amountToFillLevel > amount) {
                xpGainingThisLevel = amount;
            } else {
                xpGainingThisLevel = amountToFillLevel;
            }
            
            amount -= xpGainingThisLevel;
            current.add(new Object(), Constants.FLAT, xpGainingThisLevel);
            if (current.getTotal() >= threshhold.getTotal()) {
                level.setBase(level.getTotal() + 1);
                current.clear();
                threshhold.clear();
                threshhold.setBase(getExperienceThreshold(level.getTotal()));
                System.out.println("LEVEING UP");
            }
        }
    }

    private int getExperienceThreshold(int level) {
        double x = Math.pow(level, 3);
        double y = Math.pow(level, 2);
        return (int)Math.round( 0.04 * x + 0.8 * y + 2 * level);
    }

    // public StatsNode getNode(S)tring key) { return statsMap.get(key); }
    private void clear() { statsMap.forEach((k, v) -> { v.clear(); }); }

    public boolean isDirty() { return statsMap.values().stream().anyMatch(e -> e.isDirty()); }

    public Set<String> getKeySet() { return statsMap.keySet(); }

    public void addGemBonus(Gem gem) {
        StatsNode node = null;
        switch (gem.type) {
            case RESET -> {
                owner.get(StatusEffects.class).clear();
                clear();
            }
            case HEALTH -> {
                node = getResourceNode(Constants.HEALTH);
            }
            case ENERGY -> {
                node =  getResourceNode(Constants.ENERGY);
            }
            case SPEED -> {
                node = getStatsNode(Constants.SPEED);
            }
            case CRIT -> {
                node = getStatsNode(Constants.SPEED);
            }
            default -> logger.info("Unsupported gem type {}", gem.type);
        }

        if (node != null) {  node.add(gem, Constants.PERCENT, Constants.PERCENT_PER_STAGE); }
    }
}
