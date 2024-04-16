package main.game.components;

import main.game.entity.Entity;
import main.game.stores.pools.ability.Ability;

import java.util.Set;

public class Combat extends Component {

    public Set<Entity> targetedUnits = null;
    public Ability selectedAbility = null;
}
