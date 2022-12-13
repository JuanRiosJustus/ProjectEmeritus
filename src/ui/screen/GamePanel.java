package ui.screen;

import constants.Constants;
import engine.Engine;
import engine.EngineModel;

import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;

public class GamePanel extends JPanel {

    public GamePanel() {
        int width = Constants.APPLICATION_WIDTH, height = Constants.APPLICATION_HEIGHT;
        setSize(width, height);
        setLayout(new OverlayLayout(this));
        add(setupGameMainPanel(Engine.get().controller().model));
        add(new GameScreen(Engine.get().controller()));
        setDoubleBuffered(true);
//        setBackground(Color.BLACK);
        revalidate();
        repaint();
    }

    private JPanel setupGameMainPanel(EngineModel model) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(model.ui, BorderLayout.EAST);
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setOpaque(false);

        return panel;
    }
}
