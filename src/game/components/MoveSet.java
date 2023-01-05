package game.components;

import constants.Constants;
import game.stats.node.StringNode;
import game.stores.pools.ability.AbilityPool;
import game.stores.pools.ability.Ability;
import game.stores.pools.unit.Unit;

import java.util.*;

public class MoveSet extends Component {

    private final Set<Ability> abilities = new HashSet<>();

    public MoveSet() { }

    public MoveSet(Unit unit) { subscribe(unit); }

    public void subscribe(Unit unit) {
        abilities.clear();

        for (String abilityName : unit.abilities) {
            Ability ability = AbilityPool.instance().getAbility(abilityName);
            abilities.add(ability);
        }
    }

    public List<Ability> getCopy() { return new ArrayList<>(abilities); }
}
