package main.game.main;

import org.json.JSONArray;
import org.json.JSONObject;
import main.engine.EngineScene;
import main.game.entity.Entity;
import main.input.InputController;

import javax.swing.*;
import java.util.List;

public class GameController extends EngineScene {

    private GameAPI mGameAPI;
    private GameModel mGameModel;
    private GameView mGameView;
    private static GameController mInstance = null;

    public static GameController getInstance() {
        if (mInstance == null) {
//            mInstance = new GameController(screenWidth, screenHeight);
        }
        return mInstance;
    }

    public GameController create(int viewWidth, int viewHeight, int rows, int columns, int spriteWidth, int spriteHeight) {
//        GameDataStore newGameDataStore = GameDataStore.getDefaults()
//                .setMapGenerationStep1MapRows(rows)
//                .setMapGenerationStep2MapColumns(columns)
//                .setViewportWidth(viewWidth)
//                .setViewportHeight(viewHeight)
//                .setSpriteWidth(spriteWidth)
//                .setSpriteHeight(spriteHeight);
        GameGenerationConfigs configs = GameGenerationConfigs.getDefaults()
                .setRows(rows)
                .setColumns(columns);
        GameController newGameController = new GameController(configs);
        return newGameController;
    }

    public static GameController create(GameGenerationConfigs configs) {
        GameController newGameController = new GameController(configs);
        return newGameController;
    }

    public static GameController create(JSONObject settings, JSONArray map) {
        GameState gameStateV2 = new GameState(settings);
        GameController newGameController = new GameController(gameStateV2, map);
        return newGameController;
    }

    private GameController(JSONObject configsJson, JSONArray gameMap) {
        GameGenerationConfigs configs = (GameGenerationConfigs) configsJson;
        mGameModel = new GameModel(configs, gameMap);
        mGameView = new GameView(mGameModel);
        mGameAPI = new GameAPI();
    }

    private GameController(JSONObject configsJson) {
        GameGenerationConfigs configs = (GameGenerationConfigs) configsJson;
        mGameModel = new GameModel(configs, null);
        mGameView = new GameView(mGameModel);
        mGameAPI = new GameAPI();
    }


    public void update() {
        if (!mGameView.isGamePanelShowing() || !mGameModel.isRunning()) { return; }
        mGameModel.update();
        mGameView.update(mGameModel);
    }
    public void input() {
        if (!mGameModel.isRunning()) { return; }
        InputController.getInstance().update();
        mGameModel.input(InputController.getInstance());
    }

    public JPanel render() { return null; }
    public GameModel getModel() { return mGameModel; }
    public JPanel getGamePanel(int width, int height) {
        JPanel newGamePanel = mGameView.getViewPort(width, height);
        InputController.getInstance().setup(newGamePanel);
        return newGamePanel;
    }


    public int getRows() { return mGameModel.getRows(); }
    public int getColumns() { return mGameModel.getColumns(); }
    public void run() { mGameModel.run(); }
    public boolean isRunning() { return mGameModel.isRunning(); }
    public boolean placeUnit(Entity entity, String team, int row, int column) {
        return mGameModel.placeUnit(entity, team, row, column);
    }


    public void setTileToGlideTo(JSONObject request) { mGameAPI.setTileToGlideTo(mGameModel, request); }
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
    public List<JSONObject> getSelectedTiles() { return mGameAPI.getSelectedTiles(mGameModel); }
    public List<JSONObject> getHoveredTiles() { return mGameAPI.getHoveredTiles(mGameModel); }
    public JSONObject getTileOfCurrentUnitsTurn() { return mGameAPI.getTileOfCurrentUnitsTurn(mGameModel); }
    public void setSelectedTiles(JSONArray request) { mGameAPI.setSelectedTiles(mGameModel, request); }
    public void setSelectedTiles(JSONObject request) { setSelectedTiles(new JSONArray().put(request)); }
    public void updateGameState(JSONObject request) { mGameAPI.updateGameState(mGameModel, request); }
    public void setActionOfUnitOfCurrentTurn(JSONObject request) {
        mGameAPI.setActionOfUnitOfCurrentTurn(mGameModel, request);
    }

    public void stageActionForUnit(String id, String action) { mGameAPI.stageActionForUnit(id, action); }

    public JSONObject getGameState() { return mGameAPI.getGameState(mGameModel); }

    public JSONArray getActionsOfUnit(String id) { return mGameAPI.getActionsOfUnit(id); }
    public String getCurrentTurnsUnitID() { return mGameAPI.getCurrentTurnsUnitID(mGameModel); }
    public JSONObject getMovementStatsOfUnit(String id) { return mGameAPI.getMovementStatsOfUnit(id); }
    public String getUnitAtSelectedTiles() { return mGameAPI.getUnitAtSelectedTiles(mGameModel); }
    public String getUnitName(String id) { return mGameAPI.getUnitName(id); }
    public JSONObject getUnitResourceStats(JSONObject request) { return mGameAPI.getUnitResourceStats(request); }
    public JSONObject getUnitIdentifiers(JSONObject request) { return mGameAPI.getUnitIdentifiers(request); }
    public void setActionPanelIsOpen(boolean isOpen) { mGameAPI.setActionPanelIsOpen(mGameModel, isOpen); }
    public void setMovementPanelIsOpen(boolean isOpen) { mGameAPI.setMovementPanelIsOpen(mGameModel, isOpen); }
    public JSONArray getUnitStatsForMiniUnitInfoPanel(JSONObject request) {
        return mGameAPI.getUnitStatsForMiniUnitInfoPanel(request);
    }

    public JSONArray getActionsOfUnitOfCurrentTurn() { return mGameAPI.getActionsOfUnitOfCurrentTurn(mGameModel); }
    public JSONObject getMovementStatsOfUnitOfCurrentTurn() { return
            mGameAPI.getMovementStatsOfUnitOfCurrentTurn(mGameModel);
    }
    public JSONObject getUnitsOnSelectedTiles() { return mGameAPI.getUnitsOnSelectedTiles(mGameModel); }
    public boolean consumeShouldAutomaticallyGoToHomeControls() {
        return mGameAPI.consumeShouldAutomaticallyGoToHomeControls(mGameModel);
    }

    public JSONArray getNodeBaseAndModifiedOfUnitOfCurrentTurn(JSONArray request) {
        return mGameAPI.getNodeBaseAndModifiedOfUnitOfCurrentTurn(mGameModel, request); }

    public String getNicknameOfID(String id) { return mGameAPI.getNicknameOfID(id); }
}
