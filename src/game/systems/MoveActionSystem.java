package game.systems;

import game.components.behaviors.AiBehavior;
import game.components.behaviors.UserBehavior;
import game.entity.Entity;
import game.main.GameModel;
import game.queue.SpeedQueue;
import game.systems.actions.ActionHandler;
import input.InputController;


public class MoveActionSystem extends GameSystem {

    private final ActionHandler handler = new ActionHandler();

    public void update(GameModel model, Entity unit) {
        // Nothing to handle if not the units turn
        SpeedQueue queue = model.speedQueue;
        if (queue.peek() != unit) { return; }
        
        // Handle user and AI seperately
        if (unit.get(UserBehavior.class) != null) {
            handler.handleUser(model, InputController.instance(), unit);
        } else if (unit.get(AiBehavior.class) != null) {
            handler.handleAi(model, unit);
        }
    }
}
