package main.game.foundation;

import java.util.*;

public class LootTable<T> {

    private static class Drop<T> {
        private final T item;
        private final float probability;
        private Drop(T t, float p) {
            item = t;
            probability = p;
        }
    }

    private static final Random random = new Random();
    private final Map<T, Float> table = new HashMap<>();

    public T getDrop() {
        float roll = random.nextFloat(1);
        for (Map.Entry<T, Float> entry : table.entrySet()) {
            roll -= entry.getValue();
            if (roll > 0) { continue; }
            return entry.getKey();
        }
        return null;
    }

    public void add(T item, float chance) {
        table.put(item, chance);
    }
}
