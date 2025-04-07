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
import org.json.JSONObject;

public class GameHud extends GamePanel {
    private final MainControlsPanel mMainControlsPanel;
    private final AbilitySelectionPanel mAbilitySelectionPanel;
    private final MovementInformationPanel mMovementInformationPanel;
    private final SettingsPanel mSettingsPanel;
    private final StatisticsInformationPanel mStatisticsInformationPanel;
    private final GreaterStatisticsInformationPanel mGreaterStatisticsInformationPanel;
    private final GreaterAbilityInformationPanel mGreaterAbilityInformationPanel;
    private final TimeLinePanel mTimeLinePanel;
    private final SelectedTilePanel mSelectedTilePanel;
    private final DevPanel mDevPanel;

    public GameHud(GameController controller, int width, int height) {
        super(width, height);

        int verticalPadding = (int) (height * .01);
        int horizontalPadding = (int) (width * .01);

        Color color = Color.DIMGRAY;

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

        mMainControlsPanel.getEndTurnButton().getFirst().getUnderlyingButton().setOnMouseReleased(e -> {
            controller.setEndTurn();
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
                mAbilitySelectionPanel, mAbilitySelectionPanel.getEscapeButton().getUnderlyingButton(),
                () -> {
                    JSONObject entityOfCurrentTurnResponse = controller.getEntityOfCurrentTurnsID();
                    JSONObject focusRequest = new JSONObject();
                    focusRequest.put("id", entityOfCurrentTurnResponse.getString("id"));
                    focusRequest.put("camera", "0");
                    controller.focusCamerasAndSelectionsOnActiveEntity(focusRequest);
                }
        );


        mMovementInformationPanel = new MovementInformationPanel(
                mainControlsX,
                mainControlsY,
                mainControlsWidth,
                mainControlsHeight,
                color,
                4
        );
        mMovementInformationPanel.setVisible(false);
        link(
                mMainControlsPanel,
                mMainControlsPanel.getMovementButton().getFirst().getUnderlyingButton(),
                mMovementInformationPanel,
                mMovementInformationPanel.getEscapeButton().getUnderlyingButton(),
                () -> {
                    JSONObject entityOfCurrentTurnResponse = controller.getEntityOfCurrentTurnsID();
                    JSONObject focusRequest = new JSONObject();
                    focusRequest.put("id", entityOfCurrentTurnResponse.getString("id"));
                    focusRequest.put("camera", "0");
                    controller.focusCamerasAndSelectionsOnActiveEntity(focusRequest);
                }
        );

        mDevPanel = new DevPanel(controller, mainControlsWidth, mainControlsHeight);
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
                mSettingsPanel.getEscapeButton().getUnderlyingButton(),
                () -> {
                    mDevPanel.show();
        });





        mStatisticsInformationPanel = new StatisticsInformationPanel(
                mainControlsX,
                mainControlsY,
                mainControlsWidth,
                mainControlsHeight,
                color,
                4
        );
        mStatisticsInformationPanel.setVisible(false);
        link(mMainControlsPanel, mMainControlsPanel.getStatisticsButton().getFirst().getUnderlyingButton(),
                mStatisticsInformationPanel, mStatisticsInformationPanel.getEscapeButton().getUnderlyingButton());


        int greaterStatisticsInformationPanelWidth = mainControlsWidth;
        int greaterStatisticsInformationPanelHeight = height - mainControlsHeight - (verticalPadding * 3);
        int greaterStatisticsInformationPanelX = width - greaterStatisticsInformationPanelWidth - horizontalPadding;
        int greaterStatisticsInformationPanelY = verticalPadding;
        mGreaterStatisticsInformationPanel = new GreaterStatisticsInformationPanel(
                greaterStatisticsInformationPanelX,
                greaterStatisticsInformationPanelY,
                greaterStatisticsInformationPanelWidth,
                greaterStatisticsInformationPanelHeight,
                color, 5
        );
        mGreaterStatisticsInformationPanel.setVisible(false);


        int greaterAbilityInformationPanelWidth = mainControlsWidth;
        int greaterAbilityInformationPanelHeight = height - mainControlsHeight - (verticalPadding * 3);
        int greaterAbilityInformationPanelX = width - greaterAbilityInformationPanelWidth - horizontalPadding;
        int greaterAbilityInformationPanelY = verticalPadding;
        mGreaterAbilityInformationPanel = new GreaterAbilityInformationPanel(
                greaterAbilityInformationPanelX,
                greaterAbilityInformationPanelY,
                greaterAbilityInformationPanelWidth,
                greaterAbilityInformationPanelHeight,
                color,
                6
        );
        mGreaterAbilityInformationPanel.setVisible(true);


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
                controller
        );




        mGreaterStatisticsInformationPanel.setVisible(false);
        mStatisticsInformationPanel.getBanner().getUnderlyingButton().setOnMousePressed(e -> {
            mGreaterStatisticsInformationPanel.setVisible(true);
            mGreaterAbilityInformationPanel.setVisible(false);
        });
        mStatisticsInformationPanel.getEscapeButton().getUnderlyingButton().setOnMousePressed(e -> {
            mGreaterStatisticsInformationPanel.setVisible(false);
            mGreaterAbilityInformationPanel.setVisible(false);
        });


        mGreaterAbilityInformationPanel.setVisible(false);
        mAbilitySelectionPanel.getBanner().getUnderlyingButton().setOnMousePressed(e -> {
            mGreaterAbilityInformationPanel.setVisible(true);
            mGreaterStatisticsInformationPanel.setVisible(false);
        });
        mAbilitySelectionPanel.getEscapeButton().getUnderlyingButton().setOnMousePressed(e -> {
            mGreaterAbilityInformationPanel.setVisible(false);
            mGreaterStatisticsInformationPanel.setVisible(false);
        });


        mMovementInformationPanel.getBanner().getUnderlyingButton().setOnMousePressed(e -> {
            mGreaterStatisticsInformationPanel.setVisible(true);
            mGreaterAbilityInformationPanel.setVisible(false);
        });
        mMovementInformationPanel.getEscapeButton().getUnderlyingButton().setOnMousePressed(e -> {
            mGreaterAbilityInformationPanel.setVisible(false);
            mGreaterStatisticsInformationPanel.setVisible(false);
        });


        mSelectedTilePanel.getLabel().getUnderlyingButton().setOnMousePressed(e -> {
            mGreaterStatisticsInformationPanel.setVisible(false);
            mGreaterAbilityInformationPanel.setVisible(false);
        });


        Pane containerPane = JavaFXUtils.createWrapperPane(width, height);
        containerPane.getChildren().addAll(
                mTimeLinePanel,
                mMainControlsPanel,
                mAbilitySelectionPanel,
                mMovementInformationPanel,
                mSettingsPanel,
                mStatisticsInformationPanel,
                mGreaterStatisticsInformationPanel,
                mGreaterAbilityInformationPanel,
                mSelectedTilePanel
        );

        getChildren().add(containerPane);
        setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        setPickOnBounds(false); // Allow clicks to pass through
        setFocusTraversable(false);
    }

    public void gameUpdate(GameController gc) {

        mDevPanel.gameUpdate(gc);

        mMainControlsPanel.gameUpdate(gc);
        mAbilitySelectionPanel.gameUpdate(gc);
        mMovementInformationPanel.gameUpdate(gc);
        mTimeLinePanel.gameUpdate(gc);
        mSelectedTilePanel.gameUpdate(gc);
        mStatisticsInformationPanel.gameUpdate(gc);
        mGreaterStatisticsInformationPanel.gameUpdate(gc);
        mGreaterAbilityInformationPanel.gameUpdate(gc);

        boolean shouldGoToHomeControls = gc.consumeShouldAutomaticallyGoToHomeControls();

        if (shouldGoToHomeControls) {
            mMainControlsPanel.setVisible(true);
            mMovementInformationPanel.setVisible(false);
            mAbilitySelectionPanel.setVisible(false);
            mGreaterAbilityInformationPanel.setVisible(false);
            mGreaterStatisticsInformationPanel.setVisible(false);
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
        sourceToDestination.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
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
