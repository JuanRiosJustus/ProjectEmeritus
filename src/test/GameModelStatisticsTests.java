package test;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import main.game.main.GameConfigs;
import main.game.main.GameController;
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
        GameController gameController = GameController.createFlatTestMap(10, 10, 0, 0);
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
        GameController gameController = GameController.createFlatTestMap(10, 10, 0, 0);
        JSONObject unit1 = createAndPlaceRandomUnit(gameController, 3, 3);
        JSONObject unit2 = createAndPlaceRandomUnit(gameController, 3, 4);
        JSONObject unit3 = createAndPlaceRandomUnit(gameController, 3, 6);
        JSONObject unit4 = createAndPlaceRandomUnit(gameController, 2, 6);


        String unit2tile = unit2.getString("tile_id");
        JSONArray tilesInAreaOfSight = gameController.getTileInAreaOfSight(toJSON(
                "start_tile_id", unit2tile, "range", 2, "respectfully", true
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
                "start_tile_id", unit2tile, "range", 2, "respectfully", true
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

//    addUOrSubtractFromUnitStatisticResource
    @Test
    public void testApplyNegativeModification() {
        GameController game = GameController.create(5, 5, 400, 400);

        JSONObject unitData = game.createUnit();
        String unitID = unitData.getString("unit_id");

        JSONObject request = new JSONObject().fluentPut("unit_id", unitID).fluentPut("attribute", "mana");
        JSONObject before = game.getStatisticsFromUnit(request);

        request = new JSONObject()
                .fluentPut("unit_id", unitID)
                .fluentPut("attribute", "mana")
                .fluentPut("scaling", "total")
                .fluentPut("value", -.1f);
        JSONObject result = game.addUOrSubtractUnitStatisticModification(request);

        request = new JSONObject().fluentPut("unit_id", unitID).fluentPut("attribute", "mana");
        JSONObject after = game.getStatisticsFromUnit(request);

        assertEquals(before.getFloatValue("base"), after.getFloatValue("base"));
        assertTrue(before.getFloatValue("modified") > after.getFloatValue("modified"));
        assertTrue(before.getFloatValue("total") > after.getFloatValue("total"));
        assertNotEquals(before.getFloatValue("current"), after.getFloatValue("current"));
        assertEquals(before.getFloatValue("missing"),  after.getFloatValue("missing"));
    }


    @Test
    public void testApplyPositiveModification() {
        GameController game = GameController.create(5, 5, 400, 400);

        JSONObject unitData = game.createUnit();
        String unitID = unitData.getString("unit_id");

        JSONObject request = new JSONObject().fluentPut("unit_id", unitID).fluentPut("attribute", "health");
        JSONObject before = game.getStatisticsFromUnit(request);

        request = new JSONObject()
                .fluentPut("unit_id", unitID)
                .fluentPut("attribute", "health")
                .fluentPut("scaling", "base")
                .fluentPut("value", 0.333f);
        JSONObject result = game.addUOrSubtractUnitStatisticModification(request);

        request = new JSONObject().fluentPut("unit_id", unitID).fluentPut("attribute", "health");
        JSONObject after = game.getStatisticsFromUnit(request);

        assertEquals(before.getFloatValue("base"), after.getFloatValue("base"));
        assertTrue(before.getFloatValue("modified") < after.getFloatValue("modified"));
        assertTrue(before.getFloatValue("total") < after.getFloatValue("total"));
        assertEquals(before.getFloatValue("current"), after.getFloatValue("current"));
        assertTrue(before.getFloatValue("missing") < after.getFloatValue("missing"));
    }

    @Test
    public void testApplyWithNullScalingDefaultsToFlat() {
        GameController game = GameController.create(5, 5, 400, 400);
        String unitID = game.createUnit().getString("unit_id");
        String tileID = game.getTile(toJSON("row", 2, "column", 2)).getString("tile_id");
        game.setUnit(toJSON("unit_id", unitID, "tile_id", tileID));

        JSONObject statsBefore = game.getStatisticsFromUnit(toJSON("unit_id", unitID, "attribute", "health"));
        JSONObject req = toJSON("unit_id", unitID, "attribute", "health", "value", 30f);  // no scaling
        JSONObject result = game.addUOrSubtractUnitStatisticModification(req);
        JSONObject statsAfter = game.getStatisticsFromUnit(toJSON("unit_id", unitID, "attribute", "health"));

        assertEquals(30f, result.getFloatValue("delta"), 0.01f);
        assertEquals(statsBefore.getFloatValue("total") + 30f, statsAfter.getFloatValue("total"), 0.01f);
    }

    @Test
    public void testInvalidScalingReturnsError() {
        GameController game = GameController.create(5, 5, 400, 400);
        String unitID = game.createUnit().getString("unit_id");

        JSONObject req = toJSON("unit_id", unitID, "attribute", "health", "scaling", "invalid_scale", "value", 1.0f);
        JSONObject result = game.addUOrSubtractUnitStatisticModification(req);

        assertTrue(result.containsKey("error"));
        assertEquals("Invalid scaling source", result.getString("error"));
    }


//    @Test
//    public void canPayAbilityCosts() {
//        GameController gameController = GameController.createFlatTestMap(10, 10, 0, 0);
//        JSONObject unit1 = createAndPlaceRandomUnit(gameController, 3, 3);
//        String unitID = unit1.getString("unit_id");
//        String tileID = unit1.getString("tile_id");
//
//        JSONObject beforeStats = gameController.getStatisticsFromUnit(toJSON("unit_id", unitID, "attribute", "health"));
//
//        JSONObject data = gameController.addUOrSubtractUnitStatisticModification(
//                new JSONObject()
//                        .fluentPut("unit_id", unitID)
//                        .fluentPut("attribute", "health")
//                        .fluentPut("scaling", "base")
//                        .fluentPut("value", 5)
//        );
//
//
//        JSONObject afterStats = gameController.getStatisticsFromUnit(toJSON("unit_id", unitID, "attribute", "health"));
//        System.err.println("took");
//
//
////        gameController.canPayAbilityCost(new JSONObject().fluentPut("unit_id", unitID).fluentPut("ability", "slash"));
////        JSONObject unit2 = createAndPlaceRandomUnit(gameController, 3, 4);
////        gameController.useAbility(new JSONObject());
//
//
//        //[ ][ ][0][ ][ ]
//        //[ ][o][o][0][4]
//        //[ ][1][2][0][3]
//        //[ ][o][o][0][ ]
//        //[ ][ ][0][ ][ ]
//    }
//
//
//    @Test
//    public void testApplyFlatStatModification() {
//        GameController game = GameController.create(5, 5, 400, 400);
//
//        JSONObject request = game.createUnit();
//        String unitID = request.getString("unit_id");
//
//        request = new JSONObject()
//                .fluentPut("row", 2)
//                .fluentPut("column", 2);
//        JSONObject unitTile = game.getTile(request);
//        String tileID = unitTile.getString("tile_id");
//
//        request = new JSONObject()
//                .fluentPut("unit_id", unitID)
//                .fluentPut("tile_id", tileID);
//        game.setUnit(request);
//
//        request = new JSONObject()
//                .fluentPut("unit_id", unitID)
//                .fluentPut("attribute", "health")
//                .fluentPut("scaling", "flat")
//                .fluentPut("value", 25)
//                .fluentPut("source", "test_item");
//
//        JSONObject result = game.addUOrSubtractUnitStatisticModification(request);
//
//        assertTrue(result.getFloatValue("after") > result.getFloatValue("before"));
//        assertEquals(25.0f, result.getFloatValue("delta"), 0.01);
//
////        request = new JSONObject().fluentPut("unit_id", unitID).fluentPut("attribute", "health");
////        JSONObject current = game.getStatisticsFromUnit(request);
//
////        assertEquals(current.getFloatValue(""));
//        System.err.println("okrokok");
//    }
//
//    @Test
//    public void testApplyBaseScaledStatModification() {
//        GameController game = GameController.create(5, 5, 400, 400);
//
//        JSONObject request = game.createUnit();
//        String unitID = request.getString("unit_id");
//
//        request = new JSONObject().fluentPut("row", 1).fluentPut("column", 1);
//        JSONObject unitTile = game.getTile(request);
//
//        request = new JSONObject()
//                .fluentPut("unit_id", unitID)
//                .fluentPut("tile_id", unitTile.getString("tile_id"));
//        game.setUnit(request);
//
//        request = new JSONObject()
//                .fluentPut("unit_id", unitID)
//                .fluentPut("attribute", "health");
//        JSONObject statBefore = game.getStatisticsFromUnit(request);
//
//
//        request = new JSONObject()
//                .fluentPut("unit_id", unitID)
//                .fluentPut("attribute", "health")
//                .fluentPut("scaling", "base")
//                .fluentPut("value", .1f) // +10%
//                .fluentPut("name", "test_buff")
//                .fluentPut("source", "testApplyBaseScaledStatModification");
//        JSONObject result = game.addUOrSubtractUnitStatisticModification(request);
//
//        request = new JSONObject()
//                .fluentPut("unit_id", unitID)
//                .fluentPut("attribute", "health");
//        JSONObject statAfter = game.getStatisticsFromUnit(request);
//
//        assertEquals(statBefore.get("current"), statAfter.get("current"));
//        assertTrue(statBefore.getFloatValue("total") < statAfter.getFloatValue("total"));
//        assertTrue(result.getFloatValue("after") > result.getFloatValue("before"));
////        assertEquals(expectedDelta, result.getFloatValue("delta"), 0.5); // Allow slight float deviation
//    }
//
//    @Test
//    public void testInvalidAttributeGracefullyHandled() {
//        GameController game = GameController.create(5, 5, 400, 400);
//
//        JSONObject unitData = game.createUnit();
//        String unitID = unitData.getString("unit_id");
//
//        JSONObject request = new JSONObject()
//                .fluentPut("unit_id", unitID)
//                .fluentPut("attribute", "nonexistent_stat")
//                .fluentPut("scaling", "flat")
//                .fluentPut("value", 10);
//
//        JSONObject result = game.addUOrSubtractUnitStatisticModification(request);
//        // Your method currently returns before/after even on invalid attributes, so just check it doesn't crash
//        assertNotNull(result);
//        assertTrue(result.containsKey("before"));
//        assertTrue(result.containsKey("after"));
//    }


//
//    @Test
//    public void testApplyFlatStatModification_reflectsInGetStatistics() {
//        GameController game = GameController.create(5, 5, 400, 400);
//
//        String unitID = EntityStore.getInstance().createUnit(false);
//        game.setUnit(new JSONObject()
//                .fluentPut("unit_id", unitID)
//                .fluentPut("row", 2)
//                .fluentPut("column", 2));
//
//        // Get original stats
//        JSONObject statsBefore = game.getStatisticsFromUnit(new JSONObject()
//                .fluentPut("unit_id", unitID)
//                .fluentPut("attribute", "hp"));
//        float originalTotal = statsBefore.getFloatValue("total");
//
//        // Apply +20 flat
//        float flatBonus = 20f;
//        JSONObject request = new JSONObject()
//                .fluentPut("unit_id", unitID)
//                .fluentPut("attribute", "hp")
//                .fluentPut("scaling", "flat")
//                .fluentPut("value", flatBonus);
//        game.applyUnitStatisticModification(request);
//
//        // Check updated stats
//        JSONObject statsAfter = game.getStatisticsFromUnit(new JSONObject()
//                .fluentPut("unit_id", unitID)
//                .fluentPut("attribute", "hp"));
//        float newTotal = statsAfter.getFloatValue("total");
//
//        assertEquals(originalTotal + flatBonus, newTotal, 0.01);
//    }
//
//    @Test
//    public void testApplyBaseScaledStatModification_reflectsInGetStatistics() {
//        GameController game = GameController.create(5, 5, 400, 400);
//
//        String unitID = EntityStore.getInstance().createUnit(false);
//        game.setUnit(new JSONObject()
//                .fluentPut("unit_id", unitID)
//                .fluentPut("row", 1)
//                .fluentPut("column", 1));
//
//        // Get base value
//        JSONObject statsBefore = game.getStatisticsFromUnit(new JSONObject()
//                .fluentPut("unit_id", unitID)
//                .fluentPut("attribute", "hp"));
//        float baseValue = statsBefore.getFloatValue("base");
//        float originalTotal = statsBefore.getFloatValue("total");
//
//        // Apply +10% base
//        float percent = 0.1f;
//        JSONObject request = new JSONObject()
//                .fluentPut("unit_id", unitID)
//                .fluentPut("attribute", "hp")
//                .fluentPut("scaling", "base")
//                .fluentPut("value", percent);
//        game.applyUnitStatisticModification(request);
//
//        // Check updated stats
//        JSONObject statsAfter = game.getStatisticsFromUnit(new JSONObject()
//                .fluentPut("unit_id", unitID)
//                .fluentPut("attribute", "hp"));
//        float newTotal = statsAfter.getFloatValue("total");
//
//        float expectedIncrease = baseValue * percent;
//        assertEquals(originalTotal + expectedIncrease, newTotal, 0.5); // Allow float tolerance
//    }

//    @Test
//    public void gameModel_astarShortestPathWithObstructionWall() {
//        GameController gameController = GameController.create(5, 5, 400, 400);
//
//        // Place Start and End tiles
//        JSONObject startTile = gameController.getTile(toJSON("row", 0, "column", 0));
//        String startTileID = startTile.getString("tile_id");
//        JSONObject endTile = gameController.getTile(toJSON("row", 4, "column", 4));
//        String endTileID = endTile.getString("tile_id");
//
//        // Obstructions (set a unit on each obstructed tile)
//        int[][] obstructions = {
//                {0, 3}, {1, 1}, {1, 3},
//                {2, 1}, {3, 3}, {4, 1}
//        };
//
//        /**
//         * [S][ ][ ][ ][ ]
//         * [ ][X][ ][ ][ ]
//         * [ ][X][ ][ ][ ]
//         * [ ][X][ ][ ][ ]
//         * [ ][ ][ ][ ][ ]
//
//         */
//        /**
//         * S * * X .
//         * . X * X .
//         * . X * * *
//         * . . * X .
//         * . X * * E
//         */
//
//        for (int[] pos : obstructions) {
//            String blockingUnitID = EntityStore.getInstance().createUnit(false);
//            gameController.setUnit(toJSON("unit_id", blockingUnitID, "row", pos[0], "column", pos[1]));
//        }
//
//        // Create request to get shortest path
//        JSONObject request = new JSONObject();
//        request.put("start_tile_id", startTileID);
//        request.put("end_tile_id", endTileID);
//        request.put("range", 20);  // Ensure it's large enough to allow detours
//        request.put("respectfully", true);  // Should avoid units
//
//        JSONArray path = gameController.getTilesInMovementPath(request);
//
//        // Validate the path
//        assertFalse(path.isEmpty(), "A* should find a valid path");
//
//        // Start and end should match
//        assertEquals(startTileID, path.getString(0));
//        assertEquals(endTileID, path.getString(path.size() - 1));
//
//        // Should not include any obstructed tile
//        for (int[] pos : obstructions) {
//            String obstructedID = gameController.getTile(toJSON("row", pos[0], "column", pos[1])).getString("tile_id");
//            assertFalse(path.contains(obstructedID), "Path should not include obstructed tile: " + obstructedID);
//        }
//
//        // Optionally print the path
//        System.out.println("A* Path: " + path);
//    }

}
