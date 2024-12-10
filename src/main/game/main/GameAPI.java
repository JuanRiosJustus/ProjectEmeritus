package main.game.main;

import main.constants.Direction;
import main.game.components.ActionComponent;
import main.game.components.IdentityComponent;
import main.game.components.MovementComponent;
import main.game.components.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.map.base.TileMap;
import main.game.queue.SpeedQueue;
import main.game.stores.factories.EntityFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class GameAPI {
    private final JSONObject mEphemeralObjectResponse = new JSONObject();
    private final JSONArray mEphemeralArrayResponse = new JSONArray();
    private final List<JSONObject> mEphemeralArrayList = new ArrayList<>();
    public static final String GET_CURRENT_UNIT_TURN_STATUS_HAS_MOVED = "HasMoved";
    public static final String GET_CURRENT_UNIT_TURN_STATUS_HAS_ACTED = "HasActed";
    private static final String ID = "id";

    public JSONObject getCurrentUnitTurnStatus(GameModel gameModel) {
        SpeedQueue speedQueue = gameModel.getSpeedQueue();
        JSONObject response = mEphemeralObjectResponse;
        response.clear();

        Entity unitEntity = speedQueue.peek();
        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);

        response.put(GET_CURRENT_UNIT_TURN_STATUS_HAS_MOVED, movementComponent.hasMoved());
        response.put(GET_CURRENT_UNIT_TURN_STATUS_HAS_ACTED, actionComponent.hasActed());
        return response;
    }

    public void setSelectedTiles(GameModel gameModel, JSONArray request) {
        GameState gameState = gameModel.getGameState();
        TileMap tileMap = gameModel.getTileMap();

        JSONArray selectedTiles = new JSONArray();

        for (Object object : request) {
            if (!(object instanceof JSONObject jsonObject)) { continue; }
            int row = jsonObject.getInt(Tile.ROW);
            int column = jsonObject.getInt(Tile.COLUMN);
            Tile tile = tileMap.tryFetchingTileAt(row, column);
            if (tile == null) { continue; }
            selectedTiles.put(tile);
        }

        gameState.setSelectedTiles(selectedTiles);
    }

    public List<JSONObject> getSelectedTiles(GameModel gameModel) {
        GameState gameState = gameModel.getGameState();
        List<JSONObject> selectedTiles = gameState.getSelectedTiles();
        List<JSONObject> results = new ArrayList<>();
        for (JSONObject jsonObject : selectedTiles) {
            Tile newTile = new Tile(jsonObject);
            results.add(newTile);
        }

        return results;
    }

//    public boolean


//    public List<JSONObject> getSelectedTiles(GameModel gameModel) {
//        GameState gameState = gameModel.getGameState();
//        List<JSONObject> selectedTiles = gameState.getSelectedTiles();
//        List<JSONObject> results = new ArrayList<>();
//        mEphemeralArrayList.clear();
//        for (JSONObject jsonObject : selectedTiles) {
//            Tile newTile = new Tile(jsonObject);
//            mEphemeralArrayList.add(newTile);
//        }
//
//        return mEphemeralArrayList;
//    }


    public List<JSONObject> getHoveredTiles(GameModel gameModel) {
        GameState gameState = gameModel.getGameState();
        List<JSONObject> hoveredTiles = gameState.getHoveredTiles();
        List<JSONObject> result = new ArrayList<>();
        for (JSONObject jsonObject : hoveredTiles) {
            Tile newTile = new Tile(jsonObject);
            result.add(newTile);
        }
        return result;
    }
//    public List<JSONObject> getHoveredTiles(GameModel gameModel) {
//        GameState gameState = gameModel.getGameState();
//        List<JSONObject> hoveredTiles = gameState.getHoveredTiles();
//        List<JSONObject> result = new ArrayList<>();
//        mEphemeralArrayList.clear();
//        for (JSONObject jsonObject : hoveredTiles) {
//            Tile newTile = new Tile(jsonObject);
//            result.add(newTile);
////            mEphemeralArrayList.add(newTile);
//        }
////        return mEphemeralArrayList;
//        return result;
//    }


    public JSONArray getSelectedUnitsActions(GameModel gameModel) {
        GameState gameState = gameModel.getGameState();
        List<JSONObject> selectedTiles = gameState.getSelectedTiles();
        mEphemeralArrayResponse.clear();

        for (JSONObject jsonObject : selectedTiles) {
            Tile tile = (Tile) jsonObject;
            Entity unitEntity = tile.getUnit();
            if (unitEntity == null) { continue; }
            StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
            for (String action : statisticsComponent.getActions()) {
                mEphemeralArrayResponse.put(action);
            }
            break;
        }

        return mEphemeralArrayResponse;
    }

    public JSONObject getTileOfCurrentUnitsTurn(GameModel gameModel) {
        SpeedQueue speedQueue = gameModel.getSpeedQueue();


        Entity currentEntityWithTurn = speedQueue.peek();
        if (currentEntityWithTurn == null) { return null; }
        MovementComponent movementComponent = currentEntityWithTurn.get(MovementComponent.class);
        Entity currentTile = movementComponent.getCurrentTile();
        Tile tile = currentTile.get(Tile.class);
        return tile;
    }

    public static final String UPDATE_STRUCTURE_MODE = "set_structure_operation";
    public static final String UPDATE_STRUCTURE_ADD_MODE = "add_structure";
    public static final String UPDATE_STRUCTURE_DELETE_MODE = "delete_structure";
    public static final String UPDATE_STRUCTURE_ASSET = "structure_asset";
    public static final String UPDATE_STRUCTURE_HEALTH = "update_structure_health";
    public void updateStructures(GameModel gameModel, JSONObject request) {
        try {

            String asset = request.getString(UPDATE_STRUCTURE_ASSET);
            String mode = request.getString(UPDATE_STRUCTURE_MODE);
            int health = request.getInt(UPDATE_STRUCTURE_HEALTH);

            List<JSONObject> tiles = getSelectedTiles(gameModel);
            tiles.stream().map(e -> (Tile)e).forEach(tile -> {
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
    public JSONArray getTilesAtRowColumn(GameModel gameModel, JSONObject request) {
        TileMap tileMap = gameModel.getTileMap();


        int radius = request.optInt(GET_TILES_AT_RADIUS, 0);
        int row = request.getInt(GET_TILES_AT_ROW);
        int column = request.getInt(GET_TILES_AT_COLUMN);

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

    public JSONArray getTilesAtXY(GameModel gameModel, JSONObject request) {
        GameState gameStateV2 = gameModel.getGameState();
        TileMap tileMap = gameModel.getTileMap();
        JSONArray response = mEphemeralArrayResponse;
        response.clear();

        try {
            int radius = request.optInt(GET_TILES_AT_RADIUS, 0);
            int x = request.getInt(GET_TILES_AT_X);
            int y = request.getInt(GET_TILES_AT_Y);

            int cameraX = gameStateV2.getCameraX();
            int cameraY = gameStateV2.getCameraY();
            int spriteWidth = gameStateV2.getSpriteWidth();
            int spriteHeight = gameStateV2.getSpriteHeight();
            int column = (x + cameraX) / spriteWidth;
            int row = (y +cameraY) / spriteHeight;

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
    public void updateSpawners(GameModel gameModel, JSONObject request) {

        try {
            GameState gameState = gameModel.getGameState();


            List<JSONObject> selectedTiles = getSelectedTiles(gameModel);
            String spawner = request.getString(UPDATE_SPAWN_OPERATION_ON_TEAM);
            String operation = request.getString(UPDATE_SPAWN_MODE);

            selectedTiles.stream().map(e -> (Tile)e).forEach(tile -> {
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
    public void updateTileLayers(GameModel gameModel, JSONObject request) {

        try {
            TileMap tileMap = gameModel.getTileMap();
            List<JSONObject> selectedTiles = getSelectedTiles(gameModel);


            String manipulation = request.getString(UPDATE_TILE_LAYERS_MODE);
            String type = request.getString(Tile.LAYER_TYPE);
            int amount = request.getInt(Tile.LAYER_HEIGHT);
            String asset = (String) request.get(Tile.LAYER_ASSET);

            selectedTiles.stream().map(e -> (Tile)e).forEach(tile -> {
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
    private static void stripTerrain(TileMap tileMap, List<JSONObject> selectedTiles, String type, String asset) {
        if (selectedTiles.isEmpty()) { return; }
        // Get starting tile
        JSONObject tileJson = selectedTiles.get(0);
        Tile tile = (Tile) tileJson;
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

    private static void tileFillToLevel(TileMap tileMap, List<JSONObject> selectedTiles, String type, String asset) {
        if (selectedTiles.isEmpty()) { return; }
        JSONObject tileJson = selectedTiles.get(0);
        Tile tile = (Tile) tileJson;
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

    public static final String SHOULD_END_THE_TURN = "should.end.the.turn";
    public static final String SHOULD_AUTOMATICALLY_GO_TO_HOME_CONTROLS = "should.automatically.go.to.home.controls";
    public static final String ACTION_PANEL_IS_OPEN = "action.panel.is.open";
    public static final String MOVE_PANEL_IS_OPEN = "move.panel.is.open";
    public JSONObject getGameState(GameModel gameModel) {
        GameState gameState = gameModel.getGameState();
        mEphemeralObjectResponse.clear();
        mEphemeralObjectResponse.put(SHOULD_AUTOMATICALLY_GO_TO_HOME_CONTROLS, gameState.shouldAutomaticallyGoToHomeControls());
        mEphemeralObjectResponse.put(SHOULD_END_THE_TURN, gameState.shouldEndTheTurn());
        return mEphemeralObjectResponse;
    }

    public void updateGameState(GameModel gameModel, JSONObject request) {
        GameState gameState = gameModel.getGameState();
        if (request.has(SHOULD_AUTOMATICALLY_GO_TO_HOME_CONTROLS)) {
            gameState.setAutomaticallyGoToHomeControls(request.getBoolean(SHOULD_AUTOMATICALLY_GO_TO_HOME_CONTROLS));
        }

        if (request.has(SHOULD_END_THE_TURN)) {
            gameState.setShouldEndTheTurn(request.getBoolean(SHOULD_END_THE_TURN));
        }

        if (request.has(ACTION_PANEL_IS_OPEN)) { gameState.setActionPanelIsOpen(request.getBoolean(ACTION_PANEL_IS_OPEN)); }
        if (request.has(MOVE_PANEL_IS_OPEN)) { gameState.setMovementPanelIsOpen(request.getBoolean(MOVE_PANEL_IS_OPEN)); }
    }

    public void setTileToGlideTo(GameModel gameModel, JSONObject request) {
        GameState gameState = gameModel.getGameState();

        try {
            int row = request.getInt(Tile.ROW);
            int column = request.getInt(Tile.COLUMN);
            Tile tile = gameModel.tryFetchingTileAt(row, column);
            gameState.setTileToGlideTo(tile);
//        gameState.setTileToGlideTo(request);
        } catch (Exception e) {
            System.out.println(e + "??? " );
            e.printStackTrace();
        }
    }

    public String getCurrentTurnsUnitID(GameModel model) {

        String result = null;

        Entity unitEntity = model.getSpeedQueue().peek();
        if (unitEntity == null) { return result; }

        IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
        result = identityComponent.getID();

        return result;
    }

    public JSONArray getActionsOfUnitOfCurrentTurn(GameModel gameModel) {
        JSONArray response = mEphemeralArrayResponse;
        response.clear();

        Entity unitOfCurrentTurn = gameModel.getSpeedQueue().peek();
        if (unitOfCurrentTurn == null) { return mEphemeralArrayResponse; }

        StatisticsComponent statisticsComponent = unitOfCurrentTurn.get(StatisticsComponent.class);
        List<String> actions = statisticsComponent.getActions();
        for (String action : actions) {
            response.put(action);
        }

        return response;
    }



    public JSONArray getActionsOfUnit(String id) {
        JSONArray response = mEphemeralArrayResponse;
        response.clear();

        Entity unitEntity = EntityFactory.getInstance().get(id);
        if (unitEntity == null) { return response;  }

        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);

        List<String> actions = statisticsComponent.getActions();
        for (String action : actions) {
            response.put(action);
        }

        return response;
    }

    public static final String SET_ACTION_OF_UNIT_OF_CURRENT_TURN = "set.action.of.unit.of.current.turn";
    public void setActionOfUnitOfCurrentTurn(GameModel gameModel, JSONObject response) {

        Entity unitOfCurrentTurn = gameModel.getSpeedQueue().peek();
        if (unitOfCurrentTurn == null) { return; }

        String action = response.getString(SET_ACTION_OF_UNIT_OF_CURRENT_TURN);
        StatisticsComponent statisticsComponent = unitOfCurrentTurn.get(StatisticsComponent.class);
        if (!statisticsComponent.getActions().contains(action)) { return; }
        ActionComponent actionComponent = unitOfCurrentTurn.get(ActionComponent.class);
        actionComponent.stageAction(action);
    }

    public void stageActionForUnit(String id, String action) {

        Entity unitEntity = EntityFactory.getInstance().get(id);
        if (unitEntity == null) { return; }

        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        actionComponent.stageAction(action);
    }

    private static final String[] MOVEMENT_RELATED_STATS = new String[] {"move", "speed", "climb", "jump"};
    public JSONObject getMovementStatsOfUnitOfCurrentTurn(GameModel gameModel) {
        JSONObject response = mEphemeralObjectResponse;
        response.clear();

        Entity unitOfCurrentTurn = gameModel.getSpeedQueue().peek();
        if (unitOfCurrentTurn == null) { return response; }

        StatisticsComponent statisticsComponent = unitOfCurrentTurn.get(StatisticsComponent.class);
        for (String key : MOVEMENT_RELATED_STATS) {
            int total = statisticsComponent.getTotal(key);
            response.put(key, total);
        }

        return response;
    }


    public JSONObject getMovementStatsOfUnit(String id) {
        JSONObject response = mEphemeralObjectResponse;
        response.clear();

        Entity unitEntity = EntityFactory.getInstance().get(id);
        if (unitEntity == null) { return response; }

        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        for (String key : MOVEMENT_RELATED_STATS) {
            int total = statisticsComponent.getTotal(key);
            response.put(key, total);
        }

        return response;
    }

//    public JSONArray getUnitsOnSelectedTiles(GameModel gameModel) {
//        List<JSONObject> selectedTiles = gameModel.getGameState().getSelectedTiles();
//        JSONArray response = mEphemeralArrayResponse;
//        response.clear();
//
//        for (JSONObject jsonObject : selectedTiles) {
//            Tile tile = (Tile) jsonObject;
//            Entity unitEntity = tile.getUnit();
//            if (unitEntity == null) { continue; }
////            IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
////            response.put(identityComponent.getUuid());
//            StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
//            response.put(statisticsComponent.getUnit());
//        }
//
//        return response;
//    }

    public JSONObject getUnitsOnSelectedTiles(GameModel gameModel) {
        List<JSONObject> selectedTiles = gameModel.getGameState().getSelectedTiles();
        JSONObject response = mEphemeralObjectResponse;
        response.clear();

        for (JSONObject jsonObject : selectedTiles) {
            Tile tile = (Tile) jsonObject;
            Entity unitEntity = tile.getUnit();
            if (unitEntity == null) { continue; }

            IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
            StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);

            response.put(identityComponent.getID(), statisticsComponent.getUnit());
        }

        return response;
    }


    // EXPERIMENTAL, should this really consume the state like this
    public boolean consumeShouldAutomaticallyGoToHomeControls(GameModel mGameModel) {
        boolean shouldAutomaticallyGoToHomeControls = mGameModel.getGameState().shouldAutomaticallyGoToHomeControls();
        mGameModel.getGameState().setAutomaticallyGoToHomeControls(false);
        return shouldAutomaticallyGoToHomeControls;
    }

    public JSONArray getNodeBaseAndModifiedOfUnitOfCurrentTurn(GameModel gameModel, JSONArray request) {
        JSONArray response = mEphemeralArrayResponse;
        response.clear();

        Entity unitOfCurrentTurn = gameModel.getSpeedQueue().peek();
        if (unitOfCurrentTurn == null) { return response; }

        StatisticsComponent statisticsComponent = unitOfCurrentTurn.get(StatisticsComponent.class);

        for (int index = 0; index < request.length(); index++) {
            String key = request.getString(index);
            int base = statisticsComponent.getBase(key);
            int modified = statisticsComponent.getModified(key);

            JSONArray values = new JSONArray();

            values.put(key);
            values.put(base);
            values.put(modified);
        }

        return response;
    }

    public String getUnitAtMousePosition(GameModel gameModel) {
        String result = null;

        Entity tileEntity = gameModel.tryFetchingTileMousedAt();
        if (tileEntity == null) { return result; }

        Tile tile = tileEntity.get(Tile.class);
        Entity unitEntity = tile.getUnit();
        if (unitEntity == null) { return  result; }

        IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
        result = identityComponent.getID();

        return result;
    }

    public String getUnitNameAtMousePosition(GameModel gameModel) {
        String result = null;

        Entity tileEntity = gameModel.tryFetchingTileMousedAt();
        if (tileEntity == null) { return result; }

        Tile tile = tileEntity.get(Tile.class);
        Entity unitEntity = tile.getUnit();
        if (unitEntity == null) { return  result; }

        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        result = statisticsComponent.getUnit();

        return result;
    }

    public String getNicknameOfID(String id) {
        String result = null;

        Entity entity = EntityFactory.getInstance().get(id);
        if (entity == null) { return null; }

        IdentityComponent identityComponent = entity.get(IdentityComponent.class);
        result = identityComponent.getNickname();

        return result;
    }

    public String getUnitName(String id) {
        String result = null;

        Entity unitEntity = EntityFactory.getInstance().get(id);
        if (unitEntity == null) { return result; }
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        result = statisticsComponent.getUnit();

        return result;
    }

    private static String getID(JSONObject objectWithId) {
        String id = null;
        for (String potentiallyIdKey : objectWithId.keySet()) {
            switch (potentiallyIdKey) {
                case "id", "ID", "iD", "Id" -> { id = objectWithId.getString(potentiallyIdKey); }
                default -> { }
            }
        }
        return id;
    }

    private static final String[][] MINI_UNIT_INFO_PANEL_STATS_V2 = new String[][]{
//            new String[]{ "Health", "Health"},
            new String[]{ "physical_attack", "Physical Attack"},
            new String[]{ "magical_attack", "Magical Attack"},
            new String[]{ "physical_defense", "Physical Defense"},
            new String[]{ "magical_defense", "Magical Defense"},
            new String[]{ "speed", "Speed"},
            new String[]{ "move", "Move"},
            new String[]{ "climb", "Climb"},
    };



    public JSONArray getUnitStatsForMiniUnitInfoPanel(JSONObject request) {
        JSONArray response = mEphemeralArrayResponse;
        response.clear();

        String id = getID(request);
        if (id == null) { return response; }
        Entity unitEntity = EntityFactory.getInstance().get(id);
        if (unitEntity == null) { return response; }
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);

        for (String[] statAndAbbreviation : MINI_UNIT_INFO_PANEL_STATS_V2) {
            String stat = statAndAbbreviation[0];
            String abbreviation = statAndAbbreviation[1];
            int base = statisticsComponent.getBase(stat);
            int modified = statisticsComponent.getModified(stat);
            JSONArray values = new JSONArray();
            values.put(abbreviation);
            values.put(base);
            values.put(modified);
            response.put(values);
        }

        return response;
    }

    public JSONObject getUnitResourceStats(JSONObject request) {
        JSONObject response = mEphemeralObjectResponse;
        response.clear();

        String unitId = request.getString("id");
        String resource = request.getString("resource");

        if (unitId == null) { return response; }
        Entity unitEntity = EntityFactory.getInstance().get(unitId);
        if (unitEntity == null) { return null; }

        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        int total = statisticsComponent.getTotal(resource);
        int modified = statisticsComponent.getModified(resource);
        int current = statisticsComponent.getCurrent(resource);
        int base = statisticsComponent.getBase(resource);

        response.put("total", total);
        response.put("modified", modified);
        response.put("current", current);
        response.put("base", base);

        return response;
    }

    public String getUnitAtSelectedTiles(GameModel gameModel) {
        String result = null;

        List<JSONObject> selectedTiles = gameModel.getGameState().getSelectedTiles();
        for (JSONObject jsonObject : selectedTiles) {
            Tile tile = (Tile) jsonObject;
            Entity unitEntity = tile.getUnit();
            if (unitEntity == null) { continue; }
            IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
            result = identityComponent.getID();
            break;
        }

        return result;
    }

    private final String ID_KEY = "id";
    private final String NICKNAME_KEY = "nickname";
    private final String UNIT_KEY = "unit";

    public JSONObject getUnitIdentifiers(JSONObject request) {
        JSONObject response = mEphemeralObjectResponse;
        response.clear();

        String id = request.getString("id");

        if (id == null) { return response; }
        Entity unitEntity = EntityFactory.getInstance().get(id);
        if (unitEntity == null) { return null; }

        IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
        response.put(ID_KEY, identityComponent.getID());
        response.put(NICKNAME_KEY, identityComponent.getNickname());

        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        if (statisticsComponent != null) {
            response.put("unit", statisticsComponent.getUnit());
        }

        return response;
    }

    public void setMovementPanelIsOpen(GameModel gameModel, boolean value) {
        gameModel.getGameState().setMovementPanelIsOpen(value);
    }

    public void setActionPanelIsOpen(GameModel gameModel, boolean value) {
        gameModel.getGameState().setActionPanelIsOpen(value);
    }
}
