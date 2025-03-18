package main.game.main;


import javafx.scene.layout.StackPane;
import main.ui.game.*;

public class GameView {
    private final GameController mGameController;
    private GameCanvas mGameCanvas;
    private GameHud mGameHud;

    public GameView(GameController gameController) { mGameController = gameController; }

    public StackPane getViewPort(int width, int height) {
        mGameCanvas = new GameCanvas(mGameController, width, height);
        mGameHud = new GameHud(mGameController, width, height);
        StackPane sp = new StackPane(mGameCanvas, mGameHud);
        return sp;
    }

    public void update() {
        if (!mGameController.isRunning()) return;
        mGameHud.gameUpdate(mGameController);
        mGameCanvas.gameUpdate(mGameController);
    }
}
