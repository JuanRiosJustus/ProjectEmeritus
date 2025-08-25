package test;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import main.game.main.GameConfigs;
import main.game.main.GameController;
import main.game.stores.EntityStore;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameModelMovementTests extends GameTests {

    @Test
    public void movesUnitAroundFlatMap() {
        GameController game = createAndStartGameWithDefaults();
        game.disableAutoBehavior();

        JSONObject unit1 = game.createRandomForgettableUnit(false, 3, 2);

        simulateUserInactivity(game, 2000);

        String unitID = unit1.getString("unit_id");
        JSONObject response = game.useMove(unitID, 3, 7, true, true);

        JSONObject tileMetaData = game.getTile(3, 7);
        assertEquals(tileMetaData.get("tile_id"), response.get("tile_id"));

        simulateUserInactivity(game, 2000);
    }


    @Test
    public void movesUnitAroundHeightMap() {
        GameController game = createAndStartGameWithDefaults();
        game.disableAutoBehavior();

        // Setup platform 1 for unit
        game.raiseTile(2, 1, 1);
        game.raiseTile(2, 2, 1);
        game.raiseTile(2, 3, 1);

        game.raiseTile(3, 1, 1);
        game.raiseTile(3, 2, 1);
        game.raiseTile(3, 3, 1);

        game.raiseTile(4, 1, 1);
        game.raiseTile(4, 2, 1);
        game.raiseTile(4, 3, 1);

        // Create bridge
        game.raiseTile(3, 4, 2);
        game.raiseTile(3, 5, 3);

        // Setup platform 2 for unit
        game.raiseTile(2, 6, 4);
        game.raiseTile(2, 7, 4);
        game.raiseTile(2, 8, 4);

        game.raiseTile(3, 6, 4);
        game.raiseTile(3, 7, 4);
        game.raiseTile(3, 8, 4);

        game.raiseTile(4, 6, 4);
        game.raiseTile(4, 7, 4);
        game.raiseTile(4, 8, 4);

        JSONObject unit1 = game.createRandomForgettableUnit(false, 3, 2);

        simulateUserInactivity(game, 2000);

        String unitID = unit1.getString("unit_id");
        JSONObject response = game.useMove(unitID, 3, 7, true, true);

        JSONObject tileMetaData = game.getTile(3, 7);
        assertEquals(tileMetaData.get("tile_id"), response.get("tile_id"));

        simulateUserInactivity(game, 2000);
    }


    /**
     * Computes the total number of tiles within Manhattan distance d (inclusive).
     * @param maxLevel the maximum Manhattan depth level (must be >= 0)
     * @return total number of tiles from level 0 to maxLevel
     */
    public static int computeManhattanTotalCount(int maxLevel) {
        if (maxLevel < 0) throw new IllegalArgumentException("Level must be non-negative");

        int totalCount = 0;

        for (int level = 0; level <= maxLevel; level++) {
            int ringCount = (level == 0) ? 1 : 4 * level;
            totalCount += ringCount;
        }

        return totalCount;
    }

    @Test
    public void assertTileHeightsMapCreationIs1() {
        GameController gameController = GameController.create(
                GameConfigs.getDefaults()
                        .setMapGenerationRows(10)
                        .setMapGenerationColumns(10)
                        .setMapGenerationFoundationDepth(3)
                        .setMapGenerationTerrainStartingElevation(6)
                        .setMapGenerationTerrainEndingElevation(9)
                        .setMapGenerationTerrainHeightNoise(0f)
        );
        assertEquals(gameController.getRows(), 10);
        assertEquals(gameController.getColumns(), 10);
        int latestValue = -1;

        for (int row = 0; row < gameController.getRows(); row++) {
            for (int column = 0; column < gameController.getColumns(); column++) {
                JSONObject tile = gameController.getTile(toJSON("row", row, "column", column));
                assertNotNull(tile);

                // Assert map is flat by default
                int elevation = tile.getIntValue("base_elevation") + tile.getIntValue("modified_elevation");
                String structureID = tile.getString("structure_id");
                String unitID = tile.getString("unit_id");

                if (latestValue == -1) { latestValue = elevation; }
                assertEquals(latestValue, elevation);
                assertNull(structureID);
                assertNull(unitID);

            }
        }
        JSONObject tile = gameController.getTile(toJSON("row", -1, "column", 0));
        assertNull(tile);

        tile = gameController.getTile(toJSON("row", -1, "column", -1));
        assertNull(tile);
    }

    @Test
    public void gameModel_pathfindingReturnsCorrectPath() {
        GameController gameController = GameController.create(10, 10, 400, 400);

        // Place a unit on tile (3, 3)
        String unitID = EntityStore.getInstance().createUnit(false);
        JSONObject startTileJSON = toJSON("row", 3, "column", 3);
        JSONObject startTile = gameController.getTile(startTileJSON);
        String startTileID = startTile.getString("tile_id");
        gameController.setUnit(toJSON("unit_id", unitID, "tile_id", startTileID));

        // Choose an end tile within range (e.g., 2 tiles away)
        JSONObject endTileJSON = toJSON("row", 3, "column", 5);
        JSONObject endTile = gameController.getTile(endTileJSON);
        String endTileID = endTile.getString("tile_id");

        // Construct request
        JSONObject request = new JSONObject();
        request.put("start_tile_id", startTileID);
        request.put("end_tile_id", endTileID);
        request.put("range", 3);
        request.put("respectfully", true);

        JSONArray path = gameController.getTilesInMovementPath(request);

        // Ensure path is non-empty and ends on the target tile
        assertFalse(path.isEmpty(), "Path should not be empty");
        assertEquals(endTileID, path.getString(path.size() - 1), "Last tile in path should be the destination");

        // Ensure first tile in path is the origin
        assertEquals(startTileID, path.getString(0), "First tile in path should be the origin");

        // Optional: Check Manhattan distance does not exceed range
        assertTrue(path.size() - 1 <= 3, "Path should not exceed max range");
    }

    @Test
    public void gameModel_pathfindingRespectsUnitObstructions() {
        GameController gameController = GameController.create(10, 10, 400, 400);

        // Place unit at (5, 5) as the starting point
        String startUnitID = EntityStore.getInstance().createUnit(false);
        JSONObject startTile = toJSON("row", 5, "column", 5);
        JSONObject startTileInfo = gameController.getTile(startTile);
        String startTileID = startTileInfo.getString("tile_id");
        gameController.setUnit(toJSON("unit_id", startUnitID, "tile_id", startTileID));

        // Target tile is two tiles to the right (5, 7)
        JSONObject endTile = toJSON("row", 5, "column", 7);
        JSONObject endTileInfo = gameController.getTile(endTile);
        String endTileID = endTileInfo.getString("tile_id");

        // Place blocking units at (5,6), directly between start and end
        String blockingUnitID = EntityStore.getInstance().createUnit(false);
        gameController.setUnit(toJSON("unit_id", blockingUnitID, "row", 5, "column", 6));
        String blockingTileID = gameController.getTile(toJSON("row", 5, "column", 6)).getString("tile_id");

        // Prepare movement path request
        JSONObject request = new JSONObject();
        request.put("start_tile_id", startTileID);
        request.put("end_tile_id", endTileID);
        request.put("range", 3);
        request.put("respectfully", true);

        JSONArray path = gameController.getTilesInMovementPath(request);

        // Path should be empty because the direct path is blocked and there’s no alternate path in range
        assertTrue(path.isEmpty(), "Path should be empty due to obstruction");

        // Check if path is available when we expand our range
        // Prepare movement path request
        request = new JSONObject();
        request.put("start_tile_id", startTileID);
        request.put("end_tile_id", endTileID);
        request.put("range", 4);
        request.put("respectfully", true);

        path = gameController.getTilesInMovementPath(request);
        assertFalse(path.isEmpty());
        assertEquals(path.getString(0), startTileID);
        assertEquals(path.getString(path.size() - 1), endTileID);
        assertFalse(path.contains(blockingTileID));
    }
}
