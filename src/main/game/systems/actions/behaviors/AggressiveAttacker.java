package main.game.systems.actions.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.stream.Collectors;

import main.constants.Constants;
import main.game.components.*;
import main.game.components.Summary;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.PathBuilder;
import main.game.pathfinding.TilePathing;
import main.game.stores.pools.ability.Ability;
import main.game.stores.pools.ability.AbilityPool;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class AggressiveAttacker extends Behavior {

    private final static ELogger logger = ELoggerFactory.getInstance().getELogger(AggressiveAttacker.class);
    private final static SplittableRandom random = new SplittableRandom();

    private List<Ability> getDamagingAbilities(Entity unit) {
        return new ArrayList<>(unit.get(Abilities.class)
                .getAbilities()
                .stream()
                .map(e -> AbilityPool.getInstance().get(e))
                .filter(Objects::nonNull)
                .filter(e -> e.isEnergyDamaging() || e.isHealthDamaging())
                .toList());
    }
        
    private List<Ability> getHealingAbilities(Entity unit) {
        return new ArrayList<>(unit.get(Abilities.class)
                .getAbilities()
                .stream()
                .map(e -> AbilityPool.getInstance().get(e))
                .filter(Objects::nonNull)
                .filter(e -> !e.isHealthDamaging())
                .toList());
    }

    private List<Ability> getEnergizingAbilities(Entity unit) {
        return new ArrayList<>(unit.get(Abilities.class)
                .getAbilities()
                .stream()
                .map(e -> AbilityPool.getInstance().get(e))
                .filter(Objects::nonNull)
                .filter(e -> !e.isEnergyDamaging())
                .toList());
    }

    

    public void move(GameModel model, Entity unit) {
        // Go through all of the possible tiles that can be moved to
        MovementManager movement = unit.get(MovementManager.class);
        Summary stats = unit.get(Summary.class);
        int move = stats.getStatsNode(Constants.MOVE).getTotal();
        int jump = stats.getStatsNode(Constants.CLIMB).getTotal();
        Set<Entity> withinMovementRange = movement.movementRange;
        TilePathing.getTilesWithinClimbAndMovement(model, movement.currentTile, move, jump, withinMovementRange);
        List<Entity> shuffledWithinMovementRange = new ArrayList<>(withinMovementRange);
        Collections.shuffle(shuffledWithinMovementRange);

        // TODO we could sample the tiles or just check each tile and and see what moves have an attack
        // Only get abilities that cause damage

       List<Ability> abilities = getDamagingAbilities(unit);
       Collections.shuffle(abilities);

        ActionManager action = unit.get(ActionManager.class);

        for (Entity tile : shuffledWithinMovementRange) {
            for (Ability ability : abilities) {

                // Get tiles within LOS based on the ability range
                action.addTilesInLineOfSight(
                    PathBuilder.newBuilder()
                        .setModel(model)
                        .setStart(tile)
                        .setRange(ability.range)
                        .getTilesInRange()
                );
                                
                boolean foundTarget = action.actionLineOfSight.stream()
                    .anyMatch(entity -> {
                        Tile potentialTarget = entity.get(Tile.class);
                        boolean hasUnit = potentialTarget.unit != null;
                        boolean hasNonSelfTarget = potentialTarget.unit != unit;
                        if (!hasUnit || !hasNonSelfTarget) { return false; }
                        boolean sameTeam = model.speedQueue.shareSameTeam(potentialTarget.unit, unit);
                        return sameTeam && random.nextBoolean();
                    }
                );

                if (foundTarget) {
                    utils.getTilesWithinClimbAndMovementRange(model, unit);
                    utils.getTilesWithinClimbAndMovementPath(model, unit, tile);
                    utils.tryMovingUnit(model, unit, tile);
//                    model.logger.log(unit, "targeted with " + ability.name);
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
        Summary stats = unit.get(Summary.class);

        // get all the abilities into a map
        List<Ability> abilities = getDamagingAbilities(unit);
        Collections.shuffle(abilities);

        for (Ability ability : abilities) {
            if (ability == null) { continue; }
            
            // Get all tiles that can be attacked/targeted
            if (ability.canNotPayCosts(unit)) { continue; }

            // Get tiles within LOS based on the ability range
//            List<Entity> withinLineOfSight = PathBuilder.newBuilder()
//                    .setGameModel(model)
//                    .setStartingPoint(movement.currentTile)
//                    .setDistance(ability.range)
//                    .getTilesWithinLineOfSight();

            action.addTilesInLineOfSight(
                PathBuilder.newBuilder()
                    .setModel(model)
                    .setStart(movement.currentTile)
                    .setRange(ability.range)
                    .getTilesInRange()
            );

            // Don't target self unless the ability allows self targeting
            List<Entity> filter1 = action.actionLineOfSight.stream()
                .filter(tile -> !(!ability.hasTag(Ability.CAN_FRIENDLY_FIRE) && tile.get(Tile.class).unit == unit))
                .toList();

            // for each tile in LOS, check what happens if it is the focal point/center of attack
            // this is necessary in that we might be able to attack without hitting ourself
            for (Entity mainTarget : filter1) {

                // TilePathing.getTilesWithinLineOfSight(model, mainTarget, ability.area, action.tilesWithinActionAOE);
                action.addTilesInAreaOfEffect(
                    PathBuilder.newBuilder()
                    .setModel(model)
                    .setStart(mainTarget)
                    .setRange(ability.area)
                    .getTilesInRange()
                );
                
                //Dont target self unless the ability allows self targeting
                List<Entity> filter2 = action.actionAreaOfEffect.stream()
                    .filter(tileEntity -> !(!ability.hasTag(Ability.CAN_FRIENDLY_FIRE) && tileEntity.get(Tile.class).unit == unit))
                    .filter(tileEntity -> tileEntity.get(Tile.class).unit != null)
                    .collect(Collectors.toList());

                // BE A LITTLE SMART, if there are two targets 
                // 1 is the acting unit, and the 2nd is someone else, 
                // just use someone else as target, else change to self target if beneficial 
                if (filter2.contains(movement.currentTile) && filter2.size() == 1) {
                    continue;
                }

                if (filter2.size() > 0) {
                    utils.tryAttackingUnits(model, unit, filter2.get(0), ability);
                    return;
                }
            }
        }

        abilities = getHealingAbilities(unit);
        if (stats.getResourceNode(Constants.HEALTH).isLessThanMax()) {
            if (!abilities.isEmpty() && random.nextBoolean()) {
                Collections.shuffle(abilities);
                Ability ability = abilities.get(0);
                action.addTilesInLineOfSight(
                        PathBuilder.newBuilder()
                                .setModel(model)
                                .setStart(movement.currentTile)
                                .setRange(ability.range)
                                .getTilesInRange()
                );
                action.addTilesInAreaOfEffect(
                        PathBuilder.newBuilder()
                                .setModel(model)
                                .setStart(movement.currentTile)
                                .setRange(ability.area)
                                .getTilesInRange()
                );

                utils.tryAttackingUnits(model, unit, movement.currentTile, ability);
                return;
            }
        }
                
        // logger.info(unit + " found a target aggresively");
        // model.uiLogQueue.add(unit + " did not attack aggresively");
        action.acted = true;
    }
}
