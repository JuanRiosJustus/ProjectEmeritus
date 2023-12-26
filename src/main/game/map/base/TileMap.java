package main.game.map.base;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import main.constants.ColorPalette;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.map.builders.utils.TileMapLayer;
import main.game.stores.factories.TileFactory;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.MathUtils;
import main.utils.noise.SimplexNoise;

import java.awt.Color;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class TileMap implements Serializable {

    public static final String ROWS = "rows", COLUMNS = "columns", FLOOR = "floor", WALL = "wall",
            LIQUID = Tile.LIQUID, CURRENT_SEED = "current_seed", PREVIOUS_SEED = "previous_seed",
            ZOOM = "zoom", ALGORITHM = "algorithm", STRUCTURES = "structures",
            WATER_LEVEL = "waterLevel", MIN_HEIGHT = "minHeight", MAX_HEIGHT = "maxHeight",
            OBSTRUCTION = Tile.OBSTRUCTION,
            DESTROYABLE_BLOCKER = Tile.OBSTRUCTION_DESTROYABLE_BLOCKER, ROUGH_TERRAIN = Tile.OBSTRUCTION_ROUGH_TERRAIN,
            ENTRANCE_STRUCTURE = "entrance_structure", EXIT_STRUCTURE = "exit_structure";
    protected static final String COLLIDER_LAYER = "collider_layer", HEIGHT_LAYER = "height_layer",
            LIQUID_LAYER = "liquid_layer", TERRAIN_LAYER = "terrain_layer";
    protected static final ELogger mLogger = ELoggerFactory.getInstance().getELogger(TileMapBuilder.class);
    public Entity[][] mRawMap;
    protected final Random mRandom = new Random();
    protected Map<String, Object> mConfiguration = new HashMap<>();
    protected Map<String, TileMapLayer> mTileMapLayers = new HashMap<>();
    public TileMap(Map<String, Object> configuration) {
        mConfiguration = configuration;
    }
    public TileMap(Entity[][] map) {
        mRawMap = map;
        TileMapBuilder.placeShadows(this);
    }
    public TileMap(JsonArray array) {
        mRawMap = fromJson(array);
        TileMapBuilder.placeShadows(this);
    }

    public static TileMap createRandom(int rows, int columns) {
        return TileMapBuilder.createRandom(rows, columns);
    }

    public static TileMap create(Map<String, Object> configuration) {
        return TileMapBuilder.create(configuration);
    }

    public void init() {
        mLogger.info("Started initializing TileMap!");

        if (mConfiguration.containsKey(PREVIOUS_SEED)) {
            mConfiguration.put(PREVIOUS_SEED, mConfiguration.get(CURRENT_SEED));
            mConfiguration.put(CURRENT_SEED, mRandom.nextLong());
        } else {
            mConfiguration.put(PREVIOUS_SEED, 0L);
        }

        long seed = (long) mConfiguration.get(CURRENT_SEED);
        mRandom.setSeed(seed);

        int rows = (int) mConfiguration.get(ROWS);
        int columns = (int) mConfiguration.get(COLUMNS);
        float zoom = (float) mConfiguration.get(ZOOM);
        int minHeight = (int) mConfiguration.get(MIN_HEIGHT);
        int maxHeight = (int) mConfiguration.get(MAX_HEIGHT);
        int liquidLevel = getLiquidLevel(minHeight, maxHeight);

        mConfiguration.put(WATER_LEVEL, liquidLevel);

        mTileMapLayers.put(COLLIDER_LAYER, new TileMapLayer(rows, columns));
        mTileMapLayers.put(HEIGHT_LAYER, new TileMapLayer(rows, columns));
        mTileMapLayers.put(LIQUID_LAYER, new TileMapLayer(rows, columns));
        mTileMapLayers.put(TERRAIN_LAYER, new TileMapLayer(rows, columns));

        applySimplexNoise(mTileMapLayers.get(HEIGHT_LAYER), minHeight, maxHeight, zoom);
        mLogger.info("Finished initializing TileMap!");
    }

    public void commit() {
        mLogger.info("Started committing final changes to TileMap!");
        TileMapBuilder.placeTerrain(this);
        TileMapBuilder.placeLiquids(this);
        mLogger.info("Finished committing final changes to TileMap!");
    }

    public void push() {
        mLogger.info("Starting to pushing changes for TileMap!");
        TileMapLayer colliderMap = getColliderLayer();
        TileMapLayer heightMap = getHeightLayer();
        TileMapLayer liquidMap = getLiquidLayer();
        TileMapLayer terrainMap = getTerrainLayer();

        int rows = (int) mConfiguration.get(ROWS);
        int columns = (int) mConfiguration.get(COLUMNS);
        Entity[][] entityMap = new Entity[rows][columns];

        for (int row = 0; row < entityMap.length; row++) {
            for (int column = 0; column < entityMap[row].length; column++) {

                Entity entity = TileFactory.create(row, column);
                entityMap[row][column] = entity;

                Tile details = entity.get(Tile.class);

                int collider = colliderMap.isUsed(row, column) ? 0 : -1;
                int height = heightMap.get(row, column);
                int terrain = terrainMap.get(row, column);
                int liquid = liquidMap.get(row, column);

                details.encode(collider, height, terrain, liquid);
            }
        }

        mRawMap = entityMap;
        TileMapBuilder.placeShadows(this);
        mLogger.info("Starting to pushing changes for TileMap!");
    }


    private static void applySimplexNoise(TileMapLayer tml, int minHeight, int maxHeight, float zoom) {
        SimplexNoise simplexNoise = new SimplexNoise();
        double[][] map = simplexNoise.get2DNoiseMap(tml.getRows(), tml.getColumns(), zoom);
        for (int row = 0; row < map.length; row++) {
            for (int column = 0; column < map[row].length; column++) {
                double val = map[row][column];
                int mapped = (int) MathUtils.map((float) val, 0, 1, minHeight, maxHeight);
                tml.set(row, column, mapped);
            }
        }
    }

    private static int getLiquidLevel(int min, int max) {
        int distanceBetweenLimits = max - min;
        int quarterOfDistance = distanceBetweenLimits / 4;
        return  min + quarterOfDistance;
    }

    public Entity tryFetchingTileAt(int row, int column) {
        if (row < 0 || column < 0 || row >= mRawMap.length || column >= mRawMap[row].length) {
            return null;
        } else {
            return mRawMap[row][column];
        }
    }
    public Entity getNaivelyRandomTile() {
        int row = mRandom.nextInt(mRawMap.length);
        int column = mRandom.nextInt(mRawMap[row].length);
        return mRawMap[row][column];
    }

    public int[] getRandomRowColumn() {
        int row = mRandom.nextInt(mRawMap.length);
        int column = mRandom.nextInt(mRawMap[row].length);
        return new int[]{ row, column };
    }

    public JsonArray toJson() {
        JsonArray rows = new JsonArray();
        // Add all cells of the tile to a json structure
        for (Entity[] row : mRawMap) {
            JsonArray jsonArray = new JsonArray();
            for (Entity column : row) {
                Tile tile = column.get(Tile.class);
                jsonArray.add(tile.toJson());
            }
            rows.add(jsonArray);
        }
       return rows;
    }

    private Entity[][] fromJson(JsonArray tileMapJson) {
        Entity[][] newEntityArray = new Entity[tileMapJson.size()][];
        for (int row = 0; row < tileMapJson.size(); row++) {
            JsonArray jsonRow = (JsonArray) tileMapJson.get(row);
            Entity[] entityRowArray = new Entity[jsonRow.size()];
            for (int column = 0; column < jsonRow.size(); column++) {
                Entity entity = TileFactory.create(row, column);
                Tile tile = entity.get(Tile.class);
                JsonObject tileObject = (JsonObject) jsonRow.get(column);
                tile.fromJson(tileObject);
                entityRowArray[column] = entity;
            }
            newEntityArray[row] = entityRowArray;
        }
        return newEntityArray;
    }

    public void saveToFile() {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("HH-mm");
            String fileName = LocalDate.now() + "-" + formatter.format(new Date()) + ".json";
            PrintWriter out = new PrintWriter(new FileWriter(fileName, false), true);
            out.write(toJson().toJson());
            out.close();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public void place(Entity entity, int[] location) {
        // Get the tile to place the entity on
        Tile tile = mRawMap[location[0]][location[1]].get(Tile.class);
        if (tile.isNotNavigable()) { return; }

        tile.setUnit(entity);
    }

    public boolean placeByDivision(int slices, int location, List<Entity> team) {
        return placeByDivision(slices, location, team, true);
    }

    public boolean placeByDivision(int slices, int location, List<Entity> team, boolean randomized) {

        mLogger.info("Placing Entities onto tile map");
        // Divide map by NxN
        Map<Integer, List<Entity>> areas = getAreas(slices);

        List<Entity> area = areas.get(location);
        if (team.size() > area.size()) { return false; }

        if (randomized) {
            for (Entity value : team) {
                Tile tile = area.get(mRandom.nextInt(area.size())).get(Tile.class);
                while (tile.isNotNavigable()) {
                    tile = area.get(mRandom.nextInt(area.size())).get(Tile.class);
                }
                tile.setUnit(value);
            }
        } else {
            int unit = 0;
            for (Entity entity : area) {
                Tile tile = entity.get(Tile.class);

                if (tile.isNotNavigable()) { continue; }
                if (unit >= team.size()) { continue; }

                tile.setUnit(team.get(unit));
                unit++;
            }
        }
        mLogger.info("Finished Placing Entities onto tile map");
        return true;
    }

    private Map<Integer, List<Entity>> getAreas(int slices) {
        Map<Integer, List<Entity>> areas = new LinkedHashMap<>();

        for (int row = 0; row < slices; row++) {
            for (int column = 0; column < slices; column++) {
                int startRow = row * (mRawMap.length / slices);
                int endRow = (row + 1) * (mRawMap.length / slices);
                int startColumn = column * (mRawMap[startRow].length / slices);
                int endColumn = (column + 1) * (mRawMap[startRow].length / slices);

                Color c = ColorPalette.getRandomColorWithAlpha();
                List<Entity> area = new ArrayList<>();
                for (int innerRow = startRow; innerRow < endRow; innerRow++) {
                    for (int innerColumn = startColumn; innerColumn < endColumn; innerColumn++) {

                        Entity entity = mRawMap[innerRow][innerColumn];
                        area.add(entity);

                        Tile tile = entity.get(Tile.class);
                        tile.setProperty(Tile.SPAWN, c);
                    }
                }
                areas.put(areas.size(), area);
            }
        }

        return areas;
    }

    public int getRows() { return mRawMap.length; }
    public int getColumns(int row) { return mRawMap[row].length; }
    public int getColumns() { return getColumns(0); }

    public TileMapLayer getColliderLayer() { return mTileMapLayers.get(COLLIDER_LAYER); }
    public TileMapLayer getHeightLayer() { return mTileMapLayers.get(HEIGHT_LAYER); }
    public TileMapLayer getLiquidLayer() { return mTileMapLayers.get(LIQUID_LAYER); }
    public TileMapLayer getTerrainLayer() { return mTileMapLayers.get(TERRAIN_LAYER); }

    public int getFloor() { return (int) mConfiguration.get(FLOOR); }
    public int getWall() { return (int) mConfiguration.get(WALL); }
    public int getLiquid() { return (int) mConfiguration.get(LIQUID); }
    public int getWaterLevel() { return (int) mConfiguration.get(WATER_LEVEL); }
    public long getSeed() { return (long) mConfiguration.get(CURRENT_SEED); }
    public int getStructureConfiguration() { return (int) mConfiguration.get(OBSTRUCTION); }
    public Object getConfiguration(String config) { return mConfiguration.get(config); }
}
