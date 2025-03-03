package main.game.main;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.StackPane;
import main.game.entity.Entity;
import main.input.InputController;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.JPanel;

public class GameController {
    private final GameAPI mGameAPI;
    private final GameModel mGameModel;
    private final GameView mGameView;
    private final GameLoopManager mGameLoopManager = new GameLoopManager();

    public static GameController create(GameGenerationConfigs configs) {
        GameController newGameController = new GameController(configs);
        return newGameController;
    }

    private GameController(JSONObject configsJson) {
        GameGenerationConfigs configs = (GameGenerationConfigs) configsJson;
        mGameModel = new GameModel(configs, null);
        mGameView = new GameView(mGameModel);
        mGameAPI = new GameAPI();
    }

    public void update() {
        if (!mGameModel.isRunning()) { return; }
        mGameModel.update();
        mGameView.update(this);
    }

    public void input() {
        if (!mGameModel.isRunning()) { return; }
        InputController ic = InputController.getInstance();
        ic.update();
        mGameModel.input(ic);
    }

    public StackPane getGamePanel() {
        int width = mGameModel.getGameState().getViewportWidth();
        int height = mGameModel.getGameState().getViewportHeight();
        StackPane newGamePanel = mGameView.getViewPort(width, height);
        InputController.getInstance().setup(newGamePanel);
        return newGamePanel;
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

    public void setTileToGlideTo(JSONArray request) { mGameAPI.setTileToGlideTo(mGameModel, request); }
    public void setTileToGlideTo(String request) { setTileToGlideTo(new JSONArray().put(request)); }


    public void setSelectedTiles(JSONArray request) { mGameAPI.setSelectedTiles(mGameModel, request); }
    public void setSelectedTiles(String request) { setSelectedTiles(new JSONArray().put(request)); }








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
    public int getSelectedTilesHash() { return mGameAPI.getSelectedTilesHash(mGameModel); }
    public JSONObject getSelectedTilesInfoForMiniSelectionInfoPanel() {
        return mGameAPI.getSelectedTilesInfoForMiniSelectionInfoPanel(mGameModel);
    }

    public void setSelectedTilesV1(JSONArray request) { mGameAPI.setSelectedTilesV1(mGameModel, request); }
    public void setSelectedTilesV1(JSONObject request) { setSelectedTilesV1(new JSONArray().put(request)); }
    public void updateGameState(JSONObject request) { mGameAPI.updateGameState(mGameModel, request); }
    public void setActionOfUnitOfCurrentTurn(JSONObject request) {
        mGameAPI.setActionOfUnitOfCurrentTurn(mGameModel, request);
    }

    public void stageActionForUnit(String id, String action) { mGameAPI.stageActionForUnit(id, action); }
    public void stageActionForUnit(JSONObject request) { mGameAPI.stageActionForUnit(request); }

    public JSONObject getGameState() { return mGameAPI.getGameState(mGameModel); }

    public JSONArray getActionsOfUnit(String id) { return mGameAPI.getActionsOfUnit(id); }
    public int getUnitAttributeScaling(JSONObject request) { return mGameAPI.getUnitAttributeScaling(request); }
    public String getCurrentTurnsUnit() { return mGameAPI.getCurrentUnitOnTurn(mGameModel); }
    public JSONObject getMovementStatsOfUnit(String id) { return mGameAPI.getMovementStatsOfUnit(id); }
    public JSONObject getMovementStatsForMovementPanel(JSONObject request) {
        return mGameAPI.getMovementStatsForMovementPanel(mGameModel, request);
    }
    public JSONObject getStatisticsForUnit(JSONObject request) {
        return mGameAPI.getStatisticsForUnit(mGameModel, request);
    }

    public JSONObject getStatisticsForStatisticsPanel(JSONObject request) {
        return mGameAPI.getStatisticsForStatisticsPanel(mGameModel, request);
    }
    public String getUnitAtSelectedTiles() { return mGameAPI.getUnitAtSelectedTiles(mGameModel); }
    public JSONObject getUnitAtSelectedTilesForStandardUnitInfoPanel() {
        return mGameAPI.getSelectedUnitDataForStandardUnitInfoPanel(mGameModel);
    }
    public JSONObject getSelectedUnitStatisticsHashState() {
        return mGameAPI.getSelectedUnitStatisticsHashState(mGameModel);
    }

//    public JSONObject getUnitAtSelectedTiles() { return mGameAPI.getUnitAtSelectedTiles(mGameModel); }
    public String getUnitName(String id) { return mGameAPI.getUnitName(id); }
    public JSONObject getUnitResourceStats(JSONObject request) { return mGameAPI.getUnitResourceStats(request); }
    public JSONObject getUnitIdentifiers(JSONObject request) { return mGameAPI.getUnitIdentifiers(request); }
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

    public JSONArray getNodeBaseAndModifiedOfUnitOfCurrentTurn(JSONArray request) {
        return mGameAPI.getNodeBaseAndModifiedOfUnitOfCurrentTurn(mGameModel, request); }

    public String getNicknameOfID(String id) { return mGameAPI.getNicknameOfID(id); }

    public void setAbilitySelectedFromUI(JSONObject request) { mGameAPI.setAbilitySelectedFromUI(mGameModel, request); }
    public JSONObject getAbilitySelectedFromUI() { return mGameAPI.getAbilitySelectedFromUI(mGameModel); }
    public void setUnitSelectedFromUI(JSONObject request) {
//        mGameAPI.setUnitSelectedFromUI(mGameModel, request);
    }

}
