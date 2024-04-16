package main.game.components;

import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.PathBuilder;
import main.game.stores.pools.ability.Ability;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AbilityManager extends Component {

    public Entity targeting = null;
    public boolean acted = false;
    public Ability preparing = null;

    public final Set<Entity> targets = ConcurrentHashMap.newKeySet();
    public final Set<Entity> aoe = ConcurrentHashMap.newKeySet();
    public final Set<Entity> los = ConcurrentHashMap.newKeySet();

    private void setAoe(Set<Entity> set) {
        aoe.clear();
        aoe.addAll(set);
    }

    private void setLos(Set<Entity> set) {
        los.clear();
        los.addAll(set);
    }

    private void setTargets(Set<Entity> set) {
        targets.clear();
        targets.addAll(set);
    }

    public void reset() {
        aoe.clear();
        los.clear();
        targets.clear();
        acted = false;
        preparing = null;
        previouslyTargeting = null;
    }

    public static AbilityManager project(GameModel model, Entity start, Ability ability, Entity target) {
        // "Start == null"; if the unit was not set, it should, start is null, it should not be able to attack
        if (ability == null || start == null) { return null; }

        AbilityManager result = new AbilityManager();
        // get tiles within LOS for the ability
        result.setTargets(PathBuilder.newBuilder()
                .setModel(model)
                .setStart(start)
                .setRange(ability.range)
                .getTilesInRange());

        if (result.targets.isEmpty()) { return result; }
        if (target == null) { return result; }

        //TODO maybe keep this? If we don't, shows the tiles that were hovered before being Out of range
        if (!result.targets.contains(target)) { return result; }

        result.targeting = target;

        if (ability.range > 0)  {
            result.setLos(PathBuilder.newBuilder()
                    .setModel(model)
                    .setStart(start)
                    .setEnd(target)
                    .setRange(ability.range)
                    .getTilesInLineOfSight());
        }

        if (ability.area > 0) {
            result.setAoe(PathBuilder.newBuilder()
                    .setModel(model)
                    .setStart(target)
                    .setRange(ability.area - 1)
                    .getTilesInRange());
        }
        return result;
    }

    private Entity previouslyTargeting = null;
    private boolean shouldNotUpdate(GameModel model, Entity targeting) {
        boolean isSameTarget = previouslyTargeting == targeting;
        if (!isSameTarget) {
//            System.out.println("Waiting for user action input... " + previouslyTargeting + " vs " + targeting);
        }
        previouslyTargeting = targeting;
        return isSameTarget && mOwner.get(UserBehavior.class) != null;
    }

    public static boolean act(GameModel model, Entity unit, Ability ability, Entity target, boolean execute) {
        AbilityManager abilityManager = unit.get(AbilityManager.class);
        if (abilityManager.acted || (abilityManager.shouldNotUpdate(model, target) && !execute)) { return false; }

        // Get ranges for the ability
        MovementManager movementManager = unit.get(MovementManager.class);
        AbilityManager projection = project(model, movementManager.currentTile, ability, target);
        abilityManager.setTargets(projection.targets);
        abilityManager.setAoe(projection.aoe);
        abilityManager.setLos(projection.los);
        abilityManager.preparing = ability;

        // try committing action
        if (target == null || !execute) { return false; }
        boolean started = model.system.combat.startCombat(model, unit, ability, abilityManager.aoe);
        if (!started) { return false; }
        abilityManager.acted = true;
        return true;
    }
}
