package game.systems.actions;

import engine.Engine;
import engine.EngineController;
import game.components.ActionManager;
import game.components.Movement;
import game.components.behaviors.UserBehavior;
import game.entity.Entity;
import game.systems.UpdateSystem;

import static game.systems.actions.ActionUtils.*;

public class AiActionSystem {

    private static long lastMoved = Engine.get().getUptime();

    public static void handle(EngineController engine, Entity unit) {
        if (Engine.get().getUptime() < 3) { return; } // start after 3 s
        if (unit.get(UserBehavior.class) != null) { return; }
        if (engine.model.game.model.queue.peek() != unit) { return; }

        Movement movement = unit.get(Movement.class);
        if (movement.isMoving()) { return; }

        // if fast-forward is not selected, wait a second
        if (!engine.model.ui.settings.fastForward.isSelected()) {
            if (Engine.get().getUptime() - lastMoved < 1) {
                return;
            }
        }

        lastMoved = Engine.get().getUptime();
        ActionManager action = unit.get(ActionManager.class);

        // potentially attack then move, or move then attack

//        if (!action.moved) { randomlyMove(engine, unit); moveTowardsEntityElseRandom(engine, unit); }

        if (!action.moved) {  moveTowardsEntityIfPossible(engine, unit); }

        if (!action.attacked) { randomlyAttack(engine, unit); }

        if (action.attacked && action.moved && !movement.isMoving() &&
                engine.model.ui.settings.autoEndTurns.isSelected()) {
            UpdateSystem.endTurn();
        }
    }
}
