package main.game.systems.actions.behaviors;

import main.constants.Pair;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathing.lineofsight.ManhattanPathing;
import main.game.pathing.lineofsight.PathingAlgorithm;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.*;

public abstract class MoveActionBehavior {
    protected PathingAlgorithm mAlgorithm = new ManhattanPathing();
    protected ELogger mLogger = ELoggerFactory.getInstance().getELogger(MoveActionBehavior.class);
    protected SplittableRandom mRandom = new SplittableRandom();

    public abstract Entity toMoveTo(GameModel model, Entity unitEntity);
    public abstract Pair<Entity, String> toActOn(GameModel model, Entity unitEntity);
}
