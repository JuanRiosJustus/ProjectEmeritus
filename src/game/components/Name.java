package game.components;

import game.stores.pools.unit.Unit;

public class Name extends Component {

    public final String value;

    public Name() { value = ""; }
    public Name(Unit unit) { value = unit.name; }
    public Name(String name) { value = name; }
}
