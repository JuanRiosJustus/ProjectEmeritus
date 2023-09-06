package main.game.map.builders.utils;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import designer.fundamentals.Direction;
import main.game.components.Tile;
import main.game.map.builders.TileMapBuilder;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class TileMapOperations {

    protected final static int ROOM_CREATION_ATTEMPTS = 2000;
    protected final static int STRUCTURE_PLACEMENT_ATTEMPTS = 200;
    protected static final ELogger logger = ELoggerFactory.getInstance().getELogger(TileMapOperations.class);

    public static List<Set<Tile>> tryCreatingRooms(TileMapBuilder builder, boolean outline) {

        if (builder.getFloor() == builder.getWall())  { return new ArrayList<>(); }
        if (builder.getFloor() <= -1 || builder.getWall() <= -1)  { return new ArrayList<>(); }

        TileMapLayer pathMap = builder.getPathLayer();
        Random random = builder.getRandom();

        List<Rectangle> rooms = new ArrayList<>();
        List<Rectangle> buffers = new ArrayList<>();
        List<Set<Tile>> result = new ArrayList<>();

        int bufferSize = 2;

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
            Rectangle newBuffer = newRoom.getBounds();
            newBuffer.grow(bufferSize, bufferSize);

            // check if this room intersects with another room
            boolean overlaps = false;
            for (Rectangle room : rooms) {
                if (room.intersects(newRoom)) { overlaps = true; break; }
            }

            // Check if the rooms buffer zone intersects with another buffer zone
            for (Rectangle bufferZone : buffers) {
                if (bufferZone.intersects(newBuffer)) { overlaps = true; break; }
            }
            
            // check that the room does not touch the edges of the map. X Y is left most corner of room
            if (newBuffer.y + newBuffer.height >= pathMap.getRows()) { continue; }
            if (newBuffer.x + newBuffer.width >= pathMap.getColumns()) { continue; }

            if (overlaps) { continue; }

            rooms.add(newRoom);
            buffers.add(newBuffer);

            Set<Tile> floors = createRoomOnMap(builder, newRoom, rooms.size() + 1, outline);
            result.add(new HashSet<>(floors));
        }

        return result;
    }

    private static Set<Tile> createRoomOnMap(TileMapBuilder builder, Rectangle room, int id, boolean outline) {
        TileMapLayer pathMap = builder.getPathLayer();
        Random random = builder.getRandom();

        // Fill out the room in the schema path map
        Set<Tile> tiles = new HashSet<>();
        for (int row = room.y; row < room.y + room.height; row++) {
            for (int column = room.x; column < room.x + room.width; column++) {
                if (pathMap.isOutOfBounds(row, column)) { continue; }
                Tile tile = new Tile(row, column);
                tiles.add(tile);
                pathMap.set(row, column, id);
            }
        }

        if (outline == false) { return tiles; }
        // Get the surrounding tiles and label as walls
        Rectangle buffer = room.getBounds();
        buffer.grow(1, 1);

        // Get all the wall tiles associated with the room
        Set<Tile> walls = new HashSet<>();
        Set<Tile> corners = new HashSet<>();
        for (int row = buffer.y; row < buffer.y + buffer.height; row++) {
            for (int column = buffer.x; column < buffer.x + buffer.width; column++) {
                if (pathMap.isOutOfBounds(row, column)) { continue; }
                Tile tile = new Tile(row, column);
                if (tiles.contains(tile)) { continue; }
                // if (tiles.contains(tile)) { continue; } // Feature? TODO
                // If we make large blocks of the rooms, it looks pretty cool
                boolean topLeft = row == buffer.y && column == buffer.x;
                boolean topRight = row == buffer.y && column == buffer.x + buffer.width -1;
                boolean bottomLeft = row == buffer.y + buffer.height - 1 && column == buffer.x;
                boolean bottomRight = row == buffer.y + buffer.height - 1 && column == buffer.x + buffer.width - 1;

                if (topLeft || topRight || bottomRight || bottomLeft) {
                    corners.add(tile);
                }
                walls.add(tile);
                pathMap.clear(row, column);
//                pathMap.set(row, column, 0);
            }
        }

        // have a chance of removing corner walls
        for (Tile tile : corners) {
            if (random.nextBoolean()) { continue; }
            pathMap.set(tile.row, tile.column, id);
            walls.remove(tile);
        }

        // always keep at least door tile
        List<Tile> doorCandidates = new ArrayList<>(walls);
        doorCandidates.removeAll(corners);

        // Open room tiles that are closest to the map center
        Tile door = doorCandidates.get(0);
        pathMap.set(door.row, door.column, id);

        int toRemove = random.nextInt(5) + random.nextInt(walls.size() / 2);
        for (int i = 0; i < toRemove; i++) {
             Tile opening = doorCandidates.remove(random.nextInt(doorCandidates.size()));
             pathMap.clear(opening.row, opening.column);
        }

        return tiles;
    }

    public static void tryPlacingLiquids(TileMapBuilder builder) {

        TileMapLayer heightMap = builder.getHeightLayer();
        TileMapLayer specialMap = builder.getLiquidLayer();
        TileMapLayer pathMap = builder.getPathLayer();
        int liquidType = builder.getLiquid();
        int seaLevel = builder.getSeaLevel();

        // Don't place liquids if config is not set
        if (liquidType <= -1) { return; }

        // Find the lowest height in the height map to flood
        Queue<Point> toVisit = new LinkedList<>();

        for (int row = 0; row < heightMap.getRows(); row++) {
            for (int column = 0; column < heightMap.getColumns(row); column++) {

                // Path must be usable/walkable
                if (pathMap.isNotUsed(row, column)) { continue; }

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

    public static void tryPlacingStructures(TileMapBuilder builder) {

        TileMapLayer heightMap = builder.getHeightLayer();
        TileMapLayer specialMap = builder.getLiquidLayer();
        TileMapLayer pathMap = builder.getPathLayer();
        TileMapLayer structureMap = builder.getStructureLayer();
        TileMapLayer liquidMap = builder.getLiquidLayer();
        int structureType = builder.getStructure();
        Random random = builder.getRandom();

        // Don't place structures if config is not set
        if (structureType <= -1) { return; }

        for (int attempt = 0; attempt < STRUCTURE_PLACEMENT_ATTEMPTS; attempt++) {
            int row = random.nextInt(pathMap.getRows());
            int column = random.nextInt(pathMap.getColumns());

            // Cell must not already have a structure placed on it
            if (pathMap.isNotUsed(row, column)) { continue; }
            if (structureMap.isUsed(row, column)) { continue; }
            // Cell must not be liquid
            if (liquidMap.isUsed(row, column)) { continue; }
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

            structureMap.set(row, column, structureType);
        }
    }

    public static Set<Tile> createWallForMap(TileMapBuilder builder) {

        if (builder.getFloor() == builder.getWall())  { return new HashSet<>(); }
        if (builder.getFloor() <= -1 || builder.getWall() <= -1)  { return new HashSet<>(); }

        TileMapLayer pathMap = builder.getPathLayer();
        Set<Tile> walling = new HashSet<>();
        for (int row = 0; row < pathMap.getRows(); row++) {
            for (int column = 0; column < pathMap.getColumns(row); column++) {
                boolean isTop = row == 0;
                boolean isRight = row == pathMap.getRows() - 1;
                boolean isBottom = column == pathMap.getColumns(row) - 1;
                boolean isLeft = column == 0;

                if (!isTop && !isRight && !isBottom && !isLeft) { continue; }

                pathMap.clear(row, column);
                walling.add(new Tile(row, column));
            }
        }
        return walling;
    }
    
    public static boolean isValidPath(TileMapBuilder builder) {
        TileMapLayer pathMap = builder.getPathLayer();
        // If there are no non-path calls of the map, no need to validate, every tile is valid
        boolean hasAllPaths = hasAllPathCells(pathMap);
        if (hasAllPaths) { return true; }
        // count all the path cells
        int bruteForcePathCount = getBruteForcePathCount(pathMap);
        // Get a cell from the map
        Point starting = getFirstPathCellNaively(pathMap);
        int breadthFirstSearchPathCount = getBreadthFirstSearchPathCount(pathMap, starting);
        int depthFirstSearchPathCount = getDepthFirstSearchPathCount(pathMap, starting);
        // return true if a == b && a == c
        boolean bfsMatch = bruteForcePathCount == breadthFirstSearchPathCount;
        boolean dfsMatch = bruteForcePathCount == depthFirstSearchPathCount;

        return bfsMatch && dfsMatch;
    }

    private static boolean hasAllPathCells(TileMapLayer pathMap) {
        // If a cell is non-zero, then it is a path cell
        for (int row = 0; row < pathMap.getRows(); row++) {
            for (int column = 0; column < pathMap.getColumns(); column++) {
                if (pathMap.isUsed(row, column)) { continue; }
                return false;
            }
        }
        return true;
    }

    private static int getBruteForcePathCount(TileMapLayer pathMap) {
        // Count all non-zero a.k.a. path call, values to determine all the valid path-cells
        int count = 0;
        for (int row = 0; row < pathMap.getRows(); row++) {
            for (int column = 0; column < pathMap.getColumns(); column++) {
                if (pathMap.isNotUsed(row, column)) { continue; }
                count++;
            }
        }
        return count;
    }

    private static int getBreadthFirstSearchPathCount(TileMapLayer pathMap, Point starting) {
        // Count all non-zero values to determine all the valid path-cells
        Set<Point> visited = new HashSet<>();
        Queue<Point> toVisit = new LinkedList<>();
        toVisit.add(starting);

        while (toVisit.size() > 0) {

            Point visiting = toVisit.poll();

            boolean isVisited = visited.contains(visiting);
            if (isVisited) { continue; }
            boolean isOutOfBounds = pathMap.isOutOfBounds(visiting.y, visiting.x);
            if (isOutOfBounds) { continue; }
            boolean isPathCell = pathMap.isUsed(visiting.y, visiting.x); //isPathCell(pathMap, visiting.y, visiting.x);
            if (!isPathCell) { continue; }

            visited.add(visiting);

            for (Direction direction : Direction.cardinal) {
                toVisit.add(new Point(visiting.x + direction.x, visiting.y + direction.y));
            }
        }
        return visited.size();
    }

    private static int getDepthFirstSearchPathCount(TileMapLayer pathMap, Point starting) {
        // Count all non-zero values to determine all the valid path-cells
        Set<Point> visited = new HashSet<>();
        Stack<Point> toVisit = new Stack<>();
        toVisit.add(starting);

        while (toVisit.size() > 0) {
            Point visiting = toVisit.pop();

            boolean isVisited = visited.contains(visiting);
            if (isVisited) { continue; }
            boolean isOutOfBounds = pathMap.isOutOfBounds(visiting.y, visiting.x);
            if (isOutOfBounds) { continue; }
            boolean isPathCell = pathMap.isUsed(visiting.y, visiting.x);
            if (!isPathCell) { continue; }

            visited.add(visiting);

            for (Direction direction : Direction.cardinal) {
                toVisit.add(new Point(visiting.x + direction.x, visiting.y + direction.y));
            }
        }
        return visited.size();
    }

    private static Point getFirstPathCellNaively(TileMapLayer pathMap) {
        // Get the first path cell, (non-zero)
        for (int row = 0; row < pathMap.getRows(); row++) {
            for (int column = 0; column < pathMap.getColumns(); column++) {
                if (pathMap.isNotUsed(row, column)) { continue; }
                return new Point(column, row);
            }
        }
        // Should NEVER be null
        return null;
    }

    public static List<Set<Tile>> tryConnectingRooms(TileMapBuilder builder, List<Set<Tile>> rooms) {
        TileMapLayer pathMap = builder.getPathLayer();
        Random random = builder.getRandom();

        List<Set<Tile>> halls = new ArrayList<>();
        Set<Tile> hall;
        int size;
        // connect each of the rooms, one after the other
        for (int index = 1; index < rooms.size(); index++) {
            Set<Tile> previousRoom = rooms.get(index - 1);
            Set<Tile> currentRoomTile = rooms.get(index);
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

//    public static List<Set<Point>> tryConnectingRooms(TileMapBuilder builder, List<Set<Point>> rooms) {
//        TileMapLayer pathMap = builder.getPathLayer();
//        Random random = builder.getRandom();
//
//        List<Set<Point>> halls = new ArrayList<>();
//        Set<Point> hall;
//        int size;
//        // connect each of the rooms, one after the other
//        for (int index = 1; index < rooms.size(); index++) {
//            Set<Point> previousRoom = rooms.get(index - 1);
//            Set<Point> currentRoomTile = rooms.get(index);
//            size = random.nextBoolean() ? 1 : 2;
//            hall = connectViaTunneling(pathMap, currentRoomTile, previousRoom, size);
//            halls.add(hall);
//        }
//        // connect the first and last room
//        size = random.nextBoolean() ? 1 : 2;
//        hall = connectViaTunneling(pathMap, rooms.get(0), rooms.get(rooms.size() - 1), size);
//        halls.add(hall);
//
//        return halls;
//    }

    public static Set<Tile> connectViaTunneling(TileMapLayer pathMap, Set<Tile> room1, Set<Tile> room2, int size) {
        // Get random tiles to connect rooms
        List<Tile> randomizerList = new ArrayList<>(room1);
        Collections.shuffle(randomizerList);
        Tile pointFromRoom1 = randomizerList.get(0);

        randomizerList = new ArrayList<>(room2);
        Collections.shuffle(randomizerList);
        Tile pointFromRoom2 = randomizerList.get(0);

        Set<Tile> columnTunnel = connectRows(pathMap, pointFromRoom1.row, pointFromRoom2.row, pointFromRoom1.column, size);

        Set<Tile> rowTunnel = connectColumns(pathMap, pointFromRoom1.column, pointFromRoom2.column, pointFromRoom2.row, size);

        Set<Tile> corridor = new HashSet<>();
        corridor.addAll(rowTunnel);
        corridor.addAll(columnTunnel);

        return corridor;
    }

    private static Set<Tile> connectRows(TileMapLayer pathMap, int startingRow, int endingRow, int column, int size) {
        int startRow = Math.min(startingRow, endingRow);
        int endRow = Math.max(startingRow, endingRow) + size;
        Set<Tile> tiles = new HashSet<>();
        for (int curRow = startRow; curRow < endRow; curRow++) {
            for (int curCol = column; curCol < column + size; curCol++) {
                if (pathMap.isOutOfBounds(curRow, curCol)) { continue; }
                if (pathMap.isOutOfBounds(curRow + 1, curCol + 1)) { continue; }
                // if the tile is already a path, go to next
                if (pathMap.isUsed(curRow, curCol)) { continue; }

                pathMap.set(curRow, curCol, 1);
                tiles.add(new Tile(curRow, curCol));
            }
        }
        return tiles;
    }

    private static Set<Tile> connectColumns(TileMapLayer pathMap, int starting, int ending, int row, int size) {
        // Check which point is actually the first, numerically
        int startCol = Math.min(starting, ending);
        int endCol = Math.max(starting, ending) + size;
        Set<Tile> tiles = new HashSet<>();
        for (int curCol = startCol; curCol < endCol; curCol++) {
            for (int curRow = row; curRow < row + size; curRow++) {
                if (pathMap.isOutOfBounds(curRow, curCol)) { continue; }
                if (pathMap.isOutOfBounds(curRow + 1, curCol + 1)) { continue; }
                // if the tile is already a path, go to next
                if (pathMap.isUsed(curRow, curCol)) { continue; }

                pathMap.set(curRow, curCol, 1);
                tiles.add(new Tile(curRow, curCol));
            }
        }
        return tiles;
    }

        
    public static void tryPlacingSingleExit(TileMapBuilder builder) {
        if (builder.getExit() <= 0) { return; }
        Random random = builder.getRandom();
        boolean isPlaced = false;
        TileMapLayer heightMap = builder.getHeightLayer();
        TileMapLayer pathMap = builder.getPathLayer();
        TileMapLayer structureMap = builder.getStructureLayer();
        TileMapLayer liquidMap = builder.getLiquidLayer();
        TileMapLayer exitMap = builder.getExitMapLayer();
        int value = builder.getExit();
        while(isPlaced == false) {
            int row = random.nextInt(exitMap.getRows());
            int column = random.nextInt(exitMap.getColumns(row));
            if (builder.isUsed(row, column)) { continue; }
            exitMap.set(row, column, value);
            isPlaced = true;
        }
    }
}
