package main.game.pathfinding.algorithms;

import main.game.main.Settings;
import main.game.components.tile.Tile;
import main.constants.Vector3f;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.LinkedHashSet;
import java.util.Set;

// refs.: javidx9 - https://www.youtube.com/watch?v=NbSee-XM7WA&t=815s
//        https://lodev.org/cgtutor/raycasting.html
public class DigitalDifferentialAnalysis {
    private final Set<Entity> results = new LinkedHashSet<>();
    private final Set<Vector3f> startpoints = new LinkedHashSet<>();
    private final Set<Vector3f> endpoints = new LinkedHashSet<>();
    private Entity start = null;
    private Entity end = null;
    private GameModel model = null;
    private int distance = -1;
    public DigitalDifferentialAnalysis addStart(Entity entity) { start = entity; return this; }
    public DigitalDifferentialAnalysis addEnd(Entity entity) { end = entity; return this; }
    public DigitalDifferentialAnalysis addModel(GameModel gameModel) { model = gameModel; return this; }
    public DigitalDifferentialAnalysis addDistance(int num) { distance = num; return this; }
    public Set<Entity> perform() {
        // Setup starting point where the ray starts, sits at the center of tile
        Vector3f source = start.get(Vector3f.class);
//        Size dim = start.get(Size.class);
        float spriteWidth = Settings.getInstance().getSpriteWidth();
        float spriteHeight = Settings.getInstance().getSpriteHeight();

        startpoints.clear();
//        startpoints.add(new Vector(source.x, source.y));
//        startpoints.add(new Vector(source.x + dim.width, source.y));
//        startpoints.add(new Vector(source.x + dim.width, source.y + dim.height));
//        startpoints.add(new Vector(source.x, source.y + dim.height));
        startpoints.add(new Vector3f(source.x + (spriteWidth/ 2), source.y + (spriteHeight / 2)));

        // Get the ending point for the ray, and the 5 points to try and hit
        Vector3f destination = end.get(Vector3f.class);
//        dim = end.get(Size.class);

        // Respectively, top left, top right, bottom right, bottom left, center
        endpoints.clear();
        endpoints.add(new Vector3f(destination.x, destination.y));
        endpoints.add(new Vector3f(destination.x + spriteWidth, destination.y));
        endpoints.add(new Vector3f(destination.x + spriteWidth, destination.y + spriteHeight));
        endpoints.add(new Vector3f(destination.x, destination.y + spriteHeight));
        endpoints.add(new Vector3f(destination.x + (spriteWidth / 2), destination.y + (spriteHeight / 2)));

        for (Vector3f startpoint : startpoints) {
            startpoint.x /= spriteWidth;
            startpoint.y /= spriteHeight;

            for (Vector3f endpoint : endpoints) {
                endpoint.x /= spriteWidth;
                endpoint.y /= spriteHeight;

                results.clear();
                rayCast(startpoint, endpoint, results);

                if (!results.contains(end) || !results.contains(start)) { continue; }
                return results;
            }
        }

        return results;
    }

    private void rayCast(Vector3f source, Vector3f destination, Set<Entity> result) {
        Vector3f rayCell = new Vector3f();
        double dy = destination.y - source.y;
        double dx = destination.x - source.x;
        double DIV_BY_ZERO_REPLACE = 0.000000001;
        dx = dx == 0 ? DIV_BY_ZERO_REPLACE : dx;
        dy = dy == 0 ? DIV_BY_ZERO_REPLACE : dy;
        double distInv = 1.0 / Math.hypot(dx, dy);
        dx *= distInv;
        dy *= distInv;
        int dxSign = (int) Math.signum(dx);
        int dySign = (int) Math.signum(dy);
        rayCell.copy((int) source.x, (int) source.y);
        double startDy = rayCell.y + dySign * 0.5 + 0.5 - source.y;
        double startDx = rayCell.x + dxSign * 0.5 + 0.5 - source.x;
        double distDx = Math.abs(1 / dx);
        double distDy = Math.abs(1 / dy);
        double totalDistDx = distDx * dxSign * startDx;
        double totalDistDy = distDy * dySign * startDy;

        result.add(start);
        Entity entity;
        Tile tile;

        for (int travel = 0; travel < distance; travel++) {
            if (totalDistDx < totalDistDy) {
                rayCell.x += dxSign;
                totalDistDx += distDx;
            } else {
                rayCell.y += dySign;
                totalDistDy += distDy;
            }

            entity = model.tryFetchingTileAt((int) rayCell.y, (int) rayCell.x);
            if (entity == null) { return; }
            result.add(entity);
            tile = entity.get(Tile.class);
            if (tile.isNotNavigable()) { return; }
            if (entity == end) { return; }
        }
    }

    private void rayCastV1(Vector3f source, Vector3f destination, Vector3f rayCell, Set<Entity> result) {
        double dy = destination.y - source.y;
        double dx = destination.x - source.x;
        double DIV_BY_ZERO_REPLACE = 0.000000001;
        dx = dx == 0 ? DIV_BY_ZERO_REPLACE : dx;
        dy = dy == 0 ? DIV_BY_ZERO_REPLACE : dy;
        double distInv = 1.0 / Math.hypot(dx, dy);
        dx *= distInv;
        dy *= distInv;
        int dxSign = (int) Math.signum(dx);
        int dySign = (int) Math.signum(dy);
        rayCell.copy((int) source.x, (int) source.y);
        double startDy = rayCell.y + dySign * 0.5 + 0.5 - source.y;
        double startDx = rayCell.x + dxSign * 0.5 + 0.5 - source.x;
        double distDx = Math.abs(1 / dx);
        double distDy = Math.abs(1 / dy);
        double totalDistDx = distDx * dxSign * startDx;
        double totalDistDy = distDy * dySign * startDy;
        double intersectionDistance = 0;
        result.add(start);
        int travels = 1;
        Entity entity = null;
        Tile tile = null;

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

            entity = model.tryFetchingTileAt((int) rayCell.y, (int) rayCell.x);
            if (entity == null) { break; }
            travels++;
            result.add(entity);
            tile = entity.get(Tile.class);
            if ((tile.isNotNavigable() && entity != start) || entity == end) {
                return;
            }
        }
    }

//    public void rayCastV2(GameModel model, Entity start, Entity end, int distance, Set<Entity> result) {
//
//        Vector3f src = start.get(Vector3f.class).copy();
//        Size dim = start.get(Size.class);
//        src.x += dim.width / 2f;
//        src.y += dim.height / 2f;
//        src.x /= dim.width;
//        src.y /= dim.height;
//
//        Vector3f dst = end.get(Vector3f.class).copy();
//        dim = end.get(Size.class);
//        dst.x += dim.width / 2f;
//        dst.y += dim.height / 2f;
//        dst.x /= dim.width;
//        dst.y /= dim.height;
//
//        Vector3f rayCell = new Vector3f();
//        Vector3f intersectionPoint = new Vector3f();
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
//        result.add(start);
//        int travels = 1;
//        Entity entity = null;
//        Tile tile = null;
//
//        while (distance > travels) {
//            if (totalDistDx < totalDistDy) {
//                rayCell.x += dxSign;
//                intersectionDistance = totalDistDx;
//                totalDistDx += distDx;
//            } else if (totalDistDx > totalDistDy) {
//                rayCell.y += dySign;
//                intersectionDistance = totalDistDy;
//                totalDistDy += distDy;
//            } else {
//                // Try both sides
//                rayCell.y += dySign;
//                rayCell.x += dxSign;
//                totalDistDy += distDy;
//                totalDistDx += distDx;
//                rayCell.x += dxSign;
//                entity = model.tryFetchingTileAt((int) rayCell.y, (int) rayCell.x);
//                if (entity == null || entity.get(Tile.class).isNotNavigable()) {
//                    rayCell.x -= dxSign;
//
//                    rayCell.y += dySign;
//                    entity = model.tryFetchingTileAt((int) rayCell.y, (int) rayCell.x);
//                    if (entity == null || entity.get(Tile.class).isNotNavigable()) {
//                        rayCell.y -= dySign;
//                        break;
//                    } else {
//                        intersectionDistance = totalDistDy;
//                        totalDistDy += distDy;
//                    }
//                } else {
//                    intersectionDistance = totalDistDx;
//                    totalDistDx += distDx;
//                }
//                travels--;
//            }
//
//            entity = model.tryFetchingTileAt((int) rayCell.y, (int) rayCell.x);
//            if (entity == null) { break; }
//            travels++;
//            result.add(entity);
//            tile = entity.get(Tile.class);
//            if ((tile.isNotNavigable() && entity != start) || entity == end) {
//                return;
//            }
//        }
//    }
}
