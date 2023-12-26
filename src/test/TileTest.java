package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import main.game.components.tile.Tile;

public class TileTest {

    @Test
    public void tileIsCorrectlyEncoded() {
        Tile t = new Tile(4,4);
        t.encode(1, 2, 3, 4);
        assertEquals(t.hasCollider(), 1);
        assertEquals(t.getHeight(), 2);
        assertEquals(t.getTerrain(), 3);
        assertEquals(t.getLiquid(), 4);
    }
}
