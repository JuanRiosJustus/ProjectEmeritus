package game.systems.actions;

import constants.Constants;
import engine.EngineController;
import game.components.ActionManager;
import game.components.Movement;
import game.components.Tile;
import game.components.statistics.Statistics;
import game.entity.Entity;
import game.pathfinding.TilePathing;

import java.util.Optional;
import java.util.Set;

public class AggressivelyAttackIfPossible {

    public static void move(EngineController engine, Entity unit) {
        Movement movement = unit.get(Movement.class);
        if (movement.isMoving()) { return; } // ensure not currently acting
        Statistics stats = unit.get(Statistics.class);
        ActionManager manager = unit.get(ActionManager.class);

        Set<Entity> tilesWithinLOS = manager.tilesWithinMovementRange;

        // Get tiles within the movement range
        TilePathing.getUnobstructedTilePath(engine.model.game.model, manager.tileOccupying,
                stats.getScalarNode(Constants.DISTANCE).getTotal() + 3, tilesWithinLOS);

        // check if there are any units we want to move closer to so that we can attack them
        Optional<Entity> tilesWithinEntities = tilesWithinLOS.stream()
                .filter(entity -> entity.get(Tile.class).unit != null)
                .filter(entity -> entity.get(Tile.class).unit != unit)
                .findFirst();

        if (tilesWithinEntities.isPresent()) {
            // We have someone we want to try and attack
        } else {
            // There are no units we can or should attack, move randomly
            ActionUtils.randomlyMove(engine, unit);
        }
    }

    public static void attack() {

    }
}
