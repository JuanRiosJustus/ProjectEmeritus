package main.game.main;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.List;

public class GameConfigs extends JSONObject {
    public static final String MAP_GENERATION_ROWS = "map.generation.rows";
    public static final String MAP_GENERATION_COLUMNS = "map.generation.columns";
    public static final String MAP_GENERATION_WATER_LEVEL = "map.generation.water.level";
    public static final String MAP_GENERATION_WATER_ASSET = "map.generation.water.asset";

    public static final String MAP_GENERATION_MIN_HEIGHT = "map.generation.min.height";
    public static final String MAP_GENERATION_MAX_HEIGHT = "map.generation.max.height";
    public static final String MAP_GENERATION_NOISE_ZOOM = "map.generation.zoom";
    public static final String MAP_GENERATION_TERRAIN_ASSET = "map.generation.base.terrain";
    public static final String MAP_GENERATION_FOUNDATION_DEPTH = "map.generation.foundation.level";
    public static final String MAP_GENERATION_FOUNDATION_ASSET = "map.generation.foundation.asset";
    public static final String MAP_GENERATION_STRUCTURE_ASSETS = "map.generation.structure.assets";

    public static final String ON_STARTUP_CAMERA_X = "on.startup.starting.x";
    public static final String ON_STARTUP_CAMERA_Y = "on.startup.starting.y";
    public static final String ON_STARTUP_SPRITE_WIDTH = "on.startup.starting.sprite.width";
    public static final String ON_STARTUP_SPRITE_HEIGHT = "on.startup.starting.sprite.height";
    public static final String ON_STARTUP_CENTER_CAMERA_ON_MAP = "on.startup.center.camera.on.map";

    public static final String ON_STARTUP_CAMERA_WIDTH = "map.generation.starting.camera.width";
    public static final String ON_STARTUP_CAMERA_HEIGHT = "map.generation.starting.camerra.height";

    private static final String SPAWN_PLACEMENT_POLICY = "spawn.placement.policy";
    public static final String SPAWN_PLACEMENT_POLICY_EVERYWHERE = "everywhere";



    public static GameConfigs getDefaults() {
        GameConfigs ggc = new GameConfigs();
        
        // Initial default options for game setup
        ggc.setMapGenerationRows(20);
        ggc.setMapGenerationColumns(20);

        ggc.setMapGenerationLiquidAsset("water_liquid");
        ggc.setMapGenerationLiquidElevation(2);

        ggc.setMapGenerationTerrainAsset("dirty_grass_1_floor");
        ggc.setMapGenerationTerrainMinimumElevation(0);
        ggc.setMapGenerationTerrainMaximumElevation(10);
        ggc.setMapGenerationNoiseZoom(.75f);

        ggc.setMapGenerationStructureAssets(List.of("tree4_structure"));

        ggc.setOnStartupCameraWidth(1280);
        ggc.setOnStartupCameraHeight(720);
        ggc.setOnStartupSpriteWidth(64);
        ggc.setOnStartupSpriteHeight(64);
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


    public int getRows() { return getIntValue(MAP_GENERATION_ROWS); }
    public GameConfigs setMapGenerationRows(int tileMapRows) {
        put(MAP_GENERATION_ROWS, tileMapRows);
        return this;
    }

    public int getColumns() { return getIntValue(MAP_GENERATION_COLUMNS); }
    public GameConfigs setMapGenerationColumns(int tileMapColumns) {
        put(MAP_GENERATION_COLUMNS, tileMapColumns);
        return this;
    }

    public int setMapGenerationTerrainMinimumElevation() { return getIntValue(MAP_GENERATION_MIN_HEIGHT); }
    public GameConfigs setMapGenerationTerrainMinimumElevation(int minHeight) {
        put(MAP_GENERATION_MIN_HEIGHT, minHeight);
        return this;
    }

    public int getMapGenerationTerrainMaximumElevation() { return getIntValue(MAP_GENERATION_MAX_HEIGHT); }
    public GameConfigs setMapGenerationTerrainMaximumElevation(int maxHeight) {
        put(MAP_GENERATION_MAX_HEIGHT, maxHeight);
        return this;
    }

    public float getMapGenerationNoiseZoom() { return getFloat(MAP_GENERATION_NOISE_ZOOM); }
    public GameConfigs setMapGenerationNoiseZoom(float zoom) {
        put(MAP_GENERATION_NOISE_ZOOM, zoom);
        return this;
    }

    public int getLiquidLevel() { return getIntValue(MAP_GENERATION_WATER_LEVEL); }
    public GameConfigs setMapGenerationLiquidElevation(int height) {
        put(MAP_GENERATION_WATER_LEVEL, height);
        return this;
    }
    public String getLiquidAsset() { return getString(MAP_GENERATION_WATER_ASSET); }
    public GameConfigs setMapGenerationLiquidAsset(String asset) {
        put(MAP_GENERATION_WATER_ASSET, asset);
        return this;
    }

    public String getMapGenerationTerrainAsset() { return getString(MAP_GENERATION_TERRAIN_ASSET); }
    public GameConfigs setMapGenerationTerrainAsset(String terrain) {
        put(MAP_GENERATION_TERRAIN_ASSET, terrain);
        return this;
    }


    public List<String> getStructureAssets() {
        return getJSONArray(MAP_GENERATION_STRUCTURE_ASSETS)
                .stream()
                .map(Object::toString)
                .toList();
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




    public int getOnStartupCameraWidth() { return getIntValue(ON_STARTUP_CAMERA_WIDTH); }
    public GameConfigs setOnStartupCameraWidth(int value) { put(ON_STARTUP_CAMERA_WIDTH, value); return this; }

    public int getOnStartupCameraHeight() { return getIntValue(ON_STARTUP_CAMERA_HEIGHT); }
    public GameConfigs setOnStartupCameraHeight(int value) { put(ON_STARTUP_CAMERA_HEIGHT, value); return this; }













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
