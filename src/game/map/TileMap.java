package game.map;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import game.components.Tile;
import game.entity.Entity;
import game.queue.SpeedQueue;
import logging.Logger;
import logging.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

public class TileMap {

    private Entity[][] raw;
    private final SplittableRandom random = new SplittableRandom();
    private final Logger logger = LoggerFactory.instance().logger(getClass());
    public TileMap(Entity[][] map) { raw = map; }

    public void place(SpeedQueue queue) {
        Entity entity = getNaivelyRandomTile();
        Tile tile = entity.get(Tile.class);
        for (Entity unit : queue.getOrdering()) {
            while (tile.isStructureUnitOrWall()) {
                entity = getNaivelyRandomTile();
                tile = entity.get(Tile.class);
            }
            tile.setUnit(unit);
            logger.log(unit + " placed on " + unit);
        }
        logger.log("Starting turn order -> " + queue);
    }

    public int getRows() { return raw.length; }
    public int getColumns(int row) { return raw[row].length; }
    public int getColumns() { return getColumns(0); }
    public Entity getTileAt(int row, int column) { return raw[row][column]; }
    public Entity getNaivelyRandomTile() {
        int row = random.nextInt(raw.length);
        int column = random.nextInt(raw[row].length);
        return raw[row][column];
    }

    public JsonObject toJson() {
        JsonObject wrapper = new JsonObject();
        JsonArray tilemap = new JsonArray();
        // Add all cells of the tile to a json structure
        for (Entity[] entities : raw) {
            JsonArray jsonArrayRow = new JsonArray();
            for (Entity entity : entities) {
                Tile tile = entity.get(Tile.class);
                jsonArrayRow.add(tile.toJson());
            }
            tilemap.add(jsonArrayRow);
        }
        wrapper.put(Jsoner.mintJsonKey(getClass().getSimpleName(), new JsonArray()), tilemap);
        return wrapper;
    }
}
