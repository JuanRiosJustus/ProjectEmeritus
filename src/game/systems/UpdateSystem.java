package game.systems;


import constants.Constants;
import game.GameModel;
import game.components.*;
import game.entity.Entity;
import game.stores.factories.UnitFactory;
import input.InputController;
import logging.Logger;
import logging.LoggerFactory;

import java.util.List;

public class UpdateSystem {

    private static boolean endTurn = false;
    private static boolean lockOn = true;
    private static final Logger logger = LoggerFactory.instance().logger(UpdateSystem.class);

    public static void update(GameModel model, InputController controller) {

        // update all entities
        List<Entity> units = UnitFactory.list;
        for (Entity unit : units) {
            ActionSystem.update(model, controller, unit);
            SpriteAnimationSystem.update(model, unit);
            CombatSystem.update(model, unit);
            CombatAnimationSystem.update(model, unit);
        }

        Entity current = model.queue.peek();

        UiSystem.update(model, current);
        FloatingTextSystem.update(model, current);

        if (model.ui.getBoolean(Constants.ACTIONS_UI_ENDTURN)) {
            endTurn();
            model.ui.set(Constants.ACTIONS_UI_ENDTURN, false);
        }

        if (endTurn) {
            endTurn(model, current);
            if (current != null) { StatusEffectSystem.update(model, current); }
        }

        model.queue.update();
    }

    public static void endTurn() {
        endTurn = true;
    }

    private static void endTurn(GameModel model, Entity unit) {
        model.queue.dequeue();
//        engine.model.ui.order.dequeue();
//        engine.model.ui.exitToMain();

        logger.log("Starting new turn -> " + model.queue);

        // update the unit
        if (unit == null) { return; }

        ActionManager manager = unit.get(ActionManager.class);
        manager.reset();

        endTurn = false;
    }
}
