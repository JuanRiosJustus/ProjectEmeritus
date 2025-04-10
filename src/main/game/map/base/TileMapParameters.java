package main.game.map.base;

import main.constants.Constants;
import main.graphics.AssetPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TileMapParameters {
    public static final String ALGORITHM_KEY = "algorithm";
    public static final String ROWS_KEY = "rows";
    public static final String COLUMNS_KEY = "columns";
    public static final String WALL_KEY = "wall";
    public static final String LIQUID_KEY = "liquid";
    public static final String FLOOR_KEY = "floor";
    public static final String SPRITEMAP_KEY = "sprite_map";
    public static final String NOISE_ZOOM_KEY = "noise_zoom";
    public static final String MIN_TERRAIN_HEIGHT_KEY = "min_terrain_height";
    public static final String MAX_TERRAIN_HEIGHT_KEY = "max_terrain_height";
    public static final String SEED_KEY = "generation_seed";
    public static final String WATER_LEVEL_KEY = "water_level";

    private final Map<String, Object> mTileMapConfigs = new HashMap<>();

    private TileMapParameters() {}

    public static TileMapParameters getDefaultParameters(int rows, int columns) {
        return getDefaultParameters(rows, columns, Constants.TILES_SPRITEMAP_FILEPATH);
    }
    public static TileMapParameters getDefaultParameters(int rows, int columns, String spriteMap) {
        Random random = new Random();

        List<String> list = AssetPool.getInstance().getBucket(AssetPool.WALL_TILES);
        String wall = list.get(random.nextInt(list.size()));

        list = AssetPool.getInstance().getBucket(AssetPool.FLOOR_TILES);
        String floor = list.get(random.nextInt(list.size()));

        list = AssetPool.getInstance().getBucket(AssetPool.LIQUIDS_TILES);
        String liquid = list.get(random.nextInt(list.size()));

        TileMapParameters tileMapParameters = TileMapParameters.getBuilder()
                .put(TileMapParameters.ROWS_KEY, rows)
                .put(TileMapParameters.COLUMNS_KEY, columns)
                .put(TileMapParameters.WALL_KEY, wall)
                .put(TileMapParameters.FLOOR_KEY, floor)
                .put(TileMapParameters.LIQUID_KEY, liquid)
                .put(TileMapParameters.SEED_KEY, new Random().nextLong())
                .put(TileMapParameters.NOISE_ZOOM_KEY, .8f)
                .put(TileMapParameters.MAX_TERRAIN_HEIGHT_KEY, 5)
                .put(TileMapParameters.WATER_LEVEL_KEY, 0)
                .put(TileMapParameters.MIN_TERRAIN_HEIGHT_KEY, -5);

        return tileMapParameters;
    }

    public static TileMapParameters getBuilder() {
        return new TileMapParameters();
    }

    public TileMapParameters put(String key, Object value) {
        mTileMapConfigs.put(key, value);
        return this;
    }

    public Object get(String key) { return mTileMapConfigs.get(key); }

    public int size() { return mTileMapConfigs.size(); }

    public Map<String, Object> build() {
        return mTileMapConfigs;
    }
}
