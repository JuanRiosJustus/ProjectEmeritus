package main.game.systems;

import main.game.components.ActionComponent;
import main.game.components.IdentityComponent;
import main.game.components.MovementComponent;
import main.game.components.TagComponent;
import main.game.components.behaviors.AiBehavior;
import main.game.components.behaviors.Behavior;
import main.game.entity.Entity;
import main.game.main.GameModel;

public class HandleEndOfTurnSystem extends GameSystem{
    @Override
    public void update(GameModel model, Entity unitEntity) {
        TagComponent tagComponent = unitEntity.get(TagComponent.class);
        model.mSpeedQueue.dequeue();
        if (tagComponent.contains(TagComponent.YIELD)) {
            model.mSpeedQueue.requeue(unitEntity);
        }

        Entity turnStarter = model.mSpeedQueue.peek();
        if (turnStarter != null) {
            model.mLogger.log(turnStarter.get(IdentityComponent.class) + "'s turn starts");
        }

//        logger.info("Starting new Turn");

        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        actionComponent.reset();

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