package main.game.main;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.SplittableRandom;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.constants.Settings;
import main.engine.Engine;
import main.game.camera.Camera;
import main.game.components.Size;
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

    private TileMap tileMap = null;
    public SpeedQueue speedQueue = null;
    public ActivityLogger logger = null;
    public GameState gameState = null;
    public Vector3f mousePosition = null;
    private SplittableRandom random = null;
    private GameController mGameController = null;
    public InputHandler input = null;
    public UpdateSystem system = null;
    private Settings mSettings = Settings.getInstance();
    private boolean running = false;
    public GameModel(GameController gc) { mGameController = gc; }
    public void setGameState(String key, Object value) {
        gameState.set(key, value);
    }

    public void initialize(GameController gc, JsonObject uploadedMap, JsonObject unitPlacements) {
        mGameController = gc;

        system = new UpdateSystem();
        input = new InputHandler();
        random = new SplittableRandom();
        mousePosition = new Vector3f();
        gameState = new GameState();
        logger = new ActivityLogger();
        speedQueue = new SpeedQueue();

        tileMap = new TileMap(uploadedMap);

        if (unitPlacements != null) {
            placeUnits(unitPlacements);
        }
    }

    public void placeUnits(JsonObject unitPlacements) {
        placeUnits(tileMap, speedQueue, unitPlacements);
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
        tileMap.createLeftAndRightSpawnRegions();
////        tileMap.tryCreatingRegions(2, true);
//        tileMap.setSpawnRegion("0", 0, 0, tileMap.get);
    }

    public List<Entity> setSpawnRegion(String region, int row, int column, int width, int height) {
        return tileMap.setSpawnRegion(region, row, column, width, height);
    }


    public boolean placeUnit(Entity entity, String team, int row, int column) {
        boolean wasPlaced = tileMap.place(entity, row, column);
        if (wasPlaced) {
            speedQueue.enqueue(new Entity[]{ entity }, team);
        }
        return wasPlaced;
    }


    public void update() {
        if (!running) { return; }
        system.update(this);
//        UpdateSystem.update(this, controller.input);
        Camera.getInstance().update();

        int gameWidth = mSettings.getScreenWidth();
        int gameHeight = mSettings.getScreenHeight();
        boolean differentWidth = gameWidth != Engine.getInstance().getController().getView().getWidth();
        boolean differentHeight = gameHeight != Engine.getInstance().getController().getView().getHeight();
        if (differentWidth || differentHeight) {
            Engine.getInstance().getController().setSize(gameWidth, gameHeight);
            mGameController.getView().initialize(mGameController, gameWidth, gameHeight);
        }
    }

    public void input() {
        if (!running) { return; }
        mGameController.mInputController.update();
        input.handle(mGameController.mInputController, this);
        mousePosition.copy(mGameController.mInputController.getMouse().position);

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
        Vector3f camera = Camera.getInstance().get(Vector3f.class);
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

    public int getRows() { return tileMap.getRows(); }
    public int getColumns() { return tileMap.getColumns(); }

    public double getVisibleStartOfRows() {
        Vector3f pv = Camera.getInstance().get(Vector3f.class);
        return pv.y / (double) mSettings.getSpriteHeight();
    }
    // How much our camera has moved in terms of tiles on the y axis
    public double getVisibleStartOfColumns() {
        Vector3f pv = Camera.getInstance().get(Vector3f.class);
        return pv.x / (double) mSettings.getSpriteWidth();
    }
    // How much our camera has moved in terms of tiles on the x axis on the other end of the screen (width)
    public double getVisibleEndOfColumns() {
        Vector3f pv = Camera.getInstance().get(Vector3f.class);
        Size d = Camera.getInstance().get(Size.class);
        return (pv.x + d.width) / (double) mSettings.getSpriteWidth();
    }
    // How much our camera has moved in terms of tiles on the y axis on the other end of the screen (height)
    public double getVisibleEndOfRows() {
        Vector3f pv = Camera.getInstance().get(Vector3f.class);
        Size d = Camera.getInstance().get(Size.class);
        return (pv.y + d.height) / (double) mSettings.getSpriteHeight();
    }

    public TileMap getTileMap() { return tileMap; }
    public Entity tryFetchingTileAt(int row, int column) { return tileMap.tryFetchingTileAt(row, column); }

    public void setSettings(String key, Object value) { mSettings.set(key, value); }
    public int getIntegerSetting(String key) { return mSettings.getInteger(key); }

    public boolean getGameStateBoolean(String key) { return gameState.getBoolean(key); }
    public boolean isLoadOutMode() {
        return mSettings.isLoadOutMode();
    }
}
