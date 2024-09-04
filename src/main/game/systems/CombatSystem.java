package main.game.systems;


import main.game.entity.Entity;
import main.game.main.GameModel;

public class CombatSystem extends GameSystem {
//
//    private final Map<Entity, ActionEvent> mQueue = new HashMap<>();
//    private final SplittableRandom mRandom = new SplittableRandom();
//    private final ELogger logger = ELoggerFactory.getInstance().getELogger(CombatSystem.class);
//    public static final String GYRATE_ANIMATION = "GYRATE";
//    public static final String FORWARDS_AND_BACK_ANIMATION = "FORWARDS_AND_BACK";
//    public static final String SHAKE_ANIMATION = "SHAKE";
//
    @Override
    public void update(GameModel model, Entity unit) {
    }
//        // 1. if the current unit is not in queue, skip
//        ActionEvent event = mQueue.get(unit);
//        if (event == null) { return; }
//
//        // 2. wait next loop to check if attacker has finished animating
//        boolean isFastForwarding = Settings.getInstance().getBoolean(Settings.GAMEPLAY_FAST_FORWARD_TURNS);
//        MovementTrackComponent track = unit.get(MovementTrackComponent.class);
//        if (!isFastForwarding && track.isMoving()) { return; }
//
//        // 3. Finish the combat by applying the damage to the defending units. Remove from queue
//        finishCombat(model, unit, event);
//        mQueue.remove(unit);
//    }
//
//    public boolean startCombat(GameModel model, Entity user, Action action, Set<Entity> attackAt) {
//
//        // 0. if the ability can't affect the user, remove if available
//        if (!action.hasTag(TagComponent.CAN_FRIENDLY_FIRE)) {
//            MovementComponent movementComponent = user.get(MovementComponent.class);
//            attackAt.remove(movementComponent.getCurrentTile());
//        }
//        if (attackAt.isEmpty()) { return false; }
//
//        // 1. Check that unit has resources for ability
//        if (action.canNotPayCosts(user)) { return false; }
//
//        // 2. Animate based on the abilities range
//        applyAnimation(
//                model,
//                user,
//                action.travel.contains("Melee") ? FORWARDS_AND_BACK_ANIMATION : GYRATE_ANIMATION,
//                attackAt.iterator().next()
//        );
//
//        // 3. Draw ability name to screen
////        announceWithFloatingText(model, action.name, user, ColorPalette.getColorOfAbility(action));
//
//        // 4. Cache the combat state...
//        mQueue.put(user, new ActionEvent(user, action, attackAt));
//
//        return true;
//    }
//
//    private void finishCombat(GameModel model, Entity attacker, ActionEvent event) {
//        logger.debug("{} initiates combat", attacker);
//
//        // 0. Pay the ability costs
//        payAbilityCosts(event);
//
////        applyEffects(model, event.actor, event, event.action.tagsToUserMap.entrySet());
//
//
//        // 3. Execute a hit on all selected defenders
//        for (Entity tileEntity : event.targets) {
//            Tile tile = tileEntity.get(Tile.class);
//
//            if (tile.isNotNavigable()) { tile.removeStructure(); }
//            if (tile.getUnit() == null) { continue; }
//
//            boolean hit = MathUtils.passesChanceOutOf100(event.action.accuracy);
//            logger.debug("{} uses {} on {}", attacker, event.action.name, tileEntity);
//
//            // 4. Attack if possible
//            if (hit) {
//                executeHit(model, attacker, event, tile.getUnit());
//                applyAnimation(model, tile.getUnit(), SHAKE_ANIMATION, null);
//            } else {
//                executeMiss(model, attacker, event, tile.getUnit());
//            }
//        }
////        Statistics stats = attacker.get(Statistics.class);
////        if (stats.toExperience(random.nextInt(1, 5))) {
////            announceWithFloatingText(gameModel, "Lvl Up!", attacker, Color.WHITE);
////        }
//
//        logger.debug("{} finishes combat", attacker);
//    }
//
//    private  void executeMiss(GameModel model, Entity attacker, ActionEvent event, Entity defender) {
//        Vector3f vector = attacker.get(Animation.class).getVector();
////        model.mSystem.mFloatingTextSystem.enqueue("Missed!", vector, ColorPalette.getColorOfAbility(event.action));
//        model.mSystem.mFloatingTextSystem.enqueue("Missed!", vector, ColorPalette.WHITE);
//        logger.info("{} misses {}", attacker, defender);
//    }
//
//    private  void executeHit(GameModel model, Entity attacker, ActionEvent event, Entity defender) {
//
//        // 0. Setup
//        StatisticsComponent defendingStatisticsComponent = defender.get(StatisticsComponent.class);
////        Vector3f defendingVector = defender.get(Animation.class).getVector();
////        Statistics attackingStatistics = attacker.get(Statistics.class);
////        Vector3f attackingVector = attacker.get(Animation.class).getVector();
//
//        // 1. Calculate damage
////        DamageCalculator report = new DamageCalculator(model, attacker, event.action, defender);
//        DamageHandler report = new DamageHandler(model, attacker, event.getAction(), defender);
//        Map<String, Integer> damageMap = report.calculate();
//
//        for (String resource : report.getDamageKeys()) {
//            int damage = (int) report.getDamage(resource);
//            int critical = (int) report.getCritical(resource);
//            defendingStatisticsComponent.modify(resource, -damage);
//            String negative = "", positive = "";
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
//                model.mLogger.log(
//                        ColorPalette.getHtmlColor(attacker.toString(), ColorPalette.HEX_CODE_GREEN),
//                        StringFormatter.format(
//                                "uses {} {} {}",
//                                ColorPalette.getHtmlColor(String.valueOf(event.action), ColorPalette.HEX_CODE_CREAM),
//                                defender == attacker ? "" : "on " + defender,
//                                damage > 0 ?
//                                        ColorPalette.getHtmlColor("dealing " + Math.abs(damage) + " Damage", negative) :
//                                        ColorPalette.getHtmlColor("recovering " + Math.abs(damage) + resource, positive)
//                        )
//                );
//
//                MovementComponent defenderMovementComponent = defender.get(MovementComponent.class);
//                Entity tileEntity = defenderMovementComponent.getCurrentTile();
//                Tile tile = tileEntity.get(Tile.class);
//
//                model.mSystem.mFloatingTextSystem.enqueue((critical != 0 ? "!" : "") + (damage <  0 ? "+" : "") +
//                                Math.abs(damage), tile.getLocation(model),
//                        ColorPalette.RED);
//            } else {
//                model.mLogger.log(
//                        ColorPalette.getHtmlColor(attacker.toString(), ColorPalette.HEX_CODE_GREEN), "uses " + event.action
//                );
//            }
//        }
//
//        // Draw the correct combat animations
////        applyAnimationsBasedOnAbility(model, event.ability, defender, health, energy, buffValue);
//
//        // 2. If the defender has no more health, just remove
//        if (model.mSpeedQueue.removeIfNoCurrentHealth(defender)) {
//            announceWithStationaryText(model, "Dead!", defender, ColorPalette.NORMAL_TYPE);
//            return;
//        }
//
//        // 3. apply status effects to target
//        applyEffects(model, defender, event, event.action.conditionsToTargetsChances.entrySet());
//
//        // don't move if already performing some action
//        MovementTrackComponent track = defender.get(MovementTrackComponent.class);
//        if (track.isMoving()) { return; }
//
//        // defender has already queued an attack/is the attacker, don't animate
//        if (mQueue.containsKey(defender)) { return; }
//
////        track.shake(model, defender);
//    }
//
//    private void applyAnimationsToTargets(GameModel model, Action action, Set<Entity> targets) {
//
//
//        for (Entity entity : targets) {
//
//        }
//
//        String type = EmeritusUtils.getAbilityTypes(action);
//        Animation animation = AssetPool.getInstance().getAbilityAnimation(model, type);
//
//        if (animation == null) { logger.error("Some reason, the animation returned null"); }
//
//        // animation.lengthenAnimation();
//
////        model.mSystem.combatAnimation.apply(targets, animation);
//    }
//
//    private void applyEffects(GameModel model, Entity target, ActionEvent event, Set<Map.Entry<String, Float>> statuses) {
//        // Go through all the different status effects and their probability
//        StatisticsComponent statisticsComponent = target.get(StatisticsComponent.class);
//        for (Map.Entry<String, Float> entry : statuses) {
//            // If the stat chance passes, handle
//            float statusChance = Math.abs(entry.getValue());
//            if (statusChance < mRandom.nextFloat()) { continue; }
//            // Check if the status effect increases a stat
//            String status = entry.getKey();
////            StatNode node = statistics.getStatsNode(status);
//
//
//            if (status.endsWith("Knockback")) {
//                handleKnockback(model, target, event);
//            } else {
//                target.get(TagComponent.class).add(status, event.action);
//            }
////            Color c = ColorPalette.getColorOfAbility(event.action);
//
////            if (node != null) {
////                statistics.modify(status,
////                        event.ability, StatNode.MULTIPLICATIVE, (int) (entry.getValue() <  0 ? -.5f : .5f));
////                model.logger.log(target + "'s " + status + " " +
////                        (entry.getValue() <  0 ? "decreased" : "increased"));
////
////                announceWithFloatingText(gameModel, (entry.getValue() <  0 ? "-" : "+") +
////                                StringUtils.spaceByCapitalization(status), target, c);
////            } else {
////                announceWithFloatingText(gameModel,
////                        StringUtils.spaceByCapitalization(status) + "'d", target, c);
////                model.logger.log(target + " was inflicted with " + StringUtils.spaceByCapitalization(status));
////            }
//            logger.info("{} has {}", target, status);
//        }
//    }
//
//    private static void handleKnockback(GameModel model, Entity target, ActionEvent event) {
//        Tile actorTile = event.actor.get(MovementComponent.class).currentTile.get(Tile.class);
//        Tile targetTile = target.get(MovementComponent.class).currentTile.get(Tile.class);
//        Direction direction = TileUtils.getDirectionFrom(targetTile, actorTile);
//        Entity toMoveTo = null;
//        for (int i = 1; i < 3; i++) {
//            Entity toCheck = model.tryFetchingTileAt(targetTile.row + (direction.y * i),
//                    targetTile.column + (direction.x * i));
//            if (toCheck == null) { break; }
//            Tile location = toCheck.get(Tile.class);
//            if (location.isWall() || location.isNotNavigable()) { continue; }
//            toMoveTo = toCheck;
//        }
//        if (toMoveTo == null) { return; }
//        MovementSystem movementSystem = new MovementSystem();
//        movementSystem.forceMove(model, target, toMoveTo);
//    }
//
//    public void payAbilityCosts(ActionEvent event) {
//        Entity unit = event.actor;
//        Action action = event.action;
//
//        // Deduct the cost from the user
//        StatisticsComponent statisticsComponent = unit.get(StatisticsComponent.class);
//        for (String key : statisticsComponent.getResourceKeys()) {
//            int cost = action.getCost(unit, key);
//
//            statisticsComponent.modify(key,  -cost);
//        }
//    }
//
//    public void applyAnimationToUser(GameModel model, Entity actor, Action action, Set<Entity> targets) {
//        MovementTrackComponent track = actor.get(MovementTrackComponent.class);
//        if (action.travel.contains("Melee")) {
//            Entity tile = targets.iterator().next();
//            track.toTargetAndBack(model, actor, tile);
//        } else {
//            track.gyrate(model, actor);
//        }
//    }
//    public void applyAnimation(GameModel model, Entity unitEntity, String animation, Entity target) {
//        if (unitEntity == null) { return; }
//        MovementTrackComponent track = unitEntity.get(MovementTrackComponent.class);
//        switch (animation) {
//            case FORWARDS_AND_BACK_ANIMATION -> track.toTargetAndBack(model, unitEntity, target);
//            case GYRATE_ANIMATION -> track.gyrate(model, unitEntity);
//            case SHAKE_ANIMATION -> track.shake(model, unitEntity);
//        }
//    }
//
//    private void announceWithStationaryText(GameModel model, String announcement, Entity unitEntity, Color color) {
//        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//        Entity tileEntity = movementComponent.getCurrentTile();
//        if (tileEntity == null) { return; }
//        Tile tile = tileEntity.get(Tile.class);
//        Vector3f vector3f = tile.getLocation(model);
//        model.mSystem.mFloatingTextSystem.enqueueStationary(announcement, vector3f, color);
//    }
//
//    private void announceWithFloatingText(GameModel model, String announcement, Entity unitEntity, Color color) {
//        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//        Entity tileEntity = movementComponent.getCurrentTile();;
//        Tile tile = tileEntity.get(Tile.class);
//        Vector3f vector3f = tile.getLocation(model);
//        model.mSystem.mFloatingTextSystem.enqueue(announcement, vector3f, color);
//    }
}
