package engine;

import constants.ColorPalette;
import constants.Constants;
import input.InputController;
import ui.presets.SceneManager;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EngineView extends JFrame {
    private final JPanel container = new JPanel();
    public EngineView() {

        int width = Constants.APPLICATION_WIDTH, height = Constants.APPLICATION_HEIGHT;
        addMouseMotionListener(InputController.instance().getMouse());
        addMouseListener(InputController.instance().getMouse());
        addKeyListener(InputController.instance().getKeyboard());
        addMouseWheelListener(InputController.instance().getMouse());

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
        add(container);

        container.revalidate();
        container.repaint();
        container.setDoubleBuffered(true);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setScene(JPanel scene) {
        container.removeAll();
//        container.add(SceneManager.instance().getSceneSelectionPanel());
        container.add(scene);
        container.revalidate();
        container.repaint();
    }

    public void render() {
        revalidate();
        repaint();
    }
}
