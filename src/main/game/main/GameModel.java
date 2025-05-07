package main.game.main;

import javafx.scene.image.Image;
import main.constants.Vector3f;
import main.game.components.*;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.TileComponent;
import main.game.stores.EntityStore;
import main.game.systems.InputHandler;
import main.game.systems.JSONEventBus;
import main.game.systems.UpdateSystem;
import main.input.InputController;
import main.input.Mouse;
import com.alibaba.fastjson2.JSONArray;
import main.game.entity.Entity;
import main.game.logging.ActivityLogger;
import main.game.map.base.TileMap;
import main.game.queue.SpeedQueue;
import com.alibaba.fastjson2.JSONObject;

import java.util.*;


public class GameModel {
    private JSONEventBus mEventBus = null;
    private TileMap mTileMap = null;
    private SpeedQueue mSpeedQueue = null;
    public ActivityLogger mLogger = null;
    public InputHandler mInputHandler = null;
    public UpdateSystem mSystem = null;
    private GameState mGameState = null;
    private GameAssetStore mGameAssetStore = null;
    private boolean mRunning = false;
    public GameModel(JSONObject rawConfigs, JSONArray map) { setup(rawConfigs, map); }
    public void setup(JSONObject rawConfigs, JSONArray map) {
        GameConfigs configs = new GameConfigs(rawConfigs);

        mTileMap = new TileMap(configs, map);
        mGameState = new GameState(configs);

        mGameState.setSpriteWidth(configs.getOnStartupSpriteWidth())
                .setSpriteHeight(configs.getOnStartupSpriteHeight())
                .setOriginalSpriteWidth(configs.getOnStartupSpriteWidth())
                .setOriginalSpriteHeight(configs.getOnStartupSpriteHeight())
                .setMainCameraX(configs.getOnStartupCameraX())
                .setMainCameraY(configs.getOnStartupCameraY())
                .setMainCameraWidth(configs.getOnStartupCameraWidth())
                .setMainCameraHeight(configs.getOnStartupCameraHeight());

        if (configs.setOnStartupCenterCameraOnMap()) {
            Vector3f centerValues = Vector3f.getCenteredVector(
                    0,
                    0,
                    configs.getOnStartupSpriteWidth() * configs.getColumns(),
                    configs.getOnStartupSpriteHeight() * configs.getRows(),
                    configs.getOnStartupCameraWidth(),
                    configs.getOnStartupCameraHeight()
            );
            mGameState.setMainCameraX(mGameState.getMainCameraX() - centerValues.x);
            mGameState.setMainCameraY(mGameState.getMainCameraY() - centerValues.y);
        }

        mLogger = new ActivityLogger();
        mSpeedQueue = new SpeedQueue();;
        mEventBus = new JSONEventBus();
        mInputHandler = new InputHandler(mEventBus);
        mSystem = new UpdateSystem(this);
        mTileMap.designateSpawns(true);
//        designateTypicalSpawns();
    }

//    public JSONArray getSpawnRegions() { return mTileMap.getSpawnRegions(); }

    public JSONObject getSpawnRegionsData() { return mTileMap.getSpawnRegionsData(); }




    public void setDeltaTime(double newDeltaTime) { getGameState().setDeltaTime(newDeltaTime); }


    public void update() {
        if (!mRunning || mSystem == null) { return; }
        mSystem.update(this);
//        mCameraHandler.update(mGameState);
    }

    public void input(InputController ic) {
        mInputHandler.input(mGameState, ic, this);
    }

    public Entity tryFetchingMousedAtTileEntity() {
        Mouse mouse = InputController.getInstance().getMouse();
        int x = (int) mouse.getPosition().x;
        int y = (int) mouse.getPosition().y;
        Entity mousedAt = tryFetchingTileWithXY(x, y);
        return mousedAt;
    }

    public String tryFetchingMousedAtTileID() {
        Mouse mouse = InputController.getInstance().getMouse();
        int x = (int) mouse.getPosition().x;
        int y = (int) mouse.getPosition().y;

        Entity mousedAt = tryFetchingTileWithXY(x, y);
        if (mousedAt == null) { return null; }
        IdentityComponent identityComponent = mousedAt.get(IdentityComponent.class);
        return identityComponent.getID();
    }

    public Entity tryFetchingTileWithXY(int x, int y) {
        int cameraX = mGameState.getMainCameraX();
        int cameraY = mGameState.getMainCameraY();
        int spriteWidth = mGameState.getSpriteWidth();
        int spriteHeight = mGameState.getSpriteHeight();
        int column = (x + cameraX) / spriteWidth;
        int row = (y + cameraY) / spriteHeight;
        return tryFetchingEntityAt(row, column);
    }

    public boolean isRunning() { return mRunning; }
    public void run() { mRunning = true; }
    public void stop() { mRunning = false; }

    public int getRows() { return mTileMap.getRows(); }
    public int getColumns() { return mTileMap.getColumns(); }

    // How much our camera has moved in terms of tiles on the y axis
    public double getVisibleStartOfColumns() {
        int x = mGameState.getMainCameraX();
        int spriteWidth = mGameState.getSpriteWidth();
        return x / (double) spriteWidth;
    }
    // How much our camera has moved in terms of tiles on the x axis on the other end of the screen (width)
    public double getVisibleEndOfColumns() {
        int x = mGameState.getMainCameraX();
        int screenWidth = mGameState.getMainCameraWidth();
        int spriteWidth = mGameState.getSpriteWidth();
        return (double) (x + screenWidth) / spriteWidth;
    }
    public double getVisibleStartOfRows() {
        int y = mGameState.getMainCameraY();
        int spriteHeight = mGameState.getSpriteHeight();
        return y / (double) spriteHeight;
    }
    // How much our camera has moved in terms of tiles on the y axis on the other end of the screen (height)
    public double getVisibleEndOfRows() {
        int y = mGameState.getMainCameraY();
        int screenHeight = mGameState.getMainCameraHeight();
        int spriteHeight = mGameState.getSpriteHeight();
        return (double) (y + screenHeight) / spriteHeight;
    }





    public double getVisibleStartOfColumns(String camera) {
        int x = mGameState.getCameraX(camera);
        int spriteWidth = mGameState.getSpriteWidth();
        return x / (double) spriteWidth;
    }
    // How much our camera has moved in terms of tiles on the x axis on the other end of the screen (width)
    public double getVisibleEndOfColumns(String camera) {
        int x = mGameState.getCameraX(camera);
        int screenWidth = mGameState.getCameraWidth(camera);
        int spriteWidth = mGameState.getSpriteWidth();
        return (double) (x + screenWidth) / spriteWidth;
    }
    public double getVisibleStartOfRows(String camera) {
        int y = mGameState.getCameraY(camera);
        int spriteHeight = mGameState.getSpriteHeight();
        return y / (double) spriteHeight;
    }
    // How much our camera has moved in terms of tiles on the y axis on the other end of the screen (height)
    public double getVisibleEndOfRows(String camera) {
        int y = mGameState.getCameraY(camera);
        int screenHeight = mGameState.getCameraHeight(camera);
        int spriteHeight = mGameState.getSpriteHeight();
        return (double) (y + screenHeight) / spriteHeight;
    }


    public String getCurrentActiveEntityTileID() {
        String unitID = getSpeedQueue().peek();
        Entity unitEntity = EntityStore.getInstance().get(unitID);
        if (unitEntity == null) { return null; }
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        String tileID = movementComponent.getCurrentTileID();
        return tileID;
    }

    public void focusCamerasAndSelectionsOfActiveEntity() {
        String currentActiveEntityTileID = getCurrentActiveEntityTileID();
        focusCamerasAndSelectionsOfActiveEntity(currentActiveEntityTileID);
    }

    public void focusCamerasAndSelectionsOfActiveEntity(String tileID) {
        String currentActiveEntityTileID = getCurrentActiveEntityTileID();
        mGameState.setSelectedTileIDs(tileID);
        mGameState.addTileToGlideTo(currentActiveEntityTileID, mGameState.getMainCameraID());
        mGameState.addTileToGlideTo(currentActiveEntityTileID, mGameState.getSecondaryCameraID());
    }

    public void setSelectedTiles() {
        String currentActiveEntityTileID = getCurrentActiveEntityTileID();

    }



    private static Entity getEntityWithID(String id) { return EntityStore.getInstance().get(id); }
    public TileMap getTileMap() { return mTileMap; }
    public Entity tryFetchingEntityAt(int row, int column) { return mTileMap.tryFetchingEntityAt(row, column); }
    public String tryFetchingTileEntityID(int row, int column) {
        return mTileMap.tryFetchingEntityIDAt(row, column);
    }
    public GameState getGameState() { return mGameState; }
    public SpeedQueue getSpeedQueue() { return mSpeedQueue; }
    public Image getBackgroundWallpaper() { return mSystem.getBackgroundWallpaper(); }
    public JSONEventBus getEventBus() { return mEventBus; }






































































































































































































//    public JSONArray getHoveredTileIDs() { return getGameState().getHoveredTileIDs(); }
    public String getHoveredTileID() { return getGameState().getHoveredTileID(); }

    public boolean isAbilityPanelOpen() { return getGameState().isAbilityPanelOpen(); }
    public boolean isMovementPanelOpen() { return getGameState().isMovementPanelOpen(); }
    public boolean isStatisticsPanelOpen() { return getGameState().isStatisticsPanelOpen(); }
    public void setUserSelectedStandby(boolean b) { getGameState().setUserSelectedStandby(b); }
    public boolean isUserSelectedStandby() { return getGameState().isUserSelectedStandby(); }
    public int getSelectedTilesCheckSum() { return getGameState().getSelectedTilesChecksum(); }

    public int getSpeedQueueChecksum() { return mSpeedQueue.getCheckSum(); }
    public JSONArray getAllEntityIDsInTurnQueue() { return getSpeedQueue().getAllEntityIDsInTurnQueue(); }
    public JSONArray getAllEntityIDsPendingTurnInTurnQueue() {
        return getSpeedQueue().getAllEntityIDsPendingTurnInTurnQueue();
    }
    public String getSelectedEntityID() {
        JSONArray selectedTilesIDs = getGameState().getSelectedTileIDs();
        if (selectedTilesIDs.isEmpty()) { return null; }
        String selectedTileID = selectedTilesIDs.getString(0);
        Entity tileEntity = getEntityWithID(selectedTileID);
        TileComponent tileComponent = tileEntity.get(TileComponent.class);
        String unitEntityID = tileComponent.getUnitID();
        Entity unitEntity = getEntityWithID(unitEntityID);
        if (unitEntity == null) { return null; }
        return unitEntityID;
    }
    public int getSelectedEntityHash() {
        JSONArray selectedTilesIDs = getGameState().getSelectedTileIDs();
        if (selectedTilesIDs.isEmpty()) { return 0; }
        String selectedTileID = selectedTilesIDs.getString(0);
        Entity tileEntity = getEntityWithID(selectedTileID);
        TileComponent tileComponent = tileEntity.get(TileComponent.class);
        String unitEntityID = tileComponent.getUnitID();
        Entity unitEntity = getEntityWithID(unitEntityID);
        if (unitEntity == null) { return 0; }
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        int hash = statisticsComponent.hashCode();
        return hash;
    }





    public static final String GET_CURRENT_UNIT_TURN_STATUS_HAS_MOVED = "HasMoved";
    public static final String GET_CURRENT_UNIT_TURN_STATUS_HAS_ACTED = "HasActed";
    public JSONObject getCurrentUnitTurnStatus() {
        SpeedQueue speedQueue = getSpeedQueue();
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

    public JSONObject getSelectedUnitsTurnState() {
        JSONObject response = new JSONObject();

        String unitEntityID = getSpeedQueue().peek(); // tile.getUnitID();
        Entity unitEntity = EntityStore.getInstance().get(unitEntityID);

        if (unitEntity == null) { return  response; }

        boolean isOwnerOfCurrentTurn = getSpeedQueue().peek().equalsIgnoreCase(unitEntityID);

        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        ActionsComponent actionsComponentComponent = unitEntity.get(ActionsComponent.class);

        response.put(GET_CURRENT_UNIT_TURN_STATUS_HAS_MOVED, actionsComponentComponent.hasFinishedMoving());
        response.put(GET_CURRENT_UNIT_TURN_STATUS_HAS_ACTED, actionsComponentComponent.hasFinishedUsingAbility());
        response.put("is_current_turn", isOwnerOfCurrentTurn);
        return response;
    }


    public void setSelectedTileIDs(String tileID) { 
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(tileID);
        setSelectedTileIDs(jsonArray); 
    }
    public void setSelectedTileIDs(JSONArray request) {
        GameState gameState = mGameState;

        // Validate that the request can be placed in the Game State store
        for (int index = 0; index < request.size(); index++) {
            String selectedTileID = request.getString(index);
            Entity tileEntity = EntityStore.getInstance().get(selectedTileID);
            if (tileEntity == null) { return; }
        }

        gameState.setSelectedTileIDs(request);
    }

    public JSONArray getSelectedTiles(GameModel gameModel) { return mGameState.getSelectedTileIDs(); }
    public int getHoveredTilesHash(GameModel gameModel) { return mGameState.getHoveredTilesHash(); }
    public JSONArray getHoveredTiles(GameModel gameModel) { return mGameState.getHoveredTileIDs(); }
    public static final String UPDATE_STRUCTURE_MODE = "set_structure_operation";
    public static final String UPDATE_STRUCTURE_ADD_MODE = "add_structure";
    public static final String UPDATE_STRUCTURE_DELETE_MODE = "delete_structure";
    public static final String UPDATE_STRUCTURE_ASSET = "structure_asset";
    public static final String UPDATE_STRUCTURE_HEALTH = "update_structure_health";
    public void updateStructures(GameModel gameModel, JSONObject request) {
        try {

            String asset = request.getString(UPDATE_STRUCTURE_ASSET);
            String mode = request.getString(UPDATE_STRUCTURE_MODE);
            int health = request.getIntValue(UPDATE_STRUCTURE_HEALTH);

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


        int radius = request.getIntValue(GET_TILES_AT_RADIUS, 0);
        int row = request.getIntValue(GET_TILES_AT_ROW);
        int column = request.getIntValue(GET_TILES_AT_COLUMN);

        TileComponent tile = tileMap.tryFetchingTileAt(row, column);
        if (tile == null) { return null; }

        // Get tiles for the specified radius
        JSONArray tiles = new JSONArray();
        for (row = tile.getRow() - radius; row <= tile.getRow() + radius; row++) {
            for (column = tile.getColumn() - radius; column <= tile.getColumn() + radius; column++) {
                TileComponent adjacentTile = tileMap.tryFetchingTileAt(row, column);
                if (adjacentTile == null) { continue; }
                tiles.add(adjacentTile);
            }
        }

        return tiles;
    }

    public static final String GET_TILES_AT_X = "x";
    public static final String GET_TILES_AT_Y = "y";

//    public JSONArray getTilesAtXY(GameModel gameModel, JSONObject request) {
//        GameState gameStateV2 = mGameState;
//        TileMap tileMap = gameModel.getTileMap();
//        JSONArray response = mEphemeralArrayResponse;
//        response.clear();
//
//        try {
//            int radius = request.getIntValue(GET_TILES_AT_RADIUS, 0);
//            int x = request.getIntValue(GET_TILES_AT_X);
//            int y = request.getIntValue(GET_TILES_AT_Y);
//
//            int cameraX = gameStateV2.getMainCameraX();
//            int cameraY = gameStateV2.getMainCameraY();
//            int spriteWidth = gameStateV2.getSpriteWidth();
//            int spriteHeight = gameStateV2.getSpriteHeight();
//            int column = (x + cameraX) / spriteWidth;
//            int row = (y +cameraY) / spriteHeight;
//
//            TileComponent tile = tileMap.tryFetchingTileAt(row, column);
//            if (tile == null) { return null; }
//
//            // Get tiles for the specified radius
//            for (row = tile.getRow() - radius; row <= tile.getRow() + radius; row++) {
//                for (column = tile.getColumn() - radius; column <= tile.getColumn() + radius; column++) {
//                    TileComponent adjacentTile = tileMap.tryFetchingTileAt(row, column);
//                    if (adjacentTile == null) { continue; }
//                    response.put(adjacentTile);
//                }
//            }
//        } catch (Exception ex) {
//            response.clear();
//        }
//
//        return response;
//    }




    public static final String UPDATE_SPAWN_MODE = "spawn.mode";
    public static final String UPDATE_SPAWNER_OPERATION_ADD = "add.spawn";
    public static final String UPDATE_SPAWNER_OPERATION_DELETE = "delete.spawn";
    public static final String UPDATE_SPAWN_OPERATION_ON_TEAM = "spawn.team";
    public void updateSpawners(GameModel gameModel, JSONObject request) {

        try {
//            GameState gameState = mGameState;
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
            int amount = request.getIntValue(TileComponent.LAYER_HEIGHT);
//            String asset = (String) request.get(TileComponent.LAYER_ASSET);

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
//    private static void stripTerrain(TileMap tileMap, List<JSONObject> selectedTiles, String type, String asset) {
//        if (selectedTiles.isEmpty()) { return; }
//        // Get starting tile
//        JSONObject tileJson = selectedTiles.get(0);
//        TileComponent tile = (TileComponent) tileJson;
//        int heightOfStartingTile = tile.getModifiedElevation();
//        // Fill all tiles
//        Queue<TileComponent> queue = new LinkedList<>();
//        queue.add(tile);
//        Set<TileComponent> set = new HashSet<>();
//        List<TileComponent> tilesToUpdate = new ArrayList<>();
//
//        while (!queue.isEmpty()) {
//            TileComponent traversedTile = queue.poll();
//            // If we've seen he current tile, skip
//            if (set.contains(traversedTile)) { continue; }
//            // If the current tile is HIGHER than starting tile, skip
//            if (heightOfStartingTile < traversedTile.getModifiedElevation() && traversedTile != tile) { continue; }
//            set.add(traversedTile);
//            // Mark tile to process
//            tilesToUpdate.add(traversedTile);
//            // Collect more tiles
//            for (Direction direction : Direction.values()) {
//                int row = traversedTile.getRow() + direction.y;
//                int column = traversedTile.getColumn() + direction.x;
//                TileComponent adjacentNeighborTile = tileMap.tryFetchingTileAt(row, column);
//                if (adjacentNeighborTile == null) { continue; }
//                queue.add(adjacentNeighborTile);
//            }
//        }
//        heightOfStartingTile += 1;
//        for (TileComponent tileToUpdate : tilesToUpdate) {
//            int heightOfTileToUpdate = tileToUpdate.getModifiedElevation();
//            int heightDelta = heightOfStartingTile - heightOfTileToUpdate;
//            if (heightDelta <= 0) { continue; }
//            tileToUpdate.addLayer(type, heightDelta, asset);
//        }
//    }

//    private static void tileFillToLevel(TileMap tileMap, List<JSONObject> selectedTiles, String type, String asset) {
//        if (selectedTiles.isEmpty()) { return; }
//        JSONObject tileJson = selectedTiles.get(0);
//        TileComponent tile = (TileComponent) tileJson;
//        int heightOfStartingTile = tile.getModifiedElevation();
//        // Fill all tiles
//        Queue<TileComponent> queue = new LinkedList<>();
//        queue.add(tile);
//        Set<TileComponent> set = new HashSet<>();
//        List<TileComponent> tilesToUpdate = new ArrayList<>();
//
//        while (!queue.isEmpty()) {
//            TileComponent traversedTile = queue.poll();
//            // If we've seen he current tile, skip
//            if (set.contains(traversedTile)) { continue; }
//            // If the current tile is HIGHER than starting tile, skip
//            if (heightOfStartingTile < traversedTile.getModifiedElevation() && traversedTile != tile) { continue; }
//            set.add(traversedTile);
//            // Mark tile to process
//            tilesToUpdate.add(traversedTile);
//            // Collect more tiles
//            for (Direction direction : Direction.values()) {
//                int row = traversedTile.getRow() + direction.y;
//                int column = traversedTile.getColumn() + direction.x;
//                TileComponent adjacentNeighborTile = tileMap.tryFetchingTileAt(row, column);
//                if (adjacentNeighborTile == null) { continue; }
//                queue.add(adjacentNeighborTile);
//            }
//        }
//        heightOfStartingTile += 1;
//        for (TileComponent tileToUpdate : tilesToUpdate) {
//            int heightOfTileToUpdate = tileToUpdate.getModifiedElevation();
//            int heightDelta = heightOfStartingTile - heightOfTileToUpdate;
//            if (heightDelta <= 0) { continue; }
//            tileToUpdate.addLayer(type, heightDelta, asset);
//        }
//    }
//


    public void setEndTurn() { getGameState().setShouldForcefullyEndTurn(true); }

    public void setTileToGlideTo(JSONObject request) {
        GameState gameState = mGameState;

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

        GameState gameState = mGameState;
        gameState.addTileToGlideTo(tileID, camera);

        response.put("status_code", "success");
        return response;
    }

    public void setTileToGlideTo(String tileID, String camera) {

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
        String cameraName = request.getString(GET_CAMERA_INFO_CAMERA_ID_KEY);

        if (cameraName == null) { return response; }

        int x = mGameState.getCameraX(cameraName);
        int y = mGameState.getCameraY(cameraName);
        int width = mGameState.getCameraWidth(cameraName);
        int height = mGameState.getCameraHeight(cameraName);

        response.put("x", x);
        response.put("y", y);
        response.put("width", width);
        response.put("height", height);
        response.put("camera", cameraName);

        return response;
    }



    public JSONObject getCurrentActiveEntityData() {
        JSONObject response = new JSONObject();
        String entityID = getSpeedQueue().peek();
        if (entityID == null) { return response; }

        response.put("id", entityID);
        return response;
    }

    public JSONObject getCurrentTurnsEntity() {
        JSONObject response = new JSONObject();
        String entityID = getSpeedQueue().peek();
        if (entityID == null) { return response; }

        response.put("id", entityID);
        return response;
    }

    public void setCurrentActiveEntityAbilityToDefault() {
        String entityID = getSpeedQueue().peek();
        if (entityID == null) { return; }

        Entity entity = getEntityWithID(entityID);
        AbilityComponent abilityComponent = entity.get(AbilityComponent.class);
        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
        abilityComponent.stageAbility(statisticsComponent.getBasicAbility());;
    }

    public void getActiveEntityStatisticsComponentHash(JSONObject out) {
        String entityID = getSpeedQueue().peek();

        out.clear();
        if (entityID == null || entityID.isEmpty()) { return; }

        Entity entity = EntityStore.getInstance().get(entityID);
        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
        int statsChecksum = statisticsComponent.hashCode();
        MovementComponent movementComponent = entity.get(MovementComponent.class);
        int moveChecksum = movementComponent.getChecksum();
        IdentityComponent identityComponent = entity.get(IdentityComponent.class);
        String nickname = identityComponent.getNickname();

        out.put("nickname", nickname);
        out.put("id", entityID);
        out.put("checksum", statsChecksum);
    }



    public int getActiveEntityStatisticsComponentHash() {
        String entityID = getSpeedQueue().peek();

        if (entityID == null) { return 0; }

        Entity entity = EntityStore.getInstance().get(entityID);
        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
        int hashCode = statisticsComponent.hashCode();

        return hashCode;
    }

    public String getActiveEntityID() {
        String entityID = getSpeedQueue().peek();
        return entityID;
    }

    public int getActiveEntityAbilityComponentHash() {
        String entityID = getSpeedQueue().peek();
        if (entityID == null) { return -1; }
        Entity entity = getEntityWithID(entityID);
        AbilityComponent abilityComponent = entity.get(AbilityComponent.class);
        int hashCode = abilityComponent.hashCode();
        return hashCode;
    }

    public int getSpecificEntityAbilityComponentHash(String entityID) {
        Entity entity = getEntityWithID(entityID);
        if (entity == null) { return -1; }
        AbilityComponent abilityComponent = entity.get(AbilityComponent.class);
        int hashCode = abilityComponent.hashCode();
        return hashCode;
    }

    public int getSpecificEntityStatisticsComponentHash(String entityID) {
        Entity entity = EntityStore.getInstance().get(entityID);
        if (entity == null) { return -1; }
        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
        int hashCode = statisticsComponent.hashCode();
        return hashCode;
    }

    public String getTargetedUnitFromSpecificEntityID(String entityID) {
        // Get the tile being targeted by the current active entity
        Entity entity = getEntityWithID(entityID);
        if (entity == null) { return null; }

        AbilityComponent abilityComponent = entity.get(AbilityComponent.class);
        String targetTileID = abilityComponent.getStagedTileTargeted();
        if (targetTileID == null) { return null; }
        // Get the entity on targeted tile if possible
        Entity targeTileEntity = getEntityWithID(targetTileID);
        TileComponent tileComponent = targeTileEntity.get(TileComponent.class);
        String unitID = tileComponent.getUnitID();
        return unitID;
    }

    public String getActiveEntityTargetedUnitID() {
        String entityID = getSpeedQueue().peek();
        if (entityID == null) { return null; }
        // Get the tile being targeted by the current active entity
        Entity entity = getEntityWithID(entityID);
        AbilityComponent abilityComponent = entity.get(AbilityComponent.class);
        String targetTileID = abilityComponent.getStagedTileTargeted();
        if (targetTileID == null) { return null; }
        // Get the entity on targeted tile if possible
        Entity targeTileEntity = getEntityWithID(targetTileID);
        TileComponent tileComponent = targeTileEntity.get(TileComponent.class);
        String unitID = tileComponent.getUnitID();
        return unitID;
    }




    public void getEntityOnSelectedTilesChecksum(GameModel gameModel, JSONObject out) {
        JSONArray selectedTiles = mGameState.getSelectedTileIDs();

        out.clear();
        if (selectedTiles == null || selectedTiles.isEmpty()) { return; }

        // Get the first selected tile
        String selectedTileID = selectedTiles.getString(0);
        Entity selectedTileEntity = getEntityWithID(selectedTileID);
        if (selectedTileEntity == null) { return; }

        // Get the unit on the selected tile
        TileComponent tile = selectedTileEntity.get(TileComponent.class);
        String unitEntityID = tile.getUnitID();
        Entity unitEntity = getEntityWithID(unitEntityID);
        if (unitEntity == null) { return; }

        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        int statsChecksum = statisticsComponent.hashCode();
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        int moveChecksum = movementComponent.getChecksum();
        IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
        String nickname = identityComponent.getNickname();
        int selectedTilesChecksum = mGameState.getSelectedTilesChecksum();

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



    public static final String SET_ACTION_OF_UNIT_OF_CURRENT_TURN = "set.action.of.unit.of.current.turn";

    public void stageAbilityForUnit(String id, String action) {

        Entity unitEntity = EntityStore.getInstance().get(id);
        if (unitEntity == null) { return; }

        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
        abilityComponent.stageAbility(action);
    }

    public void stageAbilityForUnit(JSONObject request) {

        String unitID = request.getString("id");
        String unitAction = request.getString("ability");

        Entity unitEntity = EntityStore.getInstance().get(unitID);
        if (unitEntity == null) { return; }

        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
        abilityComponent.stageAbility(unitAction);
    }

    // EXPERIMENTAL, should this really consume the state like this
    public boolean consumeShouldAutomaticallyGoToHomeControls() {
        boolean shouldAutomaticallyGoToHomeControls = getGameState().shouldAutomaticallyGoToHomeControls();
        mGameState.setAutomaticallyGoToHomeControls(false);
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

    public JSONArray getEntitiesAtSelectedTileIDs() {
        JSONArray response = new JSONArray();

        JSONArray selectedTiles = getGameState().getSelectedTileIDs();
        for (int index = 0; index < selectedTiles.size(); index++) {
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
            response.add(unitData);
        }

        return response;
    }

    public void updateIsStatisticsPanelOpen(boolean b) { getGameState().updateIsStatisticsPanelOpen(b); }
    public void updateIsMovementPanelOpen(boolean b) {
        getGameState().updateIsMovementPanelOpen(b);
    }
    public void updateIsAbilityPanelOpen(boolean b) { getGameState().updateIsAbilityPanelOpen(b); }
    public void updateIsGreaterStatisticsPanelOpen(boolean b) { getGameState().updateIsGreaterStatisticsPanelOpen(b); }
    public void updateIsGreaterAbilityPanelOpen(boolean b) { getGameState().updateIsGreaterAbilityPanelOpen(b); }
    public void updateIsDamageFromPreviewPanelOpen(boolean b) { getGameState().updateIsDamageFromPreviewPanelOpen(b); }
    public void updateIsDamageToPreviewPanelOpen(boolean b) { getGameState().updateIsDamageToPreviewPanelOpen(b); }



    public boolean isDamageFromPreviewPanelOpen() { return getGameState().isDamageFromPreviewPanelOpen(); }
    public boolean isDamageToPreviewPanelOpen() { return getGameState().isDamageToPreviewPanelOpen(); }




    public boolean shouldStatisticsPanelOpen() { return getGameState().shouldOpenStatisticsPanel(); }
    public boolean shouldMovementPanelOpen() {
        return getGameState().shouldOpenMovementPanel();
    }
    public boolean shouldAbilityPanelOpen() { return getGameState().shouldOpenAbilityPanel(); }
    public boolean shouldGreaterStatisticsPanelOpen() { return getGameState().shouldOpenGreaterStatisticsPanel(); }
    public boolean shouldGreaterAbilityPanelOpen() { return getGameState().shouldOpenGreaterAbilityPanel(); }

    public void triggerOpenGreaterAbilityPanel() { getGameState().triggerOpenGreaterAbilityPanel(); }


    public boolean isGreaterStatisticsInformationPanelOpen() { return getGameState().isGreaterStatisticsPanelOpen(); }
    public boolean isGreaterAbilityInformationPanelOpen() { return getGameState().isGreaterAbilityPanelOpen(); }





    public void getTurnQueueChecksums(JSONObject out) {
        int finishedCheckSum = getSpeedQueue().getAllEntitiesInTurnQueueWithFinishedTurnChecksum();
        int pendingCheckSum = getSpeedQueue().getAllEntitiesInTurnQueueWithPendingTurnChecksum();
        int allCheckSum = getSpeedQueue().getAllEntitiesInTurnQueueChecksum();

        out.clear();
        out.put("finished", finishedCheckSum);
        out.put("pending", pendingCheckSum);
        out.put("all", allCheckSum);
    }

    public JSONArray getAllEntitiesInTurnQueuePendingTurn() {
        JSONArray response = new JSONArray();
        List<String> unitsPendingTurn = getSpeedQueue().getAllEntitiesInTurnQueueWithPendingTurn();
        response.addAll(unitsPendingTurn);
        return response;
    }


    public JSONArray getAllEntitiesInTurnQueue() {
        JSONArray response = new JSONArray();
        List<String> unitsPendingTurn = getSpeedQueue().getAllEntitiesInTurnQueue();
        response.addAll(unitsPendingTurn);
        return response;
    }


    public JSONArray getEntityTileID(JSONObject request) {
        JSONArray response = new JSONArray();

        String unitID = request.getString("id");
        Entity unitEntity = getEntityWithID(unitID);
        if (unitEntity == null) { return response; }
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        String tileID = movementComponent.getCurrentTileID();
        response.add(tileID);

        return response;
    }
    public JSONArray getCurrentActiveEntityTileID(JSONObject request) {
        JSONArray response = new JSONArray();

        String tileID = getCurrentActiveEntityTileID();
        if (tileID == null) { return response; }
        response.add(tileID);

        return response;
    }

    public JSONObject getStatisticsForEntity(JSONObject request) {
        JSONObject response = new JSONObject();
        String entityID = request.getString("id");
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
        for (String key : statisticsComponent.getOtherAbility()) { abilities.add(key); }
        response.put("abilities", abilities);
        response.put("basic_ability", statisticsComponent.getBasicAbility());
        response.put("passive_ability", statisticsComponent.getPassiveAbility());
        response.put("other_ability", statisticsComponent.getOtherAbility());

        JSONObject tags = new JSONObject();
        keys = statisticsComponent.getTags();
        for (String key : keys) {
            int duration = statisticsComponent.getTagDuration(key);

            JSONObject tag = new JSONObject();
            tag.put("name", key);
            tag.put("duration", duration);
            tags.put(key, tag);
        }
        response.put("tags", tags);

        AbilityComponent abilityComponent = entity.get(AbilityComponent.class);
        String selectedAbility = abilityComponent.getAbility();
        if (selectedAbility != null) {
            response.put("selected_ability", selectedAbility);
        }

        return response;
    }

    public void setConfigurableStateGameplayHudIsVisible(GameModel gameModel, boolean value) {
        mGameState.setConfigurableStateGameplayHudIsVisible(value);
    }

    public boolean getConfigurableStateGameplayHudIsVisible() {
        return getGameState().getConfigurableStateGameplayHudIsVisible();
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
        GameState gameState = mGameState;

    }

    public JSONObject focusCamerasAndSelectionsOnActiveEntity(JSONObject request) {
        JSONObject response = new JSONObject();

        JSONArray currentTiles = getCurrentActiveEntityTileID(request);
//        setSelectedTileIDs(this, currentTiles);

        JSONObject cameraInfoRequest = getCameraInfo(request);

        JSONObject tileToGlideToRequest = new JSONObject();
        tileToGlideToRequest.put(TILE_TO_GLIDE_TO_CAMERA, cameraInfoRequest.getString("camera"));
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
        mGameState.setCameraMode(cameraMode);
        return response;
    }

    public JSONArray getCameraModes() {
        return mGameState.getCameraModes();
    }


    public void forcefullyEndTurn() {
        mGameState.setShouldForcefullyEndTurn(true);
    }

    /**
     * Attempts to place a unit on a tile using the provided {@link JSONObject} data.
     *
     * <p>The input JSON object must include the following fields:</p>
     * <ul>
     *   <li><b>"unit_id"</b> (String): The unique identifier of the unit to be placed.</li>
     *   <li><b>"tile_id"</b> (String): The unique identifier of the destination tile.</li>
     *   <li><b>"team_id"</b> (String, optional): The team to which the unit belongs. Defaults to {@code "neutral"} if absent.</li>
     * </ul>
     *
     * <p>Example input:</p>
     * <pre>{@code
     * {
     *   "unit_id": "fire_dragon_342423-522452-52662-252432",
     *   "tile_id": "4_2_43242-2555223-5234324-25324",
     *   "team_id": "Enemy"
     * }
     * }</pre>
     *
     * @param input the JSON object containing placement data; must not be {@code null}
     * @return {@code true} if the unit was successfully placed and queued; {@code false} if placement failed
     */
    public boolean setUnitSpawn(JSONObject input) {
        String tileID = input.getString("tile_id");
        String unitID = input.getString("unit_id");
        String teamID = (String) input.getOrDefault("team_id", "neutral");

        Entity tileEntity = getEntityWithID(tileID);
        if (tileEntity == null) { return false; }

        Entity unitEntity = getEntityWithID(unitID);
        if (unitEntity == null) { return false; }

        TileComponent tile = tileEntity.get(TileComponent.class);
        if (tile.isNotNavigable()) { return false; }

        tile.setUnit(unitID);

        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        movementComponent.stageTarget(tileID);
        movementComponent.commit();

        mSpeedQueue.add(unitID);
        mGameState.addUnitToTeam(unitID, teamID);

        return true;
    }
}
