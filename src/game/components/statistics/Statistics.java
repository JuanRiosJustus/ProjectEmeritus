package game.components.statistics;

import constants.Constants;
import game.components.Component;
import game.stats.node.ScalarNode;
import game.stats.node.StatsNode;
import game.stores.pools.unit.UnitTemplate;

import java.util.*;

public class Statistics extends Component {
    private final Map<String, ScalarNode> map = new HashMap<>();
    public Statistics() { }
    public Statistics(UnitTemplate unitTemplate) { initialize(unitTemplate); }
    private void initialize(UnitTemplate unitTemplate) {
        for (String key : unitTemplate.stats.keySet()) {
            int value = unitTemplate.stats.get(key);
            map.put(key, new ScalarNode(value));
        }
    }
    public void addBonusStats(Object source, Statistics stats) {
        // Go over all the stats to boost
        for (String key : stats.map.keySet()) {
            StatsNode nodeToAdd = stats.getNode(key);
            if (nodeToAdd instanceof ScalarNode value) {
                   ScalarNode nodeToBeAddedTo = map.get(key);
                   nodeToBeAddedTo.add(source, Constants.FLAT, value.getTotal());
            }
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
}
