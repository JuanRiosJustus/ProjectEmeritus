package main.game.stats;

import java.util.*;

public class StatNode  {
    public static class Modification {

        public final float mValue;
        public final Object mSource;
        public final String mType;

        public Modification(Object source, String type, float value) {
            mValue = value;
            mSource = source;
            mType = type;
        }
    }
    
    protected final String mName;
    protected float mBase;
    protected float mModified;
    protected float mTotal;
    protected boolean mDirty;
    public static final String ADDITIVE = "additive";
    public static final String MULTIPLICATIVE = "multiplicative";
    public static final String EXPONENTIAL = "exponential";
    protected final Map<String, Set<Modification>> mModificationMap = new HashMap<>();
    protected final Map<Object, Set<Modification>> mSourceMap = new HashMap<>();

    public StatNode(String key, float value) {
        mName = key;
        mBase = (int) value;
        mModified = calculateTotalValue() - mBase;
        mTotal = mBase + mModified;
    }
    public StatNode(String key, int value) {
        mName = key;
        mBase = value;
        mModified = calculateTotalValue() - mBase;
        mTotal = mBase + mModified;
    }

    public void modify(Object source, String type, float value) {

        mModificationMap.put(type, mModificationMap.getOrDefault(type, new HashSet<>()));
        mSourceMap.put(source, mSourceMap.getOrDefault(source, new HashSet<>()));

        Modification newModification = new Modification(source, type, value);

        mSourceMap.get(source).add(newModification);
        mModificationMap.get(type).add(newModification);

        mDirty = true;
    }

    public void remove(Object source) {
        Set<Modification> modificationsFromSource = mSourceMap.get(source);

        for (Map.Entry<String, Set<Modification>> modificationType : mModificationMap.entrySet()) {
            for (Modification modificationFromSource : modificationsFromSource) {
                modificationType.getValue().remove(modificationFromSource);
            }
        }

        mSourceMap.remove(source);
        mDirty = true;
    }

    public void clear() {
        for (Map.Entry<String, Set<Modification>> modificationType : mModificationMap.entrySet()) {
            modificationType.getValue().clear();
        }
        mSourceMap.clear();

        mDirty = true;
    }

    public int getTotal() {
        if (mDirty) {
            mModified = calculateTotalValue() - mBase;
            mDirty = false;
        }
        mTotal = Math.max(mBase + mModified, 0);
        return (int) mTotal;
    }

    public int getCurrent() { return getTotal(); }
    public int getBase() { return (int) mBase; }
    public int getModified() { return (int) mModified; }
    public void setBase(int base) {
        mBase = base;
        mModificationMap.clear();
        mSourceMap.clear();
    }
    public void setModified(int modified) {
        mModified = modified;
        mModificationMap.clear();
        mSourceMap.clear();
    }

    public String getName() { return mName; }

    private int calculateTotalValue() {
        float total = mBase;

        // calculate the flat values first
        float flatSum = 0;
        for (Modification modifier : mModificationMap.getOrDefault(ADDITIVE, new HashSet<>())) {
            flatSum += modifier.mValue;
        }

        // get pre total percentage values
        float preTotalPercentSum = 0;
        for (Modification modifier : mModificationMap.getOrDefault(MULTIPLICATIVE, new HashSet<>())) {
            preTotalPercentSum += modifier.mValue;
        }

        // calculate total after adding flat and base
        float flatModifiersAndBaseTotal = flatSum + total;
        float postTotal = flatModifiersAndBaseTotal + (flatModifiersAndBaseTotal * preTotalPercentSum);

        // get post total percentage values
        float postTotalPercentSum = 0;
        for (Modification modifier : mModificationMap.getOrDefault(EXPONENTIAL, new HashSet<>())) {
            postTotalPercentSum += postTotal * modifier.mValue;
        }
        postTotal += postTotalPercentSum;

        return (int) postTotal;
    }

//    private int calculateTotalValue() {
//        float preTotal = mBase;
//
//        // calculate the flat values first
//        float flatSum = 0;
//        for (Modification modifier : mModificationMap.getOrDefault(ADDITIVE, new HashSet<>())) {
//            flatSum += modifier.mValue;
//        }
//
//        // get pre total percentage values
//        float preTotalPercentSum = 0;
//        for (Modification modifier : mModificationMap.getOrDefault(MULTIPLICATIVE, new HashSet<>())) {
//            preTotalPercentSum += modifier.mValue;
//        }
//
//        // calculate total after adding flat and base
//        float flatModifiersAndBaseTotal = flatSum + preTotal;
//        float postTotal = flatModifiersAndBaseTotal + (flatModifiersAndBaseTotal * preTotalPercentSum);
//
//        // get post total percentage values
//        float postTotalPercentSum = 0;
//        for (Modification modifier : mModificationMap.getOrDefault(EXPONENTIAL, new HashSet<>())) {
//            postTotalPercentSum += postTotal * modifier.mValue;
//        }
//        postTotal += postTotalPercentSum;
//
//        return (int) postTotal;
//    }

    public int hashState() {
        int total = 0;
        for (Map.Entry<String, Set<Modification>> modificationType : mModificationMap.entrySet()) {
            total += modificationType.getValue().size();
        }

        return (int) (mName.length() + mBase + mTotal + getCurrent() + total);
    }

    public Map<String, Float> getSummary() {
//        Map<String, Float> summary = new HashMap<>();
//
//        int count = 0;
//        for (Map.Entry<Object, Set<Modification>> modifications : mSourceMap.entrySet()) {
//            for (Modification modification : modifications.getValue()) {
//                summary.put(modifications.getKey().toString() + "_" + count++, modification.mValue);
//            }
//        }
//        return summary;

        Map<String, Float> summary = new HashMap<>();
//        List<Map.Entry<String, Float>> summary = new ArrayList<>();

        int count = 0;
        for (Map.Entry<Object, Set<Modification>> modifications : mSourceMap.entrySet()) {
            for (Modification modification : modifications.getValue()) {
                summary.put(modifications.getKey().toString() + "_" + count++, modification.mValue);
            }
        }
        return summary;
    }
}
