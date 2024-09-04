package main.game.components;

import main.game.entity.Entity;
import main.json.JsonSerializable;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public abstract class Component extends JsonSerializable {

    protected static final ELogger mLogger = ELoggerFactory.getInstance().getELogger(Component.class);

    public Entity mOwner;
    public void setOwner(Entity entity) {
        if (mOwner != null) { return; }
        mOwner = entity;
    }
}
