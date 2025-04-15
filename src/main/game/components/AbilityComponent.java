package main.game.components;

import main.constants.HashSlingingSlasher;

import java.util.*;

public class AbilityComponent extends Component {
    private int mHash = -1;
    private String mFinalAbility = null;
    private String mFinalTarget = null;

    private final List<String> mFinalActionRange = new ArrayList<>();
    private final List<String> mFinalActionAreaOfEffect = new ArrayList<>();
    private final List<String> mFinalActionLineOfSight = new ArrayList<>();
    private final List<String> mFinalVisionRange = new ArrayList<>();

    private final List<String> mStagedActionRange = new ArrayList<>();
    private final List<String> mStagedActionAreaOfEffect = new ArrayList<>();
    private final List<String> mStagedActionLineOfSight = new ArrayList<>();
    private final List<String> mStagedVisionRange = new ArrayList<>();
    private String mStagedAbility = null;
    private String mStagedTarget = null;

    public void stageTarget(String target) {
        mStagedTarget = target;
        calculateHash();
    }
    public void stageRange(Collection<String> range) {
        mStagedActionRange.clear();
        mStagedActionRange.addAll(range);
        calculateHash();
    }
    public void stageLineOfSight(Collection<String> lineOfSight) {
        mStagedActionLineOfSight.clear();
        mStagedActionLineOfSight.addAll(lineOfSight);
        calculateHash();
    }
    public void stageAreaOfEffect(Collection<String> areaOfEffect) {
        mStagedActionAreaOfEffect.clear();
        mStagedActionAreaOfEffect.addAll(areaOfEffect);
        calculateHash();
    }

    public void stageAbility(String ability) {
        mStagedAbility = ability;
    }

    public void commit() {
        mFinalVisionRange.clear();
        mFinalVisionRange.addAll(mStagedVisionRange);
        mFinalActionRange.clear();
        mFinalActionRange.addAll(mStagedActionRange);
        mFinalActionLineOfSight.clear();
        mFinalActionLineOfSight.addAll(mStagedActionLineOfSight);
        mFinalActionAreaOfEffect.clear();
        mFinalActionAreaOfEffect.addAll(mStagedActionAreaOfEffect);
        mFinalTarget = mStagedTarget;
        mFinalAbility = mStagedAbility;

    }

    private void calculateHash() {
        mHash = mStagedTarget == null ? -1 : mStagedTarget.hashCode();
        mHash += mStagedAbility == null ? -1 : mStagedAbility.hashCode();
        mHash += mStagedActionAreaOfEffect.hashCode();
        mHash += mStagedActionLineOfSight.hashCode();
        mHash += mStagedActionRange.hashCode();
    }

    public String getAbility() { return mStagedAbility; }

    public String getFinalTileTargeted() { return mFinalTarget; }
    public List<String> getTilesInFinalRange() { return mFinalActionRange; }
    public List<String> getTilesInFinalLineOfSight() { return mFinalActionLineOfSight; }
    public List<String> getTilesInFinalAreaOfEffect() { return mFinalActionAreaOfEffect; }

    public String getStagedTileTargeted() { return mStagedTarget; }
    public List<String> getStageTiledRange() { return mStagedActionRange; }
    public List<String> getStagedTileLineOfSight() { return mStagedActionLineOfSight; }
    public List<String> getStagedTileAreaOfEffect() { return mStagedActionAreaOfEffect; }
    public boolean isValidTarget() { return mStagedActionRange.contains(mStagedTarget); }

    @Override
    public int hashCode() { return mHash; }
}
