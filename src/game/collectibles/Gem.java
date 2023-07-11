package game.collectibles;

import game.components.statistics.Summary;

public class Gem {
    public enum GemType {
        RESET,
        HEALTH,
        ENERGY,
        CRIT,
        SPEED,
        MYSTERY;
    }
    public Summary statistics;
    public Gem.GemType type;
    public int animationId;

    public String toString() {
        return type.name();
    }
}
