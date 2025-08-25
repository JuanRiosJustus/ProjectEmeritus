package test;

import com.alibaba.fastjson2.JSONObject;
import main.game.components.statistics.Statistics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsTest {

    private Statistics createStats() {
        JSONObject baseValues = new JSONObject();
        baseValues.put("health", 100f);
        baseValues.put("attack", 50f);
        return new Statistics(baseValues);
    }

    @Test
    void testGetBase() {
        Statistics stats = createStats();
        assertEquals(100f, stats.getBase("health"));
        assertEquals(50f, stats.getBase("attack"));
    }

    @Test
    void testSetBase() {
        Statistics stats = createStats();
        stats.setBase("defense", 30f);
        assertEquals(30f, stats.getBase("defense"));
    }

    @Test
    void testAddBonusDefaultSource() {
        Statistics stats = createStats();
        stats.addBonus("health", 20f);
        assertEquals(20f, stats.getBonus("health", "unknown"));
        assertEquals(20f, stats.getBonuses("health"));
    }

    @Test
    void testAddBonusWithSource() {
        Statistics stats = createStats();
        stats.addBonus("attack", 10f, "sword");
        stats.addBonus("attack", 5f, "buff");
        assertEquals(10f, stats.getBonus("attack", "sword"));
        assertEquals(5f, stats.getBonus("attack", "buff"));
        assertEquals(15f, stats.getBonuses("attack"));
    }

    @Test
    void testGetBonusesWhenEmpty() {
        Statistics stats = createStats();
        assertEquals(0f, stats.getBonuses("speed")); // not in base, should initialize and return 0
    }

    @Test
    void testRemoveFromCurrent() {
        Statistics stats = createStats();

        // remove less than total
        stats.removeFromCurrent("health", 30f);
        float afterRemoval = stats.getBase("health") + stats.getBonuses("health") - 30f;
        assertEquals(afterRemoval, stats.getCurrent("health"));

        // remove more than total -> should clamp at 0
        stats.removeFromCurrent("health", 200f);
        assertEquals(0f, stats.getCurrent("health"));
    }

    @Test
    void testAddToCurrent() {
        Statistics stats = createStats();

        // reduce first to make sure add works
        stats.removeFromCurrent("health", 50f);
        stats.addToCurrent("health", 20f);
        assertEquals(70f, stats.getCurrent("health"));

        // add more than max -> should clamp at total (base + bonuses)
        stats.addToCurrent("health", 200f);
        float total = stats.getBase("health") + stats.getBonuses("health");
        assertEquals(total, stats.getCurrent("health"));
    }

    @Test
    void testAddAndRemoveMix() {
        Statistics stats = createStats();
        stats.addBonus("health", 20f, "potion");
        float total = stats.getBase("health") + stats.getBonuses("health");

        // remove then add back
        stats.removeFromCurrent("health", 50f);
        stats.addToCurrent("health", 30f);

        float current = stats.getCurrent("health");
        assertTrue(current <= total && current >= 0);
    }
}