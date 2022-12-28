package game.map.generators;

import constants.Constants;
import constants.Direction;
import game.components.Tile;
import game.entity.Entity;
import game.map.generators.validation.SchemaMap;
import game.map.TileMap;
import game.stores.factories.TileFactory;
import game.stores.pools.AssetPool;
import utils.NoiseGenerator;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.*;

public abstract class TileMapGenerator {

    protected final static SplittableRandom random = new SplittableRandom();
    protected final static int ROOM_CREATION_ATTEMPTS = 2000;
    protected boolean isCompletelyConnected = false;
    protected SchemaMap pathMap = null;
    protected SchemaMap heightMap = null;
    protected SchemaMap terrainMap = null;
    protected SchemaMap structureMap = null;
    protected SchemaMap specialMap = null;
    protected int flooring;
    protected int walling;

    public abstract TileMap build(int mapRows, int mapColumns, int mapFlooring, int mapWalling);
    protected void createSchemaMaps(int mapRows, int mapColumns, int mapFlooring, int mapWalling) {

        pathMap = new SchemaMap(mapRows, mapColumns);

        heightMap = new SchemaMap(mapRows, mapColumns);
        generateHeightMap(heightMap);

        terrainMap = new SchemaMap(mapRows, mapColumns);

        structureMap = new SchemaMap(mapRows, mapColumns);

        specialMap = new SchemaMap(mapRows, mapColumns);

        flooring = mapFlooring;

        walling = mapWalling;
    }

    protected void generateHeightMap(SchemaMap heightMap) {
        double[][] map = NoiseGenerator.generateSimplexNoiseV2(heightMap.getRows(), heightMap.getColumns(), .5f);
        for (int row = 0; row < map.length; row++) {
            for (int column = 0; column < map[row].length; column++) {
                heightMap.set(row, column, (int) (map[row][column] * 10));
            }
        }
    }

    public static TileMap createTileMap(SchemaMap pathMap,
                                        SchemaMap heightMap,
                                        SchemaMap terrainMap,
                                        SchemaMap specialMap,
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
                int special = specialMap.get(row, column);
                int structure = structureMap.get(row, column);

//                int water = 0;//floodMap.get(row, column) != 0 ? floodMap.get(row, column) : 0; //waterMap[row][column] == 0 ? 0 : 0;//waterMap[row][column];
                details.encode(new int[]{ path, height, terrain, special, structure });


                if (pathMap.isUsed(row, column)) {
                    placeShadowsOnTile(heightMap, pathMap, details, row, column);
                }
            }
        }

        return new TileMap(rawTileMap);
    }

    private static void placeShadowsOnTile(SchemaMap heightMap, SchemaMap pathMap, Tile details, int row, int column) {
        int currentHeight = heightMap.get(row, column);
        BufferedImage image;

        // TODO, figure out why we need to invert x and y from direction
        // Add the cardinal directional tiles only first
        for (Direction direction : Direction.values()) {

            int nextRow = row + direction.x;
            int nextColumn = column + direction.y;
            if (heightMap.isOutOfBounds(nextRow, nextColumn)) { continue; }

            int nextHeight = heightMap.get(nextRow, nextColumn);
            if (nextHeight <= currentHeight && pathMap.isUsed(nextRow, nextColumn)) { continue; }

            int index = direction.ordinal();

            image = AssetPool.instance().getSpecificImage(Constants.SHADOWS_SPRITESHEET_FILEPATH, 0, index);
            details.shadows.add(image);
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
                pathMap.set(row, column, 0);
            }
        }

        // Remove at least 1 non corner wall to make an entrance
        Point door = nonCorners.iterator().next();
        pathMap.set(door.y, door.x, 1);

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

    protected static void floodLowestHeight(SchemaMap heightMap, SchemaMap specialMap, SchemaMap pathMap) {
        // Find the lowest height in the height map to flood
        Point position = null;

        for (int attempt = 0; attempt < ROOM_CREATION_ATTEMPTS; attempt++) {
            int startRow = random.nextInt(pathMap.getRows());
            int startColumn = random.nextInt(pathMap.getColumns());

            if (pathMap.isNotUsed(startRow, startColumn)) { continue; }

            position = new Point(startColumn, startRow);
            break;
        }

        if (position == null) { return; }

        int fill = 4;
        // Fill in the height map at that area with BFS
        Set<Point> visited = new HashSet<>();
        Queue<Point> toVisit = new LinkedList<>();
        toVisit.add(position);

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
                if (pathMap.isNotUsed(nextRow, nextColumn)) { continue; }
                if (heightMap.get(nextRow, nextColumn) > heightMap.get(current.y, current.x)) { continue; }
                toVisit.add(new Point(nextColumn, nextRow));
            }
        }
    }


//    public void floodFillRoom(SchemaMap floorMap, SchemaMap fillMap, List<Set<Point>> rooms, int roomsToFill, int contentIndex) {
//        System.out.println(floorMap.debug() + " floor");
//        System.out.println(terrainMap.debug() + " flood");
//        System.out.println("========================");
//        // select a random room, rooms number of times
//        Queue<Point> toVisit = new LinkedList<>();
//        Set<Point> visited = new HashSet<>();
//        for (int i = 0; i < roomsToFill; i++) {
//            Set<Point> room = rooms.get(random.nextInt(rooms.size()));
//            toVisit.clear();
//            // Start at a random point
//            Point starting = room.iterator().next();
//            toVisit.add(starting);
//            int total = 20;
//
//            while (toVisit.size() > 0) {
//
//                Point current = toVisit.poll();
//                boolean hasClearedRoom = hasFoundAllPoints(room, visited);
//
////                if (!room.tiles().contains(current)) { continue; }
//                if (floorMap.isOutOfBounds(current.y, current.x)) { continue; }
//                if (fillMap.isOutOfBounds(current.y, current.x)) { continue; }
////                if (isOutOfBounds(pathMap, current.y, current.x)) { continue; }
////                if (isOutOfBounds(waterMap, current.y, current.x)) { continue; }
//
//                if (visited.contains(current)) { continue; }
//                if (floorMap.isNotUsed(current.y, current.x)) { continue; }
//                if (total < 0) { continue; }
////                if (pathMap[current.y][current.x] == 0) { continue; }
//
////                waterMap[current.y][current.x] = contentIndex;
//                fillMap.set(current.y, current.x, contentIndex);
//                total--;
//
//                visited.add(current);
//
//                for (Direction direction : Direction.cardinal) {
//                    if (hasClearedRoom) { continue; }
//                    toVisit.add(new Point(current.y + direction.y,current.x + direction.x));
//                }
//            }
//            System.out.println(terrainMap.debug() + " flood");
//        }
//        System.out.println(floorMap.debug() + " floor");
//        System.out.println(terrainMap.debug() + " flood");
//    }

//    public void floodFillRoom(int[][] pathMap, int[][] waterMap, List<MapRoom> mapRooms, int roomsToFill, int contentIndex) {
//        // select a random room, rooms number of times
//        Queue<Point> toVisit = new LinkedList<>();
//        Set<Point> visited = new HashSet<>();
//        for (int i = 0; i < roomsToFill; i++) {
//            MapRoom room = mapRooms.get(random.nextInt(mapRooms.size()));
//            toVisit.clear();
//            // Start at a random point
//            Point starting = room.tiles().iterator().next();
//            toVisit.add(starting);
//
//            System.err.println("Filling " + starting);
//
//            while (toVisit.size() > 0) {
//
//                Point current = toVisit.poll();
//                boolean hasClearedRoom = hasFoundAllPoints(room, visited);
//
////                if (!room.tiles().contains(current)) { continue; }
//                if (isOutOfBounds(pathMap, current.y, current.x)) { continue; }
//                if (isOutOfBounds(waterMap, current.y, current.x)) { continue; }
//
//                if (visited.contains(current)) { continue; }
//                if (pathMap[current.y][current.x] == 0) { continue; }
//
//                waterMap[current.y][current.x] = contentIndex;
//                visited.add(current);
//
//                for (Direction direction : Direction.cardinal) {
//                    if (hasClearedRoom && random.nextBoolean()) { continue; }
//                    toVisit.add(new Point(current.y + direction.y,current.x + direction.x));
//                }
//            }
//        }
//    }

    private boolean hasFoundAllPoints(Set<Point> room, Set<Point> visited) {
        for (Point tile : room) {
            if (!visited.contains(tile)) { return false;}
        }
        return true;
    }

    private Point getFirstOccurrence(int[][] pathMap, int marker) {
        for (int row = 0; row < pathMap.length; row++) {
            for (int col = 0; col < pathMap[row].length; col++) {
                if (pathMap[row][col] != marker) { continue; }
                return new Point(row, col);
            }
        }
        return null;
    }

    public boolean isConnected(SchemaMap pathMap, Point starting, Point ending) {
        // locate the starting tile
        if (starting == null || ending == null) { return false; }

        // search for a path to the end
        Queue<Point> queue = new LinkedList<>();
        queue.add(starting);
        Set<Point> set = new HashSet<>();

        while (queue.size() > 0) {

            Point current = queue.poll();
            // We've found the end
            if (current.equals(ending)) { return true; }
            // Outside the bounds of the path map
            if (pathMap.isOutOfBounds(current.y, current.x)) { continue; }
            // if not path tile, skip
            if (!pathMap.isUsed(current.y, current.x)) { continue; }
            // if already visited, skip
            if (set.contains(current)) { continue; }

            set.add(current);

            // Get the next set of points in cardinal direction to check
            for (Direction dir : Direction.cardinal) {
                queue.add(new Point(current.x + dir.x, current.y + dir.y));
            }
        }

        // If all points have been exhausted, return false
        return false;
    }

    public boolean doesPathConnectFromStartToEnd(int[][] pathMap, int start, int end) {
        if (pathMap == null) { return false; }

        // locate the starting tile
        Point starting = getFirstOccurrence(pathMap, start);
        if (starting == null) { return false; }

        // locate the ending tile
        Point ending = getFirstOccurrence(pathMap, end);
        if (ending == null) { return false; }

        // search for a path to the end
        Queue<Point> queue = new LinkedList<>();
        queue.add(starting);
        Set<Point> set = new HashSet<>();

        while (queue.size() > 0) {

            Point current = queue.poll();
            // We've found the end
            if (current.equals(ending)) { return true; }
            // Outside the bounds of the path map
            if (isOutOfBounds(pathMap, current.y, current.x)) { continue; }

            int val = pathMap[current.y][current.x];
            // if not path tile, skip
            if (val == 0) { continue; }
            // if already visited, skip
            if (set.contains(current)) { continue; }

            set.add(current);

            // Get the next set of points in cardinal direction to check
            for (Direction dir : Direction.cardinal) {
                queue.add(new Point(current.y + dir.y, current.x + dir.x));
            }
        }

        // If all points have been exhausted, return false
        return false;
    }

    protected static int[][] createMap(int rows, int columns) {
        return createMap(rows, columns, 0);
    }

    protected static int[][] createMap(int rows, int columns, int toFillAs) {
        int[][] pathMap = new int[rows][columns];
        for (int[] row : pathMap) { Arrays.fill(row, toFillAs); }
        return pathMap;
    }

    // This method needs to be perfected, kinda sus TODO
    protected boolean hasSpaceAround(Entity[][] map, Entity entity) {
        Tile details = entity.get(Tile.class);
        for (Direction direction : Direction.values()) {
            int dcolumn = details.column + direction.x;
            int drow = details.row + direction.y;
            Entity toCheck = tryFetchingEntity(map, drow, dcolumn);
            if (toCheck == null) { continue; }
            Tile toCheckDetails = toCheck.get(Tile.class);
            if (toCheckDetails.isStructureUnitOrWall()) {
                return false;
            }
        }
        return true;
    }

    public void tryPlacingStructures(int[][] pathMap) {
        int attempts = 2000;
        for (int attempt = 0; attempt < attempts; attempt++) {
            // Only place a structure on an empty
            int row = random.nextInt(pathMap.length);
            int column = random.nextInt(pathMap[row].length);
            if (pathMap[row][column] != 0) { continue; }

            boolean cluster = random.nextBoolean() && random.nextBoolean();
            int clusterSize = (int) ((pathMap.length * pathMap[0].length) * .1);

            // BFS to place structures

            Queue<Point> toVisit = new LinkedList<>();
            Set<Point> visited = new HashSet<>();
            toVisit.add(new Point(column, row));

            while (toVisit.size() > 0) {

                Point current = toVisit.poll();

                if (isOutOfBounds(pathMap, current.y, current.x)) { continue; }
            }
        }
    }
//
//    protected List<Set<Point>> tryCreatingRooms(SchemaMap floorMap) {
//        int attempts = 2000;
//        List<Rectangle> rooms = new ArrayList<>();
//        List<Rectangle> roomBufferZones = new ArrayList<>();
//        List<Set<Point>> finalizedRooms = new ArrayList<>();
//
//        // Try placing rooms randomly
//        for (int attempt = 0; attempt < attempts; attempt++) {
//            int height = random.nextInt(floorMap.getRows() / 3);
//            int width = random.nextInt(floorMap.getColumns()) / 3;
//            int row = random.nextInt(floorMap.getRows());
//            int column = random.nextInt(floorMap.getColumns());
//
//            // width and height must be at least greater than 3 (allows 1 non wall room tile)
//            if (width < 3 || height < 3) { continue; }
//            // ensure were not on edge of map, so we don't have to worry about room entrances
//            if (row < 1 || column < 1) { continue; }
//            if (row + height >= floorMap.getRows() - 1) { continue; }
//            if (column + width >= floorMap.getColumns() - 1) { continue; }
//
//            Rectangle newRoom = new Rectangle(column, row, width, height);
//            Rectangle newRoomBufferZone = newRoom.getBounds();
//            newRoomBufferZone.grow(1, 1);
//
//            // check if this room intersects with another room
//            boolean overlaps = false;
//            for (Rectangle room : rooms) {
//                if (room.intersects(newRoom)) { overlaps = true; break; }
//            }
//
//            // Check if the rooms buffer zone intersects with another buffer zone
//            for (Rectangle bufferZone : roomBufferZones) {
//                if (bufferZone.intersects(newRoomBufferZone)) { overlaps = true; break; }
//            }
//
//            if (overlaps) { continue; }
//
//            // check that the room does not touch the edges of the map. X Y is left most corner of room
//            if (newRoomBufferZone.y + newRoomBufferZone.height >= floorMap.getRows()) { continue; }
//            if (newRoomBufferZone.x + newRoomBufferZone.width >= floorMap.getColumns()) { continue; }
//
//            rooms.add(newRoom);
//            roomBufferZones.add(newRoomBufferZone);
//
//
//            Set<Point> tiles = createRoom(floorMap, newRoom, finalizedRooms.size() + 1);
//            finalizedRooms.add(new HashSet<>(tiles));
//        }
//
//        return finalizedRooms;
//    }

//    protected Set<Point> createRoom(SchemaMap pathMap, Rectangle room, int number) {
//        Set<Point> tiles = new HashSet<>();
//        for (int row = room.y; row < room.y + room.height; row++) {
//            for (int column = room.x; column < room.x + room.width; column++) {
//                if (pathMap.isOutOfBounds(row, column)) { continue; }
//                tiles.add(new Point(column, row));
//                pathMap.set(row, column, number);
//            }
//        }
//
//        return tiles;
//    }

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
    public static boolean isOutOfBounds(int[][] pathMap, int row, int col) {
        return row < 0 || row >= pathMap.length || col < 0 || col >= pathMap[row].length;
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
//    public static Set<Point> connectRows(int[][] pathMap, int startingRow, int endingRow, int column, int size) {
//        int startRow = Math.min(startingRow, endingRow);
//        int endRow = Math.max(startingRow, endingRow) + size;
//        Set<Point> tiles = new HashSet<>();
//        for (int curRow = startRow; curRow < endRow; curRow++) {
//            for (int curCol = column; curCol < column + size; curCol++) {
//                if (isOutOfBounds(pathMap, curRow, curCol)) { continue; }
//                pathMap[curRow][curCol] = 1;
//                tiles.add(new Point(curRow, curCol));
//            }
//        }
//        return tiles;
//    }
//
//    protected static Set<Point> connectColumns(int[][] pathMap, int starting, int ending, int row, int size) {
//        int startCol = Math.min(starting, ending);
//        int endCol = Math.max(starting, ending) + size;
//        Set<Point> tiles = new HashSet<>();
//        for (int curCol = startCol; curCol < endCol; curCol++) {
//            for (int curRow = row; curRow < row + size; curRow++) {
//                if (isOutOfBounds(pathMap, curRow, curCol)) { continue; }
//                // if the tile is already a path, go to next
//                pathMap[curRow][curCol] = 1;
//                tiles.add(new Point(curRow, curCol));
//            }
//        }
//        return tiles;
//    }

    public static void developTerrainMapFromPathMap(SchemaMap pathMap, SchemaMap terrainMap, int mapFlooring, int mapWalling) {
        for (int row = 0; row < pathMap.getRows(); row++) {
            for (int column = 0; column < pathMap.getColumns(); column++) {
                if (pathMap.isUsed(row, column)) {
                    terrainMap.set(row, column, mapFlooring);
                } else {
                    terrainMap.set(row, column, mapWalling);
                }
            }
        }
    }

    protected static Entity tryFetchingEntity(Entity[][] map, int row, int column) {
        if (row < 0 || column < 0 || row >= map.length || column >= map[row].length) {
            return null;
        } else {
            return map[row][column];
        }
    }
}
