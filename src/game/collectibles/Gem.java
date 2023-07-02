package game.collectibles;

import game.components.statistics.Summary;

public class Gem {
    public enum Type {
        DEBUFF,
        HEALTH,
        ENERGY,
        CRIT,
        SPEED,
        MYSTERY;
    }
    public Summary statistics;
    public Gem.Type type;
    public int animationId;
}
