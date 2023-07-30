package main.game.systems;

import main.game.components.behaviors.AiBehavior;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.queue.SpeedQueue;
import main.game.systems.actions.ActionHandler;
import main.input.InputController;


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
