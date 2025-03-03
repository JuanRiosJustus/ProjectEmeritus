package main.game.components;

import org.json.JSONObject;
import main.game.entity.Entity;
import main.logging.EmeritusLogger;


public abstract class Component extends JSONObject {

    protected static final EmeritusLogger mLogger = EmeritusLogger.create(Component.class);

    public Entity mOwner;
    public void setOwner(Entity entity) {
        if (mOwner != null) { return; }
        mOwner = entity;
    }

    public String getIdentity() { return String.valueOf(hashCode()); }
}
