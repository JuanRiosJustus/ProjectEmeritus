package game.systems.combat;

import game.entity.Entity;
import game.stores.pools.ability.Ability;

import java.util.Set;

public class CombatEvent {

    public final Ability ability;
    public final Set<Entity> tiles;
    public final Entity actor;
    public int healthCost = 0;
    public int energyCost = 0;

    public CombatEvent(Entity u, Ability a, Set<Entity> t) {
        actor = u;
        tiles = t;
        ability = a;
    }
}