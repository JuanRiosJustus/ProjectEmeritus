package main.game.systems.actions.behaviors;

import main.constants.Pair;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.*;

public abstract class MoveActionBehavior {
    protected ELogger mLogger = ELoggerFactory.getInstance().getELogger(MoveActionBehavior.class);
    protected SplittableRandom mRandom = new SplittableRandom();
//    protected List<Action> getDamagingActions(Entity unit) {
//        return new ArrayList<>(unit.get(StatisticsComponent.class)
//                .getAbilities()
//                .stream()
//                .map(e -> ActionPool.getInstance().getAction(e))
//                .filter(Objects::nonNull)
//                .filter(e -> e.getHealthDamage(unit) > 0 || e.getManaDamage(unit) > 0 || e.getStaminaDamage(unit) > 0)
//                .toList());
//    }
//
//    protected List<Action> getHealingActions(Entity unit) {
//        return new ArrayList<>(unit.get(StatisticsComponent.class)
//                .getAbilities()
//                .stream()
//                .map(e -> ActionPool.getInstance().)
//                .filter(Objects::nonNull)
//                .filter(e -> e.getHealthDamage(unit) < 0 || e.getManaDamage(unit) < 0 || e.getStaminaDamage(unit) < 0)
//                .toList());
//    }

    public abstract Entity toMoveTo(GameModel model, Entity unitEntity);
    public abstract Pair<Entity, String> toActOn(GameModel model, Entity unitEntity);
}
