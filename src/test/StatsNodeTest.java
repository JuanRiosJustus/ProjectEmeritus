package test;

import main.game.stats.node.StatsNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class StatsNodeTest {

    @Test
    public void statsNodeHasExpectedDefaults() {
        StatsNode node = new StatsNode("attack", 100);
        assertEquals(100, node.getTotal());
        assertEquals(100, node.getBase());
        assertEquals(0, node.getMods());
        assertEquals("attack", node.getName());
    }

    @Test
    public void statsNodeReceivesCorrectUpdates() {
        StatsNode node = new StatsNode("attack", 100);
        assertEquals(100, node.getTotal());
        assertEquals(100, node.getBase());
        assertEquals(0, node.getMods());
        node.add(null, "Flat", 50);
        assertEquals(150, node.getTotal());
        assertEquals(50, node.getMods());
        assertEquals(100, node.getBase());
        node.add(null, "Flat", 50);
        assertEquals(200, node.getTotal());
        assertEquals(100, node.getMods());
        assertEquals(100, node.getBase());
        node.add(2, "Percent", .25f);
        assertEquals(250, node.getTotal());
        assertEquals(150, node.getMods());
        assertEquals(100, node.getBase());
        node.remove(2);
        assertEquals(200, node.getTotal());
        assertEquals(100, node.getMods());
        assertEquals(100, node.getBase());
        assertEquals("attack", node.getName());
    }
}
