package main.game.systems;

import main.constants.Vector3f;
import main.game.components.AnimationComponent;
import main.game.components.MovementComponent;
import main.game.components.animation.AnimationTrack;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;
import main.utils.RandomUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AnimationSystem extends GameSystem {
    private static final String ACTION_SYSTEM = "ACTION_SYSTEM";
    public static final String GYRATE = "gyrate";
    public static final String TO_TARGET_AND_BACK = "to_target_and_back";
    public static final String SHAKE = "shake";

    private Map<String, Entity> mMap = new HashMap<>();
    private boolean mIsCurrentAnimating = false;

    public void update(GameModel model, String unitID) {
        Entity unitEntity = getEntityWithID(unitID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        String tileID = movementComponent.getCurrentTileID();
        Entity tileEntity = getEntityWithID(tileID);
        if (tileEntity == null) { return; }
        Tile tile = tileEntity.get(Tile.class);
        Vector3f vector = tile.getLocalVector(model);

        movementComponent.setPosition((int) vector.x, (int) vector.y);

        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        if (!animationComponent.hasPendingAnimations()) { return; }
        // Mark the current entity being animated to keep track of curerntly animated entities
        mMap.put(unitID, unitEntity);

        AnimationTrack currentAnimationTrack = animationComponent.getCurrentAnimation();

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

    public void executeMoveAnimation(GameModel model, Entity unitEntity, Set<Entity> pathing) {
        AnimationTrack newAnimationTrack = new AnimationTrack();
        // Add all points from the pathing
        for (Entity pathedEntity : pathing) {
            Tile pathedTile = pathedEntity.get(Tile.class);
            Vector3f tileLocation = pathedTile.getLocalVector(model);
            newAnimationTrack.addPoint(tileLocation);
        }
        // Set an appropriate speed for the movement
        newAnimationTrack.setSpeed(getSpeed(model, 3, 4));
        newAnimationTrack.setDurationInSeconds(2);

        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        animationComponent.addAnimation(newAnimationTrack);
        animationComponent.addOnCompleteListener(new Runnable() {
            @Override
            public void run() {
                System.out.println("Finished animation!");
            }
        });

//        mAnimationMap.put(newAnimation, newAnimation);
    }
    public AnimationTrack executeToTargetAndBackAnimation(GameModel model, Entity unitEntity, Entity target) {

        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        String currentTileID = movementComponent.getCurrentTileID();
        Entity tileEntity = EntityStore.getInstance().get(currentTileID);
        Tile startTile = tileEntity.get(Tile.class);

        Tile targetTile = target.get(Tile.class);

        Vector3f startLocation = startTile.getLocalVector(model);
        Vector3f targetLocation = targetTile.getLocalVector(model);

        AnimationTrack newAnimationTrack = new AnimationTrack();

        newAnimationTrack.addPoint(startLocation);
        newAnimationTrack.addPoint(targetLocation);
        newAnimationTrack.addPoint(startLocation);

        newAnimationTrack.setSpeed(getSpeed(model, 5, 20));
        newAnimationTrack.setDurationInSeconds(2);

        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        animationComponent.addAnimation(newAnimationTrack);

//        mAnimationMap.put(newAnimation, newAnimation);

//        if (isBlocking) { putBlockingAnimation(source, newAnimation); }
//        mAnimationSourceMap.put(newAnimation, source);

        return newAnimationTrack;
    }

    public AnimationTrack executeGyrateAnimation(GameModel model, Entity unitEntity) {
        // Initialize the track

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
//        trackComponent.addPoint(origin);

        // Set the speed of the animation
        newAnimationTrack.setSpeed(getSpeed(model, 6900, 6950));
        newAnimationTrack.setDurationInSeconds(2);


        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        animationComponent.addAnimation(newAnimationTrack);

//        mAnimationMap.put(newAnimation, newAnimation);

//        if (isBlocking) { putBlockingAnimation(source, newAnimation); }
//        mAnimationSourceMap.put(newAnimation, source);

        return newAnimationTrack;
    }

    public AnimationTrack executeShakeAnimation(GameModel model, Entity unitEntity) {

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

        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        animationComponent.addAnimation(newAnimationTrack);

//        mAnimationMap.put(newAnimation, newAnimation);

//        if (isBlocking) { putBlockingAnimation(source, newAnimation); }
//        mAnimationSourceMap.put(newAnimation, source);
        return newAnimationTrack;
    }

    public AnimationTrack applyAnimation(GameModel model, Entity unitEntity, String animation, Entity target) {
        if (unitEntity == null) { return null; }
        AnimationTrack appliedanimation = null;
        switch (animation) {
            case TO_TARGET_AND_BACK -> appliedanimation = executeToTargetAndBackAnimation(model, unitEntity, target);
            case GYRATE -> appliedanimation = executeGyrateAnimation(model, unitEntity);
            case SHAKE -> appliedanimation = executeShakeAnimation(model, unitEntity);
        }
        return appliedanimation;
    }

    private int getSpeed(GameModel model, int minSpeed, int maxSpeed) {
        float spriteSize = (model.getGameState().getSpriteWidth() + model.getGameState().getSpriteHeight()) / 2f;
        return (int) (spriteSize * RandomUtils.getRandomNumberBetween(minSpeed, maxSpeed));
    }

    public boolean hasPendingAnimations() { return !mMap.isEmpty(); }
}
