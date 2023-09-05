package main.game.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.game.stores.pools.action.Action;
import main.game.stores.pools.action.ActionPool;
import main.game.stores.pools.unit.Unit;

public class MoveSet extends Component {

    private final Set<Action> abilities = new HashSet<>();
    private final Set<Action> passives = new HashSet<>();
    private final Set<Action> actions = new HashSet<>();

    public MoveSet() { }

    public MoveSet(Unit unitTemplate) { subscribe(unitTemplate); }

    public void subscribe(Unit unitTemplate) {
        abilities.clear();

        for (String abilityName : unitTemplate.abilities) {
            Action action = ActionPool.getInstance().get(abilityName);
            abilities.add(action);
        }
        abilities.add(ActionPool.getInstance().get("Prone"));
    }

    public List<Action> getCopy() { return new ArrayList<>(abilities); }
}
