package game.systems.actions;

import constants.Constants;
import game.GameModel;
import game.collectibles.Gem;
import game.components.*;
import game.components.behaviors.UserBehavior;
import game.components.statistics.Statistics;
import game.entity.Entity;
import game.pathfinding.TilePathing;
import game.stores.pools.ability.Ability;
import logging.Logger;
import logging.LoggerFactory;
import utils.MathUtils;

import java.util.*;
import java.util.stream.Collectors;


public class ActionHandler {

    private static final SplittableRandom random = new SplittableRandom();
    private static final Logger logger = LoggerFactory.instance().logger(ActionHandler.class);

    public static void tryMovingUnit(GameModel model, Entity unit, Entity toMoveTo) {
        // Check unit has not moved and is within movement range
        // Other tile validation stuff
        MovementManager movement = unit.get(MovementManager.class);
        if (movement.moved) { return; }
        if (toMoveTo == null || toMoveTo == movement.tile) { return; }

        boolean inRange = movement.tilesWithinMovementRange.contains(toMoveTo);
        boolean inPath = movement.tilesWithinMovementPath.contains(toMoveTo);

        if (!inPath || !inRange) { return; }
        if (logger != null) { logger.log("{0} moving from {1} to {2}", unit, movement.tile, toMoveTo); }

        MovementTrack movementTrack = unit.get(MovementTrack.class);
        movementTrack.move(model, unit, toMoveTo);
        movement.moved = true;

        Tile tileMovedTo = toMoveTo.get(Tile.class);
        if (tileMovedTo.getGem() != null) {
            Gem gem = tileMovedTo.getGem();
            Statistics stats = unit.get(Statistics.class);
            stats.addBonusStats(gem, gem.statistics);

            tileMovedTo.setGem(null);
        }

        if (unit.get(UserBehavior.class) != null) { model.state.set(Constants.RESET_UI, true); }
    }


    public static void moveUnitToTile(GameModel model, Entity unit, Entity tile) {
        // Check unit has not moved and is within movement range
        // Other tile validation stuff
        MovementManager movement = unit.get(MovementManager.class);

        if (tile == null || movement.moved) { return; }
        if (!movement.tilesWithinMovementRange.contains(tile)) { return; }
        if (!movement.tilesWithinMovementPath.contains(tile)) { return; }
        if (tile == movement.tile) { return; }
        MovementTrack movementTrack = unit.get(MovementTrack.class);

        if (logger != null) { logger.log("{0} moving from {1} to {2}", unit, movement.tile, tile); }
        movementTrack.move(model, unit, tile);
        movement.moved = true;
    }

    public static void getTilesWithinJumpAndMovementPath(GameModel model, Entity unit, Entity selected) {
        MovementManager movement = unit.get(MovementManager.class);
        if (movement.moved) { return; }

        Statistics stats = unit.get(Statistics.class);
        int move = stats.getScalarNode(Constants.MOVE).getTotal();
        int jump = stats.getScalarNode(Constants.JUMP).getTotal();

        Deque<Entity> tilesWithinMovementPath = movement.tilesWithinMovementPath;
        Entity starting = movement.tile;

        TilePathing.getTilesWithinJumpAndMovementPath(model, starting, selected, move, jump, tilesWithinMovementPath);
    }

    public static void getTilesWithinJumpAndMovementRange(GameModel model, Entity unit) {
        MovementManager movement = unit.get(MovementManager.class);
        if (movement.moved) { return; }

        ActionManager action = unit.get(ActionManager.class);
        action.tilesWithinActionRange.clear();
        action.tilesWithinActionAOE.clear();

        Statistics stats = unit.get(Statistics.class);
        int move = stats.getScalarNode(Constants.MOVE).getTotal();
        int jump = stats.getScalarNode(Constants.JUMP).getTotal();

        Set<Entity> tilesWithinMovementRange = movement.tilesWithinMovementRange;
        TilePathing.getTilesWithinJumpAndMovementRange(model, movement.tile, move, jump, tilesWithinMovementRange);
    }

    public static void getTilesWithinActionRange(GameModel model, Entity unit, Entity target, Ability ability) {
        ActionManager action = unit.get(ActionManager.class);
        if (action.acted) { return; }
        if (target == null || ability == null) { return; }

        MovementManager movement = unit.get(MovementManager.class);

        // get tiles within LOS for the ability
        Set<Entity> tilesWithinActionRange = action.tilesWithinActionRange;
        Entity current = movement.tile;
        TilePathing.getTilesWithinActionRange(model, current, ability.range, tilesWithinActionRange);

        if (tilesWithinActionRange.isEmpty()) { return; }

        movement.tilesWithinMovementPath.clear();
        movement.tilesWithinMovementRange.clear();

        action.targeting = target;

        if (ability.range >= 0) {
            Set<Entity> tilesWithinLOS = action.tilesWithinActionLOS;
            TilePathing.getShadowTracing(model, movement.tile, action.targeting, ability.range, tilesWithinLOS);
        }

        if (ability.area >= 0) {
            Set<Entity> tilesWithinAOE = action.tilesWithinActionAOE;
            TilePathing.getTilesWithinActionRange(model, target, ability.area, tilesWithinAOE);
        }
    }

    public static void tryAttackingUnits(GameModel model, Entity unit, Entity tile, Ability ability) {
        ActionManager action = unit.get(ActionManager.class);
        if (action.acted) { return; }

        boolean inRange = action.tilesWithinActionRange.contains(tile); //action.tilesWithinAbilityRange.contains(tile);
        if (!inRange) { return; }

        MovementManager movement = unit.get(MovementManager.class);
        // get all tiles within LOS and attack at them
        Set<Entity> tilesWithinAOE = action.tilesWithinActionAOE;
        TilePathing.getTilesWithinActionLineOfSight(model, tile, ability.area, tilesWithinAOE);

        // start combat
        model.system.combat.startCombat(model, unit, ability, new ArrayList<>(action.tilesWithinActionAOE));
        action.acted = true;
    }


    public static void attackTileWithinAbilityRange(GameModel model, Entity unit, Ability ability, Entity tile) {
        // Check unit has not attacked and tile is within ability range
        ActionManager manager = unit.get(ActionManager.class);
        if (tile == null || ability == null || manager.acted) { return; }
        if (!manager.tilesWithinActionRange.contains(tile)) { return; }

        // get all tiles within LOS and attack at them
        TilePathing.getTilesWithinActionLineOfSight(model, tile, ability.area, manager.tilesWithinActionAOE);

        // start combat
        model.system.combat.startCombat(model, unit, ability, new ArrayList<>(manager.tilesWithinActionAOE));
        manager.acted = true;
    }

    public static void randomlyAttack(GameModel model, Entity unit) {
        MovementTrack movementTrack = unit.get(MovementTrack.class);
        if (movementTrack.isMoving()) { return; }
        // Get all the abilities the unit can use
        ActionManager manager = unit.get(ActionManager.class);
        MovementManager movement = unit.get(MovementManager.class);
        Statistics stats = unit.get(Statistics.class);
        // get all the abilities into a map
        List<Ability> abilities = unit.get(MoveSet.class).getCopy();
        Collections.shuffle(abilities);
        Set<Entity> tilesWithinAbilityLOS = manager.tilesWithinActionRange;

        // consider all the abilities to use
        for (Ability ability : abilities) {

            // TODO Why can ability be null?
            if (ability == null) { continue; }

            // Don't attack self if not beneficial
            if (ability.friendlyFire && !beneficiallyEffectsUser(ability)) {
                continue;
            }

            // Get tiles within LOS based on the ability range
            TilePathing.getTilesWithinActionLineOfSight(model, movement.tile, ability.range, tilesWithinAbilityLOS);

            // tile must have a unit and is not a wall or structure. Dont target self unless is status ability
            List<Entity> tilesWithEntities = tilesWithinAbilityLOS.stream()
                    .filter(tile -> tile.get(Tile.class).unit != null)
                    .filter(tile -> !tile.get(Tile.class).isWall())
                    .filter(tile -> !tile.get(Tile.class).isStructure())
                    .filter(tile -> (ability.friendlyFire || tile.get(Tile.class).unit != unit))
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
                    TilePathing.getTilesWithinActionLineOfSight(model, tileToTarget, ability.area, tilesWithinAoeLOS);
                    Optional<Entity> tileWithEntity = tilesWithinAoeLOS.stream()
                            .filter(tile -> tile.get(Tile.class).unit != null)
                            .filter(tile -> !tile.get(Tile.class).isWall())
                            .filter(tile -> !tile.get(Tile.class).isStructure())
                            .filter(tile -> (ability.friendlyFire || tile.get(Tile.class).unit != unit))
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

    private static boolean beneficiallyEffectsUser(Ability ability) {
        return ability.energyDamage.base < 0 || ability.healthDamage.base < 0;
    }


    public static void moveTowardsEntityIfPossible(GameModel model, Entity unit) {
        // Ensure the entity is not already moving
        MovementTrack movementTrack = unit.get(MovementTrack.class);
        if (movementTrack.isMoving()) { return; }
        Statistics stats = unit.get(Statistics.class);
        MovementManager movement = unit.get(MovementManager.class);

        // Check if already adjacent to tile with an entity on it
        Set<Entity> cardinalTiles = new HashSet<>();
        // Get all the tiles adjacent to that tileWithTarget
        TilePathing.getCardinallyAdjacentTiles(model, movement.tile, cardinalTiles);

//        TilePathing.getCardinalTiles(model, manager.tileOccupying, cardinalTiles);
        boolean isCardinallyAdjacentToSomeUnit = cardinalTiles.stream()
                .filter(tile -> tile.get(Tile.class).unit != unit)
                .anyMatch(tile -> tile.get(Tile.class).unit != null);

        if (isCardinallyAdjacentToSomeUnit && random.nextBoolean()) {
            movement.moved = true;
            System.out.println("Already near some unit");
            return;
        }

        // Get tiles within the view. If there are is an entity, move towards
        int viewRange = (int) (stats.getScalarNode(Constants.MOVE).getTotal() * 1.5);
        Set<Entity> tilesWithinView = new HashSet<>();
        TilePathing.getTilesWithinActionLineOfSight(model, movement.tile, viewRange, tilesWithinView);
        List<Entity> tilesWithTargets = new ArrayList<>(tilesWithinView.stream()
                .filter(tile -> tile.get(Tile.class).unit != null)
                .filter(tile -> tile.get(Tile.class).unit != unit)
                .toList());

        Collections.shuffle(tilesWithTargets);

        if (tilesWithTargets.isEmpty()) {
            // Move randomly somewhere if there are no targets in view range
            logger.error("Moving Unit randomly");
            randomlyMove(model, unit);
        } else {

            // Get tiles closest to the entity found
            int jump = stats.getScalarNode(Constants.JUMP).getTotal();
            int move = stats.getScalarNode(Constants.MOVE).getTotal();
            Set<Entity> canMoveTo = movement.tilesWithinMovementRange;
            TilePathing.getTilesWithinJumpAndMovementRange(model, movement.tile, move, jump, canMoveTo);

            // Check the cardinal tiles of target, if they can be moved to
            Collections.shuffle(tilesWithTargets);
            Entity tileToMoveTo = null;

            for (Entity tileWithTarget : tilesWithTargets) {
                if (tileToMoveTo != null) { continue; }

                // Get all the tiles adjacent to that tileWithTarget
                TilePathing.getCardinallyAdjacentTiles(model, tileWithTarget, cardinalTiles);

                for (Entity cardinalTile : cardinalTiles) {
                    Tile details = cardinalTile.get(Tile.class);
                    if (details.isStructureUnitOrWall()) { continue; }
                    // Stop at the very first tile we can move to
                    if (canMoveTo.contains(cardinalTile)) {
                        tileToMoveTo = cardinalTile;
                    }
                }
            }

            // No way to move to a cardinal tile, get the closest tile
            if (tileToMoveTo == null) {
                // Chance to randomly move
                if (random.nextBoolean()) {
                    logger.error("Moving Unit randomly");
                    randomlyMove(model, unit);
                } else {
                    // Get a tile thats closer
                    Entity tileWithLowestDiff = null;
                    int diff = Integer.MAX_VALUE;
                    for (Entity tileThatCanBeMovedTo : canMoveTo) {
                        int localDiff = distance(tileThatCanBeMovedTo, movement.tile);
                        if (localDiff < diff) {
                            diff = localDiff;
                            tileWithLowestDiff = tileThatCanBeMovedTo;
                        }
                    }
                    if (tileWithLowestDiff != null) { tileToMoveTo = tileWithLowestDiff; }
                    System.err.println("this is interesting");
                }
            }

            // Move to the tile that was found
//            gatherTilesWithinMovementRange(model, unit,
//                    stats.getScalarNode(Constants.MOVE).getTotal(), tileToMoveTo);
            getTilesWithinJumpAndMovementRange(model, unit);
//            moveUnitToTile(model, unit, tileToMoveTo);
        }
        movement.moved = true;
    }

    public static int distance(Entity tile1, Entity tile2) {
        int rowDiff = MathUtils.diff(tile1.get(Tile.class).row, tile2.get(Tile.class).row);
        int colDiff = MathUtils.diff(tile1.get(Tile.class).column, tile2.get(Tile.class).column);
        return rowDiff + colDiff;
    }

    public static void randomlyMove(GameModel model, Entity unit) {
        MovementTrack movementTrack = unit.get(MovementTrack.class);
        if (movementTrack.isMoving()) { return; } // ensure not currently acting
        Statistics stats = unit.get(Statistics.class);
        MovementManager movement = unit.get(MovementManager.class);

        // Get tiles within the movement range
        int move = stats.getScalarNode(Constants.MOVE).getTotal();
        int jump = stats.getScalarNode(Constants.JUMP).getTotal();
        Set<Entity> withinMovementRange = movement.tilesWithinMovementRange;
        TilePathing.getTilesWithinJumpAndMovementRange(model, movement.tile, move, jump, withinMovementRange);

        // select a random tile to move to
        List<Entity> candidates = movement.tilesWithinMovementRange.stream().toList();
        Entity randomTile = candidates.get(random.nextInt(candidates.size()));

        // if the random tile is current tile, don't move (Moving to same tile causes exception in animation track)
        if (randomTile != movement.tile) {
            // regather tiles
//            gatherTilesWithinMovementRange(model, unit, stats.getScalarNode(Constants.MOVE).getTotal(), randomTile);
            getTilesWithinJumpAndMovementRange(model, unit);
            getTilesWithinJumpAndMovementPath(model, unit, randomTile);
            tryMovingUnit(model, unit, randomTile);
//            moveUnitToTile(model, unit, randomTile);
        }
        movement.moved = true;
    }

    public Ability tryGetRangeFromLongestRangeAbility(Entity unit) {

        Optional<Ability> furthest = unit.get(MoveSet.class)
                .getCopy()
                .stream()
                .max(Comparator.comparingInt(o -> o.range));
        return furthest.orElse(null);
    }
}
