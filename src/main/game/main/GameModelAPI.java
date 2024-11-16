package main.game.main;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import main.constants.Direction;
import main.constants.Vector3f;
import main.game.camera.Camera;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.map.base.TileMap;

import java.util.*;

public class GameModelAPI {
    public boolean setSelectedTiles(GameState gameState, TileMap tileMap, JsonArray request) {
        JsonArray selectedTiles = new JsonArray();

        for (Object object : request) {
            if (!(object instanceof JsonObject jsonObject)) { continue; }
            JsonObject valid = JsonUtils.validate(jsonObject, new String[]{ Tile.ROW, Tile.COLUMN });
            if (valid == null) { continue; }
            int row = (int) valid.get(Tile.ROW);
            int column = (int) valid.get(Tile.COLUMN);
            Tile tile = tileMap.tryFetchingTileAt(row, column);
            if (tile == null) { continue; }
            selectedTiles.add(tile);
        }
        return gameState.setSelectedTiles(selectedTiles);
    }

    public Entity getSelectedTile(GameState gameState, TileMap tileMap) {
        return gameState.getSelectedTile(tileMap);
    }

    public List<Entity> getSelectedTiles(GameState gameState, TileMap tileMap) {
        return gameState.getSelectedTiles(tileMap);
    }

    public void setUpdateTileLayersOperation(GameState gameState, TileMap tileMap, JsonObject updatedAttributes) {
        List<Entity> selectedTiles = getSelectedTiles(gameState, tileMap);
        selectedTiles.forEach(e -> {
            Tile tile = e.get(Tile.class);
            tile.putAll(updatedAttributes);
        });
    }

//    public static final String GET_TILE_OPERATION = "tile_at_operation";
//    public static final String GET_TILE_OPERATION_ROW_AND_COLUMN = "row_and_column";
//    public static final String GET_TILE_OPERATION_X_AND_Y = "x_and_y";
//    public static final String GET_TILE_OPERATION_ROW_OR_Y = "rowOrY";
//    public static final String GET_TILE_OPERATION_COLUMN_OR_X = "columnOrX";
//    public JsonObject getTileAt(GameSettings gameSettings, TileMap tileMap, Camera camera, JsonObject request) {
//        JsonObject valid = JsonUtils.validate(request, new String[]{
//                GET_TILE_OPERATION_ROW_OR_Y,
//                GET_TILE_OPERATION_COLUMN_OR_X
//        });
//        if (valid == null) { return null; }
//        int rowOrY = (int) valid.get(GET_TILE_OPERATION_ROW_OR_Y);
//        int columnOrX = (int) valid.get(GET_TILE_OPERATION_COLUMN_OR_X);
//        String operation = (String) valid.getOrDefault(GET_TILE_OPERATION, GET_TILE_OPERATION_ROW_AND_COLUMN);
//        int row = rowOrY;
//        int column = columnOrX;
//        if (operation.equalsIgnoreCase(GET_TILE_OPERATION_X_AND_Y)) {
//            Vector3f globalCameraPosition = camera.getPosition();
//            int spriteWidth = gameSettings.getSpriteWidth();
//            int spriteHeight = gameSettings.getSpriteHeight();
//            column = (int) ((columnOrX + globalCameraPosition.x) / spriteWidth);
//            row = (int) ((rowOrY + globalCameraPosition.y) / spriteHeight);
//        }
//
//        Tile tile = tileMap.tryFetchingTileAt(row, column);
//        return tile;
//    }

    public static final String SET_OBSTRUCTION_OPERATION = "set_obstruction_operation";
    public static final String UPDATE_STRUCTURE_OPERATION_ADD = "add_obstruct";
    public static final String UPDATE_STRUCTURE_OPERATION_DELETE = "delete_obstruct";
    public void updateStructures(GameSettings gameSettings, TileMap tileMap, JsonObject request) {
        JsonObject valid = JsonUtils.validate(request, new String[]{
                SET_OBSTRUCTION_OPERATION,
                GET_TILE_OPERATION_ROW_OR_Y,
                GET_TILE_OPERATION_COLUMN_OR_X
        });
        if (valid == null) { return; }

        int rowOrY = (int) valid.get(GET_TILE_OPERATION_ROW_OR_Y);
        int columnOrX = (int) valid.get(GET_TILE_OPERATION_COLUMN_OR_X);

    }

    public static final String GET_TILE_OPERATION = "tile_at_operation";
    public static final String GET_TILE_OPERATION_ROW_AND_COLUMN = "row_and_column";
    public static final String GET_TILE_OPERATION_X_AND_Y = "x_and_y";
    public static final String GET_TILE_OPERATION_RADIUS = "radius";
    public static final String GET_TILE_OPERATION_ROW_OR_Y = "rowOrY";
    public static final String GET_TILE_OPERATION_COLUMN_OR_X = "columnOrX";
    public JsonArray getTilesAt(GameSettings gameSettings, TileMap tileMap, Camera camera, JsonObject request) {
        JsonObject valid = JsonUtils.validate(request, new String[]{
                GET_TILE_OPERATION_ROW_OR_Y,
                GET_TILE_OPERATION_COLUMN_OR_X
        });
        if (valid == null) { return null; }

        int rowOrY = (int) valid.get(GET_TILE_OPERATION_ROW_OR_Y);
        int columnOrX = (int) valid.get(GET_TILE_OPERATION_COLUMN_OR_X);
        String operation = (String) valid.getOrDefault(GET_TILE_OPERATION, GET_TILE_OPERATION_ROW_AND_COLUMN);
        int radius = (int) valid.getOrDefault(GET_TILE_OPERATION_RADIUS, 0);
        int row = rowOrY;
        int column = columnOrX;

        // If the user asks for X and Y, get the tile at those respective coordinates
        if (operation.equalsIgnoreCase(GET_TILE_OPERATION_X_AND_Y)) {
            Vector3f globalCameraPosition = camera.getPosition();
            int spriteWidth = gameSettings.getSpriteWidth();
            int spriteHeight = gameSettings.getSpriteHeight();
            column = (int) ((columnOrX + globalCameraPosition.x) / spriteWidth);
            row = (int) ((rowOrY + globalCameraPosition.y) / spriteHeight);
        }

        Tile tile = tileMap.tryFetchingTileAt(row, column);
        if (tile == null) { return null; }

        JsonArray tiles = new JsonArray();

        // Get tiles for the specified radius
        for (row = tile.getRow() - radius; row <= tile.getRow() + radius; row++) {
            for (column = tile.getColumn() - radius; column <= tile.getColumn() + radius; column++) {
                Tile adjacentTile = tileMap.tryFetchingTileAt(row, column);
                if (adjacentTile == null) { continue; }
                tiles.add(adjacentTile);
            }
        }

        return tiles;
    }


    public static final String UPDATE_SPAWN_OPERATION = "spawn.operation";
    public static final String UPDATE_SPAWNER_OPERATION_ADD = "add.spawn";
    public static final String UPDATE_SPAWNER_OPERATION_DELETE = "delete.spawn";
    public static final String UPDATE_SPAWN_OPERATION_ON_TEAM = "spawn.team";
    public void updateSpawners(GameState gameState, TileMap tileMap, JsonObject request) {
        JsonObject valid = JsonUtils.validate(request, new String[]{
                UPDATE_SPAWN_OPERATION,
                UPDATE_SPAWN_OPERATION_ON_TEAM
        });
        if (valid == null) { return; }

        List<Entity> selectedTiles = getSelectedTiles(gameState, tileMap);
        String spawner = (String) valid.get(UPDATE_SPAWN_OPERATION_ON_TEAM);
        String operation = (String) valid.get(UPDATE_SPAWN_OPERATION);
        selectedTiles.forEach(e -> {
            Tile tile = e.get(Tile.class);
            if (operation.equalsIgnoreCase(UPDATE_SPAWNER_OPERATION_ADD)) {
                tile.addSpawner(spawner);
            } else if (operation.equalsIgnoreCase(UPDATE_SPAWNER_OPERATION_DELETE)) {
                tile.deleteSpawner(spawner);
            }
        });
    }


    // Eventually feed the selected tiles through the API
    public static final String UPDATE_TILE_LAYERS_OPERATION = "layer.operation";
    public static final String UPDATE_TILE_LAYERS_OPERATION_ADD_LAYER = "add.layer"; // Adds a layer to the tile
    public static final String UPDATE_TILE_LAYERS_OPERATION_DELETE_LAYER = "delete.layer"; // Deletes the top layer to the tile
    public static final String UPDATE_TILE_LAYERS_OPERATION_EXTEND_LAYER = "extend.layer"; // Lengthens the top layer of the tile
    public static final String UPDATE_TILE_LAYERS_OPERATION_SHORTEN_LAYER = "shorten.layer"; // Shrinks the top layer of the tile
    public static final String UPDATE_TILE_LAYERS_OPERATION_FILL_TO_LAYER = "fill.to.level";
    public void updateTileLayers(GameState gameState, TileMap tileMap, JsonObject request) {
        JsonObject valid = JsonUtils.validate(request, new String[]{
                UPDATE_TILE_LAYERS_OPERATION,
                Tile.LAYER_TYPE,
                Tile.LAYER_HEIGHT,
                Tile.LAYER_ASSET
        });
        if (valid == null) { return; }

        List<Entity> selectedTiles = getSelectedTiles(gameState, tileMap);
        String manipulation = (String) valid.get(UPDATE_TILE_LAYERS_OPERATION);
        String type = (String) valid.get(Tile.LAYER_TYPE);
        int amount = Integer.parseInt(valid.get(Tile.LAYER_HEIGHT) + "");
        String asset = (String) valid.get(Tile.LAYER_ASSET);

        selectedTiles.forEach(e -> {
            Tile tile = e.get(Tile.class);
            switch (manipulation) {
                case UPDATE_TILE_LAYERS_OPERATION_ADD_LAYER -> tile.addLayer(type, amount, asset);
                case UPDATE_TILE_LAYERS_OPERATION_EXTEND_LAYER -> tile.addLayer(type, amount);
                case UPDATE_TILE_LAYERS_OPERATION_DELETE_LAYER -> tile.removeLayer();
                case UPDATE_TILE_LAYERS_OPERATION_SHORTEN_LAYER -> tile.removeLayer(amount);
                case UPDATE_TILE_LAYERS_OPERATION_FILL_TO_LAYER -> tileFillToLevel(tileMap, selectedTiles, type, asset);
                default -> { }
            }
        });
    }

    private static void tileFillToLevel(TileMap tileMap, List<Entity> selectedTiles, String type, String asset) {
        Entity tileEntity = selectedTiles.get(0);
        Tile tile = tileEntity.get(Tile.class);
        int heightOfStartingTile = tile.getHeight();
        // Fill all tiles
        Queue<Tile> queue = new LinkedList<>();
        queue.add(tile);
        Set<Tile> set = new HashSet<>();
        List<Tile> tilesToUpdate = new ArrayList<>();

        while (!queue.isEmpty()) {
            Tile traversedTile = queue.poll();
            // If we've seen he current tile, skip
            if (set.contains(traversedTile)) { continue; }
            // If the current tile is HIGHER than starting tile, skip
            if (heightOfStartingTile < traversedTile.getHeight() && traversedTile != tile) { continue; }
            set.add(traversedTile);
            // Mark tile to process
            tilesToUpdate.add(traversedTile);
            // Collect more tiles
            for (Direction direction : Direction.values()) {
                int row = traversedTile.getRow() + direction.y;
                int column = traversedTile.getColumn() + direction.x;
                Tile adjacentNeighborTile = tileMap.tryFetchingTileAt(row, column);
                if (adjacentNeighborTile == null) { continue; }
                queue.add(adjacentNeighborTile);
            }
        }
        heightOfStartingTile += 1;
        for (Tile tileToUpdate : tilesToUpdate) {
            int heightOfTileToUpdate = tileToUpdate.getHeight();
            int heightDelta = heightOfStartingTile - heightOfTileToUpdate;
            if (heightDelta <= 0) { continue; }
            tileToUpdate.addLayer(type, heightDelta, asset);
        }
    }

//    public JsonObject getMap(GameState gameState, TileMap tileMap) {
//
//    }
}
