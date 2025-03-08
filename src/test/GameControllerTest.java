package test;

public class GameControllerTest {

//    @Test
//    public void testCreateNewGameController() {
//        int rows = 10;
//        int columns = 10;
//        TileMapParameters parameters = TileMapParameters.getDefaultParameters(rows, columns);
//        parameters.put(TileMapParameters.MAX_TERRAIN_HEIGHT_KEY, 3);
//        parameters.put(TileMapParameters.MIN_TERRAIN_HEIGHT_KEY, 3);
//        TileMap tileMap = TileMapFactory.create(parameters);
//
//        GameController gameController = GameController.getInstance().create(
//                100, 100,
//                rows, columns,
//                100 / rows, 100 / columns
//        );
//
//        gameController.setMap(tileMap.toJsonObject(), null);
//
//        for (int row = 0; row < tileMap.getRows(); row++) {
//            for (int column = 0; column < tileMap.getColumns(); column++) {
//                Entity ogTileEntity = tileMap.tryFetchingTileAt(row, column);
//                Tile ogTile = ogTileEntity.get(Tile.class);
//                Entity newTileEntity = tileMap.tryFetchingTileAt(row, column);
//                Tile newTile = newTileEntity.get(Tile.class);
//                Assertions.assertNotNull(ogTile);
//                Assertions.assertNotNull(newTile);
//                Assertions.assertFalse(ogTile.toJsonObject().isEmpty());
//                Assertions.assertFalse(newTile.toJsonObject().isEmpty());
//                Assertions.assertEquals(ogTile.toJsonObject(), newTile.toJsonObject());
//                Assertions.assertEquals(ogTile.getHeight(), newTile.getHeight());
//            }
//        }
//    }
}
