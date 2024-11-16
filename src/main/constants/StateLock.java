package main.constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StateLock {
    private final LRUCache<String, Integer> mStateMap = new LRUCache<>();

    public boolean isUpdated(String key, Object... values) {
        // returns true if successfully added new state
        int givenState = Objects.hash(values);
        int currentState = mStateMap.getOrDefault(key, 0);

        if (givenState == currentState && mStateMap.containsKey(key)) {
            return false;
        }

        mStateMap.put(key, givenState);
        return true;
    }
}
