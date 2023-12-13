package main.game.systems.actions.behaviors;

//import main.constants.Tuple;
import main.game.components.AbilityManager;
import main.game.components.MovementManager;
import main.game.components.Summary;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.action.Ability;
import main.game.stores.pools.action.AbilityPool;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Behavior {
    protected static final ELogger logger = ELoggerFactory.getInstance().getELogger(Behavior.class);
    protected static final SplittableRandom random = new SplittableRandom();
    public abstract void move(GameModel model, Entity unit);
    public abstract void attack(GameModel model, Entity unit);
    protected static Map<String, List<Object[]>> getAbilityToTargetAndUnitsMap(GameModel model, Entity entity) {

        Summary summary = entity.get(Summary.class);
        MovementManager movementManager = entity.get(MovementManager.class);

        Map<String, List<Object[]>> mapping = new HashMap<>();

        // Go through all the abilities.
        for (String abilityName : summary.getAbilities()) {
            Ability ability = AbilityPool.getInstance().get(abilityName);
            AbilityManager projection = AbilityManager.project(model, movementManager.currentTile, ability, null);

            // TODO Determine if ability is for friendly units or hostile units

            // Check that targeting this tile yields unique entities to target
            for (Entity target : projection.targets) {
                AbilityManager projection2 = AbilityManager.project(model, movementManager.currentTile, ability, target);

                // Check targets within abilities range and aoe.., los is subset of range, included for completeness
                Set<Entity> targets = new HashSet<>();
                targets.addAll(projection2.targets);
                targets.addAll(projection2.aoe);
                targets.addAll(projection2.los);

                // Get units associated with this ability projection
                Set<Entity> units = targets.stream()
                        .filter(tile -> tile.get(Tile.class).unit != null)
//                        .map(tile -> tile.get(Tile.class).unit)
                        .collect(Collectors.toSet());

                if (units.isEmpty()) { continue; }

                // NOTE! the first value in duple is the target, and the second value are the impacted units
                List<Object[]> entry = mapping.getOrDefault(abilityName, new ArrayList<>());
                entry.add(new Object[]{ target, units });
                mapping.put(abilityName, entry);
            }
        }
        return mapping;
    }
}
