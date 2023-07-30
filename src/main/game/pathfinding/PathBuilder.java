package main.game.pathfinding;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import designer.fundamentals.Direction;
import main.game.components.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;

public class PathBuilder {

    private int distanceAllowance = -1;
    private int heightAllowance = -1;
    private boolean respectObstructions = false;
    private Entity startingPoint = null;
    private Entity endingPoint = null;
    private GameModel gameModel = null;

    private PathBuilder() {}

    public static PathBuilder newBuilder() {
        return new PathBuilder();
    }

    public PathBuilder setGameModel(GameModel model) {
        gameModel = model;
        return this;
    }

    public PathBuilder setDistanceAllowance(int allowance) {
        distanceAllowance = allowance;
        return this;
    }

    public PathBuilder setHeightAllowance(int allowance) {
        heightAllowance = allowance;
        return this;
    }

    public PathBuilder setStartingPoint(Entity entity) {
        startingPoint = entity;
        return this;
    }

    public PathBuilder setEndingPoint(Entity entity) {
        endingPoint = entity;
        return this;
    }

    public PathBuilder setRespectObstructions(boolean respect) {
        respectObstructions = respect;
        return this;
    }

    public Deque<Entity> getTilesWithinMovementPath() {
        Map<Entity, Entity> tileToParentMap = createGraph();
        Deque<Entity> result = new LinkedList<>();

        if (tileToParentMap == null) { return result; }
        if (!tileToParentMap.containsKey(startingPoint)) { return result; }
        if (!tileToParentMap.containsKey(endingPoint)) { return result; }

        Entity current = endingPoint;
        while (current != null) {
            result.addFirst(current);
            current = tileToParentMap.get(current);
        }
        return result;
    }
    
    public Set<Entity> getTilesWithinMovementRange() {
        Map<Entity, Entity> tileToParentMap = createGraph();
        Set<Entity> result = new HashSet<>();
        if (tileToParentMap == null) { return result; }
        result.addAll(tileToParentMap.keySet());
        return result;
    }

    public Set<Entity> getTilesWithinLineOfSight() {
        Map<Entity, Entity> graph = createGraph();
        Set<Entity> result = new HashSet<>();
        if (graph == null) { return result; }
        for (Map.Entry<Entity, Entity> entry : graph.entrySet()) {
            getShadowTracing(gameModel, startingPoint, entry.getKey(), distanceAllowance, result);
        }
        return result;
    }

    private Map<Entity, Entity> createGraph() {
        if (startingPoint == null) { return null; }

        Map<Entity, Integer> tileToDepthMap = new HashMap<>();
        Map<Entity, Entity> tileToParentMap = new HashMap<>();

        Queue<Entity> toVisit = new LinkedList<>();
        Set<Entity> visited = new HashSet<>();

        toVisit.add(startingPoint);
        tileToDepthMap.put(startingPoint, 0);
        tileToParentMap.put(startingPoint, null);

        while (toVisit.size() > 0) {

            // get the tile and its depth
            Entity parentEntity = toVisit.poll();
            Tile parentTile = parentEntity.get(Tile.class);
            int tileDepth = tileToDepthMap.get(parentEntity);

            // check that we have not visited already and is within range
            if (visited.contains(parentEntity)) { continue; }

            // only go the range of the caller
            if (tileDepth >= distanceAllowance) { continue; }

            visited.add(parentEntity);

            // go through each child tile and set connection
            for (Direction direction : Direction.cardinal) {
                int row = parentTile.row + direction.y;
                int col = parentTile.column + direction.x;
                Entity childEntity = gameModel.tryFetchingTileAt(row, col);

                // skip tiles off the map or being occupied or already visited
                if (childEntity == null) { continue; }
                if (visited.contains(childEntity)) { continue; }

                // ensure the tile isn't obstructed and within jump or move
                Tile childTile = childEntity.get(Tile.class);    
                if (childTile.isWall()) { continue; }
                if (respectObstructions && childTile.isStructureUnitOrWall()) { continue; }

                // if height allowance is set to -1, ignore
                if (heightAllowance != -1) {        
                    boolean isJumping = parentTile.getHeight() < childTile.getHeight();
                    int jumpCost = Math.abs(parentTile.getHeight() - childTile.getHeight());   
                    if (isJumping && jumpCost > heightAllowance) {
                        // If the first tile is too high, if youre close enough, allow 1 movement in the direction
                        if (parentEntity == startingPoint) {
                            visited.add(childEntity);
                            tileToDepthMap.put(childEntity, tileDepth + 1);
                            tileToParentMap.put(childEntity, parentEntity);
                        }
                        continue;
                    }
                }

                // set to visit and compute depth
                toVisit.add(childEntity);
                tileToDepthMap.put(childEntity, tileDepth + 1);
                tileToParentMap.put(childEntity, parentEntity);
            }
        }
        visited.add(startingPoint);

        return tileToParentMap;
    }

    private static void getShadowTracing(GameModel model, Entity start, Entity end, int length, Set<Entity> result) {
        Tile startDetails = start.get(Tile.class);
        Tile endDetails = end.get(Tile.class);
        int columnDelta = Math.abs(endDetails.column - startDetails.column) * 2;
        int rowDelta = Math.abs(endDetails.row - startDetails.row) * 2;
        int column = startDetails.column;
        int row = startDetails.row;
        int column_inc = (endDetails.column > startDetails.column) ? 1 : -1;
        int row_inc = (endDetails.row > startDetails.row) ? 1 : -1;
        int error = columnDelta - rowDelta;
        for (int iteration = 1 + columnDelta + rowDelta; iteration > 0; --iteration) {
            if (length < 0) { return; }
            length--;
            // TODO if iteration is the first, continue
            Entity entity = model.tryFetchingTileAt(row, column);
            if (entity != null) {
                Tile tile = entity.get(Tile.class);
                result.add(entity);
                if (tile.isStructureUnitOrWall() && entity != start) { return; }
            }

            if (error > 0) {
                column += column_inc;
                error -= rowDelta;
            } else {
                row += row_inc;
                error += columnDelta;
            }
        }
    }

    public static void getShadowTracing2(GameModel model, Entity start, Entity end, int length, Set<Entity> result) {
        Tile startDetails = start.get(Tile.class);
        Tile endDetails = end.get(Tile.class);
        int columnDelta = Math.abs(endDetails.column - startDetails.column) * 2;
        int rowDelta = Math.abs(endDetails.row - startDetails.row) * 2;
        int column = startDetails.column;
        int row = startDetails.row;
        int column_inc = (endDetails.column > startDetails.column) ? 1 : -1;
        int row_inc = (endDetails.row > startDetails.row) ? 1 : -1;
        int error = columnDelta - rowDelta;
        result.clear();
        for (int iteration = 1 + columnDelta + rowDelta; iteration > 0; --iteration) {
            if (length < 0) { return; }
            length--;
            Entity entity = model.tryFetchingTileAt(row, column);
            if (entity != null) {
                Tile tile = entity.get(Tile.class);
                result.add(entity);
                if (tile.isStructureUnitOrWall() && entity != start) { return; }
            }

            if (error > 0) {
                column += column_inc;
                error -= rowDelta;
            } else {
                row += row_inc;
                error += columnDelta;
            }
        }
    }
}
