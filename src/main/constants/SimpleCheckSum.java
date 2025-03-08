package main.constants;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class SimpleCheckSum {
    private static final String DEFAULT_KEY = "";
    private static final int DEFAULT_VALUE = 0;
    private final Map<String, Integer> mStateMap = new LinkedHashMap<>();
    public boolean update(Object... values) { return update(DEFAULT_KEY, values); }
    public boolean update(String key, Object... values) {
        // returns true if successfully added new state
        int givenState = Objects.hash(values);
        int currentState = mStateMap.getOrDefault(key, DEFAULT_VALUE);

        if (givenState == currentState && mStateMap.containsKey(key)) {
            return false;
        }
        mStateMap.put(key, givenState);
        return true;
    }

    public int get(String key) { return mStateMap.getOrDefault(key, DEFAULT_VALUE); }
    public int get() { return get(DEFAULT_KEY); }
}
