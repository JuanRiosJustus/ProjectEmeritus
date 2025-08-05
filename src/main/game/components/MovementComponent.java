package main.game.components;

import main.constants.HashSlingingSlasher;
import main.constants.Vector3f;

import java.util.*;


public class MovementComponent extends Component {
    private final List<String> mStagedMovementRange = new ArrayList<>();
    private final List<String> mStagedMovementPath = new ArrayList<>();
    private final List<String> mFinalMovementRange = new ArrayList<>();
    private final List<String> mFinalMovementPath = new ArrayList<>();
    private String mStagedTilePosition = null;
    private String mFinalTilePosition = null;

    private final HashSlingingSlasher mHashSlingingSlasher = new HashSlingingSlasher();
    public void stageTarget(String tileID) {
        mStagedTilePosition = tileID;
    }

    public void stageMovementPath(List<String> pathIDs) {
        mStagedMovementPath.clear();
        mStagedMovementPath.addAll(pathIDs);
    }

    public void stageMovementRange(List<String> rangeIDs) {
        mStagedMovementRange.clear();
        mStagedMovementRange.addAll(rangeIDs);
    }


    public void commit() {
        mFinalMovementPath.clear();
        mFinalMovementPath.addAll(mStagedMovementPath);
        mFinalMovementRange.clear();
        mFinalMovementRange.addAll(mStagedMovementRange);
        mFinalTilePosition = mStagedTilePosition;

        mHashSlingingSlasher.setOnDifference(mFinalMovementPath.toString(), mFinalMovementRange.toString());
    }

    public String getCurrentTileID() { return mFinalTilePosition; }

    public List<String> getTilesInFinalMovementRange() { return mFinalMovementRange; }
    public List<String> getTilesInFinalMovementPath() { return mFinalMovementPath; }
    public List<String> getStagedMovementRange() { return mStagedMovementRange; }
    public List<String> getStagedMovementPath() { return mStagedMovementPath; }

    public boolean isValidMovementPath() { return mStagedMovementRange.contains(mStagedTilePosition); }

    public String getStagedTarget() { return mStagedTilePosition; }
    public String getFinalTarget() { return mFinalTilePosition; }
    public int getChecksum() { return mHashSlingingSlasher.get(); }
}
