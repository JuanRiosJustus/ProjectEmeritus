package main.game.main;

import java.awt.event.KeyEvent;
import java.util.List;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.constants.Settings;
import main.engine.Engine;
import main.game.camera.Camera;
import main.game.components.Vector3f;
import main.game.entity.Entity;
import main.game.logging.ActivityLogger;
import main.game.map.base.TileMap;
import main.game.queue.SpeedQueue;
import main.game.stores.pools.unit.UnitPool;
import main.game.systems.InputHandler;
import main.game.systems.UpdateSystem;
import main.input.Mouse;
import main.constants.GameState;


public class GameModel {

    private TileMap mtileMap = null;
    public SpeedQueue mSpeedQueue = null;
    public ActivityLogger mLogger = null;
    public GameState mGameState = null;
    public Vector3f mMousePosition = null;
    private GameController mGameController = null;
    public InputHandler mInputHandler = null;
    public UpdateSystem mSystem = null;
    private Settings mSettings = null;
    private final Camera mCamera = Camera.getInstance();
    private boolean running = false;

    public GameModel(GameController gc) { mGameController = gc; }
    public void setGameState(String key, Object value) {
        mGameState.put(key, value);
    }

    public void initialize(GameController gc, JsonObject uploadedMap, JsonObject unitPlacements) {
        mGameController = gc;

        mSettings = Settings.getDefaults();
        mSystem = new UpdateSystem();
        mInputHandler = new InputHandler();
        mMousePosition = new Vector3f();
        mGameState = new GameState();
        mLogger = new ActivityLogger();
        mSpeedQueue = new SpeedQueue();

        mtileMap = new TileMap(uploadedMap);
        if (unitPlacements != null) {
            placeUnits(unitPlacements);
        }
    }

    public void placeUnits(JsonObject unitPlacements) {
        placeUnits(mtileMap, mSpeedQueue, unitPlacements);
    }
    private void placeUnits(TileMap tileMap, SpeedQueue speedQueue, JsonObject unitPlacements) {

        // For each team
        for (String teamName : unitPlacements.keySet()) {
            JsonObject team = (JsonObject) unitPlacements.get(teamName);
            // For each unit
            for (String unitUuid : team.keySet()) {
                JsonObject unit = (JsonObject) team.get(unitUuid);
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
        mtileMap.createLeftAndRightSpawnRegions();
////        tileMap.tryCreatingRegions(2, true);
//        tileMap.setSpawnRegion("0", 0, 0, tileMap.get);
    }

    public List<Entity> setSpawnRegion(String region, int row, int column, int width, int height) {
        return mtileMap.setSpawnRegion(region, row, column, width, height);
    }


    public boolean placeUnit(Entity entity, String team, int row, int column) {
        boolean wasPlaced = mtileMap.place(entity, row, column);
        if (wasPlaced) {
            mSpeedQueue.enqueue(new Entity[]{ entity }, team);
        }
        return wasPlaced;
    }


    public void update() {
        if (!running) { return; }
        mSystem.update(this);
//        UpdateSystem.update(this, controller.input);
        mCamera.update(this);

        int gameWidth = mSettings.getScreenWidth();
        int gameHeight = mSettings.getScreenHeight();
        boolean isLoudOutMode = mSettings.isLoadOutMode();
        boolean differentWidth = gameWidth != Engine.getInstance().getController().getView().getWidth();
        boolean differentHeight = gameHeight != Engine.getInstance().getController().getView().getHeight();
        if ((differentWidth || differentHeight) && !isLoudOutMode) {
//            Engine.getInstance().getController().setSize(gameWidth, gameHeight);
//            mGameController.getView().initialize(mGameController, gameWidth, gameHeight);
        }
    }

    public void input() {
        if (!running) { return; }
        mGameController.mInputController.update();
        mInputHandler.handle(mGameController.mInputController, this);
        mMousePosition.copy(mGameController.mInputController.getMouse().position);

        if (mGameController.mInputController.getKeyboard().isPressed(KeyEvent.VK_SPACE)) {
//            setup();

//            System.out.println("t-t-t-t-t-t-t--t-t-t-t-tt-t-t-t");
//            initialize(controller);
// //            TileMapIO.encode(tileMap);
//             configs = SchemaConfigs.newConfigs()
//                 .setWalling(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.WALLS_SPRITESHEET_FILEPATH).getSize()))
//                 .setFlooring(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.FLOORS_SPRITESHEET_FILEPATH).getSize()))
//                 .setSize(20, 25)
//                 .setType(4)
//                 .setZoom(.6f)
//                 .setStructure(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.STRUCTURES_SPRITESHEET_FILEPATH).getSize()))
//                 .setLiquid(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.LIQUIDS_SPRITESHEET_FILEPATH).getSize()));

                    
//             tileMap = TileMapFactory.create(configs);
//             tileMap.place(speedQueue);
//            UserSavedData.getInstance().save(speedQueue.peek());
//            UserSavedData.getInstance().createOrRead("test.json");
//            UserSavedData.getInstance().createOrRead("tests.json");
//            logger.log("SAVING DATA");
        }

        if (mGameController.mInputController.getKeyboard().isPressed(KeyEvent.VK_P)) {
            // logger.info("Saving map... " + UUID.randomUUID());
            // uiLogQueue.add("Added " + UUID.randomUUID());

//            TileMapFactory.save(tileMap);
//            tileMap.toJson(".");
//            TileMapIO.encode(tileMap);
//            controller.getView().hideAuxPanels();
//            UserSavedData.getInstance().saveCharacter(speedQueue.peek());
//            UserSavedData.getInstance().createOrRead("tests.json");
//            logger.log("SAVING DATA");
        }

        if (mGameController.mInputController.getKeyboard().isPressed(KeyEvent.VK_COMMA)) {
            // logger.info("Saving map... " + UUID.randomUUID());
            // uiLogQueue.add("Added " + UUID.randomUUID());

//            TileMapFactory.save(tileMap);
//            tileMap.toJson(".");
//            TileMapIO.encode(tileMap);
            mGameController.getView().hideAuxPanels();
        }
//        System.out.println(Camera.instance().get(Vector.class).toString());
//        System.out.println(controller.input.getMouse().position);
    }

    public Entity tryFetchingTileMousedAt() {
        Vector3f camera = mCamera.getPosition();
        Mouse mouse = mGameController.mInputController.getMouse();
        int titleBarHeight = Engine.getInstance().getHeaderSize();

        // Ensure that the moused at tile is correctly placed
        if (mSettings.isLoadOutMode()) {
            camera = new Vector3f();
            titleBarHeight = 0;
        }

        int spriteWidth = mSettings.getSpriteWidth();
        int spriteHeight = mSettings.getSpriteHeight();
        int column = (int) ((mouse.position.x + camera.x) / spriteWidth);
        int row = (int) ((mouse.position.y - titleBarHeight + camera.y) / spriteHeight);
        return tryFetchingTileAt(row, column);
    }

    public boolean isRunning() { return running; }
    public void run() { running = true; }
    public void stop() { running = false; }

    public int getRows() { return mtileMap.getRows(); }
    public int getColumns() { return mtileMap.getColumns(); }

    public double getVisibleStartOfRows() {
        Vector3f pv = mCamera.getPosition();
        int spriteHeight = mSettings.getSpriteHeight();
        return pv.y / (double) spriteHeight;
    }
    // How much our camera has moved in terms of tiles on the y axis
    public double getVisibleStartOfColumns() {
        Vector3f pv = mCamera.getPosition();
        int spriteWidth = mSettings.getSpriteWidth();
        return pv.x / (double) spriteWidth;
    }
    // How much our camera has moved in terms of tiles on the x axis on the other end of the screen (width)
    public double getVisibleEndOfColumns() {
        Vector3f pv = mCamera.getPosition();
        int screenWidth = mSettings.getScreenWidth();
        int spriteWidth = mSettings.getSpriteWidth();
        return (pv.x + screenWidth) / spriteWidth;
    }
    // How much our camera has moved in terms of tiles on the y axis on the other end of the screen (height)
    public double getVisibleEndOfRows() {
        Vector3f pv = mCamera.getPosition();
        int screenHeight = mSettings.getScreenHeight();
        int spriteHeight = mSettings.getSpriteHeight();
        return (pv.y + screenHeight) / (double) spriteHeight;
    }

    public TileMap getTileMap() { return mtileMap; }
    public Entity tryFetchingTileAt(int row, int column) { return mtileMap.tryFetchingTileAt(row, column); }
    public void setSettings(String key, Object value) { mSettings.put(key, value); }
    public int getIntegerSetting(String key) { return mSettings.getInteger(key); }
    public boolean getGameStateBoolean(String key) { return mGameState.getBoolean(key); }
    public boolean isLoadOutMode() { return mSettings.isLoadOutMode(); }
    public Settings getSettings() { return mSettings; }
    public Camera getCamera() { return mCamera; }
    public GameState getGameState() { return mGameState; }
    public Entity getSelectedEntity() { return (Entity) mGameState.get(GameState.CURRENTLY_SELECTED); }
}
