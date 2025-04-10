package main.game.systems.actions.behaviors;

import main.constants.Pair;
import main.game.components.IdentityComponent;
import main.game.components.MovementComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.TileComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.EntityStore;
import main.game.stores.AbilityTable;

import java.util.*;

public class RandomnessBehavior extends MoveActionBehavior {

    private final int mRandomnessVisionRange = 6;

    @Override
    public String toMoveTo(GameModel model, String unitID) {
        Entity unitEntity = EntityStore.getInstance().get(unitID);
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        String currentTileID = movementComponent.getCurrentTileID();
        Entity currentTileEntity = EntityStore.getInstance().get(currentTileID);

        // Get all tiles that can be walked to
        List<String> tilesThatCantBeMovedTo = mAlgorithm.getMovementRange(
                model,
                currentTileID,
                statisticsComponent.getTotalMovement()
        );


        // Move to a random tile other than current
        tilesThatCantBeMovedTo.remove(movementComponent.getCurrentTileID());
        if (tilesThatCantBeMovedTo.isEmpty()) {
            return null;
        }

        String randomTileID = tilesThatCantBeMovedTo.get(mRandom.nextInt(tilesThatCantBeMovedTo.size()));
        return randomTileID;
    }

    @Override
    public Pair<String, String> toActOn(GameModel model, String entityID) {
        Entity entity = EntityStore.getInstance().get(entityID);
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
                            Entity unitOnTile = EntityStore.getInstance().get(unitEntityID);
                            return unitOnTile != entity;
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

    public Pair<String, String> toActOnV2(GameModel model, String entityID) {
        Entity entity = EntityStore.getInstance().get(entityID);
        // Get try selecting an ability randomly
        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);

//        List<String> damagingActions = new ArrayList<>(
//                statisticsComponent.getOtherAbility()
//                        .stream()
//                        .filter(action -> AbilityDatabase.getInstance().isDamagingAbility(action))
//                        .toList()
//        );
//        List<String> damagingActions = new ArrayList<>();
//        Collections.shuffle(damagingActions);

        MovementComponent movementComponent = entity.get(MovementComponent.class);
        String currentTileID = movementComponent.getCurrentTileID();
        if (true) { return null; }

        List<String> abilities = new ArrayList<>();
        abilities.add(statisticsComponent.getBasicAbility());
        abilities.addAll(statisticsComponent.getOtherAbility());
        Collections.shuffle(abilities);
//
//        for (String ability : abilities) {
//
//            int range = AbilityTable.getInstance().getRange(ability);
//            List<String> tilesWithinActionRange = mAlgorithm.computeAreaOfSightV3(model, currentTileID, range);
//            // Check what happens when we focus on one of the tiles
//            for (String tileEntityID : tilesWithinActionRange) {
//                Entity tileEntity = EntityStore.getInstance().get(tileEntityID);
//                // if the current tile has an entity of different faction/team, target
//                int area = AbilityTable.getInstance().getArea(ability);
//                Set<Entity> tilesWithinActionAreaOfEffect = mAlgorithm.computeAreaOfSight(model, tileEntity, area);
//                // Check all the units within tilesWithinActionRange and tilesWithinAreaOfEffect
////                List<Entity> tilesWithUnits = new ArrayList<>();
//                List<Entity> tilesWithUnits = tilesWithinActionAreaOfEffect.stream()
//                        .filter(tileWithPotentialUnit -> tileWithPotentialUnit.get(TileComponent.class).getUnitID() != null)
//                        .filter(tileWithPotentialUnit -> !tileWithPotentialUnit.get(TileComponent.class).getUnitID().isBlank())
//                        .filter(tileWithUnit -> {
//                            TileComponent inspectedTile = tileWithUnit.get(TileComponent.class);
//                            String unitEntityID = inspectedTile.getUnitID();
//                            Entity unitOnTile = EntityStore.getInstance().get(unitEntityID);
//                            return unitOnTile != entity;
////                            return !model.getSpeedQueue().isOnSameTeam(unitEntity, unitOnTile);
////                            return true;
//                        }).toList();
//
//                // Attack one of the units randomly
//                if (tilesWithUnits.isEmpty()) { continue; }
//                Entity randomTileWithUnit = tilesWithUnits.get(mRandom.nextInt(tilesWithUnits.size()));
//                IdentityComponent randomTileIdentityComponent = randomTileWithUnit.get(IdentityComponent.class);
//                String randomTileID = randomTileIdentityComponent.getID();
//                return new Pair<>(randomTileID, ability);
//            }
//        }
        return null;
    }
}
