package test;

import com.alibaba.fastjson2.JSONObject;
import javafx.embed.swing.JFXPanel;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.stores.EntityStore;
import org.junit.Before;

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
