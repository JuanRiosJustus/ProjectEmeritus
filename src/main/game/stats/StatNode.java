package main.game.stats;

import org.json.JSONObject;

import java.util.*;

public class StatNode extends JSONObject {
    public static final String ADDITIVE = "additive";
    public static final String MULTIPLICATIVE = "multiplicative";
    public static final String EXPONENTIAL = "exponential";

    private static final String BASE_KEY = "Base";
    private static final String MODIFIED_KEY = "Bonus";
    private static final String CURRENT_KEY = "Current";
    private static final String TOTAL_KEY = "Total";
    private static final String NAME_KEY = "Name";
    private static final Queue<String> trashQueue = new LinkedList<>();
    private final JSONObject modificationToBucketMap = new JSONObject();
    private boolean mDirty;
    public StatNode(String name) { this(name, 0); }

    public StatNode(String name, float base) {
        put(ADDITIVE, new JSONObject());
        put(MULTIPLICATIVE, new JSONObject());

        put(CURRENT_KEY, 0);
        put(BASE_KEY, base);
        put(MODIFIED_KEY, 0);
        put(NAME_KEY, name);

        mDirty = true;
    }

    public void putAdditiveModification(String source, String name, float value, int duration) {
        putModification(ADDITIVE, source, name, value, duration);
    }

    public void putMultiplicativeModification(String source, String name, float value, int duration) {
        putModification(MULTIPLICATIVE, source, name, value, duration);
    }


    private void putModification(String bucketName, String source, String name, float value, int duration) {

        if (modificationToBucketMap.opt(name) != null) { return; }

        JSONObject modification = new Modification(source, name, value, duration);

        modificationToBucketMap.put(name, bucketName);
        JSONObject bucket = getJSONObject(bucketName);
        bucket.put(name, modification);

        mDirty = true;
    }

    public void updateDurations() {
        trashQueue.clear();
        for (String buffOrDebuffName : modificationToBucketMap.keySet()) {
            String bucketName = modificationToBucketMap.getString(buffOrDebuffName);
            JSONObject bucket = getJSONObject(bucketName);
            Modification modification = (Modification) bucket.getJSONObject(buffOrDebuffName);
            int duration = modification.getDuration() - 1;
            modification.putDuration(duration);
            if (duration >= 0) { continue; }
            trashQueue.add(buffOrDebuffName);
        }
        while (!trashQueue.isEmpty()) {
            String modificationName = trashQueue.poll();
            String bucketName = modificationToBucketMap.getString(modificationName);
            modificationToBucketMap.remove(modificationName);
            JSONObject bucket = getJSONObject(bucketName);
            bucket.remove(modificationName);
            modificationToBucketMap.remove(modificationName);
            mDirty = true;
        }
    }

    public void removeBuffOrDebuffByName(String name) {

        String bucketName = modificationToBucketMap.getString(name);
        JSONObject bucket = getJSONObject(bucketName);
        bucket.remove(name);
        modificationToBucketMap.remove(name);

        mDirty = true;
    }

    public void removeBuffOrDebuffBySource(String source) {
        trashQueue.clear();
        for (String modificationName : modificationToBucketMap.keySet()) {
            JSONObject bucket = getJSONObject(modificationToBucketMap.getString(modificationName));
            Modification modification = (Modification) bucket.getJSONObject(modificationName);
            if (!modification.getSource().equalsIgnoreCase(source)) { continue; }
            trashQueue.add(modificationName);
        }

        for (String modificationName : trashQueue) {
            removeBuffOrDebuffByName(modificationName);
        }

        mDirty = true;
    }

    public int getDuration(String buffOrDebuff) {
        if (!modificationToBucketMap.keySet().contains(buffOrDebuff)) { return -1; }
        String bucketName = modificationToBucketMap.getString(buffOrDebuff);
        JSONObject bucket = getJSONObject(bucketName);
        Modification modification = (Modification) bucket.getJSONObject(buffOrDebuff);

        int duration = modification.getDuration();

        return duration;
    }

    public void clear() {
        JSONObject bucket = getJSONObject(MULTIPLICATIVE);
        bucket.clear();

        bucket = getJSONObject(ADDITIVE);
        bucket.clear();

        mDirty = true;
    }

    public int getBaseModifiedOrTotal(String key) {
        int result = -1;
        if (key.equalsIgnoreCase(BASE_KEY)) {
            result = getBase();
        } else if (key.equalsIgnoreCase(MODIFIED_KEY)) {
            result = getModified();
        } else if (key.equalsIgnoreCase(TOTAL_KEY)) {
            result = getTotal();
        }

        return result;
    }

    public int getCurrent() {
        return getInt(CURRENT_KEY);
    }
    public int getBase() { handleDirtiness(); return getInt(BASE_KEY); }
    public int getModified() { handleDirtiness(); return getInt(MODIFIED_KEY); }
    public int getTotal() { handleDirtiness(); return getInt(TOTAL_KEY); }

    public void setBase(int value) { put(BASE_KEY, value); mDirty = true; }
    public void setCurrent(int newValue) { put(CURRENT_KEY, newValue); }

    public float getMissingPercent() {
        float total = getTotal();
        float current = getCurrent();
        float missingHealth = total - current;
        float percent = (missingHealth / total);
        return percent;
    }

    public float getCurrentPercent() {
        return 1 - getMissingPercent();
    }


    private void handleDirtiness() {
        if (!mDirty) { return; }
        int base = getInt(BASE_KEY);
        int modified = calculateModified();
        int total = base + modified;

        put(BASE_KEY, base);
        put(MODIFIED_KEY, modified);
        put(TOTAL_KEY, total);

        mDirty = false;
    }

    private int calculateModified() {
        float base = getFloat(BASE_KEY);

        // calculate the flat values first
        float additiveSum = 0;
        JSONObject additiveMap = getJSONObject(ADDITIVE);
        for (String key : additiveMap.keySet()) {
            Modification modification = (Modification) additiveMap.getJSONObject(key);
            additiveSum += modification.getValue();
        }

        // get pre total percentage values
        float multiplicativeSum = 0;
        JSONObject multiplicativeMap = getJSONObject(MULTIPLICATIVE);
        for (String key : multiplicativeMap.keySet()) {
            Modification modification = (Modification) multiplicativeMap.getJSONObject(key);
            multiplicativeSum += modification.getValue();
        }

        // calculate total after adding flat and base
        float baseAndAdditiveTotal = base + additiveSum;
        float multiplicativeTotal = baseAndAdditiveTotal * multiplicativeSum;
        float postTotal = baseAndAdditiveTotal + multiplicativeTotal;

        return (int) (postTotal - base);
    }
}
