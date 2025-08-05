package main.game.systems.actions.behaviors;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import main.constants.Direction;
import main.constants.Pair;
import main.game.components.AIComponent;
import main.game.components.IdentityComponent;
import main.game.components.MovementComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.TileComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.AbilityTable;
import main.game.stores.EntityStore;

import java.util.*;

public class OmniscientBehavior extends MoveActionBehavior {
    @Override
    public String toMoveTo(GameModel model, String unitID) {
        Entity unitEntity = getEntityWithID(unitID);
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        String currentTileID = movementComponent.getCurrentTileID();
        int movement = statisticsComponent.getTotalMovement();

        // Tiles this unit can move to
        List<String> movableTiles = mAlgorithm.getMovementRange(
                model,
                currentTileID,
                movement
        );

        // Exclude current tile (optional)
        movableTiles.remove(currentTileID);
        if (movableTiles.isEmpty()) { return null; }

        // Choose tile closest to any enemy
        String bestTile = null;
        int minDistance = Integer.MAX_VALUE;

        AIComponent aiComponent = unitEntity.get(AIComponent.class);

        // Get the closest entity
        String focusedEntityID = aiComponent.getFocusedEntityID();
        if (focusedEntityID == null) {
            List<String> allEnemyUnits = mBehaviorLibrary.getAllEnemyUnits(model, unitID);
            if (allEnemyUnits.isEmpty()) { return null; }
            String randomEnemy = allEnemyUnits.get(mRandom.nextInt(allEnemyUnits.size()));
            aiComponent.focusedEntity(randomEnemy);
            focusedEntityID = aiComponent.getFocusedEntityID();
        }
        // Check if currently adjacent

        // Get tile available tile around focused entity
        String tileEntityIDAdjacentToFocusedUnitEntity = null;
        if (tileEntityIDAdjacentToFocusedUnitEntity == null) {
            Entity focuedEntity = getEntityWithID(focusedEntityID);
            MovementComponent focusedEntityMC = focuedEntity.get(MovementComponent.class);
            String focusedTileEntityID = focusedEntityMC.getCurrentTileID();
            Entity focusedTileEntity = getEntityWithID(focusedTileEntityID);
            TileComponent tileComponent = focusedTileEntity.get(TileComponent.class);
            List<Direction> adjacent = new ArrayList<>(Arrays.stream(Direction.cardinal).toList());
            Collections.shuffle(adjacent);
            for (Direction direction : adjacent) {
                int row = tileComponent.getRow() + direction.y;
                int column = tileComponent.getColumn() + direction.x;
                String tileID = model.getTile(row, column);
                Entity adjacentTileEntity = getEntityWithID(tileID);
                if (adjacentTileEntity == null) { continue; }
                TileComponent adjacentTileComponent = adjacentTileEntity.get(TileComponent.class);
                if (adjacentTileComponent.isNotNavigable()) { continue; }
                tileEntityIDAdjacentToFocusedUnitEntity = tileID;
                break;
            }
        }


        JSONObject request = new JSONObject();
        request.put("start_tile_id", currentTileID);
        request.put("end_tile_id", tileEntityIDAdjacentToFocusedUnitEntity);
        request.put("range", -1);
        JSONArray response = model.getTilesInMovementPath(request);

        String nextBestTile = null;
        if (response != null && !response.isEmpty()) {
            nextBestTile = response.getString(movement);
            mLogger.info("Moving {} from {} to {}", unitID, currentTileID, tileEntityIDAdjacentToFocusedUnitEntity);
        }

        return nextBestTile;
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

                            return unitOnTile != entity && !myTeam.equalsIgnoreCase(theirTeam);
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
