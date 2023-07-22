package game.systems.actions.behaviors;

import game.entity.Entity;
import game.main.GameModel;

public abstract class Behavior {
    protected static BehaviorUtils utils = new BehaviorUtils();
    public abstract void move(GameModel model, Entity unit);
    public abstract void attack(GameModel model, Entity unit);
}
