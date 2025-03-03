package main.ui.game;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

public abstract class GameScene {
    protected Scene scene;
    protected Canvas canvas;
    protected GraphicsContext gc;

    public GameScene(int width, int height) {
        this.canvas = new Canvas(width, height);
        this.gc = canvas.getGraphicsContext2D();
        Pane root = new Pane(canvas);
        this.scene = new Scene(root, width, height);
    }

    public Scene getScene() {
        return scene;
    }

    public abstract void update(double deltaTime);

    public abstract void render();
}