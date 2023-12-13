package main.game.map;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import main.constants.ColorPalette;
import main.constants.Direction;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.stores.factories.TileFactory;
import main.game.stores.pools.AssetPool;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.awt.Color;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class TileMap implements Serializable {

    private final Entity[][] mRawMap;
    private static final SplittableRandom mRandom = new SplittableRandom();
    private static final ELogger mLogger = ELoggerFactory.getInstance().getELogger(TileMap.class);

    public TileMap(Entity[][] map) { 
        mRawMap = map;
        createShadows(mRawMap);
    }

    public TileMap(JsonArray array) {
        mRawMap = fromJson(array);
        createShadows(mRawMap);
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
                    int id = AssetPool.getInstance()
                            .createAsset(AssetPool.MISC_SPRITEMAP, "directional_shadows", index, AssetPool.STATIC_ANIMATION);
                    currentTile.putAsset(direction + " " + Tile.SHADOW, id);
                    int tileHeightDifference = Math.abs(currentTile.getHeight() - adjacentTile.getHeight());
//                    if (tileHeightDifference > 1) {
////                        currentTile.shadowIds.add(id);
//                    }
                }
            }
        }
    }

//    private void createShadows(Entity[][] map) {
//
//        // Go through each tile
//        for (int row = 0; row < map.length; row++) {
//            for (int column = 0; column < map[row].length; column++) {
//
//                // Ensure within bounds
//                // if (row == 0 || column == 0) { continue; }
//                // if (row == map.length - 1 || column == map[row].length - 1) { continue; }
//
//                // get current height
//                Entity currentEntity = map[row][column];
//                Tile currentTile = currentEntity.get(Tile.class);
//                if (currentTile.isWall()) {
//                    continue;
//                }
//
//                Map<String, Integer> shadows = new LinkedHashMap<>();
//
//                // Check all the tiles in all directions
//                for (Direction direction : Direction.values()) {
//
//                    int nextRow = row + direction.y;
//                    int nextColumn = column + direction.x;
//
//                    Entity adjacentEntity = tryFetchingTileAt(nextRow, nextColumn);
//                    if (adjacentEntity == null) {
//                        continue;
//                    }
//                    Tile adjacentTile = adjacentEntity.get(Tile.class);
//
//                    // If the adjacent tile is higher, add a shadow in that direction
//                    if (adjacentTile.getHeight() <= currentTile.getHeight() && adjacentTile.isPath()) {
//                        continue;
//                    }
//                    // Enhanced liquid visuals where shadows not showing on them
////                    if (adjacentTile.getLiquid() != 0) { continue; }
//
//                    int index = direction.ordinal();
//
//                    // TODO this is showing under walls, find a way to remove it
//                    int id = AssetPool.getInstance()
//                            .createAsset(AssetPool.MISC_SPRITEMAP, "directional_shadows", index, AssetPool.STATIC_ANIMATION);
//                    shadows.put(direction + " " + Tile.SHADOW, id);
//                }
//
//
//                for (Map.Entry<String, Integer> entry : shadows.entrySet()) {
//                    currentTile.putAssetId(entry.getKey(), entry.getValue());
//                }
//            }
//        }
//    }

//    private static boolean canPlaceCornerShadow(Map<String, Integer> map) {
//
//        Set<Direction> ordinals = Set.of(Direction.ordinal);
//        Set<Direction> cardinals = Set.of(Direction.cardinal);
//
//                Set<String> cardinalsToRemove = new HashSet<>();
//
//        // Find all the ordinal/corner tiles
//        for (Map.Entry<String, Integer> entry : map.entrySet()) {
//            Direction direction = Direction.valueOf(entry.getKey());
//            if (!ordinals.contains(direction)) { continue; } // if not ordinal, we don't care
//            // find the cardinal tiles related to the ordinal tile
//            Set detectedCardinals =
//
//
////            currentTile.putAssetId(entry.getKey(), entry.getValue());
//        }
//
//        return false;
//    }

    public int getRows() { return mRawMap.length; }
    public int getColumns(int row) { return mRawMap[row].length; }
    public int getColumns() { return getColumns(0); }
    public Entity tryFetchingTileAt(int row, int column) {
        if (row < 0 || column < 0 || row >= mRawMap.length || column >= mRawMap[row].length) {
            return null;
        } else {
            return mRawMap[row][column];
        }
    }
    public Entity getNaivelyRandomTile() {
        int row = mRandom.nextInt(mRawMap.length);
        int column = mRandom.nextInt(mRawMap[row].length);
        return mRawMap[row][column];
    }

    public int[] getRandomRowColumn() {
        int row = mRandom.nextInt(mRawMap.length);
        int column = mRandom.nextInt(mRawMap[row].length);
        return new int[]{ row, column };
    }

    public JsonArray toJson() {
        JsonArray rows = new JsonArray();
        // Add all cells of the tile to a json structure
        for (Entity[] row : mRawMap) {
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

    public void place(Entity entity, int[] location) {
        // Get the tile to place the entity on
        Tile tile = mRawMap[location[0]][location[1]].get(Tile.class);
        if (tile.isNotNavigable()) { return; }

        tile.setUnit(entity);
    }
    public boolean placeByDivision(int slices, int location, List<Entity> team) {
        return placeByDivision(slices, location, team, true);
    }

    public boolean placeByDivision(int slices, int location, List<Entity> team, boolean randomized) {
        // Divide map by NxN
        Map<Integer, List<Entity>> areas = getAreas(slices);

        List<Entity> area = areas.get(location);
        if (team.size() > area.size()) { return false; }


        if (randomized) {
            for (Entity value : team) {
                Tile tile = area.get(mRandom.nextInt(area.size())).get(Tile.class);
                while (tile.isNotNavigable()) { tile = area.get(mRandom.nextInt(area.size())).get(Tile.class); }
                tile.setUnit(value);
            }
        } else {
            int unit = 0;
            for (Entity entity : area) {
                Tile tile = entity.get(Tile.class);

                if (tile.isNotNavigable()) { continue; }
                if (unit >= team.size()) { continue; }

                tile.setUnit(team.get(unit));
                unit++;
            }
        }

        return true;
    }

    private Map<Integer, List<Entity>> getAreas(int slices) {
        Map<Integer, List<Entity>> areas = new LinkedHashMap<>();

        for (int row = 0; row < slices; row++) {
            for (int column = 0; column < slices; column++) {
                int startRow = row * (mRawMap.length / slices);
                int endRow = (row + 1) * (mRawMap.length / slices);
                int startColumn = column * (mRawMap[startRow].length / slices);
                int endColumn = (column + 1) * (mRawMap[startRow].length / slices);

//                mLogger.info("ROW: [{}, {}), COLUMN: [{}, {})", startRow, endRow, startColumn, endColumn);

                Color c = ColorPalette.getRandomColorWithAlpha();
                List<Entity> area = new ArrayList<>();
                for (int innerRow = startRow; innerRow < endRow; innerRow++) {
                    for (int innerColumn = startColumn; innerColumn < endColumn; innerColumn++) {

                        Entity entity = mRawMap[innerRow][innerColumn];
                        area.add(entity);

                        Tile tile = entity.get(Tile.class);
                        tile.setProperty(Tile.SPAWN, c);
                    }
                }
                areas.put(areas.size(), area);
            }
        }

        return areas;
    }
}
