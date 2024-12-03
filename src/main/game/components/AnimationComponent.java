package main.game.components;

import main.constants.Vector3f;
import main.engine.Engine;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnimationComponent extends Component {
    private final List<Vector3f> mAnimationTrack = new ArrayList<>();
    private float mProgress = 0;
    private int mIndex = 0;
    private float mSpeed = 0;

    /**
     * Clears the current track and resets the index and progress.
     */
    public void clear() {
        mAnimationTrack.clear();
        mIndex = 0;
        mProgress = 0;
    }

    /**
     * Associates a unit with a destination tile and clears the current track.
     */
    public void set(GameModel model, Entity unit, Entity toMoveTo) {
        clear();
        Tile tileToMoveTo = toMoveTo.get(Tile.class);
        tileToMoveTo.setUnit(unit);
    }

    /**
     * Adds a point to the track.
     */
    public void addPoint(Vector3f vector3f) {
        mAnimationTrack.add(vector3f);
    }

    /**
     * Sets a full path (e.g., from pathfinding) as the track.
     */
    public void setPath(List<Vector3f> path) {
        clear();
        mAnimationTrack.addAll(path);
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        mProgress = progress;
    }

    public void increaseProgress(float toAdd) {
        mProgress += toAdd;
    }

    public void increaseProgressAuto(float pixelsToTravel) {
        double pixelsTraveled = Engine.getInstance().getDeltaTime() * getSpeed();
        float progressIncrease = (float) (pixelsTraveled / pixelsToTravel);
        mProgress += progressIncrease;
    }

    public void incrementIndex() {
        mIndex++;
    }

    public Vector3f getCurrentNode() { return mAnimationTrack.get(mIndex); }

    public Vector3f getNextNode() { return mAnimationTrack.get(mIndex + 1); }

    public boolean isMoving() {
        return !mAnimationTrack.isEmpty();
    }

    public boolean isDone() {
        return mIndex >= mAnimationTrack.size() - 1;
    }

    public boolean isEmpty() {
        return mAnimationTrack.isEmpty();
    }

    public float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float speed) {
        mSpeed = speed;
    }

    public String getPrint() {
        return mAnimationTrack.toString();
    }
}