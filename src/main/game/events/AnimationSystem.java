package main.game.events;

import main.constants.Vector3f;
import main.game.components.AnimationComponent;
import main.game.components.MovementComponent;
import main.game.components.animation.AnimationTrack;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;
import main.game.systems.GameSystem;
import main.utils.RandomUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class AnimationSystem extends GameSystem {
    public static final String GYRATE_ANIMATION = "gyrate";
    public static final String TO_TARGET_AND_BACK_ANIMATION = "to_target_and_back";
    public static final String SHAKE_ANIMATION = "shake";
    public static final String WALK_ANIMATION = "walk";

    public static final String EXECUTE_ANIMATION_EVENT = "execute_apply_animation_event";
    private Map<String, Entity> mMap = new HashMap<>();

    public AnimationSystem() { }
    public AnimationSystem(GameModel gameModel) {
        super(gameModel);

        mEventBus.subscribe(EXECUTE_ANIMATION_EVENT, this::onExecuteAnimationEvent);
    }

    public static JSONObject createExecuteAnimationEvent(String unitID, String animation, String tileIDs) {
        return createExecuteAnimationEvent(unitID, animation, List.of(tileIDs));
    }

    public static JSONObject createExecuteAnimationEvent(String unitID, String animation, List<String> tileIDs) {
        JSONObject result = new JSONObject();
        result.put("unit_id_to_apply_animation_to", unitID);
        result.put("animation_name", animation);
        if (tileIDs != null) {
            JSONArray pathing = new JSONArray();
            for (String tileID : tileIDs) {
                pathing.put(tileID);
            }
            result.put("tile_ids", pathing);
        }
        return result;
    }

    private void onExecuteAnimationEvent(JSONObject event) {
        String unitToApplyAnimationToID = event.getString("unit_id_to_apply_animation_to");
        String animationName = event.getString("animation_name");
        JSONArray tile_ids = event.optJSONArray("tile_ids", null);

        AnimationTrack track = null;
         switch (animationName) {
            case TO_TARGET_AND_BACK_ANIMATION -> track = executeToTargetAndBackAnimation(mGameModel, unitToApplyAnimationToID, tile_ids.getString(0));
            case GYRATE_ANIMATION -> track = executeGyrateAnimation(mGameModel, unitToApplyAnimationToID);
            case SHAKE_ANIMATION -> track = executeShakeAnimation(mGameModel, unitToApplyAnimationToID);
            case WALK_ANIMATION -> track = executeWalkAnimation(mGameModel, unitToApplyAnimationToID, tile_ids);
        }

        if (track == null) { return; }
        Entity unitEntity = getEntityWithID(unitToApplyAnimationToID);
        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        animationComponent.addTrack(track);
    }

    public void update(GameModel model, String ignored) {
//    public void update(GameModel model, String unitID) {
//        Entity unitEntity = getEntityWithID(unitID);
//        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//        String tileID = movementComponent.getCurrentTileID();
//        Entity tileEntity = getEntityWithID(tileID);
//        if (tileEntity == null) { return; }
//        Tile tile = tileEntity.get(Tile.class);
//        Vector3f vector = tile.getLocalVector(model);
//
//        movementComponent.setPosition((int) vector.x, (int) vector.y);
//
//        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
//        if (!animationComponent.hasPendingAnimations()) { return; }
//        // Mark the current entity being animated to keep track of curerntly animated entities
//        mMap.put(unitID, unitEntity);
//
//        AnimationTrack currentAnimationTrack = animationComponent.getCurrentAnimation();
//
//        double deltaTime = model.getGameState().getDeltaTime();
//        int spriteHeight = model.getGameState().getSpriteHeight();
//        int spriteWidth = model.getGameState().getSpriteWidth();
//        float pixelsToTravel = (spriteWidth + spriteHeight) / 2.0f;
//        currentAnimationTrack.increaseProgressAuto(pixelsToTravel, deltaTime);
//
//        Vector3f currentPosition = Vector3f.lerp(
//                currentAnimationTrack.getCurrentNode(),
//                currentAnimationTrack.getNextNode(),
//                currentAnimationTrack.getProgressToNextNode()
//        );
//
//        movementComponent.setPosition((int) currentPosition.x, (int) currentPosition.y);
//
//        if (currentAnimationTrack.getProgressToNextNode() >= 1) {
//            currentAnimationTrack.setToNextNode();
//            currentAnimationTrack.setProgressToNextNode(0);
//        }
//
//        if (currentAnimationTrack.isDone()) {
//            animationComponent.popAnimation();
//        }
//
//        if (!animationComponent.hasPendingAnimations()) {
//            mMap.remove(unitID);
//        }


        List<String> unitIDs = model.getSpeedQueue().getAllUnitIDs();

        for (String unitID : unitIDs) {
            Entity unitEntity = getEntityWithID(unitID);
            MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
            String tileID = movementComponent.getCurrentTileID();
            Entity tileEntity = getEntityWithID(tileID);
            if (tileEntity == null) {
                return;
            }
            Tile tile = tileEntity.get(Tile.class);
            Vector3f vector = tile.getLocalVector(model);

            movementComponent.setPosition((int) vector.x, (int) vector.y);

            AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
            if (!animationComponent.hasPendingAnimations()) {
//                return;
            }
            // Mark the current entity being animated to keep track of curerntly animated entities
            mMap.put(unitID, unitEntity);

            AnimationTrack currentAnimationTrack = animationComponent.getCurrentAnimation();
            if (currentAnimationTrack == null || currentAnimationTrack.isEmpty()) { continue; }

            double deltaTime = model.getGameState().getDeltaTime();
            int spriteHeight = model.getGameState().getSpriteHeight();
            int spriteWidth = model.getGameState().getSpriteWidth();
            float pixelsToTravel = (spriteWidth + spriteHeight) / 2.0f;
            currentAnimationTrack.increaseProgressAuto(pixelsToTravel, deltaTime);

            Vector3f currentPosition = Vector3f.lerp(
                    currentAnimationTrack.getCurrentNode(),
                    currentAnimationTrack.getNextNode(),
                    currentAnimationTrack.getProgressToNextNode()
            );

            movementComponent.setPosition((int) currentPosition.x, (int) currentPosition.y);

            if (currentAnimationTrack.getProgressToNextNode() >= 1) {
                currentAnimationTrack.setToNextNode();
                currentAnimationTrack.setProgressToNextNode(0);
            }

            if (currentAnimationTrack.isDone()) {
                animationComponent.popAnimation();
            }

            if (!animationComponent.hasPendingAnimations()) {
                mMap.remove(unitID);
            }
        }
    }

    public AnimationTrack executeWalkAnimation(GameModel model, String unitEntityID, JSONArray pathing) {

        AnimationTrack newAnimationTrack = new AnimationTrack();
        // Add all points from the pathing
        for (int i = 0; i < pathing.length(); i++) {
            String tileEntityID = pathing.getString(i);
            Entity tileEntity = EntityStore.getInstance().get(tileEntityID);
            Tile pathedTile = tileEntity.get(Tile.class);
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
        Tile startTile = tileEntity.get(Tile.class);

        Entity targetTileEntity = getEntityWithID(targetTileEntityID);
        Tile targetTile = targetTileEntity.get(Tile.class);

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
        Tile tile = tileEntity.get(Tile.class);
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

    public AnimationTrack executeShakeAnimation(GameModel model, String unitEntityID) {

        Entity unitEntity = getEntityWithID(unitEntityID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        String currentTileID = movementComponent.getCurrentTileID();
        Entity tileEntity = getEntityWithID(currentTileID);
        Tile tile = tileEntity.get(Tile.class);
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

    private int getSpeed(GameModel model, int minSpeed, int maxSpeed) {
        float spriteSize = (model.getGameState().getSpriteWidth() + model.getGameState().getSpriteHeight()) / 2f;
        return (int) (spriteSize * RandomUtils.getRandomNumberBetween(minSpeed, maxSpeed));
    }

    public boolean hasPendingAnimations() { return !mMap.isEmpty(); }
}
