package main.game.components;

import main.constants.StateLock;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;


public class MovementComponent extends Component {

    public boolean mHasMoved = false;
    public Entity mCurrentTile = null;
    public Entity mPreviousTile = null;
    public boolean mUseTrack = true;
    private final StateLock mStateLock = new StateLock();
    private final Set<Entity> mFinalRange = ConcurrentHashMap.newKeySet();
    private final Deque<Entity> mFinalPath = new ConcurrentLinkedDeque<>();
    private final Set<Entity> mStagingRange = ConcurrentHashMap.newKeySet();
    private final Deque<Entity> mStagingPath = new ConcurrentLinkedDeque<>();


    public void setMoved(boolean hasMoved) { mHasMoved = hasMoved; }
    public boolean shouldUseTrack() {
        return mUseTrack;
    }
    public void setCurrentTile(Entity tileEntity) {
        mPreviousTile = mCurrentTile;
        mCurrentTile = tileEntity;
    }

    public void stagePath(Deque<Entity> path) {
        mStagingPath.clear();
        mStagingPath.addAll(path);
    }

    public void stageRange(Set<Entity> range) {
        mStagingRange.clear();
        mStagingRange.addAll(range);
    }

    public void commit() {
        mFinalPath.clear();
        mFinalPath.addAll(mStagingPath);
        mFinalRange.clear();
        mFinalRange.addAll(mStagingRange);
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
    public Set<Entity> getTilesInFinalRange() { return mFinalRange; }
    public Deque<Entity> getTilesInFinalPath() { return mFinalPath; }
    public Set<Entity> getTileInStagingRange() { return mStagingRange; }
    public Deque<Entity> getTilesInStagingPath() { return mStagingPath; }
    public boolean isValidPath(Entity tileEntity) { return mStagingRange.contains(tileEntity); }
    public boolean isUpdatedState(String key, Object... values) { return mStateLock.isUpdated(key, values); }
}
