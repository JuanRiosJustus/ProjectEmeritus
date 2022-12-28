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

import static game.systems.actions.ActionUtils.*;

public class UserActionSystem {

    public static Logger logger = LoggerFactory.instance().logger(UserActionSystem.class);

    public static void handle(GameModel model, InputController controller, Entity unit) {
        boolean combatPanelOpen = model.ui.getBoolean(Constants.ABILITY_UI_SHOWING); //engine.model.ui.ability.isShowing();
        boolean movementPanelOpen = model.ui.getBoolean(Constants.MOVEMENT_UI_SHOWING); //engine.model.ui.movement.isShowing();

        if (!combatPanelOpen && !movementPanelOpen) { return; }

        Mouse mouse = controller.mouse();

        Entity tileToMoveTo = model.tryFetchingMousedTile();

        Ability ability = AbilityPool.instance().getAbility(model.ui.getString(Constants.ABILITY_UI_SELECTEDABILITIY));
        if (ability == null) { ability = tryGetRangeFromLongestRangeAbility(unit); }
        if (ability == null) { logger.log("Invalid ability choice"); return; }

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
            gatherTilesWithinMovementRange(model, unit, ability.range, tileToMoveTo);
            if (mouse.isPressed()) {
                moveToTileWithinMovementRange(model, unit, tileToMoveTo, logger);
//                engine.model.ui.exitToMain();
            }
        }
    }

//    public static void handle(GameModel model, Entity unit) {
//        boolean combatPanelOpen = engine.model.ui.ability.isShowing();
//        boolean movementPanelOpen = engine.model.ui.movement.isShowing();
//
//        if (!combatPanelOpen && !movementPanelOpen) { return; }
//
//        Mouse mouse = engine.model.input.mouse();
//
//        Entity tileToMoveTo = engine.model.game.model.tryFetchingMousedTile();
//
//        Ability ability = engine.model.ui.ability.getSelected();
//        if (ability == null) { ability = tryGetRangeFromLongestRangeAbility(engine, unit); }
//        if (ability == null) { logger.log("Invalid ability choice"); return; }
//
//        // of combat panel is open
//        if (combatPanelOpen) {
//            gatherTilesWithinAbilityRange(engine, unit, ability, tileToMoveTo);
//            if (mouse.isPressed()) {
//                attackTileWithinAbilityRange(engine, unit, ability, tileToMoveTo);
//                engine.model.ui.exitToMain();
//            }
//        }
//        // If the movement panel is open, handle it this way
//        if (movementPanelOpen) {
//            gatherTilesWithinMovementRange(engine, unit, ability.range, tileToMoveTo);
//            if (mouse.isPressed()) {
//                moveToTileWithinMovementRange(engine, unit, tileToMoveTo, logger);
//                engine.model.ui.exitToMain();
//            }
//        }
//    }
}
