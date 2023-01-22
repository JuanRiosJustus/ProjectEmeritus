package game.components;

import game.stores.pools.unit.UnitTemplate;

public class Name extends Component {

    public final String value;

    public Name() { value = ""; }
    public Name(UnitTemplate unitTemplate) { value = unitTemplate.name; }
    public Name(String name) { value = name; }
}
