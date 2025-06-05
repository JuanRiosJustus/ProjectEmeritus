package main.game.main;


import javafx.scene.CacheHint;
import javafx.scene.layout.StackPane;
import main.input.InputController;
import main.ui.game.*;

public class GameView {
    private GameCanvas mGameCanvas;
    private GameHud mGameHud;
    private GameModel mGameModel;

    public GameView(GameModel gameModel) { mGameModel = gameModel; }

    public StackPane getViewPort(int width, int height) {
        mGameCanvas = new GameCanvas(width, height);
        mGameCanvas.setCache(true);
        mGameCanvas.setCacheHint(CacheHint.SPEED);

        mGameHud = new GameHud(mGameModel, width, height);
        mGameHud.setCache(true);
        mGameHud.setCacheHint(CacheHint.SPEED);

        StackPane stackPane = new StackPane(mGameCanvas, mGameHud);

        return stackPane;
    }

    public void update() {
        boolean isRunning = mGameModel.isRunning();
        boolean isVisible = mGameModel.getConfigurableStateGameplayHudIsVisible();
        if (mGameHud != null) {
            mGameHud.setVisible(isVisible && isRunning);
        }
        if (!isRunning) {
            mGameCanvas.setVisible(false);
        } else {
            if (mGameHud != null) {
                mGameHud.gameUpdate(mGameModel);
            }
            if (mGameCanvas != null) {
                mGameCanvas.gameUpdate(mGameModel, "0");
            }
        }
    }


}
