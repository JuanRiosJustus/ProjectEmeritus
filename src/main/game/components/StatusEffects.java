package main.game.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StatusEffects extends Component {
    private final Map<String, Object> statusEffects = new HashMap<>();
    private boolean handled = false;

    public void add(String effect, Object source) { statusEffects.put(effect, source); }
    public Object remove(String effect) { return statusEffects.remove(effect); }
    public boolean contains(String effect) { return statusEffects.containsKey(effect); }
    public void clear() { statusEffects.clear(); }

    public Map<String, Object> getStatusEffects() { return new HashMap<>(statusEffects); }

    public boolean shouldHandle() { return !handled; }
    public void setHandled(boolean b) { handled = b; }

    public void handleEndStep() {

    }

    public void handleUpkeep() {

    }
}
