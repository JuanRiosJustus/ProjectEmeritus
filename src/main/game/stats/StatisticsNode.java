package main.game.stats;

import main.game.components.tile.Tile;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class StatisticsNode {
    protected final String mName;
    protected int mBase;
    protected int mTotal;
    protected boolean mDirty;
    protected final Set<ResourceNodeModification> mFlatMods = new HashSet<>();
    protected final Set<ResourceNodeModification> mPreTotalPercentMods = new HashSet<>();
    protected final Set<ResourceNodeModification> mPostTotalPercentMods = new HashSet<>();
    protected final Map<Object, ResourceNodeModification> mModMap = new HashMap<>();
    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(StatisticsNode.class);

    public static final String FLAT_MODIFICATION = "flat";
    public static final String PRE_TOTAL_PERCENT_MODIFICATION = "prePercent";
    public static final String POST_TOTAL_PERCENT_MODIFICATION = "postPercent";

    public StatisticsNode(String key, int value) {
        mName = key;
        mBase = value;
        mTotal = calculateTotalValue();
    }

    public void add(Object source, String flatOrPercent, float value) {
        ResourceNodeModification rnm = new ResourceNodeModification(source, value);
        mModMap.put(source, rnm);
        switch (flatOrPercent) {
            case FLAT_MODIFICATION -> mFlatMods.add(rnm);
            case PRE_TOTAL_PERCENT_MODIFICATION -> mPreTotalPercentMods.add(rnm);
            case POST_TOTAL_PERCENT_MODIFICATION -> mPostTotalPercentMods.add(rnm);
            default -> logger.info("Could not add new modifier [" + flatOrPercent + "] with value: " + value);
        }
        mDirty = true;
    }

    public void remove(Object source) {
        ResourceNodeModification modifier = mModMap.get(source);
        mFlatMods.remove(modifier);
        mPreTotalPercentMods.remove(modifier);
        mPostTotalPercentMods.remove(modifier);
        mModMap.remove(modifier);
        mDirty = true;
    }

    public void clear() {
        mFlatMods.clear();
        mPreTotalPercentMods.clear();
        mPostTotalPercentMods.clear();
        mModMap.clear();
        mDirty = true;
    }

    public int getTotal() {
        if (mDirty) {
            mTotal = Math.max(calculateTotalValue(), 0);
            mDirty = false;
        }
        return mTotal;
    }

    public int getBase() { return mBase; }
    public int getModified() { return getTotal() - getBase(); }
    public String getName() { return mName; }
    public void setBase(int baseValue) {
        mBase = baseValue;
        mDirty = true;
    }

    private int calculateTotalValue() {
        float preTotal = mBase;

        // calculate the flat values first
        float flatSum = 0;
        for (ResourceNodeModification modifier : mFlatMods) {
            flatSum += modifier.getValue();
        }

        // get pre total percentage values
        float preTotalPercentSum = 0;
        for (ResourceNodeModification modifier : mPreTotalPercentMods) {
            preTotalPercentSum += modifier.getValue();
        }

        // calculate total after adding flat and base
        float flatModifiersAndBaseTotal = flatSum + preTotal;
        float postTotal = flatModifiersAndBaseTotal + (flatModifiersAndBaseTotal * preTotalPercentSum);

        // get post total percentage values
        float postTotalPercentSum = 0;
        for (ResourceNodeModification modifier : mPostTotalPercentMods) {
            postTotalPercentSum += postTotal * modifier.getValue();
        }
        postTotal += postTotalPercentSum;

        return (int) postTotal;
    }
    
    public Map<Object, ResourceNodeModification> getModifications() { return mModMap; }
    public int hashState() {
        return Objects.hash(mName, mBase, mTotal, mDirty, mFlatMods, mPreTotalPercentMods, mPostTotalPercentMods, mModMap);
    }

    public Map<String, Float> getSummary() {
        Map<String, Float> summary = new HashMap<>();
        for (Map.Entry<Object, ResourceNodeModification> entry : mModMap.entrySet()) {
            summary.put(entry.getKey().toString(), entry.getValue().getValue());
        }
        return summary;
    }
}
