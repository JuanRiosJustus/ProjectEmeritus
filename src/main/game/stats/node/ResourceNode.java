package main.game.stats.node;

public class ResourceNode extends StatsNode {

    private int current = 0;

    public ResourceNode(String name, int value) {
        super(name, value);
        add(value);
    }

    public void add(int amount) {
        current += amount;

        int max = getTotal();
        if (current > max) { current = max; }
        if (current < 0) { current = 0; }
        dirty = true;
    }

    public void setCurrent(int currentValue) { current = currentValue; dirty = true; }
    public float getPercentage() { return (float)current / (float)getTotal(); }
    public int getCurrent() { return current; }
    public int getMissing() { return getTotal() - getCurrent(); }
    public boolean isLessThanMax() { return current < getTotal(); }
}
