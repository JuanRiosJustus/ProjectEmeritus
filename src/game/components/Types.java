package game.components;

import game.stores.pools.unit.Unit;

import java.util.HashSet;
import java.util.Set;

public class Types extends Component {
    public final Set<String> value;
    public Types(Unit unitTemplate) {
        value = new HashSet<>(unitTemplate.types);
    }
    public Set<String> getCopy() {
        return new HashSet<>(value);
    }
}
