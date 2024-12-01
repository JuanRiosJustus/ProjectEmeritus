package main.game.systems;

import main.engine.Engine;
import main.constants.Vector3f;
import main.game.components.MovementComponent;
import main.game.components.TrackComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.utils.RandomUtils;

import java.util.Deque;

public class TrackSystem extends GameSystem {

    public void update(GameModel model, Entity unit) {
        TrackComponent trackComponent = unit.get(TrackComponent.class);
        MovementComponent movementComponent = unit.get(MovementComponent.class);
        Entity tileEntity = movementComponent.getCurrentTile();
        Tile tile = tileEntity.get(Tile.class);
        movementComponent.setPosition((int) tile.getLocalVector(model).x, (int) tile.getLocalVector(model).y);

        if (trackComponent.isEmpty()) { return; }

        int configuredSpriteHeight = model.getGameState().getSpriteHeight();
        int configuredSpriteWidth = model.getGameState().getSpriteWidth();

        double pixelsBetweenStartPositionAndEndPosition = (double) (configuredSpriteWidth + configuredSpriteHeight) / 2;

        double pixelsTraveledThisTick = Engine.getInstance().getDeltaTime() * trackComponent.getSpeed();
        float increasedProgress = (float) (pixelsTraveledThisTick / pixelsBetweenStartPositionAndEndPosition);
        trackComponent.increaseProgress(increasedProgress);

        int currentIndex = trackComponent.getIndex();
        Vector3f result = Vector3f.lerp(
                trackComponent.getVectorAt(currentIndex),
                trackComponent.getVectorAt(currentIndex + 1),
                trackComponent.getProgress()
        );

        trackComponent.setPosition((int) result.x, (int) result.y);
        movementComponent.setPosition((int) result.x, (int) result.y);

        if (trackComponent.getProgress() >= 1) {
            trackComponent.incrementIndex();
            currentIndex = trackComponent.getIndex();
            Vector3f next = trackComponent.getVectorAt(currentIndex);
            trackComponent.setPosition((int) next.x, (int) next.y);
            movementComponent.setPosition((int) result.x, (int) result.y);
            trackComponent.setProgress(0);
        }

        if (currentIndex == trackComponent.getTrackMarkers() - 1) {
            trackComponent.clear();
        }
    }

    public void executeToTargetAndBackAnimation(GameModel model, Entity unitEntity, Entity endingTileEntity) {
        // clear used data
        TrackComponent trackComponent = unitEntity.get(TrackComponent.class);
        trackComponent.clear();
        // set the starting location
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity startingTileEntity = movementComponent.getCurrentTile();
        Tile startingTile = startingTileEntity.get(Tile.class);
        Vector3f startingTileLocation = startingTile.getLocalVector(model);
        trackComponent.setPosition((int) startingTileLocation.x, (int) startingTileLocation.y);

        Tile endindTile = endingTileEntity.get(Tile.class);
        Vector3f endingTileLocation = endindTile.getLocalVector(model);

        trackComponent.addPoint(startingTileLocation);
        trackComponent.addPoint(endingTileLocation);
        trackComponent.addPoint(startingTileLocation);

        int speed = getSpeed(model, 5, 20);
        trackComponent.setSpeed(speed);
    }
    public void executeGyrateAnimation(GameModel model, Entity unitEntity) {
        // clear used data
        TrackComponent trackComponent = unitEntity.get(TrackComponent.class);
        trackComponent.clear();
        // set the starting location
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity startingTileEntity = movementComponent.getCurrentTile();
        Tile startingTile = startingTileEntity.get(Tile.class);
        Vector3f startingTileLocation = startingTile.getLocalVector(model);
        trackComponent.setPosition((int) startingTileLocation.x, (int) startingTileLocation.y);

        Vector3f vector = new Vector3f();
        vector.copy(startingTileLocation);
        trackComponent.addPoint(vector);

        double angle;
        for (int i = 0; i < 360; i++) {
            if (i % 15 != 0) { continue; }
            angle = i * (Math.PI / 180);
            float x = (float) (startingTileLocation.x + 5 * Math.sin(angle));
            float y = (float) (startingTileLocation.y + 5 * Math.cos(angle));
            trackComponent.addPoint(new Vector3f(x, y));
        }

        vector = new Vector3f();
        vector.copy(startingTileLocation);
        trackComponent.addPoint(vector);

        int speed = getSpeed(model, 6900, 6950);
        trackComponent.setSpeed(speed);
    }

    public void executeMoveAnimation(GameModel model, Entity unitEntity, Deque<Entity> pathing) {
        // clear used data
        TrackComponent trackComponent = unitEntity.get(TrackComponent.class);
        trackComponent.clear();

        // set the starting location
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity startingTileEntity = movementComponent.getCurrentTile();
        Tile startingTile = startingTileEntity.get(Tile.class);
        Vector3f startingTileLocation = startingTile.getLocalVector(model);
        trackComponent.setPosition((int) startingTileLocation.x, (int) startingTileLocation.y);

        for (Entity pathedEntity : pathing) {
            Tile pathedTile = pathedEntity.get(Tile.class);
            Vector3f tileLocation = pathedTile.getLocalVector(model);
            trackComponent.addPoint(tileLocation);
        }

        int speed = getSpeed(model, 5, 7);
        trackComponent.setSpeed(speed);
    }

    public void executeShakeAnimation(GameModel model, Entity unitEntity) {
        // clear used data
        TrackComponent trackComponent = unitEntity.get(TrackComponent.class);
        trackComponent.clear();

        // set the starting location
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity startingTileEntity = movementComponent.getCurrentTile();
        Tile startingTile = startingTileEntity.get(Tile.class);
        Vector3f startingTileLocation = startingTile.getLocalVector(model);
        trackComponent.setPosition((int) startingTileLocation.x, (int) startingTileLocation.y);

        // create chake for the track
        Vector3f vector = new Vector3f(startingTileLocation.x, startingTileLocation.y);
        trackComponent.addPoint(vector);
        int spriteWidth = model.getGameState().getSpriteWidth();
        int spriteHeight = model.getGameState().getSpriteHeight();
        float spriteSize = (float) (spriteWidth + spriteHeight) / 2;

        for (int i = 0; i < 8; i++) {
            vector = new Vector3f();
            if (i % 2 == 0) {
                vector.x = startingTileLocation.x - (spriteSize / 8f);
            } else {
                vector.x = startingTileLocation.x + (spriteSize / 8f);
            }
            vector.y = startingTileLocation.y;
            trackComponent.addPoint(vector);
        }
        vector = new Vector3f(startingTileLocation.x, startingTileLocation.y);
        trackComponent.addPoint(vector);

        int speed = getSpeed(model, 15, 25);
        trackComponent.setSpeed(speed);
    }

    private int getSpeed(GameModel model, int speed1, int speed2) {
        int spriteWidth = model.getGameState().getSpriteWidth();
        int spriteHeight = model.getGameState().getSpriteHeight();
        float spriteSize = (float) (spriteWidth + spriteHeight) / 2;
        return (int) (spriteSize * RandomUtils.getRandomNumberBetween(speed1, speed2));
    }
}
