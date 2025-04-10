package main.game.map.builders;

import main.constants.Direction;
import main.game.components.TileComponent;
import main.game.map.base.TileMap;

import java.awt.Point;
import java.util.*;

public class TileMapValidator {

    public static boolean isValid(TileMap tileMap) {
        String layer = TileComponent.COLLIDER;
        // If there are no non-path calls of the map, no need to validate, every tile is valid
        boolean hasAllColliderCells = hasCompletelyUsedTileMapLayer(tileMap, layer);
        if (hasAllColliderCells) { return true; }
        // count all the path cells
        int[] starting = getFirstUnusedLocation(tileMap, layer);
        int breadthFirstSearchPathCount = getBreadthFirstSearchPathCount(tileMap, starting, layer);
        int depthFirstSearchPathCount = getDepthFirstSearchPathCount(tileMap, starting, layer);
        int bruteForcePathCount = getBruteForcePathCount(tileMap, layer);

        boolean bfsMatch = bruteForcePathCount == breadthFirstSearchPathCount;
        boolean dfsMatch = bruteForcePathCount == depthFirstSearchPathCount;

        return bfsMatch && dfsMatch;
    }

    /**
     * Count the number of unused tiles in the given tile map layer
     */
    private static int getBruteForcePathCount(TileMap tileMap, String layer) {
        int count = 0;
        for (int row = 0; row < tileMap.getRows(); row++) {
            for (int column = 0; column < tileMap.getColumns(row); column++) {
                if (tileMap.isUsed(layer, row, column)) { continue; }
                count++;
            }
        }
        return count;
    }

    /**
     * Count the number of tiles capable of being reached on the given tile map layer
     * from the given tile position using BFS traversal.
     */
    private static int getBreadthFirstSearchPathCount(TileMap tileMap, int[] starting, String layer) {
        Set<Point> visited = new HashSet<>();
        Queue<Point> toVisit = new LinkedList<>();
        toVisit.add(new Point(new Point(starting[0], starting[1])));

        while (!toVisit.isEmpty()) {

            Point visiting = toVisit.poll();

            boolean isVisited = visited.contains(visiting);
            if (isVisited || visiting == null) { continue; }
            boolean isOutOfBounds = tileMap.isOutOfBounds(visiting.y, visiting.x);
            if (isOutOfBounds) { continue; }
            boolean isPathCell = tileMap.isNotUsed(layer, visiting.y, visiting.x);
            if (!isPathCell) { continue; }

            visited.add(visiting);

            for (Direction direction : Direction.cardinal) {
                toVisit.add(new Point(visiting.x + direction.x, visiting.y + direction.y));
            }
        }
        return visited.size();
    }

    /**
     * Count the number of tiles capable of being reached on the given tile map layer
     * from the given tile position using DFS.
     */
    private static int getDepthFirstSearchPathCount(TileMap tileMap, int[] starting, String layer) {
        Set<Point> visited = new HashSet<>();
        Stack<Point> toVisit = new Stack<>();
        toVisit.add(new Point(starting[0], starting[0]));

        while (!toVisit.isEmpty()) {
            Point visiting = toVisit.pop();

            boolean isVisited = visited.contains(visiting);
            if (isVisited || visiting == null) { continue; }
            boolean isOutOfBounds = tileMap.isOutOfBounds(visiting.y, visiting.x);
            if (isOutOfBounds) { continue; }
            boolean isPathCell = tileMap.isNotUsed(layer, visiting.y, visiting.x);
            if (!isPathCell) { continue; }

            visited.add(visiting);

            for (Direction direction : Direction.cardinal) {
                toVisit.add(new Point(visiting.x + direction.x, visiting.y + direction.y));
            }
        }
        return visited.size();
    }

    /**
     * Get the first un-used tile in the tile map layer
     */
    private static int[] getFirstUnusedLocation(TileMap tileMap, String layer) {
        for (int row = 0; row < tileMap.getRows(); row++) {
            for (int column = 0; column < tileMap.getColumns(row); column++) {
                if (tileMap.isUsed(layer, row, column)) { continue; }
                return new int[]{ row, column };
            }
        }
        return new int[]{ 0, 0 };
    }

    /**
     * Returns true if the entire tile map layer is used
     */
    private static boolean hasCompletelyUsedTileMapLayer(TileMap tileMap, String layer) {
        // If a cell is non-zero, then it is a path cell
        for (int row = 0; row < tileMap.getRows(); row++) {
            for (int column = 0; column < tileMap.getColumns(row); column++) {
                if (tileMap.isUsed(layer, row, column)) { continue; }
                return false;
            }
        }
        return true;
    }

//    public static boolean isValid(TileMap tileMap) {
//        TileMapLayer colliderMap = tileMap.getColliderLayer();
//        // If there are no non-path calls of the map, no need to validate, every tile is valid
//        boolean hasAllColliderCells = hasCompletelyUsedTileMapLayer(colliderMap);
//        if (hasAllColliderCells) { return true; }
//        // count all the path cells
//        int[] starting = getFirstUnusedLocation(colliderMap);
//        int breadthFirstSearchPathCount = getBreadthFirstSearchPathCount(colliderMap, starting);
//        int depthFirstSearchPathCount = getDepthFirstSearchPathCount(colliderMap, starting);
//        int bruteForcePathCount = getBruteForcePathCount(colliderMap);
//
//        boolean bfsMatch = bruteForcePathCount == breadthFirstSearchPathCount;
//        boolean dfsMatch = bruteForcePathCount == depthFirstSearchPathCount;
//
//        return bfsMatch && dfsMatch;
//    }
//
//    /**
//     * Count the number of unused tiles in the given tile map layer
//     */
//    private static int getBruteForcePathCount(TileMapLayer tml) {
//        int count = 0;
//        for (int row = 0; row < tml.getRows(); row++) {
//            for (int column = 0; column < tml.getColumns(row); column++) {
//                if (tml.isUsed(row, column)) { continue; }
//                count++;
//            }
//        }
//        return count;
//    }
//
//    /**
//     * Count the number of tiles capable of being reached on the given tile map layer
//     * from the given tile position using BFS traversal.
//     */
//    private static int getBreadthFirstSearchPathCount(TileMapLayer tml, int[] starting) {
//        Set<Point> visited = new HashSet<>();
//        Queue<Point> toVisit = new LinkedList<>();
//        toVisit.add(new Point(new Point(starting[0], starting[1])));
//
//        while (!toVisit.isEmpty()) {
//
//            Point visiting = toVisit.poll();
//
//            boolean isVisited = visited.contains(visiting);
//            if (isVisited || visiting == null) { continue; }
//            boolean isOutOfBounds = tml.isOutOfBounds(visiting.y, visiting.x);
//            if (isOutOfBounds) { continue; }
//            boolean isPathCell = tml.isNotUsed(visiting.y, visiting.x);
//            if (!isPathCell) { continue; }
//
//            visited.add(visiting);
//
//            for (Direction direction : Direction.cardinal) {
//                toVisit.add(new Point(visiting.x + direction.x, visiting.y + direction.y));
//            }
//        }
//        return visited.size();
//    }
//
//    /**
//     * Count the number of tiles capable of being reached on the given tile map layer
//     * from the given tile position using DFS.
//     */
//    private static int getDepthFirstSearchPathCount(TileMapLayer tml, int[] starting) {
//        Set<Point> visited = new HashSet<>();
//        Stack<Point> toVisit = new Stack<>();
//        toVisit.add(new Point(starting[0], starting[0]));
//
//        while (!toVisit.isEmpty()) {
//            Point visiting = toVisit.pop();
//
//            boolean isVisited = visited.contains(visiting);
//            if (isVisited || visiting == null) { continue; }
//            boolean isOutOfBounds = tml.isOutOfBounds(visiting.y, visiting.x);
//            if (isOutOfBounds) { continue; }
//            boolean isPathCell = tml.isNotUsed(visiting.y, visiting.x);
//            if (!isPathCell) { continue; }
//
//            visited.add(visiting);
//
//            for (Direction direction : Direction.cardinal) {
//                toVisit.add(new Point(visiting.x + direction.x, visiting.y + direction.y));
//            }
//        }
//        return visited.size();
//    }
//
//    /**
//     * Get the first un-used tile in the tile map layer
//     */
//    private static int[] getFirstUnusedLocation(TileMapLayer tml) {
//        for (int row = 0; row < tml.getRows(); row++) {
//            for (int column = 0; column < tml.getColumns(row); column++) {
//                if (tml.isUsed(row, column)) { continue; }
//                return new int[]{ row, column };
//            }
//        }
//        return new int[]{ 0, 0 };
//    }
//
//    /**
//     * Returns true if the entire tile map layer is used
//     */
//    private static boolean hasCompletelyUsedTileMapLayer(TileMapLayer tml) {
//        // If a cell is non-zero, then it is a path cell
//        for (int row = 0; row < tml.getRows(); row++) {
//            for (int column = 0; column < tml.getColumns(row); column++) {
//                if (tml.isUsed(row, column)) { continue; }
//                return false;
//            }
//        }
//        return true;
//    }
}
