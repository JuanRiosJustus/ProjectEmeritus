package ui;

import constants.GameStateKey;
import logging.Logger;
import logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GameState {
    private final Map<String, Object> state = new HashMap<>();
    private Logger logger = LoggerFactory.instance().logger(getClass());
    public GameState() {
        state.put(GameStateKey.SETTINGS_UI_AUTOENDTURNS, true);
    }

    public void set(String key, Object obj) { state.put(key, obj); }
    public void log(String key, Object obj) {
        logger.log("Storing key {0} with value {1}", key, obj);
        set(key, obj);
    }
    public boolean getBoolean(String key) {
        if (!state.containsKey(key)) { return false; }
        return (boolean) state.get(key); }

    public String getString(String key) { return (String) state.get(key); }
    public Object get(String key) { return state.get(key); }
    public boolean contains(String key) { return state.containsKey(key); }
    public void remove(String key) { state.remove(key); }
}
