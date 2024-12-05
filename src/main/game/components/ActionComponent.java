package main.game.components;

import main.constants.StateLock;
import main.game.entity.Entity;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ActionComponent extends Component {
    public Entity targeting = null;
    private boolean mActed = false;
    private final StateLock mStateLock = new StateLock();

    private final Map<Entity, Entity> mFinalActionRange = new LinkedHashMap<>();
    private final Map<Entity, Entity> mFinalActionAreaOfEffect = new LinkedHashMap<>();
    private final Map<Entity, Entity> mFinalActionLineOfSight = new LinkedHashMap<>();
    private final Map<Entity, Entity> mFinalVisionRange = new LinkedHashMap<>();
    private String mFinalAction = null;
    private Entity mFinalTarget = null;


    private final Map<Entity, Entity> mStagedActionRange = new LinkedHashMap<>();
    private final Map<Entity, Entity> mStagedActionAreaOfEffect = new LinkedHashMap<>();
    private final Map<Entity, Entity> mStagedActionLineOfSight = new LinkedHashMap<>();
    private final Map<Entity, Entity> mStagedVisionRange = new LinkedHashMap<>();
    private String mStagedAction = null;
    private Entity mStagedTarget = null;

    public void stageRange(Collection<Entity> range) {
        mStagedActionRange.clear();
        range.forEach(e -> mStagedActionRange.put(e, e));
    }

    public void stageLineOfSight(Collection<Entity> lineOfSight) {
        mStagedActionLineOfSight.clear();
        lineOfSight.forEach(e -> mStagedActionLineOfSight.put(e, e));
    }

    public void stageTarget(Entity target) {
        mStagedTarget = target;
    }

    public void stageAreaOfEffect(Collection<Entity> areaOfEffect) {
        mStagedActionAreaOfEffect.clear();
        areaOfEffect.forEach(e -> mStagedActionAreaOfEffect.put(e, e));
    }

    public void stageAction(String action) {
        mStagedAction = action;
    }

    public void stageVision(Collection<Entity> vision) {
        mStagedVisionRange.clear();
        vision.forEach(e -> mStagedVisionRange.put(e, e));
//        mStagedVisionRange.addAll(vision);
    }

    public void commit() {
        mFinalVisionRange.clear();
        mFinalVisionRange.putAll(mStagedVisionRange);
        mFinalActionRange.clear();
        mFinalActionRange.putAll(mStagedActionRange);
        mFinalActionLineOfSight.clear();
        mFinalActionLineOfSight.putAll(mStagedActionLineOfSight);
        mFinalActionAreaOfEffect.clear();
        mFinalActionAreaOfEffect.putAll(mStagedActionAreaOfEffect);
        mFinalTarget = mStagedTarget;
        mFinalAction = mStagedAction;
    }

    public void reset() {
        mActed = false;
        previouslyTargeting = null;
    }
    private Entity previouslyTargeting = null;

    public String getAction() { return mStagedAction; }

    public boolean hasActed() { return mActed; }
    public void setActed(boolean hasActed) { mActed = hasActed; }
    public boolean isUpdated(String key, Object... values) { return mStateLock.isUpdated(key, values); }


    public Entity getFinalTileTargeted() { return mFinalTarget; }
    public Set<Entity> getTilesInFinalRange() { return mFinalActionRange.keySet(); }
    public Set<Entity> getTilesInFinalLineOfSight() { return mFinalActionLineOfSight.keySet(); }
    public Set<Entity> getTilesInFinalAreaOfEffect() { return mFinalActionAreaOfEffect.keySet(); }

    public Entity getStagedTileTargeted() { return mStagedTarget; }
    public Set<Entity> getTilesInStagedRange() { return mStagedActionRange.keySet(); }
    public Set<Entity> getTilesInStagedLineOfSight() { return mStagedActionLineOfSight.keySet(); }
    public Set<Entity> getTilesInStagedAreaOfEffect() { return mStagedActionAreaOfEffect.keySet(); }
    public boolean isValidTarget() { return mStagedActionRange.containsKey(mStagedTarget); }




//    public Entity targeting = null;
//    private boolean mActed = false;
//    private final StateLock mStateLock = new StateLock();
//
//    private final Set<Entity> mFinalActionRange = ConcurrentHashMap.newKeySet();
//    private final Set<Entity> mFinalActionAreaOfEffect = ConcurrentHashMap.newKeySet();
//    private final Set<Entity> mFinalActionLineOfSight = ConcurrentHashMap.newKeySet();
//    private final Set<Entity> mFinalVisionRange = ConcurrentHashMap.newKeySet();
//    private String mFinalAction = null;
//    private Entity mFinalTarget = null;
//
//
//    private final Set<Entity> mStagedActionRange = ConcurrentHashMap.newKeySet();
//    private final Set<Entity> mStagedActionAreaOfEffect = ConcurrentHashMap.newKeySet();
//    private final Set<Entity> mStagedActionLineOfSight = ConcurrentHashMap.newKeySet();
//    private final Set<Entity> mStagedVisionRange = ConcurrentHashMap.newKeySet();
//    private String mStagedAction = null;
//    private Entity mStagingTarget = null;
//
//    public void stageRange(Collection<Entity> range) {
//        mStagedActionRange.clear();;
//        mStagedActionRange.addAll(range);
//    }
//
//    public void stageLineOfSight(Collection<Entity> lineOfSight) {
//        mStagedActionLineOfSight.clear();
//        mStagedActionLineOfSight.addAll(lineOfSight);
//    }
//
//    public void stageTarget(Entity target) {
//        mStagingTarget = target;
//    }
//
//    public void stageAreaOfEffect(Collection<Entity> areaOfEffect) {
//        mStagedActionAreaOfEffect.clear();
//        mStagedActionAreaOfEffect.addAll(areaOfEffect);
//    }
//
//    public void stageAction(String action) {
//        mStagedAction = action;
//    }
//
//    public void stageVision(Collection<Entity> vision) {
//        mStagedVisionRange.clear();
//        mStagedVisionRange.addAll(vision);
//    }
//
//    public void commit() {
//        mFinalVisionRange.clear();
//        mFinalVisionRange.addAll(mStagedVisionRange);
//        mFinalActionRange.clear();
//        mFinalActionRange.addAll(mStagedActionRange);
//        mFinalActionLineOfSight.clear();
//        mFinalActionLineOfSight.addAll(mStagedActionLineOfSight);
//        mFinalActionAreaOfEffect.clear();
//        mFinalActionAreaOfEffect.addAll(mStagedActionAreaOfEffect);
//        mFinalTarget = mStagingTarget;
//        mFinalAction = mStagedAction;
//    }
//
//    public void reset() {
//        mActed = false;
//        previouslyTargeting = null;
//    }
//    private Entity previouslyTargeting = null;
//
//    public String getAction() { return mStagedAction; }
//
//    public boolean hasActed() { return mActed; }
//    public void setActed(boolean hasActed) { mActed = hasActed; }
//    public boolean isUpdated(String key, Object... values) { return mStateLock.isUpdated(key, values); }
//
//
//    public Entity getFinalTileTargeted() { return mFinalTarget; }
//    public Set<Entity> getTilesInFinalRange() { return mFinalActionRange; }
//    public Set<Entity> getTilesInFinalLineOfSight() { return mFinalActionLineOfSight; }
//    public Set<Entity> getTilesInFinalAreaOfEffect() { return mFinalActionAreaOfEffect; }
//
//    public Entity getStagingTileTargeted() { return mStagingTarget; }
//    public Set<Entity> getTilesInStagingRange() { return mStagedActionRange; }
//    public Set<Entity> getTilesInStagingLineOfSight() { return mStagedActionLineOfSight; }
//    public Set<Entity> getTilesInStagingAreaOfEffect() { return mStagedActionAreaOfEffect; }
//    public boolean isValidTarget(Entity tileEntity) { return mStagedActionRange.contains(tileEntity); }
}
