package game.stats.node;

import constants.Constants;
import logging.Logger;
import logging.LoggerFactory;

import java.util.*;

public class ScalarNode extends StatsNode {
    private int base;
    private boolean dirty;
    private int modified;
    private final Set<ScalarNodeModifier> flatModifiers = new HashSet<>();
    private final Set<ScalarNodeModifier> percentageModifiers = new HashSet<>();
    private final Map<Object, ScalarNodeModifier> modifierMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.instance().logger(ScalarNode.class);


    public ScalarNode(String name, int baseValue) {
        super(name);
        setBase(baseValue);
    }

    private void setBase(int baseValue) {
        base = baseValue;
        modified = getModifiedValue();
    }

    public void add(Object source, String type, float value) {
        ScalarNodeModifier modifier = new ScalarNodeModifier(source, type, value);
        modifierMap.put(source, modifier);
        switch (type) {
            case Constants.FLAT -> flatModifiers.add(modifier);
            case Constants.PERCENT -> percentageModifiers.add(modifier);
            default -> logger.log("Could not add new modifier [" + type + "] with value: " + value);
        }
        dirty = true;
    }

    public void remove(Object source) {
        ScalarNodeModifier modifier = modifierMap.get(source);
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
        for (ScalarNodeModifier modifier : flatModifiers) {
            flatModifiersSum += modifier.value;
        }

        // get percentage values
        float totalPercentageValueSum = 0;
        for (ScalarNodeModifier modifier : percentageModifiers) {
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
