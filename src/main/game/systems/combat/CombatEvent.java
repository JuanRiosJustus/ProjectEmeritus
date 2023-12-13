package main.game.systems.combat;

import main.game.entity.Entity;
import main.game.stores.pools.action.Ability;

import java.util.Set;

public class CombatEvent {

    public final Ability ability;
    public final Set<Entity> tiles;
    public final Entity actor;

    public CombatEvent(Entity u, Ability a, Set<Entity> t) {
        actor = u;
        tiles = t;
        ability = a;
    }
}