package game.systems.actions.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import constants.Constants;
import game.GameModel;
import game.components.ActionManager;
import game.components.MoveSet;
import game.components.MovementManager;
import game.components.Tile;
import game.components.statistics.Statistics;
import game.entity.Entity;
import game.pathfinding.TilePathing;
import game.stores.pools.ability.Ability;
import game.systems.actions.ActionUtils;
import logging.Logger;
import logging.LoggerFactory;

public class AggressiveAttacker {

    private final Logger logger = LoggerFactory.instance().logger(getClass());

    public void move(GameModel model, Entity unit, ActionUtils actionUtils) {
        // Go through all of the possible tiles that can be moved to
        MovementManager movement = unit.get(MovementManager.class);
        Statistics stats = unit.get(Statistics.class);
        int move = stats.getScalarNode(Constants.MOVE).getTotal();
        int jump = stats.getScalarNode(Constants.CLIMB).getTotal();
        Set<Entity> withinMovementRange = movement.tilesWithinMovementRange;
        TilePathing.getTilesWithinClimbAndMovement(model, movement.currentTile, move, jump, withinMovementRange);
        List<Entity> shuffledWithinMovementRange = new ArrayList<>(withinMovementRange);
        Collections.shuffle(shuffledWithinMovementRange);

        // TODO we could sample the tiles or just check each tile and and see what moves have an attack
        // Only get abilities that cause damage
        List<Ability> abilities = new ArrayList<>(unit.get(MoveSet.class)
                .getCopy()
                .stream()
                .filter(e -> !e.healthDamage.isEmpty()).toList());
        Collections.shuffle(abilities);

        ActionManager manager = unit.get(ActionManager.class);
        Set<Entity> tilesWithinActionLOS = manager.tilesWithinActionRange;

        for (Entity tile : shuffledWithinMovementRange) {
            for (Ability ability : abilities) {

                // Get tiles within LOS based on the ability range                
                TilePathing.getTilesWithinLineOfSight(model, tile, ability.range, tilesWithinActionLOS);

                // Check if we have any units in sight
                boolean foundTarget = tilesWithinActionLOS.stream().anyMatch(entity ->
                    entity.get(Tile.class).unit != null && entity.get(Tile.class).unit != unit
                );
                
                if (foundTarget) {
                    actionUtils.getTilesWithinJumpAndMovementRange(model, unit);
                    actionUtils.getTilesWithinJumpAndMovementPath(model, unit, tile);
                    actionUtils.tryMovingUnit(model, unit, tile);
                    model.uiLogQueue.add(unit + " acquired target with " + ability.name);
                    return;
                }
            }
        }

        model.uiLogQueue.add(unit + " didnt fine a target");
        logger.log("Didn't find target");
        actionUtils.randomlyMove(model, unit);
    }

    public void attack(GameModel model, Entity unit, ActionUtils actionUtils) {

        MovementManager movement = unit.get(MovementManager.class);
        ActionManager action = unit.get(ActionManager.class);

        // get all the abilities into a map
        List<Ability> abilities = unit.get(MoveSet.class).getCopy();
        Collections.shuffle(abilities);

        Set<Entity> tilesWithinActionLOS = action.tilesWithinActionLOS;

        for (Ability ability : abilities) {

            // Get all tiles that can be attacked/targeted
            if (model.system.combat.canPayAbilityCosts(unit, ability) == false) { continue; }

            // Get tiles within LOS based on the ability range
            TilePathing.getTilesWithinLineOfSight(model, movement.currentTile, ability.range, tilesWithinActionLOS);

            // tile must have a unit and is not a wall or structure. Dont target self unless is status ability
            List<Entity> tilesWithEntities = tilesWithinActionLOS.stream()
                .filter(tile -> tile.get(Tile.class).unit != null)
                .filter(tile -> tile.get(Tile.class).unit != unit)
                .collect(Collectors.toList());

            // for each tile in LOS, check what happens if it is the focal point/center of attack
            // this is necessary in that we might be able to attack without hitting ourself
            for (Entity mainTarget : tilesWithEntities) {
                TilePathing.getTilesWithinRange(model, mainTarget, ability.area, action.tilesWithinActionAOE);
                
                // if this tile being aimed at does not hit the user, attack there
                boolean hasTarget = action.tilesWithinActionAOE.stream().anyMatch(
                    e -> e != movement.currentTile && e.get(Tile.class).unit != null
                );
                if (hasTarget == false) { continue; }
                TilePathing.getTilesWithinRange(model, movement.currentTile, ability.range, action.tilesWithinActionRange);
                actionUtils.tryAttackingUnits(model, unit, tilesWithEntities.get(0), ability);
                action.acted = true;
                logger.log(unit + " found a target aggresively");
                model.uiLogQueue.add(unit + " attacked aggresively");
                return;
            }

            // Get tiles within LOS based on the ability range
            TilePathing.getTilesWithinLineOfSight(model, movement.currentTile, ability.range, tilesWithinActionLOS);

            // tile must have a unit and is not a wall or structure. Dont target self unless is status ability
            tilesWithEntities = tilesWithinActionLOS.stream()
                .filter(tile -> tile.get(Tile.class).unit != null)
                .filter(tile -> tile.get(Tile.class).unit != unit)
                .collect(Collectors.toList());

            // If there are tiles with units
            if (tilesWithEntities.size() > 0) {
                TilePathing.getTilesWithinRange(model, movement.currentTile, ability.range, action.tilesWithinActionRange);
                TilePathing.getTilesWithinRange(model, tilesWithEntities.get(0), ability.area, action.tilesWithinActionAOE);
                actionUtils.tryAttackingUnits(model, unit, tilesWithEntities.get(0), ability);

                action.acted = true;
                logger.log(unit + " found a target aggresively");
                model.uiLogQueue.add(unit + " attacked aggresively");
                return;
            }
        }
        model.uiLogQueue.add(unit + " did not attack aggresively");
        action.acted = true;
    }
}
