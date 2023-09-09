package main.game.stats.node;

import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.*;

public class StatsNode {

    protected final String name;
    protected int base;
    protected int total;
    protected boolean dirty;
    protected final Set<StatsNodeModification> flat = new HashSet<>();
    protected final Set<StatsNodeModification> percent = new HashSet<>();
    protected final Map<Object, StatsNodeModification> modifierMap = new HashMap<>();

    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(StatsNode.class);

    public static final String HEALTH = "Health";
    public static final String ENERGY = "Energy";
    public static final String LEVEL = "Level";
    public static final String EXPERIENCE = "Experience";
    public static final String STRENGTH = "Strength";
    public static final String INTELLIGENCE = "Intelligence";
    public static final String DEXTERITY = "Dexterity";
    public static final String WISDOM = "Wisdom";
    public static final String CONSTITUTION = "Constitution";
    public static final String CHARISMA = "Charisma";
    public static final String LUCK = "Luck";
    public static final String RESISTANCE = "Resistance";


    public StatsNode(String key, int value) {
        name = key;
        base = value;
        total = calculateTotalValue();
    }

    public void add(Object source, String flatOrPercent, float value) {
        flatOrPercent = flatOrPercent.toLowerCase();
        StatsNodeModification modifier = new StatsNodeModification(source, flatOrPercent, value);
        modifierMap.put(source, modifier);
        switch (flatOrPercent) {
            case "flat" -> flat.add(modifier);
            case "percent" -> percent.add(modifier);
            default -> logger.info("Could not add new modifier [" + flatOrPercent + "] with value: " + value);
        }
        dirty = true;
    }

    public void remove(Object source) {
        StatsNodeModification modifier = modifierMap.get(source);
        flat.remove(modifier);
        percent.remove(modifier);
        modifierMap.remove(modifier);
        dirty = true;
    }

    public void clear() {
        flat.clear();
        percent.clear();
        modifierMap.clear();
        dirty = true;
    }

    public int getTotal() {
        if (dirty) {
            total = Math.max(calculateTotalValue(), 0);
            dirty = false;
        }
        return total;
    }

    public int getBase() { return base; }
    public int getModified() { return getTotal() - base; }
    public String getName() { return name; }
    public boolean isDirty() { return dirty; }
    public void setBase(int baseValue) {
        base = baseValue;
        dirty = true;
    }

    private int calculateTotalValue() {
        float preTotal = base;

        // calculate the flat values first
        float flatModifiersSum = 0;
        for (StatsNodeModification modifier : flat) {
            flatModifiersSum += modifier.value;
        }

        // get percentage values
        float totalPercentageValueSum = 0;
        for (StatsNodeModification modifier : percent) {
            totalPercentageValueSum += modifier.value;
        }

        // calculate total after adding flat and base
        float flatModifiersAndBaseTotal = flatModifiersSum + preTotal;
        float postTotal = flatModifiersAndBaseTotal + (flatModifiersAndBaseTotal * totalPercentageValueSum);

        return (int) postTotal;
    }
    
    public Map<Object, StatsNodeModification> getModifications() { return modifierMap; }
}
