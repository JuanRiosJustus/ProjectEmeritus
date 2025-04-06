package main.game.components;

import main.game.entity.Entity;

import java.util.*;

public class AbilityComponent extends Component {
    public Entity targeting = null;
    private boolean mActed = false;
    private boolean mHasStartedUsingAbility;
    private boolean mHasFinishedUsingAbility;
    private String mFinalAction = null;
    private String mFinalTarget = null;

    private final List<String> mFinalActionRange = new ArrayList<>();
    private final List<String> mFinalActionAreaOfEffect = new ArrayList<>();
    private final List<String> mFinalActionLineOfSight = new ArrayList<>();
    private final List<String> mFinalVisionRange = new ArrayList<>();

    private final List<String> mStagedActionRange = new ArrayList<>();
    private final List<String> mStagedActionAreaOfEffect = new ArrayList<>();
    private final List<String> mStagedActionLineOfSight = new ArrayList<>();
    private final List<String> mStagedVisionRange = new ArrayList<>();
    private String mStagedAction = null;
    private String mStagedTarget = null;


//    private final Set<Entity> mStagedActionRange = new LinkedHashSet<>();
//    private final Set<Entity> mStagedActionAreaOfEffect = new LinkedHashSet<>();
//    private final Set<Entity> mStagedActionLineOfSight = new LinkedHashSet<>();
//    private final Set<Entity> mStagedVisionRange = new LinkedHashSet<>();
//    private String mStagedAction = null;
//    private Entity mStagedTarget = null;

//    public void stageTarget(Entity target) {
//        mStagedTarget = target;
//    }
//    public void stageRange(Collection<Entity> range) {
//        mStagedActionRange.clear();
//        mStagedActionRange.addAll(range);
//    }
//    public void stageLineOfSight(Collection<Entity> lineOfSight) {
//        mStagedActionLineOfSight.clear();
//        mStagedActionLineOfSight.addAll(lineOfSight);
//    }
//    public void stageAreaOfEffect(Collection<Entity> areaOfEffect) {
//        mStagedActionAreaOfEffect.clear();
//        mStagedActionAreaOfEffect.addAll(areaOfEffect);
//    }


    public void stageTarget(String target) {
        mStagedTarget = target;
    }
    public void stageRange(Collection<String> range) {
        mStagedActionRange.clear();
        mStagedActionRange.addAll(range);
    }
    public void stageLineOfSight(Collection<String> lineOfSight) {
        mStagedActionLineOfSight.clear();
        mStagedActionLineOfSight.addAll(lineOfSight);
    }
    public void stageAreaOfEffect(Collection<String> areaOfEffect) {
        mStagedActionAreaOfEffect.clear();
        mStagedActionAreaOfEffect.addAll(areaOfEffect);
    }

    public void stageAbility(String action) {
        mStagedAction = action;
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
        mFinalAction = mStagedAction;
    }

    public void reset() {
        mActed = false;
        previouslyTargeting = null;
    }
    private Entity previouslyTargeting = null;

    public String getAbility() { return mStagedAction; }

    public boolean hasActed() { return mActed; }
    public void setActed(boolean hasActed) { mActed = hasActed; }
    public boolean hasStartedUsingAbility() { return mHasStartedUsingAbility; }
    public void setHasStartedUsingAbility(boolean b) { mHasFinishedUsingAbility = b; }
    public boolean hasFinishedUsingAbility() { return mHasFinishedUsingAbility || mActed; }
    public void setFinishedUsingAbility(boolean b) { mHasFinishedUsingAbility = b; mActed = b; }


    public String getFinalTileTargeted() { return mFinalTarget; }
    public List<String> getTilesInFinalRange() { return mFinalActionRange; }
    public List<String> getTilesInFinalLineOfSight() { return mFinalActionLineOfSight; }
    public List<String> getTilesInFinalAreaOfEffect() { return mFinalActionAreaOfEffect; }

    public String getStagedTileTargeted() { return mStagedTarget; }
    public List<String> getStageTiledRange() { return mStagedActionRange; }
    public List<String> getStagedTileLineOfSight() { return mStagedActionLineOfSight; }
    public List<String> getStagedTileAreaOfEffect() { return mStagedActionAreaOfEffect; }
    public boolean isValidTarget() { return mStagedActionRange.contains(mStagedTarget); }
}
