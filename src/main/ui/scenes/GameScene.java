package main.ui.scenes;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import main.engine.EngineRunnable;
import main.game.main.GameController;

public class GameScene extends EngineRunnable {

    private GameController mGameController = null;
    public GameScene(int width, int height) {
        super(width, height);
    }

    public GameScene(int width, int height, GameController gameController) {
        this(width, height);
        mGameController = gameController;
    }

    @Override
    public Scene render() {
        StackPane sp = new StackPane();

        if (mGameController == null) {
            mGameController = GameController.create(10, 10, mWidth, mHeight);
        }
        mGameController.run();

        sp.getChildren().add(mGameController.getGamePanel());
        return new Scene(sp, mWidth, mHeight);
    }
}
