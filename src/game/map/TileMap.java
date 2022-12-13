package game.map;

import game.components.ActionManager;
import game.components.Tile;
import game.entity.Entity;
import game.queue.RPGQueue;
import logging.Logger;
import logging.LoggerFactory;

import java.util.SplittableRandom;

public class TileMap {

    public final Entity[][] tiles;
    private static final SplittableRandom random = new SplittableRandom();
    private final Logger logger = LoggerFactory.instance().logger(getClass());

    public TileMap(Entity[][] map) { tiles = map; }

    public void place(RPGQueue queue) {
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
        int row = random.nextInt(tiles.length);
        int column = random.nextInt(tiles[row].length);
        return tiles[row][column];
    }
}
