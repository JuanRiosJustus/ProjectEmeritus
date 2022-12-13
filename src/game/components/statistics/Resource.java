package game.components.statistics;

import game.components.Component;
import game.stats.node.ScalarNode;

public class Resource extends Component {

    public String name;
    public ScalarNode node;
    public int current;

    public void apply(int amount) {
        current += amount;

        int max = node.getTotal();
        if (current > max) { current = max; }
        if (current < 0) { current = 0; }
    }

    public void subscribe(ScalarNode toSubscribeTo) {
        node = toSubscribeTo;
        current = toSubscribeTo.getTotal();
    }

    public float percentage() {
        return (float)current / (float)node.getTotal();
    }
}
