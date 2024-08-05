package test;

import main.constants.Constants;
import main.game.components.Statistics;
import main.game.entity.Entity;
import main.game.stores.pools.unit.UnitPool;
import main.utils.StringFormatter;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StatisticsTest {

    @Test
    public void statsAreCorrectlyDefaulting() {
//        // Create a unit.
//        String uuid = UnitPool.getInstance().create(
//                "Crystal Dragon",
//                "Sid",
//                "some-test-uuid-uuid",
//                "Mage",
//                5,
//                0,
//                false);
//
//        Entity entity = UnitPool.getInstance().get(uuid);
//        Statistics stats = entity.get(Statistics.class);
//
//        assertTrue(stats.getStatTotal(Statistics.HEALTH) > 0);
//        assertTrue(stats.getStatTotal(Statistics.MANA) > 0);
//        assertEquals(5, stats.getStatTotal(Statistics.LEVEL));
//        assertEquals(0, stats.getStatCurrent(Statistics.LEVEL));
//
//        assertTrue(stats.getStatTotal(Statistics.PHYSICAL_ATTACK) > 0);
//        assertTrue(stats.getStatTotal(Statistics.PHYSICAL_DEFENSE) > 0);
//        assertTrue(stats.getStatTotal(Statistics.MAGICAL_ATTACK) > 0);
//        assertTrue(stats.getStatTotal(Statistics.MAGICAL_DEFENSE) > 0);
//
//        assertTrue(stats.getStatTotal(Statistics.SPEED) > 0);
//        assertTrue(stats.getStatTotal(Statistics.MOVE) > 0);
//        assertTrue(stats.getStatTotal(Statistics.CLIMB) > 0);
//
//
//        for (int i = 1; i < 201; i++) {
//            String additionalXP = (Statistics.getExperienceNeeded(i) - Statistics.getExperienceNeeded(i - 1)) + "";
//            String requiredXP = Statistics.getExperienceNeeded(i) + "";
//            String formatted = StringFormatter.format("Lvl: {}, Xp Req: {}, Xp Add: {}", i, requiredXP, additionalXP);
//            System.out.println(formatted);
//        }
    }

//    @Test
//    public void statNodeTotalCanBeRead() {
//        Statistics stats = new Statistics();
//        stats.getStatsNode()
//        assertEquals(1, stats.getStatTotal(Constants.LEVEL));
//        assertEquals(0, stats.getStatCurrent(Constants.EXPERIENCE));
//        stats.toExperience(5);
//        assertEquals(2, stats.getStatTotal(Constants.LEVEL));
//    }
}
