package game.systems.actions;

import constants.Constants;
import game.GameModel;
import game.components.ActionManager;
import game.components.MoveSet;
import game.components.Movement;
import game.components.Tile;
import game.components.statistics.Statistics;
import game.entity.Entity;
import game.pathfinding.TilePathing;
import game.stores.pools.ability.Ability;
import logging.Logger;
import logging.LoggerFactory;
import utils.MathUtils;

import java.util.*;
import java.util.stream.Collectors;

import static game.pathfinding.TilePathing.getTilesInLineOfSight;

public class ActionHandler {

    private static final SplittableRandom random = new SplittableRandom();
    private static final Logger logger = LoggerFactory.instance().logger(ActionHandler.class);

    public static void moveUnitToTile(GameModel model, Entity unit, Entity tile) {
        // Check unit has not moved and is within movement range
        // Other tile validation stuff
        ActionManager manager = unit.get(ActionManager.class);
        if (tile == null || manager.moved) { return; }
        if (!manager.tilesWithinMovementRange.contains(tile)) { return; }
        if (!manager.tilesWithinMovementRangePath.contains(tile)) { return; }
        if (tile == manager.tileOccupying) { return; }
        Movement movement = unit.get(Movement.class);

        if (logger != null) { logger.log("{0} moving from {1} to {2}", unit, manager.tileOccupying, tile); }
        movement.move(model, unit, tile);
        manager.moved = true;

//        // Get all the items within the tiles to move to
//        for (Entity tile : manager.tilesWithinMovementRangePath) {
//            Inventory inventory = tile.get(Inventory.class);
//            if (inventory == null) { continue; }
//            // Get the items on the tile to the unit, then remove items completely from tile
//            Inventory unitsInventory = unitToMove.get(Inventory.class);
//            unitsInventory.addAll(inventory);
//            tile.remove(Inventory.class);
//            System.err.println("Removing item from tiles");
//        }
    }

    public static void gatherTilesWithinMovementRange(GameModel model, Entity unit, int range, Entity tile) {
        ActionManager manager = unit.get(ActionManager.class);

        // if the user didn't change anything about the state, not need to update
        if (tile == null || tile == manager.tileToMoveTo) { return; }
        Statistics stats = unit.get(Statistics.class);
        manager.tileToMoveTo = tile;

        // get all possible tiles within range
        TilePathing.getUnobstructedTilePath(model, manager.tileOccupying,
                stats.getScalarNode(Constants.MOVE).getTotal(), manager.tilesWithinMovementRange);

        // remove all tiles that are Structures/Units/etc...
        List<Entity> available = manager.tilesWithinMovementRange.stream()
                .filter(toCheck -> !toCheck.get(Tile.class).isStructureUnitOrWall()).toList();

        manager.tilesWithinMovementRange.clear();
        manager.tilesWithinMovementRange.addAll(available);
        manager.tilesWithinMovementRange.add(manager.tileOccupying); // allow self targeting
        // show all the tiles that could be attack from the longest range ability

        if (range == 0) { return; }

        // if the tile to move to is not within range, return
        if (!manager.tilesWithinMovementRange.contains(manager.tileToMoveTo)) {
            manager.tilesWithinAbilityRange.clear();
            return;
        }

        if (range > 0) {
            // populate the tiles to be shown with the ability
            getTilesInLineOfSight(model, manager.tileToMoveTo, range, manager.tilesWithinAbilityRange);
        }

        // populate the path tiles
        TilePathing.getTilesWithinPath(model, manager.tileOccupying, manager.tileToMoveTo,
                stats.getScalarNode(Constants.MOVE).getTotal(), manager.tilesWithinMovementRangePath);
    }

    public static void gatherTilesWithinAbilityRange(GameModel model, Entity unit, Ability ability, Entity tile) {
        ActionManager manager = unit.get(ActionManager.class);

        if (tile == null || manager.tileToAttackAt == tile) { return; }
        manager.tileToAttackAt = tile;

        // get tiles within LOS for the ability
        getTilesInLineOfSight(model, manager.tileOccupying, ability.range, manager.tilesWithinAbilityRange);

        // get tiles within LOS for the AOE... Does the target need to be within ability range? (Probably)
        if (ability.area == 0 && manager.tilesWithinAbilityRange.contains(tile)) {
            manager.tilesWithinAreaOfEffect.clear();
            manager.tilesWithinAreaOfEffect.add(tile);
        } else {
            getTilesInLineOfSight(model, tile, ability.area, manager.tilesWithinAreaOfEffect);
        }
    }

    public static void attackTileWithinAbilityRange(GameModel model, Entity unit, Ability ability, Entity tile) {
        // Check unit has not attacked and tile is within ability range
        ActionManager manager = unit.get(ActionManager.class);
        if (tile == null || ability == null || manager.attacked) { return; }
        if (!manager.tilesWithinAbilityRange.contains(tile)) { return; }

        // get all tiles within LOS and attack at them
        getTilesInLineOfSight(model, tile, ability.area, manager.tilesWithinAreaOfEffect);

        // start combat
        model.system.combat.startCombat(model, unit, ability, new ArrayList<>(manager.tilesWithinAreaOfEffect));
        manager.attacked = true;
    }

    public static void randomlyAttack(GameModel model, Entity unit) {
        Movement movement = unit.get(Movement.class);
        if (movement.isMoving()) { return; }
        // Get all the abilities the unit can use
        ActionManager manager = unit.get(ActionManager.class);
        Statistics stats = unit.get(Statistics.class);
        // get all the abilities into a map
        List<Ability> abilities = unit.get(MoveSet.class).getCopy();
        Collections.shuffle(abilities);
        Set<Entity> tilesWithinAbilityLOS = manager.tilesWithinAbilityRange;

        // consider all the abilities to use
        for (Ability ability : abilities) {

            // TODO Why can ability be null?
            if (ability == null) { continue; }

            // Don't attack self if not beneficial
            if (ability.friendlyFire && !beneficiallyEffectsUser(ability)) {
                continue;
            }

            // Get tiles within LOS based on the ability range
            getTilesInLineOfSight(model, manager.tileOccupying, ability.range, tilesWithinAbilityLOS);

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
                Set<Entity> tilesWithinAoeLOS = manager.tilesWithinAreaOfEffect;
                for (Entity tileToTarget : tilesWithinAbilityLOS) {

                    // ignore checking walls and structures
                    boolean isWall = tileToTarget.get(Tile.class).isWall();
                    boolean isStructure = tileToTarget.get(Tile.class).isStructure();
                    if (isWall || isStructure) { continue; }

                    // Get tiles within the los of the Aoe
                    getTilesInLineOfSight(model, tileToTarget, ability.area, tilesWithinAoeLOS);
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
        manager.attacked = true;
    }

    private static boolean beneficiallyEffectsUser(Ability ability) {
        return ability.energyDamage.base < 0 || ability.healthDamage.base < 0;
    }


    public static void moveTowardsEntityIfPossible(GameModel model, Entity unit) {
        // Ensure the entity is not already moving
        Movement movement = unit.get(Movement.class);
        if (movement.isMoving()) { return; }
        Statistics stats = unit.get(Statistics.class);
        ActionManager manager = unit.get(ActionManager.class);

        // Check if already adjacent to tile with an entity on it
        Set<Entity> cardinalTiles = new HashSet<>();
        // Get all the tiles adjacent to that tileWithTarget
        TilePathing.getCardinallyAdjacentTiles(model, manager.tileOccupying, cardinalTiles);

//        TilePathing.getCardinalTiles(model, manager.tileOccupying, cardinalTiles);
        boolean isCardinallyAdjacentToSomeUnit = cardinalTiles.stream()
                .filter(tile -> tile.get(Tile.class).unit != unit)
                .anyMatch(tile -> tile.get(Tile.class).unit != null);

        if (isCardinallyAdjacentToSomeUnit && random.nextBoolean()) {
            manager.moved = true;
            System.out.println("Already near some unit");
            return;
        }

        // Get tiles within the view. If there are is an entity, move towards
        int viewRange = (int) (stats.getScalarNode(Constants.MOVE).getTotal() * 1.5);
        Set<Entity> tilesWithinView = new HashSet<>();
        TilePathing.getTilesInLineOfSight(model, manager.tileOccupying, viewRange, tilesWithinView);
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
            Set<Entity> unobstructedTilesThatCanBeMovedTo = new HashSet<>();
            TilePathing.getUnobstructedTilePath(model, manager.tileOccupying, viewRange, unobstructedTilesThatCanBeMovedTo);

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
                    if (unobstructedTilesThatCanBeMovedTo.contains(cardinalTile)) {
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
                    for (Entity tileThatCanBeMovedTo : unobstructedTilesThatCanBeMovedTo) {
                        int localDiff = distance(tileThatCanBeMovedTo, manager.tileOccupying);
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
            gatherTilesWithinMovementRange(model, unit,
                    stats.getScalarNode(Constants.MOVE).getTotal(), tileToMoveTo);
            moveUnitToTile(model, unit, tileToMoveTo);
        }
        manager.moved = true;
    }

    public static int distance(Entity tile1, Entity tile2) {
        int rowDiff = MathUtils.diff(tile1.get(Tile.class).row, tile2.get(Tile.class).row);
        int colDiff = MathUtils.diff(tile1.get(Tile.class).column, tile2.get(Tile.class).column);
        return rowDiff + colDiff;
    }

    public static void randomlyMove(GameModel model, Entity unit) {
        Movement movement = unit.get(Movement.class);
        if (movement.isMoving()) { return; } // ensure not currently acting
        Statistics stats = unit.get(Statistics.class);
        ActionManager manager = unit.get(ActionManager.class);

        // Get tiles within the movement range
        TilePathing.getUnobstructedTilePath(model, manager.tileOccupying,
                stats.getScalarNode(Constants.MOVE).getTotal(), manager.tilesWithinMovementRange);

        // select a random tile to move to
        List<Entity> candidates = manager.tilesWithinMovementRange.stream().toList();
        Entity randomTile = candidates.get(random.nextInt(candidates.size()));

        // if the random tile is current tile, don't move (Moving to same tile causes exception in animation track)
        if (randomTile != manager.tileOccupying) {
            // regather tiles
            gatherTilesWithinMovementRange(model, unit, stats.getScalarNode(Constants.MOVE).getTotal(), randomTile);
            moveUnitToTile(model, unit, randomTile);
        }
        manager.moved = true;
    }

    public static Ability tryGetRangeFromLongestRangeAbility(Entity unit) {

        Optional<Ability> furthest = unit.get(MoveSet.class)
                .getCopy()
                .stream()
                .max(Comparator.comparingInt(o -> o.range));
        return furthest.orElse(null);
    }
}
