package main.game.map.base;

import main.constants.Constants;
import main.constants.Direction;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.map.builders.BorderedMapWithBorderedRooms;
import main.game.map.builders.HauberkDungeonMap;
import main.game.map.builders.LargeContinuousRoom;
import main.game.map.builders.TileMapBuilderAlgorithm;
import main.game.map.builders.utils.TileMapLayer;
import main.game.map.builders.utils.TileMapOperations;
import main.game.stores.pools.AssetPool;
import main.graphics.SpriteMap;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.awt.Point;
import java.util.*;

public class TileMapBuilder {

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

    public static Map<String, TileMapOperations> getTileMapBuilderMapping() {
        Map<String, TileMapOperations> operationsMap = new LinkedHashMap<>();
        operationsMap.put("HauberkDungeonMap", new HauberkDungeonMap());
        operationsMap.put("BorderedMapWithBorderedRooms", new BorderedMapWithBorderedRooms());
        operationsMap.put("LargeContinuousRoom", new LargeContinuousRoom());
        return operationsMap;
    }

    static TileMap createRandom(int rows, int columns) {

        SpriteMap spriteMap = AssetPool.getInstance().getSpriteMap(Constants.TILES_SPRITEMAP_FILEPATH);

        Random random = new Random();

        List<String> list = spriteMap.contains(TileMapBuilder.WALL);
        int wall = spriteMap.indexOf(list.get(random.nextInt(list.size())));

        list = spriteMap.contains(TileMapBuilder.FLOOR);
        int floor = spriteMap.indexOf(list.get(random.nextInt(list.size())));

        list = spriteMap.contains(TileMapBuilder.LIQUID);
        int liquid = spriteMap.indexOf(list.get(random.nextInt(list.size())));

        Map<String, Object> configuration = new HashMap<>();

        configuration.put(ALGORITHM, "");
        configuration.put(ROWS, rows);
        configuration.put(COLUMNS, columns);
        configuration.put(WALL, wall);
        configuration.put(FLOOR, floor);
        configuration.put(LIQUID, liquid);
        int randomBounds = 5;
//        randomBounds = randomBounds % 10;
        configuration.put(MAX_HEIGHT, randomBounds);
        configuration.put(MIN_HEIGHT, randomBounds * -1);

        return create(configuration);
    }

    static TileMap create(Map<String, Object> configuration) {
        sanitize(configuration);

        TileMap newTileMap = new TileMap(configuration);

        String value = (String) configuration.getOrDefault(ALGORITHM, "");

        TileMapOperations operation = getTileMapBuilderMapping().get(value);

//        new LargeContinuousRoom().execute(newTileMap);
        if (operation == null) {
            new BorderedMapWithBorderedRooms().execute(newTileMap);
        } else {
            operation.execute(newTileMap);
        }

        TileMapOperations.tryPlacingDestroyableBlockers(newTileMap);
//        Algorithm algorithm = Algorithm.valueOf((String) configuration.get(ALGORITHM));
//        TileMapBuilder builder = null;
//        switch (algorithm) {
////            case BasicOpenMap -> builder = new BasicOpenMap(configuration);
//            case BorderedOpenMapWithBorderedRooms -> builder = new BorderedMapWithBorderedRooms(configuration);
////            case LargeBorderedRooms -> builder = new LargeBorderedRoom(configuration);
////            case LargeContinuousRoom -> builder = new LargeContinuousRoom(configuration);
////            case NoBorderWithSmallRooms -> builder = new NoBorderWithSmallRooms(configuration);
////            case HauberkDungeonMap -> builder = new HauberkDungeonMap(configuration);
//        }

        return newTileMap;
    }

    static void sanitize(Map<String, Object> configuration) {
        configuration.put(ROWS, configuration.getOrDefault(ROWS, -1));
        configuration.put(COLUMNS, configuration.getOrDefault(COLUMNS, -1));
        configuration.put(FLOOR, configuration.getOrDefault(FLOOR, -1));
        configuration.put(WALL, configuration.getOrDefault(WALL, -1));
        configuration.put(LIQUID, configuration.getOrDefault(LIQUID, -1));
        configuration.put(CURRENT_SEED, configuration.getOrDefault(CURRENT_SEED, new Random().nextLong()));
        configuration.put(ZOOM, configuration.getOrDefault(ZOOM, .75f));
        configuration.put(MIN_HEIGHT, configuration.getOrDefault(MIN_HEIGHT, 0));
        configuration.put(MAX_HEIGHT, configuration.getOrDefault(MAX_HEIGHT, 10));
        configuration.put(ALGORITHM, configuration.getOrDefault(ALGORITHM, ""));
        configuration.put(STRUCTURES, configuration.getOrDefault(STRUCTURES, null));
    }

    static void placeTerrain(TileMap tileMap) {
        TileMapLayer colliderMap = tileMap.getColliderLayer();
        TileMapLayer terrainMap = tileMap.getTerrainLayer();
        int wall = (int) tileMap.getConfiguration(WALL);
        int floor = (int) tileMap.getConfiguration(FLOOR);

        for (int row = 0; row < colliderMap.getRows(); row++) {
            for (int column = 0; column < colliderMap.getColumns(row); column++) {
                if (colliderMap.isUsed(row, column)) {
                    terrainMap.set(row, column, wall);
                } else {
                    terrainMap.set(row, column, floor);
                }
            }
        }
    }

    static void placeLiquids(TileMap tileMap) {

        TileMapLayer heightMap = tileMap.getHeightLayer();
        TileMapLayer liquidMap = tileMap.getLiquidLayer();
        TileMapLayer colliderMap = tileMap.getColliderLayer();

        int liquidType = tileMap.getLiquid();
        int seaLevel = tileMap.getWaterLevel();

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

        // Fill in the height map at that area with BFS
        Set<Point> visited = new HashSet<>();

        while (!toVisit.isEmpty()) {

            Point current = toVisit.poll();

            if (visited.contains(current)) { continue; }
            if (heightMap.isOutOfBounds(current.y, current.x)) { continue; }
            if (colliderMap.isUsed(current.y, current.x)) { continue; }

            visited.add(current);
            liquidMap.set(current.y, current.x, liquidType);

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

    static void placeShadows(TileMap tileMap) {

        Entity[][] map = tileMap.mRawMap;
        // Go through each tile
        for (int row = 0; row < map.length; row++) {
            for (int column = 0; column < map[row].length; column++) {

                // Ensure within bounds
                // if (row == 0 || column == 0) { continue; }
                // if (row == map.length - 1 || column == map[row].length - 1) { continue; }

                // get current height
                Entity currentEntity = map[row][column];
                Tile currentTile = currentEntity.get(Tile.class);
                if (currentTile.isWall()) { continue; }


                // Check all the tiles in all directions
                for (Direction direction : Direction.values()) {

                    int nextRow = row + direction.y;
                    int nextColumn = column + direction.x;

                    Entity adjacentEntity = tileMap.tryFetchingTileAt(nextRow, nextColumn);
                    if (adjacentEntity == null) { continue; }
                    Tile adjacentTile = adjacentEntity.get(Tile.class);

                    // If the adjacent tile is higher, add a shadow in that direction
                    if (adjacentTile.getHeight() <= currentTile.getHeight() && adjacentTile.isPath()) { continue; }
                    // Enhanced liquid visuals where shadows not showing on them
//                    if (adjacentTile.getLiquid() != 0) { continue; }

                    int index = direction.ordinal();

                    // TODO this is showing under walls, find a way to remove it
                    String id = AssetPool.getInstance()
                            .createAsset(AssetPool.MISC_SPRITEMAP, "directional_shadows", index, AssetPool.STATIC_ANIMATION);
                    currentTile.putAsset(direction + " " + Tile.SHADOW, id);
                    int tileHeightDifference = Math.abs(currentTile.getHeight() - adjacentTile.getHeight());
//                    if (tileHeightDifference > 1) {
////                        currentTile.shadowIds.add(id);
//                    }
                }
            }
        }
    }
}
