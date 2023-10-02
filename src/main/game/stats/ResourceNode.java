package main.game.stats;

public class ResourceNode extends StatisticsNode {

    private int mCurrent = 0;

    public ResourceNode(String name, int base) {
        this(name, base, false);
    }

    public ResourceNode(String name, int base, boolean zero) {
        super(name, base);
        if (!zero) { add(base); }
    }

    public void add(int amount) {
        mCurrent += amount;

        int max = getTotal();
        if (mCurrent > max) { mCurrent = max; }
        if (mCurrent < 0) { mCurrent = 0; }
        mDirty = true;
    }

    public void setCurrent(int currentValue) { mCurrent = currentValue; mDirty = true; }
    public float getPercentage() { return (float) mCurrent / (float)getTotal(); }
    public int getCurrent() { return mCurrent; }
    public int getMissing() { return getTotal() - getCurrent(); }
    public boolean isLessThanMax() { return mCurrent < getTotal(); }
}
