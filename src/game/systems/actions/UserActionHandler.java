package game.systems.actions;

import constants.GameStateKey;
import game.GameModel;
import game.entity.Entity;
import game.stores.pools.ability.Ability;
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

        boolean actionPanelOpen = model.state.getBoolean(GameStateKey.ACTION_PANEL_SHOWING);
        boolean movementPanelOpen = model.state.getBoolean(GameStateKey.MOVEMENT_PANEL_SHOWING);

        // if (!actionPanelOpen && !movementPanelOpen) { return; }

        Mouse mouse = controller.getMouse();

        Entity selected = model.tryFetchingTileMousedAt();

        if (actionPanelOpen) {
            Ability ability = (Ability) model.state.get(GameStateKey.ACTION_PANEL_SELECTED_ACTION);
            if (ability == null) { return; }
            getTilesWithinActionRange(model, unit, selected, ability);
            if (mouse.isPressed()) { 
                tryAttackingUnits(model, unit, selected, ability);
            }
        }

        if (movementPanelOpen) {
            getTilesWithinJumpAndMovementRange(model, unit);
            getTilesWithinJumpAndMovementPath(model, unit, selected);
            if (mouse.isPressed()) {
                tryMovingUnit(model, unit, selected);
            }
        }
    }
}
