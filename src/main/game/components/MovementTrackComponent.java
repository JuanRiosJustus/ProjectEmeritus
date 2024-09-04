package main.game.components;

import main.constants.Vector3f;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.utils.RandomUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MovementTrackComponent extends Component {

    private final List<Vector3f> mTrack = new LinkedList<>();
    private final Vector3f mLocation = new Vector3f();
    private float mProgress = 0;
    private int mIndex = 0;
    private float mSpeed = 0;

    public void clear() {
        mTrack.clear();
        mIndex = 0;
    }

    public void gyrate(GameModel model, Entity unitEntity) {
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity tileEntity = movementComponent.getCurrentTile();
        Tile tile = tileEntity.get(Tile.class);
        Vector3f startingVector = tile.getLocation(model);

        clear();

        Vector3f vector = new Vector3f();
        vector.copy(startingVector);
        mTrack.add(vector);

        double angle;
        for (int i = 0; i < 360; i++) {
            if (i % 15 != 0) { continue; }
            angle = i * (Math.PI / 180);
            float x = (float) (startingVector.x + 5 * Math.sin(angle));
            float y = (float) (startingVector.y + 5 * Math.cos(angle));
            mTrack.add(new Vector3f(x, y));
        }

        vector = new Vector3f();
        vector.copy(startingVector);
        mTrack.add(vector);

        mSpeed = getSpeed(model, 6900, 6950);
    }

    public void toTargetAndBack(GameModel model, Entity unitEntity, Entity endingTileEntity) {
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity startingTileEntity = movementComponent.getCurrentTile();
        Tile tile = startingTileEntity.get(Tile.class);
        Vector3f startingVector = tile.getLocation(model);

        tile = endingTileEntity.get(Tile.class);
        Vector3f endingVector = tile.getLocation(model);

        clear();

        mTrack.add(startingVector);
        mTrack.add(endingVector);
        mTrack.add(startingVector);

        mSpeed = getSpeed(model, 5, 20);
    }

    public void shake(GameModel model, Entity unitEntity) {
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity tileEntity = movementComponent.getCurrentTile();
        Tile tile = tileEntity.get(Tile.class);
        Vector3f startingVector = tile.getLocation(model);


        clear();

        Vector3f vector = new Vector3f(startingVector.x, startingVector.y);
        mTrack.add(vector);
        int spriteWidth = model.getSettings().getSpriteWidth();
        int spriteHeight = model.getSettings().getSpriteHeight();
        float spriteSize = (float) (spriteWidth + spriteHeight) / 2;

        for (int i = 0; i < 8; i++) {
            vector = new Vector3f();
            if (i % 2 == 0) {
                vector.x = startingVector.x - (spriteSize / 8f);
            } else {
                vector.x = startingVector.x + (spriteSize / 8f);
            }
            vector.y = startingVector.y;
            mTrack.add(vector);
        }
        vector = new Vector3f(startingVector.x, startingVector.y);
        mTrack.add(vector);

        mSpeed = getSpeed(model, 15, 25);
    }

    public void move(GameModel model, Queue<Entity> tilePath) {
        int spriteWidth = model.getSettings().getSpriteWidth();
        int spriteHeight = model.getSettings().getSpriteHeight();

        clear();

        Tile tile = null;

        for (Entity entity : tilePath) {
            tile = entity.get(Tile.class);
            Vector3f vector3f = new Vector3f(tile.column * spriteWidth, tile.row * spriteHeight);
            mTrack.add(vector3f);
        }

        mSpeed = getSpeed(model, 5, 7);
    }

//    public void move(GameModel model, Entity unit, Deque<Entity> tilesInPath) {
//        int spriteWidth = model.getSettings().getSpriteWidth();
//        int spriteHeight = model.getSettings().getSpriteHeight();
//
//        clear();
//
//        Tile tile = null;
//
//        for (Entity entity : tilesInPath) {
//            tile = entity.get(Tile.class);
//            Vector3f vector3f = new Vector3f(tile.column * spriteWidth, tile.row * spriteHeight);
//            track.add(vector3f);
//        }
//
//        tile = tilesInPath.getLast().get(Tile.class);
//        tile.setUnit(unit);
//
//        speed = getSpeed(model, 5, 7);
//    }

//    public void move(GameModel model, Entity unit, Entity toMoveTo) {
//        MovementManager movementManager = unit.get(MovementManager.class);
//        int spriteWidth = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_WIDTH);
//        int spriteHeight = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_HEIGHT);
//
//        clear();
//
//        Tile tile = null;
//
//        for (Entity entity : movementManager.tilesInPath) {
//            tile = entity.get(Tile.class);
//            Vector3f vector3f = new Vector3f(tile.column * spriteWidth, tile.row * spriteHeight);
//            track.add(vector3f);
//        }
//
//        tile = toMoveTo.get(Tile.class);
//        tile.setUnit(unit);
//
//        speed = getSpeed(model, 5, 7);
//    }

    public void set(GameModel model, Entity unit, Entity toMoveTo) {
        clear();
        Tile tileToMoveTo = toMoveTo.get(Tile.class);
        tileToMoveTo.setUnit(unit);
    }

    private static int getSpeed(GameModel model, int speed1, int speed2) {
        int spriteWidth = model.getSettings().getSpriteWidth();
        int spriteHeight = model.getSettings().getSpriteHeight();
        float spriteSize = (float) (spriteWidth + spriteHeight) / 2;
        return (int) (spriteSize * RandomUtils.getRandomNumberBetween(speed1, speed2));
    }

    public float getProgress() { return mProgress; }
    public void incrementIndex() { mIndex++; }
    public int getIndex() { return mIndex; }
    public Vector3f getVectorAt(int index) { return mTrack.get(index); }

    public boolean isMoving() { return !mTrack.isEmpty(); }
    public boolean isDone() { return mIndex >= mTrack.size() - 1; }
    public boolean isEmpty() { return mTrack.isEmpty(); }
    public int getX() { return (int) mLocation.x; }
    public int getY() { return (int) mLocation.y; }
    public void setLocation(int x, int y) { mLocation.copy(x, y, 0); }
    public int getTrackMarkers() { return mTrack.size(); }
    public void setProgress(float progress) { mProgress = progress; }
    public void increaseProgress(float toAdd) { mProgress += toAdd; }
    public float getSpeed() { return mSpeed; }
}