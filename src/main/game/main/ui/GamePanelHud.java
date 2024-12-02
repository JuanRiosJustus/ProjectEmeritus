package main.game.main.ui;

import main.constants.StateLock;
import main.engine.Engine;
import main.game.main.GameAPI;
import main.game.main.GameController;
import main.game.main.GameModel;
import main.graphics.GameUI;
import main.ui.huds.*;
import main.ui.huds.controls.OutlineMapPanel;
import org.json.JSONObject;

import javax.swing.JButton;
import java.awt.Color;
import java.util.List;

public class GamePanelHud extends GameUI {
    private GameController mGameController;
    private TimeLinePanel mTimeLinePanel;
    private JButton mEndTurnButton = new JButton();
    private boolean mSetupEndTurnButton = false;
    private SettingsPanel mSettingsPanel = null;
    private OutlineMapPanel mOutlineMapPanel = null;
    private Color color = null;
    private UiShowingManager mUiShowingManager = null;

    private MainControlsPanel mMainControlsPanel;
    private ActionsPanel mActionsPanel;
    private MovesPanel mMovesPanel;
    private MiniTileInfoPanel mMiniTileInfoPanel;
    private MiniUnitInfoPanel mMiniUnitInfoPanel;
    private StateLock mStateLock = new StateLock();
    private int paddingForWidth = 0;
    private int paddingForHeight = 0;
    private int genericPanelWidth = 0;
    private int genericPanelHeight = 0;
    private int mWidth = 0;
    private int mHeigth = 0;

    private JSONObject mEphemeralMessage = new JSONObject();
    public GamePanelHud(GameController gameController, int width, int height) {
        super(width, height);

        setLayout(null);

        mGameController = gameController;
        mUiShowingManager = new UiShowingManager();
        color = Color.DARK_GRAY;

        mWidth = width;
        mHeigth = height;

        // Panels must allow space between one another At max, 4 panel per axis, just because
        paddingForWidth = (int) (width * .01);
        paddingForHeight = (int) (height * .04);

        // Panels must allow space between one another At max, 4 panel per axis, just because
        genericPanelWidth = (int) (width * .25);
        genericPanelHeight = (int) (height * .25);

        // Never more than 4 UIs per axis, so .2 * width left for padding
        int genericPanelWidthPadding = (int) (width * .05);
        int genericPanelHeightPadding = (int) (height * .05);

        // Panel for the main controller buttons *Attack,Move,Settings,etc...
        int controllerPanelX = width - genericPanelWidth - genericPanelWidthPadding;
        int controllerPanelY = height - genericPanelHeight - genericPanelHeightPadding - Engine.getInstance().getHeaderSize();

        int timelinePanelWidth = (int) (width * .70);
        int timelinePanelHeight = (int) (height * .075);

//        int controllerPanelX = width - genericPanelWidth - paddingForWidth;
//        int controllerPanelY = height - genericPanelHeight - paddingForHeight - Engine.getInstance().getHeaderSize();

        mTimeLinePanel = new TimeLinePanel(timelinePanelWidth, timelinePanelHeight);
        mTimeLinePanel.setBounds(genericPanelWidthPadding, genericPanelHeightPadding, timelinePanelWidth, timelinePanelHeight);
//        mTimeLinePanel.setBounds(timelinePanelX, timelinePanelY, timelinePanelWidth, timelinePanelHeight);


        int controlsPanelWidth = (int) (mWidth * .25);
        int controlsPanelHeight = (int) (mHeigth * .2);
        int controlsPanelX = mWidth - controlsPanelWidth - paddingForWidth;
        int controlsPanelY = mHeigth - controlsPanelHeight - (paddingForHeight);
        mMainControlsPanel = new MainControlsPanel(controlsPanelWidth, controlsPanelHeight, color);
        mMainControlsPanel.setBounds(controlsPanelX, controlsPanelY, controlsPanelWidth, controlsPanelHeight);
        add(mMainControlsPanel);


        mMainControlsPanel.getActionsButton().addActionListener(e -> {
            JSONObject jsonObject = mGameController.getTileOfCurrentUnitsTurn();
            if (jsonObject == null) { return; }
            mGameController.setTileToGlideTo(jsonObject);
            mGameController.setSelectedTiles(jsonObject);
        });


        mMainControlsPanel.getMoveButton().addActionListener(e -> {
            JSONObject jsonObject = mGameController.getTileOfCurrentUnitsTurn();
            if (jsonObject == null) { return; }
            mGameController.setTileToGlideTo(jsonObject);
            mGameController.setSelectedTiles(jsonObject);
        });

        mMainControlsPanel.getEndTurnButton().addActionListener(e -> {
            mEphemeralMessage.clear();
            mEphemeralMessage.put(GameAPI.SHOULD_END_THE_TURN, true);
            mGameController.updateGameState(mEphemeralMessage);
        });



        mSettingsPanel = new SettingsPanel(controlsPanelWidth, controlsPanelHeight, color);
        mSettingsPanel.setBounds(controlsPanelX, controlsPanelY, controlsPanelWidth, controlsPanelHeight);
        mSettingsPanel.setVisible(false);
        add(mSettingsPanel);
        mUiShowingManager.link(mMainControlsPanel, mMainControlsPanel.getSettingsButton(), mSettingsPanel, mSettingsPanel.getReturnButton());

        mActionsPanel = new ActionsPanel(controlsPanelWidth, controlsPanelHeight, color);
        mActionsPanel.setBounds(controlsPanelX, controlsPanelY, controlsPanelWidth, controlsPanelHeight);
        mActionsPanel.setVisible(false);
        add(mActionsPanel);
        mUiShowingManager.link(mMainControlsPanel, mMainControlsPanel.getActionsButton(), mActionsPanel, mActionsPanel.getReturnButton());

        mMovesPanel = new MovesPanel(controlsPanelWidth, controlsPanelHeight, color, 5);
        mMovesPanel.setBounds(controlsPanelX, controlsPanelY, controlsPanelWidth, controlsPanelHeight);
        mMovesPanel.setVisible(false);
        add(mMovesPanel);
        mUiShowingManager.link(mMainControlsPanel, mMainControlsPanel.getMoveButton(), mMovesPanel, mMovesPanel.getReturnButton());







        int miniTileInfoPanelWidth = (int) (mWidth * .2);
        int miniTileInfoPanelHeight = (int) (mHeigth * .05);
        int miniTileInfoPanelX = paddingForWidth;
        int miniTileInfoPanelY = mHeigth - paddingForHeight - miniTileInfoPanelHeight;

        mMiniTileInfoPanel = new MiniTileInfoPanel(miniTileInfoPanelWidth, miniTileInfoPanelHeight, color);
        mMiniTileInfoPanel.setBounds(miniTileInfoPanelX, miniTileInfoPanelY, miniTileInfoPanelWidth, miniTileInfoPanelHeight);
        mMiniTileInfoPanel.setVisible(false);
        add(mMiniTileInfoPanel);

        mMiniTileInfoPanel.getValueButton().addActionListener(e -> {
            List<JSONObject> jsonObject = mGameController.getSelectedTiles();
            if (jsonObject == null || jsonObject.isEmpty()) { return; }
            JSONObject singleTile = jsonObject.get(0);
            mGameController.setTileToGlideTo(singleTile);
            mGameController.setSelectedTiles(singleTile);
        });



        int miniUnitInfoPanelWidth = (int) (mWidth * .2);
        int miniUnitInfoPanelHeight = (int) (mHeigth * .2);
        int miniUnitInfoPanelX = paddingForWidth;
        int miniUnitInfoPanelY = miniTileInfoPanelY - (paddingForHeight / 2) - miniUnitInfoPanelHeight;

        mMiniUnitInfoPanel = new MiniUnitInfoPanel(miniUnitInfoPanelWidth, miniUnitInfoPanelHeight, color);
        mMiniUnitInfoPanel.setBounds(miniUnitInfoPanelX, miniUnitInfoPanelY, miniUnitInfoPanelWidth, miniUnitInfoPanelHeight);
        mMiniUnitInfoPanel.setVisible(false);
        add(mMiniUnitInfoPanel);



        add(mTimeLinePanel);
    }

    @Override
    public void gameUpdate(GameController gameController) {
        mEphemeralMessage.clear();

        mTimeLinePanel.gameUpdate(gameController);
        mMiniTileInfoPanel.gameUpdate(gameController);
        mMiniUnitInfoPanel.gameUpdate(gameController);
        mSettingsPanel.gameUpdate(gameController);
        mMovesPanel.gameUpdate(gameController);
        mActionsPanel.gameUpdate(gameController);
        mMainControlsPanel.gameUpdate(gameController);

        boolean shouldGoToHomeControls = mGameController.consumeShouldAutomaticallyGoToHomeControls();

        if (shouldGoToHomeControls) {
            mMainControlsPanel.setVisible(true);
            mMovesPanel.setVisible(false);
            mActionsPanel.setVisible(false);
        }
    }
}
