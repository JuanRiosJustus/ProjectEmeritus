package game.collectibles;

import game.components.statistics.Statistics;

public class Gem {
    public enum Type {
        DEBUFF,
        HEALTH,
        ENERGY,
        CRIT,
        SPEED,
        MYSTERY;
    }
    public Statistics statistics;
    public Gem.Type type;
    public int animationId;
}
