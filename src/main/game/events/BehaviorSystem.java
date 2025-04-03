package main.game.events;

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

    public BehaviorSystem() { }
    public BehaviorSystem(GameModel gameModel) {
        super(gameModel);
    }


    public void update(GameModel model, String unitID) {
//        // Setup initial behavior for ai
//        String currentTurnsUnit = model.getSpeedQueue().peek();
//        if (currentTurnsUnit == null) { return; }
//
//        for (String unitID : model.getSpeedQueue().getAllEntitiesInTurnQueue()) {
//            // Ensure the behavior has not already been setup
//            Entity unitEntity = EntityStore.getInstance().get(unitID);
//            Behavior behavior = unitEntity.get(Behavior.class);
//            if (behavior.isUserControlled()) { continue; }
//
//            AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
//            MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//            if (abilityComponent.hasActed() || movementComponent.hasMoved()) { return; }
//
//            if (behavior.isSetup()) { continue; }
//            behavior.setMoveFirst(mRandom.nextBoolean());
//            behavior.setIsSetup(true);
//        }
//         Ensure the behavior has not already been setup
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
