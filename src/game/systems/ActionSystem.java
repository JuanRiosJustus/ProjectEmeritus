package game.systems;

import engine.EngineController;
import game.GameModel;
import game.components.behaviors.AIBehavior;
import game.components.behaviors.UserBehavior;
import game.entity.Entity;
import game.systems.actions.AiActionSystem;
import game.systems.actions.UserActionSystem;
import input.InputController;

public class ActionSystem {
    public static void update(GameModel model, InputController controller, Entity unit) {
        if (unit.get(UserBehavior.class) != null) {
            UserActionSystem.handle(model, controller, unit);
        } else if (unit.get(AIBehavior.class) != null) {
            AiActionSystem.handle(model, unit);
        }
    }
}
