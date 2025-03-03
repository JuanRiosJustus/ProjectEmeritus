package main.ui.game;

import javafx.scene.paint.Color;

public class GamePlayScene extends GameScene {

    public GamePlayScene(int width, int height) {
        super(width, height);
    }

    @Override
    public void update(double deltaTime) {
        // Handle game logic (movement, physics, AI)
    }

    @Override
    public void render() {
        gc.setFill(Color.BLUE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.WHITE);
        gc.fillText("Game Scene - Playing", 200, 200);
    }
}