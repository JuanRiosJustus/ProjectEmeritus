package game.systems.actions.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import constants.Constants;
import game.components.ActionManager;
import game.components.MoveSet;
import game.components.MovementManager;
import game.components.Tile;
import game.components.statistics.Summary;
import game.entity.Entity;
import game.main.GameModel;
import game.pathfinding.TilePathing;
import game.stores.pools.ability.Ability;
import game.stores.pools.ability.AbilityPool;
import logging.ELogger;
import logging.ELoggerFactory;

public class AggressiveAttacker extends Behavior {

    private final ELogger logger = ELoggerFactory.getInstance().getELogger(AggressiveAttacker.class);

    private List<Ability> getDamagingAbilities(Entity unit) {
        return new ArrayList<>(unit.get(Summary.class)
            .getAbilities().stream()
            .map(e -> AbilityPool.getInstance().getAbility(e))
            .filter(Objects::nonNull)
            .filter(e -> {
                boolean hasBaseHealthDamage = e.baseHealthDamage != 0;
                boolean hasScalingHealthDamage = e.scalingHealthDamage.size() > 0;
                return hasBaseHealthDamage || hasScalingHealthDamage;
            }).toList());
    }

    public void move(GameModel model, Entity unit) {
        // Go through all of the possible tiles that can be moved to
        MovementManager movement = unit.get(MovementManager.class);
        Summary stats = unit.get(Summary.class);
        int move = stats.getScalarNode(Constants.MOVE).getTotal();
        int jump = stats.getScalarNode(Constants.CLIMB).getTotal();
        Set<Entity> withinMovementRange = movement.tilesWithinMovementRange;
        TilePathing.getTilesWithinClimbAndMovement(model, movement.currentTile, move, jump, withinMovementRange);
        List<Entity> shuffledWithinMovementRange = new ArrayList<>(withinMovementRange);
        Collections.shuffle(shuffledWithinMovementRange);

        // TODO we could sample the tiles or just check each tile and and see what moves have an attack
        // Only get abilities that cause damage


       List<Ability> abilities = getDamagingAbilities(unit);
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
                    utils.getTilesWithinJumpAndMovementRange(model, unit);
                    utils.getTilesWithinJumpAndMovementPath(model, unit, tile);
                    utils.tryMovingUnit(model, unit, tile);
                    model.uiLogQueue.add(unit + " acquired target with " + ability.name);
                    return;
                }
            }
        }

        model.uiLogQueue.add(unit + " didnt fine a target");
        logger.info("Didn't find target");
        utils.randomlyMove(model, unit);
    }

    public void attack(GameModel model, Entity unit) {

        MovementManager movement = unit.get(MovementManager.class);
        ActionManager action = unit.get(ActionManager.class);

        // get all the abilities into a mapz
        List<Ability> abilities = unit.get(Summary.class).getAbilities()
            .stream().map(e -> AbilityPool.getInstance().getAbility(e)).toList();
        
        
        /*
         * Exception in thread "main" java.lang.UnsupportedOperationException
        at java.base/java.util.ImmutableCollections.uoe(ImmutableCollections.java:142)
        at java.base/java.util.ImmutableCollections$AbstractImmutableList.set(ImmutableCollections.java:260)
        at java.base/java.util.Collections.swap(Collections.java:501)
        at java.base/java.util.Collections.shuffle(Collections.java:462)
        at java.base/java.util.Collections.shuffle(Collections.java:429)
        at game.systems.actions.behaviors.AggressiveAttacker.attack(AggressiveAttacker.java:94)
        at game.systems.actions.ActionHandler.handleAi(ActionHandler.java:132)
        at game.systems.MoveActionSystem.update(MoveActionSystem.java:19)
        at game.systems.UpdateSystem.update(UpdateSystem.java:33)
        at game.main.GameModel.update(GameModel.java:80)
        at game.main.GameController.update(GameController.java:61)
        at engine.EngineModel.update(EngineModel.java:17)
        at engine.EngineController.update(EngineController.java:15)
        at engine.Engine.run(Engine.java:35)
        at Main.main(Main.java:33)
         */
        //  Collections.shuffle(abilities);

        Set<Entity> tilesWithinActionLOS = action.tilesWithinActionLOS;

        for (Ability ability : abilities) {

            if (ability == null) { continue; }
            
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
                utils.tryAttackingUnits(model, unit, tilesWithEntities.get(0), ability);
                action.acted = true;
                logger.info(unit + " found a target aggresively");
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
                utils.tryAttackingUnits(model, unit, tilesWithEntities.get(0), ability);

                action.acted = true;
                logger.info(unit + " found a target aggresively");
                model.uiLogQueue.add(unit + " attacked aggresively");
                return;
            }
        }
        model.uiLogQueue.add(unit + " did not attack aggresively");
        action.acted = true;
    }
}
