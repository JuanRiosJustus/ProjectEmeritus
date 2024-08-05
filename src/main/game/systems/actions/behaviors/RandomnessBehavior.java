package main.game.systems.actions.behaviors;

import main.game.components.MovementManager;
import main.game.components.Statistics;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.PathBuilder;

import java.util.Set;

public class RandomnessBehavior extends Behavior {

    public void move(GameModel model, Entity unitEntity) {
        Statistics statistics = unitEntity.get(Statistics.class);
        MovementManager movementManager = unitEntity.get(MovementManager.class);
        Set<Entity> tilesWithinWalkingDistance = PathBuilder.newBuilder().inMovementRange(
                model,
                movementManager.currentTile,
                statistics.getStatTotal(Statistics.MOVE),
                statistics.getStatTotal(Statistics.CLIMB)
        );

        if (tilesWithinWalkingDistance.isEmpty()) {
            return;
        }
        Entity tileEntity = tilesWithinWalkingDistance.iterator().next();
        movementSystem.move(model, unitEntity, tileEntity, true);


//        // Go through all the possible tiles that can be moved to
//        MovementTrack track = unit.get(MovementTrack.class);
//        if (track.isMoving()) { return; } // ensure not currently acting
//        MovementManager movementManager = unit.get(MovementManager.class);
//
//        // Get tiles within the movement range
//        MovementManager.move(model, unit, null, false);
//
//        // select a random tile to move to
//        List<Entity> candidates = movementManager.tilesInRange.stream().toList();
//        Entity randomTile = candidates.get(random.nextInt(candidates.size()));
//
//        // if the random tile is current tile, don't move (Moving to same tile causes exception in animation track)
//        if (randomTile != movementManager.currentTile) {
//            // regather tiles=
//            MovementManager.move(model, unit, randomTile, true);
//        }
//        movementManager.moved = true;
    }

    public void attack(GameModel model, Entity unitEntity) {

        Statistics statistics = unitEntity.get(Statistics.class);
        MovementManager movementManager = unitEntity.get(MovementManager.class);
        Set<Entity> tilesWithinWalkingDistance = PathBuilder.newBuilder().inMovementRange(
                model,
                movementManager.currentTile,
                statistics.getStatTotal(Statistics.MOVE),
                statistics.getStatTotal(Statistics.CLIMB)
        );
    }
}
