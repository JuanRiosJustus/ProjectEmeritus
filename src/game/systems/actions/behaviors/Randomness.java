package game.systems.actions.behaviors;

import game.components.MovementManager;
import game.entity.Entity;
import game.main.GameModel;
import logging.ELogger;
import logging.ELoggerFactory;

public class Randomness extends Behavior {
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    public void move(GameModel model, Entity unit) {
        // Go through all of the possible tiles that can be moved to
        model.uiLogQueue.add(unit + " randomly moves");
        utils.randomlyMove(model, unit);
    }

    public void attack(GameModel model, Entity unit) {
        model.uiLogQueue.add(unit + " randomly attacks");
        utils.randomlyAttack(model, unit);
    }
}
