package game.systems;

import game.GameModel;
import game.components.behaviors.AiBehavior;
import game.components.behaviors.UserBehavior;
import game.entity.Entity;
import game.systems.actions.ActionHandler;
import input.InputController;


public class MoveActionSystem extends GameSystem {

    private final ActionHandler handler = new ActionHandler();

    public void update(GameModel model, Entity unit) {
        if (unit.get(UserBehavior.class) != null) {
            handler.handleUser(model, InputController.instance(), unit);
        } else if (unit.get(AiBehavior.class) != null) {
            handler.handleAi(model, unit);
        }
    }
}
