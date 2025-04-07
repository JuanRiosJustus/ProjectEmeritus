package main.game.systems;

import main.constants.Pair;
import main.game.components.AIComponent;
import main.game.components.ActionsComponent;
import main.game.components.TimerComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.EntityStore;
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
        String entityID = systemContext.getCurrentUnitID();
        Entity entity = EntityStore.getInstance().get(entityID);
        if (entity == null) { return; }

        // Decide to attack first, or move first
        ActionsComponent actionsComponent = entity.get(ActionsComponent.class);
        AIComponent aiComponent = entity.get(AIComponent.class);
        if (!aiComponent.isAI()) { return; }
        TimerComponent timerComponent = entity.get(TimerComponent.class);

        if (!actionsComponent.hasFinishedSetup()) {
            actionsComponent.setShouldMoveFirst(mRandom.nextBoolean());
            actionsComponent.setHasFinishedSetup(true);
        }


        long waitTime = 1000;
        if (actionsComponent.shouldMoveFirst()) {
            timerComponent.startTimer(TimerComponent.WAIT_BEFORE_USING_MOVE);
            if (!actionsComponent.hasFinishedMoving() && timerComponent.hasElapsedTime(TimerComponent.WAIT_BEFORE_USING_MOVE, waitTime)) {
                handleMovement(model, entityID);
                if (actionsComponent.hasFinishedMoving()) {
                    timerComponent.startTimer(TimerComponent.WAIT_BEFORE_USING_ABILITY);
                }
            } else if (!actionsComponent.hasFinishedUsingAbility() && timerComponent.hasElapsedTime(TimerComponent.WAIT_BEFORE_USING_ABILITY, waitTime)) {
                handleTryUsingAbility(model, entityID);
            }
        } else {
            timerComponent.startTimer(TimerComponent.WAIT_BEFORE_USING_ABILITY);
            if (!actionsComponent.hasFinishedUsingAbility() && timerComponent.hasElapsedTime(TimerComponent.WAIT_BEFORE_USING_ABILITY, waitTime)) {
                handleTryUsingAbility(model, entityID);
                if (actionsComponent.hasFinishedUsingAbility()) {
                    timerComponent.startTimer(TimerComponent.WAIT_BEFORE_USING_MOVE);
                }
            } else if (!actionsComponent.hasFinishedMoving() && timerComponent.hasElapsedTime(TimerComponent.WAIT_BEFORE_USING_MOVE, waitTime)) {
                handleMovement(model, entityID);
            }
        }
    }

    private void handleMovement(GameModel model, String entityID) {
        String tileToMoveToID = mRandomnessBehavior.toMoveTo(model, entityID);
        if (tileToMoveToID == null) {
            Entity entity = EntityStore.getInstance().get(entityID);
            ActionsComponent actionsComponentComponent = entity.get(ActionsComponent.class);
            actionsComponentComponent.setHasFinishedMoving(true);
            return;
        }

        mEventBus.publish(MovementSystem.MOVE_ENTITY_EVENT, MovementSystem.createMoveEntityEvent(
                entityID, tileToMoveToID, true
        ));
    }

    private void handleTryUsingAbility(GameModel model, String entityID) {
        Pair<String, String> abilityAndTileID = mRandomnessBehavior.toActOn(model, entityID);
        if (abilityAndTileID == null) {
            Entity entity = EntityStore.getInstance().get(entityID);
            ActionsComponent actionsComponentComponent = entity.get(ActionsComponent.class);
            actionsComponentComponent.setHasFinishedUsingAbility(true);
            return;
        }

        String ability = abilityAndTileID.getFirst();
        String tileID = abilityAndTileID.getSecond();
        mEventBus.publish(AbilitySystem.USE_ABILITY_EVENT, AbilitySystem.createUseAbilityEvent(
                entityID, ability, tileID, true
        ));
    }
}
