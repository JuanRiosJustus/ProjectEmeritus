package main.game.stores.pools.action;

import main.game.entity.Entity;

import java.util.Set;

public class ActionEvent {
    private final Set<Entity> mTargets;
    private final Entity mActor;
    private final String mAction;
    private Runnable mEvent;
    public ActionEvent(Entity actor, String action, Set<Entity> targets) {
        mActor = actor;
        mAction = action;
        mTargets = targets;
    }

    public Entity getActor() { return mActor; }
    public String getAction() { return mAction; }
    public Set<Entity> getTargets() { return mTargets; }
    public Runnable getEvent() { return mEvent; }
    public void setDelayedEvent(Runnable event) {
        if (event == null) { return; }
        if (mEvent != null) { return; }
        mEvent = event;
    }
}