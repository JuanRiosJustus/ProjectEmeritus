package main.game.main;

import java.awt.image.BufferedImage;
import java.util.List;

import main.constants.Vector3f;
import main.game.camera.Camera;
import main.game.components.tile.Tile;
import org.json.JSONArray;
import org.json.JSONObject;
import main.engine.Engine;
import main.game.entity.Entity;
import main.game.logging.ActivityLogger;
import main.game.map.base.TileMap;
import main.game.queue.SpeedQueue;
import main.game.stores.pools.UnitDatabase;
import main.game.systems.InputHandler;
import main.game.systems.UpdateSystem;
import main.input.InputController;
import main.input.Mouse;


public class GameModel {

    private TileMap mTileMap = null;
    private SpeedQueue mSpeedQueue = null;
    public ActivityLogger mLogger = null;
    public InputHandler mInputHandler = null;
    public UpdateSystem mSystem = null;
    private GameState mGameState = null;
    private Camera mCamera = null;
    private boolean mRunning = false;
    public GameModel(GameGenerationConfigs configs) { this(configs, null); }
    public GameModel(GameGenerationConfigs configs, JSONArray map) { setup(configs, map); }
    public void setup(GameGenerationConfigs configs, JSONArray map) {

        mTileMap = new TileMap(configs, map);
        mGameState = new GameState();
        mGameState.setSpriteWidth(configs.getStartingSpriteWidth())
                .setSpriteHeight(configs.getStartingSpriteHeight())
                .setCameraX(configs.getStartingCameraX())
                .setCameraY(configs.getStartingCameraY())
                .setViewportWidth(configs.getStartingViewportWidth())
                .setViewportHeight(configs.getStartingViewportHeight());

        // This centers the camera on the map
        if (configs.shouldCenterMapOnStartup()) {
            Vector3f centerValues = Vector3f.getCenteredVector(
                    0,
                    0,
                    configs.getStartingSpriteWidth() * configs.getMapColumns(),
                    configs.getStartingSpriteHeight() * configs.getMapRows(),
                    configs.getStartingViewportWidth(),
                    configs.getStartingViewportHeight()
            );
            mGameState.setCameraX(mGameState.getCameraX() - centerValues.x);
            mGameState.setCameraY(mGameState.getCameraY() - centerValues.y);
        }

        mCamera = new Camera();
        mSystem = new UpdateSystem();
        mInputHandler = new InputHandler();
        mLogger = new ActivityLogger();
        mSpeedQueue = new SpeedQueue();;
    }


    public void setMap(JSONObject uploadedMap, JSONObject unitPlacements) {
        mTileMap = new TileMap(uploadedMap);
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
                String uuid = UnitDatabase.getInstance().create(species, nickname, unitUuid, false);
                Entity unitToPlace = UnitDatabase.getInstance().get(uuid);
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
//        mCamera.update(this);
        mCamera.update(mGameState);

        int gameWidth = mGameState.getViewportWidth();
        int gameHeight = mGameState.getViewportHeight();
        boolean isLoudOutMode = mGameState.isUnitDeploymentMode();
        boolean differentWidth = gameWidth != Engine.getInstance().getController().getView().getWidth();
        boolean differentHeight = gameHeight != Engine.getInstance().getController().getView().getHeight();
        if ((differentWidth || differentHeight) && !isLoudOutMode) {
//            Engine.getInstance().getController().setSize(gameWidth, gameHeight);
//            mGameController.getView().initialize(mGameController, gameWidth, gameHeight);
        }
    }

    public void input() {
        InputController.getInstance().update();
        mInputHandler.input(mGameState, mCamera, InputController.getInstance(), this);
    }

    public Entity tryFetchingTileMousedAt() {
        Mouse mouse = InputController.getInstance().getMouse();
        Entity mousedAt = tryFetchingTileWithXY((int) mouse.getPosition().x, (int) mouse.getPosition().y);
        return mousedAt;
    }

    public Entity tryFetchingTileWithXY(int x, int y) {
        int cameraX = mGameState.getCameraX();
        int cameraY = mGameState.getCameraY();
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
        int x = mGameState.getCameraX();
        int spriteWidth = mGameState.getSpriteWidth();
        return x / (double) spriteWidth;
    }
    // How much our camera has moved in terms of tiles on the x axis on the other end of the screen (width)
    public double getVisibleEndOfColumns() {
        int x = mGameState.getCameraX();
        int screenWidth = mGameState.getViewportWidth();
        int spriteWidth = mGameState.getSpriteWidth();
        return (double) (x + screenWidth) / spriteWidth;
    }
    public double getVisibleStartOfRows() {
        int y = mGameState.getCameraY();
        int spriteHeight = mGameState.getSpriteHeight();
        return y / (double) spriteHeight;
    }
    // How much our camera has moved in terms of tiles on the y axis on the other end of the screen (height)
    public double getVisibleEndOfRows() {
        int y = mGameState.getCameraY();
        int screenHeight = mGameState.getViewportHeight();
        int spriteHeight = mGameState.getSpriteHeight();
        return (double) (y + screenHeight) / spriteHeight;
    }

    public TileMap getTileMap() { return mTileMap; }
    public Entity tryFetchingEntityAt(int row, int column) { return mTileMap.tryFetchingEntityAt(row, column); }
    public Tile tryFetchingTileAt(int row, int column) { return mTileMap.tryFetchingTileAt(row, column); }
    public boolean isLoadOutMode() { return mGameState.isUnitDeploymentMode(); }
    public boolean shouldShowGameplayUI() { return mGameState.setOptionHideGameplayHUD(); }
    public GameState getGameState() { return mGameState; }
    public SpeedQueue getSpeedQueue() { return mSpeedQueue; }
    public BufferedImage getBackgroundWallpaper() { return mSystem.getBackgroundWallpaper(); }
    public UpdateSystem getSystems() { return mSystem; }

}
