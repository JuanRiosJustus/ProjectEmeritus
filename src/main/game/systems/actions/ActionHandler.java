package main.game.systems.actions;


import main.constants.Vector3f;
import main.game.components.AnimationComponent;
import main.game.components.MovementComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.action.ActionEvent;
import main.game.systems.combat.CombatReport;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.StringUtils;

import java.awt.Color;
import java.util.*;

public class ActionHandler {

    protected SplittableRandom random = new SplittableRandom();
    private ActionEvent mLatestAction = null;
    private final ELogger mLogger = ELoggerFactory.getInstance().getELogger(ActionHandler.class);
    private final Queue<ActionEvent> mActionQueue = new LinkedList<>();


    private void announceWithStationaryText(GameModel model, String str, Entity unitEntity, Color color) {
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity tileEntity = movementComponent.getCurrentTile();
        if (tileEntity == null) { return; }

        Tile tile = tileEntity.get(Tile.class);
        Vector3f vector3f = tile.getLocalVector(model);

        int spriteWidths = model.getGameState().getSpriteWidth();
        int spriteHeights = model.getGameState().getSpriteHeight();
        int x = (int) vector3f.x;//(vector3f.x + random.nextInt((spriteWidths / 2) * -1, (spriteWidths / 2)));
        int y = (int) vector3f.y - (spriteHeights); //(int) (vector3f.x + random.nextInt((spriteHeights) * -1, spriteHeights));
        float fontSize = model.getGameState().getFloatingTextFontSize();
        int lifeTime = random.nextInt(2, 4);

        String capitalizedString = StringUtils.convertSnakeCaseToCapitalized(str);
//        model.getGameState().addFloatingText(new FloatingText(capitalizedString, fontSize, x, y, color, lifeTime));
    }


    private void executeHit(GameModel model, Entity actorUnitEntity, String action, Entity actedOnUnitEntity) {
        // 0. Setup
        StatisticsComponent defendingStatisticsComponent = actedOnUnitEntity.get(StatisticsComponent.class);

        // 1. Calculate damage
        CombatReport report = new CombatReport(actorUnitEntity, action, actedOnUnitEntity);

//        ActionDatabase.getInstance().use(model, action, actorUnitEntity, actedOnUnitEntity);

        Map<String, Integer> damageMap = report.calculate();

        for (String resource : damageMap.keySet()) {
//            int damage = damageMap.get(resource);
//            defendingStatisticsComponent.reduceResource(resource, damage);
//            defendingStatisticsComponent.toResource(resource, damage);
//            defendingStatisticsComponent.modify(resource, damage);
            String negative = "", positive = "";
//            switch (resource) {
//                case StatisticsComponent.HEALTH -> {
//                    negative = ColorPalette.HEX_CODE_RED;
//                    positive = ColorPalette.HEX_CODE_GREEN;
//                }
//                case StatisticsComponent.MANA -> {
//                    negative = ColorPalette.HEX_CODE_PURPLE;
//                    positive = ColorPalette.HEX_CODE_BLUE;
//                }
//                case StatisticsComponent.STAMINA -> {
//                    negative = ColorPalette.HEX_CODE_CREAM;
//                    positive = ColorPalette.HEX_CODE_GREEN;
//                }
//            }
//            if (damage != 0) {
////                model.mLogger.log(
////                        ColorPalette.getHtmlColor(actorUnitEntity.toString(), ColorPalette.HEX_CODE_GREEN),
////                        StringFormatter.format(
////                                "uses {} {} {}",
////                                ColorPalette.getHtmlColor(String.valueOf(action), ColorPalette.HEX_CODE_CREAM),
////                                actedOnUnitEntity == actorUnitEntity ? "" : "on " + actedOnUnitEntity,
////                                damage > 0 ?
////                                        ColorPalette.getHtmlColor("dealing " + Math.abs(damage) + " Damage", negative) :
////                                        ColorPalette.getHtmlColor("recovering " + Math.abs(damage) + resource, positive)
////                        )
////                );
//
//                boolean isNegative = damage < 0;
//                Color color = isNegative ? ColorPalette.TRANSLUCENT_SUNSET_ORANGE : ColorPalette.TRANSLUCENT_GREEN_LEVEL_4;
//                announceWithFloatingTextCentered(model, (isNegative ? "" : "+") + damage , actedOnUnitEntity, color);
//            } else {
////                model.mLogger.log(
////                        ColorPalette.getHtmlColor(actorUnitEntity.toString(), ColorPalette.HEX_CODE_GREEN),
////                        "uses " + action
////                );
//            }
        }

        // Draw the correct combat animations
//        applyAnimationsBasedOnAbility(model, event.ability, defender, health, energy, buffValue);

        // 2. If the defender has no more health, just remove
        if (model.getSpeedQueue().removeIfNoCurrentHealth(actedOnUnitEntity)) {
            announceWithStationaryText(model, "Dead!", actedOnUnitEntity, ColorPalette.WHITE);
            return;
        }

        // 3. apply status effects to target
//        applyEffects(model, actedOnUnitEntity, event, event.action.conditionsToTargetsChances.entrySet());

        // don't move if already performing some action
        AnimationComponent animationComponent = actedOnUnitEntity.get(AnimationComponent.class);
//        if (animationComponent.hasPendingAnimations()) { return; }
        model.getSystems().getAnimationSystem().executeShakeAnimation(model, actedOnUnitEntity);
//        track.shake(model, actedOnUnitEntity);

        // defender has already queued an attack/is the attacker, don't animate
//        if (mQueue.containsKey(defender)) { return; }

//        track.shake(model, defender);
    }

//    private void announceWithFloatingText(GameModel model, String str, Entity unitEntity, Color color) {
//        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//        Entity tileEntity = movementComponent.getCurrentTile();
//        if (tileEntity == null) { return; }
//        Tile tile = tileEntity.get(Tile.class);
//        Vector3f vector3f = tile.getLocalVector(model);
//
//
//        int spriteWidths = model.getGameState().getSpriteWidth();
//        int spriteHeights = model.getGameState().getSpriteHeight();
//        int x = (int) vector3f.x + random.nextInt((spriteWidths / 2) * -1, (spriteWidths / 2));
////        int y = (int) vector3f.y + random.nextInt((spriteHeights / 2) * -1, spriteHeights / 2);
//        int y = (int) vector3f.y - spriteHeights;
//
//        str = StringUtils.convertSnakeCaseToCapitalized(str);
//
//        model.getGameState().addFloatingText(new FloatingText(str, x, y, color));
//    }

//    private void announceWithFloatingTextCentered(GameModel model, String str, Entity unitEntity, Color color) {
//        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//        Entity tileEntity = movementComponent.getCurrentTile();
//        if (tileEntity == null) { return; }
//        Tile tile = tileEntity.get(Tile.class);
//        Vector3f vector3f = tile.getLocalVector(model);
//
//
//        int spriteWidths = model.getGameState().getSpriteWidth();
//        int spriteHeights = model.getGameState().getSpriteHeight();
//        int x = (int) vector3f.x;
//        int y = (int) vector3f.y - (spriteHeights / 2);
//
//        str = StringUtils.convertSnakeCaseToCapitalized(str);
//
//        model.getGameState().addFloatingText(new FloatingText(str, x, y, color));
//    }

    public void finishAction(GameModel model) {
//        if (mLatestAction == null) { return; }

        ActionEvent actionEvent = mActionQueue.peek();
        if (actionEvent == null) { return; }

        // 1. Check if the user can pay costs


        // 2. Check handle the user animation


        // 3. Continue applying the different effects from the action

        AnimationComponent animationComponent = actionEvent.getActor().get(AnimationComponent.class);
        if (animationComponent.hasPendingAnimations()) { return; }

//        mLatestAction.getEvent().run();
//        mLatestAction = null;


        Entity actor = actionEvent.getActor();
        String action = actionEvent.getAction();
        Set<Entity> targets = actionEvent.getTargets();

//        ActionDatabase.getInstance().use(model, action, actor, targets);
//        finishAction(model, mLatestAction);
        mLatestAction = null;
    }

    public void finishActionV1(GameModel model) {
        if (mLatestAction == null) { return; }



        AnimationComponent animationComponent = mLatestAction.getActor().get(AnimationComponent.class);
        if (animationComponent.hasPendingAnimations()) { return; }

//        mLatestAction.getEvent().run();
//        mLatestAction = null;
//        finishAction(model, mLatestAction);
        mLatestAction = null;
    }

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
