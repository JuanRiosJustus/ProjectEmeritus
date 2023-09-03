package main.game.map.builders;

import main.game.components.Tile;
import main.game.entity.Entity;
import main.game.map.TileMap;
import main.game.map.builders.utils.TileMapLayer;
import main.game.stores.factories.TileFactory;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.MathUtils;
import main.utils.NoiseGenerator;

import java.util.*;

public abstract class TileMapBuilder {

    protected static ELogger logger = ELoggerFactory.getInstance().getELogger(TileMapBuilder.class);
    protected Random random = new Random();
    protected boolean isPathMapCompletelyConnected = false;
    protected final Map<String, TileMapLayer> layers = new HashMap<>();
    protected static final String PATH_LAYER = "path_layer";
    protected static final String HEIGHT_LAYER = "height_layer";
    protected static final String LIQUID_LAYER = "liquid_layer";
    protected static final String STRUCTURE_LAYER = "structure_layer";
    protected static final String EXIT_LAYER = "exit_layer";
    private String path = "";
    private float zoom = -1;
    private int floor = -1;
    private int wall = -1;
    private int structure = -1;
    private int liquid = -1;
    private int rows = -1;
    private int columns = -1;
    private long seed = -1;
    private int exit = -1;
    protected int seaLevel = -1;
    public TileMapBuilder setPath(String value) { path = value; return this; }
    public TileMapBuilder setZoom(float value) { zoom = value; return this; }
    public TileMapBuilder setFlooring(int value) { floor = value; return this; }
    public TileMapBuilder setWalling(int value) { wall = value; return this; }
    public TileMapBuilder setStructure(int value) { structure = value; return this; }
    public TileMapBuilder setLiquid(int value) { liquid = value; return this; }
    public TileMapBuilder setSeed(long value) { seed = value; return this; }
    public TileMapBuilder setRows(int value) { rows = value; return this; }
    public TileMapBuilder setColumns(int value) { columns = value; return this; }
    public TileMapBuilder setExiting(int value) { exit = value; return this; }
    public TileMapBuilder setRowAndColumn(int rows, int columns) {  setRows(rows); setColumns(columns);  return this; }
    public int getFloor() { return floor; }
    public int getWall() { return wall; }
    public int getStructure() { return structure; }
    public int getLiquid() { return liquid; }
    public long getSeed() { return seed; }
    public int getRows() { return rows; }
    public int getColumns() { return columns; }
    public float getZoom() { return zoom; }
    public int getExit() { return exit; }

    public abstract TileMap build();
    public int getSeaLevel() { return seaLevel; }

    public boolean isUsed(int row, int column) {
        boolean isPath = getPathLayer().isUsed(row, column);
        if (!isPath) { return true; }
        boolean isStructure = getStructureLayer().isUsed(row, column);
//        boolean isWall = getWallLayer().isUsed(row, column);
        boolean isLiquid = getLiquidLayer().isUsed(row, column);
        boolean isExit = getExitMapLayer().isUsed(row, column);
        return isStructure || isLiquid || isExit;
    }


    // public static TileMap fromJson(String path) {
    //     ELogger logger = ELoggerFactory.getInstance().getELogger(TileMapGenerator.class);
    //     try {
    //         Reader reader = Files.newBufferedReader(Paths.get(path));
    //         JsonObject jsonObject = (JsonObject) Jsoner.deserialize(reader);
    //         JsonArray tilemap = (JsonArray) jsonObject.get(TileMapGenerator.class.getSimpleName());

    //         JsonArray jsonArrayRow = (JsonArray) tilemap.get(0);
    //         TileMapLayer pathMap = new TileMapLayer(tilemap.size(), jsonArrayRow.size());
    //         TileMapLayer heightMap = new TileMapLayer(tilemap.size(), jsonArrayRow.size());
    //         TileMapLayer terrainMap = new TileMapLayer(tilemap.size(), jsonArrayRow.size());
    //         TileMapLayer liquidMap = new TileMapLayer(tilemap.size(), jsonArrayRow.size());
    //         TileMapLayer structureMap = new TileMapLayer(tilemap.size(), jsonArrayRow.size());

    //         Entity[][]raw = new Entity[tilemap.size()][];
    //         for (int row = 0; row < tilemap.size(); row++) {
    //             jsonArrayRow = (JsonArray) tilemap.get(row);
    //             raw[row] = new Entity[jsonArrayRow.size()];
    //             for (int column = 0; column < jsonArrayRow.size(); column++) {
    //                 JsonArray tileJson = (JsonArray) jsonArrayRow.get(column);
    //                 Entity entity = TileFactory.create(row, column);
    //                 raw[row][column] = entity;
    //                 Tile tile = entity.get(Tile.class);
    //                 int[] encoding = new int[tileJson.size()];
    //                 for (int i = 0; i < encoding.length; i++) {encoding[i] = tileJson.getInteger(i); }
    //                 tile.encode(encoding);
    //             }
    //         }
    //         logger.info("Finished deserializing tilemap");
    //         return TileMapGenerator.createTileMap(pathMap, heightMap, terrainMap, liquidMap, structureMap);
    //     } catch (Exception ex) {
    //         logger.info("Unable to deserialize Json for tilemap");
    //     }
    //     return null;
    // }
        
    protected void createSchemaMaps() {
        logger.info("Creating Schema Maps");
        random.setSeed(seed);
         
        layers.put(PATH_LAYER, new TileMapLayer(rows, columns));
        layers.put(HEIGHT_LAYER, new TileMapLayer(rows, columns));
        layers.put(LIQUID_LAYER, new TileMapLayer(rows, columns));
        layers.put(STRUCTURE_LAYER, new TileMapLayer(rows, columns));
        layers.put(EXIT_LAYER, new TileMapLayer(rows, columns));

        int min = 0, max = 10;
        setupHeightMap(min, max, zoom == 0 ? .2f : zoom);
        setupSeaLevel(min, max);
        logger.info("Finished creating schema maps");
    }

    public TileMapLayer getPathLayer() { return layers.get(PATH_LAYER); }
    public TileMapLayer getHeightLayer() { return layers.get(HEIGHT_LAYER); }
    public TileMapLayer getLiquidLayer() { return layers.get(LIQUID_LAYER); }
    public TileMapLayer getStructureLayer() { return layers.get(STRUCTURE_LAYER); }
    public TileMapLayer getExitMapLayer() { return layers.get(EXIT_LAYER); }
    public Random getRandom() { return random; }

    protected TileMap createTileMap() {

        TileMapLayer pathMap = getPathLayer();
        TileMapLayer heightMap = getHeightLayer();
        TileMapLayer liquidMap = getLiquidLayer();
        TileMapLayer structureMap = getStructureLayer();
        TileMapLayer exitMap = getExitMapLayer();

        Entity[][] tileMap = new Entity[pathMap.getRows()][pathMap.getColumns()];

        for (int row = 0; row < tileMap.length; row++) {
            for (int column = 0; column < tileMap[row].length; column++) {

                Entity entity = TileFactory.create(row, column);
                
                tileMap[row][column] = entity;

                Tile details = entity.get(Tile.class);

                int path = pathMap.isUsed(row, column) ? 0 : -1;
                int height = heightMap.get(row, column);
                int terrain = pathMap.isUsed(row, column) ? getFloor() : getWall();
                int liquid = liquidMap.get(row, column);
                int structure = structureMap.get(row, column);
                int exit = exitMap.get(row, column);

                details.encode(path, height, terrain, liquid, structure, exit);
            }
        }

        return new TileMap(tileMap);
    }

    protected void setupHeightMap(int min, int max, float zoom) {
        TileMapLayer heightMap = getHeightLayer();
        double[][] map = NoiseGenerator.generateSimplexNoiseV2(
            getRandom(), heightMap.getRows(), heightMap.getColumns(), getZoom()
        );
        for (int row = 0; row < map.length; row++) {
            for (int column = 0; column < map[row].length; column++) {
                double val = map[row][column];
                int mapped = (int) MathUtils.map((float) val, 0, 1, min, max);
                heightMap.set(row, column, mapped);
            }
        }
    }

    protected void setupSeaLevel(int min, int max) {
        int distanceBetweenLimits = max - min;
        int quarterOfDistance = distanceBetweenLimits / 4;
        seaLevel = min + quarterOfDistance;
    }

    protected void generateNewSeed() {
        long newSeed = random.nextLong();
        setSeed(newSeed);
    }
}
