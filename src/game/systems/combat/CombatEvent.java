package game.systems.combat;

import game.entity.Entity;
import game.stores.pools.ability.Ability;

import java.util.Set;

public class CombatEvent {

    public final Ability ability;
    public final Set<Entity> defenders;
    public final Entity attacker;
    public int healthCost = 0;
    public int energyCost = 0;

    public CombatEvent(Entity u, Ability a, Set<Entity> t) {
        attacker = u;
        defenders = t;
        ability = a;
    }
}