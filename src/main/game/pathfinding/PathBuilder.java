package main.game.pathfinding;

import main.constants.Direction;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.algorithms.DigitalDifferentialAnalysis;

import java.util.*;

public class PathBuilder {
    private boolean ignoreObstructions = false;
    public static final String MOVEMENT = "Movement";
    public static final String VISION = "Vision";
    private final DigitalDifferentialAnalysis algorithm = new DigitalDifferentialAnalysis();
    private final Map<Entity, Integer> depthMap = new HashMap<>();
    private final Map<Entity, Entity> pathMap = new HashMap<>();
    private final Map<Entity, Integer> heightMap = new HashMap<>();
    private final Map<Entity, Entity> visitedMap = new HashMap<>();
    private final Queue<Entity> queue = new LinkedList<>();
    private PathBuilder() {}
    public static PathBuilder newBuilder() { return new PathBuilder(); }
    private void createGraph(GameModel model, Entity start, int range, int climb, int peak, String algorithm) {
        boolean completelyTraverse = range == -1 && climb == -1;
        depthMap.clear();
        pathMap.clear();
        visitedMap.clear();

        queue.clear();
        queue.add(start);

        depthMap.put(start, 0);
        pathMap.put(start, null);
        heightMap.put(start, start.get(Tile.class).getHeight());
        Tile currentTile = start.get(Tile.class);

        boolean forMovement = MOVEMENT.equalsIgnoreCase(algorithm);
//        boolean forVision = VISION_PATHING.equalsIgnoreCase(algorithm);

        while (!queue.isEmpty()) {

            // get the tile and its depth
            Entity current = queue.poll();
            if (current == null) { continue; }

            currentTile = current.get(Tile.class);
            int depth = depthMap.get(current);

            // check that we have not visited already and is within range
            if (visitedMap.containsKey(current)) { continue; }
            visitedMap.put(current, current);

            // If building graph for movement, don't traverse over obstructed tiles
            if (forMovement && current != start && (currentTile.isNotNavigable())) { continue; }

            // only go the specified range unless COMPLETELY_TRAVERSE
            if (depth > range - 1 && !completelyTraverse) { continue; }

            // go through each child tile and set connection
            for (Direction direction : Direction.cardinal) {
                int row = currentTile.row + direction.y;
                int col = currentTile.column + direction.x;
                Entity adjacent = model.tryFetchingTileAt(row, col);

                // skip tiles off the map or being occupied or already visited
                if (adjacent == null) { continue; }
                if (visitedMap.containsKey(adjacent)) { continue; }

                // ensure the tile isn't obstructed and within jump or move
                Tile adjacentTile = adjacent.get(Tile.class);

                // If building graph for movement, don't traverse over obstructed tiles
                if (forMovement && adjacentTile.isNotNavigable()) { continue; }

                // Get the amount of climb needed to traverse
                int elevationDifference = Math.abs(currentTile.getHeight() - adjacentTile.getHeight());
                boolean isClimbing  = currentTile.getHeight() <adjacentTile.getHeight();

                // if height allowance is set to -1, ignore
                if (forMovement && isClimbing) {
                    if (elevationDifference > climb) {
                        continue;
                    }
                    if (adjacentTile.getObstruction() != null) {
                        continue;
                    }
                }

                queue.add(adjacent);
                depthMap.put(adjacent, depth + (forMovement && adjacentTile.isRoughTerrain() ? 2 : 1));
                pathMap.put(adjacent, current);
                heightMap.put(adjacent, adjacentTile.getHeight());
            }
        }
        visitedMap.put(start, start);
    }

    public LinkedList<Entity> inMovementPath(GameModel model, Entity start, Entity end, int move, int climb) {
        createGraph(model, start, move, climb, 0, MOVEMENT);
        LinkedList<Entity> result = new LinkedList<>();
        if (!pathMap.containsKey(start)) { return result; }
        if (!pathMap.containsKey(end)) { return result; }
        Entity current = end;
        while (current != null) {
            result.addFirst(current);
            current = pathMap.get(current);
        }
        return result;
    }

    public Set<Entity> inMovementRange(GameModel model, Entity start, int move, int climb) {
        createGraph(model, start, move, climb,0, MOVEMENT);

        Set<Entity> result = new HashSet<>(pathMap.keySet());
        return result;
    }

    private LinkedList<Entity> bresenhamLineOfSight(GameModel model, Entity start, Entity end) {
        Tile startTile = start.get(Tile.class);
        int startRow = startTile.getRow();
        int startColumn = startTile.getColumn();
        Tile endTile = end.get(Tile.class);
        int endRow = endTile.getRow();
        int endColumn = endTile.getColumn();

        int dColumn =  Math.abs(endColumn - startColumn);
        int sColumn = startColumn < endColumn ? 1 : -1;
        int dRow = -Math.abs(endRow - startRow);
        int sRow = startRow < endRow ? 1 : -1;
        int err = dColumn + dRow;  /* error value e_xy */
        LinkedList<Entity> line = new LinkedList<>();

        while (true) {
            Entity entity = model.tryFetchingTileAt(startRow, startColumn);
            Tile tile = entity.get(Tile.class);
            line.add(entity);

            if (entity != start && (tile.isWall() || tile.isOccupied() || tile.hasObstruction())) {
                break;
            }

            if (startColumn == endColumn && startRow == endRow) {
                break;
            }
            int e2 = 2 * err;
            if (e2 >= dRow) {
                err += dRow;
                startColumn += sColumn;
            }
            if (e2 <= dColumn) {
                err += dColumn;
                startRow += sRow;
            }
        }

        return line;
    }

    public Set<Entity> inVisionRange(GameModel model, Entity start, int range) {
        // Set the climb very high for now
        if (start == null) { return new HashSet<>(); }
        createGraph(model, start, range, 999, 0,  VISION);
        Set<Entity> result = new HashSet<>(pathMap.keySet());
        LinkedList<Entity> toRemove = new LinkedList<>();

        // Check that the view tiles are within pathing range
        for (Entity tileEntity : result) {
            LinkedList<Entity> path = bresenhamLineOfSight(model, start, tileEntity);
            if (!path.contains(tileEntity)) { toRemove.add(tileEntity); }
        }
        for (Entity entity : toRemove) { result.remove(entity); }
        return result;
    }

    public LinkedList<Entity> inLineOfSight(GameModel model, Entity start, Entity target) {
        if (start == null || target == null) { return new LinkedList<>(); }
        LinkedList<Entity> path = bresenhamLineOfSight(model, start, target);
        return path;
    }
//    private LinkedList<Entity> bresenham(GameModel model, int startRow, int startColumn, int endRow, int endColumn) {
//        int dColumn =  Math.abs(endColumn - startColumn);
//        int sColumn = startColumn < endColumn ? 1 : -1;
//        int dRow = -Math.abs(endRow - startRow);
//        int sRow = startRow < endRow ? 1 : -1;
//        int err = dColumn + dRow;  /* error value e_xy */
//        LinkedList<Entity> line = new LinkedList<>();
//
//        for(;;){
//            Entity entity = model.tryFetchingTileAt(startRow, startColumn);
//            line.add(entity);
//
//            if (startColumn == endColumn && startRow == endRow) {
//                break;
//            }
//            int e2 = 2 * err;
//            if (e2 >= dRow) {
//                err += dRow;
//                startColumn += sColumn;
//            }
//            if (e2 <= dColumn) {
//                err += dColumn;
//                startRow += sRow;
//            }
//        }
//
//        return line;
//    }
}
