package main.game.systems.actions;

import main.game.components.ActionComponent;
import main.game.components.MovementComponent;
import main.game.components.behaviors.Behavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.systems.GameSystem;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.SplittableRandom;

public class BehaviorSystem extends GameSystem {
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(BehaviorSystem.class);
    private final SplittableRandom mRandom = new SplittableRandom();

    @Override
    public void update(GameModel model, Entity unitEntity) {
        // Setup initial behavior for ai
        if (model.getSpeedQueue().peek() != unitEntity) { return; }
        // Ensure the behavior has not already been setup
        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        if (actionComponent.hasActed() || movementComponent.hasMoved()) { return; }
        Behavior behavior = unitEntity.get(Behavior.class);
        if (behavior.isSetup()) { return; }
        behavior.setMoveFirst(mRandom.nextBoolean());
        behavior.setIsSetup(true);
    }
}
