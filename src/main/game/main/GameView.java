package main.game.main;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import main.constants.ColorPalette;
import main.constants.Constants;
import main.graphics.JScene;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.ui.panels.ControlPanel;
import main.ui.panels.GamePanel;
import main.ui.panels.LogPanel;
import main.ui.panels.TurnOrderPanel;


public class GameView extends JPanel {

    private final GameController controller;
    private final JScene controlPanel;
    private final TurnOrderPanel turnOrderPanel;
    private final JScene loggerPanel;
    private final JScene gamePanel;
    private final JLayeredPane container = new JLayeredPane();


    public GameView(GameController gc) {
        controller = gc;
        controller.getModel();
        // controlPanel =  new ControlPanel(Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT);
        turnOrderPanel = new TurnOrderPanel(Constants.APPLICATION_WIDTH / 2, Constants.APPLICATION_HEIGHT / 10);
        int y = Constants.APPLICATION_HEIGHT - turnOrderPanel.getHeight() - 50;
        turnOrderPanel.setBounds(10, y, turnOrderPanel.getWidth(), turnOrderPanel.getHeight());


        controlPanel =  new ControlPanel(Constants.APPLICATION_WIDTH / 3, Constants.APPLICATION_HEIGHT / 3);
        int x = Constants.APPLICATION_WIDTH - controlPanel.getWidth() - 50;
        y = Constants.APPLICATION_HEIGHT - controlPanel.getHeight() - 50;
        controlPanel.setBounds(x, y , controlPanel.getWidth(), controlPanel.getHeight());
        controlPanel.setFocusable(false);

        loggerPanel = new LogPanel(Constants.APPLICATION_WIDTH / 3, Constants.APPLICATION_HEIGHT / 4);
        loggerPanel.setBounds(10, 10, loggerPanel.getWidth(), loggerPanel.getHeight());

        gamePanel = new GamePanel(gc, Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT);
        gamePanel.setBounds(0, 0, gamePanel.getWidth(), gamePanel.getHeight());
        gamePanel.setDoubleBuffered(true);

        container.setPreferredSize(new Dimension(Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT));
        container.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        container.add(loggerPanel, JLayeredPane.MODAL_LAYER);
        container.add(controlPanel, JLayeredPane.MODAL_LAYER);
        container.add(turnOrderPanel, JLayeredPane.MODAL_LAYER);


        // JButton b = new JButton("yooo");
        // b.setBounds(30, 30, 30, 40);
        // container.add(b, JLayeredPane.MODAL_LAYER);

        container.setDoubleBuffered(true);
        add(container);
        setBackground(ColorPalette.TRANSPARENT);
        setDoubleBuffered(true);
        setOpaque(true);
        setVisible(true);
    }

    public void update() {
//        controller.getModel();
        controlPanel.update(controller.getModel());
        turnOrderPanel.update(controller.getModel());
        loggerPanel.update(controller.getModel());
        gamePanel.update(controller.getModel());
    }
}
