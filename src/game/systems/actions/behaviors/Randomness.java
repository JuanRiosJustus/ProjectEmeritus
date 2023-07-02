package game.systems.actions.behaviors;

import game.components.MovementManager;
import game.entity.Entity;
import game.main.GameModel;
import logging.Logger;
import logging.LoggerFactory;

public class Randomness extends Behavior {
    private final Logger logger = LoggerFactory.instance().logger(getClass());

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
