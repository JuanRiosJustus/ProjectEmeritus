package main.game.queue;

import com.alibaba.fastjson2.JSONArray;

import java.util.List;

public abstract class GameQueue {
    public abstract String peek();
    public abstract void add(String id);
    public abstract String dequeue();
    public abstract JSONArray turnOrder();
    public abstract JSONArray nextTurnOrder();
}
