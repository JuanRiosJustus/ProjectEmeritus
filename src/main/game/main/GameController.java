package main.game.main;

import main.engine.EngineScene;
import main.input.InputController;

import javax.swing.JPanel;

public class GameController extends EngineScene {

    private GameModel mGameModel;
    private GameView mGameView;
    public InputController mInputController;

    private static GameController instance = null;

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    public GameController() { init(); }

    private void init() {        
        mGameModel = new GameModel(this);
        mGameView = new GameView(this);
        mInputController = InputController.getInstance();
    }

    public void update() {
        if (!mGameView.isShowing()) { return; }
        mGameModel.update();
        mGameView.update();
    }
    public void input() { mGameModel.input(); }
    public JPanel render() { return mGameView; }
    public GameView getView() { return mGameView; }
    public GameModel getModel() { return mGameModel; }
}
