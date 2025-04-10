package main.constants;

import java.util.*;

public class HashSlingingSlasher {
    private static final String DEFAULT_KEY = "";
    private static final int DEFAULT_VALUE = 0;
    private final Map<String, Integer> mHashMap = new LinkedHashMap<>();
    private final Map<String, String> mRawMap = new LinkedHashMap<>();

    public boolean setOnDifference(Object... values) {
        int fastHash = fastHash(values); // Faster hashing
        int currentHash = mHashMap.getOrDefault(DEFAULT_KEY, 0);
        boolean isDifferentHash = fastHash != currentHash;
        if (isDifferentHash) { mHashMap.put(DEFAULT_KEY, fastHash); }
        return isDifferentHash;
    }


    public boolean isDifferent(Object... values) {
        int fastHash = fastHash(values); // Faster hashing
        int currentHash = mHashMap.getOrDefault(DEFAULT_KEY, 0);
        boolean isDifferentHash = fastHash != currentHash;
        return isDifferentHash;
    }

    public void setHash(Object... values) {
        int fastHash = fastHash(values); // Faster hashing
        mHashMap.put(DEFAULT_KEY, fastHash);
    }

    public int get() { return mHashMap.getOrDefault(DEFAULT_KEY, DEFAULT_VALUE); }

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