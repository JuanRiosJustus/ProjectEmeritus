package main.game.systems.actions.behaviors;

import main.constants.Pair;
import main.constants.csv.CsvRow;
import main.game.components.MovementComponent;
import main.game.components.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.PathBuilder;
import main.game.stores.pools.action.ActionPool;

import java.util.*;

public class RandomnessBehavior extends MoveActionBehavior {

    private final int mRandomnessVisionRange = 6;
    @Override
    public Entity toMoveTo(GameModel model, Entity unitEntity) {
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);

        // Get all tiles that can be walked to
        Set<Entity> tilesWithinWalkingDistance = PathBuilder.newBuilder().getMovementRange(
                model,
                movementComponent.getCurrentTile(),
                statisticsComponent.getTotal(StatisticsComponent.MOVE),
                statisticsComponent.getTotal(StatisticsComponent.CLIMB)
        );

        // Move to a random tile
        tilesWithinWalkingDistance.remove(movementComponent.getCurrentTile());
        List<Entity> tilesToMoveTo = new ArrayList<>(tilesWithinWalkingDistance);
        if (tilesToMoveTo.isEmpty()) {
            return null;
        }
        return tilesToMoveTo.get(mRandom.nextInt(tilesToMoveTo.size()));
    }

    @Override
    public Pair<Entity, String> toActOn(GameModel model, Entity unitEntity) {
        // Get try selecting an ability randomly
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        List<String> damagingActions = new ArrayList<>(
                statisticsComponent.getActions()
                .stream()
                .filter(action -> ActionPool.getInstance().isDamagingAbility(action))
                .toList()
        );
        Collections.shuffle(damagingActions);

        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity currentTile = movementComponent.getCurrentTile();

        for (String action : damagingActions) {

            int range = ActionPool.getInstance().getRange(action);
            Set<Entity> tilesWithinActionRange = mPathBuilder.getTilesInRange(model, currentTile, range);
            // Check what happens when we focus on one of the tiles
            for (Entity tileEntity : tilesWithinActionRange) {
                // if the current tile has an entity of different faction/team, target
                int area = ActionPool.getInstance().getArea(action);
                Set<Entity> tilesWithinActionAreaOfEffect = mPathBuilder.getTilesInRange(model, tileEntity, area);
                // Check all the units within tilesWithinActionRange and tilesWithinAreaOfEffect
                List<Entity> tilesWithUnits = tilesWithinActionAreaOfEffect.stream()
                        .filter(tileWithPotentialUnit -> tileWithPotentialUnit.get(Tile.class).getUnit() != null)
                        .filter(tileWithUnit -> {
                            Tile inspectedTile = tileWithUnit.get(Tile.class);
                            Entity unitOnTile = inspectedTile.getUnit();
                            return unitOnTile != unitEntity;
//                            return !model.getSpeedQueue().isOnSameTeam(unitEntity, unitOnTile);
//                            return true;
                        }).toList();

                // Attack one of the units randomly
                if (tilesWithUnits.isEmpty()) { continue; }
                Entity randomTileWithUnit = tilesWithUnits.get(mRandom.nextInt(tilesWithUnits.size()));
                return new Pair<>(randomTileWithUnit, action);
            }
        }
        return null;
    }
}