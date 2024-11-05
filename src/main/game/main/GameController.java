package main.game.main;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import main.engine.EngineScene;
import main.game.entity.Entity;
import main.game.map.base.TileMap;
import main.input.InputController;
import main.ui.panels.GamePanel;

import javax.swing.*;
import java.util.List;

public class GameController extends EngineScene {

    private GameModel mGameModel;
    private GameView mGameView;
    public InputController mInputController;
    private static GameController mInstance = null;

    public static GameController getInstance() {
        if (mInstance == null) {
            int screenWidth = GameSettings.getInstance().getViewPortWidth();
            int screenHeight = GameSettings.getInstance().getViewPortHeight();
            mInstance = new GameController(screenWidth, screenHeight);
        }
        return mInstance;
    }

    public GameController create(int viewWidth, int viewHeight, int rows, int columns, int spriteWidth, int spriteHeight) {
        GameSettings newGameSettings = GameSettings.getDefaults();
        newGameSettings.setMapRowsAndColumns(rows, columns);
        newGameSettings.setViewPortWidthAndHeight(viewWidth, viewHeight);
        newGameSettings.setSpriteWidthAndHeight(spriteWidth, spriteHeight);
        GameController newGameController = new GameController(newGameSettings);
        return newGameController;
    }

    public static GameController create(GameSettings gameSettings) {
        GameController newGameController = new GameController(gameSettings);
        return newGameController;
    }

    private GameController(int width, int height) { initialize(width, height); }

    private void initialize(int width, int height) {
        mGameModel = new GameModel(this);
        mGameView = new GameView(this, width, height);
        mInputController = InputController.getInstance();
        mInputController.getKeyboardV2().link(this);
    }

    private GameController(GameSettings gameSettings) {
        mGameModel = new GameModel(this, gameSettings);
        mGameView = new GameView(this, gameSettings);
        mInputController = InputController.getInstance();
        mInputController.getKeyboardV2().link(this);
    }

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
    public GamePanel getGamePanel(int width, int height) { return mGameView.getGamePanel(width, height); }
    public int getRows() { return mGameModel.getRows(); }
    public int getColumns() { return mGameModel.getColumns(); }
    public void run() { mGameModel.run(); }
    public boolean isRunning() { return mGameModel.isRunning(); }
    public boolean placeUnit(Entity entity, String team, int row, int column) {
        return mGameModel.placeUnit(entity, team, row, column);
    }
    public Entity tryFetchingTileMousedAt() { return mGameModel.tryFetchingTileMousedAt(); }

    public void setMap(JsonObject tileMapJson, JsonObject unitPlacementJson) {
        mGameModel.setMap(tileMapJson, unitPlacementJson);
    }

    public void setMapV2(TileMap tileMap, JsonObject unitPlacementJson) {
        mGameModel.setMapV2(tileMap, unitPlacementJson);
    }

    public void setSettings(String key, Object value) { mGameModel.setSettings(key, value); }
    public GameSettings getSettings() { return mGameModel.getSettings(); }
    public JsonObject getUnitPlacementModel() { return JsonModeler.getUnitPlacementModel(mGameModel); }
    public JsonObject getTileMapModel() { return JsonModeler.getTileMapModel(mGameModel); }

    public List<Entity> setSpawnRegion(String region, int row, int column, int width, int height) {
        return mGameModel.setSpawnRegion(region, row, column, width, height);
    }

    public void setupInput(JComponent component) {
        mInputController.setup(component);
    }

    public boolean setSelectedTiles(JsonArray selectedEntities) {
        return mGameModel.setSelectedTiles(selectedEntities);
    }
    public List<Entity> getSelectedTiles() {
        return mGameModel.getSelectedTiles();
    }
    public void updateSelectedTiles(JsonObject updatedAttributes) {
        mGameModel.updateSelectedTiles(updatedAttributes);
    }
}
