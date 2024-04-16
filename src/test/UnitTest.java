package test;

import main.game.components.Statistics;
import main.game.entity.Entity;
import main.game.stores.pools.unit.UnitPool;
import org.junit.Test;

public class UnitTest {

    @Test
    public void testUnitsHaveExpectedStats() {
        String uuid = UnitPool.getInstance().create(
                "Example Unit",
                "Sid",
                "some-test-uuid-uuid",
                "Mage",
                5,
                0,
                false);
        Entity entity = UnitPool.getInstance().get(uuid);
        Statistics statistics = entity.get(Statistics.class);

        System.out.println(System.getProperty("java.version") + "?");
        UnitPool.getInstance().save(entity);
    }
}