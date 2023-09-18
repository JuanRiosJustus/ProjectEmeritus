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

    public Settings() {
        // 1366×768
        settings.put(GAMEPLAY_CURRENT_SPRITE_SIZE, 48);
        settings.put(GAMEPLAY_FAST_FORWARD_TURNS, false);
        settings.put(GAMEPLAY_AUTO_END_TURNS, true);
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


    public boolean getBoolean(String key) { return (Boolean) settings.get(key); }
    public int getInteger(String key) { return (Integer) settings.get(key); }
    public float getFloat(String key) { return (Float) settings.get(key); }

    public int getSpriteSize() { return getInteger(GAMEPLAY_CURRENT_SPRITE_SIZE); }
    public int getScreenWidth() { return getInteger(DISPLAY_WIDTH); }
    public int getScreenHeight() { return getInteger(DISPLAY_WIDTH); }
}
