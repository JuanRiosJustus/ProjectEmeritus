package main.game.pathfinding;

import java.util.*;

import designer.fundamentals.Direction;
import main.game.components.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.algorithms.DigitalDifferentialAnalysis;

public class PathBuilder {

    private int range = -1;
    private int climb = -1;
    private Entity start = null;
    private Entity end = null;
    private GameModel model = null;
    private final DigitalDifferentialAnalysis algorithm = new DigitalDifferentialAnalysis();
    private boolean respectObstructions = false;
    private boolean checkBeforeCollect = false;
    private final Map<Entity, Integer> depthMap = new HashMap<>();
    private final Map<Entity, Entity> pathMap = new HashMap<>();
    private final Map<Entity, Entity> visited = new HashMap<>();
    private final Queue<Entity> queue = new LinkedList<>();
    private static PathBuilder instance = null;
    private PathBuilder() {}
    public static PathBuilder newBuilder() { return new PathBuilder(); }
    public PathBuilder setModel(GameModel gameModel) { model = gameModel; return this; }
    public PathBuilder setRange(int allowance) { range = allowance; return this; }
    public PathBuilder setClimb(int allowance) { climb = allowance; return this; }
    public PathBuilder setStart(Entity entity) { start = entity; return this; }
    public PathBuilder setEnd(Entity entity) { end = entity; return this; }

    private void createGraph() {

        depthMap.clear();
        pathMap.clear();
        visited.clear();

        queue.clear();
        queue.add(start);

        depthMap.put(start, 0);
        pathMap.put(start, null);

        while (!queue.isEmpty()) {

            // get the tile and its depth
            Entity current = queue.poll();
            if (current == null) { continue; }

            Tile currentTile = current.get(Tile.class);
            int depth = depthMap.get(current);

            // check that we have not visited already and is within range
            if (visited.containsKey(current)) { continue; }
            visited.put(current, current);

            if (current != start && respectObstructions && currentTile.isObstructed()) { continue; }

            // only go the range of the caller
            if (depth > range - 1) { continue; }

            // go through each child tile and set connection
            for (Direction direction : Direction.cardinal) {
                int row = currentTile.row + direction.y;
                int col = currentTile.column + direction.x;
                Entity adjacent = model.tryFetchingTileAt(row, col);

                // skip tiles off the map or being occupied or already visited
                if (adjacent == null) { continue; }
                if (visited.containsKey(adjacent)) { continue; }

                // ensure the tile isn't obstructed and within jump or move
                Tile adjacentTile = adjacent.get(Tile.class);

                if (respectObstructions && checkBeforeCollect && adjacentTile.isObstructed()) { continue; }

                // if height allowance is set to -1, ignore
                if (climb != -1 && current == start) {
                    boolean isClimbing = currentTile.getHeight() < adjacentTile.getHeight();
                    int climbCost = Math.abs(currentTile.getHeight() - adjacentTile.getHeight());
                    if (isClimbing && climbCost > climb) {
                        // If the first tile is too high, if youre close enough, allow 1 movement in the direction
                        visited.put(adjacent, adjacent);
                        depthMap.put(adjacent, depth + 1);
                        pathMap.put(adjacent, current);
                        continue;
                    }
                }

                // set to visit and compute depth
                queue.add(adjacent);
                depthMap.put(adjacent, depth + 1);
                pathMap.put(adjacent, current);
            }
        }
        visited.put(start, start);
    }

    public Deque<Entity> getTilesInMovementPath() {
        respectObstructions = true;
        checkBeforeCollect = true;
        createGraph();
        Deque<Entity> result = new LinkedList<>();
        if (!pathMap.containsKey(start)) { return result; }
        if (!pathMap.containsKey(end)) { return result; }
        Entity current = end;
        while (current != null) {
            result.addFirst(current);
            current = pathMap.get(current);
        }
        return result;
    }

    public Set<Entity> getTilesInMovementRange() {
        respectObstructions = true;
        checkBeforeCollect = true;
        createGraph();
        return new HashSet<>(pathMap.keySet());
    }

    public Set<Entity> getTilesInRange() {
        respectObstructions = false;
        checkBeforeCollect = false;
        createGraph();
        Set<Entity> result = new HashSet<>();
        if (!pathMap.containsKey(start)) { return result; }
        for (Map.Entry<Entity, Entity> entry : visited.entrySet()) {
            if (entry.getValue() == null) { continue; }
            result.addAll(algorithm.addModel(model)
                    .addStart(start)
                    .addDistance(range)
                    .addEnd(entry.getValue())
                    .perform());
        }
        return result;
    }


    public Set<Entity> getTilesInLineOfSight() {
        respectObstructions = false;
        checkBeforeCollect = false;
        createGraph();
        Set<Entity> result = new HashSet<>();
        if (start == null || !pathMap.containsKey(start)) { return result; }
        if (end == null || !pathMap.containsKey(end)) { return result; }
        if (start == end) {
            result.add(start);
        } else {
            result.addAll(algorithm.addModel(model)
                    .addStart(start)
                    .addDistance(range)
                    .addEnd(end)
                    .perform());
        }
        return result;
    }
}
