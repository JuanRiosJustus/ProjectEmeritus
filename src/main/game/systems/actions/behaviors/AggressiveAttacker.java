package main.game.systems.actions.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SplittableRandom;

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
        Set<Entity> withinMovementRange = movement.range;
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
                    PathBuilder.getInstance()
                        .setModel(model)
                        .setStart(tile)
                        .setRange(ability.range)
                        .getTilesInRange()
                );
                                
                boolean foundTarget = action.sight.stream()
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

        if (tryAttacking(model, unit, abilities)) {
            return;
        }

        abilities = getHealingAbilities(unit);
        Collections.shuffle(abilities);

        if (stats.getStatCurrent(Constants.HEALTH) < stats.getStatTotal(Constants.HEALTH)) {
            if (!abilities.isEmpty()) {
                tryAttacking(model, unit, abilities);
            }
        }

        action.acted = true;
    }

    private static boolean tryAttacking(GameModel model, Entity unit, List<Ability> abilities) {
        MovementManager movement = unit.get(MovementManager.class);
        ActionManager action = unit.get(ActionManager.class);
        for (Ability ability : abilities) {
            if (ability == null) { continue; }

            // Get all tiles that can be attacked/targeted
            if (ability.canNotPayCosts(unit)) { continue; }

            // Get tiles within LOS based on the ability range
            ActionUtils.setupAction(model, unit, null, ability);

            for (Entity tile : action.range) {

                if (tile == movement.currentTile) { continue; }
                Tile currentTile = tile.get(Tile.class);
                if (currentTile.unit == unit) { continue; }
                if (currentTile.unit == null) { continue; }

                ActionUtils.setupAction(model, unit, tile, ability);

                boolean hasEntity = action.area.stream().anyMatch(entity -> {
                    Tile toCheck = entity.get(Tile.class);
                    return toCheck.unit != null;
                });

                if (!hasEntity) { continue; }

                if (!ActionUtils.tryAttackingUnits(model, unit, action.area, ability)) { continue; }
                return true;
            }
        }
        return false;
    }
}
