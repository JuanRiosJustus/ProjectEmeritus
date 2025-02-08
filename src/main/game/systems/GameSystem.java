package main.game.systems;

import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;

import java.util.SplittableRandom;

public abstract class GameSystem {
    protected SplittableRandom random = new SplittableRandom();
//    public abstract void update(GameModel model, Entity unit);
    public abstract void update(GameModel model, String id);
    public Entity getEntityWithID(String id) { return EntityStore.getInstance().get(id); }
}
