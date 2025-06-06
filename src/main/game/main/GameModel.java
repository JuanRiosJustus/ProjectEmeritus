package main.game.main;

import javafx.scene.image.Image;
import main.constants.Vector3f;
import main.game.components.*;
import main.game.components.animation.AnimationTrack;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.StructureComponent;
import main.game.components.tile.TileComponent;
import main.game.pathing.lineofsight.PathingAlgorithms;
import main.game.stores.AbilityTable;
import main.game.stores.EntityStore;
import main.game.systems.*;
import main.game.systems.combat.CombatSystem;
import main.game.systems.texts.FloatingTextSystem;
import main.input.InputController;
import main.input.Mouse;
import com.alibaba.fastjson2.JSONArray;
import main.game.entity.Entity;
import main.game.logging.ActivityLogger;
import main.game.map.base.TileMap;
import main.game.queue.SpeedQueue;
import com.alibaba.fastjson2.JSONObject;
import main.logging.EmeritusLogger;
import main.utils.StringUtils;

import java.util.*;

import static main.game.main.GameAPI.GET_CURRENT_UNIT_TURN_STATUS_HAS_ACTED;
import static main.game.main.GameAPI.GET_CURRENT_UNIT_TURN_STATUS_HAS_MOVED;


public class GameModel {
    private static final EmeritusLogger mLogger = EmeritusLogger.create(GameModel.class);
    private JSONEventBus mEventBus = null;
    private TileMap mTileMap = new TileMap();
//    private GameQueue mSpeedQueue = null;
    private SpeedQueue mSpeedQueue = null;
//    private InitiativeQueue mInitiativeQueue = null;
    public ActivityLogger mActivityLogger = null;
    public InputHandler mInputHandler = null;
    public UpdateSystem mSystem = null;
    private GameState mGameState = null;
    private Random mRandom = new Random();

    private final PathingAlgorithms algorithm = new PathingAlgorithms();
    private GameAssetStore mGameAssetStore = null;
    private boolean mRunning = false;
    public GameModel() { }
    public GameModel(JSONObject rawConfigs, JSONArray map) { setup(rawConfigs, map); }
    public void setup(JSONObject rawConfigs, JSONArray map) {
        GameConfigs configs = new GameConfigs(rawConfigs);

        mTileMap = new TileMap(configs, map);
        mGameState = new GameState(configs);

        mGameState
//                .setSpriteWidth(configs.getOnStartupSpriteWidth())
//                .setSpriteHeight(configs.getOnStartupSpriteHeight())
//                .setOriginalSpriteWidth(configs.getOnStartupSpriteWidth())
//                .setOriginalSpriteHeight(configs.getOnStartupSpriteHeight())
                .setViewportX(configs.getOnStartupCameraX())
                .setViewportY(configs.getOnStartupCameraY())
                .setViewportWidth(configs.getViewportWidth())
                .setViewportHeight(configs.getViewportHeight());

        if (configs.setOnStartupCenterCameraOnMap()) {
            Vector3f centerValues = Vector3f.getCenteredVector(
                    0,
                    0,
                    configs.getOnStartupSpriteWidth() * configs.getMapGenerationColumns(),
                    configs.getOnStartupSpriteHeight() * configs.getMapGenerationRows(),
                    configs.getViewportWidth(),
                    configs.getViewportHeight()
            );
            mGameState.setViewportX(mGameState.getViewportX() - centerValues.x);
            mGameState.setViewportY(mGameState.getViewportY() - centerValues.y);
        }

        mActivityLogger = new ActivityLogger();
//        mSpeedQueue = new SpeedQueue();
//        mInitiativeQueue = new InitiativeQueue();
        mSpeedQueue = new SpeedQueue();
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
        int cameraX = mGameState.getViewportX();
        int cameraY = mGameState.getViewportY();
        int spriteWidth = mGameState.getSpriteWidth();
        int spriteHeight = mGameState.getSpriteHeight();
        if (spriteWidth == 0) {
            System.out.println("too");
        }
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
        int x = mGameState.getViewportX();
        int spriteWidth = mGameState.getSpriteWidth();
        return x / (double) spriteWidth;
    }
    // How much our camera has moved in terms of tiles on the x axis on the other end of the screen (width)
    public double getVisibleEndOfColumns() {
        int x = mGameState.getViewportX();
        int screenWidth = mGameState.getMainCameraWidth();
        int spriteWidth = mGameState.getSpriteWidth();
        return (double) (x + screenWidth) / spriteWidth;
    }
    public double getVisibleStartOfRows() {
        int y = mGameState.getViewportY();
        int spriteHeight = mGameState.getSpriteHeight();
        return y / (double) spriteHeight;
    }
    // How much our camera has moved in terms of tiles on the y axis on the other end of the screen (height)
    public double getVisibleEndOfRows() {
        int y = mGameState.getViewportY();
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
    public Entity tryFetchingEntityAt(int row, int column) {
        String tileID = mTileMap.tryFetchingTileID(row, column);
        return getEntityWithID(tileID);
    }
    public String tryFetchingTileEntityID(int row, int column) {
        return mTileMap.tryFetchingTileID(row, column);
    }
    public GameState getGameState() { return mGameState; }
    public SpeedQueue getSpeedQueue() { return mSpeedQueue; }
//    public GameQueue getInitiativeQueue() { return mSpeedQueue; }
    public Image getBackgroundWallpaper() { return mSystem.getBackgroundWallpaper(); }
    public JSONEventBus getEventBus() { return mEventBus; }



































































































































































































    public JSONObject getHoveredTile() {
        String hoveredTileID = getHoveredTileID();
        if (hoveredTileID == null) { return null; }

        Entity entity = getEntityWithID(hoveredTileID);
        TileComponent tile = entity.get(TileComponent.class);

        String structureID = tile.getStructureID();
        entity = getEntityWithID(structureID);
        JSONObject structure = null;
        if (entity != null) {
            StructureComponent structureComponent = entity.get(StructureComponent.class);
            String name = structureComponent.getName();

            structure = new JSONObject();
            structure.put("asset", name);
        }

        JSONObject hoveredTile = new JSONObject();
        hoveredTile.put("row", tile.getRow());
        hoveredTile.put("column", tile.getColumn());
        hoveredTile.put("base_elevation", tile.getBaseElevation());
        hoveredTile.put("modified_elevation", tile.getModifiedElevation());
        hoveredTile.put("layers", tile.getLayers());
        hoveredTile.put("structure", structure);


        return hoveredTile;
    }







    //    public JSONArray getHoveredTileIDs() { return getGameState().getHoveredTileIDs(); }
    public String getHoveredTileID() { return getGameState().getHoveredTileID(); }

    public boolean isAbilityPanelOpen() { return getGameState().isAbilityPanelOpen(); }
    public boolean isMovementPanelOpen() { return getGameState().isMovementPanelOpen(); }
    public boolean isStatisticsPanelOpen() { return getGameState().isStatisticsPanelOpen(); }
    public void setUserSelectedStandby(boolean b) { getGameState().setUserSelectedStandby(b); }
    public boolean isUserSelectedStandby() { return getGameState().isUserSelectedStandby(); }
    public int getSelectedTilesCheckSum() { return getGameState().getSelectedTilesChecksum(); }

//    public int getSpeedQueueChecksum() { return Objects.hashCode(getInitiativeQueue()); }
//    public JSONArray getAllEntityIDsPendingTurnInTurnQueue() {
//        return new JSONArray(getInitiativeQueue().getTurnOrder());// getInitiativeQueue().getAllEntityIDsPendingTurnInTurnQueue();
//    }
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




    public JSONArray getAllEntities() { return mGameState.getAllUnits(); }
    public JSONArray getAllEnemyEntities(String activeEntityID) {
        JSONArray allEntities = mGameState.getAllUnits();
        String currentEntityTeam = mGameState.getTeam(activeEntityID);
        JSONArray result = new JSONArray();
        for (int i = 0; i < allEntities.size(); i++) {
            String entityID = allEntities.getString(i);
            String team = mGameState.getTeam(entityID);
            boolean isSameTeam = currentEntityTeam.equalsIgnoreCase(team);
            if (isSameTeam) { continue; }
            result.add(entityID);
        }
        return result;
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

//    public static final String GET_TILES_AT_RADIUS = "radius";
//    public static final String GET_TILES_AT_ROW = "row";
//    public static final String GET_TILES_AT_COLUMN = "column";
//    public JSONArray getTilesAtRowColumn(GameModel gameModel, JSONObject request) {
//        TileMap tileMap = gameModel.getTileMap();
//
//
//        int radius = request.getIntValue(GET_TILES_AT_RADIUS, 0);
//        int row = request.getIntValue(GET_TILES_AT_ROW);
//        int column = request.getIntValue(GET_TILES_AT_COLUMN);
//
//        TileComponent tile = tileMap.tryFetchingTileAt(row, column);
//        if (tile == null) { return null; }
//
//        // Get tiles for the specified radius
//        JSONArray tiles = new JSONArray();
//        for (row = tile.getRow() - radius; row <= tile.getRow() + radius; row++) {
//            for (column = tile.getColumn() - radius; column <= tile.getColumn() + radius; column++) {
//                TileComponent adjacentTile = tileMap.tryFetchingTileAt(row, column);
//                if (adjacentTile == null) { continue; }
//                tiles.add(adjacentTile);
//            }
//        }
//
//        return tiles;
//    }

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
//        String entityID = gameModel.getInitiativeQueue().peekV2();
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





//    public void getTurnQueueChecksums(JSONObject out) {
//        int finishedCheckSum = getInitiativeQueue().getAllEntitiesInTurnQueueWithFinishedTurnChecksum();
//        int pendingCheckSum = getInitiativeQueue().getAllEntitiesInTurnQueueWithPendingTurnChecksum();
//        int allCheckSum = getInitiativeQueue().getAllEntitiesInTurnQueueChecksum();
//
//        out.clear();
//        out.put("finished", finishedCheckSum);
//        out.put("pending", pendingCheckSum);
//        out.put("all", allCheckSum);
//    }

//    public JSONArray getAllEntitiesInTurnQueuePendingTurn() {
//        JSONArray response = new JSONArray();
//        List<String> unitsPendingTurn = getInitiativeQueue().getAllEntitiesInTurnQueueWithPendingTurn();
//        response.addAll(unitsPendingTurn);
//        return response;
//    }
//
//
//    public JSONArray getAllEntitiesInTurnQueue() {
//        JSONArray response = new JSONArray();
//        List<String> unitsPendingTurn = getInitiativeQueue().getAllEntitiesInTurnQueue();
//        response.addAll(unitsPendingTurn);
//        return response;
//    }


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
     * Attempts to place a unit on a tile using the provided {@link JSONObject} input.
     *
     * <p>This method will:</p>
     * <ul>
     *   <li>Resolve the destination tile using <b>"tile_id"</b> or fallback to <b>"row"</b> and <b>"column"</b>.</li>
     *   <li>Stage and commit the unit's movement to the destination tile.</li>
     *   <li>Assign the unit to the specified team, or to the {@code "neutral"} team by default.</li>
     *   <li>Add the unit to the turn order queue.</li>
     * </ul>
     *
     * <p>The input JSON object must contain:</p>
     * <ul>
     *   <li><b>"unit_id"</b> (String): The unique ID of the unit to place.</li>
     * </ul>
     * <p>Additionally, one of the following must be provided to identify the tile:</p>
     * <ul>
     *   <li><b>"tile_id"</b> (String): The ID of the target tile entity.</li>
     *   <li><b>"row"</b> (int) and <b>"column"</b> (int): Used if "tile_id" is not given.</li>
     * </ul>
     *
     * <p><b>Optional:</b></p>
     * <ul>
     *   <li><b>"team_id"</b> (String): The team to assign the unit to. Defaults to {@code "neutral"}.</li>
     * </ul>
     *
     * <p>If the tile is not navigable or the unit/tile ID is invalid, the method returns {@code null}.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     * {
     *   "unit_id": "fire_dragon_342423-522452-52662-252432",
     *   "row": 4,
     *   "column": 2,
     *   "team_id": "Enemy"
     * }
     * }</pre>
     *
     * @param request the JSON object containing unit and tile placement data; must not be {@code null}
     * @return the resolved tile ID where the unit was placed, or {@code null} on failure
     */
    public JSONObject setUnit(JSONObject request) {
        String tileID = getTile(request);
        String unitID = request.getString("unit_id");
        String teamID = (String) request.getOrDefault("team_id", "neutral");

        Entity tileEntity = getEntityWithID(tileID);
        if (tileEntity == null) { return null; }

        Entity unitEntity = getEntityWithID(unitID);
        if (unitEntity == null) { return null; }

        TileComponent tile = tileEntity.get(TileComponent.class);
        if (tile.isNotNavigable()) { return null; }

        tile.setUnit(unitID);

        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        movementComponent.stageTarget(tileID);
        movementComponent.commit();

        mSpeedQueue.add(unitID);
//        mInitiativeQueue.add(unitID);
        mGameState.addUnitToTeam(unitID, teamID);

//        mSpeedQueue.update();
        JSONObject response = getTileWithMetadata(request);

        return response;
    }

    /**
     * Places a structure on a specified tile or randomly distributes structures across the entire tile map.
     *
     * <p>This method allows a structure (e.g., building, obstacle, decoration) to be assigned to a tile by
     * updating the tile's structure component. The placement can be done for a single tile or in bulk across
     * the entire map depending on the {@code "bulk"} flag in the input.</p>
     *
     * <p><b>Input fields:</b></p>
     * <ul>
     *   <li><b>"structure"</b> (String): The ID or type of structure to create and place</li>
     *   <li><b>"tile_id"</b> (String): The ID of the target tile (only required for non-bulk placement)</li>
     *   <li><b>"bulk"</b> (boolean, optional): If true, places structures across the map using random chance</li>
     *   <li><b>"chance"</b> (float, optional): Used with bulk placement; a value from 0.0 to 1.0 indicating
     *       the probability that a structure will be placed on each tile (e.g., 0.25 = 25% chance)</li>
     * </ul>
     *
     * <p><b>Placement rules:</b></p>
     * <ul>
     *   <li>Tiles already containing a unit or liquid at the top layer will be skipped</li>
     *   <li>Structure entities are created via {@link EntityStore#createStructure}</li>
     *   <li>Each placed structure is associated with its tile and given a main asset ID</li>
     * </ul>
     *
     * <p><b>Examples:</b></p>
     *
     * <pre>{@code
     * // Single placement
     * {
     *   "structure": "watchtower",
     *   "tile_id": "tile_3_4_abc123"
     * }
     *
     * // Bulk placement with 30% chance per tile
     * {
     *   "structure": "tree",
     *   "bulk": true,
     *   "chance": 0.3
     * }
     * }</pre>
     *
     * @param request a {@link JSONObject} containing structure placement data
     * @return the updated tile's metadata as a {@link JSONObject}, or an empty object if placement failed
     */
    public JSONObject setStructure(JSONObject request) {
        String structure = request.getString("structure");
        boolean massPlace = request.getBooleanValue("bulk", false);
        float chance = request.getFloatValue("chance");

        if (massPlace) {
            for (int row = 0; row < getRows(); row++) {
                for (int column = 0; column < getColumns(); column++) {
                    if (mRandom.nextFloat() > chance) continue;

                    String tileID = mTileMap.tryFetchingTileID(row, column);
                    tryPlaceStructureOnTile(tileID, structure);
                }
            }
            return new JSONObject(); // Bulk mode doesn't return a single tile
        } else {
            String tileID = getTile(request);
            boolean success = tryPlaceStructureOnTile(tileID, structure);
            return success ? getTileWithMetadata(request) : new JSONObject();
        }
    }

    /**
     * Attempts to place a structure on the given tile.
     *
     * @param tileID     ID of the tile to place the structure on
     * @param structure  Type or ID of the structure to create
     * @return true if the structure was successfully placed; false otherwise
     */
    private boolean tryPlaceStructureOnTile(String tileID, String structure) {
        Entity tileEntity = getEntityWithID(tileID);
        if (tileEntity == null)  { return false; }

        TileComponent tileComponent = tileEntity.get(TileComponent.class);
        if (tileComponent.hasUnit() || tileComponent.isTopLayerLiquid()) { return false; }

        String structureID = EntityStore.getInstance().createStructure(structure);
        tileComponent.putStructureID(structureID);

        Entity structureEntity = getEntityWithID(structureID);
        AssetComponent assetComponent = structureEntity.get(AssetComponent.class);
        assetComponent.putMainID(structureID);

        System.out.println("Put Structure: " + structure);
        return true;
    }

    public JSONObject updateLayering(JSONObject request) {
        String tileID = getTile(request);
        String function = request.getString("function");

        JSONObject response = new JSONObject();
        Entity tileEntity = getEntityWithID(tileID);
        if (tileEntity == null) { return response; }
        TileComponent tileComponent = tileEntity.get(TileComponent.class);


        if (function.equalsIgnoreCase("add")) {
            try {
                String asset = (String) request.getOrDefault("asset", tileComponent.getTopLayerAsset());
                String state = (String) request.getOrDefault("state", tileComponent.getTopLayerState());
                int depth = request.getInteger("depth");
                tileComponent.addLayer(asset, state, depth);
            } catch (Exception ex) {
                System.out.println("prkookok");
            }
        }

        return getTileWithMetadata(request);
    }

    /**
     * Retrieves the unique identifier of the unit currently occupying the tile at the specified row and column.
     *
     * <p>The input JSON object must include the following fields:</p>
     * <ul>
     *   <li><b>"row"</b> (int): The row index of the target tile.</li>
     *   <li><b>"column"</b> (int): The column index of the target tile.</li>
     * </ul>
     *
     * <p>If the specified tile is invalid or unoccupied, this method returns {@code null}.</p>
     *
     * <p>Example input:</p>
     * <pre>{@code
     * {
     *   "row": 5,
     *   "column": 2
     * }
     * }</pre>
     *
     * @param input the JSON object specifying tile coordinates; must not be {@code null}
     * @return the unique identifier of the unit on the tile, or {@code null} if no unit is present or the tile does not exist
     */
    public String getUnitOfTile(JSONObject input) {
        String tileID = getTile(input);
        Entity tileEntity = getEntityWithID(tileID);
        if (tileEntity == null) { return null; }

        TileComponent tileComponent = tileEntity.get(TileComponent.class);
        String entityID = tileComponent.getUnitID();

        return entityID;
    }


    /**
     * Retrieves the unique identifier of a tile based on the provided input.
     *
     * <p>The input may specify the tile in one of two ways:</p>
     * <ul>
     *   <li><b>"tile_id"</b> (String): The unique identifier of the tile. If present, this is returned directly.</li>
     *   <li><b>"row"</b> (int) and <b>"column"</b> (int): Used to locate the tile ID if "tile_id" is not provided.</li>
     * </ul>
     *
     * <p>If the tile cannot be resolved using either approach, {@code null} is returned.</p>
     *
     * <p>Example inputs:</p>
     * <pre>{@code
     * { "tile_id": "tile_4_2_xyz" }
     * }</pre>
     *
     * or
     *
     * <pre>{@code
     * { "row": 4, "column": 2 }
     * }</pre>
     *
     * @param input a {@link JSONObject} containing either "tile_id", or "row" and "column"
     * @return the tile's unique identifier, or {@code null} if not found
     */
    private String getTile(JSONObject input) {
        String tileID = input.getString("tile_id");
        if (tileID == null) {
            int row = input.getIntValue("row", -1);
            int column = input.getIntValue("column", -1);
            tileID = mTileMap.tryFetchingTileID(row, column);
        }

        return tileID;
    }


    /**
     * Retrieves detailed metadata about a tile specified by a row/column or tile ID.
     *
     * <p>This method provides a high-level external representation of a tile's spatial
     * and elevation data. The input {@link JSONObject} must contain either:
     * <ul>
     *   <li><b>"tile_id"</b> (String): the ID of the target tile</li>
     *   <li>or <b>"row"</b> (int) and <b>"column"</b> (int): the coordinates of the tile</li>
     * </ul>
     *
     * <p>The returned {@link JSONObject} contains:
     * <ul>
     *   <li><b>"tile_id"</b> (String): the resolved tile ID</li>
     *   <li><b>"row"</b> (int): the row position of the tile</li>
     *   <li><b>"column"</b> (int): the column position of the tile</li>
     *   <li><b>"base_elevation"</b> (int): the base terrain height of the tile</li>
     *   <li><b>"modified_elevation"</b> (int): the final elevation including all stacked layers</li>
     * </ul>
     *
     * <p>Example usage:
     * <pre>{@code
     * {
     *   "row": 2,
     *   "column": 5
     * }
     * }</pre>
     *
     * or
     *
     * <pre>{@code
     * {
     *   "tile_id": "tile_2_5_abc123"
     * }
     * }</pre>
     *
     * @param request a {@link JSONObject} containing tile coordinates or ID; must not be {@code null}
     * @return a {@link JSONObject} with detailed tile information, or {@code null} if the tile doesn't exist
     */
    public JSONObject getTileWithMetadata(JSONObject request) {
        String tileID = getTile(request);
        Entity tileEntity = getEntityWithID(tileID);
        if (tileEntity == null) { return null; }

        TileComponent tileComponent = tileEntity.get(TileComponent.class);

        // Get the most shallowest of data to ensure mutation cant happen
        JSONObject result = new JSONObject();
        for (String key : tileComponent.keySet()) {
            Object value = tileComponent.get(key);
            if (value instanceof JSONObject || value instanceof JSONArray) { continue; }
            result.put(key, value);
        }
        result.put("tile_id", tileID);
        result.put("id", tileID);
        result.put("row", tileComponent.getRow());
        result.put("column", tileComponent.getColumn());
        result.put("base_elevation", tileComponent.getBaseElevation());
        result.put("modified_elevation", tileComponent.getModifiedElevation());
        result.put("structure_id", tileComponent.getStructureID());
        result.put("unit_id", tileComponent.getUnitID());
        result.put("is_liquid", tileComponent.isTopLayerLiquid());

        return result;
    }

    /**
     * Retrieves the unique identifier of the tile currently occupied by a unit.
     *
     * <p>The input JSON object must include the following field:</p>
     * <ul>
     *   <li><b>"unit_id"</b> (String): The unique identifier of the unit whose tile location is being queried.</li>
     * </ul>
     *
     * <p>If the unit ID is invalid or the unit is not currently placed on a tile, this method returns {@code null}.</p>
     *
     * <p>Example input:</p>
     * <pre>{@code
     * {
     *   "unit_id": "warrior_abc123-xyz987"
     * }
     * }</pre>
     *
     * @param input the JSON object containing the unit ID; must not be {@code null}
     * @return the unique identifier of the tile the unit is on, or {@code null} if the unit is not found or not placed
     */
    public JSONObject getTileOfUnit(JSONObject input) {
        String unitID = input.getString("unit_id");
        Entity entity = getEntityWithID(unitID);
        if (entity == null) { return null; }

        MovementComponent movementComponent = entity.get(MovementComponent.class);
        String tileID = movementComponent.getCurrentTileID();
        Entity tileEntity = getEntityWithID(tileID);

        TileComponent tileComponent = tileEntity.get(TileComponent.class);

        JSONObject response = new JSONObject();
        response.put("tile_id", tileID);
        for (String key : tileComponent.keySet()) {
            Object value = tileComponent.get(key);
            if (value instanceof JSONObject || value instanceof  JSONArray) {
                continue;
            }
            response.put(key, value);
        }


        return response;
    }



    public static JSONObject createGetTilesInMovementRangeRequest(String startTileID, int range, boolean respectfully) {
        JSONObject request = new JSONObject();
        request.put(START_TILE_ID, startTileID);
        request.put(RANGE, range);
        request.put(RESPECTFULLY, respectfully);
        return request;
    }
    /**
     * Constructs a directed tile graph representing all reachable tiles from a given origin within a specified range.
     *
     * <p>The input JSON object must include the following fields:</p>
     * <ul>
     *   <li><b>"start_tile_id"</b> (String): The unique identifier of the origin tile.</li>
     *   <li><b>"range"</b> (int): The maximum number of steps allowed from the origin tile.</li>
     *   <li><b>"respectfully"</b> (boolean): Whether to respect tile traversal constraints such as navigability or obstruction.</li>
     * </ul>
     *
     * <p>The result is a {@code Map<String, Set<String>>} where each key is a tile ID, and each value is a set of adjacent tile IDs
     * that can be reached directly from that tile (i.e., outgoing edges in a directional graph).</p>
     *
     * <p>This method is typically used for generating movement or interaction graphs in tactical grid-based games,
     * such as determining all possible destinations a unit can move to, or paths available for area-of-effect abilities.</p>
     *
     * <p>Example input:</p>
     * <pre>{@code
     * {
     *   "tile_id": "3_4_abc123",
     *   "range": 3,
     *   "respectfully": true
     * }
     * }</pre>
     *
     * @param request the JSON object specifying the origin tile, range limit, and movement rules; must not be {@code null}
     * @return a directed graph of reachable tiles within the specified range and constraints, or an empty map if none are reachable
     */
    public JSONArray getTilesInMovementRange(JSONObject request) {
//        String tileID = request.getString(START_TILE_ID);//getTile(request);
        String tileID = (String) request.getOrDefault(START_TILE_ID, getTile(request));//getTile(request);
        int range = request.getIntValue(RANGE);
        boolean respectfully = request.getBooleanValue(RESPECTFULLY, true);

        Map<String, String> graph = mTileMap.createDirectedGraph(tileID, range, respectfully);
        List<String> inMovementRange = new ArrayList<>(graph.keySet());
        JSONArray result = new JSONArray(inMovementRange);
        return result;
    }

    public JSONArray getTilesInMovementRangeV2(JSONObject request) {
        String tileID = (String) getTile(request);
        int range = request.getIntValue("range");
        boolean respect = request.getBooleanValue("respect", true);

        Map<String, String> graph = mTileMap.createDirectedGraph(tileID, range, respect);
        List<String> inMovementRange = new ArrayList<>(graph.keySet());
        JSONArray result = new JSONArray(inMovementRange);
        return result;
    }




    private static final String START_TILE_ID = "start_tile_id";
    private static final String END_TILE_ID = "end_tile_id";
    private static final String RANGE = "range";
    private static final String RESPECTFULLY = "respectfully";
    public static JSONObject createGetTilesInMovementPathRequest(String startTileID, int range, String endTileID, boolean respectfully) {
        JSONObject request = new JSONObject();
        request.put(START_TILE_ID, startTileID);
        request.put(RANGE, range);
        request.put(END_TILE_ID, endTileID);
        request.put(RESPECTFULLY, respectfully);
        return request;
    }
    /**
     * Computes the shortest traversable path from a starting tile to a specified destination tile,
     * given a movement range and tile traversal constraints.
     *
     * <p>This method uses a directed graph (generated via {@code TileMap#createDirectedGraph})
     * to trace a backwards path from the destination tile to the origin, following recorded parent links.
     * The graph respects range and optionally tile navigability or obstructions based on the {@code respectfully} flag.</p>
     *
     * <p>The input {@link JSONObject} must include:</p>
     * <ul>
     *   <li><b>"tile_id"</b> (String): The origin tile ID.</li>
     *   <li><b>"end_tile_id"</b> (String): The target destination tile ID.</li>
     *   <li><b>"range"</b> (int): The number of tiles the unit can move.</li>
     * </ul>
     *
     * <p>Optional:</p>
     * <ul>
     *   <li><b>"respectfully"</b> (boolean): Whether to respect terrain and movement rules (default: {@code true}).</li>
     * </ul>
     *
     * <p>If no path exists, or either tile is invalid or unreachable within the constraints, the method returns an empty array.</p>
     *
     * <p>Example input:</p>
     * <pre>{@code
     * {
     *   "tile_id": "start_3_4",
     *   "end_tile_id": "end_5_7",
     *   "range": 5,
     *   "respectfully": true
     * }
     * }</pre>
     *
     * @param request a {@link JSONObject} specifying start tile, end tile, movement range, and constraints
     * @return a {@link JSONArray} of tile IDs from origin to destination (inclusive), or an empty array if no valid path exists
     */
    public JSONArray getTilesInMovementPath(JSONObject request) {
        String tileID = request.getString(START_TILE_ID); //getTile(request);
        int range = request.getIntValue(RANGE);
        boolean respectfully = request.getBooleanValue(RESPECTFULLY, true);
        String endTileID = request.getString(END_TILE_ID);

        Map<String, String> graph = mTileMap.createDirectedGraph(tileID, range, respectfully);

        JSONArray response = new JSONArray();
        if (!graph.containsKey(tileID)) { return response; }
        if (!graph.containsKey(endTileID)) { return response; }


        LinkedList<String> queue = new LinkedList<>();
        String currentTileID = endTileID;
        while (currentTileID != null) {
            queue.addFirst(currentTileID);
            currentTileID = graph.get(currentTileID);
        }

        response.addAll(queue);
        return response;
    }

    public JSONArray getTilesInMovementPathV2(JSONObject request) {
        String tileID = request.getString("start_tile_id"); //getTile(request);
        int range = request.getIntValue("range");
        boolean respectfully = request.getBooleanValue("respect", true);
        String endTileID = request.getString("end_tile_id");

        Map<String, String> graph = mTileMap.createDirectedGraph(tileID, range, respectfully);

        JSONArray response = new JSONArray();
        if (!graph.containsKey(tileID)) { return response; }
        if (!graph.containsKey(endTileID)) { return response; }


        LinkedList<String> queue = new LinkedList<>();
        String currentTileID = endTileID;
        while (currentTileID != null) {
            queue.addFirst(currentTileID);
            currentTileID = graph.get(currentTileID);
        }

        response.addAll(queue);
        return response;
    }





    public static JSONObject createGetTilesInLineOfSightRequest(String startTileID, String endTileID, boolean respectfully) {
        JSONObject request = new JSONObject();
        request.put(START_TILE_ID, startTileID);
        request.put(END_TILE_ID, endTileID);
        request.put(RESPECTFULLY, respectfully);

        return request;
    }
    /**
     * Computes the line of sight between two tiles and returns all tiles along that path.
     * <p>
     * This method delegates to {@code computeLineOfSightJSON} and returns the tile IDs that form the
     * visible path from a starting tile to an ending tile. The result may be truncated early if an
     * obstructing tile is encountered and the {@code respectfully} flag is enabled.
     *
     * @param request a {@link JSONObject} containing the following required fields:
     *                <ul>
     *                  <li><b>"start_tile_id"</b>: the ID of the tile where the line of sight begins</li>
     *                  <li><b>"end_tile_id"</b>: the ID of the target tile</li>
     *                  <li><b>"respectfully"</b> (optional): if true, the line of sight stops when a non-navigable tile is encountered; defaults to true</li>
     *                </ul>
     *
     * @return a {@link JSONArray} of tile ID strings representing the ordered path of tiles
     *         from the start to the end tile (inclusive), possibly truncated by obstruction.
     */
    public JSONArray getTilesInLineOfSight(JSONObject request) {
        String startTileID = request.getString("start_tile_id");
        String endTileID = request.getString("end_tile_id");
        boolean respect = request.getBooleanValue("respect", true);

        JSONArray response = mTileMap.computeLineOfSightJSON(startTileID, endTileID, respect);

        return response;
    }




    public static JSONObject createGetTilesInAreaOfSightRequest(String startTileID, int range, boolean respectfully) {
        JSONObject request = new JSONObject();
        request.put(START_TILE_ID, startTileID);
        request.put(RANGE, range);
        request.put(RESPECTFULLY, respectfully);
        return request;
    }
    /**
     * Computes the full area of tiles visible from a given starting tile within a specified range.
     * <p>
     * This method delegates to {@code computeAreaOfSightJSON}, returning all tiles that are within a
     * diamond-shaped Manhattan range and are reachable via line-of-sight from the start tile.
     * <p>
     * The result is the union of all line-of-sight paths from the origin to each tile in range,
     * optionally stopping early for obstructed paths if {@code respectfully} is true.
     *
     * @param request a {@link JSONObject} containing the following fields:
     *                <ul>
     *                    <li><b>"start_tile_id"</b> (String): the ID of the tile at the origin of sight</li>
     *                    <li><b>"range"</b> (int): the maximum Manhattan distance for line-of-sight checks</li>
     *                    <li><b>"respectfully"</b> (boolean, optional): whether to stop lines early when encountering non-navigable tiles; defaults to true</li>
     *                </ul>
     *
     * @return a {@link JSONArray} of tile ID strings representing all tiles visible from the start tile
     *         within the given range, following line-of-sight constraints.
     */
    public JSONArray getTilesInAreaOfSight(JSONObject request) {
        String tileID = getTile(request);
        int range = request.getIntValue(RANGE);
        boolean respectfully = request.getBooleanValue(RESPECTFULLY, true);
        JSONArray response = mTileMap.computeAreaOfSightJSON(tileID, range, respectfully);
        return response;
    }
    public JSONArray getTilesInAreaOfSightV2(JSONObject request) {
        String startTileID = getTile(request);
        int range = request.getIntValue("range");
        boolean respectfully = request.getBooleanValue("respect", true);
        JSONArray response = mTileMap.computeAreaOfSightJSON(startTileID, range, respectfully);
        return response;
    }

    /**
     * Applies a named additive or multiplicative statistic modification to a unit's attribute.
     *
     * <p>This method calculates a value to add (or subtract) based on a specified scaling rule
     * and applies it as a named modifier to a target unit's attribute. The modification is stored
     * under a given source and name, allowing later reference or removal. It supports both flat
     * and percent-based modifications.</p>
     *
     * <p>The JSON input must include the following fields:</p>
     * <ul>
     *   <li><b>"unit_id"</b> (String): ID of the unit to modify.</li>
     *   <li><b>"attribute"</b> (String): The attribute to modify (e.g., "hp", "attack").</li>
     *   <li><b>"name"</b> (String): A unique name for this specific modifier (used for replacement or removal).</li>
     *   <li><b>"value"</b> (float): The amount to apply. Treated as a flat value unless scaling is specified.</li>
     * </ul>
     *
     * <p>Optional fields:</p>
     * <ul>
     *   <li><b>"scaling"</b> (String): Optional scaling method; valid values are:
     *     <ul>
     *       <li><code>"flat"</code>: Apply the value as-is.</li>
     *       <li><code>"base"</code>: Scale based on the base value of the attribute.</li>
     *       <li><code>"modified"</code>: Scale based on the modified (base + additive) value.</li>
     *       <li><code>"total"</code> or <code>"max"</code>: Scale based on the total (after all modifiers).</li>
     *       <li><code>"current"</code>: Scale based on the current value.</li>
     *       <li><code>"missing"</code>: Scale based on the difference between total and current.</li>
     *     </ul>
     *   </li>
     *   <li><b>"source"</b> (String): Logical group or system applying the modifier (default: "system").</li>
     * </ul>
     *
     * <p>Returns a JSON object with the following response fields:</p>
     * <ul>
     *   <li><b>"before"</b>: The total value of the attribute before applying the modifier.</li>
     *   <li><b>"after"</b>: The total value after the modifier is applied.</li>
     *   <li><b>"delta"</b>: The difference between before and after.</li>
     *   <li><b>"source"</b>: The source string used for this modification.</li>
     *   <li><b>"scaling"</b>: The scaling mode applied ("flat" by default).</li>
     *   <li><b>"error"</b>: A message if the request is invalid or the scaling source is not available.</li>
     * </ul>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * {
     *   "unit_id": "unit_123",
     *   "attribute": "hp",
     *   "name": "regen_buff",
     *   "value": 0.15,
     *   "scaling": "base",
     *   "source": "regen_system"
     * }
     * }</pre>
     *
     * @param request a {@link JSONObject} containing unit ID, attribute, value, and scaling mode
     * @return a {@link JSONObject} with the result of the operation, including before/after stats or an error
     */
    public JSONObject addUOrSubtractUnitStatisticModification(JSONObject request) {
        String unitID = request.getString("unit_id");
        String attribute = request.getString("attribute");
        String scaling = request.getString("scaling");
        float value = request.getFloatValue("value");
        String name = request.getString("name");
        String source = (String) request.getOrDefault("source", "system");

        Entity unitEntity = getEntityWithID(unitID);
        if (unitEntity == null || attribute == null) {
            return new JSONObject().fluentPut("error", "Invalid unit or attribute");
        }

        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        if (statisticsComponent == null) {
            return new JSONObject().fluentPut("error", "No statistics component found");
        }

        float before = statisticsComponent.getTotal(attribute);
        boolean isFlat = scaling.equalsIgnoreCase("flat");
        if (isFlat) {
            statisticsComponent.putAdditiveModification(source, name, attribute, value);
        } else {
            // Interpret value as percent (e.g. 0.1f = +10%)
            statisticsComponent.putMultiplicativeModification(source, name, attribute, value);
        }
        float after = statisticsComponent.getTotal(attribute);

        return new JSONObject()
                .fluentPut("before", before)
                .fluentPut("after", after)
                .fluentPut("delta", after - before)
                .fluentPut("source", source)
                .fluentPut("scaling", scaling);
    }

    public JSONObject addUOrSubtractUnitStatisticResource(JSONObject request) {
        String unitID = request.getString("unit_id");
        String attribute = request.getString("attribute");
        float value = request.getFloatValue("value");
        String source = (String) request.getOrDefault("source", "system");

        Entity unitEntity = getEntityWithID(unitID);
        if (unitEntity == null || attribute == null) {
            return new JSONObject().fluentPut("error", "Invalid unit or attribute");
        }

        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        if (statisticsComponent == null) {
            return new JSONObject().fluentPut("error", "No statistics component found");
        }

        float before = statisticsComponent.getTotal(attribute);
        statisticsComponent.toResource(attribute, value);
        float after = statisticsComponent.getTotal(attribute);

        return new JSONObject()
                .fluentPut("before", before)
                .fluentPut("after", after)
                .fluentPut("delta", after - before)
                .fluentPut("source", source);
    }

    public JSONObject addUOrSubtractFromUnitStatisticResource(JSONObject request) {
        String unitID = request.getString("unit_id");
        String attribute = request.getString("attribute");
        float value = request.getFloatValue("value");
        String source = (String) request.getOrDefault("source", "system");

        Entity unitEntity = getEntityWithID(unitID);
        if (unitEntity == null || attribute == null) {
            return new JSONObject().fluentPut("error", "Invalid unit or attribute");
        }

        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        if (statisticsComponent == null) {
            return new JSONObject().fluentPut("error", "No statistics component found");
        }

        float before = statisticsComponent.getCurrent(attribute);
        statisticsComponent.toResource(attribute, value);
        float after = statisticsComponent.getCurrent(attribute);

        return new JSONObject()
                .fluentPut("before", before)
                .fluentPut("after", after)
                .fluentPut("delta", after - before)
                .fluentPut("source", source);
    }



    /**
     * Retrieves detailed statistics for a specific attribute from a unit entity.
     *
     * <p>This method returns the breakdown of an attribute's values for a unit, including:</p>
     * <ul>
     *   <li><b>base</b>: The initial, unmodified value of the attribute.</li>
     *   <li><b>modified</b>: The value after applying permanent modifiers (e.g. gear, passive effects).</li>
     *   <li><b>current</b>: The current in-battle value (e.g. after taking damage or receiving a buff).</li>
     *   <li><b>missing</b>: The difference between the total and the current value.</li>
     *   <li><b>total</b>: The maximum attainable value after all modifiers.</li>
     * </ul>
     *
     * <p>The request must include:</p>
     * <ul>
     *   <li><b>"unit_id"</b> (String): The unique identifier of the unit entity.</li>
     *   <li><b>"attribute"</b> (String): The name of the attribute to query (e.g., "hp", "attack").</li>
     * </ul>
     *
     * <p>Example input:</p>
     * <pre>{@code
     * {
     *   "unit_id": "unit_abc123",
     *   "attribute": "hp"
     * }
     * }</pre>
     *
     * <p>Example output:</p>
     * <pre>{@code
     * {
     *   "base": 100,
     *   "modified": 120,
     *   "current": 85,
     *   "missing": 35,
     *   "total": 120
     * }
     * }</pre>
     *
     * @param request a {@link JSONObject} containing the unit ID and attribute name
     * @return a {@link JSONObject} containing the base, modified, current, missing, and total values of the attribute
     */
    public JSONObject getStatisticsFromUnit(JSONObject request) {
        String unitID = request.getString("unit_id");
        String attribute = request.getString("attribute");

        Entity unitEntity = getEntityWithID(unitID);
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);

        float total = statisticsComponent.getTotal(attribute);
        float base = statisticsComponent.getBase(attribute);
        float modified = statisticsComponent.getModified(attribute);
        float current = statisticsComponent.getCurrent(attribute);
        float missing = statisticsComponent.getMissing(attribute);

        JSONObject result = new JSONObject();
        result.put("base", base);
        result.put("modified", modified);
        result.put("current", current);
        result.put("missing", missing);
        result.put("total", total);

        return result;
    }

    public JSONObject getCostMap(String userID, String ability) {
        Entity userEntity = getEntityWithID(userID);
        StatisticsComponent statisticsComponent = userEntity.get(StatisticsComponent.class);
        JSONObject costMap = new JSONObject();

        JSONArray costs = AbilityTable.getInstance().getCosts(ability);

        for (int i = 0; i < costs.size(); i++) {
            JSONObject abilityCost = costs.getJSONObject(i);
            String resource = AbilityTable.getInstance().getAttribute(abilityCost);
            String equation = AbilityTable.getInstance().getEquation(abilityCost);
            String[] partitions = equation.split(" ");

            // Handle the number
            float value = 0;
            boolean isPercentage = false;
            String equationValue = partitions[0].trim();
            if (partitions.length == 3 && equationValue.endsWith("%")) {
                isPercentage = true;
                if (equationValue.startsWith("+") || equationValue.startsWith("*")) {
                    value = Float.parseFloat(equationValue.substring(1, equationValue.length() - 1));
                } else if (Character.isDigit(equationValue.charAt(0))) {
                    value = Float.parseFloat(equationValue.substring(0, equationValue.length() - 1));
                } else {
                    return new JSONObject().fluentPut("error", "Unable to parse equation value " + equationValue);
                }
                value = value * .01f; // Make into percentage
            } else {
                value = Float.parseFloat(equationValue);
            }
            String scaling = partitions[1].trim();
            String attribute = partitions[2].trim();

            // Calculate based on scaling or flat
            float currentAccruedCost = (float) costMap.getOrDefault(resource, 0f);
            float additionalCost = 0;
            if (isPercentage) {
                float baseModifiedTotalMissingCurrent = statisticsComponent.getScaling(attribute, scaling);
                additionalCost = baseModifiedTotalMissingCurrent * value;
            } else {
                additionalCost += value;
            }

            float newAccruedCost = currentAccruedCost + additionalCost;
            costMap.put(resource,newAccruedCost);
        }
        return costMap;
    }

    public JSONObject getDamageMap(String userID, String ability) {
        Entity userEntity = getEntityWithID(userID);
        StatisticsComponent statisticsComponent = userEntity.get(StatisticsComponent.class);
        JSONObject damageMap = new JSONObject();

        JSONArray damages = AbilityTable.getInstance().getDamages(ability);

        for (int i = 0; i < damages.size(); i++) {
            JSONObject abilityDamage = damages.getJSONObject(i);
            String resource = AbilityTable.getInstance().getAttribute(abilityDamage);
            String equation = AbilityTable.getInstance().getEquation(abilityDamage);
            String[] partitions = equation.split(" ");

            // Handle the number
            float value = 0;
            boolean isPercentage = false;
            String equationValue = partitions[0].trim();
            if (partitions.length == 3 && equationValue.endsWith("%")) {
                isPercentage = true;
                if (equationValue.startsWith("+") || equationValue.startsWith("*")) {
                    value = Float.parseFloat(equationValue.substring(1, equationValue.length() - 1));
                } else if (Character.isDigit(equationValue.charAt(0))) {
                    value = Float.parseFloat(equationValue.substring(0, equationValue.length() - 1));
                } else {
                    return new JSONObject().fluentPut("error", "Unable to parse equation value " + equationValue);
                }
                value = value * .01f; // Make into percentage
            } else {
                value = Float.parseFloat(equationValue);
            }

            // Calculate based on scaling or flat
            float currentAccruedDamage = (float) damageMap.getOrDefault(resource, 0f);
            float additionalCost = 0;
            if (isPercentage) {
                String scaling = partitions[1].trim();
                String attribute = partitions[2].trim();
                float baseModifiedTotalMissingCurrent = statisticsComponent.getScaling(attribute, scaling);
                additionalCost = baseModifiedTotalMissingCurrent * value;
            } else {
                additionalCost = value;
            }

            float newAccruedDamage = currentAccruedDamage + additionalCost;
            damageMap.put(resource, newAccruedDamage);
        }
        return damageMap;
    }



    /**
     * Attempts to validate and optionally deduct the resource costs required to use a given ability.
     * <p>
     * This method retrieves the unit's current statistics, checks whether the unit can afford
     * the cost of each attribute required by the ability, and optionally commits the deduction
     * if the `commit` flag is true.
     * </p>
     *
     * @param request a {@link JSONObject} containing:
     *                <ul>
     *                  <li><b>"unit_id"</b> (String): ID of the unit attempting to use the ability</li>
     *                  <li><b>"ability"</b> (String): ID of the ability being used</li>
     *                  <li><b>"commit"</b> (boolean, optional): if true, the method deducts the cost from the unit's stats</li>
     *                </ul>
     *
     * @return a {@link JSONObject} with:
     *         <ul>
     *           <li>An empty object if the cost is payable and, if committed, successfully paid.</li>
     *           <li>A field <b>"error"</b> if the unit cannot afford any part of the cost.</li>
     *         </ul>
     *
     * Logs important information about the cost validation and payment process using the internal logger.
     *
     * @see AbilityTable#getCosts(String)
     * @see StatisticsComponent#toResource(String, float)
     * @see #getCostMap(String, String)
     */
    public JSONObject payAbilityCost(JSONObject request) {
        String unitID = request.getString("unit_id");
        String ability = request.getString("ability");
        boolean commit = request.getBooleanValue("commit", false);

        Entity userEntity = getEntityWithID(unitID);
        StatisticsComponent statisticsComponent = userEntity.get(StatisticsComponent.class);

        mLogger.info("Started gathering cost requirements for {}", ability);
        JSONObject costMap = getCostMap(unitID, ability);
        mLogger.info("Finished gathering cost requirements for {}", ability);

        for (String attribute : costMap.keySet()) {
            float cost = costMap.getFloatValue(attribute);
            int currentValue = statisticsComponent.getCurrent(attribute);
            if (currentValue >= cost) { continue; }
            mLogger.info("{} is unable to pay for {} because of {}", userEntity, ability, attribute);
            return new JSONObject().fluentPut("error", "Can't pay " + attribute + " cost requirement");
        }
        mLogger.info("Finished checking cost requirements for {}", ability);

        JSONObject response = new JSONObject();
        if (commit) {
            mLogger.info("Started paying cost requirements for {}", ability);
            for (String attribute : costMap.keySet()) {
                float cost = - costMap.getFloatValue(attribute);
                int current = statisticsComponent.getCurrent(attribute);
                response.put(attribute, cost);
                statisticsComponent.toResource(attribute, cost);
                mLogger.info("Payed {} out of {} to use {}", Math.abs(cost), Math.abs(current), ability);
            }
            mLogger.info("Finished paying cost requirements for {}", ability);
        }

        return response;
    }

//    public JSONObject canPayAbilityCosts(String userUnitID, String ability) {
//        Map<String, Float> costMap = getCostMapping(userUnitID, ability);
//        Entity userEntity = getEntityWithID(userUnitID);
//        StatisticsComponent statisticsComponent = userEntity.get(StatisticsComponent.class);
//        mLogger.info("Checking cost requirements for {} to use {}", userEntity, ability);
//
//        for (Map.Entry<String, Float> entry : costMap.entrySet()) {
//            String attribute = entry.getKey();
//            float cost = entry.getValue();
//
//            int currentValue = statisticsComponent.getCurrent(attribute);
//            if (currentValue >= cost) { continue; }
//            mLogger.info("{} is unable to pay for {} because of {}", userEntity, ability, attribute);
//            return false;
//        }
//
//        mLogger.info("{} is able to pay for {}", userEntity, ability);
//        return true;
//    }


    /**
     * Attempts to use a specified ability from a unit on a target tile, handling targeting,
     * area-of-effect previewing, line-of-sight validation, and optional commitment of ability costs and effects.
     * <p>
     * This method performs the following steps:
     * <ol>
     *   <li>Validates the presence of a target tile.</li>
     *   <li>Retrieves range, area, and relevant components (movement, ability, actions) from the user unit.</li>
     *   <li>Stages range (area of sight), line of sight, and area of effect tiles based on ability configuration.</li>
     *   <li>Stages the selected ability and its target into the AbilityComponent for preview or execution.</li>
     *   <li>If {@code commit} is true and all conditions are met, deducts resource costs and commits the ability.</li>
     *   <li>Publishes a combat event if the ability is successfully committed.</li>
     * </ol>
     *
     * @param request a {@link JSONObject} containing the following fields:
     *                <ul>
     *                    <li>{@code unit_id} – ID of the unit attempting to use the ability</li>
     *                    <li>{@code ability} – name of the ability to use</li>
     *                    <li>{@code commit} (optional) – if true, the ability will be executed; otherwise, just previewed</li>
     *                </ul>
     * @return a {@link JSONObject} result indicating:
     *         <ul>
     *             <li>{@code error} – if no tile was selected, the target is invalid, or cost requirements are not met</li>
     *             <li>empty object – if the ability was staged but not committed</li>
     *             <li>{@code null} – if the ability was successfully used and committed</li>
     *         </ul>
     */
    public JSONObject useAbility(JSONObject request) {
        String unitID = request.getString("unit_id");
        String targetTileID = getTile(request);
        String ability = request.getString("ability");
        boolean commit = request.getBooleanValue("commit", false);

        if (ability == null) {
            return new JSONObject().fluentPut("error", "No Ability Specified");
        }

        if (targetTileID == null) {
            return new JSONObject().fluentPut("error", "No Tile Selected");
        }

        Entity unitEntity = getEntityWithID(unitID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
        ActionsComponent actionsComponent = unitEntity.get(ActionsComponent.class);

        String currentTileID = movementComponent.getCurrentTileID();
        int range = AbilityTable.getInstance().getRange(ability);
        int area = AbilityTable.getInstance().getArea(ability);

        // Setup Area of Sight
        request = new JSONObject();
        request.put("tile_id", currentTileID);
        request.put("range", range);
        request.put("respect", true);

        JSONArray response = getTilesInAreaOfSightV2(request);
        List<String> aos = response.toJavaList(String.class);
        abilityComponent.stageRange(aos);
        mLogger.info("Updated area of sight for {}, {} tiles", unitEntity, aos.size());

        // Setup Line of Sight
        request = new JSONObject();
        request.put("start_tile_id", currentTileID);
        request.put("end_tile_id", targetTileID);
        request.put("respect", true);

        response = getTilesInLineOfSight(request);
        List<String> los = response.toJavaList(String.class);
        abilityComponent.stageLineOfSight(los);
        mLogger.info("Updated line of sight for {}, {} tiles", unitEntity, los.size());


        // Setup Area of Effect
        request = new JSONObject();
        request.put("tile_id", targetTileID);
        request.put("range", area - 1);
        request.put("respect", true);

        response = getTilesInAreaOfSight(request);
        List<String> aoe = response.toJavaList(String.class);
        abilityComponent.stageAreaOfEffect(aoe);
        mLogger.info("Updated area of effect for {}, {} tiles", unitEntity, aoe.size());



        abilityComponent.stageTarget(targetTileID);
        mLogger.info("Updating target for {}, {}", unitEntity, targetTileID);

        abilityComponent.stageAbility(ability);
        mLogger.info("Updating ability for {}, {}", unitEntity, ability);

        // try executing action only if specified
        // - Target is not null
        // - Target is within range
        // - We are not in preview mode
        if (!commit) {
            return new JSONObject().fluentPut("", "Not committing to ability use");
        }
        if (actionsComponent.hasFinishedUsingAbility()) {
            return new JSONObject().fluentPut("error", "Already finished using");
        }
        if (!abilityComponent.getStageTiledRange().contains(targetTileID)) {
            return new JSONObject().fluentPut("error", "Invalid target selected for ability");
        }

        request = new JSONObject();
        request.put("unit_id", unitID);
        request.put("ability", ability);
        request.put("commit", true);
        JSONObject canPayAbility = payAbilityCost(request);
        if (canPayAbility.containsKey("error")) {
            return canPayAbility;
        }

        abilityComponent.commit();
//        mEventBus.publish(CombatSystem.createCombatStartEvent(unitID, ability));

        request = new JSONObject();
        request.put("unit_id", unitID);
        request.put("ability", ability);
        handleCombatAnimationChaining(request);


        actionsComponent.setHasFinishedUsingAbility(true);

        return new JSONObject().fluentPut("success", "Successfully started using ability");
    }

    public JSONObject useMove(JSONObject request) {
        String unitID = request.getString("unit_id");
        String toTileID = getTile(request);
        boolean commit = request.getBooleanValue("commit", false);

        Entity unitEntity = getEntityWithID(unitID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        ActionsComponent actionsComponent = unitEntity.get(ActionsComponent.class);

        if (toTileID == null) {
            return new JSONObject().fluentPut("error", "No Tile Specified");
        }

        if (unitID == null) {
            return new JSONObject().fluentPut("error", "No Unit Selected");
        }

        int move = statisticsComponent.getTotalMovement();
        int climb = statisticsComponent.getTotalClimb();
        String fromTileID = movementComponent.getCurrentTileID();

        // Get Movement Range
        request = new JSONObject();
        request.put("tile_id", fromTileID);
        request.put("range", move);
        request.put("respect", true);

        JSONArray response = getTilesInMovementRangeV2(request);
        List<String> range = response.toJavaList(String.class);
        movementComponent.stageMovementRange(range);
        mLogger.info("Updated range for {}, {} tiles", unitEntity, range.size());


        // Get Movement Path
        request = new JSONObject();
        request.put("start_tile_id", fromTileID);
        request.put("range", -1);
        request.put("end_tile_id", toTileID);
        request.put("respect", true);

        response = getTilesInMovementPathV2(request);
        List<String> path = response.toJavaList(String.class);
        movementComponent.stageMovementPath(path);
        mLogger.info("Updated path for {}, {} tiles", unitEntity, path.size());


        movementComponent.stageTarget(toTileID);
        mLogger.info("Updated target tile {}, {}", unitEntity, toTileID);

        // try executing action only if specified
        // - Target is not null
        // - Target is within range
        // - We are not in preview mode
        // - We are targeting the current tiles were one
        if (!commit) { return new JSONObject(); }
        if (actionsComponent.hasFinishedMoving()) { return new JSONObject(); }
        if (!movementComponent.getStagedMovementRange().contains(movementComponent.getStagedTarget())) { return new JSONObject(); }

        removeUnitFromTile(unitID);
        movementComponent.commit();
        putUnitOnTile(unitID);

        // do the animation for the tile
        mEventBus.publish(AnimationSystem.createPathingAnimationEvent(
                unitID,  movementComponent.getTilesInFinalMovementPath()
        ));


        actionsComponent.setHasFinishedMoving(true);
        focusCamerasAndSelectionsOfActiveEntity();

//        boolean isLockedOnActivityCamera = mGameState.isLockOnActivityCamera();
//        if (isLockedOnActivityCamera) { model.focusCamerasAndSelectionsOfActiveEntity(); }
//        model.focusCamerasAndSelectionsOfActiveEntity(tileToMoveUnitToID);

//        DirectionComponent directionComponent = unitEntity.get(DirectionComponent.class);
//        directionComponent.setDirection(getDirection(currentTile, tileToMoveTo));


        getGameState().setAutomaticallyGoToHomeControls(true);

        return new JSONObject().fluentPut("success", "Moved " + unitID + " from " + fromTileID + " to " + toTileID);
    }







    private void handleCombatAnimationChaining(JSONObject request) {
        String actorEntityID = request.getString("unit_id");
        String ability = request.getString("ability");

        String announcement = AbilityTable.getInstance().getAnnouncement(ability);
        if (announcement.isEmpty()) {
            announcement = StringUtils.convertSnakeCaseToCapitalized(ability);
        }
        getEventBus().publish(FloatingTextSystem.createFloatingTextEvent( announcement, actorEntityID ));

        Entity actorEntity = getEntityWithID(actorEntityID);
        AbilityComponent abilityComponent = actorEntity.get(AbilityComponent.class);

        String targetTile = abilityComponent.getFinalTileTargeted();
        String userAnimation = AbilityTable.getInstance().getUserAnimation(ability);

        getEventBus().publish(AnimationSystem.createAnimationEvent(actorEntityID, userAnimation, List.of(targetTile)));

        abilityComponent.getTilesInFinalAreaOfEffect().forEach(iteratedTargetTileID -> {
            Entity tileEntity = getEntityWithID(iteratedTargetTileID);
            TileComponent tile = tileEntity.get(TileComponent.class);
            String actedOnEntityID = tile.getUnitID();
            if (actedOnEntityID == null) { return; }

            boolean hitsTarget = AbilityTable.getInstance().isSuccessful(ability);
            if (!hitsTarget) { return; }

            // Trigger an animation for the user
            // Add a listener to notify when the animation completes
            AnimationComponent actorComponent = actorEntity.get(AnimationComponent.class);
            AnimationTrack track = AnimationSystem.executeShakeAnimation(this, actedOnEntityID);
            actorComponent.addOnCompleteListener(() -> {
                Entity actedOnEntity = getEntityWithID(actedOnEntityID);
                if (actedOnEntity == null) { return; }
                AnimationComponent actedOnComponent = actedOnEntity.get(AnimationComponent.class);
                actedOnComponent.addTrack(track);
                getEventBus().publish(CombatSystem.createCombatEndEvent(actorEntityID, ability, actedOnEntityID));
            });
        });
    }


    public void removeUnitFromTile(String unitID) {
        Entity unitEntity = getEntityWithID(unitID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity tileEntity = getEntityWithID(movementComponent.getCurrentTileID());
        TileComponent tileComponent = tileEntity.get(TileComponent.class);
        tileComponent.removeUnit();
    }
    public void putUnitOnTile(String unitID) {
        Entity unitEntity = getEntityWithID(unitID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity tileEntity = getEntityWithID(movementComponent.getCurrentTileID());
        TileComponent tileComponent = tileEntity.get(TileComponent.class);
        tileComponent.setUnit(unitID);
    }

    public JSONObject triggerTurnOrderQueue() {
//        boolean update = getInitiativeQueue().refill();
        String entityAtStartOfQueue = getSpeedQueue().peek();

//        if (entityAtStartOfQueue == null) { return new JSONObject().fluentPut("error", "Turn queue empty"); }

        getGameState().setCurrentEntitiesTurn(entityAtStartOfQueue);
        return new JSONObject().fluentPut("success", entityAtStartOfQueue + " starts the turn");
    }

    public JSONObject setAutomaticallyShouldEndCpusTurn(JSONObject request) {
        boolean value = request.getBooleanValue("value", true);
        getGameState().setShouldAutomaticallyEndCpusTurn(value);
        return new JSONObject();
    }

    public JSONObject setAbilities(JSONObject request) {
        String unitID = request.getString("unit_id");
        JSONArray abilities = request.getJSONArray("abilities");
        String passive = request.getString("passive");
        String basic = request.getString("basic");

        Entity unitEntity = getEntityWithID(unitID);
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);

        if (abilities != null) {
            String invalidAbility = null;
            for (int i = 0; i < abilities.size(); i++) {
                String ability = abilities.getString(i);
                boolean isRealAbility = AbilityTable.getInstance().exists(ability);
                if (isRealAbility) { continue; }
                invalidAbility = ability;
            }
            if (invalidAbility == null) {
                return new JSONObject().fluentPut("error", "Unable to parse " + invalidAbility);
            }
            statisticsComponent.putOtherAbility(abilities);
        }

        if (passive != null) {
            boolean isRealAbility = AbilityTable.getInstance().exists(passive);
            if (isRealAbility) {
                statisticsComponent.putPassiveAbility(passive);
            } else {
                return new JSONObject().fluentPut("error", "Unable to parse " + passive);
            }
        }

        if (basic != null) {
            boolean isRealAbility = AbilityTable.getInstance().exists(basic);
            if (isRealAbility) {
                statisticsComponent.putBasicAbility(basic);
            } else {
                return new JSONObject().fluentPut("error", "Unable to parse " + basic);
            }
        }


        return new JSONObject();
    }


    /**
     * Creates a unit entity with the given parameters and sets its type to "unit".
     * @param request the unit creation parameters
     * @return a {@link JSONObject} containing the new unit ID
     */
    public JSONObject createUnit(JSONObject request) {
        request.put("type", "unit");
        return createEntity(request);
    }
    /**
     * Creates a structure entity with the given parameters and sets its type to "structure".
     * @param request the structure creation parameters
     * @return a {@link JSONObject} containing the new structure ID
     */
    public JSONObject createStructure(JSONObject request) {
        request.put("type", "structure");
        return createEntity(request);
    }
    /**
     * Creates a tile entity with the given parameters and sets its type to "tile".
     * @param request the tile creation parameters
     * @return a {@link JSONObject} containing the new tile ID
     */
    public JSONObject createTile(JSONObject request) {
        request.put("type", "unit");
        return createEntity(request);
    }
    /**
     * Creates a new entity in the game world based on the parameters defined in the request.
     * <p>
     * The type of entity is determined by the `"type"` field in the request, which must be one of:
     * <ul>
     *     <li><b>"unit"</b> – creates a unit with optional nickname and playable flag</li>
     *     <li><b>"structure"</b> – creates a structure from a base definition</li>
     *     <li><b>"tile"</b> – creates a tile at the specified row, column, and elevation</li>
     * </ul>
     *
     * @param request a {@link JSONObject} containing the parameters for entity creation:
     * <ul>
     *     <li><b>Common:</b> <code>"type"</code> (required): one of <code>"unit"</code>, <code>"structure"</code>, or <code>"tile"</code></li>
     *     <li><b>Unit:</b> <code>"unit"</code> (required), <code>"nickname"</code> (optional), <code>"playable"</code> (optional)</li>
     *     <li><b>Structure:</b> <code>"structure"</code> (required)</li>
     *     <li><b>Tile:</b> <code>"row"</code> (required), <code>"column"</code> (required), <code>"elevation"</code> (optional)</li>
     * </ul>
     *
     * @return a {@link JSONObject} containing the created entity ID under the key <code>"id"</code>, e.g. <code>{"id": "U123"}</code>.
     */
    public JSONObject createEntity(JSONObject request) {
        String type = request.getString("type");

        String unit = request.getString("unit");
        String structure = request.getString("structure");
        String tile = request.getString("tile");
        String nickname = request.getString("nickname");
        int row = request.getIntValue("row", -1);
        int column = request.getIntValue("column", -1);
        int elevation = request.getIntValue("elevation", -1);
        boolean playable = request.getBooleanValue("playable", false);

        JSONObject response = new JSONObject();
        String id = null;
        switch (type) {
            case "unit" -> {
                if (unit == null && nickname == null && !playable) {
                    id = EntityStore.getInstance().createUnit(true);
                } else {
                    id = EntityStore.getInstance().createUnit(unit, nickname, playable);
                }
                response.put("unit_id", id);
            }
            case "structure" -> {
                id = EntityStore.getInstance().createStructure(structure);
                response.put("structure_id", id);
            }
            case "tile" -> {
                id = EntityStore.getInstance().createTile(row, column);
                response.put("tile_id", id);
            }
        }
        response.put("id", id);

        return response;
    }


    public JSONObject getViewportData() {
        JSONObject result = new JSONObject();
        result.put("zoom", getGameState().getViewportZoom());
        result.put("x", getGameState().getViewportX());
        result.put("y", getGameState().getViewportY());
        return result;
    }

    /**
     * Adjusts the game's viewport zoom level and updates the sprite dimensions accordingly.
     * <p>
     * This method sets the zoom factor for the current game state, which affects how tiles and assets
     * are scaled during rendering. It retrieves the original sprite dimensions and scales them by the
     * new zoom factor, then updates the game state with the resulting dimensions.
     *
     * <p>
     * Expected input format:
     * <pre>{@code
     * {
     *   "zoom": 1.5
     * }
     * }</pre>
     *
     * @param request a {@link JSONObject} containing:
     *                <ul>
     *                  <li><b>"zoom"</b> (float): the new zoom factor (e.g., 1.0 for 100%, 2.0 for 200%)</li>
     *                </ul>
     *
     * @throws IllegalArgumentException if the zoom factor is non-positive
     */
    public void setViewportZoom(JSONObject request) {
        float zoom = request.getFloatValue("zoom");

        getGameState().setViewportZoom(zoom);
        zoom = getGameState().getViewportZoom();

        int spriteWidth = getGameState().getOriginalSpriteWidth();
        int spriteHeight = getGameState().getOriginalSpriteHeight();

        int newSpriteWidth = (int) (spriteWidth * zoom);
        int newSpriteHeight = (int) (spriteHeight * zoom);

        getGameState().setSpriteWidth(newSpriteWidth);
        getGameState().setSpriteHeight(newSpriteHeight);
    }

    /**
     * Initializes and generates a new tile map based on the given request parameters.
     * <p>
     * This method sets up a grid of tiles using elevation noise, floor/terrain/liquid assets,
     * and foundational configuration. It also prepares the internal game state, viewport,
     * event systems, and input handlers required for gameplay.
     *
     * <p><b>Expected request fields:</b>
     * <ul>
     *   <li><b>"rows"</b> (int): Number of tile rows in the map</li>
     *   <li><b>"columns"</b> (int): Number of tile columns in the map</li>
     *   <li><b>"viewport_width"</b> (int): Width of the game viewport in pixels</li>
     *   <li><b>"viewport_height"</b> (int): Height of the game viewport in pixels</li>
     *   <li><b>"viewport_x"</b> (int): Initial horizontal offset of the viewport</li>
     *   <li><b>"viewport_y"</b> (int): Initial vertical offset of the viewport</li>
     *   <li><b>"foundation_asset"</b> (String, optional): Asset ID for the tile base layer</li>
     *   <li><b>"foundation_depth"</b> (int, optional): Number of foundation layers to apply (default: 3)</li>
     *   <li><b>"liquid_asset"</b> (String, optional): Asset ID for liquid layers</li>
     *   <li><b>"liquid_elevation"</b> (int, optional): Threshold elevation below which liquid is applied (default: 4)</li>
     *   <li><b>"terrain_asset"</b> (String, optional): Asset ID for upper terrain layers</li>
     *   <li><b>"lower_terrain_elevation"</b> (int): Minimum elevation for terrain noise</li>
     *   <li><b>"upper_terrain_elevation"</b> (int): Maximum elevation for terrain noise</li>
     *   <li><b>"terrain_elevation_noise"</b> (float): Amplitude/frequency factor for terrain elevation noise</li>
     *   <li><b>"terrain_elevation_noise_seed"</b> (long, optional): Random seed for deterministic terrain generation</li>
     * </ul>
     *
     * <p>
     * After tile map generation, this method:
     * <ul>
     *   <li>Centers the camera view based on map and viewport dimensions</li>
     *   <li>Initializes gameplay systems: event bus, logger, input, update loop</li>
     *   <li>Designates spawn regions using the generated tile data</li>
     * </ul>
     *
     * @param request a {@link JSONObject} containing the map dimensions, asset settings, and viewport config
     * @return {@code null} (the map is generated internally and accessible through state)
     */
    public JSONObject generateTileMap(JSONObject request) {
        int rows = request.getIntValue("rows", 0);
        int columns = request.getIntValue("columns", 0);
        int viewportWidth = request.getIntValue("viewport_width", 0);
        int viewportHeight = request.getIntValue("viewport_height", 0);
        int viewportX = request.getIntValue("viewport_x", 0);
        int viewportY = request.getIntValue("viewport_y", 0);

        mTileMap = new TileMap();
        mTileMap.generate(request);

        mGameState = new GameState(GameState.getDefaults());
        mGameState.setViewportX(viewportX)
                .setViewportY(viewportY)
                .setViewportWidth(viewportWidth)
                .setViewportHeight(viewportHeight);

        if (true) {
            Vector3f centerValues = Vector3f.getCenteredVector(
                    0,
                    0,
                    getGameState().getSpriteWidth() * columns,
                    getGameState().getSpriteHeight() * rows,
                    viewportWidth,
                    viewportHeight
            );
            mGameState.setViewportX(mGameState.getViewportX() - centerValues.x);
            mGameState.setViewportY(mGameState.getViewportY() - centerValues.y);
        }

        mActivityLogger = new ActivityLogger();
        mSpeedQueue = new SpeedQueue();
        mEventBus = new JSONEventBus();
        mInputHandler = new InputHandler(mEventBus);
        mSystem = new UpdateSystem(this);
        mTileMap.designateSpawns(true);

        return null;
    }

    public JSONObject applyAbility(JSONObject request) {
        String actorEntityID = request.getString("actor_unit_id");
        String ability = request.getString("ability");
        String actedOnEntityID = request.getString("acted_on_unit_id");


        JSONObject damageMap = getDamageMap(actorEntityID, ability);

        Entity actedOnEntity = getEntityWithID(actedOnEntityID);
        StatisticsComponent actedOnStatisticsComponent = actedOnEntity.get(StatisticsComponent.class);
        mLogger.info("Started dealing damage with {} to {} ", ability, actedOnEntity);
//
        for (String attribute : damageMap.keySet()) {
            int finalDamage = damageMap.getIntValue(attribute);
            actedOnStatisticsComponent.toResource(attribute, -finalDamage);

            String displayText = (finalDamage < 0 ? "+" : "") + Math.abs(finalDamage);

            mEventBus.publish(FloatingTextSystem.createFloatingTextEvent( displayText, actedOnEntityID ));
        }

        Entity actorEntity = getEntityWithID(actorEntityID);
        ActionsComponent actionsComponent = actorEntity.get(ActionsComponent.class);
        actionsComponent.setHasFinishedUsingAbility(true);


        return damageMap;
    }

    public JSONObject pause() {
//        getGameState().setCpuTurnStartDelayInSeconds();
        return new JSONObject();
    }

    public JSONObject unpause() {
        return new JSONObject();
    }

//
//    public float getCameraZoom() { return getGameState().getMapZoom(); }

//    public List<String> getTilesInUnitsMovementRangeInternal(JSONObject input) {
//        String unitID = input.getString("unit_id");
//        boolean respectfully = input.getBooleanValue("respectfully", true);
//
//        Map<String, Set<String>> graph = mTileMap.createDirectedGraph(tileID, range, respectfully);
//        List<String> inMovementRange = new ArrayList<>(graph.keySet());
//        return inMovementRange;
//    }







}
