package main.game.systems.actions;

import main.game.components.AbilityComponent;
import main.game.components.MovementComponent;
import main.game.components.behaviors.Behavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;
import main.game.systems.GameSystem;
import main.logging.EmeritusLogger;


import java.util.SplittableRandom;

public class BehaviorSystem extends GameSystem {
    private final EmeritusLogger logger = EmeritusLogger.create(BehaviorSystem.class);
    private final SplittableRandom mRandom = new SplittableRandom();

//    @Override
//    public void update(GameModel model, Entity unitEntity) {
//        // Setup initial behavior for ai
//        if (model.getSpeedQueue().peek() != unitEntity) { return; }
//        // Ensure the behavior has not already been setup
//        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
//        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//        if (abilityComponent.hasActed() || movementComponent.hasMoved()) { return; }
//        Behavior behavior = unitEntity.get(Behavior.class);
//        if (behavior.isSetup()) { return; }
//        behavior.setMoveFirst(mRandom.nextBoolean());
//        behavior.setIsSetup(true);
//    }

    public void update(GameModel model, String unitID) {
        // Setup initial behavior for ai
        String currentTurnsUnit = model.getSpeedQueue().peekV2();
        if (currentTurnsUnit == null) { return; }
        // Ensure the behavior has not already been setup
        Entity unitEntity = EntityStore.getInstance().get(unitID);
        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        if (abilityComponent.hasActed() || movementComponent.hasMoved()) { return; }

        Behavior behavior = unitEntity.get(Behavior.class);
        if (behavior.isSetup()) { return; }
        behavior.setMoveFirst(mRandom.nextBoolean());
        behavior.setIsSetup(true);
    }
}
