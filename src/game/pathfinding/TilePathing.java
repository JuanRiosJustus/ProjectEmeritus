package game.pathfinding;

import constants.Direction;
import game.GameModel;
import game.components.Tile;
import game.entity.Entity;

import java.util.*;
import java.util.stream.Collectors;

public class TilePathing {

    public static void getTilesWithinPath(GameModel model, Entity start, Entity end, int range, Deque<Entity> result) {
        Map<Entity, Entity> tileToParentMap = getTilesWithinPath(model, start, range, true);
        if (!tileToParentMap.containsKey(start)) { return; }
        if (!tileToParentMap.containsKey(end)) { return; }
        result.clear();
        Entity current = end;
        while (current != null) {
            result.addFirst(current);
            current = tileToParentMap.get(current);
        }
    }

    public static void getCardinalTiles(GameModel model, Entity target, Set<Entity> result) {
        result.clear();
        for (Direction direction : Direction.cardinal) {
            Tile details = target.get(Tile.class);
            if (details == null) { continue; }
            Entity tile = model.tryFetchingTileAt(details.row + direction.x, details.column + direction.y);
            if (tile == null) { continue; }
            result.add(tile);
        }
    }

    public static void getUnobstructedTilePath(GameModel model, Entity start, int range, Set<Entity> result) {
        Map<Entity, Entity> tileToParentMap = getTilesWithinPath(model, start, range, true);
        result.clear();
        result.addAll(tileToParentMap.keySet());
    }

//    public static void getAllTilesWithinRange(GameModel model, Entity start, int range, boolean strict, Set<Entity> result) {
//        Map<Entity, Entity> tileToParentMap = getTilesWithinRange(model, start, range, strict);
//        result.clear();
//        result.addAll(tileToParentMap.keySet());
//    }

    public static void getTilesInLineOfSight(GameModel model, Entity start, int range, Set<Entity> result) {
        Map<Entity, Entity> tileToParentMap = getTilesWithinPath(model, start, range, false);
        result.clear();
        for (Map.Entry<Entity, Entity> entry : tileToParentMap.entrySet()) {
            TilePathing.raytrace(model, start, entry.getKey(), range, result);
        }
    }

    private static Map<Entity, Entity> getTilesWithinPath(GameModel model, Entity start, int range, boolean strict) {
        // invalid inputs
        if (range < 0 || start == null) { return new HashMap<>(); }

        // create maps if not supplied. If not supplied, only used for local cache
        Set<Entity> tilesWithinRange = new HashSet<>();
        Map<Entity, Integer> tileToDepthMap = new HashMap<>();
        Map<Entity, Entity> tileToParentMap = new HashMap<>();

        // init caches
        Queue<Entity> toVisit = new LinkedList<>();
        toVisit.add(start);
        tileToDepthMap.put(start, 0);
        tileToParentMap.put(start, null);

        while (toVisit.size() > 0 && range != 0) {

            // get the tile and its depth
            Entity tile = toVisit.poll();
            Tile tileDetails = tile.get(Tile.class);
            int tileDepth = tileToDepthMap.get(tile);

            // check that we have not visited already and is within range
            if (tilesWithinRange.contains(tile)) { continue; }
            // ignore walls
            // only go the range of the caller
            if (tileDepth >= range) { continue; }

            tilesWithinRange.add(tile);

            // go through each child tile and set connection
            for (Direction direction : Direction.cardinal) {
                int row = tileDetails.row + direction.y;
                int col = tileDetails.column + direction.x;
                Entity child = model.tryFetchingTileAt(row, col);

                // skip tiles off the map or being occupied or already visited
                if (child == null) { continue; }
                if (tilesWithinRange.contains(child)) { continue; }

                // ensure the tile isn't obstructed if strictMode
                Tile details = child.get(Tile.class);
                if (strict && details.isStructureUnitOrWall()) { continue; }

                // set to visit and compute depth
                toVisit.add(child);
                tileToDepthMap.put(child, tileDepth + 1);
                tileToParentMap.put(child, tile);
            }
        }
        tilesWithinRange.add(start);
        return tileToParentMap;
    }

    private static void raytrace(GameModel model, Entity start, Entity end, int limit, Set<Entity> result) {
        Tile startDetails = start.get(Tile.class);
        Tile endDetails = end.get(Tile.class);
        int columnDelta = Math.abs(endDetails.column - startDetails.column) * 2;
        int rowDelta = Math.abs(endDetails.row - startDetails.row) * 2;
        int column = startDetails.column;
        int row = startDetails.row;
        int column_inc = (endDetails.column > startDetails.column) ? 1 : -1;
        int row_inc = (endDetails.row > startDetails.row) ? 1 : -1;
        int error = columnDelta - rowDelta;
        int cycles = 0;

        for (int iteration = 1 + columnDelta + rowDelta; iteration > 0; --iteration) {
            if (cycles > limit) { return; }
            cycles++;
            Entity entity = model.tryFetchingTileAt(row, column);
            if (entity != null) {
                Tile details = entity.get(Tile.class);
                result.add(entity);
                if (details.isStructureUnitOrWall() && entity != start) {
                    return;
                }
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

    public static List<Entity> getTilesWithEntitiesFromLOSFromUser(Set<Entity> tilesWithinLOS,
                                                                   Entity user, boolean canHitUser) {
        return tilesWithinLOS.stream()
                .filter(tile -> tile.get(Tile.class).unit != null)
                .filter(tile -> !tile.get(Tile.class).isWall())
                .filter(tile -> !tile.get(Tile.class).isStructure())
                .filter(tile -> (canHitUser || tile.get(Tile.class).unit != user))
                .collect(Collectors.toList());
    }

    public int distanceBetweenTiles(Entity tile1, Entity tile2) {
        Tile t1 = tile1.get(Tile.class);
        Tile t2 = tile2.get(Tile.class);
        return Math.abs(t1.row - t2.row) + Math.abs(t1.column - t2.column);
    }
}
