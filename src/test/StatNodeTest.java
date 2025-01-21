package test;

import main.game.stats.StatNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StatNodeTest {

    private StatNode statNode;

    @BeforeEach
    void setUp() {
        statNode = new StatNode("Health", 100);
    }

    @Test
    void testBaseValueInitialization() {
        assertEquals(100, statNode.getBase());
        assertEquals(100, statNode.getTotal());
        assertEquals(100, statNode.getCurrent());
        assertEquals(0, statNode.getBonus());
    }

    @Test
    void testAdditiveModifier() {
        statNode.putAdditiveModification("Bonus HP", 50);
        assertEquals(150, statNode.getTotal());
        assertEquals(50, statNode.getBonus());
    }

    @Test
    void testMultiplicativeModifier() {
        statNode.putMultiplicativeModification("20% Increase", 0.2f);
        assertEquals(120, statNode.getTotal());
        assertEquals(20, statNode.getBonus());
    }

    @Test
    void testExponentialModifier() {
        statNode.putExponentialModification("Double HP", 2.0f);
        assertEquals(200, statNode.getTotal());
        assertEquals(100, statNode.getBonus());
    }

    @Test
    void testCombinedModifiers() {
        statNode.putAdditiveModification("Flat Bonus", 50);
        statNode.putMultiplicativeModification("50% Increase", 0.5f);
        statNode.putExponentialModification("Double HP", 2.0f);

        assertTrue(statNode.isDirty());
        assertEquals(450, statNode.getTotal());
        assertEquals(350, statNode.getBonus());
    }

    @Test
    void testCurrentValueClamping() {
        statNode.setCurrent(120);
        assertEquals(100, statNode.getCurrent()); // Clamped to max

        statNode.setCurrent(-20);
        assertEquals(0, statNode.getCurrent()); // Clamped to min
    }

    @Test
    void testGetMissingPercent() {
        statNode.setCurrent(75);
        assertEquals(0.25f, statNode.getMissingPercent(), 0.01f); // 25% missing
    }

    @Test
    void testGetCurrentPercent() {
        statNode.setCurrent(75);
        assertEquals(0.75f, statNode.getCurrentPercent(), 0.01f); // 75% remaining
    }

    @Test
    void testDirtyFlagBehavior() {
        assertTrue(statNode.isDirty()); // Dirty after initialization

        statNode.getTotal(); // Trigger recalculation
        assertFalse(statNode.isDirty()); // Clean after recalculation

        statNode.putAdditiveModification("New Bonus", 10);
        assertTrue(statNode.isDirty()); // Dirty after modifier addition
    }

    @Test
    void testNegativeModifiers() {
        statNode.putAdditiveModification("Negative Flat", -20);
        assertEquals(80, statNode.getTotal());

        statNode.putMultiplicativeModification("Negative Multiplier", -0.5f);
        assertEquals(40, statNode.getTotal());
    }
}