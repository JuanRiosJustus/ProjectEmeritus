package game.components.statistics;

import java.util.HashSet;
import java.util.Set;

public class StatusEffectManager {

    private final Set<String> statusEffects = new HashSet<>();

    public void add(String effect) { statusEffects.add(effect); }
    public void update() {

    }
}
