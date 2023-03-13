package game.stats.node;

import constants.Constants;
import logging.Logger;
import logging.LoggerFactory;

import java.util.*;

public class ScalarNode extends StatsNode {

    private final String name;
    private int base;
    private int modified;
    private boolean dirty;
    private final Set<ScalarNodeModification> flatModifiers = new HashSet<>();
    private final Set<ScalarNodeModification> percentageModifiers = new HashSet<>();
    private final Map<Object, ScalarNodeModification> modifierMap = new HashMap<>();

    private static final Logger logger = LoggerFactory.instance().logger(ScalarNode.class);

    public ScalarNode(String nodeName, int baseValue) {
        name = nodeName;
        setBase(baseValue);
    }

    private void setBase(int baseValue) {
        base = baseValue;
        modified = getModifiedValue();
    }

    public void add(Object source, String flatOrPercent, float value) {
        ScalarNodeModification modifier = new ScalarNodeModification(source, flatOrPercent, value);
        modifierMap.put(source, modifier);
        switch (flatOrPercent) {
            case Constants.FLAT -> flatModifiers.add(modifier);
            case Constants.PERCENT -> percentageModifiers.add(modifier);
            default -> logger.log("Could not add new modifier [" + flatOrPercent + "] with value: " + value);
        }
        dirty = true;
    }

    public void remove(Object source) {
        ScalarNodeModification modifier = modifierMap.get(source);
        flatModifiers.remove(modifier);
        percentageModifiers.remove(modifier);
        modifierMap.remove(modifier);
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
    public int getMods() { return modified - (int) base; }

    private int getModifiedValue() {
        float preTotal = base;

        // calculate the flat values first
        float flatModifiersSum = 0;
        for (ScalarNodeModification modifier : flatModifiers) {
            flatModifiersSum += modifier.value;
        }

        // get percentage values
        float totalPercentageValueSum = 0;
        for (ScalarNodeModification modifier : percentageModifiers) {
            totalPercentageValueSum += modifier.value;
        }

        // calculate total after adding flat and base
        float flatModifiersAndBaseTotal = flatModifiersSum + preTotal;
        float postTotal = flatModifiersAndBaseTotal + (flatModifiersAndBaseTotal * totalPercentageValueSum);

        return (int) postTotal;
    }

    @Override
    public String toString() {
        return "ScalarNode{" +
                "base=" + base +
                ", dirty=" + dirty +
                ", modified=" + modified +
                ", flatModifiers=" + flatModifiers +
                ", percentageModifiers=" + percentageModifiers +
                ", modifierMap=" + modifierMap +
                '}';
    }
}
