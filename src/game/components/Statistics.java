package game.components;

import constants.Constants;
import game.collectibles.Gem;
import game.stats.node.ResourceNode;
import game.stats.node.StatsNode;
import game.stats.node.StatsNode;
import game.stores.pools.ability.Ability;
import game.stores.pools.ability.AbilityPool;
import game.stores.pools.unit.Unit;
import logging.ELogger;
import logging.ELoggerFactory;

import java.util.*;

public class Statistics extends Component {

    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(Statistics.class);

    private final Map<String, StatsNode> statsMap = new HashMap<>();
    
    public Statistics() { }
    
    public Statistics(Unit template) { initialize(template); }
    
    private void initialize(Unit template) {

        statsMap.put(Constants.HEALTH, new ResourceNode(Constants.HEALTH, template.health));
        statsMap.put(Constants.ENERGY, new ResourceNode(Constants.ENERGY, template.energy));

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
    
    public Statistics putStatsNode(String name, int value) { 
        statsMap.put(name, new StatsNode(name, value)); 
        return this; 
    }

    public Statistics putResourceNode(String name, int value) {
        statsMap.put(name, new ResourceNode(name, value));
        return this;
    }

    public ResourceNode getResourceNode(String key) { return (ResourceNode) statsMap.get(key); }
    public StatsNode getStatsNode(String key) { return (StatsNode) statsMap.get(key); }
    public StatsNode getNode(String key) { return statsMap.get(key); }
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
