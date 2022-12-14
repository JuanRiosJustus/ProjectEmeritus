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

import static game.systems.actions.ActionHandler.*;

public class UserActionHandler {

    public Logger logger = LoggerFactory.instance().logger(getClass());

    public void handle(GameModel model, InputController controller, Entity unit) {
        boolean combatPanelOpen = model.ui.getBoolean(Constants.ABILITY_UI_SHOWING);
        boolean movementPanelOpen = model.ui.getBoolean(Constants.MOVEMENT_UI_SHOWING);

        if (!combatPanelOpen && !movementPanelOpen) { return; }

        Mouse mouse = controller.getMouse();

        Entity tileToMoveTo = model.tryFetchingTileMousedAt();

        Ability ability = AbilityPool.instance().getAbility(model.ui.getString(Constants.ABILITY_UI_SELECTEDABILITIY));
//        if (ability == null) { return; }
//        if (ability == null) { ability = tryGetRangeFromLongestRangeAbility(unit); }
//        if (ability == null) { logger.log("Invalid ability choice"); return; }

//        if (ability == null) { return; }
        // of combat panel is open
        if (combatPanelOpen) {
            gatherTilesWithinAbilityRange(model, unit, ability, tileToMoveTo);
            if (mouse.isPressed()) {
                attackTileWithinAbilityRange(model, unit, ability, tileToMoveTo);
//                engine.model.ui.exitToMain();
            }
        }
        // If the movement panel is open, handle it this way
        if (movementPanelOpen) {
            int range = ability == null ? -1 : ability.range;
            gatherTilesWithinMovementRange(model, unit, range, tileToMoveTo);
            if (mouse.isPressed()) {
                moveUnitToTile(model, unit, tileToMoveTo);
//                engine.model.ui.exitToMain();
            }
        }
    }
}
