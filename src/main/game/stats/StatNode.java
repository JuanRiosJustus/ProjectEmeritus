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
    private JSONObject mAdditiveModifiers = null;
    private JSONObject mMultiplicativeModifiers = null;
    private JSONObject mExponentialModifiers = null;
    private boolean mDirty;
    public StatNode(String name) { this(name, 0); }

    public StatNode(String name, float base) {
        mAdditiveModifiers = new JSONObject();
        put(ADDITIVE, mAdditiveModifiers);

        mMultiplicativeModifiers = new JSONObject();
        put(MULTIPLICATIVE, mMultiplicativeModifiers);

        mExponentialModifiers = new JSONObject();
        put(EXPONENTIAL, mExponentialModifiers);

        put(CURRENT_KEY, base);
        put(BASE_KEY, base);
        put(MODIFIED_KEY, 0);
        put(NAME_KEY, name);

        mDirty = true;
    }

    public void putAdditiveModification(String name, float value) {
        putModification(ADDITIVE, null, name, value);
    }

    public void putMultiplicativeModification(String name, float value) {
        putModification(MULTIPLICATIVE, null, name, value);
    }

    public void putExponentialModification(String name, float value) {
        putModification(EXPONENTIAL, null, name, value);
    }


    private void putModification(String bucketName, String source, String name, float value) {
        switch (bucketName) {
            case ADDITIVE -> mAdditiveModifiers.put(name, value);
            case MULTIPLICATIVE -> mMultiplicativeModifiers.put(name, value);
            case EXPONENTIAL -> mExponentialModifiers.put(name, value);
        }

        mDirty = true;
    }


    public int getBaseModifiedOrTotal(String key) {
        float result = -1;
        if (key.equalsIgnoreCase(BASE_KEY)) {
            result = getBase();
        } else if (key.equalsIgnoreCase(MODIFIED_KEY)) {
            result = getModified();
        } else if (key.equalsIgnoreCase(TOTAL_KEY)) {
            result = getTotal();
        }

        return (int) result;
    }

    public void adjustCurrent(float amount) { setCurrent(getCurrent() + amount); }
    public int getCurrent() { return (int) getFloat(CURRENT_KEY); }
    public int getBase() { return (int) getFloat(BASE_KEY); }
    public int getModified() { handleDirtiness(); return (int) getFloat(MODIFIED_KEY); }
    public int getTotal() { handleDirtiness(); return (int) getFloat(TOTAL_KEY); }
    public void setBase(float value) { put(BASE_KEY, value); mDirty = true; }

    public void setCurrent(float value) {
        float clampedValue = Math.max(0, Math.min(value, getTotal())); // Clamp between 0 and total
        put(CURRENT_KEY, clampedValue);
    }

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

    private void handleDirtiness() {
        // If the stat is already clean, no need to recalculate
        if (!mDirty) { return; }

        // Retrieve the base value
        float base = getFloat(BASE_KEY);

        // Calculate the additive, multiplicative, and exponential modifiers
        float additiveSum = calculateAdditive(); // Sum of flat modifiers
        float multiplicativeFactor = calculateMultiplicative(); // Product of (1 + modifiers)
        float exponentialFactor = calculateExponential(); // Product of all exponential factors

        // Apply additive modifiers to the base
        float baseWithAdditive = base + additiveSum;

        // Apply multiplicative modifiers to the result
        float baseWithAdditiveAndMultiplicative = baseWithAdditive * multiplicativeFactor;

        // Apply exponential modifiers to the result
        float total = baseWithAdditiveAndMultiplicative * exponentialFactor;

        // Update the JSON object with the recalculated values
        put(BASE_KEY, base); // Base value remains unchanged
        put(MODIFIED_KEY, total - base); // Modified value is the total minus base
        put(TOTAL_KEY, total); // Total is the final calculated value

        // Mark the stat as clean
        mDirty = false;
    }



    private float calculateAdditive() {
        float additiveSum = 0f;

        // Add up all additive modifiers
        JSONObject bucket = mAdditiveModifiers;
        for (String key : bucket.keySet()) {
            float value = bucket.getFloat(key);
            additiveSum += value;
        }

        return additiveSum;
    }

    private float calculateMultiplicative() {
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

    private float calculateExponential() {
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
}
