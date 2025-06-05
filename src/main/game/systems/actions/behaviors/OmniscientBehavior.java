package main.game.systems.actions.behaviors;

import com.alibaba.fastjson2.JSONObject;
import main.constants.Pair;
import main.game.components.IdentityComponent;
import main.game.components.MovementComponent;
import main.game.components.PositionComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.TileComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.AbilityTable;
import main.game.stores.EntityStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class OmniscientBehavior extends MoveActionBehavior {
    @Override
    public String toMoveTo(GameModel model, String unitID) {
        Entity unitEntity = getEntityWithID(unitID);
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        String currentTileID = movementComponent.getCurrentTileID();

        List<String> allEnemyUnits = mBehaviorLibrary.getAllEnemyUnits(model, unitID);
        if (allEnemyUnits.isEmpty()) { return null; }

        // Tiles this unit can move to
        List<String> movableTiles = mAlgorithm.getMovementRange(
                model,
                currentTileID,
                statisticsComponent.getTotalMovement()
        );

        // Exclude current tile (optional)
        movableTiles.remove(currentTileID);
        if (movableTiles.isEmpty()) { return null; }

        // Choose tile closest to any enemy
        String bestTile = null;
        int minDistance = Integer.MAX_VALUE;

        for (String tileID : movableTiles) {
            PositionComponent tilePos = getEntityWithID(tileID).get(PositionComponent.class);

            for (String enemyID : allEnemyUnits) {
                PositionComponent enemyPos = getEntityWithID(enemyID).get(PositionComponent.class);
                int distance = PositionComponent.getManhattanDistance(tilePos, enemyPos);
                if (distance < minDistance) {
                    minDistance = distance;
                    bestTile = tileID;
                }
            }
        }

        return bestTile;
    }

    @Override
    public Pair<String, String> toActOn(GameModel model, String entityID) {
        Entity entity = getEntityWithID(entityID);
        // Get try selecting an ability randomly
        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
        MovementComponent movementComponent = entity.get(MovementComponent.class);
        String currentTileID = movementComponent.getCurrentTileID();

        List<String> abilities = new ArrayList<>();
        abilities.add(statisticsComponent.getBasicAbility());
        abilities.addAll(statisticsComponent.getOtherAbility());
        Collections.shuffle(abilities);

        for (String ability : abilities) {
            int range = AbilityTable.getInstance().getRange(ability);
            List<String> tileIDsWithinAreaOfSight = mAlgorithm.computeAreaOfSight(model, currentTileID, range);
            // Check what happens when we focus on one of the tiles
            for (String tileIDWithinAreaOfSight : tileIDsWithinAreaOfSight) {
//                Entity tileEntity = EntityStore.getInstance().get(tileEntityID);
                // if the current tile has an entity of different faction/team, target
                int area = AbilityTable.getInstance().getArea(ability);
                List<String> tileIDsWithinAreaOfEffect = mAlgorithm.computeAreaOfEffect(model, tileIDWithinAreaOfSight, area);
                // Check all the units within tilesWithinActionRange and tilesWithinAreaOfEffect
                List<Entity> tilesWithUnits = new ArrayList<>();
                tilesWithUnits = tileIDsWithinAreaOfEffect.stream()
                        .filter(Objects::nonNull)
                        .map(e -> EntityStore.getInstance().get(e))
                        .filter(tileWithPotentialUnit -> tileWithPotentialUnit.get(TileComponent.class).getUnitID() != null)
                        .filter(tileWithPotentialUnit -> !tileWithPotentialUnit.get(TileComponent.class).getUnitID().isBlank())
                        .filter(tileWithUnit -> {
                            TileComponent inspectedTile = tileWithUnit.get(TileComponent.class);
                            String unitEntityID = inspectedTile.getUnitID();
                            Entity unitOnTile = getEntityWithID(unitEntityID);
                            // If unit is on same team, skip
                            String myTeam = model.getGameState().getTeam(entityID);
                            String theirTeam = model.getGameState().getTeam(unitEntityID);
//                            JSONObject request = new JSONObject();
//                            request.put("unit_1_id", entityID);
//                            request.put("unit_2_id", unitEntityID);
//                            boolean sameTeam = model.areUnitsOnSameTeam(request);


                            return unitOnTile != entity && !myTeam.equalsIgnoreCase(theirTeam);
//                            return !model.getSpeedQueue().isOnSameTeam(unitEntity, unitOnTile);
//                            return true;
                        }).toList();

                // Attack one of the units randomly
                if (tilesWithUnits.isEmpty()) { continue; }
                Entity randomTileWithUnit = tilesWithUnits.get(mRandom.nextInt(tilesWithUnits.size()));
                IdentityComponent randomTileIdentityComponent = randomTileWithUnit.get(IdentityComponent.class);
                String randomTileID = randomTileIdentityComponent.getID();
                return new Pair<>(ability, randomTileID);
            }
        }
        return null;
    }
}
