package main.game.main;

import java.awt.Dimension;

import javax.swing.*;

import main.constants.GameState;
import main.engine.Engine;
import main.game.stores.pools.ColorPalette;
import main.ui.custom.DatasheetPanel;
import main.ui.custom.MovementPanel;
import main.ui.custom.ActionsPanel;
import main.ui.custom.SummaryPanel;
import main.ui.huds.controls.v2.AdditionalInfoPane;
import main.ui.huds.controls.v2.BlankHoldingPane;
import main.ui.huds.controls.v2.MainUiHUD2;
import main.ui.panels.GamePanel;
import main.ui.huds.TimelineHUD;


public class GameView extends JPanel {

    private GameController mGameController;
    private TimelineHUD timelineHUD;
    private AdditionalInfoPane additionalInfoPane;
    private GamePanel mGamePanel;
    private JLayeredPane container;
    private MainUiHUD2 mainUiHud;
    private JButton mEndTurnButton = new JButton();
    private boolean mSetupEndTurnButton = false;
    private ActionsPanel mActionsPanel = null;
    private MovementPanel mMovementPanel = null;
    private SummaryPanel mSummaryPanel = null;
    private BlankHoldingPane mBlankHoldingPane = null;

    public GameView(GameController gc, int width, int height) {
        initialize(gc, width, height);
    }

    public void initialize(GameController gc, int width, int height) {
        removeAll();
        container = new JLayeredPane();
        mGameController = gc;

//        System.out.println("Width: " + width + ", Height" + height);

        timelineHUD = new TimelineHUD((int) (width * .6), (int) (height * .075));
        timelineHUD.setPreferredLocation(10, height - timelineHUD.getJSceneHeight() - (timelineHUD.getJSceneHeight() / 2));

        // TODO why do we need these random multipliers?
//        additionalInfoPane = new AdditionalInfoPane((int) (mainHudPanelWidths * .9), (int) (mainHudPanelHeights * .8));
//        additionalInfoPane.setPreferredLocation((int) (width - (additionalInfoPane.getDisplayWidth() * 1.05)),
//                (int) (height - (additionalInfoPane.getJSceneHeight() * 2.4)));
//        additionalInfoPane.setVisible(false);

//        mainUiHud = new MainUiHUD2(mainHudPanelWidths, mainHudPanelHeights);
//        mainUiHud.setPreferredLocation(width - mainUiHud.getJSceneWidth(),
//                height - mainUiHud.getJSceneHeight() - Engine.getInstance().getHeaderSize());

        int mainHudPanelWidths = (int) (width * .3);
        int mainHudPanelHeights = (int) (height * .3);
        int mainHudPanelX = width - mainHudPanelWidths;
        int mainHudPanelY = height - mainHudPanelHeights - Engine.getInstance().getHeaderSize();

        mainUiHud = new MainUiHUD2(mainHudPanelWidths, mainHudPanelHeights, mainHudPanelX, mainHudPanelY);

//        mBlankHoldingPane = new BlankHoldingPane((int) (mainHudPanelWidths * .9), (int) (mainHudPanelHeights * .8));
//        mBlankHoldingPane.setPreferredLocation((int) (width - (mBlankHoldingPane.getJSceneWidth() * 1.05)),
//                (int) (height - (mBlankHoldingPane.getJSceneHeight() * 2.4)));
        mBlankHoldingPane = new BlankHoldingPane(
                mainHudPanelWidths,
                mainHudPanelHeights,
                mainHudPanelX,
                mainHudPanelY - mainHudPanelHeights
        );
        mBlankHoldingPane.setVisible(true);


        mActionsPanel = new ActionsPanel(mainHudPanelWidths, mainHudPanelHeights, mainHudPanelX, mainHudPanelY);
        mainUiHud.addPanel("Actions", mActionsPanel, mActionsPanel.getExitButton());

        mMovementPanel = new MovementPanel(mainHudPanelWidths, mainHudPanelHeights, mainHudPanelX, mainHudPanelY);
        mainUiHud.addPanel("Movement", mMovementPanel, mMovementPanel.getExitButton());

        mSummaryPanel = new SummaryPanel(mainHudPanelWidths, mainHudPanelHeights, mainHudPanelX, mainHudPanelY);
        mainUiHud.addPanel("Summary", mSummaryPanel, mSummaryPanel.getExitButton());

        mainUiHud.addPanel("Other", new JButton("Panel for Button 5"), new JButton("EXIT"));
        mEndTurnButton = new JButton("End Turn");
        mainUiHud.addPanelRaw("End Turn", mEndTurnButton);

//        loggerHUD = new GameLogHUD((int) (width * .25), (int) (height * .25));
//        loggerHUD.setPreferredLocation(10, 10);

        mGamePanel = new GamePanel(gc, width, height);
        mGamePanel.setPreferredLocation(0, 0);

        container.setPreferredSize(new Dimension(width, height));
        container.add(mGamePanel, JLayeredPane.DEFAULT_LAYER);
//        container.add(additionalInfoPane, JLayeredPane.MODAL_LAYER);
        container.add(mBlankHoldingPane, JLayeredPane.MODAL_LAYER);
        container.add(mGameController, JLayeredPane.MODAL_LAYER);
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

        if (mActionsPanel.shouldShowAdditionalDetailPanel()) {
            DatasheetPanel infoPanel = mActionsPanel.getActionDatasheet();
            infoPanel.setBackground(ColorPalette.GOLD);
            mBlankHoldingPane.setup(infoPanel);
            mBlankHoldingPane.setVisible(true);
            mBlankHoldingPane.setOpaque(true);
        }

        if (!mSetupEndTurnButton) {
            mEndTurnButton.addActionListener(e -> {
                model.setGameState(GameState.END_CURRENT_UNITS_TURN, true);
            });
            mSetupEndTurnButton = true;
        }
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
