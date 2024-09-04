package main.game.main;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.engine.Engine;

public class Settings extends JsonObject {
    public static final String SHOW_VISION_RANGE = "Show.vision.Range";
    private static Settings instance;
    public static final String GAMEPLAY_AUTO_END_TURNS = "auto.end.turns";
    public static final String GAMEPLAY_FAST_FORWARD_TURNS = "speed.turns";
    public static final String GAMEPLAY_CURRENT_SPRITE_SIZE = "current.sprite.size";
    public static final String GAMEPLAY_SPRITE_WIDTH = "current_sprite_width";
    public static final String GAMEPLAY_SPRITE_HEIGHT = "current_sprite_height";
    public static final String GAMEPLAY_MODE = "gameplay.mode";
    public static final String GAMEPLAY_MODE_LOAD_OUT = "gameplay.mode.load.out";
    public static final String GAMEPLAY_MODE_REGULAR = "gameplay.mode.regular";
    public static final String GAMEPLAY_TILE_ROWS = "gameplay.rows";
    public static final String GAMEPLAY_TILE_COLUMNS = "gameplay.columns";
    public static final String GAMEPLAY_DEBUG_MODE = "debug_mode_for_the_game_state";

    private Settings() { /* DO NOT USE */ }

    public static Settings getDefaults() {
        Settings settings = new Settings();
        // 1366×768
        settings.put(GAMEPLAY_CURRENT_SPRITE_SIZE, 64);
        settings.put(GAMEPLAY_SPRITE_WIDTH, 64);
        settings.put(GAMEPLAY_SPRITE_HEIGHT, 64);

        settings.put(GAMEPLAY_TILE_ROWS, 20);
        settings.put(GAMEPLAY_TILE_COLUMNS, 20);

        settings.put(GAMEPLAY_FAST_FORWARD_TURNS, false);
        settings.put(GAMEPLAY_AUTO_END_TURNS, true);
        settings.put(GAMEPLAY_MODE, GAMEPLAY_MODE_REGULAR);

        settings.put(GAMEPLAY_DEBUG_MODE, false);

        return settings;

    }
    public static Settings getInstance() {
        if (instance == null) {
            instance = getDefaults();
        }
        return instance;
    }

    public boolean getBoolean(String key) { return (Boolean) getBooleanOrDefault(key, false); }
    public boolean getBooleanOrDefault(String key, boolean defaultingValue) {
        if (containsKey(key)) {
            return (Boolean) get(key);
        } else {
            return defaultingValue;
        }
    }
    public int getInteger(String key) { return (Integer) get(key); }

    public int getSpriteSize() { return getInteger(GAMEPLAY_CURRENT_SPRITE_SIZE); }
    public int getSpriteWidth() { return getInteger(GAMEPLAY_SPRITE_WIDTH); }
    public int getSpriteHeight() { return getInteger(GAMEPLAY_SPRITE_HEIGHT); }
    
    public int getScreenWidth() { return Engine.getInstance().getViewWidth(); }
    public int getScreenHeight() { return Engine.getInstance().getViewHeight(); }
    public int getTileRows() { return getInteger(GAMEPLAY_TILE_ROWS); }
    public int getTileColumns() { return getInteger(GAMEPLAY_TILE_COLUMNS); }
    public boolean isLoadOutMode() { return get(GAMEPLAY_MODE).equals(GAMEPLAY_MODE_LOAD_OUT); }
    public boolean isDebugMode() { return getBooleanOrDefault(GAMEPLAY_DEBUG_MODE, false); }
}
