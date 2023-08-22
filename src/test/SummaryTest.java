package test;

import main.constants.Constants;
import main.game.components.Summary;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SummaryTest {

    @Test
    public void statsAreCorrectlyDefaulting() {
        Summary stats = new Summary();
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
        Summary stats = new Summary();
        assertEquals(1, stats.getStatTotal(Constants.LEVEL));
        assertEquals(0, stats.getStatCurrent(Constants.EXPERIENCE));
        stats.toExperience(5);
        assertEquals(2, stats.getStatTotal(Constants.LEVEL));
    }

    @Test
    public void statsNodeBaseUnchangedAfterClear() {
        Map<String, Integer> nodes = new HashMap<>();
        nodes.put(Constants.MAGICAL_DEFENSE, 2);
        Summary stats = new Summary(nodes);
        assertEquals(2, stats.getStatTotal(Constants.MAGICAL_DEFENSE));
        stats.addModification(Constants.MAGICAL_DEFENSE,"test", "flat", 3);
        assertEquals(5, stats.getStatTotal(Constants.MAGICAL_DEFENSE));
        stats.clearModifications(Constants.MAGICAL_DEFENSE);
        assertEquals(2, stats.getStatTotal(Constants.MAGICAL_DEFENSE));
    }
}
