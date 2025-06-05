package main.ui.game;

import javafx.scene.paint.Color;

public class DeploymentScene extends GameScene {

    public DeploymentScene(int width, int height) {
        super(width, height);
    }

    @Override
    public void update(double deltaTime) {
        // Handle unit placement, dragging
    }

    @Override
    public void render() {
        gc.setFill(Color.GREEN);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.WHITE);
        gc.fillText("Deployment Scene - Place Units", 200, 200);
    }
}