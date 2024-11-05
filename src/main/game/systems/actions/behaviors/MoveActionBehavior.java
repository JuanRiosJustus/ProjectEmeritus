package main.game.systems.actions.behaviors;

import main.constants.Pair;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.PathBuilder;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.*;

public abstract class MoveActionBehavior {
    protected ELogger mLogger = ELoggerFactory.getInstance().getELogger(MoveActionBehavior.class);
    protected SplittableRandom mRandom = new SplittableRandom();
    protected PathBuilder mPathBuilder = new PathBuilder();

    public abstract Entity toMoveTo(GameModel model, Entity unitEntity);
    public abstract Pair<Entity, String> toActOn(GameModel model, Entity unitEntity);
}
