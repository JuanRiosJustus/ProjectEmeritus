package test;

import main.constants.Constants;
import main.game.components.Statistics;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class StatisticsTest {

    @Test
    public void statsAreCorrectlyDefaulting() {
        Statistics stats = new Statistics();
        assertEquals(0, stats.getStatTotal(Constants.ENERGY));
        assertEquals(0, stats.getStatTotal(Constants.HEALTH));
        assertEquals(1, stats.getStatTotal(Constants.LEVEL));
        assertEquals(0, stats.getStatCurrent(Constants.EXPERIENCE));
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
        assertEquals(0, stats.getStatCurrent(Constants.EXPERIENCE));
        stats.toExperience(5);
        assertEquals(2, stats.getStatTotal(Constants.LEVEL));
    }

    @Test
    public void statsNodeBaseUnchangedAfterClear() {
        Map<String, Integer> nodes = new HashMap<>();
        nodes.put(Constants.MAGICAL_DEFENSE, 2);
        Statistics stats = new Statistics(nodes);
        assertEquals(2, stats.getStatTotal(Constants.MAGICAL_DEFENSE));
        stats.addModification(Constants.MAGICAL_DEFENSE,"test", "flat", 3);
        assertEquals(5, stats.getStatTotal(Constants.MAGICAL_DEFENSE));
        stats.clearModifications(Constants.MAGICAL_DEFENSE);
        assertEquals(2, stats.getStatTotal(Constants.MAGICAL_DEFENSE));
    }
}
