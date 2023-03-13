package game.components;

import game.stores.pools.unit.Unit;

public class Name extends Component {

    public final String value;

    public Name() { value = ""; }
    public Name(Unit unitTemplate) { value = unitTemplate.name; }
    public Name(String name) { value = name; }
}
