package test;

import main.constants.Constants;
import main.game.components.Statistics;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StatisticsTest {

    @Test
    public void statsAreCorrectlyDefaulting() {
        Statistics stats = new Statistics();
        assertEquals(0, stats.getStatTotal(Constants.ENERGY));
        assertEquals(0, stats.getStatTotal(Constants.HEALTH));
        assertEquals(1, stats.getStatTotal(Constants.LEVEL));
        assertEquals(0, stats.getResourceCurrent(Constants.EXPERIENCE));
        assertEquals(0, stats.getStatTotal(Constants.PHYSICAL_ATTACK));
        assertEquals(0, stats.getStatTotal(Constants.PHYSICAL_DEFENSE));
        assertEquals(0, stats.getStatTotal(Constants.MAGICAL_ATTACK));
        assertEquals(0, stats.getStatTotal(Constants.MAGICAL_DEFENSE));
        assertEquals(0, stats.getStatTotal(Constants.CLIMB));
        assertEquals(0, stats.getStatTotal(Constants.MOVE));
        assertEquals(0, stats.getStatTotal(Constants.SPEED));
    }

    @Test
    public void statsNodeCorrectlyLevelsUp() {
        Statistics stats = new Statistics();
        assertEquals(1, stats.getStatTotal(Constants.LEVEL));
        assertEquals(0, stats.getResourceCurrent(Constants.EXPERIENCE));
        stats.toExperience(5);
        assertEquals(2, stats.getStatTotal(Constants.LEVEL));
    }

    @Test
    public void statsNodeBaseUnchangedAfterClear() {
        Statistics stats = new Statistics();
        stats.putStatsNode(Constants.MAGICAL_DEFENSE, 2);
        assertEquals(2, stats.getStatTotal(Constants.MAGICAL_DEFENSE));
        stats.getStatsNode(Constants.MAGICAL_DEFENSE).add("test", "flat", 3);
        assertEquals(5, stats.getStatTotal(Constants.MAGICAL_DEFENSE));
        stats.getStatsNode(Constants.MAGICAL_DEFENSE).clear();
        assertEquals(2, stats.getStatTotal(Constants.MAGICAL_DEFENSE));
    }
}
