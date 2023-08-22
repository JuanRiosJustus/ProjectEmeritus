package main.game.components;

import main.game.stores.pools.unit.Unit;

import java.util.UUID;

public class Identity extends Component {

    private final String name;
    private final String uuid;

    public Identity() { this("", null); }
    public Identity(Unit unitTemplate) { this(unitTemplate.species, null); }
    public Identity(String name) { this(name, null); }

    public Identity(String name, String uuid) {
        this.name = name;
        this.uuid = (uuid == null ? UUID.randomUUID().toString() : uuid);
    }
    public String getUuid() { return uuid; }
    public String getName() { return name; }
    
    public String toString() { return name; }
}
