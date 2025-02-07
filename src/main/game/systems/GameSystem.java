package main.game.systems;

import main.game.entity.Entity;
import main.game.main.GameModel;
import main.input.InputController;

import java.util.SplittableRandom;

public abstract class GameSystem {
    protected SplittableRandom random = new SplittableRandom();
    public abstract void update(GameModel model, Entity unit);
//    public abstract void update(GameModel model, String id);
}
