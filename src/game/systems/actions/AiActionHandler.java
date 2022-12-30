package game.systems.actions;

import constants.Constants;
import engine.Engine;
import game.GameModel;
import game.components.ActionManager;
import game.components.Movement;
import game.components.behaviors.AiBehavior;
import game.components.behaviors.UserBehavior;
import game.entity.Entity;

import static game.systems.actions.ActionHandler.*;

public class AiActionHandler {

//    private long lastMoved = Engine.instance().getUptime();

    public void handle(GameModel model, Entity unit) {
        if (Engine.instance().getUptime() < 3) { return; } // start after 3 s
        if (unit.get(UserBehavior.class) != null) { return; }
        if (model.queue.peek() != unit) { return; }

        Movement movement = unit.get(Movement.class);
        if (movement.isMoving()) { return; }

        AiBehavior aiBehavior = unit.get(AiBehavior.class);
        // if fast-forward is not selected, wait a second
        if (!model.ui.getBoolean(Constants.SETTINGS_UI_FASTFORWARDTURNS)) {
            double seconds = aiBehavior.actionDelay.elapsed();
            if (seconds < 1) {
                return;
            }
//            if (Engine.instance().getUptime() - lastMoved < 1) {
//                return;
//            }
        }

//        lastMoved = Engine.instance().getUptime();
        ActionManager action = unit.get(ActionManager.class);

        // potentially attack then move, or move then attack

//        if (!action.moved) { randomlyMove(engine, unit); moveTowardsEntityElseRandom(engine, unit); }

        if (!action.moved) {
            moveTowardsEntityIfPossible(model, unit);
            action.moved = true;
            aiBehavior.actionDelay.reset();
            return;
        }

        if (!action.attacked) {
            randomlyAttack(model, unit);
            action.attacked = true;
            aiBehavior.actionDelay.reset();
            return;
        }

        if (action.attacked && action.moved && !movement.isMoving() &&
                model.ui.getBoolean(Constants.SETTINGS_UI_AUTOENDTURNS)) {
//            UpdateSystem.endTurn();
            model.system.endTurn();
        }
    }
}
