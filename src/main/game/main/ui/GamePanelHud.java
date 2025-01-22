package main.game.main.ui;

import main.constants.StateLock;
import main.engine.Engine;
import main.game.main.GameAPI;
import main.game.main.GameController;
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
    private MiniActionInfoPanel mMiniActionInfoPanel;
    private MiniTileInfoPanel mMiniTileInfoPanel;
    private MiniUnitInfoPanel mMiniUnitInfoPanel;
    private StandardUnitInfoPanel mStandardUnitInfoPanel;
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

//        height = height - Engine.getInstance().getHeaderSize();

        // Panels must allow space between one another At max, 4 panel per axis, just because
        paddingForWidth = (int) (width * .01);
        paddingForHeight = (int) (height * .05);

        // Panels must allow space between one another At max, 4 panel per axis, just because
        genericPanelWidth = (int) (width * .25);
        genericPanelHeight = (int) (height * .25);

        int timelinePanelWidth = (int) (width * .70);
        int timelinePanelHeight = (int) (height * .075);

        mTimeLinePanel = new TimeLinePanel(timelinePanelWidth, timelinePanelHeight);
        mTimeLinePanel.setBounds(paddingForWidth, paddingForHeight, timelinePanelWidth, timelinePanelHeight);
//        mTimeLinePanel.setBounds(timelinePanelX, timelinePanelY, timelinePanelWidth, timelinePanelHeight);


        int mainControlsPanelWidth = (int) (mWidth * .25);
        int mainControlsPanelHeight = (int) (mHeigth * .2);
        int mainControlsPanelX = mWidth - mainControlsPanelWidth - paddingForWidth;
        int mainControlsPanelY = mHeigth - mainControlsPanelHeight - (paddingForHeight);
        mMainControlsPanel = new MainControlsPanel(mainControlsPanelWidth, mainControlsPanelHeight, color);
        mMainControlsPanel.setBounds(mainControlsPanelX, mainControlsPanelY, mainControlsPanelWidth, mainControlsPanelHeight);
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



        mSettingsPanel = new SettingsPanel(mainControlsPanelWidth, mainControlsPanelHeight, color);
        mSettingsPanel.setBounds(mainControlsPanelX, mainControlsPanelY, mainControlsPanelWidth, mainControlsPanelHeight);
        mSettingsPanel.setVisible(false);
        add(mSettingsPanel);
        mUiShowingManager.link(mMainControlsPanel, mMainControlsPanel.getSettingsButton(), mSettingsPanel, mSettingsPanel.getReturnButton());

        mActionsPanel = new ActionsPanel(mainControlsPanelWidth, mainControlsPanelHeight, color);
        mActionsPanel.setBounds(mainControlsPanelX, mainControlsPanelY, mainControlsPanelWidth, mainControlsPanelHeight);
        mActionsPanel.setVisible(false);
        add(mActionsPanel);
        mUiShowingManager.link(mMainControlsPanel, mMainControlsPanel.getActionsButton(), mActionsPanel, mActionsPanel.getReturnButton());

        mMovesPanel = new MovesPanel(mainControlsPanelWidth, mainControlsPanelHeight, color, 5);
        mMovesPanel.setBounds(mainControlsPanelX, mainControlsPanelY, mainControlsPanelWidth, mainControlsPanelHeight);
        mMovesPanel.setVisible(false);
        add(mMovesPanel);
        mUiShowingManager.link(mMainControlsPanel, mMainControlsPanel.getMoveButton(), mMovesPanel, mMovesPanel.getReturnButton());







        int miniTileInfoPanelWidth = (int) (mWidth * .25);
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



        int miniUnitInfoPanelWidth = miniTileInfoPanelWidth;
        int miniUnitInfoPanelHeight = (int) (mHeigth * .25);
        int miniUnitInfoPanelX = paddingForWidth;
        int miniUnitInfoPanelY = miniTileInfoPanelY - (paddingForHeight / 2) - miniUnitInfoPanelHeight;

        mMiniUnitInfoPanel = new MiniUnitInfoPanel(miniUnitInfoPanelWidth, miniUnitInfoPanelHeight, color);
        mMiniUnitInfoPanel.setBounds(miniUnitInfoPanelX, miniUnitInfoPanelY, miniUnitInfoPanelWidth, miniUnitInfoPanelHeight);
        mMiniUnitInfoPanel.setVisible(false);
        add(mMiniUnitInfoPanel);


        int miniActionInfoPanelWidth = mainControlsPanelWidth;
        int miniActionInfoPanelHeight = mainControlsPanelHeight;
//        int miniActionInfoPanelHeight = (int) (height - mainControlsPanelHeight - (paddingForHeight * 3));
        int miniActionInfoPanelX = mainControlsPanelX;
        int miniActionInfoPanelY = mainControlsPanelY - mainControlsPanelHeight - paddingForHeight;
//        int miniActionInfoPanelY = mainControlsPanelY - mainControlsPanelHeight - paddingForHeight;
//        int miniActionInfoPanelY = miniUnitInfoPanelY - miniActionInfoPanelHeight - paddingForHeight;
        mMiniActionInfoPanel = new MiniActionInfoPanel(miniActionInfoPanelWidth, miniActionInfoPanelHeight, color);
        mMiniActionInfoPanel.setBounds(miniActionInfoPanelX, miniActionInfoPanelY, miniActionInfoPanelWidth, miniActionInfoPanelHeight);
        mMiniActionInfoPanel.setVisible(false);
        add(mMiniActionInfoPanel);


        int standardInfoPanelWidth = (int) mainControlsPanelWidth;
        int standardInfoPanelHeight = (int) (height * .7);
//        int standardInfoPanelHeight = (int) (height * .95);
        int standardInfoPanelX = width - standardInfoPanelWidth - paddingForWidth;
        int standardInfoPanelY = (int) (height * .01);
//        int standardInfoPanelY = (int) (height - standardInfoPanelHeight - (height * .04));
        mStandardUnitInfoPanel = new StandardUnitInfoPanel(standardInfoPanelWidth, standardInfoPanelHeight, color);
        mStandardUnitInfoPanel.setBounds(standardInfoPanelX, standardInfoPanelY, standardInfoPanelWidth, standardInfoPanelHeight);
        mStandardUnitInfoPanel.setVisible(true);
        add(mStandardUnitInfoPanel);

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
        mMiniActionInfoPanel.gameUpdate(gameController);
        mStandardUnitInfoPanel.gameUpdate(gameController);

        mMiniActionInfoPanel.gameUpdate(gameController, mActionsPanel.getMonitoredAction(), mActionsPanel.getMonitoredEntity());
        if (!mActionsPanel.isShowing()) { mMiniActionInfoPanel.setVisible(false); }

        boolean shouldGoToHomeControls = mGameController.consumeShouldAutomaticallyGoToHomeControls();

        if (shouldGoToHomeControls) {
            mMainControlsPanel.setVisible(true);
            mMovesPanel.setVisible(false);
            mActionsPanel.setVisible(false);
        }
    }
}
