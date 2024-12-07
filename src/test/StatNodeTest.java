package test;

import main.game.stats.StatNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatNodeTest {

    private StatNode stat;

    @BeforeEach
    void setUp() {
        stat = new StatNode("TestStat", 100);
    }

    @Test
    void testInitialValues() {
        assertEquals(100, stat.getBase());
        assertEquals(100, stat.getTotal());
        assertEquals(100, stat.getCurrent());
        assertEquals(0, stat.getModified());
    }

    @Test
    void testAdditiveModifiers() {
        stat.putAdditiveModification("Buff1", 10); // +10
        stat.putAdditiveModification("Buff2", -5); // -5
        assertEquals(105, stat.getTotal());
        assertEquals(5, stat.getModified());
    }

    @Test
    void testMultiplicativeModifiers() {
        stat.putMultiplicativeModification("Buff1", 0.2f); // +20%
        stat.putMultiplicativeModification("Debuff1", -0.1f); // -10%
        assertEquals(108, stat.getTotal()); // Base + 20% - 10% = 108
    }

    @Test
    void testExponentialModifiers() {
        stat.putExponentialModification("Overdrive", 1.5f); // *1.5
        assertEquals(150, stat.getTotal()); // Base * 1.5
    }

    @Test
    void testCombinedModifiers() {
        stat.putAdditiveModification("FlatBonus", 10); // +10
        stat.putMultiplicativeModification("PercentageBonus", 0.2f); // +20%
        stat.putExponentialModification("Overdrive", 1.5f); // *1.5
        assertEquals(198, stat.getTotal()); // (Base + Additive) * Multiplicative * Exponential
    }

    @Test
    void testCurrentValueAdjustments() {
        stat.setCurrent(80); // Set to 80
        assertEquals(80, stat.getCurrent());

        stat.adjustCurrent(-30); // Decrease by 30
        assertEquals(50, stat.getCurrent());

        stat.adjustCurrent(60); // Increase by 60, clamped to total
        assertEquals(100, stat.getCurrent());
    }

    @Test
    void testClampedCurrentValue() {
        stat.setCurrent(150); // Attempt to set above total
        assertEquals(100, stat.getCurrent()); // Should clamp to total

        stat.setCurrent(-10); // Attempt to set below zero
        assertEquals(0, stat.getCurrent()); // Should clamp to zero
    }

    @Test
    void testStatDirtinessHandling() {
        stat.putAdditiveModification("Buff1", 10); // +10
        stat.putMultiplicativeModification("Buff2", 0.2f); // +20%
        assertTrue(stat.getTotal() > 0); // Ensure the total is recalculated correctly

        stat.setBase(120); // Update base
        assertEquals(120, stat.getBase());
        assertEquals(156, stat.getTotal()); // (Base + Additive) * Multiplicative
    }

    @Test
    void testMissingAndCurrentPercent() {
        stat.setCurrent(80); // Current is 80
        assertEquals(0.2f, stat.getMissingPercent(), 0.01f); // 20% missing
        assertEquals(0.8f, stat.getCurrentPercent(), 0.01f); // 80% remaining
    }

    @Test
    void testEdgeCasesForModifiers() {
        stat.putAdditiveModification("ZeroAdd", 0); // No change
        stat.putMultiplicativeModification("ZeroMult", 0); // No change
        stat.putExponentialModification("ZeroExp", 1); // No change
        assertEquals(100, stat.getTotal()); // Should remain the same

        stat.putAdditiveModification("NegativeAdd", -200); // Base becomes effectively negative
        assertEquals(-100, stat.getTotal());
    }

    @Test
    void testNegativeMultiplicativeModifiers() {
        stat.putMultiplicativeModification("NegativeMult", -1.0f); // 100% reduction
        assertEquals(0, stat.getTotal()); // Stat reduced to 0
    }

    @Test
    void testExponentialModifiersEdgeCase() {
        stat.putExponentialModification("ZeroExp", 0); // Multiplier of 0
        assertEquals(0, stat.getTotal()); // Stat reduced to 0
    }
}