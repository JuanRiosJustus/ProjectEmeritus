package main.game.systems.actions.behaviors;

import main.game.entity.Entity;
import main.game.main.GameModel;

public class AggressiveBehavior {
    private final RandomnessBehavior randomnessBehavior = new RandomnessBehavior();

//    private List<Action> getDamagingAbilities(Entity unit) {
//        return new ArrayList<>(unit.get(StatisticsComponent.class)
//                .getAbilities()
//                .stream()
//                .map(e -> ActionPool.getInstance().getAction(e))
//                .filter(Objects::nonNull)
//                .filter(e -> e.getHealthDamage(unit) > 0 || e.getManaDamage(unit) > 0 || e.getStaminaDamage(unit) > 0)
//                .toList());
//    }
//
//    private List<Action> getHealingAbilities(Entity unit) {
//        return new ArrayList<>(unit.get(StatisticsComponent.class)
//                .getAbilities()
//                .stream()
//                .map(e -> ActionPool.getInstance().getAction(e))
//                .filter(Objects::nonNull)
//                .filter(e -> e.getHealthDamage(unit) < 0 || e.getManaDamage(unit) < 0 || e.getStaminaDamage(unit) < 0)
//                .toList());
//    }

//    private List<Action> getEnergizingAbilities(Entity unit) {
//        return new ArrayList<>(unit.get(Actions.class)
//                .getAbilities()
//                .stream()
//                .map(e -> ActionPool.getInstance().get(e))
//                .filter(Objects::nonNull)
//                .filter(e -> !e.isEnergyDamaging())
//                .toList());
//    }

    public void move2(GameModel model, Entity unitEntity) {
//        movementSystem.move(model, unit, mousedAt, false);
//        if (mouse.isPressed()) {
//            movementSystem.move(model, unit, mousedAt, true);
//        }
//        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
//        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//        Set<Entity> tilesWithinWalkingDistance = PathBuilder.newBuilder().getMovementRange(
//                model,
//                movementComponent.mCurrentTile,
//                statisticsComponent.getTotal(StatisticsComponent.MOVE),
//                statisticsComponent.getTotal(StatisticsComponent.CLIMB)
//        );
    }

    public void move(GameModel model, Entity unit) {
        // Go through all the possible tiles that can be moved to

//        List<Ability> abilities = getDamagingAbilities(unit);
//        Collections.shuffle(abilities);
//        // check current tile
//
//        MovementManager movementManager = unit.get(MovementManager.class);
//        Statistics statistics = unit.get(Statistics.class);
//        MovementManager projection = MovementManager.project(
//                model, movementManager.currentTile,
//                statistics.getStatTotal(Constants.MOVE),
//                statistics.getStatTotal(Constants.CLIMB),
//                null);
//
//        // if the unit was not set, it should not be able to attack
//        if (projection == null) { return; }
//
//        // check all tiles within movement range
//        Set<Entity> tilesToMoveTo = new HashSet<>(projection.tilesInRange);
//        for (Ability ability : abilities) {
//            for (Entity tileToMoveTo : tilesToMoveTo) {
//
//                // dont stay on the same tile
//                if (tileToMoveTo == movementManager.currentTile) { continue; }
//
//                // Get tiles within action range
//                ActionManager projection2 = ActionManager.project(model, tileToMoveTo, ability, null);
//
//                // get tiles within ability range
//                Set<Entity> tilesToTarget = new HashSet<>(projection2.mTargets);
//                for (Entity tileToTarget : tilesToTarget) {
//
//                    // check if attacking this target will put any entities in cross-hairs
//                    projection2 = ActionManager.project(model, tileToMoveTo, ability, tileToTarget);
//                    boolean foundTarget = projection2.mAreaOfEffect.stream().anyMatch(entity -> {
//                        Tile potentialTarget = entity.get(Tile.class);
//                        boolean hasUnit = potentialTarget.mUnit != null;
//                        boolean hasNonSelfTarget = potentialTarget.mUnit != unit;
//                        return hasUnit && hasNonSelfTarget;
//                    });
//
//                    String targetFound = projection2.mAreaOfEffect.stream()
//                            .filter(Objects::nonNull)
//                            .filter(e -> e.get(Tile.class).mUnit != null)
//                            .filter(e -> e.get(Tile.class).mUnit != unit)
//                            .map(e -> e.get(Tile.class).mUnit.toString()).findFirst().orElse(null);
//
//                    if (foundTarget) {
//                        boolean moved = MovementManager.move(model, unit, tileToMoveTo, true);
//                        if (moved) {
//                            model.logger.log(unit + " is moving to " + tileToMoveTo);
//                            logger.info("Moving {} to {} ", movementManager.currentTile, tileToMoveTo);
//                            return;
//                        }
//                    }
//                }
//            }
//        }
//
//        logger.info("Didn't find target");
//        randomnessBehavior.move(model, unit);
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

//    public void attack(GameModel model, Entity unit) {
//
//        ActionComponent actionComponent = unit.get(ActionComponent.class);
//        StatisticsComponent stats = unit.get(StatisticsComponent.class);
//
//        // get all the abilities into a map
//        List<Action> damagingAbilities = new ArrayList<>();//getDamagingAbilities(unit);
//        Collections.shuffle(damagingAbilities);
//
//        boolean attacked = tryAttacking(model, unit, damagingAbilities);
//        if (attacked) {
//            actionComponent.setActed(true);
//            return;
//        }
//
//        List<Action> healingAbilities = new ArrayList<>();//getHealingAbilities(unit);
//        Collections.shuffle(healingAbilities);
//
//        if (stats.getCurrent(Constants.HEALTH) < stats.getTotal(Constants.HEALTH)) {
//            if (!healingAbilities.isEmpty()) {
//                boolean healed = tryAttacking(model, unit, healingAbilities);
//            }
//        }
//
//        actionComponent.setActed(true);
//    }

//    private static boolean tryAttacking(GameModel model, Entity unit, List<Action> abilities) {
//        MovementComponent movementComponent = unit.get(MovementComponent.class);
////        for (Ability ability : abilities) {
////            if (ability == null) { continue; }
////
////            // Get all tiles that can be attacked/targeted
////            if (ability.canNotPayCosts(unit)) { continue; }
////
////            // Get tiles within LOS based on the ability range
////            ActionManager projection = ActionManager.project(model, movementManager.currentTile, ability, null);
////            if (projection == null) { continue; }
////
////            for (Entity tile : projection.mTargets) {
////
////                if (tile == movementManager.currentTile) { continue; }
////                Tile currentTile = tile.get(Tile.class);
////                if (currentTile.mUnit == unit) { continue; }
////                if (currentTile.mUnit == null) { continue; }
////
////                ActionManager projection2 = ActionManager.project(model, movementManager.currentTile, ability, tile);
////
////                boolean hasEntity = projection2.mAreaOfEffect.stream().anyMatch(entity -> {
////                    Tile toCheck = entity.get(Tile.class);
////                    return toCheck.mUnit != null;
////                });
////
////                if (!hasEntity) { continue; }
////                boolean acted = ActionManager.act(model, unit, ability, tile, true);
////                if (!acted) { continue; }
////                return true;
////            }
////        }
//        return false;
//    }
}
