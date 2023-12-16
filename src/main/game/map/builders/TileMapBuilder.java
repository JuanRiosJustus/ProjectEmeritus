package main.game.map.builders;

import main.constants.Constants;
import main.constants.Direction;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.map.TileMap;
import main.game.map.builders.utils.TileMapLayer;
import main.game.stores.factories.TileFactory;
import main.game.stores.pools.AssetPool;
import main.graphics.SpriteMap;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.MathUtils;
import main.utils.noise.SimplexNoise;

import java.awt.Point;
import java.util.*;

public abstract class TileMapBuilder {
    protected final Map<String, Object> mConfiguration;
    protected final Map<String, TileMapLayer> mLayers = new HashMap<>();
    protected final Random mRandom = new Random();
    protected static ELogger logger = ELoggerFactory.getInstance().getELogger(TileMapBuilder.class);

    public static final String ROWS = "rows", COLUMNS = "columns", FLOOR = "floor", WALL = "wall",
            LIQUID = Tile.LIQUID, SEED = "seed", ZOOM = "zoom", ALGORITHM = "algorithm", STRUCTURES = "structures",
            WATER_LEVEL = "waterLevel", MIN_HEIGHT = "minHeight", MAX_HEIGHT = "maxHeight",
            DESTROYABLE_BLOCKER = Tile.OBSTRUCTION_DESTROYABLE_BLOCKER, ROUGH_TERRAIN = Tile.OBSTRUCTION_ROUGH_TERRAIN,
            ENTRANCE_STRUCTURE = "entrance_structure", EXIT_STRUCTURE = "exit_structure";

    public TileMapBuilder(Map<String, Object> configuration) { mConfiguration = sanitize(configuration); }

    private static Map<String, Object> sanitize(Map<String, Object> configuration) {

        configuration.put(ROWS, configuration.getOrDefault(ROWS, -1));
        configuration.put(COLUMNS, configuration.getOrDefault(COLUMNS, -1));
        configuration.put(FLOOR, configuration.getOrDefault(FLOOR, -1));
        configuration.put(WALL, configuration.getOrDefault(WALL, -1));
        configuration.put(LIQUID, configuration.getOrDefault(LIQUID, -1));
        configuration.put(SEED, configuration.getOrDefault(SEED, new Random().nextLong()));
        configuration.put(ZOOM, configuration.getOrDefault(ZOOM, .75f));
        configuration.put(MIN_HEIGHT, configuration.getOrDefault(MIN_HEIGHT, 0));
        configuration.put(MAX_HEIGHT, configuration.getOrDefault(MAX_HEIGHT, 10));
        configuration.put(ALGORITHM, configuration.getOrDefault(ALGORITHM, null));
        configuration.put(STRUCTURES, configuration.getOrDefault(STRUCTURES, null));
        return configuration;
    }

    public static TileMap create(Map<String, Object> configuration) {
        sanitize(configuration);

        TileMapBuilderAlgorithm algorithm = TileMapBuilderAlgorithm.valueOf((String) configuration.get(ALGORITHM));
        TileMapBuilder builder = null;
        logger.info("Using " + algorithm.name());

        builder = new BorderedMapWithBorderedRooms(configuration);
        switch (algorithm) {
//            case BasicOpenMap -> builder = new BasicOpenMap(configuration);
//            case BorderedOpenMapWithBorderedRooms -> builder = new BorderedMapWithBorderedRooms(configuration);
//            case LargeBorderedRooms -> builder = new LargeBorderedRoom(configuration);
//            case LargeContinuousRoom -> builder = new LargeContinuousRoom(configuration);
//            case NoBorderWithSmallRooms -> builder = new NoBorderWithSmallRooms(configuration);

//            case HauberkDungeonMap -> builder = new HauberkDungeonMap(configuration);
        }

        return builder.build();
    }

    public static TileMap createRandom(int rows, int columns) {

        SpriteMap spriteMap = AssetPool.getInstance().getSpriteMap(Constants.TILES_SPRITEMAP_FILEPATH);

        Random random = new Random();

        List<String> list = spriteMap.getKeysEndingWith(TileMapBuilder.WALL);
        int wall = spriteMap.indexOf(list.get(random.nextInt(list.size())));

        list = spriteMap.getKeysEndingWith(TileMapBuilder.FLOOR);
        int floor = spriteMap.indexOf(list.get(random.nextInt(list.size())));

        list = spriteMap.getKeysEndingWith(TileMapBuilder.LIQUID);
        int liquid = spriteMap.indexOf(list.get(random.nextInt(list.size())));

        Map<String, Object> configuration = new HashMap<>();
//        configuration.put(ALGORITHM, TileMapBuilderAlgorithm.values()[random.nextInt(TileMapBuilderAlgorithm.values().length)].name());
        configuration.put(ALGORITHM, TileMapBuilderAlgorithm.LargeBorderedRooms.name());
        configuration.put(ROWS, rows);
        configuration.put(COLUMNS, columns);
        configuration.put(WALL, wall);
        configuration.put(FLOOR, floor);
        configuration.put(LIQUID, liquid);
        int randomBounds = 5;
//        randomBounds = randomBounds % 10;
        configuration.put(MAX_HEIGHT, randomBounds);
        configuration.put(MIN_HEIGHT, randomBounds * -1);

        if (random.nextBoolean()) {
            list = spriteMap.getKeysEndingWith(TileMapBuilder.ENTRANCE_STRUCTURE);
            int exit = spriteMap.indexOf(list.get(random.nextInt(list.size())));
            configuration.put(TileMapBuilder.ENTRANCE_STRUCTURE, exit);
        }

        if (random.nextBoolean()) {
            list = spriteMap.getKeysEndingWith(TileMapBuilder.EXIT_STRUCTURE);
            int exit = spriteMap.indexOf(list.get(random.nextInt(list.size())));
            configuration.put(TileMapBuilder.EXIT_STRUCTURE, exit);
        }

        if (random.nextBoolean()) {
            list = spriteMap.getKeysEndingWith(TileMapBuilder.DESTROYABLE_BLOCKER);
            int destroyableBlocker = spriteMap.indexOf(list.get(random.nextInt(list.size())));
            configuration.put(TileMapBuilder.DESTROYABLE_BLOCKER, destroyableBlocker);
        }

        if (random.nextBoolean()) {
            list = spriteMap.getKeysEndingWith(TileMapBuilder.ROUGH_TERRAIN);
            int destroyableBlocker = spriteMap.indexOf(list.get(random.nextInt(list.size())));
            configuration.put(TileMapBuilder.ROUGH_TERRAIN, destroyableBlocker);
        }

//        Map<String, Integer> obstructionMap = new HashMap<>();
//        if (random.nextBoolean()) { obstructionMap.put(Tile.OBSTRUCTION_ROUGH_TERRAIN, roughTerrain); }
//        if (random.nextBoolean()) { obstructionMap.put(Tile.OBSTRUCTION_DESTROYABLE_BLOCKER, destroyableBlocker);  }
//        configuration.put(OBSTRUCTIONS, obstructionMap);


        return create(configuration);
    }









    protected boolean isPathMapCompletelyConnected = false;
    protected static final String COLLIDER_LAYER = "path_layer";
    protected static final String HEIGHT_LAYER = "height_layer";
    protected static final String LIQUID_LAYER = "liquid_layer";
    protected static final String TERRAIN_LAYER = "terrain_layer";
    protected static final String OBSTRUCTION_LAYER = "obstruction_layer";
    protected static final String EXIT_LAYER = "exit_layer";
    private String path = "";
    public Object getConfiguration(String config) { return mConfiguration.getOrDefault(config, -1); }
    public int getFloor() { return (int) mConfiguration.get(FLOOR); }
    public int getWall() { return (int) mConfiguration.get(WALL); }
    public int getLiquid() { return (int) mConfiguration.get(LIQUID); }
    public int getWaterLevel() { return (int) mConfiguration.get(WATER_LEVEL); }

    public abstract TileMap build();


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
        
    protected void initializeMap() {
        logger.info("Started Initializing Schema Map");

        long seed = (long) mConfiguration.get(SEED);
        mRandom.setSeed(seed);

        int rows = (int) mConfiguration.get(ROWS);
        int columns = (int) mConfiguration.get(COLUMNS);
        float zoom = (float) mConfiguration.get(ZOOM);
        int minHeight = (int) mConfiguration.get(MIN_HEIGHT);
        int maxHeight = (int) mConfiguration.get(MAX_HEIGHT);

        mLayers.put(COLLIDER_LAYER, new TileMapLayer(rows, columns));
        mLayers.put(HEIGHT_LAYER, new TileMapLayer(rows, columns));
        mLayers.put(LIQUID_LAYER, new TileMapLayer(rows, columns));
        mLayers.put(TERRAIN_LAYER, new TileMapLayer(rows, columns));

        mLayers.put(OBSTRUCTION_LAYER, new TileMapLayer(rows, columns));
        mLayers.put(EXIT_LAYER, new TileMapLayer(rows, columns));

        computeHeightMap(minHeight, maxHeight, zoom);
        mConfiguration.put(WATER_LEVEL, getSeaLevel(minHeight, maxHeight));

        logger.info("Finished Initializing Schema Map");
    }

    protected void finalizeMap() {
        placeTerrain(this);
        placeLiquids(this);
    }

//    private static void distribute

    private static void placeTerrain(TileMapBuilder builder) {
        TileMapLayer colliderMap = builder.getColliderLayer();
        TileMapLayer terrainMap = builder.getTerrainLayer();

        for (int row = 0; row < colliderMap.getRows(); row++) {
            for (int column = 0; column < colliderMap.getColumns(row); column++) {
                if (colliderMap.isUsed(row, column)) {
                    terrainMap.set(row, column, builder.getWall());
                } else {
                    terrainMap.set(row, column, builder.getFloor());
                }
            }
        }
    }

    private static void placeLiquids(TileMapBuilder builder) {

        TileMapLayer heightMap = builder.getHeightLayer();
        TileMapLayer liquidMap = builder.getLiquidLayer();
        TileMapLayer colliderMap = builder.getColliderLayer();

        int liquidType = builder.getLiquid();
        int seaLevel = builder.getWaterLevel();

        // Don't place liquids if config is not set
        if (liquidType <= -1) { return; }
        // Find the lowest height in the height map to flood
        Queue<Point> toVisit = new LinkedList<>();

        for (int row = 0; row < heightMap.getRows(); row++) {
            for (int column = 0; column < heightMap.getColumns(row); column++) {

                // Path must be usable/walkable
                if (colliderMap.isUsed(row, column)) { continue; }

                int currentHeight = heightMap.get(row, column);
                if (currentHeight > seaLevel) { continue; }

                toVisit.add(new Point(column, row));
            }
        }

        int fill = liquidType;
        // Fill in the height map at that area with BFS
        Set<Point> visited = new HashSet<>();

        while (toVisit.size() > 0) {

            Point current = toVisit.poll();

            if (visited.contains(current)) { continue; }
            if (heightMap.isOutOfBounds(current.y, current.x)) { continue; }
            if (colliderMap.isUsed(current.y, current.x)) { continue; }

            visited.add(current);
            liquidMap.set(current.y, current.x, fill);

            for (Direction direction : Direction.cardinal) {
                int nextRow = current.y + direction.y;
                int nextColumn = current.x + direction.x;
                // Only visit tiles that are pats and the tile is lower or equal height to current
                if (colliderMap.isOutOfBounds(nextRow, nextColumn)) { continue; }
                if (colliderMap.isUsed(nextRow, nextColumn)) { continue; }
                if (heightMap.get(nextRow, nextColumn) > heightMap.get(current.y, current.x)) { continue; }
                toVisit.add(new Point(nextColumn, nextRow));
            }
        }
    }



    public TileMapLayer getColliderLayer() { return mLayers.get(COLLIDER_LAYER); }
    public TileMapLayer getHeightLayer() { return mLayers.get(HEIGHT_LAYER); }
    public TileMapLayer getLiquidLayer() { return mLayers.get(LIQUID_LAYER); }
    public TileMapLayer getTerrainLayer() { return mLayers.get(TERRAIN_LAYER); }

//    public TileMapLayer getObstructionLayer() { return mLayers.get(OBSTRUCTION_LAYER); }
    public Random getRandom() { return mRandom; }

    protected TileMap createTileMap() {

        TileMapLayer colliderMap = getColliderLayer();
        TileMapLayer heightMap = getHeightLayer();
        TileMapLayer liquidMap = getLiquidLayer();
        TileMapLayer terrainMap = getTerrainLayer();
//        TileMapLayer obstructionMap = getObstructionLayer();

        Entity[][] tileMap = new Entity[colliderMap.getRows()][colliderMap.getColumns()];

        for (int row = 0; row < tileMap.length; row++) {
            for (int column = 0; column < tileMap[row].length; column++) {

                Entity entity = TileFactory.create(row, column);
                
                tileMap[row][column] = entity;

                Tile details = entity.get(Tile.class);

                int collider = colliderMap.isUsed(row, column) ? 0 : -1;
                int height = heightMap.get(row, column);
//                int terrain = colliderMap.isUsed(row, column) ? getFloor() : getWall();
                int terrain = terrainMap.get(row, column);
                int liquid = liquidMap.get(row, column);
//                int obstruction = obstructionMap.get(row, column);columnsn

                details.encode(collider, height, terrain, liquid, 0);
            }
        }

        return new TileMap(tileMap);
    }

    protected void computeHeightMap(int minHeight, int maxHeight, float zoom) {
        TileMapLayer heightMap = getHeightLayer();
        SimplexNoise generator = new SimplexNoise();
        double[][] map = generator.get2DNoiseMap(heightMap.getRows(), heightMap.getColumns(), zoom);
        for (int row = 0; row < map.length; row++) {
            for (int column = 0; column < map[row].length; column++) {
                double val = map[row][column];
                int mapped = (int) MathUtils.map((float) val, 0, 1, minHeight, maxHeight);
                heightMap.set(row, column, mapped);
            }
        }
    }

    protected int getSeaLevel(int min, int max) {
        int distanceBetweenLimits = max - min;
        int quarterOfDistance = distanceBetweenLimits / 4;
        return  min + quarterOfDistance;
    }

    protected void generateNewSeed() {
        long newSeed = mRandom.nextLong();
        mConfiguration.put(SEED, newSeed);
    }
}
