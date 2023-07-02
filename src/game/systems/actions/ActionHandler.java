package game.systems.actions;

import constants.GameStateKey;
import engine.Engine;
import game.components.*;
import game.components.behaviors.AiBehavior;
import game.components.behaviors.UserBehavior;
import game.entity.Entity;
import game.main.GameModel;
import game.stores.pools.ability.Ability;
import game.systems.actions.behaviors.BehaviorUtils;
import game.systems.actions.behaviors.AggressiveAttacker;
import game.systems.actions.behaviors.Randomness;
import input.InputController;
import input.Mouse;
import logging.Logger;
import logging.LoggerFactory;


public class ActionHandler {

    private final Logger logger = LoggerFactory.instance().logger(getClass());
    
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
        actionUtils.getTilesWithinJumpAndMovementRange(model, unit);

        boolean actionPanelShowing = model.state.getBoolean(GameStateKey.UI_ACTION_PANEL_SHOWING);
        boolean movementPanelShowing = model.state.getBoolean(GameStateKey.UI_MOVEMENT_PANEL_SHOWING);

         if (!actionPanelShowing && !movementPanelShowing) { return; }

        Mouse mouse = controller.getMouse();

        Entity selected = model.tryFetchingTileMousedAt();

        boolean undoMovementButtonPressed = model.state.getBoolean(GameStateKey.UI_UNDO_MOVEMENT_PRESSED);
        if (undoMovementButtonPressed && movementPanelShowing) {
            actionUtils.undoMovement(model, unit);
            model.state.set(GameStateKey.UI_UNDO_MOVEMENT_PRESSED, false);
            return;
        }

        if (actionPanelShowing) {
            Ability ability = (Ability) model.state.getObject(GameStateKey.ACTION_PANEL_SELECTED_ACTION);
            if (ability == null) { return; }
            actionUtils.getTilesWithinActionRange(model, unit, selected, ability);
            if (mouse.isPressed()) { 
                actionUtils.tryAttackingUnits(model, unit, selected, ability);
            }
            return;
        }

        if (movementPanelShowing) {
            actionUtils.getTilesWithinJumpAndMovementRange(model, unit);
            actionUtils.getTilesWithinJumpAndMovementPath(model, unit, selected);
            if (mouse.isPressed()) {
                actionUtils.tryMovingUnit(model, unit, selected);
            }
            return;
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
        actionUtils.getTilesWithinJumpAndMovementRange(model, unit);

        if (Engine.instance().getUptime() < 5) { return; } // start after 3 s
        if (unit.get(UserBehavior.class) != null) { return; }
        if (model.unitTurnQueue.peek() != unit) { return; }

        MovementTrack movementTrack = unit.get(MovementTrack.class);
        if (movementTrack.isMoving()) { return; }

        // if fast-forward is not selected, wait a second
        if (!model.state.getBoolean(GameStateKey.UI_SETTINGS_FAST_FORWARD_TURNS)) {
            //double seconds = aiBehavior.actionDelay.elapsed();
            //if (seconds < 1) { return; }
        }

        AiBehavior behavior = unit.get(AiBehavior.class);
        double seconds = behavior.actionDelay.elapsed();
        if (seconds < 1) { return; }

        ActionManager action = unit.get(ActionManager.class);
        MovementManager movement = unit.get(MovementManager.class);
        
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
