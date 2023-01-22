package game.components.statistics;

public enum Stat {
    Health,
    Energy,
    PhysicalAttack,
    PhysicalDefense,
    MagicalAttack,
    MagicalDefense,
    Speed,
    Jump,
    Move;
    public static Stat getValueOf(String value) {
        value = value.toLowerCase();
        for (Stat key : values()) {
            if (!key.toString().toLowerCase().equals(value)) { continue; }
            return key;
        }
        return null;
    }
}
