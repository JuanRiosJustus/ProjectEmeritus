package main.game.stores.pools.action;

import main.game.entity.Entity;

import java.util.Set;

public class ActionEvent {

    public final Action action;
    public String actionName;
    public final Set<Entity> targets;
    public final Entity actor;

    public ActionEvent(Entity u, Action a, Set<Entity> t) {
        actor = u;
        targets = t;
        action = a;
    }

    public ActionEvent(Entity u, Action a, String actionName2, Set<Entity> t) {
        actor = u;
        targets = t;
        action = a;
        actionName = actionName2;
    }

    public Entity getActor() { return actor; }
    public String getAction() { return actionName; }
    public Set<Entity> getTargets() { return targets; }
}