package main.game.pathfinding;

import main.constants.Direction;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.algorithms.DigitalDifferentialAnalysis;

import java.util.*;

public class PathBuilder {
    public static final String MOVEMENT = "Movement";
    public static final String VISION = "Vision";
    private final DigitalDifferentialAnalysis algorithm = new DigitalDifferentialAnalysis();
    private final Map<Entity, Integer> mTileToDepthMap = new HashMap<>();
    private final Map<Entity, Entity> mTileToParentMap = new HashMap<>();
    private final Set<Entity> mVisitedSet = new HashSet<>();
    private final Queue<Entity> mToVisitQueue = new LinkedList<>();
    public PathBuilder() {}
    public static PathBuilder newBuilder() { return new PathBuilder(); }

    private void createGraphV2(GameModel model, Entity startTileEntity, int range) {
        mTileToDepthMap.clear();
        mTileToDepthMap.put(startTileEntity, 0);

        mTileToParentMap.clear();
        mTileToParentMap.put(startTileEntity, null);

        mToVisitQueue.clear();
        mToVisitQueue.add(startTileEntity);

        mVisitedSet.clear();

        while (!mToVisitQueue.isEmpty()) {
            // get the tile and its depth
            Entity currentTileEntity = mToVisitQueue.poll();
            if (currentTileEntity == null) { continue; }

            Tile currentTile = currentTileEntity.get(Tile.class);
            int depth = mTileToDepthMap.get(currentTileEntity);

            // check that we have not visited already and is within range
            if (mVisitedSet.contains(currentTileEntity)) { continue; }
            mVisitedSet.add(currentTileEntity);

            // If building graph for movement, don't traverse over obstructed tiles
            if (currentTileEntity != startTileEntity && (currentTile.isNotNavigable())) { continue; }

            // only go the specified range
            if (depth >= range) { continue; }

            // go through each child tile and set connection
            for (Direction direction : Direction.cardinal) {
                int row = currentTile.getRow() + direction.y;
                int column = currentTile.getColumn() + direction.x;
                Entity cardinallyAdjacentTileEntity = model.tryFetchingEntityAt(row, column);

                // skip tiles off the map or being occupied or already visited
                if (cardinallyAdjacentTileEntity == null) { continue; }
                if (mVisitedSet.contains(cardinallyAdjacentTileEntity)) { continue; }

                // ensure the tile isn't obstructed and within jump or move
                Tile adjacentTile = cardinallyAdjacentTileEntity.get(Tile.class);

                // If building graph for movement, don't traverse over obstructed tiles
                if (adjacentTile.isNotNavigable()) { continue; }

                mToVisitQueue.add(cardinallyAdjacentTileEntity);
                mTileToParentMap.put(cardinallyAdjacentTileEntity, currentTileEntity);
                mTileToDepthMap.put(cardinallyAdjacentTileEntity, depth + 1);
            }
        }
        mVisitedSet.add(startTileEntity);
    }

    private void createGraph(GameModel model, Entity start, int range, int climb, String algorithm) {
        createGraphV2(model, start, range);
//        boolean completelyTraverse = range == -1 && climb == -1;
//        depthMap.clear();
//        pathMap.clear();
//        visitedMap.clear();
//
//        queue.clear();
//        queue.add(start);
//
//        depthMap.put(start, 0);
//        pathMap.put(start, null);
//        heightMap.put(start, start.get(Tile.class).getHeight());
//
//        boolean forMovement = MOVEMENT.equalsIgnoreCase(algorithm);
//
//        while (!queue.isEmpty()) {
//
//            // get the tile and its depth
//            Entity currentTileEntity = queue.poll();
//            if (currentTileEntity == null) { continue; }
//
//            Tile currentTile = currentTileEntity.get(Tile.class);
//            int depth = depthMap.get(currentTileEntity);
//
//            // check that we have not visited already and is within range
//            if (visitedMap.containsKey(currentTileEntity)) { continue; }
//            visitedMap.put(currentTileEntity, currentTileEntity);
//
//            // If building graph for movement, don't traverse over obstructed tiles
//            if (forMovement && currentTileEntity != start && (currentTile.isNotNavigable())) { continue; }
//
//            // only go the specified range unless COMPLETELY_TRAVERSE
//            if (depth >= range && !completelyTraverse) { continue; }
//
//            // go through each child tile and set connection
//            for (Direction direction : Direction.cardinal) {
//                int row = currentTile.getRow() + direction.y;
//                int column = currentTile.getColumn() + direction.x;
//                Entity adjacentTileEntity = model.tryFetchingEntityAt(row, column);
//
//                // skip tiles off the map or being occupied or already visited
//                if (adjacentTileEntity == null) { continue; }
//                if (visitedMap.containsKey(adjacentTileEntity)) { continue; }
//
//                // ensure the tile isn't obstructed and within jump or move
//                Tile adjacentTile = adjacentTileEntity.get(Tile.class);
//
//                // If building graph for movement, don't traverse over obstructed tiles
//                if (forMovement && adjacentTile.isNotNavigable()) { continue; }
//
//                // Get the amount of climb needed to traverse
//                int elevationDifference = Math.abs(currentTile.getHeight() - adjacentTile.getHeight());
//                boolean isClimbing  = currentTile.getHeight() < adjacentTile.getHeight();
//
//                // if height allowance is set to -1, ignore
//                if (forMovement && isClimbing) {
//                    if (elevationDifference > climb) {
//                        continue;
//                    }
//                    if (adjacentTile.getTopStructure() != null) {
//                        continue;
//                    }
//                }
//
//                queue.add(adjacentTileEntity);
//                depthMap.put(adjacentTileEntity, depth + (forMovement && adjacentTile.isRoughTerrain() ? 2 : 1));
//                pathMap.put(adjacentTileEntity, currentTileEntity);
//                heightMap.put(adjacentTileEntity, adjacentTile.getHeight());
//            }
//        }
//        visitedMap.put(start, start);
    }

    // TODO this should be A*
    public LinkedList<Entity> getMovementPath(GameModel model, Entity start, Entity end, int move, int climb) {
        createGraph(model, start, move, climb, MOVEMENT);
        LinkedList<Entity> result = new LinkedList<>();
        if (!mTileToParentMap.containsKey(start)) { return result; }
        if (!mTileToParentMap.containsKey(end)) { return result; }
        Entity current = end;
        while (current != null) {
            result.addFirst(current);
            current = mTileToParentMap.get(current);
        }
        return result;
    }

    public LinkedList<Entity> getMovementPathV2(GameModel model, Entity start, Entity end, int move, int climb) {
        createGraph(model, start, move, climb, MOVEMENT);
        LinkedList<Entity> result = new LinkedList<>();
        if (!mTileToParentMap.containsKey(start)) { return result; }
        if (!mTileToParentMap.containsKey(end)) { return result; }
        Entity current = end;
        while (current != null) {
            result.addFirst(current);
            current = mTileToParentMap.get(current);
        }
        return result;
    }

    public Set<Entity> getMovementRange(GameModel model, Entity start, int move, int climb) {
        createGraph(model, start, move, climb, MOVEMENT);

        return new HashSet<>(mTileToParentMap.keySet());
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
            Entity entity = model.tryFetchingEntityAt(startRow, startColumn);
            Tile tile = entity.get(Tile.class);
            line.add(entity);

            if (entity != start && (tile.isWall() || tile.isOccupied())) {
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


    public Set<Entity> getTilesInRange(GameModel model, Entity startTileEntity, int range) {
        // Set the climb very high for now
        if (startTileEntity == null) { return new HashSet<>(); }
        createGraph(model, startTileEntity, range, 999, VISION);
        Set<Entity> result = new HashSet<>(mTileToParentMap.keySet());
        LinkedList<Entity> toRemove = new LinkedList<>();

        // Check that the view tiles are within pathing range
        for (Entity tileEntity : result) {
            LinkedList<Entity> path = bresenhamLineOfSight(model, startTileEntity, tileEntity);
            if (!path.contains(tileEntity)) { toRemove.add(tileEntity); }
        }
        for (Entity entity : toRemove) { result.remove(entity); }
        return result;
    }

    public LinkedList<Entity> getTilesLineOfSight(GameModel model, Entity start, Entity target) {
        if (start == null || target == null) { return new LinkedList<>(); }
        LinkedList<Entity> path = bresenhamLineOfSight(model, start, target);
        return path;
    }
}
