package game;

import input.InputController;

import javax.swing.JPanel;
import javax.swing.OverlayLayout;

public class GameController {

    public final GameModel model;
    public final GameView view;
    public final InputController input;
    public final JPanel scene;
    private static GameController instance = null;
    public static GameController instance() {
        if (instance == null) {
            instance = new GameController();
            // TODO can this be done more pretty ?
            instance.view.initialize(instance);
            instance.model.initialize(instance);
        }
        return instance; }


    private GameController() {
        input = InputController.instance();
        model = new GameModel();
        view = new GameView();

        scene = new JPanel();
        scene.setLayout(new OverlayLayout(scene));
        scene.add(view.ui.getContainer());
        scene.add(view);
        scene.revalidate();
        scene.repaint();
        scene.setDoubleBuffered(true);
    }

    public void update() {
        model.update();
        view.update();
    }
    public void input() {
        model.input();
    }
}
