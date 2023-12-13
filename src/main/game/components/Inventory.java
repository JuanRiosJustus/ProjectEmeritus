package main.game.components;

import main.constants.Constants;
import main.game.entity.Entity;
import main.game.stats.StatNode;
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
//            add(entry.getValue());
        }
    }

    public void add(Entity item) {
        Summary summary = item.get(Summary.class);
        items.put(item.toString(), item);
//        items.put()
        // items.put(item.get(Statistics.class).getName(), item);
        // Testing this, seems to work
//        if (owner == null) { return; }
        // equip(owner, item.get(Statistics.class).getName());
    }
    public boolean hasItems() { return !items.isEmpty(); }

    public Set<String> itemNames() { return items.keySet(); }

    public static void equip(Entity unit, String name) {

        Inventory inventory = unit.get(Inventory.class);

        if (!inventory.items.containsKey(name)) { return; }
        if (inventory.equipped.containsKey(name)) { return; }

        Entity item = inventory.items.get(name);

        Summary itemStats = item.get(Summary.class);
        Summary ownerStats = unit.get(Summary.class);

        if (itemStats == null || ownerStats == null) { DebuggingSystem.log("Unable to equip"); return; }

        inventory.equipped.put(name, item);

        for (String statName : itemStats.getStatNodeKeys()) {
//            StatNode ownerStat = ownerStats.getStatsNode(statName);
            int value = itemStats.getStatTotal(statName);
//            ownerStats.
//            ownerStats.to




//            StatNode ownerStat = ownerStats.getStatsNode(statName);
//            StatNode itemStat = itemStats.getStatsNode(statName);
//            String increaseType = (itemStat.getTotal() < 1 ? Constants.PERCENT : Constants.FLAT);
//            DebuggingSystem.log("Before: " + ownerStat.getTotal());
//            ownerStat.add(item, increaseType, itemStat.getTotal());
//            DebuggingSystem.log(itemStat.getTotal() + " was added to " + statName);
//            DebuggingSystem.log("After: " + ownerStat.getTotal());
//            ownerStat.getTotal();
        }
    }
}
