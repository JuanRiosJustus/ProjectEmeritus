package game.components.statistics;

import constants.Constants;
import game.collectibles.Gem;
import game.components.Component;
import game.stats.node.ScalarNode;
import game.stats.node.StatsNode;
import game.stores.pools.unit.Unit;
import logging.Logger;
import logging.LoggerFactory;

import java.util.*;

public class Statistics extends Component {

    private final Logger logger = LoggerFactory.instance().logger(getClass());
    private final Map<String, ScalarNode> map = new HashMap<>();
    
    private Statistics() { }
    
    public Statistics(Unit unitTemplate) { initialize(unitTemplate); }
    
    private void initialize(Unit template) {

        map.put(Constants.HEALTH, new ScalarNode(Constants.HEALTH, template.health));
        map.put(Constants.ENERGY, new ScalarNode(Constants.ENERGY, template.energy));
        map.put(Constants.PHYSICAL_ATTACK, new ScalarNode(Constants.PHYSICAL_ATTACK, template.physicalAttack));
        map.put(Constants.PHYSICAL_DEFENSE, new ScalarNode(Constants.PHYSICAL_DEFENSE ,template.physicalDefense));
        map.put(Constants.MAGICAL_ATTACK, new ScalarNode(Constants.MAGICAL_ATTACK ,template.magicalAttack));
        map.put(Constants.MAGICAL_DEFENSE, new ScalarNode(Constants.MAGICAL_DEFENSE ,template.magicalDefense));

        map.put(Constants.SPEED, new ScalarNode(Constants.SPEED, template.speed));
        map.put(Constants.MOVE, new ScalarNode(Constants.MOVE, template.move));
        map.put(Constants.CLIMB, new ScalarNode(Constants.CLIMB, template.climb));

    }

    public static Statistics builder() {
        return new Statistics();
    }
    
    public Statistics putScalar(String name, int value) { map.put(name, new ScalarNode(name, value)); return this; }
    public ScalarNode getScalarNode(String name) { return map.get(name); }
    public StatsNode getNode(String key) { return map.get(key); }
    public Set<String> getKeySet() { return map.keySet(); }
    @Override
    public String toString() {
        return "Statistics{" +
                "stats=" + map +
                '}';
    }

    public void addGemBonus(Gem gem) {
        ScalarNode node;
        switch (gem.type) {
            case HEALTH -> {
                node = map.get(Constants.HEALTH);
                node.add(Gem.class, Constants.PERCENT, .5f);
            }
            case ENERGY -> {
                node = map.get(Constants.ENERGY);
                node.add(Gem.class, Constants.PERCENT, .5f);
            }
            case SPEED -> {
                node = map.get(Constants.SPEED);
                node.add(Gem.class, Constants.PERCENT, .5f);
            }
            default -> logger.log("Unsupported gem type {1}", gem.type);
        }
    }
}
