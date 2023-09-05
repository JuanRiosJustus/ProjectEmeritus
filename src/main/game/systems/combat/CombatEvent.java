package main.game.systems.combat;

import main.game.entity.Entity;
import main.game.stores.pools.action.Action;

import java.util.Set;

public class CombatEvent {

    public final Action action;
    public final Set<Entity> tiles;
    public final Entity actor;

    public CombatEvent(Entity u, Action a, Set<Entity> t) {
        actor = u;
        tiles = t;
        action = a;
    }
}