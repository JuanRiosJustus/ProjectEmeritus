package test;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import javafx.embed.swing.JFXPanel;
import main.game.main.GameController;
import main.game.stores.EntityStore;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameModelTests {

//    @Before
//    public void initJavaFX() throws Exception {
//        // This initializes the JavaFX runtime
//        new JFXPanel(); // implicitly calls Platform.startup(...) if not already done
//    }
//
//    private static JSONObject toJSON(Object... values) {
//        JSONObject object = new JSONObject();
//        if (values.length % 2 != 0) { return object; }
//        for (int i = 0; i < values.length; i = i + 2) {
//            Object key = values[i];
//            Object value = values[i + 1];
//            object.put(String.valueOf(key), value);
//        }
//        return object;
//    }
//    @Test
//    public void initGameModelIsRunning() {
//        GameController gameController = GameController.create(10, 10, 400, 400);
//        gameController.run();
//        assertTrue(gameController.isRunning());
//    }
//
//    @Test
//    public void gameModelAssertTileMapExistence() {
//        GameController gameController = GameController.create(10, 10, 400, 400);
//        assertEquals(gameController.getRows(), 10);
//        assertEquals(gameController.getColumns(), 10);
//
//        for (int row = 0; row < gameController.getRows(); row++) {
//            for (int column = 0; column < gameController.getColumns(); column++) {
//                String tile = gameController.getTile(new JSONObject().fluentPut("row", row).fluentPut("column", column));
//                assertNotNull(tile);
//            }
//        }
//        String tile = gameController.getTile(new JSONObject().fluentPut("row", -1).fluentPut("column", 0));
//        assertNull(tile);
//
//        tile = gameController.getTile(new JSONObject().fluentPut("row", -1).fluentPut("column", -1));
//        assertNull(tile);
//    }
//
//
//    @Test
//    public void gameModel_assertUnitSuccessfullyPlaced() {
//        GameController gameController = GameController.create(10, 10, 400, 400);
//        assertEquals(gameController.getRows(), 10);
//        assertEquals(gameController.getColumns(), 10);
//
//        for (int row = 0; row < gameController.getRows(); row++) {
//            for (int column = 0; column < gameController.getColumns(); column++) {
//                String tile = gameController.getTile(new JSONObject().fluentPut("row", row).fluentPut("column", column));
//                assertNotNull(tile);
//            }
//        }
//
//        String unitID = EntityStore.getInstance().getOrCreateUnit(false);
//        String tileID = gameController.getTile(toJSON("row", 3, "column", 6));
//        String setTile = gameController.setUnit(toJSON("unit_id", unitID, "tile_id", tileID));
//        assertEquals(tileID, setTile);
//
//        String unitOnTile = gameController.getUnitOfTile(new JSONObject(Map.of("row", 3, "column", 6)));
//        assertEquals(unitOnTile, unitID);
//    }
//
//    @Test
//    public void gameModel_successfullyTraversed() {
//        GameController gameController = GameController.create(10, 10, 400, 400);
//        assertEquals(gameController.getRows(), 10);
//        assertEquals(gameController.getColumns(), 10);
//
//        for (int row = 0; row < gameController.getRows(); row++) {
//            for (int column = 0; column < gameController.getColumns(); column++) {
//                String tile = gameController.getTile(new JSONObject().fluentPut("row", row).fluentPut("column", column));
//                assertNotNull(tile);
//            }
//        }
//
//        String tileID = gameController.getTile(toJSON("row", 5, "column", 5));
//        JSONArray traversableTiles = gameController.getTilesInMovementRange(toJSON(
//                "tile_id", tileID, "range", 2, "respectfully", false
//        ));
//        assertTrue(traversableTiles.contains(gameController.getTile(toJSON("row", 5, "column", 5))));
//        assertTrue(traversableTiles.contains(gameController.getTile(toJSON("row", 5, "column", 6))));
//        assertTrue(traversableTiles.contains(gameController.getTile(toJSON("row", 5, "column", 4))));
//        assertTrue(traversableTiles.contains(gameController.getTile(toJSON("row", 6, "column", 5))));
//        assertTrue(traversableTiles.contains(gameController.getTile(toJSON("row", 4, "column", 5))));
//
//        tileID = gameController.getTile(toJSON("row", 5, "column", 5));
//        traversableTiles = gameController.getTilesInMovementRange(toJSON(
//                "tile_id", tileID, "range", 2, "respectfully", true
//        ));
//        assertTrue(traversableTiles.contains(gameController.getTile(toJSON("row", 5, "column", 5))));
//        assertTrue(traversableTiles.contains(gameController.getTile(toJSON("row", 5, "column", 6))));
//        assertTrue(traversableTiles.contains(gameController.getTile(toJSON("row", 5, "column", 4))));
//        assertTrue(traversableTiles.contains(gameController.getTile(toJSON("row", 6, "column", 5))));
//        assertTrue(traversableTiles.contains(gameController.getTile(toJSON("row", 4, "column", 5))));
//    }
//
//    @Test
//    public void gameModel_successfullyTraversedWithObstacle() {
//        GameController gameController = GameController.create(10, 10, 400, 400);
//        assertEquals(gameController.getRows(), 10);
//        assertEquals(gameController.getColumns(), 10);
//
//        for (int row = 0; row < gameController.getRows(); row++) {
//            for (int column = 0; column < gameController.getColumns(); column++) {
//                String tile = gameController.getTile(new JSONObject().fluentPut("row", row).fluentPut("column", column));
//                assertNotNull(tile);
//            }
//        }
//
//        String centralTileID = gameController.getTile(toJSON("row", 5, "column", 5));
//        JSONArray traversableTiles = gameController.getTilesInMovementRange(toJSON(
//                "tile_id", centralTileID, "range", 2, "respectfully", true
//        ));
//        assertEquals(traversableTiles.size(), 13);
//        assertTrue(traversableTiles.contains(gameController.getTile(toJSON("row", 5, "column", 5))));
//        assertTrue(traversableTiles.contains(gameController.getTile(toJSON("row", 5, "column", 6))));
//        assertTrue(traversableTiles.contains(gameController.getTile(toJSON("row", 5, "column", 4))));
//        assertTrue(traversableTiles.contains(gameController.getTile(toJSON("row", 6, "column", 5))));
//        assertTrue(traversableTiles.contains(gameController.getTile(toJSON("row", 4, "column", 5))));
//
//        String unitID = EntityStore.getInstance().getOrCreateUnit(false);
//        gameController.setUnit(toJSON("unit_id", unitID, "row", 4, "column", 5));
//
//        traversableTiles = gameController.getTilesInMovementRange(toJSON(
//                "tile_id", centralTileID, "range", 2, "respectfully", true
//        ));
//        assertEquals(traversableTiles.size(), 12);
//        assertTrue(traversableTiles.contains(gameController.getTile(toJSON("row", 5, "column", 5))));
//        assertTrue(traversableTiles.contains(gameController.getTile(toJSON("row", 5, "column", 6))));
//        assertTrue(traversableTiles.contains(gameController.getTile(toJSON("row", 5, "column", 4))));
//        assertTrue(traversableTiles.contains(gameController.getTile(toJSON("row", 6, "column", 5))));
//    }

}
