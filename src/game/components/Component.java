package game.components;

import game.entity.Entity;

public abstract class Component {

    public Entity owner;

    public void setOwner(Entity entity) {
        if (owner != null) { return; }
        owner = entity;
    }
}
