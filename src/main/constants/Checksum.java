package main.constants;

import java.util.*;

public class Checksum {
    private static final String DEFAULT_KEY = "";
    private static final int DEFAULT_VALUE = 0;
    private final Map<String, Integer> mStateMap = new LinkedHashMap<>();
    private final Map<String, String> mRawMap = new LinkedHashMap<>();

    public boolean set(Object... values) {
        int givenState = fastHash(values); // Faster hashing
        Integer currentState = mStateMap.get(Checksum.DEFAULT_KEY);

        if (currentState != null && currentState == givenState) {
            return false; // No change
        }

        mStateMap.put(Checksum.DEFAULT_KEY, givenState);
        mRawMap.put(Checksum.DEFAULT_KEY, Arrays.toString(values));
        return true;
    }

    public int get() { return mStateMap.getOrDefault(DEFAULT_KEY, DEFAULT_VALUE); }

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