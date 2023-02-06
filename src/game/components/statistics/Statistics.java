package game.components.statistics;

import constants.Constants;
import game.collectibles.Gem;
import game.components.Component;
import game.stats.node.ScalarNode;
import game.stats.node.StatsNode;
import game.stores.pools.unit.UnitTemplate;
import logging.Logger;
import logging.LoggerFactory;

import java.util.*;

public class Statistics extends Component {
    private final Logger logger = LoggerFactory.instance().logger(getClass());
    private final Map<String, ScalarNode> map = new HashMap<>();
    public Statistics() { }
    public Statistics(UnitTemplate unitTemplate) { initialize(unitTemplate); }
    private void initialize(UnitTemplate unitTemplate) {
        for (String key : unitTemplate.stats.keySet()) {
            int value = unitTemplate.stats.get(key);
            map.put(key, new ScalarNode(value));
        }
    }

    public static Statistics builder() {
        return new Statistics();
    }
    public Statistics putScalar(String name, int value) { map.put(name, new ScalarNode(value)); return this; }
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
