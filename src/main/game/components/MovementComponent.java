package main.game.components;

import main.constants.Checksum;
import main.constants.Vector3f;
import main.game.entity.Entity;

import java.util.*;


public class MovementComponent extends Component {
    private static final String CURRENT_TILE_ENTITY = "current_tile_entity";
    private static final String PREVIOUS_TILE_ENTITY = "previous_tile_entity";
    private static final String STAGED_TARGET_TILE = "staged_target_tile";
    public boolean mHasMoved = false;
    public Entity mCurrentTile = null;
    public Entity mPreviousTile = null;
    public boolean mUseTrack = true;
    private final List<String> mStagedMovementRange = new ArrayList<>();
    private final List<String> mStagedMovementPath = new ArrayList<>();
    private final List<String> mFinalMovementRange = new ArrayList<>();
    private final List<String> mFinalMovementPath = new ArrayList<>();
    private String mStagedTarget = null;
    private String mFinalTarget = null;

    private final Checksum mChecksum = new Checksum();
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

    public void stageTarget(String tileID) {
        mStagedTarget = tileID;
    }

    public void stageMovementPath(Collection<String> path) {
        mStagedMovementPath.clear();
        mStagedMovementPath.addAll(path);
    }

    public void stageMovementRange(Collection<String> range) {
        mStagedMovementRange.clear();
        mStagedMovementRange.addAll(range);
    }


    public void commit() {
        mFinalMovementPath.clear();
        mFinalMovementPath.addAll(mStagedMovementPath);
        mFinalMovementRange.clear();
        mFinalMovementRange.addAll(mStagedMovementRange);
        mFinalTarget = mStagedTarget;
        mChecksum.getThenSet(mFinalMovementPath.toString(), mFinalMovementRange.toString());
    }

    public void reset() {
        previouslyTargeting = null;
        mHasMoved = false;
    }

    private Entity previouslyTargeting = null;
//    public boolean shouldNotUpdate(GameModel model, Entity targeting) {
//        boolean isSameTarget = previouslyTargeting == targeting;
//        if (!isSameTarget) {
////            System.out.println("Waiting for user movement input... " + previouslyTargeting + " vs " + targeting);
//        }
//        previouslyTargeting = targeting;
//        return isSameTarget && mOwner.get(UserBehavior.class) != null;
//    }
    public Entity getCurrentTileV1() {
        return mCurrentTile;
    }

    public String getCurrentTileID() { return getString(CURRENT_TILE_ENTITY); }
    public boolean hasMoved() { return mHasMoved; }
//    public Set<Entity> getStagedTileRange() { return mStagedMovementRange; }
//    public Set<Entity> getStagedTilePath() { return mStagedMovementPath; }

    public List<String> getTilesInFinalMovementRange() { return mFinalMovementRange; }
    public List<String> getTilesInFinalMovementPath() { return mFinalMovementPath; }
    public List<String> getStagedMovementRange() { return mStagedMovementRange; }
    public List<String> getStagedMovementPath() { return mStagedMovementPath; }

    public boolean isValidMovementPath() { return mStagedMovementRange.contains(mStagedTarget); }
    public void setPosition(int x, int y) { mPosition.x = x; mPosition.y = y; }
    public int getX() { return (int) mPosition.x; }
    public int getY() { return (int) mPosition.y; }

    public String getStagedNextTile() { return mStagedTarget; }
    public String getFinalNextTile() { return mFinalTarget; }
    public int getChecksum() { return mChecksum.get(); }
}
