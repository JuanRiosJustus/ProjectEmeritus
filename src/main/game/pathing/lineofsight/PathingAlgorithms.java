package main.game.pathing.lineofsight;

import main.constants.Direction;
import main.game.components.tile.TileComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.EntityStore;

import java.util.*;

public class PathingAlgorithms {

    /**
     * ██╗     ██╗███╗   ██╗███████╗     ██████╗ ███████╗    ███████╗██╗ ██████╗ ██╗  ██╗████████╗
     * ██║     ██║████╗  ██║██╔════╝    ██╔═══██╗██╔════╝    ██╔════╝██║██╔════╝ ██║  ██║╚══██╔══╝
     * ██║     ██║██╔██╗ ██║█████╗      ██║   ██║█████╗      ███████╗██║██║  ███╗███████║   ██║
     * ██║     ██║██║╚██╗██║██╔══╝      ██║   ██║██╔══╝      ╚════██║██║██║   ██║██╔══██║   ██║
     * ███████╗██║██║ ╚████║███████╗    ╚██████╔╝██║         ███████║██║╚██████╔╝██║  ██║   ██║
     * ╚══════╝╚═╝╚═╝  ╚═══╝╚══════╝     ╚═════╝ ╚═╝         ╚══════╝╚═╝ ╚═════╝ ╚═╝  ╚═╝   ╚═╝
     */

    /**
     * Computes the Manhattan line of sight between two entities.
     *
     * @param model        The game model containing the grid and entities.
     * @param startTileID       The starting entity.
     * @param endTileID          The target entity.
     * @param respectfully Whether to respect obstacles (e.g., walls).
     * @return A set of entities forming the line of sight.
     */

    public List<String> computeLineOfSight(GameModel model, String startTileID, String endTileID) {
        return computeLineOfSight(model, startTileID, endTileID, true);
    }

    public List<String> computeLineOfSight(GameModel model, String startTileID, String endTileID, boolean respectfully) {
        Set<String> line = new LinkedHashSet<>();

        // Validate input entities
        if (startTileID == null || endTileID == null) {
            return new ArrayList<>();
        }

        // Retrieve the starting and ending tiles
        Entity start = getEntityWithID(startTileID);
        Entity end = getEntityWithID(endTileID);

        TileComponent startTile = start.get(TileComponent.class);
        TileComponent endTile = end.get(TileComponent.class);

        // Get the row and column indices for start and end tiles
        int startRow = startTile.getRow();
        int startColumn = startTile.getColumn();
        int endRow = endTile.getRow();
        int endColumn = endTile.getColumn();

        // Calculate the Manhattan distance differences and the total steps
        int dRow = Math.abs(endRow - startRow);
        int dColumn = Math.abs(endColumn - startColumn);
        int totalSteps = dRow + dColumn;

        // Initialize the current row and column to the starting point
        int currentRow = startRow;
        int currentColumn = startColumn;

        // Determine the step direction for row and column traversal
        int rowStep = (endRow > startRow) ? 1 : -1;
        int columnStep = (endColumn > startColumn) ? 1 : -1;

        // Add the starting tile to the line of sight
        line.add(startTileID);

        // Use a for loop to step from the start to the end tile.
        for (int step = 0; step < totalSteps; step++) {
            // Prioritize movement based on which difference is larger.
            if (dRow >= dColumn && currentRow != endRow) {
                currentRow += rowStep;
                dRow--;
            } else if (currentColumn != endColumn) {
                currentColumn += columnStep;
                dColumn--;
            }

            String tileEntityID = model.tryFetchingTileEntityID(currentRow, currentColumn);
            Entity tileEntity = getEntityWithID(tileEntityID);
            if (tileEntity == null) {
                continue;
            }

            // Add the current tile to the line of sight.
            line.add(tileEntityID);

            // If "respectfully" is true and the tile is not navigable, stop the traversal.
            TileComponent tile = tileEntity.get(TileComponent.class);
            if (respectfully && tile.isNotNavigable()) {
                break;
            }
        }

        return new ArrayList<>(line);
    }

//    public List<String> computeLineOfSight(GameModel model, String startTileID, String endTileID, boolean respectfully) {
//        Set<String> line = new LinkedHashSet<>();
//
//        // Validate input entities
//        if (startTileID == null || endTileID == null) { return new ArrayList<>(); }
//
//        // Retrieve the starting and ending tiles
//        Entity start = getEntityWithID(startTileID);
//        Entity end = getEntityWithID(endTileID);
//
//        TileComponent startTile = start.get(TileComponent.class);
//        TileComponent endTile = end.get(TileComponent.class);
//
//        // Get the row and column indices for start and end tiles
//        int startRow = startTile.getRow();
//        int startColumn = startTile.getColumn();
//        int endRow = endTile.getRow();
//        int endColumn = endTile.getColumn();
//
//        // Calculate the Manhattan distance differences
//        int dRow = Math.abs(endRow - startRow);
//        int dColumn = Math.abs(endColumn - startColumn);
//
//        // Initialize the current row and column to the starting point
//        int currentRow = startRow;
//        int currentColumn = startColumn;
//
//        // Determine the step direction for row and column traversal
//        int rowStep = (endRow > startRow) ? 1 : -1;
//        int columnStep = (endColumn > startColumn) ? 1 : -1;
//
//        // Add the starting tile to the line of sight
//        line.add(startTileID);
//
//        // Traverse the grid toward the target tile
//        while (currentRow != endRow || currentColumn != endColumn) {
//            // Prioritize movement based on which difference is larger
//            if (dRow >= dColumn && currentRow != endRow) {
//                currentRow += rowStep;
//                dRow--;
//            } else if (currentColumn != endColumn) {
//                currentColumn += columnStep;
//                dColumn--;
//            }
//
//            String tileEntityID = model.tryFetchingTileEntityID(currentRow, currentColumn);
//            Entity tileEntity = getEntityWithID(tileEntityID);
//            if (tileEntity == null) { continue; }
//
//            // Add the current tile to the line of sight
//            line.add(tileEntityID);
//
//            // If respectfully is true and the tile is not navigable, stop the traversal
//            TileComponent tile = tileEntity.get(TileComponent.class);
//            if (respectfully && tile.isNotNavigable()) { break; }
//        }
//
//        return new ArrayList<>(line);
//    }


    /**
     *  █████╗ ██████╗ ███████╗ █████╗      ██████╗ ███████╗    ███████╗██╗ ██████╗ ██╗  ██╗████████╗
     * ██╔══██╗██╔══██╗██╔════╝██╔══██╗    ██╔═══██╗██╔════╝    ██╔════╝██║██╔════╝ ██║  ██║╚══██╔══╝
     * ███████║██████╔╝█████╗  ███████║    ██║   ██║█████╗      ███████╗██║██║  ███╗███████║   ██║
     * ██╔══██║██╔══██╗██╔══╝  ██╔══██║    ██║   ██║██╔══╝      ╚════██║██║██║   ██║██╔══██║   ██║
     * ██║  ██║██║  ██║███████╗██║  ██║    ╚██████╔╝██║         ███████║██║╚██████╔╝██║  ██║   ██║
     * ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝     ╚═════╝ ╚═╝         ╚══════╝╚═╝ ╚═════╝ ╚═╝  ╚═╝   ╚═╝
     */
    /**
     * Finds all tiles visible within the specified Manhattan range of the origin using Manhattan LoS.
     *
     * @param model        The game model containing the grid and entities.
     * @param start        The starting entity for LoS.
     * @param range        The maximum range to check for visibility.
     * @param respectfully Whether to respect obstacles in line of sight.
     * @return A set of all visible tiles within range.
     */
    public List<String> computeAreaOfSight(GameModel model, String startTileID, int range) {
        return computeAreaOfSight(model, startTileID, range, true);
    }

    public List<String> computeAreaOfEffect(GameModel model, String startTileID, int range) {
        return computeAreaOfSight(model, startTileID, range - 1, true);
    }

    public List<String> computeAreaOfSight(GameModel model, String startID, int range, boolean respectfully) {
        Set<String> visibleTiles = new LinkedHashSet<>();

        Entity start = EntityStore.getInstance().get(startID);
        // Validate the starting entity
        if (start == null) { return new ArrayList<>(); }

        // Retrieve the starting tile
        TileComponent originTile = start.get(TileComponent.class);

        // Ensure the starting tile is valid
        if (originTile == null) { return new ArrayList<>(); };

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
//                Entity target = model.tryFetchingEntityAt(targetRow, targetColumn);
                String targetID = model.tryFetchingTileEntityID(targetRow, targetColumn);
                if (targetID == null) { continue; }

                // Compute the line of sight to the target entity
                List<String> line = computeLineOfSight(model, startID, targetID, respectfully);

                // Add all tiles from the computed line to the visible set
                visibleTiles.addAll(line);
            }
        }

        return new ArrayList<>(visibleTiles);
    }

    /**
     * ███╗   ███╗ ██████╗ ██╗   ██╗███████╗███╗   ███╗███████╗███╗   ██╗████████╗    ██████╗  █████╗ ████████╗██╗  ██╗
     * ████╗ ████║██╔═══██╗██║   ██║██╔════╝████╗ ████║██╔════╝████╗  ██║╚══██╔══╝    ██╔══██╗██╔══██╗╚══██╔══╝██║  ██║
     * ██╔████╔██║██║   ██║██║   ██║█████╗  ██╔████╔██║█████╗  ██╔██╗ ██║   ██║       ██████╔╝███████║   ██║   ███████║
     * ██║╚██╔╝██║██║   ██║╚██╗ ██╔╝██╔══╝  ██║╚██╔╝██║██╔══╝  ██║╚██╗██║   ██║       ██╔═══╝ ██╔══██║   ██║   ██╔══██║
     * ██║ ╚═╝ ██║╚██████╔╝ ╚████╔╝ ███████╗██║ ╚═╝ ██║███████╗██║ ╚████║   ██║       ██║     ██║  ██║   ██║   ██║  ██║
     * ╚═╝     ╚═╝ ╚═════╝   ╚═══╝  ╚══════╝╚═╝     ╚═╝╚══════╝╚═╝  ╚═══╝   ╚═╝       ╚═╝     ╚═╝  ╚═╝   ╚═╝   ╚═╝  ╚═╝
     */

    public List<String> getMovementPath(GameModel model, String startTileID, String endTileID) {
        return getMovementPath(model, startTileID, endTileID, true);
    }
    public List<String> getMovementPath(GameModel model, String startTileID, String endTileID, boolean respectfully) {
        Map<String, String> graph = createGraph(model, startTileID, -1, respectfully);

        if (!graph.containsKey(startTileID)) { return new ArrayList<>(); }
        if (!graph.containsKey(endTileID)) { return new ArrayList<>(); }

        LinkedList<String> queue = new LinkedList<>();
        String currentTileID = endTileID;
        while (currentTileID != null) {
            queue.addFirst(currentTileID);
            currentTileID = graph.get(currentTileID);
        }

        return new ArrayList<>(queue);
    }

    /**
     * ███╗   ███╗ ██████╗ ██╗   ██╗███████╗███╗   ███╗███████╗███╗   ██╗████████╗     █████╗ ██████╗ ███████╗ █████╗
     * ████╗ ████║██╔═══██╗██║   ██║██╔════╝████╗ ████║██╔════╝████╗  ██║╚══██╔══╝    ██╔══██╗██╔══██╗██╔════╝██╔══██╗
     * ██╔████╔██║██║   ██║██║   ██║█████╗  ██╔████╔██║█████╗  ██╔██╗ ██║   ██║       ███████║██████╔╝█████╗  ███████║
     * ██║╚██╔╝██║██║   ██║╚██╗ ██╔╝██╔══╝  ██║╚██╔╝██║██╔══╝  ██║╚██╗██║   ██║       ██╔══██║██╔══██╗██╔══╝  ██╔══██║
     * ██║ ╚═╝ ██║╚██████╔╝ ╚████╔╝ ███████╗██║ ╚═╝ ██║███████╗██║ ╚████║   ██║       ██║  ██║██║  ██║███████╗██║  ██║
     * ╚═╝     ╚═╝ ╚═════╝   ╚═══╝  ╚══════╝╚═╝     ╚═╝╚══════╝╚═╝  ╚═══╝   ╚═╝       ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝
     */

    public List<String> getMovementRange(GameModel model, String startTileID, int range) {
        return getMovementRange(model, startTileID, range, true);
    }
    public List<String> getMovementRange(GameModel model, String startTileID, int range, boolean respectfully) {
        Map<String, String> graph = createGraph(model, startTileID, range, respectfully);
        Set<String> result = new LinkedHashSet<>(graph.keySet());
        return new ArrayList<>(result);
    }


    /**
     * ███╗   ███╗ ██████╗ ██╗   ██╗███████╗███╗   ███╗███████╗███╗   ██╗████████╗
     * ████╗ ████║██╔═══██╗██║   ██║██╔════╝████╗ ████║██╔════╝████╗  ██║╚══██╔══╝
     * ██╔████╔██║██║   ██║██║   ██║█████╗  ██╔████╔██║█████╗  ██╔██╗ ██║   ██║
     * ██║╚██╔╝██║██║   ██║╚██╗ ██╔╝██╔══╝  ██║╚██╔╝██║██╔══╝  ██║╚██╗██║   ██║
     * ██║ ╚═╝ ██║╚██████╔╝ ╚████╔╝ ███████╗██║ ╚═╝ ██║███████╗██║ ╚████║   ██║
     * ╚═╝     ╚═╝ ╚═════╝   ╚═══╝  ╚══════╝╚═╝     ╚═╝╚══════╝╚═╝  ╚═══╝   ╚═╝
     *
     *  ██████╗ ██████╗  █████╗ ██████╗ ██╗  ██╗
     * ██╔════╝ ██╔══██╗██╔══██╗██╔══██╗██║  ██║
     * ██║  ███╗██████╔╝███████║██████╔╝███████║
     * ██║   ██║██╔══██╗██╔══██║██╔═══╝ ██╔══██║
     * ╚██████╔╝██║  ██║██║  ██║██║     ██║  ██║
     *  ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝     ╚═╝  ╚═╝
     */

    protected Map<String, String> createGraph(GameModel model, String startTileID, int range, boolean respectfully) {
        Map<String, Integer> depthMap = new HashMap<>();
        depthMap.put(startTileID, 0);

        Map<String, String> graphMap = new HashMap<>();
        graphMap.put(startTileID, null);

        Queue<String> tileIDsToVisit = new LinkedList<>();
        tileIDsToVisit.add(startTileID);

        Set<String> visitedTileIDs = new HashSet<>();

        while (!tileIDsToVisit.isEmpty()) {
            // get the tile and its depth
            String currentTileID = tileIDsToVisit.poll();
            if (currentTileID == null) { continue; }

            Entity currentTileEntity = getEntityWithID(currentTileID);
            TileComponent currentTile = currentTileEntity.get(TileComponent.class);
            int depth = depthMap.get(currentTileID);

            // check that we have not visited already and is within range
            if (visitedTileIDs.contains(currentTileID)) { continue; }
            visitedTileIDs.add(currentTileID);

            // If building graph for movement, don't traverse over obstructed tiles
            if (currentTileID != startTileID && (respectfully && currentTile.isNotNavigable())) { continue; }

            // only go the specified range
            if (range >= 0 && depth >= range) { continue; }

            // go through each child tile and set connection
            for (Direction direction : Direction.cardinal) {
                int row = currentTile.getRow() + direction.y;
                int column = currentTile.getColumn() + direction.x;
                String adjacentTileID = model.tryFetchingTileEntityID(row, column);
                Entity adjacentTileEntity = getEntityWithID(adjacentTileID);

                // skip tiles off the map or being occupied or already visited
                if (adjacentTileEntity == null) { continue; }
                if (visitedTileIDs.contains(adjacentTileID)) { continue; }

                // ensure the tile isn't obstructed and within jump or move
                TileComponent adjacentTile = adjacentTileEntity.get(TileComponent.class);

                // If building graph for movement, don't traverse over obstructed tiles
                if (respectfully && adjacentTile.isNotNavigable()) { continue; }

                tileIDsToVisit.add(adjacentTileID);
                graphMap.put(adjacentTileID, currentTileID);
                depthMap.put(adjacentTileID, depth + 1);
            }
        }

        return graphMap;
    }
//    protected Map<Entity, Entity> createGraph(GameModel model, Entity start, int range, boolean respectfully) {
//        Map<Entity, Integer> depthMap = new LinkedHashMap<>();
//        depthMap.put(start, 0);
//
//        Map<Entity, Entity> graphMap = new LinkedHashMap<>();
//        graphMap.put(start, null);
//
//        Queue<Entity> tilesToVisit = new LinkedList<>();
//        tilesToVisit.add(start);
//
//        Set<Entity> visitedTiles = new HashSet<>();
//
//        while (!tilesToVisit.isEmpty()) {
//            // get the tile and its depth
//            Entity currentTileEntity = tilesToVisit.poll();
//            if (currentTileEntity == null) { continue; }
//
//            Tile currentTile = currentTileEntity.get(Tile.class);
//            int depth = depthMap.get(currentTileEntity);
//
//            // check that we have not visited already and is within range
//            if (visitedTiles.contains(currentTileEntity)) { continue; }
//            visitedTiles.add(currentTileEntity);
//
//            // If building graph for movement, don't traverse over obstructed tiles
//            if (currentTileEntity != start && (respectfully && currentTile.isNotNavigable())) { continue; }
//
//            // only go the specified range
//            if (range >= 0 && depth >= range) { continue; }
//
//            // go through each child tile and set connection
//            for (Direction direction : Direction.cardinal) {
//                int row = currentTile.getRow() + direction.y;
//                int column = currentTile.getColumn() + direction.x;
//                Entity cardinallyAdjacentTileEntity = model.tryFetchingEntityAt(row, column);
//
//                // skip tiles off the map or being occupied or already visited
//                if (cardinallyAdjacentTileEntity == null) { continue; }
//                if (visitedTiles.contains(cardinallyAdjacentTileEntity)) { continue; }
//
//                // ensure the tile isn't obstructed and within jump or move
//                Tile adjacentTile = cardinallyAdjacentTileEntity.get(Tile.class);
//
//                // If building graph for movement, don't traverse over obstructed tiles
//                if (respectfully && adjacentTile.isNotNavigable()) { continue; }
//
//                tilesToVisit.add(cardinallyAdjacentTileEntity);
//                graphMap.put(cardinallyAdjacentTileEntity, currentTileEntity);
//                depthMap.put(cardinallyAdjacentTileEntity, depth + 1);
//            }
//        }
//
//        return graphMap;
//    }


    public Entity getEntityWithID(String entityID) { return EntityStore.getInstance().get(entityID); }
}
