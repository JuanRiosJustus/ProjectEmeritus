package main.game.systems.actions.behaviors;

import main.constants.Constants;
import main.constants.GameState;
import main.game.components.tile.Gem;
import main.game.components.*;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.PathBuilder;
import main.game.pathfinding.TilePathing;
import main.game.stores.pools.ability.Ability;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.MathUtils;

import java.util.*;

public class ActionUtils {

    private final SplittableRandom random = new SplittableRandom();

    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    public void tryMovingUnit(GameModel model, Entity unit, Entity toMoveTo) {
        // Check unit has not moved and is within movement range
        // Other tile validation stuff
        MovementManager movement = unit.get(MovementManager.class);
        if (movement.moved) { return; }
        if (toMoveTo == null || toMoveTo == movement.currentTile) { return; }

        boolean inRange = movement.range.contains(toMoveTo);
        boolean inPath = movement.path.contains(toMoveTo);

        if (!inPath || !inRange) { return; }

        movement.move(model, toMoveTo);

        model.logger.log(unit, " moves to " + toMoveTo);

        Tile tileMovedTo = toMoveTo.get(Tile.class);
        if (tileMovedTo.getGem() != null) {
            Gem gem = tileMovedTo.getGem();
            Summary stats = unit.get(Summary.class);
            stats.addGem(gem);
            tileMovedTo.setGem(null);
        }
        if (unit.get(UserBehavior.class) != null) {
            model.gameState.set(GameState.UI_GO_TO_CONTROL_HOME, true);
        }
    }

    public void getTilesWithinClimbAndMovementPath(GameModel model, Entity unit, Entity selected) {
        MovementManager movement = unit.get(MovementManager.class);
        if (movement.moved) { return; }

        Summary stats = unit.get(Summary.class);
        int move = stats.getStatTotal(Constants.MOVE);
        int climb =  stats.getStatTotal(Constants.CLIMB);

        movement.setPath(
            PathBuilder.getInstance()
                .setModel(model)
                .setRange(move)
                .setClimb(climb)
                .setStart(movement.currentTile)
                .setEnd(selected)
                .getTilesInMovementPath()
        );
    }

    public void getTilesWithinClimbAndMovementRange(GameModel model, Entity unit) {
        MovementManager movement = unit.get(MovementManager.class);
        if (movement.moved) { return; }

        ActionManager action = unit.get(ActionManager.class);
        action.range.clear();
        action.area.clear();

        Summary stats = unit.get(Summary.class);
        int move = stats.getStatTotal(Constants.MOVE);
        int climb = stats.getStatTotal(Constants.CLIMB);

        movement.setRange(
            PathBuilder.getInstance()
                .setModel(model)
                .setStart(movement.currentTile)
                .setRange(move)
                .setClimb(climb)
                .getTilesInMovementRange()
        );
    }

    public static void setupAction(GameModel model, Entity unit, Entity target, Ability ability) {
        ActionManager action = unit.get(ActionManager.class);
        if (action.acted || ability == null) { return; }
        MovementManager movement = unit.get(MovementManager.class);

        // get tiles within LOS for the ability
        action.addTilesInRange(
                PathBuilder.getInstance()
                        .setModel(model)
                        .setStart(movement.currentTile)
                        .setRange(ability.range)
                        .getTilesInRange());

        if (action.range.isEmpty()) { return; }
        if (target == null) { return; }

        //TODO maybe keep this? If we don't, shows the tiles that were hovered before being Out of range
        if (!action.range.contains(target)) {
            action.sight.clear();
            action.area.clear();
            return;
        }

        action.targeting = target;

        if (ability.range > 0)  {
            action.addTilesInLineOfSight(
                    PathBuilder.getInstance()
                            .setModel(model)
                            .setStart(movement.currentTile)
                            .setEnd(target)
                            .setRange(ability.range)
                            .getTilesInLineOfSight());
        }

        if (ability.area > 0) {
            action.addTilesInAreaOfEffect(
                    PathBuilder.getInstance()
                            .setModel(model)
                            .setStart(target)
                            .setRange(ability.area - 1)
                            .getTilesInRange());
        }
    }

    public static boolean tryAttackingUnits(GameModel model, Entity unit, Set<Entity> area, Ability ability) {
        ActionManager action = unit.get(ActionManager.class);
        if (action.acted) { return false; }

        action.acted = model.system.combat.startCombat(model, unit, ability, area);

        if (action.acted && unit.get(UserBehavior.class) != null) {
            model.gameState.set(GameState.UI_GO_TO_CONTROL_HOME, true);
        }
        return action.acted;
    }

    public void randomlyAttack(GameModel model, Entity unit) {
//        MovementTrack movementTrack = unit.get(MovementTrack.class);
//        if (movementTrack.isMoving()) { return; }
//        // Get all the abilities the unit can use
//        ActionManager manager = unit.get(ActionManager.class);
//        MovementManager movement = unit.get(MovementManager.class);
//        Statistics stats = unit.get(Statistics.class);
//        // get all the abilities into a map
//        List<Ability> abilities = newunit.get(Abilities.class).getAbilities();
//        Collections.shuffle(abilities);
//        Set<Entity> tilesWithinAbilityLOS = manager.withinRange;
//
//        // consider all the abilities to use
//        for (Ability ability : abilities) {
//
//            // TODO Why can ability be null?
//            if (ability == null) { continue; }
//
//            // Don't attack self if not beneficial
//            if (ability.canFriendlyFire && !beneficiallyEffectsUser(ability)) {
//                continue;
//            }
//
//            // Get tiles within LOS based on the ability range
//            TilePathing.getTilesWithinLineOfSight(model, movement.currentTile, ability.range, tilesWithinAbilityLOS);
//
//            // tile must have a unit and is not a wall or structure. Dont target self unless is status ability
//            List<Entity> tilesWithEntities = tilesWithinAbilityLOS.stream()
//                    .filter(tile -> tile.get(Tile.class).unit != null)
//                    .filter(tile -> !tile.get(Tile.class).isWall())
//                    .filter(tile -> !tile.get(Tile.class).isStructure())
//                    .filter(tile -> (ability.canFriendlyFire || tile.get(Tile.class).unit != unit))
//                    .collect(Collectors.toList());
//
//            // no tiles in ability range, check if theres an enemy within the ability's aoe from anywhere
//            if (tilesWithEntities.isEmpty()) {
//
//                // check if the area of effect will reach any of the unit
//                Set<Entity> tilesWithinAoeLOS = manager.areaOfEffect;
//                for (Entity tileToTarget : tilesWithinAbilityLOS) {
//
//                    // ignore checking walls and structures
//                    boolean isWall = tileToTarget.get(Tile.class).isWall();
//                    boolean isStructure = tileToTarget.get(Tile.class).isStructure();
//                    if (isWall || isStructure) { continue; }
//
//                    // Get tiles within the los of the Aoe
//                    TilePathing.getTilesWithinLineOfSight(model, tileToTarget, ability.area, tilesWithinAoeLOS);
//                    Optional<Entity> tileWithEntity = tilesWithinAoeLOS.stream()
//                            .filter(tile -> tile.get(Tile.class).unit != null)
//                            .filter(tile -> !tile.get(Tile.class).isWall())
//                            .filter(tile -> !tile.get(Tile.class).isStructure())
//                            .filter(tile -> (ability.canFriendlyFire || tile.get(Tile.class).unit != unit))
//                            .findFirst();
//
//                    // There is a unit present, we can hit this tile with an attack
//                    if (tileWithEntity.isPresent()) { tilesWithEntities.add(tileToTarget); break; }
//                }
//            }
//
//            // if no valid targets, try next ability
//            if (tilesWithEntities.isEmpty()) {continue; }
//
//            // choose random target
//            Collections.shuffle(tilesWithEntities);
//            Entity selectedTileWithEntity = tilesWithEntities.get(0);
//            tilesWithEntities.clear();
//            tilesWithEntities.add(selectedTileWithEntity);
//
//            attackTileWithinAbilityRange(model, unit,ability, selectedTileWithEntity);
//            break;
//        }
//        manager.acted = true;
    }

    public int getDistanceBetween(Entity from, Entity to) {
        int rowDiff = MathUtils.diff(from.get(Tile.class).row, to.get(Tile.class).row);
        int colDiff = MathUtils.diff(from.get(Tile.class).column, to.get(Tile.class).column);
        return rowDiff + colDiff;
    }

    public void randomlyMove(GameModel model, Entity unit) {
        MovementTrack movementTrack = unit.get(MovementTrack.class);
        if (movementTrack.isMoving()) { return; } // ensure not currently acting
        Summary stats = unit.get(Summary.class);
        MovementManager movement = unit.get(MovementManager.class);

        // Get tiles within the movement range
        int move = stats.getStatsNode(Constants.MOVE).getTotal();
        int jump = stats.getStatsNode(Constants.CLIMB).getTotal();
        Set<Entity> withinMovementRange = movement.range;
        TilePathing.getTilesWithinClimbAndMovement(model, movement.currentTile, move, jump, withinMovementRange);

        // select a random tile to move to
        List<Entity> candidates = movement.range.stream().toList();
        Entity randomTile = candidates.get(random.nextInt(candidates.size()));

        // if the random tile is current tile, don't move (Moving to same tile causes exception in animation track)
        if (randomTile != movement.currentTile) {
            // regather tiles
//            gatherTilesWithinMovementRange(model, unit, stats.getStatsNode(Constants.MOVE).getTotal(), randomTile);
            getTilesWithinClimbAndMovementRange(model, unit);
            getTilesWithinClimbAndMovementPath(model, unit, randomTile);
            tryMovingUnit(model, unit, randomTile);
//            moveUnitToTile(model, unit, randomTile);
        }
        movement.moved = true;
    }

    public void undoMovement(GameModel model, Entity unit) {
        MovementManager manager = unit.get(MovementManager.class);
        manager.moved = false;

        Entity previous = manager.previousTile;

        // Handle undo procedure
        getTilesWithinClimbAndMovementRange(model, unit);
        getTilesWithinClimbAndMovementPath(model, unit, previous);
        manager.useTrack = false;
        tryMovingUnit(model, unit, previous);
        manager.useTrack = true;
        manager.previousTile = null;

        // handle waiting tile selection state
        manager.moved = false;
        model.gameState.set(GameState.UI_GO_TO_CONTROL_HOME, false);
        model.logger.log(unit, " Moves back to " + previous);
    }
}
