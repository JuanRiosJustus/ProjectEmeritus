package main.game.main;

import javafx.scene.image.Image;
import main.constants.Vector3f;
import main.game.components.IdentityComponent;
import main.game.components.MovementComponent;
import main.game.components.TileComponent;
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

import java.util.List;


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
}
