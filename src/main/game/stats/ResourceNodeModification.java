package main.game.stats;

public class ResourceNodeModification {

    private final float mValue;
    private final Object mSource;

    public ResourceNodeModification(Object source, float value) {
        mValue = value;
        mSource = source;
    }

    public Object getSource() { return mSource; }
    public float getValue() { return mValue; }

}
