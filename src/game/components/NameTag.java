package game.components;

import game.stores.pools.unit.Unit;

public class NameTag extends Component {

    public final String value;

    public NameTag() { value = ""; }
    public NameTag(Unit unitTemplate) { value = unitTemplate.name; }
    public NameTag(String name) { value = name; }
}
