package main.game.systems;


import main.game.components.behaviors.Behavior;
import main.game.components.behaviors.UserBehavior;
import main.constants.GameState;
import main.game.components.*;
import main.game.components.behaviors.AiBehavior;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.systems.texts.FloatingTextSystem;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class UpdateSystem {

    public UpdateSystem() { }
    private boolean endTurn = false;
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    public final MoveActionSystem moveAction = new MoveActionSystem();
    public final MovementTrackSystem movementTrackSystem = new MovementTrackSystem();
    public final OverlaySystem combatAnimation = new OverlaySystem();
    public final CombatSystem combat = new CombatSystem();
    public final FloatingTextSystem floatingText = new FloatingTextSystem();
    public final GemSpawnerSystem gemSpawnerSystem = new GemSpawnerSystem();
    public final TileVisualsSystem tileVisualsSystem = new TileVisualsSystem();
    private final UnitVisualsSystem unitVisualsSystem = new UnitVisualsSystem();

    public void update(GameModel model) {
        // update all tiles and units
        for (int row = 0; row < model.getRows(); row++) {
            for (int column = 0; column < model.getColumns(); column++) {
                Entity entity = model.tryFetchingTileAt(row, column);
                tileVisualsSystem.update(model, entity);
                Tile tile = entity.get(Tile.class);
                updateUnit(model, tile.getUnit());
            }
        }

        combatAnimation.update(model, null);
        floatingText.update(model, null);

        Entity current = model.mSpeedQueue.peek();

        boolean endCurrentUnitsTurn = model.getGameStateBoolean(GameState.END_CURRENT_UNITS_TURN);
        if (endCurrentUnitsTurn) {
            endTurn();

            model.setGameState(GameState.END_CURRENT_UNITS_TURN, false);
            if (current.get(UserBehavior.class) == null) {
                model.setGameState(GameState.CHANGE_BATTLE_UI_TO_HOME_SCREEN, true);
            }
        }

        if (endTurn) {
            endTurn(model, current);
        }

        boolean newRound = model.mSpeedQueue.update();
        if (newRound) { model.mLogger.log("New Round"); }
    }

    private void updateUnit(GameModel model, Entity unit) {
        if (unit == null) { return; }

        if (model.isLoadOutMode()) {
            movementTrackSystem.update(model, unit);
        } else {
            moveAction.update(model, unit);
            movementTrackSystem.update(model, unit);
            combat.update(model, unit);
        }
        unitVisualsSystem.update(model, unit);
    }

    public void endTurn() { endTurn = true; }

    private void endTurn(GameModel model, Entity unit) {
        Tags tags = unit.get(Tags.class);
        model.mSpeedQueue.dequeue();
        if (tags.contains(Tags.YIELD)) {
            model.mSpeedQueue.requeue(unit);
        }

        Entity turnStarter = model.mSpeedQueue.peek();
        if (turnStarter != null) { model.mLogger.log(turnStarter.get(Identity.class) + "'s turn starts"); }

        logger.info("Starting new Turn");

        // update the unit
        if (unit == null) { return; }

        ActionManager actionManager = unit.get(ActionManager.class);
        actionManager.reset();

        MovementManager movementManager = unit.get(MovementManager.class);
        movementManager.reset();

        Behavior behavior = unit.get(AiBehavior.class);
//        if (behavior == null) { behavior = unit.get(UserBehavior.class); }
//        behavior.reset();

//        Tags tags = unit.get(Tags.class);
        Tags.handleEndOfTurn(model, unit);
        tags.reset();

//        Passives passives = unit.get(Passives.class);
//        if (passives.contains(Passives.MANA_REGEN_I)) {
//            Summary summary = unit.get(Summary.class);
//            int amount = summary.addTotalAmountToResource(Summary.MANA, .05f);
//            Animation animation = unit.get(Animation.class);
//            model.system.floatingText.floater("+" + amount + "EP", animation.getVector(), ColorPalette.WHITE);
//        }

        gemSpawnerSystem.update(model, unit);
        endTurn = false;
    }
}
