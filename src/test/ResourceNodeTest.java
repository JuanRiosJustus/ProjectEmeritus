package test;

import main.game.stats.node.ResourceNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ResourceNodeTest {

    @Test
    public void nodeCurrentDoesNotGoAboveMaxOrBelowMin() {
        ResourceNode node = new ResourceNode("health", 100);
        assertEquals(100, node.getCurrent());
        assertEquals(100, node.getTotal());
        node.add(-50);
        assertEquals(50, node.getCurrent());
        assertEquals(100, node.getTotal());
        node.add(-51);
        assertEquals(0, node.getCurrent());
        assertEquals(100, node.getTotal());
        node.add(102);
        assertEquals(100, node.getCurrent());
        assertEquals(100, node.getTotal());
        assertEquals("health", node.getName());
    }

    @Test
    public void nodeBaseTotalGoesUpAndDownAppropriately() {
        ResourceNode node = new ResourceNode("health", 100);
        assertEquals(100, node.getTotal());
        assertEquals(100, node.getCurrent());
        node.add("test", "flat", 50);
        assertEquals(150, node.getTotal());
        assertEquals(100, node.getCurrent());
        assertEquals(50, node.getModified());
        assertEquals("health", node.getName());
    }

    @Test
    public void nodeCurrentValueBehavesCorrectly() {
        ResourceNode node = new ResourceNode("experience", 3);
        assertEquals("experience", node.getName());
        assertEquals(3, node.getTotal());
        assertEquals(3, node.getCurrent());
        assertEquals(0, node.getMissing());
        node.add(-40);
        assertEquals(3, node.getTotal());
        assertEquals(0, node.getCurrent());
        assertEquals(3, node.getMissing());
        node.add(2);
        assertEquals(3, node.getTotal());
        assertEquals(2, node.getCurrent());
        assertEquals(1, node.getMissing());
        node.add(500);
        assertEquals(3, node.getTotal());
        assertEquals(3, node.getCurrent());
        assertEquals(0, node.getMissing());
        assertEquals(Double.valueOf("1"), Double.valueOf(node.getPercentage()));
    }
}