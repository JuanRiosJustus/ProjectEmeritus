package main.game.main;

import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import main.input.InputController;

public class GameController {

    private GameModel model;
    private GameView view;
    public InputController input = null;

    private static GameController instance = null;

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }
    

    public GameController() { init(); }

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

    public GameView getView() { return view; }
    public GameModel getModel() { return model; }
}
