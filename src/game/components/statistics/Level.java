package game.components.statistics;

import game.components.Component;

public class Level extends Component {

    public int experience = 0;
    public int threshold = 10;
    public int level = 1;

    public void gain(int amount) {
        experience += amount;

        while (experience >= threshold) {
            level++;
            experience = experience - threshold;
            threshold = (threshold * 2) + 5;
        }
    }
}
