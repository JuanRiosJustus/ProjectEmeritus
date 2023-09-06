package main.game.components;

import main.game.entity.Entity;

import java.io.Serializable;

public abstract class Component implements Serializable {

    public Entity owner;
    public void setOwner(Entity entity) {
        if (owner != null) { return; }
        owner = entity;
    }
}
