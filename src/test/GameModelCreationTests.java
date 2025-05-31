package test;

import com.alibaba.fastjson2.JSONObject;
import main.game.main.GameConfigs;
import main.game.main.GameController;
import main.graphics.AssetPool;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GameModelCreationTests extends GameTests {

    @Test
    public void assertEmptyGameCreated() {
        GameController gameController = GameController.create();
        assertEquals(gameController.getRows(), 0);
        assertEquals(gameController.getColumns(), 0);

        JSONObject tile = gameController.getTile(toJSON("row", 0, "column", 0));
        assertNull(tile);

        tile = gameController.getTile(toJSON("row", -1, "column", -1));
        assertNull(tile);
    }

    @Test
    public void assertRowsAndTilesCreated() {
        GameController gameController = GameController.create();
        gameController.generateTileMap(toJSON("rows", 10, "columns", 10));
        assertEquals(gameController.getRows(), 10);
        assertEquals(gameController.getColumns(), 10);

        JSONObject tile = gameController.getTile(toJSON("row", 0, "column", 0));
        assertNotNull(tile);

        tile = gameController.getTile(toJSON("row", -1, "column", -1));
        assertNull(tile);
    }

    @Test
    public void assertFlatTileMapCreation() {
        GameController gameController = GameController.createFlatTestMap(11, 22, 1, 1);
        assertEquals(gameController.getRows(), 11);
        assertEquals(gameController.getColumns(), 22);
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

        tile = gameController.getTile(toJSON("row", 0, "column", 0));
        assertNotNull(tile);

        tile = gameController.getTile(toJSON("row", -1, "column", -1));
        assertNull(tile);
    }


    @Test
    public void assertTileHeightsMapCreationIsVarious() {
        GameController gameController = GameController.createVariousHeightTestMap(11, 33, 1, 1);
        assertEquals(gameController.getRows(), 11);
        assertEquals(gameController.getColumns(), 33);

        for (int row = 0; row < gameController.getRows(); row++) {
            for (int column = 0; column < gameController.getColumns(); column++) {
                JSONObject tile = gameController.getTile(toJSON("row", row, "column", column));
                assertNotNull(tile);

                // Assert map is flat by default
                int elevation = tile.getIntValue("base_elevation") + tile.getIntValue("modified_elevation");
                String structureID = tile.getString("structure_id");
                String unitID = tile.getString("unit_id");

                assertTrue(elevation >= 2);
                assertNull(structureID);
                assertNull(unitID);

            }
        }
        JSONObject tile = gameController.getTile(toJSON("row", -1, "column", 0));
        assertNull(tile);

        tile = gameController.getTile(toJSON("row", 0, "column", 0));
        assertNotNull(tile);

        tile = gameController.getTile(toJSON("row", -1, "column", -1));
        assertNull(tile);
    }

    @Test
    public void assertTileHeightsMapCreationIsVariousWithLiquid() {
        int liquidLevel = 3;
        GameController gameController = GameController.createVariousHeightTestMapWithLiquid(18, 12, 1500, 950);
        assertEquals(gameController.getRows(), 18);
        assertEquals(gameController.getColumns(), 12);
        Set<Integer> heights = new HashSet<>();
        boolean hasLiquid = false;

        for (int row = 0; row < gameController.getRows(); row++) {
            for (int column = 0; column < gameController.getColumns(); column++) {
                JSONObject tile = gameController.getTile(toJSON("row", row, "column", column));
                assertNotNull(tile);

                // Assert map is flat by default
                int elevation = tile.getIntValue("base_elevation") + tile.getIntValue("modified_elevation");
                String structureID = tile.getString("structure_id");
                String unitID = tile.getString("unit_id");
                boolean isLiquid = tile.getBoolean("is_liquid");
                heights.add(elevation);

                if (!hasLiquid) { hasLiquid = isLiquid; }
                assertNull(structureID);
                assertNull(unitID);

            }
        }
        assertFalse(heights.isEmpty());
        assertTrue(heights.size() > 1);
//        assertTrue(hasLiquid);



        JSONObject tile = gameController.getTile(toJSON("row", -1, "column", 0));
        assertNull(tile);

        tile = gameController.getTile(toJSON("row", 0, "column", 0));
        assertNotNull(tile);

        tile = gameController.getTile(toJSON("row", -1, "column", -1));
        assertNull(tile);
    }

    @Test
    public void assertTileHeightsMapCreationIsFlatWithLiquid() {
        GameController gameController = GameController.createFlatTestMapWithLiquid(22, 33, 9, 9);
        assertEquals(gameController.getRows(), 22);
        assertEquals(gameController.getColumns(), 33);

        for (int row = 0; row < gameController.getRows(); row++) {
            for (int column = 0; column < gameController.getColumns(); column++) {
                JSONObject tile = gameController.getTile(toJSON("row", row, "column", column));
                assertNotNull(tile);

                // Assert map is flat by default
                int elevation = tile.getIntValue("base_elevation") + tile.getIntValue("modified_elevation");
                String structureID = tile.getString("structure_id");
                String unitID = tile.getString("unit_id");
                boolean isLiquid = tile.getBoolean("is_liquid");

                assertTrue(elevation > 0);
                assertTrue(isLiquid);
                assertNull(structureID);
                assertNull(unitID);

            }
        }
        JSONObject tile = gameController.getTile(toJSON("row", -1, "column", 0));
        assertNull(tile);

        tile = gameController.getTile(toJSON("row", 0, "column", 0));
        assertNotNull(tile);

        tile = gameController.getTile(toJSON("row", -1, "column", -1));
        assertNull(tile);
    }


    @Test
    public void assertTileHeightsMapCreationIsFlatWithStructures() {
        int liquidLevel = 10;
        GameController gameController = GameController.create(
                GameConfigs.getDefaults()
                        .setMapGenerationRows(10)
                        .setMapGenerationColumns(10)
                        .setMapGenerationTerrainHeightNoise(.75f)
                        .setMapGenerationTerrainStartingElevation(2)
                        .setMapGenerationTerrainEndingElevation(3)
        );
        assertEquals(gameController.getRows(), 10);
        assertEquals(gameController.getColumns(), 10);


        List<String> structures = AssetPool.getInstance().getStructureTileSets();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int row = random.nextInt(gameController.getRows());
            int column = random.nextInt(gameController.getColumns());
            String structure = structures.get(random.nextInt(structures.size()));

            JSONObject request = new JSONObject();
            request.put("row", row);
            request.put("column", column);
            JSONObject response = gameController.getTile(request);

            request.put("tile_id", response.get("id"));
            request.put("structure", structure);
            gameController.setStructure(request);
        }

        boolean atLeastOneStructure = false;

        for (int row = 0; row < gameController.getRows(); row++) {
            for (int column = 0; column < gameController.getColumns(); column++) {
                JSONObject tile = gameController.getTile(toJSON("row", row, "column", column));
                assertNotNull(tile);

                // Assert map is flat by default
                int elevation = tile.getIntValue("base_elevation") + tile.getIntValue("modified_elevation");
                String structureID = tile.getString("structure_id");
                String unitID = tile.getString("unit_id");
                boolean isLiquid = tile.getBoolean("is_liquid");

                assertTrue(elevation >= 2);
                assertTrue(elevation <= liquidLevel);
                if (structureID != null) { atLeastOneStructure = true; }
                assertNull(unitID);

            }
        }

        assertTrue(atLeastOneStructure);

        JSONObject tile = gameController.getTile(toJSON("row", -1, "column", 0));
        assertNull(tile);

        tile = gameController.getTile(toJSON("row", 0, "column", 0));
        assertNotNull(tile);

        tile = gameController.getTile(toJSON("row", -1, "column", -1));
        assertNull(tile);
    }
}
