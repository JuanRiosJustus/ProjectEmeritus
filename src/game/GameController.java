package game;

import input.InputController;

import javax.swing.JPanel;
import javax.swing.OverlayLayout;

public class GameController {

    public final GameModel model;
    public final GameView view;
    public final InputController input;
    public JPanel scene;
    private static GameController instance = null;
    public static GameController instance() {
        if (instance == null) {
            instance = new GameController();
            instance.initialize();
        }
        return instance;
    }


    private GameController() {
        input = InputController.instance();
        model = new GameModel();
        view = new GameView();
    }

    private void initialize() {
        scene = new JPanel();
        scene.setLayout(new OverlayLayout(scene));
        scene.add(view.turnOrderPanel);
        scene.add(view.controlPanel);
        scene.add(view);
        scene.revalidate();
        scene.repaint();
        scene.setDoubleBuffered(true);
//
//        scene.addMouseListener(input.getMouse());
//        scene.addKeyListener(input.getKeyboard());
//        addKeyMouseMotionListener(input);

        view.initialize(this);
        model.initialize(this);
    }

    private void addKeyMouseMotionListener(InputController controls) {
        scene.addKeyListener(controls.getKeyboard());
        scene.addMouseListener(controls.getMouse());
        scene.addMouseMotionListener(controls.getMouse());
        scene.addMouseWheelListener(controls.getMouse());
    }

    public void update() {
        model.update();
        view.update();
    }
    public void input() {
//        view.input(model);
        model.input();
    }
}
