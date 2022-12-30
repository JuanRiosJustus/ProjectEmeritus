package game.systems;

import game.GameModel;
import game.components.behaviors.AiBehavior;
import game.components.behaviors.UserBehavior;
import game.entity.Entity;
import game.systems.actions.AiActionHandler;
import game.systems.actions.UserActionHandler;
import input.InputController;

public class ActionSystem extends GameSystem {

    private final UserActionHandler userHandler = new UserActionHandler();
    private final AiActionHandler aiHandler = new AiActionHandler();

    public void update(GameModel model, Entity unit) {
        if (unit.get(UserBehavior.class) != null) {
            userHandler.handle(model, InputController.instance(), unit);
        } else if (unit.get(AiBehavior.class) != null) {
            aiHandler.handle(model, unit);
        }
    }
}
