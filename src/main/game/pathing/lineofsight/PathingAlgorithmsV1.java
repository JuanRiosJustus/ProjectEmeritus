package main.game.pathing.lineofsight;

import main.constants.Direction;
import main.game.components.IdentityComponent;
import main.game.components.TileComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.EntityStore;

import java.util.*;

public class PathingAlgorithmsV1 {

    /**
     * ██╗     ██╗███╗   ██╗███████╗     ██████╗ ███████╗    ███████╗██╗ ██████╗ ██╗  ██╗████████╗
     * ██║     ██║████╗  ██║██╔════╝    ██╔═══██╗██╔════╝    ██╔════╝██║██╔════╝ ██║  ██║╚══██╔══╝
     * ██║     ██║██╔██╗ ██║█████╗      ██║   ██║█████╗      ███████╗██║██║  ███╗███████║   ██║
     * ██║     ██║██║╚██╗██║██╔══╝      ██║   ██║██╔══╝      ╚════██║██║██║   ██║██╔══██║   ██║
     * ███████╗██║██║ ╚████║███████╗    ╚██████╔╝██║         ███████║██║╚██████╔╝██║  ██║   ██║
     * ╚══════╝╚═╝╚═╝  ╚═══╝╚══════╝     ╚═════╝ ╚═╝         ╚══════╝╚═╝ ╚═════╝ ╚═╝  ╚═╝   ╚═╝
     */

    public List<String> computeLineOfSightV2(GameModel model, Entity start, Entity end) {
        return computeLineOfSightV2(model, start, end, true);
    }
    /**
     * Computes the Manhattan line of sight between two entities.
     *
     * @param model        The game model containing the grid and entities.
     * @param start        The starting entity.
     * @param end          The target entity.
     * @param respectfully Whether to respect obstacles (e.g., walls).
     * @return A set of entities forming the line of sight.
     */
    public List<String> computeLineOfSightV2(GameModel model, Entity start, Entity end, boolean respectfully) {
        LinkedHashSet<Entity> line = new LinkedHashSet<>();

        // Validate input entities
        if (start == null || end == null) { return new ArrayList<>(); }

        // Retrieve the starting and ending tiles
        TileComponent startTile = start.get(TileComponent.class);
        TileComponent endTile = end.get(TileComponent.class);

        // Ensure both tiles are valid
        if (startTile == null || endTile == null) { return new ArrayList<>(); }

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

        List<String> tilesIDs = line.stream().map(e -> {
            IdentityComponent identityComponent = e.get(IdentityComponent.class);
            return identityComponent.getID();
        }).toList();
        return tilesIDs;
    }

    public LinkedHashSet<Entity> computeLineOfSight(GameModel model, Entity start, Entity end) {
        return computeLineOfSight(model, start, end, true);
    }
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
                if (tile == null) { continue; }

                // Add the current tile to the line of sight
                line.add(entity);

                // If respectfully is true and the tile is not navigable, stop the traversal
                if (respectfully && tile.isNotNavigable()) { break; }
            }
        }

        return line;
    }


    /**
     * Computes the Manhattan line of sight between two entities.
     *
     * @param model        The game model containing the grid and entities.
     * @param start        The starting entity.
     * @param end          The target entity.
     * @param respectfully Whether to respect obstacles (e.g., walls).
     * @return A set of entities forming the line of sight.
     */
    public Set<String> computeLineOfSightV2(GameModel model, String startID, String endID, boolean respectfully) {
        Set<String> line = new LinkedHashSet<>();

        // Validate input entities
        if (startID == null || endID == null) return line;

        // Retrieve the starting and ending tiles
        Entity start = EntityStore.getInstance().get(startID);
        Entity end = EntityStore.getInstance().get(endID);
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
        line.add(startID);

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
//            Entity entity = model.tryFetchingEntityAt(currentRow, currentColumn);
            String entityID = model.tryFetchingTileEntityID(currentRow, currentColumn);
            if (entityID != null) {
                Entity entity = getEntityWithID(entityID);
                TileComponent tile = entity.get(TileComponent.class);

                // Ensure the tile exists
                if (tile == null) { continue; }

                // Add the current tile to the line of sight
                line.add(entityID);

                // If respectfully is true and the tile is not navigable, stop the traversal
                if (respectfully && tile.isNotNavigable()) { break; }
            }
        }

        return line;
    }

    /**
     *  █████╗ ██████╗ ███████╗ █████╗      ██████╗ ███████╗    ███████╗██╗ ██████╗ ██╗  ██╗████████╗
     * ██╔══██╗██╔══██╗██╔════╝██╔══██╗    ██╔═══██╗██╔════╝    ██╔════╝██║██╔════╝ ██║  ██║╚══██╔══╝
     * ███████║██████╔╝█████╗  ███████║    ██║   ██║█████╗      ███████╗██║██║  ███╗███████║   ██║
     * ██╔══██║██╔══██╗██╔══╝  ██╔══██║    ██║   ██║██╔══╝      ╚════██║██║██║   ██║██╔══██║   ██║
     * ██║  ██║██║  ██║███████╗██║  ██║    ╚██████╔╝██║         ███████║██║╚██████╔╝██║  ██║   ██║
     * ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝     ╚═════╝ ╚═╝         ╚══════╝╚═╝ ╚═════╝ ╚═╝  ╚═╝   ╚═╝
     */
    public LinkedHashSet<Entity> computeAreaOfSight(GameModel model, Entity start, int range) {
        return computeAreaOfSight(model, start, range, true);
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


    public List<String> computeAreaOfSightV2(GameModel model, Entity start, int range) {
        return computeAreaOfSightV2(model, start, range, true);
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
    public List<String> computeAreaOfSightV2(GameModel model, Entity start, int range, boolean respectfully) {
        LinkedHashSet<Entity> visibleTiles = new LinkedHashSet<>();

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
                Entity target = model.tryFetchingEntityAt(targetRow, targetColumn);
                if (target == null) continue;

                // Compute the line of sight to the target entity
                LinkedHashSet<Entity> line = computeLineOfSight(model, start, target, respectfully);

                // Add all tiles from the computed line to the visible set
                visibleTiles.addAll(line);
            }
        }

        List<String> visibleTileIDs = visibleTiles.stream().map(e -> {
            IdentityComponent identityComponent = e.get(IdentityComponent.class);
            return identityComponent.getID();
        }).toList();
        return visibleTileIDs;
    }

    public List<String> computeAreaOfSightV3(GameModel model, String startTileID, int range) {
        return computeAreaOfSightV3(model, startTileID, range, true);
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
    public List<String> computeAreaOfSightV3(GameModel model, String startID, int range, boolean respectfully) {
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
                Set<String> line = computeLineOfSightV2(model, startID, targetID, respectfully);

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
    public LinkedHashSet<Entity> computeMovementPath(GameModel model, Entity start, Entity end) {
        return computeMovementPath(model, start, end, true);
    }
    public LinkedHashSet<Entity> computeMovementPath(GameModel model, Entity start, Entity end, boolean respectfully) {
        Map<Entity, Entity> map = createGraph(model, start, -1, respectfully);

        LinkedHashSet<Entity> result = new LinkedHashSet<>();
        if (!map.containsKey(start)) { return result; }
        if (!map.containsKey(end)) { return result; }

        LinkedList<Entity> queue = new LinkedList<>();
        Entity current = end;
        while (current != null) {
            queue.addFirst(current);
            current = map.get(current);
        }

        result.addAll(queue);

        return result;
    }

    public List<String> computeMovementPathV2(GameModel model, Entity start, Entity end) {
        return computeMovementPathV2(model, start, end, true);
    }
    public List<String> computeMovementPathV2(GameModel model, Entity start, Entity end, boolean respectfully) {
        Map<Entity, Entity> map = createGraph(model, start, -1, respectfully);

        if (!map.containsKey(start)) { return new ArrayList<>(); }
        if (!map.containsKey(end)) { return new ArrayList<>(); }

        LinkedList<Entity> queue = new LinkedList<>();
        Entity current = end;
        while (current != null) {
            queue.addFirst(current);
            current = map.get(current);
        }

        List<String> movementPathIDs = queue.stream().map(e -> {
            IdentityComponent identityComponent = e.get(IdentityComponent.class);
            return identityComponent.getID();
        }).toList();

        return movementPathIDs;
    }

    /**
     * ███╗   ███╗ ██████╗ ██╗   ██╗███████╗███╗   ███╗███████╗███╗   ██╗████████╗     █████╗ ██████╗ ███████╗ █████╗
     * ████╗ ████║██╔═══██╗██║   ██║██╔════╝████╗ ████║██╔════╝████╗  ██║╚══██╔══╝    ██╔══██╗██╔══██╗██╔════╝██╔══██╗
     * ██╔████╔██║██║   ██║██║   ██║█████╗  ██╔████╔██║█████╗  ██╔██╗ ██║   ██║       ███████║██████╔╝█████╗  ███████║
     * ██║╚██╔╝██║██║   ██║╚██╗ ██╔╝██╔══╝  ██║╚██╔╝██║██╔══╝  ██║╚██╗██║   ██║       ██╔══██║██╔══██╗██╔══╝  ██╔══██║
     * ██║ ╚═╝ ██║╚██████╔╝ ╚████╔╝ ███████╗██║ ╚═╝ ██║███████╗██║ ╚████║   ██║       ██║  ██║██║  ██║███████╗██║  ██║
     * ╚═╝     ╚═╝ ╚═════╝   ╚═══╝  ╚══════╝╚═╝     ╚═╝╚══════╝╚═╝  ╚═══╝   ╚═╝       ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝
     */


    public LinkedHashSet<Entity> computeMovementArea(GameModel model, Entity start, int range) {
        return computeMovementArea(model, start, range, true);
    }
    public LinkedHashSet<Entity> computeMovementArea(GameModel model, Entity start, int range, boolean respectfully) {
        Map<Entity, Entity> map = createGraph(model, start, range, respectfully);
        LinkedHashSet<Entity> result = new LinkedHashSet<>(map.keySet());
        return result;
    }

    public List<String> computeMovementAreaV2(GameModel model, Entity start, int range) {
        return computeMovementAreaV2(model, start, range, true);
    }
    public List<String> computeMovementAreaV2(GameModel model, Entity start, int range, boolean respectfully) {
        Map<Entity, Entity> map = createGraph(model, start, range, respectfully);
        LinkedHashSet<Entity> result = new LinkedHashSet<>(map.keySet());
        List<String> movementArea = result.stream().map(e -> {
            IdentityComponent identityComponent = e.get(IdentityComponent.class);
            return identityComponent.getID();
        }).toList();
        return movementArea;
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
    protected Map<Entity, Entity> createGraph(GameModel model, Entity start, int range, boolean respectfully) {
        Map<Entity, Integer> depthMap = new LinkedHashMap<>();
        depthMap.put(start, 0);

        Map<Entity, Entity> graphMap = new LinkedHashMap<>();
        graphMap.put(start, null);

        Queue<Entity> tilesToVisit = new LinkedList<>();
        tilesToVisit.add(start);

        Set<Entity> visitedTiles = new HashSet<>();

        while (!tilesToVisit.isEmpty()) {
            // get the tile and its depth
            Entity currentTileEntity = tilesToVisit.poll();
            if (currentTileEntity == null) { continue; }

            TileComponent currentTile = currentTileEntity.get(TileComponent.class);
            int depth = depthMap.get(currentTileEntity);

            // check that we have not visited already and is within range
            if (visitedTiles.contains(currentTileEntity)) { continue; }
            visitedTiles.add(currentTileEntity);

            // If building graph for movement, don't traverse over obstructed tiles
            if (currentTileEntity != start && (respectfully && currentTile.isNotNavigable())) { continue; }

            // only go the specified range
            if (range >= 0 && depth >= range) { continue; }

            // go through each child tile and set connection
            for (Direction direction : Direction.cardinal) {
                int row = currentTile.getRow() + direction.y;
                int column = currentTile.getColumn() + direction.x;
                Entity cardinallyAdjacentTileEntity = model.tryFetchingEntityAt(row, column);

                // skip tiles off the map or being occupied or already visited
                if (cardinallyAdjacentTileEntity == null) { continue; }
                if (visitedTiles.contains(cardinallyAdjacentTileEntity)) { continue; }

                // ensure the tile isn't obstructed and within jump or move
                TileComponent adjacentTile = cardinallyAdjacentTileEntity.get(TileComponent.class);

                // If building graph for movement, don't traverse over obstructed tiles
                if (respectfully && adjacentTile.isNotNavigable()) { continue; }

                tilesToVisit.add(cardinallyAdjacentTileEntity);
                graphMap.put(cardinallyAdjacentTileEntity, currentTileEntity);
                depthMap.put(cardinallyAdjacentTileEntity, depth + 1);
            }
        }

        return graphMap;
    }


    public Entity getEntityWithID(String entityID) { return EntityStore.getInstance().get(entityID); }
}
