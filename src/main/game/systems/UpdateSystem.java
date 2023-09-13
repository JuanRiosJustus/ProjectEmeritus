package main.game.systems;


import main.constants.ColorPalette;
import main.game.components.behaviors.Behavior;
import main.game.components.behaviors.UserBehavior;
import main.constants.GameState;
import main.game.components.*;
import main.game.components.behaviors.AiBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stats.Stat;
import main.game.stores.factories.UnitFactory;
import main.game.systems.texts.FloatingTextSystem;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.List;

public class UpdateSystem {

    public UpdateSystem() { }
    private boolean endTurn = false;
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    public final MoveActionSystem moveAction = new MoveActionSystem();
    public final AnimationAndTrackSystem spriteAnimation = new AnimationAndTrackSystem();
    public final OverlaySystem combatAnimation = new OverlaySystem();
    public final ActionSystem combat = new ActionSystem();
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
        Tags tags = unit.get(Tags.class);
        model.speedQueue.dequeue();
        if (tags.contains(Tags.YIELD)) {
            model.speedQueue.requeue(unit);
        }

        Entity turnStarter = model.speedQueue.peek();
        if (turnStarter != null) { model.logger.log(turnStarter.get(Identity.class) + "'s turn starts"); }

        logger.info("Starting new Turn");

        // update the unit
        if (unit == null) { return; }

        ActionManager actionManager = unit.get(ActionManager.class);
        actionManager.reset();

        MovementManager movementManager = unit.get(MovementManager.class);
        movementManager.reset();

        Behavior behavior = unit.get(AiBehavior.class);
        if (behavior == null) { behavior = unit.get(UserBehavior.class); }
        behavior.reset();

//        Tags tags = unit.get(Tags.class);
        Tags.handleEndOfTurn(model, unit);
        tags.reset();

        Passives passives = unit.get(Passives.class);
        if (passives.contains(Passives.MANA_REGEN_I)) {
            Statistics statistics = unit.get(Statistics.class);
            int amount = statistics.addTotalAmountToResource(Statistics.MANA, .05f);
            Animation animation = unit.get(Animation.class);
            model.system.floatingText.floater("+" + amount + "EP", animation.getVector(), ColorPalette.WHITE);
        }

        gemSpawnerSystem.update(model, unit);
        endTurn = false;
    }
}
