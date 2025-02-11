package main.game.stats;

import org.json.JSONObject;

import java.util.Objects;

public class StatisticNode extends JSONObject {
    public static final String ADDITIVE = "additive";
    public static final String MULTIPLICATIVE = "multiplicative";
    public static final String EXPONENTIAL = "exponential";
    private static final String BASE_KEY = "base";
    private static final String MISSING_KEY = "missing";
    private static final String MODIFIED_KEY = "modification";
    private static final String CURRENT_KEY = "current";
    private static final String TOTAL_KEY = "total";
    private static final String MAX_KEY = "max";
    private static final String NAME_KEY = "Name";
    private JSONObject mAdditiveModifiers = null;
    private JSONObject mMultiplicativeModifiers = null;
    private JSONObject mExponentialModifiers = null;
    private boolean mDirty;
    private int mHashState = 0;
    private boolean mCurrentIsInUse = false;
    public StatisticNode(String name) { this(name, 0); }

    public StatisticNode(String name, float base) {
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

    public void putAdditiveModification(String source, float value) {
        putModification(ADDITIVE, source, value);
    }

    public void putMultiplicativeModification(String source, float value) {
        putModification(MULTIPLICATIVE, source, value);
    }
    public void putExponentialModification(String source, float value) {
        putModification(EXPONENTIAL, source, value);
    }




    public void putModification(String source, String modification, float value) {
        switch (modification) {
            case ADDITIVE -> mAdditiveModifiers.put(source, value);
            case MULTIPLICATIVE -> mMultiplicativeModifiers.put(source, value);
            case EXPONENTIAL -> mExponentialModifiers.put(source, value);
        }

        mDirty = true;
    }


    public int getScaling(String key) {
        float result = -1f;
        switch (key) {
            case BASE_KEY -> result = getBase();
            case MODIFIED_KEY -> result = getModified();
            case TOTAL_KEY, MAX_KEY -> result = getBase() + getModified();
            case CURRENT_KEY -> result = getCurrent();
            case MISSING_KEY -> result = getMissing();
        }
        return (int) result;
    }
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
    public int getTotal() { handleDirtiness(); return getBase() + getModified(); }
    public void setBase(float value) { put(BASE_KEY, value); mDirty = true; }

    public void setCurrent(float value) {
        float clampedValue = Math.max(0, Math.min(value, getTotal())); // Clamp between 0 and total
        put(CURRENT_KEY, clampedValue);
    }

    public int getMissing() {
        return getTotal() - getCurrent();
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

    public float getTotalPercent() {
        float missingPercent = getMissingPercent();
        float currentPercent = getCurrentPercent();
        return currentPercent + missingPercent; // This should always equal 1.0f
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

        if (!mCurrentIsInUse) {
            put(CURRENT_KEY, total);
        }

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
