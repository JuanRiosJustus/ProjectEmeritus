package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import main.game.components.statistics.StatisticsComponent;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticsComponentTest {

    @Test
    void testAddAndRemoveTags() {
        StatisticsComponent stats = new StatisticsComponent();

        // Add tags
        stats.addTag("warrior");
        stats.addTag("warrior");
        stats.addTag("mage");

        Map<String, Integer> tags = stats.getTags();
        assertEquals(2, tags.get("warrior"));
        assertEquals(1, tags.get("mage"));

        // Remove tags
        stats.removeTag("warrior");
        tags = stats.getTags();
        assertEquals(1, tags.get("warrior"));

        stats.removeTag("warrior");
        tags = stats.getTags();
        assertFalse(tags.containsKey("warrior")); // Tag should be removed completely
    }

    @Test
    void testPutAndGetAbilities() {
        StatisticsComponent stats = new StatisticsComponent();
        List<String> abilities = Arrays.asList("fireball", "heal", "teleport");

        stats.putAbilities(abilities);
        Set<String> retrievedAbilities = stats.getAbilities();

        assertEquals(abilities.size(), retrievedAbilities.size());
        assertTrue(retrievedAbilities.contains("fireball"));
        assertTrue(retrievedAbilities.contains("heal"));
        assertTrue(retrievedAbilities.contains("teleport"));
    }

    @Test
    void testPutAndGetType() {
        StatisticsComponent stats = new StatisticsComponent();
        List<String> types = Arrays.asList("human", "knight");

        stats.putType(types);
        Set<String> retrievedTypes = stats.getType();

        assertEquals(types.size(), retrievedTypes.size());
        assertTrue(retrievedTypes.contains("human"));
        assertTrue(retrievedTypes.contains("knight"));
    }

    @Test
    void testResourceManagement() {
        StatisticsComponent stats = new StatisticsComponent(Map.of(
                "health", 100f,
                "mana", 50f,
                "stamina", 75f
        ));

        assertEquals(100, stats.getTotalHealth());
        assertEquals(50, stats.getTotalMana());
        assertEquals(75, stats.getTotalStamina());

        // Modify resources
        stats.toResource("health", -20);
        stats.toResource("mana", 10);
        assertEquals(80, stats.getCurrentHealth());
        assertEquals(50, stats.getCurrentMana()); // Current can't go past total
    }

    @Test
    void testExperienceNeededCalculation() {
        int level1XP = StatisticsComponent.getExperienceNeeded(1);
        int level5XP = StatisticsComponent.getExperienceNeeded(5);
        int level10XP = StatisticsComponent.getExperienceNeeded(10);

        assertTrue(level1XP < level5XP);
        assertTrue(level5XP < level10XP);
    }

    @Test
    void testStatisticNodeKeys() {
        StatisticsComponent stats = new StatisticsComponent(Map.of(
                "physical_attack", 15f,
                "magical_attack", 20f,
                "defense", 10f
        ));

        Set<String> keys = stats.getStatisticNodeKeys();
        assertTrue(keys.contains("physical_attack"));
        assertTrue(keys.contains("magical_attack"));
        assertTrue(keys.contains("defense"));
        assertFalse(keys.contains("health")); // Health was not initialized
    }

    @Test
    void testHashStateConsistency() {
        StatisticsComponent stats = new StatisticsComponent(Map.of(
                "health", 100f,
                "mana", 50f
        ));

        int initialHash = stats.getHashState();

        stats.toResource("health", -10);
        int updatedHash = stats.getHashState();

        assertNotEquals(initialHash, updatedHash); // Hash state should change after modification
    }
}