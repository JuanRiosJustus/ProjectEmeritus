package main.game.map.builders.utils;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.*;

import main.constants.Direction;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.map.base.TileMap;
import main.game.map.base.TileMapBuilder;
import main.game.stores.pools.asset.AssetPool;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public abstract class TileMapOperations {

    protected static final ELogger logger = ELoggerFactory.getInstance().getELogger(TileMapOperations.class);
    protected final static int ROOM_CREATION_ATTEMPTS = 4000;
    protected final static int STRUCTURE_PLACEMENT_ATTEMPTS = 200;

    protected final static Random mRandom = new Random();
    protected boolean isPathCompletelyConnected = false;
    public abstract void execute(TileMap tileMap);

//    public static List<Set<Tile>> tryCreatingRooms(TileMap tileMap, boolean outline) {
//
//        int floor = tileMap.getFloor();
//        int wall = tileMap.getWall();
//        long seed = tileMap.getSeed();
//
//        return tryCreatingRooms(tileMap.getColliderLayer(), outline, floor, wall, seed);
//    }

    public static List<Set<Tile>> tryCreatingRooms(TileMap tileMap, boolean outline) {

        String floor = tileMap.getFloor();
        String wall = tileMap.getWall();
        long seed = tileMap.getSeed();

        return tryCreatingRooms(tileMap.getColliderLayer(), outline, floor, wall, seed);
    }

    public static List<Set<Tile>> tryCreatingRooms(TileMapLayer layer, boolean outline, String floor, String wall, long seed) {

        mRandom.setSeed(seed);

        if (floor == null || wall == null || floor.equals(wall))  { return new ArrayList<>(); }

        List<Rectangle> rooms = new ArrayList<>();
        List<Rectangle> buffers = new ArrayList<>();
        List<Set<Tile>> result = new ArrayList<>();

        int bufferSize = 2;

        // Try placing rooms randomly
        for (int attempt = 0; attempt < ROOM_CREATION_ATTEMPTS; attempt++) {
            int height = mRandom.nextInt(layer.getRows() / 3);
            int width = mRandom.nextInt(layer.getColumns()) / 3;
            int row = mRandom.nextInt(layer.getRows());
            int column = mRandom.nextInt(layer.getColumns());

            // width and height must be at least greater than 3 (allows one non wall room tile)
            if (width < 3 || height < 3) { continue; }
            // ensure were not on edge of map, so we don't have to worry about room entrances
            if (row < 1 || column < 1) { continue; }
            if (row + height >= layer.getRows() - 1) { continue; }
            if (column + width >= layer.getColumns() - 1) { continue; }

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
            if (newBuffer.y + newBuffer.height >= layer.getRows()) { continue; }
            if (newBuffer.x + newBuffer.width >= layer.getColumns()) { continue; }

            if (overlaps) { continue; }

            rooms.add(newRoom);
            buffers.add(newBuffer);

            Set<Tile> floors = createRoomOnMap(layer, newRoom, "ROOM " + rooms.size() + 1, outline);
            result.add(new HashSet<>(floors));
        }

        return result;
    }

    private static Set<Tile> createRoomOnMap(TileMapLayer colliderMap, Rectangle room, String id, boolean outline) {

        // Ensure there are no colliders within the room area
        Set<Tile> tiles = new HashSet<>();
        for (int row = room.y; row < room.y + room.height; row++) {
            for (int column = room.x; column < room.x + room.width; column++) {
                if (colliderMap.isOutOfBounds(row, column)) { continue; }
                Tile tile = new Tile(row, column);
                tiles.add(tile);
                colliderMap.clear(row, column);
            }
        }

        if (!outline) { return tiles; }
        // Get the surrounding tiles and label as walls
        Rectangle buffer = room.getBounds();
        buffer.grow(1, 1);

        // Get all the wall tiles associated with the room
        Set<Tile> walls = new HashSet<>();
        Set<Tile> corners = new HashSet<>();
        for (int row = buffer.y; row < buffer.y + buffer.height; row++) {
            for (int column = buffer.x; column < buffer.x + buffer.width; column++) {
                if (colliderMap.isOutOfBounds(row, column)) { continue; }
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
                colliderMap.set(row, column, id);
            }
        }

        // have a chance of removing corner walls
        for (Tile tile : corners) {
            if (mRandom.nextBoolean()) { continue; }
            colliderMap.set(tile.row, tile.column, id);
            walls.remove(tile);
        }

        // always keep at least door tile
        List<Tile> doorCandidates = new ArrayList<>(walls);
        doorCandidates.removeAll(corners);

        // Open room tiles that are closest to the map center
        Tile door = doorCandidates.get(0);
        colliderMap.clear(door.row, door.column);

        int toRemove = mRandom.nextInt(5) + mRandom.nextInt(walls.size() / 2);
        for (int i = 0; i < toRemove; i++) {
             Tile opening = doorCandidates.remove(mRandom.nextInt(doorCandidates.size()));
             colliderMap.clear(opening.row, opening.column);
        }

        return tiles;
    }

//    public static void tryPlacingLiquids(TileMapBuilder builder) {
//
//        TileMapLayer heightMap = builder.getHeightLayer();
//        TileMapLayer liquidMap = builder.getLiquidLayer();
//        TileMapLayer colliderMap = builder.getColliderLayer();
//        int liquidType = builder.getLiquid();
//        int seaLevel = builder.getWaterLevel();
//
//        // Don't place liquids if config is not set
//        if (liquidType <= -1) { return; }
//
//        // Find the lowest height in the height map to flood
//        Queue<Point> toVisit = new LinkedList<>();
//
//        for (int row = 0; row < heightMap.getRows(); row++) {
//            for (int column = 0; column < heightMap.getColumns(row); column++) {
//
//                // Path must be usable/walkable
//                if (colliderMap.isNotUsed(row, column)) { continue; }
//
//                int currentHeight = heightMap.get(row, column);
//
//                if (currentHeight > seaLevel) { continue; }
//
//                toVisit.add(new Point(column, row));
//            }
//        }
//
//        int fill = liquidType;
//        // Fill in the height map at that area with BFS
//        Set<Point> visited = new HashSet<>();
//
//        while (toVisit.size() > 0) {
//
//            Point current = toVisit.poll();
//
//            if (visited.contains(current)) { continue; }
//            if (heightMap.isOutOfBounds(current.y, current.x)) { continue; }
//            if (colliderMap.isNotUsed(current.y, current.x)) { continue; }
//
//            visited.add(current);
//            liquidMap.set(current.y, current.x, fill);
//
//            for (Direction direction : Direction.cardinal) {
//                int nextRow = current.y + direction.y;
//                int nextColumn = current.x + direction.x;
//                // Only visit tiles that are pats and the tile is lower or equal height to current
//                if (colliderMap.isOutOfBounds(nextRow, nextColumn)) { continue; }
//                if (colliderMap.isNotUsed(nextRow, nextColumn)) { continue; }
//                if (heightMap.get(nextRow, nextColumn) > heightMap.get(current.y, current.x)) { continue; }
//                toVisit.add(new Point(nextColumn, nextRow));
//            }
//        }
//    }

    public static void tryPlacingObstruction(TileMap tileMap) {

        List<String> structures =
                AssetPool.getInstance().getSpriteMap(AssetPool.TILES_SPRITEMAP).contains("tree");
        String obstruction = structures.get(mRandom.nextInt(structures.size()));

        mRandom.setSeed(tileMap.getSeed());
//
        for (int attempt = 0; attempt < STRUCTURE_PLACEMENT_ATTEMPTS; attempt++) {
            int row = mRandom.nextInt(tileMap.getRows());
            int column = mRandom.nextInt(tileMap.getColumns());

            // Cell must not already have a structure placed on it
            Entity entity = tileMap.tryFetchingTileAt(row, column);
            Tile tile = entity.get(Tile.class);

            if (tile.isNotNavigable()) { continue; }
            if (tile.getLiquid() != null) { continue; }
//            if (tile.getLiquid() != null) { continue; }

            boolean hasEntirePathAround = true;
            for (Direction direction : Direction.values()) {
                int nextRow = row + direction.y;
                int nextColumn = column + direction.x;

                Entity adjacentEntity = tileMap.tryFetchingTileAt(nextRow, nextColumn);
                if (adjacentEntity == null) { continue; }
                Tile adjacentTile = adjacentEntity.get(Tile.class);
                if (adjacentTile.isNotNavigable()) { hasEntirePathAround = false; }
                if (adjacentTile.getLiquid() != null) { hasEntirePathAround = false; }
//                if (adjacentTile.getLiquid() != null) { hasEntirePathAround = false; }
            }

            if (!hasEntirePathAround) { continue; }
            tile.setObstruction(obstruction);
        }
    }

    public static void tryPlacingObstruction(TileMapBuilder builder) {

//        int structureType = (int) builder.getConfiguration(TileMapBuilder.DESTROYABLE_BLOCKER);
//        // Don't place structures if config is not set
//        if (structureType <= -1) { return; }
//
//        TileMapLayer pathMap = builder.getColliderLayer();
//        TileMapLayer obstruction = builder.getObstructionLayer();
//        TileMapLayer liquidMap = builder.getLiquidLayer();
//        Random random = builder.getRandom();
//
//        for (int attempt = 0; attempt < STRUCTURE_PLACEMENT_ATTEMPTS; attempt++) {
//            int row = random.nextInt(pathMap.getRows());
//            int column = random.nextInt(pathMap.getColumns());
//
//            // Cell must not already have a structure placed on it
//            if (pathMap.isNotUsed(row, column)) { continue; }
//            if (obstruction.isUsed(row, column)) { continue; }
//            // Cell must not be liquid
//            if (liquidMap.isUsed(row, column)) { continue; }
//            if (random.nextBoolean() || random.nextBoolean()) { continue; }
//
//            // If there is not path around the structure via path or another structure, try again
//            boolean hasEntirePathAround = true;
//            for (Direction direction : Direction.values()) {
//                int nextRow = row + direction.y;
//                int nextColumn = column + direction.x;
//                if (pathMap.isOutOfBounds(nextRow, nextColumn)) { continue; }
//                if (pathMap.isNotUsed(nextRow, nextColumn)) { hasEntirePathAround = false; }
//                if (obstruction.isUsed(nextRow, nextColumn)) { hasEntirePathAround = false; }
//            }
//            if (!hasEntirePathAround && random.nextBoolean()) { continue; }
//
//            obstruction.set(row, column, structureType);
//        }
    }

    public static void tryPlacingRoughTerrain(TileMapBuilder builder) {
//
//        int structureType = (int) builder.getConfiguration(TileMapBuilder.ROUGH_TERRAIN);
//
//        // Don't place structures if config is not set
//        if (structureType <= -1) { return; }
//
//        Random random = builder.getRandom();
//        TileMapLayer colliderLayer = builder.getColliderLayer();
//        TileMapLayer obstructionMap = builder.getObstructionLayer();
//        TileMapLayer liquidMap = builder.getLiquidLayer();
//
//        for (int attempt = 0; attempt < STRUCTURE_PLACEMENT_ATTEMPTS; attempt++) {
//            int row = random.nextInt(colliderLayer.getRows());
//            int column = random.nextInt(colliderLayer.getColumns());
//
//            // Cell must not already have a structure placed on it
//            if (colliderLayer.isNotUsed(row, column)) { continue; }
//            if (obstructionMap.isUsed(row, column)) { continue; }
//            // Cell must not be liquid
//            if (liquidMap.isUsed(row, column)) { continue; }
//            if (random.nextBoolean() || random.nextBoolean()) { continue; }
//
//            // If there is no path around the structure via path or another structure, try again
//            boolean hasEntirePathAround = true;
//            for (Direction direction : Direction.values()) {
//                int nextRow = row + direction.y;
//                int nextColumn = column + direction.x;
//                if (colliderLayer.isOutOfBounds(nextRow, nextColumn)) { continue; }
//                if (colliderLayer.isNotUsed(nextRow, nextColumn)) { hasEntirePathAround = false; }
//                if (obstructionMap.isUsed(nextRow, nextColumn)) { hasEntirePathAround = false; }
//            }
//            if (!hasEntirePathAround && random.nextBoolean()) { continue; }
//
//            obstructionMap.set(row, column, structureType);
//        }
    }

    public static Set<Tile> tryPlaceCollidersAroundEdges(TileMapLayer tileMapLayer) {

        Set<Tile> edges = new HashSet<>();

        for (int row = 0; row < tileMapLayer.getRows(); row++) {
            for (int column = 0; column < tileMapLayer.getColumns(row); column++) {
                boolean isTop = row == 0;
                boolean isRight = row == tileMapLayer.getRows() - 1;
                boolean isBottom = column == tileMapLayer.getColumns(row) - 1;
                boolean isLeft = column == 0;

                if (!isTop && !isRight && !isBottom && !isLeft) { continue; }

                tileMapLayer.set(row, column, "CORRIDOR");
                edges.add(new Tile(row, column));
            }
        }
        return edges;
    }

    public static Set<Tile> tryPlaceCollidersAroundEdges(TileMap TileMap) {

        TileMapLayer colliderMap = TileMap.getColliderLayer();
        Set<Tile> edges = new HashSet<>();

        for (int row = 0; row < colliderMap.getRows(); row++) {
            for (int column = 0; column < colliderMap.getColumns(row); column++) {
                boolean isTop = row == 0;
                boolean isRight = row == colliderMap.getRows() - 1;
                boolean isBottom = column == colliderMap.getColumns(row) - 1;
                boolean isLeft = column == 0;

                if (!isTop && !isRight && !isBottom && !isLeft) { continue; }

                colliderMap.set(row, column, "WALL");
                edges.add(new Tile(row, column));
            }
        }
        return edges;
    }

    public static boolean isValidConfiguration(TileMapBuilder builder) {
//        TileMapLayer colliderMap = builder.getColliderLayer();
//        // If there are no non-path calls of the map, no need to validate, every tile is valid
//        boolean hasAllColliderCells = hasAllColliderCells(colliderMap);
//        if (hasAllColliderCells) { return true; }
//        // count all the path cells
//        int bruteForcePathCount = getBruteForcePathCount(colliderMap);
//        // Get a cell from the map
//        int[] starting = getFirstPathCellNaively(colliderMap);
//        int breadthFirstSearchPathCount = getBreadthFirstSearchPathCount(colliderMap, starting);
//        int depthFirstSearchPathCount = getDepthFirstSearchPathCount(colliderMap, starting);
//        // return true if a == b && a == c
//        boolean bfsMatch = bruteForcePathCount == breadthFirstSearchPathCount;
//        boolean dfsMatch = bruteForcePathCount == depthFirstSearchPathCount;

//        return bfsMatch && dfsMatch;
        return false;
    }

    private static boolean hasAllColliderCells(TileMapLayer colliderMap) {
        // If a cell is non-zero, then it is a path cell
        for (int row = 0; row < colliderMap.getRows(); row++) {
            for (int column = 0; column < colliderMap.getColumns(row); column++) {
                if (colliderMap.isUsed(row, column)) { continue; }
                return false;
            }
        }
        return true;
    }

    private static int getBruteForcePathCount(TileMapLayer colliderMap) {
        // Count all non-zero a.k.a. path call, values to determine all the valid path-cells
        int count = 0;
        for (int row = 0; row < colliderMap.getRows(); row++) {
            for (int column = 0; column < colliderMap.getColumns(row); column++) {
                if (colliderMap.isUsed(row, column)) { continue; }
                count++;
            }
        }
        return count;
    }
    private static int getBreadthFirstSearchPathCount(TileMapLayer colliderMap, int[] starting) {
        Set<Point> visited = new HashSet<>();
        Queue<Point> toVisit = new LinkedList<>();
        toVisit.add(new Point(new Point(starting[0], starting[1])));

        while (!toVisit.isEmpty()) {

            Point visiting = toVisit.poll();

            boolean isVisited = visited.contains(visiting);
            if (isVisited || visiting == null) { continue; }
            boolean isOutOfBounds = colliderMap.isOutOfBounds(visiting.y, visiting.x);
            if (isOutOfBounds) { continue; }
            boolean isPathCell = colliderMap.isNotUsed(visiting.y, visiting.x);
            if (!isPathCell) { continue; }

            visited.add(visiting);

            for (Direction direction : Direction.cardinal) {
                toVisit.add(new Point(visiting.x + direction.x, visiting.y + direction.y));
            }
        }
        return visited.size();
    }

    private static int getDepthFirstSearchPathCount(TileMapLayer colliderMap, int[] starting) {
        Set<Point> visited = new HashSet<>();
        Stack<Point> toVisit = new Stack<>();
        toVisit.add(new Point(starting[0], starting[0]));

        while (!toVisit.isEmpty()) {
            Point visiting = toVisit.pop();

            boolean isVisited = visited.contains(visiting);
            if (isVisited || visiting == null) { continue; }
            boolean isOutOfBounds = colliderMap.isOutOfBounds(visiting.y, visiting.x);
            if (isOutOfBounds) { continue; }
            boolean isPathCell = colliderMap.isNotUsed(visiting.y, visiting.x);
            if (!isPathCell) { continue; }

            visited.add(visiting);

            for (Direction direction : Direction.cardinal) {
                toVisit.add(new Point(visiting.x + direction.x, visiting.y + direction.y));
            }
        }
        return visited.size();
    }


    private static int[] getFirstPathCellNaively(TileMapLayer colliderMap) {
        // Get the first path cell, (non-collider)
        for (int row = 0; row < colliderMap.getRows(); row++) {
            for (int column = 0; column < colliderMap.getColumns(row); column++) {
                if (colliderMap.isUsed(row, column)) { continue; }
                return new int[]{ row, column };
            }
        }
        // Should NEVER get here
        return new int[]{ 0, 0 };
    }

//    private static Point getFirstPathCellNaively(TileMapLayer colliderMap) {
//        // Get the first path cell, (non-collider)
//        for (int row = 0; row < colliderMap.getRows(); row++) {
//            for (int column = 0; column < colliderMap.getColumns(row); column++) {
//                if (colliderMap.isUsed(row, column)) { continue; }
//                return new Point(column, row);
//            }
//        }
//        // Should NEVER be null
//        return new Point;
//    }

    public static List<Set<Tile>> tryConnectingRooms(TileMap tileMap, List<Set<Tile>> rooms) {

        if (rooms.isEmpty()) { return new ArrayList<>(); }
        TileMapLayer colliderMap = tileMap.getColliderLayer();

        mRandom.setSeed(tileMap.getSeed());

        List<Set<Tile>> halls = new ArrayList<>();
        Set<Tile> hall;
        int size;
        // connect each of the rooms, one after the other
        for (int index = 1; index < rooms.size(); index++) {
            Set<Tile> previousRoom = rooms.get(index - 1);
            Set<Tile> currentRoomTile = rooms.get(index);
            size = mRandom.nextBoolean() ? 1 : 2;
            hall = connectViaTunneling(colliderMap, currentRoomTile, previousRoom, size);
            halls.add(hall);
        }
        // connect the first and last room
        size = mRandom.nextBoolean() ? 1 : 2;
        hall = connectViaTunneling(colliderMap, rooms.get(0), rooms.get(rooms.size() - 1), size);
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

    public static Set<Tile> connectViaTunneling(TileMapLayer colliderMap, Set<Tile> room1, Set<Tile> room2, int size) {
        // Get random tiles to connect rooms
        List<Tile> randomizerList = new ArrayList<>(room1);
        Collections.shuffle(randomizerList);
        Tile pointFromRoom1 = randomizerList.get(0);

        randomizerList = new ArrayList<>(room2);
        Collections.shuffle(randomizerList);
        Tile pointFromRoom2 = randomizerList.get(0);

        Set<Tile> columnTunnel =
                connectRows(colliderMap, pointFromRoom1.row, pointFromRoom2.row, pointFromRoom1.column, size);
        Set<Tile> rowTunnel =
                connectColumns(colliderMap, pointFromRoom1.column, pointFromRoom2.column, pointFromRoom2.row, size);

        Set<Tile> corridor = new HashSet<>();
        corridor.addAll(rowTunnel);
        corridor.addAll(columnTunnel);

        return corridor;
    }

    private static Set<Tile> connectRows(TileMapLayer colliderMap, int startingRow, int endingRow, int column, int size) {
        int startRow = Math.min(startingRow, endingRow);
        int endRow = Math.max(startingRow, endingRow) + size;
        Set<Tile> tiles = new HashSet<>();
        for (int curRow = startRow; curRow < endRow; curRow++) {
            for (int curCol = column; curCol < column + size; curCol++) {
                if (colliderMap.isOutOfBounds(curRow, curCol)) { continue; }
                if (colliderMap.isOutOfBounds(curRow + 1, curCol + 1)) { continue; }
                // if the tile is already a path, go to next
                if (colliderMap.isNotUsed(curRow, curCol)) { continue; }

                colliderMap.clear(curRow, curCol);
                tiles.add(new Tile(curRow, curCol));
            }
        }
        return tiles;
    }

    private static Set<Tile> connectColumns(TileMapLayer colliderMap, int starting, int ending, int row, int size) {
        // Check which point is actually the first, numerically
        int startCol = Math.min(starting, ending);
        int endCol = Math.max(starting, ending) + size;
        Set<Tile> tiles = new HashSet<>();
        for (int curCol = startCol; curCol < endCol; curCol++) {
            for (int curRow = row; curRow < row + size; curRow++) {
                if (colliderMap.isOutOfBounds(curRow, curCol)) { continue; }
                if (colliderMap.isOutOfBounds(curRow + 1, curCol + 1)) { continue; }
                // if the tile is already a path, go to next
                if (colliderMap.isNotUsed(curRow, curCol)) { continue; }

                colliderMap.clear(curRow, curCol);
                tiles.add(new Tile(curRow, curCol));
            }
        }
        return tiles;
    }


    public static void tryPlacingSingleExit(TileMapBuilder builder) {
//        if (builder.getExit() <= 0) { return; }
//        Random random = builder.getRandom();
//        boolean isPlaced = false;
//        TileMapLayer heightMap = builder.getHeightLayer();
//        TileMapLayer pathMap = builder.getPathLayer();
////        TileMapLayer structureMap = builder.getGreaterStructureLayer();
//        TileMapLayer liquidMap = builder.getLiquidLayer();
//        TileMapLayer exitMap = builder.getExitMapLayer();
//        int value = 0;//builder.getExit();
//        while(isPlaced == false) {
//            int row = random.nextInt(exitMap.getRows());
//            int column = random.nextInt(exitMap.getColumns(row));
//            if (builder.isUsed(row, column)) { continue; }
//            exitMap.set(row, column, value);
//            isPlaced = true;
//        }
    }

    public static void tryPlacingExits(TileMapBuilder tileMapBuilder) {
//        int exit = (int) tileMapBuilder.getConfiguration(TileMapBuilder.EXIT_STRUCTURE);
//
//        if (exit < 0) { return; }
//        // Divide the map into
//
//        TileMapLayer obstructionLayer = tileMapBuilder.getObstructionLayer();
//        TileMapLayer pathLayer = tileMapBuilder.getColliderLayer();
//
//        boolean isPlaced = false;
//        while (!isPlaced) {
//            int row = tileMapBuilder.getRandom().nextInt(tileMapBuilder.getRows());
//            int column = tileMapBuilder.getRandom().nextInt(tileMapBuilder.getColumns());
//            boolean isOpen = obstructionLayer.isNotUsed(row, column);
//            if (!isOpen) { continue; }
//            if (!pathLayer.isUsed(row, column)) { continue; }
//            obstructionLayer.set(row, column, exit);
//            isPlaced = true;
//        }
    }

    public static void tryPlacingEntrance(TileMapBuilder tileMapBuilder) {
//        int entrance = (int) tileMapBuilder.getConfiguration(TileMapBuilder.ENTRANCE_STRUCTURE);
//
//        if (entrance < 0) { return; }
//        // Divide the map into
//
//        TileMapLayer obstructionLayer = tileMapBuilder.getObstructionLayer();
//        TileMapLayer pathLayer = tileMapBuilder.getColliderLayer();
//
//        boolean isPlaced = false;
//        while (!isPlaced) {
//            int row = tileMapBuilder.getRandom().nextInt(tileMapBuilder.getRows());
//            int column = tileMapBuilder.getRandom().nextInt(tileMapBuilder.getColumns());
//            boolean isOpen = obstructionLayer.isNotUsed(row, column);
//            if (!isOpen) { continue; }
//            if (!pathLayer.isUsed(row, column)) { continue; }
//            obstructionLayer.set(row, column, entrance);
//            isPlaced = true;
//        }
    }
}
