package game.stats.node;

public class ScalarNodeModifier {

    public final float value;
    public final String type;
    public final Object source;

    public ScalarNodeModifier(Object modSource, String modType, float modValue) {
        value = modValue;
        type = modType;
        source = modSource;

    }

    @Override
    public String toString() {
        return "ScalarNodeModifier{" +
                "value=" + value +
                ", type='" + type + '\'' +
                ", source=" + source +
                '}';
    }
}
