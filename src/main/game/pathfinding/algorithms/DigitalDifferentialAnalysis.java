package main.game.pathfinding.algorithms;

import main.game.components.Size;
import main.game.components.tile.Tile;
import main.game.components.Vector;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.LinkedHashSet;
import java.util.Set;

// refs.: javidx9 - https://www.youtube.com/watch?v=NbSee-XM7WA&t=815s
//        https://lodev.org/cgtutor/raycasting.html
public class DigitalDifferentialAnalysis {
    private final Set<Entity> results = new LinkedHashSet<>();
    private final Set<Vector> startpoints = new LinkedHashSet<>();
    private final Set<Vector> endpoints = new LinkedHashSet<>();
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
        Vector source = start.get(Vector.class);
        Size dim = start.get(Size.class);

        startpoints.clear();
//        startpoints.add(new Vector(source.x, source.y));
//        startpoints.add(new Vector(source.x + dim.width, source.y));
//        startpoints.add(new Vector(source.x + dim.width, source.y + dim.height));
//        startpoints.add(new Vector(source.x, source.y + dim.height));
        startpoints.add(new Vector(source.x + (dim.width / 2), source.y + (dim.height / 2)));

        // Get the ending point for the ray, and the 5 points to try and hit
        Vector destination = end.get(Vector.class);
        dim = end.get(Size.class);

        // Respectively, top left, top right, bottom right, bottom left, center
        endpoints.clear();
        endpoints.add(new Vector(destination.x, destination.y));
        endpoints.add(new Vector(destination.x + dim.width, destination.y));
        endpoints.add(new Vector(destination.x + dim.width, destination.y + dim.height));
        endpoints.add(new Vector(destination.x, destination.y + dim.height));
        endpoints.add(new Vector(destination.x + (dim.width / 2), destination.y + (dim.height / 2)));

        for (Vector startpoint : startpoints) {
            startpoint.x /= dim.width;
            startpoint.y /= dim.height;

            for (Vector endpoint : endpoints) {
                endpoint.x /= dim.width;
                endpoint.y /= dim.height;

                results.clear();
                rayCast(startpoint, endpoint, results);

                if (!results.contains(end) || !results.contains(start)) { continue; }
                return results;
            }
        }

        return results;
    }

    private void rayCast(Vector source, Vector destination, Set<Entity> result) {
        Vector rayCell = new Vector();
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

    private void rayCastV1(Vector source, Vector destination, Vector rayCell, Set<Entity> result) {
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

    public void rayCastV2(GameModel model, Entity start, Entity end, int distance, Set<Entity> result) {

        Vector src = start.get(Vector.class).copy();
        Size dim = start.get(Size.class);
        src.x += dim.width / 2f;
        src.y += dim.height / 2f;
        src.x /= dim.width;
        src.y /= dim.height;

        Vector dst = end.get(Vector.class).copy();
        dim = end.get(Size.class);
        dst.x += dim.width / 2f;
        dst.y += dim.height / 2f;
        dst.x /= dim.width;
        dst.y /= dim.height;

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
        result.add(start);
        int travels = 1;
        Entity entity = null;
        Tile tile = null;

        while (distance > travels) {
            if (totalDistDx < totalDistDy) {
                rayCell.x += dxSign;
                intersectionDistance = totalDistDx;
                totalDistDx += distDx;
            } else if (totalDistDx > totalDistDy) {
                rayCell.y += dySign;
                intersectionDistance = totalDistDy;
                totalDistDy += distDy;
            } else {
                // Try both sides
                rayCell.y += dySign;
                rayCell.x += dxSign;
                totalDistDy += distDy;
                totalDistDx += distDx;
                rayCell.x += dxSign;
                entity = model.tryFetchingTileAt((int) rayCell.y, (int) rayCell.x);
                if (entity == null || entity.get(Tile.class).isNotNavigable()) {
                    rayCell.x -= dxSign;

                    rayCell.y += dySign;
                    entity = model.tryFetchingTileAt((int) rayCell.y, (int) rayCell.x);
                    if (entity == null || entity.get(Tile.class).isNotNavigable()) {
                        rayCell.y -= dySign;
                        break;
                    } else {
                        intersectionDistance = totalDistDy;
                        totalDistDy += distDy;
                    }
                } else {
                    intersectionDistance = totalDistDx;
                    totalDistDx += distDx;
                }
                travels--;
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
}
