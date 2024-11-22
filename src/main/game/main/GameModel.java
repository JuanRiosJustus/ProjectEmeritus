package main.game.main;

import java.awt.image.BufferedImage;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import main.engine.Engine;
import main.game.camera.Camera;
import main.constants.Vector3f;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.logging.ActivityLogger;
import main.game.map.base.TileMap;
import main.game.queue.SpeedQueue;
import main.game.stores.pools.unit.UnitPool;
import main.game.systems.InputHandler;
import main.game.systems.UpdateSystem;
import main.input.InputController;
import main.input.Mouse;


public class GameModel {

    private TileMap mTileMap = null;
    public SpeedQueue mSpeedQueue = null;
    public ActivityLogger mLogger = null;
    public GameState mGameState = null;
    private GameController mGameController = null;
    public InputHandler mInputHandler = null;
    public UpdateSystem mSystem = null;
    private GameConfigurations mGameConfigurations = null;
    private final Camera mCamera = Camera.getInstance();
    private boolean mRunning = false;
    private GameModelAPI mGameModelApi;

    public GameModel(GameController gc) { mGameController = gc; }
    public GameModel(GameController gc, int rows, int columns) {
        mGameController = gc;
        setup(rows, columns);
    }





    public GameModel(GameController gc, GameConfigurations gs) { this(gc, gs, null); }
    public GameModel(GameController gc, GameConfigurations gs, JSONArray mp) { setup(gc, gs, mp); }

    public void setup(GameController gameController, GameConfigurations gameConfigurations, JSONArray map) {
        mGameController = gameController;
        mGameConfigurations = gameConfigurations;
        mTileMap = new TileMap(mGameConfigurations, map);
        mSystem = new UpdateSystem();
        mInputHandler = new InputHandler();
        mGameState = new GameState();
        mLogger = new ActivityLogger();
        mSpeedQueue = new SpeedQueue();
        mGameModelApi = new GameModelAPI();
    }



//    public GameModel(GameController gc, GameSettings gs) { setup(gc, gs); }

//    public void setup(GameController gameController, GameSettings gameSettings) {
//        mGameController = gameController;
//        mGameSettings = gameSettings;
//        mTileMap = new TileMap(gameSettings);
//        mSystem = new UpdateSystem();
//        mInputHandler = new InputHandler();
//        mGameState = new GameState();
//        mLogger = new ActivityLogger();
//        mSpeedQueue = new SpeedQueue();
//        mGameModelApi = new GameModelAPI();
//    }

    public void setup(int rows, int columns) {
        mGameConfigurations = GameConfigurations.getDefaults();
        if (rows > 0 && columns > 0) {
            mGameConfigurations.setMapRowsAndColumns(rows, columns);
        }
        mSystem = new UpdateSystem();
        mInputHandler = new InputHandler();
        mGameState = new GameState();
        mLogger = new ActivityLogger();
        mSpeedQueue = new SpeedQueue();
    }

    public void setGameState(String key, Object value) {
        mGameState.put(key, value);
    }
    public void setMap(JSONObject uploadedMap, JSONObject unitPlacements) {
        mTileMap = new TileMap(uploadedMap);
        if (unitPlacements != null) {
            placeUnits(unitPlacements);
        }
    }

    public void setMapV2(TileMap tileMap, JSONObject unitPlacements) {
        mTileMap = tileMap;
        if (unitPlacements != null) {
            placeUnits(unitPlacements);
        }
    }

    public void placeUnits(JSONObject unitPlacements) {
        placeUnits(mTileMap, mSpeedQueue, unitPlacements);
    }
    private void placeUnits(TileMap tileMap, SpeedQueue speedQueue, JSONObject unitPlacements) {

        // For each team
        for (String teamName : unitPlacements.keySet()) {
            JSONObject team = (JSONObject) unitPlacements.get(teamName);
            // For each unit
            for (String unitUuid : team.keySet()) {
                JSONObject unit = (JSONObject) team.get(unitUuid);
                int row = (int) unit.get("row");
                int column = (int) unit.get("column");
                String species = (String) unit.get("species");
                String nickname = (String) unit.get("name");
                String uuid = UnitPool.getInstance().create(species, nickname, unitUuid, false);
                Entity unitToPlace = UnitPool.getInstance().get(uuid);
                tileMap.place(unitToPlace, row, column);
                speedQueue.enqueue(unitToPlace, teamName);

            }
        }
    }

    public void setSpawnStrategy(String strategy) {
//        mTileMap.createLeftAndRightSpawnRegions();
////        tileMap.tryCreatingRegions(2, true);
//        tileMap.setSpawnRegion("0", 0, 0, tileMap.get);
    }

    public List<Entity> setSpawnRegion(String region, int row, int column, int width, int height) {
        return mTileMap.setSpawnRegion(region, row, column, width, height);
    }


    public boolean placeUnit(Entity entity, String team, int row, int column) {
        boolean wasPlaced = mTileMap.place(entity, row, column);
        if (wasPlaced) {
            mSpeedQueue.enqueue(new Entity[]{ entity }, team);
        }
        return wasPlaced;
    }


    public void update() {
        if (!mRunning || mSystem == null) { return; }
        mSystem.update(this);
//        UpdateSystem.update(this, controller.input);
        mCamera.update(this);

        int gameWidth = mGameConfigurations.getViewPortWidth();
        int gameHeight = mGameConfigurations.getViewPortHeight();
        boolean isLoudOutMode = mGameConfigurations.isUnitDeploymentMode();
        boolean differentWidth = gameWidth != Engine.getInstance().getController().getView().getWidth();
        boolean differentHeight = gameHeight != Engine.getInstance().getController().getView().getHeight();
        if ((differentWidth || differentHeight) && !isLoudOutMode) {
//            Engine.getInstance().getController().setSize(gameWidth, gameHeight);
//            mGameController.getView().initialize(mGameController, gameWidth, gameHeight);
        }
    }

    public void input(InputController ic) {
//        if (!mRunning) { return; }
//        mGameController.mInputController.update();



//        mInputController.update();
        // TODO figure out why input is not working for zoom
        ic.update();
        mInputHandler.handle(ic, this);
//        System.out.println("HAndling!");



//        mMousePosition.copy(mGameController.mInputController.getMouse().position);

//        if (mGameController.mInputController.getKeyboard().isPressed(KeyEvent.VK_SPACE)) {
////            setup();
//
////            System.out.println("t-t-t-t-t-t-t--t-t-t-t-tt-t-t-t");
////            initialize(controller);
//// //            TileMapIO.encode(tileMap);
////             configs = SchemaConfigs.newConfigs()
////                 .setWalling(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.WALLS_SPRITESHEET_FILEPATH).getSize()))
////                 .setFlooring(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.FLOORS_SPRITESHEET_FILEPATH).getSize()))
////                 .setSize(20, 25)
////                 .setType(4)
////                 .setZoom(.6f)
////                 .setStructure(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.STRUCTURES_SPRITESHEET_FILEPATH).getSize()))
////                 .setLiquid(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.LIQUIDS_SPRITESHEET_FILEPATH).getSize()));
//
//
////             tileMap = TileMapFactory.create(configs);
////             tileMap.place(speedQueue);
////            UserSavedData.getInstance().save(speedQueue.peek());
////            UserSavedData.getInstance().createOrRead("test.json");
////            UserSavedData.getInstance().createOrRead("tests.json");
////            logger.log("SAVING DATA");
//        }
//
//        if (mGameController.mInputController.getKeyboard().isPressed(KeyEvent.VK_P)) {
//            // logger.info("Saving map... " + UUID.randomUUID());
//            // uiLogQueue.add("Added " + UUID.randomUUID());
//
////            TileMapFactory.save(tileMap);
////            tileMap.toJson(".");
////            TileMapIO.encode(tileMap);
////            controller.getView().hideAuxPanels();
////            UserSavedData.getInstance().saveCharacter(speedQueue.peek());
////            UserSavedData.getInstance().createOrRead("tests.json");
////            logger.log("SAVING DATA");
//        }
//
//        if (mGameController.mInputController.getKeyboard().isPressed(KeyEvent.VK_COMMA)) {
//            // logger.info("Saving map... " + UUID.randomUUID());
//            // uiLogQueue.add("Added " + UUID.randomUUID());
//
////            TileMapFactory.save(tileMap);
////            tileMap.toJson(".");
////            TileMapIO.encode(tileMap);
//            mGameController.getView().hideAuxPanels();
//        }
//        System.out.println(Camera.instance().get(Vector.class).toString());
//        System.out.println(controller.input.getMouse().position);
    }

    public Entity tryFetchingTileMousedAt() {
//        Vector3f camera = mCamera.getPosition();
//        Mouse mouse = mGameController.mInputController.getMouse();
////        int titleBarHeight = Engine.getInstance().getHeaderSize();
//        int titleBarHeight = 0;
//
//        // Ensure that the moused at tile is correctly placed
//        if (mGameSettings.isUnitDeploymentMode()) {
//            camera = new Vector3f();
//            titleBarHeight = 0;
//        }
//
//        int spriteWidth = mGameSettings.getSpriteWidth();
//        int spriteHeight = mGameSettings.getSpriteHeight();
//
//        // Something about this at smaller widths and heights throws the size off
////        System.out.println("width " + spriteWidth + ", height " + spriteHeight + ", tb " + titleBarHeight);
//        int column = (int) ((mouse.position.x + camera.x) / spriteWidth);
//        int row = (int) ((mouse.position.y - titleBarHeight + camera.y) / spriteHeight);
//        return tryFetchingTileAt(row, column);
//

        Mouse mouse = mGameController.mInputController.getMouse();
        return tryFetchingTileWithXY((int) mouse.getPosition().x, (int) mouse.getPosition().y);
    }

    public Entity tryFetchingTileWithXY(int x, int y) {
        Vector3f globalCameraPosition = mCamera.getPosition();
        int spriteWidth = mGameConfigurations.getSpriteWidth();
        int spriteHeight = mGameConfigurations.getSpriteHeight();
        int column = (int) ((x + globalCameraPosition.x) / spriteWidth);
        int row = (int) ((y + globalCameraPosition.y) / spriteHeight);
        return tryFetchingTileAt(row, column);
    }

//    + Engine.getInstance().getHeaderSize()
    public boolean isRunning() { return mRunning; }
    public void run() { mRunning = true; }
    public void stop() { mRunning = false; }

    public int getRows() { return mTileMap.getRows(); }
    public int getColumns() { return mTileMap.getColumns(); }

    public double getVisibleStartOfRows() {
        Vector3f pv = mCamera.getPosition();
        int spriteHeight = mGameConfigurations.getSpriteHeight();
        return pv.y / (double) spriteHeight;
    }
    // How much our camera has moved in terms of tiles on the y axis
    public double getVisibleStartOfColumns() {
        Vector3f pv = mCamera.getPosition();
        int spriteWidth = mGameConfigurations.getSpriteWidth();
        return pv.x / (double) spriteWidth;
    }
    // How much our camera has moved in terms of tiles on the x axis on the other end of the screen (width)
    public double getVisibleEndOfColumns() {
        Vector3f pv = mCamera.getPosition();
        int screenWidth = mGameConfigurations.getViewPortWidth();
        int spriteWidth = mGameConfigurations.getSpriteWidth();
        return (pv.x + screenWidth) / spriteWidth;
    }
    // How much our camera has moved in terms of tiles on the y axis on the other end of the screen (height)
    public double getVisibleEndOfRows() {
        Vector3f pv = mCamera.getPosition();
        int screenHeight = mGameConfigurations.getViewPortHeight();
        int spriteHeight = mGameConfigurations.getSpriteHeight();
        return (pv.y + screenHeight) / (double) spriteHeight;
    }

    public TileMap getTileMap() { return mTileMap; }
    public Entity tryFetchingTileAt(int row, int column) { return mTileMap.tryFetchingEntityAt(row, column); }
    public void setSettings(String key, Object value) { mGameConfigurations.put(key, value); }
    public boolean isLoadOutMode() { return mGameConfigurations.isUnitDeploymentMode(); }
    public boolean shouldShowGameplayUI() { return mGameConfigurations.setOptionHideGameplayHUD(); }
    public void setShouldShowGameplayUI(boolean show) { mGameConfigurations.setOptionHideGameplayHUD(show); }
    public GameConfigurations getSettings() { return mGameConfigurations; }
    public Camera getCamera() { return mCamera; }
    public GameState getGameState() { return mGameState; }
    public SpeedQueue getSpeedQueue() { return mSpeedQueue; }
    public BufferedImage getBackgroundWallpaper() { return mSystem.getBackgroundWallpaper(); }
    public UpdateSystem getSystems() { return mSystem; }



//    public boolean setSelectedTile(JSONObject selectedTile) {
//        return setSelectedTiles(new JSONArray(List.of(selectedTile)));
//        return mGameModelApi.setSelected(new JSONArray(List.of(selectedTile)));
//    }
//    public boolean setSelectedTiles(JSONArray selectedTiles) {
//        return mGameState.setSelectedTiles(selectedTiles);
//    }
//    public Entity getSelectedTile() {
//        return mGameState.getSelectedTile(mTileMap);
//    }
//    public List<Entity> getSelectedTiles() {
//        return mGameState.getSelectedTiles(mTileMap);
//    }
//
//    public void updateSelectedTiles(JSONObject updatedAttributes) {
//        List<Entity> selectedTiles = getSelectedTiles();
//        selectedTiles.forEach(e -> {
//            Tile tile = e.get(Tile.class);
//            tile.putAll(updatedAttributes);
//        });
//    }

    public boolean setSelectedTile(JSONObject selectedTile) {
        return mGameModelApi.updateSelectedTiles(mGameState, mTileMap, new JSONArray(List.of(selectedTile)));
    }
    public boolean updateSelectedTiles(JSONArray selectedTiles) {
        return mGameModelApi.updateSelectedTiles(mGameState, mTileMap, selectedTiles);
    }
    public List<Tile> getSelectedTiles() {
        return mGameModelApi.getSelectedTiles(mGameState, mTileMap);
    }
    public void updateTileLayers(JSONObject updatedAttributes) {
        mGameModelApi.updateTileLayers(mGameState, mTileMap, updatedAttributes);
    }

    public void updateSpawners(JSONObject request) { mGameModelApi.updateSpawners(mGameState, mTileMap, request); }

    public JSONArray getTilesAtRowColumn(JSONObject request) {
        return mGameModelApi.getTilesAtRowColumn(mTileMap, request);
    }

    public JSONArray getTilesAtXY(JSONObject request) {
        return mGameModelApi.getTilesAtXY(mGameConfigurations, mTileMap, mCamera, request);
    }


    public void updateStructures(JSONObject request) {
        mGameModelApi.updateStructures(mGameState, mTileMap, request);
    }
}
