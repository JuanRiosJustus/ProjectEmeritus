package main.game.collectibles;

import main.game.components.Statistics;

public class Gem {
    public enum GemType {
        RESET,
        HEALTH,
        ENERGY,
        CRIT,
        SPEED,
        MYSTERY;
    }
    public Statistics statistics;
    public Gem.GemType type;
    public int animationId;

    public String toString() {
        return type.name();
    }
}
