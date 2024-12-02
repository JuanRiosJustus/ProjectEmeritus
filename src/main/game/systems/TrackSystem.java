package main.game.systems;

import main.engine.Engine;
import main.constants.Vector3f;
import main.game.components.MovementComponent;
import main.game.components.TrackComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.utils.RandomUtils;

import java.awt.Point;
import java.util.Deque;




public class TrackSystem extends GameSystem {

    public void update(GameModel model, Entity unit) {
        TrackComponent trackComponent = unit.get(TrackComponent.class);
        MovementComponent movementComponent = unit.get(MovementComponent.class);
        Entity tileEntity = movementComponent.getCurrentTile();
        Tile tile = tileEntity.get(Tile.class);
        Point p = tile.getLocalVectorV2(model);
        movementComponent.setPosition(p.x, p.y);

        if (trackComponent.isEmpty()) return;

        int spriteHeight = model.getGameState().getSpriteHeight();
        int spriteWidth = model.getGameState().getSpriteWidth();
        double pixelsBetween = (spriteWidth + spriteHeight) / 2.0;

        double pixelsTraveled = Engine.getInstance().getDeltaTime() * trackComponent.getSpeed();
        float progressIncrease = (float) (pixelsTraveled / pixelsBetween);
        trackComponent.increaseProgress(progressIncrease);

        int currentIndex = trackComponent.getIndex();
        Vector3f currentPosition = Vector3f.lerp(
                trackComponent.getVectorAt(currentIndex),
                trackComponent.getVectorAt(currentIndex + 1),
                trackComponent.getProgress()
        );

        movementComponent.setPosition((int) currentPosition.x, (int) currentPosition.y);
        trackComponent.setPosition((int) currentPosition.x, (int) currentPosition.y);

        if (trackComponent.getProgress() >= 1) {
            trackComponent.incrementIndex();
            trackComponent.setProgress(0);
        }

        if (trackComponent.isDone()) {
            trackComponent.clear();
        }
    }

    public void executeMoveAnimation(GameModel model, Entity unitEntity, Deque<Entity> pathing) {
        // Initialize the track
        TrackComponent trackComponent = initializeTrack(model, unitEntity);

        // Add all points from the pathing
        for (Entity pathedEntity : pathing) {
            Tile pathedTile = pathedEntity.get(Tile.class);
            Vector3f tileLocation = pathedTile.getLocalVector(model);
            trackComponent.addPoint(tileLocation);
        }

        // Set an appropriate speed for the movement
        trackComponent.setSpeed(getSpeed(model, 5, 7));
    }
    public void executeToTargetAndBackAnimation(GameModel model, Entity unit, Entity target) {
        TrackComponent trackComponent = initializeTrack(model, unit);

        Tile startTile = unit.get(MovementComponent.class).getCurrentTile().get(Tile.class);
        Tile targetTile = target.get(Tile.class);

        Vector3f startLocation = startTile.getLocalVector(model);
        Vector3f targetLocation = targetTile.getLocalVector(model);

        trackComponent.addPoint(startLocation);
        trackComponent.addPoint(targetLocation);
        trackComponent.addPoint(startLocation);

        trackComponent.setSpeed(getSpeed(model, 5, 20));
    }

    public void executeGyrateAnimation(GameModel model, Entity unit) {
        // Initialize the track
        TrackComponent trackComponent = initializeTrack(model, unit);

        // Get the sprite's width and height
        int spriteWidth = model.getGameState().getSpriteWidth();
        int spriteHeight = model.getGameState().getSpriteHeight();

        // Calculate the radius as a proportion of the sprite size
        double radius = Math.min(spriteWidth, spriteHeight) * 0.005; // Adjust 0.25 to set the proportion

        // Get the origin point (center of gyration)
        Vector3f origin = unit.get(MovementComponent.class).getCurrentTile().get(Tile.class).getLocalVector(model);
//        trackComponent.addPoint(origin);

        // Add points forming a circle around the origin
        for (int angle = 0; angle < 360; angle += 10) {
            double radians = Math.toRadians(angle);
            float x = (float) (origin.x + radius * Math.sin(radians));
            float y = (float) (origin.y + radius * Math.cos(radians));
            trackComponent.addPoint(new Vector3f(x, y));
        }

        // Return to the origin
//        trackComponent.addPoint(origin);

        // Set the speed of the animation
        trackComponent.setSpeed(getSpeed(model, 6900, 6950));
    }

    public void executeShakeAnimation(GameModel model, Entity unit) {
        TrackComponent trackComponent = initializeTrack(model, unit);

        Vector3f origin = unit.get(MovementComponent.class).getCurrentTile().get(Tile.class).getLocalVector(model);
        float shakeOffset = model.getGameState().getSpriteWidth() / 8f;

        for (int i = 0; i < 8; i++) {
            Vector3f shakePoint = new Vector3f(
                    origin.x + (i % 2 == 0 ? -shakeOffset : shakeOffset),
                    origin.y
            );
            trackComponent.addPoint(shakePoint);
        }

        trackComponent.addPoint(origin);
        trackComponent.setSpeed(getSpeed(model, 15, 25));
    }

    private TrackComponent initializeTrack(GameModel model, Entity unit) {
        TrackComponent trackComponent = unit.get(TrackComponent.class);
        trackComponent.clear();

        Vector3f startLocation = unit.get(MovementComponent.class).getCurrentTile()
                .get(Tile.class).getLocalVector(model);
        trackComponent.setPosition((int) startLocation.x, (int) startLocation.y);
        return trackComponent;
    }

    private int getSpeed(GameModel model, int minSpeed, int maxSpeed) {
        float spriteSize = (model.getGameState().getSpriteWidth() + model.getGameState().getSpriteHeight()) / 2f;
        return (int) (spriteSize * RandomUtils.getRandomNumberBetween(minSpeed, maxSpeed));
    }
}
