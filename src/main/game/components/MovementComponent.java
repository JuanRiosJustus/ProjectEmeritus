package main.game.components;

import main.constants.SimpleCheckSum;
import main.constants.Vector3f;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.*;


public class MovementComponent extends Component {
    private static final String CURRENT_TILE_ENTITY = "current_tile_entity";
    private static final String PREVIOUS_TILE_ENTITY = "previous_tile_entity";
    private static final String STAGED_TARGET_TILE = "staged_target_tile";

    public boolean mHasMoved = false;
    public Entity mCurrentTile = null;
    public Entity mPreviousTile = null;
    public boolean mUseTrack = true;
    private final SimpleCheckSum mSimpleCheckSum = new SimpleCheckSum();

    private final Set<Entity> mFinalMovementRange = new LinkedHashSet<>();
    private final Set<Entity> mFinalMovementPath = new LinkedHashSet<>();
    private Entity mFinalTarget = null;
    private final Set<Entity> mStagedMovementRange = new LinkedHashSet<>();
    private final Set<Entity> mStagedMovementPath = new LinkedHashSet<>();
    private Entity mStagedTarget = null;
    private final Vector3f mPosition = new Vector3f();

    public MovementComponent() {
        put(PREVIOUS_TILE_ENTITY, "");
        put(CURRENT_TILE_ENTITY, "");
    }

    public void setMoved(boolean hasMoved) { mHasMoved = hasMoved; }
    public boolean shouldUseTrack() {
        return mUseTrack;
    }

    public void setCurrentTile(String tileID) {
        put(PREVIOUS_TILE_ENTITY, getString(CURRENT_TILE_ENTITY));
        put(CURRENT_TILE_ENTITY, tileID);
    }

    public void stageTarget(Entity tileEntity) {
        mStagedTarget = tileEntity;
    }

//    public void stageTarget(String tileID) {
//        put(STAGED_TARGET_TILE, tileID);
//    }
    public void stageMovementPath(Collection<Entity> path) {
        mStagedMovementPath.clear();
        mStagedMovementPath.addAll(path);
    }

    public void stageMovementRange(Collection<Entity> range) {
        mStagedMovementRange.clear();
        mStagedMovementRange.addAll(range);
    }


    public void commit() {
        mFinalMovementPath.clear();
        mFinalMovementPath.addAll(mStagedMovementPath);
        mFinalMovementRange.clear();
        mFinalMovementRange.addAll(mStagedMovementRange);
        mFinalTarget = mStagedTarget;
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
    public Entity getCurrentTileV1() {
        return mCurrentTile;
    }

    public String getCurrentTileID() { return getString(CURRENT_TILE_ENTITY); }
    public boolean hasMoved() { return mHasMoved; }
    public Set<Entity> getTilesInFinalRange() { return mFinalMovementRange; }
    public Set<Entity> getTilesInFinalPath() { return mFinalMovementPath; }
    public Set<Entity> getStagedTileRange() { return mStagedMovementRange; }
    public Set<Entity> getStagedTilePath() { return mStagedMovementPath; }
    public boolean isValidMovementPath() { return mStagedMovementRange.contains(mStagedTarget); }
    public boolean isUpdatedState(String key, Object... values) { return mSimpleCheckSum.update(key, values); }

    public void setPosition(int x, int y) {
        mPosition.x = x;
        mPosition.y = y;
    }

    public int getX() { return (int) mPosition.x; }
    public int getY() { return (int) mPosition.y; }

    public Entity getStagedNextTile() { return mStagedTarget; }
    public Entity getFinalNextTile() { return mFinalTarget; }
}
