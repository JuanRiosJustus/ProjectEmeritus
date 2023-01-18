package game.systems.actions;

import constants.GameStateKey;
import engine.Engine;
import game.GameModel;
import game.components.ActionManager;
import game.components.MovementManager;
import game.components.MovementTrack;
import game.components.behaviors.AiBehavior;
import game.components.behaviors.UserBehavior;
import game.entity.Entity;

public class AiActionHandler extends ActionHandler {

//    private long lastMoved = Engine.instance().getUptime();

    public void handle(GameModel model, Entity unit) {
        // Gets tiles within movement range if the entity does not already have them...
        // these tiles should be removed after their turn is over
        getTilesWithinJumpAndMovementRange(model, unit);

        if (Engine.instance().getUptime() < 3) { return; } // start after 3 s
        if (unit.get(UserBehavior.class) != null) { return; }
        if (model.queue.peek() != unit) { return; }

        MovementTrack movementTrack = unit.get(MovementTrack.class);
        if (movementTrack.isMoving()) { return; }

        AiBehavior aiBehavior = unit.get(AiBehavior.class);
//        if (aiBehavior.slowlyStartTurn.elapsed() < 1) { return; }
        // if fast-forward is not selected, wait a second
        if (!model.state.getBoolean(GameStateKey.SETTINGS_UI_FASTFORWARDTURNS)) {
            double seconds = aiBehavior.actionDelay.elapsed();
            if (seconds < .5) { return; }
        }

//        lastMoved = Engine.instance().getUptime();
        ActionManager action = unit.get(ActionManager.class);
        MovementManager movement = unit.get(MovementManager.class);

        // potentially attack then move, or move then attack

        if (!movement.moved) {
            randomlyMove(model, unit);
//            moveTowardsEntityIfPossible(model, unit);
            movement.moved = true;
            aiBehavior.actionDelay.reset();
            return;
        }

        if (!action.acted) {
            randomlyAttack(model, unit);
            action.acted = true;
            aiBehavior.actionDelay.reset();
            return;
        }

        if (action.acted && movement.moved && !movementTrack.isMoving() &&
                model.state.getBoolean(GameStateKey.SETTINGS_UI_AUTOENDTURNS)) {
//            UpdateSystem.endTurn();
            model.system.endTurn();
        }
    }
}
