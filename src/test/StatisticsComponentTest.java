package test;

import main.game.stats.StatNode;
import main.game.components.StatisticsComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsComponentTest {

//    private StatisticsComponent statsComponent;
//
//    @BeforeEach
//    void setUp() {
//        Map<String, Integer> initialStats = Map.of(
//                StatisticsComponent.HEALTH, 100,
//                StatisticsComponent.MANA, 50,
//                StatisticsComponent.STAMINA, 30,
//                StatisticsComponent.MOVE, 5
//        );
//        statsComponent = new StatisticsComponent(initialStats);
//    }
//
//    @Test
//    void testBaseStatsInitialization() {
//        assertEquals(100, statsComponent.getBase(StatisticsComponent.HEALTH));
//        assertEquals(50, statsComponent.getBase(StatisticsComponent.MANA));
//        assertEquals(30, statsComponent.getBase(StatisticsComponent.STAMINA));
//        assertEquals(5, statsComponent.getBase(StatisticsComponent.MOVE));
//    }
//
//    @Test
//    void testAdditiveModifier() {
//        statsComponent.modify(StatisticsComponent.HEALTH, 20);
//        assertEquals(120, statsComponent.getTotal(StatisticsComponent.HEALTH));
//    }
//
//    @Test
//    void testResourceAdjustment() {
//        statsComponent.reduceResource(StatisticsComponent.HEALTH, 20);
//        assertEquals(80, statsComponent.getCurrentHealth());
//
//        statsComponent.addResource(StatisticsComponent.HEALTH, 10);
//        assertEquals(90, statsComponent.getCurrentHealth());
//    }
//
//    @Test
//    void testExperienceToLevelUp() {
//        statsComponent.modify(StatisticsComponent.EXPERIENCE, 1000);
//        boolean leveledUp = statsComponent.toExperience(500);
//        assertEquals(1, statsComponent.getLevel()); // Level before experience applied
//        assertEquals(500, statsComponent.getCurrent(StatisticsComponent.EXPERIENCE)); // Remaining experience
//
//        if (leveledUp) {
//            assertEquals(2, statsComponent.getLevel()); // Should have leveled up
//        }
//    }
//
//    @Test
//    void testCurrentHealthClamping() {
//        statsComponent.reduceResource(StatisticsComponent.HEALTH, 200); // Attempt to reduce below zero
//        assertEquals(0, statsComponent.getCurrentHealth()); // Current health should not go below zero
//
//        statsComponent.addResource(StatisticsComponent.HEALTH, 200); // Attempt to exceed total
//        assertEquals(statsComponent.getTotalHealth(), statsComponent.getCurrentHealth()); // Current health should not exceed total
//    }
//
//    @Test
//    void testGetTotalMovement() {
//        assertEquals(5, statsComponent.getTotalMovement());
//        statsComponent.modify(StatisticsComponent.MOVE, 2);
//        assertEquals(7, statsComponent.getTotalMovement());
//    }
//
//    @Test
//    void testGetExperienceNeeded() {
//        int level = 5;
//        int expectedExperience = StatisticsComponent.getExperienceNeeded(level);
//        int calculatedExperience = (int) Math.floor(10 * Math.pow(level, 2.1));
//        assertEquals(calculatedExperience, expectedExperience);
//    }
}