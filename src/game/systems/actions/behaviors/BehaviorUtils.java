package game.systems.actions.behaviors;

import constants.ColorPalette;
import constants.Constants;
import constants.GameStateKey;
import game.collectibles.Gem;
import game.components.*;
import game.components.Vector;
import game.components.behaviors.UserBehavior;
import game.components.statistics.Summary;
import game.entity.Entity;
import game.main.GameModel;
import game.pathfinding.TilePathing;
import game.stores.pools.ability.Ability;
import logging.ELogger;
import logging.ELoggerFactory;
import utils.MathUtils;
import utils.StringUtils;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

public class BehaviorUtils {

    private final SplittableRandom random = new SplittableRandom();

    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

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

        model.logger.log(unit, " moves to " + toMoveTo);

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

    public void getTilesWithinClimbAndMovementPath(GameModel model, Entity unit, Entity selected) {
        MovementManager movement = unit.get(MovementManager.class);
        if (movement.moved) { return; }

        Summary stats = unit.get(Summary.class);
        int move = stats.getStatsNode(Constants.MOVE).getTotal();
        int jump = stats.getStatsNode(Constants.CLIMB).getTotal();

        Deque<Entity> tilesWithinMovementPath = movement.tilesWithinMovementPath;
        Entity starting = movement.currentTile;

        TilePathing.getTilesWithinClimbAndMovementPath(model, starting, selected, move, jump, tilesWithinMovementPath);
    }

    public void getTilesWithinClimbAndMovementRange(GameModel model, Entity unit) {
        MovementManager movement = unit.get(MovementManager.class);
        if (movement.moved) { return; }

        ActionManager action = unit.get(ActionManager.class);
        action.tilesWithinActionRange.clear();
        action.tilesWithinActionAOE.clear();

        Summary stats = unit.get(Summary.class);
        int move = stats.getStatsNode(Constants.MOVE).getTotal();
        int jump = stats.getStatsNode(Constants.CLIMB).getTotal();

        Set<Entity> tilesWithinMovementRange = movement.tilesWithinMovementRange;
        TilePathing.getTilesWithinClimbAndMovement(model, movement.currentTile, move, jump, tilesWithinMovementRange);
    }

    public void getTilesWithinActionRange(GameModel model, Entity unit, Entity target, Ability ability) {
        ActionManager action = unit.get(ActionManager.class);
        if (action.acted || target == null || ability == null) { return; }

        MovementManager movement = unit.get(MovementManager.class);

        // get tiles within LOS for the ability
        Set<Entity> tilesWithinActionRange = action.tilesWithinActionRange;
        Entity current = movement.currentTile;
        TilePathing.getTilesWithinRange(model, current, ability.range, tilesWithinActionRange);

        if (tilesWithinActionRange.isEmpty()) { return; }

        // TODO maybe keep this? If we dont, shows the tiles that were hovered before being Out of range
        if (!tilesWithinActionRange.contains(target)) {
            action.tilesWithinActionLOS.clear();
            action.tilesWithinActionAOE.clear();
            return;
        }

        // movement.tilesWithinMovementPath.clear();
        // movement.tilesWithinMovementRange.clear();

        action.targeting = target;

        if (ability.range >= 0) {
            Set<Entity> tilesWithinLOS = action.tilesWithinActionLOS;
            TilePathing.getTilesWithinLineOfSight(model, movement.currentTile, action.targeting, ability.range, tilesWithinLOS);
            if (ability.area >= 0 && action.tilesWithinActionLOS.contains(target)) {
                Set<Entity> tilesWithinAOE = action.tilesWithinActionAOE;
                TilePathing.getTilesWithinRange(model, target, ability.area, tilesWithinAOE);
            }
        }
    }

    public void tryAttackingUnits(GameModel model, Entity unit, Entity tile, Ability ability) {
        ActionManager action = unit.get(ActionManager.class);
        if (action.acted || tile == null) { return; }

        boolean withinLineOfSight = action.tilesWithinActionLOS.contains(tile);
        if (!withinLineOfSight) { return; }

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
            if (ability.canFriendlyFire && !beneficiallyEffectsUser(ability)) {
                continue;
            }

            // Get tiles within LOS based on the ability range
            TilePathing.getTilesWithinLineOfSight(model, movement.currentTile, ability.range, tilesWithinAbilityLOS);

            // tile must have a unit and is not a wall or structure. Dont target self unless is status ability
            List<Entity> tilesWithEntities = tilesWithinAbilityLOS.stream()
                    .filter(tile -> tile.get(Tile.class).unit != null)
                    .filter(tile -> !tile.get(Tile.class).isWall())
                    .filter(tile -> !tile.get(Tile.class).isStructure())
                    .filter(tile -> (ability.canFriendlyFire || tile.get(Tile.class).unit != unit))
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
                            .filter(tile -> (ability.canFriendlyFire || tile.get(Tile.class).unit != unit))
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
        int move = stats.getStatsNode(Constants.MOVE).getTotal();
        int jump = stats.getStatsNode(Constants.CLIMB).getTotal();
        Set<Entity> withinMovementRange = movement.tilesWithinMovementRange;
        TilePathing.getTilesWithinClimbAndMovement(model, movement.currentTile, move, jump, withinMovementRange);

        // select a random tile to move to
        List<Entity> candidates = movement.tilesWithinMovementRange.stream().toList();
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
        model.state.set(GameStateKey.UI_GO_TO_CONTROL_HOME, false);
        model.logger.log(unit, " Moves back to " + previous);
    }

    public void handleStatusEffects(GameModel model, Entity unit) {
        StatusEffects se = unit.get(StatusEffects.class);
        MovementManager mm = unit.get(MovementManager.class);
        if (se.shouldHandle()) {
            
            Vector location = mm.currentTile.get(Vector.class).copy();
            Color c = ColorPalette.getRandomColor().brighter().brighter();
            for (Map.Entry<String, Object> entry : se.getStatusEffects().entrySet()) {
                model.system.floatingText.floater(StringUtils.capitalize(entry.getKey()) + "'d", location, c);
            }

            se.setHandled(true);
        }
    }
}
