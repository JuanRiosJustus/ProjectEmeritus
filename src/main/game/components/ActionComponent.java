package main.game.components;

import main.constants.StateLock;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.action.Action;

import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ActionComponent extends Component {
    public Entity targeting = null;
    private boolean mActed = false;
    public Action mSelected = null;
    private String mSelectedAction = null;
    private final StateLock mStateLock = new StateLock();

    private final Set<Entity> mFinalActionRange = ConcurrentHashMap.newKeySet();
    private final Set<Entity> mFinalActionAreaOfEffect = ConcurrentHashMap.newKeySet();
    private final Set<Entity> mFinalActionLineOfSight = ConcurrentHashMap.newKeySet();
    private final Set<Entity> mFinalVisionRange = ConcurrentHashMap.newKeySet();
    private Entity mFinalTarget = null;


    private final Set<Entity> mStagingActionRange = ConcurrentHashMap.newKeySet();
    private final Set<Entity> mStagingActionAreaOfEffect = ConcurrentHashMap.newKeySet();
    private final Set<Entity> mStagingActionLineOfSight = ConcurrentHashMap.newKeySet();
    private final Set<Entity> mStagingVisionRange = ConcurrentHashMap.newKeySet();
    private Entity mStagingTarget = null;

    public void stageRange(Set<Entity> range) {
        mStagingActionRange.clear();;
        mStagingActionRange.addAll(range);
    }

    public void stageLineOfSight(LinkedList<Entity> lineOfSight) {
        mStagingActionLineOfSight.clear();
        mStagingActionLineOfSight.addAll(lineOfSight);
    }

    public void stageTarget(Entity target) {
        mStagingTarget = target;
    }

    public void stageAreaOfEffect(Set<Entity> areaOfEffect) {
        mStagingActionAreaOfEffect.clear();
        mStagingActionAreaOfEffect.addAll(areaOfEffect);
    }

    public void stageAction(String action) {
        mSelectedAction = action;
    }

    public void stageVision(Set<Entity> vision) {
        mStagingVisionRange.clear();
        mStagingVisionRange.addAll(vision);
    }

    public void commit() {
        mFinalVisionRange.clear();
        mFinalVisionRange.addAll(mStagingVisionRange);
        mFinalActionRange.clear();
        mFinalActionRange.addAll(mStagingActionRange);
        mFinalActionLineOfSight.clear();
        mFinalActionLineOfSight.addAll(mStagingActionLineOfSight);
        mFinalActionAreaOfEffect.clear();
        mFinalActionAreaOfEffect.addAll(mStagingActionAreaOfEffect);
        mFinalTarget = mStagingTarget;
    }

    public void reset() {
        mActed = false;
        mSelected = null;
        previouslyTargeting = null;
    }
    public Action getSelected() { return mSelected; }
    private Entity previouslyTargeting = null;
    public boolean shouldNotUpdate(GameModel model, Entity targeting) {
        boolean isSameTarget = previouslyTargeting == targeting;
        if (!isSameTarget) {
//            System.out.println("Waiting for user action input... " + previouslyTargeting + " vs " + targeting);
        }
        previouslyTargeting = targeting;
        return isSameTarget && mOwner.get(UserBehavior.class) != null;
    }

    public String getAction() { return mSelectedAction; }

    public boolean hasActed() { return mActed; }
    public void setActed(boolean hasActed) { mActed = hasActed; }
    public boolean isUpdated(String key, Object... values) { return mStateLock.isUpdated(key, values); }

    public Set<Entity> getTilesInFinalRange() { return mFinalActionRange; }
    public Set<Entity> getTilesInFinalLineOfSight() { return mFinalActionLineOfSight; }
    public Set<Entity> getTilesInFinalAreaOfEffect() { return mFinalActionAreaOfEffect; }

    public Set<Entity> getTilesInStagingRange() { return mStagingActionRange; }
    public Set<Entity> getTilesInStagingLineOfSight() { return mStagingActionLineOfSight; }
    public Set<Entity> getTilesInStagingAreaOfEffect() { return mStagingActionAreaOfEffect; }
    public boolean isValidTarget(Entity tileEntity) { return mStagingActionRange.contains(tileEntity); }
}
