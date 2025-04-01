package main.game.main;

import java.util.List;

import javafx.scene.image.Image;
import main.constants.Vector3f;
import main.game.components.IdentityComponent;
import main.game.components.tile.Tile;
import main.game.events.JSONEventBus;
import main.input.InputController;
import main.input.Mouse;
import org.json.JSONArray;
import main.game.entity.Entity;
import main.game.logging.ActivityLogger;
import main.game.map.base.TileMap;
import main.game.queue.SpeedQueue;
import main.game.systems.InputHandler;
import main.game.systems.UpdateSystem;
import org.json.JSONObject;


public class GameModel {

    private TileMap mTileMap = null;
    private SpeedQueue mSpeedQueue = null;
    public ActivityLogger mLogger = null;
    public InputHandler mInputHandler = null;
    public UpdateSystem mSystem = null;
    private GameState mGameState = null;
    private boolean mRunning = false;
//    public GameModel(GameConfigs configs) { this(configs, null); }

//    public GameModel(GameConfigs configs, JSONArray map) { setup(configs, map); }
//    public void setup(GameConfigs configs, JSONArray map) {
//
//        mTileMap = new TileMap(configs, map);
//        mGameState = new GameState(configs);
//        mGameState.setSpriteWidth(configs.getStartingSpriteWidth())
//                .setSpriteHeight(configs.getStartingSpriteHeight())
//                .setMainCameraX(configs.getStartingCameraX())
//                .setMainCameraY(configs.getStartingCameraY())
//                .setMainCameraWidth(configs.getStartingCameraWidth())
//                .setMainCameraHeight(configs.getStartingCameraHeight());
//
//        if (configs.shouldCenterMapOnStartup()) {
//            Vector3f centerValues = Vector3f.getCenteredVector(
//                    0,
//                    0,
//                    configs.getStartingSpriteWidth() * configs.getColumns(),
//                    configs.getStartingSpriteHeight() * configs.getRows(),
//                    configs.getStartingCameraWidth(),
//                    configs.getStartingCameraHeight()
//            );
//            mGameState.setMainCameraX(mGameState.getMainCameraX() - centerValues.x);
//            mGameState.setMainCameraY(mGameState.getMainCameraY() - centerValues.y);
//        }
//
//        mCameraHandler = new CameraHandler();
//        mSystem = new UpdateSystem();
//        mInputHandler = new InputHandler();
//        mLogger = new ActivityLogger();
//        mSpeedQueue = new SpeedQueue();;
//    }

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

        JSONEventBus eventBus = new JSONEventBus();
        mSystem = new UpdateSystem(eventBus, this);
        mInputHandler = new InputHandler(eventBus);
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






    public TileMap getTileMap() { return mTileMap; }
    public Entity tryFetchingEntityAt(int row, int column) { return mTileMap.tryFetchingEntityAt(row, column); }
    public String tryFetchingTileEntity(int row, int column) {
        Entity tileEntity = mTileMap.tryFetchingEntityAt(row, column);
        if (tileEntity == null) { return null; }
        IdentityComponent identityComponent = tileEntity.get(IdentityComponent.class);
        String id = identityComponent.getID();
        return id;
    }
    public Tile tryFetchingTileAt(int row, int column) { return mTileMap.tryFetchingTileAt(row, column); }
    public GameState getGameState() { return mGameState; }
    public SpeedQueue getSpeedQueue() { return mSpeedQueue; }
    public Image getBackgroundWallpaper() { return mSystem.getBackgroundWallpaper(); }
    public UpdateSystem getSystems() { return mSystem; }

}
