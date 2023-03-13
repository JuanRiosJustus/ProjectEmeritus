package game.systems.actions.behaviors;

import game.GameModel;
import game.entity.Entity;

public abstract class Behavior {
    protected BehaviorUtils utils = new BehaviorUtils();
    public abstract void move(GameModel model, Entity unit);
    public abstract void attack(GameModel model, Entity unit);
}
