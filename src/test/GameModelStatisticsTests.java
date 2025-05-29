package test;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import main.game.main.GameConfigs;
import main.game.main.GameController;
import main.game.stores.EntityStore;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameModelStatisticsTests extends GameTests {

    @Test
    public void ensureNewUnitsAreCreated() {
        GameController gameController = GameController.create(new GameConfigs());
        String npc1 = gameController.createUnit().getString("id");
        String npc2 = gameController.createUnit().getString("id");
        assertNotNull(npc1);
        assertNotNull(npc2);
    }

    @Test
    public void lineOfSightObstructedOrNotObstructedByUnit() {
        GameController gameController = GameController.create(10, 10, 0, 0);
        String npc1 = gameController.createUnit().getString("id");
        String npc2 = gameController.createUnit().getString("id");
        assertNotNull(npc1);
        assertNotNull(npc2);

        String tile1 = gameController.getTile(toJSON("row", 3, "column", 3)).getString("id");
        gameController.setUnit(toJSON("tile_id", tile1, "unit_id", npc1));

        String tile2 = gameController.getTile(toJSON("row", 3, "column", 4)).getString("id");
        gameController.setUnit(toJSON("tile_id", tile2, "unit_id", npc2));

        String tile3 = gameController.getTile(toJSON("row", 3, "column", 5)).getString("id");

        JSONArray tilesInLineOfSight = gameController.getTilesInLineOfSight(toJSON(
                "start_tile_id", tile1, "end_tile_id", tile3, "respectfully", true
        ));

        assertEquals(2, tilesInLineOfSight.size());
        assertFalse(tilesInLineOfSight.contains(tile3));

        String tile4 = gameController.getTile(toJSON("row", 3, "column", 7)).getString("id");
        JSONArray tilesInLineOfSight2 = gameController.getTilesInLineOfSight(toJSON(
                "start_tile_id", tile2, "end_tile_id", tile4, "respectfully", true
        ));

        assertEquals(4, tilesInLineOfSight2.size());
        assertTrue(tilesInLineOfSight2.contains(tile2));
        assertTrue(tilesInLineOfSight2.contains(tile4));
    }

    @Test
    public void areaOfSightObstructedOrNotObstructedByUnit() {
        GameController gameController = GameController.create(10, 10, 0, 0);
        String npc1 = gameController.createUnit().getString("id");
        String npc2 = gameController.createUnit().getString("id");
        assertNotNull(npc1);
        assertNotNull(npc2);

        // Assert the map is flat
        for (int row = 0; row < gameController.getRows(); row++) {
            for (int column = 0; column < gameController.getColumns(); column++) {
                JSONObject tile = gameController.getTile(toJSON("row", row, "column", column));
                assertNotNull(tile);

                // Assert map is flat by default
                int elevation = tile.getIntValue("base_elevation") + tile.getIntValue("modified_elevation");
                String structureID = tile.getString("structure_id");
                String unitID = tile.getString("unit_id");

                assertEquals(1, elevation);
                assertNull(structureID);
                assertNull(unitID);

            }
        }

        String tile1 = gameController.getTile(toJSON("row", 3, "column", 3)).getString("id");
        gameController.setUnit(toJSON("tile_id", tile1, "unit_id", npc1));

        String tile2 = gameController.getTile(toJSON("row", 3, "column", 4)).getString("id");
        gameController.setUnit(toJSON("tile_id", tile2, "unit_id", npc2));

        String tile3 = gameController.getTile(toJSON("row", 3, "column", 5)).getString("id");

        JSONArray tilesInAreaOfSight = gameController.getTileInAreaOfSight(toJSON(
                "start_tile_id", tile1, "range", 2, "respectfully", true
        ));


        //[ ][ ][0][ ][ ]
        //[ ][o][o][0][ ]
        //[o][o][1][2][ ]
        //[ ][o][o][0][ ]
        //[ ][ ][0][ ][ ]

        assertEquals(12, tilesInAreaOfSight.size());
        assertTrue(tilesInAreaOfSight.contains(tile2));
        assertTrue(tilesInAreaOfSight.contains(tile1));
        assertFalse(tilesInAreaOfSight.contains(tile3));
    }


}
