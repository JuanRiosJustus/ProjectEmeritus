package game.systems;


import engine.EngineController;
import game.components.*;
import game.entity.Entity;
import game.stores.factories.UnitFactory;
import logging.Logger;
import logging.LoggerFactory;

import java.util.List;

public class UpdateSystem {

    private static boolean endTurn = false;
    private static boolean lockOn = true;
    private static final Logger logger = LoggerFactory.instance().logger(UpdateSystem.class);

    public static void update(EngineController engine) {

        // update all entities
        List<Entity> units = UnitFactory.list;
        for (Entity unit : units) {
            ActionSystem.update(engine, unit);
            SpriteAnimationSystem.update(engine, unit);
            CombatSystem.update(engine, unit);
            CombatAnimationSystem.update(engine, unit);
        }

        Entity current = engine.model.game.model.queue.peek();

        UiSystem.update(engine, current);
        FloatingTextSystem.update(engine, current);

        if (engine.model.ui.actions.endTurnToggleButton.isSelected()) {
            endTurn();
            engine.model.ui.actions.endTurnToggleButton.setSelected(false);
        }

        if (endTurn) {
            endTurn(engine, current);
            if (current != null) { StatusEffectSystem.update(engine, current); }
        }

        engine.model.game.model.queue.update();
    }

    public static void endTurn() {
        endTurn = true;
    }

    private static void endTurn(EngineController engine, Entity unit) {
        engine.model.game.model.queue.dequeue();
        engine.model.ui.order.dequeue();
//        engine.model.ui.exitToMain();

        logger.log("Starting new turn -> " + engine.model.game.model.queue.toString());

        // update the unit
        if (unit == null) { return; }

        ActionManager manager = unit.get(ActionManager.class);
        manager.reset();

        endTurn = false;
    }
}
