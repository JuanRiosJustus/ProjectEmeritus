package main.game.main;


import javafx.scene.CacheHint;
import javafx.scene.layout.StackPane;
import main.input.InputController;
import main.ui.game.*;

public class GameView {
    private final GameController mGameController;
    private GameCanvas mGameCanvas;
    private GameHud mGameHud;

    public GameView(GameController gameController) { mGameController = gameController; }

    public StackPane getViewPort(int width, int height) {
        mGameCanvas = new GameCanvas(mGameController, width, height);
        mGameCanvas.setCache(true);
        mGameCanvas.setCacheHint(CacheHint.SPEED);

        mGameHud = new GameHud(mGameController, width, height);
        mGameHud.setCache(true);
        mGameHud.setCacheHint(CacheHint.SPEED);

        StackPane stackPane = new StackPane(mGameCanvas, mGameHud);

        return stackPane;
    }

    public void update() {
        boolean isRunning = mGameController.isRunning();
        boolean isVisible = mGameController.getConfigurableStateGameplayHudIsVisible();
        mGameHud.setVisible(isVisible && isRunning);
        if (!isRunning) {
            mGameCanvas.setVisible(false);
        } else {
            mGameHud.gameUpdate(mGameController);
            mGameCanvas.gameUpdate(mGameController);
        }
    }
}
