package main.game.systems;

import main.constants.Vector3f;
import main.game.components.MovementComponent;
import main.game.components.AnimationComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.utils.RandomUtils;

import java.util.Deque;

public class AnimationSystem extends GameSystem {

    public void update(GameModel model, Entity unit) {
        AnimationComponent animationComponent = unit.get(AnimationComponent.class);
        MovementComponent movementComponent = unit.get(MovementComponent.class);
        Entity tileEntity = movementComponent.getCurrentTile();
        Tile tile = tileEntity.get(Tile.class);
        Vector3f vector = tile.getLocalVector(model);
        movementComponent.setPosition((int) vector.x, (int) vector.y);

        if (animationComponent.isEmpty()) return;

        int spriteHeight = model.getGameState().getSpriteHeight();
        int spriteWidth = model.getGameState().getSpriteWidth();
        float pixelsToTravel = (spriteWidth + spriteHeight) / 2.0f;
        animationComponent.increaseProgressAuto(pixelsToTravel);

        Vector3f currentPosition = Vector3f.lerp(
                animationComponent.getCurrentNode(),
                animationComponent.getNextNode(),
                animationComponent.getProgress()
        );

        movementComponent.setPosition((int) currentPosition.x, (int) currentPosition.y);

//        animationComponent.updatePositionBasedOnLERP(pixelsToTravel);
//        int currentX = animationComponent.getX();
//        int currentY = animationComponent.getY();
//        movementComponent.setPosition(currentX, currentY);


        if (animationComponent.getProgress() >= 1) {
            animationComponent.incrementIndex();
            animationComponent.setProgress(0);
        }

        if (animationComponent.isDone()) {
            animationComponent.clear();
        }
    }

    public void executeMoveAnimation(GameModel model, Entity unitEntity, Deque<Entity> pathing) {
        // Initialize the track
        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        animationComponent.clear();

        // Add all points from the pathing
        for (Entity pathedEntity : pathing) {
            Tile pathedTile = pathedEntity.get(Tile.class);
            Vector3f tileLocation = pathedTile.getLocalVector(model);
            animationComponent.addPoint(tileLocation);
        }

        // Set an appropriate speed for the movement
        animationComponent.setSpeed(getSpeed(model, 5, 7));
    }
    public void executeToTargetAndBackAnimation(GameModel model, Entity unitEntity, Entity target) {
        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        animationComponent.clear();

        Tile startTile = unitEntity.get(MovementComponent.class).getCurrentTile().get(Tile.class);
        Tile targetTile = target.get(Tile.class);

        Vector3f startLocation = startTile.getLocalVector(model);
        Vector3f targetLocation = targetTile.getLocalVector(model);

        animationComponent.addPoint(startLocation);
        animationComponent.addPoint(targetLocation);
        animationComponent.addPoint(startLocation);

        animationComponent.setSpeed(getSpeed(model, 5, 20));
    }

    public void executeGyrateAnimation(GameModel model, Entity unitEntity) {
        // Initialize the track
        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        animationComponent.clear();

        // Get the sprite's width and height
        int spriteWidth = model.getGameState().getSpriteWidth();
        int spriteHeight = model.getGameState().getSpriteHeight();

        // Calculate the radius as a proportion of the sprite size
        double radius = Math.min(spriteWidth, spriteHeight) * 0.005; // Adjust 0.25 to set the proportion

        // Get the origin point (center of gyration)
        Vector3f origin = unitEntity.get(MovementComponent.class).getCurrentTile().get(Tile.class).getLocalVector(model);
//        trackComponent.addPoint(origin);

        // Add points forming a circle around the origin
        for (int angle = 0; angle < 360; angle += 10) {
            double radians = Math.toRadians(angle);

            float x = (float) (origin.x + radius * Math.sin(radians));
            float y = (float) (origin.y + radius * Math.cos(radians));
//            float x = (int) ((float) (origin.x + radius * Math.sin(radians)));
//            float y = (float) (origin.y + radius * Math.cos(radians));
            animationComponent.addPoint(new Vector3f(x, y));
        }

        // Return to the origin
//        trackComponent.addPoint(origin);

        // Set the speed of the animation
        animationComponent.setSpeed(getSpeed(model, 6900, 6950));
    }

    public void executeShakeAnimation(GameModel model, Entity unitEntity) {
        AnimationComponent animationComponent = unitEntity.get(AnimationComponent.class);
        animationComponent.clear();

        Vector3f origin = unitEntity.get(MovementComponent.class).getCurrentTile().get(Tile.class).getLocalVector(model);
        float shakeOffset = model.getGameState().getSpriteWidth() / 8f;

        for (int i = 0; i < 8; i++) {
            Vector3f shakePoint = new Vector3f(
                    origin.x + (i % 2 == 0 ? -shakeOffset : shakeOffset),
                    origin.y
            );
            animationComponent.addPoint(shakePoint);
        }

        animationComponent.addPoint(origin);
        animationComponent.setSpeed(getSpeed(model, 15, 25));
    }

    private int getSpeed(GameModel model, int minSpeed, int maxSpeed) {
        float spriteSize = (model.getGameState().getSpriteWidth() + model.getGameState().getSpriteHeight()) / 2f;
        return (int) (spriteSize * RandomUtils.getRandomNumberBetween(minSpeed, maxSpeed));
    }
}
