package main.constants;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int mMaxSize;
    private static final int DEFAULT_MAX_SIZE = 1000;
    // Constructor that takes the maximum size of the cache
    public LRUCache() { this(DEFAULT_MAX_SIZE); }
    // Constructor that takes the maximum size of the cache
    public LRUCache(int maxSize) {
        super(maxSize, 0.75f, true); // `true` for access-order
        mMaxSize = maxSize;
    }

    // Override to define when to remove the eldest entry
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > mMaxSize;
    }

    // Example of how to use put and get with the cache
    public static void main(String[] args) {
        LRUCache<Integer, String> cache = new LRUCache<>(3); // Cache size is 3
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");
        System.out.println("Cache: " + cache);

        cache.get(1); // Access key 1 to make it "recently used"
        cache.put(4, "Four"); // This will evict the least recently used item (key 2)

        System.out.println("Cache after adding 4: " + cache); // Expected to contain keys 1, 3, 4
    }
}
