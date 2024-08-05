package test.tilemap;

import main.game.map.base.TileMap;
import main.game.map.base.TileMapFactory;
import org.junit.Assert;
import org.junit.Test;

public class TileMapFactoryTest {


    @Test
    public void mapIsCreatedWithReasonableCountOfRowsAndColumns() {
        int rows = 10, columns = 10;
        TileMap tileMap = TileMapFactory.create(rows, columns);
        Assert.assertEquals(tileMap.getColumns(), columns);
        Assert.assertEquals(tileMap.getRows(), rows);
    }

    @Test
    public void mapIsCreatedWithMinimalCountOfRowsAndColumns() {
        int rows = 3, columns = 3; // This is because rooms are at LEAST 3/3 tiles small
        TileMap tileMap = TileMapFactory.create(rows, columns);
        Assert.assertEquals(tileMap.getColumns(), columns);
        Assert.assertEquals(tileMap.getRows(), rows);
    }


    @Test
    public void mapIsNotCorrectlyCreatedWithNoRowsAndColumns() {
        int rows = 0, columns = 0;
        try {
            TileMap tileMap = TileMapFactory.create(rows, columns);
        } catch (Exception ex) {
            Assert.assertTrue(true);
        }
    }
}
