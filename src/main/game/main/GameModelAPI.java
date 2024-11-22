package main.game.main;

import org.json.JSONArray;
import main.constants.Direction;
import main.constants.Vector3f;
import main.game.camera.Camera;
import main.game.components.tile.Tile;
import main.game.map.base.TileMap;
import org.json.JSONObject;

import java.util.*;

public class GameModelAPI {
//    public boolean updateSelectedTiles(GameState gameState, TileMap tileMap, JSONArray request) {
//        JSONArray selectedTiles = new JSONArray();
//
//        for (Object object : request) {
//            if (!(object instanceof JSONObject JSONObject)) { continue; }
//            JSONObject valid = JsonUtils.validate(JSONObject, new String[]{ Tile.ROW, Tile.COLUMN });
//            if (valid == null) { continue; }
//            int row = valid.getInt(Tile.ROW);
//            int column = valid.getInt(Tile.COLUMN);
//            Tile tile = tileMap.tryFetchingTileAt(row, column);
//            if (tile == null) { continue; }
//            selectedTiles.put(tile);
//        }
//        return gameState.updateSelectedTiles(selectedTiles);
//    }


    public boolean updateSelectedTiles(GameState gameState, TileMap tileMap, JSONArray request) {
        JSONArray selectedTiles = new JSONArray();

        for (Object object : request) {
            if (!(object instanceof JSONObject JSONObject)) { continue; }
            JSONObject valid = JsonUtils.validate(JSONObject, new String[]{ Tile.ROW, Tile.COLUMN });
            if (valid == null) { continue; }
            int row = valid.getInt(Tile.ROW);
            int column = valid.getInt(Tile.COLUMN);
            Tile tile = tileMap.tryFetchingTileAt(row, column);
            if (tile == null) { continue; }
            selectedTiles.put(tile);
        }
        return gameState.updateSelectedTiles(selectedTiles);
    }


    public List<Tile> getSelectedTiles(GameState gameState, TileMap tileMap) {
        JSONArray selectedTiles = gameState.getSelectedTiles();
        List<Tile> actuallySelectedTiles = new ArrayList<>();
        for (int index = 0; index < selectedTiles.length(); index++) {
            Object object = selectedTiles.get(index);
            JSONObject jsonObject = (JSONObject) object;
            int row = jsonObject.getInt(Tile.ROW);
            int column = jsonObject.getInt(Tile.COLUMN);
            Tile tile = tileMap.tryFetchingTileAt(row, column);
            actuallySelectedTiles.add(tile);
        }

        return actuallySelectedTiles;
    }

    public static final String UPDATE_STRUCTURE_MODE = "set_structure_operation";
    public static final String UPDATE_STRUCTURE_ADD_MODE = "add_structure";
    public static final String UPDATE_STRUCTURE_DELETE_MODE = "delete_structure";
    public static final String UPDATE_STRUCTURE_ASSET = "structure_asset";
    public static final String UPDATE_STRUCTURE_HEALTH = "update_structure_health";
    public void updateStructures(GameState gameState, TileMap tileMap, JSONObject request) {
        try {
            String asset = (String) request.get(UPDATE_STRUCTURE_ASSET);
            String mode = (String) request.get(UPDATE_STRUCTURE_MODE);
            int health = request.getInt(UPDATE_STRUCTURE_HEALTH);
            List<Tile> tiles = getSelectedTiles(gameState, tileMap);
            tiles.forEach(tile -> {
                if (mode.equalsIgnoreCase(UPDATE_STRUCTURE_ADD_MODE)) {
                    tile.addStructure(asset, health);
                } else if (mode.equalsIgnoreCase(UPDATE_STRUCTURE_DELETE_MODE)) {
                    tile.deleteStructure();
                }
            });
        } catch (Exception ex) {
            System.err.print("Unable to update structures: " + ex);
        }
    }

    public static final String GET_TILES_AT_RADIUS = "radius";
    public static final String GET_TILES_AT_ROW = "row";
    public static final String GET_TILES_AT_COLUMN = "column";
    public JSONArray getTilesAtRowColumn(TileMap tileMap, JSONObject request) {
        JSONObject valid = JsonUtils.validate(request, new String[]{
                GET_TILES_AT_ROW,
                GET_TILES_AT_COLUMN
        });
        if (valid == null) { return null; }
        int radius = (int) valid.optInt(GET_TILES_AT_RADIUS, 0);
        int row = (int) valid.get(GET_TILES_AT_ROW);
        int column = (int) valid.get(GET_TILES_AT_COLUMN);

        Tile tile = tileMap.tryFetchingTileAt(row, column);
        if (tile == null) { return null; }

        // Get tiles for the specified radius
        JSONArray tiles = new JSONArray();
        for (row = tile.getRow() - radius; row <= tile.getRow() + radius; row++) {
            for (column = tile.getColumn() - radius; column <= tile.getColumn() + radius; column++) {
                Tile adjacentTile = tileMap.tryFetchingTileAt(row, column);
                if (adjacentTile == null) { continue; }
                tiles.put(adjacentTile);
            }
        }

        return tiles;
    }

    public static final String GET_TILES_AT_X = "x";
    public static final String GET_TILES_AT_Y = "y";
    public JSONArray getTilesAtXY(GameConfigurations gameConfigurations, TileMap tileMap, Camera camera, JSONObject request) {
        JSONObject valid = JsonUtils.validate(request, new String[]{GET_TILES_AT_X, GET_TILES_AT_Y});
        if (valid == null) { return null; }
        JSONArray response = new JSONArray();

        try {
            int radius = valid.optInt(GET_TILES_AT_RADIUS, 0);
            int x = (int) valid.get(GET_TILES_AT_X);
            int y = (int) valid.get(GET_TILES_AT_Y);
            Vector3f globalCameraPosition = camera.getPosition();
            int spriteWidth = gameConfigurations.getSpriteWidth();
            int spriteHeight = gameConfigurations.getSpriteHeight();
            int column = (int) ((x + globalCameraPosition.x) / spriteWidth);
            int row = (int) ((y + globalCameraPosition.y) / spriteHeight);

            Tile tile = tileMap.tryFetchingTileAt(row, column);
            if (tile == null) { return null; }

            // Get tiles for the specified radius
            for (row = tile.getRow() - radius; row <= tile.getRow() + radius; row++) {
                for (column = tile.getColumn() - radius; column <= tile.getColumn() + radius; column++) {
                    Tile adjacentTile = tileMap.tryFetchingTileAt(row, column);
                    if (adjacentTile == null) { continue; }
                    response.put(adjacentTile);
                }
            }
        } catch (Exception ex) {
            response.clear();
        }

        return response;
    }




    public static final String UPDATE_SPAWN_MODE = "spawn.mode";
    public static final String UPDATE_SPAWNER_OPERATION_ADD = "add.spawn";
    public static final String UPDATE_SPAWNER_OPERATION_DELETE = "delete.spawn";
    public static final String UPDATE_SPAWN_OPERATION_ON_TEAM = "spawn.team";
    public void updateSpawners(GameState gameState, TileMap tileMap, JSONObject request) {
        JSONObject valid = JsonUtils.validate(request, new String[]{
                UPDATE_SPAWN_MODE,
                UPDATE_SPAWN_OPERATION_ON_TEAM
        });
        if (valid == null) { return; }

        try {
            List<Tile> selectedTiles = getSelectedTiles(gameState, tileMap);
            String spawner = (String) valid.get(UPDATE_SPAWN_OPERATION_ON_TEAM);
            String operation = (String) valid.get(UPDATE_SPAWN_MODE);
            selectedTiles.forEach(tile -> {
                if (operation.equalsIgnoreCase(UPDATE_SPAWNER_OPERATION_ADD)) {
                    tile.addSpawner(spawner);
                } else if (operation.equalsIgnoreCase(UPDATE_SPAWNER_OPERATION_DELETE)) {
                    tile.deleteSpawner(spawner);
                }
            });
        } catch (Exception ex) {

        }
    }


    // Eventually feed the selected tiles through the API
    public static final String UPDATE_TILE_LAYERS_MODE = "layer.operation";
    public static final String UPDATE_TILE_LAYERS_OPERATION_ADD_LAYER = "add.layer"; // Adds a layer to the tile
    public static final String UPDATE_TILE_LAYERS_OPERATION_DELETE_LAYER = "delete.layer"; // Deletes the top layer to the tile
    public static final String UPDATE_TILE_LAYERS_OPERATION_EXTEND_LAYER = "extend.layer"; // Lengthens the top layer of the tile
    public static final String UPDATE_TILE_LAYERS_OPERATION_SHORTEN_LAYER = "shorten.layer"; // Shrinks the top layer of the tile
    public static final String UPDATE_TILE_LAYERS_OPERATION_FILL_TO_LAYER = "fill.to.level";
    public static final String UPDATE_TILE_LAYERS_OPERATION_STRIP_TERRAIN = "strip.terrain";
    public void updateTileLayers(GameState gameState, TileMap tileMap, JSONObject request) {
//        JSONObject valid = JsonUtils.validate(request, new String[]{
//                UPDATE_TILE_LAYERS_MODE,
//                Tile.LAYER_TYPE,
//                Tile.LAYER_HEIGHT,
//                Tile.LAYER_ASSET
//        });
//        if (valid == null) { return; }
//

        try {
            List<Tile> selectedTiles = getSelectedTiles(gameState, tileMap);
            String manipulation = request.getString(UPDATE_TILE_LAYERS_MODE);
            String type = request.getString(Tile.LAYER_TYPE);
            int amount = request.getInt(Tile.LAYER_HEIGHT);
            String asset = (String) request.get(Tile.LAYER_ASSET);

            selectedTiles.forEach(tile -> {
                switch (manipulation) {
                    case UPDATE_TILE_LAYERS_OPERATION_ADD_LAYER -> tile.addLayer(type, amount, asset);
                    case UPDATE_TILE_LAYERS_OPERATION_EXTEND_LAYER -> tile.addLayer(type, amount);
                    case UPDATE_TILE_LAYERS_OPERATION_DELETE_LAYER -> tile.removeLayer();
                    case UPDATE_TILE_LAYERS_OPERATION_SHORTEN_LAYER -> tile.removeLayer(amount);
                    case UPDATE_TILE_LAYERS_OPERATION_FILL_TO_LAYER -> tileFillToLevel(tileMap, selectedTiles, type, asset);
                    case UPDATE_TILE_LAYERS_OPERATION_STRIP_TERRAIN -> { stripTerrain(tileMap, selectedTiles, type, asset); }
                    default -> { }
                }
            });
        } catch (Exception ex) {

        }
    }


    // TODO
    private static void stripTerrain(TileMap tileMap, List<Tile> selectedTiles, String type, String asset) {
        if (selectedTiles.isEmpty()) { return; }
        // Get starting tile
        Tile tile = selectedTiles.get(0);
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

    private static void tileFillToLevel(TileMap tileMap, List<Tile> selectedTiles, String type, String asset) {
        if (selectedTiles.isEmpty()) { return; }
        Tile tile = selectedTiles.get(0);
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

//    public JSONObject getMap(GameState gameState, TileMap tileMap) {
//
//    }
}
