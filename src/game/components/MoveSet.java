package game.components;

import constants.Constants;
import game.stats.node.StringNode;
import game.stores.pools.ability.AbilityPool;
import game.stores.pools.ability.Ability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoveSet extends Component {

    private final List<Ability> abilities = new ArrayList<>();

    public MoveSet() { }

    public MoveSet(StringNode abilityNode) { subscribe(abilityNode); }

    public void subscribe(StringNode abilityNode) {
        abilities.clear();
        abilities.addAll(Arrays.stream(abilityNode.value.split(Constants.SEMICOLON))
                .map(ability -> AbilityPool.instance().getAbility(ability.trim()))
                .toList());
    }

    public List<Ability> getCopy() { return new ArrayList<>(abilities); }
}
