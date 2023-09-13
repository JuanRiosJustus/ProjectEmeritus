package main.game.stats;

public class Modification {

    private final float mValue;
    private final Object mSource;

    public Modification(Object source, float value) {
        mValue = value;
        mSource = source;
    }

    public Object getSource() { return mSource; }
    public float getValue() { return mValue; }

}
