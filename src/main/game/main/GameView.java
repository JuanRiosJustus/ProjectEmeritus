package main.game.main;

import java.awt.Dimension;

import javax.swing.*;

import main.constants.GameState;
import main.engine.Engine;
import main.game.stores.pools.ColorPalette;
import main.ui.components.OutlineButton;
import main.ui.custom.*;
import main.ui.huds.*;
import main.ui.huds.controls.BlankHoldingPane;
import main.ui.huds.controls.ControllerHUD;
import main.ui.panels.GamePanel;


public class GameView extends JPanel {

    private GameController mGameController;
    private TimelinePanel timelineHUD;
    private GamePanel mGamePanel;
    private JLayeredPane container;
    private ControllerHUD mainUiHud;
    private JButton mEndTurnButton = new JButton();
    private boolean mSetupEndTurnButton = false;
    private ActionsPanel mActionsPanel = null;
    private MovementPanel mMovementPanel = null;
    private SummaryPanel mSummaryPanel = null;
    private SettingsPanel mSettingsPanel = null;
    private BlankHoldingPane mainHudSecondaryPane = null;
    private BlankHoldingPane currentlySelectedPane = null;

    public GameView(GameController gc, int width, int height) {
        initialize(gc, width, height);
    }

    public void initialize(GameController gc, int width, int height) {
        removeAll();
        container = new JLayeredPane();
        mGameController = gc;

//        System.out.println("Width: " + width + ", Height" + height);

//        timelineHUD = new TimelineHUD((int) (width * .6), (int) (height * .075));
//        timelineHUD.setPreferredLocation(10, height - timelineHUD.getJSceneHeight() - (timelineHUD.getJSceneHeight() / 2));

        // TODO why do we need these random multipliers?
//        additionalInfoPane = new AdditionalInfoPane((int) (mainHudPanelWidths * .9), (int) (mainHudPanelHeights * .8));
//        additionalInfoPane.setPreferredLocation((int) (width - (additionalInfoPane.getDisplayWidth() * 1.05)),
//                (int) (height - (additionalInfoPane.getJSceneHeight() * 2.4)));
//        additionalInfoPane.setVisible(false);

//        mainUiHud = new MainUiHUD2(mainHudPanelWidths, mainHudPanelHeights);
//        mainUiHud.setPreferredLocation(width - mainUiHud.getJSceneWidth(),
//                height - mainUiHud.getJSceneHeight() - Engine.getInstance().getHeaderSize());

        int mainHudPanelWidths = (int) (width * .25);
        int mainHudPanelHeights = (int) (height * .25);

        int timelinePaneWidth = (int) (width * .70);
        int timelinePaneHeight = (int) (height * .08);

        int paddingForWidth = (int) (width * .01);
        int paddingForHeight = (int) (height * .01);

        int timelinePaneX = paddingForWidth;
        int timelinePaneY = height - timelinePaneHeight - paddingForHeight - Engine.getInstance().getHeaderSize();

        int mainHudPanelX = width - mainHudPanelWidths - paddingForWidth;
        int mainHudPanelY = height - mainHudPanelHeights - paddingForHeight - Engine.getInstance().getHeaderSize();

        mainUiHud = new ControllerHUD(mainHudPanelWidths, mainHudPanelHeights, mainHudPanelX, mainHudPanelY);

        mActionsPanel = new ActionsPanel(mainHudPanelWidths, mainHudPanelHeights, mainHudPanelX, mainHudPanelY);
        mainUiHud.addPanel("Actions", mActionsPanel,
                mActionsPanel.getEnterButton(), mActionsPanel.getExitButton());

        mMovementPanel = new MovementPanel(mainHudPanelWidths, mainHudPanelHeights, mainHudPanelX, mainHudPanelY);
        mainUiHud.addPanel("Movement", mMovementPanel,
                mMovementPanel.getEnterButton(), mMovementPanel.getExitButton());

        mSummaryPanel = new SummaryPanel(mainHudPanelWidths, mainHudPanelHeights, mainHudPanelX, mainHudPanelY);
        mainUiHud.addPanel("Summary", mSummaryPanel,
                mSummaryPanel.getEnterButton(), mSummaryPanel.getExitButton());

        mSettingsPanel = new SettingsPanel(mainHudPanelWidths, mainHudPanelHeights, mainHudPanelX, mainHudPanelY);
        mainUiHud.addPanel("Settings", mSettingsPanel,
                mSettingsPanel.getEnterButton(), mSettingsPanel.getExitButton());

//        JButton test = new OutlineButton("End Turn");
//        mainUiHud.addPanelButton("End Turn", test, test, test);

        mEndTurnButton = new OutlineButton("End Turn");
        mainUiHud.addPanelButton("End Turn", mEndTurnButton, mEndTurnButton, mEndTurnButton);

        mainHudSecondaryPane = new BlankHoldingPane(mainHudPanelWidths, mainHudPanelHeights,
                mainHudPanelX, mainHudPanelY - mainHudPanelHeights);
        mainHudSecondaryPane.setVisible(false);

        currentlySelectedPane = new BlankHoldingPane(mainHudPanelWidths, mainHudPanelHeights,
                width - paddingForWidth - mainHudPanelWidths, paddingForHeight);

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(mainHudPanelWidths, mainHudPanelHeights));
        panel.setMinimumSize(new Dimension(mainHudPanelWidths, mainHudPanelHeights));
        panel.setMaximumSize(new Dimension(mainHudPanelWidths, mainHudPanelHeights));

        currentlySelectedPane.setup(panel);
//        currentlySelectedPane.setVisible(true);

        timelineHUD = new TimelinePanel(timelinePaneWidth, timelinePaneHeight, timelinePaneX, timelinePaneY);

//        loggerHUD = new GameLogHUD((int) (width * .25), (int) (height * .25));
//        loggerHUD.setPreferredLocation(10, 10);

        mGamePanel = new GamePanel(gc, width, height);
        mGamePanel.setPreferredLocation(0, 0);

        container.setPreferredSize(new Dimension(width, height));
        container.add(mGamePanel, JLayeredPane.DEFAULT_LAYER);
        container.add(currentlySelectedPane, JLayeredPane.MODAL_LAYER);
        container.add(mainHudSecondaryPane, JLayeredPane.MODAL_LAYER);
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

        boolean isActionPanelShowing = mActionsPanel.isShowing();
        if (isActionPanelShowing) {
            if (mActionsPanel.shouldShowAdditionalDetailPanel()) {
                DatasheetPanel infoPanel = mActionsPanel.getActionDatasheet();
                infoPanel.setBackground(ColorPalette.GOLD);
                mainHudSecondaryPane.setup(infoPanel);
                mainHudSecondaryPane.setOpaque(true);
            }
            mainHudSecondaryPane.setVisible(true);
        } else {
            mainHudSecondaryPane.setVisible(false);
        }

        if (!mSetupEndTurnButton) {
            mEndTurnButton.addActionListener(e -> {
                model.setGameState(GameState.END_CURRENT_UNITS_TURN, true);
            });
            mSetupEndTurnButton = true;
        }

        if (!mActionsPanel.isShowing() && !mMovementPanel.isShowing() && !mSummaryPanel.isShowing()) {
            model.setGameState(GameState.SHOW_SELECTED_UNIT_MOVEMENT_PATHING, true);
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
