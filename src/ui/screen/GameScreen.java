package ui.screen;

import engine.EngineController;
import game.GameController;
import input.InputController;

import javax.swing.*;
import java.awt.*;

public class GameScreen extends JPanel {

    private final EngineController engine;

    public GameScreen(EngineController controller) {
        engine = controller;
        removeAll();
        setOpaque(true);
        setDoubleBuffered(true);
        setVisible(true);
        addKeyMouseMotionListener(controller.model.input);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        GameController.get().render(engine, g);
    }

    private void addKeyMouseMotionListener(InputController controls) {
        setFocusable(true);
        addMouseListener(controls.mouse());
        addMouseMotionListener(controls.mouse());
        addMouseWheelListener(controls.mouse());
        requestFocusInWindow();
    }
}
