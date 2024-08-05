package test;

import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.map.base.TileMap;
import main.game.map.base.TileMapFactory;
import main.game.map.base.TileMapParameters;
import org.junit.Assert;
import org.junit.Test;

public class GameControllerTest {

    @Test
    public void testCreateNewGameController() {
        int rows = 10;
        int columns = 10;
        TileMapParameters parameters = TileMapParameters.getDefaultParameters(rows, columns);
        parameters.put(TileMapParameters.MAX_TERRAIN_HEIGHT_KEY, 3);
        parameters.put(TileMapParameters.MIN_TERRAIN_HEIGHT_KEY, 3);
        TileMap tileMap = TileMapFactory.create(parameters);

        GameController gameController = GameController.getInstance().create();
        gameController.setMap(tileMap.toJsonObject(), null);

        for (int row = 0; row < tileMap.getRows(); row++) {
            for (int column = 0; column < tileMap.getColumns(); column++) {
                Entity ogTileEntity = tileMap.tryFetchingTileAt(row, column);
                Tile ogTile = ogTileEntity.get(Tile.class);
                Entity newTileEntity = tileMap.tryFetchingTileAt(row, column);
                Tile newTile = newTileEntity.get(Tile.class);
                Assert.assertNotNull(ogTile);
                Assert.assertNotNull(newTile);
                Assert.assertFalse(ogTile.toJsonObject().isEmpty());
                Assert.assertFalse(newTile.toJsonObject().isEmpty());
                Assert.assertEquals(ogTile.toJsonObject(), newTile.toJsonObject());
                Assert.assertEquals(ogTile.getHeight(), newTile.getHeight());
            }
        }
    }
}
