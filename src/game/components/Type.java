package game.components;

import java.util.HashSet;
import java.util.Set;

import game.stores.pools.unit.Unit;

public class Type extends Component {
    private final Set<String> typing = new HashSet<>();

     public Type(Unit template) {  typing.addAll(template.type); }

     public Set<String> getTypes() { return new HashSet<>(typing); }
}
