package main.game.queue;

import java.util.List;

public abstract class GameQueue {
    public abstract String peek();
    public abstract void add(String entityID);
    public abstract String dequeue();
    public abstract List<String> order();
}
