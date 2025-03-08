package main.game.main;


import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import main.ui.game.*;
import main.ui.game.panels.TimeLinePanel;
import org.json.JSONObject;

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
