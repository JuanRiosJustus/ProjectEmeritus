package main.game.main;

import main.constants.Direction;
import main.game.components.AbilityComponent;
import main.game.components.AssetComponent;
import main.game.components.IdentityComponent;
import main.game.components.MovementComponent;
import main.game.components.ActionsComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.TileComponent;
import main.game.entity.Entity;
import main.game.map.base.TileMap;
import main.game.queue.SpeedQueue;
import main.game.stores.EntityStore;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class GameAPI {
    private final JSONObject mEphemeralObjectResponse = new JSONObject();
    private final JSONArray mEphemeralArrayResponse = new JSONArray();
    private final List<JSONObject> mEphemeralArrayList = new ArrayList<>();
    public static final String GET_CURRENT_UNIT_TURN_STATUS_HAS_MOVED = "HasMoved";
    public static final String GET_CURRENT_UNIT_TURN_STATUS_HAS_ACTED = "HasActed";
    private final GameModel mGameModel;
    private static final String ID = "id";

    public GameAPI(GameModel gameModel) { mGameModel = gameModel; }

    private Entity getEntityWithID(String id) { return EntityStore.getInstance().get(id); }

    public JSONObject getCurrentUnitTurnStatus(GameModel gameModel) {
        SpeedQueue speedQueue = gameModel.getSpeedQueue();
        JSONObject response = new JSONObject();
        response.clear();

        String entityID = speedQueue.peek();
        Entity unitEntity = EntityStore.getInstance().get(entityID);

        if (unitEntity == null) { return  response; }

        ActionsComponent actionsComponent = unitEntity.get(ActionsComponent.class);

        response.put(GET_CURRENT_UNIT_TURN_STATUS_HAS_MOVED, actionsComponent.hasFinishedMoving());
        response.put(GET_CURRENT_UNIT_TURN_STATUS_HAS_ACTED, actionsComponent.hasFinishedUsingAbility());
        return response;
    }

    public JSONObject getSelectedUnitsTurnState(GameModel gameModel) {
        JSONObject response = new JSONObject();

        String unitEntityID = gameModel.getSpeedQueue().peek(); // tile.getUnitID();
        Entity unitEntity = EntityStore.getInstance().get(unitEntityID);

        if (unitEntity == null) { return  response; }

        boolean isOwnerOfCurrentTurn = gameModel.getSpeedQueue().peek().equalsIgnoreCase(unitEntityID);

        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        ActionsComponent actionsComponentComponent = unitEntity.get(ActionsComponent.class);

        response.put(GET_CURRENT_UNIT_TURN_STATUS_HAS_MOVED, actionsComponentComponent.hasFinishedMoving());
        response.put(GET_CURRENT_UNIT_TURN_STATUS_HAS_ACTED, actionsComponentComponent.hasFinishedUsingAbility());
        response.put("is_current_turn", isOwnerOfCurrentTurn);
        return response;
    }

    public void setSelectedTileSize() {

    }

    public void setSelectedTilesV1(GameModel gameModel, JSONArray request) {
        GameState gameState = gameModel.getGameState();
        TileMap tileMap = gameModel.getTileMap();

        JSONArray selectedTiles = new JSONArray();

        for (Object object : request) {
            if (!(object instanceof JSONObject jsonObject)) { continue; }
            int row = jsonObject.getInt(TileComponent.ROW);
            int column = jsonObject.getInt(TileComponent.COLUMN);
            TileComponent tile = tileMap.tryFetchingTileAt(row, column);
            if (tile == null) { continue; }
            selectedTiles.put(tile);
        }

//        gameState.setSelectedTiles(selectedTiles);
    }

    public void setSelectedTileIDs(GameModel gameModel, JSONArray request) {
        GameState gameState = gameModel.getGameState();

        // Validate that the request can be placed in the Game State store
        for (int index = 0; index < request.length(); index++) {
            String selectedTileID = request.getString(index);
            Entity tileEntity = EntityStore.getInstance().get(selectedTileID);
            if (tileEntity == null) { return; }
        }

        gameState.setSelectedTileIDs(request);
    }



//    public void setSelectedTiles(GameModel gameModel, JSONArray request) {
//        GameState gameState = gameModel.getGameState();
//        TileMap tileMap = gameModel.getTileMap();
//
//        JSONArray selectedTiles = new JSONArray();
//
//        for (Object object : request) {
//            if (!(object instanceof JSONObject jsonObject)) { continue; }
//            int row = jsonObject.getInt(Tile.ROW);
//            int column = jsonObject.getInt(Tile.COLUMN);
//            Tile tile = tileMap.tryFetchingTileAt(row, column);
//            if (tile == null) { continue; }
//            selectedTiles.put(tile);
//        }
//
////        gameState.setSelectedTiles(selectedTiles);
//    }

//    public void getSelectedTilesChecksum(GameModel gameModel, JSONObject result) {
//        int checksum = getSelectedTilesChecksum(gameModel);
//        result.clear();
//        result.put("checksum", checksum);
//    }

    public void getSelectedTilesChecksum(GameModel gameModel, JSONObject out) {
        int checksum = gameModel.getGameState().getSelectedTilesChecksum();
        out.clear();
        out.put("checksum", checksum);
    }

    public JSONArray getSelectedTiles(GameModel gameModel) { return gameModel.getGameState().getSelectedTileIDs(); }
    public int getHoveredTilesHash(GameModel gameModel) { return gameModel.getGameState().getHoveredTilesHash(); }
    public JSONArray getHoveredTiles(GameModel gameModel) { return gameModel.getGameState().getHoveredTileIDs(); }

    public JSONArray getSelectedUnitsActions(GameModel gameModel) {
        GameState gameState = gameModel.getGameState();
        JSONArray selectedTiles = gameState.getSelectedTileIDs();
        mEphemeralArrayResponse.clear();

        for (int index = 0; index < selectedTiles.length(); index++) {
            String selectedTile = selectedTiles.getString(index);
            Entity entity = EntityStore.getInstance().get(selectedTile);
            TileComponent tile = entity.get(TileComponent.class);
            String unitID = tile.getUnitID();
            Entity unitEntity = EntityStore.getInstance().get(unitID);
            if (unitEntity == null) { continue; }
            StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
            for (String action : statisticsComponent.getOtherAbility()) {
                mEphemeralArrayResponse.put(action);
            }
            break;
        }

        return mEphemeralArrayResponse;
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

//            List<JSONObject> tiles = getSelectedTiles(gameModel);
//            tiles.stream().map(e -> (Tile)e).forEach(tile -> {
////                if (mode.equalsIgnoreCase(UPDATE_STRUCTURE_ADD_MODE)) {
////                    tile.addStructure(asset, health);
////                } else if (mode.equalsIgnoreCase(UPDATE_STRUCTURE_DELETE_MODE)) {
////                    tile.deleteStructure();
////                }
//            });
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

        TileComponent tile = tileMap.tryFetchingTileAt(row, column);
        if (tile == null) { return null; }

        // Get tiles for the specified radius
        JSONArray tiles = new JSONArray();
        for (row = tile.getRow() - radius; row <= tile.getRow() + radius; row++) {
            for (column = tile.getColumn() - radius; column <= tile.getColumn() + radius; column++) {
                TileComponent adjacentTile = tileMap.tryFetchingTileAt(row, column);
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

            int cameraX = gameStateV2.getMainCameraX();
            int cameraY = gameStateV2.getMainCameraY();
            int spriteWidth = gameStateV2.getSpriteWidth();
            int spriteHeight = gameStateV2.getSpriteHeight();
            int column = (x + cameraX) / spriteWidth;
            int row = (y +cameraY) / spriteHeight;

            TileComponent tile = tileMap.tryFetchingTileAt(row, column);
            if (tile == null) { return null; }

            // Get tiles for the specified radius
            for (row = tile.getRow() - radius; row <= tile.getRow() + radius; row++) {
                for (column = tile.getColumn() - radius; column <= tile.getColumn() + radius; column++) {
                    TileComponent adjacentTile = tileMap.tryFetchingTileAt(row, column);
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
//            GameState gameState = gameModel.getGameState();
//
//
//            List<JSONObject> selectedTiles = getSelectedTiles(gameModel);
//            String spawner = request.getString(UPDATE_SPAWN_OPERATION_ON_TEAM);
//            String operation = request.getString(UPDATE_SPAWN_MODE);
//
//            selectedTiles.stream().map(e -> (Tile)e).forEach(tile -> {
//                if (operation.equalsIgnoreCase(UPDATE_SPAWNER_OPERATION_ADD)) {
//                    tile.addSpawner(spawner);
//                } else if (operation.equalsIgnoreCase(UPDATE_SPAWNER_OPERATION_DELETE)) {
//                    tile.deleteSpawner(spawner);
//                }
//            });
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
//            List<JSONObject> selectedTiles = getSelectedTiles(gameModel);


            String manipulation = request.getString(UPDATE_TILE_LAYERS_MODE);
            String type = request.getString(TileComponent.LAYER_TYPE);
            int amount = request.getInt(TileComponent.LAYER_HEIGHT);
            String asset = (String) request.get(TileComponent.LAYER_ASSET);

//            selectedTiles.stream().map(e -> (Tile)e).forEach(tile -> {
//                switch (manipulation) {
//                    case UPDATE_TILE_LAYERS_OPERATION_ADD_LAYER -> tile.addLayer(type, amount, asset);
//                    case UPDATE_TILE_LAYERS_OPERATION_EXTEND_LAYER -> tile.addLayer(type, amount);
//                    case UPDATE_TILE_LAYERS_OPERATION_DELETE_LAYER -> tile.removeLayer();
//                    case UPDATE_TILE_LAYERS_OPERATION_SHORTEN_LAYER -> tile.removeLayer(amount);
//                    case UPDATE_TILE_LAYERS_OPERATION_FILL_TO_LAYER -> tileFillToLevel(tileMap, selectedTiles, type, asset);
//                    case UPDATE_TILE_LAYERS_OPERATION_STRIP_TERRAIN -> { stripTerrain(tileMap, selectedTiles, type, asset); }
//                    default -> { }
//                }
//            });
        } catch (Exception ex) {

        }
    }


    // TODO
    private static void stripTerrain(TileMap tileMap, List<JSONObject> selectedTiles, String type, String asset) {
        if (selectedTiles.isEmpty()) { return; }
        // Get starting tile
        JSONObject tileJson = selectedTiles.get(0);
        TileComponent tile = (TileComponent) tileJson;
        int heightOfStartingTile = tile.getModifiedElevation();
        // Fill all tiles
        Queue<TileComponent> queue = new LinkedList<>();
        queue.add(tile);
        Set<TileComponent> set = new HashSet<>();
        List<TileComponent> tilesToUpdate = new ArrayList<>();

        while (!queue.isEmpty()) {
            TileComponent traversedTile = queue.poll();
            // If we've seen he current tile, skip
            if (set.contains(traversedTile)) { continue; }
            // If the current tile is HIGHER than starting tile, skip
            if (heightOfStartingTile < traversedTile.getModifiedElevation() && traversedTile != tile) { continue; }
            set.add(traversedTile);
            // Mark tile to process
            tilesToUpdate.add(traversedTile);
            // Collect more tiles
            for (Direction direction : Direction.values()) {
                int row = traversedTile.getRow() + direction.y;
                int column = traversedTile.getColumn() + direction.x;
                TileComponent adjacentNeighborTile = tileMap.tryFetchingTileAt(row, column);
                if (adjacentNeighborTile == null) { continue; }
                queue.add(adjacentNeighborTile);
            }
        }
        heightOfStartingTile += 1;
        for (TileComponent tileToUpdate : tilesToUpdate) {
            int heightOfTileToUpdate = tileToUpdate.getModifiedElevation();
            int heightDelta = heightOfStartingTile - heightOfTileToUpdate;
            if (heightDelta <= 0) { continue; }
            tileToUpdate.addLayer(type, heightDelta, asset);
        }
    }

    private static void tileFillToLevel(TileMap tileMap, List<JSONObject> selectedTiles, String type, String asset) {
        if (selectedTiles.isEmpty()) { return; }
        JSONObject tileJson = selectedTiles.get(0);
        TileComponent tile = (TileComponent) tileJson;
        int heightOfStartingTile = tile.getModifiedElevation();
        // Fill all tiles
        Queue<TileComponent> queue = new LinkedList<>();
        queue.add(tile);
        Set<TileComponent> set = new HashSet<>();
        List<TileComponent> tilesToUpdate = new ArrayList<>();

        while (!queue.isEmpty()) {
            TileComponent traversedTile = queue.poll();
            // If we've seen he current tile, skip
            if (set.contains(traversedTile)) { continue; }
            // If the current tile is HIGHER than starting tile, skip
            if (heightOfStartingTile < traversedTile.getModifiedElevation() && traversedTile != tile) { continue; }
            set.add(traversedTile);
            // Mark tile to process
            tilesToUpdate.add(traversedTile);
            // Collect more tiles
            for (Direction direction : Direction.values()) {
                int row = traversedTile.getRow() + direction.y;
                int column = traversedTile.getColumn() + direction.x;
                TileComponent adjacentNeighborTile = tileMap.tryFetchingTileAt(row, column);
                if (adjacentNeighborTile == null) { continue; }
                queue.add(adjacentNeighborTile);
            }
        }
        heightOfStartingTile += 1;
        for (TileComponent tileToUpdate : tilesToUpdate) {
            int heightOfTileToUpdate = tileToUpdate.getModifiedElevation();
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
        mEphemeralObjectResponse.put(SHOULD_END_THE_TURN, gameState.shouldForcefullyEndTurn());
        return mEphemeralObjectResponse;
    }

    public void setEndTurn(GameModel gameModel) {
        GameState gameState = gameModel.getGameState();
        gameState.setShouldForcefullyEndTurn(true);
    }

    public void setTileToGlideTo(GameModel gameModel, JSONObject request) {
        GameState gameState = gameModel.getGameState();

        if (request.isEmpty()) { return; }
        String tileID = request.getString("id");
        String camera = request.getString("camera");

        // Ensure the id is at least an existing entity
        Entity tileEntity = EntityStore.getInstance().get(tileID);
        if (tileEntity == null) { return; }

        // Ensure the entity is a tile we can glide to
        TileComponent tile = tileEntity.get(TileComponent.class);
        if (tile == null) { return; }
        gameState.addTileToGlideTo(tileID, camera);
    }

    public static final String TILE_TO_GLIDE_TO_TILE_ID = "tile_id";
    public static final String TILE_TO_GLIDE_TO_CAMERA = "camera";
    public JSONObject setTileToGlideToV2(JSONObject request) {
        JSONObject response = new JSONObject();
        if (request.isEmpty()) { return response; }

        String tileID = request.getString(TILE_TO_GLIDE_TO_TILE_ID);
        String camera = request.getString(TILE_TO_GLIDE_TO_CAMERA);

        // Ensure the id is at least an existing entity
        Entity tileEntity = EntityStore.getInstance().get(tileID);
        if (tileEntity == null) { return response; }

        // Ensure the entity is a tile we can glide to
        TileComponent tile = tileEntity.get(TileComponent.class);
        if (tile == null) { return response; }

        GameState gameState = mGameModel.getGameState();
        gameState.addTileToGlideTo(tileID, camera);

        response.put("status_code", "success");
        return response;
    }


    public JSONObject getSecondaryCameraInfo(GameModel model) {
        JSONObject response = new JSONObject();

        String camera = model.getGameState().getSecondaryCameraID();
        int x = model.getGameState().getCameraX(camera);
        int y = model.getGameState().getCameraY(camera);
        int width = model.getGameState().getCameraWidth(camera);
        int height = model.getGameState().getCameraHeight(camera);

        response.put("x", x);
        response.put("y", y);
        response.put("width", width);
        response.put("height", height);
        response.put("camera", camera);

        return response;
    }

    public JSONObject getMainCameraInfo(GameModel model) {
        JSONObject response = new JSONObject();

        String camera = model.getGameState().getMainCameraID();
        int x = model.getGameState().getCameraX(camera);
        int y = model.getGameState().getCameraY(camera);
        int width = model.getGameState().getCameraWidth(camera);
        int height = model.getGameState().getCameraHeight(camera);

        response.put("x", x);
        response.put("y", y);
        response.put("width", width);
        response.put("height", height);
        response.put("camera", camera);

        return response;
    }

    public static final String GET_CAMERA_INFO_CAMERA_ID_KEY = "camera";
    public JSONObject getCameraInfo(JSONObject request) {
        JSONObject response = new JSONObject();
        String cameraName = request.optString(GET_CAMERA_INFO_CAMERA_ID_KEY, null);

        if (cameraName == null) { return response; }

        int x = mGameModel.getGameState().getCameraX(cameraName);
        int y = mGameModel.getGameState().getCameraY(cameraName);
        int width = mGameModel.getGameState().getCameraWidth(cameraName);
        int height = mGameModel.getGameState().getCameraHeight(cameraName);

        response.put("x", x);
        response.put("y", y);
        response.put("width", width);
        response.put("height", height);
        response.put("camera", cameraName);

        return response;
    }




    public JSONObject getEntityOfCurrentTurnsID() {
        JSONObject response = new JSONObject();
        String entityID = mGameModel.getSpeedQueue().peek();
        if (entityID == null) { return response; }

        response.put(ID, entityID);
        return response;
    }

    public JSONObject getCurrentTurnsEntity(GameModel model) {
        JSONObject response = new JSONObject();
        String entityID = model.getSpeedQueue().peek();
        if (entityID == null) { return response; }

        response.put(ID, entityID);
        return response;
    }

    public void getCurrentTurnsEntityAndStatisticsChecksum(GameModel gameModel, JSONObject out) {
        String entityID = gameModel.getSpeedQueue().peek();

        out.clear();
        if (entityID == null || entityID.isEmpty()) { return; }

        Entity entity = EntityStore.getInstance().get(entityID);
        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
        int statsChecksum = statisticsComponent.getChecksum();
        MovementComponent movementComponent = entity.get(MovementComponent.class);
        int moveChecksum = movementComponent.getChecksum();
        IdentityComponent identityComponent = entity.get(IdentityComponent.class);
        String nickname = identityComponent.getNickname();

        out.put("nickname", nickname);
        out.put("id", entityID);
        out.put("checksum", statsChecksum);
    }

    public void getEntityOnSelectedTilesChecksum(GameModel gameModel, JSONObject out) {
        JSONArray selectedTiles = gameModel.getGameState().getSelectedTileIDs();

        out.clear();
        if (selectedTiles == null || selectedTiles.isEmpty()) { return; }

        // Get the first selected tile
        String selectedTileID = selectedTiles.getString(0);
        Entity selectedTileEntity = EntityStore.getInstance().get(selectedTileID);
        if (selectedTileEntity == null) { return; }

        // Get the unit on the selected tile
        TileComponent tile = selectedTileEntity.get(TileComponent.class);
        String unitEntityID = tile.getUnitID();
        Entity unitEntity = EntityStore.getInstance().get(unitEntityID);
        if (unitEntity == null) { return; }

        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        int statsChecksum = statisticsComponent.getChecksum();
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        int moveChecksum = movementComponent.getChecksum();
        IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
        String nickname = identityComponent.getNickname();
        int selectedTilesChecksum = gameModel.getGameState().getSelectedTilesChecksum();

        out.put("nickname", nickname);
        out.put("id", unitEntityID);
        out.put("checksum", statsChecksum + moveChecksum + selectedTilesChecksum);
    }



//    public JSONObject getCurrentTurnsEntityAndStatisticsChecksum(GameModel gameModel) {
//        JSONObject response = new JSONObject();
//        String entityID = gameModel.getSpeedQueue().peekV2();
//        if (entityID == null) { return response; }
//
//        Entity entity = EntityStore.getInstance().get(entityID);
//        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
//        int checksum = statisticsComponent.getChecksum();
//        IdentityComponent identityComponent = entity.get(IdentityComponent.class);
//        String nickname = identityComponent.getNickname();
//
//        response.put("nickname", nickname);
//        response.put("id", entityID);
//        response.put("checksum", checksum);
//        return response;
//    }

    public JSONArray getAbilitiesOfUnitEntity(String id) {
        JSONArray response = mEphemeralArrayResponse;
        response.clear();

        Entity unitEntity = EntityStore.getInstance().get(id);
        if (unitEntity == null) { return response;  }

        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);

        Set<String> actions = statisticsComponent.getOtherAbility();
        for (String action : actions) {
            response.put(action);
        }

        return response;
    }

    public JSONArray getAbilitiesOfUnitEntity(JSONObject request) {
        JSONArray response = new JSONArray();
        String unitEntityID = request.optString(ID, null);
        if (unitEntityID == null) { return response; }


//        mGameModel.getAbilitiesOfEntity(unitEntityID);

        Entity unitEntity = EntityStore.getInstance().get(unitEntityID);
        if (unitEntity == null) { return response;  }

        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);

        Set<String> actions = statisticsComponent.getOtherAbility();
        for (String action : actions) {
            response.put(action);
        }

        return response;
    }


    public static final String SET_ACTION_OF_UNIT_OF_CURRENT_TURN = "set.action.of.unit.of.current.turn";

    public void stageActionForUnit(String id, String action) {

        Entity unitEntity = EntityStore.getInstance().get(id);
        if (unitEntity == null) { return; }

        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
        abilityComponent.stageAbility(action);
    }

    public void stageActionForUnit(JSONObject request) {

        String unitID = request.getString("id");
        String unitAction = request.getString("action");

        Entity unitEntity = EntityStore.getInstance().get(unitID);
        if (unitEntity == null) { return; }

        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
        abilityComponent.stageAbility(unitAction);
    }


    private static final String[] MOVEMENT_RELATED_STATS = new String[] {"move", "speed", "climb", "jump"};


    public JSONObject getMovementStatsOfUnit(String id) {
        JSONObject response = mEphemeralObjectResponse;
        response.clear();

        Entity unitEntity = EntityStore.getInstance().get(id);
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

    // EXPERIMENTAL, should this really consume the state like this
    public boolean consumeShouldAutomaticallyGoToHomeControls(GameModel mGameModel) {
        boolean shouldAutomaticallyGoToHomeControls = mGameModel.getGameState().shouldAutomaticallyGoToHomeControls();
        mGameModel.getGameState().setAutomaticallyGoToHomeControls(false);
        return shouldAutomaticallyGoToHomeControls;
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
            new String[]{ "health", "Health"},
            new String[]{ "mana", "Mana"},
            new String[]{ "stamina", "Stamina"},
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
        Entity unitEntity = EntityStore.getInstance().get(id);
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

    public JSONArray getEntityIDsAtSelectedTiles(GameModel gameModel) {
        JSONArray response = new JSONArray();

        JSONArray selectedTiles = gameModel.getGameState().getSelectedTileIDs();
        for (int index = 0; index < selectedTiles.length(); index++) {
            String selectedTileID = selectedTiles.getString(index);
            Entity entity = EntityStore.getInstance().get(selectedTileID);
            if (entity == null) { continue; }

            TileComponent tile = entity.get(TileComponent.class);
            String tileID = tile.getUnitID();
            Entity unitEntity = EntityStore.getInstance().get(tileID);
            if (unitEntity == null) { continue; }


            IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
            String id = identityComponent.getID();
            String nickname = identityComponent.getNickname();

            JSONObject unitData = new JSONObject();
            unitData.put("id", id);
            unitData.put("nickname", nickname);
            response.put(unitData);
        }

        return response;
    }

//    public JSONObject getSelectedUnitStatisticsHashState(GameModel gameModel) {
//        JSONObject response = new JSONObject();
//        JSONArray selectedTiles = gameModel.getGameState().getSelectedTiles();
//        for (int index = 0; index < selectedTiles.length(); index++) {
//            String selectedTile = selectedTiles.getString(index);
//            Entity entity = EntityStore.getInstance().get(selectedTile);
//            Tile tile = entity.get(Tile.class);
//            String unitID = tile.getUnitID();
//            Entity unitEntity = EntityStore.getInstance().get(unitID);
//            if (unitEntity == null) { continue; }
//            IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
//            StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
//            int hashState = statisticsComponent.getHashState();
//            response.put("unit", identityComponent.getID());
//            response.put("hash", hashState);
//        }
//
//        return response;
//    }
//    public JSONObject getSelectedUnitDataForStandardUnitInfoPanel(GameModel gameModel) {
//        JSONObject response = new JSONObject();
//
//        JSONArray ids = gameModel.getGameState().getSelectedTileIDs();
//        for (int index = 0; index < ids.length(); index++) {
//            String id = ids.getString(index);
//            Entity entity = EntityStore.getInstance().get(id);
//            Tile tile = entity.get(Tile.class);
//            String unitID = tile.getUnitID();
//            Entity unitEntity = EntityStore.getInstance().get(unitID);
//            if (unitEntity == null) { continue; }
//            IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
//            StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
//
//            response.put("level", statisticsComponent.getLevel());
//            response.put("type", statisticsComponent.getType().iterator().next());
//            response.put("nickname", identityComponent.getNickname());
//            response.put("unit", statisticsComponent.getUnit());
//            response.put("id", identityComponent.getID());
//
//            JSONObject statRequest = mEphemeralObjectResponse;
//            statRequest.clear();
//            statRequest.put("id", identityComponent.getID());
//
//            JSONObject statNodes = getStatisticsForUnit(gameModel, statRequest);
//            response.put("statistics", statNodes);
//
//            JSONArray abilities = new JSONArray();
//            for (String key : statisticsComponent.getAbilities()) { abilities.put(key); }
//            response.put("abilities", abilities);
//
//            JSONArray tags = new JSONArray();
//            Map<String, Integer> statisticTags = statisticsComponent.getTags();
//            for (Map.Entry<String, Integer> statisticTag : statisticTags.entrySet()) {
//
//                String tag = statisticTag.getKey();
//                int count = statisticTag.getValue();
//
//                JSONObject tagData = new JSONObject();
//                tagData.put("tag", tag);
//                tagData.put("count", count);
//                tags.put(tagData);
//
//            }
//            response.put("tags", tags);
//
//            break;
//        }
//
//        return response;
//    }

    private final String ID_KEY = "id";
    private final String NICKNAME_KEY = "nickname";
    private final String UNIT_KEY = "unit";

    public JSONObject getUnitIdentifiers(JSONObject request) {
        JSONObject response = mEphemeralObjectResponse;
        response.clear();

        String id = request.getString("id");

        if (id == null) { return response; }
        Entity unitEntity = EntityStore.getInstance().get(id);
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

    public void setStatisticsPanelIsOpen(GameModel gameModel, boolean value) {
        gameModel.getGameState().setStatisticsPanelIsOpen(value);
    }
    public void setMovementPanelIsOpen(GameModel gameModel, boolean value) {
        gameModel.getGameState().setMovementPanelIsOpen(value);
    }

    public void setAbilityPanelIsOpen(GameModel gameModel, boolean value) {
        gameModel.getGameState().setAbilityPanelIsOpen(value);
    }

    public int getUnitAttributeScaling(JSONObject request) {

        String id = request.getString("id");
        String attribute = request.getString("attribute");
        String scaling = request.getString("scaling");

        Entity entity = EntityStore.getInstance().get(id);
        if (entity == null) { return 0; }

        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
        int value = (int) statisticsComponent.getScaling(attribute, scaling);
        return value;
    }

    public JSONObject getSelectedTilesInfoForMiniSelectionInfoPanel(GameModel model) {
        JSONObject response = new JSONObject();
        JSONArray ids = getSelectedTiles(model);

        // This panel only takes a single TILE element
        for (int index = 0; index < ids.length(); index++) {
            String id = ids.getString(index);
            Entity entity = EntityStore.getInstance().get(id);
            TileComponent tile = entity.get(TileComponent.class);
            AssetComponent assetComponent = entity.get(AssetComponent.class);

            response.put("id", id);
            response.put("top_layer_asset", tile.getTopLayerAsset());
            response.put("label", "[" + tile.getRow() + ", " + tile.getColumn() + "] (" + tile.getModifiedElevation() + ")");
            response.put("asset_id", assetComponent.getMainID());
            response.put("entity_on_tile", tile.getUnitID());
            break;
        }

        return response;
    }


    public JSONObject getStatisticsForUnit(GameModel mGameModel, JSONObject request) {
        String unitID = request.getString(ID);
        Entity unitEntity = getEntityWithID(unitID);

        JSONObject response = new JSONObject();
        if (unitEntity == null) { return response; }

        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        Set<String> nodes = statisticsComponent.getAttributes();
        for (String node : nodes) {
            int base = statisticsComponent.getBase(node);
            int modified = statisticsComponent.getModified(node);
            int current = statisticsComponent.getCurrent(node);
            JSONObject nodeData = new JSONObject();
            nodeData.put("base", base);
            nodeData.put("modified", modified);
            nodeData.put("current", current);

            response.put(node, nodeData);
        }

        return response;
    }




    public void setHoveredTilesCursorSizeAPI(GameModel gameModel, JSONObject request) {
        int cursorSize = request.getInt("cursor_size");
        gameModel.getGameState().setHoveredTilesCursorSize(cursorSize);
    }

    public JSONObject getStatisticsChecksumForUnit(GameModel mGameModel, JSONObject request) {

        String unitID = request.getString(ID);
        Entity unitEntity = getEntityWithID(unitID);
        JSONObject response = new JSONObject();
        if (unitEntity == null) { return response; }

        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        Set<String> nodes = statisticsComponent.getAttributes();
        for (String node : nodes) {
            int base = statisticsComponent.getBase(node);
            int modified = statisticsComponent.getModified(node);
            int current = statisticsComponent.getCurrent(node);
            JSONObject nodeData = new JSONObject();
            nodeData.put("base", base);
            nodeData.put("modified", modified);
            nodeData.put("current", current);

            response.put(node, nodeData);
        }

        return response;
    }
    public JSONObject getStatisticsForStatisticsPanel(GameModel mGameModel, JSONObject request) {

        String unitID = request.getString(ID);
        Entity unitEntity = getEntityWithID(unitID);
        JSONObject response = new JSONObject();
        if (unitEntity == null) { return response; }

        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        Set<String> nodes = statisticsComponent.getAttributes();
        for (String node : nodes) {
            int base = statisticsComponent.getBase(node);
            int modified = statisticsComponent.getModified(node);
            JSONObject nodeData = new JSONObject();
            nodeData.put("base", base);
            nodeData.put("modified", modified);

            response.put(node, nodeData);
        }


        return response;
    }


    public JSONObject getMovementStatsForMovementPanel(GameModel mGameModel, JSONObject request) {
        JSONObject response = new JSONObject();

        String unitID = request.getString(ID);
        Entity unitEntity = getEntityWithID(unitID);
        if (unitEntity == null) { return response; }

        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        String[] nodes = new String[]{ "move", "climb", "jump", "speed" };
        for (String node : nodes) {
            int base = statisticsComponent.getBase(node);
            int modified = statisticsComponent.getModified(node);
            JSONObject nodeData = new JSONObject();
            nodeData.put("base", base);
            nodeData.put("modified", modified);

            response.put(node, nodeData);
        }


        return response;
    }

    public void setAbilitySelectedFromUI(GameModel gameModel, JSONObject request) {
        String ability = request.getString("ability");
        if (ability == null) { return; }
        gameModel.getGameState().setAbilitySelectedFromUI(ability);
    }

    public JSONObject getAbilitySelectedFromUI(GameModel gameModel) {
        JSONObject response = new JSONObject();
        String abilitySelectedFromUI = gameModel.getGameState().getAbilitySelectedFromUI();
        response.put("ability", abilitySelectedFromUI);
        return response;
    }

    public JSONArray getAllEntitiesInTurnQueueWithPendingTurnCheckSum(GameModel gameModel) {
        JSONArray response = mEphemeralArrayResponse;
        response.clear();
        int checksum = gameModel.getSpeedQueue().getAllEntitiesInTurnQueueWithPendingTurnChecksum();
        response.put(checksum);
        return response;
    }

    public JSONArray getAllEntitiesInTurnQueueWithFinishedTurnCheckSum(GameModel gameModel) {
        JSONArray response = mEphemeralArrayResponse;
        response.clear();
        int checksum = gameModel.getSpeedQueue().getAllEntitiesInTurnQueueWithFinishedTurnChecksum();
        response.put(checksum);
        return response;
    }

    public JSONArray getAllEntitiesInTurnQueueCheckSum(GameModel gameModel) {
        JSONArray response = mEphemeralArrayResponse;
        response.clear();
        int checksum = gameModel.getSpeedQueue().getAllEntitiesInTurnQueueChecksum();
        response.put(checksum);
        return response;
    }

    public void getTurnQueueChecksums(GameModel gameModel, JSONObject out) {
        int finishedCheckSum = gameModel.getSpeedQueue().getAllEntitiesInTurnQueueWithFinishedTurnChecksum();
        int pendingCheckSum = gameModel.getSpeedQueue().getAllEntitiesInTurnQueueWithPendingTurnChecksum();
        int allCheckSum = gameModel.getSpeedQueue().getAllEntitiesInTurnQueueChecksum();

        out.clear();
        out.put("finished", finishedCheckSum);
        out.put("pending", pendingCheckSum);
        out.put("all", allCheckSum);
    }

    public JSONArray getAllEntitiesInTurnQueuePendingTurn(GameModel gameModel) {
        JSONArray response = new JSONArray();
        List<String> unitsPendingTurn = gameModel.getSpeedQueue().getAllEntitiesInTurnQueueWithPendingTurn();
        response.putAll(unitsPendingTurn);
        return response;
    }

    public JSONArray getAllEntitiesInTurnQueueFinishedTurn(GameModel gameModel) {
        JSONArray response = new JSONArray();
        List<String> unitsPendingTurn = gameModel.getSpeedQueue().getAllEntitiesInTurnQueueWithPendingTurn();
        response.putAll(unitsPendingTurn);
        return response;
    }



    public JSONArray getAllEntitiesInTurnQueue(GameModel gameModel) {
        JSONArray response = new JSONArray();
        List<String> unitsPendingTurn = gameModel.getSpeedQueue().getAllEntitiesInTurnQueue();
        response.putAll(unitsPendingTurn);
        return response;
    }


    public JSONArray getEntityTileID(JSONObject request) {
        JSONArray response = new JSONArray();

        String unitID = request.getString(ID);
        Entity unitEntity = getEntityWithID(unitID);
        if (unitEntity == null) { return response; }
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        String tileID = movementComponent.getCurrentTileID();
        response.put(tileID);

        return response;
    }
    public JSONArray getCurrentActiveEntityTileID(JSONObject request) {
        JSONArray response = new JSONArray();

        String tileID = mGameModel.getCurrentActiveEntityTileID();
        if (tileID == null) { return response; }
        response.put(tileID);

        return response;
    }

    public JSONObject getStatisticsForEntity(GameModel gameModel, JSONObject request) {

        JSONObject response = new JSONObject();
        String entityID = request.getString(ID);
        Entity entity = getEntityWithID(entityID);
        if (entity == null) { return response; }

        IdentityComponent identityComponent = entity.get(IdentityComponent.class);
        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);

        response.put("level", statisticsComponent.getLevel());
        response.put("type", statisticsComponent.getType());
        response.put("nickname", identityComponent.getNickname());
        response.put("unit", statisticsComponent.getUnit());
        response.put("id", identityComponent.getID());

        // Get the attributes for the entity
        JSONObject attributes = new JSONObject();
        Set<String> keys = statisticsComponent.getAttributes();
        for (String key : keys) {
            int base = statisticsComponent.getBase(key);
            int modified = statisticsComponent.getModified(key);
            int current = statisticsComponent.getCurrent(key);
            int total = statisticsComponent.getTotal(key);
            JSONObject attribute = new JSONObject();
            attribute.put("base", base);
            attribute.put("modified", modified);
            attribute.put("current", current);
            attribute.put("total", total);
            attributes.put(key, attribute);
        }
        response.put("attributes", attributes);

        JSONArray abilities = new JSONArray();
        for (String key : statisticsComponent.getOtherAbility()) { abilities.put(key); }
        response.put("abilities", abilities);
        response.put("basic_ability", statisticsComponent.getBasicAbility());
        response.put("passive_ability", statisticsComponent.getPassiveAbility());
        response.put("other_ability", statisticsComponent.getOtherAbility());

        JSONArray tags = new JSONArray();
        keys = statisticsComponent.getTagKeys();
        for (String key : keys) {
            JSONObject tag = statisticsComponent.getTag(key);
            tags.put(tag);
        }

        response.put("tags", tags);

        return response;
    }

    public void setConfigurableStateGameplayHudIsVisible(GameModel gameModel, boolean value) {
        gameModel.getGameState().setConfigurableStateGameplayHudIsVisible(value);
    }

    public boolean getConfigurableStateGameplayHudIsVisible(GameModel gameModel) {
        return gameModel.getGameState().getConfigurableStateGameplayHudIsVisible();
    }

    public void setCameraZoomAPI(GameModel mGameModel, JSONObject request) {
        float zoom = request.optFloat("zoom");

        int spriteWidth = mGameModel.getGameState().getOriginalSpriteWidth();
        int spriteHeight = mGameModel.getGameState().getOriginalSpriteHeight();

        int newSpriteWidth = (int) (spriteWidth * zoom);
        int newSpriteHeight = (int) (spriteHeight * zoom);

        mGameModel.getGameState().setSpriteWidth(newSpriteWidth);
        mGameModel.getGameState().setSpriteHeight(newSpriteHeight);
    }

    public JSONObject getCenterTileEntity(GameModel model) {
        JSONObject response = new JSONObject();

        String camera = model.getGameState().getMainCameraID();
        int row = model.getRows()  / 2;
        int column = model.getColumns() / 2;
        Entity entity = model.tryFetchingEntityAt(row, column);

        if (entity == null) { return response; }

        IdentityComponent identityComponent = entity.get(IdentityComponent.class);

        response.put("id", identityComponent.getID());

        return response;
    }

    public void publishEvent(GameModel mGameModel, JSONObject event) {
        GameState gameState = mGameModel.getGameState();

    }

    public JSONObject focusCamerasAndSelectionsOnActiveEntity(JSONObject request) {
        JSONObject response = new JSONObject();

        JSONArray currentTiles = getCurrentActiveEntityTileID(request);
        setSelectedTileIDs(mGameModel, currentTiles);

//        JSONObject cameraInfoRequest = getCameraInfo(request);

        JSONObject tileToGlideToRequest = new JSONObject();
//        tileToGlideToRequest.put(TILE_TO_GLIDE_TO_CAMERA, cameraInfoRequest.getString("camera"));
        String cameraSelection = request.getString("camera");
        tileToGlideToRequest.put(TILE_TO_GLIDE_TO_CAMERA, cameraSelection);
        tileToGlideToRequest.put(TILE_TO_GLIDE_TO_TILE_ID, currentTiles.getString(0));
        setTileToGlideToV2(tileToGlideToRequest);
        return response;
    }

    public JSONObject setEntityWaitTimeBetweenActivities(JSONObject request) {
        JSONObject response = new JSONObject();

        return response;
    }

    public JSONObject setCameraMode(JSONObject request) {
        JSONObject response = new JSONObject();
        String cameraMode = request.getString("mode");
        mGameModel.getGameState().setCameraMode(cameraMode);
        return response;
    }

    public JSONArray getCameraModes() {
        return mGameModel.getGameState().getCameraModes();
    }

    public JSONArray getAllUnitIDs() {
        List<String> allUnitIDs = mGameModel.getAllUnitIDs();
        return new JSONArray(allUnitIDs);
    }

    public void forcefullyEndTurn() {
        mGameModel.getGameState().setShouldForcefullyEndTurn(true);
    }
}
