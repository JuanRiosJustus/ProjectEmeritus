package main.game.stats;

import java.util.*;

public class StatNode {
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
    protected int mBase;
    protected int mTotal;
    protected boolean mDirty;
    public static final String ADDITIVE = "additive";
    public static final String MULTIPLICATIVE = "multiplicative";
    public static final String EXPONENTIAL = "exponential";
    protected final Map<String, Set<Modification>> mModificationMap = new HashMap<>();
    protected final Map<Object, Set<Modification>> mSourceMap = new HashMap<>();
    private final Map<String, Map<Object, List<Object[]>>> mModificationMapV2 = new HashMap<>();
    private final Map<Object, List<Object[]>> mSourceMapV2 = new HashMap<>();
    public StatNode(String key, int value) {
        mName = key;
        mBase = value;
        mModificationMapV2.put(ADDITIVE, new HashMap<>());
        mModificationMapV2.put(MULTIPLICATIVE, new HashMap<>());
        mModificationMapV2.put(EXPONENTIAL, new HashMap<>());
        mTotal = calculateTotalValueV2();
    }

    public void modifyV2(Object source, String type, float value) {
        // Ensure there are maps for additive, multiplicative, and exponential
        // First level represents modification-type, second represents source, third the modification
        Map<Object, List<Object[]>> typesToModifications = mModificationMapV2.get(type);

        Object[] newModification = new Object[]{ source, type, value };

        List<Object[]> modificationsList1 = typesToModifications.getOrDefault(source, new ArrayList<>());
        modificationsList1.add(newModification);
        typesToModifications.put(source, modificationsList1);

        List<Object[]> modificationsList2 = mSourceMapV2.getOrDefault(source, new ArrayList<>());
        modificationsList2.add(newModification);
        mSourceMapV2.put(source, modificationsList2);

        mDirty = true;
    }

    public void modify(Object source, String type, float value) {

        mModificationMap.put(type, mModificationMap.getOrDefault(type, new HashSet<>()));
        mSourceMap.put(source, mSourceMap.getOrDefault(source, new HashSet<>()));

        Modification newModification = new Modification(source, type, value);

        mSourceMap.get(source).add(newModification);
        mModificationMap.get(type).add(newModification);

        modifyV2(source, type, value);

        mDirty = true;
    }

    public void removeV2(Object source) {
        for (Map.Entry<String, Map<Object, List<Object[]>>> entry : mModificationMapV2.entrySet()) {
            entry.getValue().remove(source);
        }
        mSourceMapV2.remove(source);
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
            mTotal = Math.max(calculateTotalValueV2(), 0);
            mDirty = false;
        }
        return mTotal;
    }

    public int getCurrent() { return getTotal(); }
    public int getBase() { return mBase; }
    public int getModified() { return getTotal() - getBase(); }
    public String getName() { return mName; }


    private int calculateTotalValueV2() {
        float preTotal = mBase;

        float additive = 0;
        for (Map.Entry<Object, List<Object[]>> entry : mModificationMapV2.get(ADDITIVE).entrySet()) {
            for (Object[] modification : entry.getValue()) {
                additive += (Float) modification[modification.length - 1];
            }
        }

        float multiplicative = 0;
        for (Map.Entry<Object, List<Object[]>> entry : mModificationMapV2.get(MULTIPLICATIVE).entrySet()) {
            for (Object[] modification : entry.getValue()) {
                multiplicative += (Float) modification[modification.length - 1];
            }
        }

        // calculate total after adding flat and base
        float flatModifiersAndBaseTotal = additive + preTotal;
        float postTotal = flatModifiersAndBaseTotal + (flatModifiersAndBaseTotal * multiplicative);

        float exponential = 0;
        for (Map.Entry<Object, List<Object[]>> entry : mModificationMapV2.get(EXPONENTIAL).entrySet()) {
            for (Object[] modification : entry.getValue()) {
                exponential += postTotal *  (Float) modification[modification.length - 1];
            }
        }

        postTotal += exponential;

        return (int) postTotal;
    }

    public int hashState() {
        int total = 0;
        for (Map.Entry<String, Set<Modification>> modificationType : mModificationMap.entrySet()) {
            total += modificationType.getValue().size();
        }

        return mName.length() + mBase + mTotal + getCurrent() + total;
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
