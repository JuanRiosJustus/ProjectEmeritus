package main.game.systems;

import main.constants.Pair;
import main.constants.Vector3f;
import main.constants.csv.CsvRow;
import main.game.components.*;
import main.game.components.behaviors.Behavior;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathfinding.PathBuilder;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.action.ActionPool;
import main.game.stores.pools.action.ActionEvent;
import main.game.systems.actions.behaviors.AggressiveBehavior;
import main.game.systems.actions.behaviors.RandomnessBehavior;
import main.game.systems.combat.DamageCalculator;
import main.graphics.Animation;
import main.input.InputController;
import main.input.Mouse;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.awt.Color;
import java.util.*;

public class ActionSystem extends GameSystem {
    private final SplittableRandom mRandom = new SplittableRandom();
    private final ELogger mLogger = ELoggerFactory.getInstance().getELogger(ActionSystem.class);
    private final AggressiveBehavior mAggressiveBehavior = new AggressiveBehavior();
    private final RandomnessBehavior mRandomnessBehavior = new RandomnessBehavior();

    private static final String GYRATE = "gyrate";
    private static final String TO_TARGET_AND_BACK = "toTargetAndBack";
    private static final String SHAKE = "shake";
    private final Queue<ActionEvent> mQueueV2 = new LinkedList<>();

    @Override
    public void update(GameModel model, Entity unitEntity) {
        handlePendingActions(model, unitEntity);
        if (model.getSpeedQueue().peek() != unitEntity) { return; }

        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        if (actionComponent.hasActed()) { return; }
        MovementTrackComponent movementTrackComponent = unitEntity.get(MovementTrackComponent.class);
        if (movementTrackComponent.isMoving()) { return; }
        Behavior behavior = unitEntity.get(Behavior.class);
        if (behavior.isUserControlled()) {
            updateUser(model, unitEntity);
        } else {
            updateAI(model, unitEntity);
        }
    }

    public void handlePendingActions(GameModel model, Entity unitEntity) {
        ActionEvent event = mQueueV2.poll();

        if (event == null) { return; }

//        // 2. wait next loop to check if attacker has finished animating
//        boolean isFastForwarding = model.getSettings().getBoolean(Settings.GAMEPLAY_FAST_FORWARD_TURNS);
//        MovementTrackComponent track = unitEntity.get(MovementTrackComponent.class);
//        if (!isFastForwarding && track.isMoving()) { return; }

        // 3. Finish the combat by applying the damage to the defending units. Remove from queue
        finishAction(model, event);
    }


    public void updateUser(GameModel model, Entity unitEntity) {

        boolean isActionPanelOpen = model.getGameState().isActionPanelOpen();
        if (!isActionPanelOpen) { return; }

        Mouse mouse = InputController.getInstance().getMouse();
        Entity mousedAt = model.tryFetchingTileMousedAt();

        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        // Check that the unit actually has the ability, not sure why we need to check, but keeping for now
        String action = actionComponent.getAction();
        if (action == null) { return; }

        // Execute the action
        act(model, unitEntity, action, mousedAt, false);
        if (actionComponent.hasActed() || !mouse.isPressed()) { return; }

        boolean acted = act(model, unitEntity, action, mousedAt, true);
        actionComponent.setActed(acted);
        if (acted) {
            model.getGameState().setControllerToHomeScreen(acted);
        }
    }
    public void updateAI(GameModel model, Entity unitEntity) {
        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        if (actionComponent.hasActed()) { return; }
        // Check that the unit actually has the ability, not sure why we need to check, but keeping for now
        Pair<Entity, String> toActOn = mRandomnessBehavior.toActOn(model, unitEntity);
        if (toActOn != null) {
            act(model, unitEntity, toActOn.item2, toActOn.item1, true);
        }
        actionComponent.setActed(true);
    }

    public boolean startAction(GameModel model, Entity unitEntity, String action, Set<Entity> targetTileEntities) {

        if (targetTileEntities.isEmpty()) { return false; }

        CsvRow actionRow = ActionPool.getInstance().getAction(action);
        // 1. Check that unit has resources for ability
        boolean canNotPayCosts = !canPayActionCosts(unitEntity, action);
        if (canNotPayCosts) { return false; }

//        // 0. if the ability can't affect the user, remove if available
//        if (!action.hasTag(TagComponent.CAN_FRIENDLY_FIRE)) {
//            MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//            tileEntityTargets.remove(movementComponent.getCurrentTile());
//        }
//
        int range = ActionPool.getInstance().getRange(action);
        // 2. Animate based on the ability range
        applyAnimation(
                model,
                unitEntity,
                range == 1 ? TO_TARGET_AND_BACK : GYRATE,
                targetTileEntities.iterator().next()
        );

        // 3. Draw ability name to screen
        String name = ActionPool.getInstance().getName(action);
        announceWithFloatingText(model, name, unitEntity, ColorPalette.getColorOfAbility(actionRow));

        // 4. Cache the combat state...
        mQueueV2.add(new ActionEvent(unitEntity, null, action, targetTileEntities));

        return true;
    }

    private void finishAction(GameModel model, ActionEvent event) {
        mLogger.info("initiates combat");

        Entity actor = event.getActor();
        String action = event.getAction();
//        CsvRow actionData = ActionPool.getInstance().getAction(event.getAction());
        // 0. Pay the ability costs
        payActionCosts(actor, action);
//
////        applyEffects(model, event.actor, event, event.action.tagsToUserMap.entrySet());
//
//
//        // 3. Execute the action for all targets
        for (Entity tileEntity : event.getTargets()) {
            Tile tile = tileEntity.get(Tile.class);

            // to remove environment
            if (tile.isNotNavigable()) { tile.removeStructure(); }
            Entity actedOnUnitEntity = tile.getUnit();
            if (actedOnUnitEntity == null) { continue; }

            boolean successful = ActionPool.getInstance().isSuccessful(action);
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
        Map<String, Float> costMap = ActionPool.getInstance().getResourceCosts(unitEntity, action);
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        for (Map.Entry<String, Float> entry : costMap.entrySet()) {
            int current = statisticsComponent.getStatCurrent(entry.getKey());
            if (current < entry.getValue()) { return false; }
        }
        return true;
    }

    private void payActionCosts(Entity unitEntity, String action) {
        if (!canPayActionCosts(unitEntity, action)) { return; }
        // Deduct the cost from the user
        Map<String, Float> costMap = ActionPool.getInstance().getResourceCosts(unitEntity, action);
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        for (Map.Entry<String, Float> entry : costMap.entrySet()) {
            String resource = entry.getKey();
            int cost = (int) (entry.getValue() * -1);
            statisticsComponent.modify(resource, cost);
        }
    }

    public boolean isChangedState(Entity unitEntity) {
        MovementComponent movement = unitEntity.get(MovementComponent.class);
        int currentState = Objects.hash(movement.getPreviewTilesInPath(), movement.getPreviewTilesInRange());
        if (currentState == movement.getCurrentStateHash()) { return false; }
        movement.setCurrentStateHash(currentState);
        return true;
    }

    public boolean act(GameModel model, Entity unit, String action, Entity target, boolean commit) {
        MovementComponent movementComponent = unit.get(MovementComponent.class);
        // TODO, this can be optimized to not create new path

        Set<Entity> tilesWithinRange = PathBuilder.newBuilder().inVisionRange(
                model,
                movementComponent.getCurrentTile(),
                ActionPool.getInstance().getRange(action)
        );

        LinkedList<Entity> tileWithinLineOfSight = PathBuilder.newBuilder().inLineOfSight(
                model,
                movementComponent.getCurrentTile(),
                target
        );

        Set<Entity> tilesWithinAreaOfEffect = PathBuilder.newBuilder().inVisionRange(
                model,
                target,
                ActionPool.getInstance().getArea(action)
        );

        Set<Entity> tileWithinLineOfSightTotality = PathBuilder.newBuilder().inVisionRange(
                model,
                movementComponent.getCurrentTile(),
                5
        );

        ActionComponent actionComponent = unit.get(ActionComponent.class);
        actionComponent.setRange(tilesWithinRange);
        actionComponent.setLineOfSight(tileWithinLineOfSight);
        actionComponent.setAreaOfEffect(tilesWithinAreaOfEffect);
        actionComponent.setTarget(target);
        actionComponent.setAction(action);

        actionComponent.setVision(tileWithinLineOfSightTotality);

        // try executing action only if specified
        // - Target is not null
        // - Target is within range
        // - We are not in preview mode
        if (target == null || !commit || !tilesWithinRange.contains(target)) { return false; }
        actionComponent.commit();

        return model.getSystems()
                .getActionSystem()
                .startAction(
                        model,
                        unit,
                        action,
                        tilesWithinAreaOfEffect
                );
    }

    private  void executeMiss(GameModel model, Entity attacker, ActionEvent event, Entity defender) {
        Vector3f vector = attacker.get(Animation.class).getVector();
//        model.mSystem.mFloatingTextSystem.enqueue("Missed!", vector, ColorPalette.getColorOfAbility(event.actionName));
        model.mSystem.mFloatingTextSystem.enqueue("Missed!", vector, ColorPalette.WHITE);
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
        DamageCalculator report = new DamageCalculator(model, actorUnitEntity, action, actedOnUnitEntity);
        Map<String, Integer> damageMap = report.calculate();

        for (String resource : damageMap.keySet()) {
            int damage = damageMap.get(resource);
            int critical = 0;//(int) report.getCritical(resource);
            defendingStatisticsComponent.modify(resource, -damage);
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
                announceWithFloatingText(model, (isNegative ? "" : "+") + Math.abs(damage) ,
                        actedOnUnitEntity, color);
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
        if (model.mSpeedQueue.removeIfNoCurrentHealth(actedOnUnitEntity)) {
            announceWithStationaryText(model, "Dead!", actedOnUnitEntity, ColorPalette.NORMAL_TYPE);
            return;
        }

        // 3. apply status effects to target
//        applyEffects(model, actedOnUnitEntity, event, event.action.conditionsToTargetsChances.entrySet());

        // don't move if already performing some action
        MovementTrackComponent track = actedOnUnitEntity.get(MovementTrackComponent.class);
        if (track.isMoving()) { return; }

//        new Thread(() -> {
//            MovementTrackComponent actorMovementTrack = actorUnitEntity.get(MovementTrackComponent.class);
//            MovementTrackComponent track1 = actedOnUnitEntity.get(MovementTrackComponent.class);
//            int attempts = 3;
//            while (actorMovementTrack.isMoving() && attempts > 0) {
//                try {
//                    Thread.sleep(1000);
//                    attempts--;
//                } catch (InterruptedException ignored) { }
//            }
//            track1.shake(model, actedOnUnitEntity);
//        });
        track.shake(model, actedOnUnitEntity);

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
                target.get(TagComponent.class).add(status, event.action);
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
        MovementTrackComponent track = unitEntity.get(MovementTrackComponent.class);
        switch (animation) {
            case TO_TARGET_AND_BACK -> track.toTargetAndBack(model, unitEntity, target);
            case GYRATE -> track.gyrate(model, unitEntity);
            case SHAKE -> track.shake(model, unitEntity);
        }
    }

    private void announceWithStationaryText(GameModel model, String str, Entity unitEntity, Color color) {
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity tileEntity = movementComponent.getCurrentTile();
        if (tileEntity == null) { return; }
        Tile tile = tileEntity.get(Tile.class);
        Vector3f vector3f = tile.getLocation(model);
        model.getSystems().getFloatingTextSystem().enqueueStationary(str, vector3f, color);
    }


    private void announceWithFloatingText(GameModel model, String str, Entity unitEntity, Color color) {
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity tileEntity = movementComponent.getCurrentTile();;
        model.getSystems().getFloatingTextSystem().enqueue(model, tileEntity, str, color);
    }
}
