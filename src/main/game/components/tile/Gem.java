package main.game.components.tile;

import main.game.components.Statistics;

public enum Gem {

//    public enum Type {
//        RESET,
//        HEALTH_RESTORE,
//        ENERGY_RESTORE,
//        PHYSICAL_BUFF,
//        MAGICAL_BUFF,
//        CRITICAL_BUFF,
//        SPEED_BUFF
//    }
//    public Type type;

    RESET,
    HEALTH_RESTORE,
    ENERGY_RESTORE,
    PHYSICAL_BUFF,
    MAGICAL_BUFF,
    CRITICAL_BUFF,
    SPEED_BUFF;
    public int animationId;
//    public Gem() { type = Type.values()[0]; }
//    public Gem() {
//        type = Type.
//    }

    public String toString() {
        return name();
    }
}
