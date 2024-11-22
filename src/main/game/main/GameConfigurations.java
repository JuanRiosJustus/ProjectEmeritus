package main.game.main;

import org.json.JSONObject;

public class GameConfigurations extends JSONObject {
    private static final String SHOW_ACTION_RANGES = "Show.action.Ranges";
    private static final String SHOW_MOVEMENT_RANGES = "Show_movement_ranges";
    private static final String OPTION_HIDE_TILE_HEIGHTS = "show.heights";

    private static GameConfigurations instance;
    public static final String GAMEPLAY_AUTO_END_TURNS = "auto.end.turns";
    public static final String GAMEPLAY_FAST_FORWARD_TURNS = "speed.turns";
    public static final String OPTION_HIDE_GAMEPLAY_HUD = "hide.gameplay.ui";

    public static final String VIEW_VIEWPORT_WIDTH = "view.width";
    public static final String VIEW_VIEWPORT_HEIGHT = "view.height";
    public static final String MODEL_SPRITE_WIDTH = "current_sprite_width";
    public static final String MODEL_SPRITE_HEIGHT = "current_sprite_height";

    // USED FOR GENERATING THE TILE MAP AND SHOULD ALWAYS BE SET
    public static final String MODEL_MAP_GENERATION_MAP_ROWS = "gameplay.rows";
    public static final String MODEL_MAP_GENERATION_MAP_COLUMNS = "gameplay.columns";
    public static final String MODEL_MAP_GENERATION_WATER_LEVEL = "gameplay.water.level";
    public static final String MODEL_MAP_GENERATION_WATER_ASSET = "gameplay.water.asset";
    public static final String MODEL_MAP_GENERATION_USE_NOISE = "map.generation.use.noise";
    public static final String MODEL_MAP_GENERATION_MIN_HEIGHT = "map.generation.min.height";
    public static final String MODEL_MAP_GENERATION_MAX_HEIGHT = "map.generation.max.height";
    public static final String MODEL_MAP_GENERATION_NOISE_ZOOM = "map.generation.zoom";
    public static final String MODEL_MAP_GENERATION_TERRAIN_ASSET = "map.generation.base.terrain";
    public static final String MODEL_MAP_GENERATION_BASE_LEVEL = "map.generation.foundation.level";
    public static final String MODEL_MAP_GENERATION_BASE_ASSET = "map.generation.foundation.asset";

    public static final String GAMEPLAY_MODE = "gameplay.mode";
    public static final String GAMEPLAY_MODE_MAP_EDITOR_MODE = "gameplay.map.editor.mode";
    public static final String GAMEPLAY_MODE_UNIT_DEPLOYMENT = "gameplay.mode.load.out";
    public static final String GAMEPLAY_MODE_REGULAR = "gameplay.mode.regular";
    public static final String GAMEPLAY_DEBUG_MODE = "debug_mode_for_the_game_state";

    public static final String MODEL_MAP_DESCRIPTION = "model.map.description";

    private GameConfigurations() { }
    public GameConfigurations(JSONObject raw) {
        for (String key : raw.keySet()) {
            put(key, raw.get(key));
        }
    }

    public static GameConfigurations getDefaults() {
        GameConfigurations gameConfigurations = new GameConfigurations();
        gameConfigurations.setViewportWidth(1280);
        gameConfigurations.setViewportHeight(720);

        gameConfigurations.setSpriteWidth(64);
        gameConfigurations.setSpriteHeight(64);

        gameConfigurations.setMapGenerationStep1MapRows(20);
        gameConfigurations.setMapGenerationStep2MapColumns(20);
        gameConfigurations.setMapGenerationStep3BaseAsset("base_floor");
        gameConfigurations.setMapGenerationStep4BaseLevel(1);
        gameConfigurations.setMapGenerationStep5WaterAsset("water_liquid");
        gameConfigurations.setMapGenerationStep6WaterLevel(0);
        gameConfigurations.setMapGenerationStep7TerrainAsset("dirty_grass_1_floor");
        gameConfigurations.setMapGenerationStep8UseNoise(true);
        gameConfigurations.setMapGenerationStep9MinHeight(-10);
        gameConfigurations.setMapGenerationStep10MaxHeight(10);
        gameConfigurations.setMapGenerationStep11NoiseZoom(.75f);



        gameConfigurations.setIsDebugMode(false);
        gameConfigurations.setGameMode(GAMEPLAY_MODE_REGULAR);

        gameConfigurations.setOptionShouldHideGameplayTileHeights(true);

        gameConfigurations.setOptionHideGameplayHUD(true);


        return gameConfigurations;
    }

//    public static GameConfigurations getDefaults() {
//        GameConfigurations gameConfigurations = new GameConfigurations();
//        // 1366×768,
//        gameConfigurations.put(VIEW_VIEWPORT_WIDTH, 100);
//        gameConfigurations.put(VIEW_VIEWPORT_HEIGHT, 100);
//
//        gameConfigurations.put(MODEL_SPRITE_WIDTH, 64);
//        gameConfigurations.put(MODEL_SPRITE_HEIGHT, 64);
//
//        gameConfigurations.put(MODEL_MAP_GENERATION_MAP_ROWS, 20);
//        gameConfigurations.put(MODEL_MAP_GENERATION_MAP_COLUMNS, 20);
//
//        gameConfigurations.put(MODEL_MAP_GENERATION_BASE_ASSET, "base_floor");
//        gameConfigurations.put(MODEL_MAP_GENERATION_BASE_LEVEL, 1);
//
//        // Map Height Generation
//        gameConfigurations.put(MODEL_MAP_GENERATION_USE_NOISE, true);
//        gameConfigurations.put(MODEL_MAP_GENERATION_MIN_HEIGHT, -10);
//        gameConfigurations.put(MODEL_MAP_GENERATION_WATER_LEVEL, 0);
//        gameConfigurations.put(MODEL_MAP_GENERATION_MAX_HEIGHT, 10);
//        gameConfigurations.put(MODEL_MAP_GENERATION_NOISE_ZOOM, .75f);
//
//
//
//        gameConfigurations.put(GAMEPLAY_FAST_FORWARD_TURNS, false);
//        gameConfigurations.put(GAMEPLAY_AUTO_END_TURNS, true);
//        gameConfigurations.put(GAMEPLAY_MODE, GAMEPLAY_MODE_REGULAR);
//
//
//
//
//
//        gameConfigurations.append(SHOW_HEIGHTS, false);
//
//        gameConfigurations.put(GAMEPLAY_DEBUG_MODE, false);
//
//
//
//
//
//
//
//
//
//
////        GameConfigurations settings = GameConfigurations.getDefaults()
////                // Required args
////                .setViewportWidth(mGamePanelWidth)
////                .setViewportHeight(mGamePanelHeight)
////                .setTileMapRows(newTileMapRows)
////                .setTileMapColumns(newTileMapColumns)
////                .setSpriteWidth(newSpriteWidth)
////                .setSpriteHeight(newSpriteHeight)
////                .setMapGenerationBaseAsset(baseAsset)
////                .setMapGenerationBaseLevel(baseLevel)
////                .setMapGenerationWaterAsset(waterAsset)
////                .setMapGenerationWaterLevel(waterLevel)
////                .setMapGenerationTerrainAsset(terrainAsset)
////                // Setup randomization
////                .setShowGameplayUI(false)
////                .setUseNoiseGeneration(true)
////                .setMinNoiseGenerationHeight(minHeight)
////                .setMaxNoiseGenerationHeight(maxHeight)
////                .setNoiseGenerationZoom(noiseZoom)
////
////                .setUseNoiseGeneration(true);
//
//
//        return gameConfigurations;
//    }
    public static GameConfigurations getInstance() {
        if (instance == null) {
            instance = getDefaults();
        }
        return instance;
    }
    public int getSpriteWidth() { return getInt(MODEL_SPRITE_WIDTH); }
    public int getSpriteHeight() { return getInt(MODEL_SPRITE_HEIGHT); }
    public int getViewPortWidth() { return getInt(VIEW_VIEWPORT_WIDTH); }
    public int getViewPortHeight() { return getInt(VIEW_VIEWPORT_HEIGHT); }

    public int getMapRows() { return getInt(MODEL_MAP_GENERATION_MAP_ROWS); }
    public int getMapColumns() { return getInt(MODEL_MAP_GENERATION_MAP_COLUMNS); }

    public GameConfigurations setSpriteWidthAndHeight(int width, int height) {
        put(MODEL_SPRITE_WIDTH, width);
        put(MODEL_SPRITE_HEIGHT, height);
        return this;
    }
    public GameConfigurations setMapRowsAndColumns(int rows, int columns) {
        put(MODEL_MAP_GENERATION_MAP_ROWS, rows);
        put(MODEL_MAP_GENERATION_MAP_COLUMNS, columns);
        return this;
    }

    public GameConfigurations setViewPortWidthAndHeight(int width, int height) {
        put(VIEW_VIEWPORT_WIDTH, width);
        put(VIEW_VIEWPORT_HEIGHT, height);
        return this;
    }

    public void setGameMode(String mode) { put(GAMEPLAY_MODE, mode); }
    public void setModeAsUnitDeploymentMode() { put(GAMEPLAY_MODE, GAMEPLAY_MODE_UNIT_DEPLOYMENT); }
    public boolean isUnitDeploymentMode() { return get(GAMEPLAY_MODE).equals(GAMEPLAY_MODE_UNIT_DEPLOYMENT); }
    public void setModeAsMapEditorMode() { put(GAMEPLAY_MODE, GAMEPLAY_MODE_MAP_EDITOR_MODE); }
    public boolean isMapEditorMode() { return get(GAMEPLAY_MODE).equals(GAMEPLAY_MODE_MAP_EDITOR_MODE); }
//    public boolean isDebugMode() { return getBoolean(GAMEPLAY_DEBUG_MODE); }
    public void setShouldShowActionRanges(boolean value) { put(SHOW_ACTION_RANGES, value); }
    public boolean shouldShowActionRanges() { return optBoolean(SHOW_ACTION_RANGES, true); }
    public void setShouldShowMovementRanges(boolean value) { put(SHOW_MOVEMENT_RANGES, value); }
    public boolean shouldShowMovementRanges() { return optBoolean(SHOW_MOVEMENT_RANGES, true); }
    public void setOptionShouldHideGameplayTileHeights(boolean value) { put(OPTION_HIDE_TILE_HEIGHTS, value); }
    public boolean shouldHideGameplayTileHeights() { return optBoolean(OPTION_HIDE_TILE_HEIGHTS, true); }

    public boolean setOptionHideGameplayHUD() { return getBoolean(OPTION_HIDE_GAMEPLAY_HUD); }
    public GameConfigurations setOptionHideGameplayHUD(boolean show) {
        put(OPTION_HIDE_GAMEPLAY_HUD, show);
        return this;
    }

    public GameConfigurations setViewportWidth(int viewportWidth) {
        put(VIEW_VIEWPORT_WIDTH, viewportWidth);
        return this;
    }
    public GameConfigurations setViewportHeight(int viewportHeight) {
        put(VIEW_VIEWPORT_HEIGHT, viewportHeight);
        return this;
    }
    public GameConfigurations setMapGenerationStep1MapRows(int tileMapRows) {
        put(MODEL_MAP_GENERATION_MAP_ROWS, tileMapRows);
        return this;
    }
    public GameConfigurations setMapGenerationStep2MapColumns(int tileMapColumns) {
        put(MODEL_MAP_GENERATION_MAP_COLUMNS, tileMapColumns);
        return this;
    }
    public GameConfigurations setSpriteWidth(int spriteWidth) {
        put(MODEL_SPRITE_WIDTH, spriteWidth);
        return this;
    }
    public GameConfigurations setSpriteHeight(int spriteHeight) {
        put(MODEL_SPRITE_HEIGHT, spriteHeight);
        return this;
    }

    public boolean getUseNoiseGeneration() { return getBoolean(MODEL_MAP_GENERATION_USE_NOISE); }
    public GameConfigurations setMapGenerationStep8UseNoise(boolean useMapGeneration) {
        put(MODEL_MAP_GENERATION_USE_NOISE, useMapGeneration);
        return this;
    }

    public int getMinNoiseGenerationHeight() { return getInt(MODEL_MAP_GENERATION_MIN_HEIGHT); }
    public GameConfigurations setMapGenerationStep9MinHeight(int minHeight) {
        put(MODEL_MAP_GENERATION_MIN_HEIGHT, minHeight);
        return this;
    }

    public int getMaxNoiseGenerationHeight() { return getInt(MODEL_MAP_GENERATION_MAX_HEIGHT); }
    public GameConfigurations setMapGenerationStep10MaxHeight(int maxHeight) {
        put(MODEL_MAP_GENERATION_MAX_HEIGHT, maxHeight);
        return this;
    }

    public float getNoiseGenerationZoom() { return getFloat(MODEL_MAP_GENERATION_NOISE_ZOOM); }
    public GameConfigurations setMapGenerationStep11NoiseZoom(float zoom) {
        put(MODEL_MAP_GENERATION_NOISE_ZOOM, zoom);
        return this;
    }

    public int getMapGenerationWaterLevel() { return getInt(MODEL_MAP_GENERATION_WATER_LEVEL); }
    public GameConfigurations setMapGenerationStep6WaterLevel(int height) {
        put(MODEL_MAP_GENERATION_WATER_LEVEL, height);
        return this;
    }
    public String getMapGenerationWaterAsset() { return getString(MODEL_MAP_GENERATION_WATER_ASSET); }
    public GameConfigurations setMapGenerationStep5WaterAsset(String asset) {
        put(MODEL_MAP_GENERATION_WATER_ASSET, asset);
        return this;
    }

    public String getMapGenerationTerrainAsset() { return getString(MODEL_MAP_GENERATION_TERRAIN_ASSET); }
    public GameConfigurations setMapGenerationStep7TerrainAsset(String terrain) {
        put(MODEL_MAP_GENERATION_TERRAIN_ASSET, terrain);
        return this;
    }
//    MODAL_MAP_FOUNDATION_AMOUNT

    public int getMapGenerationBaseLevel() { return getInt(MODEL_MAP_GENERATION_BASE_LEVEL); }
    public GameConfigurations setMapGenerationStep4BaseLevel(int amount) {
        put(MODEL_MAP_GENERATION_BASE_LEVEL, amount);
        return this;
    }
    public String getMapGenerationBaseAsset() { return getString(MODEL_MAP_GENERATION_BASE_ASSET); }
    public GameConfigurations setMapGenerationStep3BaseAsset(String asset) {
        put(MODEL_MAP_GENERATION_BASE_ASSET, asset);
        return this;
    }

    public boolean isDebugMode() { return getBoolean(GAMEPLAY_DEBUG_MODE); }
    public GameConfigurations setIsDebugMode(boolean isDebugMode) {
        put(GAMEPLAY_DEBUG_MODE, isDebugMode);
        return this;
    }
}
