package test;

import main.game.stats.StatNode;
import org.junit.Test;

import static org.junit.Assert.*;

public class StateNodeTest {

    @Test
    public void statsNodeHasExpectedDefaults() {
        StatNode node = new StatNode("attack", 100);
        assertEquals(100, node.getTotal());
        assertEquals(100, node.getBase());
        assertEquals(0, node.getModified());
        assertEquals("attack", node.getName());
    }

    @Test
    public void statNodeHasExpectedAdditive() {
        StatNode node = new StatNode("attack", 100);
        assertEquals(100, node.getTotal());
        assertEquals(100, node.getBase());
        assertEquals(0, node.getModified());

        node.modify(null, StatNode.ADDITIVE, 50);
        assertEquals(150, node.getTotal());
        assertEquals(50, node.getModified());
        assertEquals(100, node.getBase());
    }

    @Test
    public void statNodeHasExpectedMultiplicative() {
        StatNode node = new StatNode("attack", 100);
        assertEquals(100, node.getTotal());
        assertEquals(100, node.getBase());
        assertEquals(0, node.getModified());

        node.modify(null, StatNode.MULTIPLICATIVE, .15f);
        assertEquals(115, node.getTotal());
        assertEquals(15, node.getModified());
        assertEquals(100, node.getBase());
    }

    @Test
    public void statNodeHasExpectedExponential() {
        StatNode node = new StatNode("attack", 100);
        assertEquals(100, node.getTotal());
        assertEquals(100, node.getBase());
        assertEquals(0, node.getModified());

        node.modify(null, StatNode.MULTIPLICATIVE, .5f);
        assertEquals(150, node.getTotal());
        assertEquals(50, node.getModified());
        assertEquals(100, node.getBase());

        node.modify(null, StatNode.EXPONENTIAL, .5f);
        assertEquals(225, node.getTotal());
        assertEquals(125, node.getModified());
        assertEquals(100, node.getBase());
    }

    @Test
    public void statNodeHasExpectedRemovedValue() {
        StatNode node = new StatNode("attack", 100);
        assertEquals(100, node.getTotal());
        assertEquals(100, node.getBase());
        assertEquals(0, node.getModified());

        String toRemove = "TO_REMOVE";
        node.modify(toRemove, StatNode.MULTIPLICATIVE, .5f);
        assertEquals(150, node.getTotal());
        assertEquals(50, node.getModified());
        assertEquals(100, node.getBase());

        node.remove(toRemove);
        assertEquals(100, node.getTotal());
        assertEquals(100, node.getBase());
        assertEquals(0, node.getModified());
    }


    @Test
    public void statNodeHasExpectedWhenSettingBaseManually() {
        StatNode node = new StatNode("attack", 100);
        assertEquals(100, node.getTotal());
        assertEquals(100, node.getBase());
        assertEquals(0, node.getModified());

        node.modify(null, StatNode.MULTIPLICATIVE, .5f);
        assertEquals(150, node.getTotal());
        assertEquals(50, node.getModified());
        assertEquals(100, node.getBase());

        node.setBase(50);
        assertEquals(100, node.getTotal());
        assertEquals(50, node.getBase());
        assertEquals(50, node.getModified());
    }

    @Test
    public void statNodeHasExpectedWhenSettingModifiedManually() {
        StatNode node = new StatNode("attack", 100);
        assertEquals(100, node.getTotal());
        assertEquals(100, node.getBase());
        assertEquals(0, node.getModified());

        node.modify(null, StatNode.MULTIPLICATIVE, .5f);
        assertEquals(150, node.getTotal());
        assertEquals(50, node.getModified());
        assertEquals(100, node.getBase());

        node.setModified(50);
        assertEquals(150, node.getTotal());
        assertEquals(100, node.getBase());
        assertEquals(50, node.getModified());
    }

    @Test
    public void statNodeCanAccommodateSetBaseAndModifiedValue1() {
        StatNode node = new StatNode("attack", 100);
        assertEquals(100, node.getTotal());
        assertEquals(100, node.getBase());
        assertEquals(0, node.getModified());

        node.modify(null, StatNode.MULTIPLICATIVE, .5f);
        assertEquals(150, node.getTotal());
        assertEquals(50, node.getModified());
        assertEquals(100, node.getBase());

        node.setBase(70);
        assertEquals(120, node.getTotal());
        assertEquals(70, node.getBase());
        assertEquals(50, node.getModified());

        node.setModified(10);
        assertEquals(80, node.getTotal());
        assertEquals(70, node.getBase());
        assertEquals(10, node.getModified());
    }

    @Test
    public void statNodeCanAccommodateSetBaseAndModifiedValue2() {
        StatNode node = new StatNode("attack", 100);
        assertEquals(100, node.getTotal());
        assertEquals(100, node.getBase());
        assertEquals(0, node.getModified());

        node.modify(null, StatNode.MULTIPLICATIVE, .5f);
        assertEquals(150, node.getTotal());
        assertEquals(50, node.getModified());
        assertEquals(100, node.getBase());

        node.setModified(50);
        assertEquals(150, node.getTotal());
        assertEquals(100, node.getBase());
        assertEquals(50, node.getModified());

        node.setBase(150);
        assertEquals(200, node.getTotal());
        assertEquals(150, node.getBase());
        assertEquals(50, node.getModified());
    }
}
