package main.game.components;

import main.constants.csv.CsvRow;
import main.game.stores.pools.unit.Unit;

import java.util.UUID;

public class IdentityComponent extends Component {

    public IdentityComponent() { this("", null); }
    public IdentityComponent(CsvRow unit) { this(unit.get("Unit"), null); }
    public IdentityComponent(String name) { this(name, null); }

    public IdentityComponent(String name, String uuid) {
        put("name", name);
        put("uuid", uuid == null ? UUID.randomUUID().toString() : uuid);
    }
    public String getUuid() { return (String) get("uuid"); }
    public String getName() { return (String) get("name"); }
    public String toString() { return (String) get("name"); }
}
