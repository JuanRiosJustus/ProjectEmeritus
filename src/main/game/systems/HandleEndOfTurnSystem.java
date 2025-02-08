package main.game.systems;

import main.game.components.AbilityComponent;
import main.game.components.IdentityComponent;
import main.game.components.MovementComponent;
import main.game.components.TagComponent;
import main.game.components.behaviors.Behavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;

public class HandleEndOfTurnSystem extends GameSystem{
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
    public void update(GameModel model, String id) {
        Entity unitEntity = getEntityWithID(id);
        TagComponent tagComponent = unitEntity.get(TagComponent.class);
        model.getSpeedQueue().dequeue();
        if (tagComponent.contains(TagComponent.YIELD)) {
            model.getSpeedQueue().requeue(unitEntity);
        }

        Entity turnStarter = model.getSpeedQueue().peek();
        if (turnStarter != null) {
            model.mLogger.log(turnStarter.get(IdentityComponent.class) + "'s turn starts");
        }

//        logger.info("Starting new Turn");

        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
        abilityComponent.reset();

        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        movementComponent.reset();

        Behavior behavior = unitEntity.get(Behavior.class);
        behavior.setIsSetup(false);

//        Tags tags = unit.get(Tags.class);
        TagComponent.handleEndOfTurn(model, unitEntity);
        tagComponent.reset();

//        Passives passives = unit.get(Passives.class);
//        if (passives.contains(Passives.MANA_REGEN_I)) {
//            Summary summary = unit.get(Summary.class);
//            int amount = summary.addTotalAmountToResource(Summary.MANA, .05f);
//            Animation animation = unit.get(Animation.class);
//            model.system.floatingText.floater("+" + amount + "EP", animation.getVector(), ColorPalette.WHITE);
//        }

    }
}
