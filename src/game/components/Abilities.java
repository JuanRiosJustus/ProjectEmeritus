package game.components;

import java.util.HashSet;
import java.util.Set;

import game.stores.pools.unit.Unit;

public class Abilities extends Component {
    
    private final Set<String> typing = new HashSet<>();

     public Abilities(Unit template) {  typing.addAll(template.abilities); }

     public Set<String> getAbilities() { return new HashSet<>(typing); }
}
