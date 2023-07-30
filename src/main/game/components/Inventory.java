package main.game.components;

import main.constants.Constants;
import main.game.entity.Entity;
import main.game.stats.node.StatsNode;
import main.game.systems.DebuggingSystem;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.*;

public class Inventory extends Component {
    private final Map<String, Entity> equipped = new HashMap<>();
    private final Map<String, Entity> items = new HashMap<>();

    private final static ELogger logger = ELoggerFactory.getInstance().getELogger(Inventory.class);

    public void addAll(Inventory inventory) {
//        items.putAll(inventory.items);
        for (Map.Entry<String, Entity> entry : inventory.items.entrySet()) {
            add(entry.getValue());
        }
    }

    public void add(Entity item) {
        // items.put(item.get(Statistics.class).getName(), item);
        // Testing this, seems to work
        if (owner == null) { return; }
        // equip(owner, item.get(Statistics.class).getName());
    }
    public boolean hasItems() { return items.size() > 0; }

    public Set<String> itemNames() { return items.keySet(); }

    public static void equip(Entity unit, String name) {

        Inventory unitInventory = unit.get(Inventory.class);

        if (!unitInventory.items.containsKey(name)) { return; }
        if (unitInventory.equipped.containsKey(name)) { return; }

        Entity item = unitInventory.items.get(name);

        Statistics itemStats = item.get(Statistics.class);
        Statistics ownerStats = unit.get(Statistics.class);

        if (itemStats == null || ownerStats == null) { DebuggingSystem.log("Unable to equip"); return; }

        unitInventory.equipped.put(name, item);

        for (String statName : itemStats.getKeySet()) {
            StatsNode ownerStat = ownerStats.getStatsNode(statName);
            StatsNode itemStat = itemStats.getStatsNode(statName);
            String increaseType = (itemStat.getTotal() < 1 ? Constants.PERCENT : Constants.FLAT);
            DebuggingSystem.log("Before: " + ownerStat.getTotal());
            ownerStat.add(item, increaseType, itemStat.getTotal());
            DebuggingSystem.log(itemStat.getTotal() + " was added to " + statName);
            DebuggingSystem.log("After: " + ownerStat.getTotal());
            ownerStat.getTotal();
        }
    }
}
