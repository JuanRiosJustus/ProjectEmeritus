package main.game.systemleftovers.actions.behaviors;

import main.constants.Pair;
import main.game.components.IdentityComponent;
import main.game.components.MovementComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;
import main.game.stores.pools.AbilityDatabase;

import java.util.*;

public class RandomnessBehavior extends MoveActionBehavior {

    private final int mRandomnessVisionRange = 6;
    @Override
    public Entity toMoveTo(GameModel model, Entity unitEntity) {
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);

        // Get all tiles that can be walked to
        Set<Entity> tilesWithinWalkingDistance = mAlgorithm.computeMovementArea(
                model,
                movementComponent.getCurrentTileV1(),
                statisticsComponent.getTotalMovement()
        );

        // Move to a random tile
        tilesWithinWalkingDistance.remove(movementComponent.getCurrentTileV1());
        List<Entity> tilesToMoveTo = new ArrayList<>(tilesWithinWalkingDistance);
        if (tilesToMoveTo.isEmpty()) {
            return null;
        }
        return tilesToMoveTo.get(mRandom.nextInt(tilesToMoveTo.size()));
    }

    public String toMoveTo(GameModel model, String unitID) {
        Entity unitEntity = EntityStore.getInstance().get(unitID);
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        String currentTileID = movementComponent.getCurrentTileID();
        Entity currentTileEntity = EntityStore.getInstance().get(currentTileID);

        // Get all tiles that can be walked to
        Set<Entity> tilesWithinWalkingDistance = mAlgorithm.computeMovementArea(
                model, currentTileEntity,
                statisticsComponent.getTotalMovement()
        );

        // Move to a random tile
        tilesWithinWalkingDistance.remove(movementComponent.getCurrentTileV1());
        List<Entity> tilesToMoveTo = new ArrayList<>(tilesWithinWalkingDistance);
        if (tilesToMoveTo.isEmpty()) {
            return null;
        }
        Entity tileToMoveTo = tilesToMoveTo.get(mRandom.nextInt(tilesToMoveTo.size()));
        IdentityComponent identityComponent = tileToMoveTo.get(IdentityComponent.class);
        return identityComponent.getID();
    }

    public Pair<String, String> toActOnV2(GameModel model, Entity unitEntity) {
        // Get try selecting an ability randomly
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        List<String> damagingActions = new ArrayList<>(
                statisticsComponent.getAbilities()
                        .stream()
                        .filter(action -> AbilityDatabase.getInstance().isDamagingAbility(action))
                        .toList()
        );
        Collections.shuffle(damagingActions);

        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity currentTile = movementComponent.getCurrentTileV1();

        if (true) { return null; }
        for (String action : damagingActions) {

            int range = AbilityDatabase.getInstance().getRange(action);
            Set<Entity> tilesWithinActionRange = mAlgorithm.computeAreaOfSight(model, currentTile, range);
            // Check what happens when we focus on one of the tiles
            for (Entity tileEntity : tilesWithinActionRange) {
                // if the current tile has an entity of different faction/team, target
                int area = AbilityDatabase.getInstance().getArea(action);
                Set<Entity> tilesWithinActionAreaOfEffect = mAlgorithm.computeAreaOfSight(model, tileEntity, area);
                // Check all the units within tilesWithinActionRange and tilesWithinAreaOfEffect
//                List<Entity> tilesWithUnits = new ArrayList<>();
                List<Entity> tilesWithUnits = tilesWithinActionAreaOfEffect.stream()
                        .filter(tileWithPotentialUnit -> tileWithPotentialUnit.get(Tile.class).getUnitID() != null)
                        .filter(tileWithPotentialUnit -> !tileWithPotentialUnit.get(Tile.class).getUnitID().isBlank())
                        .filter(tileWithUnit -> {
                            Tile inspectedTile = tileWithUnit.get(Tile.class);
                            String unitEntityID = inspectedTile.getUnitID();
                            Entity unitOnTile = EntityStore.getInstance().get(unitEntityID);
                            return unitOnTile != unitEntity;
//                            return !model.getSpeedQueue().isOnSameTeam(unitEntity, unitOnTile);
//                            return true;
                        }).toList();

                // Attack one of the units randomly
                if (tilesWithUnits.isEmpty()) { continue; }
                Entity randomTileWithUnit = tilesWithUnits.get(mRandom.nextInt(tilesWithUnits.size()));
                IdentityComponent randomTileIdentityComponent = randomTileWithUnit.get(IdentityComponent.class);
                String randomTileID = randomTileIdentityComponent.getID();
                return new Pair<>(randomTileID, action);
            }
        }
        return null;
    }

    @Override
    public Pair<Entity, String> toActOn(GameModel model, Entity unitEntity) {
        // Get try selecting an ability randomly
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        List<String> damagingActions = new ArrayList<>(
                statisticsComponent.getAbilities()
                .stream()
                .filter(action -> AbilityDatabase.getInstance().isDamagingAbility(action))
                .toList()
        );
        Collections.shuffle(damagingActions);

        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity currentTile = movementComponent.getCurrentTileV1();

        for (String action : damagingActions) {

            int range = AbilityDatabase.getInstance().getRange(action);
            Set<Entity> tilesWithinActionRange = mAlgorithm.computeAreaOfSight(model, currentTile, range);
            // Check what happens when we focus on one of the tiles
            for (Entity tileEntity : tilesWithinActionRange) {
                // if the current tile has an entity of different faction/team, target
                int area = AbilityDatabase.getInstance().getArea(action);
                Set<Entity> tilesWithinActionAreaOfEffect = mAlgorithm.computeAreaOfSight(model, tileEntity, area);
                // Check all the units within tilesWithinActionRange and tilesWithinAreaOfEffect
//                List<Entity> tilesWithUnits = new ArrayList<>();
                List<Entity> tilesWithUnits = tilesWithinActionAreaOfEffect.stream()
                        .filter(tileWithPotentialUnit -> tileWithPotentialUnit.get(Tile.class).getUnitID() != null)
                        .filter(tileWithPotentialUnit -> !tileWithPotentialUnit.get(Tile.class).getUnitID().isBlank())
                        .filter(tileWithUnit -> {
                            Tile inspectedTile = tileWithUnit.get(Tile.class);
                            String unitEntityID = inspectedTile.getUnitID();
                            Entity unitOnTile = EntityStore.getInstance().get(unitEntityID);
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
