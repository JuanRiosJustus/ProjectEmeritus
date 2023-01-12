package game.systems.actions;

import constants.Constants;
import game.GameModel;
import game.components.ActionManager;
import game.components.MovementTrack;
import game.components.Tile;
import game.components.statistics.Statistics;
import game.entity.Entity;
import game.pathfinding.TilePathing;

import java.util.Optional;
import java.util.Set;

public class AggressivelyAttackIfPossible {

    public static void move(GameModel model, Entity unit) {
//        MovementTrack movementTrack = unit.get(MovementTrack.class);
//        if (movementTrack.isMoving()) { return; } // ensure not currently acting
//        Statistics stats = unit.get(Statistics.class);
//        ActionManager manager = unit.get(ActionManager.class);
//
//        Set<Entity> tilesWithinLOS = manager.tilesWithinMovementRange;
//
//        // Get tiles within the movement range
//        TilePathing.getUnobstructedTilePath(model, manager.tileOccupying,
//                stats.getScalarNode(Constants.MOVE).getTotal() + 3, tilesWithinLOS);
//
//        // check if there are any units we want to move closer to so that we can attack them
//        Optional<Entity> tilesWithinEntities = tilesWithinLOS.stream()
//                .filter(entity -> entity.get(Tile.class).unit != null)
//                .filter(entity -> entity.get(Tile.class).unit != unit)
//                .findFirst();
//
//        if (tilesWithinEntities.isPresent()) {
//            // We have someone we want to try and attack
//        } else {
//            // There are no units we can or should attack, move randomly
//            ActionHandler.randomlyMove(model, unit);
//        }
    }

    public static void attack() {

    }
}
