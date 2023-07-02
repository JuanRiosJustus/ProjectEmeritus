package game.components;

import constants.Constants;
import game.components.statistics.Summary;
import game.entity.Entity;
import game.stats.node.ScalarNode;
import game.systems.DebuggingSystem;
import logging.Logger;
import logging.LoggerFactory;

import java.util.*;

public class Inventory extends Component {
    private final Map<String, Entity> equipped = new HashMap<>();
    private final Map<String, Entity> items = new HashMap<>();

    private final static Logger logger = LoggerFactory.instance().logger(Inventory.class);

    public void addAll(Inventory inventory) {
//        items.putAll(inventory.items);
        for (Map.Entry<String, Entity> entry : inventory.items.entrySet()) {
            add(entry.getValue());
        }
    }

    public void add(Entity item) {
        items.put(item.get(Summary.class).getName(), item);
        // Testing this, seems to work
        if (owner == null) { return; }
        equip(owner, item.get(Summary.class).getName());
    }
    public boolean hasItems() { return items.size() > 0; }

    public Set<String> itemNames() { return items.keySet(); }

    public static void equip(Entity unit, String name) {

        Inventory unitInventory = unit.get(Inventory.class);

        if (!unitInventory.items.containsKey(name)) { return; }
        if (unitInventory.equipped.containsKey(name)) { return; }

        Entity item = unitInventory.items.get(name);

        Summary itemStats = item.get(Summary.class);
        Summary ownerStats = unit.get(Summary.class);

        if (itemStats == null || ownerStats == null) { DebuggingSystem.log("Unable to equip"); return; }

        unitInventory.equipped.put(name, item);

        logger.banner("Equipping item");

        for (String statName : itemStats.getKeySet()) {
            ScalarNode ownerStat = ownerStats.getScalarNode(statName);
            ScalarNode itemStat = itemStats.getScalarNode(statName);
            String increaseType = (itemStat.getTotal() < 1 ? Constants.PERCENT : Constants.FLAT);
            DebuggingSystem.log("Before: " + ownerStat.getTotal());
            ownerStat.add(item, increaseType, itemStat.getTotal());
            DebuggingSystem.log(itemStat.getTotal() + " was added to " + statName);
            DebuggingSystem.log("After: " + ownerStat.getTotal());
            ownerStat.getTotal();
        }
    }
}
