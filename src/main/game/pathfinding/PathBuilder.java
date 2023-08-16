package main.game.pathfinding;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import designer.fundamentals.Direction;
import main.constants.Constants;
import main.game.components.Tile;
import main.game.components.Vector;
import main.game.entity.Entity;
import main.game.main.GameModel;

public class PathBuilder {

    private int distance = -1;
    private int height = -1;
    private boolean respectObstructions = false;
    private Entity startEntity = null;
    private Entity endEntity = null;
    private GameModel gameModel = null;

    private PathBuilder() {}

    public static PathBuilder newBuilder() {
        return new PathBuilder();
    }

    public PathBuilder setGameModel(GameModel model) {
        gameModel = model;
        return this;
    }

    public PathBuilder setDistance(int allowance) {
        distance = allowance;
        return this;
    }

    public PathBuilder setHeight(int allowance) {
        height = allowance;
        return this;
    }

    public PathBuilder setStart(Entity entity) {
        startEntity = entity;
        return this;
    }

    public PathBuilder setEnd(Entity entity) {
        endEntity = entity;
        return this;
    }

    public PathBuilder setRespectObstructions(boolean respect) {
        respectObstructions = respect;
        return this;
    }

    private Map<Entity, Entity> createGraph() {
        if (startEntity == null) { return null; }

        Map<Entity, Integer> tileToDepthMap = new HashMap<>();
        Map<Entity, Entity> tileToParentMap = new HashMap<>();

        Queue<Entity> toVisit = new LinkedList<>();
        Set<Entity> visited = new HashSet<>();

        toVisit.add(startEntity);
        tileToDepthMap.put(startEntity, 0);
        tileToParentMap.put(startEntity, null);

        while (!toVisit.isEmpty()) {

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
                Entity childEntity = gameModel.tryFetchingTileAt(row, col);

                // skip tiles off the map or being occupied or already visited
                if (childEntity == null) { continue; }
                if (visited.contains(childEntity)) { continue; }

                // ensure the tile isn't obstructed and within jump or move
                Tile childTile = childEntity.get(Tile.class);
                if (respectObstructions && childTile.isObstructed()) { continue; }

                // if height allowance is set to -1, ignore
                if (height != -1) {
                    boolean isJumping = parentTile.getHeight() < childTile.getHeight();
                    int jumpCost = Math.abs(parentTile.getHeight() - childTile.getHeight());   
                    if (isJumping && jumpCost > height) {
                        // If the first tile is too high, if youre close enough, allow 1 movement in the direction
                        if (parentEntity == startEntity) {
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
        visited.add(startEntity);

        return tileToParentMap;
    }

    public Deque<Entity> getTilesWithinMovementPath() {
        Map<Entity, Entity> tileToParentMap = createGraph();
        Deque<Entity> result = new LinkedList<>();

        if (tileToParentMap == null) { return result; }
        if (!tileToParentMap.containsKey(startEntity)) { return result; }
        if (!tileToParentMap.containsKey(endEntity)) { return result; }

        Entity current = endEntity;
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

    public Set<Entity> getAllTilesWithinLineOfSight() {
        Map<Entity, Entity> graph = createGraph();
        Set<Entity> result = new HashSet<>();
        if (graph == null) { return result; }
        if (startEntity == null || !graph.containsKey(startEntity)) { return result; }
        for (Map.Entry<Entity, Entity> entry : graph.entrySet()) {
            digitalDifferentialAnalyzer(gameModel, startEntity, entry.getKey(), distance, result);
//            raycast(gameModel, startEntity, entry.getKey(), distance, result);
//            refreshVisibility(gameModel, startEntity, distance, result);
        }
        return result;
    }

    public Set<Entity> getAllTilesWithinLineOfSightPath() {
        Map<Entity, Entity> graph = createGraph();
        Set<Entity> result = new HashSet<>();
        if (graph == null) { return result; }
        if (startEntity == null || !graph.containsKey(startEntity)) { return result; }
        if (endEntity == null || !graph.containsKey(endEntity)) { return result; }
//        raycast(gameModel, startEntity, endEntity, distance, result);
        Entity mousedAt = gameModel.tryFetchingTileMousedAt();
        digitalDifferentialAnalyzer(gameModel, startEntity, mousedAt, distance, result);
//        digitalDifferentialAnalyzer(gameModel, startEntity, endEntity, distance, result);
        return result;
    }

    public void digitalDifferentialAnalyzer(GameModel model, Entity start, Entity end, int distance, Set<Entity> result) {

        Vector src = start.get(Vector.class).copy();
        src.x += Constants.CURRENT_SPRITE_SIZE / 2f;
        src.y += Constants.CURRENT_SPRITE_SIZE / 2f;
        src.x /= Constants.CURRENT_SPRITE_SIZE;
        src.y /= Constants.CURRENT_SPRITE_SIZE;

        Vector dst = end.get(Vector.class).copy();
        dst.x += Constants.CURRENT_SPRITE_SIZE / 2f;
        dst.y += Constants.CURRENT_SPRITE_SIZE / 2f;
        dst.x /= Constants.CURRENT_SPRITE_SIZE;
        dst.y /= Constants.CURRENT_SPRITE_SIZE;

        Vector rayCell = new Vector();
        Vector intersectionPoint = new Vector();

        double dy = dst.y - src.y;
        double dx = dst.x - src.x;
        double DIV_BY_ZERO_REPLACE = 0.000000001;
        dx = dx == 0 ? DIV_BY_ZERO_REPLACE : dx;
        dy = dy == 0 ? DIV_BY_ZERO_REPLACE : dy;
        double distInv = 1.0 / Math.hypot(dx, dy);
        dx *= distInv;
        dy *= distInv;
        int dxSign = (int) Math.signum(dx);
        int dySign = (int) Math.signum(dy);
        rayCell.copy((int) src.x, (int) src.y);
        double startDy = rayCell.y + dySign * 0.5 + 0.5 - src.y;
        double startDx = rayCell.x + dxSign * 0.5 + 0.5 - src.x;
        double distDx = Math.abs(1 / dx);
        double distDy = Math.abs(1 / dy);
        double totalDistDx = distDx * dxSign * startDx;
        double totalDistDy = distDy * dySign * startDy;
        double intersectionDistance = 0;
        result.add(startEntity);
        int travels = 1;

        while (distance > travels) {
            if (totalDistDx < totalDistDy) {
                rayCell.x += dxSign;
                intersectionDistance = totalDistDx;
                totalDistDx += distDx;
            } else {
                rayCell.y += dySign;
                intersectionDistance = totalDistDy;
                totalDistDy += distDy;
            }

            Entity entity = model.tryFetchingTileAt((int) rayCell.y, (int) rayCell.x);
            if (entity == null) { break; }
            travels++;
            result.add(entity);
            Tile tile = entity.get(Tile.class);
            if (tile.isObstructed() || entity == end) {
                double ipx = src.x + intersectionDistance * dx;
                double ipy = src.y + intersectionDistance * dy;
                intersectionPoint.copy((float) ipx, (float) ipy);
                return;
            }
        }
    }

//    public void digitalDifferentialAnalyzer(GameModel model, Entity start, Entity end, int maxRayDistance, Set<Entity> result) {
//
//        Vector src = start.get(Vector.class).copy();
//        src.x += Constants.CURRENT_SPRITE_SIZE / 2f;
//        src.y += Constants.CURRENT_SPRITE_SIZE / 2f;
//        src.x /= Constants.CURRENT_SPRITE_SIZE;
//        src.y /= Constants.CURRENT_SPRITE_SIZE;
//
//        Vector dst = end.get(Vector.class).copy();
//        dst.x += Constants.CURRENT_SPRITE_SIZE / 2f;
//        dst.y += Constants.CURRENT_SPRITE_SIZE / 2f;
//        dst.x /= Constants.CURRENT_SPRITE_SIZE;
//        dst.y /= Constants.CURRENT_SPRITE_SIZE;
//
//        Vector rayCell = new Vector();
//        Vector intersectionPoint = new Vector();
//
//        double dy = dst.y - src.y;
//        double dx = dst.x - src.x;
//        double DIV_BY_ZERO_REPLACE = 0.000000001;
//        dx = dx == 0 ? DIV_BY_ZERO_REPLACE : dx;
//        dy = dy == 0 ? DIV_BY_ZERO_REPLACE : dy;
//        double distInv = 1.0 / Math.hypot(dx, dy);
//        dx *= distInv;
//        dy *= distInv;
//        int dxSign = (int) Math.signum(dx);
//        int dySign = (int) Math.signum(dy);
//        rayCell.copy((int) src.x, (int) src.y);
//        double startDy = rayCell.y + dySign * 0.5 + 0.5 - src.y;
//        double startDx = rayCell.x + dxSign * 0.5 + 0.5 - src.x;
//        double distDx = Math.abs(1 / dx);
//        double distDy = Math.abs(1 / dy);
//        double totalDistDx = distDx * dxSign * startDx;
//        double totalDistDy = distDy * dySign * startDy;
//        double intersectionDistance = 0;
//        result.add(startEntity);
//        int travels = 0;
//
//        while (intersectionDistance < maxRayDistance) {
//            if (totalDistDx < totalDistDy) {
//                rayCell.x += dxSign;
//                intersectionDistance = totalDistDx;
//                totalDistDx += distDx;
//            } else {
//                rayCell.y += dySign;
//                intersectionDistance = totalDistDy;
//                totalDistDy += distDy;
//            }
//
//            Entity entity = model.tryFetchingTileAt((int) rayCell.y, (int) rayCell.x);
//            if (entity == null) { break; }
//            result.add(entity);
//            travels++;
//            Tile tile = entity.get(Tile.class);
//            if (tile.isObstructed() || entity == endEntity) {
//                double ipx = src.x + intersectionDistance * dx;
//                double ipy = src.y + intersectionDistance * dy;
//                intersectionPoint.copy((float) ipx, (float) ipy);
//                return;
//            }
//        }
//    }

//    void refreshVisibility(GameModel model, Entity start, int distanceallowed, Set<Entity> result) {
//        for (var octant = 0; octant < 8; octant++) {
//            refreshOctant(model, start, octant, distanceallowed, result);
//        }
//    }
//
//    void refreshOctant(GameModel model, Entity start, int octant, int distanceAllowed, Set<Entity> result) {
//        ShadowLine line = new ShadowLine();
//        boolean fullShadow = false;
//
////        Tile startTile = start.get(Tile.class);
////
////        Entity current = start;
////        Tile currentTile = start.get(Tile.class);
//
//        // Skip the center by starting at 1. 'Rows' maybe vertical or horizontal
//        for (int row = 1; row < 100; row++) {
//            // Stop once we go out of bounds.
//            Tile transform = transformOctant(
//                    start.get(Tile.class).row + row,
//                    start.get(Tile.class).column,
//                    octant
//            );
//            Entity toCheck = model.tryFetchingTileAt(transform.row, transform.column);
//            if (toCheck == null) { break; }
//
//            for (int column = 0; column <= row; column++) {
//                transform = transformOctant(
//                        start.get(Tile.class).row + row,
//                        start.get(Tile.class).column + column,
//                        octant
//                );
//                toCheck = model.tryFetchingTileAt(transform.row, transform.column);
//                if (toCheck == null) { break; }
//
//                // If we've traversed out of bounds, bail on this row.
//                if (fullShadow) {
//                    result.remove(toCheck);
//                } else {
//                    Shadow projection = projectTile(row, column);
//                    // Set the visibility of this tile.
//                    boolean  visible = !line.isInShadow(projection);
//                    if (visible) {
//                        result.add(toCheck);
//                    } else {
//                        result.remove(toCheck);
//                    }
//
//                    // Add any opaque tiles to the shadow map.
//                    if (visible && toCheck.get(Tile.class).isObstructed()) {
//                        line.add(projection);
//                        fullShadow = line.isFullShadow();
//                    }
//                }
//            }
//        }

//        Entity current = startingPoint;
//        if (current == null) { return; }
//        Tile currentTile = startingPoint.get(Tile.class);
//        var line = new ShadowLine();
//        var fullShadow = false;
//
//        for (var row = 1; row < distanceAllowed; row++) {
//            // Stop once we go out of bounds.
//            Tile transform1 = transformOctant(currentTile.row + row, 0, octant);
//            current = model.tryFetchingTileAt(
//                    currentTile.row + transform1.row, currentTile.column + transform1.column);
//            if (current == null) break;
//            currentTile = current.get(Tile.class);
//
//            for (var col = 0; col <= row; col++) {
////                var pos = hero + transformOctant(row, col, octant);
//                transform1 = transformOctant(row, col, octant);
//                current = model.tryFetchingTileAt(currentTile.row + transform1.row, currentTile.column + transform1.column);
//
//                // If we've traversed out of bounds, bail on this row.
//                if (current == null) break;
//                currentTile = startingPoint.get(Tile.class);
//
//                if (fullShadow) {
////                    tiles[pos].isVisible = false;
//                    result.add(current);
//                } else {
//                    var projection = projectTile(row, col);
//
//                    // Set the visibility of this tile.
//                    var visible = !line.isInShadow(projection);
//                    if (visible) {
//                        result.add(current);
//                    }
////                    tiles[pos].isVisible = visible;
//
//                    // Add any opaque tiles to the shadow map.
//
//                    if (visible && currentTile.isObstructed()) {
////                    if (visible && tiles[pos].isWall) {
//                        line.add(projection);
//                        fullShadow = line.isFullShadow();
//                    }
//                }
//            }
//        }
//    }

//    private Shadow projectTile(int row, int col) {
//        var topLeft = col / (row + 2);
//        var bottomRight = (col + 1) / (row + 1);
//        return new Shadow(topLeft, bottomRight);
//    }
//
//    private static Tile transformOctant(int row, int col, int octant) {
//        return switch (octant) {
//            case 0 -> new Tile(col, -row);
//            case 1 -> new Tile(row, -col);
//            case 2 -> new Tile(row, col);
//            case 3 -> new Tile(col, row);
//            case 4 -> new Tile(-col, row);
//            case 5 -> new Tile(-row, col);
//            case 6 -> new Tile(-row, -col);
//            case 7 -> new Tile(-col, -row);
//            default -> new Tile(0, 0);
//        };
//    }

    private static void raycast(GameModel model, Entity start, Entity end, int length, Set<Entity> result) {
        Tile startTile = start.get(Tile.class);
        Tile endTile = end.get(Tile.class);

        int columnIncrement = (endTile.column > startTile.column) ? 1 : -1;
        int rowIncrement = (endTile.row > startTile.row) ? 1 : -1;

        int columnDelta = Math.abs(endTile.column - startTile.column);
        int rowDelta = Math.abs(endTile.row - startTile.row);

        int error = columnDelta - rowDelta;
        int errorCorrectColumn = columnDelta * 2;
        int errorCorrectRow = rowDelta * 2;

        Entity current = null;
        Tile currentTile = startTile;

        int column = currentTile.column;
        int row = currentTile.row;

//        for (int iteration = 0; iteration < length; iteration++) {
        while (currentTile != endTile) {


            current = model.tryFetchingTileAt(row, column);
            if (current == null) { return; }
            result.add(current);

            currentTile = current.get(Tile.class);
            if (currentTile.isObstructed() && currentTile != startTile) { return; }
            if (currentTile == endTile) { return; }

            if (error > 0) {
                column = column + columnIncrement;
                error = error - errorCorrectRow;
            } else if (error < 0) {
                row = row + rowIncrement;
                error = error + errorCorrectColumn;
            } else {
//                row = row + rowIncrement;
//                column = column + columnIncrement;
                if (row != endTile.row) {
                    row = row + rowIncrement;
                }
                if (column != endTile.column) {
                    column = column + columnIncrement;
                }
//                iteration++; // Diagonal tiles traversal cost 2 movement instead of 1
            }
        }
    }

    private static void rayCastV1(GameModel model, Entity start, Entity end, int length, Set<Entity> result) {
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
                if (tile.isObstructed() && entity != start) { return; }
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

    public static void rayCastV0(GameModel model, Entity start, Entity end, int length, Set<Entity> result) {
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
                if (tile.isObstructed() && entity != start) { return; }
            }

            if (error > 0) {
                column += column_inc;
                error -= rowDelta;
            } else if (error < 0) {
                row += row_inc;
                error += columnDelta;
            } else {
                row += row_inc;
                column += column_inc;
            }
        }
    }
}
