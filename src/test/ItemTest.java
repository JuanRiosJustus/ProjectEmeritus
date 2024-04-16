package test;

import main.game.components.Statistics;
import main.game.entity.Entity;
import main.game.foundation.LootTable;
import main.game.stores.factories.ItemFactory;
import main.game.stores.pools.ItemPool;
import main.game.stores.pools.unit.UnitPool;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ItemTest {
    @Test
    public void testItemStatsAreLinked() {
        String uuid = UnitPool.getInstance().create(
                "",
                "Sid",
                "some-test-uuid-uuid",
                "Mage",
                5,
                0,
                false);
        Entity entity = UnitPool.getInstance().get(uuid);
        Statistics statistics = entity.get(Statistics.class);

        Entity item = ItemPool.getInstance().create(5);
    }
}
