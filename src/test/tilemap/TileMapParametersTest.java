package test.tilemap;

import main.constants.Constants;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.map.base.TileMap;
import main.game.map.base.TileMapFactory;
import main.game.map.base.TileMapParameters;
import org.junit.Assert;
import org.junit.Test;

public class TileMapParametersTest {
    @Test
    public void correctCreatesTileMapParameters() {
        int rows = 10, columns = 10;
        String spriteMap = Constants.TILES_SPRITEMAP_FILEPATH;
        TileMapParameters tileMapParameters = TileMapParameters.getDefaultParameters(rows, columns, spriteMap);
        Assert.assertNotNull(tileMapParameters);
        Assert.assertNotNull(tileMapParameters.get(TileMapParameters.ROWS_KEY));
        Assert.assertNotNull(tileMapParameters.get(TileMapParameters.COLUMNS_KEY));
        Assert.assertNotNull(tileMapParameters.get(TileMapParameters.FLOOR_KEY));
        Assert.assertNotNull(tileMapParameters.get(TileMapParameters.WALL_KEY));
        Assert.assertNotNull(tileMapParameters.get(TileMapParameters.LIQUID_KEY));
        Assert.assertNotNull(tileMapParameters.get(TileMapParameters.SEED_KEY));
        Assert.assertNotNull(tileMapParameters.get(TileMapParameters.NOISE_ZOOM_KEY));
        Assert.assertNotNull(tileMapParameters.get(TileMapParameters.MIN_TERRAIN_HEIGHT_KEY));
        Assert.assertNotNull(tileMapParameters.get(TileMapParameters.MAX_TERRAIN_HEIGHT_KEY));
        Assert.assertNotNull(tileMapParameters.get(TileMapParameters.WATER_LEVEL_KEY));
    }
}
