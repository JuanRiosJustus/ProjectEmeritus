package main.game.systems.actions.behaviors;

import java.util.*;

import main.constants.Constants;
import main.game.components.*;
import main.game.components.Summary;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.action.Ability;
import main.game.stores.pools.action.AbilityPool;

public class AggressiveAttacker extends Behavior {
    private final Randomness randomness = new Randomness();

    private List<Ability> getDamagingAbilities(Entity unit) {
        return new ArrayList<>(unit.get(Summary.class)
                .getAbilities()
                .stream()
                .map(e -> AbilityPool.getInstance().getAbility(e))
                .filter(Objects::nonNull)
                .filter(e -> e.getHealthDamage(unit) > 0 || e.getManaDamage(unit) > 0 || e.getStaminaDamage(unit) > 0)
                .toList());
    }
        
    private List<Ability> getHealingAbilities(Entity unit) {
        return new ArrayList<>(unit.get(Summary.class)
                .getAbilities()
                .stream()
                .map(e -> AbilityPool.getInstance().getAbility(e))
                .filter(Objects::nonNull)
                .filter(e -> e.getHealthDamage(unit) < 0 || e.getManaDamage(unit) < 0 || e.getStaminaDamage(unit) < 0)
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

        List<Ability> abilities = getDamagingAbilities(unit);
        Collections.shuffle(abilities);
        // check current tile

        MovementManager movementManager = unit.get(MovementManager.class);
        Summary summary = unit.get(Summary.class);
        MovementManager projection = MovementManager.project(
                model, movementManager.currentTile,
                summary.getStatTotal(Constants.MOVE),
                summary.getStatTotal(Constants.CLIMB),
                null);

        // if the unit was not set, it should not be able to attack
        if (projection == null) { return; }

        // check all tiles within movement range
        Set<Entity> tilesToMoveTo = new HashSet<>(projection.range);
        for (Ability ability : abilities) {
            for (Entity tileToMoveTo : tilesToMoveTo) {

                // dont stay on the same tile
                if (tileToMoveTo == movementManager.currentTile) { continue; }

                // Get tiles within action range
                AbilityManager projection2 = AbilityManager.project(model, tileToMoveTo, ability, null);

                // get tiles within ability range
                Set<Entity> tilesToTarget = new HashSet<>(projection2.targets);
                for (Entity tileToTarget : tilesToTarget) {

                    // check if attacking this target will put any entities in cross-hairs
                    projection2 = AbilityManager.project(model, tileToMoveTo, ability, tileToTarget);
                    boolean foundTarget = projection2.aoe.stream().anyMatch(entity -> {
                        Tile potentialTarget = entity.get(Tile.class);
                        boolean hasUnit = potentialTarget.mUnit != null;
                        boolean hasNonSelfTarget = potentialTarget.mUnit != unit;
                        return hasUnit && hasNonSelfTarget;
                    });

                    String targetFound = projection2.aoe.stream()
                            .filter(Objects::nonNull)
                            .filter(e -> e.get(Tile.class).mUnit != null)
                            .filter(e -> e.get(Tile.class).mUnit != unit)
                            .map(e -> e.get(Tile.class).mUnit.toString()).findFirst().orElse(null);

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

    public void attackV2(GameModel model, Entity unit) {
//
//        // Get all enemies within each abilities range
//        Map<String, List<Object[]>> abilityToTargets = Behavior.getAbilityToTargetAndUnitsMap(model, unit);
//
//        // get all the abilities into a map
//        List<Ability> abilities = getDamagingAbilities(unit);
//        abilities.sort((ability1, ability2) -> {
//            float damage1 = 0;
//            for (String key : ability1.getCostKeys()) { damage1 += ability1.getDamage(unit, key); }
//            float damage2 = 0;
//            for (String key : ability2.getCostKeys()) { damage1 += ability2.getDamage(unit, key); }
//
//            return (int) (damage2 - damage1);
//        });
//
//
//        AbilityManager abilityManager = unit.get(AbilityManager.class);
//        Summary stats = unit.get(Summary.class);
//
//        // get all the abilities into a map
//        List<Ability> abilities = getDamagingAbilities(unit);
//        Collections.shuffle(abilities);
//
//        if (tryAttacking(model, unit, abilities)) {
//            return;
//        }
//
//        abilities = getHealingAbilities(unit);
//        Collections.shuffle(abilities);
//
//        if (stats.getStatCurrent(Constants.HEALTH) < stats.getStatTotal(Constants.HEALTH)) {
//            if (!abilities.isEmpty()) {
//                tryAttacking(model, unit, abilities);
//            }
//        }
//
//        abilityManager.acted = true;
    }

    public void attack(GameModel model, Entity unit) {

        AbilityManager abilityManager = unit.get(AbilityManager.class);
        Summary stats = unit.get(Summary.class);

        // get all the abilities into a map
        List<Ability> damagingAbilities = getDamagingAbilities(unit);
        Collections.shuffle(damagingAbilities);

        boolean attacked = tryAttacking(model, unit, damagingAbilities);
        if (attacked) {
            abilityManager.acted = true;
            return;
        }

        List<Ability> healingAbilities = getHealingAbilities(unit);
        Collections.shuffle(healingAbilities);

        if (stats.getStatCurrent(Constants.HEALTH) < stats.getStatTotal(Constants.HEALTH)) {
            if (!healingAbilities.isEmpty()) {
                boolean healed = tryAttacking(model, unit, healingAbilities);
            }
        }

        abilityManager.acted = true;
    }

    private static boolean tryAttacking(GameModel model, Entity unit, List<Ability> abilities) {
        MovementManager movementManager = unit.get(MovementManager.class);
        for (Ability ability : abilities) {
            if (ability == null) { continue; }

            // Get all tiles that can be attacked/targeted
            if (ability.cantPayCosts(unit)) { continue; }

            // Get tiles within LOS based on the ability range
            AbilityManager projection = AbilityManager.project(model, movementManager.currentTile, ability, null);
            if (projection == null) { continue; }

            for (Entity tile : projection.targets) {

                if (tile == movementManager.currentTile) { continue; }
                Tile currentTile = tile.get(Tile.class);
                if (currentTile.mUnit == unit) { continue; }
                if (currentTile.mUnit == null) { continue; }

                AbilityManager projection2 = AbilityManager.project(model, movementManager.currentTile, ability, tile);

                boolean hasEntity = projection2.aoe.stream().anyMatch(entity -> {
                    Tile toCheck = entity.get(Tile.class);
                    return toCheck.mUnit != null;
                });

                if (!hasEntity) { continue; }
                boolean acted = AbilityManager.act(model, unit, ability, tile, true);
                if (!acted) { continue; }
                return true;
            }
        }
        return false;
    }
}
