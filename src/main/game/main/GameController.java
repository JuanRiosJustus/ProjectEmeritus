package main.game.main;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.engine.EngineScene;
import main.game.map.base.TileMap;
import main.input.InputController;

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

    public GameController() { init(); }

    private void init() {        
        mGameModel = new GameModel(this);
        mGameView = new GameView(this);
        mInputController = InputController.getInstance();
    }

    public void update() {
        if (!mGameView.isShowing() || !mGameModel.isRunning()) { return; }
        mGameModel.update();
        mGameView.update(mGameModel);
    }
    public void input() { mGameModel.input(); }
    public JPanel render() { return mGameView; }
    public GameView getView() { return mGameView; }
    public GameModel getModel() { return mGameModel; }
    public void run() {
        mGameModel.run();
    }
    public boolean isRunning() {
        return mGameModel.isRunning();
    }

//    public void setMap(TileMap tileMap) {
////        mGameModel.initialize(this, tileMap, null);
//    }

    public void setMap(JsonObject tileMapJson, JsonObject unitPlacementJson) {
//        TileMap newTileMap = new TileMap();
        mGameModel.initialize(this, tileMapJson, unitPlacementJson);
    }
}
