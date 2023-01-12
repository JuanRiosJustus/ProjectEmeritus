package game.systems.actions;

import constants.Constants;
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
        getTilesWithinMovementRange(model, unit);

        boolean actionPanelOpen = model.state.getBoolean(Constants.ACTION_UI_SHOWING);
        boolean movementPanelOpen = model.state.getBoolean(Constants.MOVEMENT_UI_SHOWING);

        if (!actionPanelOpen && !movementPanelOpen) { return; }

        Mouse mouse = controller.getMouse();

        Entity selectedEntity = model.tryFetchingTileMousedAt();

//        Ability ability = AbilityPool.instance().getAbility(model.ui.getString(Constants.ABILITY_UI_SELECTEDABILITIY));
//        if (ability == null) { return; }
//        if (ability == null) { ability = tryGetRangeFromLongestRangeAbility(unit); }
//        if (ability == null) { logger.log("Invalid ability choice"); return; }

//        if (ability == null) { return; }

        // of combat panel is open
//        if (actionPanelOpen) {
//            Ability ability = AbilityPool.instance().getAbility(model.state.getString(Constants.ABILITY_UI_SELECTEDABILITIY));
//            gatherTilesWithinAbilityRange(model, unit, ability, tileToMoveTo);
//            getTilesWithinActionRange(model, unit, ability);
//            if (mouse.isPressed()) {
//                attackTileWithinAbilityRange(model, unit, ability, tileToMoveTo);
////                engine.model.ui.exitToMain();
//            }
//        }

        if (actionPanelOpen) {
//            Ability ability = AbilityPool.instance().getAbility(model.state.getString(Constants.ABILITY_UI_SELECTEDABILITIY));
            Ability ability = AbilityPool.instance().getAbility("Ingle");
            getTilesWithinActionRange(model, unit, selectedEntity, ability);
            if (mouse.isPressed()) {
                tryAttackingUnits(model, unit, selectedEntity, ability);
            }
        }

        // If the movement panel is open, handle it this way
        if (movementPanelOpen) {
            getTilesWithinMovementRange(model, unit);
            getTilesWithinMovementPath(model, unit, selectedEntity);
            if (mouse.isPressed()) {
                tryMovingUnit(model, unit, selectedEntity);
            }
        }
    }
}
