package main.ui.game;

import javafx.scene.paint.Color;

public class MenuScene extends GameScene {

    public MenuScene(int width, int height) {
        super(width, height);
    }

    @Override
    public void update(double deltaTime) {
        // Handle menu interactions (button clicks, animations)
    }

    @Override
    public void render() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.WHITE);
        gc.fillText("Menu Scene - Press ENTER to Start", 200, 200);
    }
}