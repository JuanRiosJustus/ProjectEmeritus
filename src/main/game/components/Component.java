package main.game.components;

import main.game.entity.Entity;
import main.json.JsonSerializable;

public abstract class Component extends JsonSerializable {

    public Entity mOwner;
    public void setOwner(Entity entity) {
        if (mOwner != null) { return; }
        mOwner = entity;
    }
}
