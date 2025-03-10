package main.constants;

import java.util.*;

public class CheckSum {
    private static final String DEFAULT_KEY = "";
    private static final int DEFAULT_VALUE = 0;
    private final Map<String, Integer> mStateMap = new LinkedHashMap<>();
    private final Map<String, String> mRawMap = new LinkedHashMap<>();

    public boolean setDefault(Object... values) {
        return set(DEFAULT_KEY, values);
    }

    private boolean set(String key, Object... values) {
        int givenState = fastHash(values); // Faster hashing
        Integer currentState = mStateMap.get(key);

        if (currentState != null && currentState == givenState) {
            return false; // No change
        }

        mStateMap.put(key, givenState);
        mRawMap.put(key, Arrays.toString(values));
        return true;
    }

    public int getDefault() { return mStateMap.getOrDefault(DEFAULT_KEY, DEFAULT_VALUE); }
    public Object getDefaultRaw() { return mRawMap.getOrDefault(DEFAULT_KEY, null); }

    /**
     * 🔥 **Faster Hashing Function**
     * - Uses bitwise XOR (`^`) and prime multiplication.
     * - More efficient than `Objects.hash()`, avoids unnecessary array creation.
     */
    public static int fastHash(Object... values) {
        int hash = 1; // Start with non-zero value
        final int prime = 31; // Small prime multiplier

        for (Object value : values) {
            int elementHash = (value != null) ? value.hashCode() : 0;
            hash = hash * prime + (elementHash ^ (elementHash >>> 16)); // Mix bits for better distribution
        }

        return hash;
    }
}