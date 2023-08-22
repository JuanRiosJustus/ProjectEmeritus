package main.game.main;

import java.awt.Dimension;

import javax.swing.*;

import main.constants.ColorPalette;
import main.constants.Settings;
import main.ui.huds.controls.v2.ControllerHUD;
import main.ui.panels.GamePanel;
import main.ui.huds.ActivityLogHUD;
import main.ui.huds.TurnOrderTimelineHUD;


public class GameView extends JPanel {

    private final GameController controller;
//    private final ControlHUD controlHUD;
    private final TurnOrderTimelineHUD turnOrderTimelineHUD;
    private final ActivityLogHUD loggerPanel;
    private final GamePanel gamePanel;
    private final ControllerHUD controllerHUD;
    private final JLayeredPane container = new JLayeredPane();


    public GameView(GameController gc) {
        controller = gc;
        controller.getModel();

        int width = Settings.getInstance().getInteger(Settings.DISPLAY_WIDTH);
        int height = Settings.getInstance().getInteger(Settings.DISPLAY_HEIGHT);

        controllerHUD = new ControllerHUD(width, height);
        controllerHUD.setPreferredLocation(0, 0);

        turnOrderTimelineHUD = new TurnOrderTimelineHUD((int) (width * .5), (int) (height * .1));
        turnOrderTimelineHUD.setPreferredLocation(10, height - turnOrderTimelineHUD.getHeight() - 50);

        loggerPanel = new ActivityLogHUD(width / 4, height / 4);
        loggerPanel.setPreferredLocation(10, 10);

        gamePanel = new GamePanel(gc, width, height);
        gamePanel.setPreferredLocation(0, 0);

        container.setPreferredSize(new Dimension(width, height));
        container.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        container.add(loggerPanel, JLayeredPane.MODAL_LAYER);
        container.add(controllerHUD, JLayeredPane.MODAL_LAYER);
        container.add(turnOrderTimelineHUD, JLayeredPane.MODAL_LAYER);

        setBackground(ColorPalette.BLACK);
        setLayout(null);
        container.setBounds(0, 0, width, height);
        add(container);
        setDoubleBuffered(true);
        setOpaque(true);
        setVisible(true);
    }

    public void update() {
        controllerHUD.jSceneUpdate(controller.getModel());
        turnOrderTimelineHUD.jSceneUpdate(controller.getModel());
        loggerPanel.jSceneUpdate(controller.getModel());
        gamePanel.jSceneUpdate(controller.getModel());
    }

    public void hideAuxPanels() {
        controllerHUD.setVisible(!controllerHUD.isVisible());
        turnOrderTimelineHUD.setVisible(!turnOrderTimelineHUD.isVisible());
        loggerPanel.setVisible(!loggerPanel.isVisible());
    }
}
