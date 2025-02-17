package test.statisticscomponent;

import main.game.components.statistics.StatisticsComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StatisticsComponentTest {
    private StatisticsComponent stats;

    @BeforeEach
    public void setUp() {
        Map<String, Float> attributes = new HashMap<>();
        attributes.put("health", 100f);
        attributes.put("mana", 50f);
        attributes.put("stamina", 75f);
        attributes.put("level", 1f);
        attributes.put("experience", 0f);
        attributes.put("physical_attack", 10f);
        attributes.put("physical_defense", 5f);
        attributes.put("magical_attack", 15f);
        attributes.put("magical_defense", 8f);
        attributes.put("speed", 7f);

        stats = new StatisticsComponent(attributes);
    }

    @Test
    public void testInitialization() {
        assertEquals(100, stats.getTotalHealth());
        assertEquals(50, stats.getTotalMana());
        assertEquals(75, stats.getTotalStamina());
        assertEquals(1, stats.getLevel());
        assertEquals(10, stats.getTotalPhysicalAttack());
        assertEquals(5, stats.getTotalPhysicalDefense());
        assertEquals(15, stats.getTotalMagicalAttack());
        assertEquals(8, stats.getTotalMagicalDefense());
        assertEquals(7, stats.getTotalSpeed());
    }

    @Test
    public void testAdditiveModification() {
        stats.putAdditiveModification("health", "Buff1", 20, 3);
        assertEquals(20, stats.getModified("health"));
        assertEquals(120, stats.getTotalHealth());
    }

    @Test
    public void testMultiplicativeModification() {
        stats.putMultiplicativeModification("health", "Buff1", 0.2f, 3); // +20%
        stats.putMultiplicativeModification("health", "Buff2", 0.1f, 3); // +10%

        assertEquals(32, stats.getModified("health")); // (100 * 1.2 * 1.1) - 100 = 32
        assertEquals(132, stats.getTotalHealth()); // 100 * 1.2 * 1.1 = 132
    }

    @Test
    public void testResourceManipulation() {
        stats.toResource("health", -30); // Reduce health by 30
        assertEquals(70, stats.getCurrentHealth());

        stats.toResource("health", 20); // Increase health by 20
        assertEquals(90, stats.getCurrentHealth());

        stats.toResource("mana", -20);
        assertEquals(30, stats.getCurrentMana());

        stats.toResource("mana", 10);
        assertEquals(40, stats.getCurrentMana());
    }

    @Test
    public void testExperienceGain() {
//        int initialLevel = stats.getLevel();
//        boolean leveledUp = stats.toExperience(500);
//        assertTrue(leveledUp);
//        assertTrue(stats.getLevel() > initialLevel);
    }

    @Test
    public void testTagsManagement() {
        stats.addTag("fire_resistant");
        stats.addTag("fire_resistant");
        stats.addTag("poison_resistant");

        assertEquals(2, stats.getTag("fire_resistant"));
        assertEquals(1, stats.getTag("poison_resistant"));

        stats.removeTag("fire_resistant");
        assertEquals(1, stats.getTag("fire_resistant"));

        stats.removeTag("fire_resistant");
        assertEquals(0, stats.getTag("fire_resistant"));
    }

    @Test
    public void testStatisticNodeKeys() {
        Set<String> keys = stats.getStatisticNodeKeys();
        assertTrue(keys.contains("health"));
        assertTrue(keys.contains("mana"));
        assertTrue(keys.contains("stamina"));
        assertTrue(keys.contains("level"));
        assertTrue(keys.contains("experience"));
        assertTrue(keys.contains("physical_attack"));
        assertTrue(keys.contains("physical_defense"));
        assertTrue(keys.contains("magical_attack"));
        assertTrue(keys.contains("magical_defense"));
        assertTrue(keys.contains("speed"));
    }

    @Test
    public void testHashStateChanges() {
//        int initialHash = stats.getHashState();
//        stats.putAdditiveModification("health", "Buff1", 10, 3);
//        assertNotEquals(initialHash, stats.getHashState());
    }
}
