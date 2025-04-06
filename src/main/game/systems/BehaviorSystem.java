package main.game.systems;

import main.game.components.AbilityComponent;
import main.game.components.MovementComponent;
import main.game.components.behaviors.Behavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;
import main.game.systems.actions.behaviors.RandomnessBehavior;
import main.logging.EmeritusLogger;


import java.util.SplittableRandom;

public class BehaviorSystem extends GameSystem {
    private final EmeritusLogger logger = EmeritusLogger.create(BehaviorSystem.class);
    private final SplittableRandom mRandom = new SplittableRandom();
    private final RandomnessBehavior mRandomnessBehavior = new RandomnessBehavior();
//    public BehaviorSystem() { }
    public BehaviorSystem(GameModel gameModel) { super(gameModel); }


    public void update(GameModel model, SystemContext systemContext) {
        String unitID = systemContext.getCurrentUnitID();
        Entity unitEntity = EntityStore.getInstance().get(unitID);
        if (unitEntity == null) { return; }
        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        if (abilityComponent.hasActed() || movementComponent.hasMoved()) { return; }

        Behavior behavior = unitEntity.get(Behavior.class);
        if (behavior.hasFinishedSetup()) { return; }
        behavior.setShouldMoveFirst(mRandom.nextBoolean());
        behavior.setHasFinishedSetup(true);


//        String unitID = systemContext.getCurrentUnitID();
//        Entity unitEntity = EntityStore.getInstance().get(unitID);
//        if (unitEntity == null) { return; }
//        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
//        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//        // Decide to attack first, or move first
//        Behavior behavior = unitEntity.get(Behavior.class);
//        if (!behavior.hasFinishedSetup) {
////            behavior.setShouldMoveFirst(mRandom.nextBoolean());
//            behavio
//            behavior.hasFinishedSetup = true;
//        }
//        boolean shouldMove = behavior.shouldMoveFirst() & !behavior.h
//        if (behavior.shouldMoveFirst() && ne)
//        //
//
//
////        if (movementComponent.)
//        if (abilityComponent.hasActed() || movementComponent.hasMoved()) { return; }
//
//        Behavior behavior = unitEntity.get(Behavior.class);
//        if (behavior.hasFinishedSetup()) { return; }
//        behavior.setShouldMoveFirst(mRandom.nextBoolean());
//        behavior.setHasFinishedSetup(true);

    }

//    private void ttt() {
//        // Only move if its entities turn
//        String unityEntityID = systemContext.getCurrentUnitID();
//        Entity unitEntity = getEntityWithID(unityEntityID);
//        if (unitEntity == null) { return; }
//
//        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
//        if (animationComponent.hasPendingAnimations()) { return; }
//
//        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//        if (movementComponent.hasMoved()) { return; }
//
//        Behavior behavior = unitEntity.get(Behavior.class);
//        float unitWaitTimeBetweenActivities = model.getGameState().getUnitWaitTimeBetweenActivities();
//        if (behavior.isUserControlled() || behavior.shouldWait(unitWaitTimeBetweenActivities)) { return; }
//
//        String tileToMoveToID = mRandomnessBehavior.toMoveTo(model, unityEntityID);
//        if (tileToMoveToID == null) {  movementComponent.setMoved(true); return; }
//
//        mEventBus.publish(MovementSystem.MOVE_ENTITY_EVENT, MovementSystem.createMoveEntityEvent(
//                unityEntityID, tileToMoveToID, true
//        ));
//    }

}
