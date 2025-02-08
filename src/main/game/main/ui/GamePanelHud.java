package main.game.main.ui;

import main.game.main.GameAPI;
import main.game.main.GameController;
import main.graphics.GameUI;
import main.ui.huds.*;
import main.ui.huds.controls.OutlineMapPanel;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.JButton;
import java.awt.Color;

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
    private AbilitiesPanel mAbilitiesPanel;
    private MovementPanel mMovementPanel;
    private MiniActionInfoPanel mMiniActionInfoPanel;
    private MiniUnitInfoPanel mMiniUnitInfoPanel;
    private CurrentSelectionPanel mCurrentSelectionPanel;
    private UnitStatisticsPanel mUnitStatisticsPanel;
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


        int mMainControlsPanelWidth = (int) (mWidth * .25);
        int mMainControlsPanelHeight = (int) (mHeigth * .225);
        int mMainControlsPanelX = mWidth - mMainControlsPanelWidth - paddingForWidth;
        int mMainControlsPanelY = mHeigth - mMainControlsPanelHeight - (paddingForHeight);
        mMainControlsPanel = new MainControlsPanel(
                mMainControlsPanelX,
                mMainControlsPanelY,
                mMainControlsPanelWidth,
                mMainControlsPanelHeight,
                color
        );
        add(mMainControlsPanel);

        mMainControlsPanel.getActionsButton().addActionListener(e -> {
            // Can only select a single unit at a time ATM
            JSONArray currentTurnsUnitsTile = mGameController.getCurrentTurnsUnitsTile();
            if (currentTurnsUnitsTile.isEmpty()) { return; }
            String currentTurnsUnitsTileID = currentTurnsUnitsTile.getString(0);
            mGameController.setTileToGlideTo(currentTurnsUnitsTileID);
            mGameController.setSelectedTiles(currentTurnsUnitsTileID);
        });


        mMainControlsPanel.getMoveButton().addActionListener(e -> {
            JSONArray currentTurnsUnitsTile = mGameController.getCurrentTurnsUnitsTile();
            if (currentTurnsUnitsTile.isEmpty()) { return; }
            String currentTurnsUnitsTileID = currentTurnsUnitsTile.getString(0);
            mGameController.setTileToGlideTo(currentTurnsUnitsTileID);
            mGameController.setSelectedTiles(currentTurnsUnitsTileID);
        });

        mMainControlsPanel.getEndTurnButton().addActionListener(e -> {
            mEphemeralMessage.clear();
            mEphemeralMessage.put(GameAPI.SHOULD_END_THE_TURN, true);
            mGameController.updateGameState(mEphemeralMessage);
        });



        mSettingsPanel = new SettingsPanel(mMainControlsPanelWidth, mMainControlsPanelHeight, color);
        mSettingsPanel.setBounds(mMainControlsPanelX, mMainControlsPanelY, mMainControlsPanelWidth, mMainControlsPanelHeight);
        mSettingsPanel.setVisible(false);
        add(mSettingsPanel);
        mUiShowingManager.link(mMainControlsPanel, mMainControlsPanel.getSettingsButton(), mSettingsPanel, mSettingsPanel.getReturnButton());

        mAbilitiesPanel = new AbilitiesPanel(
                mMainControlsPanelX,
                mMainControlsPanelY,
                mMainControlsPanelWidth,
                mMainControlsPanelHeight,
                color,
                4
        );
        mAbilitiesPanel.setVisible(false);
        add(mAbilitiesPanel);
        mUiShowingManager.link(mMainControlsPanel, mMainControlsPanel.getActionsButton(), mAbilitiesPanel, mAbilitiesPanel.getBackButton());

        mMovementPanel = new MovementPanel(
                mMainControlsPanelX,
                mMainControlsPanelY,
                mMainControlsPanelWidth,
                mMainControlsPanelHeight,
                color,
                4
        );
//        mMovementPanel.setBounds(mMainControlsPanelX, mMainControlsPanelY, mMainControlsPanelWidth, mMainControlsPanelHeight);
        mMovementPanel.setVisible(false);
        add(mMovementPanel);
        mUiShowingManager.link(mMainControlsPanel, mMainControlsPanel.getMoveButton(), mMovementPanel, mMovementPanel.getBackButton());







        int mCurrentSelectionPanelWidth = (int) (mWidth * .125);
        int mCurrentSelectionPanelHeight = mMainControlsPanelHeight;
        int mCurrentSelectionPanelX = paddingForWidth;
        int mCurrentSelectionPanelY = mMainControlsPanelY;
        mCurrentSelectionPanel = new CurrentSelectionPanel(
                mCurrentSelectionPanelX,
                mCurrentSelectionPanelY,
                mCurrentSelectionPanelWidth,
                mCurrentSelectionPanelHeight,
                color
        );

        mCurrentSelectionPanel.setVisible(false);
        add(mCurrentSelectionPanel);




        int miniUnitInfoPanelWidth = mCurrentSelectionPanelWidth;
        int miniUnitInfoPanelHeight = (int) (mHeigth * .25);
        int miniUnitInfoPanelX = paddingForWidth;
        int miniUnitInfoPanelY = mCurrentSelectionPanelY - (paddingForHeight / 2) - miniUnitInfoPanelHeight;

        mMiniUnitInfoPanel = new MiniUnitInfoPanel(miniUnitInfoPanelWidth, miniUnitInfoPanelHeight, color);
        mMiniUnitInfoPanel.setBounds(miniUnitInfoPanelX, miniUnitInfoPanelY, miniUnitInfoPanelWidth, miniUnitInfoPanelHeight);
        mMiniUnitInfoPanel.setVisible(false);
//        add(mMiniUnitInfoPanel);


        int miniActionInfoPanelWidth = mMainControlsPanelWidth;
        int miniActionInfoPanelHeight = mMainControlsPanelHeight;
//        int miniActionInfoPanelHeight = (int) (height - mainControlsPanelHeight - (paddingForHeight * 3));
        int miniActionInfoPanelX = mCurrentSelectionPanelX + mCurrentSelectionPanelWidth + paddingForWidth;
        int miniActionInfoPanelY = mCurrentSelectionPanelY;
//        int miniActionInfoPanelY = mMainControlsPanelY - mMainControlsPanelHeight - paddingForHeight;
//        int miniActionInfoPanelY = mainControlsPanelY - mainControlsPanelHeight - paddingForHeight;
//        int miniActionInfoPanelY = miniUnitInfoPanelY - miniActionInfoPanelHeight - paddingForHeight;
        mMiniActionInfoPanel = new MiniActionInfoPanel(miniActionInfoPanelWidth, miniActionInfoPanelHeight, color);
        mMiniActionInfoPanel.setBounds(miniActionInfoPanelX, miniActionInfoPanelY, miniActionInfoPanelWidth, miniActionInfoPanelHeight);
        mMiniActionInfoPanel.setVisible(false);
        add(mMiniActionInfoPanel);


        int standardInfoPanelWidth = (int) mMainControlsPanelWidth;
        int standardInfoPanelHeight = (int) (height * .7);
//        int standardInfoPanelHeight = (int) (height * .95);
        int standardInfoPanelX = width - standardInfoPanelWidth - paddingForWidth;
        int standardInfoPanelY = (int) (height * .01);
//        int standardInfoPanelY = (int) (height - standardInfoPanelHeight - (height * .04));
        mUnitStatisticsPanel = new UnitStatisticsPanel(standardInfoPanelWidth, standardInfoPanelHeight, color);
        mUnitStatisticsPanel.setBounds(standardInfoPanelX, standardInfoPanelY, standardInfoPanelWidth, standardInfoPanelHeight);
        mUnitStatisticsPanel.setVisible(false);




//        mUiShowingManager.link(mMiniUnitInfoPanel.., mMainControlsPanel.getSettingsButton(), mSettingsPanel, mSettingsPanel.getReturnButton());



        mCurrentSelectionPanel.getLabelButton().addActionListener(e -> {
            JSONArray selectedTiles = mGameController.getSelectedTiles();
            if (selectedTiles == null || selectedTiles.isEmpty()) { return; }
            String selectedTile = selectedTiles.getString(0);

            JSONArray request = new JSONArray();
            request.put(selectedTile);

            mGameController.setTileToGlideTo(request);
            mUnitStatisticsPanel.setMonitoredUnitEntityID(mCurrentSelectionPanel.getMonitoredUnitID());
        });





        add(mUnitStatisticsPanel);

        add(mTimeLinePanel);
    }

    @Override
    public void gameUpdate(GameController gameController) {
        mEphemeralMessage.clear();

        mTimeLinePanel.gameUpdate(gameController);
        mCurrentSelectionPanel.gameUpdate(gameController);
        mMiniUnitInfoPanel.gameUpdate(gameController);
        mSettingsPanel.gameUpdate(gameController);
        mMovementPanel.gameUpdate(gameController);
        mAbilitiesPanel.gameUpdate(gameController);
        mMainControlsPanel.gameUpdate(gameController);
        mMiniActionInfoPanel.gameUpdate(gameController);


//        if (mSelectionInfoPanel.getMonitoredUnitID() == null) { mStandardUnitInfoPanel.setMonitoredUnitEntityID(null); }
        mUnitStatisticsPanel.gameUpdate(gameController);

        if (!mAbilitiesPanel.isShowing()) { mMiniActionInfoPanel.setVisible(false); }
        mMiniActionInfoPanel.gameUpdate(gameController, mAbilitiesPanel.getMonitoredAction(), mAbilitiesPanel.getMonitoredEntity());





//        gameController.setMovementPanelIsOpen();


        boolean shouldGoToHomeControls = mGameController.consumeShouldAutomaticallyGoToHomeControls();

        if (shouldGoToHomeControls) {
            mMainControlsPanel.setVisible(true);
            mMovementPanel.setVisible(false);
            mAbilitiesPanel.setVisible(false);
        }
    }
}
