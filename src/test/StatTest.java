package test;

import main.game.stats.Stat;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class StatTest {

    @Test
    public void statsNodeHasExpectedDefaults() {
        Stat node = new Stat("attack", 100);
        assertEquals(100, node.getTotal());
        assertEquals(100, node.getBase());
        assertEquals(0, node.getModified());
        assertEquals("attack", node.getName());
    }

    @Test
    public void statsNodeReceivesCorrectUpdates() {
        Stat node = new Stat("attack", 100);
        assertEquals(100, node.getTotal());
        assertEquals(100, node.getBase());
        assertEquals(0, node.getModified());
        node.add(null, "Flat", 50);
        assertEquals(150, node.getTotal());
        assertEquals(50, node.getModified());
        assertEquals(100, node.getBase());
        node.add(null, "Flat", 50);
        assertEquals(200, node.getTotal());
        assertEquals(100, node.getModified());
        assertEquals(100, node.getBase());
        node.add(2, "Percent", .25f);
        assertEquals(250, node.getTotal());
        assertEquals(150, node.getModified());
        assertEquals(100, node.getBase());
        node.remove(2);
        assertEquals(200, node.getTotal());
        assertEquals(100, node.getModified());
        assertEquals(100, node.getBase());
        assertEquals("attack", node.getName());
    }
}
