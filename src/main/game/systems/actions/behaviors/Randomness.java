package main.game.systems.actions.behaviors;

import main.game.components.MovementManager;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class Randomness extends Behavior {
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    public void move(GameModel model, Entity unit) {
        // Go through all of the possible tiles that can be moved to
//        model.uiLogQueue.add(unit + " randomly moves");
        utils.randomlyMove(model, unit);
    }

    public void attack(GameModel model, Entity unit) {
//        model.uiLogQueue.add(unit + " randomly attacks");
//        utils.randomlyAttack(model, unit);
    }
}
