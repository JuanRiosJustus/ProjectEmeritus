package test;

import com.alibaba.fastjson2.JSONObject;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.stores.EntityStore;
import org.junit.Before;

import static org.junit.jupiter.api.Assertions.*;

public class GameTests {
    @Before
    public void initJavaFX() {
        // This initializes the JavaFX runtime
        var rrrr = new JFXPanel(); // implicitly calls Platform.startup(...) if not already done
    }

    protected static JSONObject toJSON(Object... values) {
        JSONObject object = new JSONObject();
        if (values.length % 2 != 0) { return object; }
        for (int i = 0; i < values.length; i = i + 2) {
            Object key = values[i];
            Object value = values[i + 1];
            object.put(String.valueOf(key), value);
        }
        return object;
    }

    protected void simulateUserInactivity(GameController gc, long waitTime) {
        try {
            System.out.println("Started pausing");
            Thread waiter = new Thread(() -> {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            waiter.start();
            waiter.join(0);
            System.out.println("Finished pausing");
        } catch (Exception ex) {
            System.err.println("Unable to sleep");
        }
    }

    protected void stopAndEndGame(GameController gc, long waitTime) {
        try {
            System.out.println("Started sleeping");
            Thread waiter = new Thread(() -> {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            waiter.start();
            waiter.join(0);
            gc.stop();
            System.out.println("Finished sleeping");
        } catch (Exception ex) {
            System.err.println("Unable to sleep");
        }
    }

    protected GameController createAndStartGameWithDefaults() {
        GameController gameController = createGameWithDefaults(10, 10, false);
        gameController.start();
        return gameController;
    }
    protected GameController createGameWithDefaults(int rows, int columns, boolean headless) {
        int width = 1280;
        int height = 720;
        GameController rc = GameController.create(rows, columns, width, height);

        JSONObject request = new JSONObject().fluentPut("value", false);
        rc.setAutomaticallyShouldEndCpusTurn(request);


        if (headless) {
            new Thread(() -> {
                rc.run();
                while (rc.isRunning()) {
//                    rc.updateDeltaTime();
                }
            }).start();
        } else {
            try {
                new JFXPanel();
                JavaFXTestUtils.runAndWait(() -> {
                    rc.run();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(rc.getGamePanel(), width, height));
                    stage.show();
                });
            } catch (Exception e) {
                fail();
            }
        }
        return rc;
    }


    public static JSONObject fillUnitBaseResourcesTo100Percent(GameController gc, String unitID, int val) {
        JSONObject result = gc.setBaseStatForUnit(unitID, "health", val, true);
        assertEquals(result.getIntValue("base"), 100);

        result = gc.setBaseStatForUnit(unitID, "mana", val, true);
        assertEquals(result.getIntValue("base"), 100);

        result = gc.setBaseStatForUnit(unitID, "stamina", val, true);
        assertEquals(result.getIntValue("base"), 100);

        return new JSONObject();
    }

    public static JSONObject getAndSetUnitOnTile(GameController gc, String unitID, int row, int column) {
        JSONObject request = new JSONObject()
                .fluentPut("row", row)
                .fluentPut("column", column);
        JSONObject response = gc.getTile(request);
        String tileID = response.getString("tile_id");

        request = new JSONObject()
                .fluentPut("tile_id", tileID)
                .fluentPut("unit_id", unitID);
        response = gc.setUnit(request);

        request = new JSONObject()
                .fluentPut("unit_id", unitID);
        response = gc.getTileOfUnit(response);

        String actualTileID = response.getString("tile_id");
        assertEquals(tileID, actualTileID);

        return response;
    }

    public static JSONObject setUnitAttributeToValue(GameController gc, String id, String attribute, int value) {
        return setUnitAttributeToValue(gc, id, attribute, value, false);
    }
    public static JSONObject setUnitAttributeToValue(GameController gc, String id, String attribute, int value, boolean fill) {
        assertTrue(value >= 0);

        // Get Unit Attribute
        JSONObject request = new JSONObject().fluentPut("id", id).fluentPut("attribute", attribute);
        JSONObject before = gc.getStatisticsFromUnit(request);
        assertTrue(before.getIntValue("base") >= 0);
        assertTrue(before.getIntValue("bonus") >= 0);
        assertTrue(before.getIntValue("total") >= 0);
        assertTrue(before.getIntValue("current") >= 0);
        assertTrue(before.getIntValue("missing") >= 0);

        // Calculate the difference between current total/max and the input
        int currentTotal = before.getIntValue("total");
        int difference = Math.abs(currentTotal - value) * (value < currentTotal ? -1 : 1);

        // Start request
        request = new JSONObject()
                .fluentPut("id", id)
                .fluentPut("attribute", attribute)
                .fluentPut("scaling", difference + " to " + attribute)
                .fluentPut("source", "setUnitAttributeToValue");
        JSONObject result = gc.addStatisticBonus(request);

        // Check state is still valid results
        request = new JSONObject().fluentPut("id", id).fluentPut("attribute", attribute);
        JSONObject after = gc.getStatisticsFromUnit(request);
//        assertTrue(before.getIntValue("base") >= 0);
//        assertTrue(before.getIntValue("bonus") >= 0);
        assertTrue(after.getIntValue("total") >= 0);
        assertTrue(after.getIntValue("current") >= 0);
        assertTrue(after.getIntValue("missing") >= 0);

        assertEquals(before.getIntValue("base"), after.getIntValue("base"));
        if (difference > 0) {
            assertTrue(before.getIntValue("bonus") < after.getIntValue("bonus"));
            assertTrue(before.getIntValue("total") < after.getIntValue("total"));
        } else if (difference < 0) {
            assertTrue(before.getIntValue("bonus") > after.getIntValue("bonus"));
            assertTrue(before.getIntValue("total") > after.getIntValue("total"));
        } else {
            assertEquals(before.getIntValue("base"), after.getIntValue("base"));
            assertEquals(before.getIntValue("bonus"), after.getIntValue("bonus"));
            assertEquals(before.getIntValue("total"), after.getIntValue("total"));
            assertEquals(before.getIntValue("current"), after.getIntValue("current"));
            assertEquals(before.getIntValue("missing"), after.getIntValue("missing"));
        }
        assertEquals(after.getFloatValue("total"), value);


        // If a resource was set, top it off if possible
        if (fill) {
            after = fillUnitResourceToToValue(gc, id, attribute, value);
            assertEquals(after.getIntValue("current"), after.getIntValue("total"));
            assertTrue(after.getIntValue("current") >= 0);
            assertTrue(after.getIntValue("missing") == 0);
        }
        return after;
    }

    public static JSONObject fillUnitResourceToToValue(GameController gc, String unitID, String attribute, int value) {
        // get current stats
        JSONObject request = new JSONObject().fluentPut("id", unitID).fluentPut("attribute", attribute);
        JSONObject before = gc.getStatisticsFromUnit(request);

        // Calculate the difference between current total/max and the input
        int currentTotal = before.getIntValue("current");
        int difference = Math.abs(currentTotal - value) * (value < 0 ? -1 : 1);

        // Start request
        request = new JSONObject()
                .fluentPut("id", unitID)
                .fluentPut("attribute", attribute)
                .fluentPut("value", value)
                .fluentPut("source", "setUnitResourceToToValue");
        JSONObject result = gc.addUnitStatisticsResources(request);

        // Check stats after changes
        request = new JSONObject().fluentPut("id", unitID).fluentPut("attribute", attribute);
        JSONObject after = gc.getStatisticsFromUnit(request);

        assertTrue(after.getIntValue("current") > 0);
        if (difference > 0) {
            assertTrue(before.getIntValue("current") <= after.getIntValue("current"));
        } else {
            assertTrue(before.getIntValue("current") >= after.getIntValue("current"));
        }

        return after;
    }


    public static JSONObject setUnitStatistic(GameController gc, String unitID, String stat, int value) {
        JSONObject request = new JSONObject()
                .fluentPut("unit_id", unitID)
                .fluentPut("statistic", stat);
        JSONObject before = gc.getStatisticsFromUnit(request);

        if (before.getIntValue("base") == value) {
            return before;
        }

        // Start request
        request = new JSONObject()
                .fluentPut("unit_id", unitID)
                .fluentPut("statistic", stat)
                .fluentPut("value", value)
                .fluentPut("fill", true);
        JSONObject result = gc.setBaseStatForUnit(request);

        request = new JSONObject()
                .fluentPut("unit_id", unitID)
                .fluentPut("statistic", stat);
        JSONObject after = gc.getStatisticsFromUnit(request);

        assertTrue(after.getIntValue("current") >= 0);
        if (value > 0) {
            assertTrue(after.getIntValue("current") <= value);
            assertEquals(after.getIntValue("base"), value);
        }
        return after;
    }




    protected JSONObject createAndPlaceRandomUnit(GameController gc, int row, int column) {
        String unit_id = gc.createCpuUnit().getString("id");
        String tile_id = gc.getTile(toJSON("row", row, "column", column)).getString("id");
        gc.setUnit(toJSON("tile_id", tile_id, "unit_id", unit_id));
        return new JSONObject().fluentPut("tile_id", tile_id).fluentPut("unit_id", unit_id);
    }

    protected JSONObject createAndPlaceRandomStructure(GameController gc, int row, int column) {
        String structure_id = gc.createStructure().getString("id");
        String tile_id = gc.getTile(toJSON("row", row, "column", column)).getString("id");
        gc.setStructure(toJSON("tile_id", tile_id, "structure_id", structure_id));
        return new JSONObject().fluentPut("tile_id", tile_id).fluentPut("structure", structure_id);
    }

    protected JSONObject createAndPlaceStructure(GameController gc, int row, int column) {
        JSONObject result = gc.setStructure(new JSONObject().fluentPut("row", row).fluentPut("column", column));
        return result;
    }
    protected Entity getEntityWithID(String id) {
        return EntityStore.getInstance().get(id);
    }
}
