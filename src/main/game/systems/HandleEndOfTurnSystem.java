package main.game.systems;

import main.game.components.*;
import main.game.entity.Entity;
import main.game.main.GameModel;

public class HandleEndOfTurnSystem extends GameSystem {
    public HandleEndOfTurnSystem(GameModel gameModel) { super(gameModel); }
//    @Override
//    public void update(GameModel model, Entity unitEntity) {
//        TagComponent tagComponent = unitEntity.get(TagComponent.class);
//        model.getSpeedQueue().dequeue();
//        if (tagComponent.contains(TagComponent.YIELD)) {
//            model.getSpeedQueue().requeue(unitEntity);
//        }
//
//        Entity turnStarter = model.getSpeedQueue().peek();
//        if (turnStarter != null) {
//            model.mLogger.log(turnStarter.get(IdentityComponent.class) + "'s turn starts");
//        }
//
////        logger.info("Starting new Turn");
//
//        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
//        abilityComponent.reset();
//
//        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//        movementComponent.reset();
//
//        Behavior behavior = unitEntity.get(Behavior.class);
//        behavior.setIsSetup(false);
//
////        Tags tags = unit.get(Tags.class);
//        TagComponent.handleEndOfTurn(model, unitEntity);
//        tagComponent.reset();
//
////        Passives passives = unit.get(Passives.class);
////        if (passives.contains(Passives.MANA_REGEN_I)) {
////            Summary summary = unit.get(Summary.class);
////            int amount = summary.addTotalAmountToResource(Summary.MANA, .05f);
////            Animation animation = unit.get(Animation.class);
////            model.system.floatingText.floater("+" + amount + "EP", animation.getVector(), ColorPalette.WHITE);
////        }
//
//    }

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
        boolean isAI = aiComponent.isAI();

//        if (forcefullyEndTurn && !isAI) {
//            model.getSpeedQueue().dequeue();
//            if (isLockedOnActivityCamera) { model.focusCamerasAndSelectionsOfActiveEntity(); }
////            model.focusCamerasAndSelectionsOfActiveEntity();
//            model.getGameState().setShouldForceEndTurn(false);
//            actionsComponent.setHasFinishedSetup(false);
//            return;
//        }

        if (!forcefullyEndTurn) {
            if (isAI || shouldAutomaticallyEndUserTurn) {
                // If the unit has moved, were allowed to end the turn;
                if (!actionsComponent.hasFinishedMoving()) { return; }
                // If the unit has acted, were allowed to end the turn
                if (!actionsComponent.hasFinishedUsingAbility()) { return; }
                // if the unit is waiting for some reason, do not end the turn
                float unitWaitTimeBetweenActivities = model.getGameState().getUnitWaitTimeBetweenActivities();
                timerComponent.startTimer(TimerComponent.WAIT_BEFORE_ENDING_TURN);
                if (timerComponent.hasElapsedTime(TimerComponent.WAIT_BEFORE_ENDING_TURN, 3000)) { return; }
                // if the unit has pending animations, do not end the turn
                if (animationComponent.hasPendingAnimations()) { return; }
            }
        }



        if (isLockedOnActivityCamera) { model.focusCamerasAndSelectionsOfActiveEntity(); }
        model.getSpeedQueue().dequeue();
        model.getGameState().setShouldForcefullyEndTurn(false);
        if (isLockedOnActivityCamera) { model.focusCamerasAndSelectionsOfActiveEntity(); }

//        Entity turnStarter = model.getSpeedQueue().peek();
//        if (turnStarter != null) {
//            model.mLogger.log(turnStarter.get(IdentityComponent.class) + "'s turn starts");
//        }

//        logger.info("Starting new Turn");

//        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
//
////        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//
        actionsComponent = unitEntity.get(ActionsComponent.class);
        actionsComponent.reset();
        timerComponent.reset();
//
////        Tags tags = unit.get(Tags.class);
//        TagComponent.handleEndOfTurn(model, unitEntity);
//        tagComponent.reset();


//        Passives passives = unit.get(Passives.class);
//        if (passives.contains(Passives.MANA_REGEN_I)) {
//            Summary summary = unit.get(Summary.class);
//            int amount = summary.addTotalAmountToResource(Summary.MANA, .05f);
//            Animation animation = unit.get(Animation.class);
//            model.system.floatingText.floater("+" + amount + "EP", animation.getVector(), ColorPalette.WHITE);
//        }

    }
}
