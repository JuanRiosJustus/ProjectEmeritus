package main.game.components;

import main.constants.Settings;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class AnimationMovementTrack extends Component {

    public float speed = 0;
    public List<Vector3f> track = new ArrayList<>();
    public float progress = 0;
    public int index = 0;

    public void clear() { track.clear(); index = 0; }

    public void gyrate(Entity unit) {
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

        speed = getSpeed(6900, 6950);
    }

    public void forwardsThenBackwards(Entity unit, Entity toGoTo) {
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

        speed = getSpeed(5, 9);
    }

    public void wiggle(Entity unit) {
        Entity startingTile = unit.get(MovementManager.class).currentTile;
        Vector3f startingVector = startingTile.get(Vector3f.class);
        clear();

        Vector3f vector = new Vector3f(startingVector.x, startingVector.y);
        track.add(vector);
        float spriteSize = Settings.getInstance().getInteger(Settings.GAMEPLAY_CURRENT_SPRITE_SIZE);
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

        speed = getSpeed(15, 25);
    }

    public void move(GameModel model, Entity unit, Entity toMoveTo) {
        MovementManager movementManager = unit.get(MovementManager.class);

        clear();

        for (Entity entity : movementManager.path) {
            Vector3f tileVector = entity.get(Vector3f.class);
            Vector3f vector = new Vector3f(tileVector.x, tileVector.y);
            track.add(vector);
        }

        Tile tileToMoveTo = toMoveTo.get(Tile.class);
        tileToMoveTo.setUnit(unit);

        speed = getSpeed(5, 7);
    }

    public void set(GameModel model, Entity unit, Entity toMoveTo) {
        clear();
        Tile tileToMoveTo = toMoveTo.get(Tile.class);
        tileToMoveTo.setUnit(unit);
    }

    private static int getSpeed(int speed1, int speed2) {
        float spriteSize = Settings.getInstance().getInteger(Settings.GAMEPLAY_CURRENT_SPRITE_SIZE);
        return (int) (spriteSize * RandomUtils.getRandomNumberBetween(speed1, speed2));
    }

    public boolean isMoving() { return !track.isEmpty(); }
}
