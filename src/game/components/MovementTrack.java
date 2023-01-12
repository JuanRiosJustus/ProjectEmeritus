package game.components;

import constants.Constants;
import game.GameModel;
import game.components.statistics.Statistics;
import game.entity.Entity;
import game.pathfinding.TilePathing;
import utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class MovementTrack extends Component {

    public float speed = 0;
    public List<Vector> track = new ArrayList<>();
    public float progress = 0;
    public int index = 0;

    public void clear() { track.clear(); index = 0; }

    public void gyrate(Entity unit) {

        Entity startingTile = unit.get(MovementManager.class).tileOccupying;
        Vector startingVector = startingTile.get(Vector.class);

        clear();

        Vector vector = new Vector();
        vector.copy(startingVector);
        track.add(vector);

        double angle;
        for (int i = 0; i < 360; i++) {
            if (i % 15 != 0) { continue; }
            angle = i * (Math.PI / 180);
            float x = (float) (startingVector.x + 5 * Math.sin(angle));
            float y = (float) (startingVector.y + 5 * Math.cos(angle));
            track.add(new Vector(x, y));
        }

        vector = new Vector();
        vector.copy(startingVector);
        track.add(vector);

        speed = getSpeed(6900, 6950);
    }

    public void forwardsThenBackwards(Entity unit, Entity toGoTo) {
        Entity startingTile = unit.get(MovementManager.class).tileOccupying;
        Vector startingVector = startingTile.get(Vector.class);

        clear();

        Vector vector = new Vector();
        vector.copy(startingVector);
        track.add(vector);

        vector = new Vector();
        vector.copy(toGoTo.get(Vector.class));
        track.add(vector);

        vector = new Vector();
        vector.copy(startingVector);
        track.add(vector);

        speed = getSpeed(5, 9);
    }

    public void wiggle(Entity unit) {
        Entity startingTile = unit.get(MovementManager.class).tileOccupying;
        Vector startingVector = startingTile.get(Vector.class);
        clear();

        Vector vector = new Vector(startingVector.x, startingVector.y);
        track.add(vector);
        for (int i = 0; i < 6; i++) {
            vector = new Vector();
            if (i % 2 == 0) {
                vector.x = startingVector.x - (Constants.CURRENT_SPRITE_SIZE / 8f);
            } else {
                vector.x = startingVector.x + (Constants.CURRENT_SPRITE_SIZE / 8f);
            }
            vector.y = startingVector.y;
            track.add(vector);
        }
        vector = new Vector(startingVector.x, startingVector.y);
        track.add(vector);

        speed = getSpeed(15, 25);
    }

    public void move(GameModel model, Entity unit, Entity toMoveTo) {
        Statistics stats = unit.get(Statistics.class);
        MovementManager movement = unit.get(MovementManager.class);

        TilePathing.getTilesWithinPath(
                model,
                movement.tileOccupying,
                toMoveTo,
                stats.getScalarNode(Constants.MOVE).getTotal(),
                movement.tilesWithinMovementPath
        );


        clear();

        for (Entity entity : movement.tilesWithinMovementPath) {
            Vector tileVector = entity.get(Vector.class);
            Vector vector = new Vector(tileVector.x, tileVector.y);
            track.add(vector);
        }

        Tile tileToMoveTo = toMoveTo.get(Tile.class);
        tileToMoveTo.setUnit(unit);

        speed = getSpeed(4, 7);
    }

    private static int getSpeed(int speed1, int speed2) {
        return Constants.CURRENT_SPRITE_SIZE * RandomUtils.getRandomNumberBetween(speed1, speed2);
    }

    public boolean isMoving() { return !track.isEmpty(); }
}
