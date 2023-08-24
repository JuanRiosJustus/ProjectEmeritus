package main.game.components;

import main.game.components.behaviors.AiBehavior;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.PathBuilder;
import main.game.stores.pools.ability.Ability;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Action extends Component {

    public Entity targeting = null;
    public boolean acted = false;
    public Ability action = null;

    public final Set<Entity> range = ConcurrentHashMap.newKeySet();
    public final Set<Entity> area = ConcurrentHashMap.newKeySet();
    public final Set<Entity> sight = ConcurrentHashMap.newKeySet();

    private void setArea(Set<Entity> set) {
        area.clear();
        area.addAll(set);
    }

    private void setSight(Set<Entity> set) {
        sight.clear();
        sight.addAll(set);
    }

    private void setRange(Set<Entity> set) {
        range.clear();
        range.addAll(set);
    }

    public void reset() {
        area.clear();
        sight.clear();
        range.clear();
        acted = false;
        action = null;
        previouslyTargeting = null;
    }

    public static Action project(GameModel model, Entity start, Ability ability, Entity target) {
        if (ability == null) { return null; }

        Action result = new Action();
        // get tiles within LOS for the ability
        result.setRange(PathBuilder.newBuilder()
                .setModel(model)
                .setStart(start)
                .setRange(ability.range)
                .getTilesInRange());

        if (result.range.isEmpty()) { return result; }
        if (target == null) { return result; }

        //TODO maybe keep this? If we don't, shows the tiles that were hovered before being Out of range
        if (!result.range.contains(target)) { return result; }

        result.targeting = target;

        if (ability.range > 0)  {
            result.setSight(PathBuilder.newBuilder()
                    .setModel(model)
                    .setStart(start)
                    .setEnd(target)
                    .setRange(ability.range)
                    .getTilesInLineOfSight());
        }

        if (ability.area > 0) {
            result.setArea(PathBuilder.newBuilder()
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
        return isSameTarget && owner.get(UserBehavior.class) != null;
    }

    public static boolean act(GameModel model, Entity unit, Ability ability, Entity target, boolean execute) {
        Action action = unit.get(Action.class);
        if (action.acted || (action.shouldNotUpdate(model, target) && !execute)) { return false; }

        // Get ranges for the ability
        Movement movement = unit.get(Movement.class);
        Action projection = project(model, movement.currentTile, ability, target);
        action.setRange(projection.range);
        action.setArea(projection.area);
        action.setSight(projection.sight);
        action.action = ability;

        // try committing action
        if (target == null || !execute) { return false; }
        boolean started = model.system.combat.startCombat(model, unit, ability, action.area);
        if (!started) { return false; }
        action.acted = true;
        return true;
    }
}
