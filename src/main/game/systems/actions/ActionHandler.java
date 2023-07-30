package main.game.systems.actions;

import java.awt.Color;
import java.util.Map;

import main.constants.ColorPalette;
import main.constants.Constants;
import main.constants.GameStateKey;
import main.engine.Engine;
import main.game.components.*;
import main.game.components.behaviors.AiBehavior;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ability.Ability;
import main.game.systems.actions.behaviors.BehaviorUtils;
import main.game.systems.actions.behaviors.AggressiveAttacker;
import main.game.systems.actions.behaviors.Randomness;
import main.input.InputController;
import main.input.Mouse;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.StringUtils;


public class ActionHandler {

    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    
    private final AggressiveAttacker aggressive = new AggressiveAttacker();
    private final Randomness random = new Randomness();

    private final BehaviorUtils actionUtils = new BehaviorUtils();

    /* ANSI Regular
        ██    ██ ███████ ███████ ██████      ██████  ███████ ██   ██  █████  ██    ██ ██  ██████  ██████
        ██    ██ ██      ██      ██   ██     ██   ██ ██      ██   ██ ██   ██ ██    ██ ██ ██    ██ ██   ██
        ██    ██ ███████ █████   ██████      ██████  █████   ███████ ███████ ██    ██ ██ ██    ██ ██████
        ██    ██      ██ ██      ██   ██     ██   ██ ██      ██   ██ ██   ██  ██  ██  ██ ██    ██ ██   ██
         ██████  ███████ ███████ ██   ██     ██████  ███████ ██   ██ ██   ██   ████   ██  ██████  ██   ██
     */
    public void handleUser(GameModel model, InputController controller, Entity unit) {        
        // Gets tiles within movement range if the entity does not already have them...
        // these tiles should be removed after their turn is over
        actionUtils.getTilesWithinClimbAndMovementRange(model, unit);

        boolean actionPanelShowing = model.state.getBoolean(GameStateKey.UI_ACTION_PANEL_SHOWING);
        boolean movementPanelShowing = model.state.getBoolean(GameStateKey.UI_MOVEMENT_PANEL_SHOWING);

        //  if (!actionPanelShowing && !movementPanelShowing) { return; }

        Mouse mouse = controller.getMouse();

        Entity mousedAt = model.tryFetchingTileMousedAt();

        ActionManager action = unit.get(ActionManager.class);
        MovementManager movement = unit.get(MovementManager.class);
        if (action.acted && movement.moved) { return; }
        StatusEffects se = unit.get(StatusEffects.class);
        if (se.shouldHandle()) {
            actionUtils.handleStatusEffects(model, unit);
            se.setHandled(true);
        }


        boolean undoMovementButtonPressed = model.state.getBoolean(GameStateKey.UI_UNDO_MOVEMENT_PRESSED);
        if (undoMovementButtonPressed && movementPanelShowing) {
            actionUtils.undoMovement(model, unit);
            model.state.set(GameStateKey.UI_UNDO_MOVEMENT_PRESSED, false);
            return;
        }

        if (actionPanelShowing) {
            Ability ability = (Ability) model.state.getObject(GameStateKey.ACTION_PANEL_SELECTED_ACTION);
            Abilities abilities = unit.get(Abilities.class);
            if (ability == null || !abilities.getAbilities().contains(ability.name)) { return; }
            actionUtils.getTilesWithinActionRange(model, unit, mousedAt, ability);
            if (mouse.isPressed()) { 
                actionUtils.tryAttackingUnits(model, unit, mousedAt, ability);
            }
            return;
        } else if (movementPanelShowing) {
            actionUtils.getTilesWithinClimbAndMovementRange(model, unit);
            actionUtils.getTilesWithinClimbAndMovementPath(model, unit, mousedAt);
            if (mouse.isPressed()) {
                actionUtils.tryMovingUnit(model, unit, mousedAt);
            }
            return;
        } else {
            actionUtils.getTilesWithinClimbAndMovementRange(model, unit);
            actionUtils.getTilesWithinClimbAndMovementPath(model, unit, mousedAt);
            if (mouse.isPressed()) {
                actionUtils.tryMovingUnit(model, unit, mousedAt);
            }
        }
    }

    /* ANSI REGULAR
         █████  ██     ██████  ███████ ██   ██  █████  ██    ██ ██  ██████  ██████
        ██   ██ ██     ██   ██ ██      ██   ██ ██   ██ ██    ██ ██ ██    ██ ██   ██
        ███████ ██     ██████  █████   ███████ ███████ ██    ██ ██ ██    ██ ██████
        ██   ██ ██     ██   ██ ██      ██   ██ ██   ██  ██  ██  ██ ██    ██ ██   ██
        ██   ██ ██     ██████  ███████ ██   ██ ██   ██   ████   ██  ██████  ██   ██
     */
    public void handleAi(GameModel model, Entity unit) {        
        // Gets tiles within movement range if the entity does not already have them...
        // these tiles should be removed after their turn is over
        actionUtils.getTilesWithinClimbAndMovementRange(model, unit);

        if (Engine.getInstance().getUptime() < 5) { return; } // start after 3 s
        if (unit.get(UserBehavior.class) != null) { return; }
        if (model.speedQueue.peek() != unit) { return; }

        MovementTrack movementTrack = unit.get(MovementTrack.class);
        if (movementTrack.isMoving()) { return; }

        // if fast-forward is not selected, wait a second
        if (!model.state.getBoolean(GameStateKey.UI_SETTINGS_FAST_FORWARD_TURNS)) {
            //double seconds = aiBehavior.actionDelay.elapsed();
            //if (seconds < 1) { return; }
        }

        ActionManager action = unit.get(ActionManager.class);
        MovementManager movement = unit.get(MovementManager.class);
        
        StatusEffects se = unit.get(StatusEffects.class);
        if (se.shouldHandle()) {
            actionUtils.handleStatusEffects(model, unit);
            se.setHandled(true);
        }

        AiBehavior behavior = unit.get(AiBehavior.class);
        double seconds = behavior.actionDelay.elapsed();
        if (seconds < 1) { return; }

        // potentially attack then move, or move then attack
        if (behavior.actThenMove) {
            if (!action.acted) {
                aggressive.attack(model, unit);
                action.acted = true;
                behavior.actionDelay.reset();
                return;
            }

            if (!movement.moved) {
                aggressive.move(model, unit);
                movement.moved = true;
                behavior.actionDelay.reset();
                return;
            }
        } else {
            if (!movement.moved) {
                aggressive.move(model, unit);
                movement.moved = true;
                behavior.actionDelay.reset();
                return;
            }
            if (!action.acted) {
                aggressive.attack(model, unit);
                action.acted = true;
                behavior.actionDelay.reset();
                return;
            }
        }

        if (action.acted && movement.moved && !movementTrack.isMoving() &&
                model.state.getBoolean(GameStateKey.UI_SETTINGS_AUTO_END_TURNS)) {
//            UpdateSystem.endTurn();
            model.system.endTurn();
        }
    }
}
