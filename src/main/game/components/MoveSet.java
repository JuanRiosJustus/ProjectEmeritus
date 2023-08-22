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
    private final Set<Ability> passives = new HashSet<>();
    private final Set<Ability> actions = new HashSet<>();

    public MoveSet() { }

    public MoveSet(Unit unitTemplate) { subscribe(unitTemplate); }

    public void subscribe(Unit unitTemplate) {
        abilities.clear();

        for (String abilityName : unitTemplate.abilities) {
            Ability ability = AbilityPool.getInstance().get(abilityName);
            abilities.add(ability);
        }
        abilities.add(AbilityPool.getInstance().get("Prone"));
    }

    public List<Ability> getCopy() { return new ArrayList<>(abilities); }
}
