package main.ui;

import main.constants.GameStateKey;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GameState {
    private final Map<String, Object> state = new HashMap<>();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    public GameState() {
        state.put(GameStateKey.UI_SETTINGS_AUTO_END_TURNS, true);
    }

    public void set(String key, Object obj) { state.put(key, obj); }
    public void log(String key, Object obj) {
        logger.info("Storing key {0} with value {1}", key, obj);
        set(key, obj);
    }
    public boolean getBoolean(String key) {
        Object value = state.get(key);
        if (value == null) {
            return false;
        } else {
            return (boolean) value;
        }
    }

    public Object getObject(String key) { return state.get(key); }
}
