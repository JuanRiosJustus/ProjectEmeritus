package main.game.systems;


import main.game.components.behaviors.Behavior;
import main.game.components.behaviors.UserBehavior;
import main.ui.GameState;
import main.game.components.*;
import main.game.components.behaviors.AiBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.UnitFactory;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.List;

public class UpdateSystem {

    public UpdateSystem() { }
    private boolean endTurn = false;
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    public final MoveActionSystem moveAction = new MoveActionSystem();
    public final SpriteAnimationSystem spriteAnimation = new SpriteAnimationSystem();
    public final OverlayAnimationSystem combatAnimation = new OverlayAnimationSystem();
    public final CombatSystem combat = new CombatSystem();
    public final FloatingTextSystem floatingText = new FloatingTextSystem();
    public final GemSpawnerSystem gemSpawnerSystem = new GemSpawnerSystem();

    public void update(GameModel model) {
        // update all entities
        List<Entity> units = UnitFactory.list;
        for (Entity unit : units) {
            moveAction.update(model, unit);
            spriteAnimation.update(model, unit);
            combat.update(model, unit);
        }

        combatAnimation.update(model, null);
        floatingText.update(model, null);

        Entity current = model.speedQueue.peek();

        if (model.gameState.getBoolean(GameState.ACTIONS_END_TURN)) {
            endTurn();
            model.gameState.set(GameState.ACTIONS_END_TURN, false);
            // only switch ui if its the user finishing the turn TODO tentative
            if (current.get(UserBehavior.class) == null) {
                model.gameState.set(GameState.UI_GO_TO_CONTROL_HOME, true);
            }
        }

        if (endTurn) {
            endTurn(model, current);
        }

        boolean newRound = model.speedQueue.update();
        if (newRound) { model.logger.log("---------------New Round"); }
    }

    public void endTurn() { endTurn = true; }

    private void endTurn(GameModel model, Entity unit) {
        model.speedQueue.dequeue();

        Entity turnStarter = model.speedQueue.peek();
        if (turnStarter != null) { model.logger.log(turnStarter.get(Identity.class) + "'s turn starts"); }

        logger.info("Starting new Turn");

        // update the unit
        if (unit == null) { return; }

        ActionManager action = unit.get(ActionManager.class);
        action.reset();

        MovementManager movement = unit.get(MovementManager.class);
        movement.reset();

        Behavior behavior = unit.get(AiBehavior.class);
        if (behavior == null) { behavior = unit.get(UserBehavior.class); }
        behavior.reset();

        Tags tags = unit.get(Tags.class);
        if (tags.shouldHandleEndOfTurn()) { tags.handleEndOfTurn(model, unit); }
        tags.reset();

        gemSpawnerSystem.update(model, unit);
        endTurn = false;
    }

//
//
//    private static boolean endTurn = false;
//    private static boolean lockOn = true;
//    private static final Logger logger = LoggerFactory.instance().logger(UpdateSystem.class);
//
//    public static void update(GameModel model, InputController controller) {
//
//        // update all entities
//        List<Entity> units = UnitFactory.list;
//        for (Entity unit : units) {
//            ActionSystem.update(model, controller, unit);
//            SpriteAnimationSystem.update(model, unit);
//            CombatSystem.update(model, unit);
//            CombatAnimationSystem.update(model, unit);
//        }
//
//        BuffSpawnerSystem.update(GameModel model);
//        Entity current = model.queue.peek();
//
//        UiSystem.update(model, current);
//        FloatingTextSystem.update(model, current);
//
//        if (model.ui.getBoolean(Constants.ACTIONS_UI_ENDTURN)) {
//            endTurn();
//            model.ui.set(Constants.ACTIONS_UI_ENDTURN, false);
//        }
//
//        if (endTurn) {
//            endTurn(model, current);
//            if (current != null) { StatusEffectSystem.update(model, current); }
//        }
//
//        model.queue.update();
//    }

//    public static void endTurn() {
//        endTurn = true;
//    }
//
//    private static void endTurn(GameModel model, Entity unit) {
//        model.queue.dequeue();
////        engine.model.ui.order.dequeue();
////        engine.model.ui.exitToMain();
//
//        logger.log("Starting new turn -> " + model.queue);
//
//        // update the unit
//        if (unit == null) { return; }
//
//        ActionManager manager = unit.get(ActionManager.class);
//        manager.reset();
//
//        endTurn = false;
//    }
}