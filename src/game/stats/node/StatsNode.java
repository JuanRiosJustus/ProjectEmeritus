package game.stats.node;

import constants.Constants;
import logging.ELogger;
import logging.ELoggerFactory;

import java.util.*;

public class StatsNode {

    private final String name;
    private int base;
    private int modified;
    private boolean dirty;
    private final Set<StatsNodeModification> flatModifiers = new HashSet<>();
    private final Set<StatsNodeModification> percentageModifiers = new HashSet<>();
    private final Map<Object, StatsNodeModification> modifierMap = new HashMap<>();

    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(StatsNode.class);

    public StatsNode(String nodeName, int baseValue) {
        name = nodeName;
        setBase(baseValue);
    }

    private void setBase(int baseValue) {
        base = baseValue;
        modified = getModifiedValue();
    }

    public void add(Object source, String flatOrPercent, float value) {
        StatsNodeModification modifier = new StatsNodeModification(source, flatOrPercent, value);
        modifierMap.put(source, modifier);
        switch (flatOrPercent) {
            case Constants.FLAT -> flatModifiers.add(modifier);
            case Constants.PERCENT -> percentageModifiers.add(modifier);
            default -> logger.info("Could not add new modifier [" + flatOrPercent + "] with value: " + value);
        }
        dirty = true;
    }

    public void remove(Object source) {
        StatsNodeModification modifier = modifierMap.get(source);
        flatModifiers.remove(modifier);
        percentageModifiers.remove(modifier);
        modifierMap.remove(modifier);
        dirty = true;
    }

    public void clear() {
        flatModifiers.clear();
        percentageModifiers.clear();
        dirty = true;
    }

    public int getTotal() {
        if (dirty) {
            modified = Math.max(getModifiedValue(), 1);
            dirty = false;
        }
        return modified;
    }

    public int getBase() { return (int) base; }
    public int getMods() { return getTotal() - (int) base; }
    public String getName() { return name; }
    public boolean isDirty() { return dirty; }

    private int getModifiedValue() {
        float preTotal = base;

        // calculate the flat values first
        float flatModifiersSum = 0;
        for (StatsNodeModification modifier : flatModifiers) {
            flatModifiersSum += modifier.value;
        }

        // get percentage values
        float totalPercentageValueSum = 0;
        for (StatsNodeModification modifier : percentageModifiers) {
            totalPercentageValueSum += modifier.value;
        }

        // calculate total after adding flat and base
        float flatModifiersAndBaseTotal = flatModifiersSum + preTotal;
        float postTotal = flatModifiersAndBaseTotal + (flatModifiersAndBaseTotal * totalPercentageValueSum);

        return (int) postTotal;
    }

    @Override
    public String toString() {
        return "StatsNode{" +
                "base=" + base +
                ", dirty=" + dirty +
                ", modified=" + modified +
                ", flatModifiers=" + flatModifiers +
                ", percentageModifiers=" + percentageModifiers +
                ", modifierMap=" + modifierMap +
                '}';
    }


    
    public Map<Object, StatsNodeModification> getModifications() { return modifierMap; }
}
