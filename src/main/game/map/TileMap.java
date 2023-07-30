package main.game.map;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import main.constants.Constants;
import designer.fundamentals.Direction;
import main.game.components.Tile;
import main.game.entity.Entity;
import main.game.queue.SpeedQueue;
import main.game.stores.pools.AssetPool;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

public class TileMap {

    private final Entity[][] raw;
    private final SplittableRandom random = new SplittableRandom();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(TileMap.class);

    public TileMap(Entity[][] map) { 
        raw = map; 
        createShadows(raw); 
    }

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

                    int nextRow = row + direction.y;
                    int nextColumn = column + direction.x;

                    Entity adjacentEntity = tryFetchingTileAt(nextRow, nextColumn);
                    if (adjacentEntity == null) { continue; }
                    Tile adjacentTile = adjacentEntity.get(Tile.class);

                    // If the adjacent tile is higher, add a shadow in that direction
                    int adjacentHeight = adjacentTile.getHeight();
                    if (adjacentHeight <= currentHeight && adjacentTile.isPath()) { continue; }
                    // Enhancd liquied visuals where shadows not showing on them
                    if (adjacentTile.getLiquid() != 0) { continue; }

                    int index = direction.ordinal();

                    // TODO this is showing under walls, find a way to remove it
                    BufferedImage image = AssetPool.getInstance().getImage(Constants.SHADOWS_SPRITESHEET_FILEPATH, 0, 0, index);
                    currentTile.shadows.add(image);
                    int tileHeightDifference = Math.abs(currentTile.getHeight() - adjacentTile.getHeight());
                    for (int i = 0; i < tileHeightDifference/2; i++) { 
                        if (i > 1) { continue; }                   
                        currentTile.shadows.add(image);
                    }
                }
            }
        }
    }

    /**
     * Place all the entities from the queue on the map
     * @param queue
     */
    public void placeRandomly(SpeedQueue queue) {
        Entity entity = getNaivelyRandomTile();
        Tile tile = entity.get(Tile.class);
        for (Entity unit : queue.getAvailableTurnQueue()) {
            while (tile.isStructureUnitOrWall()) {
                entity = getNaivelyRandomTile();
                tile = entity.get(Tile.class);
            }
            tile.setUnit(unit);
            logger.info(unit + " placed on " + unit);
        }
        logger.info("Starting turn order -> " + queue);
    }

    public void placeByTeam(SpeedQueue speedQueue, int width, int height) {
        Set<Set<Entity>> teams = speedQueue.getTeams();
        int[] rowColumn = getRandomRowColumn();
        for (Set<Entity> team : teams) {    
            // get list of all tiles within 3 x 4
            Set<Entity> rectangleSpawn = tryGettingRectangleSpawn(rowColumn[0], rowColumn[1], width, height);
            while(rectangleSpawn == null) {
                rowColumn = getRandomRowColumn();
                rectangleSpawn = tryGettingRectangleSpawn(rowColumn[0], rowColumn[1], width, height);
            }
            // place all entities from the team within rectangle
            Iterator<Entity> teamIterator = team.iterator();
            Iterator<Entity> tileIterator = rectangleSpawn.iterator();
            while (teamIterator.hasNext()) {
                Entity teamMember = teamIterator.next();
                Entity tileEntity = tileIterator.next();
                Tile tile = tileEntity.get(Tile.class);
                tile.setUnit(teamMember);
            }
        }
    }

    public Set<Entity> tryGettingRectangleSpawn(int row, int column, int width, int height) {
        if (row < 0 || row >= getRows()) { return null; }
        if (column < 0 || column > getColumns(row)) { return null; }
        if (row + height < 0 || row + height >= getRows()) { return null; }
        if (column + width < 0 || column + width > getColumns(row + height)) { return null; }

        int topRow = row; 
        int bottomRow = row + height;
        int leftColumn = column;
        int rightColumn = column + width;

        Set<Entity> tiles = new HashSet<>();

        for (int currentRow = topRow; currentRow < bottomRow; currentRow++) {
            for (int currentColumn = leftColumn; currentColumn < rightColumn; currentColumn++) {
                Entity entity = tryFetchingTileAt(currentRow, currentColumn);
                Tile tile = entity.get(Tile.class);
                if (tile.isStructureUnitOrWall()) {
                    return null;
                } else {
                    tiles.add(entity);
                }
            }
        }
        return tiles;
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

    public int[] getRandomRowColumn() {
        int row = random.nextInt(raw.length);
        int column = random.nextInt(raw[row].length);
        return new int[]{ row, column };
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
