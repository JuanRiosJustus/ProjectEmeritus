package main.game.systems;

import main.constants.Vector3f;
import main.game.components.MovementComponent;
import main.game.components.AnimationComponent;
import main.game.components.animation.Animation;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.utils.RandomUtils;

import java.util.*;

public class AnimationSystem extends GameSystem {

    public void update(GameModel model, Entity unitEntity) {
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity tileEntity = movementComponent.getCurrentTile();
        Tile tile = tileEntity.get(Tile.class);
        Vector3f vector = tile.getLocalVector(model);
        movementComponent.setPosition((int) vector.x, (int) vector.y);

        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        if (!animationComponent.hasPendingAnimations()) { return; }
        Animation currentAnimation = animationComponent.getCurrentAnimation();

        int spriteHeight = model.getGameState().getSpriteHeight();
        int spriteWidth = model.getGameState().getSpriteWidth();
        float pixelsToTravel = (spriteWidth + spriteHeight) / 2.0f;
        currentAnimation.increaseProgressAuto(pixelsToTravel);

        Vector3f currentPosition = Vector3f.lerp(
                currentAnimation.getCurrentNode(),
                currentAnimation.getNextNode(),
                currentAnimation.getProgressToNextNode()
        );

        movementComponent.setPosition((int) currentPosition.x, (int) currentPosition.y);

//        animationComponent.updatePositionBasedOnLERP(pixelsToTravel);
//        int currentX = animationComponent.getX();
//        int currentY = animationComponent.getY();
//        movementComponent.setPosition(currentX, currentY);

        if (currentAnimation.getProgressToNextNode() >= 1) {
            currentAnimation.setToNextNode();
            currentAnimation.setProgressToNextNode(0);
        }

        if (currentAnimation.isDone()) {
            animationComponent.popAnimation();
        }
    }

    public void executeMoveAnimation(GameModel model, Entity unitEntity, Set<Entity> pathing) {

        Animation newAnimation = new Animation();
        // Add all points from the pathing
        for (Entity pathedEntity : pathing) {
            Tile pathedTile = pathedEntity.get(Tile.class);
            Vector3f tileLocation = pathedTile.getLocalVector(model);
            newAnimation.addPoint(tileLocation);
        }
        // Set an appropriate speed for the movement
        newAnimation.setSpeed(getSpeed(model, 5, 7));

        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        animationComponent.addAnimation(newAnimation, false);

//        mAnimationMap.put(newAnimation, newAnimation);
    }
    public void executeToTargetAndBackAnimation(GameModel model, Entity unitEntity, Entity target, String source, boolean isBlocking) {

        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity tileEntity = movementComponent.getCurrentTile();
        Tile startTile = tileEntity.get(Tile.class);

        Tile targetTile = target.get(Tile.class);

        Vector3f startLocation = startTile.getLocalVector(model);
        Vector3f targetLocation = targetTile.getLocalVector(model);

        Animation newAnimation = new Animation();

        newAnimation.addPoint(startLocation);
        newAnimation.addPoint(targetLocation);
        newAnimation.addPoint(startLocation);

        newAnimation.setSpeed(getSpeed(model, 5, 20));

        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        animationComponent.addAnimation(newAnimation, false);

//        mAnimationMap.put(newAnimation, newAnimation);

//        if (isBlocking) { putBlockingAnimation(source, newAnimation); }
//        mAnimationSourceMap.put(newAnimation, source);
    }

    public void executeGyrateAnimation(GameModel model, Entity unitEntity, String source, boolean isBlocking) {
        // Initialize the track

        // Get the sprite's width and height
        int spriteWidth = model.getGameState().getSpriteWidth();
        int spriteHeight = model.getGameState().getSpriteHeight();

        // Calculate the radius as a proportion of the sprite size
        double radius = Math.min(spriteWidth, spriteHeight) * 0.005; // Adjust 0.25 to set the proportion

        // Get the origin point (center of gyration)
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity tileEntity = movementComponent.getCurrentTile();
        Tile tile = tileEntity.get(Tile.class);
        Vector3f origin = tile.getLocalVector(model);
//        trackComponent.addPoint(origin);

        Animation newAnimation = new Animation();

        // Add points forming a circle around the origin
        for (int angle = 0; angle < 360; angle += 10) {
            double radians = Math.toRadians(angle);

            float x = (float) (origin.x + radius * Math.sin(radians));
            float y = (float) (origin.y + radius * Math.cos(radians));
//            float x = (int) ((float) (origin.x + radius * Math.sin(radians)));
//            float y = (float) (origin.y + radius * Math.cos(radians));
            newAnimation.addPoint(new Vector3f(x, y));
        }

        // Return to the origin
//        trackComponent.addPoint(origin);

        // Set the speed of the animation
        newAnimation.setSpeed(getSpeed(model, 6900, 6950));


        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        animationComponent.addAnimation(newAnimation, false);

//        mAnimationMap.put(newAnimation, newAnimation);

//        if (isBlocking) { putBlockingAnimation(source, newAnimation); }
//        mAnimationSourceMap.put(newAnimation, source);
    }

    public void executeShakeAnimation(GameModel model, Entity unitEntity, String source, boolean isBlocking) {

        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity tileEntity = movementComponent.getCurrentTile();
        Tile tile = tileEntity.get(Tile.class);
        Vector3f origin = tile.getLocalVector(model);

        float shakeOffset = model.getGameState().getSpriteWidth() / 8f;
        Animation newAnimation = new Animation();
        for (int i = 0; i < 8; i++) {
            Vector3f shakePoint = new Vector3f(
                    origin.x + (i % 2 == 0 ? -shakeOffset : shakeOffset),
                    origin.y
            );
            newAnimation.addPoint(shakePoint);
        }

        newAnimation.addPoint(origin);
        newAnimation.setSpeed(getSpeed(model, 15, 25));

        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        animationComponent.addAnimation(newAnimation, false);

//        mAnimationMap.put(newAnimation, newAnimation);

//        if (isBlocking) { putBlockingAnimation(source, newAnimation); }
//        mAnimationSourceMap.put(newAnimation, source);
    }

    private int getSpeed(GameModel model, int minSpeed, int maxSpeed) {
        float spriteSize = (model.getGameState().getSpriteWidth() + model.getGameState().getSpriteHeight()) / 2f;
        return (int) (spriteSize * RandomUtils.getRandomNumberBetween(minSpeed, maxSpeed));
    }
}