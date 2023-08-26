package main.game.systems.actions.behaviors;

import main.game.components.Movement;
import main.game.components.Track;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.List;

public class Randomness extends Behavior {

    public void move(GameModel model, Entity unit) {
        // Go through all the possible tiles that can be moved to
        Track track = unit.get(Track.class);
        if (track.isMoving()) { return; } // ensure not currently acting
        Movement movement = unit.get(Movement.class);

        // Get tiles within the movement range
        Movement.move(model, unit, null, false);

        // select a random tile to move to
        List<Entity> candidates = movement.range.stream().toList();
        Entity randomTile = candidates.get(random.nextInt(candidates.size()));

        // if the random tile is current tile, don't move (Moving to same tile causes exception in animation track)
        if (randomTile != movement.currentTile) {
            // regather tiles=
            Movement.move(model, unit, randomTile, true);
        }
        movement.moved = true;
    }

    public void attack(GameModel model, Entity unit) {

//        model.uiLogQueue.add(unit + " randomly attacks");
//        utils.randomlyAttack(model, unit);
    }
}
