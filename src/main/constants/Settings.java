package main.constants;

import java.util.HashMap;
import java.util.Map;

public class Settings {
    private final Map<String, Object> settings = new HashMap<>();
    private static Settings instance;
    public static final String DISPLAY_WIDTH = "application.width";
    public static final String DISPLAY_HEIGHT = "application.height";
    public static final String GAMEPLAY_AUTO_END_TURNS = "auto.end.turns";
    public static final String GAMEPLAY_FAST_FORWARD_TURNS = "speed.turns";
    public static final String GAMEPLAY_CURRENT_SPRITE_SIZE = "current.sprite.size";
    public static final String GAMEPLAY_CURRENT_SPRITE_WIDTH = "current.sprite.width";
    public static final String GAMEPLAY_CURRENT_SPRITE_HEIGHT = "current.sprite.height";
    public static final String GAMEPLAY_MODE = "gameplay.mode";
    public static final String GAMEPLAY_MODE_LOAD_OUT = "gameplay.mode.load.out";
    public static final String GAMEPLAY_MODE_REGULAR = "gameplay.mode.regular";
    public static final String GAMEPLAY_TILE_ROWS = "gameplay.rows";
    public static final String GAMEPLAY_TILE_COLUMNS = "gameplay.columns";

    public Settings() {
        // 1366×768
        settings.put(GAMEPLAY_CURRENT_SPRITE_SIZE, 64);
        settings.put(GAMEPLAY_CURRENT_SPRITE_WIDTH, 64);
        settings.put(GAMEPLAY_CURRENT_SPRITE_HEIGHT, 64);

        settings.put(GAMEPLAY_TILE_ROWS, 20);
        settings.put(GAMEPLAY_TILE_COLUMNS, 20);

        settings.put(GAMEPLAY_FAST_FORWARD_TURNS, false);
        settings.put(GAMEPLAY_AUTO_END_TURNS, true);
        settings.put(GAMEPLAY_MODE, GAMEPLAY_MODE_REGULAR);

        settings.put(DISPLAY_WIDTH, 1366);
        settings.put(DISPLAY_HEIGHT, 768);
//        settings.put(DISPLAY_WIDTH, 1600);
//        settings.put(DISPLAY_HEIGHT, 1000);
    }
    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public void set(String setting, Object value) {
        settings.put(setting, value);
    }


    public boolean getBoolean(String key) { return (Boolean) settings.get(key); }
    public int getInteger(String key) { return (Integer) settings.get(key); }
    public float getFloat(String key) { return (Float) settings.get(key); }

    public int getSpriteSize() { return getInteger(GAMEPLAY_CURRENT_SPRITE_SIZE); }
    public int getSpriteWidth() { return getInteger(GAMEPLAY_CURRENT_SPRITE_WIDTH); }
    public int getSpriteHeight() { return getInteger(GAMEPLAY_CURRENT_SPRITE_HEIGHT); }
    
    public int getScreenWidth() { return getInteger(DISPLAY_WIDTH); }
    public int getScreenHeight() { return getInteger(DISPLAY_HEIGHT); }
    public int getTileRows() { return getInteger(GAMEPLAY_TILE_ROWS); }
    public int getTileColumns() { return getInteger(GAMEPLAY_TILE_COLUMNS); }
    public boolean isLoadOutMode() { return settings.get(GAMEPLAY_MODE).equals(GAMEPLAY_MODE_LOAD_OUT); }
}
