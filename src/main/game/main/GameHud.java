package main.game.main;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.ui.game.*;
import main.ui.game.panels.TimeLinePanel;
import org.json.JSONObject;

public class GameHud extends GamePanel {
    private final MainControlsPanel mMainControlsPanel;
    private final AbilitySelectionPanel mAbilitySelectionPanel;
    private final MovementInformationPanel mMovementInformationPanel;
    private final TimeLinePanel mTimeLinePanel;
    private final SelectedTilePanel mSelectedTilePanel;
    private JSONObject mEphemeralMessage = null;

    public GameHud(GameController gc, int width, int height) {
        super(width, height);

        int verticalPadding = (int) (height * .01);
        int horizontalPadding = (int) (width * .01);

        Color color = Color.DIMGRAY;

        // Create main controls panel
        int mainControlsWidth = (int) (width * .225);
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
            gc.setEndTurn();
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
        Pane containerPane = JavaFxUtils.createWrapperPane(width, height);
        link(mMainControlsPanel, mMainControlsPanel.getMovementButton().getFirst().getUnderlyingButton(),
                mMovementInformationPanel, mMovementInformationPanel.getEscapeButton().getUnderlyingButton());


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





        containerPane.getChildren().addAll(
                mTimeLinePanel,
                mMainControlsPanel,
                mAbilitySelectionPanel,
                mMovementInformationPanel,
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

        boolean shouldGoToHomeControls = gc.consumeShouldAutomaticallyGoToHomeControls();

        if (shouldGoToHomeControls) {
            mMainControlsPanel.setVisible(true);
            mMovementInformationPanel.setVisible(false);
            mAbilitySelectionPanel.setVisible(false);
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
