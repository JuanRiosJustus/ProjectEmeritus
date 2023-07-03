package game.systems.actions.behaviors;

import constants.Constants;
import constants.GameStateKey;
import game.collectibles.Gem;
import game.components.*;
import game.components.behaviors.UserBehavior;
import game.components.statistics.Summary;
import game.entity.Entity;
import game.main.GameModel;
import game.pathfinding.TilePathing;
import game.stores.pools.ability.Ability;
import logging.Logger;
import logging.LoggerFactory;
import utils.MathUtils;

import java.util.*;
import java.util.stream.Collectors;

public class BehaviorUtils {

    private final SplittableRandom random = new SplittableRandom();

    private final Logger logger = LoggerFactory.instance().logger(getClass());

    public void tryMovingUnit(GameModel model, Entity unit, Entity toMoveTo) {
        // Check unit has not moved and is within movement range
        // Other tile validation stuff
        MovementManager movement = unit.get(MovementManager.class);
        if (movement.moved) { return; }
        if (toMoveTo == null || toMoveTo == movement.currentTile) { return; }

        boolean inRange = movement.tilesWithinMovementRange.contains(toMoveTo);
        boolean inPath = movement.tilesWithinMovementPath.contains(toMoveTo);

        if (!inPath || !inRange) { return; }

        movement.move(model, toMoveTo);

        String name = unit.toString();
        model.uiLogQueue.add(name + " moves to " + toMoveTo);

        Tile tileMovedTo = toMoveTo.get(Tile.class);
        if (tileMovedTo.getGem() != null) {
            Gem gem = tileMovedTo.getGem();
            Summary stats = unit.get(Summary.class);
            stats.addGemBonus(gem);
            tileMovedTo.setGem(null);
        }
        if (unit.get(UserBehavior.class) != null) {
            model.state.set(GameStateKey.UI_GO_TO_CONTROL_HOME, true);
        }
    }

    public void moveUnitToTile(GameModel model, Entity unit, Entity tile) {
        // Check unit has not moved and is within movement range
        // Other tile validation stuff
        MovementManager movement = unit.get(MovementManager.class);

        if (tile == null || movement.moved) { return; }
        if (!movement.tilesWithinMovementRange.contains(tile)) { return; }
        if (!movement.tilesWithinMovementPath.contains(tile)) { return; }
        if (tile == movement.currentTile) { return; }

        if (logger != null) { logger.info("{0} moving from {1} to {2}", unit, movement.currentTile, tile); }

        movement.move(model, tile);

//        MovementTrack movementTrack = unit.get(MovementTrack.class);
//        movementTrack.move(model, unit, tile);
//        movement.moved = true;
    }

    public void getTilesWithinJumpAndMovementPath(GameModel model, Entity unit, Entity selected) {
        MovementManager movement = unit.get(MovementManager.class);
        if (movement.moved) { return; }

        Summary stats = unit.get(Summary.class);
        int move = stats.getScalarNode(Constants.MOVE).getTotal();
        int jump = stats.getScalarNode(Constants.CLIMB).getTotal();

        Deque<Entity> tilesWithinMovementPath = movement.tilesWithinMovementPath;
        Entity starting = movement.currentTile;

        TilePathing.getTilesWithinClimbAndMovementPath(model, starting, selected, move, jump, tilesWithinMovementPath);
    }

    public void getTilesWithinJumpAndMovementRange(GameModel model, Entity unit) {
        MovementManager movement = unit.get(MovementManager.class);
        if (movement.moved) { return; }

        ActionManager action = unit.get(ActionManager.class);
        action.tilesWithinActionRange.clear();
        action.tilesWithinActionAOE.clear();

        Summary stats = unit.get(Summary.class);
        int move = stats.getScalarNode(Constants.MOVE).getTotal();
        int jump = stats.getScalarNode(Constants.CLIMB).getTotal();

        Set<Entity> tilesWithinMovementRange = movement.tilesWithinMovementRange;
        TilePathing.getTilesWithinClimbAndMovement(model, movement.currentTile, move, jump, tilesWithinMovementRange);
    }

    public void getTilesWithinActionRange(GameModel model, Entity unit, Entity target, Ability ability) {
        ActionManager action = unit.get(ActionManager.class);
        if (action.acted) { return; }
        if (target == null || ability == null) { return; }

        MovementManager movement = unit.get(MovementManager.class);

        // get tiles within LOS for the ability
        Set<Entity> tilesWithinActionRange = action.tilesWithinActionRange;
        Entity current = movement.currentTile;
        TilePathing.getTilesWithinRange(model, current, ability.range, tilesWithinActionRange);

        if (tilesWithinActionRange.isEmpty()) { return; }

        movement.tilesWithinMovementPath.clear();
        movement.tilesWithinMovementRange.clear();

        action.targeting = target;

        if (ability.range >= 0) {
            Set<Entity> tilesWithinLOS = action.tilesWithinActionLOS;
            tilesWithinLOS.clear();
            TilePathing.getTilesWithinLineOfSight(model, movement.currentTile, action.targeting, ability.range, tilesWithinLOS);
        }

        if (ability.area >= 0) {
            Set<Entity> tilesWithinAOE = action.tilesWithinActionAOE;
            TilePathing.getTilesWithinRange(model, target, ability.area, tilesWithinAOE);
        }
    }

    public void tryAttackingUnits(GameModel model, Entity unit, Entity tile, Ability ability) {
        ActionManager action = unit.get(ActionManager.class);
        if (action.acted) { return; }

        boolean inRange = action.tilesWithinActionRange.contains(tile);
        if (!inRange) { return; }

        // get all tiles within LOS and attack at them
        Set<Entity> tilesWithinAOE = action.tilesWithinActionAOE;

        // start combat
        model.system.combat.startCombat(model, unit, ability, new HashSet<>(tilesWithinAOE));
        action.acted = true;
    }


    public void attackTileWithinAbilityRange(GameModel model, Entity unit, Ability ability, Entity tile) {
        // Check unit has not attacked and tile is within ability range
        ActionManager manager = unit.get(ActionManager.class);
        if (tile == null || ability == null || manager.acted) { return; }
        if (!manager.tilesWithinActionRange.contains(tile)) { return; }

        // get all tiles within LOS and attack at them
        TilePathing.getTilesWithinLineOfSight(model, tile, ability.area, manager.tilesWithinActionAOE);

        // start combat
        model.system.combat.startCombat(model, unit, ability, new HashSet<>(manager.tilesWithinActionAOE));
        manager.acted = true;
    }

    public void randomlyAttack(GameModel model, Entity unit) {
        MovementTrack movementTrack = unit.get(MovementTrack.class);
        if (movementTrack.isMoving()) { return; }
        // Get all the abilities the unit can use
        ActionManager manager = unit.get(ActionManager.class);
        MovementManager movement = unit.get(MovementManager.class);
        Summary stats = unit.get(Summary.class);
        // get all the abilities into a map
        List<Ability> abilities = unit.get(Summary.class).getAbilities();
        Collections.shuffle(abilities);
        Set<Entity> tilesWithinAbilityLOS = manager.tilesWithinActionRange;

        // consider all the abilities to use
        for (Ability ability : abilities) {

            // TODO Why can ability be null?
            if (ability == null) { continue; }

            // Don't attack self if not beneficial
            if (ability.friendlyFire && !beneficiallyEffectsUser(ability)) {
                continue;
            }

            // Get tiles within LOS based on the ability range
            TilePathing.getTilesWithinLineOfSight(model, movement.currentTile, ability.range, tilesWithinAbilityLOS);

            // tile must have a unit and is not a wall or structure. Dont target self unless is status ability
            List<Entity> tilesWithEntities = tilesWithinAbilityLOS.stream()
                    .filter(tile -> tile.get(Tile.class).unit != null)
                    .filter(tile -> !tile.get(Tile.class).isWall())
                    .filter(tile -> !tile.get(Tile.class).isStructure())
                    .filter(tile -> (ability.friendlyFire || tile.get(Tile.class).unit != unit))
                    .collect(Collectors.toList());

            // no tiles in ability range, check if theres an enemy within the ability's aoe from anywhere
            if (tilesWithEntities.isEmpty()) {

                // check if the area of effect will reach any of the unit
                Set<Entity> tilesWithinAoeLOS = manager.tilesWithinActionAOE;
                for (Entity tileToTarget : tilesWithinAbilityLOS) {

                    // ignore checking walls and structures
                    boolean isWall = tileToTarget.get(Tile.class).isWall();
                    boolean isStructure = tileToTarget.get(Tile.class).isStructure();
                    if (isWall || isStructure) { continue; }

                    // Get tiles within the los of the Aoe
                    TilePathing.getTilesWithinLineOfSight(model, tileToTarget, ability.area, tilesWithinAoeLOS);
                    Optional<Entity> tileWithEntity = tilesWithinAoeLOS.stream()
                            .filter(tile -> tile.get(Tile.class).unit != null)
                            .filter(tile -> !tile.get(Tile.class).isWall())
                            .filter(tile -> !tile.get(Tile.class).isStructure())
                            .filter(tile -> (ability.friendlyFire || tile.get(Tile.class).unit != unit))
                            .findFirst();

                    // There is a unit present, we can hit this tile with an attack
                    if (tileWithEntity.isPresent()) { tilesWithEntities.add(tileToTarget); break; }
                }
            }

            // if no valid targets, try next ability
            if (tilesWithEntities.isEmpty()) {continue; }

            // choose random target
            Collections.shuffle(tilesWithEntities);
            Entity selectedTileWithEntity = tilesWithEntities.get(0);
            tilesWithEntities.clear();
            tilesWithEntities.add(selectedTileWithEntity);

            attackTileWithinAbilityRange(model, unit,ability, selectedTileWithEntity);
            break;
        }
        manager.acted = true;
    }

    private boolean beneficiallyEffectsUser(Ability ability) {
        return false;
//        return ability.energyDamage.base < 0 || ability.healthDamage.base < 0;
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
        int move = stats.getScalarNode(Constants.MOVE).getTotal();
        int jump = stats.getScalarNode(Constants.CLIMB).getTotal();
        Set<Entity> withinMovementRange = movement.tilesWithinMovementRange;
        TilePathing.getTilesWithinClimbAndMovement(model, movement.currentTile, move, jump, withinMovementRange);

        // select a random tile to move to
        List<Entity> candidates = movement.tilesWithinMovementRange.stream().toList();
        Entity randomTile = candidates.get(random.nextInt(candidates.size()));

        // if the random tile is current tile, don't move (Moving to same tile causes exception in animation track)
        if (randomTile != movement.currentTile) {
            // regather tiles
//            gatherTilesWithinMovementRange(model, unit, stats.getScalarNode(Constants.MOVE).getTotal(), randomTile);
            getTilesWithinJumpAndMovementRange(model, unit);
            getTilesWithinJumpAndMovementPath(model, unit, randomTile);
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
        getTilesWithinJumpAndMovementRange(model, unit);
        getTilesWithinJumpAndMovementPath(model, unit, previous);
        manager.useTrack = false;
        tryMovingUnit(model, unit, previous);
        manager.useTrack = true;
        manager.previousTile = null;

        // handle waiting tile selection state
        manager.moved = false;
        model.state.set(GameStateKey.UI_GO_TO_CONTROL_HOME, false);
        model.uiLogQueue.add("Moved " + unit + " back to " + previous);
        // logger.log("Moving {0} back to {1}", unit, previous);
    }
}
