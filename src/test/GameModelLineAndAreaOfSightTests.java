package test;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import main.game.main.GameController;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameModelLineAndAreaOfSightTests extends GameTests {

    @Test
    public void lineOfSightObstructedOrNotObstructedByUnit() {
        GameController gameController = GameController.createFlatTestMap(10, 10, 0, 0);
        String npc1 = gameController.createCpuUnit().getString("id");
        String npc2 = gameController.createCpuUnit().getString("id");
        assertNotNull(npc1);
        assertNotNull(npc2);

        JSONObject request = new JSONObject().fluentPut("row", 3).fluentPut("column", 3);
        String tile1 = gameController.getTile(request).getString("id");
        request = new JSONObject().fluentPut("tile_id", tile1).fluentPut("unit_id", npc1);
        gameController.setUnit(request);

        request = new JSONObject().fluentPut("row", 3).fluentPut("column", 4);
        String tile2 = gameController.getTile(request).getString("id");
        request = new JSONObject().fluentPut("tile_id", tile2).fluentPut("unit_id", npc2);
        gameController.setUnit(request);

        request = toJSON("row", 3, "column", 5);
        String tile3 = gameController.getTile(request).getString("id");

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
        GameController gameController = GameController.createFlatTestMap(10, 10, 0, 0);
        JSONObject unit1 = createAndPlaceRandomUnit(gameController, 3, 3);
        JSONObject unit2 = createAndPlaceRandomUnit(gameController, 3, 4);
        JSONObject unit3 = createAndPlaceRandomUnit(gameController, 3, 6);
        JSONObject unit4 = createAndPlaceRandomUnit(gameController, 2, 6);


        String unit2tile = unit2.getString("tile_id");
        JSONArray tilesInAreaOfSight = gameController.getTileInAreaOfSight(toJSON(
                "tile_id", unit2tile, "range", 2, "respectfully", true
        ));


        assertEquals(12, tilesInAreaOfSight.size());
        assertTrue(tilesInAreaOfSight.contains(unit1.getString("tile_id")));
        assertTrue(tilesInAreaOfSight.contains(unit2.getString("tile_id")));
        assertTrue(tilesInAreaOfSight.contains(unit3.getString("tile_id")));
        assertFalse(tilesInAreaOfSight.contains(unit4.getString("tile_id")));

        //[ ][ ][0][ ][ ]
        //[ ][o][o][0][4]
        //[ ][1][2][0][3]
        //[ ][o][o][0][ ]
        //[ ][ ][0][ ][ ]
    }

    @Test
    public void areaOfSightObstructedOrNotObstructedByStructure() {
        GameController gameController = GameController.createFlatTestMap(10, 10, 0, 0);
        JSONObject struct1 = createAndPlaceStructure(gameController, 3, 3);
        JSONObject unit2 = createAndPlaceRandomUnit(gameController, 3, 4);
        JSONObject struct3 = createAndPlaceStructure(gameController, 3, 6);
        JSONObject struct4 = createAndPlaceStructure(gameController, 2, 6);


        String unit2tile = unit2.getString("tile_id");
        JSONArray tilesInAreaOfSight = gameController.getTileInAreaOfSight(toJSON(
                "tile_id", unit2tile, "range", 2, "respectfully", true
        ));


        assertEquals(12, tilesInAreaOfSight.size());
        assertTrue(tilesInAreaOfSight.contains(struct1.getString("tile_id")));
        assertTrue(tilesInAreaOfSight.contains(unit2.getString("tile_id")));
        assertTrue(tilesInAreaOfSight.contains(struct3.getString("tile_id")));
        assertFalse(tilesInAreaOfSight.contains(struct4.getString("tile_id")));

        //[ ][ ][0][ ][ ]
        //[ ][o][o][0][4]
        //[ ][1][2][0][3]
        //[ ][o][o][0][ ]
        //[ ][ ][0][ ][ ]
    }

}
