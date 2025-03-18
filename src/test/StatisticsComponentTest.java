package test;

import main.game.components.statistics.StatisticsComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsComponentTest {
    private StatisticsComponent statisticsComponent;

    @BeforeEach
    void setUp() {
        Map<String, Float> initialAttributes = new HashMap<>();
        initialAttributes.put("health", 100f);
        initialAttributes.put("mana", 50f);
        initialAttributes.put("stamina", 75f);

        statisticsComponent = new StatisticsComponent(initialAttributes);
    }

    @Test
    void testInitialization() {
        assertEquals(100, statisticsComponent.getTotalHealth());
        assertEquals(50, statisticsComponent.getTotalMana());
        assertEquals(75, statisticsComponent.getTotalStamina());

        assertEquals(100, statisticsComponent.getCurrentHealth());
        assertEquals(50, statisticsComponent.getCurrentMana());
        assertEquals(75, statisticsComponent.getCurrentStamina());
    }

    @Test
    void testAdditiveModification() {
        statisticsComponent.putAdditiveModification("health", "buff", 20, 5);
        assertEquals(120, statisticsComponent.getTotalHealth());
    }

    @Test
    void testMultiplicativeModification() {
        statisticsComponent.putMultiplicativeModification("health", "power boost", 0.2f, 5);
        assertEquals(120, statisticsComponent.getTotalHealth()); // 100 * 1.2
    }

    @Test
    void testAdditiveAndMultiplicativeTogether() {
        statisticsComponent.putAdditiveModification("health", "buff", 20, 5);
        statisticsComponent.putMultiplicativeModification("health", "power boost", 0.2f, 5);
        assertEquals(144, statisticsComponent.getTotalHealth()); // (100 + 20) * 1.2
    }

    @Test
    void testTagAdditionAndRemoval() {
        statisticsComponent.addTag("FireResistant");
        assertTrue(statisticsComponent.getTagKeys().contains("FireResistant"));

        statisticsComponent.removeTag("FireResistant");
        assertFalse(statisticsComponent.getTagKeys().contains("FireResistant"));
    }

    @Test
    void testResourceModification() {
        statisticsComponent.toResource("health", -30);
        assertEquals(70, statisticsComponent.getCurrentHealth());

        statisticsComponent.toResource("health", 50);
        assertEquals(100, statisticsComponent.getCurrentHealth()); // Should clamp to max
    }

    @Test
    void testScalingFunctionality() {
        statisticsComponent.putAdditiveModification("mana", "buff", 10, 3);
        assertEquals(60, statisticsComponent.getScaling("mana", "total"));
        assertEquals(10, statisticsComponent.getScaling("mana", "modification"));
    }

    @Test
    void testChecksumUpdates() {
        int initialChecksum = statisticsComponent.getChecksum();
        statisticsComponent.putAdditiveModification("health", "buff", 10, 5);
        assertNotEquals(initialChecksum, statisticsComponent.getChecksum());
    }

    @Test
    void testAttributeKeys() {
        Set<String> keys = statisticsComponent.getAttributeKeys();
        assertTrue(keys.contains("health"));
        assertTrue(keys.contains("mana"));
        assertTrue(keys.contains("stamina"));
    }

    @Test
    void testExperienceGain() {
        boolean leveledUp = statisticsComponent.toExperience(100);
        assertFalse(leveledUp); // No leveling logic implemented yet
    }
}