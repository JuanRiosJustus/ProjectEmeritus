package main.game.components.animation;

import main.constants.Vector3f;
import main.engine.Engine;

import java.util.ArrayList;
import java.util.List;

public class Animation {
    private final List<Vector3f> mAnimationTrackNodes = new ArrayList<>();
    private float mBetweenNodeProgress = 0;
    private int mAnimationTrackIndex = 0;
    private float mSpeed = 0;

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

    public void increaseProgressAuto(float pixelsToTravel) {
        double pixelsTraveled = Engine.getInstance().getDeltaTime() * getSpeed();
        float progressIncrease = (float) (pixelsTraveled / pixelsToTravel);
        mBetweenNodeProgress += progressIncrease;
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

    public String getPrint() {
        return mAnimationTrackNodes.toString();
    }
}
