package game.systems.actions.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.stream.Collectors;

import constants.Constants;
import game.components.Abilities;
import game.components.ActionManager;
import game.components.MoveSet;
import game.components.MovementManager;
import game.components.Tile;
import game.components.Type;
import game.components.statistics.Summary;
import game.entity.Entity;
import game.main.GameModel;
import game.pathfinding.TilePathing;
import game.stores.pools.ability.Ability;
import game.stores.pools.ability.AbilityPool;
import logging.ELogger;
import logging.ELoggerFactory;

public class AggressiveAttacker extends Behavior {

    private final static ELogger logger = ELoggerFactory.getInstance().getELogger(AggressiveAttacker.class);
    private final static SplittableRandom random = new SplittableRandom();

    private List<Ability> getDamagingAbilities(Entity unit) {
        return new ArrayList<>(unit.get(Abilities.class)
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
        int move = stats.getStatsNode(Constants.MOVE).getTotal();
        int jump = stats.getStatsNode(Constants.CLIMB).getTotal();
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
                    utils.getTilesWithinClimbAndMovementRange(model, unit);
                    utils.getTilesWithinClimbAndMovementPath(model, unit, tile);
                    utils.tryMovingUnit(model, unit, tile);
                    model.logger.log(unit, "targetted with " + ability.name);
                    return;
                }
            }
        }

        model.logger.log(unit, " didnt fine a target");
        logger.info("Didn't find target");
        utils.randomlyMove(model, unit);
    }

    public void attack(GameModel model, Entity unit) {

        MovementManager movement = unit.get(MovementManager.class);
        ActionManager action = unit.get(ActionManager.class);

        // get all the abilities into a map
        List<Ability> abilities = unit.get(Abilities.class).getAbilities()
            .stream()
            .map(e -> AbilityPool.getInstance().getAbility(e))
            .toList();

        abilities = new ArrayList<>(abilities);
        Collections.shuffle(abilities);

        for (Ability ability : abilities) {

            if (ability == null) { continue; }
            
            // Get all tiles that can be attacked/targeted
            if (model.system.combat.canPayAbilityCosts(unit, ability) == false) { continue; }

            // Get tiles within LOS based on the ability range
            TilePathing.getTilesWithinLineOfSight(model, movement.currentTile, ability.range, action.tilesWithinActionLOS);

            // Dont target self unless the ability allows self targeting
            List<Entity> filter1 = action.tilesWithinActionLOS.stream()
                .filter(tile -> !(!ability.canFriendlyFire && tile.get(Tile.class).unit == unit))
                .collect(Collectors.toList());

            // for each tile in LOS, check what happens if it is the focal point/center of attack
            // this is necessary in that we might be able to attack without hitting ourself
            for (Entity mainTarget : filter1) {

                TilePathing.getTilesWithinLineOfSight(model, mainTarget, ability.area, action.tilesWithinActionAOE);
                
                //Dont target self unless the ability allows self targeting
                List<Entity> filter2 = action.tilesWithinActionAOE.stream()
                    .filter(tileEntity -> !(!ability.canFriendlyFire && tileEntity.get(Tile.class).unit == unit))
                    .filter(tileEntity -> tileEntity.get(Tile.class).unit != null)
                    .collect(Collectors.toList());

                // BE A LITTLE SMART, if there are two targets 
                // 1 is the acting unit, and the 2nd is someone else, 
                // just use someone else as target, else change to self target if beneficial 
                if (filter2.contains(movement.currentTile)) {
                    if (filter2.size() == 1) {
                        continue;
                    }
                    
                }

                if (filter2.size() > 0) {
                    utils.tryAttackingUnits(model, unit, filter2.get(0), ability);
                    return;
                }
            }
        }
                
        // logger.info(unit + " found a target aggresively");
        // model.uiLogQueue.add(unit + " did not attack aggresively");
        action.acted = true;
    }
}
