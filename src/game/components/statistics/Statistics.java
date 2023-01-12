package game.components.statistics;

import game.components.Component;
import game.stats.node.ScalarNode;
import game.stats.node.StatsNode;
import game.stats.node.StringNode;
import game.stores.pools.unit.Unit;

import java.util.*;

public class Statistics extends Component {
    private final Map<String, StatsNode> stats = new HashMap<>();
    public Statistics() { }
//    public Statistics(Map<String, String> template) { subscribe(template); }
    public Statistics(Unit unit) { subscribe(unit); }
    public void subscribe(Unit unit) {
        stats.put("name", new StringNode(unit.name));
        stats.put("unique", new StringNode( unit.unique + ""));
        stats.put("health", new ScalarNode(unit.health));
        stats.put("energy", new ScalarNode(unit.energy));
        stats.put("physicalAttack", new ScalarNode(unit.physicalAttack));
        stats.put("physicalDefense", new ScalarNode(unit.physicalDefense));
        stats.put("magicalAttack", new ScalarNode(unit.magicalAttack));
        stats.put("magicalDefense", new ScalarNode(unit.magicalDefense));
        stats.put("speed", new ScalarNode(unit.speed));
        stats.put("jump", new ScalarNode(unit.jump));
        stats.put("move", new ScalarNode(unit.move));
    }

//    public void subscribe(Map<String, String> template) {
//        for (Map.Entry<String, String> property : template.entrySet()) {
//            StatsNode statValue;
//            String statName = property.getKey();
//            if (StringUtils.isNumber(property.getValue())) {
//                statValue = new ScalarNode(statName, Integer.parseInt(property.getValue()));
//            } else {
//                statValue = new StringNode(statName, property.getValue());
//            }
//            stats.put(statName, statValue);
//        }
//    }

    public static Statistics builder() {
        return new Statistics();
    }

    public Statistics putString(String name, String value) { stats.put(name, new StringNode(value)); return this; }

    public Statistics putScalar(String name, int value) { stats.put(name, new ScalarNode(value)); return this; }

    public ScalarNode getScalarNode(String name) { return (ScalarNode) stats.get(name); }
    public StringNode getStringNode(String name) { return (StringNode) stats.get(name); }
    public StatsNode getNode(String key) { return stats.get(key); }

    public Set<String> getNodeNames() { return stats.keySet(); }


    @Override
    public String toString() {
        return "Statistics{" +
                "stats=" + stats +
                '}';
    }
}
