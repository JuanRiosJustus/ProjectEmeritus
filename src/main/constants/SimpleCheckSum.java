package main.constants;

import java.util.Objects;

public class SimpleCheckSum {

    private int mSum = 0;
    public boolean update(Object... values) {
        int sum = Objects.hash(values);
        boolean hasChanged = sum != mSum;
        mSum = sum;
        return hasChanged;
    }

    public int get() { return mSum; }







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
    public int getHashState() { return mStateMap.hashCode(); }
}
