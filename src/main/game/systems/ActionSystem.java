package main.game.systems;

import main.constants.Pair;
import main.constants.Vector3f;
import main.game.components.*;
import main.game.components.behaviors.Behavior;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathing.PathBuilder;
import main.game.pathing.lineofsight.Bresenham;
import main.game.pathing.lineofsight.PathingAlgorithm;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.action.ActionDatabase;
import main.game.stores.pools.action.ActionEvent;
import main.game.systems.actions.behaviors.AggressiveBehavior;
import main.game.systems.actions.behaviors.RandomnessBehavior;
import main.game.systems.combat.CombatReport;
import main.game.systems.texts.FloatingText;
import main.input.InputController;
import main.input.Mouse;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.awt.Color;
import java.util.*;

public class ActionSystem extends GameSystem {
    private final SplittableRandom mRandom = new SplittableRandom();
    private final ELogger mLogger = ELoggerFactory.getInstance().getELogger(ActionSystem.class);
    private final PathingAlgorithm algorithm = new Bresenham();
    private final AggressiveBehavior mAggressiveBehavior = new AggressiveBehavior();
    private final RandomnessBehavior mRandomnessBehavior = new RandomnessBehavior();
    private final PathBuilder mPathBuilder = new PathBuilder();

    private static final String GYRATE = "gyrate";
    private static final String TO_TARGET_AND_BACK = "toTargetAndBack";
    private static final String SHAKE = "shake";
    private static final int DEFAULT_VISION_RANGE = 8;

    private static final String ACTION_SYSTEM = "ACTION_SYSTEM";

    @Override
    public void update(GameModel model, Entity unitEntity) {
        finishAction();

        Behavior behavior = unitEntity.get(Behavior.class);

        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        if (actionComponent.hasActed()) { return; }

        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        if (animationComponent.hasPendingAnimations()) { return; }


        if (behavior.isUserControlled()) {
            updateUser(model, unitEntity);
        } else {
            updateAI(model, unitEntity);
        }
    }

    public void updateUser(GameModel model, Entity unitEntity) {
        boolean isActionPanelOpen = model.getGameState().isActionPanelOpen();
        if (!isActionPanelOpen) { return; }

        if (model.getSpeedQueue().peek() != unitEntity) { return; }

        Mouse mouse = InputController.getInstance().getMouse();
        Entity mousedAt = model.tryFetchingTileMousedAt();

        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        // Check that the unit actually has the ability, not sure why we need to check, but keeping for now
        String action = actionComponent.getAction();
        if (action == null) { return; }

        // Execute the action
        boolean acted = act(model, unitEntity, action, mousedAt, mouse.isPressed());
        actionComponent.setActed(acted);
        if (!acted) { return;  }
        model.getGameState().setAutomaticallyGoToHomeControls(true);
    }

    public void updateAI(GameModel model, Entity unitEntity) {
        if (model.getSpeedQueue().peek() != unitEntity) { return; }

        Behavior behavior = unitEntity.get(Behavior.class);
        if (behavior.shouldWait()) { return; }

        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        if (actionComponent.hasActed()) { return; }
        // Check that the unit actually has the ability, not sure why we need to check, but keeping for now
        Pair<Entity, String> toActOn = mRandomnessBehavior.toActOn(model, unitEntity);
        if (toActOn != null) {
            act(model, unitEntity, toActOn.second, toActOn.first, true);
        }
        actionComponent.setActed(true);
    }

    public void finishAction() {

        if (mLatestAction == null) { return; }

        AnimationComponent animationComponent = mLatestAction.getActor().get(AnimationComponent.class);
        if (animationComponent.hasPendingAnimations()) { return; }

        mLatestAction.getEvent().run();
        mLatestAction = null;
    }

    public boolean startAction(GameModel model, Entity unitEntity, String action, Set<Entity> targetTileEntities) {

        if (targetTileEntities.isEmpty()) { return false; }

        // 1. Check that unit has resources for ability
        boolean canNotPayCosts = !canPayActionCosts(unitEntity, action);
        if (canNotPayCosts) { return false; }

        // 3. Draw ability name to screen
        announceWithStationaryText(model, action, unitEntity, ColorPalette.WHITE);

        int range = ActionDatabase.getInstance().getRange(action);
        // 2. Animate based on the ability range
        applyAnimation(
                model,
                unitEntity,
                range == 1 ? TO_TARGET_AND_BACK : GYRATE,
                targetTileEntities.iterator().next()
        );

        // 4. Cache the combat state...
        ActionEvent actionEvent = new ActionEvent(unitEntity, action, targetTileEntities);
        actionEvent.setDelayedEvent(() -> { finishAction(model, actionEvent); });
        mLatestAction = actionEvent;

        return true;
    }
    private ActionEvent mLatestAction = null;

    private void finishAction(GameModel model, ActionEvent event) {
        mLogger.info("initiates combat");

        Entity actor = event.getActor();
        String action = event.getAction();
        // 0. Pay the ability costs
        payActionCosts(actor, action);

//        model.getSystems().getAnimationSystem()
//        applyEffects(model, event.actor, event, event.action.tagsToUserMap.entrySet());
//
//
//        // 3. Execute the action for all targets
        for (Entity tileEntity : event.getTargets()) {
            Tile tile = tileEntity.get(Tile.class);

            // to remove environment
            if (tile.isNotNavigable()) { tile.deleteStructure(); }
            Entity actedOnUnitEntity = tile.getUnit();
            if (actedOnUnitEntity == null) { continue; }

            boolean successful = ActionDatabase.getInstance().isSuccessful(action);
            mLogger.debug("{} uses {} on {}", actor, event.getAction(), actedOnUnitEntity);

            // 4. Attack if possible
            if (successful) {
                executeHit(model, actor, action, actedOnUnitEntity);
//                applyAnimation(model, tile.getUnit(), SHAKE, unitEntity);
            } else {
                executeMiss(model, actor, event, tile.getUnit());
            }
        }
//        Statistics stats = actor.get(Statistics.class);
//        if (stats.toExperience(random.nextInt(1, 5))) {
//            announceWithFloatingText(gameModel, "Lvl Up!", actor, Color.WHITE);
//        }

        mLogger.debug("{} finishes combat", actor);
    }

    public boolean canPayActionCosts(Entity unitEntity, String action) {
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        List<Map.Entry<String, String>> costList = ActionDatabase.getInstance().getResourceToCostScaling(action);
        for (Map.Entry<String, String> entry : costList) {
            String node = entry.getKey();
            int current = statisticsComponent.getCurrent(node);
            int total = statisticsComponent.getTotal(node);
            String scaling = entry.getValue();
            int cost = ActionDatabase.getInstance().getResourceCost(action, node, scaling, current, total);
            if (cost < current) { continue; }
            return false;
        }
        return true;
    }

    private void payActionCosts(Entity unitEntity, String action) {
        if (!canPayActionCosts(unitEntity, action)) { return; }
        // Deduct the cost from the user
//        List<Map.Entry<String, String>> costList = ActionPool.getInstance().getResourceToCostScaling(action);
//        Map<String, Float> costMap = ActionPool.getInstance().getResourceCosts(action);
//        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
//        for (Map.Entry<String, Float> entry : costMap.entrySet()) {
//            String attribute = entry.getKey();
//            int cost = (int) (entry.getValue() * -1);
//            statisticsComponent.modify(attribute, cost);
//        }

        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        List<Map.Entry<String, String>> costList = ActionDatabase.getInstance().getResourceToCostScaling(action);
        for (Map.Entry<String, String> entry : costList) {
            String node = entry.getKey();
            int current = statisticsComponent.getCurrent(node);
            int total = statisticsComponent.getTotal(node);
            String scaling = entry.getValue();
            int cost = ActionDatabase.getInstance().getResourceCost(action, node, scaling, current, total);
            if (cost < current) { continue; }
            statisticsComponent.modify(node, cost * -1);
        }
    }

    public boolean act(GameModel model, Entity unitEntity, String action, Entity target, boolean commit) {
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        Entity currentTile = movementComponent.getCurrentTile();;
        int range = ActionDatabase.getInstance().getRange(action);
        int area = ActionDatabase.getInstance().getArea(action);

        boolean isUpdated = actionComponent.isUpdated("action_range", currentTile, range);
        if (isUpdated) {
            Set<Entity> rng = algorithm.computeAreaOfSight(model, currentTile, range);
            actionComponent.stageRange(rng);
        }

        isUpdated = actionComponent.isUpdated("action_line_of_sight", currentTile, target);
        if (isUpdated) {
            Set<Entity> los = algorithm.computeLineOfSight(model, currentTile, target);
            actionComponent.stageLineOfSight(los);
        }

        isUpdated = actionComponent.isUpdated("action_area_of_effect", target, area);
        if (isUpdated) {
            Set<Entity> aoe = algorithm.computeAreaOfSight(model, target, area);
            actionComponent.stageAreaOfEffect(aoe);
        }

        // Below may or may not be used
        boolean showVision = model.getGameState().shouldShowActionRanges();
        isUpdated = actionComponent.isUpdated("action_vision_range", currentTile, DEFAULT_VISION_RANGE, showVision);
        if (isUpdated) {
//            Queue<Entity> tilesWithinVision = mPathBuilder.getTilesInActionRange(model, currentTile, DEFAULT_VISION_RANGE);
//            actionComponent.stageVision(tilesWithinVision);
        }

        actionComponent.stageTarget(target);
        actionComponent.stageAction(action);

        // try executing action only if specified
        // - Target is not null
        // - Target is within range
        // - We are not in preview mode
        if (target == null || !commit || !actionComponent.isValidTarget() || actionComponent.hasActed()) {
            return false;
        }
        actionComponent.commit();

        return model.getSystems()
                .getActionSystem()
                .startAction(
                        model,
                        unitEntity,
                        action,
                        actionComponent.getStagedTileAreaOfEffect()
                );
    }

    private  void executeMiss(GameModel model, Entity attacker, ActionEvent event, Entity defender) {
        announceWithStationaryText(model, "Missed!", attacker, ColorPalette.WHITE);
        mLogger.info("{} misses {}", attacker, defender);
    }

    private void executeHit(GameModel model, Entity actorUnitEntity, String action, Entity actedOnUnitEntity) {
//        private void executeHit(GameModel model, ActionEvent event, Entity defender) {

        // 0. Setup
        StatisticsComponent defendingStatisticsComponent = actedOnUnitEntity.get(StatisticsComponent.class);
//        Vector3f defendingVector = defender.get(Animation.class).getVector();
//        Statistics attackingStatistics = attacker.get(Statistics.class);
//        Vector3f attackingVector = attacker.get(Animation.class).getVector();

        // 1. Calculate damage
        CombatReport report = new CombatReport(model, actorUnitEntity, action, actedOnUnitEntity);
        Map<String, Integer> damageMap = report.calculate();

        for (String resource : damageMap.keySet()) {
            int damage = damageMap.get(resource);
            defendingStatisticsComponent.reduceResource(resource, damage);
//            defendingStatisticsComponent.modify(resource, damage);
            String negative = "", positive = "";
            switch (resource) {
                case StatisticsComponent.HEALTH -> {
                    negative = ColorPalette.HEX_CODE_RED;
                    positive = ColorPalette.HEX_CODE_GREEN;
                }
                case StatisticsComponent.MANA -> {
                    negative = ColorPalette.HEX_CODE_PURPLE;
                    positive = ColorPalette.HEX_CODE_BLUE;
                }
                case StatisticsComponent.STAMINA -> {
                    negative = ColorPalette.HEX_CODE_CREAM;
                    positive = ColorPalette.HEX_CODE_GREEN;
                }
            }
            if (damage != 0) {
//                model.mLogger.log(
//                        ColorPalette.getHtmlColor(actorUnitEntity.toString(), ColorPalette.HEX_CODE_GREEN),
//                        StringFormatter.format(
//                                "uses {} {} {}",
//                                ColorPalette.getHtmlColor(String.valueOf(action), ColorPalette.HEX_CODE_CREAM),
//                                actedOnUnitEntity == actorUnitEntity ? "" : "on " + actedOnUnitEntity,
//                                damage > 0 ?
//                                        ColorPalette.getHtmlColor("dealing " + Math.abs(damage) + " Damage", negative) :
//                                        ColorPalette.getHtmlColor("recovering " + Math.abs(damage) + resource, positive)
//                        )
//                );

                boolean isNegative = damage < 0;
                Color color = isNegative ? ColorPalette.RED : ColorPalette.BLUE;
                announceWithFloatingText(model, (isNegative ? "" : "+") + Math.abs(damage) , actedOnUnitEntity, color);
            } else {
//                model.mLogger.log(
//                        ColorPalette.getHtmlColor(actorUnitEntity.toString(), ColorPalette.HEX_CODE_GREEN),
//                        "uses " + action
//                );
            }
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
        model.getSystems().getAnimationSystem().executeShakeAnimation(model, actedOnUnitEntity, ACTION_SYSTEM, true);
//        track.shake(model, actedOnUnitEntity);

        // defender has already queued an attack/is the attacker, don't animate
//        if (mQueue.containsKey(defender)) { return; }

//        track.shake(model, defender);
    }

    private void applyEffects(GameModel model, Entity target, ActionEvent event, Set<Map.Entry<String, Float>> statuses) {
        // Go through all the different status effects and their probability
        StatisticsComponent statisticsComponent = target.get(StatisticsComponent.class);
        for (Map.Entry<String, Float> entry : statuses) {
            // If the stat chance passes, handle
            float statusChance = Math.abs(entry.getValue());
            if (statusChance < mRandom.nextFloat()) { continue; }
            // Check if the status effect increases a stat
            String status = entry.getKey();
//            StatNode node = statistics.getStatsNode(status);


            if (status.endsWith("Knockback")) {
//                handleKnockback(model, target, event);
            } else {
                target.get(TagComponent.class).add(status, event.getAction());
            }
//            Color c = ColorPalette.getColorOfAbility(event.action);

//            if (node != null) {
//                statistics.modify(status,
//                        event.ability, StatNode.MULTIPLICATIVE, (int) (entry.getValue() <  0 ? -.5f : .5f));
//                model.logger.log(target + "'s " + status + " " +
//                        (entry.getValue() <  0 ? "decreased" : "increased"));
//
//                announceWithFloatingText(gameModel, (entry.getValue() <  0 ? "-" : "+") +
//                                StringUtils.spaceByCapitalization(status), target, c);
//            } else {
//                announceWithFloatingText(gameModel,
//                        StringUtils.spaceByCapitalization(status) + "'d", target, c);
//                model.logger.log(target + " was inflicted with " + StringUtils.spaceByCapitalization(status));
//            }
            mLogger.info("{} has {}", target, status);
        }
    }

    public void applyAnimation(GameModel model, Entity unitEntity, String animation, Entity target) {
        if (unitEntity == null) { return; }
        AnimationSystem animationSystem = model.getSystems().getAnimationSystem();
        switch (animation) {
            case TO_TARGET_AND_BACK -> animationSystem.executeToTargetAndBackAnimation(model, unitEntity, target, ACTION_SYSTEM, true);
            case GYRATE -> animationSystem.executeGyrateAnimation(model, unitEntity, ACTION_SYSTEM, true);
            case SHAKE -> animationSystem.executeShakeAnimation(model, unitEntity, ACTION_SYSTEM, true);
        }
    }

//    public void applyAnimation(GameModel model, Entity unitEntity, String animation, Entity target) {
//        if (unitEntity == null) { return; }
//        AnimationSystem animationSystem = model.getSystems().getAnimationSystem();
//        switch (animation) {
//            case TO_TARGET_AND_BACK -> animationSystem.executeToTargetAndBackAnimation(model, unitEntity, target);
//            case GYRATE -> animationSystem.executeGyrateAnimation(model, unitEntity);
//            case SHAKE -> animationSystem.executeShakeAnimation(model, unitEntity);
//        }
//    }

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

        model.getGameState().addFloatingText(new FloatingText(str, x, y, color, true));
    }


    private void announceWithFloatingText(GameModel model, String str, Entity unitEntity, Color color) {
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity tileEntity = movementComponent.getCurrentTile();
        if (tileEntity == null) { return; }
        Tile tile = tileEntity.get(Tile.class);
        Vector3f vector3f = tile.getLocalVector(model);


        int spriteWidths = model.getGameState().getSpriteWidth();
        int spriteHeights = model.getGameState().getSpriteHeight();
        int x = (int) vector3f.x + random.nextInt((spriteWidths / 2) * -1, (spriteWidths / 2));
        int y = (int) vector3f.y + random.nextInt((spriteHeights / 2) * -1, spriteHeights / 2);

        model.getGameState().addFloatingText(new FloatingText(str, x, y, color, false));
    }
}
