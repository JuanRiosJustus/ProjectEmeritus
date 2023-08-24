package main.game.systems.actions;

import main.constants.ColorPalette;
import main.constants.GameState;
import main.constants.Settings;
import main.engine.Engine;
import main.game.components.*;
import main.game.components.behaviors.AiBehavior;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ability.Ability;
import main.game.systems.actions.behaviors.AggressiveAttacker;
import main.game.systems.actions.behaviors.Randomness;
import main.input.InputController;
import main.input.Mouse;
import main.logging.ELogger;
import main.logging.ELoggerFactory;


public class ActionHandler {

    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    
    private final AggressiveAttacker aggressive = new AggressiveAttacker();
    private final Randomness random = new Randomness();

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

        boolean actionHudShowing = model.gameState.getBoolean(GameState.ACTION_HUD_IS_SHOWING);
        boolean movementHudShowing = model.gameState.getBoolean(GameState.MOVEMENT_HUD_IS_SHOWING);
        boolean inspectionHudShowing = model.gameState.getBoolean(GameState.INSPECTION_HUD_IS_SHOWING);
        boolean summaryHudShowing = model.gameState.getBoolean(GameState.SUMMARY_HUD_IS_SHOWING);

        Mouse mouse = controller.getMouse();
        Entity mousedAt = model.tryFetchingTileMousedAt();

        Action action = unit.get(Action.class);
        Movement movement = unit.get(Movement.class);

        if (action.acted && movement.moved) { return; }

        Tags tags = unit.get(Tags.class);
        if (tags.shouldHandleStartOfTurn()) {
            tags.handleStartOfTurn(model, unit);
        }

        if (tags.contains(Tags.SLEEP)) {
            action.acted = true;
            movement.moved = true;
            model.system.floatingText.floater("zZzZ", unit.get(Animation.class).position, ColorPalette.WHITE);
            model.logger.log(unit + " is sleeping");
            // TODO can these be combined?
            model.system.endTurn();
            model.gameState.set(GameState.ACTIONS_END_TURN, true);
        }

        boolean undoMovementButtonPressed = model.gameState.getBoolean(GameState.UNDO_MOVEMENT_BUTTON_PRESSED);
        if (undoMovementButtonPressed && movementHudShowing) {
            Entity previous = movement.previousTile;
            Movement.undo(model, unit);
            model.logger.log(unit, " Moves back to " + previous);
            model.gameState.set(GameState.UI_GO_TO_CONTROL_HOME, false);
            model.gameState.set(GameState.UNDO_MOVEMENT_BUTTON_PRESSED, false);
            return;
        }

        if (actionHudShowing) {
            Ability ability = action.action;
            Abilities abilities = unit.get(Abilities.class);
            if (ability == null || !abilities.getAbilities().contains(ability.name)) { return; }
            Action.act(model, unit, ability, mousedAt, false);
            if (mouse.isPressed() && !action.acted) {
                Action.act(model, unit, ability, mousedAt, true);
                model.gameState.set(GameState.UI_GO_TO_CONTROL_HOME, true);
            }
        } else if (movementHudShowing) {
            Movement.move(model, unit, mousedAt, false);
            if (mouse.isPressed()) {
                Movement.move(model, unit, mousedAt, true);
            }
        } else if (inspectionHudShowing) {

        } else if (summaryHudShowing) {

        } else {
            Movement.move(model, unit, mousedAt, false);
            if (mouse.isPressed()) {
                Movement.move(model, unit, mousedAt, true);
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
        Movement.move(model, unit, null, false);

        if (Engine.getInstance().getUptime() < 5) { return; } // start after 3 s
        if (unit.get(UserBehavior.class) != null) { return; }
        if (model.speedQueue.peek() != unit) { return; }

        Track track = unit.get(Track.class);
        if (track.isMoving()) { return; }

        // if fast-forward is not selected, wait a second
        if (!model.gameState.getBoolean(GameState.UI_SETTINGS_FAST_FORWARD_TURNS)) {
            //double seconds = aiBehavior.actionDelay.elapsed();
            //if (seconds < 1) { return; }
        }

        Action action = unit.get(Action.class);
        Movement movement = unit.get(Movement.class);
        
        Tags tags = unit.get(Tags.class);
        if (tags.shouldHandleStartOfTurn()) {
            tags.handleStartOfTurn(model, unit);
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

        if (!track.isMoving() && Settings.getInstance().getBoolean(Settings.GAMEPLAY_AUTO_END_TURNS)) {
//            UpdateSystem.endTurn();
            model.system.endTurn();
        }
    }
}
