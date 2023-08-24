package main.game.systems.actions.behaviors;

import java.util.*;

import main.constants.Constants;
import main.constants.GameState;
import main.game.components.*;
import main.game.components.Summary;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ability.Ability;
import main.game.stores.pools.ability.AbilityPool;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class AggressiveAttacker extends Behavior {

    private final static ELogger logger = ELoggerFactory.getInstance().getELogger(AggressiveAttacker.class);
    private final static SplittableRandom random = new SplittableRandom();

    private final Randomness randomness = new Randomness();

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
        // Go through all the possible tiles that can be moved to

        List<Ability> abilities = getDamagingAbilities(unit);
        Collections.shuffle(abilities);
        // check current tile

        Movement movement = unit.get(Movement.class);
        Summary summary = unit.get(Summary.class);
        Movement projection = Movement.project(
                model, movement.currentTile,
                summary.getStatTotal(Constants.MOVE),
                summary.getStatTotal(Constants.CLIMB),
                null);

        // check all tiles within movement range
        Set<Entity> tilesToMoveTo = new HashSet<>(projection.range);
        for (Ability ability : abilities) {
            for (Entity tileToMoveTo : tilesToMoveTo) {

                // dont stay on the same tile
                if (tileToMoveTo == movement.currentTile) { continue; }

                // Get tiles within action range
                Action action = Action.project(model, tileToMoveTo, ability, null);

                // get tiles within ability range
                Set<Entity> tilesToTarget = new HashSet<>(action.range);
                for (Entity tileToTarget : tilesToTarget) {

                    // check if attacking this target will put any entities in cross-hairs
                    action = Action.project(model, tileToMoveTo, ability, tileToTarget);
                    boolean foundTarget = action.area.stream().anyMatch(entity -> {
                        Tile potentialTarget = entity.get(Tile.class);
                        boolean hasUnit = potentialTarget.unit != null;
                        boolean hasNonSelfTarget = potentialTarget.unit != unit;
                        return hasUnit && hasNonSelfTarget;
                    });

                    String targetFound = action.area.stream()
                            .filter(Objects::nonNull)
                            .filter(e -> e.get(Tile.class).unit != null)
                            .filter(e -> e.get(Tile.class).unit != unit)
                            .map(e -> e.get(Tile.class).unit.toString()).findFirst().orElse(null);

                    if (foundTarget) {
                        model.logger.log(unit + " is targeting " + targetFound);
                        Movement.move(model, unit, tileToMoveTo, true);
                        return;
                    }
                }
            }
        }

        logger.info("Didn't find target");
        randomness.move(model, unit);
    }

    public void attack(GameModel model, Entity unit) {

        Movement movement = unit.get(Movement.class);
        Action action = unit.get(Action.class);
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
        Movement movement = unit.get(Movement.class);
        Action action = unit.get(Action.class);
        for (Ability ability : abilities) {
            if (ability == null) { continue; }

            // Get all tiles that can be attacked/targeted
            if (ability.canNotPayCosts(unit)) { continue; }

            // Get tiles within LOS based on the ability range
            Action.act(model, unit, ability, null, false);

            for (Entity tile : action.range) {

                if (tile == movement.currentTile) { continue; }
                Tile currentTile = tile.get(Tile.class);
                if (currentTile.unit == unit) { continue; }
                if (currentTile.unit == null) { continue; }

                Action.act(model, unit, ability, tile, false);

                boolean hasEntity = action.area.stream().anyMatch(entity -> {
                    Tile toCheck = entity.get(Tile.class);
                    return toCheck.unit != null;
                });

                if (!hasEntity) { continue; }

                if (!Action.act(model, unit, ability, tile, true)) { continue; }
                return true;
            }
        }
        return false;
    }
}
