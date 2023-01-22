package game.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import game.stores.pools.ability.Ability;
import game.stores.pools.ability.AbilityPool;
import game.stores.pools.unit.UnitTemplate;

public class MoveSet extends Component {

    private final Set<Ability> abilities = new HashSet<>();

    public MoveSet() { }

    public MoveSet(UnitTemplate unitTemplate) { subscribe(unitTemplate); }

    public void subscribe(UnitTemplate unitTemplate) {
        abilities.clear();

        for (String abilityName : unitTemplate.abilities) {
            Ability ability = AbilityPool.instance().getAbility(abilityName);
            abilities.add(ability);
        }
    }

    public List<Ability> getCopy() { return new ArrayList<>(abilities); }
}
