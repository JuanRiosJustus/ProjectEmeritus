package main.game.systems.actions.behaviors;

import main.constants.Pair;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathing.lineofsight.PathingAlgorithms;
import main.game.stores.EntityStore;
import main.logging.EmeritusLogger;


import java.util.SplittableRandom;

public abstract class MoveActionBehavior {
    protected PathingAlgorithms mAlgorithm = new PathingAlgorithms();
    protected EmeritusLogger mLogger = EmeritusLogger.create(MoveActionBehavior.class);
    protected SplittableRandom mRandom = new SplittableRandom();
    protected BehaviorLibrary mBehaviorLibrary = new BehaviorLibrary();


    protected Entity getEntityWithID(String id) { return EntityStore.getInstance().get(id); }
    public abstract String toMoveTo(GameModel model, String entityID);
    public abstract Pair<String, String> toActOn(GameModel model, String entityID);
}
