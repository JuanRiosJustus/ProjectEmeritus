package main.game.main;

import java.awt.image.BufferedImage;
import java.util.List;

import main.constants.Vector3f;
import main.game.camera.CameraHandler;
import main.game.components.IdentityComponent;
import main.game.components.tile.Tile;
import main.input.InputControllerV1;
import main.input.InputController;
import main.input.Mouse;
import main.input.MouseV1;
import org.json.JSONArray;
import main.engine.Engine;
import main.game.entity.Entity;
import main.game.logging.ActivityLogger;
import main.game.map.base.TileMap;
import main.game.queue.SpeedQueue;
import main.game.systems.InputHandler;
import main.game.systems.UpdateSystem;


public class GameModel {

    private TileMap mTileMap = null;
    private SpeedQueue mSpeedQueue = null;
    public ActivityLogger mLogger = null;
    public InputHandler mInputHandler = null;
    public UpdateSystem mSystem = null;
    private GameState mGameState = null;
    private CameraHandler mCameraHandler = null;
    private boolean mRunning = false;
    public GameModel(GameGenerationConfigs configs) { this(configs, null); }
    public GameModel(GameGenerationConfigs configs, JSONArray map) { setup(configs, map); }
    public void setup(GameGenerationConfigs configs, JSONArray map) {

        mTileMap = new TileMap(configs, map);
        mGameState = new GameState(configs);
        mGameState.setSpriteWidth(configs.getStartingSpriteWidth())
                .setSpriteHeight(configs.getStartingSpriteHeight())
                .setCameraX(configs.getStartingCameraX())
                .setCameraY(configs.getStartingCameraY())
                .setViewportWidth(configs.getStartingViewportWidth())
                .setViewportHeight(configs.getStartingViewportHeight());

        if (configs.shouldCenterMapOnStartup()) {
            Vector3f centerValues = Vector3f.getCenteredVector(
                    0,
                    0,
                    configs.getStartingSpriteWidth() * configs.getColumns(),
                    configs.getStartingSpriteHeight() * configs.getRows(),
                    configs.getStartingViewportWidth(),
                    configs.getStartingViewportHeight()
            );
            mGameState.setCameraX(mGameState.getCameraX() - centerValues.x);
            mGameState.setCameraY(mGameState.getCameraY() - centerValues.y);
        }

        mCameraHandler = new CameraHandler();
        mSystem = new UpdateSystem();
        mInputHandler = new InputHandler();
        mLogger = new ActivityLogger();
        mSpeedQueue = new SpeedQueue();;
    }


//    public void setMap(JSONObject uploadedMap, JSONObject unitPlacements) {
//        mTileMap = new TileMap(uploadedMap);
//        if (unitPlacements != null) {
//            placeUnits(unitPlacements);
//        }
//    }
//
//    public void setMapV2(TileMap tileMap, JSONObject unitPlacements) {
//        mTileMap = tileMap;
//        if (unitPlacements != null) {
//            placeUnits(unitPlacements);
//        }
//    }

//    public void placeUnits(JSONObject unitPlacements) {
//        placeUnits(mTileMap, mSpeedQueue, unitPlacements);
//    }
//    private void placeUnits(TileMap tileMap, SpeedQueue speedQueue, JSONObject unitPlacements) {
//
//        // For each team
//        for (String teamName : unitPlacements.keySet()) {
//            JSONObject team = (JSONObject) unitPlacements.get(teamName);
//            // For each unit
//            for (String unitUuid : team.keySet()) {
//                JSONObject unit = (JSONObject) team.get(unitUuid);
//                int row = (int) unit.get("row");
//                int column = (int) unit.get("column");
//                String species = (String) unit.get("species");
//                String nickname = (String) unit.get("name");
//                String uuid = EntityStore.getInstance().getOrCreateUnit(species, nickname, unitUuid, false);
//                Entity unitToPlace = EntityStore.getInstance().get(uuid);
//                tileMap.spawnUnit(unitToPlace, row, column);
//                speedQueue.enqueue(unitToPlace, teamName);
//
//            }
//        }
//    }

    public void setSpawnStrategy(String strategy) {
//        mTileMap.createLeftAndRightSpawnRegions();
////        tileMap.tryCreatingRegions(2, true);
//        tileMap.setSpawnRegion("0", 0, 0, tileMap.get);
    }

    public List<Entity> setSpawnRegion(String region, int row, int column, int width, int height) {
        return mTileMap.setSpawnRegion(region, row, column, width, height);
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
//        UpdateSystem.update(this, controller.input);
//        mCamera.update(this);
        mCameraHandler.update(mGameState);
    }

    public void input(InputController ic) {
        mInputHandler.input(mGameState, mCameraHandler, ic, this);
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
    public String tryFetchingTileEntity(int row, int column) {
        Entity tileEntity = mTileMap.tryFetchingEntityAt(row, column);
        IdentityComponent identityComponent = tileEntity.get(IdentityComponent.class);
        return identityComponent.getID();
    }
    public Tile tryFetchingTileAt(int row, int column) { return mTileMap.tryFetchingTileAt(row, column); }
    public boolean isLoadOutMode() { return mGameState.isUnitDeploymentMode(); }
    public boolean shouldShowGameplayUI() { return mGameState.setOptionHideGameplayHUD(); }
    public GameState getGameState() { return mGameState; }
    public SpeedQueue getSpeedQueue() { return mSpeedQueue; }
    public BufferedImage getBackgroundWallpaper() { return mSystem.getBackgroundWallpaper(); }
    public UpdateSystem getSystems() { return mSystem; }

}
