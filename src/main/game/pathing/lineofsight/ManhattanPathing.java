package main.game.pathing.lineofsight;

import main.game.components.TileComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.LinkedHashSet;

public class ManhattanPathing extends PathingAlgorithms {

    /**
     * Computes the Manhattan line of sight between two entities.
     *
     * @param model        The game model containing the grid and entities.
     * @param start        The starting entity.
     * @param end          The target entity.
     * @param respectfully Whether to respect obstacles (e.g., walls).
     * @return A set of entities forming the line of sight.
     */
    public LinkedHashSet<Entity> computeLineOfSight(GameModel model, Entity start, Entity end, boolean respectfully) {
        LinkedHashSet<Entity> line = new LinkedHashSet<>();

        // Validate input entities
        if (start == null || end == null) return line;

        // Retrieve the starting and ending tiles
        TileComponent startTile = start.get(TileComponent.class);
        TileComponent endTile = end.get(TileComponent.class);

        // Ensure both tiles are valid
        if (startTile == null || endTile == null) return line;

        // Get the row and column indices for start and end tiles
        int startRow = startTile.getRow();
        int startColumn = startTile.getColumn();
        int endRow = endTile.getRow();
        int endColumn = endTile.getColumn();

        // Calculate the Manhattan distance differences
        int dRow = Math.abs(endRow - startRow);
        int dColumn = Math.abs(endColumn - startColumn);

        // Initialize the current row and column to the starting point
        int currentRow = startRow;
        int currentColumn = startColumn;

        // Determine the step direction for row and column traversal
        int rowStep = (endRow > startRow) ? 1 : -1;
        int columnStep = (endColumn > startColumn) ? 1 : -1;

        // Add the starting tile to the line of sight
        line.add(start);

        // Traverse the grid toward the target tile
        while (currentRow != endRow || currentColumn != endColumn) {
            // Prioritize movement based on which difference is larger
            if (dRow >= dColumn && currentRow != endRow) {
                currentRow += rowStep;
                dRow--;
            } else if (currentColumn != endColumn) {
                currentColumn += columnStep;
                dColumn--;
            }

            // Fetch the entity at the current row and column
            Entity entity = model.tryFetchingEntityAt(currentRow, currentColumn);
            if (entity != null) {
                TileComponent tile = entity.get(TileComponent.class);

                // Ensure the tile exists
                if (tile == null) continue;

                // Add the current tile to the line of sight
                line.add(entity);

                // If respectfully is true and the tile is not navigable, stop the traversal
                if (respectfully && tile.isNotNavigable()) break;
            }
        }

        return line;
    }

    /**
     * Finds all tiles visible within the specified Manhattan range of the origin using Manhattan LoS.
     *
     * @param model        The game model containing the grid and entities.
     * @param start        The starting entity for LoS.
     * @param range        The maximum range to check for visibility.
     * @param respectfully Whether to respect obstacles in line of sight.
     * @return A set of all visible tiles within range.
     */
    public LinkedHashSet<Entity> computeAreaOfSight(GameModel model, Entity start, int range, boolean respectfully) {
        LinkedHashSet<Entity> visibleTiles = new LinkedHashSet<>();

        // Validate the starting entity
        if (start == null) return visibleTiles;

        // Retrieve the starting tile
        TileComponent originTile = start.get(TileComponent.class);

        // Ensure the starting tile is valid
        if (originTile == null) return visibleTiles;

        // Get the row and column indices for the starting tile
        int originRow = originTile.getRow();
        int originColumn = originTile.getColumn();

        // Iterate over all possible tiles within the diamond-shaped Manhattan range
        for (int rowOffset = -range; rowOffset <= range; rowOffset++) {
            for (int columnOffset = -range + Math.abs(rowOffset); columnOffset <= range - Math.abs(rowOffset); columnOffset++) {
                // Compute the target row and column
                int targetRow = originRow + rowOffset;
                int targetColumn = originColumn + columnOffset;

                // Fetch the entity at the target row and column
                Entity target = model.tryFetchingEntityAt(targetRow, targetColumn);
                if (target == null) continue;

                // Compute the line of sight to the target entity
                LinkedHashSet<Entity> line = computeLineOfSight(model, start, target, respectfully);

                // Add all tiles from the computed line to the visible set
                visibleTiles.addAll(line);
            }
        }

        return visibleTiles;
    }
}