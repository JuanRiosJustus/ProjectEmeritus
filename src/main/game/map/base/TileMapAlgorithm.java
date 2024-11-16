package main.game.map.base;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.*;

import main.constants.Direction;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.stores.pools.asset.AssetPool;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public abstract class TileMapAlgorithm {

    protected static final ELogger logger = ELoggerFactory.getInstance().getELogger(TileMapAlgorithm.class);
    protected final static int ROOM_CREATION_ATTEMPTS = 4000;
    protected final static int STRUCTURE_PLACEMENT_ATTEMPTS = 200;
    protected final static int MINIMUM_ROOM_SIZE = 3;
    protected final static Random mRandom = new Random();
    protected boolean isPathCompletelyConnected = false;
    public abstract TileMap evaluate(TileMapParameters parameters);

    public static void placeObstructions(TileMap tileMap) {

        List<String> structures = AssetPool.getInstance().getBucket("structures");

        mRandom.setSeed(tileMap.getSeed());
        String obstruction = structures.get(mRandom.nextInt(structures.size()));

        for (int attempt = 0; attempt < STRUCTURE_PLACEMENT_ATTEMPTS; attempt++) {
            int row = mRandom.nextInt(tileMap.getRows());
            int column = mRandom.nextInt(tileMap.getColumns());

            // Cell must not already have a structure placed on it
            Entity tileEntity = tileMap.tryFetchingEntityAt(row, column);
            Tile tile = tileEntity.get(Tile.class);

            if (tile.isNotNavigable()) { continue; }
            if (tile.getLiquid() != null) { continue; }
            if (tile.hasObstruction()) { continue; }
            if (mRandom.nextBoolean()) { continue; }

            boolean hasEntirePathAround = true;
            for (Direction direction : Direction.values()) {
                int nextRow = row + direction.y;
                int nextColumn = column + direction.x;

                Entity adjacentEntity = tileMap.tryFetchingEntityAt(nextRow, nextColumn);
                if (adjacentEntity == null) { continue; }
                Tile adjacentTile = adjacentEntity.get(Tile.class);
                if (adjacentTile.isNotNavigable()) { hasEntirePathAround = false; }
                if (adjacentTile.getLiquid() != null) { hasEntirePathAround = false; }
                if (adjacentTile.getObstruction() != null) { hasEntirePathAround = false; }
            }

            if (!hasEntirePathAround) { continue; }
            tileMap.set(Tile.OBSTRUCTION, row, column, obstruction);
//            debug(tileMap, Tile.OBSTRUCTION);
        }
    }

    public static void placeTerrain(TileMap tileMap, boolean useColliderAsWall) {
        String wall = (String) tileMap.getConfiguration(TileMapParameters.WALL_KEY);
        String floor = (String) tileMap.getConfiguration(TileMapParameters.FLOOR_KEY);

        for (int row = 0; row < tileMap.getRows(); row++) {
            for (int column = 0; column < tileMap.getColumns(row); column++) {
                if (tileMap.isUsed(Tile.COLLIDER, row, column)) {
                    tileMap.set(Tile.TERRAIN, row, column, useColliderAsWall ? wall : floor);
                } else {
                    tileMap.set(Tile.TERRAIN, row, column, floor);
                }
            }
        }
    }

    public static void completeTerrainLiquidAndObstruction(TileMap tileMap, boolean useColliderAsWall) {
        TileMapAlgorithm.placeTerrain(tileMap, useColliderAsWall);
        TileMapAlgorithm.placeLiquids(tileMap);
        TileMapAlgorithm.placeObstructions(tileMap);
    }

    public static void placeLiquids(TileMap tileMap) {

        String liquidType = (String) tileMap.getConfiguration(TileMapParameters.LIQUID_KEY);
        int seaLevel = (int) tileMap.getConfiguration(TileMapParameters.WATER_LEVEL_KEY);

        // Don't place liquids if config is not set
        if (liquidType == null) { return; }
        // Find the lowest height in the height map to flood
        Queue<Point> toVisit = new LinkedList<>();

        for (int row = 0; row < tileMap.getRows(); row++) {
            for (int column = 0; column < tileMap.getColumns(row); column++) {

                // Path must be usable/walkable
                if (tileMap.isUsed(Tile.COLLIDER, row, column)) { continue; }

                int currentHeight = (int) tileMap.get(Tile.HEIGHT, row, column);
                if (currentHeight > seaLevel) { continue; }

                toVisit.add(new Point(column, row));
            }
        }

        // Fill in the height map at that area with BFS
        Set<Point> visited = new HashSet<>();

        while (!toVisit.isEmpty()) {

            Point current = toVisit.poll();

            if (visited.contains(current)) { continue; }
            if (tileMap.isOutOfBounds(current.y, current.x)) { continue; }
            if (tileMap.isUsed(Tile.COLLIDER, current.y, current.x)) { continue; }

            visited.add(current);
            tileMap.set(Tile.LIQUID, current.y, current.x, liquidType);

            for (Direction direction : Direction.cardinal) {
                int nextRow = current.y + direction.y;
                int nextColumn = current.x + direction.x;
                // Only visit tiles that are pats and the tile is lower or equal height to current
                if (tileMap.isOutOfBounds(nextRow, nextColumn)) { continue; }
                if (tileMap.isUsed(Tile.COLLIDER, nextRow, nextColumn)) { continue; }
                int nextHeight = (int) tileMap.get(Tile.HEIGHT, nextRow, nextColumn);
                int currentHeight = (int) tileMap.get(Tile.HEIGHT, current.y, current.x);
                if (nextHeight > currentHeight) { continue; }
                toVisit.add(new Point(nextColumn, nextRow));
            }
        }
    }

    public static List<Set<Tile>> createTileRooms(TileMap tileMap, boolean outlineOnly) {

        String floor = (String) tileMap.getConfiguration(TileMapParameters.FLOOR_KEY);
        String wall = (String) tileMap.getConfiguration(TileMapParameters.WALL_KEY);
        long seed = (long) tileMap.getConfiguration(TileMapParameters.SEED_KEY);

        List<Set<Tile>> rooms = tryCreatingRooms(tileMap, floor, wall, seed, outlineOnly);

        return rooms;
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
            if (width < MINIMUM_ROOM_SIZE || height < MINIMUM_ROOM_SIZE) { continue; }
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

            Set<Tile> floors = getAllTilesOfRoom(layer, newRoom, "ROOM " + rooms.size() + 1, outline);
//            Set<Tile> floors = createRoomOnMap(, newRoom, "ROOM " + rooms.size() + 1, outline);
            result.add(new HashSet<>(floors));
        }

        return result;
    }

    public static List<Set<Tile>> tryCreatingRooms(TileMap tileMap, String floor, String wall, long seed, boolean outlineOnly) {
        if (floor == null || wall == null || floor.equals(wall))  { return new ArrayList<>(); }

        mRandom.setSeed(seed);

        List<Rectangle> rooms = new ArrayList<>();
        List<Rectangle> buffers = new ArrayList<>();
        List<Set<Tile>> createdRooms = new ArrayList<>();

        int bufferSize = 1;

        // Try placing rooms randomly
        for (int attempt = 0; attempt < ROOM_CREATION_ATTEMPTS; attempt++) {
            int height = mRandom.nextInt(tileMap.getRows() / 3);
            int width = mRandom.nextInt(tileMap.getColumns()) / 3;
            int row = mRandom.nextInt(tileMap.getRows());
            int column = mRandom.nextInt(tileMap.getColumns());

            // width and height must be at least greater than (MINIMUM ROOM SIZE) (allows one non wall room tile)
            if (width < MINIMUM_ROOM_SIZE || height < MINIMUM_ROOM_SIZE) { continue; }
            // ensure were not on edge of map, so we don't have to worry about room entrances
            if (row < 1 || column < 1) { continue; }
            if (row + height >= tileMap.getRows() - 1) { continue; }
            if (column + width >= tileMap.getColumns() - 1) { continue; }

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
            if (newBuffer.y + newBuffer.height >= tileMap.getRows()) { continue; }
            if (newBuffer.x + newBuffer.width >= tileMap.getColumns()) { continue; }

            if (overlaps) { continue; }

            rooms.add(newRoom);
            buffers.add(newBuffer);

            Set<Tile> room = getAllTilesOfRoom(tileMap, newRoom);
            if (outlineOnly) {
                List<Tile> shuffledWallTiles = new ArrayList<>(getWallTilesOfRoom(tileMap, newRoom));
                Collections.shuffle(shuffledWallTiles);
                for (int i = 0; i < Math.min(5, shuffledWallTiles.size()); i++) { shuffledWallTiles.remove(0); }
                for (int i = 0; i < shuffledWallTiles.size(); i++) {
                    if (mRandom.nextFloat() > .8) { shuffledWallTiles.remove(0); }
                }
                room = new HashSet<>(shuffledWallTiles);
            }

            createdRooms.add(room);
        }

        return createdRooms;
    }

    public static Set<Tile> getAllTilesOfRoom(TileMap tileMap, Rectangle room) {

        // Ensure there are no colliders within the room area
        Set<Tile> roomTiles = new HashSet<>();
        for (int row = room.y; row < room.y + room.height; row++) {
            for (int column = room.x; column < room.x + room.width; column++) {
                if (tileMap.isOutOfBounds(row, column)) { continue; }
//                Entity entity = tileMap.tryFetchingTileAt(row, column);
//                Tile tile = entity.get(Tile.class);
                roomTiles.add(new Tile(row, column));
            }
        }

        return roomTiles;
    }

    public static Set<Tile> getWallTilesOfRoom(TileMap tileMap, Rectangle room) {

        // Ensure there are no colliders within the room area
        Set<Tile> roomTiles = getAllTilesOfRoom(tileMap, room);

        // Get all the wall tiles associated with the room
        Set<Tile> walls = new HashSet<>();
        for (Tile tile : roomTiles) {
            boolean isTop = tile.row == room.y;
            boolean isRight = tile.column == room.x + room.width - 1;
            boolean isBottom = tile.row == room.y + room.height - 1;
            boolean isLeft = tile.column == room.x;
            if (isTop || isRight || isBottom || isLeft) {
                walls.add(tile);
            }
        }

        return walls;
    }

    public static Set<Tile> getCornerTilesOfRoom(TileMap tileMap, Rectangle room) {

        // Ensure there are no colliders within the room area
        Set<Tile> roomTiles = getAllTilesOfRoom(tileMap, room);

        // Get all the wall tiles associated with the room
        Set<Tile> corners = new HashSet<>();
        for (Tile tile : roomTiles) {
            boolean isTop = tile.row == room.y;
            boolean isRight = tile.column == room.x + room.width - 1;
            boolean isBottom = tile.row == room.y + room.height - 1;
            boolean isLeft = tile.column == room.x;

            if ((isTop && isLeft) || (isTop && isRight) || (isBottom && isLeft) || (isBottom && isRight)) {
                corners.add(tile);
            }
        }

        return corners;
    }

    public static Set<Tile> carveRoomOnTileMap(TileMap tileMap, Rectangle room, String layer, String id) {

        // Ensure there are no colliders within the room area
        Set<Tile> roomTiles = getAllTilesOfRoom(tileMap, room);
        Set<Tile> wallTiles = getWallTilesOfRoom(tileMap, room);
        Set<Tile> cornerTiles = getCornerTilesOfRoom(tileMap, room);

        // Clear the entire room
//        for (Tile tile : roomTiles) {
//            tileMap.clear(layer, tile.row, tile.column);
//        }
        carveIntoMap(tileMap, roomTiles, layer, null);

        // Create walls for room
        for (Tile tile : wallTiles) {
            tileMap.set(layer, tile.row, tile.column, id);
        }

        // Randomly remove some of the wall tiles to make things interesting
        Set<Tile> nonCornerWallTiles = new HashSet<>(wallTiles);
        nonCornerWallTiles.removeAll(cornerTiles);
        for (Tile tile : nonCornerWallTiles) {
            if (mRandom.nextBoolean()) { continue; }
            tileMap.clear(layer, tile.row, tile.column);
        }

        // Always create a surefire door
        Tile door = nonCornerWallTiles.iterator().next();
//        tileMap.clear(layer, door.row, door.column);
        carveIntoMap(tileMap, Set.of(door), layer, null);

        return roomTiles;
    }

    private static Set<Tile> getAllTilesOfRoom(TileMapLayer colliderMap, Rectangle room, String id, boolean outline) {

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

    public static Set<Tile> tryPlacingLooseRoom(TileMapLayer tileMapLayer) {

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

    public static Set<Tile> tryPlacingLooseRoom(TileMap tileMap, Rectangle room) {

        Set<Tile> wallTiles = getWallTilesOfRoom(tileMap,room);
        for (Tile tile : wallTiles) {
            tileMap.set(Tile.COLLIDER, tile.row, tile.column, "WALL");
        }

        return wallTiles;

//        Set<Tile> edges = new HashSet<>();
//
//        for (int row = 0; row < tileMap.getRows(); row++) {
//            for (int column = 0; column < tileMap.getColumns(row); column++) {
//                boolean isTop = row == 0;
//                boolean isRight = row == tileMap.getRows() - 1;
//                boolean isBottom = column == tileMap.getColumns(row) - 1;
//                boolean isLeft = column == 0;
//
//                if (!isTop && !isRight && !isBottom && !isLeft) { continue; }
//
//                tileMap.set(Tile.COLLIDER, row, column, "WALL");
//                edges.add(new Tile(row, column));
//            }
//        }
//        return edges;
    }

//    public static Set<Tile> tryPlaceCollidersAroundEdges(TileMap tileMap) {
//
//        Set<Tile> wallTiles = getWallTilesOfRoom(tileMap, ;
//
////        Set<Tile> edges = new HashSet<>();
////
////        for (int row = 0; row < tileMap.getRows(); row++) {
////            for (int column = 0; column < tileMap.getColumns(row); column++) {
////                boolean isTop = row == 0;
////                boolean isRight = row == tileMap.getRows() - 1;
////                boolean isBottom = column == tileMap.getColumns(row) - 1;
////                boolean isLeft = column == 0;
////
////                if (!isTop && !isRight && !isBottom && !isLeft) { continue; }
////
////                tileMap.set(Tile.COLLIDER, row, column, "WALL");
////                edges.add(new Tile(row, column));
////            }
////        }
////        return edges;
//    }

//    public static Set<Tile> tryPlaceCollidersAroundEdges(TileMap TileMap) {
//
//        TileMapLayer colliderMap = TileMap.getColliderLayer();
//        Set<Tile> edges = new HashSet<>();
//
//        for (int row = 0; row < colliderMap.getRows(); row++) {
//            for (int column = 0; column < colliderMap.getColumns(row); column++) {
//                boolean isTop = row == 0;
//                boolean isRight = row == colliderMap.getRows() - 1;
//                boolean isBottom = column == colliderMap.getColumns(row) - 1;
//                boolean isLeft = column == 0;
//
//                if (!isTop && !isRight && !isBottom && !isLeft) { continue; }
//
//                colliderMap.set(row, column, "WALL");
//                edges.add(new Tile(row, column));
//            }
//        }
//        return edges;
//    }

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

//    public static List<Set<Tile>> tryConnectingRooms(TileMap tileMap, List<Set<Tile>> rooms) {
//
//        if (rooms.isEmpty()) { return new ArrayList<>(); }
//        TileMapLayer colliderMap = tileMap.getColliderLayer();
//
//        mRandom.setSeed(tileMap.getSeed());
//
//        List<Set<Tile>> halls = new ArrayList<>();
//        Set<Tile> hall;
//        int size;
//        // connect each of the rooms, one after the other
//        for (int index = 1; index < rooms.size(); index++) {
//            Set<Tile> previousRoom = rooms.get(index - 1);
//            Set<Tile> currentRoomTile = rooms.get(index);
//            size = mRandom.nextBoolean() ? 1 : 2;
//            hall = connectViaTunneling(colliderMap, currentRoomTile, previousRoom, size);
//            halls.add(hall);
//        }
//        // connect the first and last room
//        size = mRandom.nextBoolean() ? 1 : 2;
//        hall = connectViaTunneling(colliderMap, rooms.get(0), rooms.get(rooms.size() - 1), size);
//        halls.add(hall);
//
//        return halls;
//    }

    public static Set<Tile> connectRooms(TileMap tileMap, List<Set<Tile>> rooms) {

        if (rooms.isEmpty()) { return new HashSet<>(); }

        mRandom.setSeed(tileMap.getSeed());

        Set<Tile> halls = new HashSet<>();

        Set<Tile> connections;
        int size;
        // connect each of the rooms, one after the other
        for (int index = 1; index < rooms.size(); index++) {
            Set<Tile> previousRoom = rooms.get(index - 1);
            Set<Tile> currentRoomTile = rooms.get(index);
            size = mRandom.nextBoolean() ? 1 : 2;
            connections = connectViaTunneling(tileMap, currentRoomTile, previousRoom, size);
            halls.addAll(connections);
        }
        // connect the first and last room
        size = mRandom.nextBoolean() ? 1 : 2;
        connections = connectViaTunneling(tileMap, rooms.get(0), rooms.get(rooms.size() - 1), size);
        halls.addAll(connections);

        return halls;
    }

//    public static List<Set<Tile>> tryConnectingRooms(TileMap tileMap, List<Set<Tile>> rooms) {
//
//        if (rooms.isEmpty()) { return new ArrayList<>(); }
//
//        mRandom.setSeed(tileMap.getSeed());
//
//        List<Set<Tile>> halls = new ArrayList<>();
//
//        Set<Tile> hall;
//        int size;
//        // connect each of the rooms, one after the other
//        for (int index = 1; index < rooms.size(); index++) {
//            Set<Tile> previousRoom = rooms.get(index - 1);
//            Set<Tile> currentRoomTile = rooms.get(index);
//            size = mRandom.nextBoolean() ? 1 : 2;
//            hall = connectViaTunneling(tileMap, currentRoomTile, previousRoom, size);
//            halls.add(hall);
//        }
//        // connect the first and last room
//        size = mRandom.nextBoolean() ? 1 : 2;
//        hall = connectViaTunneling(tileMap, rooms.get(0), rooms.get(rooms.size() - 1), size);
//        halls.add(hall);
//
//        return halls;
//    }

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


    public static void carveIntoMap(TileMap tileMap, Set<Tile> tiles, String layer) {
        carveIntoMap(tileMap, tiles, layer, null);
    }

    public static void carveIntoMap(TileMap tileMap, Set<Tile> tiles, String layer, Object value) {
        for (Tile tile : tiles) {
            tileMap.set(layer, tile.row ,tile.column, value);
        }
    }

    public static Set<Tile> connectViaTunneling(TileMap tileMap, Set<Tile> room1, Set<Tile> room2, int size) {
        // Get random tiles to connect rooms
        List<Tile> randomizerList = new ArrayList<>(room1);
        Collections.shuffle(randomizerList);
        Tile pointFromRoom1 = randomizerList.get(0);

        randomizerList = new ArrayList<>(room2);
        Collections.shuffle(randomizerList);
        Tile pointFromRoom2 = randomizerList.get(0);

        Set<Tile> columnTunnel =
                getConnectingRows(tileMap, pointFromRoom1.row, pointFromRoom2.row, pointFromRoom1.column, size);
        Set<Tile> rowTunnel =
                getConnectingColumns(tileMap, pointFromRoom1.column, pointFromRoom2.column, pointFromRoom2.row, size);

        Set<Tile> corridor = new HashSet<>();
        corridor.addAll(rowTunnel);
        corridor.addAll(columnTunnel);

        return corridor;
    }

    public static Set<Tile> connectViaTunneling(TileMapLayer colliderMap, Set<Tile> room1, Set<Tile> room2, int size) {
        // Get random tiles to connect rooms
        List<Tile> randomizerList = new ArrayList<>(room1);
        Collections.shuffle(randomizerList);
        Tile pointFromRoom1 = randomizerList.get(0);

        randomizerList = new ArrayList<>(room2);
        Collections.shuffle(randomizerList);
        Tile pointFromRoom2 = randomizerList.get(0);

        Set<Tile> columnTunnel =
                getConnectingRows(colliderMap, pointFromRoom1.row, pointFromRoom2.row, pointFromRoom1.column, size);
        Set<Tile> rowTunnel =
                getConnectingColumns(colliderMap, pointFromRoom1.column, pointFromRoom2.column, pointFromRoom2.row, size);

        Set<Tile> corridor = new HashSet<>();
        corridor.addAll(rowTunnel);
        corridor.addAll(columnTunnel);

        return corridor;
    }

    public static Set<Tile> getConnectingRows(TileMap tileMap, int startingRow, int endingRow, int column, int size) {
        int startRow = Math.min(startingRow, endingRow);
        int endRow = Math.max(startingRow, endingRow);
        Set<Tile> tiles = new HashSet<>();
        for (int curRow = startRow; curRow < endRow; curRow++) {
            for (int curCol = column - size; curCol < column + size; curCol++) {
                if (tileMap.isOutOfBounds(curRow, curCol)) { continue; }
                tiles.add(new Tile(curRow, curCol));
            }
        }
        return tiles;
    }

    public static Set<Tile> getConnectingColumns(TileMap tileMap, int startingCol, int endingCol, int row, int size) {
        // Check which point is actually the first, numerically
        int startCol = Math.min(startingCol, endingCol);
        int endCol = Math.max(startingCol, endingCol);
        Set<Tile> tiles = new HashSet<>();
        for (int curCol = startCol; curCol < endCol; curCol++) {
            for (int curRow = row - size; curRow < row + size; curRow++) {
                if (tileMap.isOutOfBounds(curRow, curCol)) { continue; }
                tiles.add(new Tile(curRow, curCol));
            }
        }
        return tiles;
    }

    private static Set<Tile> getConnectingRows(TileMapLayer colliderMap, int startingRow, int endingRow, int column, int size) {
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

    private static Set<Tile> getConnectingColumns(TileMapLayer colliderMap, int starting, int ending, int row, int size) {
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

    public static void debug(TileMap tileMap, String layer) {
        boolean[][] grid = new boolean[tileMap.getRows()][tileMap.getColumns()];
        System.out.println("Constructing TileMap for " + layer + " layer");
        for (int row = 0; row < tileMap.getRows(); row++) {
            for (int column = 0; column < tileMap.getColumns(row);column++) {
                boolean isUsed = tileMap.isUsed(layer, row, column);
                grid[row][column] = isUsed;
                System.out.print("[" + (isUsed ? "X" : " ") + "]");
            }
            System.out.println();
        }
    }
}
