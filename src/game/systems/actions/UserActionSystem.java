package game.systems.actions;

import engine.EngineController;
import game.entity.Entity;
import game.stores.pools.ability.Ability;
import input.Mouse;
import logging.Logger;
import logging.LoggerFactory;

import static game.systems.actions.ActionUtils.*;

public class UserActionSystem {

    public static Logger logger = LoggerFactory.instance().logger(UserActionSystem.class);

    public static void handle(EngineController engine, Entity unit) {
        boolean combatPanelOpen = engine.model.ui.ability.isShowing();
        boolean movementPanelOpen = engine.model.ui.movement.isShowing();

        if (!combatPanelOpen && !movementPanelOpen) { return; }

        Mouse mouse = engine.model.input.mouse();

        Entity tileToMoveTo = engine.model.game.model.tryFetchingMousedTile();

        Ability ability = engine.model.ui.ability.getSelected();
        if (ability == null) { ability = tryGetRangeFromLongestRangeAbility(engine, unit); }
        if (ability == null) { logger.log("Invalid ability choice"); return; }

        // of combat panel is open
        if (combatPanelOpen) {
            gatherTilesWithinAbilityRange(engine, unit, ability, tileToMoveTo);
            if (mouse.isPressed()) {
                attackTileWithinAbilityRange(engine, unit, ability, tileToMoveTo);
                engine.model.ui.exitToMain();
            }
        }
        // If the movement panel is open, handle it this way
        if (movementPanelOpen) {
            gatherTilesWithinMovementRange(engine, unit, ability.range, tileToMoveTo);
            if (mouse.isPressed()) {
                moveToTileWithinMovementRange(engine, unit, tileToMoveTo, logger);
                engine.model.ui.exitToMain();
            }
        }
    }
}
