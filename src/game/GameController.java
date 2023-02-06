package game;

import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import input.InputController;

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

        /**
         * Exception in thread "main" java.lang.NullPointerException: Cannot store to object array because "this.xChildren" is null
        at java.desktop/javax.swing.OverlayLayout.checkRequests(OverlayLayout.java:273)
        at java.desktop/javax.swing.OverlayLayout.layoutContainer(OverlayLayout.java:225)
        at java.desktop/java.awt.Container.layout(Container.java:1541)
        at java.desktop/java.awt.Container.doLayout(Container.java:1530)
        at java.desktop/java.awt.Container.validateTree(Container.java:1725)
        at java.desktop/java.awt.Container.validateTree(Container.java:1734)
        at java.desktop/java.awt.Container.validateTree(Container.java:1734)
         */
        scene.setLayout(new OverlayLayout(scene));
        scene.add(view.turnOrderPanel);
        scene.add(view.controlPanel);
        scene.add(view.loggerPanel);
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

    public void update() {
        if (view.isShowing() == false) { return; }
        model.update();
        view.update();
    }
    public void input() {
//        view.input(model);
        model.input();
    }
}
