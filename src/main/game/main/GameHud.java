package main.game.main;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.JavaFXUtils;
import main.ui.*;
import main.ui.game.*;
import main.ui.TimeLinePanel;
import com.alibaba.fastjson2.JSONObject;

public class GameHud extends GamePanel {
    private final MainControlsPanel mMainControlsPanel;
    private final AbilitySelectionPanel mAbilitySelectionPanel;
    private final MovementPanel mMovementPanel;
    private final SettingsPanel mSettingsPanel;
    private final StatisticsPanel mStatisticsPanel;
    private final GreaterStatisticsPanel mGreaterStatisticsPanel;
    private final AbilityDescriptionPanel mAbilityDescriptionPanel;
    private final GreaterAbilityPanel mGreaterAbilityPanel;
    private final TimeLinePanel mTimeLinePanel;
    private final SelectedTilePanel mSelectedTilePanel;
    private final DamagePreviewPanel mDamagePreviewFromPanel;
    private final DamagePreviewPanel mDamagePreviewToPanel;
    private final DevPanel mDevPanel;
    private final TilerLayersStage mLayersStage;

    public GameHud(GameModel gameModel, int width, int height) {
        super(width, height);

        int verticalPadding = (int) (height * .01);
        int horizontalPadding = (int) (width * .01);

        Color color = Color.GREY;

        // Create main controls panel
        int mainControlsWidth = (int) (width * .25);
        int mainControlsHeight = (int) (height * .25);
        int mainControlsX = width - mainControlsWidth - horizontalPadding;
        int mainControlsY = height - mainControlsHeight - verticalPadding;
        mMainControlsPanel = new MainControlsPanel(
                mainControlsX,
                mainControlsY,
                mainControlsWidth,
                mainControlsHeight,
                color
        );

        mMainControlsPanel.getStandbyButton().getFirst().getUnderlyingButton().setOnMouseReleased(e -> {
            gameModel.setUserSelectedStandby(true);
        });

        mAbilitySelectionPanel = new AbilitySelectionPanel(
                mainControlsX,
                mainControlsY,
                mainControlsWidth,
                mainControlsHeight,
                color,
                4
        );
        mAbilitySelectionPanel.setVisible(false);


        link(
                mMainControlsPanel,
                mMainControlsPanel.getAbilitiesButton().getFirst().getUnderlyingButton(),
                mAbilitySelectionPanel, mAbilitySelectionPanel.getEscapeButton(),
                () -> {
                    String currentUnitTileID = gameModel.getActiveUnitTileID();

                    JSONObject request = new JSONObject();
                    request.put("tile_id", currentUnitTileID);
                    request.put("camera_id", "0");
                    request.put("source", "ability_selection_hud");
                    gameModel.glideCameraToTile(request);


//                    JSONObject response = gameModel.getCurrentActiveEntityData();
//                    JSONObject focusRequest = new JSONObject();
//                    focusRequest.put("id", response.getString("id"));
//                    focusRequest.put("camera", "0");
//                    gameModel.focusCamerasAndSelectionsOnActiveEntity(focusRequest);
                }
        );


        mMovementPanel = new MovementPanel(
                mainControlsX,
                mainControlsY,
                mainControlsWidth,
                mainControlsHeight,
                color,
                4
        );
        mMovementPanel.setVisible(false);
        link(
                mMainControlsPanel,
                mMainControlsPanel.getMovementButton().getFirst().getUnderlyingButton(),
                mMovementPanel,
                mMovementPanel.getEscapeButton(),
                () -> {
                    String currentUnitTileID = gameModel.getActiveUnitTileID();

                    JSONObject request = new JSONObject();
                    request.put("tile_id", currentUnitTileID);
                    request.put("camera_id", "0");
                    request.put("source", "ability_selection_hud");
                    gameModel.glideCameraToTile(request);

//                    JSONObject entityOfCurrentTurnResponse = gameModel.getCurrentActiveEntityData();
//                    JSONObject focusRequest = new JSONObject();
//                    focusRequest.put("id", entityOfCurrentTurnResponse.getString("id"));
//                    focusRequest.put("camera", "0");
//                    gameModel.focusCamerasAndSelectionsOnActiveEntity(focusRequest);
                }
        );

        mLayersStage = new TilerLayersStage(mainControlsWidth, mainControlsHeight);
        mLayersStage.show();

        mDevPanel = new DevPanel(gameModel, mainControlsWidth, mainControlsHeight);
        mDevPanel.setX(mLayersStage.getX());
        mDevPanel.setY(mLayersStage.getY() + mLayersStage.getHeight());
        mDevPanel.show();




        mSettingsPanel = new SettingsPanel(
                mainControlsX,
                mainControlsY,
                mainControlsWidth,
                mainControlsHeight,
                color,
                4
        );
        mSettingsPanel.setVisible(false);
        link(
                mMainControlsPanel,
                mMainControlsPanel.getSettingsButton().getFirst().getUnderlyingButton(),
                mSettingsPanel,
                mSettingsPanel.getEscapeButton(),
                () -> {
                    mDevPanel.show();
                });





        mStatisticsPanel = new StatisticsPanel(
                mainControlsX,
                mainControlsY,
                mainControlsWidth,
                mainControlsHeight,
                color,
                4
        );
        mStatisticsPanel.setVisible(false);
        link(mMainControlsPanel, mMainControlsPanel.getStatisticsButton().getFirst().getUnderlyingButton(),
                mStatisticsPanel, mStatisticsPanel.getEscapeButton());


        int greaterStatisticsInformationPanelWidth = mainControlsWidth;
        int greaterStatisticsInformationPanelHeight = height - mainControlsHeight - (verticalPadding * 3);
        int greaterStatisticsInformationPanelX = width - greaterStatisticsInformationPanelWidth - horizontalPadding;
        int greaterStatisticsInformationPanelY = verticalPadding;
        mGreaterStatisticsPanel = new GreaterStatisticsPanel(
                greaterStatisticsInformationPanelX,
                greaterStatisticsInformationPanelY,
                greaterStatisticsInformationPanelWidth,
                greaterStatisticsInformationPanelHeight,
                color, 5
        );
        mGreaterStatisticsPanel.setVisible(false);


        int greaterAbilityInformationPanelWidth = mainControlsWidth;
        int greaterAbilityInformationPanelHeight = height - mainControlsHeight - (verticalPadding * 3);
        int greaterAbilityInformationPanelX = width - greaterAbilityInformationPanelWidth - horizontalPadding;
        int greaterAbilityInformationPanelY = verticalPadding;
        mGreaterAbilityPanel = new GreaterAbilityPanel(
                greaterAbilityInformationPanelX,
                greaterAbilityInformationPanelY,
                greaterAbilityInformationPanelWidth,
                greaterAbilityInformationPanelHeight,
                color,
                6
        );
//        mGreaterAbilityPanel.setVisible(true);

        mAbilityDescriptionPanel = new AbilityDescriptionPanel(
                greaterAbilityInformationPanelX,
                greaterAbilityInformationPanelY,
                greaterAbilityInformationPanelWidth,
                greaterAbilityInformationPanelHeight / 2,
                color,
                6
        );
        mAbilityDescriptionPanel.setVisible(false);


        int turnOrderPanelWidth = mainControlsWidth * 2;
        int turnOrderPanelHeight = mainControlsHeight / 3;
        int turnOrderPanelX = horizontalPadding;
        int turnOrderPanelY = verticalPadding;
        mTimeLinePanel = new TimeLinePanel(
                turnOrderPanelX,
                turnOrderPanelY,
                turnOrderPanelWidth,
                turnOrderPanelHeight,
                color,
                10
        );



        int tileSelectionPanelWidth = (int) (mainControlsWidth * .6);
        int tileSelectionPanelHeight = mainControlsHeight;
        int tileSelectionPanelX = horizontalPadding;
        int tileSelectionPanelY= mainControlsY;
        mSelectedTilePanel = new SelectedTilePanel(
                tileSelectionPanelX,
                tileSelectionPanelY,
                tileSelectionPanelWidth,
                tileSelectionPanelHeight,
                color,
                gameModel
        );


        int damagePreviewPanelWidth = mainControlsWidth;
        int damagePreviewPanelHeight = (mainControlsHeight) / 2;
        int damagePreviewPanelX = (tileSelectionPanelX + tileSelectionPanelWidth) + 20;
        int damagePreviewPanelY = mainControlsY + damagePreviewPanelHeight;
        mDamagePreviewFromPanel = new DamagePreviewPanel(
                damagePreviewPanelX,
                damagePreviewPanelY,
                damagePreviewPanelWidth,
                damagePreviewPanelHeight,
                color
        );

        mDamagePreviewToPanel = new DamagePreviewPanel(
                (mainControlsX - damagePreviewPanelWidth) - 20,
                damagePreviewPanelY,
                damagePreviewPanelWidth,
                damagePreviewPanelHeight,
                color
        );


        mGreaterStatisticsPanel.setVisible(false);
        mStatisticsPanel.getBanner().setOnMousePressed(e -> {
            mGreaterStatisticsPanel.setVisible(true);
            mGreaterAbilityPanel.setVisible(false);
        });
        mStatisticsPanel.getEscapeButton().setOnMousePressed(e -> {
            mGreaterStatisticsPanel.setVisible(false);
            mGreaterAbilityPanel.setVisible(false);
        });


        mGreaterAbilityPanel.setVisible(false);
        mAbilitySelectionPanel.getBanner().setOnMousePressed(e -> {
//            mGreaterAbilityPanel.setVisible(!mGreaterAbilityPanel.isVisible());
            mAbilityDescriptionPanel.setVisible(!mAbilityDescriptionPanel.isVisible());
            mGreaterStatisticsPanel.setVisible(false);
        });
        mAbilitySelectionPanel.getEscapeButton().setOnMousePressed(e -> {
//            mGreaterAbilityPanel.setVisible(false);
            mAbilityDescriptionPanel.setVisible(false);
            mGreaterStatisticsPanel.setVisible(false);
        });


//        mMovementPanel.getBanner().setOnMousePressed(e -> {
////            mGreaterStatisticsInformationPanel.setVisible(true);
////            mGreaterAbilityInformationPanel.setVisible(false);
//        });
//        mMovementPanel.getEscapeButton().setOnMousePressed(e -> {
////            mGreaterAbilityInformationPanel.setVisible(false);
////            mGreaterStatisticsInformationPanel.setVisible(false);
//        });


        mSelectedTilePanel.getLabel().getUnderlyingButton().setOnMousePressed(e -> {
            mGreaterStatisticsPanel.setVisible(false);
            mGreaterAbilityPanel.setVisible(false);
        });


        Pane containerPane = JavaFXUtils.createWrapperPane(width, height);
        containerPane.getChildren().addAll(
                mTimeLinePanel,
                mMainControlsPanel,
                mAbilitySelectionPanel,
                mMovementPanel,
                mSettingsPanel,
                mStatisticsPanel,
                mGreaterStatisticsPanel,
                mGreaterAbilityPanel,
                mSelectedTilePanel,
                mDamagePreviewFromPanel,
                mDamagePreviewToPanel,
                mAbilityDescriptionPanel
        );

        getChildren().add(containerPane);
        setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        setPickOnBounds(false); // Allow clicks to pass through
        setFocusTraversable(false);
    }

    public void gameUpdate(GameModel gameModel) {

        mDevPanel.gameUpdate(gameModel);
        mLayersStage.gameUpdate(gameModel);

        mMainControlsPanel.gameUpdate(gameModel);
        mAbilitySelectionPanel.gameUpdate(gameModel);
        mMovementPanel.gameUpdate(gameModel);
        mTimeLinePanel.gameUpdate(gameModel);
        mStatisticsPanel.gameUpdate(gameModel);
        mSelectedTilePanel.gameUpdate(gameModel);

        mDamagePreviewFromPanel.gameUpdateDamageFrom(gameModel, mAbilitySelectionPanel);
        mDamagePreviewToPanel.gameUpdateDamageToPanel(gameModel, mDamagePreviewFromPanel);


        if (mMovementPanel.isVisible()) {
            mGreaterStatisticsPanel.gameUpdate(gameModel, mMovementPanel);
        } else if (mStatisticsPanel.isVisible()) {
            mGreaterStatisticsPanel.gameUpdate(gameModel, mStatisticsPanel);
        }

        // Logic for handling ability selection
        if (mAbilitySelectionPanel.isVisible()) {
            mAbilityDescriptionPanel.gameUpdate(gameModel);
            String selectedAbility = mAbilitySelectionPanel.consumeAbilitySelected();
            if (selectedAbility != null) { mAbilityDescriptionPanel.setVisible(true); }
            mGreaterStatisticsPanel.setVisible(false);
        }

        String selectedEntity = mSelectedTilePanel.consumeSelectedEntityID();
        if (selectedEntity != null) {
            mGreaterStatisticsPanel.setVisible(true);
            mAbilityDescriptionPanel.setVisible(false);
        }

        if (mGreaterStatisticsPanel.isVisible()) {
            mGreaterStatisticsPanel.gameUpdate(gameModel);
        }


        boolean shouldGoToHomeControls = gameModel.consumeCloseAllSubControllers();

        if (shouldGoToHomeControls) {
            mMainControlsPanel.setVisible(true);
            mMovementPanel.setVisible(false);
            mAbilitySelectionPanel.setVisible(false);
            mGreaterAbilityPanel.setVisible(false);
            mAbilityDescriptionPanel.setVisible(false);
            mGreaterStatisticsPanel.setVisible(false);
        }
    }


    private void link(Region source, Button sourceToDestination, Region destination, Button destinationToSource) {
        sourceToDestination.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            source.setVisible(false);
            destination.setVisible(true);
        });

        destinationToSource.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            destination.setVisible(false);
            source.setVisible(true);
        });
    }

    private void link(Region source, Button sourceToDestination, Region destination, Button destinationToSource, Runnable runnable) {
        sourceToDestination.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            source.setVisible(false);
            destination.setVisible(true);
            runnable.run();
        });

        destinationToSource.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            destination.setVisible(false);
            source.setVisible(true);
        });
    }
}
