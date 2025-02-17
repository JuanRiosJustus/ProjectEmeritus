package main.game.stats;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class StatisticNode extends JSONObject {
    public static final String ADDITIVE = "additive";
    public static final String MULTIPLICATIVE = "multiplicative";
    private static final String BASE_KEY = "base";
    private static final String MISSING_KEY = "missing";
    private static final String MODIFIED_KEY = "modification";
    private static final String CURRENT_KEY = "current";
    private static final String TOTAL_KEY = "total";
    private static final String MAX_KEY = "max";
    private static final String NAME_KEY = "Name";
    private static final String LIFE_TIME_MAP = "Life_time_map";
    private static final String AGE_MAP = "age_map";
    private JSONObject mAdditiveModifiers = null;
    private JSONObject mMultiplicativeModifiers = null;
    private JSONObject mExponentialModifiers = null;
    private JSONObject mDurationMapping = null;
    private JSONObject mAgeMapping = null;
    private boolean mDirty;
    private int mHashState = 0;
    private boolean mCurrentIsInUse = false;
    private Queue<String> mEphemeral = new LinkedList<>();
    public StatisticNode(String name) { this(name, 0); }

    public StatisticNode(String name, float base) {
        mAdditiveModifiers = new JSONObject();
        put(ADDITIVE, mAdditiveModifiers);

        mMultiplicativeModifiers = new JSONObject();
        put(MULTIPLICATIVE, mMultiplicativeModifiers);

        mDurationMapping = new JSONObject();
        put(LIFE_TIME_MAP, mDurationMapping);

        mAgeMapping = new JSONObject();
        put(AGE_MAP, mAgeMapping);

        put(CURRENT_KEY, base);
        put(BASE_KEY, base);
        put(MODIFIED_KEY, 0);
        put(NAME_KEY, name);

        mDirty = true;
    }

    public void putAdditiveModification(String source, float value) {
        putModification(ADDITIVE, source, value);
    }

    public void putMultiplicativeModification(String source, float value) {
        putModification(MULTIPLICATIVE, source, value);
    }

    public void putAdditiveModification(String source, float value, int duration) {
        putModification(ADDITIVE, source, value, duration);
    }

    public void putMultiplicativeModification(String source, float value, int duration) {
        putModification(MULTIPLICATIVE, source, value, duration);
    }

    public void putModification(String type, String source, float value) {
        putModification(type, source, value, -1);
    }
    public void putModification(String type, String source, float value, int duration) {
        switch (type) {
            case ADDITIVE -> mAdditiveModifiers.put(source, value);
            case MULTIPLICATIVE -> mMultiplicativeModifiers.put(source, value);
        }

        mAgeMapping.put(source, 0);
        mDurationMapping.put(source, duration);
        mDirty = true;
    }


    public float getScaling(String type) {
        float result = -1f;
        switch (type) {
            case BASE_KEY -> result = getBase();
            case MODIFIED_KEY -> result = getModified();
            case TOTAL_KEY, MAX_KEY -> result = getBase() + getModified();
            case CURRENT_KEY -> result = getCurrent();
            case MISSING_KEY -> result = getMissing();
        }
        return result;
    }

    public void updateAges() {
        for (String key : mAgeMapping.keySet()) {
            int currentAge = mAgeMapping.getInt(key);
            int duration = mDurationMapping.getInt(key);

            mAgeMapping.put(key, currentAge + 1);

            if (duration != -1 && currentAge >= duration) { mEphemeral.add(key); }
        }

        // Remove buffs that are too old
        while (!mEphemeral.isEmpty()) {
            String key = mEphemeral.poll();
            mAgeMapping.remove(key);
            mDurationMapping.remove(key);
            mAdditiveModifiers.remove(key);
            mMultiplicativeModifiers.remove(key);
            mDirty = true;
        }
    }

    public int getAge(String source) { return mAgeMapping.optInt(source, -1); }
    public int getDuration(String source) { return mDurationMapping.optInt(source, -1); }
//    public int getBaseModifiedOrTotal(String key) {
//        float result = -1;
//        if (key.equalsIgnoreCase(BASE_KEY)) {
//            result = getBase();
//        } else if (key.equalsIgnoreCase(BONUS_KEY)) {
//            result = getBonus();
//        } else if (key.equalsIgnoreCase(TOTAL_KEY)) {
//            result = getTotal();
//        }
//
//        return (int) result;
//    }

//    public int getCurrentMissingTotal(String key) {
//        float result = -1;
//        if (key.contains(CURRENT_KEY)) {
//            result = getCurrent();
//        } else if (key.contains(MISSING_KEY)) {
//            result = getMissing();
//        } else if (key.contains(TOTAL_KEY)) {
//            result = getTotal();
//        }
//
//        return (int) result;
//    }

    public int getCurrent() { return (int) getFloat(CURRENT_KEY); }
    public int getBase() { return (int) getFloat(BASE_KEY); }
    public int getModified() { handleDirtiness(); return (int) getFloat(MODIFIED_KEY); }
    public int getTotal() { handleDirtiness(); return (int) getFloat(TOTAL_KEY); }
    public void setBase(float value) { put(BASE_KEY, value); mDirty = true; }

    public void setCurrent(float value) {
        mCurrentIsInUse = true;
        float clampedValue = Math.max(0, Math.min(value, getTotal())); // Clamp between 0 and total
        put(CURRENT_KEY, clampedValue);
    }

    public int getMissing() { return getTotal() - getCurrent(); }

    public float getMissingPercent() {
        float total = getTotal() * 1f;
        float current = getCurrent() * 1f;
        return (total - current) / total;
    }

    public float getCurrentPercent() {
        float total = getTotal() * 1f;
        float current = getCurrent() * 1f;
        return current / total;
    }

    public float getTotalPercent() {
        float missingPercent = getMissingPercent();
        float currentPercent = getCurrentPercent();
        return currentPercent + missingPercent; // This should always equal 1.0f
    }


    private void handleDirtiness() {
        if (!mDirty) { return; }

        float base = getFloat(BASE_KEY);

        // Calculate Additive and Multiplicative Modifiers
        float additiveSum = calculateAdditiveModifiers();
        float multiplicativeFactor = calculateMultiplicativeModifiers();

        // Apply Additive First
        float baseWithAdditive = base + additiveSum;

        // Apply Multiplicative
        float total = baseWithAdditive * multiplicativeFactor;

        // Store the proper values
        put(BASE_KEY, base);
        put(MODIFIED_KEY, total - base); // ✅ Now correctly represents buffs/debuffs
        put(TOTAL_KEY, total);

        if (!mCurrentIsInUse) {
            put(CURRENT_KEY, total);
        }

        mDirty = false;
    }

//    private void handleDirtiness() {
//        // If the stat is already clean, no need to recalculate
//        if (!mDirty) { return; }
//
//        // Retrieve the base value
//        float base = getFloat(BASE_KEY);
//
//        // Calculate the additive, multiplicative, and exponential modifiers
//        float additiveSum = calculateAdditive(); // Sum of flat modifiers
//        float multiplicativeFactor = calculateMultiplicative(); // Product of (1 + modifiers)
//        float exponentialFactor = calculateExponential(); // Product of all exponential factors
//
//        // Apply additive modifiers to the base
//        float baseWithAdditive = base + additiveSum;
//
//        // Apply multiplicative modifiers to the result
//        float baseWithAdditiveAndMultiplicative = baseWithAdditive * multiplicativeFactor;
//
//        // Apply exponential modifiers to the result
//        float total = baseWithAdditiveAndMultiplicative * exponentialFactor;
//
//        // Update the JSON object with the recalculated values
//        put(BASE_KEY, base); // Base value remains unchanged
//        put(MODIFIED_KEY, total - base); // Modified value is the total minus base
//        put(TOTAL_KEY, total); // Total is the final calculated value
//
//        if (!mCurrentIsInUse) {
//            put(CURRENT_KEY, total);
//        }
//
//        // Mark the stat as clean
//        mDirty = false;
//    }



    private float calculateAdditiveModifiers() {
        float additiveSum = 0f;

        // Add up all additive modifiers
        JSONObject bucket = mAdditiveModifiers;
        for (String key : bucket.keySet()) {
            float value = bucket.getFloat(key);
            additiveSum += value;
        }

        return additiveSum;
    }

    private float calculateMultiplicativeModifiers() {
        float multiplicativeFactor = 1f;

        // Multiply each modifier as (1 + modifier) to apply percentage increase/decrease
        JSONObject bucket = mMultiplicativeModifiers;
        // Convert modifier to factor (e.g., +20% becomes 1.2)
        for (String key : bucket.keySet()) {
            float value = bucket.getFloat(key);
            multiplicativeFactor *= (1 + value);
        }

        return multiplicativeFactor;
    }

    private float calculateExponentialModifiers() {
        float exponentialFactor = 1f;

        // Multiply all exponential modifiers directly
        JSONObject bucket = mExponentialModifiers;
        // Convert modifier to factor (e.g., +20% becomes 1.2)
        for (String key : bucket.keySet()) {
            float value = bucket.getFloat(key);
            exponentialFactor *= (value);
        }

        return exponentialFactor;
    }

    public boolean isDirty() {
        return mDirty;
    }
    public int hashState() {
        int base = getBase(); // Base value remains unchanged
        int modification = getModified(); // Modified value is the total minus base
        int current = getCurrent();
        return Objects.hash(base, modification, current);
    }
}
