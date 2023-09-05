package main.game.components;

import java.util.HashSet;
import java.util.Set;

import main.game.stores.pools.unit.Unit;

public class Actions extends Component {
    
    private final Set<String> active = new HashSet<>();

     public Actions(Unit template) {
         active.addAll(template.abilities);
         active.add("Prone");
         active.add("Defend");
         active.add("Intimidate");
         active.add("Yield");
     }

     public Set<String> getAbilities() { return new HashSet<>(active); }
}
