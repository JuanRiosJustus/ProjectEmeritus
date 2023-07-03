package game.map;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import constants.Constants;
import designer.fundamentals.Direction;
import game.components.Tile;
import game.entity.Entity;
import game.queue.SpeedQueue;
import game.stores.pools.AssetPool;
import logging.Logger;
import logging.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.SplittableRandom;

public class TileMap {

    private final Entity[][] raw;
    private final SplittableRandom random = new SplittableRandom();
    private final Logger logger = LoggerFactory.instance().logger(TileMap.class);

    public TileMap(Entity[][] map) { raw = map; createShadows(raw); }

    private void createShadows(Entity[][] map) {
        // Go through each tile
        for (int row = 0; row < map.length; row++) {
            for (int column = 0; column < map[row].length; column++) {

                // Ensure within bounds
                // if (row == 0 || column == 0) { continue; }
                // if (row == map.length - 1 || column == map[row].length - 1) { continue; }

                // get current height
                Entity currentEntity = map[row][column];
                Tile currentTile = currentEntity.get(Tile.class);
                if (currentTile.isWall()) { continue; }

                int currentHeight = currentTile.getHeight();

                // Check all the tiles in all directions
                for (Direction direction : Direction.values()) {

                    int nextRow = row + direction.x;
                    int nextColumn = column + direction.y;

                    Entity adjacentEntity = tryFetchingTileAt(nextRow, nextColumn);
                    if (adjacentEntity == null) { continue; }
                    Tile adjacentTile = adjacentEntity.get(Tile.class);

                    // If the adjacent tile is higher, add a shadow in that direction
                    int adjacentHeight = adjacentTile.getHeight();
                    if (adjacentHeight <= currentHeight && adjacentTile.isPath()) { continue; }
                    // Enhancd liquied visuals
                    if (adjacentTile.getLiquid() != 0) { continue; }

                    int index = direction.ordinal();

                    BufferedImage image = AssetPool.instance().getImage(Constants.SHADOWS_SPRITESHEET_FILEPATH, 0, 0, index);
                    currentTile.shadows.add(image);
                }
            }
        }
    }

    public void place(SpeedQueue queue) {
        Entity entity = getNaivelyRandomTile();
        Tile tile = entity.get(Tile.class);
        for (Entity unit : queue.getQueue()) {
            while (tile.isStructureUnitOrWall()) {
                entity = getNaivelyRandomTile();
                tile = entity.get(Tile.class);
            }
            tile.setUnit(unit);
            logger.info(unit + " placed on " + unit);
        }
        logger.info("Starting turn order -> " + queue);
    }

    public int getRows() { return raw.length; }
    public int getColumns(int row) { return raw[row].length; }
    public int getColumns() { return getColumns(0); }
    public Entity tryFetchingTileAt(int row, int column) {
        if (row < 0 || column < 0 || row >= raw.length || column >= raw[row].length) {
            return null;
        } else {
            return raw[row][column];
        }
    }
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
