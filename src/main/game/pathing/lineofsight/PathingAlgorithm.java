package main.game.pathing.lineofsight;

import main.constants.Direction;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.*;

public abstract class PathingAlgorithm {
    public LinkedHashSet<Entity> computeLineOfSight(GameModel model, Entity start, Entity end) {
        return computeLineOfSight(model, start, end, true);
    }
    public LinkedHashSet<Entity> computeAreaOfSight(GameModel model, Entity start, int range) {
        return computeAreaOfSight(model, start, range, true);
    }



    public abstract LinkedHashSet<Entity> computeAreaOfSight(GameModel model, Entity start, int range, boolean respectfully);
    public abstract LinkedHashSet<Entity> computeLineOfSight(GameModel model, Entity start, Entity end, boolean respectfully);



    public LinkedHashSet<Entity> computeMovementPath(GameModel model, Entity start, Entity end) {
        return computeMovementPath(model, start, end, true);
    }
    public LinkedHashSet<Entity> computeMovementPath(GameModel model, Entity start, Entity end, boolean respectfully) {
        Map<Entity, Entity> map = createGraph(model, start, -1, respectfully);

        LinkedHashSet<Entity> result = new LinkedHashSet<>();
        if (!map.containsKey(start)) { return result; }
        if (!map.containsKey(end)) { return result; }

        LinkedList<Entity> queue = new LinkedList<>();
        Entity current = end;
        while (current != null) {
            queue.addFirst(current);
            current = map.get(current);
        }

        result.addAll(queue);

        return result;
    }



    public LinkedHashSet<Entity> computeMovementArea(GameModel model, Entity start, int range) {
        return computeMovementArea(model, start, range, true);
    }
    public LinkedHashSet<Entity> computeMovementArea(GameModel model, Entity start, int range, boolean respectfully) {
        Map<Entity, Entity> map = createGraph(model, start, range, respectfully);
        LinkedHashSet<Entity> result = new LinkedHashSet<>(map.keySet());
        return result;
    }



    protected Map<Entity, Entity> createGraph(GameModel model, Entity start, int range, boolean respectfully) {
        Map<Entity, Integer> depthMap = new LinkedHashMap<>();
        depthMap.put(start, 0);

        Map<Entity, Entity> graphMap = new LinkedHashMap<>();
        graphMap.put(start, null);

        Queue<Entity> tilesToVisit = new LinkedList<>();
        tilesToVisit.add(start);

        Set<Entity> visitedTiles = new HashSet<>();

        while (!tilesToVisit.isEmpty()) {
            // get the tile and its depth
            Entity currentTileEntity = tilesToVisit.poll();
            if (currentTileEntity == null) { continue; }

            Tile currentTile = currentTileEntity.get(Tile.class);
            int depth = depthMap.get(currentTileEntity);

            // check that we have not visited already and is within range
            if (visitedTiles.contains(currentTileEntity)) { continue; }
            visitedTiles.add(currentTileEntity);

            // If building graph for movement, don't traverse over obstructed tiles
            if (currentTileEntity != start && (respectfully && currentTile.isNotNavigable())) { continue; }

            // only go the specified range
            if (range >= 0 && depth >= range) { continue; }

            // go through each child tile and set connection
            for (Direction direction : Direction.cardinal) {
                int row = currentTile.getRow() + direction.y;
                int column = currentTile.getColumn() + direction.x;
                Entity cardinallyAdjacentTileEntity = model.tryFetchingEntityAt(row, column);

                // skip tiles off the map or being occupied or already visited
                if (cardinallyAdjacentTileEntity == null) { continue; }
                if (visitedTiles.contains(cardinallyAdjacentTileEntity)) { continue; }

                // ensure the tile isn't obstructed and within jump or move
                Tile adjacentTile = cardinallyAdjacentTileEntity.get(Tile.class);

                // If building graph for movement, don't traverse over obstructed tiles
                if (respectfully && adjacentTile.isNotNavigable()) { continue; }

                tilesToVisit.add(cardinallyAdjacentTileEntity);
                graphMap.put(cardinallyAdjacentTileEntity, currentTileEntity);
                depthMap.put(cardinallyAdjacentTileEntity, depth + 1);
            }
        }

        return graphMap;
    }
}
