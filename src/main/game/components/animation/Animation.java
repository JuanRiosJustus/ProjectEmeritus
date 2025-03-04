package main.game.components.animation;

import main.constants.Vector3f;
import main.engine.Engine;

import java.util.ArrayList;
import java.util.List;

public class Animation {

    private final List<Runnable> mOnCompleteListeners = new ArrayList<>(); // List of listeners
    private final List<Vector3f> mAnimationTrackNodes = new ArrayList<>();
    private float mBetweenNodeProgress = 0;
    private int mAnimationTrackIndex = 0;
    private float mSpeed = 0;
    private float mDurationInSeconds = 2;
    private float mAgeInSeconds = 0;
    public Animation() { mDurationInSeconds = 2; }
    /**
     * Adds a point to the track.
     */
    public void addPoint(Vector3f vector3f) {
        mAnimationTrackNodes.add(vector3f);
    }

    public float getProgressToNextNode() {
        return mBetweenNodeProgress;
    }

    public void setProgressToNextNode(float progress) {
        mBetweenNodeProgress = progress;
    }

    public void increaseProgress(float toAdd) {
        mBetweenNodeProgress += toAdd;
    }

    public void increaseProgressAuto(float pixelsToTravel, double deltaTime) {
//        double deltaTime = Engine.getInstance().getDeltaTime();  // Get frame time
//        mAgeInSeconds += (float) deltaTime;  // NEW: Track elapsed time

        double pixelsTraveled = deltaTime * getSpeed();
        float progressIncrease = (float) (pixelsTraveled / pixelsToTravel);
        mBetweenNodeProgress += progressIncrease;
    }

    public void update() {


//        float elapsedTime = getAgeInSeconds();  // Get elapsed animation time
//        float totalDuration = getDurationInSeconds();
//        float animationProgress = elapsedTime / totalDuration;  // Normalize progress (0.0 to 1.0)

        double deltaTime = Engine.getInstance().getDeltaTime();  // Get frame time
        mAgeInSeconds += (float) deltaTime;  // NEW: Track elapsed time


    }
    public void setToNextNode() {
        mAnimationTrackIndex++;
    }

    public Vector3f getCurrentNode() { return mAnimationTrackNodes.get(mAnimationTrackIndex); }

    public Vector3f getNextNode() { return mAnimationTrackNodes.get(mAnimationTrackIndex + 1); }

    public boolean isMoving() {
        return !mAnimationTrackNodes.isEmpty();
    }

    public boolean isDone() {
//        return mAgeInSeconds > mDurationInSeconds;
        return mAnimationTrackIndex >= mAnimationTrackNodes.size() - 1;
    }

    public boolean isEmpty() {
        return mAnimationTrackNodes.isEmpty();
    }

    public float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float speed) {
        mSpeed = speed;
    }

    public float getAgeInSeconds() { return mAgeInSeconds; }
    public void setDurationInSeconds(float duration) { mDurationInSeconds = duration; }
    public float getDurationInSeconds() { return mDurationInSeconds; }

    public String getPrint() {
        return mAnimationTrackNodes.toString();
    }

    // Add an onComplete listener
    public void addOnCompleteListener(Runnable listener) {
        mOnCompleteListeners.add(listener);
    }

    // Notify all listeners
    public void notifyListeners() {
        for (Runnable listener : mOnCompleteListeners) {
            listener.run();
        }
        mOnCompleteListeners.clear(); // Clear listeners to avoid duplicate notifications
    }
}
