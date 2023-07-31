package main.game.stats.node;

public class StatsNodeModification {

    public final float value;
    public final String type;
    public final Object source;

    public StatsNodeModification(String type, float value) {
        this(null, type, value);
    }

    public StatsNodeModification(Object source, String type, float value) {
        this.value = value;
        this.type = type;
        this.source = source;
    }
}
