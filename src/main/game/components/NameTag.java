package main.game.components;

import main.game.stores.pools.unit.Unit;

public class NameTag extends Component {

    private final String value;

    public NameTag() { value = ""; }
    public NameTag(Unit unitTemplate) { value = unitTemplate.name; }
    public NameTag(String name) { value = name; }
    
    public String toString() { return value; }
}
