package game.components.behaviors;

import game.components.Component;
import logging.Logger;
import logging.LoggerFactory;

public abstract class Behavior extends Component {

    protected final Logger logger = LoggerFactory.instance().logger(getClass());

//    protected static Ability tryGetRangeFromSelected(EngineController engine, Entity unit) {
//        Statistics stats = unit.get(Statistics.class);
//        Optional<Ability> furthest = stats.getAbilities().stream().max(Comparator.comparingInt(o -> o.range));
//        return furthest.orElse(null);
//    }
//
//    protected void gatherTilesWithinMovementRange(EngineController engine, Entity unit, int range, Entity tile) {
//        ActionManager manager = unit.get(ActionManager.class);
//
//        // if the user didn't change anything about the state, not need to update
//        if (tile == null || tile == manager.tileToMoveTo) { return; }
//        Statistics stats = unit.get(Statistics.class);
//        manager.tileToMoveTo = tile;
//
//        // get all possible tiles within range
//        TileTargeting.getTilesWithinRange(
//                engine.model.game.model,
//                manager.tileOccupying,
//                stats.getScalarNode(Constants.DISTANCE).getTotal(),
//                true,
//                manager.tilesWithinMovementRange
//        );
//
//        // remove all tiles that are Structures/Units/etc...
//        List<Entity> available = manager.tilesWithinMovementRange.stream()
//                .filter(toCheck -> !toCheck.get(Tile.class).isStructureUnitOrWall())
//                .collect(Collectors.toList());
//        manager.tilesWithinMovementRange.clear();
//        manager.tilesWithinMovementRange.addAll(available);
//        manager.tilesWithinMovementRange.add(manager.tileOccupying); // allow self targeting
//        // show all the tiles that could be attack from the longest range ability
//
//        if (range == 0) { return; }
//
//        // if the tile to move to is not within range, return
//        if (!manager.tilesWithinMovementRange.contains(manager.tileToMoveTo)) {
//            manager.tilesWithinAbilityRange.clear();
//            return;
//        }
//
//        // populate the tiles to be shown with the ability
//        getTilesWithinLOSFromTile(
//                engine,
//                manager.tileToMoveTo,
//                range,
//                manager.tilesWithinAbilityRange
//        );
//
//        // populate the path tiles
//        TileTargeting.getTilesWithinPath(
//                engine.model.game.model,
//                manager.tileOccupying,
//                manager.tileToMoveTo,
//                stats.getScalarNode(Constants.DISTANCE).getTotal(),
//                manager.tilesWithinMovementRangePath
//        );
//    }
//
//    protected void attackTileWithinAbilityRange(EngineController engine, Entity unit, Ability ability, Entity tile) {
//        // Check unit has not attacked and tile is within ability range
//        ActionManager manager = unit.get(ActionManager.class);
//        if (tile == null || ability == null || manager.attacked) { return; }
//        if (!manager.tilesWithinAbilityRange.contains(tile)) { return; }
//
//        // get all tiles within LOS and attack at them
//        getTilesWithinLOSFromTile(engine, tile, ability.areaOfEffect, manager.tilesWithinAreaOfEffect);
//
//        // start combat
//        CombatSystem.startCombat(engine, unit, ability, new ArrayList<>(manager.tilesWithinAreaOfEffect));
//        manager.attacked = true;
//    }
//
//    protected void gatherTilesWithinAbilityRange(EngineController engine, Entity unit, int range, int aoe, Entity tile) {
//        ActionManager manager = unit.get(ActionManager.class);
//
//        if (tile == null || manager.tileToAttackAt == tile) { return; }
//        manager.tileToAttackAt = tile;
//
//        // get tiles within LOS for the ability
//        getTilesWithinLOSFromTile(engine, manager.tileOccupying, range, manager.tilesWithinAbilityRange);
//
//        // get tiles within LOS for the AOE... Does the target need to be within ability range? (Probably)
//        if (aoe == 0 && manager.tilesWithinAbilityRange.contains(tile)) {
//            manager.tilesWithinAreaOfEffect.clear();
//            manager.tilesWithinAreaOfEffect.add(tile);
//        } else {
//            getTilesWithinLOSFromTile(engine, tile, aoe, manager.tilesWithinAreaOfEffect);
//        }
//    }
//
//    protected void gatherTilesWithinAbilityRange(EngineController engine, Entity unit, Ability ability, Entity tile) {
//        ActionManager manager = unit.get(ActionManager.class);
//
//        if (tile == null || manager.tileToAttackAt == tile) { return; }
//        manager.tileToAttackAt = tile;
//
//        // get tiles within LOS for the ability
//        getTilesWithinLOSFromTile(engine, manager.tileOccupying, ability.range, manager.tilesWithinAbilityRange);
//
//        // get tiles within LOS for the AOE... Does the target need to be within ability range? (Probably)
//        if (ability.areaOfEffect == 0 && manager.tilesWithinAbilityRange.contains(tile)) {
//            manager.tilesWithinAreaOfEffect.clear();
//            manager.tilesWithinAreaOfEffect.add(tile);
//        } else {
//            getTilesWithinLOSFromTile(engine, tile, ability.areaOfEffect, manager.tilesWithinAreaOfEffect);
//        }
//    }
//
//    protected void moveToTileWithinMovementRange(EngineController engine, Entity unit, Entity tile) {
//
//        // Check unit has not moved and is within movement range
//        ActionManager manager = unit.get(ActionManager.class);
//        if (tile == null || manager.moved) { return; }
//        if (!manager.tilesWithinMovementRange.contains(tile)) { return; }
//        // move unit
//        Movement movement = unit.get(Movement.class);
//        logger.log(unit + " moving from " + manager.tileOccupying + " to " + tile);
//        movement.move(engine, unit, tile);
//        manager.moved = true;
//    }
}
