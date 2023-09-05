package main.game.components;

import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.PathBuilder;
import main.game.stores.pools.action.Action;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ActionManager extends Component {

    public Entity targeting = null;
    public boolean acted = false;
    public Action preparing = null;

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
        preparing = null;
        previouslyTargeting = null;
    }

    public static ActionManager project(GameModel model, Entity start, Action action, Entity target) {
        if (action == null) { return null; }

        ActionManager result = new ActionManager();
        // get tiles within LOS for the ability
        result.setRange(PathBuilder.newBuilder()
                .setModel(model)
                .setStart(start)
                .setRange(action.range)
                .getTilesInRange());

        if (result.range.isEmpty()) { return result; }
        if (target == null) { return result; }

        //TODO maybe keep this? If we don't, shows the tiles that were hovered before being Out of range
        if (!result.range.contains(target)) { return result; }

        result.targeting = target;

        if (action.range > 0)  {
            result.setSight(PathBuilder.newBuilder()
                    .setModel(model)
                    .setStart(start)
                    .setEnd(target)
                    .setRange(action.range)
                    .getTilesInLineOfSight());
        }

        if (action.area > 0) {
            result.setArea(PathBuilder.newBuilder()
                    .setModel(model)
                    .setStart(target)
                    .setRange(action.area - 1)
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

    public static boolean act(GameModel model, Entity unit, Action action, Entity target, boolean execute) {
        ActionManager actionManager = unit.get(ActionManager.class);
        if (actionManager.acted || (actionManager.shouldNotUpdate(model, target) && !execute)) { return false; }

        // Get ranges for the ability
        MovementManager movementManager = unit.get(MovementManager.class);
        ActionManager projection = project(model, movementManager.currentTile, action, target);
        actionManager.setRange(projection.range);
        actionManager.setArea(projection.area);
        actionManager.setSight(projection.sight);
        actionManager.preparing = action;

        // try committing action
        if (target == null || !execute) { return false; }
        boolean started = model.system.combat.startCombat(model, unit, action, actionManager.area);
        if (!started) { return false; }
        actionManager.acted = true;
        return true;
    }
}
