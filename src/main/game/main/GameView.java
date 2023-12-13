package main.game.main;

import java.awt.Dimension;

import javax.swing.*;

import main.constants.ColorPalette;
import main.constants.Settings;
import main.ui.huds.controls.v2.MainUiHUD;
import main.ui.panels.GamePanel;
import main.ui.huds.GameLogHUD;
import main.ui.huds.TimelineHUD;


public class GameView extends JPanel {

    private final GameController mGameController;
//    private final ControlHUD controlHUD;
    private final TimelineHUD timelineHUD;
//    private final GameLogHUD loggerHUD;
    private final GamePanel gamePanel;
    private final JLayeredPane container = new JLayeredPane();
    private final MainUiHUD mainUiHud;

    public GameView(GameController gc) {
        mGameController = gc;

        int width = Settings.getInstance().getInteger(Settings.DISPLAY_WIDTH);
        int height = Settings.getInstance().getInteger(Settings.DISPLAY_HEIGHT);

        mainUiHud = new MainUiHUD(width, height);
        mainUiHud.setPreferredLocation(0, 0);

        timelineHUD = new TimelineHUD((int) (width * .5), (int) (height * .1));
        timelineHUD.setPreferredLocation(10, height - timelineHUD.getHeight() - 50);

//        loggerHUD = new GameLogHUD((int) (width * .25), (int) (height * .25));
//        loggerHUD.setPreferredLocation(10, 10);

        gamePanel = new GamePanel(gc, width, height);
        gamePanel.setPreferredLocation(0, 0);

        container.setPreferredSize(new Dimension(width, height));
        container.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
//        container.add(loggerHUD, JLayeredPane.MODAL_LAYER);
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

    public void update(GameModel model) {
        if (!model.isRunning()) { return; }
        mainUiHud.jSceneUpdate(model);
        timelineHUD.jSceneUpdate(model);
//        loggerHUD.jSceneUpdate(model);
        gamePanel.jSceneUpdate(model);
    }

    public void hideAuxPanels() {
//        controllerHUD.setVisible(!controllerHUD.isVisible());
        mainUiHud.setVisible(!mainUiHud.isVisible());
        timelineHUD.setVisible(!timelineHUD.isVisible());
//        loggerHUD.setVisible(!loggerHUD.isVisible());
    }
}
