package main.game.systems;

import main.game.components.ActionManager;
import main.game.components.MovementManager;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.PathBuilder;
import main.game.stores.pools.ability.Ability;

import java.util.*;

public class ActionSystem {

    public boolean act(GameModel model, Entity unit, Ability ability, Entity target, boolean execute) {
        ActionManager actionManager = unit.get(ActionManager.class);
        if (actionManager.mActed || (actionManager.shouldNotUpdate(model, target) && !execute)) { return false; }

        MovementManager movementManager = unit.get(MovementManager.class);
        Set<Entity> visionRange = PathBuilder.newBuilder().inVisionRange(
                model,
                movementManager.currentTile,
                ability.range
        );

        LinkedList<Entity> lineOfSight = PathBuilder.newBuilder().inLineOfSight(
                model,
                movementManager.currentTile,
                target
        );

        // Remove tiles out of range
//        List<Entity> outOfRange = new ArrayList<>();
//        for (Entity tile : lineOfSight) {
//            if (visionRange.contains(tile)) { continue; }
//            outOfRange.add(tile);
//        }
//        for (Entity tile : outOfRange) {
//            lineOfSight.remove(tile);
//        }

        actionManager.setTargets(visionRange);
        actionManager.setLos(new HashSet<>(lineOfSight));

        if (visionRange.contains(target)) {
            Set<Entity> aoe = PathBuilder.newBuilder().inVisionRange(
                    model,
                    target,
                    ability.area
            );

            actionManager.setAoe(aoe);
        } else {
            actionManager.setAoe(new HashSet<>());
        }

        actionManager.setSelected(ability);

        // try executing action
        if (target == null || !execute) { return false; }
//        boolean started = model.system.combat.startCombat(model, unit, ability, actionManager.aoe);
        boolean started = true;
        if (!started) { return false; }
//        actionManager.acted = true;
        return true;
    }
}
