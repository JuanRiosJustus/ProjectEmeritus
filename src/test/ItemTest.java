package test;

import main.game.components.statistics.StatisticsComponent;
import main.game.entity.Entity;
import main.game.stores.pools.ItemPool;
import main.game.stores.pools.UnitDatabase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ItemTest {
    @Test
    public void testItemStatsAreLinked() {
        String uuid = UnitDatabase.getInstance().create(
                "",
                "Sid",
                "some-test-uuid-uuid",
                false);
        Entity entity = UnitDatabase.getInstance().get(uuid);
        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);

        Entity item = ItemPool.getInstance().create(5);
    }
}
