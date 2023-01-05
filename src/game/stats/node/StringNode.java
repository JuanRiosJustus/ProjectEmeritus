package game.stats.node;

public class StringNode extends StatsNode {
    public String value;
    public StringNode(String nodeValue) {
        value = nodeValue;
    }
    @Override
    public String toString() {
        return "StringNode{" +
                "value='" + value + '\'' +
                '}';
    }
}
