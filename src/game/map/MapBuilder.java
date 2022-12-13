package game.map;

import constants.Direction;
import game.components.Tile;
import game.entity.Entity;
import game.stores.factories.TileFactory;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.*;


public abstract class MapBuilder {

    protected final SplittableRandom random = new SplittableRandom();
    protected boolean isCompletelyConnected = false;
    protected SchemaMap floorMap = null;
    protected SchemaMap structureMap = null;

    public abstract TileMap build(int rows, int columns);

    public static Entity[][] encode(SchemaMap pathMap, SchemaMap structureMap, int baseImageIndex) {
        Entity[][] rawTileMap = new Entity[pathMap.getRows()][pathMap.getColumns()];
        for (int row = 0; row < rawTileMap.length; row++) {
            for (int column = 0; column < rawTileMap[row].length; column++) {

                Entity tile = TileFactory.create(row, column);
                rawTileMap[row][column] = tile;

                Tile details = tile.get(Tile.class);

                // 0 means not a part of the path
                int path = pathMap.getOccupied(row, column) != 0 ? baseImageIndex : -(baseImageIndex + 1);
//                int path = pathMap.isObstructed(row, column) ? baseImageIndex : -(baseImageIndex + 1);

                int structure = structureMap.getOccupied(row, column) != 0 ? structureMap.getOccupied(row, column) : 0;
                if (structureMap.getOccupied(row, column) != 0) {
                    System.out.println("Yooo");
                }

                int water = 0;//waterMap[row][column] == 0 ? 0 : 0;//waterMap[row][column];
                details.encode(new int[]{ path, structure, water});
            }
        }
        return rawTileMap;
    }

//    public static Entity[][] encode(int[][] pathMap, int[][] waterMap, int baseImageIndex) {
//        Entity[][] rawTileMap = new Entity[pathMap.length][pathMap[0].length];
//        for (int row = 0; row < rawTileMap.length; row++) {
//            for (int column = 0; column < rawTileMap[row].length; column++) {
//
//                Entity tile = TileFactory.create(row, column);
//                rawTileMap[row][column] = tile;
//
//                Tile details = tile.get(Tile.class);
//
//                // 0 means not a part of the path
//                int path = pathMap[row][column] != 0 ? baseImageIndex : -(baseImageIndex + 1);
//
//                int water = waterMap[row][column] == 0 ? 0 : waterMap[row][column];
//                details.encode(new int[]{ path, 0, water});
//            }
//        }
//        return rawTileMap;
//    }

    public boolean isCompletelyConnected(SchemaMap pathMap, List<Set<Point>> rooms) {
        // For every room, see that it has a path to every other room
        for (Set<Point> currentRoom : rooms) {
            for (Set<Point> nextRoom : rooms) {
                // Check that each room connects to all other rooms
                if (!isConnected(pathMap, currentRoom.iterator().next(), nextRoom.iterator().next())) {
                    return false;
                }
            }
        }
        return true;
    }

    public void floodFillRoom(int[][] pathMap, int[][] waterMap, List<MapRoom> mapRooms, int roomsToFill, int contentIndex) {
        // select a random room, rooms number of times
        Queue<Point> toVisit = new LinkedList<>();
        Set<Point> visited = new HashSet<>();
        for (int i = 0; i < roomsToFill; i++) {
            MapRoom room = mapRooms.get(random.nextInt(mapRooms.size()));
            toVisit.clear();
            // Start at a random point
            Point starting = room.tiles().iterator().next();
            toVisit.add(starting);

            System.err.println("Filling " + starting);

            while (toVisit.size() > 0) {

                Point current = toVisit.poll();
                boolean hasClearedRoom = hasFoundAllPoints(room, visited);

//                if (!room.tiles().contains(current)) { continue; }
                if (isOutOfBounds(pathMap, current.y, current.x)) { continue; }
                if (isOutOfBounds(waterMap, current.y, current.x)) { continue; }

                if (visited.contains(current)) { continue; }
                if (pathMap[current.y][current.x] == 0) { continue; }

                waterMap[current.y][current.x] = contentIndex;
                visited.add(current);

                for (Direction direction : Direction.cardinal) {
                    if (hasClearedRoom && random.nextBoolean()) { continue; }
                    toVisit.add(new Point(current.y + direction.y,current.x + direction.x));
                }
            }
        }
    }

    private boolean hasFoundAllPoints(MapRoom room, Set<Point> visited) {
        for (Point tile : room.tiles()) {
            if (!visited.contains(tile)) { return false;}
        }
        return true;
    }

    public void assignStartAndEnd(int[][] pathMap, List<MapRoom> rooms, int start, int end) {
        // assign start
        MapRoom room = rooms.get(random.nextInt(rooms.size()));
        pathMap[room.blueprint().y][room.blueprint().x] = start;
        room = rooms.get(random.nextInt(rooms.size()));
        while (pathMap[room.blueprint().y][room.blueprint().x] == start) {
            room = rooms.get(random.nextInt(rooms.size()));
        }
        // assign end
        pathMap[room.blueprint().y][room.blueprint().x] = end;
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
            if (!pathMap.isOccupied(current.y, current.x)) { continue; }
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

    public List<Set<Point>> tryCreatingRooms(SchemaMap floorMap) {
        int attempts = 2000;
        List<Rectangle> rooms = new ArrayList<>();
        List<Rectangle> roomBufferZones = new ArrayList<>();
        List<Set<Point>> finalizedRooms = new ArrayList<>();

        // Try placing rooms randomly
        for (int attempt = 0; attempt < attempts; attempt++) {
            int height = random.nextInt(floorMap.getRows() / 3);
            int width = random.nextInt(floorMap.getColumns()) / 3;
            int row = random.nextInt(floorMap.getRows());
            int column = random.nextInt(floorMap.getColumns());

            // width and height must be at least greater than 3 (allows 1 non wall room tile)
            if (width < 3 || height < 3) { continue; }
            // ensure were not on edge of map, so we don't have to worry about room entrances
            if (row < 1 || column < 1) { continue; }
            if (row + height >= floorMap.getRows() - 1) { continue; }
            if (column + width >= floorMap.getColumns() - 1) { continue; }

            Rectangle newRoom = new Rectangle(column, row, width, height);
            Rectangle newRoomBufferZone = newRoom.getBounds();
            newRoomBufferZone.grow(1, 1);

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
            if (newRoomBufferZone.y + newRoomBufferZone.height >= floorMap.getRows()) { continue; }
            if (newRoomBufferZone.x + newRoomBufferZone.width >= floorMap.getColumns()) { continue; }

            rooms.add(newRoom);
            roomBufferZones.add(newRoomBufferZone);


            Set<Point> tiles = createRoom(floorMap, newRoom, finalizedRooms.size() + 1);
            finalizedRooms.add(new HashSet<>(tiles));
        }

        return finalizedRooms;
    }

    public Set<Point> createRoom(SchemaMap pathMap, Rectangle room, int number) {
        Set<Point> tiles = new HashSet<>();
        for (int row = room.y; row < room.y + room.height; row++) {
            for (int column = room.x; column < room.x + room.width; column++) {
                if (pathMap.isOutOfBounds(row, column)) { continue; }
                tiles.add(new Point(column, row));
                pathMap.setOccupied(row, column, number);
            }
        }

        return tiles;
    }

    public static void encloseMapWithWall(int[][] pathMap) {
        for (int row = 0; row < pathMap.length; row++) {
            for (int column = 0; column < pathMap[row].length; column++) {
                boolean isTop = row == 0;
                boolean isRight = row == pathMap.length - 1;
                boolean isBottom = column == pathMap[row].length - 1;
                boolean isLeft = column == 0;

                if (!isTop && !isRight && !isBottom && !isLeft ) { continue; }

                pathMap[row][column] = 0;
            }
        }
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
                if (pathMap.isOccupied(curRow, curCol)) { continue; }

                pathMap.setOccupied(curRow, curCol, 1);
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
                if (pathMap.isOccupied(curRow, curCol)) { continue; }

                pathMap.setOccupied(curRow, curCol, 1);
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

    protected static Tile tryFetchingTile(Entity[][] map, int row, int column) {
        if (row < 0 || column < 0 || row >= map.length || column >= map[row].length) {
            return null;
        } else {
            return map[row][column].get(Tile.class);
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
