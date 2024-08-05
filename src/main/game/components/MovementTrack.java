package main.game.components;

import main.constants.Settings;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.utils.RandomUtils;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

public class MovementTrack extends Component {

    public final List<Vector3f> track = new ArrayList<>();
    public final Vector3f location = new Vector3f();
    public float progress = 0;
    public int index = 0;
    public float speed = 0;

    public void clear() {
        track.clear();
        index = 0;
    }

    public void gyrate(GameModel model, Entity unit) {
        Entity startingTile = unit.get(MovementManager.class).currentTile;
        Vector3f startingVector = startingTile.get(Vector3f.class);

        clear();

        Vector3f vector = new Vector3f();
        vector.copy(startingVector);
        track.add(vector);

        double angle;
        for (int i = 0; i < 360; i++) {
            if (i % 15 != 0) { continue; }
            angle = i * (Math.PI / 180);
            float x = (float) (startingVector.x + 5 * Math.sin(angle));
            float y = (float) (startingVector.y + 5 * Math.cos(angle));
            track.add(new Vector3f(x, y));
        }

        vector = new Vector3f();
        vector.copy(startingVector);
        track.add(vector);

        speed = getSpeed(model, 6900, 6950);
    }

    public void forwardsThenBackwards(GameModel model, Entity unit, Entity toGoTo) {
        Entity startingTile = unit.get(MovementManager.class).currentTile;
        Vector3f startingVector = startingTile.get(Vector3f.class);

        clear();

        Vector3f vector = new Vector3f();
        vector.copy(startingVector);
        track.add(vector);

        vector = new Vector3f();
        vector.copy(toGoTo.get(Vector3f.class));
        track.add(vector);

        vector = new Vector3f();
        vector.copy(startingVector);
        track.add(vector);

        speed = getSpeed(model, 5, 9);
    }

    public void wiggle(GameModel model, Entity unit) {
        Entity startingTile = unit.get(MovementManager.class).currentTile;
        Vector3f startingVector = startingTile.get(Vector3f.class);
        clear();

        Vector3f vector = new Vector3f(startingVector.x, startingVector.y);
        track.add(vector);
        int spriteWidth = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_WIDTH);
        int spriteHeight = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_HEIGHT);
        float spriteSize = (float) (spriteWidth + spriteHeight) / 2;

        for (int i = 0; i < 6; i++) {
            vector = new Vector3f();
            if (i % 2 == 0) {
                vector.x = startingVector.x - (spriteSize / 8f);
            } else {
                vector.x = startingVector.x + (spriteSize / 8f);
            }
            vector.y = startingVector.y;
            track.add(vector);
        }
        vector = new Vector3f(startingVector.x, startingVector.y);
        track.add(vector);

        speed = getSpeed(model, 15, 25);
    }

    public void move2(GameModel model, Queue<Entity> tilePath) {
        int spriteWidth = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_WIDTH);
        int spriteHeight = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_HEIGHT);

        clear();

        Tile tile = null;

        for (Entity entity : tilePath) {
            tile = entity.get(Tile.class);
            Vector3f vector3f = new Vector3f(tile.column * spriteWidth, tile.row * spriteHeight);
            track.add(vector3f);
        }

        speed = getSpeed(model, 5, 7);
    }

    public void move(GameModel model, Entity unit, Deque<Entity> tilesInPath) {
        int spriteWidth = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_WIDTH);
        int spriteHeight = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_HEIGHT);

        clear();

        Tile tile = null;

        for (Entity entity : tilesInPath) {
            tile = entity.get(Tile.class);
            Vector3f vector3f = new Vector3f(tile.column * spriteWidth, tile.row * spriteHeight);
            track.add(vector3f);
        }

        tile = tilesInPath.getLast().get(Tile.class);
        tile.setUnit(unit);

        speed = getSpeed(model, 5, 7);
    }

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
        int spriteWidth = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_WIDTH);
        int spriteHeight = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_HEIGHT);
        float spriteSize = (float) (spriteWidth + spriteHeight) / 2;
        return (int) (spriteSize * RandomUtils.getRandomNumberBetween(speed1, speed2));
    }

    public boolean isMoving() { return !track.isEmpty(); }
    public boolean isDone() { return index >= track.size() - 1;}
}


//public class MovementTrack extends Component {
//
//    public final List<Vector3f> track = new ArrayList<>();
//    public final Vector3f location = new Vector3f();
//    public float progress = 0;
//    public int index = 0;
//    public float speed = 0;
//
//    public void clear() {
//        track.clear();
//        index = 0;
//    }
//
//    public void gyrate(GameModel model, Entity unit) {
//        Entity startingTile = unit.get(MovementManager.class).currentTile;
//        Vector3f startingVector = startingTile.get(Vector3f.class);
//
//        clear();
//
//        Vector3f vector = new Vector3f();
//        vector.copy(startingVector);
//        track.add(vector);
//
//        double angle;
//        for (int i = 0; i < 360; i++) {
//            if (i % 15 != 0) { continue; }
//            angle = i * (Math.PI / 180);
//            float x = (float) (startingVector.x + 5 * Math.sin(angle));
//            float y = (float) (startingVector.y + 5 * Math.cos(angle));
//            track.add(new Vector3f(x, y));
//        }
//
//        vector = new Vector3f();
//        vector.copy(startingVector);
//        track.add(vector);
//
//        speed = getSpeed(model, 6900, 6950);
//    }
//
//    public void forwardsThenBackwards(GameModel model, Entity unit, Entity toGoTo) {
//        Entity startingTile = unit.get(MovementManager.class).currentTile;
//        Vector3f startingVector = startingTile.get(Vector3f.class);
//
//        clear();
//
//        Vector3f vector = new Vector3f();
//        vector.copy(startingVector);
//        track.add(vector);
//
//        vector = new Vector3f();
//        vector.copy(toGoTo.get(Vector3f.class));
//        track.add(vector);
//
//        vector = new Vector3f();
//        vector.copy(startingVector);
//        track.add(vector);
//
//        speed = getSpeed(model, 5, 9);
//    }
//
//    public void wiggle(GameModel model, Entity unit) {
//        Entity startingTile = unit.get(MovementManager.class).currentTile;
//        Vector3f startingVector = startingTile.get(Vector3f.class);
//        clear();
//
//        Vector3f vector = new Vector3f(startingVector.x, startingVector.y);
//        track.add(vector);
//        int spriteWidth = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_WIDTH);
//        int spriteHeight = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_HEIGHT);
//        float spriteSize = (float) (spriteWidth + spriteHeight) / 2;
//
//        for (int i = 0; i < 6; i++) {
//            vector = new Vector3f();
//            if (i % 2 == 0) {
//                vector.x = startingVector.x - (spriteSize / 8f);
//            } else {
//                vector.x = startingVector.x + (spriteSize / 8f);
//            }
//            vector.y = startingVector.y;
//            track.add(vector);
//        }
//        vector = new Vector3f(startingVector.x, startingVector.y);
//        track.add(vector);
//
//        speed = getSpeed(model, 15, 25);
//    }
//
//    public void move(GameModel model, Entity unit, Deque<Entity> tilesInPath) {
//        int spriteWidth = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_WIDTH);
//        int spriteHeight = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_HEIGHT);
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
//
////    public void move(GameModel model, Entity unit, Entity toMoveTo) {
////        MovementManager movementManager = unit.get(MovementManager.class);
////        int spriteWidth = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_WIDTH);
////        int spriteHeight = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_HEIGHT);
////
////        clear();
////
////        Tile tile = null;
////
////        for (Entity entity : movementManager.tilesInPath) {
////            tile = entity.get(Tile.class);
////            Vector3f vector3f = new Vector3f(tile.column * spriteWidth, tile.row * spriteHeight);
////            track.add(vector3f);
////        }
////
////        tile = toMoveTo.get(Tile.class);
////        tile.setUnit(unit);
////
////        speed = getSpeed(model, 5, 7);
////    }
//
//    public void set(GameModel model, Entity unit, Entity toMoveTo) {
//        clear();
//        Tile tileToMoveTo = toMoveTo.get(Tile.class);
//        tileToMoveTo.setUnit(unit);
//    }
//
//    private static int getSpeed(GameModel model, int speed1, int speed2) {
//        int spriteWidth = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_WIDTH);
//        int spriteHeight = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_HEIGHT);
//        float spriteSize = (float) (spriteWidth + spriteHeight) / 2;
//        return (int) (spriteSize * RandomUtils.getRandomNumberBetween(speed1, speed2));
//    }
//
//    public boolean isMoving() { return !track.isEmpty(); }
//    public boolean isDone() { return index >= track.size() - 1;}
//}
