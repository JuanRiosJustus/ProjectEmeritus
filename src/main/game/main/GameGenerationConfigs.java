package main.game.main;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class GameGenerationConfigs extends JSONObject {


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
    public static final String MODEL_MAP_GENERATION_BASE_LEVEL = "map.generation.base.level";
    public static final String MODEL_MAP_GENERATION_BASE_ASSET = "map.generation.base.asset";
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
        ggc.setMapGenerationStep1MapRows(20);
        ggc.setMapGenerationStep2MapColumns(20);
        ggc.setMapGenerationStep3BaseAsset("base_floor");
        ggc.setMapGenerationStep4BaseLevel(1);
        ggc.setMapGenerationStep5WaterAsset("water_liquid");
        ggc.setMapGenerationStep6WaterLevel(0);
        ggc.setMapGenerationStep7TerrainAsset("dirty_grass_1_floor");
        ggc.setMapGenerationStep8UseNoise(true);
        ggc.setMapGenerationStep9MinHeight(-10);
        ggc.setMapGenerationStep10MaxHeight(10);
        ggc.setMapGenerationStep11NoiseZoom(.75f);
        ggc.setMapGenerationStep12StructureAssets(List.of("tree4_structure"));


        ggc.setStartingViewportWidth(1280);
        ggc.setStartingViewportHeight(720);
        ggc.setStartingSpriteWidth(64);
        ggc.setStartingSpriteHeight(64);
        ggc.setStartingCameraX(0);
        ggc.setStartingCameraY(0);

        ggc.setCenterMapOnStartup(true);

        return ggc;
    }


    public int getMapRows() { return getInt(MODEL_MAP_GENERATION_MAP_ROWS); }
    public GameGenerationConfigs setMapGenerationStep1MapRows(int tileMapRows) {
        put(MODEL_MAP_GENERATION_MAP_ROWS, tileMapRows);
        return this;
    }

    public int getMapColumns() { return getInt(MODEL_MAP_GENERATION_MAP_COLUMNS); }
    public GameGenerationConfigs setMapGenerationStep2MapColumns(int tileMapColumns) {
        put(MODEL_MAP_GENERATION_MAP_COLUMNS, tileMapColumns);
        return this;
    }

    public boolean getUseNoiseGeneration() { return getBoolean(MODEL_MAP_GENERATION_USE_NOISE); }
    public GameGenerationConfigs setMapGenerationStep8UseNoise(boolean useMapGeneration) {
        put(MODEL_MAP_GENERATION_USE_NOISE, useMapGeneration);
        return this;
    }

    public int getMinNoiseGenerationHeight() { return getInt(MODEL_MAP_GENERATION_MIN_HEIGHT); }
    public GameGenerationConfigs setMapGenerationStep9MinHeight(int minHeight) {
        put(MODEL_MAP_GENERATION_MIN_HEIGHT, minHeight);
        return this;
    }

    public int getMaxNoiseGenerationHeight() { return getInt(MODEL_MAP_GENERATION_MAX_HEIGHT); }
    public GameGenerationConfigs setMapGenerationStep10MaxHeight(int maxHeight) {
        put(MODEL_MAP_GENERATION_MAX_HEIGHT, maxHeight);
        return this;
    }

    public float getNoiseGenerationZoom() { return getFloat(MODEL_MAP_GENERATION_NOISE_ZOOM); }
    public GameGenerationConfigs setMapGenerationStep11NoiseZoom(float zoom) {
        put(MODEL_MAP_GENERATION_NOISE_ZOOM, zoom);
        return this;
    }

    public int getMapGenerationWaterLevel() { return getInt(MODEL_MAP_GENERATION_WATER_LEVEL); }
    public GameGenerationConfigs setMapGenerationStep6WaterLevel(int height) {
        put(MODEL_MAP_GENERATION_WATER_LEVEL, height);
        return this;
    }
    public String getMapGenerationWaterAsset() { return getString(MODEL_MAP_GENERATION_WATER_ASSET); }
    public GameGenerationConfigs setMapGenerationStep5WaterAsset(String asset) {
        put(MODEL_MAP_GENERATION_WATER_ASSET, asset);
        return this;
    }

    public String getMapGenerationTerrainAsset() { return getString(MODEL_MAP_GENERATION_TERRAIN_ASSET); }
    public GameGenerationConfigs setMapGenerationStep7TerrainAsset(String terrain) {
        put(MODEL_MAP_GENERATION_TERRAIN_ASSET, terrain);
        return this;
    }

    public int getMapGenerationBaseLevel() { return getInt(MODEL_MAP_GENERATION_BASE_LEVEL); }
    public GameGenerationConfigs setMapGenerationStep4BaseLevel(int amount) {
        put(MODEL_MAP_GENERATION_BASE_LEVEL, amount);
        return this;
    }
    public String getMapGenerationBaseAsset() { return getString(MODEL_MAP_GENERATION_BASE_ASSET); }
    public GameGenerationConfigs setMapGenerationStep3BaseAsset(String asset) {
        put(MODEL_MAP_GENERATION_BASE_ASSET, asset);
        return this;
    }
//
    public List<String> getMapGenerationStructureAssets() {
        return getJSONArray(MODEL_MAP_GENERATION_STRUCTURE_ASSETS)
                .toList()
                .stream()
                .map(Object::toString)
                .toList();
    }
    public GameGenerationConfigs setMapGenerationStep12StructureAssets(List<String> assets) {
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
