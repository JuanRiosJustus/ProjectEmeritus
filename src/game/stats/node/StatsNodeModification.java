package game.stats.node;

public class StatsNodeModification {

    public final float value;
    public final String type;
    public final Object source;

    public StatsNodeModification(Object modSource, String modType, float modValue) {
        value = modValue;
        type = modType;
        source = modSource;

    }
}
