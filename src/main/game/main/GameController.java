package main.game.main;

import javafx.animation.AnimationTimer;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import main.engine.EngineController;
import main.engine.EngineRunnable;
import main.game.entity.Entity;
import main.graphics.AssetPool;
import main.input.InputController;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class GameController extends EngineRunnable {
    private final GameAPI mGameAPI;
    private final GameMapEditorAPI mGameMapEditorAPI;
    private final GameModel mGameModel;
    private final GameView mGameView;
    private final InputController mInputController = InputController.getInstance();

    public static GameController create(int rows, int columns, int width, int height) {

        Map<String, String> floors = AssetPool.getInstance().getBucketV2("floor_tiles");
        Map<String, String> structures = AssetPool.getInstance().getBucketV2("structures");

        GameConfigs configs = GameConfigs.getDefaults()
                .setMapGenerationRows(rows)
                .setMapGenerationColumns(columns)
                .setOnStartupSpriteWidth(96)
                .setOnStartupSpriteHeight(96)
                .setOnStartupCameraWidth(width)
                .setOnStartupCameraHeight(height)
//                .setMapGenerationNoiseZoom(.2f)
//                .setOnStartupCenterCameraOnMap(true)
                .setMapGenerationTerrainAsset(new ArrayList<>(floors.keySet()).get(new Random().nextInt(floors.size())))
                .setMapGenerationStructureAssets(structures.keySet().stream().toList().stream().findFirst().stream().toList());

        GameController newGameController = new GameController(configs);
        return newGameController;
    }

    public static GameController create(GameConfigs gc) {
        GameController newGameController = new GameController(gc);
        return newGameController;
    }

    private GameController(JSONObject configs) {
        mGameModel = new GameModel(configs, null);
        mGameView = new GameView(this);
        mGameAPI = new GameAPI(mGameModel);
        mGameMapEditorAPI = new GameMapEditorAPI();
    }

    public void start() {

//        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
//        for (GarbageCollectorMXBean gcBean : gcBeans) {
//            System.out.println("Name: " + gcBean.getName());
//            System.out.println("Collection count: " + gcBean.getCollectionCount());
//            System.out.println("Collection time: " + gcBean.getCollectionTime());
//            System.out.println("---------------------------");
//        }
//

        mUpdateAnimationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!mGameModel.isRunning()) { return; }
                processInput();
                updateGame();
            }
        };
        run();
        mUpdateAnimationTimer.start();
    }

    @Override
    public void stop() {
        super.stop();
        mGameModel.stop();
    }

    private void updateGame() {
        double deltaTime = EngineController.getInstance().getDeltaTime();
        mGameModel.getGameState().setDeltaTime(deltaTime);
        mGameModel.update();
        mGameView.update();
    }

    private void processInput() {
        mInputController.update();
        mGameModel.input(mInputController);
    }

    public GameModel getGameModel() {
        return mGameModel;
    }

    public StackPane getGamePanel() {
        int width = mGameModel.getGameState().getMainCameraWidth();
        int height = mGameModel.getGameState().getMainCameraHeight();
        StackPane newGamePanel = mGameView.getViewPort(width, height);

//        newGamePanel.setCache(true);
//        newGamePanel.setCacheHint(CacheHint.SPEED);
        return newGamePanel;
    }

    public Scene render() {
        int sceneWidth = mGameModel.getGameState().getMainCameraWidth();
        int sceneHeight = mGameModel.getGameState().getMainCameraHeight();
        return render(sceneWidth, sceneHeight);
    }

    public Scene render(int width, int height) {
        StackPane gamePanel = getGamePanel();
        int sceneWidth = width;
        int sceneHeight = height;

        gamePanel.setCache(true);
        gamePanel.setCacheHint(CacheHint.SPEED);

        Scene scene = new Scene(gamePanel, sceneWidth, sceneHeight, Color.BLUE);

        return scene;
    }


    public int getRows() { return mGameModel.getRows(); }
    public int getColumns() { return mGameModel.getColumns(); }
    public void run() { mGameModel.run(); }



    public boolean isRunning() { return mGameModel.isRunning(); }
    public boolean spawnUnit(Entity entity, String team, int row, int column) {
        return mGameModel.spawnUnit(entity, team, row, column);
    }






    public void setTileToGlideToAPI(JSONObject request) { mGameAPI.setTileToGlideTo(mGameModel, request); }

    //    public void setTileToGlideToID(String request) { setTileToGlideToID(new JSONObject().put(request)); }


    public void setSelectedTileIDsAPI(JSONArray request) { mGameAPI.setSelectedTileIDs(mGameModel, request); }
    public void setSelectedTileIDsAPI(String request) { setSelectedTileIDsAPI(new JSONArray().put(request)); }








    public void updateSpawners(JSONObject request) { mGameAPI.updateSpawners(mGameModel, request); }
    public void updateTileLayers(JSONObject request) { mGameAPI.updateTileLayers(mGameModel, request); }
    public void updateStructures(JSONObject request) { mGameAPI.updateStructures(mGameModel, request); }
    public JSONArray getTilesAtRowColumn(JSONObject request) { return mGameAPI.getTilesAtRowColumn(mGameModel, request); }
    public JSONArray getTilesAtXY(JSONObject request) { return mGameAPI.getTilesAtXY(mGameModel, request); }


    public String getTileMapJson() { return mGameModel.getTileMap().toString(2); }
    public String getSettingsJson() {
        return mGameModel.getGameState().toString(2);
    }



    public JSONObject getCurrentUnitTurnStatus() { return mGameAPI.getCurrentUnitTurnStatus(mGameModel); }
    public JSONObject getSelectedUnitsTurnState() { return mGameAPI.getSelectedUnitsTurnState(mGameModel); }
    public JSONArray getSelectedUnitsActions() { return mGameAPI.getSelectedUnitsActions(mGameModel); }
    public JSONArray getSelectedTiles() { return mGameAPI.getSelectedTiles(mGameModel); }
    public JSONArray getHoveredTiles() { return mGameAPI.getHoveredTiles(mGameModel); }
    public void getSelectedTilesChecksumAPI(JSONObject ephemeral) { mGameAPI.getSelectedTilesChecksum(mGameModel, ephemeral); }
    public JSONObject getSelectedTilesInfoForMiniSelectionInfoPanel() {
        return mGameAPI.getSelectedTilesInfoForMiniSelectionInfoPanel(mGameModel);
    }

    public void setEndTurn() { mGameAPI.setEndTurn(mGameModel); }
    public JSONObject setEntityWaitTimeBetweenActivities(JSONObject request) {
        return mGameAPI.setEntityWaitTimeBetweenActivities(request);
    }
    public JSONObject setCameraMode(JSONObject request) { return mGameAPI.setCameraMode(request); }
    public JSONArray getCameraModes() { return mGameAPI.getCameraModes(); }


    public void stageActionForUnit(String id, String action) { mGameAPI.stageActionForUnit(id, action); }
    public void stageActionForUnit(JSONObject request) { mGameAPI.stageActionForUnit(request); }
    public JSONArray getActionsOfUnit(String id) { return mGameAPI.getAbilitiesOfUnitEntity(id); }
    public JSONArray getAllUnitIDs() { return mGameAPI.getAllUnitIDs(); }
    public JSONArray getAbilitiesOfUnitEntity(JSONObject request) { return mGameAPI.getAbilitiesOfUnitEntity(request); }
    public int getUnitAttributeScaling(JSONObject request) { return mGameAPI.getUnitAttributeScaling(request); }
    public JSONObject getEntityOfCurrentTurnsID() {  return mGameAPI.getEntityOfCurrentTurnsID(); }
    public JSONObject getMovementStatsOfUnit(String id) { return mGameAPI.getMovementStatsOfUnit(id); }
    public JSONObject getMovementStatsForMovementPanel(JSONObject request) {
        return mGameAPI.getMovementStatsForMovementPanel(mGameModel, request);
    }
    public JSONObject getStatisticsForUnit(JSONObject request) {
        return mGameAPI.getStatisticsForUnit(mGameModel, request);
    }

//    public String getUnitAtSelectedTiles() { return mGameAPI.getUnitAtSelectedTiles(mGameModel); }

    public JSONArray getEntityIDsAtSelectedTiles() { return mGameAPI.getEntityIDsAtSelectedTiles(mGameModel); }
    public void setAbilityPanelIsOpen(boolean isOpen) { mGameAPI.setAbilityPanelIsOpen(mGameModel, isOpen); }
    public void setMovementPanelIsOpen(boolean isOpen) { mGameAPI.setMovementPanelIsOpen(mGameModel, isOpen); }
    public void setStatisticsPanelIsOpen(boolean isOpen) { mGameAPI.setStatisticsPanelIsOpen(mGameModel, isOpen); }
    public JSONArray getUnitStatsForMiniUnitInfoPanel(JSONObject request) {
        return mGameAPI.getUnitStatsForMiniUnitInfoPanel(request);
    }

//    public JSONArray getActionsOfUnitOfCurrentTurn() { return mGameAPI.getActionsOfUnitOfCurrentTurn(mGameModel); }
//    public JSONObject getMovementStatsOfUnitOfCurrentTurn() { return
//            mGameAPI.getMovementStatsOfUnitOfCurrentTurn(mGameModel);
//    }
//    public JSONObject getUnitsOnSelectedTiles() { return mGameAPI.getUnitsOnSelectedTiles(mGameModel); }
    public boolean consumeShouldAutomaticallyGoToHomeControls() {
        return mGameAPI.consumeShouldAutomaticallyGoToHomeControls(mGameModel);
    }



    public String getState() { return mGameModel.getGameState().toString(2); }

    /**
     *
     * For Operations involving the turn order queue
     *
     * ████████╗██╗   ██╗██████╗ ███╗   ██╗     ██████╗ ██╗   ██╗███████╗██╗   ██╗███████╗     █████╗ ██████╗ ██╗
     * ╚══██╔══╝██║   ██║██╔══██╗████╗  ██║    ██╔═══██╗██║   ██║██╔════╝██║   ██║██╔════╝    ██╔══██╗██╔══██╗██║
     *    ██║   ██║   ██║██████╔╝██╔██╗ ██║    ██║   ██║██║   ██║█████╗  ██║   ██║█████╗      ███████║██████╔╝██║
     *    ██║   ██║   ██║██╔══██╗██║╚██╗██║    ██║▄▄ ██║██║   ██║██╔══╝  ██║   ██║██╔══╝      ██╔══██║██╔═══╝ ██║
     *    ██║   ╚██████╔╝██║  ██║██║ ╚████║    ╚██████╔╝╚██████╔╝███████╗╚██████╔╝███████╗    ██║  ██║██║     ██║
     *    ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═══╝     ╚══▀▀═╝  ╚═════╝ ╚══════╝ ╚═════╝ ╚══════╝    ╚═╝  ╚═╝╚═╝     ╚═╝
     */
    public void turnOrderAPI() { }
    public JSONArray getAllEntitiesInTurnQueueFinishedTurn() {
        return mGameAPI.getAllEntitiesInTurnQueueFinishedTurn(mGameModel);
    }
    public JSONArray getAllEntitiesInTurnQueuePendingTurn() {
        return mGameAPI.getAllEntitiesInTurnQueuePendingTurn(mGameModel);
    }
    public JSONArray getAllEntitiesInTurnQueue() {
        return mGameAPI.getAllEntitiesInTurnQueue(mGameModel);
    }
    public void getTurnQueueChecksumsAPI(JSONObject out) { mGameAPI.getTurnQueueChecksums(mGameModel, out); }



    public void setHoveredTilesCursorSizeAPI(JSONObject request) {
        mGameMapEditorAPI.setHoveredTilesCursorSizeAPI(mGameModel, request);
    }


    public void setConfigurableStateGameplayHudIsVisible(boolean value) {
        mGameAPI.setConfigurableStateGameplayHudIsVisible(mGameModel, value);
    }
    public boolean getConfigurableStateGameplayHudIsVisible() {
        return mGameAPI.getConfigurableStateGameplayHudIsVisible(mGameModel);
    }










    public JSONObject getMainCameraInfoAPI() { return mGameAPI.getMainCameraInfo(mGameModel); }
    public JSONObject getSecondaryCameraInfoAPI() { return mGameAPI.getSecondaryCameraInfo(mGameModel); }

    public void getEntityOnSelectedTilesChecksumAPI(JSONObject out) {
        mGameAPI.getEntityOnSelectedTilesChecksum(mGameModel, out);
    }

    public void getCurrentActiveEntityWithStatisticsChecksumAPI(JSONObject out) {
        mGameAPI.getCurrentTurnsEntityAndStatisticsChecksum(mGameModel, out);
    }


    public JSONArray getEntityTileID(JSONObject request) { return mGameAPI.getEntityTileID(request); }
    public JSONArray getCurrentActiveEntityTileID(JSONObject request) {
        return mGameAPI.getCurrentActiveEntityTileID(request);
    }

    public JSONObject getDataForGreaterStatisticsInformationPanel(JSONObject request) {
        return mGameAPI.getDataForGreaterStatisticsInformationPanel(mGameModel, request);
    }

    public void setCameraZoomAPI(JSONObject request) {
        mGameAPI.setCameraZoomAPI(mGameModel, request);
    }

    public JSONObject getCenterTileEntityAPI() {
        return mGameAPI.getCenterTileEntity(mGameModel);
    }

    public JSONArray getTileDetailsFromGameMapEditorAPI() {
        return mGameMapEditorAPI.getHoveredTileDetailsFromGameMapEditorAPI(mGameModel);
    }

    public void publishEvent(JSONObject event) {
        mGameAPI.publishEvent(mGameModel, event);
    }

    public static JSONObject createEvent(String event_id, Object... values) {
        JSONObject event = new JSONObject();
        if (values.length % 2 != 0) { return null; }
        for (int i = 0; i < values.length; i += 2) {
            String key = String.valueOf(values[i]);
            Object value = values[i + 1];
            event.put(key, value);
        }
        event.put("event_id", event_id);
        return event;
    }

    public JSONObject focusCamerasAndSelectionsOnActiveEntity(JSONObject request) {
        return mGameAPI.focusCamerasAndSelectionsOnActiveEntity(request);
    }
}
