package main.game.systems.actions;

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

        AbilityManager abilityManager = unit.get(AbilityManager.class);
        MovementManager movementManager = unit.get(MovementManager.class);

        if (abilityManager.acted && movementManager.moved) { return; }


        Tags.handleStartOfTurn(model, unit);
        Tags tags = unit.get(Tags.class);

        if (tags.contains(Tags.SLEEP)) {
            abilityManager.acted = true;
            movementManager.moved = true;
            model.logger.log(unit + " is sleeping");
            // TODO can these be combined?
            model.system.endTurn();
            model.gameState.set(GameState.ACTIONS_END_TURN, true);
        }

        boolean undoMovementButtonPressed = model.gameState.getBoolean(GameState.UNDO_MOVEMENT_BUTTON_PRESSED);
        if (undoMovementButtonPressed && movementHudShowing) {
            Entity previous = movementManager.previousTile;
            MovementManager.undo(model, unit);
            model.logger.log(unit, " Moves back to " + previous);
            model.gameState.set(GameState.UI_GO_TO_CONTROL_HOME, false);
            model.gameState.set(GameState.UNDO_MOVEMENT_BUTTON_PRESSED, false);
            return;
        }


        if (actionHudShowing) {
            Ability ability = abilityManager.preparing;
            if (ability == null) { return; }
            Statistics statistics = unit.get(Statistics.class);
//            Actions actions = unit.get(Actions.class);
            boolean isInAbilities = statistics.setContains(Statistics.ABILITIES, ability.name);
            boolean isInSkills = statistics.setContains(Statistics.SKILLS, ability.name);
            if (!isInSkills && !isInAbilities) { return; }
            AbilityManager.act(model, unit, ability, mousedAt, false);
            if (mouse.isPressed()) {
                boolean acted = AbilityManager.act(model, unit, ability, mousedAt, true);
                if (acted) {
                    model.gameState.set(GameState.UI_GO_TO_CONTROL_HOME, true);
                }
            }
        } else if (movementHudShowing) {
            MovementManager.move(model, unit, mousedAt, false);
            if (mouse.isPressed()) {
                MovementManager.move(model, unit, mousedAt, true);
            }
        } else if (inspectionHudShowing) {

        } else if (summaryHudShowing) {
            MovementManager.move(model, unit, mousedAt, false);
            if (mouse.isPressed()) {
                MovementManager.move(model, unit, mousedAt, true);
            }
        } else {
            MovementManager.move(model, unit, mousedAt, false);
            if (mouse.isPressed()) {
                MovementManager.move(model, unit, mousedAt, true);
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
        MovementManager.move(model, unit, null, false);

        if (Engine.getInstance().getUptime() < 5) { return; } // start after 3 s
        if (unit.get(UserBehavior.class) != null) { return; }
        if (model.speedQueue.peek() != unit) { return; }

        AnimationMovementTrack track = unit.get(AnimationMovementTrack.class);
        if (track.isMoving()) { return; }

        // if fast-forward is not selected, wait a second
        if (!model.gameState.getBoolean(GameState.UI_SETTINGS_FAST_FORWARD_TURNS)) {
            //double seconds = aiBehavior.actionDelay.elapsed();
            //if (seconds < 1) { return; }
        }

        AbilityManager abilityManager = unit.get(AbilityManager.class);
        MovementManager movementManager = unit.get(MovementManager.class);

        Tags.handleStartOfTurn(model, unit);

        AiBehavior behavior = unit.get(AiBehavior.class);
        double seconds = behavior.actionDelay.elapsed();
        if (seconds < 1) { return; }

        // potentially attack then move, or move then attack
        if (behavior.actThenMove) {
            if (!abilityManager.acted) {
                aggressive.attack(model, unit);
                abilityManager.acted = true;
                behavior.actionDelay.reset();
                return;
            }

            if (!movementManager.moved) {
                aggressive.move(model, unit);
                movementManager.moved = true;
                behavior.actionDelay.reset();
                return;
            }
        } else {
            if (!movementManager.moved) {
                aggressive.move(model, unit);
                movementManager.moved = true;
                behavior.actionDelay.reset();
                return;
            }
            if (!abilityManager.acted) {
                aggressive.attack(model, unit);
                abilityManager.acted = true;
                behavior.actionDelay.reset();
                return;
            }
        }

        if (!track.isMoving() && Settings.getInstance().getBoolean(Settings.GAMEPLAY_AUTO_END_TURNS)) {
            model.system.endTurn();
        }
    }
}
