package test;

import main.game.main.GameController;
import main.game.map.base.TileMap;
import main.game.map.base.TileMapFactory;
import main.game.map.base.TileMapParameters;
import main.game.pathfinding.PathBuilder;
import org.junit.Assert;
import org.junit.Test;

public class PathBuilderTest {

    @Test
    public void correctlyGetsAllAvailableTiles() {
        int rows = 10;
        int columns = 10;
        TileMapParameters parameters = TileMapParameters.getDefaultParameters(rows, columns);
        parameters.put(TileMapParameters.MAX_TERRAIN_HEIGHT_KEY, 3);
        parameters.put(TileMapParameters.MIN_TERRAIN_HEIGHT_KEY, 3);
        TileMap tileMap = TileMapFactory.create(parameters);

        GameController gameController = GameController.getInstance().create();
        gameController.setMap(tileMap.toJsonObject(), null);

        var r = PathBuilder.newBuilder().getMovementRange(
                gameController.getModel(),
                gameController.getModel().tryFetchingTileAt(5, 5),
                1,
                1
        );
        Assert.assertEquals(r.size(), 4);
    }
}
