package main.game.main;


import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import main.ui.game.*;
import main.ui.game.panels.TimeLinePanel;

public class GameView {
    private final GameModel mGameModel;
    private GameCanvas gameCanvas;
    private MainControlsPanel mMainControlsPanel = null;
    private AbilitySelectionPanel mAbilitySelectionPanel = null;
    private MovementInformationPanel mMovementInformationPanel = null;
    private TimeLinePanel mTimeLinePanel = null;

    public GameView(GameModel gameModel) {
        mGameModel = gameModel;
    }

    public StackPane getViewPort(int width, int height) {
        gameCanvas = new GameCanvas(mGameModel, width, height);

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
            mGameModel.getGameState().setShouldEndTheTurn(true);
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


        int turnOrderPanelWidth = mainControlsWidth * 3;
        int turnOrderPaneHeight = mainControlsHeight / 3;
        int turnOrderPanelX = horizontalPadding;
        int turnOrderPanelY = verticalPadding;
        mTimeLinePanel = new TimeLinePanel(
                turnOrderPanelX,
                turnOrderPanelY,
                turnOrderPanelWidth,
                turnOrderPaneHeight,
                color,
                15
        );

        containerPane.getChildren().addAll(
                mTimeLinePanel,
                mMainControlsPanel,
                mAbilitySelectionPanel,
                mMovementInformationPanel
        );
//        containerPane.getChildren().add(mMovementInformationPanel);


//        mAbilitySelectionPanel.setLayoutX(mainControlsX);
//        mAbilitySelectionPanel.setLayoutY(mainControlsY);
//
//        mMainControlsPanel.setLayoutX(mainControlsX);
//        mMainControlsPanel.setLayoutY(mainControlsY);










        StackPane sp = new StackPane(gameCanvas, containerPane);
        return sp;
    }

    public void update(GameController gc) {
        if (!gc.isRunning()) return;

        mMainControlsPanel.gameUpdate(gc);
        mAbilitySelectionPanel.gameUpdate(gc);
        mMovementInformationPanel.gameUpdate(gc);
        mTimeLinePanel.gameUpdate(gc);


        gameCanvas.update();
    }

//    public void update(GameController gc) {
//        if (!gc.isRunning()) return;
//
//        mMainControlsPanel.gameUpdate(gc);
//        mAbilitySelectionPanel.gameUpdate(gc);
//        mMovementInformationPanel.gameUpdate(gc);
//
//
//        gameCanvas.update();
//    }

    private void link(Region source, Button sourceToDestination, Region destination, Button destinationToSource) {
        sourceToDestination.setOnMouseReleased(e -> {
            source.setVisible(false);
            destination.setVisible(true);
        });

        destinationToSource.setOnMouseReleased(e -> {
            destination.setVisible(false);
            source.setVisible(true);
        });
    }
}
