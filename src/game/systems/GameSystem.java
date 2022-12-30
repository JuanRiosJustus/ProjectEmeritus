package game.systems;

import game.GameModel;
import game.entity.Entity;
import input.InputController;

import java.util.SplittableRandom;

public abstract class GameSystem {
    protected SplittableRandom random = new SplittableRandom();
    public abstract void update(GameModel model, Entity unit);
}
