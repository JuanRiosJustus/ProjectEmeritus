package main.game.map.base;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.map.builders.utils.TileMapLayer;
import main.json.JsonSerializable;
import main.game.stores.factories.TileFactory;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.MathUtils;
import main.utils.noise.SimplexNoise;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class TileMap extends JsonSerializable {

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
    private Entity[][] mRawMap;
    private final Random mRandom = new Random();
    private Map<String, Object> mConfiguration = new HashMap<>();
    private final Map<String, TileMapLayer> mTileMapLayers = new HashMap<>();
    public TileMap(Map<String, Object> configuration) {
        mConfiguration = configuration;
    }
    public TileMap() {}

    public TileMap(Entity[][] map) {
        mRawMap = map;
        TileMapBuilder.placeShadows(this);
    }

    public TileMap(JsonObject jsonObject) {
        mRawMap = fromJson(jsonObject);
        TileMapBuilder.placeShadows(this);
    }

    public static TileMap createRandom(int rows, int columns) {
        return TileMapBuilder.createRandom(rows, columns);
    }

    public static TileMap create(Map<String, Object> configuration) {
        return TileMapBuilder.create(configuration);
    }
    public void addShadowEffect() {
        TileMapBuilder.placeShadows(this);
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

        mTileMapLayers.put(COLLIDER_LAYER, new TileMapLayer(COLLIDER_LAYER, rows, columns));
        mTileMapLayers.put(HEIGHT_LAYER, new TileMapLayer(HEIGHT_LAYER, rows, columns));
        mTileMapLayers.put(LIQUID_LAYER, new TileMapLayer(LIQUID_LAYER, rows, columns));
        mTileMapLayers.put(TERRAIN_LAYER, new TileMapLayer(TERRAIN_LAYER, rows, columns));

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

                Tile tile = entity.get(Tile.class);

                String collider = colliderMap.get(row, column);
                String height = heightMap.get(row, column);
                String terrain = terrainMap.get(row, column);
                String liquid = liquidMap.get(row, column);

                tile.encode(collider, height, terrain, liquid, null);
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
                tml.set(row, column, String.valueOf(mapped));
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

    @Override
    public JsonObject toJsonObject() {
        JsonArray rows = new JsonArray();
        // Add all cells of the tile to a json structure
        for (Entity[] row : mRawMap) {
            JsonArray jsonArray = new JsonArray();
            for (Entity column : row) {
                Tile tile = column.get(Tile.class);
                jsonArray.add(tile.asJson());
            }
            rows.add(jsonArray);
        }
        mJsonData.put(getClass().getSimpleName().toLowerCase(Locale.ROOT), rows);
        return mJsonData;
    }

//    public JsonArray asJson() {
//        JsonArray rows = new JsonArray();
//        // Add all cells of the tile to a json structure
//        for (Entity[] row : mRawMap) {
//            JsonArray jsonArray = new JsonArray();
//            for (Entity column : row) {
//                Tile tile = column.get(Tile.class);
//                jsonArray.add(tile.asJson());
//            }
//            rows.add(jsonArray);
//        }
//       return rows;
//    }

    private Entity[][] fromJson(JsonObject jsonObject) {
        JsonArray tileMapJson = (JsonArray) jsonObject.get(getClass().getSimpleName().toLowerCase(Locale.ROOT));
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
            var r = toJsonObject();
//            out.write(toJsonObject().toJson());
//            out.close();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public void place(Entity entity, int[] location) {
        place(entity, location[0], location[1]);
    }

    public void place(Entity entity, int row, int column) {
        // Get the tile to place the entity on
        Tile tile = mRawMap[row][column].get(Tile.class);
        if (tile.isNotNavigable()) { return; }

        tile.setUnit(entity);
    }

    public void placeByAxis(boolean horizontal, List<Entity> leftOrTopTeam, List<Entity> rightOrBottom, int spawnSize) {
        // split the map into regions.This will look odd when the map cannot be devided evenly
        // left most and right most
        List<Entity> leftOrTopSpawns = new ArrayList<>();
        List<Entity> rightOrBottomSpawns = new ArrayList<>();
        for (int row = 0; row < mRawMap.length; row++) {
            for (int column = 0; column < mRawMap[row].length; column++) {
                // Left most team
                Entity tileEntity = mRawMap[row][column];
                Tile tile = tileEntity.get(Tile.class);
                if (horizontal) {
                    if (column < spawnSize) {
                        leftOrTopSpawns.add(tileEntity);
                        tile.setSpawnRegion(1);
                    } else if (column >= mRawMap[row].length - spawnSize) {
                        rightOrBottomSpawns.add(tileEntity);
                        tile.setSpawnRegion(2);
                    }
                } else {
                    if (row < spawnSize) {
                        leftOrTopSpawns.add(tileEntity);
                        tile.setSpawnRegion(1);
                    } else if (row >= mRawMap.length - spawnSize) {
                        rightOrBottomSpawns.add(tileEntity);
                        tile.setSpawnRegion(2);
                    }
                }
            }
        }
    }
    public void placeGroupVsGroup(List<Entity> team1, List<Entity> team2) {
        placeGroupVsGroup(team1, team2, true);
    }

    public void placeGroupVsGroup(List<Entity> team1, List<Entity> team2, boolean ignoreObstructed) {
        // split the map into regions. This will look odd when the map cannot be devided evenly
        List<List<List<Entity>>> regionMap = tryCreatingRegions(3, ignoreObstructed);
        // Set team spawns in West most regions and East most regions OR
        // set the spawns in North most regions and South most regions
        List<List<Entity>> spawns = tryGettingOpposingRegions(regionMap, true);
        Queue<Entity> queue;

        if (team1 != null) {
            queue = new LinkedList<>(team1);
            while (!queue.isEmpty()) {
                Entity unitEntity = queue.poll();
                Entity tileEntity = spawns.get(0).get(mRandom.nextInt(spawns.get(0).size()));
                Tile tile = tileEntity.get(Tile.class);
                while (tile.isNotNavigable()) {
                    tileEntity = spawns.get(0).get(mRandom.nextInt(spawns.get(0).size()));
                    tile = tileEntity.get(Tile.class);
                }
                tile.setUnit(unitEntity);
            }
        }

        if (team2 != null) {
            queue = new LinkedList<>(team2);
            while (!queue.isEmpty()) {
                Entity unitEntity = queue.poll();
                Entity tileEntity = spawns.get(spawns.size() - 1).get(mRandom.nextInt(spawns.get(spawns.size() - 1).size()));
                Tile tile = tileEntity.get(Tile.class);
                while (tile.isNotNavigable()) {
                    tileEntity = spawns.get(spawns.size() - 1).get(mRandom.nextInt(spawns.get(spawns.size() - 1).size()));
                    tile = tileEntity.get(Tile.class);
                }
                tile.setUnit(unitEntity);
            }
        }
    }

    private List<List<Entity>> tryGettingOpposingRegions(List<List<List<Entity>>> regionMap, boolean useColumns) {
        List<Entity> side1 = new ArrayList<>();
        List<Entity> side2 = new ArrayList<>();
        int allocation = 1;
        int regionVal = 1;
        // column wise only atm
        for (int row = 0; row < regionMap.size(); row++) {
            for (int column = 0; column < regionMap.get(row).size(); column++) {
                if (useColumns) {
                    // The region
                    if (column < allocation) {
                        List<Entity> region = regionMap.get(row).get(column);
                        for (Entity entity : region) {
                            Tile tile = entity.get(Tile.class);
                            tile.setSpawnRegion(regionVal);
                            side1.add(entity);
                        }
                    } else if (column > regionMap.get(row).size() - allocation - 1) {
                        List<Entity> region = regionMap.get(row).get(column);
                        for (Entity entity : region) {
                            Tile tile = entity.get(Tile.class);
                            tile.setSpawnRegion(regionVal + 1);
                            side2.add(entity);
                        }
                    }
                } else {
                    // The region
                    if (row < allocation) {
                        List<Entity> region = regionMap.get(row).get(column);
                        for (Entity entity : region) {
                            Tile tile = entity.get(Tile.class);
                            tile.setSpawnRegion(regionVal);
                            side1.add(entity);
                        }
                    } else if (row > regionMap.get(row).size() - allocation - 1) {
                        List<Entity> region = regionMap.get(row).get(column);
                        for (Entity entity : region) {
                            Tile tile = entity.get(Tile.class);
                            tile.setSpawnRegion(regionVal + 1);
                            side2.add(entity);
                        }
                    }
                }
            }
        }
        List<List<Entity>> oppositions = new ArrayList<>();
        oppositions.add(side1);
        oppositions.add(side2);
        return oppositions;
    }

    public boolean placeByDivision(int slices, int location, List<Entity> team) {
        return placeByDivision(slices, location, team, true);
    }

    public boolean placeByDivision(int slices, int location, List<Entity> team, boolean randomized) {

        mLogger.info("Placing Entities onto tile map");
        // Divide map by NxN
//        Map<Integer, List<Entity>> areas = tryCollectingRegions(slices);
//
//        List<Entity> area = areas.get(location);
//        if (team.size() > area.size()) { return false; }
//
//        if (randomized) {
//            for (Entity value : team) {
//                Tile tile = area.get(mRandom.nextInt(area.size())).get(Tile.class);
//                while (tile.isNotNavigable()) {
//                    tile = area.get(mRandom.nextInt(area.size())).get(Tile.class);
//                }
//                tile.setUnit(value);
//            }
//        } else {
//            int unit = 0;
//            for (Entity entity : area) {
//                Tile tile = entity.get(Tile.class);
//
//                if (tile.isNotNavigable()) { continue; }
//                if (unit >= team.size()) { continue; }
//
//                tile.setUnit(team.get(unit));
//                unit++;
//            }
//        }
        mLogger.info("Finished Placing Entities onto tile map");
        return true;
    }

    /**
     *
     * rowsByColumns = 1
     *  0   |   1
     *  ----+-----
     *  2   |   3
     *  ==================================
     * rowsByColumns = 2
     *  0   |   1    |   2
     *  ----+--------+-----
     *  3   |   4    |   6
     *  ----+--------+-----
     *  6   |   7    |   8
     *
     */

    private List<List<List<Entity>>> tryCreatingRegions(int slicesPerAxis, boolean ignoreObstructed) {

        List<List<List<Entity>>> regions = new ArrayList<>();

        if (slicesPerAxis <= 0) { return regions; }

        slicesPerAxis++;

        int tileRowsPerRegion = mRawMap.length / slicesPerAxis;
        int leftoverRowValue = (mRawMap.length % slicesPerAxis != 0 ? 1 : 0);

        for (int row = 0; row < mRawMap.length; row += tileRowsPerRegion + leftoverRowValue) {
            List<List<Entity>> rowList = new ArrayList<>();
            int tileColumnsPerRegion = mRawMap[row].length / slicesPerAxis;
            int leftoverColumnValue = (mRawMap[row].length % slicesPerAxis != 0 ? 1 : 0);

            for (int column = 0; column < mRawMap[row].length; column += tileColumnsPerRegion + leftoverColumnValue) {
                int regionEndRow = row + tileRowsPerRegion + leftoverRowValue;
                int regionEndColumn = column + tileColumnsPerRegion + leftoverColumnValue;
                List<Entity> area = new ArrayList<>();

                for (int regionRow = row; regionRow < regionEndRow; regionRow++) {
                    for (int regionColumn = column; regionColumn < regionEndColumn; regionColumn++) {
                        Entity entity = tryFetchingTileAt(regionRow, regionColumn);
                        if (entity == null) { continue; }
                        Tile tile = entity.get(Tile.class);
                        area.add(entity);
                        if (ignoreObstructed && tile.isNotNavigable()) { continue; }
                    }
                }
                // This means that the regions are way too small
                if (area.isEmpty()) { return regions; }

                rowList.add(area);
            }
            regions.add(rowList);
        }
        return regions;
    }

    public int getRows() { return mRawMap.length; }
    public int getColumns(int row) { return mRawMap[row].length; }
    public int getColumns() { return getColumns(0); }

    public TileMapLayer getColliderLayer() { return mTileMapLayers.get(COLLIDER_LAYER); }
    public TileMapLayer getHeightLayer() { return mTileMapLayers.get(HEIGHT_LAYER); }
    public TileMapLayer getLiquidLayer() { return mTileMapLayers.get(LIQUID_LAYER); }
    public TileMapLayer getTerrainLayer() { return mTileMapLayers.get(TERRAIN_LAYER); }

//    public int getFloor() { return (int) mConfiguration.get(FLOOR); }
//    public int getWall() { return (int) mConfiguration.get(WALL); }
//    public int getLiquid() { return (int) mConfiguration.get(LIQUID); }
    public String getFloor() { return (String) mConfiguration.get(FLOOR); }
    public String getWall() { return (String) mConfiguration.get(WALL); }
    public String getLiquid() { return (String) mConfiguration.get(LIQUID); }
    public int getWaterLevel() { return (int) mConfiguration.get(WATER_LEVEL); }
    public long getSeed() { return (long) mConfiguration.get(CURRENT_SEED); }
    public int getStructureConfiguration() { return (int) mConfiguration.get(OBSTRUCTION); }
    public Object getConfiguration(String config) { return mConfiguration.get(config); }
    public Entity[][] getRawTileMap() { return mRawMap; }
}
