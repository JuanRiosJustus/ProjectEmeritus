package main.engine;



import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public abstract class EngineRunnable {
    protected AnimationTimer mUpdateAnimationTimer = null;
    protected int mWidth;
    protected int mHeight;
    public EngineRunnable() { }
    public EngineRunnable(int width, int height) {
        mWidth = width;
        mHeight = height;
    }
    public abstract Scene render();
    public void start() {
        mUpdateAnimationTimer = new AnimationTimer() { @Override public void handle(long l) {} };
        mUpdateAnimationTimer.start();
    }
    public void stop() {
        mUpdateAnimationTimer.stop();
    }

    public Pane createWrapperPane(int width, int height) {
        Pane pane = new Pane();
        pane.setPrefSize(width, height);
        pane.setMinSize(width, height);
        pane.setMaxSize(width, height);
        pane.setPickOnBounds(false);
        return pane;
    }
}
