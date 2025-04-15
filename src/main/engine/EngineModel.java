package main.engine;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;

public class EngineModel {
    private final AnimationTimer mDeltaTimer;
    private double mDeltaTime = 0;
    private double mLateUpdatedTime = 0;
    private EngineRunnable mCurrentRunnable = null;
    public EngineModel() {
        mDeltaTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                long currentTime = System.nanoTime();
                mDeltaTime = (currentTime - mLateUpdatedTime) / 1.0E9D;
                mLateUpdatedTime = currentTime;
            }
        };
        mDeltaTimer.start();
    }
    public void stage(String key, EngineRunnable engineRunnable) {
        if (mCurrentRunnable != null) {
            mCurrentRunnable.stop();
        }

        engineRunnable.start();
    }
    public double getDeltaTime() { return mDeltaTime; }
}
