package test;

import main.game.foundation.LootTable;
import org.junit.Test;

import static org.junit.Assert.*;

public class LootTableTest {

    @Test
    public void testLootTable_100_percent_chance() {
        LootTable<String> table = new LootTable<>();
        table.add("100", 1);
        assertEquals(table.getDrop(), "100");
    }

    @Test
    public void testLootTable_0_percent_chance() {
        LootTable<String> table = new LootTable<>();
        table.add("0", 0);
        assertNull(table.getDrop());
    }

    @Test
    public void testLootTable1() {
        LootTable<String> table = new LootTable<>();
        table.add("49Percent", .49f);
        table.add("01Percent", .01f);

        int nullCounts = 0;

        // call table many times
        int times = 1000;
        for (int i = 0; i < times; i++) {
            if (table.getDrop() == null) {
                nullCounts++;
            }
        }
        assertTrue(nullCounts > 0);
        assertTrue(nullCounts < times);
        assertTrue(nullCounts > times * .4);
        assertTrue(nullCounts < times * .6);
    }
}
