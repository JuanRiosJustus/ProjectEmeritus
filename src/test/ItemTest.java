package test;

import main.game.components.StatisticsComponent;
import main.game.entity.Entity;
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
                5,
                0,
                false);
        Entity entity = UnitPool.getInstance().get(uuid);
        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);

        Entity item = ItemPool.getInstance().create(5);
    }
}
