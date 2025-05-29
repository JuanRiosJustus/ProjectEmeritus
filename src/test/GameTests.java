package test;

import com.alibaba.fastjson2.JSONObject;
import javafx.embed.swing.JFXPanel;
import main.game.entity.Entity;
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

    protected Entity getEntityWithID(String id) {
        return EntityStore.getInstance().get(id);
    }
}
