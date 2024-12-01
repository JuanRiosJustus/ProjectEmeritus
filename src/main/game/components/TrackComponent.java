package main.game.components;

import main.constants.Vector3f;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackComponent extends Component {
    private final List<Vector3f> mTrack = new ArrayList<>();
    private final Vector3f mPosition = new Vector3f();
    private float mProgress = 0;
    private int mIndex = 0;
    private float mSpeed = 0;
    private long lastUpdateTime;

    public TrackComponent() {
        lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * Clears the current track and resets the index and progress.
     */
    public void clear() {
        mTrack.clear();
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
        mTrack.add(vector3f);
    }

    /**
     * Sets a full path (e.g., from pathfinding) as the track.
     */
    public void setPath(List<Vector3f> path) {
        clear();
        mTrack.addAll(path);
    }

    /**
     * Reverses the current track and resets the index and progress.
     */
    public void reverse() {
        Collections.reverse(mTrack);
        mIndex = 0;
        mProgress = 0;
    }

    /**
     * Updates the position based on progress and speed.
     */
    public void updatePosition() {
        if (isDone()) return;

        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTime) / 1000.0f; // Convert to seconds
        mProgress += deltaTime * mSpeed;
        lastUpdateTime = currentTime;

        while (mProgress >= 1 && mIndex < mTrack.size() - 1) {
            mProgress -= 1; // Move to the next segment
            mIndex++;
        }

        if (!isDone()) {
            Vector3f current = mTrack.get(mIndex);
            Vector3f next = mTrack.get(mIndex + 1);

            // Interpolate position between current and next points
            mPosition.x = current.x + (next.x - current.x) * mProgress;
            mPosition.y = current.y + (next.y - current.y) * mProgress;
            mPosition.z = current.z + (next.z - current.z) * mProgress;
        }
    }

    /**
     * Draws the track for debugging purposes.
     */
    public void drawTrack(java.awt.Graphics g) {
        g.setColor(java.awt.Color.RED);
        for (int i = 0; i < mTrack.size() - 1; i++) {
            Vector3f start = mTrack.get(i);
            Vector3f end = mTrack.get(i + 1);
            g.drawLine((int) start.x, (int) start.y, (int) end.x, (int) end.y);
        }
    }

    // Accessors and Mutators

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        mProgress = progress;
    }

    public void increaseProgress(float toAdd) {
        mProgress += toAdd;
    }

    public int getIndex() {
        return mIndex;
    }

    public void incrementIndex() {
        mIndex++;
    }

    public Vector3f getVectorAt(int index) {
        if (index < 0 || index >= mTrack.size()) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for track size " + mTrack.size());
        }
        return mTrack.get(index);
    }

    public boolean isMoving() {
        return !mTrack.isEmpty();
    }

    public boolean isDone() {
        return mIndex >= mTrack.size() - 1;
    }

    public boolean isEmpty() {
        return mTrack.isEmpty();
    }

    public int getX() {
        return (int) mPosition.x;
    }

    public int getY() {
        return (int) mPosition.y;
    }

    public void setPosition(int x, int y) {
        mPosition.copy(x, y, 0);
    }

    public int getTrackMarkers() {
        return mTrack.size();
    }

    public float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float speed) {
        mSpeed = speed;
    }

    public String getPrint() {
        return mTrack.toString();
    }
}