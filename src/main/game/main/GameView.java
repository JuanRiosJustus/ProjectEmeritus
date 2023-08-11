package main.game.main;

import java.awt.Dimension;

import javax.swing.*;

import main.constants.ColorPalette;
import main.constants.Constants;
import main.ui.panels.ControlPanel;
import main.ui.panels.GamePanel;
import main.ui.huds.ActivityLogHUD;
import main.ui.huds.TurnOrderTimelineHUD;


public class GameView extends JPanel {

    private final GameController controller;
    private final ControlPanel controlPanel;
    private final TurnOrderTimelineHUD turnOrderTimelineHUD;
    private final ActivityLogHUD loggerPanel;
    private final GamePanel gamePanel;


    public GameView(GameController gc) {
        controller = gc;
        controller.getModel();
        // controlPanel =  new ControlPanel(Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT);
        turnOrderTimelineHUD = new TurnOrderTimelineHUD(
                (int) (Constants.APPLICATION_WIDTH * .5), (int) (Constants.APPLICATION_HEIGHT * .1)
        );
        turnOrderTimelineHUD.setPreferredLocation(10, Constants.APPLICATION_HEIGHT - turnOrderTimelineHUD.getHeight() - 50);

        controlPanel = new ControlPanel(Constants.APPLICATION_WIDTH / 3, Constants.APPLICATION_HEIGHT / 3);
        controlPanel.setPreferredLocation(
                Constants.APPLICATION_WIDTH - controlPanel.getWidth() - 50,
                Constants.APPLICATION_HEIGHT - controlPanel.getHeight() - 50
        );
        controlPanel.setDoubleBuffered(true);
        controlPanel.setFocusable(false);

        loggerPanel = new ActivityLogHUD(Constants.APPLICATION_WIDTH / 3, Constants.APPLICATION_HEIGHT / 4);
        loggerPanel.setPreferredLocation(10, 10);
        loggerPanel.setDoubleBuffered(true);
        loggerPanel.setFocusable(false);

        gamePanel = new GamePanel(gc, Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT);
        gamePanel.setPreferredLocation(0, 0);
        gamePanel.setDoubleBuffered(true);
        gamePanel.setFocusable(true);

        JLayeredPane container = new JLayeredPane();
        container.setPreferredSize(new Dimension(Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT));
        container.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        container.add(loggerPanel, JLayeredPane.MODAL_LAYER);
        container.add(controlPanel, JLayeredPane.MODAL_LAYER);
        container.add(turnOrderTimelineHUD, JLayeredPane.MODAL_LAYER);

        container.setDoubleBuffered(true);
        setBackground(ColorPalette.BLACK);
        add(container);
        setDoubleBuffered(true);
        setOpaque(true);
        setVisible(true);
    }

    public void update() {
        controlPanel.jSceneUpdate(controller.getModel());
        turnOrderTimelineHUD.jSceneUpdate(controller.getModel());
        loggerPanel.jSceneUpdate(controller.getModel());
        gamePanel.jSceneUpdate(controller.getModel());
//        gamePanel.requestFocus();
    }

    public void hideAuxPanels() {
        controlPanel.setVisible(!controlPanel.isVisible());
        turnOrderTimelineHUD.setVisible(!turnOrderTimelineHUD.isVisible());
        loggerPanel.setVisible(!loggerPanel.isVisible());
    }
}
