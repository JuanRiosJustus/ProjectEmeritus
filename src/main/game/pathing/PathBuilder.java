package main.game.pathing;

import main.constants.Direction;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathing.lineofsight.Bresenham;

import java.util.*;

public class PathBuilder {
    public static final String MOVEMENT = "Movement";
    public static final String VISION = "Vision";
    private final Bresenham algorithm = new Bresenham();
    private final Map<Entity, Integer> mTileToDepthMap = new LinkedHashMap<>();
    private final Map<Entity, Entity> mTileToParentMap = new LinkedHashMap<>();
    private final Set<Entity> mVisitedSet = new HashSet<>();
    private final Queue<Entity> mToVisitQueue = new LinkedList<>();
    public PathBuilder() {}

    private void createGraph(GameModel model, Entity startTileEntity, int range) {
        createGraph(model, startTileEntity, range, true);
    }

    private void createGraph(GameModel model, Entity startTileEntity, int range, boolean respectNavigability) {
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
            if (currentTileEntity != startTileEntity && (respectNavigability && currentTile.isNotNavigable())) { continue; }

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
                if (respectNavigability && adjacentTile.isNotNavigable()) { continue; }

                mToVisitQueue.add(cardinallyAdjacentTileEntity);
                mTileToParentMap.put(cardinallyAdjacentTileEntity, currentTileEntity);
                mTileToDepthMap.put(cardinallyAdjacentTileEntity, depth + 1);
            }
        }
        mVisitedSet.add(startTileEntity);
    }

    // TODO this should be A*
    public Queue<Entity> getMovementPath(GameModel model, Entity start, Entity end, int move) {
        createGraph(model, start, move);
        Queue<Entity> result = getPath(start, end);
        return result;
    }

    private Queue<Entity> getPath(Entity start, Entity end) {
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

    public Queue<Entity> getMovementRange(GameModel model, Entity start, int move, int climb) {
        createGraph(model, start, move);

        return new LinkedList<>(mTileToParentMap.keySet());
    }

    public Queue<Entity> getMovementRangeV2(GameModel model, Entity start, int move) {
        createGraph(model, start, move);
        return new LinkedList<>(mTileToParentMap.keySet());
    }


    public Queue<Entity> getTilesInActionRange(GameModel model, Entity startTileEntity, int range) {
        if (startTileEntity == null) { return new LinkedList<>(); }

//        // Set the climb very high for now
//        createGraph(model, startTileEntity, range, false);
//
//        // Get all the tiles within the action range
//        Set<Entity> stagedResult = new HashSet<>(mTileToParentMap.keySet());
//
//        // Remove all the tiles not within line of sight
//        Queue<Entity> toRemove = new LinkedList<>();
//        for (Entity tileEntity : stagedResult) {
////            Map<Entity, Entity> path = algorithm.computeLine(model, startTileEntity, tileEntity, true);
//            Map<Entity, Entity> path = algorithmV2.computeLine(model, startTileEntity, tileEntity, true);
//            if (path.containsKey(tileEntity)) { continue; }
//            toRemove.add(tileEntity);
//        }
//
//        for (Entity entity : toRemove) { stagedResult.remove(entity); }
//
//        return new LinkedList<>(stagedResult);

        return new LinkedList<>(algorithm.computeAreaOfSight(model, startTileEntity, range));
    }

}
