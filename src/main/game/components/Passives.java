package main.game.components;

import main.game.stores.pools.unit.Unit;

import java.util.HashSet;
import java.util.Set;

public class Passives extends Component {
    private final Set<String> passives = new HashSet<>();

    public Passives(Unit template) {
        passives.addAll(template.passives);
    }

    public Set<String> getPassives() { return new HashSet<>(passives); }
    public boolean contains(String str) { return passives.contains(str); }
}
