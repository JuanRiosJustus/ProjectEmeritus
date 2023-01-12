package game.pathfinding;

import constants.Direction;
import game.GameModel;
import game.components.Tile;
import game.entity.Entity;

import java.util.*;

public class TilePathing {

    public static Deque<Entity> getPath(GameModel model, Entity start, Entity end, int move, int jump) {
        Map<Entity, Entity> tileToParentMap = getMovementTreeGraph(model, start, move, jump);
        if (tileToParentMap == null) { return null; }
        if (!tileToParentMap.containsKey(start) || !tileToParentMap.containsKey(end)) { return null; }
        Deque<Entity> result = new LinkedList<>();
        Entity current = end;
        while (current != null) {
            result.addFirst(current);
            current = tileToParentMap.get(current);
        }
        return result;
    }

    public static Set<Entity> getRange(GameModel model, Entity start, int move, int jump) {
        Map<Entity, Entity> tileToParentMap = getMovementTreeGraph(model, start, move, jump);
        return tileToParentMap == null ? null : tileToParentMap.keySet();
    }

    private static Map<Entity, Entity> getMovementTreeGraph(GameModel model, Entity start, int distance, int jump) {
        if (distance <= 0 || start == null) { return null; }

        // create maps if not supplied. If not supplied, only used for local cache
        Map<Entity, Integer> tileToDepthMap = new HashMap<>();
        Map<Entity, Entity> tileToParentMap = new HashMap<>();

        // init caches
        Queue<Entity> toVisit = new LinkedList<>();
        Set<Entity> visited = new HashSet<>();
        toVisit.add(start);
        tileToDepthMap.put(start, 0);
        tileToParentMap.put(start, null);

        while (toVisit.size() > 0) {

            // get the tile and its depth
            Entity parentEntity = toVisit.poll();
            Tile parentTile = parentEntity.get(Tile.class);
            int tileDepth = tileToDepthMap.get(parentEntity);

            // check that we have not visited already and is within range
            if (visited.contains(parentEntity)) { continue; }

            // only go the range of the caller
            if (tileDepth >= distance) { continue; }

            visited.add(parentEntity);

            // go through each child tile and set connection
            for (Direction direction : Direction.cardinal) {
                int row = parentTile.row + direction.y;
                int col = parentTile.column + direction.x;
                Entity childEntity = model.tryFetchingTileAt(row, col);

                // skip tiles off the map or being occupied or already visited
                if (childEntity == null) { continue; }
                if (visited.contains(childEntity)) { continue; }

                // ensure the tile isn't obstructed and within jump or move
                Tile childTile = childEntity.get(Tile.class);
                if (childTile.isStructureUnitOrWall()) { continue; }
                boolean isClimbing = parentTile.getHeight() < childTile.getHeight();
                int climbCost = Math.abs(parentTile.getHeight() - childTile.getHeight());

                if (isClimbing && climbCost > jump) {
                    // If the first tile is too high, if youre close enough, allow 1 movement in the direction
                    if (parentEntity == start) {
                        visited.add(childEntity);
                        tileToDepthMap.put(childEntity, tileDepth + 1);
                        tileToParentMap.put(childEntity, parentEntity);
                    }
                    continue;
                }

                // set to visit and compute depth
                toVisit.add(childEntity);
                tileToDepthMap.put(childEntity, tileDepth + 1);
                tileToParentMap.put(childEntity, parentEntity);
            }
        }
        visited.add(start);

        return tileToParentMap;
    }


    public static Set<Entity> getLineOfSight(GameModel model, Entity start, int range) {
        Map<Entity, Entity> tileToParentMap = getActionTreeGraph(model, start, range);
        if (tileToParentMap == null) { return null; }
        Set<Entity> result = new HashSet<>();
        for (Map.Entry<Entity, Entity> entry : tileToParentMap.entrySet()) {
            TilePathing.raytrace(model, start, entry.getKey(), range, result);
        }
        return result;
    }

    public static Set<Entity> getRange(GameModel model, Entity start, int range) {
        Map<Entity, Entity> tileToParentMap = getActionTreeGraph(model, start, range);
        return tileToParentMap == null ? null : tileToParentMap.keySet();
    }


    private static Map<Entity, Entity> getActionTreeGraph(GameModel model, Entity start, int distance) {
        if (distance < 0 || start == null) { return null; }

        // create maps if not supplied. If not supplied, only used for local cache
        Map<Entity, Integer> tileToDepthMap = new HashMap<>();
        Map<Entity, Entity> tileToParentMap = new HashMap<>();

        // init caches
        Queue<Entity> toVisit = new LinkedList<>();
        Set<Entity> visited = new HashSet<>();
        toVisit.add(start);
        tileToDepthMap.put(start, 0);
        tileToParentMap.put(start, null);

        while (toVisit.size() > 0) {

            // get the tile and its depth
            Entity parentEntity = toVisit.poll();
            Tile parentTile = parentEntity.get(Tile.class);
            int tileDepth = tileToDepthMap.get(parentEntity);

            // check that we have not visited already and is within range
            if (visited.contains(parentEntity)) { continue; }

            // only go the range of the caller
            if (tileDepth >= distance) { continue; }

            visited.add(parentEntity);

            // go through each child tile and set connection
            for (Direction direction : Direction.cardinal) {
                int row = parentTile.row + direction.y;
                int col = parentTile.column + direction.x;
                Entity childEntity = model.tryFetchingTileAt(row, col);

                // skip tiles off the map or being occupied or already visited
                if (childEntity == null) { continue; }
                if (visited.contains(childEntity)) { continue; }

                // ensure the tile isn't obstructed and within jump or move
                Tile childTile = childEntity.get(Tile.class);
                if (childTile.isWall()) { continue; }

                // set to visit and compute depth
                toVisit.add(childEntity);
                tileToDepthMap.put(childEntity, tileDepth + 1);
                tileToParentMap.put(childEntity, parentEntity);
            }
        }
        visited.add(start);

        return tileToParentMap;
    }


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

    public static void getCardinallyAdjacentTiles(GameModel model, Entity target, Set<Entity> result) {
        result.clear();
        Arrays.stream(Direction.cardinal).forEach(direction -> {
            int newRow = direction.y + target.get(Tile.class).row;
            int newColumn = direction.x + target.get(Tile.class).column;
            Entity adjacent = model.tryFetchingTileAt(newRow, newColumn);
            if (adjacent == null) { return; }
            result.add(adjacent);
        });
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

    public static void getLineOfSight(GameModel model, Entity start, int range, Set<Entity> result) {
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
            Entity entity = toVisit.poll();
            Tile tile = entity.get(Tile.class);
            int tileDepth = tileToDepthMap.get(entity);

            // check that we have not visited already and is within range
            if (tilesWithinRange.contains(entity)) { continue; }
            // ignore walls
            // only go the range of the caller
            if (tileDepth >= range) { continue; }

            tilesWithinRange.add(entity);

            // go through each child tile and set connection
            for (Direction direction : Direction.cardinal) {
                int row = tile.row + direction.y;
                int col = tile.column + direction.x;
                Entity childEntity = model.tryFetchingTileAt(row, col);

                // skip tiles off the map or being occupied or already visited
                if (childEntity == null) { continue; }
                if (tilesWithinRange.contains(childEntity)) { continue; }

                // ensure the tile isn't obstructed if strictMode
                Tile childTile = childEntity.get(Tile.class);
                if (strict && childTile.isStructureUnitOrWall()) { continue; }

                // set to visit and compute depth
                toVisit.add(childEntity);
                tileToDepthMap.put(childEntity, tileDepth + 1);
                tileToParentMap.put(childEntity, entity);
            }
        }
        tilesWithinRange.add(start);
        return tileToParentMap;
    }

    public static Set<Entity> raytrace(GameModel model, Entity start, Entity end, int limit) {
        Tile startDetails = start.get(Tile.class);
        Tile endDetails = end.get(Tile.class);
        int columnDelta = Math.abs(endDetails.column - startDetails.column) * 2;
        int rowDelta = Math.abs(endDetails.row - startDetails.row) * 2;
        int column = startDetails.column;
        int row = startDetails.row;
        int column_inc = (endDetails.column > startDetails.column) ? 1 : -1;
        int row_inc = (endDetails.row > startDetails.row) ? 1 : -1;
        int error = columnDelta - rowDelta;

        Set<Entity> result = new HashSet<>();
        for (int iteration = 1 + columnDelta + rowDelta; iteration > 0; --iteration) {
            if (limit < 0) { return result; }
            limit--;
            Entity entity = model.tryFetchingTileAt(row, column);
            if (entity != null) {
                Tile details = entity.get(Tile.class);
                result.add(entity);
                if (details.isStructureUnitOrWall() && entity != start) {
                    break;
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
        return result;
    }

    public static Set<Entity> raytrace(GameModel model, Entity start, Entity end) {
        Tile startDetails = start.get(Tile.class);
        Tile endDetails = end.get(Tile.class);
        int columnDelta = Math.abs(endDetails.column - startDetails.column) * 2;
        int rowDelta = Math.abs(endDetails.row - startDetails.row) * 2;
        int column = startDetails.column;
        int row = startDetails.row;
        int column_inc = (endDetails.column > startDetails.column) ? 1 : -1;
        int row_inc = (endDetails.row > startDetails.row) ? 1 : -1;
        int error = columnDelta - rowDelta;

        Set<Entity> result = new HashSet<>();
        for (int iteration = 1 + columnDelta + rowDelta; iteration > 0; --iteration) {
            Entity entity = model.tryFetchingTileAt(row, column);
            if (entity != null) {
                Tile details = entity.get(Tile.class);
                result.add(entity);
                if (details.isStructureUnitOrWall() && entity != start) {
                    break;
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
        return result;
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

//    public static List<Entity> getTilesWithEntitiesFromLOSFromUser(Set<Entity> tilesWithinLOS,
//                                                                   Entity user, boolean canHitUser) {
//        return tilesWithinLOS.stream()
//                .filter(tile -> tile.get(Tile.class).unit != null)
//                .filter(tile -> !tile.get(Tile.class).isWall())
//                .filter(tile -> !tile.get(Tile.class).isStructure())
//                .filter(tile -> (canHitUser || tile.get(Tile.class).unit != user))
//                .collect(Collectors.toList());
//    }
//
//    public int distanceBetweenTiles(Entity tile1, Entity tile2) {
//        Tile t1 = tile1.get(Tile.class);
//        Tile t2 = tile2.get(Tile.class);
//        return Math.abs(t1.row - t2.row) + Math.abs(t1.column - t2.column);
//    }
}
