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

    protected void pauseGame(GameController gc, long waitTime) {
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
    protected GameController initializeGame(int rows, int columns, boolean headless) {
        int width = 1280;
        int height = 720;
        GameController rc = GameController.create(rows, columns, width, height);

        JSONObject request = new JSONObject().fluentPut("value", false);
        rc.setAutomaticallyShouldEndCpusTurn(request);


        if (headless) {
            new Thread(() -> {
                rc.initialize();
                while (rc.isRunning()) {
                    rc.updateDeltaTime();
                }
            }).start();
        } else {
            try {
                JavaFXTestUtils.runAndWait(() -> {
                    rc.initialize();
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


    public static JSONObject fillUnitResource(GameController gc, String unitID, String att, float val, boolean fill) {
        setUnitAttributeTotalValue(gc, unitID, att, val);
        if (fill) {
            setUnitResourceToToValue(gc, unitID, att, val);
        }

        JSONObject request = new JSONObject()
                .fluentPut("unit_id", unitID)
                .fluentPut("attribute", att);
        JSONObject result = gc.getStatisticsFromUnit(request);


        assertEquals(result.getFloatValue("current"), result.getFloatValue("total"));
        return new JSONObject();
    }

    public static JSONObject fillAllUnitResources(GameController gc, String unitID, float val) {
        fillUnitResource(gc, unitID, "health", val, true);
        fillUnitResource(gc, unitID, "mana", val, true);
        fillUnitResource(gc, unitID, "stamina", val, true);
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

    public static JSONObject setUnitAttributeTotalValue(GameController gc, String unitID, String att, float val) {
        // work with 100's. Very easy
        JSONObject request = new JSONObject()
                .fluentPut("unit_id", unitID)
                .fluentPut("attribute", att);
        JSONObject before = gc.getStatisticsFromUnit(request);
        float toAddOrSubtract = 0f;
        boolean isIncreasingValue = before.getFloatValue("total") < val;
        boolean isDecreasingValue = before.getFloatValue("total") > val;
        if (isIncreasingValue) {
            toAddOrSubtract += val - before.getFloatValue("total");
        } else if (isDecreasingValue) {
            toAddOrSubtract -= Math.abs(before.getFloatValue("total") - val);
        }
        // Start request
        request = new JSONObject()
                .fluentPut("unit_id", unitID)
                .fluentPut("attribute", att)
                .fluentPut("scaling", "flat")
                .fluentPut("value", toAddOrSubtract)
                .fluentPut("source", "testTwoBuffWithSameNameDoNotOverlap");
        JSONObject result = gc.addUOrSubtractUnitStatisticModification(request);

        request = new JSONObject()
                .fluentPut("unit_id", unitID)
                .fluentPut("attribute", att);
        JSONObject after = gc.getStatisticsFromUnit(request);

        assertEquals(before.getFloatValue("base"), after.getFloatValue("base"));
        if (isIncreasingValue) {
//            assertTrue(before.getFloatValue("modified") < after.getFloatValue("modified"));
            assertTrue(before.getFloatValue("total") < after.getFloatValue("total"));
//            assertEquals(before.getFloatValue("current"), after.getFloatValue("current"));
//            assertTrue(before.getFloatValue("missing") < after.getFloatValue("missing"));
//            assertTrue(after.getFloatValue("current") > 0);
        } else if (isDecreasingValue) {
//            assertTrue(before.getFloatValue("modified") > after.getFloatValue("modified"));
            assertTrue(before.getFloatValue("total") > after.getFloatValue("total"));
//            assertTrue(before.getFloatValue("missing") >= after.getFloatValue("missing"));
        } else {
            assertEquals(before.getFloatValue("total"), after.getFloatValue("total"));
        }

        assertEquals(after.getFloatValue("total"), val);
//        addUOrSubtractUnitStatisticResource
        return after;
    }


    public static JSONObject setUnitResourceToToValue(GameController gc, String unitID, String att, float val) {
        // work with 100's. Very easy
        JSONObject request = new JSONObject()
                .fluentPut("unit_id", unitID)
                .fluentPut("attribute", att);
        JSONObject before = gc.getStatisticsFromUnit(request);


        // Start request
        request = new JSONObject()
                .fluentPut("unit_id", unitID)
                .fluentPut("attribute", att)
                .fluentPut("value", val)
                .fluentPut("source", "setUnitResourceToToValue");
        JSONObject result = gc.addUOrSubtractUnitStatisticResource(request);

        request = new JSONObject()
                .fluentPut("unit_id", unitID)
                .fluentPut("attribute", att);
        JSONObject after = gc.getStatisticsFromUnit(request);


        if (val > 0) {
            assertEquals(before.getFloatValue("base"), after.getFloatValue("base"));
            assertEquals(before.getFloatValue("modified") ,after.getFloatValue("modified"));
            assertEquals(before.getFloatValue("total"), after.getFloatValue("total"));
            assertEquals(after.getFloatValue("current"), after.getFloatValue("total"));
            assertEquals(after.getFloatValue("missing"), 0);
        } else {
            assertEquals(before.getFloatValue("base"), after.getFloatValue("base"));
            assertEquals(before.getFloatValue("modified") ,after.getFloatValue("modified"));
            assertEquals(before.getFloatValue("total"), after.getFloatValue("total"));
        }

        return after;
    }




    protected JSONObject createAndPlaceRandomUnit(GameController gc, int row, int column) {
        String unit_id = gc.createUnit().getString("id");
        String tile_id = gc.getTile(toJSON("row", row, "column", column)).getString("id");
        gc.setUnit(toJSON("tile_id", tile_id, "unit_id", unit_id));
        return new JSONObject().fluentPut("tile_id", tile_id).fluentPut("unit_id", unit_id);
    }

    protected JSONObject createAndPlaceStructure(GameController gc, int row, int column) {
        JSONObject result = gc.setStructure(new JSONObject().fluentPut("row", row).fluentPut("column", column));
        return result;
    }
    protected Entity getEntityWithID(String id) {
        return EntityStore.getInstance().get(id);
    }
}
