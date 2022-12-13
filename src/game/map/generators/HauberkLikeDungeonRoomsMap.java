package game.map.generators;

import constants.Direction;
import game.map.MapBuilder;
import game.map.SchemaMap;
import game.map.TileMap;
import game.stores.pools.AssetPool;
import logging.Logger;
import logging.LoggerFactory;

import java.awt.Point;
import java.util.*;

public class HauberkLikeDungeonRoomsMap extends MapBuilder {
    private final Logger logger = LoggerFactory.instance().logger(HauberkLikeDungeonRoomsMap.class);

    @Override
    public TileMap build(int rows, int columns) {

        logger.log("Constructing {0}", getClass());

        int baseImageIndex = random.nextInt(AssetPool.instance().tileSprites() - 1);
        while (baseImageIndex % 2 != 0 || baseImageIndex == 0) {
            baseImageIndex = random.nextInt(AssetPool.instance().tileSprites() - 1);
        }

        while (!isCompletelyConnected) {

            floorMap = new SchemaMap(rows, columns);
            structureMap = new SchemaMap(rows, columns);

            List<Set<Point>> rooms = tryCreatingRooms(floorMap);

            placeStructuresSafely(floorMap, structureMap, rooms, 3);

            for (int row = 1; row < floorMap.getRows(); row += 2) {
                for (int column = 1; column < floorMap.getColumns(); column += 2) {
                    growMaze(floorMap, rooms, new Point(row, column));
                }
            }

            connectRegions(floorMap);

            isCompletelyConnected = isCompletelyConnected(floorMap, rooms);

            if (isCompletelyConnected) {
                System.out.println(floorMap.debug(false));
                System.out.println(floorMap.debug(true));
            }
        }

        return new TileMap(encode(floorMap, structureMap, baseImageIndex));
    }

    private void placeStructuresSafely(SchemaMap floorMap, SchemaMap structureMap, List<Set<Point>> rooms, int structure) {
        for (Set<Point> room : rooms) {
            // Get a point in the room
            for (Point tile : room) {
                // if there are no tiles walls around the tile, or the tile is surrounded by two tiles, cane place
                if (!hasNoSurroundingStructuresOrWalls(floorMap, structureMap, tile)) { continue; }
                structureMap.setOccupied(tile.y, tile.x, structure);

            }
        }
    }

    public static boolean hasNoSurroundingStructuresOrWalls(SchemaMap floorMap, SchemaMap structureMap, Point tile) {
        for (Direction direction : Direction.cardinal) {
            int nextX = tile.x + direction.x;
            int nextY = tile.y + direction.y;
            if (floorMap.isNotOccupied(nextY, nextX)) { return false; }
            if (structureMap.isOccupied(nextY, nextX)) { return false; }
        }
        return true;
    }

    private Set<Point> getTilesWithinRegion(SchemaMap pathMap, Point starting) {

        // BFS for all tiles within the region
        Queue<Point> toVisit = new LinkedList<>();
        Set<Point> visited = new HashSet<>();
        toVisit.add(starting);

        while (toVisit.size() > 0) {

            Point current = toVisit.poll();

            // Skip if out of bounds
            if (pathMap.isOutOfBounds(current.y, current.x)) { continue; }
            // Skip if is not part of a pth
            if (pathMap.isNotOccupied(current.y, current.x)) { continue; }
            // Skip If already visited
            if (visited.contains(current)) { continue; }

            visited.add(current);

            // Get the tiles in all directions
            for (Direction direction : Direction.cardinal) {
                toVisit.add(new Point(current.x + direction.x, current.y + direction.y));
            }
        }

        return visited;
    }

    private Map<Integer, Set<Point>> getRegions(SchemaMap pathMap) {
        // List of all the regions
        Map<Integer, Set<Point>> regionToTilesMap = new HashMap<>();
        // Check and assign each tile to a region
        for (int row = 0; row < pathMap.getRows(); row++) {
            for (int column = 0; column < pathMap.getColumns(); column++) {
                // Skip if non path tile
                if (pathMap.isNotOccupied(row, column)) { continue; }

                // Skip if the tile is already part of a region
                Point current = new Point(column, row);
                boolean found = getTileRegion(regionToTilesMap, current) != -1;
                if (found) { continue; }

                // Get tiles within the region
                Set<Point> region = getTilesWithinRegion(pathMap, current);
                regionToTilesMap.put(regionToTilesMap.size() + 1, region);
            }
        }

        return regionToTilesMap;
    }

    private void connectRegions(SchemaMap pathMap){
        // Get all the regions, and the tiles within each
        Map<Integer, Set<Point>> regionToTilesMap = getRegions(pathMap);
        // Get all tiles that connect 2 or more regions
        Map<Point, Set<Integer>> regionConnectorTilesToRegionMap = getConnectors(pathMap, regionToTilesMap);
        // For each connect all regions
        Map<Integer, Set<Integer>> regionToRegionMap = connectRegions(pathMap, regionConnectorTilesToRegionMap);
        // Potentially remove dead ends
        List<Set<Point>> deadEndsRemoved = removeDeadEnds(pathMap);
    }

    private List<Set<Point>> removeDeadEnds(SchemaMap pathMap) {
        List<Set<Point>> deadEnds = new ArrayList<>();
        for (int row = 0; row < pathMap.getRows(); row++) {
            for (int column = 0; column < pathMap.getColumns(); column++) {
                // Skip if not path tile
                if (pathMap.isNotOccupied(row, column)) { continue; }
                // Ensure the tile is surrounded by 3 non path tiles
                Point current = new Point(column, row);
                Set<Point> adjacentWallTiles = new HashSet<>();
                for (Direction direction : Direction.cardinal) {
                    int nextX = current.x + direction.x;
                    int nextY = current.y + direction.y;
                    if (pathMap.isOccupied(nextY, nextX)) { continue; }
                    adjacentWallTiles.add(new Point(nextX, nextY));
                }
                if (adjacentWallTiles.size() <= 2) { continue; }
                Set<Point> deadEnd = removeDeadEnd(pathMap, current);
                deadEnds.add(deadEnd);
            }
        }
        return deadEnds;
    }

    private Set<Point> removeDeadEnd(SchemaMap pathMap, Point deadEnd) {
        Queue<Point> toVisit = new LinkedList<>();
        Set<Point> visited = new HashSet<>();
        toVisit.add(deadEnd);

        while (toVisit.size() > 0) {

            Point current = toVisit.poll();

            if (visited.contains(current)) { continue; }
            visited.add(current);

            Set<Point> connectedPaths = new HashSet<>();
            for (Direction direction : Direction.cardinal) {
                int nextX = current.x + direction.x;
                int nextY = current.y + direction.y;
                if (pathMap.isNotOccupied(nextY, nextX)) { continue; }
                connectedPaths.add(new Point(nextX, nextY));
            }

            if (connectedPaths.size() > 1) { continue; }
            toVisit.addAll(connectedPaths);
            pathMap.setOccupied(current.y, current.x, 0);
        }
        return visited;
    }

    private Map<Integer, Set<Integer>> connectRegions(SchemaMap pathMap, Map<Point, Set<Integer>> connectorsToRegionsMap) {
        Map<Integer, Set<Integer>> regionToRegionMap = new HashMap<>();
        int connectionsMade = 0;
        for (Map.Entry<Point, Set<Integer>> entry : connectorsToRegionsMap.entrySet()) {
            // Check that this tile connects regions that have not been connected
            boolean hasNewConnections = false;
            // if no regions have been connected already, use this connector
            for (int region : entry.getValue()) {
                // If this tile can connect a new region, set it as a path
                if (!regionToRegionMap.containsKey(region)) { hasNewConnections = true; }
                regionToRegionMap.put(region, entry.getValue());
            }
            // Connect the regions, if it has not been connected before (new connection) TODO
            if (!hasNewConnections && random.nextFloat() < .9) { continue; }
            // Use this point to connect the regions
            pathMap.setOccupied(entry.getKey().y, entry.getKey().x, 1);
            connectionsMade++;
        }
        logger.log("{0} regional connections have been made", connectionsMade);
        return regionToRegionMap;
    }

    private Map<Point, Set<Integer>> getConnectors(SchemaMap pathMap, Map<Integer, Set<Point>> regionToTilesMap) {
        // List of all the tiles that connect two regions
        Map<Point, Set<Integer>> connectors = new HashMap<>();
        // Check and assign each tile to a region
        for (int row = 0; row < pathMap.getRows(); row++) {
            for (int column = 0; column < pathMap.getColumns(); column++) {
                // Skip if path tile
                if (pathMap.isOccupied(row, column)) { continue; }
                // Get the tiles around the current tile
                Set<Point> toCheck = new HashSet<>();
                Point current = new Point(column, row);
                for (Direction direction : Direction.cardinal) {
                    toCheck.add(new Point(current.x + direction.x, current.y + direction.y));
                }

                // If the there are two tiles from separate, regions, the tile is a connector
                Set<Integer> regionsConnecting = new HashSet<>();
                for (Point tile : toCheck) {
                    // only check tiles that are paths, because only those can be part of regions
                    if (pathMap.isOutOfBounds(tile.y, tile.x)) { continue; }
                    if (pathMap.isNotOccupied(tile.y, tile.x)) { continue; }
                    int region = getTileRegion(regionToTilesMap, tile);
                    regionsConnecting.add(region);
                }

                // If there are more than 1 regions connecting here, add as connector tile
                if (regionsConnecting.size() < 2) { continue; }
                connectors.put(current, regionsConnecting);
            }
        }
        return connectors;
    }

    private int getTileRegion(Map<Integer, Set<Point>> regionToTilesMap, Point current) {
        for (Map.Entry<Integer, Set<Point>> entry : regionToTilesMap.entrySet()) {
            if (entry.getValue().contains(current)) { return entry.getKey(); }
        }
        return -1;
    }

    private void growMaze(SchemaMap pathMap, List<Set<Point>> rooms, Point starting) {
        // Don't grow if out of bounds
        if (pathMap.isOutOfBounds(starting.y, starting.x)) { return; }
        // Don't grow if already on a path
        if (pathMap.isOccupied(starting.y, starting.x)) { return; }

        Stack<Point> tiles = new Stack<>();
        Direction lastDirection = null;
        float windingPercent = 0.1f;

        // create a new room for the path
        Set<Point> windingPath = new HashSet<>();
        windingPath.add(starting);
        int region = rooms.size() + 1;

        if (!pathMap.isOccupied(starting.x, starting.y)) {
            carve(pathMap, starting.y, starting.x, region);
        }

        tiles.add(starting);

        while (tiles.size() > 0) {

            Point cell = tiles.pop();
            Set<Direction> directions = new HashSet<>();

            // Add  the directions that can be carved into
            List<Direction> randomDirections = Direction.getRandomized(Direction.cardinal);
            for (Direction direction : Direction.cardinal) {
                if (!canCarve(pathMap, cell, direction)) { continue; }
                directions.add(direction);
            }

            if (directions.size() > 0) {
                Direction direction;
                if (directions.contains(lastDirection) && random.nextDouble() > windingPercent) {
                    direction = lastDirection;
                } else {
                    direction = directions.iterator().next();
                }

                // This shouldn't happen, i think
                if (direction == null) { continue; }

                // carve the next two cells in this direction
                Point newCell1 = new Point(cell.x + direction.x, cell.y + direction.y);
                carve(pathMap, newCell1.y, newCell1.x, region);
                windingPath.add(newCell1);
//                tiles.add(newCell1);

                Point newCell2 = new Point(cell.x + (direction.x * 2), cell.y + (direction.y * 2));
                carve(pathMap, newCell2.y, newCell2.x, region);
                windingPath.add(newCell2);
                tiles.add(newCell2);

                lastDirection = direction;
            } else { lastDirection = null; }
        }
        if (windingPath.size() > 0) { rooms.add(windingPath); }
    }

    private static boolean canCarve(int[][] pathMap, Point current, Direction dir) {
        int row = current.y + dir.y * 3;
        int col = current.x + dir.x * 3;

        if (row < 0 || col < 0 || row >= pathMap.length || col >= pathMap[row].length ) { return false; }
        if (pathMap[row][col] != 0) { return false; }

        row = current.y + dir.y * 2;
        col = current.x + dir.x * 2;

        return pathMap[row][col] == 0;
    }

    private static boolean canCarve(SchemaMap pathMap, Point current, Direction dir) {
        int row = current.y + dir.y * 3;
        int column = current.x + dir.x * 3;

        // If next 3 tiles is out of bounds or is already a path, don't carve there
        if (pathMap.isOutOfBounds(row, column)) { return false; }
        if (pathMap.isOccupied(row, column)) { return false; }

        row = current.y + dir.y * 2;
        column = current.x + dir.x * 2;

        return !pathMap.isOccupied(row, column);
    }

    private static void carve(SchemaMap pathMap, int row, int col, int region) {
        pathMap.setOccupied(row, col, region);
    }
    private static void carve(int[][] pathMap, int row, int col, int region) {
        pathMap[row][col] = region;
    }
}
