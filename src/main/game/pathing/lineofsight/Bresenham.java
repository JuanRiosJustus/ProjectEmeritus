package main.game.pathing.lineofsight;

import main.constants.Direction;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.*;

public class Bresenham extends PathingAlgorithm {
    public LinkedHashSet<Entity> computeLineOfSight(GameModel model, Entity start, Entity end) {
        LinkedHashSet<Entity> result = new LinkedHashSet<>();
        if (end == null) { return result; }
        result = computeLineOfSight(model, start, end, true);
        return result;
    }
    public LinkedHashSet<Entity> computeLineOfSight(GameModel model, Entity start, Entity end, boolean respectfully) {
        Tile startTile = start.get(Tile.class);
        int startRow = startTile.getRow();
        int startColumn = startTile.getColumn();
        Tile endTile = end.get(Tile.class);
        int endRow = endTile.getRow();
        int endColumn = endTile.getColumn();

        int dColumn =  Math.abs(endColumn - startColumn);
        int sColumn = startColumn < endColumn ? 1 : -1;
        int dRow = -Math.abs(endRow - startRow);
        int sRow = startRow < endRow ? 1 : -1;
        int err = dColumn + dRow;  /* error value e_xy */
        LinkedHashSet<Entity> line = new LinkedHashSet<>();

        while (true) {
            Entity entity = model.tryFetchingEntityAt(startRow, startColumn);
            Tile tile = entity.get(Tile.class);
            line.add(entity);

            boolean shouldRespectNavigability = respectfully && tile.isNotNavigable();
            if (entity != start && shouldRespectNavigability) {
                break;
            }

            if (startColumn == endColumn && startRow == endRow) {
                break;
            }
            int e2 = 2 * err;
            if (e2 >= dRow) {
                err += dRow;
                startColumn += sColumn;
            }
            if (e2 <= dColumn) {
                err += dColumn;
                startRow += sRow;
            }
        }

        return line;
    }

    /**
     * Finds all tiles visible within the specified range of the origin using Bresenham LoS.
     *
     * @param model The game model containing the grid and entities.
     * @param start The starting entity for LoS.
     * @param range The maximum range to check for visibility.
     * @return A set of all visible tiles within range.
     */
    public LinkedHashSet<Entity> computeAreaOfSight(GameModel model, Entity start, int range, boolean respectfully) {
        LinkedHashSet<Entity> visibleTiles = new LinkedHashSet<>();
        if (start == null) {
            return visibleTiles;
        }

        Tile originTile = start.get(Tile.class);
        int originRow = originTile.getRow();
        int originColumn = originTile.getColumn();

        // Iterate through all tiles within the square bounding the range
        for (int rowOffset = -range; rowOffset <= range; rowOffset++) {
            for (int columnOffset = -range; columnOffset <= range; columnOffset++) {
                int targetRow = originRow + rowOffset;
                int targetColumn = originColumn + columnOffset;

                // Skip if the tile is out of range (circle check)
                if (Math.sqrt(rowOffset * rowOffset + columnOffset * columnOffset) > range) {
                    continue;
                }

                // Skip if the tile does not exist
                Entity target = model.tryFetchingEntityAt(targetRow, targetColumn);
                if (target == null) {
                    continue;
                }

                // Compute the line of sight to the target
                LinkedHashSet<Entity> line = computeLineOfSight(model, start, target, respectfully);

                // Add all tiles from the line to the visible set
                visibleTiles.addAll(line);
            }
        }

        return visibleTiles;
    }
}
