package game.main;

import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import input.InputController;

public class GameController {

    private GameModel model;
    private GameView view;
    public InputController input = null;

    private static GameController instance = null;

    public static GameController instance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }
    

    private GameController() { reset(); }

    public void reset() {        
        model = new GameModel(this);
        view = new GameView(this);
    }

    public void setInput(InputController controller) {
        input = controller;
    }

    public void update() {
        if (view.isShowing() == false) { return; }
        model.update();
        view.update();
    }
    public void input() { model.input(); }

    public GameView getView() { return view; }
    public GameModel getModel() { return model; }
}
