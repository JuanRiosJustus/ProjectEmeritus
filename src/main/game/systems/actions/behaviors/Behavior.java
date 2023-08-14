package main.game.systems.actions.behaviors;

import main.game.entity.Entity;
import main.game.main.GameModel;

public abstract class Behavior {
    protected static ActionUtils utils = new ActionUtils();
    public abstract void move(GameModel model, Entity unit);
    public abstract void attack(GameModel model, Entity unit);
}
