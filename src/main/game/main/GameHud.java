package main.game.main;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.ui.*;
import main.ui.game.*;
import main.ui.TimeLinePanel;
import org.json.JSONObject;

public class GameHud extends GamePanel {
    private final MainControlsPanel mMainControlsPanel;
    private final AbilitySelectionPanel mAbilitySelectionPanel;
    private final MovementInformationPanel mMovementInformationPanel;
    private final SettingsPanel mSettingsPanel;
    private final LesserStatisticsInformationPanel mLesserStatisticsInformationPanel;
    private final GreaterStatisticsInformationPanel mGreaterStatisticsInformationPanel;
    private final TimeLinePanel mTimeLinePanel;
    private final SelectedTilePanel mSelectedTilePanel;
    private JSONObject mEphemeralMessage = null;

    public GameHud(GameController gc, int width, int height) {
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

        mMainControlsPanel.getEndTurnButton().getFirst().getUnderlyingButton().setOnMouseReleased(e -> gc.setEndTurn());

        mAbilitySelectionPanel = new AbilitySelectionPanel(
                mainControlsX,
                mainControlsY,
                mainControlsWidth,
                mainControlsHeight,
                color,
                4
        );
        mAbilitySelectionPanel.setVisible(false);
        link(mMainControlsPanel, mMainControlsPanel.getAbilitiesButton().getFirst().getUnderlyingButton(),
                mAbilitySelectionPanel, mAbilitySelectionPanel.getEscapeButton().getUnderlyingButton());


        mMovementInformationPanel = new MovementInformationPanel(
                mainControlsX,
                mainControlsY,
                mainControlsWidth,
                mainControlsHeight,
                color,
                4
        );
        mMovementInformationPanel.setVisible(false);
        link(mMainControlsPanel, mMainControlsPanel.getMovementButton().getFirst().getUnderlyingButton(),
                mMovementInformationPanel, mMovementInformationPanel.getEscapeButton().getUnderlyingButton());


        mSettingsPanel = new SettingsPanel(
                mainControlsX,
                mainControlsY,
                mainControlsWidth,
                mainControlsHeight,
                color,
                4
        );
        mSettingsPanel.setVisible(false);
        link(mMainControlsPanel, mMainControlsPanel.getSettingsButton().getFirst().getUnderlyingButton(),
                mSettingsPanel, mSettingsPanel.getEscapeButton().getUnderlyingButton());





        mLesserStatisticsInformationPanel = new LesserStatisticsInformationPanel(
                mainControlsX,
                mainControlsY,
                mainControlsWidth,
                mainControlsHeight,
                color,
                4
        );
        mLesserStatisticsInformationPanel.setVisible(false);
        link(mMainControlsPanel, mMainControlsPanel.getStatisticsButton().getFirst().getUnderlyingButton(),
                mLesserStatisticsInformationPanel, mLesserStatisticsInformationPanel.getEscapeButton().getUnderlyingButton());


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
                gc
        );





        Pane containerPane = JavaFxUtils.createWrapperPane(width, height);
        containerPane.getChildren().addAll(
                mTimeLinePanel,
                mMainControlsPanel,
                mAbilitySelectionPanel,
                mMovementInformationPanel,
                mSettingsPanel,
                mLesserStatisticsInformationPanel,
                mGreaterStatisticsInformationPanel,
                mSelectedTilePanel
        );

        getChildren().add(containerPane);
        setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        setPickOnBounds(false); // Allow clicks to pass through
        setFocusTraversable(false);
    }

    public void gameUpdate(GameController gc) {

        mMainControlsPanel.gameUpdate(gc);
        mAbilitySelectionPanel.gameUpdate(gc);
        mMovementInformationPanel.gameUpdate(gc);
        mTimeLinePanel.gameUpdate(gc);
        mSelectedTilePanel.gameUpdate(gc);
        mLesserStatisticsInformationPanel.gameUpdate(gc);
        mGreaterStatisticsInformationPanel.gameUpdate(gc);

        boolean shouldGoToHomeControls = gc.consumeShouldAutomaticallyGoToHomeControls();

        if (shouldGoToHomeControls) {
            mMainControlsPanel.setVisible(true);
            mMovementInformationPanel.setVisible(false);
            mAbilitySelectionPanel.setVisible(false);
        }

        boolean shouldOpenGreaterStatisticsPanel = mSelectedTilePanel.consumeShouldOpenGreaterStatisticsPanel();
        if (shouldOpenGreaterStatisticsPanel) {
            mGreaterStatisticsInformationPanel.setVisible(!mGreaterStatisticsInformationPanel.isVisible());
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
}
