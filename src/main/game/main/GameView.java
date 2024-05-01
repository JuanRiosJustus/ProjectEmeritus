package main.game.main;

import java.awt.Dimension;

import javax.swing.*;

import main.game.stores.pools.ColorPalette;
import main.constants.Settings;
import main.ui.custom.ImagePanel;
import main.ui.custom.MovementPanel;
import main.ui.custom.SkillsPanel;
import main.ui.custom.SummaryPanel;
import main.ui.huds.controls.v2.AdditionalInfoPane;
import main.ui.huds.controls.v2.MainUiHUD2;
import main.ui.panels.GamePanel;
import main.ui.huds.TimelineHUD;


public class GameView extends JPanel {

    private final GameController mGameController;
//    private final ControlHUD controlHUD;
    private final TimelineHUD timelineHUD;
    private final AdditionalInfoPane additionalInfoPane;
//    private final GameLogHUD loggerHUD;
//    private final Ad
    private GamePanel mGamePanel;
    private final JLayeredPane container = new JLayeredPane();
    private final MainUiHUD2 mainUiHud;

    public GameView(GameController gc, int width, int height) {
        mGameController = gc;

        int mainHudPanelWidths = (int) (width * .3);
        int mainHudPanelHeights = (int) (height * .3);

        timelineHUD = new TimelineHUD((int) (width * .6), (int) (height * .075));
        timelineHUD.setPreferredLocation(10, height - timelineHUD.getJSceneHeight() - (timelineHUD.getJSceneHeight() / 2));

        // TODO why do we need these random multipliers?
        additionalInfoPane = new AdditionalInfoPane((int) (mainHudPanelWidths * .9), (int) (mainHudPanelHeights * .8));
        additionalInfoPane.setPreferredLocation((int) (width - (additionalInfoPane.getDisplayWidth() * 1.05)),
                (int) (height - (additionalInfoPane.getJSceneHeight() * 2.4)));

        mainUiHud = new MainUiHUD2(mainHudPanelWidths, mainHudPanelHeights);
        mainUiHud.setPreferredLocation(width - mainUiHud.getJSceneWidth(), height - mainUiHud.getJSceneHeight() - 10);
        mainUiHud.addPanel("View", new ImagePanel(mainUiHud.getDisplayWidth(), mainUiHud.geDisplayHeight()));
        mainUiHud.addPanel("Skills", new SkillsPanel(mainUiHud.getDisplayWidth(), mainUiHud.geDisplayHeight(), additionalInfoPane));
        mainUiHud.addPanel("Movement", new MovementPanel(mainUiHud.getDisplayWidth(), mainUiHud.geDisplayHeight()));
        mainUiHud.addPanel("Summary", new SummaryPanel(mainUiHud.getDisplayWidth(), mainUiHud.geDisplayHeight()));
        mainUiHud.addPanel("Other", new JButton("Panel for Button 5"));

//        additionalInfoPane = new AdditionalInfoPane((int) (width * .3),  (int) (height * .25));
//        additionalInfoPane.setPreferredLocation(width - additionalInfoPane.getWidth() - 10, (int) (height - (additionalInfoPane.getDisplayHeight() * 2.2) - 30));

//        loggerHUD = new GameLogHUD((int) (width * .25), (int) (height * .25));
//        loggerHUD.setPreferredLocation(10, 10);

        mGamePanel = new GamePanel(gc, width, height);
        mGamePanel.setPreferredLocation(0, 0);

        container.setPreferredSize(new Dimension(width, height));
        container.add(mGamePanel, JLayeredPane.DEFAULT_LAYER);
        container.add(additionalInfoPane, JLayeredPane.MODAL_LAYER);
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

    public GameView(GameController gc) {
        mGameController = gc;

        int width = Settings.getInstance().getInteger(Settings.DISPLAY_WIDTH);
        int height = Settings.getInstance().getInteger(Settings.DISPLAY_HEIGHT);

        int mainHudPanelWidths = (int) (width * .3);
        int mainHudPanelHeights = (int) (height * .3);

        timelineHUD = new TimelineHUD((int) (width * .6), (int) (height * .075));
        timelineHUD.setPreferredLocation(10, height - timelineHUD.getJSceneHeight() - (timelineHUD.getJSceneHeight() / 2));

        // TODO why do we need these random multipliers?
        additionalInfoPane = new AdditionalInfoPane((int) (mainHudPanelWidths * .9), (int) (mainHudPanelHeights * .8));
        additionalInfoPane.setPreferredLocation((int) (width - (additionalInfoPane.getDisplayWidth() * 1.05)),
                (int) (height - (additionalInfoPane.getJSceneHeight() * 2.4)));

        mainUiHud = new MainUiHUD2(mainHudPanelWidths, mainHudPanelHeights);
        mainUiHud.setPreferredLocation(width - mainUiHud.getJSceneWidth(), height - mainUiHud.getJSceneHeight() - 10);
        mainUiHud.addPanel("View", new ImagePanel(mainUiHud.getDisplayWidth(), mainUiHud.geDisplayHeight()));
        mainUiHud.addPanel("Skills", new SkillsPanel(mainUiHud.getDisplayWidth(), mainUiHud.geDisplayHeight(), additionalInfoPane));
        mainUiHud.addPanel("Movement", new MovementPanel(mainUiHud.getDisplayWidth(), mainUiHud.geDisplayHeight()));
        mainUiHud.addPanel("Summary", new SummaryPanel(mainUiHud.getDisplayWidth(), mainUiHud.geDisplayHeight()));
        mainUiHud.addPanel("Other", new JButton("Panel for Button 5"));

//        additionalInfoPane = new AdditionalInfoPane((int) (width * .3),  (int) (height * .25));
//        additionalInfoPane.setPreferredLocation(width - additionalInfoPane.getWidth() - 10, (int) (height - (additionalInfoPane.getDisplayHeight() * 2.2) - 30));

//        loggerHUD = new GameLogHUD((int) (width * .25), (int) (height * .25));
//        loggerHUD.setPreferredLocation(10, 10);

        mGamePanel = new GamePanel(gc, width, height);
        mGamePanel.setPreferredLocation(0, 0);

        container.setPreferredSize(new Dimension(width, height));
        container.add(mGamePanel, JLayeredPane.DEFAULT_LAYER);
        container.add(additionalInfoPane, JLayeredPane.MODAL_LAYER);
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

    public GamePanel getNewGamePanel(int width, int height) {
        mGamePanel = new GamePanel(mGameController, width, height);
        return mGamePanel;
    }

    public void update(GameModel model) {
        if (!model.isRunning()) { return; }
        mainUiHud.jSceneUpdate(model);
        timelineHUD.jSceneUpdate(model);
//        loggerHUD.jSceneUpdate(model);
        mGamePanel.jSceneUpdate(model);
    }

    public void hideAuxPanels() {
//        controllerHUD.setVisible(!controllerHUD.isVisible());
        mainUiHud.setVisible(!mainUiHud.isVisible());
        timelineHUD.setVisible(!timelineHUD.isVisible());
//        loggerHUD.setVisible(!loggerHUD.isVisible());
    }

    public boolean isGamePanelShowing() {
        return mGamePanel.isShowing();
    }
}
