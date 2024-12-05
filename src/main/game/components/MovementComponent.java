package main.game.components;

import main.constants.StateLock;
import main.constants.Vector3f;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.*;


public class MovementComponent extends Component {

    public boolean mHasMoved = false;
    public Entity mCurrentTile = null;
    public Entity mPreviousTile = null;
    public boolean mUseTrack = true;
    private final StateLock mStateLock = new StateLock();

    private final Map<Entity, Entity> mFinalMovementRange = new LinkedHashMap<>();
    private final Map<Entity, Entity> mFinalMovementPath = new LinkedHashMap<>();
    private Entity mFinalNextTile = null;
    private final Map<Entity, Entity> mStagedMovementRange = new LinkedHashMap<>();
    private final Map<Entity, Entity> mStagedMovementPath = new LinkedHashMap<>();
    private Entity mStagedNextTile = null;
    private final Vector3f mPosition = new Vector3f();


    public void setMoved(boolean hasMoved) { mHasMoved = hasMoved; }
    public boolean shouldUseTrack() {
        return mUseTrack;
    }
    public void setCurrentTile(Entity tileEntity) {
        mPreviousTile = mCurrentTile;
        mCurrentTile = tileEntity;
    }

    public void stageMovementPath(Collection<Entity> path) {
        mStagedMovementPath.clear();
        path.forEach(e -> mStagedMovementPath.put(e, e));
    }

    public void stageMovementRange(Collection<Entity> range) {
        mStagedMovementRange.clear();
        range.forEach(e -> mStagedMovementRange.put(e, e));
    }

    public void stageTarget(Entity tileEntity) {
        mStagedNextTile = tileEntity;
    }


    public void commit() {
        mFinalMovementPath.clear();
        mFinalMovementPath.putAll(mStagedMovementPath);
        mFinalMovementRange.clear();
        mFinalMovementRange.putAll(mStagedMovementRange);
        mFinalNextTile = mStagedNextTile;
    }

    public void reset() {
        previouslyTargeting = null;
        mHasMoved = false;
    }

    private Entity previouslyTargeting = null;
    public boolean shouldNotUpdate(GameModel model, Entity targeting) {
        boolean isSameTarget = previouslyTargeting == targeting;
        if (!isSameTarget) {
//            System.out.println("Waiting for user movement input... " + previouslyTargeting + " vs " + targeting);
        }
        previouslyTargeting = targeting;
        return isSameTarget && mOwner.get(UserBehavior.class) != null;
    }
    public Entity getCurrentTile() {
        return mCurrentTile;
    }
    public boolean hasMoved() { return mHasMoved; }
    public Set<Entity> getTilesInFinalRange() { return mFinalMovementRange.keySet(); }
    public Set<Entity> getTilesInFinalPath() { return mFinalMovementPath.keySet(); }
    public Set<Entity> getTileInStagedRange() { return mStagedMovementRange.keySet(); }
    public Set<Entity> getTilesInStagedPath() { return mStagedMovementPath.keySet(); }
    public boolean isValidMovementPath() { return mStagedMovementRange.containsKey(mStagedNextTile); }
    public boolean isUpdatedState(String key, Object... values) { return mStateLock.isUpdated(key, values); }

    public void setPosition(int x, int y) {
        mPosition.x = x;
        mPosition.y = y;
    }

    public Vector3f getPosition() { return mPosition; }
    public int getX() { return (int) mPosition.x; }
    public int getY() { return (int) mPosition.y; }

    public Entity getStagedNextTile() { return mStagedNextTile; }
    public Entity getFinalNextTile() { return mFinalNextTile; }
}
