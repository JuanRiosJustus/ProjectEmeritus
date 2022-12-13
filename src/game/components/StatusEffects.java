package game.components;

import java.util.HashSet;
import java.util.Set;

public class StatusEffects extends Component {
    private final Set<String> statusEffects = new HashSet<>();

    public void add(String effect) { statusEffects.add(effect); }
    public boolean remove(String effect) { return statusEffects.remove(effect); }
}
