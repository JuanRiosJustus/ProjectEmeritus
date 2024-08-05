package main.game.systems.actions.behaviors;

//import main.constants.Tuple;
import main.game.components.ActionManager;
import main.game.components.MovementManager;
import main.game.components.Statistics;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ability.Ability;
import main.game.stores.pools.ability.AbilityPool;
import main.game.systems.MovementSystem;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Behavior {
    protected static final ELogger logger = ELoggerFactory.getInstance().getELogger(Behavior.class);
    protected static final SplittableRandom random = new SplittableRandom();
    protected static final MovementSystem movementSystem = new MovementSystem();
    public abstract void move(GameModel model, Entity unit);
    public abstract void attack(GameModel model, Entity unit);
    protected static Map<String, List<Object[]>> getAbilityToTargetAndUnitsMap(GameModel model, Entity entity) {

//        Statistics statistics = entity.get(Statistics.class);
//        MovementManager movementManager = entity.get(MovementManager.class);
//
//        Map<String, List<Object[]>> mapping = new HashMap<>();
//
//        // Go through all the abilities.
//        for (String abilityName : statistics.getAbilities()) {
//            Ability ability = AbilityPool.getInstance().getAbility(abilityName);
//            ActionManager projection = ActionManager.project(model, movementManager.currentTile, ability, null);
//
//            // TODO Determine if ability is for friendly units or hostile units
//
//            // Check that targeting this tile yields unique entities to target
//            for (Entity target : projection.mTargets) {
//                ActionManager projection2 = ActionManager.project(model, movementManager.currentTile, ability, target);
//
//                // Check targets within abilities range and aoe.., los is subset of range, included for completeness
//                Set<Entity> targets = new HashSet<>();
//                targets.addAll(projection2.mTargets);
//                targets.addAll(projection2.mAreaOfEffect);
//                targets.addAll(projection2.mLineOfSight);
//
//                // Get units associated with this ability projection
//                Set<Entity> units = targets.stream()
//                        .filter(tile -> tile.get(Tile.class).mUnit != null)
////                        .map(tile -> tile.get(Tile.class).unit)
//                        .collect(Collectors.toSet());
//
//                if (units.isEmpty()) { continue; }
//
//                // NOTE! the first value in duple is the target, and the second value are the impacted units
//                List<Object[]> entry = mapping.getOrDefault(abilityName, new ArrayList<>());
//                entry.add(new Object[]{ target, units });
//                mapping.put(abilityName, entry);
//            }
//        }
//        return mapping;
        return null;
    }
}
