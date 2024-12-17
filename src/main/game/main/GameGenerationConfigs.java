package main.game.main;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class GameGenerationConfigs extends JSONObject {


    // USED FOR GENERATING THE TILE MAP AND SHOULD ALWAYS BE SET
    public static final String ROWS = "gameplay.rows";
    public static final String COLUMNS = "gameplay.columns";
    public static final String MODEL_MAP_GENERATION_WATER_LEVEL = "gameplay.water.level";
    public static final String MODEL_MAP_GENERATION_WATER_ASSET = "gameplay.water.asset";
    public static final String MODEL_MAP_GENERATION_USE_NOISE = "map.generation.use.noise";
    public static final String MODEL_MAP_GENERATION_MIN_HEIGHT = "map.generation.min.height";
    public static final String MODEL_MAP_GENERATION_MAX_HEIGHT = "map.generation.max.height";
    public static final String MODEL_MAP_GENERATION_NOISE_ZOOM = "map.generation.zoom";
    public static final String MODEL_MAP_GENERATION_TERRAIN_ASSET = "map.generation.base.terrain";
    public static final String FOUNDATION_THICKNESS = "map.generation.base.level";
    public static final String FOUNDATION_ASSET = "map.generation.base.asset";
    public static final String MODEL_MAP_GENERATION_STRUCTURE_ASSETS = "map.generation.structure.assets";

    public static final String MODEL_MAP_GENERATION_STARTING_CAMERA_X = "map.generation.starting.x";
    public static final String MODEL_MAP_GENERATION_STARTING_CAMERA_Y = "map.generation.starting.y";
    public static final String MODEL_MAP_GENERATION_STARTING_SPRITE_WIDTH = "map.generation.starting.sprite.width";
    public static final String MODEL_MAP_GENERATION_STARTING_SPRITE_HEIGHT = "map.generation.starting.sprite.height";
    public static final String MODEL_MAP_GENERATION_STARTING_VIEWPORT_WIDTH = "map.generation.starting.viewport.width";
    public static final String MODEL_MAP_GENERATION_STARTING_VIEWPORT_HEIGHT = "map.generation.starting.viewport.height";
    public static final String MODEL_MAP_GENERATION_CENTER_MAP_ON_STARTUP = "map.center.on.startup";



    public static GameGenerationConfigs getDefaults() {
        
        GameGenerationConfigs ggc = new GameGenerationConfigs();
        
        // Initial default options for game setup
        ggc.setRows(20);
        ggc.setColumns(20);

        ggc.setFoundationAsset("base_floor");
        ggc.setFoundationThickness(1);

        ggc.setWaterAsset("water_liquid");
        ggc.setWaterLevel(0);

        ggc.setTerrainAsset("dirty_grass_1_floor");

        ggc.setUseNoise(true);
        ggc.setMinimumHeight(-10);
        ggc.setMaximumHeight(10);
        ggc.setNoiseZoom(.75f);
        ggc.setStructureAssets(List.of("tree4_structure"));

        ggc.setStartingViewportWidth(1280);
        ggc.setStartingViewportHeight(720);
        ggc.setStartingSpriteWidth(64);
        ggc.setStartingSpriteHeight(64);
        ggc.setStartingCameraX(0);
        ggc.setStartingCameraY(0);

        ggc.setCenterMapOnStartup(true);

        return ggc;
    }


    public int getRows() { return getInt(ROWS); }
    public GameGenerationConfigs setRows(int tileMapRows) {
        put(ROWS, tileMapRows);
        return this;
    }

    public int getColumns() { return getInt(COLUMNS); }
    public GameGenerationConfigs setColumns(int tileMapColumns) {
        put(COLUMNS, tileMapColumns);
        return this;
    }

    public boolean getUseNoiseGeneration() { return getBoolean(MODEL_MAP_GENERATION_USE_NOISE); }
    public GameGenerationConfigs setUseNoise(boolean useMapGeneration) {
        put(MODEL_MAP_GENERATION_USE_NOISE, useMapGeneration);
        return this;
    }

    public int getMinNoiseGenerationHeight() { return getInt(MODEL_MAP_GENERATION_MIN_HEIGHT); }
    public GameGenerationConfigs setMinimumHeight(int minHeight) {
        put(MODEL_MAP_GENERATION_MIN_HEIGHT, minHeight);
        return this;
    }

    public int getMaxNoiseGenerationHeight() { return getInt(MODEL_MAP_GENERATION_MAX_HEIGHT); }
    public GameGenerationConfigs setMaximumHeight(int maxHeight) {
        put(MODEL_MAP_GENERATION_MAX_HEIGHT, maxHeight);
        return this;
    }

    public float getNoiseGenerationZoom() { return getFloat(MODEL_MAP_GENERATION_NOISE_ZOOM); }
    public GameGenerationConfigs setNoiseZoom(float zoom) {
        put(MODEL_MAP_GENERATION_NOISE_ZOOM, zoom);
        return this;
    }

    public int getWaterLevel() { return getInt(MODEL_MAP_GENERATION_WATER_LEVEL); }
    public GameGenerationConfigs setWaterLevel(int height) {
        put(MODEL_MAP_GENERATION_WATER_LEVEL, height);
        return this;
    }
    public String getWaterAsset() { return getString(MODEL_MAP_GENERATION_WATER_ASSET); }
    public GameGenerationConfigs setWaterAsset(String asset) {
        put(MODEL_MAP_GENERATION_WATER_ASSET, asset);
        return this;
    }

    public String getTerrainAsset() { return getString(MODEL_MAP_GENERATION_TERRAIN_ASSET); }
    public GameGenerationConfigs setTerrainAsset(String terrain) {
        put(MODEL_MAP_GENERATION_TERRAIN_ASSET, terrain);
        return this;
    }

    public int getFoundationThickness() { return getInt(FOUNDATION_THICKNESS); }
    public GameGenerationConfigs setFoundationThickness(int amount) {
        put(FOUNDATION_THICKNESS, amount);
        return this;
    }
    public String getFoundationAsset() { return getString(FOUNDATION_ASSET); }
    public GameGenerationConfigs setFoundationAsset(String asset) {
        put(FOUNDATION_ASSET, asset);
        return this;
    }


    public List<String> getStructureAssets() {
        return getJSONArray(MODEL_MAP_GENERATION_STRUCTURE_ASSETS)
                .toList()
                .stream()
                .map(Object::toString)
                .toList();
    }
    public GameGenerationConfigs setStructureAssets(List<String> assets) {
        put(MODEL_MAP_GENERATION_STRUCTURE_ASSETS, new JSONArray(assets));
        return this;
    }

    public int getStartingCameraX() { return getInt(MODEL_MAP_GENERATION_STARTING_CAMERA_X); }
    public GameGenerationConfigs setStartingCameraX(int value) {
        put(MODEL_MAP_GENERATION_STARTING_CAMERA_X, value);
        return this;
    }

    public int getStartingCameraY() { return getInt(MODEL_MAP_GENERATION_STARTING_CAMERA_Y); }
    public GameGenerationConfigs setStartingCameraY(int value) {
        put(MODEL_MAP_GENERATION_STARTING_CAMERA_Y, value);
        return this;
    }

    public int getStartingSpriteWidth() { return getInt(MODEL_MAP_GENERATION_STARTING_SPRITE_WIDTH); }
    public GameGenerationConfigs setStartingSpriteWidth(int value) {
        put(MODEL_MAP_GENERATION_STARTING_SPRITE_WIDTH, value);
        return this;
    }

    public int getStartingSpriteHeight() { return getInt(MODEL_MAP_GENERATION_STARTING_SPRITE_HEIGHT); }
    public GameGenerationConfigs setStartingSpriteHeight(int value) {
        put(MODEL_MAP_GENERATION_STARTING_SPRITE_HEIGHT, value);
        return this;
    }

    public int getStartingViewportWidth() { return getInt(MODEL_MAP_GENERATION_STARTING_VIEWPORT_WIDTH); }
    public GameGenerationConfigs setStartingViewportWidth(int value) {
        put(MODEL_MAP_GENERATION_STARTING_VIEWPORT_WIDTH, value);
        return this;
    }

    public int getStartingViewportHeight() { return getInt(MODEL_MAP_GENERATION_STARTING_VIEWPORT_HEIGHT); }
    public GameGenerationConfigs setStartingViewportHeight(int value) {
        put(MODEL_MAP_GENERATION_STARTING_VIEWPORT_HEIGHT, value);
        return this;
    }

    public boolean shouldCenterMapOnStartup() { return getBoolean(MODEL_MAP_GENERATION_CENTER_MAP_ON_STARTUP); }
    public GameGenerationConfigs setCenterMapOnStartup(boolean value) {
        put(MODEL_MAP_GENERATION_CENTER_MAP_ON_STARTUP, value);
        return this;
    }



}
