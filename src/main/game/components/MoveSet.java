package main.game.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.game.stores.pools.ability.Ability;
import main.game.stores.pools.ability.AbilityPool;
import main.game.stores.pools.unit.Unit;

public class MoveSet extends Component {

    private final Set<Ability> abilities = new HashSet<>();

    public MoveSet() { }

    public MoveSet(Unit unitTemplate) { subscribe(unitTemplate); }

    public void subscribe(Unit unitTemplate) {
        abilities.clear();

        for (String abilityName : unitTemplate.abilities) {
            Ability ability = AbilityPool.getInstance().getAbility(abilityName);
            abilities.add(ability);
        }
    }

    public List<Ability> getCopy() { return new ArrayList<>(abilities); }
}
