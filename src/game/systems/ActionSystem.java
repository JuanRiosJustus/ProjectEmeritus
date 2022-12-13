package game.systems;

import engine.EngineController;
import game.components.behaviors.AIBehavior;
import game.components.behaviors.UserBehavior;
import game.entity.Entity;
import game.systems.actions.AiActionSystem;
import game.systems.actions.UserActionSystem;

public class ActionSystem {

    public static void update(EngineController engine, Entity unit) {
        if (unit.get(UserBehavior.class) != null) {
            UserActionSystem.handle(engine, unit);
        } else if (unit.get(AIBehavior.class) != null) {
            AiActionSystem.handle(engine, unit);
        }
    }
}
