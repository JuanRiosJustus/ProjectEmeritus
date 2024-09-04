package main.game.systems.actions.behaviors;

import main.game.components.MovementComponent;
import main.game.components.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.PathBuilder;

import java.util.Set;
import java.util.stream.Collectors;

public class BehaviorLibrary {

    private int mUnitDefaultVisionRange = 9;
    public void shouldMoveBeforeActing(GameModel model, Entity unitEntity) {
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Set<Entity> tilesInVisionRange = PathBuilder.newBuilder().inVisionRange(model,
                movementComponent.getCurrentTile(),
                mUnitDefaultVisionRange
        );

        // Check for units within vision range
        Set<Entity> tilesWithUnitsOnThem = tilesInVisionRange.stream()
                .filter(tileEntity -> tileEntity.get(Tile.class).getUnit() != null)
                .collect(Collectors.toSet());


//        model.getCamera()
        // Prioritize who to target. If
        Set<Entity> tilesWithNonTeamUnits = null;
    }
}
