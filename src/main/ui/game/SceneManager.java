package main.ui.game;

import javafx.stage.Stage;

public class SceneManager {
    private Stage mStage;
    private GameScene mCurrentScene;

    public SceneManager(Stage stage) {
        mStage = stage;
    }

    public void setScene(GameScene scene) {
        mCurrentScene = scene;
        mStage.setScene(scene.getScene());
        scene.render(); // Ensure it's rendered on switch
    }

    public void update(double deltaTime) {
        if (mCurrentScene != null) {
            mCurrentScene.update(deltaTime);
        }
    }

    public void render() {
        if (mCurrentScene != null) {
            mCurrentScene.render();
        }
    }
}