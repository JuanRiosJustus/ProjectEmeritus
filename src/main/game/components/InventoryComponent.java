package main.game.components;

import main.game.entity.Entity;
import main.game.stats.StatNode;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.*;

public class InventoryComponent extends Component {
    private final Map<String, Entity> mEquipped = new HashMap<>();

    private final static ELogger logger = ELoggerFactory.getInstance().getELogger(InventoryComponent.class);

    public void addAll(InventoryComponent inventoryComponent) {
//        items.putAll(inventory.items);
//        for (Map.Entry<String, Entity> entry : inventory.mCollection.entrySet()) {
////            add(entry.getValue());
//        }
    }

    public void equip(Entity item) {
        StatisticsComponent ownerStats = mOwner.get(StatisticsComponent.class);
        IdentityComponent identityComponent = item.get(IdentityComponent.class);
        StatisticsComponent itemStats = item.get(StatisticsComponent.class);

        for (String key : itemStats.getKeySet()) {
            ownerStats.modify(key, identityComponent.getUuid(), StatNode.ADDITIVE, itemStats.getTotal(key));
        }
    }

    public Set<String> itemNames() { return new HashSet<>(); }

    public static void equip(Entity unit, String name) {

//        Inventory inventory = unit.get(Inventory.class);
//
//        if (!inventory.mCollection.containsKey(name)) { return; }
//        if (inventory.mEquipped.containsKey(name)) { return; }
//
//        Entity item = inventory.mCollection.get(name);
//
//        Statistics itemStats = item.get(Statistics.class);
//        Statistics ownerStats = unit.get(Statistics.class);
//
//        if (itemStats == null || ownerStats == null) { DebuggingSystem.log("Unable to equip"); return; }
//
//        inventory.mEquipped.put(name, item);
//
//        for (String statName : itemStats.getStatNodeKeys()) {
////            StatNode ownerStat = ownerStats.getStatsNode(statName);
//            int value = itemStats.getStatTotal(statName);
////            ownerStats.
////            ownerStats.to
//
//
//
//
////            StatNode ownerStat = ownerStats.getStatsNode(statName);
////            StatNode itemStat = itemStats.getStatsNode(statName);
////            String increaseType = (itemStat.getTotal() < 1 ? Constants.PERCENT : Constants.FLAT);
////            DebuggingSystem.log("Before: " + ownerStat.getTotal());
////            ownerStat.add(item, increaseType, itemStat.getTotal());
////            DebuggingSystem.log(itemStat.getTotal() + " was added to " + statName);
////            DebuggingSystem.log("After: " + ownerStat.getTotal());
////            ownerStat.getTotal();
//        }
    }
}
