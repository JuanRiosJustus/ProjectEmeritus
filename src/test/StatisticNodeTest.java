package test;

import main.game.stats.StatisticNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticNodeTest {

    private StatisticNode statisticNode;

    @BeforeEach
    void setUp() {
        statisticNode = new StatisticNode("Health", 100);
    }

    @Test
    void testBaseValueInitialization() {
        assertEquals(100, statisticNode.getBase());
        assertEquals(100, statisticNode.getTotal());
        assertEquals(100, statisticNode.getCurrent());
        assertEquals(0, statisticNode.getModified());
    }

    @Test
    void testAdditiveModifier() {
        statisticNode.putAdditiveModification("Bonus HP", 50);
        assertEquals(150, statisticNode.getTotal());
        assertEquals(50, statisticNode.getModified());
    }

    @Test
    void testMultiplicativeModifier() {
        statisticNode.putMultiplicativeModification("20% Increase", 0.2f);
        assertEquals(120, statisticNode.getTotal());
        assertEquals(20, statisticNode.getModified());
    }

    @Test
    void testExponentialModifier() {
        statisticNode.putExponentialModification("Double HP", 2.0f);
        assertEquals(200, statisticNode.getTotal());
        assertEquals(100, statisticNode.getModified());
    }

    @Test
    void testCombinedModifiers() {
        statisticNode.putAdditiveModification("Flat Bonus", 50);
        statisticNode.putMultiplicativeModification("50% Increase", 0.5f);
        statisticNode.putExponentialModification("Double HP", 2.0f);

        assertTrue(statisticNode.isDirty());
        assertEquals(450, statisticNode.getTotal());
        assertEquals(350, statisticNode.getModified());
    }

    @Test
    void testCurrentValueClamping() {
        statisticNode.setCurrent(120);
        assertEquals(100, statisticNode.getCurrent()); // Clamped to max

        statisticNode.setCurrent(-20);
        assertEquals(0, statisticNode.getCurrent()); // Clamped to min
    }

    @Test
    void testGetMissingPercent() {
        statisticNode.setCurrent(75);
        assertEquals(0.25f, statisticNode.getMissingPercent(), 0.01f); // 25% missing
    }

    @Test
    void testGetCurrentPercent() {
        statisticNode.setCurrent(75);
        assertEquals(0.75f, statisticNode.getCurrentPercent(), 0.01f); // 75% remaining
    }

    @Test
    void testDirtyFlagBehavior() {
        assertTrue(statisticNode.isDirty()); // Dirty after initialization

        statisticNode.getTotal(); // Trigger recalculation
        assertFalse(statisticNode.isDirty()); // Clean after recalculation

        statisticNode.putAdditiveModification("New Bonus", 10);
        assertTrue(statisticNode.isDirty()); // Dirty after modifier addition
    }

    @Test
    void testNegativeModifiers() {
        statisticNode.putAdditiveModification("Negative Flat", -20);
        assertEquals(80, statisticNode.getTotal());

        statisticNode.putMultiplicativeModification("Negative Multiplier", -0.5f);
        assertEquals(40, statisticNode.getTotal());
    }
}