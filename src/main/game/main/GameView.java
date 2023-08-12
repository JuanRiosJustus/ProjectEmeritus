package main.game.main;

import java.awt.Dimension;

import javax.swing.*;

import main.constants.ColorPalette;
import main.constants.Constants;
import main.ui.huds.controls.ControlHUD;
import main.ui.panels.GamePanel;
import main.ui.huds.ActivityLogHUD;
import main.ui.huds.TurnOrderTimelineHUD;


public class GameView extends JPanel {

    private final GameController controller;
    private final ControlHUD controlHUD;
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

        controlHUD = new ControlHUD(Constants.APPLICATION_WIDTH / 3, Constants.APPLICATION_HEIGHT / 3);
        controlHUD.setPreferredLocation(
                Constants.APPLICATION_WIDTH - controlHUD.getWidth() - 50,
                Constants.APPLICATION_HEIGHT - controlHUD.getHeight() - 50
        );
        controlHUD.setDoubleBuffered(true);
        controlHUD.setFocusable(false);

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
        container.add(controlHUD, JLayeredPane.MODAL_LAYER);
        container.add(turnOrderTimelineHUD, JLayeredPane.MODAL_LAYER);

        container.setDoubleBuffered(true);
        setBackground(ColorPalette.BLACK);
        add(container);
        setDoubleBuffered(true);
        setOpaque(true);
        setVisible(true);
    }

    public void update() {
        controlHUD.jSceneUpdate(controller.getModel());
        turnOrderTimelineHUD.jSceneUpdate(controller.getModel());
        loggerPanel.jSceneUpdate(controller.getModel());
        gamePanel.jSceneUpdate(controller.getModel());
//        gamePanel.requestFocus();
    }

    public void hideAuxPanels() {
        controlHUD.setVisible(!controlHUD.isVisible());
        turnOrderTimelineHUD.setVisible(!turnOrderTimelineHUD.isVisible());
        loggerPanel.setVisible(!loggerPanel.isVisible());
    }
}
