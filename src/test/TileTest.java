package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import main.game.entity.Entity;
import main.game.stores.factories.EntityFactory;
import main.game.stores.factories.TileFactory;
import org.junit.Assert;
import org.junit.Test;

import main.game.components.tile.Tile;

public class TileTest {

    @Test
    public void tileIsCorrectlyEncoded() {
        Entity entity = TileFactory.create(2, 5);
        Tile tile = entity.get(Tile.class);
        Assert.assertNotNull(tile);
        Assert.assertEquals(2, tile.row);
        Assert.assertEquals(5, tile.column);
        Assert.assertNull(tile.getUnit());
    }
}
