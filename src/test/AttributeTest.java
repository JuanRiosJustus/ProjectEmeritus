package test;

import main.game.stats.Attribute;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AttributeTest {
    private Attribute attribute;

    @BeforeEach
    void setUp() {
        attribute = new Attribute("Health", 100);
    }

    @Test
    void testInitialValues() {
        assertEquals(100, attribute.getBase());
        assertEquals(0, attribute.getModified());
        assertEquals(100, attribute.getTotal());
        assertEquals(100, attribute.getCurrent());
    }

    @Test
    void testAdditiveModification() {
        attribute.putAdditiveModification("Buff", 20);
        assertEquals(20, attribute.getModified());
        assertEquals(120, attribute.getTotal());
    }

    @Test
    void testMultiplicativeModification() {
        attribute.putMultiplicativeModification("Power Boost", 0.2f); // 20% increase
        assertEquals(120, attribute.getTotal()); // 100 * 1.2
    }

    @Test
    void testAdditiveAndMultiplicativeTogether() {
        attribute.putAdditiveModification("Buff", 20);
        attribute.putMultiplicativeModification("Power Boost", 0.2f);
        assertEquals(144, attribute.getTotal()); // (100 + 20) * 1.2
    }

    @Test
    void testSettingBaseUpdatesTotal() {
        attribute.setBase(200);
        assertEquals(200, attribute.getBase());
        assertEquals(200, attribute.getTotal());
    }

    @Test
    void testCurrentValueClamping() {
        attribute.setCurrent(120);
        assertEquals(100, attribute.getCurrent()); // Should clamp to total

        attribute.setCurrent(-10);
        assertEquals(0, attribute.getCurrent()); // Should clamp to zero
    }

    @Test
    void testMissingHealth() {
        attribute.setCurrent(50);
        assertEquals(50, attribute.getMissing());
    }

    @Test
    void testScaling() {
        attribute.putAdditiveModification("Buff", 20);
        assertEquals(120, attribute.getScaling("total"));
        assertEquals(20, attribute.getScaling("modification"));
    }

    @Test
    void testDurationAndAging() {
        attribute.putAdditiveModification("Temporary Buff", 30, 2);
        assertEquals(30, attribute.getModified());

        attribute.updateAges();
        attribute.updateAges();
        attribute.updateAges(); // Should expire after 2 updates

        assertEquals(0, attribute.getModified()); // Buff should be removed
    }

    @Test
    void testHashConsistency() {
        int initialHash = attribute.hashState();
        attribute.putAdditiveModification("Buff", 10);
        assertNotEquals(initialHash, attribute.hashState());
    }

    @Test
    void testChecksumUpdates() {
        int initialChecksum = attribute.getCheckSum();
        attribute.putAdditiveModification("Buff", 10);
        assertNotEquals(initialChecksum, attribute.getCheckSum());
    }
}