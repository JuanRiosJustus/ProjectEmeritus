package main.game.main;

import com.github.cliftonlabs.json_simple.JsonObject;

public class GameSettings extends JsonObject {
    private static final String SHOW_ACTION_RANGES = "Show.action.Ranges";
    private static final String SHOW_MOVEMENT_RANGES = "Show_movement_ranges";
    private static final String SHOW_HEIGHTS = "Show_heights";

    private static GameSettings instance;
    public static final String GAMEPLAY_AUTO_END_TURNS = "auto.end.turns";
    public static final String GAMEPLAY_FAST_FORWARD_TURNS = "speed.turns";
    public static final String MODEL_SPRITE_CURRENT_SIZE = "current.sprite.size";
    public static final String GAMEPLAY_HIDE_UI = "hide.gameplay.ui";

    public static final String VIEW_VIEWPORT_WIDTH = "view.width";
    public static final String VIEW_VIEWPORT_HEIGHT = "view.height";
    public static final String MODEL_SPRITE_WIDTH = "current_sprite_width";
    public static final String MODEL_SPRITE_HEIGHT = "current_sprite_height";

    // USED FOR GENERATING THE TILE MAP AND SHOULD ALWAYS BE SET
    public static final String MODEL_MAP_GENERATION_TILE_ROWS = "gameplay.rows";
    public static final String MODEL_MAP_GENERATION_TILE_COLUMNS = "gameplay.columns";
    public static final String MODEL_MAP_GENERATION_TILE_HEIGHT = "gameplay.tile.height";
    public static final String MODEL_MAP_GENERATION_USE_NOISE = "map.generation.use.noise";
    public static final String MODEL_MAP_GENERATION_NOISE_MIN_HEIGHT = "map.generation.min.height";
    public static final String MODEL_MAP_GENERATION_NOISE_MAX_HEIGHT = "map.generation.max.height";
    public static final String MODEL_MAP_GENERATION_NOISE_ZOOM = "map.generation.zoom";
    public static final String MODEL_MAP_GENERATION_TERRAIN_ASSET = "map.generation.base.terrain";

    public static final String GAMEPLAY_MODE = "gameplay.mode";
    public static final String GAMEPLAY_MODE_MAP_EDITOR_MODE = "gameplay.map.editor.mode";
    public static final String GAMEPLAY_MODE_UNIT_DEPLOYMENT = "gameplay.mode.load.out";
    public static final String GAMEPLAY_MODE_REGULAR = "gameplay.mode.regular";
    public static final String GAMEPLAY_DEBUG_MODE = "debug_mode_for_the_game_state";

    public static final String MODEL_MAP_DESCRIPTION = "model.map.description";

    private GameSettings() { }

    public static GameSettings getDefaults() {
        GameSettings gameSettings = new GameSettings();
        // 1366×768
        gameSettings.put(VIEW_VIEWPORT_WIDTH, 100);
        gameSettings.put(VIEW_VIEWPORT_HEIGHT, 100);

        gameSettings.put(MODEL_SPRITE_CURRENT_SIZE, 64);
        gameSettings.put(MODEL_SPRITE_WIDTH, 64);
        gameSettings.put(MODEL_SPRITE_HEIGHT, 64);

        gameSettings.put(MODEL_MAP_GENERATION_TILE_ROWS, 20);
        gameSettings.put(MODEL_MAP_GENERATION_TILE_COLUMNS, 20);
        gameSettings.put(MODEL_MAP_GENERATION_TILE_HEIGHT, 0);

        // Map Height Generation
        gameSettings.put(MODEL_MAP_GENERATION_USE_NOISE, false);
        gameSettings.put(MODEL_MAP_GENERATION_NOISE_MIN_HEIGHT, -10);
        gameSettings.put(MODEL_MAP_GENERATION_NOISE_MAX_HEIGHT, 10);
        gameSettings.put(MODEL_MAP_GENERATION_NOISE_ZOOM, .5f);

        gameSettings.put(GAMEPLAY_FAST_FORWARD_TURNS, false);
        gameSettings.put(GAMEPLAY_AUTO_END_TURNS, true);
        gameSettings.put(GAMEPLAY_MODE, GAMEPLAY_MODE_REGULAR);

        gameSettings.put(GAMEPLAY_DEBUG_MODE, false);

        return gameSettings;
    }
    public static GameSettings getInstance() {
        if (instance == null) {
            instance = getDefaults();
        }
        return instance;
    }

    public GameSettings set(String key, Object value) {
        put(key, value);
        return this;
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
    public float getFloat(String key) { return (Float) get(key); }
    public String getString(String key) { return (String) get(key); }

    public int getSpriteSize() { return getInteger(MODEL_SPRITE_CURRENT_SIZE); }
    public int getSpriteWidth() { return getInteger(MODEL_SPRITE_WIDTH); }
    public int getSpriteHeight() { return getInteger(MODEL_SPRITE_HEIGHT); }
    
//    public int getViewPortWidth() { return Engine.getInstance().getViewWidth(); }
//    public int getViewPortHeight() { return Engine.getInstance().getViewHeight(); }
    public int getViewPortWidth() { return getInteger(VIEW_VIEWPORT_WIDTH); }
    public int getViewPortHeight() { return getInteger(VIEW_VIEWPORT_HEIGHT); }

    public int getTileRows() { return getInteger(MODEL_MAP_GENERATION_TILE_ROWS); }
    public int getTileColumns() { return getInteger(MODEL_MAP_GENERATION_TILE_COLUMNS); }
    public boolean shouldUseNoiseGeneration() { return getBooleanOrDefault(MODEL_MAP_GENERATION_USE_NOISE, false); }


    public GameSettings setSpriteWidthAndHeight(int width, int height) {
        put(MODEL_SPRITE_WIDTH, width);
        put(MODEL_SPRITE_HEIGHT, height);
        return this;
    }
    public GameSettings setMapRowsAndColumns(int rows, int columns) {
        put(MODEL_MAP_GENERATION_TILE_ROWS, rows);
        put(MODEL_MAP_GENERATION_TILE_COLUMNS, columns);
        return this;
    }

    public GameSettings setViewPortWidthAndHeight(int width, int height) {
        put(VIEW_VIEWPORT_WIDTH, width);
        put(VIEW_VIEWPORT_HEIGHT, height);
        return this;
    }

    public void setGameMode(String mode) { put(GAMEPLAY_MODE, mode); }
    public void setModeAsUnitDeploymentMode() { put(GAMEPLAY_MODE, GAMEPLAY_MODE_UNIT_DEPLOYMENT); }
    public boolean isUnitDeploymentMode() { return get(GAMEPLAY_MODE).equals(GAMEPLAY_MODE_UNIT_DEPLOYMENT); }
    public void setModeAsMapEditorMode() { put(GAMEPLAY_MODE, GAMEPLAY_MODE_MAP_EDITOR_MODE); }
    public boolean isMapEditorMode() { return get(GAMEPLAY_MODE).equals(GAMEPLAY_MODE_MAP_EDITOR_MODE); }
    public boolean isDebugMode() { return getBooleanOrDefault(GAMEPLAY_DEBUG_MODE, false); }
    public void setShouldShowActionRanges(boolean value) { put(SHOW_ACTION_RANGES, value); }
    public boolean shouldShowActionRanges() { return getBooleanOrDefault(SHOW_ACTION_RANGES, false); }
    public void setShouldShowMovementRanges(boolean value) { put(SHOW_MOVEMENT_RANGES, value); }
    public boolean shouldShowMovementRanges() { return getBooleanOrDefault(SHOW_MOVEMENT_RANGES, false); }
    public void setShouldShowHeights(boolean value) { put(SHOW_HEIGHTS, value); }
    public boolean shouldShowHeights() { return getBooleanOrDefault(SHOW_HEIGHTS, false); }

    public boolean setShowGameplayUI() { return getBoolean(GAMEPLAY_HIDE_UI); }
    public GameSettings setShowGameplayUI(boolean show) {
        put(GAMEPLAY_HIDE_UI, show);
        return this;
    }
    public GameSettings setViewportWidth(int viewportWidth) {
        put(VIEW_VIEWPORT_WIDTH, viewportWidth);
        return this;
    }
    public GameSettings setViewportHeight(int viewportHeight) {
        put(VIEW_VIEWPORT_HEIGHT, viewportHeight);
        return this;
    }
    public GameSettings setTileMapRows(int tileMapRows) {
        put(MODEL_MAP_GENERATION_TILE_ROWS, tileMapRows);
        return this;
    }
    public GameSettings setTileMapColumns(int tileMapColumns) {
        put(MODEL_MAP_GENERATION_TILE_COLUMNS, tileMapColumns);
        return this;
    }
    public GameSettings setSpriteWidth(int spriteWidth) {
        put(MODEL_SPRITE_WIDTH, spriteWidth);
        return this;
    }
    public GameSettings setSpriteHeight(int spriteHeight) {
        put(MODEL_SPRITE_HEIGHT, spriteHeight);
        return this;
    }
    public GameSettings setUseNoiseGeneration(boolean useMapGeneration) {
        put(MODEL_MAP_GENERATION_USE_NOISE, useMapGeneration);
        return this;
    }

    public int getMinNoiseGenerationHeight() { return getInteger(MODEL_MAP_GENERATION_NOISE_MIN_HEIGHT); }
    public GameSettings setMinNoiseGenerationHeight(int minHeight) {
        put(MODEL_MAP_GENERATION_NOISE_MIN_HEIGHT, minHeight);
        return this;
    }

    public int getMaxNoiseGenerationHeight() { return getInteger(MODEL_MAP_GENERATION_NOISE_MAX_HEIGHT); }
    public GameSettings setMaxNoiseGenerationHeight(int maxHeight) {
        put(MODEL_MAP_GENERATION_NOISE_MAX_HEIGHT, maxHeight);
        return this;
    }

    public float getNoiseGenerationZoom() { return getFloat(MODEL_MAP_GENERATION_NOISE_ZOOM); }
    public GameSettings setNoiseGenerationZoom(float zoom) {
        put(MODEL_MAP_GENERATION_NOISE_ZOOM, zoom);
        return this;
    }

    public int getMapGenerationTileHeight() { return getInteger(MODEL_MAP_GENERATION_TILE_HEIGHT); }
    public GameSettings setMapGenerationTileHeight(int height) {
        put(MODEL_MAP_GENERATION_TILE_HEIGHT, height);
        return this;
    }

    public String getMapGenerationTerrainAsset() { return getString(MODEL_MAP_GENERATION_TERRAIN_ASSET); }
    public GameSettings setMapGenerationTerrainAsset(String terrain) {
        put(MODEL_MAP_GENERATION_TERRAIN_ASSET, terrain);
        return this;
    }
}
