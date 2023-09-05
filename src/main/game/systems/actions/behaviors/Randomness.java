package main.game.systems.actions.behaviors;

import main.game.components.MovementManager;
import main.game.components.Track;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.List;

public class Randomness extends Behavior {

    public void move(GameModel model, Entity unit) {
        // Go through all the possible tiles that can be moved to
        Track track = unit.get(Track.class);
        if (track.isMoving()) { return; } // ensure not currently acting
        MovementManager movementManager = unit.get(MovementManager.class);

        // Get tiles within the movement range
        MovementManager.move(model, unit, null, false);

        // select a random tile to move to
        List<Entity> candidates = movementManager.range.stream().toList();
        Entity randomTile = candidates.get(random.nextInt(candidates.size()));

        // if the random tile is current tile, don't move (Moving to same tile causes exception in animation track)
        if (randomTile != movementManager.currentTile) {
            // regather tiles=
            MovementManager.move(model, unit, randomTile, true);
        }
        movementManager.moved = true;
    }

    public void attack(GameModel model, Entity unit) {

//        model.uiLogQueue.add(unit + " randomly attacks");
//        utils.randomlyAttack(model, unit);
    }
}
