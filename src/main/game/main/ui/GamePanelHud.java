package main.game.main.ui;

import main.game.main.GameAPI;
import main.game.main.GameControllerV1;
import main.graphics.GameUI;
import main.ui.huds.*;
import main.ui.huds.controls.OutlineMapPanel;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.JButton;
import java.awt.Color;

public class GamePanelHud extends GameUI {
    private GameControllerV1 mGameControllerV1;
    private TimeLinePanel mTimeLinePanel;
    private JButton mEndTurnButton = new JButton();
    private boolean mSetupEndTurnButton = false;
    private SettingsPanel mSettingsPanel = null;
    private OutlineMapPanel mOutlineMapPanel = null;
    private Color color = null;
    private UiShowingManager mUiShowingManager = null;

    private MainControlsPanelV2 mMainControlsPanelV2;
    private AbilitySelectionPanelV1 mAbilitySelectionPanelV1;
    private AbilityInformationPanel mAbilityInformationPanel;
    private MovementSubPanel mMovementPanel;
    private StatisticsSubPanel mStatisticsPanel;
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
    public GamePanelHud(GameControllerV1 gameControllerV1, int width, int height) {
        super(width, height);

        setLayout(null);

        mGameControllerV1 = gameControllerV1;
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
        mMainControlsPanelV2 = new MainControlsPanelV2(
                mMainControlsPanelX,
                mMainControlsPanelY,
                mMainControlsPanelWidth,
                mMainControlsPanelHeight,
                color
        );
        add(mMainControlsPanelV2);

        mMainControlsPanelV2.getAbilitiesButton().addActionListener(e -> {
            // Can only select a single unit at a time ATM
            JSONArray currentTurnsUnitsTile = mGameControllerV1.getCurrentTurnsUnitsTile();
            if (currentTurnsUnitsTile.isEmpty()) { return; }
            String currentTurnsUnitsTileID = currentTurnsUnitsTile.getString(0);
            mGameControllerV1.setTileToGlideTo(currentTurnsUnitsTileID);
            mGameControllerV1.setSelectedTiles(currentTurnsUnitsTileID);
        });


        mMainControlsPanelV2.getMovementButton().addActionListener(e -> {
            JSONArray currentTurnsUnitsTile = mGameControllerV1.getCurrentTurnsUnitsTile();
            if (currentTurnsUnitsTile.isEmpty()) { return; }
            String currentTurnsUnitsTileID = currentTurnsUnitsTile.getString(0);
            mGameControllerV1.setTileToGlideTo(currentTurnsUnitsTileID);
            mGameControllerV1.setSelectedTiles(currentTurnsUnitsTileID);
        });

        mMainControlsPanelV2.getEndTurnButton().addActionListener(e -> {
            mEphemeralMessage.clear();
            mEphemeralMessage.put(GameAPI.SHOULD_END_THE_TURN, true);
            mGameControllerV1.updateGameState(mEphemeralMessage);
        });



        mSettingsPanel = new SettingsPanel(mMainControlsPanelWidth, mMainControlsPanelHeight, color);
        mSettingsPanel.setBounds(mMainControlsPanelX, mMainControlsPanelY, mMainControlsPanelWidth, mMainControlsPanelHeight);
        mSettingsPanel.setVisible(false);
        add(mSettingsPanel);
        mUiShowingManager.link(mMainControlsPanelV2, mMainControlsPanelV2.getSettingsButton(), mSettingsPanel, mSettingsPanel.getReturnButton());


        mAbilitySelectionPanelV1 = new AbilitySelectionPanelV1(
                mMainControlsPanelX,
                mMainControlsPanelY,
                mMainControlsPanelWidth,
                mMainControlsPanelHeight,
                color,
                4
        );
        mAbilitySelectionPanelV1.setVisible(false);
        add(mAbilitySelectionPanelV1);
        mUiShowingManager.link(mMainControlsPanelV2, mMainControlsPanelV2.getAbilitiesButton(), mAbilitySelectionPanelV1, mAbilitySelectionPanelV1.getBackButton());

        mMovementPanel = new MovementSubPanel(
                mMainControlsPanelX,
                mMainControlsPanelY,
                mMainControlsPanelWidth,
                mMainControlsPanelHeight,
                color,
                4
        );
        mMovementPanel.setBannerTitleButton("Movement");
//        mMovementPanel.setBounds(mMainControlsPanelX, mMainControlsPanelY, mMainControlsPanelWidth, mMainControlsPanelHeight);
        mMovementPanel.setVisible(false);
        add(mMovementPanel);
        mUiShowingManager.link(mMainControlsPanelV2, mMainControlsPanelV2.getMovementButton(), mMovementPanel, mMovementPanel.getBannerBackButton());



        mStatisticsPanel = new StatisticsSubPanel(
                mMainControlsPanelX,
                mMainControlsPanelY,
                mMainControlsPanelWidth,
                mMainControlsPanelHeight,
                color,
                4
        );
        mStatisticsPanel.setBannerTitleButton("Statistics");
//        mMovementPanel.setBounds(mMainControlsPanelX, mMainControlsPanelY, mMainControlsPanelWidth, mMainControlsPanelHeight);
        mStatisticsPanel.setVisible(false);
        add(mStatisticsPanel);
        mUiShowingManager.link(mMainControlsPanelV2, mMainControlsPanelV2.getStatisticsButton(), mStatisticsPanel, mStatisticsPanel.getBannerBackButton());




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



        int abilityInformationPanelWidth = mMainControlsPanelWidth;
        int abilityInformationPanelHeight = mMainControlsPanelHeight;
////        int miniActionInfoPanelHeight = (int) (height - mainControlsPanelHeight - (paddingForHeight * 3));
        int abilityInformationPanelX = mCurrentSelectionPanelX + mCurrentSelectionPanelWidth + paddingForWidth;
        int abilityInformationPanelY = mCurrentSelectionPanelY;

//        int mCurrentSelectionPanelX = paddingForWidth;
//        int mCurrentSelectionPanelY = mMainControlsPanelY;
//
        mAbilityInformationPanel = new AbilityInformationPanel(
                abilityInformationPanelX,
                abilityInformationPanelY,
                abilityInformationPanelWidth,
                abilityInformationPanelHeight,
                color,
                4
        );
        mAbilityInformationPanel.setVisible(false);
        add(mAbilityInformationPanel);



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
        int standardInfoPanelX = width - standardInfoPanelWidth - paddingForWidth;
        int standardInfoPanelY = (int) (height * .01);
        mUnitStatisticsPanel = new UnitStatisticsPanel(
                standardInfoPanelX,
                standardInfoPanelY,
                standardInfoPanelWidth,
                standardInfoPanelHeight,
                color
        );
        mUnitStatisticsPanel.setVisible(false);




//        mUiShowingManager.link(mMiniUnitInfoPanel.., mMainControlsPanel.getSettingsButton(), mSettingsPanel, mSettingsPanel.getReturnButton());



        mCurrentSelectionPanel.getLabelButton().addActionListener(e -> {
            JSONArray selectedTiles = mGameControllerV1.getSelectedTiles();
            if (selectedTiles == null || selectedTiles.isEmpty()) { return; }
            String selectedTile = selectedTiles.getString(0);

            JSONArray request = new JSONArray();
            request.put(selectedTile);

            mGameControllerV1.setTileToGlideTo(request);
            mUnitStatisticsPanel.setMonitoredUnitEntityID(mCurrentSelectionPanel.getMonitoredUnitID());
        });





        add(mUnitStatisticsPanel);

        add(mTimeLinePanel);
    }

    @Override
    public void gameUpdate(GameControllerV1 gameControllerV1) {
        mEphemeralMessage.clear();

//        if (!mAbilitySelectionPanel.isShowing()) { mAbilityInformationPanel.setVisible(false); }
//        mAbilityInformationPanel.gameUpdate(gameController, mAbilitySelectionPanel.getMonitoredAction(), mAbilitySelectionPanel.getMonitoredEntity());

        mAbilityInformationPanel.gameUpdate(gameControllerV1, mAbilitySelectionPanelV1);

        mTimeLinePanel.gameUpdate(gameControllerV1);
        mCurrentSelectionPanel.gameUpdate(gameControllerV1);
        mMiniUnitInfoPanel.gameUpdate(gameControllerV1);
        mSettingsPanel.gameUpdate(gameControllerV1);
        mMovementPanel.gameUpdate(gameControllerV1);
        mStatisticsPanel.gameUpdate(gameControllerV1);
        mAbilitySelectionPanelV1.gameUpdate(gameControllerV1);
        mMainControlsPanelV2.gameUpdate(gameControllerV1);
        mMiniActionInfoPanel.gameUpdate(gameControllerV1);


//        if (mSelectionInfoPanel.getMonitoredUnitID() == null) { mStandardUnitInfoPanel.setMonitoredUnitEntityID(null); }
        mUnitStatisticsPanel.gameUpdate(gameControllerV1);





//        gameController.setMovementPanelIsOpen();


        boolean shouldGoToHomeControls = mGameControllerV1.consumeShouldAutomaticallyGoToHomeControls();

        if (shouldGoToHomeControls) {
            mMainControlsPanelV2.setVisible(true);
            mMovementPanel.setVisible(false);
            mAbilitySelectionPanelV1.setVisible(false);
        }
    }
}
