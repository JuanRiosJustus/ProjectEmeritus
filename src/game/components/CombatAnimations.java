package game.components;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class CombatAnimations extends Component {

    static class Node {
        String name;
        Animation animation;
        public Node(String n, Animation sa) {
            name = n;
            animation = sa;
        }
    }

    private final Queue<Node> animations = new LinkedList<>();
    private final Map<String, Node> nameToAnimationMap = new HashMap<>();

    public Animation getCurrentAnimation() {
        Node n = animations.peek();
        return (n == null ? null : n.animation);
    }

    public void add(String name, Animation animation) {
        if (nameToAnimationMap.containsKey(name)) { return; }
        Node n = new Node(name, animation);
        nameToAnimationMap.put(name, n);
        animations.add(n);
    }

    public int count() { return animations.size(); }

    public void poll() {
        Node n = animations.poll();
        if (n != null) {
            nameToAnimationMap.remove(n.name);
        }
    }
}
