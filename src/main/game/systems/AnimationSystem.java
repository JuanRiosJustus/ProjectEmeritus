package main.game.systems;

import main.constants.Vector3f;
import main.game.components.AnimationComponent;
import main.game.components.MovementComponent;
import main.game.components.PositionComponent;
import main.game.components.animation.AnimationTrack;
import main.game.components.tile.TileComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.EntityStore;
import main.game.systems.combat.CombatSystem;
import main.utils.RandomUtils;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.*;

public class AnimationSystem extends GameSystem {
    public static final String EXECUTE_ANIMATION_EVENT_GYRATE_ANIMATION = "gyrate";
    public static final String EXECUTE_ANIMATION_EVENT_TO_TARGET_AND_BACK_ANIMATION = "to_target_and_back";
    public static final String EXECUTE_ANIMATION_EVENT_SHAKE_ANIMATION = "shake";
    public static final String EXECUTE_ANIMATION_EVENT_WALK_ANIMATION = "walk";
    private static final String UNIT_ID = "unit_to_apply_animation_to_id";
    private static final String ANIMATION_NAME = "animation_name";
    public static final String PATHING_ANIMATION_EVENT = "pathing_animation_event";
    private static final String UNIT_WITH_ANIMATION_TO_WAIT_ON = "unit_with_animation_to_wait_on_id";
    private static final String TILE_IDS = "tile_ids";
//    private static final String
    private Map<String, Entity> mMap = new HashMap<>();

//    public AnimationSystem() { }
    public AnimationSystem(GameModel gameModel) {
        super(gameModel);

        mEventBus.subscribe(ANIMATION_EVENT, this::handleAnimationEvent);
        mEventBus.subscribe(PATHING_ANIMATION_EVENT, this::handlePathingAnimationEvent);
        mEventBus.subscribe(DAMAGE_TAKEN_ANIMATION_EVENT, this::handleDamageTakenAnimationEvent);
    }


    public static JSONObject createAnimationEvent(String unitID, String animation, List<String> tileIDs) {
        return createAnimationEvent(unitID, animation, tileIDs, null);
    }

    public static JSONObject createAnimationEvent(String unitID, String animation, List<String> tileIDs, String blockingUnitID) {
        JSONObject result = new JSONObject();
        result.put("event", ANIMATION_EVENT);
        result.put(UNIT_ID, unitID);
        result.put(ANIMATION_NAME, animation);
        JSONArray pathing = new JSONArray();
        result.put(TILE_IDS, pathing);
        if (tileIDs != null) { pathing.addAll(tileIDs); }
        if (blockingUnitID != null) { result.put(UNIT_WITH_ANIMATION_TO_WAIT_ON, blockingUnitID); }
        return result;
    }


    public static final String ANIMATION_EVENT = "execute_apply_animation_event";
    private void handleAnimationEvent(JSONObject event) {
        String unitToApplyAnimationToID = event.getString(UNIT_ID);
        String animationName = event.getString(ANIMATION_NAME);
        JSONArray tile_ids = event.getJSONArray(TILE_IDS);
        String unitWithAnimationToWaitOnID = event.getString(UNIT_WITH_ANIMATION_TO_WAIT_ON);

        if (unitToApplyAnimationToID == null) {
            return;
        }

        AnimationTrack track = null;
         switch (animationName) {
            case EXECUTE_ANIMATION_EVENT_TO_TARGET_AND_BACK_ANIMATION -> track =
                    executeToTargetAndBackAnimation(mGameModel, unitToApplyAnimationToID, tile_ids.getString(0));
            case EXECUTE_ANIMATION_EVENT_GYRATE_ANIMATION -> track =
                    executeGyrateAnimation(mGameModel, unitToApplyAnimationToID);
            case EXECUTE_ANIMATION_EVENT_SHAKE_ANIMATION -> track =
                    executeShakeAnimation(mGameModel, unitToApplyAnimationToID);
            case EXECUTE_ANIMATION_EVENT_WALK_ANIMATION -> track =
                    executeWalkAnimation(mGameModel, unitToApplyAnimationToID, tile_ids);
        }

        if (track == null) { return; }

        Entity unitEntityToApplyAnimationTo = getEntityWithID(unitToApplyAnimationToID);
        AnimationComponent componentToApplyAnimationTo = unitEntityToApplyAnimationTo.get(AnimationComponent.class);
        if (unitWithAnimationToWaitOnID != null) {
            // Trigger an animation for the user
            // Add a listener to notify when the animation completes
            Entity unitEntityWithAnimationToWaitOn = getEntityWithID(unitWithAnimationToWaitOnID);
            AnimationComponent componentToWaitOn = unitEntityWithAnimationToWaitOn.get(AnimationComponent.class);
            AnimationTrack finalTrack = track;
            componentToWaitOn.addOnCompleteListener(() -> {
                componentToApplyAnimationTo.addTrack(finalTrack);
            });
        } else {
            componentToApplyAnimationTo.addTrack(track);
        }
    }



    private static final String ACTOR_ENTITY_ID = "actor_entity_id";
    private static final String ACTED_ON_ENTITY_ID = "acted_on_entity_ids";
    private static final String ABILITY_NAME = "damage_map";
    private static final String ON_FINISHED_WAITING_EVENT = "on_finished_waiting_event";
    private static final String ON_FINISHED_WAITING_EVENT_PAYLOAD = "on_finished_waiting_payload";
    public static final String DAMAGE_TAKEN_ANIMATION_EVENT = "damage_taken_event";
    public static JSONObject createDamageTakenAnimationEvent(String actorID, String ability, String actedOnID) {
        JSONObject result = new JSONObject();
        result.put(ACTOR_ENTITY_ID, actorID);
        result.put(ABILITY_NAME, ability);
        result.put(ACTED_ON_ENTITY_ID, actedOnID);
        return result;
    }
    private void handleDamageTakenAnimationEvent(JSONObject event) {
        String actorEntityID = event.getString(ACTOR_ENTITY_ID);
        String actedOnEntityID = event.getString(ACTED_ON_ENTITY_ID);
        String abilityName = event.getString(ABILITY_NAME);

        // Trigger an animation for the user
        // Add a listener to notify when the animation completes
        Entity actorEntity = getEntityWithID(actorEntityID);
        AnimationComponent actorComponent = actorEntity.get(AnimationComponent.class);
        AnimationTrack track = executeShakeAnimation(mGameModel, actedOnEntityID);
        actorComponent.addOnCompleteListener(() -> {
            Entity actedOnEntity = getEntityWithID(actedOnEntityID);
            if (actedOnEntity == null) { return; }
            AnimationComponent actedOnComponent = actedOnEntity.get(AnimationComponent.class);
            actedOnComponent.addTrack(track);

            mEventBus.publish(CombatSystem.createCombatEndEvent(actorEntityID, abilityName, actedOnEntityID));
        });
    }

    private static final String PATHING_EVENT_ENTITY_TO_MOVE_ID = "entity_to_move_id";
    private static final String PATHING_EVENT_TILE_ENTITY_IDS_TO_MOVE_THROUGH = "tile_entity_ids_to_move_through";
    public static JSONObject createPathingAnimationEvent(String unitID, List<String> tileIDs) {
        JSONObject event = new JSONObject();
        event.put("event", PATHING_ANIMATION_EVENT);
        event.put(PATHING_EVENT_ENTITY_TO_MOVE_ID, unitID);
        JSONArray pathing = new JSONArray();
        event.put(PATHING_EVENT_TILE_ENTITY_IDS_TO_MOVE_THROUGH, pathing);
        pathing.addAll(tileIDs);
        return event;
    }
    public void handlePathingAnimationEvent(JSONObject event) {
        String unitToApplyAnimationToID = event.getString(PATHING_EVENT_ENTITY_TO_MOVE_ID);
        JSONArray tileIDs = event.getJSONArray(PATHING_EVENT_TILE_ENTITY_IDS_TO_MOVE_THROUGH);
        if (unitToApplyAnimationToID == null) { return; }
        AnimationTrack track = executeWalkAnimation(mGameModel, unitToApplyAnimationToID, tileIDs);

        Entity unitEntityToApplyAnimationTo = getEntityWithID(unitToApplyAnimationToID);
        AnimationComponent componentToApplyAnimationTo = unitEntityToApplyAnimationTo.get(AnimationComponent.class);
        componentToApplyAnimationTo.addTrack(track);
    }



    public void update(GameModel model, SystemContext systemContext) {
        systemContext.getAllUnitEntityIDs().forEach(unitID -> {
            Entity unitEntity = getEntityWithID(unitID);
            MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
            String tileID = movementComponent.getCurrentTileID();
            Entity tileEntity = getEntityWithID(tileID);
            if (tileEntity == null) { return; }

//            String currentEntitiesturn = model.getGameState().getCurrentEntitiesTurn();
//            if (!unitID.equalsIgnoreCase(currentEntitiesturn)) { return; }

            TileComponent tile = tileEntity.get(TileComponent.class);
            Vector3f vector = tile.getLocalVector(model);
            PositionComponent positionComponent = unitEntity.get(PositionComponent.class);
            positionComponent.setPosition((int) vector.x, (int) vector.y);

            // Mark the current entity being animated to keep track of currently animated entities
            mMap.put(unitID, unitEntity);

            AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
            AnimationTrack currentAnimationTrack = animationComponent.getCurrentAnimation();
            if (currentAnimationTrack == null || currentAnimationTrack.isEmpty()) { return; }

            double deltaTime = model.getGameState().getDeltaTime();
            int spriteHeight = model.getGameState().getSpriteHeight();
            int spriteWidth = model.getGameState().getSpriteWidth();
            float pixelsToTravel = (spriteWidth + spriteHeight) / 2.0f;
            currentAnimationTrack.increaseProgressAuto(pixelsToTravel, deltaTime);

            float progressToNextNode = currentAnimationTrack.getProgressToNextNode();

            Vector3f currentPosition = Vector3f.lerp(
                    currentAnimationTrack.getCurrentNode(),
                    currentAnimationTrack.getNextNode(),
                    currentAnimationTrack.getProgressToNextNode()
            );

            positionComponent.setPosition((int) currentPosition.x, (int) currentPosition.y);

            if (currentAnimationTrack.getProgressToNextNode() >= 1) {
                currentAnimationTrack.setToNextNode();
                currentAnimationTrack.setProgressToNextNode(0);
            }

            if (currentAnimationTrack.isComplete()) {
                animationComponent.completeTrack();
            }

            if (!animationComponent.hasPendingAnimations()) {
                mMap.remove(unitID);
            }
        });
    }

    public AnimationTrack executeWalkAnimation(GameModel model, String unitEntityID, JSONArray pathing) {

        AnimationTrack newAnimationTrack = new AnimationTrack();
        // Add all points from the pathing
        for (int i = 0; i < pathing.size(); i++) {
            String tileEntityID = pathing.getString(i);
            Entity tileEntity = EntityStore.getInstance().get(tileEntityID);
            TileComponent pathedTile = tileEntity.get(TileComponent.class);
            Vector3f tileLocation = pathedTile.getLocalVector(mGameModel);
            newAnimationTrack.addPoint(tileLocation);
        }

        // Set an appropriate speed for the movement
        newAnimationTrack.setSpeed(getSpeed(mGameModel, 3, 4));
        newAnimationTrack.setDurationInSeconds(2);

//        Entity unitEntity = EntityStore.getInstance().get(unitID);
//        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
//        animationComponent.addTrack(newAnimationTrack);
//        animationComponent.addOnCompleteListener(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("Finished animation!");
//            }
//        });

        return newAnimationTrack;
    }

    public AnimationTrack executeToTargetAndBackAnimation(GameModel model, String unitEntityID, String targetTileEntityID) {

        Entity unitEntity = getEntityWithID(unitEntityID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        String currentTileID = movementComponent.getCurrentTileID();
        Entity tileEntity = getEntityWithID(currentTileID);
        TileComponent startTile = tileEntity.get(TileComponent.class);

        Entity targetTileEntity = getEntityWithID(targetTileEntityID);
        TileComponent targetTile = targetTileEntity.get(TileComponent.class);

        Vector3f startLocation = startTile.getLocalVector(model);
        Vector3f targetLocation = targetTile.getLocalVector(model);

        AnimationTrack newAnimationTrack = new AnimationTrack();

        newAnimationTrack.addPoint(startLocation);
        newAnimationTrack.addPoint(targetLocation);
        newAnimationTrack.addPoint(startLocation);

        newAnimationTrack.setSpeed(getSpeed(model, 5, 20));
        newAnimationTrack.setDurationInSeconds(2);

        return newAnimationTrack;
    }


    public AnimationTrack executeGyrateAnimation(GameModel model, String unitEntityID) {
        // Initialize the track
        Entity unitEntity = getEntityWithID(unitEntityID);

        // Get the sprite's width and height
        int spriteWidth = model.getGameState().getSpriteWidth();
        int spriteHeight = model.getGameState().getSpriteHeight();

        // Calculate the radius as a proportion of the sprite size
        double radius = Math.min(spriteWidth, spriteHeight) * 0.005; // Adjust 0.25 to set the proportion

        // Get the origin point (center of gyration)
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        String currentTileID = movementComponent.getCurrentTileID();
        Entity tileEntity = getEntityWithID(currentTileID);
        TileComponent tile = tileEntity.get(TileComponent.class);
        Vector3f origin = tile.getLocalVector(model);
//        trackComponent.addPoint(origin);

        AnimationTrack newAnimationTrack = new AnimationTrack();

        // Add points forming a circle around the origin
        for (int angle = 0; angle < 360; angle += 10) {
            double radians = Math.toRadians(angle);

            float x = (float) (origin.x + radius * Math.sin(radians));
            float y = (float) (origin.y + radius * Math.cos(radians));
//            float x = (int) ((float) (origin.x + radius * Math.sin(radians)));
//            float y = (float) (origin.y + radius * Math.cos(radians));
            newAnimationTrack.addPoint(new Vector3f(x, y));
        }

        // Return to the origin

        // Set the speed of the animation
        newAnimationTrack.setSpeed(getSpeed(model, 6900, 6950));
        newAnimationTrack.setDurationInSeconds(2);

        return newAnimationTrack;
    }

    public static AnimationTrack executeShakeAnimation(GameModel model, String unitEntityID) {
        Entity unitEntity = getEntityWithID(unitEntityID);
        if (unitEntity == null) { return null; }
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        String currentTileID = movementComponent.getCurrentTileID();
        Entity tileEntity = getEntityWithID(currentTileID);
        TileComponent tile = tileEntity.get(TileComponent.class);
        Vector3f origin = tile.getLocalVector(model);

        float shakeOffset = model.getGameState().getSpriteWidth() / 8f;
        AnimationTrack newAnimationTrack = new AnimationTrack();
        for (int i = 0; i < 8; i++) {
            Vector3f shakePoint = new Vector3f(
                    origin.x + (i % 2 == 0 ? -shakeOffset : shakeOffset),
                    origin.y
            );
            newAnimationTrack.addPoint(shakePoint);
        }

        newAnimationTrack.addPoint(origin);
        newAnimationTrack.setSpeed(getSpeed(model, 15, 25));

        return newAnimationTrack;
    }

    private static int getSpeed(GameModel model, int minSpeed, int maxSpeed) {
        float spriteSize = (model.getGameState().getSpriteWidth() + model.getGameState().getSpriteHeight()) / 2f;
        return (int) (spriteSize * RandomUtils.getRandomNumberBetween(minSpeed, maxSpeed));
    }

    public boolean hasPendingAnimations() { return !mMap.isEmpty(); }
}
