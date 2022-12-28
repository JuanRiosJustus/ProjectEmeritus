package game;

import input.InputController;

import javax.swing.JPanel;
import javax.swing.OverlayLayout;

public class GameController {

    public final GameModel model;
    public final GameView view;
    public final InputController input;
    public final JPanel scene;
    public static final GameController instance = new GameController();
    public static GameController instance() { return instance; }


    public GameController() {
        input = InputController.instance;
        view = new GameView(this);
        model = new GameModel(this);

        scene = new JPanel();
        scene.setLayout(new OverlayLayout(scene));
        scene.add(view.ui.getContainer());
        scene.add(view);
        scene.revalidate();
        scene.repaint();
        scene.setDoubleBuffered(true);
    }

    public void update() {
        if (scene == null || !scene.isShowing()) { return; }
        model.update();
        view.update();
    }
    public void input() {
        model.input();
    }
}
