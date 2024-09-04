package main.game.components;

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

    public final Set<Entity> mFinalRange = ConcurrentHashMap.newKeySet();
    public final Set<Entity> mFinalAreaOfEffect = ConcurrentHashMap.newKeySet();
    public final Set<Entity> mFinalLineOfSight = ConcurrentHashMap.newKeySet();
    public final Set<Entity> mFinalTarget = ConcurrentHashMap.newKeySet();


    public final Set<Entity> mPreviewRange = ConcurrentHashMap.newKeySet();
    public final Set<Entity> mPreviewAreaOfEffect = ConcurrentHashMap.newKeySet();
    public final Set<Entity> mPreviewLineOfSight = ConcurrentHashMap.newKeySet();
    public final Set<Entity> mPreviewTarget = ConcurrentHashMap.newKeySet();


    public final Set<Entity> mVisionRange = ConcurrentHashMap.newKeySet();

    public void setLineOfSight(LinkedList<Entity> lineOfSight) {
        mPreviewLineOfSight.clear();
        mPreviewLineOfSight.addAll(lineOfSight);
    }

    public void setAreaOfEffect(Set<Entity> areaOfEffect) {
        mPreviewAreaOfEffect.clear();
        mPreviewAreaOfEffect.addAll(areaOfEffect);
    }

    public void setRange(Set<Entity> range) {
        mPreviewRange.clear();;
        mPreviewRange.addAll(range);
    }

    public void setTarget(Entity pathToTarget) {
        if (pathToTarget == null) { return; }
        mPreviewTarget.clear();
        mPreviewTarget.add(pathToTarget);
    }

    public void setVision(Set<Entity> set) {
        mVisionRange.clear();
        mVisionRange.addAll(set);
    }

    public void commit() {
        mFinalRange.clear();
        mFinalRange.addAll(mPreviewRange);
        mFinalLineOfSight.clear();
        mFinalLineOfSight.addAll(mPreviewLineOfSight);
        mFinalAreaOfEffect.clear();
        mFinalAreaOfEffect.addAll(mPreviewAreaOfEffect);
        mFinalTarget.clear();
        mFinalTarget.addAll(mPreviewTarget);
    }

    public void reset() {

//        mPreviewRange.clear();
//        mPreviewLineOfSight.clear();
//        mPreviewAreaOfEffect.clear();
//        mPreviewTarget.clear();

        mVisionRange.clear();
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

//    public void setSelectedAction(Action action) {
//        mSelected = action;
//    }

    public void setAction(String action) {
        mSelectedAction = action;
    }
    public String getAction() { return mSelectedAction; }

    public boolean hasActed() { return mActed; }
    public void setActed(boolean hasActed) { mActed = hasActed; }

    public Set<Entity> getTilesInFinalRange() { return mFinalRange; }
    public Set<Entity> getTilesInFinalLineOfSight() { return mFinalLineOfSight; }
    public Set<Entity> getTilesInFinalAreaOfEffect() { return mFinalAreaOfEffect; }
    public Set<Entity> getTilesInFinalTargets() { return mFinalTarget; }

    public Set<Entity> getTilesInPreviewRange() { return mPreviewRange; }
    public Set<Entity> getTilesInPreviewLineOfSight() { return mPreviewLineOfSight; }
    public Set<Entity> getTilesInPreviewAreaOfEffect() { return mPreviewAreaOfEffect; }
    public Set<Entity> getTilesInPreviewTargets() { return mPreviewTarget; }
}
