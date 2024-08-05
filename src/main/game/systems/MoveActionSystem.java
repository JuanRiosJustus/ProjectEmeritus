package main.game.systems;

import main.constants.GameState;
import main.constants.Settings;
import main.engine.Engine;
import main.game.components.*;
import main.game.components.behaviors.AiBehavior;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.queue.SpeedQueue;
import main.game.stores.pools.ability.Ability;
import main.game.systems.actions.ActionHandler;
import main.game.systems.actions.behaviors.AggressiveBehavior;
import main.game.systems.actions.behaviors.RandomnessBehavior;
import main.input.InputController;
import main.input.Mouse;
import main.logging.ELogger;
import main.logging.ELoggerFactory;


public class MoveActionSystem extends GameSystem {

    private final MovementSystem movementSystem = new MovementSystem();
    private final ActionSystem actionSystem = new ActionSystem();
    private final ActionHandler handler = new ActionHandler();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    private final AggressiveBehavior mAggressiveBehavior = new AggressiveBehavior();
    private final RandomnessBehavior mRandomnessBehavior = new RandomnessBehavior();

    public void update(GameModel model, Entity unit) {
        // Nothing to handle if not the units turn
        SpeedQueue queue = model.speedQueue;
        if (queue.peek() != unit) { return; }
        
        // Handle user and AI separately
        if (unit.get(UserBehavior.class) != null) {
            updateUser(model, InputController.getInstance(), unit);
        } else if (unit.get(AiBehavior.class) != null) {
            updateAi(model, unit);
        }
    }

    /* ANSI Regular
    ██    ██ ███████ ███████ ██████      ██████  ███████ ██   ██  █████  ██    ██ ██  ██████  ██████
    ██    ██ ██      ██      ██   ██     ██   ██ ██      ██   ██ ██   ██ ██    ██ ██ ██    ██ ██   ██
    ██    ██ ███████ █████   ██████      ██████  █████   ███████ ███████ ██    ██ ██ ██    ██ ██████
    ██    ██      ██ ██      ██   ██     ██   ██ ██      ██   ██ ██   ██  ██  ██  ██ ██    ██ ██   ██
     ██████  ███████ ███████ ██   ██     ██████  ███████ ██   ██ ██   ██   ████   ██  ██████  ██   ██
 */
    public void updateUser(GameModel model, InputController controller, Entity unit) {
        // Gets tiles within movement range if the entity does not already have them...
        // these tiles should be removed after their turn is over

        boolean showSelectedUnitMovement = model.getGameStateBoolean(GameState.SHOW_SELECTED_UNIT_MOVEMENT_PATHING);
        boolean showSelectedUnitAction = model.getGameStateBoolean(GameState.SHOW_SELECTED_UNIT_ACTION_PATHING);

        Mouse mouse = controller.getMouse();
        Entity mousedAt = model.tryFetchingTileMousedAt();

        ActionManager actionManager = unit.get(ActionManager.class);
        MovementManager movementManager = unit.get(MovementManager.class);
        MovementTrack movementTrack = unit.get(MovementTrack.class);

//        if (abilityManager.acted && movementManager.moved) { return; }
        if (movementTrack.isMoving()) { return; }

        Tags.handleStartOfTurn(model, unit);
        Tags tags = unit.get(Tags.class);

        if (tags.contains(Tags.SLEEP)) {
//            actionManager.mActed = true;
//            movementManager.moved = true;
            actionManager.setActed(true);
            movementManager.setMoved(true);
            model.logger.log(unit + " is sleeping");
            // TODO can these be combined?
            model.system.endTurn();
            model.gameState.set(GameState.ACTIONS_END_TURN, true);
        }

        boolean undoMovementButtonPressed = model.gameState.getBoolean(GameState.UNDO_MOVEMENT_BUTTON_PRESSED);
        if (undoMovementButtonPressed && showSelectedUnitMovement) {
            Entity previous = movementManager.previousTile;
//            MovementManager.undo(model, unit);
            movementSystem.undo(model, unit);
            model.logger.log(unit, " Moves back to " + previous);
            model.gameState.set(GameState.UI_GO_TO_CONTROL_HOME, false);
            model.gameState.set(GameState.UNDO_MOVEMENT_BUTTON_PRESSED, false);
            return;
        }

        boolean moved;
        boolean acted;

        if (showSelectedUnitAction) {
            // Check that the unit actually has the ability, not sure why we need to check, but keeping for now
            Ability ability = actionManager.getSelected();
            if (ability == null) { return;
            }

            Statistics statistics = unit.get(Statistics.class);
            boolean hasAbility = statistics.hasAbility(ability.name);
            if (!hasAbility) { return; }

            // Execute the action
            actionSystem.act(model, unit, ability, mousedAt, false);
            if (mouse.isPressed() && !actionManager.hasActed()) {
                acted = actionSystem.act(model, unit, ability, mousedAt, true);
                actionManager.setActed(acted);
                if (acted) {
                    model.setGameState(GameState.CHANGE_BATTLE_UI_TO_HOME_SCREEN, true);
                }
            }
        } else if (showSelectedUnitMovement) {
            // Execute the movement
            movementSystem.move(model, unit, mousedAt, false);
            if (mouse.isPressed() && !movementManager.hasMoved()) {
                moved = movementSystem.move(model, unit, mousedAt, true);
                movementManager.setMoved(moved);
                if (moved) {
                    model.setGameState(GameState.CHANGE_BATTLE_UI_TO_HOME_SCREEN, true);
                }
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
    public void updateAi(GameModel model, Entity unit) {
        // Gets tiles within movement range if the entity does not already have them...
        // these tiles should be removed after their turn is over
        movementSystem.move(model, unit, null, false);

        if (Engine.getInstance().getUptime() < 5) { return; } // start after 3 s
        if (unit.get(UserBehavior.class) != null) { return; }
        if (model.speedQueue.peek() != unit) { return; }

        MovementTrack track = unit.get(MovementTrack.class);
        if (track.isMoving()) { return; }

        // if fast-forward is not selected, wait a second
        if (!model.gameState.getBoolean(GameState.UI_SETTINGS_FAST_FORWARD_TURNS)) {
            //double seconds = aiBehavior.actionDelay.elapsed();
            //if (seconds < 1) { return; }
        }

        ActionManager actionManager = unit.get(ActionManager.class);
        MovementManager movementManager = unit.get(MovementManager.class);

        Tags.handleStartOfTurn(model, unit);

        AiBehavior behavior = unit.get(AiBehavior.class);
        double seconds = behavior.actionDelay.elapsed();
        if (seconds < 1) { return; }

        // potentially attack then move, or move then attack
        if (behavior.actThenMove) {
            if (!actionManager.mActed) {
//                aggressive.attack(model, unit);
                actionManager.mActed = true;
                behavior.actionDelay.reset();
                return;
            }

            if (!movementManager.moved) {
                mRandomnessBehavior.move(model, unit);
//                movementSystem.move()
                movementManager.moved = true;
                behavior.actionDelay.reset();
                return;
            }
        } else {
            if (!movementManager.moved) {
//                mAggressiveBehavior.move(model, unit);
                mRandomnessBehavior.move(model, unit);
                movementManager.moved = true;
                behavior.actionDelay.reset();
                return;
            }
            if (!actionManager.mActed) {
//                aggressive.attack(model, unit);
                actionManager.mActed = true;
                behavior.actionDelay.reset();
                return;
            }
        }

        if (!track.isMoving() && Settings.getInstance().getBoolean(Settings.GAMEPLAY_AUTO_END_TURNS)) {
            model.system.endTurn();
        }
    }
}
