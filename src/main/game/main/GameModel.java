package main.game.main;

import javafx.scene.image.Image;
import main.constants.Vector3f;
import main.game.components.*;
import main.game.components.statistics.StatisticsComponent;
import main.game.stores.EntityStore;
import main.game.systems.InputHandler;
import main.game.systems.JSONEventBus;
import main.game.systems.UpdateSystem;
import main.input.InputController;
import main.input.Mouse;
import org.json.JSONArray;
import main.game.entity.Entity;
import main.game.logging.ActivityLogger;
import main.game.map.base.TileMap;
import main.game.queue.SpeedQueue;
import org.json.JSONObject;

import java.util.*;


public class GameModel {
    private JSONEventBus mEventBus = null;
    private TileMap mTileMap = null;
    private SpeedQueue mSpeedQueue = null;
    public ActivityLogger mLogger = null;
    public InputHandler mInputHandler = null;
    public UpdateSystem mSystem = null;
    private GameState mGameState = null;
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
    }




    public boolean spawnUnit(Entity entity, String team, int row, int column) {
        boolean wasPlaced = mTileMap.spawnUnit(entity, row, column);
        if (wasPlaced) {
            mSpeedQueue.enqueue(new Entity[]{ entity }, team);
        }
        return wasPlaced;
    }


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
    public List<String> getAllUnitIDs() {
        return mSpeedQueue.getAllUnitIDs();
    }






































































































































































































    public void setUserSelectedStandby(boolean b) { getGameState().setUserSelectedStandby(b); }
    public boolean isUserSelectedStandby() { return getGameState().isUserSelectedStandby(); }
    public int getSelectedTilesCheckSum() { return getGameState().getSelectedTilesChecksum(); }

    public int getSpeedQueueChecksum() { return mSpeedQueue.getCheckSum(); }
    public JSONArray getAllEntityIDsInTurnQueue() { return getSpeedQueue().getAllEntityIDsInTurnQueue(); }
    public JSONArray getAllEntityIDsPendingTurnInTurnQueue() {
        return getSpeedQueue().getAllEntityIDsPendingTurnInTurnQueue();
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


    public void setSelectedTileIDs(String tileID) { setSelectedTileIDs(new JSONArray().put(tileID)); }
    public void setSelectedTileIDs(JSONArray request) {
        GameState gameState = mGameState;

        // Validate that the request can be placed in the Game State store
        for (int index = 0; index < request.length(); index++) {
            String selectedTileID = request.getString(index);
            Entity tileEntity = EntityStore.getInstance().get(selectedTileID);
            if (tileEntity == null) { return; }
        }

        gameState.setSelectedTileIDs(request);
    }

    public JSONArray getSelectedTiles(GameModel gameModel) { return mGameState.getSelectedTileIDs(); }
    public int getHoveredTilesHash(GameModel gameModel) { return mGameState.getHoveredTilesHash(); }
    public JSONArray getHoveredTiles(GameModel gameModel) { return mGameState.getHoveredTileIDs(); }

    public JSONArray getSelectedUnitsActions() {
        GameState gameState = mGameState;
        JSONArray selectedTiles = gameState.getSelectedTileIDs();
//        mEphemeralArrayResponse.clear();

        JSONArray response = new JSONArray();
        for (int index = 0; index < selectedTiles.length(); index++) {
            String selectedTile = selectedTiles.getString(index);
            Entity entity = EntityStore.getInstance().get(selectedTile);
            TileComponent tile = entity.get(TileComponent.class);
            String unitID = tile.getUnitID();
            Entity unitEntity = EntityStore.getInstance().get(unitID);
            if (unitEntity == null) { continue; }
            StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
            for (String action : statisticsComponent.getOtherAbility()) {
                response.put(action);
            }
            break;
        }

        return response;
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

//    public JSONArray getTilesAtXY(GameModel gameModel, JSONObject request) {
//        GameState gameStateV2 = mGameState;
//        TileMap tileMap = gameModel.getTileMap();
//        JSONArray response = mEphemeralArrayResponse;
//        response.clear();
//
//        try {
//            int radius = request.optInt(GET_TILES_AT_RADIUS, 0);
//            int x = request.getInt(GET_TILES_AT_X);
//            int y = request.getInt(GET_TILES_AT_Y);
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
        String cameraName = request.optString(GET_CAMERA_INFO_CAMERA_ID_KEY, null);

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

    public void getCurrentActiveEntityStatisticsHashCode(JSONObject out) {
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



    public int getCurrentActiveEntityStatisticsHashCode() {
        String entityID = getSpeedQueue().peek();

        if (entityID == null) { return 0; }

        Entity entity = EntityStore.getInstance().get(entityID);
        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
        int hashCode = statisticsComponent.hashCode();

        return hashCode;
    }

    public String getCurrentActiveEntityID() {
        String entityID = getSpeedQueue().peek();
        return entityID;
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

    public JSONArray getAbilitiesOfUnitEntity(String unitID) {
        JSONArray response = new JSONArray();
        response.clear();

        Entity unitEntity = EntityStore.getInstance().get(unitID);
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
        String unitEntityID = request.optString("id", null);
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


    private static final String[] MOVEMENT_RELATED_STATS = new String[] {"move", "speed", "climb", "jump"};


    public JSONObject getMovementStatsOfUnit(String id) {
        JSONObject response = new JSONObject();
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
//        List<JSONObject> selectedTiles = mGameState.getSelectedTiles();
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

    public JSONArray getUnitStatsForMiniUnitInfoPanel(JSONObject request) {
        JSONArray response = new JSONArray();
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

    public JSONArray getEntityIDsAtSelectedTiles() {
        JSONArray response = new JSONArray();

        JSONArray selectedTiles = getGameState().getSelectedTileIDs();
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

    public JSONArray getEntitiesAtSelectedTileIDs() {
        JSONArray response = new JSONArray();

        JSONArray selectedTiles = getGameState().getSelectedTileIDs();
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
//            JSONObject statRequest = new JSONObject();
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
        JSONObject response = new JSONObject();
        response.clear();

        String id = request.getString("id");

        if (id == null) { return response; }
        Entity unitEntity = EntityStore.getInstance().get(id);
        if (unitEntity == null) { return null; }

        IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
        response.put("id", identityComponent.getID());
        response.put(NICKNAME_KEY, identityComponent.getNickname());

        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        if (statisticsComponent != null) {
            response.put("unit", statisticsComponent.getUnit());
        }

        return response;
    }

    public void setStatisticsPanelIsOpen(boolean value) {
        getGameState().setStatisticsInformationPanelOpen(value);
    }
    public void setMovementPanelIsOpen(boolean value) {
        getGameState().setMovementPanelIsOpen(value);
    }

    public void setAbilityPanelIsOpen(boolean value) { getGameState().setAbilityPanelIsOpen(value); }
    public void setGreaterStatisticsInformationPanelOpen(boolean b) { getGameState().setStatisticsInformationPanelOpen(b); }
    public boolean isGreaterStatisticsInformationPanelOpen() { return getGameState().isGreaterStatisticsPanelOpen(); }
    public void setGreaterAbilityInformationPanelOpen(boolean b) { getGameState().setGreaterAbilityInformationPanelOpen(b); }
    public boolean isGreaterAbilityInformationPanelOpen() { return getGameState().isGreaterAbilityInformationPanelOpen(); }



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


    public JSONObject getStatisticsForUnit(JSONObject request) {
        String unitID = request.getString("id");
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

        String unitID = request.getString("id");
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

        String unitID = request.getString("id");
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

        String unitID = request.getString("id");
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
        JSONArray response = new JSONArray();
        response.clear();
        int checksum = gameModel.getSpeedQueue().getAllEntitiesInTurnQueueWithPendingTurnChecksum();
        response.put(checksum);
        return response;
    }

    public JSONArray getAllEntitiesInTurnQueueWithFinishedTurnCheckSum(GameModel gameModel) {
        JSONArray response = new JSONArray();
        response.clear();
        int checksum = gameModel.getSpeedQueue().getAllEntitiesInTurnQueueWithFinishedTurnChecksum();
        response.put(checksum);
        return response;
    }

    public JSONArray getAllEntitiesInTurnQueueCheckSum(GameModel gameModel) {
        JSONArray response = new JSONArray();
        response.clear();
        int checksum = gameModel.getSpeedQueue().getAllEntitiesInTurnQueueChecksum();
        response.put(checksum);
        return response;
    }

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
        response.putAll(unitsPendingTurn);
        return response;
    }

    public JSONArray getAllEntitiesInTurnQueueFinishedTurn() {
        JSONArray response = new JSONArray();
        List<String> unitsPendingTurn = getSpeedQueue().getAllEntitiesInTurnQueueWithPendingTurn();
        response.putAll(unitsPendingTurn);
        return response;
    }



    public JSONArray getAllEntitiesInTurnQueue() {
        JSONArray response = new JSONArray();
        List<String> unitsPendingTurn = getSpeedQueue().getAllEntitiesInTurnQueue();
        response.putAll(unitsPendingTurn);
        return response;
    }


    public JSONArray getEntityTileID(JSONObject request) {
        JSONArray response = new JSONArray();

        String unitID = request.getString("id");
        Entity unitEntity = getEntityWithID(unitID);
        if (unitEntity == null) { return response; }
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        String tileID = movementComponent.getCurrentTileID();
        response.put(tileID);

        return response;
    }
    public JSONArray getCurrentActiveEntityTileID(JSONObject request) {
        JSONArray response = new JSONArray();

        String tileID = getCurrentActiveEntityTileID();
        if (tileID == null) { return response; }
        response.put(tileID);

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

    public void setCameraZoomAPI(JSONObject request) {
        float zoom = request.optFloat("zoom");

        int spriteWidth = mGameState.getOriginalSpriteWidth();
        int spriteHeight = mGameState.getOriginalSpriteHeight();

        int newSpriteWidth = (int) (spriteWidth * zoom);
        int newSpriteHeight = (int) (spriteHeight * zoom);

        mGameState.setSpriteWidth(newSpriteWidth);
        mGameState.setSpriteHeight(newSpriteHeight);
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




}
