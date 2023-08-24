package main.game.systems.actions.behaviors;

import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.SplittableRandom;

public abstract class Behavior {
    protected static final SplittableRandom random = new SplittableRandom();
    public abstract void move(GameModel model, Entity unit);
    public abstract void attack(GameModel model, Entity unit);
}
