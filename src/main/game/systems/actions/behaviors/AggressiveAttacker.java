package main.game.systems.actions.behaviors;

import java.util.*;

import main.constants.Constants;
import main.game.components.*;
import main.game.components.Summary;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.action.Action;
import main.game.stores.pools.action.ActionPool;

public class AggressiveAttacker extends Behavior {
    private final Randomness randomness = new Randomness();

    private List<Action> getDamagingAbilities(Entity unit) {
        return new ArrayList<>(unit.get(Summary.class)
                .getAbilities()
                .stream()
                .map(e -> ActionPool.getInstance().get(e))
                .filter(Objects::nonNull)
                .filter(e -> e.getHealthDamage(unit) > 0 || e.getEnergyDamage(unit) > 0)
                .toList());
    }
        
    private List<Action> getHealingAbilities(Entity unit) {
        return new ArrayList<>(unit.get(Summary.class)
                .getAbilities()
                .stream()
                .map(e -> ActionPool.getInstance().get(e))
                .filter(Objects::nonNull)
                .filter(e -> e.getHealthDamage(unit) < 0 || e.getEnergyDamage(unit) < 0)
                .toList());
    }

//    private List<Action> getEnergizingAbilities(Entity unit) {
//        return new ArrayList<>(unit.get(Actions.class)
//                .getAbilities()
//                .stream()
//                .map(e -> ActionPool.getInstance().get(e))
//                .filter(Objects::nonNull)
//                .filter(e -> !e.isEnergyDamaging())
//                .toList());
//    }

    public void move(GameModel model, Entity unit) {
        // Go through all the possible tiles that can be moved to

        List<Action> abilities = getDamagingAbilities(unit);
        Collections.shuffle(abilities);
        // check current tile

        MovementManager movementManager = unit.get(MovementManager.class);
        Summary summary = unit.get(Summary.class);
        MovementManager projection = MovementManager.project(
                model, movementManager.currentTile,
                summary.getStatTotal(Constants.MOVE),
                summary.getStatTotal(Constants.CLIMB),
                null);

        // check all tiles within movement range
        Set<Entity> tilesToMoveTo = new HashSet<>(projection.range);
        for (Action action : abilities) {
            for (Entity tileToMoveTo : tilesToMoveTo) {

                // dont stay on the same tile
                if (tileToMoveTo == movementManager.currentTile) { continue; }

                // Get tiles within action range
                ActionManager projection2 = ActionManager.project(model, tileToMoveTo, action, null);

                // get tiles within ability range
                Set<Entity> tilesToTarget = new HashSet<>(projection2.range);
                for (Entity tileToTarget : tilesToTarget) {

                    // check if attacking this target will put any entities in cross-hairs
                    projection2 = ActionManager.project(model, tileToMoveTo, action, tileToTarget);
                    boolean foundTarget = projection2.area.stream().anyMatch(entity -> {
                        Tile potentialTarget = entity.get(Tile.class);
                        boolean hasUnit = potentialTarget.unit != null;
                        boolean hasNonSelfTarget = potentialTarget.unit != unit;
                        return hasUnit && hasNonSelfTarget;
                    });

                    String targetFound = projection2.area.stream()
                            .filter(Objects::nonNull)
                            .filter(e -> e.get(Tile.class).unit != null)
                            .filter(e -> e.get(Tile.class).unit != unit)
                            .map(e -> e.get(Tile.class).unit.toString()).findFirst().orElse(null);

                    if (foundTarget) {
                        boolean moved = MovementManager.move(model, unit, tileToMoveTo, true);
                        if (moved) {
                            model.logger.log(unit + " is moving to " + tileToMoveTo);
                            logger.info("Moving {} to {} ", movementManager.currentTile, tileToMoveTo);
                            return;
                        }
                    }
                }
            }
        }

        logger.info("Didn't find target");
        randomness.move(model, unit);
    }

    public void attack(GameModel model, Entity unit) {

        ActionManager actionManager = unit.get(ActionManager.class);
        Summary stats = unit.get(Summary.class);

        // get all the abilities into a map
        List<Action> abilities = getDamagingAbilities(unit);
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

        actionManager.acted = true;
    }

    private static boolean tryAttacking(GameModel model, Entity unit, List<Action> abilities) {
        MovementManager movementManager = unit.get(MovementManager.class);
        for (Action action : abilities) {
            if (action == null) { continue; }

            // Get all tiles that can be attacked/targeted
            if (action.cantPayCosts(unit)) { continue; }

            // Get tiles within LOS based on the ability range
            ActionManager projection = ActionManager.project(model, movementManager.currentTile, action, null);

            for (Entity tile : projection.range) {

                if (tile == movementManager.currentTile) { continue; }
                Tile currentTile = tile.get(Tile.class);
                if (currentTile.unit == unit) { continue; }
                if (currentTile.unit == null) { continue; }

                ActionManager projection2 = ActionManager.project(model, movementManager.currentTile, action, tile);

                boolean hasEntity = projection2.area.stream().anyMatch(entity -> {
                    Tile toCheck = entity.get(Tile.class);
                    return toCheck.unit != null;
                });

                if (!hasEntity) { continue; }
                boolean acted = ActionManager.act(model, unit, action, tile, true);
                if (!acted) { continue; }
                return true;
            }
        }
        return false;
    }
}
