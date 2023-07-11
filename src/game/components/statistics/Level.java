package game.components.statistics;

import game.components.Component;
import game.stats.node.StatsNode;

public class Level extends Component {

    public int experience = 0;
    public int threshold = 10;
    public int current = 1;

    public void gain(int amount) {
        experience += amount;

        while (experience >= threshold) {
            current++;
            experience = experience - threshold;
            threshold = (threshold * 2) + 5;
        }
    }

    public void subscribe(StatsNode toSubscribeTo) {
        current = toSubscribeTo.getTotal();
    }
}
