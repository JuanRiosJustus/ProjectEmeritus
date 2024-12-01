package main.game.stores.pools.action;

import main.game.entity.Entity;

import java.util.Set;

public class ActionEvent {
    public final Set<Entity> targets;
    public final Entity actor;
    public final String action;


    public ActionEvent(Entity u, String a, Set<Entity> t) {
        actor = u;
        targets = t;
        action = a;
    }

    public Entity getActor() { return actor; }
    public String getAction() { return action; }
    public Set<Entity> getTargets() { return targets; }
}