package main.game.main;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.constants.Settings;
import main.engine.EngineScene;
import main.game.entity.Entity;
import main.game.map.base.TileMapFactory;
import main.input.InputController;
import main.ui.panels.GamePanel;

import javax.swing.JPanel;
import java.util.List;

public class GameController extends EngineScene {

    private GameModel mGameModel;
    private GameView mGameView;
    public InputController mInputController;
    private static GameController mInstance = null;

    public static GameController getInstance() {
        if (mInstance == null) {
            int screenWidth = Settings.getInstance().getScreenWidth();
            int screenHeight = Settings.getInstance().getScreenHeight();
            mInstance = new GameController(screenWidth, screenHeight);
        }
        return mInstance;
    }

    public GameController create() {
        int tileRows = Settings.getInstance().getTileRows();
        int tileColumns = Settings.getInstance().getTileColumns();
        int screenWidth = Settings.getInstance().getScreenWidth();
        int screenHeight = Settings.getInstance().getScreenHeight();
        return create(screenWidth, screenHeight, tileRows, tileColumns);
    }
    public GameController create(int screenWidth, int screenHeight) {
        int rows = Settings.getInstance().getTileRows();
        int columns = Settings.getInstance().getTileColumns();
        return create(screenWidth, screenHeight, rows, columns);
    }
    public GameController create(int width, int height, int rows, int columns) {
        GameController newGameController = new GameController(width, height);
        newGameController.setMap(TileMapFactory.create(rows, columns).toJsonObject(), null);
        return newGameController;
    }
//
//    private GameController() { this(100, 100); }
    private GameController(int width, int height) { initialize(width, height); }

    private void initialize(int width, int height) {
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
    public void setGameModelState(String key, Object value) {
        mGameModel.setGameState(key, value);
    }
    public void run() { mGameModel.run(); }
    public boolean isRunning() { return mGameModel.isRunning(); }
    public boolean placeUnit(Entity entity, String team, int row, int column) {
        return mGameModel.placeUnit(entity, team, row, column);
    }
    public Entity tryFetchingTileMousedAt() { return mGameModel.tryFetchingTileMousedAt(); }

    public void setMap(JsonObject tileMapJson, JsonObject unitPlacementJson) {
        mGameModel.initialize(this, tileMapJson, unitPlacementJson);
    }
    public void setSettings(String key, Object value) { mGameModel.setSettings(key, value); }
    public JsonObject getUnitPlacementModel() { return JsonModeler.getUnitPlacementModel(mGameModel); }
    public JsonObject getTileMapModel() { return JsonModeler.getTileMapModel(mGameModel); }

    public List<Entity> setSpawnRegion(String region, int row, int column, int width, int height) {
        return mGameModel.setSpawnRegion(region, row, column, width, height);
    }
}
