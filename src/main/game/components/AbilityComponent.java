package main.game.components;

import main.constants.StateLock;
import main.game.entity.Entity;

import java.util.*;

public class AbilityComponent extends Component {
    public Entity targeting = null;
    private boolean mActed = false;
    private final StateLock mStateLock = new StateLock();

    private final Set<Entity> mFinalActionRange = new LinkedHashSet<>();
    private final Set<Entity> mFinalActionAreaOfEffect = new LinkedHashSet<>();
    private final Set<Entity> mFinalActionLineOfSight = new LinkedHashSet<>();
    private final Set<Entity> mFinalVisionRange = new LinkedHashSet<>();
    private String mFinalAction = null;
    private Entity mFinalTarget = null;


    private final Set<Entity> mStagedActionRange = new LinkedHashSet<>();
    private final Set<Entity> mStagedActionAreaOfEffect = new LinkedHashSet<>();
    private final Set<Entity> mStagedActionLineOfSight = new LinkedHashSet<>();
    private final Set<Entity> mStagedVisionRange = new LinkedHashSet<>();
    private String mStagedAction = null;
    private Entity mStagedTarget = null;

    public void stageTarget(Entity target) {
        mStagedTarget = target;
    }
    public void stageRange(Collection<Entity> range) {
        mStagedActionRange.clear();
        mStagedActionRange.addAll(range);
    }
    public void stageLineOfSight(Collection<Entity> lineOfSight) {
        mStagedActionLineOfSight.clear();
        mStagedActionLineOfSight.addAll(lineOfSight);
    }
    public void stageAreaOfEffect(Collection<Entity> areaOfEffect) {
        mStagedActionAreaOfEffect.clear();
        mStagedActionAreaOfEffect.addAll(areaOfEffect);
    }

    public void stageAction(String action) {
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
    public boolean isUpdated(String key, Object... values) { return mStateLock.isUpdated(key, values); }


    public Entity getFinalTileTargeted() { return mFinalTarget; }
    public Set<Entity> getTilesInFinalRange() { return mFinalActionRange; }
    public Set<Entity> getTilesInFinalLineOfSight() { return mFinalActionLineOfSight; }
    public Set<Entity> getTilesInFinalAreaOfEffect() { return mFinalActionAreaOfEffect; }

    public Entity getStagedTileTargeted() { return mStagedTarget; }
    public Set<Entity> getStageTiledRange() { return mStagedActionRange; }
    public Set<Entity> getStagedTileLineOfSight() { return mStagedActionLineOfSight; }
    public Set<Entity> getStagedTileAreaOfEffect() { return mStagedActionAreaOfEffect; }
    public boolean isValidTarget() { return mStagedActionRange.contains(mStagedTarget); }
}
