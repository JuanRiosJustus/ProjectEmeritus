package main.game.systems;

import main.constants.Direction;
import main.game.components.*;
import main.game.components.ActionsComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.TileComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.pathing.lineofsight.PathingAlgorithms;
import main.game.stores.EntityStore;
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


    public MovementSystem(GameModel gameModel) {
        super(gameModel);

        mEventBus.subscribe(MOVE_ENTITY_EVENT, this::handleMoveEntityEvent);
    }




    public static String MOVE_ENTITY_EVENT = "entity.move.event";
    private static final String MOVE_ENTITY_EVENT_ENTITY_TO_MOVE_ID = "entity.to.move.id";
    private static final String MOVE_ENTITY_EVENT_TILE_TO_MOVE_ENTITY_TO_ID = "tile.to.move.entity.to.id";
    private static final String MOVE_ENTITY_EVENT_COMMIT = "commit";
    public static JSONObject createMoveEntityEvent(String unitToMoveID, String tileToMoveUnitToID, boolean tryCommitting) {
        JSONObject event = new JSONObject();
        event.put(MOVE_ENTITY_EVENT_ENTITY_TO_MOVE_ID, unitToMoveID);
        event.put(MOVE_ENTITY_EVENT_TILE_TO_MOVE_ENTITY_TO_ID, tileToMoveUnitToID);
        event.put(MOVE_ENTITY_EVENT_COMMIT, tryCommitting);
        return event;
    }
    private void handleMoveEntityEvent(JSONObject event) {
        String unitToMoveID = event.optString(MOVE_ENTITY_EVENT_ENTITY_TO_MOVE_ID);
        String tileToMoveUnitToID = event.optString(MOVE_ENTITY_EVENT_TILE_TO_MOVE_ENTITY_TO_ID);
        boolean commit = event.getBoolean(MOVE_ENTITY_EVENT_COMMIT);

        if (unitToMoveID == null || tileToMoveUnitToID == null) { return; }

        Entity unitEntity = EntityStore.getInstance().get(unitToMoveID);
        ActionsComponent actionsComponent = unitEntity.get(ActionsComponent.class);
        if (actionsComponent.hasFinishedMoving()) { return; }

        boolean moved = move(mGameModel, unitToMoveID, tileToMoveUnitToID, commit);
        if (!moved) { return; }

        actionsComponent.setHasFinishedMoving(true);
        mGameModel.getGameState().setAutomaticallyGoToHomeControls(true);
//        mGameModel.focusCamerasAndSelectionsOfActiveEntity();
//        mGameModel.focusCamerasAndSelectionsOfActiveEntity(tileToMoveUnitToID);
//        mLogger.info("{} has moved");

    }

    public void update(GameModel model, SystemContext systemContext) { }

    private boolean move(GameModel model, String unitToMoveID, String ToMoveToTileID, boolean commit) {
        Entity unitEntity = getEntityWithID(unitToMoveID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        int move = statisticsComponent.getTotalMovement();
        int climb = statisticsComponent.getTotalClimb();

        String toMoveFromTileID = movementComponent.getCurrentTileID();
        Entity toMoveFromTileEntity = getEntityWithID(toMoveFromTileID);
        Entity toMoveToTileEntity = getEntityWithID(ToMoveToTileID);

        // This can be flood the console, statelock to prevent flooding, probably not necessary
        boolean shouldUpdateLogger = isUpdated("planning_to_move_logger", toMoveFromTileEntity, toMoveToTileEntity);
        if (shouldUpdateLogger) {
//            mLogger.info("{} is planning to move from {} to {}", unitEntity, toMoveFromTileEntity, toMoveToTileEntity);
        }

        // Only update then the tile to move from has changed, or the units move or climb stat changed
        boolean isUpdated = isUpdated("movement_range", toMoveFromTileID, move, climb);
        if (isUpdated) {
            List<String> range = algorithm.getMovementRange(model, toMoveFromTileID, move);
            movementComponent.stageMovementRange(range);
            mLogger.info("Updated movement range to {} for {}", range.size(), unitEntity);
        }

        // Only update when the tile to move to has changed, or the units move or climb stat changed
        isUpdated = isUpdated("movement_path", ToMoveToTileID, move, climb);
        if (isUpdated) {
            List<String> path = algorithm.getMovementPath(model, toMoveFromTileID, ToMoveToTileID);
            movementComponent.stageMovementPath(path);
            mLogger.info("Updated movement path from {} to {} for {}", toMoveFromTileEntity, toMoveToTileEntity, unitEntity);
        }

        movementComponent.stageTarget(ToMoveToTileID);

        // try executing action only if specified
        // - Target is not null
        // - Target is within range
        // - We are not in preview mode
        // - We are targeting the current tile were one
        if (!commit) { return false; }
        if (toMoveToTileEntity == null) { return false; }
        if (toMoveToTileEntity == toMoveFromTileEntity) { return false; }
        if (!movementComponent.isValidMovementPath()) { return false; }



        removeUnitFromTile(unitToMoveID);
        movementComponent.commit();
        putUnitOnTile(unitToMoveID);


        // do the animation for the tile
        mEventBus.publish(AnimationSystem.PATHING_ANIMATION_EVENT, AnimationSystem.createPathingAnimationEvent(
                unitToMoveID,  movementComponent.getTilesInFinalMovementPath()
        ));


        model.focusCamerasAndSelectionsOfActiveEntity();

//        boolean isLockedOnActivityCamera = mGameState.isLockOnActivityCamera();
//        if (isLockedOnActivityCamera) { model.focusCamerasAndSelectionsOfActiveEntity(); }
//        model.focusCamerasAndSelectionsOfActiveEntity(tileToMoveUnitToID);

//        DirectionComponent directionComponent = unitEntity.get(DirectionComponent.class);
//        directionComponent.setDirection(getDirection(currentTile, tileToMoveTo));


        mLogger.info("Moved from {} to {}", toMoveFromTileEntity, toMoveToTileEntity);
        return true;
    }

    public void removeUnitFromTile(String unitID) {
        Entity unitEntity = getEntityWithID(unitID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity tileEntity = getEntityWithID(movementComponent.getCurrentTileID());
        TileComponent tileComponent = tileEntity.get(TileComponent.class);
        tileComponent.removeUnit();
    }
    public void putUnitOnTile(String unitID) {
        Entity unitEntity = getEntityWithID(unitID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity tileEntity = getEntityWithID(movementComponent.getCurrentTileID());
        TileComponent tileComponent = tileEntity.get(TileComponent.class);
        tileComponent.setUnit(unitID);
    }

    public Direction getDirection(Entity start, Entity end) {
        TileComponent startTile = start.get(TileComponent.class);
        TileComponent endTile = end.get(TileComponent.class);

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
