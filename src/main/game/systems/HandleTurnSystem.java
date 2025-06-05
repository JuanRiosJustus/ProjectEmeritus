package main.game.systems;

import main.game.components.*;
import main.game.entity.Entity;
import main.game.main.GameModel;

public class HandleTurnSystem extends GameSystem {
//    private String mCurrentTurn
    public HandleTurnSystem(GameModel gameModel) { super(gameModel); }

    @Override
    public void update(GameModel model, SystemContext systemContext) {

        String currentUnitID = systemContext.getCurrentUnitID();
        Entity unitEntity = getEntityWithID(currentUnitID);

        if (unitEntity == null) { return; }


        TimerComponent timerComponent = unitEntity.get(TimerComponent.class);
        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        ActionsComponent actionsComponent = unitEntity.get(ActionsComponent.class);
        AIComponent aiComponent = unitEntity.get(AIComponent.class);
        boolean isLockedOnActivityCamera = model.getGameState().isLockOnActivityCamera();
        boolean forcefullyEndTurn = model.getGameState().shouldForcefullyEndTurn();
        boolean shouldAutomaticallyEndUserTurn = model.getGameState().shouldAutomaticallyEndUserTurn();
        boolean shouldAutomaticallyEndCpusTurn = model.getGameState().shouldAutomaticallyEndCpusTurn();
        boolean isUserSelectedStandby = model.isUserSelectedStandby();
        boolean isAI = aiComponent.isAI();


        if (!forcefullyEndTurn) {
            if (isAI) {
                // If the unit has moved, were allowed to end the turn;
                if (!actionsComponent.hasFinishedMoving()) { return; }
                // If the unit has acted, were allowed to end the turn
                if (!actionsComponent.hasFinishedUsingAbility()) { return; }
                // If the CPU should automatically end its turn
                if (!shouldAutomaticallyEndCpusTurn) { return; }
                // if the unit is waiting for some reason, do not end the turn
                float unitWaitTimeBetweenActivities = model.getGameState().getUnitWaitTimeBetweenActivities();
                timerComponent.startTimer(TimerComponent.WAIT_BEFORE_ENDING_TURN);
                if (timerComponent.hasElapsedTime(TimerComponent.WAIT_BEFORE_ENDING_TURN, 3000)) { return; }
                // if the unit has pending animations, do not end the turn
                if (animationComponent.hasPendingAnimations()) { return; }
            } else if (!isUserSelectedStandby) {
                return;
            }
        }


        if (isLockedOnActivityCamera) { model.focusCamerasAndSelectionsOfActiveEntity(); }
//        timerComponent.endTurnTimer();

        // Setup new unit details
        model.getSpeedQueue().dequeue();
//        model.getInitiativeQueue().dequeue();
        model.getGameState().setShouldForcefullyEndTurn(false);
        model.focusCamerasAndSelectionsOfActiveEntity();
        model.getGameState().setAutomaticallyGoToHomeControls(true);
        model.getGameState().setUserSelectedStandby(false);
        System.out.println("tooooo");

        actionsComponent = unitEntity.get(ActionsComponent.class);
        actionsComponent.reset();
        timerComponent.reset();
//        timerComponent.startTurnTimer();
//        model.handleTurnTransition();
    }
}
