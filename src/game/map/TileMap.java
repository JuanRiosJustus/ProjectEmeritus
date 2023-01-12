package game.map;

import game.components.ActionManager;
import game.components.Tile;
import game.entity.Entity;
import game.queue.SpeedQueue;
import logging.Logger;
import logging.LoggerFactory;

import java.util.SplittableRandom;

public class TileMap {

    public Entity[][] raw = null;
    public Tile[][] data = null;
    private final SplittableRandom random = new SplittableRandom();
    private final Logger logger = LoggerFactory.instance().logger(getClass());

    public TileMap(Entity[][] map) {
        raw = map;
        data = new Tile[raw.length][0];
        // Get the tile component within the entities
        for (int row = 0; row < raw.length; row++) {
            data[row] = new Tile[raw[row].length];
            for (int column = 0; column < raw[row].length; column++) {
                data[row][column] = raw[row][column].get(Tile.class);
            }
        }
    }

    public void place(SpeedQueue queue) {
        Entity tile = getNaivelyRandomTile();
        Tile details = tile.get(Tile.class);
        for (Entity entity : queue.getOrdering()) {

            ActionManager actionManager = entity.get(ActionManager.class);

            while (details.isStructureUnitOrWall()) {
                tile = getNaivelyRandomTile();
                details = tile.get(Tile.class);
            }
            details.setUnit(entity);
            logger.log(entity + " placed on " + tile);
        }
        logger.log("Starting turn order -> " + queue);
    }

    public Entity getNaivelyRandomTile() {
        int row = random.nextInt(raw.length);
        int column = random.nextInt(raw[row].length);
        return raw[row][column];
    }

    public String toString() {
        // Create a 2D array as a string representation of the underlying tilemap
        StringBuilder sb = new StringBuilder();
        for (Tile[] row : data) {
            for (Tile tile : row) {
                sb.append(tile.getEncoding());
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
