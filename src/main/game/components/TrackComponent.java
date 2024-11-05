package main.game.components;

import main.constants.Vector3f;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.utils.RandomUtils;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TrackComponent extends Component {

    private Deque<Entity> mPathing = new LinkedList<>();
    private final List<Vector3f> mTrack = new LinkedList<>();
    private final Vector3f mLocation = new Vector3f();
    private float mProgress = 0;
    private int mIndex = 0;
    private float mSpeed = 0;

    public void clear() {
        mTrack.clear();
        mPathing = null;
        mIndex = 0;
    }
    public void set(GameModel model, Entity unit, Entity toMoveTo) {
        clear();
        Tile tileToMoveTo = toMoveTo.get(Tile.class);
        tileToMoveTo.setUnit(unit);
    }

    public float getProgress() { return mProgress; }
    public void incrementIndex() { mIndex++; }
    public int getIndex() { return mIndex; }
    public Vector3f getVectorAt(int index) { return mTrack.get(index); }

    public boolean isMoving() { return !mTrack.isEmpty(); }
    public boolean isDone() { return mIndex >= mTrack.size() - 1; }
    public boolean isEmpty() { return mTrack.isEmpty(); }
    public int getX() { return (int) mLocation.x; }
    public int getY() { return (int) mLocation.y; }
    public void setLocation(int x, int y) { mLocation.copy(x, y, 0); }

    public int getTrackMarkers() { return mTrack.size(); }
    public void setProgress(float progress) { mProgress = progress; }
    public void increaseProgress(float toAdd) { mProgress += toAdd; }
    public float getSpeed() { return mSpeed; }
    public void setSpeed(int speed) { mSpeed = speed; }
    public void addPoint(Vector3f vector3f) { mTrack.add(vector3f); }
    public void addPathing(Deque<Entity> pathing) { mPathing = pathing; }
    public String getPrint() { return mTrack.toString(); }
}