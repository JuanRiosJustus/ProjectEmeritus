package main.game.map;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import main.constants.Constants;
import designer.fundamentals.Direction;
import main.game.components.Tile;
import main.game.entity.Entity;
import main.game.queue.SpeedQueue;
import main.game.stores.factories.TileFactory;
import main.game.stores.pools.AssetPool;
import main.graphics.SpriteSheet;
import main.graphics.SpriteSheetMap;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class TileMap implements Serializable {

    private final Entity[][] raw;
    private static final SplittableRandom random = new SplittableRandom();
    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(TileMap.class);

    public TileMap(Entity[][] map) { 
        raw = map; 
        createShadows(raw); 
    }

    public TileMap(JsonArray array) {
        raw = fromJson(array);
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

                // Check all the tiles in all directions
                for (Direction direction : Direction.values()) {

                    int nextRow = row + direction.y;
                    int nextColumn = column + direction.x;

                    Entity adjacentEntity = tryFetchingTileAt(nextRow, nextColumn);
                    if (adjacentEntity == null) { continue; }
                    Tile adjacentTile = adjacentEntity.get(Tile.class);

                    // If the adjacent tile is higher, add a shadow in that direction
                    if (adjacentTile.getHeight() <= currentTile.getHeight() && adjacentTile.isPath()) { continue; }
                    // Enhanced liquid visuals where shadows not showing on them
//                    if (adjacentTile.getLiquid() != 0) { continue; }

                    int index = direction.ordinal();

                    // TODO this is showing under walls, find a way to remove it
                    int id = AssetPool.getInstance().createAsset("directional_shadows", index, AssetPool.STATIC_ANIMATION);
                    currentTile.shadowIds.add(id);
                    int tileHeightDifference = Math.abs(currentTile.getHeight() - adjacentTile.getHeight());
                    if (tileHeightDifference > 1) {
                        currentTile.shadowIds.add(id);
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
        for (Entity unit : queue.getAvailable()) {
            while (tile.isObstructed()) {
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
                if (tile.isObstructed()) {
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

    public JsonArray toJson() {
        JsonArray rows = new JsonArray();
        // Add all cells of the tile to a json structure
        for (Entity[] row : raw) {
            JsonArray jsonArray = new JsonArray();
            for (Entity column : row) {
                Tile tile = column.get(Tile.class);
                jsonArray.add(tile.toJson());
            }
            rows.add(jsonArray);
        }
       return rows;
    }

    private Entity[][] fromJson(JsonArray tileMapJson) {
        Entity[][] newEntityArray = new Entity[tileMapJson.size()][];
        for (int row = 0; row < tileMapJson.size(); row++) {
            JsonArray jsonRow = (JsonArray) tileMapJson.get(row);
            Entity[] entityRowArray = new Entity[jsonRow.size()];
            for (int column = 0; column < jsonRow.size(); column++) {
                Entity entity = TileFactory.create(row, column);
                Tile tile = entity.get(Tile.class);
                JsonObject tileObject = (JsonObject) jsonRow.get(column);
                tile.fromJson(tileObject);
                entityRowArray[column] = entity;
            }
            newEntityArray[row] = entityRowArray;
        }
        return newEntityArray;
    }

    public void saveToFile() {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("HH-mm");
            String fileName = LocalDate.now() + "-" + formatter.format(new Date()) + ".json";
            PrintWriter out = new PrintWriter(new FileWriter(fileName, false), true);
            out.write(toJson().toJson());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
