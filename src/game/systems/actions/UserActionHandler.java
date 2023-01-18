package game.systems.actions;

import constants.GameStateKey;
import game.GameModel;
import game.entity.Entity;
import game.stores.pools.ability.Ability;
import game.stores.pools.ability.AbilityPool;
import input.InputController;
import input.Mouse;
import logging.Logger;
import logging.LoggerFactory;

public class UserActionHandler extends ActionHandler {

    public Logger logger = LoggerFactory.instance().logger(getClass());

    public void handle(GameModel model, InputController controller, Entity unit) {
        // Gets tiles within movement range if the entity does not already have them...
        // these tiles should be removed after their turn is over
        getTilesWithinJumpAndMovementRange(model, unit);

        boolean actionPanelOpen = model.state.getBoolean(GameStateKey.ACTION_UI_SHOWING);
        boolean movementPanelOpen = model.state.getBoolean(GameStateKey.MOVEMENT_UI_SHOWING);

        // if (!actionPanelOpen && !movementPanelOpen) { return; }

        Mouse mouse = controller.getMouse();

        Entity selected = model.tryFetchingTileMousedAt();

        // if (!actionPanelOpen && !movementPanelOpen) {
        //     Entity previous = (Entity) model.state.get(GameStateKey.PREVIOUSLY_SELECTED);
        //     Entity current = (Entity) model.state.get(GameStateKey.CURRENTLY_SELECTED);
        //     if (previous == null || current == null) { return; }
        //     Tile tile = previous.get(Tile.class);
        //     boolean clickingUnit = tile.unit != null;
        //     if (clickingUnit) {
        //         getTilesWithinMovementRange(model, unit);
        //         getTilesWithinMovementPath(model, unit, selectedEntity);
        //         logger.log("Inferencing Unit");
        //         model.state.set(GameStateKey.INFERENCING_MOVEMENT, true);
        //     }
        // }

        Ability ability = AbilityPool.instance().getAbility(model.state.getString(GameStateKey.ACTION_UI_SELECTED_ABILITY));

        if (actionPanelOpen) {
            if (ability == null) { return; }
            // ability = AbilityPool.instance().getAbility("Ingle");
            getTilesWithinActionRange(model, unit, selected, ability);
            if (mouse.isPressed()) { 
                tryAttackingUnits(model, unit, selected, ability);
            }
        }

        // If the movement panel is open, handle it this way
        if (movementPanelOpen) {
            getTilesWithinJumpAndMovementRange(model, unit);
            getTilesWithinJumpAndMovementPath(model, unit, selected);
            if (mouse.isPressed()) {
                tryMovingUnit(model, unit, selected);
            }
        }
    }
}
