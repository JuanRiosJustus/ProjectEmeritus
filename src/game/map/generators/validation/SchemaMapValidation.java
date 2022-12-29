package game.map.generators.validation;

import constants.Direction;

import java.awt.Point;
import java.util.*;

public class SchemaMapValidation {

    public static boolean isValidPath(SchemaMap pathMap) {
        // If there are no non-path calls of the map, no need to validate, every tile is valid
        boolean hasAllPaths = hasAllPathCells(pathMap);
        if (hasAllPaths) { return true; }
        // count all the path cells
        int bruteForcePathCount = getBruteForcePathCount(pathMap);
        // Get a cell from the map
        Point starting = getCell(pathMap);
        int breadthFirstSearchPathCount = getBreadthFirstSearchPathCount(pathMap, starting);
        int depthFirstSearchPathCount = getDepthFirstSearchPathCount(pathMap, starting);
        // return true if a == b && a == c
        boolean bfsMatch = bruteForcePathCount == breadthFirstSearchPathCount;
        boolean dfsMatch = bruteForcePathCount == depthFirstSearchPathCount;

        return bfsMatch && dfsMatch;
    }

    private static boolean hasAllPathCells(SchemaMap pathMap) {
        // If a cell is non-zero, then it is a path cell
        for (int row = 0; row < pathMap.getRows(); row++) {
            for (int column = 0; column < pathMap.getColumns(); column++) {
                if (pathMap.isUsed(row, column)) { continue; }
                return false;
            }
        }
        return true;
    }

    private static int getBruteForcePathCount(SchemaMap pathMap) {
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

    private static int getBreadthFirstSearchPathCount(SchemaMap pathMap, Point starting) {
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

    private static int getDepthFirstSearchPathCount(SchemaMap pathMap, Point starting) {
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

    private static Point getCell(SchemaMap pathMap) {
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
}
