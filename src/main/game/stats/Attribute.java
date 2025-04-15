package main.game.stats;

import main.constants.HashSlingingSlasher;
import org.json.JSONObject;

import java.util.*;

public class Attribute {
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
    private JSONObject mDurationMapping = null;
    private JSONObject mAgeMapping = null;
    private boolean mDirty;
    private boolean mCurrentIsInUse = false;
    private Queue<String> mEphemeral = new LinkedList<>();
    private String mName = "";
    private float mCurrent = 0f;
    private float mBase = 0f;
    private float mTotal = 0f;
    private int mHashCode = 0;
    private Map<String, Float> mAdditives = new LinkedHashMap<>();
    private Map<String, Float> mMultiplicatives = new LinkedHashMap<>();

    public Attribute(String name) { this(name, 0); }
    public Attribute(String name, float base) {
        mName = name;
        mBase = base;
        mCurrent = mBase;
        mAdditives = new LinkedHashMap<>();
        mMultiplicatives = new LinkedHashMap<>();

        mDirty = true;
    }

    public void putAdditiveModification(String source, float value, int duration) {
        putModification(ADDITIVE, source, value, duration);
    }
    public void putMultiplicativeModification(String source, float value, int duration) {
        putModification(MULTIPLICATIVE, source, value, duration);
    }

    public void putAdditiveModification(String source, float value) {
        putModification(ADDITIVE, source, value, -1);
    }
    public void putMultiplicativeModification(String source, float value) {
        putModification(MULTIPLICATIVE, source, value, -1);
    }

    public void putModification(String modType, String source, float value) {
        putModification(modType, source, value, -1);
    }

    public void putModification(String modification, String source, float value, int duration) {

        switch (modification) {
            case ADDITIVE -> mAdditives.put(source, value);
            case MULTIPLICATIVE -> mMultiplicatives.put(source, value);
        }
//        switch (modType) {
//            case ADDITIVE -> mAdditiveModifiers.put(source, value);
//            case MULTIPLICATIVE -> mMultiplicativeModifiers.put(source, value);
//        }
//
//        mAgeMapping.put(source, 0);
//        mDurationMapping.put(source, duration);
        mDirty = true;
//        // We can update the hash here because its not going to be updated outside of this
//
//        int base = getBase(); // Base value remains unchanged
//        int modification = getModified(); // Modified value is the total minus base
//        int current = getCurrent();
//        String additiveState = mAdditiveModifiers.toString();
//        String multiplicativeState = mMultiplicativeModifiers.toString();
//        mChecksum.hasChanged(base, modification, current, additiveState, multiplicativeState);
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
//            mAdditiveModifiers.remove(key);
//            mMultiplicativeModifiers.remove(key);
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

    public int getCurrent() { return (int) mCurrent; }
    public int getBase() { return (int) mBase; }
    public int getModified() { return getTotal() - getBase(); }
    public int getTotal() {  return (int) getAndOrCalculateTotal(); }
    public void setBase(float value) { mBase = value; mDirty = true; }


    public void setCurrent(float value) {
        mCurrentIsInUse = true;
        float clampedValue = Math.max(0, Math.min(value, getTotal())); // Clamp between 0 and total
        mCurrent = clampedValue;
        mDirty = true;
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


    /**
     * Computes the effective stat value.
     * First, apply all additive modifiers, then multiply by (1 + sum(percentageModifiers)).
     */
    private float getAndOrCalculateTotal() {
        if (!mDirty) { return mTotal;  }

        float modifiedValue = mBase;

        for (Map.Entry<String, Float> entry : mAdditives.entrySet()) {
            String source = entry.getKey();
            Float addition = entry.getValue();
            modifiedValue += addition;
        }

        float totalPercentage = 0.0f;
        for (Map.Entry<String, Float> entry : mMultiplicatives.entrySet()) {
            String source = entry.getKey();
            Float percent = entry.getValue();
            totalPercentage += percent;
        }

        float result = modifiedValue * (1.0f + totalPercentage);

        mTotal = result;
        mDirty = false;
        if (mCurrent == mTotal) { mCurrent = mTotal; }
        mHashCode = (int) (mMultiplicatives.hashCode() + mAdditives.hashCode() + mBase + mCurrent + mTotal);

        return mTotal;
    }


    private void handleDirtiness() {
//        if (!mDirty) { return; }
//
//        float base = getFloat(BASE_KEY);
//
//        // Calculate Additive and Multiplicative Modifiers
//        float additiveSum = calculateAdditiveModifiers();
//        float multiplicativeFactor = calculateMultiplicativeModifiers();
//
//        // Apply Additive First
//        float baseWithAdditive = base + additiveSum;
//
//        // Apply Multiplicative
//        float total = baseWithAdditive * multiplicativeFactor;
//
//        // Store the proper values
//        put(BASE_KEY, base);
//        put(MODIFIED_KEY, total - base); // ✅ Now correctly represents buffs/debuffs
//        put(TOTAL_KEY, total);
//
//        if (!mCurrentIsInUse) {
//            put(CURRENT_KEY, total);
//        }
//
//        mDirty = false;
    }

//    private void handleDirtiness() {
//        if (!mDirty) { return; }
//
//        float base = getFloat(BASE_KEY);
//
//        // Calculate Additive and Multiplicative Modifiers
//        float additiveSum = calculateAdditiveModifiers();
//        float multiplicativeFactor = calculateMultiplicativeModifiers();
//
//        // Apply Additive First
//        float baseWithAdditive = base + additiveSum;
//
//        // Apply Multiplicative
//        float total = baseWithAdditive * multiplicativeFactor;
//
//        // Store the proper values
//        put(BASE_KEY, base);
//        put(MODIFIED_KEY, total - base); // ✅ Now correctly represents buffs/debuffs
//        put(TOTAL_KEY, total);
//
//        if (!mCurrentIsInUse) {
//            put(CURRENT_KEY, total);
//        }
//
//        mDirty = false;
//    }


    public int hashCode() { return mHashCode; }
}
