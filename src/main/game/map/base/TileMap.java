package main.game.map.base;

import main.game.components.AssetComponent;
import main.game.components.IdentityComponent;
import main.game.components.MovementComponent;
import main.game.main.GameConfigs;
import main.game.stores.EntityStore;
import main.logging.EmeritusLogger;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import main.game.components.tile.TileComponent;
import main.game.entity.Entity;
import main.graphics.AssetPool;

import main.utils.MathUtils;
import main.utils.noise.SimplexNoise;

import java.util.*;

public class TileMap extends JSONArray {
    protected static final EmeritusLogger mLogger = EmeritusLogger.create(TileMapBuilder.class);
    private Entity[][] mTileEntityMap;
    private String[][] mTileIDMap;
    private final List<String> mTileEntityIDs = new ArrayList<>();
    private final Random mRandom = new Random();
    private TileMapParameters mTileMapParameters = null;
    private final Map<String, List<String>> mSpawnRegions = new HashMap<>();

    public TileMap(TileMapParameters parameters) {
        mTileMapParameters = parameters;
        reset();
    }

    public TileMap(GameConfigs configs, JSONArray mapData) {
        if (mapData == null) {
            createMapFromConfigs(configs);
//            setupDefaultSpawns();
        } else {
            loadMapFromJson(mapData);
        }
    }

    private void createMapFromConfigs(GameConfigs configs) {
        int rows = configs.getRows();
        int columns = configs.getColumns();

        String liquidAsset = configs.getLiquidAsset();
        int liquidLevel = configs.getLiquidLevel();

        List<String> structures = configs.getStructureAssets();

        String terrain = configs.getMapGenerationTerrainAsset();
        int minElevation = configs.setMapGenerationTerrainMinimumElevation();
        int maxElevation = configs.getMapGenerationTerrainMaximumElevation();

        float noiseZoom = configs.getMapGenerationNoiseZoom();
        int[][] noiseMap = applySimplexNoise(rows, columns, minElevation, maxElevation, noiseZoom);

        mTileEntityMap = new Entity[rows][columns];
        mTileIDMap = new String[rows][columns];

        List<String> list = AssetPool.getInstance().getFloorTileSets();
        String foundation = list.get(mRandom.nextInt(list.size()));

        if (terrain == null || terrain.isEmpty()) {
            terrain = list.get(mRandom.nextInt(list.size()));
        }

        for (int row = 0; row < rows; row++) {
            JSONArray jsonRow = new JSONArray();
            for (int column = 0; column < columns; column++) {

                String newTileID = EntityStore.getInstance().getOrCreateTile(row, column, minElevation);
                Entity newTileEntity = EntityStore.getInstance().get(newTileID);
                TileComponent newTile = newTileEntity.get(TileComponent.class);
                mTileEntityIDs.add(newTileID);

                int randomizedElevation = noiseMap[row][column];
                newTile.addSolid(foundation, 1);
                newTile.addSolid(terrain, randomizedElevation);

                int currentElevation = newTile.getModifiedElevation();
                if (currentElevation < liquidLevel) {
                    newTile.addLiquid(liquidAsset, liquidLevel - randomizedElevation);
                }

                if (mRandom.nextFloat() < .1 && !newTile.isTopLayerLiquid()) {
                    String structureName = structures.getFirst();
                    String structureID = EntityStore.getInstance().getOrCreateStructure(structureName);
                    newTile.addStructure(structureID);

//                    Entity structureEntity = getEntityWithID(structureID);
//                    AssetComponent assetComponent = structureEntity.get(AssetComponent.class);
//                    assetComponent.putStructureID(structureID);
                }

                mTileEntityMap[row][column] = newTileEntity;
                mTileIDMap[row][column] = newTileID;

                jsonRow.add(newTile);
            }
            add(jsonRow);
        }
    }

    private void loadMapFromJson(JSONArray mapData) {
        addAll(mapData);
        int rows = size();
        JSONArray jsonRow = (JSONArray) get(0);
        int columns = jsonRow.size();
        mTileEntityMap = new Entity[rows][columns];
//        for (int row = 0; row < length(); row++) {
//            JSONArray rowArray = (JSONArray) get(row);
//            for (int column = 0; column < rowArray.length(); column++) {
//
//                JSONObject data = (JSONObject) rowArray.get(column);
//
//                String id = EntityStore.getInstance().getOrCreateTile(data);
//                Entity tileEntity = EntityStore.getInstance().get(id);
//
//                Tile tile = tileEntity.get(Tile.class);
//                rowArray.put(column, tile);
//                mRawMap[row][column] = tileEntity;
//            }
//        }
    }


    public TileMap(JSONObject JSONObject) {
        mTileEntityMap = fromJson(JSONObject);
    }

    public void reset() {
        mLogger.info("Started setting up TileMap");

//        if (mIterations > 1) {
//            mTileMapParameters.put(TileMapParameters.SEED_KEY, mRandom.nextLong());
//        }
//
//        long seed = (long) mTileMapParameters.get(TileMapParameters.SEED_KEY);
//        mRandom.setSeed(seed);
//
//        int rows = (int) mTileMapParameters.get(TileMapParameters.ROWS_KEY);
//        int columns = (int) mTileMapParameters.get(TileMapParameters.COLUMNS_KEY);
//        float zoom = (float) mTileMapParameters.get(TileMapParameters.NOISE_ZOOM_KEY);
//        int minHeight = (int) mTileMapParameters.get(TileMapParameters.MIN_TERRAIN_HEIGHT_KEY);
//        int maxHeight = (int) mTileMapParameters.get(TileMapParameters.MAX_TERRAIN_HEIGHT_KEY);
//
//        mRawMap = new Entity[rows][columns];
//        int[][] heightMap = applySimplexNoise(rows, columns, minHeight, maxHeight, zoom);
//        for (int row = 0; row < rows; row++) {
//            for (int column = 0; column < columns; column++) {
//                int height = heightMap[row][column];
//                Entity entity = TileFactory.create(
//                        row,
//                        column,
//                        null,
//                        height,
//                        null,
//                        null
//                );
//                mRawMap[row][column] = entity;
//            }
//        }

//        applySimplexNoise(mTileMapLayers.get(HEIGHT_LAYER), minHeight, maxHeight, zoom);
        mLogger.info("Finished setting up TileMap");
    }

    public void designateSpawns(boolean refresh) {
        if (!refresh) { return; }

        int rows = getRows();;
        int columns = getColumns();
        int maxSpawnAreaColumns = (int) Math.max(2, getColumns() * .05);

        String leftRegionName = "Left";
        String rightRegionName = "Right";
        List<String> leftRegion = new ArrayList<>();
        List<String> rightRegion = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {

                String entityID = null;

                if (column < maxSpawnAreaColumns) {
                    entityID = tryFetchingEntityIDAt(row, column);
                    Entity entity = getEntityWithID(entityID);
                    TileComponent tile = entity.get(TileComponent.class);
                    tile.deleteStructure();
                    tile.setSpawnRegion(leftRegionName);
                    leftRegion.add(entityID);
                }

                if (column >= columns - maxSpawnAreaColumns) {
                    entityID = tryFetchingEntityIDAt(row, column);
                    Entity entity = getEntityWithID(entityID);
                    TileComponent tile = entity.get(TileComponent.class);
                    tile.deleteStructure();
                    tile.setSpawnRegion(rightRegionName);
                    rightRegion.add(entityID);

                }
            }
        }
        mSpawnRegions.clear();
        mSpawnRegions.put(leftRegionName, leftRegion);
        mSpawnRegions.put(rightRegionName, rightRegion);
    }

    public JSONObject getSpawnRegionsData() {
        JSONObject result = new JSONObject();
        if (mSpawnRegions.isEmpty()) {
            return result;
        }

        for (Map.Entry<String, List<String>> entries : mSpawnRegions.entrySet()) {
            String region = entries.getKey();
            List<String> tileIDs = entries.getValue();
            JSONArray regionTiles = new JSONArray();
            for (String tileID : tileIDs) {
                Entity entity = getEntityWithID(tileID);
                TileComponent tileComponent = entity.get(TileComponent.class);
                tileComponent.deleteStructure();
                tileComponent.removeUnit();
                int row = tileComponent.getRow();
                int column = tileComponent.getColumn();
                JSONObject tileData = new JSONObject();
                tileData.put("tile_id", tileID);
                tileData.put("row", row);
                tileData.put("column", column);
                regionTiles.add(tileData);
            }
            result.put(region, regionTiles);
        }

        return result;
    }

    public JSONArray getSpawnRegions() {
        JSONArray result = new JSONArray();
        result.addAll(mSpawnRegions.keySet());
        return result;
    }



    private static int[][] applySimplexNoise(int rows, int columns, int minHeight, int maxHeight, float zoom) {
        SimplexNoise simplexNoise = new SimplexNoise();
        double[][] map = simplexNoise.get2DNoiseMap(rows, columns, zoom);
        int[][] heightMap = new int[rows][columns];
        for (int row = 0; row < map.length; row++) {
            for (int column = 0; column < map[row].length; column++) {
                double val = map[row][column];
                int mapped = (int) MathUtils.map((float) val, 0, 1, minHeight, maxHeight);
                heightMap[row][column] = mapped;
            }
        }
        return heightMap;
    }

    public Entity tryFetchingEntityAt(int row, int column) {
        if (row < 0 || column < 0 || row >= mTileEntityMap.length || column >= mTileEntityMap[row].length) {
            return null;
        } else {
            return mTileEntityMap[row][column];
        }
    }
    public TileComponent tryFetchingTileAt(int row, int column) {
        if (row < 0 || column < 0) { return null; }
        if (row >= size()) { return null; }
        JSONArray jsonRow = (JSONArray) get(row);
        if (column >= jsonRow.size()) { return null; }
        return (TileComponent) jsonRow.get(column);
    }

    public String tryFetchingEntityIDAt(int row, int column) {
        Entity tileEntity = tryFetchingEntityAt(row, column);
        if (tileEntity == null) { return null; }
        IdentityComponent identityComponent = tileEntity.get(IdentityComponent.class);
        String id = identityComponent.getID();
        return id;
    }

//    @Override
//    public JSONObject toJsonObject() {
//        JSONArray rows = new JSONArray();
//        // Add all cells of the tile to a json structure
//        for (Entity[] row : mRawMap) {
//            JSONArray JSONArray = new JSONArray();
//            for (Entity column : row) {
//                Tile tile = column.get(Tile.class);
//                JSONArray.add(tile.asJson());
//            }
//            rows.add(JSONArray);
//        }
//        mJsonData.put(getClass().getSimpleName().toLowerCase(Locale.ROOT), rows);
//        return mJsonData;
//    }

    private Entity[][] fromJson(JSONObject JSONObject) {
        JSONArray tileMapJson = (JSONArray) JSONObject.get(getClass().getSimpleName().toLowerCase(Locale.ROOT));
        Entity[][] newEntityArray = new Entity[tileMapJson.size()][];
//        for (int row = 0; row < tileMapJson.length(); row++) {
//            JSONArray jsonRow = (JSONArray) tileMapJson.get(row);
//            Entity[] entityRowArray = new Entity[jsonRow.length()];
//            for (int column = 0; column < jsonRow.length(); column++) {
//                JSONObject tileObject = (JSONObject) jsonRow.get(column);
//                String id = EntityStore.getInstance().getOrCreateTile(tileObject);
//                Entity entity = EntityStore.getInstance().get(id);
//                entityRowArray[column] = entity;
//            }
//            newEntityArray[row] = entityRowArray;
//        }
        return newEntityArray;
    }

    public boolean spawnUnit(Entity entity, int row, int column) {
        // Get the tile to place the entity on
        String tileEntityID = tryFetchingEntityIDAt(row, column);
        Entity tileEntity = EntityStore.getInstance().get(tileEntityID);
        if (tileEntity == null) { return false; }
        TileComponent tile = tileEntity.get(TileComponent.class);
        if (tile.isNotNavigable()) { return false; }

        IdentityComponent identityComponent = entity.get(IdentityComponent.class);
        String unitID = identityComponent.getID();

        tile.setUnit(unitID);

        MovementComponent movementComponent = entity.get(MovementComponent.class);
        movementComponent.stageTarget(tileEntityID);
        movementComponent.commit();
        return true;
    }

    public void placeByAxis(boolean horizontal, List<Entity> leftOrTopTeam, List<Entity> rightOrBottom, int spawnSize) {
        // split the map into regions.This will look odd when the map cannot be devided evenly
        // left most and right most
        List<Entity> leftOrTopSpawns = new ArrayList<>();
        List<Entity> rightOrBottomSpawns = new ArrayList<>();
        for (int row = 0; row < mTileEntityMap.length; row++) {
            for (int column = 0; column < mTileEntityMap[row].length; column++) {
                // Left most team
                Entity tileEntity = mTileEntityMap[row][column];
                TileComponent tile = tileEntity.get(TileComponent.class);
                if (horizontal) {
                    if (column < spawnSize) {
                        leftOrTopSpawns.add(tileEntity);
                        tile.setSpawnRegion(1);
                    } else if (column >= mTileEntityMap[row].length - spawnSize) {
                        rightOrBottomSpawns.add(tileEntity);
                        tile.setSpawnRegion(2);
                    }
                } else {
                    if (row < spawnSize) {
                        leftOrTopSpawns.add(tileEntity);
                        tile.setSpawnRegion(1);
                    } else if (row >= mTileEntityMap.length - spawnSize) {
                        rightOrBottomSpawns.add(tileEntity);
                        tile.setSpawnRegion(2);
                    }
                }
            }
        }
    }
//    public void placeGroupVsGroup(List<Entity> team1, List<Entity> team2) {
//        placeGroupVsGroup(team1, team2, true);
//    }
//
//    public void placeGroupVsGroup(List<Entity> team1, List<Entity> team2, boolean ignoreObstructed) {
//        // split the map into regions. This will look odd when the map cannot be devided evenly
//        List<List<List<Entity>>> regionMap = tryCreatingRegions(3, ignoreObstructed);
//        // Set team spawns in West most regions and East most regions OR
//        // set the spawns in North most regions and South most regions
//        List<List<Entity>> spawns = tryGettingOpposingRegions(regionMap, true);
//        Queue<Entity> queue;
//
//        if (team1 != null) {
//            queue = new LinkedList<>(team1);
//            while (!queue.isEmpty()) {
//                Entity unitEntity = queue.poll();
//                Entity tileEntity = spawns.get(0).get(mRandom.nextInt(spawns.get(0).size()));
//                Tile tile = tileEntity.get(Tile.class);
//                while (tile.isNotNavigable()) {
//                    tileEntity = spawns.get(0).get(mRandom.nextInt(spawns.get(0).size()));
//                    tile = tileEntity.get(Tile.class);
//                }
//                tile.setUnit(unitEntity);
//            }
//        }
//
//        if (team2 != null) {
//            queue = new LinkedList<>(team2);
//            while (!queue.isEmpty()) {
//                Entity unitEntity = queue.poll();
//                Entity tileEntity = spawns.get(spawns.size() - 1).get(mRandom.nextInt(spawns.get(spawns.size() - 1).size()));
//                Tile tile = tileEntity.get(Tile.class);
//                while (tile.isNotNavigable()) {
//                    tileEntity = spawns.get(spawns.size() - 1).get(mRandom.nextInt(spawns.get(spawns.size() - 1).size()));
//                    tile = tileEntity.get(Tile.class);
//                }
//                tile.setUnit(unitEntity);
//            }
//        }
//    }

//    public void createLeftAndRightSpawnRegions() {
//        // At minimum use 3 columns for each spawn
//        // At maximum, use 20
//        int spawnSize = (int) Math.max(3, mRawMap[0].length * .2);
//        List<Entity> leftSide = new ArrayList<>();
//        List<Entity> rightSide = new ArrayList<>();
//        for (int row = 0; row < mRawMap.length; row++) {
//            for (int column = 0; column < mRawMap[row].length; column++) {
//                Entity tileEntity = tryFetchingEntityAt(row, column);
//                Tile tile = tileEntity.get(Tile.class);
//                // Side 1
//                if (column < spawnSize) {
//                    tile.set(Tile.SPAWNERS, 0);
//                    leftSide.add(tileEntity);
//                    // Side 2
//                } else if (column >= mRawMap[row].length - spawnSize) {
//                    tile.set(Tile.SPAWNERS, 1);
//                    rightSide.add(tileEntity);
//                }
//            }
//        }
//
//        mSpawnRegions.put(0, leftSide);
//        mSpawnRegions.put(1, rightSide);
//    }

//    private List<List<Entity>> tryGettingOpposingRegions(List<List<List<Entity>>> regionMap, boolean useColumns) {
//        List<Entity> side1 = new ArrayList<>();
//        List<Entity> side2 = new ArrayList<>();
//        int allocation = 1;
//        int regionVal = 1;
//        // column wise only atm
//        for (int row = 0; row < regionMap.size(); row++) {
//            for (int column = 0; column < regionMap.get(row).size(); column++) {
//                if (useColumns) {
//                    // The region
//                    if (column < allocation) {
//                        List<Entity> region = regionMap.get(row).get(column);
//                        for (Entity entity : region) {
//                            Tile tile = entity.get(Tile.class);
//                            tile.setSpawnRegion(regionVal);
//                            side1.add(entity);
//                        }
//                    } else if (column > regionMap.get(row).size() - allocation - 1) {
//                        List<Entity> region = regionMap.get(row).get(column);
//                        for (Entity entity : region) {
//                            Tile tile = entity.get(Tile.class);
//                            tile.setSpawnRegion(regionVal + 1);
//                            side2.add(entity);
//                        }
//                    }
//                } else {
//                    // The region
//                    if (row < allocation) {
//                        List<Entity> region = regionMap.get(row).get(column);
//                        for (Entity entity : region) {
//                            Tile tile = entity.get(Tile.class);
//                            tile.setSpawnRegion(regionVal);
//                            side1.add(entity);
//                        }
//                    } else if (row > regionMap.get(row).size() - allocation - 1) {
//                        List<Entity> region = regionMap.get(row).get(column);
//                        for (Entity entity : region) {
//                            Tile tile = entity.get(Tile.class);
//                            tile.setSpawnRegion(regionVal + 1);
//                            side2.add(entity);
//                        }
//                    }
//                }
//            }
//        }
//        List<List<Entity>> oppositions = new ArrayList<>();
//        oppositions.add(side1);
//        oppositions.add(side2);
//        return oppositions;
//    }

    public boolean placeByDivision(int slices, int location, List<Entity> team) {
        return placeByDivision(slices, location, team, true);
    }

    public boolean placeByDivision(int slices, int location, List<Entity> team, boolean randomized) {

        mLogger.info("Placing Entities onto tile map");
        // Divide map by NxN
//        Map<Integer, List<Entity>> areas = tryCollectingRegions(slices);
//
//        List<Entity> area = areas.get(location);
//        if (team.size() > area.size()) { return false; }
//
//        if (randomized) {
//            for (Entity value : team) {
//                Tile tile = area.get(mRandom.nextInt(area.size())).get(Tile.class);
//                while (tile.isNotNavigable()) {
//                    tile = area.get(mRandom.nextInt(area.size())).get(Tile.class);
//                }
//                tile.setUnit(value);
//            }
//        } else {
//            int unit = 0;
//            for (Entity entity : area) {
//                Tile tile = entity.get(Tile.class);
//
//                if (tile.isNotNavigable()) { continue; }
//                if (unit >= team.size()) { continue; }
//
//                tile.setUnit(team.get(unit));
//                unit++;
//            }
//        }
        mLogger.info("Finished Placing Entities onto tile map");
        return true;
    }

    public Entity setSpawnRegion(String region, int row, int column) {
        Entity tileEntity = tryFetchingEntityAt(row, column);
        TileComponent tile = tileEntity.get(TileComponent.class);
        tile.set(TileComponent.SPAWN_REGION, region);
        return tileEntity;
    }

    public List<Entity> setSpawnRegion(String region, int startingRow, int statingColumn, int width, int height) {
        List<Entity> spawnRegion = new ArrayList<>();
        for (int row = startingRow; row < startingRow + height; row++) {
            for (int column = statingColumn; column < statingColumn + width; column++) {
                Entity tileEntity = setSpawnRegion(region, row, column);
                spawnRegion.add(tileEntity);
            }
        }
        return spawnRegion;
    }

    /**
     *
     * rowsByColumns = 1
     *  0   |   1
     *  ----+-----
     *  2   |   3
     *  ==================================
     * rowsByColumns = 2
     *  0   |   1    |   2
     *  ----+--------+-----
     *  3   |   4    |   6
     *  ----+--------+-----
     *  6   |   7    |   8
     *
     */

    public List<List<List<Entity>>> tryCreatingRegions(int slicesPerAxis, boolean ignoreObstructed) {

        List<List<List<Entity>>> regions = new ArrayList<>();

        if (slicesPerAxis <= 0) { return regions; }

        slicesPerAxis++;

        int tileRowsPerRegion = mTileEntityMap.length / slicesPerAxis;
        int leftoverRowValue = (mTileEntityMap.length % slicesPerAxis != 0 ? 1 : 0);

        for (int row = 0; row < mTileEntityMap.length; row += tileRowsPerRegion + leftoverRowValue) {
            List<List<Entity>> rowList = new ArrayList<>();
            int tileColumnsPerRegion = mTileEntityMap[row].length / slicesPerAxis;
            int leftoverColumnValue = (mTileEntityMap[row].length % slicesPerAxis != 0 ? 1 : 0);

            for (int column = 0; column < mTileEntityMap[row].length; column += tileColumnsPerRegion + leftoverColumnValue) {
                int regionEndRow = row + tileRowsPerRegion + leftoverRowValue;
                int regionEndColumn = column + tileColumnsPerRegion + leftoverColumnValue;
                List<Entity> area = new ArrayList<>();

                for (int regionRow = row; regionRow < regionEndRow; regionRow++) {
                    for (int regionColumn = column; regionColumn < regionEndColumn; regionColumn++) {
                        Entity entity = tryFetchingEntityAt(regionRow, regionColumn);
                        if (entity == null) { continue; }
                        TileComponent tile = entity.get(TileComponent.class);
                        area.add(entity);
                        if (ignoreObstructed && tile.isNotNavigable()) { continue; }
                    }
                }
                // This means that the regions are way too small
                if (area.isEmpty()) { return regions; }

                rowList.add(area);
            }
            regions.add(rowList);
        }

//        for (List<Entity> spawnRegion : regions) {
//
//        }
        return regions;
    }

    public int getRows() { return mTileEntityMap.length; }
    public int getColumns(int row) { return mTileEntityMap[row].length; }
    public int getColumns() { return getColumns(0); }

    public boolean isOutOfBounds(int row, int column) {
        boolean isHorizontallyOutOfBounds = row < 0 || row >= mTileEntityMap.length;
        if (isHorizontallyOutOfBounds) { return true; }
        boolean isVerticallyOutOfBounds = column < 0 || column >= mTileEntityMap[row].length;
        return isVerticallyOutOfBounds;
    }

    public void clear(String layer, int row, int column) {
        mTileEntityMap[row][column].get(TileComponent.class).clear(layer);
    }

    public void fill(String layer, Object filament) {
        for (int row = 0; row < mTileEntityMap.length; row++) {
            for (int column = 0; column < mTileEntityMap[row].length; column++) {
                set(layer, row, column, filament);
            }
        }
    }

    public boolean isUsed(String layer, int row, int column) {
        Entity entity = tryFetchingEntityAt(row, column);
        TileComponent tile = entity.get(TileComponent.class);
        return tile.get(layer) != null;
    }
    public boolean isNotUsed(String layer, int row, int column) {
        return !isUsed(layer, row, column);
    }

    public void set(String layer, int row, int column, Object value) {
        Entity entity = mTileEntityMap[row][column];
        TileComponent tile = entity.get(TileComponent.class);
        tile.set(layer, value);
    }

    public Object get(String layer, int row, int column) {
        Entity entity = tryFetchingEntityAt(row, column);
        TileComponent tile = entity.get(TileComponent.class);
        return tile.get(layer);
    }

    public String getFloor() { return (String) mTileMapParameters.get(TileMapParameters.FLOOR_KEY); }
    public String getWall() { return (String) mTileMapParameters.get(TileMapParameters.WALL_KEY); }
    public String getLiquid() { return (String) mTileMapParameters.get(TileMapParameters.LIQUID_KEY); }
    public int getWaterLevel() { return (int) mTileMapParameters.get(TileMapParameters.WATER_LEVEL_KEY); }
    public long getSeed() { return (long) mTileMapParameters.get(TileMapParameters.SEED_KEY); }
    public Object getConfiguration(String config) { return mTileMapParameters.get(config); }
    public List<String> getAllTileEntityIDs() { return mTileEntityIDs; }
    public Entity getEntityWithID(String id) { return EntityStore.getInstance().get(id); }
}
