package main.game.main;

import org.json.JSONArray;
import org.json.JSONObject;
import main.engine.EngineScene;
import main.game.entity.Entity;
import main.game.map.base.TileMap;
import main.input.InputController;
import main.ui.panels.GamePanel;

import javax.swing.*;

public class GameController extends EngineScene {

    private GameModel mGameModel;
    private GameView mGameView;
    public InputController mInputController;
    private static GameController mInstance = null;

    public static GameController getInstance() {
        if (mInstance == null) {
            int screenWidth = GameConfigurations.getInstance().getViewPortWidth();
            int screenHeight = GameConfigurations.getInstance().getViewPortHeight();
//            mInstance = new GameController(screenWidth, screenHeight);
        }
        return mInstance;
    }

    public GameController create(int viewWidth, int viewHeight, int rows, int columns, int spriteWidth, int spriteHeight) {
        GameConfigurations newGameConfigurations = GameConfigurations.getDefaults();
        newGameConfigurations.setMapRowsAndColumns(rows, columns);
        newGameConfigurations.setViewPortWidthAndHeight(viewWidth, viewHeight);
        newGameConfigurations.setSpriteWidthAndHeight(spriteWidth, spriteHeight);
        GameController newGameController = new GameController(newGameConfigurations);
        return newGameController;
    }

    public static GameController create(GameConfigurations gameConfigurations) {
        GameController newGameController = new GameController(gameConfigurations);
        return newGameController;
    }

    public static GameController create(JSONObject settings, JSONArray map) {
        GameConfigurations gameConfigurations = new GameConfigurations(settings);
        GameController newGameController = new GameController(gameConfigurations, map);
        return newGameController;
    }

//    private GameController(int width, int height) { initialize(width, height); }

    private void initialize(int width, int height) {
        mGameModel = new GameModel(this);
        mGameView = new GameView(this, width, height);
        mInputController = InputController.getInstance();
        mInputController.getKeyboardV2().link(this);
    }

    private GameController(JSONObject gameSettings, JSONArray gameMap) {
        GameConfigurations newGameConfigurations = (GameConfigurations) gameSettings;
        mGameModel = new GameModel(this, newGameConfigurations, gameMap);
        mGameView = new GameView(this, newGameConfigurations);
        mInputController = InputController.getInstance();
        mInputController.getKeyboardV2().link(this);
    }
    private GameController(JSONObject gameSettings) {
        GameConfigurations newGameConfigurations = (GameConfigurations) gameSettings;
        mGameModel = new GameModel(this, newGameConfigurations, null);
        mGameView = new GameView(this, newGameConfigurations);
        mInputController = InputController.getInstance();
        mInputController.getKeyboardV2().link(this);
    }

//    private GameController(GameSettings gameSettings) {
//        mGameModel = new GameModel(this, gameSettings);
//        mGameView = new GameView(this, gameSettings);
//        mInputController = InputController.getInstance();
//        mInputController.getKeyboardV2().link(this);
//    }

    public void update() {
        if (!mGameView.isGamePanelShowing() || !mGameModel.isRunning()) { return; }
        mGameModel.update();
        mGameView.update(mGameModel);
    }
    public void input() {
        if (!mGameModel.isRunning()) { return; }
        mGameModel.input(mInputController);
    }

    public JPanel render() { return mGameView; }
    public GameView getView() { return mGameView; }
    public GameModel getModel() { return mGameModel; }
    public JPanel getGamePanel(int width, int height) {
        JPanel newGamePanel = mGameView.getGamePanel(width, height);
        setupInput(newGamePanel);
        return newGamePanel;
    }
//    public GamePanel getGamePanel() {
//        return mGameView.getGamePanel(width, height);
//    }


    public int getRows() { return mGameModel.getRows(); }
    public int getColumns() { return mGameModel.getColumns(); }
    public void run() { mGameModel.run(); }
    public boolean isRunning() { return mGameModel.isRunning(); }
    public boolean placeUnit(Entity entity, String team, int row, int column) {
        return mGameModel.placeUnit(entity, team, row, column);
    }
    public Entity tryFetchingTileMousedAt() { return mGameModel.tryFetchingTileMousedAt(); }

    public void setMap(JSONObject tileMapJson, JSONObject unitPlacementJson) {
        mGameModel.setMap(tileMapJson, unitPlacementJson);
    }

    public void setMapV2(TileMap tileMap, JSONObject unitPlacementJson) {
        mGameModel.setMapV2(tileMap, unitPlacementJson);
    }

    public void setSettings(String key, Object value) { mGameModel.setSettings(key, value); }
    public GameConfigurations getSettings() { return mGameModel.getSettings(); }
    public JSONObject getUnitPlacementModel() { return JsonUtils.getUnitPlacementModel(mGameModel); }
    public JSONObject getTileMapModel() { return JsonUtils.getTileMapModel(mGameModel); }

//    public List<Entity> setSpawnRegion(String region, int row, int column, int width, int height) {
//        return mGameModel.setSpawnRegion(region, row, column, width, height);
//    }

    public void setupInput(JComponent component) {
        mInputController.setup(component);
    }

    public boolean updateSelectedTiles(JSONArray selectedEntities) {
        return mGameModel.updateSelectedTiles(selectedEntities);
    }
    public void updateTileLayers(JSONObject request) {
        mGameModel.updateTileLayers(request);
    }

    public void updateSpawners(JSONObject request) { mGameModel.updateSpawners(request); }
    public void updateStructures(JSONObject request) { mGameModel.updateStructures(request); }
    public JSONArray getTilesAtRowColumn(JSONObject request) { return mGameModel.getTilesAtRowColumn(request); }
    public JSONArray getTilesAtXY(JSONObject request) { return mGameModel.getTilesAtXY(request); }
    public String getTileMapJson() {
        return mGameModel.getTileMap().toString(2);
    }
    public String getSettingsJson() {
        return mGameModel.getSettings().toString(2);
    }

}
