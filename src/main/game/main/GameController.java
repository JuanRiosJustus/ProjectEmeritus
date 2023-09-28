package main.game.main;

import main.constants.ColorPalette;
import main.constants.Settings;
import main.engine.EngineScene;
import main.input.InputController;

import javax.swing.JButton;
import javax.swing.JPanel;

public class GameController extends EngineScene {

    private GameModel model;
    private GameView view;
    public InputController input = InputController.getInstance();

    private static GameController instance = null;

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }
    

    public GameController() {
        init();
    }

    private void init() {        
        model = new GameModel(this);
        view = new GameView(this);
    }

    public void setInput(InputController controller) {
        input = controller;
    }

    public void update() {
        if (!view.isShowing()) { return; }
        model.update();
        view.update();
    }
    public void input() { model.input(); }
    public JPanel render() { return view; }
    public GameView getView() { return view; }
    public GameModel getModel() { return model; }
}
