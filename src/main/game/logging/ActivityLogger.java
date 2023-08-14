package main.game.logging;

import java.util.LinkedList;
import java.util.Queue;

public class ActivityLogger {

    private final Queue<String> queue = new LinkedList<>();

    public void log(Object source, String text) {
        queue.add("[" + source.toString() + "] " + text);
    }

    public void log(String text) { queue.add(text); }

    public boolean isEmpty() { return queue.isEmpty(); }

    public String peek() { return queue.peek(); }

    public int size() { return queue.size(); }

    public String poll() { return queue.poll(); }
}
