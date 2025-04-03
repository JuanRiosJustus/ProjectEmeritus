package main.game.events;

import main.constants.Direction;
import main.game.components.*;
import main.game.components.behaviors.Behavior;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathing.lineofsight.PathingAlgorithms;
import main.game.stores.factories.EntityStore;
import main.game.systems.GameSystem;
import main.game.systems.actions.behaviors.AggressiveBehavior;
import main.game.systems.actions.behaviors.RandomnessBehavior;
import main.input.InputController;
import main.logging.EmeritusLogger;

import org.json.JSONObject;

import java.util.List;

public class MovementSystem extends GameSystem {
    private final EmeritusLogger mLogger = EmeritusLogger.create(MovementSystem.class);
    private final AggressiveBehavior mAggressiveBehavior = new AggressiveBehavior();
    private final RandomnessBehavior mRandomnessBehavior = new RandomnessBehavior();
    private final PathingAlgorithms algorithm = new PathingAlgorithms();
    private InputController mInput = null;

    public static String MOVE_ENTITY_EVENT = "entity_move_event";


    public MovementSystem() { }
    public MovementSystem(GameModel gameModel) {
        super(gameModel);

        mEventBus.subscribe(MOVE_ENTITY_EVENT, this::tryMovingEntity);
    }


    public static JSONObject createMoveEntityEvent(String unitToMoveID, String tileToMoveUnitToID, boolean tryCommitting) {
        JSONObject event = new JSONObject();
        event.put("unit_to_move_id", unitToMoveID);
        event.put("tile_to_move_unit_to_id", tileToMoveUnitToID);
        event.put("commit", tryCommitting);
        return event;
    }

    private void tryMovingEntity(JSONObject event) {
        String unitToMoveID = event.optString("unit_to_move_id", null);
        String tileToMoveUnitToID = event.optString("tile_to_move_unit_to_id", null);
        boolean commit = event.getBoolean("commit");

        if (unitToMoveID == null || tileToMoveUnitToID == null) { return; }

        Entity unitEntity = EntityStore.getInstance().get(unitToMoveID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        if (movementComponent.hasMoved()) { return; }

        boolean moved = move(mGameModel, unitToMoveID, tileToMoveUnitToID, commit);
        if (!moved) { return; }

        movementComponent.setMoved(true);
        mGameModel.getGameState().setAutomaticallyGoToHomeControls(true);
        mLogger.info("{} has moved");
    }

    public void update(GameModel model, String unitID) {
        // Only move if its entities turn
        String unityEntityID = model.getSpeedQueue().peek();
        if (unityEntityID == null) { return; }

        Entity unitEntity = getEntityWithID(unityEntityID);
        Behavior behavior = unitEntity.get(Behavior.class);

        if (behavior.isUserControlled()) { return; }

        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        if (movementComponent.hasMoved()) { return; }

        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        if (animationComponent.hasPendingAnimations()) { return; }

        Behavior unitBehavior = unitEntity.get(Behavior.class);
        if (unitBehavior.shouldWait()) { return; }

        String tileToMoveToID = mRandomnessBehavior.toMoveTo(model, unityEntityID);
        if (tileToMoveToID == null) {  movementComponent.setMoved(true); return; }


        if (mEventBus == null) {
            System.out.println("SERIOUS ERROR");
        }

        mEventBus.publish(MovementSystem.MOVE_ENTITY_EVENT, MovementSystem.createMoveEntityEvent(
                unityEntityID, tileToMoveToID, true
        ));
    }

    private boolean move(GameModel model, String unitToMoveID, String tileToMoveUnitToID, boolean commit) {
        Entity unitEntity = getEntityWithID(unitToMoveID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        int move = statisticsComponent.getTotalMovement();
        int climb = statisticsComponent.getTotalClimb();

        String toMoveFromTileID = movementComponent.getCurrentTileID();
        Entity toMoveFromTileEntity = getEntityWithID(toMoveFromTileID);
        Entity toMoveToTileEntity = getEntityWithID(tileToMoveUnitToID);

        // This can be flood the console, statelock to prevent flooding, probably not necessary
        boolean shouldUpdateLogger = isUpdated("planning_to_move_logger", toMoveFromTileEntity, toMoveToTileEntity);
        if (shouldUpdateLogger) {
            mLogger.info("{} is planning to move from {} to {}", unitEntity, toMoveFromTileEntity, toMoveToTileEntity);
        }

        // Only update then the tile to move from has changed, or the units move or climb stat changed
        boolean isUpdated = isUpdated("movement_range", toMoveFromTileID, move, climb);
        if (isUpdated) {
            List<String> area = algorithm.computeMovementAreaV2(model, toMoveFromTileEntity, move);
            movementComponent.stageMovementRange(area);
            mLogger.info("Updated movement area for {}", unitEntity);
        }

        // Only update when the tile to move to has changed, or the units move or climb stat changed
        isUpdated = isUpdated("movement_path", tileToMoveUnitToID, move, climb);
        if (isUpdated) {
            List<String> path = algorithm.computeMovementPathV2(model, toMoveFromTileEntity, toMoveToTileEntity);
            movementComponent.stageMovementPath(path);
            mLogger.info("Updated movement path for {}", unitEntity);
        }

//        movementComponent.stageTarget(toMoveToTileEntity);
        movementComponent.stageTarget(tileToMoveUnitToID);

        // try executing action only if specified
        // - Target is not null
        // - Target is within range
        // - We are not in preview mode
        // - We are targeting the current tile were one
        if (!commit) { return false; }
        if (toMoveToTileEntity == null) { return false; }
        if (toMoveToTileEntity == toMoveFromTileEntity) { return false; }
        if (!movementComponent.isValidMovementPath()) { return false; }

        movementComponent.commit();

        // do the animation for the tile
//        mEventBus.publish(AnimationSystem.EXECUTE_MOVE_ANIMATION_EVENT, AnimationSystem.createExecuteMoveAnimationEvent(
//                unitToMoveID, movementComponent.getTilesInFinalMovementPath()
//        ));

        mEventBus.publish(AnimationSystem.EXECUTE_ANIMATION_EVENT, AnimationSystem.createExecuteAnimationEvent(
                unitToMoveID, AnimationSystem.WALK_ANIMATION,  movementComponent.getTilesInFinalMovementPath()
        ));

        Tile tile = toMoveToTileEntity.get(Tile.class);
        tile.setUnit(unitToMoveID);

//        DirectionComponent directionComponent = unitEntity.get(DirectionComponent.class);
//        directionComponent.setDirection(getDirection(currentTile, tileToMoveTo));


        mLogger.info("Moved from {} to {}", toMoveFromTileEntity, toMoveToTileEntity);
        return true;
    }

    public Direction getDirection(Entity start, Entity end) {
        Tile startTile = start.get(Tile.class);
        Tile endTile = end.get(Tile.class);

        // top left tile is 0,0 - top right is 0,N, bottom right is N,N
        // Thus, a positive number indicates movement north
        int verticalDirectionMagnitude = startTile.getRow() - endTile.getRow();
        Direction vertidalDirection = Direction.South;
        if (verticalDirectionMagnitude > 0) {
            vertidalDirection = Direction.North;
        }

        // a positive number indicates movement west
        int horizontalDirectionMagnitude = startTile.getColumn() - endTile.getColumn();
        Direction horizontalDirection = Direction.East;
        if (horizontalDirectionMagnitude > 0) {
            horizontalDirection = Direction.West;
        }

        Direction mostObviousDirection = vertidalDirection;
        if (Math.abs(horizontalDirectionMagnitude) > Math.abs(verticalDirectionMagnitude)) {
            mostObviousDirection = horizontalDirection;
        }

        return mostObviousDirection;
    }

//    public void undo(GameModel model, Entity unit) {
//        MovementComponent movementComponent = unit.get(MovementComponent.class);
//        movementComponent.moved = false;
//
//        Entity previous = movementComponent.previousTile;
//
//        movementComponent.useTrack = false;
//        move(model, unit, previous, true);
//        movementComponent.useTrack = true;
//        movementComponent.previousTile = null;
//
//        // handle waiting tile selection state
//        movementComponent.moved = false;
//    }

//    public boolean forceMove(GameModel model, Entity unit, Entity toMoveTo) {
//        MovementComponent movementComponent = unit.get(MovementComponent.class);
//        boolean hasMoved = movementComponent.mHasMoved;
//        boolean moved = move(model, unit, toMoveTo, true);
//        return moved;
//    }

//    public int getSpeed(GameModel model, int speed1, int speed2) {
//        int spriteWidth = model.getGameState().getSpriteWidth();
//        int spriteHeight = model.getGameState().getSpriteHeight();
//        float spriteSize = (float) (spriteWidth + spriteHeight) / 2;
//        return (int) (spriteSize * RandomUtils.getRandomNumberBetween(speed1, speed2));
//    }
}
