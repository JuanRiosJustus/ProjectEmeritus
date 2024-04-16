package main.game.main;

import java.awt.Dimension;

import javax.swing.*;

import main.game.stores.pools.ColorPalette;
import main.constants.Settings;
import main.ui.custom.ImagePanel;
import main.ui.custom.MovementPanel;
import main.ui.custom.SummaryPanel;
import main.ui.huds.controls.v2.MainUiHUD;
import main.ui.huds.controls.v2.MainUiHUD2;
import main.ui.huds.controls.v2.MovementHUD;
import main.ui.panels.GamePanel;
import main.ui.huds.TimelineHUD;


public class GameView extends JPanel {

    private final GameController mGameController;
//    private final ControlHUD controlHUD;
    private final TimelineHUD timelineHUD;
//    private final GameLogHUD loggerHUD;
    private final GamePanel gamePanel;
    private final JLayeredPane container = new JLayeredPane();
    private final MainUiHUD2 mainUiHud;

    public GameView(GameController gc) {
        mGameController = gc;

        int width = Settings.getInstance().getInteger(Settings.DISPLAY_WIDTH);
        int height = Settings.getInstance().getInteger(Settings.DISPLAY_HEIGHT);

        timelineHUD = new TimelineHUD((int) (width * .6), (int) (height * .075));
        timelineHUD.setPreferredLocation(10, height - timelineHUD.getHeight() - (timelineHUD.getHeight() / 2));

        mainUiHud = new MainUiHUD2((int) (width * .3),  (int) (height * .3));
        mainUiHud.setPreferredLocation(width - mainUiHud.getWidth(), height - mainUiHud.getHeight() - 10);
        mainUiHud.addPanel("View", new ImagePanel(mainUiHud.getDisplayWidth(), mainUiHud.geDisplayHeight()));
        mainUiHud.addPanel("Abilities", new JButton("Panel for Button 2"));
        mainUiHud.addPanel("Movement", new MovementPanel(mainUiHud.getDisplayWidth(), mainUiHud.geDisplayHeight()));
        mainUiHud.addPanel("Summary", new SummaryPanel(mainUiHud.getDisplayWidth(), mainUiHud.geDisplayHeight()));
        mainUiHud.addPanel("Other", new JButton("Panel for Button 5"));

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
