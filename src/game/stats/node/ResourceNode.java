package game.stats.node;

public class ResourceNode extends ScalarNode {

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

    public float percentage() { return (float)current / (float)getTotal(); }
}
