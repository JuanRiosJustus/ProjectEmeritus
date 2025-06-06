package main.game.map.builders;

import main.game.components.tile.TileComponent;
import main.game.map.base.TileMap;
import main.game.map.base.TileMapLayer;
import main.game.map.base.TileMapAlgorithm;

import java.awt.Point;
import java.util.*;

import main.constants.Direction;
import main.game.map.base.TileMapParameters;

public class HauberkDungeonMap extends TileMapAlgorithm {

    @Override
    public TileMap evaluate(TileMapParameters tileMapParameters) {
        TileMap newTileMap = new TileMap(tileMapParameters);
        isPathCompletelyConnected = false;

        while (!isPathCompletelyConnected) {
            newTileMap.reset();

            // use pathmap approach, much easier
            TileMapLayer pathMap = new TileMapLayer("Path Layer", newTileMap.getRows(), newTileMap.getColumns());

            String wall = newTileMap.getWall();
            String floor = newTileMap.getFloor();
            long seed = newTileMap.getSeed();

            List<Set<TileComponent>> rooms = TileMapAlgorithm.tryCreatingRooms(pathMap, true, floor, wall, seed);

            for (int row = 1; row < pathMap.getRows(); row += 2) {
                for (int column = 1; column < pathMap.getColumns(row); column += 2) {
                    growMaze(pathMap, rooms, new TileComponent(row, column));
                }
            }

            placeWallsAroundEdges(pathMap);

            connectRegions(pathMap);

//            connectPathAndColliderMap(pathMap, newTileMap.getColliderLayer());
            isPathCompletelyConnected = TileMapValidator.isValid(newTileMap);
            if (isPathCompletelyConnected) {
                TileMapAlgorithm.completeTerrainLiquidAndObstruction(newTileMap, true);
//                newTileMap.complete();
            }
        }

        return newTileMap;
    }


    private static void connectPathAndColliderMap(TileMapLayer pathMap, TileMapLayer colliderMap) {
        for (int row = 0; row < pathMap.getRows(); row++) {
            for (int column = 0; column < pathMap.getColumns(row); column++) {
                if (pathMap.isUsed(row, column)) {
                    colliderMap.clear(row, column);
                } else {
                    colliderMap.set(row, column, "WALL");
                }
            }
        }
    }

    private static Set<TileComponent> placeWallsAroundEdges(TileMapLayer pathMap) {

        Set<TileComponent> edges = new HashSet<>();

        for (int row = 0; row < pathMap.getRows(); row++) {
            for (int column = 0; column < pathMap.getColumns(row); column++) {
                boolean isTop = row == 0;
                boolean isRight = row == pathMap.getRows() - 1;
                boolean isBottom = column == pathMap.getColumns(row) - 1;
                boolean isLeft = column == 0;

                if (!isTop && !isRight && !isBottom && !isLeft) { continue; }

                pathMap.clear(row, column);
                edges.add(new TileComponent(row, column));
            }
        }
        return edges;
    }

    private Set<Point> getTilesWithinRegion(TileMapLayer pathMap, Point starting) {

        // BFS for all tiles within the region
        Queue<Point> toVisit = new LinkedList<>();
        Set<Point> visited = new HashSet<>();
        toVisit.add(starting);

        while (!toVisit.isEmpty()) {

            Point current = toVisit.poll();

            // Skip if out of bounds
            if (pathMap.isOutOfBounds(current.y, current.x)) { continue; }
            // Skip if is not part of a pth
            if (pathMap.isNotUsed(current.y, current.x)) { continue; }
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

    private Map<Integer, Set<Point>> getRegions(TileMapLayer pathMap) {
        // List of all the regions
        Map<Integer, Set<Point>> regionToTilesMap = new HashMap<>();
        // Check and assign each tile to a region
        for (int row = 0; row < pathMap.getRows(); row++) {
            for (int column = 0; column < pathMap.getColumns(); column++) {
                // Skip if non path tile
                if (pathMap.isNotUsed(row, column)) { continue; }

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

    private void connectRegions(TileMapLayer pathMap){
        // Get all the regions, and the tiles within each
        Map<Integer, Set<Point>> regionToTilesMap = getRegions(pathMap);
        // Get all tiles that connect 2 or more regions
        Map<Point, Set<Integer>> regionConnectorTilesToRegionMap = getConnectors(pathMap, regionToTilesMap);
        // For each connect all regions
        Map<Integer, Set<Integer>> regionToRegionMap = connectRegions(pathMap, regionConnectorTilesToRegionMap);
        // Potentially remove dead ends
        List<Set<Point>> deadEndsRemoved = removeDeadEnds(pathMap);
    }

    private List<Set<Point>> removeDeadEnds(TileMapLayer pathMap) {
        List<Set<Point>> deadEnds = new ArrayList<>();
        for (int row = 0; row < pathMap.getRows(); row++) {
            for (int column = 0; column < pathMap.getColumns(); column++) {
                // Skip if not path tile
                if (pathMap.isNotUsed(row, column)) { continue; }
                // Ensure the tile is surrounded by 3 non path tiles
                Point current = new Point(column, row);
                Set<Point> adjacentWallTiles = new HashSet<>();
                for (Direction direction : Direction.cardinal) {
                    int nextX = current.x + direction.x;
                    int nextY = current.y + direction.y;
                    if (pathMap.isUsed(nextY, nextX)) { continue; }
                    adjacentWallTiles.add(new Point(nextX, nextY));
                }
                if (adjacentWallTiles.size() <= 2) { continue; }
                Set<Point> deadEnd = removeDeadEnd(pathMap, current);
                deadEnds.add(deadEnd);
            }
        }
        return deadEnds;
    }

    private Set<Point> removeDeadEnd(TileMapLayer pathMap, Point deadEnd) {
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
                if (pathMap.isNotUsed(nextY, nextX)) { continue; }
                connectedPaths.add(new Point(nextX, nextY));
            }

            if (connectedPaths.size() > 1) { continue; }
            toVisit.addAll(connectedPaths);
            pathMap.set(current.y, current.x, String.valueOf(0));
        }
        return visited;
    }

    private Map<Integer, Set<Integer>> connectRegions(TileMapLayer pathMap, Map<Point, Set<Integer>> connectorsToRegionsMap) {
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
            if (!hasNewConnections && mRandom.nextFloat() < .9) { continue; }
            // Use this point to connect the regions
            pathMap.set(entry.getKey().y, entry.getKey().x, String.valueOf(1));
            connectionsMade++;
        }
//        logger.info("{0} regional connections have been made", connectionsMade);
//        DebuggingSystem.debug(pathMap.);
        return regionToRegionMap;
    }

    private Map<Point, Set<Integer>> getConnectors(TileMapLayer pathMap, Map<Integer, Set<Point>> regionToTilesMap) {
        // List of all the tiles that connect two regions
        Map<Point, Set<Integer>> connectors = new HashMap<>();
        // Check and assign each tile to a region
        for (int row = 0; row < pathMap.getRows(); row++) {
            for (int column = 0; column < pathMap.getColumns(); column++) {
                // Skip if path tile
                if (pathMap.isUsed(row, column)) { continue; }
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
                    if (pathMap.isNotUsed(tile.y, tile.x)) { continue; }
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

    private void growMaze(TileMapLayer pathMap, List<Set<TileComponent>> rooms, TileComponent starting) {
        // Don't grow if out of bounds
        if (pathMap.isOutOfBounds(starting.row, starting.column)) { return; }
        // Don't grow if already on a path
        if (pathMap.isUsed(starting.row, starting.column)) { return; }

        Stack<TileComponent> tiles = new Stack<>();
        Direction lastDirection = null;
        float windingPercent = 0.0005f;

        // create a new room for the path
        int region = rooms.size() + 1;

        if (!pathMap.isUsed(starting.row, starting.column)) {
            pathMap.set(starting.row, starting.column, String.valueOf(region));
        }

        tiles.add(starting);

        while (!tiles.isEmpty()) {

            TileComponent tile = tiles.pop();
            Set<Direction> directions = new HashSet<>();

            // Add the directions that can be carved into
            for (Direction direction : Direction.cardinal) {
                if (!canCarve(pathMap, tile, direction)) { continue; }
                directions.add(direction);
            }

            if (directions.isEmpty()) {
                lastDirection = null;
            } else  {
                Direction direction;
                if (directions.contains(lastDirection) && mRandom.nextDouble() > windingPercent) {
                    direction = lastDirection;
                } else {
                    direction = directions.iterator().next();
                }

                // This shouldn't happen, i think
                if (direction == null) { continue; }

                // carve the next two cells in this direction
                TileComponent newCell1 = new TileComponent(tile.row + direction.y, tile.column + direction.x);
                pathMap.set(newCell1.row, newCell1.column, String.valueOf(region));
                //tiles.add(newCell1); //TODO, why does commenting this out resolve the issues?

                TileComponent newCell2 = new TileComponent(tile.row + (direction.y * 2), tile.column + (direction.x * 2));
                pathMap.set(newCell2.row, newCell2.column, region);
                tiles.add(newCell2);

                lastDirection = direction;
            }
        }
    }

    private static boolean canCarve(int[][] pathMap, TileComponent current, Direction dir) {
        int row = current.row + dir.y * 3;
        int col = current.column + dir.x * 3;

        if (row < 0 || col < 0 || row >= pathMap.length || col >= pathMap[row].length ) { return false; }
        if (pathMap[row][col] != 0) { return false; }

        row = current.row + dir.y * 2;
        col = current.column + dir.x * 2;

        return pathMap[row][col] == 0;
    }

    private static boolean canCarve(TileMapLayer pathMap, TileComponent current, Direction dir) {

        // check tiles 2 spaces in this direction
        int row = current.row + dir.y * 3;
        int column = current.column + dir.x * 3;

        // If the next 2 tiles is out of bounds or is already a path, don't carve there
        if (pathMap.isOutOfBounds(row, column)) { return false; }
        if (pathMap.isUsed(row, column)) { return false; }

        row = current.row + dir.y * 2;
        column = current.column + dir.x * 2;

        // if the next tile is not used, we can carve in this direction
        return !pathMap.isUsed(row, column);
    }
}
