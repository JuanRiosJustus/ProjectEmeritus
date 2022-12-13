package game.components;

import game.entity.Entity;
import game.stores.pools.ability.Ability;

import java.util.Set;

public class Combat extends Component {

    public Set<Entity> targetedUnits = null;
    public Ability selectedAbility = null;
}
