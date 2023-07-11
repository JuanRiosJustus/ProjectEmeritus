package game.main;

import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import constants.ColorPalette;
import constants.Constants;
import logging.ELogger;
import logging.ELoggerFactory;
import ui.panels.ControlPanel;
import ui.panels.GamePanel;
import ui.panels.LogPanel;
import ui.panels.TurnOrderPanel;


public class GameView extends JPanel {

    private final GameController controller;
    private final ControlPanel controlPanel;
    private final TurnOrderPanel turnOrderPanel;
    private final LogPanel loggerPanel;
    private final GamePanel gamePanel;

    public GameView(GameController gc) {
        controller = gc;
        controller.getModel();
        controlPanel =  new ControlPanel(Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT);
        turnOrderPanel = new TurnOrderPanel(Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT);
        loggerPanel = new LogPanel(Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT);
        gamePanel = new GamePanel(gc, Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT);

        setLayout(new OverlayLayout(this));
        add(turnOrderPanel);
        add(controlPanel);
        add(loggerPanel);
        add(gamePanel);
        setBackground(ColorPalette.TRANSPARENT);
        setDoubleBuffered(true);
        setOpaque(true);
        setVisible(true);
    }

    public void update() {
        controller.getModel();
        controlPanel.update(controller.getModel());
        turnOrderPanel.update(controller.getModel());
        loggerPanel.update(controller.getModel());
        gamePanel.update();
    }
}
