package main.game.main;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.engine.EngineScene;
import main.game.entity.Entity;
import main.game.map.base.TileMap;
import main.input.InputController;
import main.ui.panels.GamePanel;

import javax.swing.JPanel;

public class GameController extends EngineScene {

    private GameModel mGameModel;
    private GameView mGameView;
    public InputController mInputController;
    private static GameController mInstance = null;

    public static GameController getInstance() {
        if (mInstance == null) {
            mInstance = new GameController();
        }
        return mInstance;
    }

    public GameController create(int screenWidth, int screenHeight) {
        return new GameController(screenWidth, screenHeight);
    }
    public GameController create(int width, int height, int rows, int columns) {
        GameController newGameController = new GameController(width, height);
        newGameController.setMap(TileMap.createRandom(rows, columns).toJsonObject(), null);
        return newGameController;
    }

    public GameController() { init(); }
    public GameController(int width, int height) { init(width, height); }

    private void init() {        
        mGameModel = new GameModel(this);
        mGameView = new GameView(this);
        mInputController = InputController.getInstance();
    }

    private void init(int width, int height) {
        mGameModel = new GameModel(this);
        mGameView = new GameView(this, width, height);
        mInputController = InputController.getInstance();
    }

    public void update() {
        if (!mGameView.isGamePanelShowing() || !mGameModel.isRunning()) { return; }
        mGameModel.update();
        mGameView.update(mGameModel);
    }
    public void input() { mGameModel.input(); }
    public JPanel render() { return mGameView; }
    public GameView getView() { return mGameView; }
    public GameModel getModel() { return mGameModel; }
    public GamePanel getNewGamePanel(int width, int height) { return mGameView.getNewGamePanel(width, height); }
    public int getRows() { return mGameModel.getRows(); }
    public int getColumns() { return mGameModel.getColumns(); }
    public void addShadowEffect() { mGameModel.addShadowEffect(); }
    public void setGameModelState(String key, Object value) {
        mGameModel.setGameState(key, value);
    }
    public void run() { mGameModel.run(); }
    public boolean isRunning() { return mGameModel.isRunning(); }
    public void addUnit(Entity entity, String team, int row, int column) { mGameModel.addUnit(entity, team, row, column); }
    public Entity tryFetchingTileMousedAt() { return mGameModel.tryFetchingTileMousedAt(); }

    public void setMap(JsonObject tileMapJson, JsonObject unitPlacementJson) {
        mGameModel.initialize(this, tileMapJson, unitPlacementJson);
    }
    public void setSettings(String key, Object value) { mGameModel.setSettings(key, value); }
    public JsonObject getUnitPlacementModel() { return JsonModeler.getUnitPlacementModel(mGameModel); }
    public JsonObject getTileMapModel() { return JsonModeler.getTileMapModel(mGameModel); }
}
