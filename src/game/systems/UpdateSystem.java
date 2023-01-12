package game.systems;


import constants.Constants;
import game.GameModel;
import game.components.*;
import game.entity.Entity;
import game.stores.factories.UnitFactory;
import logging.Logger;
import logging.LoggerFactory;

import java.util.List;

public class UpdateSystem {

    public UpdateSystem() { }
    private boolean endTurn = false;
    private boolean lockOn = true;
    private final Logger logger = LoggerFactory.instance().logger(getClass());
    public final MoveActionSystem action = new MoveActionSystem();
    public final SpriteAnimationSystem spriteAnimation = new SpriteAnimationSystem();
    public final CombatAnimationSystem combatAnimation = new CombatAnimationSystem();
    public final CombatSystem combat = new CombatSystem();
    public final FloatingTextSystem floatingText = new FloatingTextSystem();
    public final StatusEffectSystem statusEffect = new StatusEffectSystem();
    public final CollectibleSpawnerSystem collectibleSpawnerSystem = new CollectibleSpawnerSystem();

    public void update(GameModel model) {
        // update all entities
        List<Entity> units = UnitFactory.list;
        for (Entity unit : units) {
            action.update(model, unit);
            spriteAnimation.update(model, unit);
            combat.update(model, unit);
            combatAnimation.update(model, unit);
        }

        Entity current = model.queue.peek();

        floatingText.update(model, current);

        if (model.state.getBoolean(Constants.ACTIONS_UI_ENDTURN)) {
            endTurn();
            model.state.set(Constants.ACTIONS_UI_ENDTURN, false);
            model.state.set(Constants.RESET_UI, true);
        }

        if (endTurn) {
            endTurn(model, current);
            if (current != null) { statusEffect.update(model, current); }
        }

        model.queue.update();
    }

    public void endTurn() {
        endTurn = true;
    }

    private void endTurn(GameModel model, Entity unit) {
        model.queue.dequeue();
//        engine.model.ui.order.dequeue();
//        engine.model.ui.exitToMain();

        logger.log("Starting new turn -> " + model.queue);

        // update the unit
        if (unit == null) { return; }

        ActionManager action = unit.get(ActionManager.class);
        action.reset();
        MovementManager movement = unit.get(MovementManager.class);
        movement.reset();

        endTurn = false;
        collectibleSpawnerSystem.update(model, unit);
    }

//
//
//    private static boolean endTurn = false;
//    private static boolean lockOn = true;
//    private static final Logger logger = LoggerFactory.instance().logger(UpdateSystem.class);
//
//    public static void update(GameModel model, InputController controller) {
//
//        // update all entities
//        List<Entity> units = UnitFactory.list;
//        for (Entity unit : units) {
//            ActionSystem.update(model, controller, unit);
//            SpriteAnimationSystem.update(model, unit);
//            CombatSystem.update(model, unit);
//            CombatAnimationSystem.update(model, unit);
//        }
//
//        BuffSpawnerSystem.update(GameModel model);
//        Entity current = model.queue.peek();
//
//        UiSystem.update(model, current);
//        FloatingTextSystem.update(model, current);
//
//        if (model.ui.getBoolean(Constants.ACTIONS_UI_ENDTURN)) {
//            endTurn();
//            model.ui.set(Constants.ACTIONS_UI_ENDTURN, false);
//        }
//
//        if (endTurn) {
//            endTurn(model, current);
//            if (current != null) { StatusEffectSystem.update(model, current); }
//        }
//
//        model.queue.update();
//    }

//    public static void endTurn() {
//        endTurn = true;
//    }
//
//    private static void endTurn(GameModel model, Entity unit) {
//        model.queue.dequeue();
////        engine.model.ui.order.dequeue();
////        engine.model.ui.exitToMain();
//
//        logger.log("Starting new turn -> " + model.queue);
//
//        // update the unit
//        if (unit == null) { return; }
//
//        ActionManager manager = unit.get(ActionManager.class);
//        manager.reset();
//
//        endTurn = false;
//    }
}
