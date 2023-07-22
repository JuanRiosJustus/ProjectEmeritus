package game.stats.node;

public class ResourceNode extends StatsNode {

    public int current;

    public ResourceNode(String nodeName, int baseValue) {
        super(nodeName, baseValue);
        current = baseValue;
    }

    public void apply(int amount) {
        current += amount;

        int max = getTotal();
        if (current > max) { current = max; }
        if (current < 0) { current = 0; }
    }

    public float getPercentage() { return (float)current / (float)getTotal(); }
    public int getCurrent() { return current; }
    public boolean isLessThanMax() { return current < getTotal(); }
}
