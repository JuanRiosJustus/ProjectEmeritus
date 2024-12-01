package test;

import main.game.stats.StatNode;
import org.junit.Test;

import static org.junit.Assert.*;

public class StatNodeTest {
    private static final String TEST = "TEST";
    @Test
    public void correctly_adds_multiplicative_buffs() {
        StatNode node = new StatNode("Attack", 10);
        assertEquals(10, node.getBase());
        assertEquals(0, node.getModified());
        assertEquals(10, node.getTotal());

        node.putMultiplicativeModification(TEST, "buff1", 0.2f, -1);

        assertEquals(10, node.getBase());
        assertEquals(2, node.getModified());
        assertEquals(12, node.getTotal());

        node.putMultiplicativeModification(TEST, "buff2", 0.3f, -1);

        assertEquals(10, node.getBase());
        assertEquals(5, node.getModified());
        assertEquals(15, node.getTotal());

        node.putMultiplicativeModification(TEST, "debuff3", -0.4f, -1);

        assertEquals(10, node.getBase());
        assertEquals(1, node.getModified());
        assertEquals(11, node.getTotal());
    }

    @Test
    public void correctly_adds_additive_buffs() {
        StatNode node = new StatNode("Attack", 10);
        assertEquals(10, node.getBase());
        assertEquals(0, node.getModified());
        assertEquals(10, node.getTotal());

        node.putAdditiveModification(TEST, "buff1", 15, -1);

        assertEquals(10, node.getBase());
        assertEquals(15, node.getModified());
        assertEquals(25, node.getTotal());

        node.putAdditiveModification(TEST, "buff2", 25, -1);

        assertEquals(10, node.getBase());
        assertEquals(40, node.getModified());
        assertEquals(50, node.getTotal());

        node.putAdditiveModification(TEST, "debuff3", -45, -1);

        assertEquals(10, node.getBase());
        assertEquals(-5, node.getModified());
        assertEquals(5, node.getTotal());
    }

    @Test
    public void correctly_adds_additive_and_multiplicative_buffs() {
        StatNode node = new StatNode("Attack", 10);
        assertEquals(10, node.getBase());
        assertEquals(0, node.getModified());
        assertEquals(10, node.getTotal());

        node.putAdditiveModification(TEST, "buff1", 20, -1);

        assertEquals(10, node.getBase());
        assertEquals(20, node.getModified());
        assertEquals(30, node.getTotal());

        node.putMultiplicativeModification(TEST, "buff2", .5f, -1);

        assertEquals(10, node.getBase());
        assertEquals(35, node.getModified());
        assertEquals(45, node.getTotal());

        node.putMultiplicativeModification(TEST, "debuff3", -1f, -1);

        assertEquals(10, node.getBase());
        assertEquals(5, node.getModified());
        assertEquals(15, node.getTotal());
    }

    @Test
    public void correctly_adds_removesBuffOrDebuffs_after_removing_buffs() {
        StatNode node = new StatNode("Attack", 10);
        assertEquals(10, node.getBase());
        assertEquals(0, node.getModified());
        assertEquals(10, node.getTotal());

        node.putAdditiveModification(TEST, "buff1", 20, 2);

        assertEquals(10, node.getBase());
        assertEquals(20, node.getModified());
        assertEquals(30, node.getTotal());

        node.putMultiplicativeModification(TEST, "buff2", .5f, 2);

        assertEquals(10, node.getBase());
        assertEquals(35, node.getModified());
        assertEquals(45, node.getTotal());

        node.removeBuffOrDebuffByName("buff1");

        assertEquals(10, node.getBase());
        assertEquals(5, node.getModified());
        assertEquals(15, node.getTotal());
    }

    @Test
    public void correctly_adds_removesBuffOrDebuffs_after_expiring() {
        StatNode node = new StatNode("Attack", 10);
        assertEquals(10, node.getBase());
        assertEquals(0, node.getModified());
        assertEquals(10, node.getTotal());

        node.putAdditiveModification(TEST, "buff1", 20, 2);

        assertEquals(2, node.getDuration("buff1"));
        assertEquals(10, node.getBase());
        assertEquals(20, node.getModified());
        assertEquals(30, node.getTotal());

        node.putMultiplicativeModification(TEST, "buff2", .5f, 3);

        assertEquals(3, node.getDuration("buff2"));
        assertEquals(10, node.getBase());
        assertEquals(35, node.getModified());
        assertEquals(45, node.getTotal());

        node.updateDurations();

        assertEquals(1, node.getDuration("buff1"));
        assertEquals(2, node.getDuration("buff2"));
        assertEquals(10, node.getBase());
        assertEquals(35, node.getModified());
        assertEquals(45, node.getTotal());

        node.updateDurations();

        assertEquals(0, node.getDuration("buff1"));
        assertEquals(1, node.getDuration("buff2"));
        assertEquals(10, node.getBase());
        assertEquals(35, node.getModified());
        assertEquals(45, node.getTotal());

        node.updateDurations();

        assertEquals(-1, node.getDuration("buff1"));
        assertEquals(0, node.getDuration("buff2"));
        assertEquals(10, node.getBase());
        assertEquals(5, node.getModified());
        assertEquals(15, node.getTotal());

        node.updateDurations();

        assertEquals(-1, node.getDuration("buff1"));
        assertEquals(-1, node.getDuration("buff2"));
        assertEquals(10, node.getBase());
        assertEquals(0, node.getModified());
        assertEquals(10, node.getTotal());
    }

    @Test
    public void correctly_removes_all_buffs_from_source() {
        StatNode node = new StatNode("Health",50);
        assertEquals(50, node.getBase());
        assertEquals(0, node.getModified());
        assertEquals(50, node.getTotal());

        node.putAdditiveModification(TEST, "buff1", 50, 2);

        assertEquals(50, node.getBase());
        assertEquals(50, node.getModified());
        assertEquals(100, node.getTotal());

        node.putAdditiveModification(TEST, "buff2", 25, 2);

        assertEquals(50, node.getBase());
        assertEquals(75, node.getModified());
        assertEquals(125, node.getTotal());

        node.putAdditiveModification(TEST + "1", "buff3", 10, 2);

        assertEquals(50, node.getBase());
        assertEquals(85, node.getModified());
        assertEquals(135, node.getTotal());

        node.removeBuffOrDebuffBySource(TEST);

        assertEquals(50, node.getBase());
        assertEquals(10, node.getModified());
        assertEquals(60, node.getTotal());
    }

    @Test
    public void correctly_uses_current_updates() {
        StatNode node = new StatNode("Health",50);
        assertEquals(50, node.getBase());
        assertEquals(0, node.getModified());
        assertEquals(50, node.getTotal());

        node.putAdditiveModification(TEST, "buff1", 50, 2);

        assertEquals(50, node.getBase());
        assertEquals(50, node.getModified());
        assertEquals(100, node.getTotal());

        node.setCurrent(10);

        assertEquals(50, node.getBase());
        assertEquals(50, node.getModified());
        assertEquals(100, node.getTotal());
        assertEquals(10, node.getCurrent());
        assertEquals(0.9, node.getMissingPercent(), 0.01);
        assertEquals(0.1, node.getCurrentPercent(), 0.01);

        node.putAdditiveModification(TEST, "buff2", -90, 2);
        node.setCurrent(8);

        assertEquals(50, node.getBase());
        assertEquals(-40, node.getModified());
        assertEquals(10, node.getTotal());
        assertEquals(8, node.getCurrent());
        assertEquals(0.2, node.getMissingPercent(), 0.01);
        assertEquals(0.8, node.getCurrentPercent(), 0.01);
    }
}
