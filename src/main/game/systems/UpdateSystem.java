package main.game.systems;


import main.game.components.behaviors.Behavior;
import main.game.components.behaviors.UserBehavior;
import main.constants.GameState;
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
        if (newRound) { model.logger.log("New Round"); }
    }

    public void endTurn() { endTurn = true; }

    private void endTurn(GameModel model, Entity unit) {
        model.speedQueue.dequeue();

        Entity turnStarter = model.speedQueue.peek();
        if (turnStarter != null) { model.logger.log(turnStarter.get(Identity.class) + "'s turn starts"); }

        logger.info("Starting new Turn");

        // update the unit
        if (unit == null) { return; }

        Action action = unit.get(Action.class);
        action.reset();

        Movement movement = unit.get(Movement.class);
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
}
