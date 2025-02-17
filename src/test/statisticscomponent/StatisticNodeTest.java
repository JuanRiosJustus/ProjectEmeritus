package test.statisticscomponent;

import main.game.stats.StatisticNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticNodeTest {

    private StatisticNode stat;

    @BeforeEach
    void setUp() {
        stat = new StatisticNode("Health", 100);
    }

    @Test
    void testInitialization() {
        assertEquals(100, stat.getBase());
        assertEquals(100, stat.getTotal());
        assertEquals(100, stat.getCurrent());
        assertEquals(0, stat.getModified());
    }

    @Test
    void testAdditiveModification() {
        stat.putAdditiveModification("Buff1", 20);
        stat.putAdditiveModification("Buff2", 10);

        assertTrue(stat.isDirty());
        assertEquals(30, stat.getModified());
        assertEquals(130, stat.getTotal());
    }

    @Test
    void testMultiplicativeModification() {
        stat.putMultiplicativeModification("Buff1", 0.2f); // +20%
        stat.putMultiplicativeModification("Buff2", 0.1f); // +10%

        assertTrue(stat.isDirty());
        assertEquals(32, stat.getModified()); // Corrected: 132 - 100 = 32
        assertEquals(132, stat.getTotal());   // Corrected: 100 * 1.2 * 1.1 = 132
    }

    @Test
    void testCurrentValueClamping() {
        stat.setCurrent(150);
        assertEquals(100, stat.getCurrent()); // Clamped to max total

        stat.setCurrent(-50);
        assertEquals(0, stat.getCurrent()); // Clamped to 0

        stat.putAdditiveModification("Buff", 20);
        stat.setCurrent(110);
        assertEquals(110, stat.getCurrent());

        stat.setCurrent(150);
        assertEquals(120, stat.getCurrent());
    }

    @Test
    void testMissingValueCalculation() {
        stat.setCurrent(70);
        assertEquals(30, stat.getMissing());

        stat.putAdditiveModification("Buff", 20);
        assertEquals(50, stat.getMissing()); // New max is 120
    }

    @Test
    void testPercentCalculations() {
        stat.setCurrent(50);
        assertEquals(0.5f, stat.getCurrentPercent(), 0.01);
        assertEquals(0.5f, stat.getMissingPercent(), 0.01);
        assertEquals(1.0f, stat.getTotalPercent(), 0.01);

        stat.setCurrent(75);
        assertEquals(0.75f, stat.getCurrentPercent(), 0.01);
        assertEquals(0.25f, stat.getMissingPercent(), 0.01);
        assertEquals(1.0f, stat.getTotalPercent(), 0.01);
    }

    @Test
    void testAgeOfModifiers() {
        stat.putAdditiveModification("Buff1", 10, 3);
        assertEquals(0, stat.getAge("Buff1"));
        assertEquals(110, stat.getTotal());

        stat.updateAges();
        assertEquals(1, stat.getAge("Buff1"));
        assertEquals(110, stat.getTotal());

        stat.updateAges();
        assertEquals(2, stat.getAge("Buff1"));
        assertEquals(110, stat.getTotal());

        stat.updateAges();
        assertEquals(3, stat.getAge("Buff1"));
        assertEquals(110, stat.getTotal());

        stat.updateAges();
        assertEquals(100, stat.getTotal());
    }

    @Test
    void testAgeOfPermanentModifiers() {
        stat.putAdditiveModification("Buff1", 10, -1);
        assertEquals(0, stat.getAge("Buff1"));
        assertEquals(-1, stat.getDuration("Buff1"));
        assertEquals(110, stat.getTotal());

        stat.updateAges();
        assertEquals(1, stat.getAge("Buff1"));
        assertEquals(-1, stat.getDuration("Buff1"));
        assertEquals(110, stat.getTotal());

        stat.updateAges();
        assertEquals(2, stat.getAge("Buff1"));
        assertEquals(-1, stat.getDuration("Buff1"));
        assertEquals(110, stat.getTotal());

        stat.updateAges();
        assertEquals(3, stat.getAge("Buff1"));
        assertEquals(-1, stat.getDuration("Buff1"));
        assertEquals(110, stat.getTotal());

        stat.updateAges();
        assertEquals(4, stat.getAge("Buff1"));
        assertEquals(-1, stat.getDuration("Buff1"));
        assertEquals(110, stat.getTotal());
    }

    @Test
    void testHashState() {
        int initialHash = stat.hashState();

        stat.putAdditiveModification("Buff", 20);
        int newHash = stat.hashState();

        assertNotEquals(initialHash, newHash);
    }

    @Test
    void testDirtyStateClearing() {
        stat.putAdditiveModification("Buff", 20);
        assertTrue(stat.isDirty());

        stat.getTotal(); // This should recalculate
        assertFalse(stat.isDirty());
    }
}
