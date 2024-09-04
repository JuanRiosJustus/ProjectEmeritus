package main.game.components;

import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;


public class MovementComponent extends Component {

    public boolean moved = false;
    public Entity currentTile = null;
    public Entity previousTile = null;
    public boolean useTrack = true;
    private final Set<Entity> mFinalRange = ConcurrentHashMap.newKeySet();
    private final Deque<Entity> mFinalPath = new ConcurrentLinkedDeque<>();
    private final Set<Entity> mPreviewRange = ConcurrentHashMap.newKeySet();
    private final Deque<Entity> mPreviewPath = new ConcurrentLinkedDeque<>();
    private int mCurrentStateHash = 0;


    public void setMoved(boolean hasMoved) { moved = hasMoved; }
    public boolean shouldUseTrack() {
        return useTrack;
    }
    public void setCurrentTile(Entity tile) {
        currentTile = tile;
    }
    public void setPreviousTile(Entity tile) {
        previousTile = tile;
    }

    public void setPath(List<Entity> path) {
        mPreviewPath.clear();
        mPreviewPath.addAll(path);
    }

    public void setRange(Set<Entity> range) {
        mPreviewRange.clear();
        mPreviewRange.addAll(range);
    }

    public void commit() {
        mFinalPath.clear();
        mFinalPath.addAll(mPreviewPath);
        mFinalRange.clear();
        mFinalRange.addAll(mFinalPath);
    }

    public void reset() {
        mPreviewPath.clear();
        mPreviewRange.clear();
        previouslyTargeting = null;
        moved = false;
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
        return currentTile;
    }
    public boolean hasMoved() { return moved; }

    public Set<Entity> getFinalTilesInRange() { return mFinalRange; }
    public Deque<Entity> getFinalTilesInPath() { return mFinalPath; }
    public Set<Entity> getPreviewTilesInRange() { return mPreviewRange; }
    public Deque<Entity> getPreviewTilesInPath() { return mPreviewPath; }
    public int getCurrentStateHash() { return mCurrentStateHash; }
    public void setCurrentStateHash(int stateHash) { mCurrentStateHash = stateHash; }
}
