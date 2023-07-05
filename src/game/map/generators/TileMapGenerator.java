package game.map.generators;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import designer.fundamentals.Direction;
import game.components.Tile;
import game.entity.Entity;
import game.map.generators.validation.SchemaConfigs;
import game.map.generators.validation.SchemaMap;
import game.map.TileMap;
import game.stores.factories.TileFactory;
import logging.ELogger;
import logging.ELoggerFactory;
import utils.MathUtils;
import utils.NoiseGenerator;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public abstract class TileMapGenerator {

    protected final static SplittableRandom random = new SplittableRandom();
    protected final static int ROOM_CREATION_ATTEMPTS = 2000;
    protected final static int STRUCTURE_PLACEMENT_ATTEMPTS = 200;
    protected boolean isPathMapCompletelyConnecting = false;
    protected SchemaMap tilePathMap = null;
    protected SchemaMap tileHeightMap = null;
    protected SchemaMap tileTerrainMap = null;
    protected SchemaMap tileStructureMap = null;
    protected SchemaMap tileLiquidMap = null;
    protected int tileSeaLevelMap = -1;

    public static TileMap fromJson(String path) {
        ELogger logger = ELoggerFactory.getInstance().getELogger(TileMapGenerator.class);
        try {
            Reader reader = Files.newBufferedReader(Paths.get(path));
            JsonObject jsonObject = (JsonObject) Jsoner.deserialize(reader);
            JsonArray tilemap = (JsonArray) jsonObject.get(TileMapGenerator.class.getSimpleName());

            JsonArray jsonArrayRow = (JsonArray) tilemap.get(0);
            SchemaMap pathMap = new SchemaMap(tilemap.size(), jsonArrayRow.size());
            SchemaMap heightMap = new SchemaMap(tilemap.size(), jsonArrayRow.size());
            SchemaMap terrainMap = new SchemaMap(tilemap.size(), jsonArrayRow.size());
            SchemaMap liquidMap = new SchemaMap(tilemap.size(), jsonArrayRow.size());
            SchemaMap structureMap = new SchemaMap(tilemap.size(), jsonArrayRow.size());

            Entity[][]raw = new Entity[tilemap.size()][];
            for (int row = 0; row < tilemap.size(); row++) {
                jsonArrayRow = (JsonArray) tilemap.get(row);
                raw[row] = new Entity[jsonArrayRow.size()];
                for (int column = 0; column < jsonArrayRow.size(); column++) {
                    JsonArray tileJson = (JsonArray) jsonArrayRow.get(column);
                    Entity entity = TileFactory.create(row, column);
                    raw[row][column] = entity;
                    Tile tile = entity.get(Tile.class);
                    int[] encoding = new int[tileJson.size()];
                    for (int i = 0; i < encoding.length; i++) {encoding[i] = tileJson.getInteger(i); }
                    tile.encode(encoding);
                }
            }
            logger.info("Finished deserializing tilemap");
            return TileMapGenerator.createTileMap(pathMap, heightMap, terrainMap, liquidMap, structureMap);
        } catch (Exception ex) {
            logger.info("Unable to deserialize Json for tilemap");
        }
        return null;
    }

    public abstract TileMap build(SchemaConfigs mapConfigs);

    protected void createSchemaMaps(SchemaConfigs mapConfigs) {

        tilePathMap = new SchemaMap(mapConfigs.rows, mapConfigs.columns);
        tileHeightMap = new SchemaMap(mapConfigs.rows, mapConfigs.columns);
        tileSeaLevelMap = initializeHeightMap(tileHeightMap, 0, 10, mapConfigs.zoom == 0 ? .2f : mapConfigs.zoom);
        tileTerrainMap = new SchemaMap(mapConfigs.rows, mapConfigs.columns);
        tileStructureMap = new SchemaMap(mapConfigs.rows, mapConfigs.columns);
        tileLiquidMap = new SchemaMap(mapConfigs.rows, mapConfigs.columns);
    }

    protected int initializeHeightMap(SchemaMap heightMap, int min, int max, float zoom) {
        double[][] map = NoiseGenerator.generateSimplexNoiseV2(heightMap.getRows(), heightMap.getColumns(), zoom);
        for (int row = 0; row < map.length; row++) {
            for (int column = 0; column < map[row].length; column++) {
                double val = map[row][column];
                int mapped = (int) MathUtils.mapToRange((float) val, 0, 1, min, max);
                heightMap.set(row, column, mapped);
            }
        }
        int distanceBetweenLimits = max - min;
        int quarterOfDistance = distanceBetweenLimits / 4;
        int seaLevel = min + quarterOfDistance;
        return seaLevel;
    }

    public static TileMap createTileMap(SchemaMap pathMap,
                                        SchemaMap heightMap,
                                        SchemaMap terrainMap,
                                        SchemaMap liquidMap,
                                        SchemaMap structureMap) {

        Entity[][] rawTileMap = new Entity[pathMap.getRows()][pathMap.getColumns()];

        for (int row = 0; row < rawTileMap.length; row++) {
            for (int column = 0; column < rawTileMap[row].length; column++) {

                Entity tile = TileFactory.create(row, column);
                rawTileMap[row][column] = tile;

                Tile details = tile.get(Tile.class);

                // 0 means not a part of the floor
                int path = pathMap.isUsed(row, column) ? 1 : 0;
                int height = heightMap.get(row, column);
                int terrain = terrainMap.get(row, column);
                int liquid = liquidMap.get(row, column);
                int structure = structureMap.get(row, column);

                details.encode(new int[]{ path, height, terrain, liquid, structure });
            }
        }

        return new TileMap(rawTileMap);
    }

    protected static void placeStructuresSafely(SchemaMap pathMap, SchemaMap structureMap, SchemaMap special, SchemaConfigs configs) {
        // Don't place structures if config is not set
        if (configs.structure <= -1) { return; }

        for (int attempt = 0; attempt < STRUCTURE_PLACEMENT_ATTEMPTS; attempt++) {
            int row = random.nextInt(pathMap.getRows());
            int column = random.nextInt(pathMap.getColumns());

            // Cell must not already have a structure placed on it
            if (pathMap.isNotUsed(row, column)) { continue; }
            if (structureMap.isUsed(row, column)) { continue; }
            // Cell must not be specialized
            if (special.isUsed(row, column)) { continue; }
            if (random.nextBoolean() || random.nextBoolean()) { continue; }

            // If there is not path around the structure via path or another structure, try again
            boolean hasEntirePathAround = true;
            for (Direction direction : Direction.values()) {
                int nextRow = row + direction.y;
                int nextColumn = column + direction.x;
                if (pathMap.isOutOfBounds(nextRow, nextColumn)) { continue; }
                if (pathMap.isNotUsed(nextRow, nextColumn)) { hasEntirePathAround = false; }
                if (structureMap.isUsed(nextRow, nextColumn)) { hasEntirePathAround = false; }
            }
            if (!hasEntirePathAround && random.nextBoolean()) { continue; }

            structureMap.set(row, column, configs.structure);
        }
    }

    protected List<Set<Point>> tryCreatingRooms(SchemaMap pathMap, boolean outline) {
        List<Rectangle> rooms = new ArrayList<>();
        List<Rectangle> roomBufferZones = new ArrayList<>();
        List<Set<Point>> finalizedRooms = new ArrayList<>();
        int bufferSize = outline ? 2 : 1;

        // Try placing rooms randomly
        for (int attempt = 0; attempt < ROOM_CREATION_ATTEMPTS; attempt++) {
            int height = random.nextInt(pathMap.getRows() / 3);
            int width = random.nextInt(pathMap.getColumns()) / 3;
            int row = random.nextInt(pathMap.getRows());
            int column = random.nextInt(pathMap.getColumns());

            // width and height must be at least greater than 3 (allows 1 non wall room tile)
            if (width < 3 || height < 3) { continue; }
            // ensure were not on edge of map, so we don't have to worry about room entrances
            if (row < 1 || column < 1) { continue; }
            if (row + height >= pathMap.getRows() - 1) { continue; }
            if (column + width >= pathMap.getColumns() - 1) { continue; }

            Rectangle newRoom = new Rectangle(column, row, width, height);
            Rectangle newRoomBufferZone = newRoom.getBounds();
            newRoomBufferZone.grow(bufferSize, bufferSize);

            // check if this room intersects with another room
            boolean overlaps = false;
            for (Rectangle room : rooms) {
                if (room.intersects(newRoom)) { overlaps = true; break; }
            }

            // Check if the rooms buffer zone intersects with another buffer zone
            for (Rectangle bufferZone : roomBufferZones) {
                if (bufferZone.intersects(newRoomBufferZone)) { overlaps = true; break; }
            }

            if (overlaps) { continue; }

            // check that the room does not touch the edges of the map. X Y is left most corner of room
            if (newRoomBufferZone.y + newRoomBufferZone.height >= pathMap.getRows()) { continue; }
            if (newRoomBufferZone.x + newRoomBufferZone.width >= pathMap.getColumns()) { continue; }

            rooms.add(newRoom);
            roomBufferZones.add(newRoomBufferZone);

            Set<Point> floors = createRoomOnMap(pathMap, newRoom, rooms.size() + 1);
            if (outline) {
                Rectangle wall = newRoom.getBounds();
                wall.grow(bufferSize - 1, bufferSize - 1);

                Set<Point> walls = createWallForRoomOnMap(pathMap, newRoom, wall);
                floors.addAll(walls);
            }
            finalizedRooms.add(new HashSet<>(floors));
        }

        return finalizedRooms;
    }

    private Set<Point> createWallForRoomOnMap(SchemaMap pathMap, Rectangle room, Rectangle buffer) {
        // Get all the wall tiles associated with the room
        Set<Point> walls = new HashSet<>();
        Set<Point> nonCorners = new HashSet<>();
        for (int row = buffer.y; row < buffer.y + buffer.height; row++) {
            for (int column = buffer.x; column < buffer.x + buffer.width; column++) {
                if (pathMap.isOutOfBounds(row, column)) { continue; }
                if (room.contains(column, row)) { continue; }

                // Get the tiles at the corner of the room that are walls
                boolean isBufferLeftmost = column == buffer.x;
                boolean isBufferRightmost = column == buffer.x + buffer.width;
                boolean isBufferTopmost = row == buffer.y;
                boolean isBufferBottommost = row == buffer.y + buffer.height;
                boolean isTopLeft = isBufferTopmost && isBufferLeftmost;
                boolean isTopRight = isBufferTopmost && isBufferRightmost;
                boolean isBottomRight = isBufferBottommost && isBufferRightmost;
                boolean isBottomLeft = isBufferBottommost && isBufferLeftmost;
                boolean isNonCorner = !isTopLeft && !isTopRight && !isBottomRight && !isBottomLeft;

                if (isNonCorner) {
                    nonCorners.add(new Point(column, row));
                }

                walls.add(new Point(column, row));
                if (tileHeightMap.get(row, column) <= tileSeaLevelMap) { continue; }
                pathMap.set(row, column, 0);
            }
        }

        // Remove at least 1 non corner wall to make an entrance
        Point door = nonCorners.iterator().next();
        pathMap.set(door.y, door.x, 1);

        // Randomly remove half of the rooms structure
        List<Point> wallsToRemove = new ArrayList<>(nonCorners);
        int toRemove = random.nextInt(wallsToRemove.size() / 2);
        for (int i = 0; i < toRemove; i++) {
            Point opening = wallsToRemove.get(random.nextInt(wallsToRemove.size()));
            pathMap.set(opening.y, opening.x, 1);
        }

        return walls;
    }

    protected Set<Point> createRoomOnMap(SchemaMap pathMap, Rectangle room, int roomNumber) {
        // Fill out the room in the schema path map
        Set<Point> tiles = new HashSet<>();
        for (int row = room.y; row < room.y + room.height; row++) {
            for (int column = room.x; column < room.x + room.width; column++) {
                if (pathMap.isOutOfBounds(row, column)) { continue; }
                tiles.add(new Point(column, row));
                pathMap.set(row, column, roomNumber);
            }
        }

        return tiles;
    }

    protected static void placeWallingSafely(SchemaMap pathMap) {

        Set<Rectangle> placements = new HashSet<>();
        for (int attempt = 0; attempt < 10; attempt++) {
            int row = random.nextInt(pathMap.getRows());
            int column = random.nextInt(pathMap.getColumns(row));
            int height = random.nextInt(pathMap.getRows() / 2);
            int width = random.nextInt(pathMap.getColumns(height) / 2);


            Rectangle rectangle = new Rectangle(column, row, width, height);
            boolean intersects = false;
            for (Rectangle placement : placements) {
                if (placement.intersects(rectangle)) {
                    intersects = true;
                }
            }

            if (intersects) { continue; }
            placements.add(rectangle);
            createWallingLine(pathMap, row, column, width, height);
        }
    }

    protected static void createWallingLine(SchemaMap pathMap, int startingRow, int startingColumn, int width, int height) {
        if (random.nextBoolean()) {
            for (int currentColumn = startingColumn; currentColumn < startingColumn + width; currentColumn++) {
                int row = startingRow + (height / 2);
                int column = currentColumn;
                if (pathMap.isOutOfBounds(row, column)) { continue; }
                pathMap.set(row, column, 0);
            }
        } else {
            for (int currentRow = startingRow; currentRow < startingRow + height; currentRow++) {
                int row = currentRow;
                int column = startingColumn + (width / 2);
                if (pathMap.isOutOfBounds(row, column)) { continue; }
                pathMap.set(row, column, 0);
            }
        }
    }

    protected static void placeLiquidsSafely(SchemaMap heightMap, SchemaMap specialMap, SchemaMap pathMap, SchemaConfigs configs, float level) {
        // Don't place liquids if config is not set
        if (configs.liquid <= -1) { return; }

        // Find the lowest height in the height map to flood
        Queue<Point> toVisit = new LinkedList<>();

        for (int row = 0; row < heightMap.getRows(); row++) {
            for (int column = 0; column < heightMap.getColumns(row); column++) {

                // Path must be usable/walkable
                if (pathMap.isNotUsed(row, column)) { continue; }

                int currentHeight = heightMap.get(row, column);

                if (currentHeight > level) { continue; }

                toVisit.add(new Point(column, row));
            }
        }

        int fill = configs.liquid;
        // Fill in the height map at that area with BFS
        Set<Point> visited = new HashSet<>();

        while (toVisit.size() > 0) {

            Point current = toVisit.poll();

            if (visited.contains(current)) { continue; }
            if (heightMap.isOutOfBounds(current.y, current.x)) { continue; }
            if (pathMap.isNotUsed(current.y, current.x)) { continue; }

            visited.add(current);
            specialMap.set(current.y, current.x, fill);

            for (Direction direction : Direction.cardinal) {
                int nextRow = current.y + direction.y;
                int nextColumn = current.x + direction.x;
                // Only visit tiles that are pats and the tile is lower or equal height to current
                if (pathMap.isOutOfBounds(nextRow, nextColumn)) { continue; }
                if (pathMap.isNotUsed(nextRow, nextColumn)) { continue; }
                if (heightMap.get(nextRow, nextColumn) > heightMap.get(current.y, current.x)) { continue; }
                toVisit.add(new Point(nextColumn, nextRow));
            }
        }
    }

    public static Set<Point> createWallForMap(SchemaMap pathMap) {
        Set<Point> walling = new HashSet<>();
        for (int row = 0; row < pathMap.getRows(); row++) {
            for (int column = 0; column < pathMap.getColumns(row); column++) {
                boolean isTop = row == 0;
                boolean isRight = row == pathMap.getRows() - 1;
                boolean isBottom = column == pathMap.getColumns(row) - 1;
                boolean isLeft = column == 0;

                if (!isTop && !isRight && !isBottom && !isLeft) { continue; }

                pathMap.set(row, column, 0);
                walling.add(new Point(column, row));
            }
        }
        return walling;
    }

    public List<Set<Point>> tryConnectingRooms(SchemaMap pathMap, List<Set<Point>> rooms) {
        List<Set<Point>> halls = new ArrayList<>();
        Set<Point> hall;
        int size;
        // connect each of the rooms, one after the other
        for (int index = 1; index < rooms.size(); index++) {
            Set<Point> previousRoom = rooms.get(index - 1);
            Set<Point> currentRoomTile = rooms.get(index);
            size = random.nextBoolean() ? 1 : 2;
            hall = connectViaTunneling(pathMap, currentRoomTile, previousRoom, size);
            halls.add(hall);
        }
        // connect the first and last room
        size = random.nextBoolean() ? 1 : 2;
        hall = connectViaTunneling(pathMap, rooms.get(0), rooms.get(rooms.size() - 1), size);
        halls.add(hall);

        return halls;
    }

    public static Set<Point> connectViaTunneling(SchemaMap pathMap, Set<Point> room1, Set<Point> room2, int size) {
        // Get random tiles to connect rooms
        List<Point> randomizerList = new ArrayList<>(room1);
        Collections.shuffle(randomizerList);
        Point pointFromRoom1 = randomizerList.get(0);

        randomizerList = new ArrayList<>(room2);
        Collections.shuffle(randomizerList);
        Point pointFromRoom2 = randomizerList.get(0);

        Set<Point> columnTunnel = connectRows(pathMap, pointFromRoom1.y, pointFromRoom2.y, pointFromRoom1.x, size);

        Set<Point> rowTunnel = connectColumns(pathMap, pointFromRoom1.x, pointFromRoom2.x, pointFromRoom2.y, size);

        Set<Point> corridor = new HashSet<>();
        corridor.addAll(rowTunnel);
        corridor.addAll(columnTunnel);

        return corridor;
    }
    public static Set<Point> connectRows(SchemaMap pathMap, int startingRow, int endingRow, int column, int size) {
        int startRow = Math.min(startingRow, endingRow);
        int endRow = Math.max(startingRow, endingRow) + size;
        Set<Point> tiles = new HashSet<>();
        for (int curRow = startRow; curRow < endRow; curRow++) {
            for (int curCol = column; curCol < column + size; curCol++) {
                if (pathMap.isOutOfBounds(curRow, curCol)) { continue; }
                if (pathMap.isOutOfBounds(curRow + 1, curCol + 1)) { continue; }
                // if the tile is already a path, go to next
                if (pathMap.isUsed(curRow, curCol)) { continue; }

                pathMap.set(curRow, curCol, 1);
                tiles.add(new Point(curCol, curRow));
            }
        }
        return tiles;
    }

    protected static Set<Point> connectColumns(SchemaMap pathMap, int starting, int ending, int row, int size) {
        // Check which point is actually the first, numerically
        int startCol = Math.min(starting, ending);
        int endCol = Math.max(starting, ending) + size;
        Set<Point> tiles = new HashSet<>();
        for (int curCol = startCol; curCol < endCol; curCol++) {
            for (int curRow = row; curRow < row + size; curRow++) {
                if (pathMap.isOutOfBounds(curRow, curCol)) { continue; }
                if (pathMap.isOutOfBounds(curRow + 1, curCol + 1)) { continue; }
                // if the tile is already a path, go to next
                if (pathMap.isUsed(curRow, curCol)) { continue; }

                pathMap.set(curRow, curCol, 1);
                tiles.add(new Point(curCol, curRow));
            }
        }
        return tiles;
    }

    public static void mapPathMapToTerrainMap(SchemaMap pathMap, SchemaMap terrainMap, SchemaConfigs configs) {
        for (int row = 0; row < pathMap.getRows(); row++) {
            for (int column = 0; column < pathMap.getColumns(); column++) {
                if (pathMap.isUsed(row, column)) {
                    terrainMap.set(row, column, configs.flooring);
                } else {
                    terrainMap.set(row, column, configs.walling);
                }
            }
        }
    }
}
