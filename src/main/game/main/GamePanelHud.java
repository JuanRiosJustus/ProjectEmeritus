package main.game.main;

import main.engine.Engine;
import main.game.components.MovementComponent;
import main.game.entity.Entity;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.graphics.ControllerUI;
import main.graphics.GameUI;
import main.ui.components.OutlineButton;
import main.ui.custom.SwingUiUtils;
import main.ui.huds.*;
import main.ui.huds.controls.OutlineMapPanel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class GamePanelHud extends GameUI {
    private GameController mGameController;
    private TimeLinePanel mTimeLinePanel;
    private JButton mEndTurnButton = new JButton();
    private boolean mSetupEndTurnButton = false;
    private ActionsPanel mActionsPanel = null;
    private ActionDetailsPanel mActionDetailsPanel = null;
    private MovementPanel mMovementPanel = null;
    private SummaryPanel mSummaryPanel = null;
    private SettingsPanel mSettingsPanel = null;
    private SelectionPanel mSelectionPanel = null;
    private OutlineMapPanel mOutlineMapPanel = null;
    private ControllerUI[] controllerUis = null;
    private Color color = null;

    public GamePanelHud(int width, int height, int x, int y) {
        super(width, height, x, y, "");

        color = Color.DARK_GRAY;

        // Panels must allow space between one another At max, 4 panel per axis, just because
        int paddingForWidth = (int) (width * .01);
        int paddingForHeight = (int) (height * .01);


        // Panels must allow space between one another At max, 4 panel per axis, just because
        int genericPanelWidth = (int) (width * .25);
        int genericPanelHeight = (int) (height * .25);

        // Never more than 4 UIs per axis, so .2 * width left for padding
        int genericPanelWidthPadding = (int) (genericPanelWidth * .05);
        int genericPanelHeightPadding = (int) (genericPanelHeight * .05);

        // Panel for the main controller buttons *Attack,Move,Settings,etc...
        int controllerPanelX = width - genericPanelWidth - genericPanelWidthPadding;
        int controllerPanelY = height - genericPanelHeight - genericPanelHeightPadding - Engine.getInstance().getHeaderSize();

        // panel for action details go - (* 2 should be * 1, but experimental for now)
        int actionPanelX = controllerPanelX;
        int actionPanelY = controllerPanelY - (genericPanelHeightPadding * 2) - genericPanelHeight;





        int timelinePanelWidth = (int) (width * .70);
        int timelinePanelHeight = (int) (height * .075);

        int timelinePanelX = paddingForWidth;
        int timelinePanelY = height - timelinePanelHeight - paddingForHeight - Engine.getInstance().getHeaderSize();

//        int controllerPanelX = width - genericPanelWidth - paddingForWidth;
//        int controllerPanelY = height - genericPanelHeight - paddingForHeight - Engine.getInstance().getHeaderSize();

        int selectionPanelWidth = timelinePanelWidth / 2;
        int selectionPanelHeight = genericPanelHeight - timelinePanelHeight - paddingForHeight;
        int selectionPanelX = timelinePanelX;
        int selectionPanelY = controllerPanelY;

        mOutlineMapPanel = new OutlineMapPanel(
                genericPanelWidth,
                genericPanelHeight,
                controllerPanelX,
                controllerPanelY,
                5
        );
        SwingUiUtils.recursivelySetBackground(ColorPalette.TRANSPARENT, mOutlineMapPanel);

        OutlineButton actionsButton = mOutlineMapPanel.createButton("Actions");
        OutlineButton movementButton = mOutlineMapPanel.createButton("Movement");
        OutlineButton summaryButton = mOutlineMapPanel.createButton("Summary");
        OutlineButton settingsButton = mOutlineMapPanel.createButton("Settings");
        OutlineButton endTurnButton =  mOutlineMapPanel.createButton("End Turn");


//        SwingUiUtils.recursivelySetBackground(color, mOutlineMapPanel);
        SwingUiUtils.setBackgroundsFor( color, actionsButton, movementButton, summaryButton, settingsButton, endTurnButton );
        SwingUiUtils.addHoverEffects(new JComponent[]{ actionsButton, movementButton, summaryButton, settingsButton, endTurnButton });
        Font font = FontPool.getInstance().getFontForHeight(mOutlineMapPanel.getComponentHeight());
        SwingUiUtils.recursivelySetFont(font, mOutlineMapPanel);

//        mControllerPanel = new ControllerPanel(
//                genericPanelWidth,
//                genericPanelHeight,
//                controllerPanelX,
//                controllerPanelY
//        );


        // SETUP UI CONTROLLER
        mActionsPanel = new ActionsPanel(
                genericPanelWidth, genericPanelHeight, controllerPanelX, controllerPanelY,
                actionsButton, new OutlineButton("Exit")
        );
        mMovementPanel = new MovementPanel(
                genericPanelWidth, genericPanelHeight, controllerPanelX, controllerPanelY,
                movementButton, new OutlineButton("Exit")
        );
        mSummaryPanel = new SummaryPanel(
                genericPanelWidth, genericPanelHeight, controllerPanelX, controllerPanelY,
                summaryButton, new OutlineButton("Exit")
        );
        mSettingsPanel = new SettingsPanel(
                genericPanelWidth, genericPanelHeight, controllerPanelX, controllerPanelY,
                settingsButton, new OutlineButton("Exit")
        );

        controllerUis = new ControllerUI[] {
                mActionsPanel,
                mMovementPanel,
                mSummaryPanel,
                mSettingsPanel
        };

        for (ControllerUI controllerUi : controllerUis) {
            controllerUi.setVisible(false);
            SwingUiUtils.recursivelySetBackground(color, controllerUi);

            JButton enterButton = controllerUi.getEnterButton();
            enterButton.addActionListener(e -> {
                controllerUi.setVisible(true);
                mOutlineMapPanel.setVisible(false);
                controllerUi.onOpenAction();
            });

            JButton exitButton = controllerUi.getExitButton();
            exitButton.addActionListener(e -> {
                controllerUi.setVisible(false);
                mOutlineMapPanel.setVisible(true);
                controllerUi.onCloseAction();
            });

            SwingUiUtils.setHoverEffect(exitButton);
            exitButton.setFont(FontPool.getInstance().getFontForHeight((int) exitButton.getPreferredSize().getHeight()));
        }


        mEndTurnButton = endTurnButton;

        mActionDetailsPanel = new ActionDetailsPanel(
                genericPanelWidth,
                genericPanelHeight,
                actionPanelX,
                actionPanelY
        );
        mActionDetailsPanel.setBackground(color);
        mActionDetailsPanel.setVisible(false);

        mTimeLinePanel = new TimeLinePanel(
                timelinePanelWidth,
                timelinePanelHeight,
                timelinePanelX,
                timelinePanelY
        );

        mSelectionPanel = new SelectionPanel(
                selectionPanelWidth,
                selectionPanelHeight,
                selectionPanelX,
                selectionPanelY
        );
        SwingUiUtils.recursivelySetBackground(color, mSelectionPanel);
//        mSelectionPanel.setBackground(color);

//        SwingUiUtils.setStylizedRaisedBevelBorder(mSelectionPanel, 1);
        mSelectionPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.DARK_GRAY, Color.GRAY));
//        mTimeLinePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.DARK_GRAY, Color.GRAY));
        mActionDetailsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.DARK_GRAY, Color.GRAY));
//        mBlankHoldingPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.DARK_GRAY, Color.GRAY));


        SwingUiUtils.recursivelySetBackground(color, mSelectionPanel);

//        loggerHUD = new GameLogHUD((int) (width * .25), (int) (height * .25));
//        loggerHUD.setPreferredLocation(10, 10);

        setLayout(null);
        setOpaque(false);
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());

        add(mSelectionPanel);

        for (GameUI controllerScene : controllerUis) {
            add(controllerScene);
        }

        add(mActionDetailsPanel);
        add(mOutlineMapPanel);
        add(mTimeLinePanel);
    }

    @Override
    public void gameUpdate(GameModel model) {
        mTimeLinePanel.gameUpdate(model);
        mActionDetailsPanel.gameUpdate(model);
        mSelectionPanel.gameUpdate(model);


        for (ControllerUI controllerUI : controllerUis) {
            controllerUI.gameUpdate(model);
        }


        // Setup action detail panel
        boolean shouldShowActionDetails = mActionsPanel.isShowing() &&  mActionDetailsPanel.hasContent();
        mActionDetailsPanel.setVisible(shouldShowActionDetails);
        boolean shouldUpdateActionDetails = mActionsPanel.isDirty();
        if (shouldUpdateActionDetails) {
            mActionDetailsPanel.forceUpdate(mActionsPanel.getSelectedAction());
            mActionsPanel.clean();
        }

        if (!mSetupEndTurnButton) {
            mEndTurnButton.addActionListener(e -> {
                model.getGameState().setControllerToHomeScreen(true);
                model.getGameState().setEndCurrentUnitsTurn(true);
            });
            mSetupEndTurnButton = true;
        }

        if (model.getGameState().shouldChangeControllerToHomeScreen()) {
            mMovementPanel.setVisible(false);
            mActionsPanel.setVisible(false);
            mSummaryPanel.setVisible(false);
            mSettingsPanel.setVisible(false);
            mOutlineMapPanel.setVisible(true);
            model.getGameState().setControllerToHomeScreen(false);
        }
    }

    private void setupButtonFocus(GameModel model, Entity unitEntity) {
        Entity entity = model.getSpeedQueue().peek();
        if (entity == null) { return; }

        MovementComponent movementComponent = entity.get(MovementComponent.class);
        model.getGameState().setTileToGlideTo(movementComponent.getCurrentTile());
        model.getGameState().setupEntitySelections(movementComponent.getCurrentTile());
    }
}
