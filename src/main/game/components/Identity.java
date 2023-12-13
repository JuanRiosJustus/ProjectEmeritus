package main.game.components;

import main.game.stores.pools.unit.Unit;

import java.util.UUID;

public class Identity extends Component {

    private final String mName;
    private final String mUuid;
    public Identity() { this("", null); }
    public Identity(Unit unitTemplate) { this(unitTemplate.name, null); }
    public Identity(String name) { this(name, null); }

    public Identity(String name, String uuid) {
        mName = name;
        mUuid = (uuid == null ? UUID.randomUUID().toString() : uuid);
    }
    public String getUuid() { return mUuid; }
    public String getName() { return mName; }
    
    public String toString() { return mName; }
}
