package main.game.main;

import java.awt.Dimension;

import javax.swing.*;

import main.constants.ColorPalette;
import main.constants.GameState;
import main.constants.Settings;
import main.engine.Engine;
import main.game.entity.Entity;
import main.ui.custom.JButtonGrid;
import main.ui.huds.controls.v2.MainUiHUD;
import main.ui.panels.GamePanel;
import main.ui.huds.ActivityLogHUD;
import main.ui.huds.TimelineHUD;


public class GameView extends JPanel {

    private final GameController mGameController;
//    private final ControlHUD controlHUD;
    private final TimelineHUD timelineHUD;
    private final ActivityLogHUD loggerHUD;
    private final GamePanel gamePanel;
    private final JLayeredPane container = new JLayeredPane();
    private final MainUiHUD mainUiHud;

    public GameView(GameController gc) {
        mGameController = gc;

        int width = Settings.getInstance().getInteger(Settings.DISPLAY_WIDTH);
        int height = Settings.getInstance().getInteger(Settings.DISPLAY_HEIGHT);

//        controllerHUD = new ControllerHUD(width, height);
//        controllerHUD.setPreferredLocation(0, 0);

//        mButtonGrid = new JButtonGrid((int) (width * .3), (int) (height * .2), 3, 2);
//        mButtonGrid.add(new String[]{ "Actions", "Movement", "Inventory", "View", "Summary", "End of Turn"});
//        mButtonGrid.getButton("Actions").addActionListener();
//        mButtonGrid.setPreferredLocation(width - mButtonGrid.getWidth() - 10,
//                height - mButtonGrid.getHeight() - 10 - Engine.getInstance().getHeaderSize());
        mainUiHud = new MainUiHUD(width, height);
        mainUiHud.setPreferredLocation(0, 0);



        timelineHUD = new TimelineHUD((int) (width * .5), (int) (height * .1));
        timelineHUD.setPreferredLocation(10, height - timelineHUD.getHeight() - 50);

        loggerHUD = new ActivityLogHUD((int) (width * .25), (int) (height * .25));
        loggerHUD.setPreferredLocation(10, 10);

        gamePanel = new GamePanel(gc, width, height);
        gamePanel.setPreferredLocation(0, 0);

        container.setPreferredSize(new Dimension(width, height));
        container.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        container.add(loggerHUD, JLayeredPane.MODAL_LAYER);
//        container.add(controllerHUD, JLayeredPane.MODAL_LAYER);
//        container.add(mButtonGrid, JLayeredPane.MODAL_LAYER);
        container.add(mainUiHud, JLayeredPane.MODAL_LAYER);
        container.add(timelineHUD, JLayeredPane.MODAL_LAYER);

        setBackground(ColorPalette.BLACK);
        setLayout(null);
        container.setBounds(0, 0, width, height);
        add(container);
        setDoubleBuffered(true);
        setOpaque(true);
        setVisible(true);
    }

    public void update() {
        if (!mGameController.getModel().isRunning()) { return; }
        mainUiHud.jSceneUpdate(mGameController.getModel());
//        controllerHUD.jSceneUpdate(mGameController.getModel());
        timelineHUD.jSceneUpdate(mGameController.getModel());
        loggerHUD.jSceneUpdate(mGameController.getModel());
        gamePanel.jSceneUpdate(mGameController.getModel());
    }

    public void hideAuxPanels() {
//        controllerHUD.setVisible(!controllerHUD.isVisible());
        timelineHUD.setVisible(!timelineHUD.isVisible());
        loggerHUD.setVisible(!loggerHUD.isVisible());
    }
}
