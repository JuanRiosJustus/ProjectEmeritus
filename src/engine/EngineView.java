package engine;

import constants.ColorPalette;
import constants.Constants;
import game.GameController;
import input.InputController;
import ui.presets.SceneManager;
import ui.screen.Ui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EngineView extends JFrame {
    private final JPanel container = new JPanel();
    public EngineView() {

        int width = Constants.APPLICATION_WIDTH, height = Constants.APPLICATION_HEIGHT;
//        addMouseMotionListener(controller.model.input);
        setFocusable(true);
        requestFocusInWindow();
        setSize(width, height);
        setBackground(ColorPalette.BLACK);
        setTitle(Constants.APPLICATION_NAME);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                Engine.instance().stop();
            }
        });

        container.setLayout(new OverlayLayout(container));
//        container.add()
        add(container);

        container.revalidate();
        container.repaint();
        container.setDoubleBuffered(true);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setScene(JPanel scene) {
        container.removeAll();
        container.add(SceneManager.instance().getSceneSelectionPanel());
        container.add(scene);
        container.revalidate();
        container.repaint();
    }

    private void addMouseMotionListener(InputController controls) {
        setFocusable(true);
        addKeyListener(controls.keyboard());
        requestFocusInWindow();
    }

    public void render() {
        if (container.getComponents().length == 0) { return; }
        revalidate();
        repaint();
//        mainUi.revalidate();
//        mainUi.repaint();
    }

//    public void register(InputController input) {
////        gameView.addKeyMouseMotionListener(input);
////        addMouseMotionListener(input);
////        game.addKeyMouseMotionListener(input);
//    }
}
