package main.game.main;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import main.game.entity.Entity;
import main.input.InputController;
import org.json.JSONArray;
import org.json.JSONObject;

public class GameController {
    private final GameAPI mGameAPI;
    private final GameModel mGameModel;
    private final GameView mGameView;
    private final GameLoopManager mGameLoopManager = new GameLoopManager();
    private final InputController mInputController = InputController.getInstance();

    public static GameController create(GameGenerationConfigs configs) {
        GameController newGameController = new GameController(configs);
        return newGameController;
    }

    private GameController(JSONObject configsJson) {
        GameGenerationConfigs configs = (GameGenerationConfigs) configsJson;
        mGameModel = new GameModel(configs, null);
        mGameView = new GameView(this);
        mGameAPI = new GameAPI();
    }

    public void update() {
        if (!mGameModel.isRunning()) { return; }
        double deltaTime = mGameLoopManager.getDeltaTime();
        mGameModel.getGameState().setDeltaTime(deltaTime);
        mGameModel.update();
        mGameView.update();
    }

    public void input() {
        if (!mGameModel.isRunning()) { return; }
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
        InputController.getInstance().setup(newGamePanel);
        return newGamePanel;
    }

    public Scene getGameScene() {
        int sceneWidth = mGameModel.getGameState().getMainCameraWidth();
        int sceneHeight = mGameModel.getGameState().getMainCameraHeight();
        return getGameScene(sceneWidth, sceneHeight);
    }

    public Scene getGameScene(int width, int height) {
        StackPane gamePanel = getGamePanel();
        int sceneWidth = width;
        int sceneHeight = height;
        Scene scene = new Scene(gamePanel, sceneWidth, sceneHeight, Color.BLACK);
        return scene;
    }


    public Scene getMenuScene() {
        int sceneWidth = mGameModel.getGameState().getMainCameraWidth();
        int sceneHeight = mGameModel.getGameState().getMainCameraHeight();
        return getMenuScene(sceneWidth, sceneHeight);
    }

    public Scene getMenuScene(int width, int height) {
        StackPane gamePanel = getGamePanel();
        int sceneWidth = width;
        int sceneHeight = height;

        Scene scene = new Scene(new StackPane(), sceneWidth, sceneHeight, Color.BLACK);
        return scene;
    }

    public int getRows() { return mGameModel.getRows(); }
    public int getColumns() { return mGameModel.getColumns(); }
    public void run() { mGameModel.run(); }

    public void runGameLoop() {
        final AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                mGameLoopManager.startLoop(now);

                if (mGameLoopManager.shouldUpdate()) {
                    if (!mGameModel.isRunning()) { return; }
                    input();
                    update();
                    mGameLoopManager.handleUpdate();
                }
                mGameLoopManager.endLoop();
            }
        };

        run();
        gameLoop.start();
    }

    public boolean isRunning() { return mGameModel.isRunning(); }
    public boolean spawnUnit(Entity entity, String team, int row, int column) {
        return mGameModel.spawnUnit(entity, team, row, column);
    }





    public JSONArray getCurrentTurnsUnitsTile() { return mGameAPI.getCurrentTurnsUnitsTile(mGameModel); }

    public void setTileToGlideToAPI(JSONObject request) { mGameAPI.setTileToGlideTo(mGameModel, request); }

    //    public void setTileToGlideToID(String request) { setTileToGlideToID(new JSONObject().put(request)); }


    public void setSelectedTileIdsAPI(JSONArray request) { mGameAPI.setSelectedTileIDs(mGameModel, request); }
    public void setSelectedTileIdsAPI(String request) { setSelectedTileIdsAPI(new JSONArray().put(request)); }








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
    public JSONArray getSelectedUnitsActions() { return mGameAPI.getSelectedUnitsActions(mGameModel); }
    public JSONArray getSelectedTiles() { return mGameAPI.getSelectedTiles(mGameModel); }
    public JSONArray getHoveredTiles() { return mGameAPI.getHoveredTiles(mGameModel); }
    public void getSelectedTilesChecksumAPI(JSONObject ephemeral) { mGameAPI.getSelectedTilesChecksum(mGameModel, ephemeral); }
    public JSONObject getSelectedTilesInfoForMiniSelectionInfoPanel() {
        return mGameAPI.getSelectedTilesInfoForMiniSelectionInfoPanel(mGameModel);
    }

    public void setSelectedTilesV1(JSONArray request) { mGameAPI.setSelectedTilesV1(mGameModel, request); }
    public void setSelectedTilesV1(JSONObject request) { setSelectedTilesV1(new JSONArray().put(request)); }
    public void updateGameState(JSONObject request) { mGameAPI.updateGameState(mGameModel, request); }
    public void setEndTurn() { mGameAPI.setEndTurn(mGameModel); }
    public void setActionOfUnitOfCurrentTurn(JSONObject request) {
        mGameAPI.setActionOfUnitOfCurrentTurn(mGameModel, request);
    }

    public void stageActionForUnit(String id, String action) { mGameAPI.stageActionForUnit(id, action); }
    public void stageActionForUnit(JSONObject request) { mGameAPI.stageActionForUnit(request); }

    public JSONObject getGameState() { return mGameAPI.getGameState(mGameModel); }

    public JSONArray getActionsOfUnit(String id) { return mGameAPI.getActionsOfUnit(id); }
    public int getUnitAttributeScaling(JSONObject request) { return mGameAPI.getUnitAttributeScaling(request); }
    public String getCurrentTurnsUnit(JSONObject response) { return mGameAPI.getCurrentUnitOnTurn(mGameModel); }
    public JSONObject getCurrentTurnsEntity() {  return mGameAPI.getCurrentTurnsEntity(mGameModel); }
    public JSONObject getMovementStatsOfUnit(String id) { return mGameAPI.getMovementStatsOfUnit(id); }
    public JSONObject getMovementStatsForMovementPanel(JSONObject request) {
        return mGameAPI.getMovementStatsForMovementPanel(mGameModel, request);
    }
    public JSONObject getStatisticsForUnit(JSONObject request) {
        return mGameAPI.getStatisticsForUnit(mGameModel, request);
    }

//    public String getUnitAtSelectedTiles() { return mGameAPI.getUnitAtSelectedTiles(mGameModel); }

    public JSONArray getUnitsAtSelectedTilesAPI() { return mGameAPI.getUnitsAtSelectedTiles(mGameModel); }
    public void setActionPanelIsOpen(boolean isOpen) { mGameAPI.setAbilityPanelIsOpen(mGameModel, isOpen); }
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



    public JSONObject getMainCameraInfoAPI() { return mGameAPI.getMainCameraInfo(mGameModel); }
    public JSONObject getSecondaryCameraInfoAPI() { return mGameAPI.getSecondaryCameraInfo(mGameModel); }

    public void getEntityOnSelectedTilesChecksumAPI(JSONObject out) {
        mGameAPI.getEntityOnSelectedTilesChecksum(mGameModel, out);
    }

    public void getCurrentActiveEntityWithStatisticsChecksumAPI(JSONObject out) {
        mGameAPI.getCurrentTurnsEntityAndStatisticsChecksum(mGameModel, out);
    }


    public JSONArray getCurrentTileIDOfUnit(JSONObject request) {
        return mGameAPI.getCurrentTileIDOfUnit(mGameModel, request);
    }

    public JSONObject getDataForGreaterStatisticsInformationPanel(JSONObject request) {
        return mGameAPI.getDataForGreaterStatisticsInformationPanel(mGameModel, request);
    }
}
