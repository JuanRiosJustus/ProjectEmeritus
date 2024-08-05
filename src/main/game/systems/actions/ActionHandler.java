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
import main.game.systems.actions.behaviors.AggressiveBehavior;
import main.game.systems.actions.behaviors.RandomnessBehavior;
import main.input.InputController;
import main.input.Mouse;
import main.logging.ELogger;
import main.logging.ELoggerFactory;


public class ActionHandler {

//    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
//
//    private final AggressiveBehavior aggressive = new AggressiveBehavior();
//    private final RandomnessBehavior random = new RandomnessBehavior();
//
//    /* ANSI Regular
//        ██    ██ ███████ ███████ ██████      ██████  ███████ ██   ██  █████  ██    ██ ██  ██████  ██████
//        ██    ██ ██      ██      ██   ██     ██   ██ ██      ██   ██ ██   ██ ██    ██ ██ ██    ██ ██   ██
//        ██    ██ ███████ █████   ██████      ██████  █████   ███████ ███████ ██    ██ ██ ██    ██ ██████
//        ██    ██      ██ ██      ██   ██     ██   ██ ██      ██   ██ ██   ██  ██  ██  ██ ██    ██ ██   ██
//         ██████  ███████ ███████ ██   ██     ██████  ███████ ██   ██ ██   ██   ████   ██  ██████  ██   ██
//     */
//    public void handleUser(GameModel model, InputController controller, Entity unit) {
//        // Gets tiles within movement range if the entity does not already have them...
//        // these tiles should be removed after their turn is over
//
//        boolean actionHudShowing = model.gameState.getBoolean(GameState.ACTION_HUD_IS_SHOWING);
//        boolean movementHudShowing = model.gameState.getBoolean(GameState.MOVEMENT_HUD_IS_SHOWING);
//        boolean inspectionHudShowing = model.gameState.getBoolean(GameState.INSPECTION_HUD_IS_SHOWING);
//        boolean summaryHudShowing = model.gameState.getBoolean(GameState.SUMMARY_HUD_IS_SHOWING);
//
//        Mouse mouse = controller.getMouse();
//        Entity mousedAt = model.tryFetchingTileMousedAt();
//
//        ActionManager actionManager = unit.get(ActionManager.class);
//        MovementManager movementManager = unit.get(MovementManager.class);
//        MovementTrack movementTrack = unit.get(MovementTrack.class);
//
////        if (abilityManager.acted && movementManager.moved) { return; }
//        if (movementManager.moved && !movementTrack.isMoving()) { return; }
//
//
//        Tags.handleStartOfTurn(model, unit);
//        Tags tags = unit.get(Tags.class);
//
//        if (tags.contains(Tags.SLEEP)) {
//            actionManager.mActed = true;
//            movementManager.moved = true;
//            model.logger.log(unit + " is sleeping");
//            // TODO can these be combined?
//            model.system.endTurn();
//            model.gameState.set(GameState.ACTIONS_END_TURN, true);
//        }
//
//        boolean undoMovementButtonPressed = model.gameState.getBoolean(GameState.UNDO_MOVEMENT_BUTTON_PRESSED);
//        if (undoMovementButtonPressed && movementHudShowing) {
//            Entity previous = movementManager.previousTile;
//            MovementManager.undo(model, unit);
//            model.logger.log(unit, " Moves back to " + previous);
//            model.gameState.set(GameState.UI_GO_TO_CONTROL_HOME, false);
//            model.gameState.set(GameState.UNDO_MOVEMENT_BUTTON_PRESSED, false);
//            return;
//        }
//
//
//        if (actionHudShowing) {
//            Ability ability = actionManager.preparing;
//            if (ability == null) { return; }
//            Statistics statistics = unit.get(Statistics.class);
////            Actions actions = unit.get(Actions.class);
//            boolean isInAbilities = statistics.setContains(Statistics.ABILITIES, ability.name);
//            boolean isInSkills = statistics.setContains(Statistics.SKILLS, ability.name);
//            if (!isInSkills && !isInAbilities) { return; }
////            ActionManager.act(model, unit, ability, mousedAt, false);
//            if (mouse.isPressed()) {
//                boolean acted = ActionManager.act(model, unit, ability, mousedAt, true);
//                if (acted) {
//                    model.gameState.set(GameState.UI_GO_TO_CONTROL_HOME, true);
//                }
//            }
//        } else if (movementHudShowing) {
//            MovementManager.move(model, unit, mousedAt, false);
//            if (mouse.isPressed()) {
//                MovementManager.move(model, unit, mousedAt, true);
//            }
//        } else if (inspectionHudShowing) {
//
//        } else if (summaryHudShowing) {
//            MovementManager.move(model, unit, mousedAt, false);
//            if (mouse.isPressed()) {
//                MovementManager.move(model, unit, mousedAt, true);
//            }
//        } else {
//            MovementManager.move(model, unit, mousedAt, false);
//            if (mouse.isPressed()) {
//                MovementManager.move(model, unit, mousedAt, true);
//            }
//        }
//    }
//
//    /* ANSI REGULAR
//         █████  ██     ██████  ███████ ██   ██  █████  ██    ██ ██  ██████  ██████
//        ██   ██ ██     ██   ██ ██      ██   ██ ██   ██ ██    ██ ██ ██    ██ ██   ██
//        ███████ ██     ██████  █████   ███████ ███████ ██    ██ ██ ██    ██ ██████
//        ██   ██ ██     ██   ██ ██      ██   ██ ██   ██  ██  ██  ██ ██    ██ ██   ██
//        ██   ██ ██     ██████  ███████ ██   ██ ██   ██   ████   ██  ██████  ██   ██
//     */
//    public void handleAi(GameModel model, Entity unit) {
//        // Gets tiles within movement range if the entity does not already have them...
//        // these tiles should be removed after their turn is over
//        MovementManager.move(model, unit, null, false);
//
//        if (Engine.getInstance().getUptime() < 5) { return; } // start after 3 s
//        if (unit.get(UserBehavior.class) != null) { return; }
//        if (model.speedQueue.peek() != unit) { return; }
//
//        MovementTrack track = unit.get(MovementTrack.class);
//        if (track.isMoving()) { return; }
//
//        // if fast-forward is not selected, wait a second
//        if (!model.gameState.getBoolean(GameState.UI_SETTINGS_FAST_FORWARD_TURNS)) {
//            //double seconds = aiBehavior.actionDelay.elapsed();
//            //if (seconds < 1) { return; }
//        }
//
//        ActionManager actionManager = unit.get(ActionManager.class);
//        MovementManager movementManager = unit.get(MovementManager.class);
//
//        Tags.handleStartOfTurn(model, unit);
//
//        AiBehavior behavior = unit.get(AiBehavior.class);
//        double seconds = behavior.actionDelay.elapsed();
//        if (seconds < 1) { return; }
//
//        // potentially attack then move, or move then attack
//        if (behavior.actThenMove) {
//            if (!actionManager.mActed) {
//                aggressive.attack(model, unit);
//                actionManager.mActed = true;
//                behavior.actionDelay.reset();
//                return;
//            }
//
//            if (!movementManager.moved) {
//                aggressive.move(model, unit);
//                movementManager.moved = true;
//                behavior.actionDelay.reset();
//                return;
//            }
//        } else {
//            if (!movementManager.moved) {
//                aggressive.move(model, unit);
//                movementManager.moved = true;
//                behavior.actionDelay.reset();
//                return;
//            }
//            if (!actionManager.mActed) {
//                aggressive.attack(model, unit);
//                actionManager.mActed = true;
//                behavior.actionDelay.reset();
//                return;
//            }
//        }
//
//        if (!track.isMoving() && Settings.getInstance().getBoolean(Settings.GAMEPLAY_AUTO_END_TURNS)) {
//            model.system.endTurn();
//        }
//    }
}
