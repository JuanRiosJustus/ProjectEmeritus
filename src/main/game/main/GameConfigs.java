package main.game.main;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.List;

public class GameConfigs extends JSONObject {
    public static final String MAP_GENERATION_WATER_ENABLED = "map.generation.water.enabled";
    public static final String MAP_GENERATION_WATER_ASSET = "map.generation.water.asset";
    public static final String MAP_GENERATION_STRUCTURE_ASSETS = "map.generation.structure.assets";

    public static final String ON_STARTUP_CAMERA_X = "on.startup.starting.x";
    public static final String ON_STARTUP_CAMERA_Y = "on.startup.starting.y";
    public static final String ON_STARTUP_SPRITE_WIDTH = "on.startup.starting.sprite.width";
    public static final String ON_STARTUP_SPRITE_HEIGHT = "on.startup.starting.sprite.height";
    public static final String ON_STARTUP_CENTER_CAMERA_ON_MAP = "on.startup.center.camera.on.map";

    public static final String VIEWPORT_WIDTH = "map.generation.starting.camera.width";
    public static final String VIEWPORT_HEIGHT = "map.generation.starting.camerra.height";

    private static final String SPAWN_PLACEMENT_POLICY = "spawn.placement.policy";
    public static final String SPAWN_PLACEMENT_POLICY_EVERYWHERE = "everywhere";



    public static GameConfigs getDefaults() {
        GameConfigs ggc = new GameConfigs();
        
        // Initial default options for game setup
        ggc.setMapGenerationRows(20);
        ggc.setMapGenerationColumns(20);
        ggc.setViewportWidth(1500);
        ggc.setViewportHeight(950);

        ggc.setMapGenerationFoundationAsset("obsidian_floor");
        ggc.setMapGenerationFoundationDepth(3);

        ggc.setMapGenerationLiquidAsset("water_liquid");
        ggc.setMapGenerationLiquidElevation(7);
        ggc.setMapGenerationLiquidElevationEnabled(false);

        ggc.setMapGenerationTerrainAsset("dirty_grass_1_floor");
        ggc.setMapGenerationTerrainStartingElevation(6);
        ggc.setMapGenerationTerrainEndingElevation(9);
        ggc.setMapGenerationTerrainHeightNoise(.666f);

//        ggc.setMapGenerationStructureAssets(List.of("tree4_structure"));
//        ggc.setOnStartupCameraWidth(1280);
//        ggc.setOnStartupCameraHeight(720);
        ggc.setOnStartupSpriteWidth(96);
        ggc.setOnStartupSpriteHeight(96);
        ggc.setOnStartupCameraX(0);
        ggc.setOnStartupCameraY(0);
        ggc.setOnStartupCenterCameraOnMap(true);

//        ggc.setSpawnPlacementPolicy(SPAWN_PLACEMENT_POLICY_EVERYWHERE);

        return ggc;
    }

    public GameConfigs() { }
    public GameConfigs(JSONObject configs) {
        JSONObject defaults = getDefaults();
        for (String key : defaults.keySet()) { put(key, defaults.get(key)); }
        for (String key : configs.keySet()) { put(key, configs.get(key)); }
    }

//    public String setMap



    public static final String MAP_GENERATION_ROWS = "map.generation.rows";
    public int getMapGenerationRows() { return getIntValue(MAP_GENERATION_ROWS); }
    public GameConfigs setMapGenerationRows(int rows) { put(MAP_GENERATION_ROWS, rows); return this; }


    public static final String MAP_GENERATION_COLUMNS = "map.generation.columns";
    public int getMapGenerationColumns() { return getIntValue(MAP_GENERATION_COLUMNS); }
    public GameConfigs setMapGenerationColumns(int columns) { put(MAP_GENERATION_COLUMNS, columns); return this; }

//    public static final String MAP_GENERATION_HEIGHT_MAP_FROM_ARRAY = "map.generation.height.map.from.array";
//    public int[][] getMapGenerationHeightMapFromArray() { return getIntValue(MAP_GENERATION_ROWS); }
//    public GameConfigs setMapGenerationHeightMapFromArray(int[][] map) { put(MAP_GENERATION_ROWS, map); return this; }


    private static final String MAP_GENERATION_TERRAIN_STARTING_ELEVATION = "map.generation.terrain.starting.elevation";
    public int getMapGenerationTerrainStartingElevation() { return getIntValue(MAP_GENERATION_TERRAIN_STARTING_ELEVATION); }
    public GameConfigs setMapGenerationTerrainStartingElevation(int minHeight) { put(MAP_GENERATION_TERRAIN_STARTING_ELEVATION, minHeight); return this; }


    private static final String MAP_GENERATION_TERRAIN_ENDING_ELEVATION = "map.generation.terrain.ending.elevation";
    public int getMapGenerationTerrainEndingElevation() { return getIntValue(MAP_GENERATION_TERRAIN_ENDING_ELEVATION); }
    public GameConfigs setMapGenerationTerrainEndingElevation(int maxHeight) { put(MAP_GENERATION_TERRAIN_ENDING_ELEVATION, maxHeight); return this; }



    private static final String MAP_GENERATION_TERRAIN_HEIGHT_NOISE = "map.generation.terrain.height.noise";
    public float getMapGenerationHeightNoise() { return getFloat(MAP_GENERATION_TERRAIN_HEIGHT_NOISE); }
    public GameConfigs setMapGenerationTerrainHeightNoise(float zoom) { put(MAP_GENERATION_TERRAIN_HEIGHT_NOISE, zoom); return this; }



    public boolean isMapGenerationLiquidElevationEnabled() {
        return getBooleanValue(MAP_GENERATION_WATER_ENABLED, false);
    }
    public GameConfigs setMapGenerationLiquidElevationEnabled(boolean value) {
        put(MAP_GENERATION_WATER_ENABLED, value);
        return this;
    }



    public static final String MAP_GENERATION_FOUNDATION_DEPTH = "map.generation.foundation.depth";
    public int getMapGenerationFoundationDepth() { return getIntValue(MAP_GENERATION_FOUNDATION_DEPTH, 0); }
    public GameConfigs setMapGenerationFoundationDepth(int depth) { put(MAP_GENERATION_FOUNDATION_DEPTH, depth); return this; }


    public static final String MAP_GENERATION_FOUNDATION_ASSET = "map.generation.foundation.asset";
    public String getMapGenerationFoundationAsset() { return getString(MAP_GENERATION_FOUNDATION_ASSET); }
    public GameConfigs setMapGenerationFoundationAsset(String asset) { put(MAP_GENERATION_FOUNDATION_ASSET, asset); return this; }




    public static final String MAP_GENERATION_WATER_LEVEL = "map.generation.water.level";
    public int getMapGenerationLiquidElevation() { return getIntValue(MAP_GENERATION_WATER_LEVEL); }
    public GameConfigs setMapGenerationLiquidElevation(int height) {
        put(MAP_GENERATION_WATER_LEVEL, height);
        return this;
    }
    public String getLiquidAsset() { return getString(MAP_GENERATION_WATER_ASSET); }
    public GameConfigs setMapGenerationLiquidAsset(String asset) {
        put(MAP_GENERATION_WATER_ASSET, asset);
        return this;
    }



    public static final String MAP_GENERATION_TERRAIN_ASSET = "map.generation.base.terrain";
    public String getMapGenerationTerrainAsset() { return getString(MAP_GENERATION_TERRAIN_ASSET); }
    public GameConfigs setMapGenerationTerrainAsset(String terrain) {
        put(MAP_GENERATION_TERRAIN_ASSET, terrain);
        return this;
    }


//    public List<String> getStructureAssets() {
//        return getJSONArray(MAP_GENERATION_STRUCTURE_ASSETS)
//                .stream()
//                .map(Object::toString)
//                .toList();
//    }

    public JSONArray getStructureAssets() {
        return getJSONArray(MAP_GENERATION_STRUCTURE_ASSETS);
    }

    public GameConfigs setMapGenerationStructureAssets(List<String> assets) {
        put(MAP_GENERATION_STRUCTURE_ASSETS, new JSONArray(assets));
        return this;
    }


    public int getOnStartupCameraX() { return getIntValue(ON_STARTUP_CAMERA_X); }
    public GameConfigs setOnStartupCameraX(int value) { put(ON_STARTUP_CAMERA_X, value); return this; }

    public int getOnStartupCameraY() { return getIntValue(ON_STARTUP_CAMERA_Y); }
    public GameConfigs setOnStartupCameraY(int value) { put(ON_STARTUP_CAMERA_Y, value); return this; }




    public int getOnStartupSpriteWidth() { return getIntValue(ON_STARTUP_SPRITE_WIDTH); }
    public GameConfigs setOnStartupSpriteWidth(int value) { put(ON_STARTUP_SPRITE_WIDTH, value); return this; }

    public int getOnStartupSpriteHeight() { return getIntValue(ON_STARTUP_SPRITE_HEIGHT); }
    public GameConfigs setOnStartupSpriteHeight(int value) { put(ON_STARTUP_SPRITE_HEIGHT, value); return this; }




    public int getViewportWidth() { return getIntValue(VIEWPORT_WIDTH); }
    public GameConfigs setViewportWidth(int value) { put(VIEWPORT_WIDTH, value); return this; }

    public int getViewportHeight() { return getIntValue(VIEWPORT_HEIGHT); }
    public GameConfigs setViewportHeight(int value) { put(VIEWPORT_HEIGHT, value); return this; }













    public boolean setOnStartupCenterCameraOnMap() { return getBoolean(ON_STARTUP_CENTER_CAMERA_ON_MAP); }
    public GameConfigs setOnStartupCenterCameraOnMap(boolean value) {
        put(ON_STARTUP_CENTER_CAMERA_ON_MAP, value);
        return this;
    }

    public String getSpawnPlacementPolicy() { return getString(SPAWN_PLACEMENT_POLICY); }
    public GameConfigs setSpawnPlacementPolicy(String placementPolicy) {
        put(SPAWN_PLACEMENT_POLICY, placementPolicy);
        return this;
    }

}
