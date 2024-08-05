package main.game.components;

import main.game.stores.pools.unit.Unit;

import java.util.UUID;

public class Identity extends Component {

    public Identity() { this("", null); }
    public Identity(Unit unit) { this(unit.getStringValue("Unit"), null); }
    public Identity(String name) { this(name, null); }

    public Identity(String name, String uuid) {
        mJsonData.put("name", name);
        mJsonData.put("uuid", uuid == null ? UUID.randomUUID().toString() : uuid);
    }
    public String getUuid() { return (String) mJsonData.get("uuid"); }
    public String getName() { return (String) mJsonData.get("name"); }
    public String toString() { return (String) mJsonData.get("name"); }
}
