package main.game.pathfinding;

import java.util.*;

import main.constants.Direction;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.algorithms.DigitalDifferentialAnalysis;

public class PathBuilder {
    private int range = -1;
    private int climb = -1;
    private Entity start = null;
    private Entity end = null;
    private GameModel model = null;
    private boolean ignoreObstructions = false;
    private boolean buildGraphForMovement = false;
    private boolean buildGraphForVision = false;
    private final DigitalDifferentialAnalysis algorithm = new DigitalDifferentialAnalysis();
    private final Map<Entity, Integer> depthMap = new HashMap<>();
    private final Map<Entity, Entity> pathMap = new HashMap<>();
    private final Map<Entity, Integer> heightMap = new HashMap<>();
    private final Map<Entity, Entity> visitedMap = new HashMap<>();
    private final Queue<Entity> queue = new LinkedList<>();
    private PathBuilder() {}
    public static PathBuilder newBuilder() { return new PathBuilder(); }
    public PathBuilder setModel(GameModel gameModel) { model = gameModel; return this; }
    public PathBuilder setRange(int allowance) { range = allowance; return this; }
    public PathBuilder setClimb(int allowance) { climb = allowance; return this; }
    public PathBuilder setStart(Entity entity) { start = entity; return this; }
    public PathBuilder setEnd(Entity entity) { end = entity; return this; }
    private PathBuilder setBuildGraphForMovement(boolean build) { buildGraphForMovement = build; return this; }
    private PathBuilder setBuildGraphForVision(boolean build) { buildGraphForVision = build; return this; }

    private void createGraph() {
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
//        int startingElevation = currentTile.getHeight();

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
            if (buildGraphForMovement && current != start && (currentTile.isNotNavigable())) { continue; }

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
                if (buildGraphForMovement && adjacentTile.isNotNavigable()) { continue; }

                // if height allowance is set to -1, ignore
                if (climb != -1 && current == start && !completelyTraverse) {
                    boolean isClimbing = currentTile.getHeight() < adjacentTile.getHeight();
                    int climbCost = Math.abs(currentTile.getHeight() - adjacentTile.getHeight());
                    if (isClimbing && climbCost > climb) {
                        // If the first tile is too high, if youre close enough, allow 1 movement in the direction
                        visitedMap.put(adjacent, adjacent);
                        depthMap.put(adjacent, depth + 1);
                        pathMap.put(adjacent, current);
                        continue;
                    }
                }

                queue.add(adjacent);
                depthMap.put(adjacent, depth + (buildGraphForMovement && adjacentTile.isRoughTerrain() ? 2 : 1));
                pathMap.put(adjacent, current);
                heightMap.put(adjacent, adjacentTile.getHeight());
            }
        }
        visitedMap.put(start, start);
    }

    public Deque<Entity> getTilesInMovementPath() {
        setBuildGraphForMovement(true);
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
        setBuildGraphForMovement(true);
        createGraph();
        return new HashSet<>(pathMap.keySet());
    }

    public Set<Entity> getTilesInRange() {
        setBuildGraphForVision(true);
        createGraph();
        Set<Entity> result = new HashSet<>();
        if (!pathMap.containsKey(start)) { return result; }
        for (Map.Entry<Entity, Entity> entry : visitedMap.entrySet()) {
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
        setBuildGraphForVision(true);
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
