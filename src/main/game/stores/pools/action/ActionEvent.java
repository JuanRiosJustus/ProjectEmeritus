package main.game.stores.pools.action;

import main.game.entity.Entity;

import java.util.Set;

public class ActionEvent {
    private final Set<Entity> mTargets;
    private final Entity mActor;
    private final String mAction;
    public ActionEvent(Entity actor, String action, Set<Entity> targets) {
        mActor = actor;
        mAction = action;
        mTargets = targets;
    }

    public Entity getActor() { return mActor; }
    public String getAction() { return mAction; }
    public Set<Entity> getTargets() { return mTargets; }
}