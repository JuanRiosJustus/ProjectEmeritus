package main.game.map.base;

import main.constants.Direction;
import main.game.components.IdentityComponent;
import main.game.main.GameConfigs;
import main.game.stores.EntityStore;
import main.logging.EmeritusLogger;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import main.game.components.tile.TileComponent;
import main.game.entity.Entity;
import main.graphics.AnimationPool;

import main.utils.MathUtils;
import main.utils.noise.SimplexNoise;

import java.util.*;

public class TileMap extends JSONArray {
    protected static final EmeritusLogger mLogger = EmeritusLogger.create(TileMapBuilder.class);
//    private Entity[][] mTileEntityMap;
//    private String[][] mTileIDMap;
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

    public TileMap() { }


    /**
     * Generates a 2D tile grid based on the specified request parameters, applying layered elevation,
     * terrain, foundation, and optional liquid levels. This method is responsible for constructing each
     * tile entity and assigning its visual and logical components.
     *
     * <p><b>Expected request fields:</b>
     * <ul>
     *   <li><b>"rows"</b> (int): Number of rows in the tile grid</li>
     *   <li><b>"columns"</b> (int): Number of columns in the tile grid</li>
     *   <li><b>"foundation_asset"</b> (String, optional): Base layer asset for all tiles; random if unspecified</li>
     *   <li><b>"foundation_depth"</b> (int, optional): Number of foundation layers to apply to each tile (default: 3)</li>
     *   <li><b>"liquid_asset"</b> (String, optional): Asset used for water or liquid layers</li>
     *   <li><b>"liquid_elevation"</b> (int, optional): Tiles with elevation below this value get liquid layers (default: 4)</li>
     *   <li><b>"terrain_asset"</b> (String, optional): Main terrain asset layered above the foundation; random if unspecified</li>
     *   <li><b>"lower_terrain_elevation"</b> (int): Minimum terrain height generated via noise</li>
     *   <li><b>"upper_terrain_elevation"</b> (int): Maximum terrain height generated via noise</li>
     *   <li><b>"terrain_elevation_noise"</b> (float): Controls amplitude or frequency of elevation noise</li>
     *   <li><b>"terrain_elevation_noise_seed"</b> (long, optional): Seed for consistent noise generation (default: 0)</li>
     * </ul>
     *
     * <p><b>Tile construction logic:</b>
     * <ul>
     *   <li>Each tile receives a foundation layer of the given asset and depth</li>
     *   <li>Terrain elevation is computed using simplex noise and layered accordingly</li>
     *   <li>If elevation is below the liquid threshold, a liquid layer is applied</li>
     * </ul>
     *
     * <p>This method populates:
     * <ul>
     *   <li>{@code mTileIDMap}: a 2D array of tile entity IDs</li>
     *   <li>{@code mTileEntityIDs}: a flat list of all created tile IDs</li>
     * </ul>
     *
     * @param request a {@link JSONObject} containing generation parameters for the tile map
     * @return {@code null} (the constructed tiles are added to the internal map structure)
     */
    public JSONObject generate(JSONObject request) {
        int rows = request.getIntValue("rows", 0);
        int columns = request.getIntValue("columns", 0);

        List<String> floors = AnimationPool.getInstance().getFloorTileSets();
        List<String> liquids = AnimationPool.getInstance().getLiquidTileSets();
        Random random = new Random();

        String foundationAsset = request.getString("foundation_asset");
        if (foundationAsset == null) { foundationAsset = floors.get(random.nextInt(floors.size())); }
        int foundationDepth = request.getIntValue("foundation_depth", 3);

        String liquidAsset = request.getString("liquid_asset");
        if (liquidAsset == null) { liquidAsset = liquids.get(random.nextInt(liquids.size())); }
        int liquidElevation = request.getIntValue("liquid_elevation", 4);

        String terrainAsset = request.getString("terrain_asset");
        if (terrainAsset == null) { terrainAsset = floors.get(random.nextInt(floors.size())); }
        int lowerTerrainElevation = request.getIntValue("lower_terrain_elevation", 3);
        int upperTerrainElevation = request.getIntValue("upper_terrain_elevation", 6);
        float terrainElevationNoise = request.getFloatValue("terrain_elevation_noise");
        long terrainElevationSeed = request.getLongValue("terrain_elevation_noise_seed", 0);


        int[][] noiseMap = null;
        if (terrainElevationSeed == 0) {
            noiseMap = applySimplexNoise(rows, columns, lowerTerrainElevation, upperTerrainElevation, terrainElevationNoise, mRandom.nextLong());
        } else {
            noiseMap = applySimplexNoise(rows, columns, lowerTerrainElevation, upperTerrainElevation, terrainElevationNoise, terrainElevationSeed);
        }

        List<String> list = AnimationPool.getInstance().getFloorTileSets();
        if (terrainAsset == null || terrainAsset.isEmpty()) {
            terrainAsset = list.get(mRandom.nextInt(list.size()));
        }

        for (int row = 0; row < rows; row++) {
            JSONArray jsonRow = new JSONArray();
            for (int column = 0; column < columns; column++) {

                String newTileID = EntityStore.getInstance().createTile(row, column);
                Entity newTileEntity = EntityStore.getInstance().get(newTileID);
                TileComponent newTileComponent = newTileEntity.get(TileComponent.class);
                mTileEntityIDs.add(newTileID);

                // Foundation as the starting point
                newTileComponent.addSolid(foundationAsset, foundationDepth);

                // Apply simplex noise only if the configuration is enabled
                int additionalElevation = lowerTerrainElevation;
                if (terrainElevationNoise >= 0) {
                    additionalElevation = (int) noiseMap[row][column];
                }
                newTileComponent.addSolid(terrainAsset, additionalElevation);

                // Add liquid if possible
                int currentElevation = additionalElevation;
                int liquidLayers = Math.abs(liquidElevation - currentElevation);
                if (liquidElevation >= 0 && currentElevation < liquidElevation && liquidLayers > 0) {
                    newTileComponent.addLiquid(liquidAsset, liquidLayers);
                }

                jsonRow.add(newTileID);
            }
            add(jsonRow);
        }

        return null;
    }

    private void createMapFromConfigs(GameConfigs configs) {
        int rows = configs.getMapGenerationRows();
        int columns = configs.getMapGenerationColumns();

        String foundationAsset = configs.getMapGenerationFoundationAsset();
        int foundationDepth = configs.getMapGenerationFoundationDepth();

        String liquidAsset = configs.getLiquidAsset();
        int liquidElevation = configs.getMapGenerationLiquidElevation();
        boolean isLiquidElevationEnabled = configs.isMapGenerationLiquidElevationEnabled();


        String terrainAsset = configs.getMapGenerationTerrainAsset();
        int terrainStartingElevation = configs.getMapGenerationTerrainStartingElevation();
        int terrainEndingElevation = configs.getMapGenerationTerrainEndingElevation();

        float noiseZoom = configs.getMapGenerationHeightNoise();
        int[][] noiseMap = applySimplexNoise(
                rows,
                columns,
                terrainStartingElevation,
                terrainEndingElevation,
                noiseZoom,
                mRandom.nextLong()
        );

        List<String> list = AnimationPool.getInstance().getFloorTileSets();
        if (terrainAsset == null || terrainAsset.isEmpty()) {
            terrainAsset = list.get(mRandom.nextInt(list.size()));
        }

        for (int row = 0; row < rows; row++) {
            JSONArray jsonRow = new JSONArray();
            for (int column = 0; column < columns; column++) {

                String newTileID = EntityStore.getInstance().createTile(row, column);
                Entity newTileEntity = EntityStore.getInstance().get(newTileID);

                TileComponent newTileComponent = newTileEntity.get(TileComponent.class);
                mTileEntityIDs.add(newTileID);

                // Foundation as the starting point
                newTileComponent.addSolid(foundationAsset, foundationDepth);

                // Apply simplex noise only if the configuration is enabled
                int randomizedElevation = noiseMap[row][column];
                if (noiseZoom >= 0) {
                    newTileComponent.addSolid(terrainAsset, randomizedElevation);
                } else {
                    newTileComponent.addSolid(terrainAsset, 1);
                }

                int currentElevation = newTileComponent.getTotalElevation();
                if (isLiquidElevationEnabled && currentElevation < liquidElevation) {
                    int liquidLayers = Math.abs(terrainEndingElevation - currentElevation);
                    if (liquidLayers != 0) { newTileComponent.addLiquid(liquidAsset, 1); }
//                    newTileComponent.addLiquid(liquidAsset, liquidLayers);
                }



//                if (structures != null && mRandom.nextFloat() < .1 && !newTileComponent.isTopLayerLiquid()) {
//                    String structureName = structures.getString(mRandom.nextInt(structures.size()));
//                    String structureID = EntityStore.getInstance().getOrCreateStructure(structureName);
//                    newTileComponent.putStructureID(structureID);
//
//                    Entity structureEntity = getEntityWithID(structureID);
//                    AssetComponent assetComponent = structureEntity.get(AssetComponent.class);
//                    assetComponent.putMainID(structureID);
//                }

//                mTileEntityMap[row][column] = newTileEntity;
//                mTileIDMap[row][column] = newTileID;

                jsonRow.add(newTileID);
            }
            add(jsonRow);
        }
    }

    private void loadMapFromJson(JSONArray mapData) {
        addAll(mapData);
        int rows = size();
        JSONArray jsonRow = (JSONArray) get(0);
        int columns = jsonRow.size();
//        mTileEntityMap = new Entity[rows][columns];
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
//        mTileEntityMap = fromJson(JSONObject);
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
                    entityID = tryFetchingTileID(row, column);
                    Entity entity = getEntityWithID(entityID);
                    if (entity == null) { return; }
                    TileComponent tile = entity.get(TileComponent.class);
                    tile.removetructure();
                    tile.setSpawnRegion(leftRegionName);
                    leftRegion.add(entityID);
                }

                if (column >= columns - maxSpawnAreaColumns) {
                    entityID = tryFetchingTileID(row, column);
                    Entity entity = getEntityWithID(entityID);
                    if (entity == null) { return; }
                    TileComponent tile = entity.get(TileComponent.class);
                    tile.removetructure();
                    tile.setSpawnRegion(rightRegionName);
                    rightRegion.add(entityID);

                }
            }
        }
        mSpawnRegions.clear();
        mSpawnRegions.put(leftRegionName, leftRegion);
        mSpawnRegions.put(rightRegionName, rightRegion);
    }

    public JSONObject getSpawnRegions() {
        JSONObject result = new JSONObject();

        for (int rowIndex = 0; rowIndex < size(); rowIndex++) {
            JSONArray row = getJSONArray(rowIndex);
            for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                String tileID = row.getString(columnIndex);
                Entity entity = getEntityWithID(tileID);
                if (entity == null) { continue; }

                TileComponent tileComponent = entity.get(TileComponent.class);
                String region = tileComponent.getSpawnRegion();

                if (region == null) { continue; }

                JSONArray regionList = result.getJSONArray(region);
                if (regionList == null) {
                    regionList = new JSONArray();
                    result.put(region, regionList);
                }

//                JSONObject spawnData = new JSONObject();
//                spawnData.put("row", rowIndex);
//                spawnData.put("column", columnIndex);
//                spawnData.put("tile_id", tileID);
                regionList.add(tileID);
            }
        }

//        for (Map.Entry<String, List<String>> entries : mSpawnRegions.entrySet()) {
//            String region = entries.getKey();
//            List<String> tileIDs = entries.getValue();
//            JSONArray regionTiles = new JSONArray();
//            for (String tileID : tileIDs) {
//                Entity entity = getEntityWithID(tileID);
//                TileComponent tileComponent = entity.get(TileComponent.class);
//                tileComponent.removetructure();
//                tileComponent.removeUnit();
//                int row = tileComponent.getRow();
//                int column = tileComponent.getColumn();
//                JSONObject tileData = new JSONObject();
//                tileData.put("tile_id", tileID);
//                tileData.put("row", row);
//                tileData.put("column", column);
//                regionTiles.add(tileData);
//            }
//            result.put(region, regionTiles);
//        }

        return result;
    }

    private static int[][] applySimplexNoise(int rows, int columns, int min, int max, float zoom, long seed) {
        SimplexNoise simplexNoise = new SimplexNoise(seed);
        double[][] map = simplexNoise.get2DNoiseMap(rows, columns, zoom);
        int[][] heightMap = new int[rows][columns];
        for (int row = 0; row < map.length; row++) {
            for (int column = 0; column < map[row].length; column++) {
                double val = map[row][column];
                int mapped = (int) MathUtils.map((float) val, 0, 1, min, max);
                heightMap[row][column] = mapped;
            }
        }
        return heightMap;
    }

//    private static float[][] applySimplexNoise(int rows, int columns, int minHeight, int maxHeight, float zoom, long seed) {
//        SimplexNoise simplexNoise = new SimplexNoise(seed);
//        double[][] map = simplexNoise.get2DNoiseMap(rows, columns, zoom);
//        float[][] heightMap = new float[rows][columns];
//
//        for (int row = 0; row < map.length; row++) {
//            for (int column = 0; column < map[row].length; column++) {
//                float val = (float) map[row][column];
//                float mapped = MathUtils.map(val, 0, 1, minHeight, maxHeight);
//
//                // Round to nearest .0 or .5 only
//                float base = (float) Math.floor(mapped);
//                float diff = mapped - base;
//
//                float finalVal;
//                if (diff < 0.25f) {
//                    finalVal = base;
//                } else if (diff < 0.75f) {
//                    finalVal = base + 0.5f;
//                } else {
//                    finalVal = base + 1.0f;
//                }
//
//                heightMap[row][column] = finalVal;
//            }
//        }
//
//        return heightMap;
//    }

//    private static int[][] applySimplexNoise(int rows, int columns, int minHeight, int maxHeight, float zoom) {
//        SimplexNoise simplexNoise = new SimplexNoise();
//        double[][] map = simplexNoise.get2DNoiseMap(rows, columns, zoom);
//        int[][] heightMap = new int[rows][columns];
//        for (int row = 0; row < map.length; row++) {
//            for (int column = 0; column < map[row].length; column++) {
//                double val = map[row][column];
//                int mapped = (int) MathUtils.map((float) val, 0, 1, minHeight, maxHeight);
//
//
//
//                heightMap[row][column] = mapped;
//            }
//        }
//        return heightMap;
//    }

    private Entity tryFetchingTileEntityRaw(int row, int column) {
        if (row < 0 || column < 0 || row >= size() || column >= getJSONArray(row).size()) {
            return null;
        } else {
            String id = getJSONArray(row).getString(column);
            Entity entity = getEntityWithID(id);
            return entity;
        }
    }

    public String tryFetchingTileID(int row, int column) {
        Entity tileEntity = tryFetchingTileEntityRaw(row, column);
        if (tileEntity == null) { return null; }
        IdentityComponent identityComponent = tileEntity.get(IdentityComponent.class);
        String id = identityComponent.getID();
        return id;
    }


//
    public Map<String, String> createDirectedGraph(String startTileID, int range, boolean respectfully) {
        Map<String, Integer> depthMap = new HashMap<>();
        depthMap.put(startTileID, 0);

        Map<String, String> graphMap = new HashMap<>();
        graphMap.put(startTileID, null);

        Queue<String> toVisit = new LinkedList<>();
        toVisit.add(startTileID);

        Set<String> visited = new HashSet<>();

        while (!toVisit.isEmpty()) {
            // get the tile and its depth
            String currentTileID = toVisit.poll();

            Entity currentTileEntity = getEntityWithID(currentTileID);
            TileComponent currentTile = currentTileEntity.get(TileComponent.class);
            int depth = depthMap.get(currentTileID);

            // check that we have not visited already and is within range
            if (visited.contains(currentTileID)) { continue; }
            visited.add(currentTileID);

            // only go the specified range
            if (range >= 0 && depth >= range) { continue; }

            // go through each child tile and set connection
            for (Direction direction : Direction.cardinal) {
                int row = currentTile.getRow() + direction.y;
                int column = currentTile.getColumn() + direction.x;
                String adjacentTileID = tryFetchingTileID(row, column);

                // skip tiles off the map or already visited
                if (visited.contains(adjacentTileID)) { continue; }

                // If building graph for movement, don't traverse over obstructed tiles
                Entity adjacentTileEntity = getEntityWithID(adjacentTileID);
                if (adjacentTileEntity == null) { continue; }
                TileComponent adjacentTile = adjacentTileEntity.get(TileComponent.class);
                if (respectfully && adjacentTile.isNotNavigable()) { continue; }

                toVisit.add(adjacentTileID);
                graphMap.put(adjacentTileID, currentTileID);
                depthMap.put(adjacentTileID, depth + 1);
            }
        }


//        printElevationGrid(new ArrayList<>(graph.keySet()));
        return graphMap;
    }




//    public Map<String, Set<String>> createDirectedGraph(String startTileID, int range, boolean respectfully) {
//        Map<String, Integer> depthMap = new HashMap<>();
//        depthMap.put(startTileID, 0);
//
//        Map<String, String> graphMap = new HashMap<>();
//        graphMap.put(startTileID, null);
//
//        Map<String, Set<String>> graph = new LinkedHashMap<>();
//        graph.put(startTileID, new LinkedHashSet<>());
//
//        Queue<String> toVisit = new LinkedList<>();
//        toVisit.add(startTileID);
//
//        Set<String> visited = new HashSet<>();
//
//        while (!toVisit.isEmpty()) {
//            // get the tile and its depth
//            String currentTileID = toVisit.poll();
//
//            Entity currentTileEntity = getEntityWithID(currentTileID);
//            TileComponent currentTile = currentTileEntity.get(TileComponent.class);
//            int depth = depthMap.get(currentTileID);
//
//            // check that we have not visited already and is within range
//            if (visited.contains(currentTileID)) { continue; }
//            visited.add(currentTileID);
//
//            // only go the specified range
//            if (range >= 0 && depth > range) { continue; }
//
//            // go through each child tile and set connection
//            for (Direction direction : Direction.cardinal) {
//                int row = currentTile.getRow() + direction.y;
//                int column = currentTile.getColumn() + direction.x;
//                String adjacentTileID = tryFetchingTileEntity(row, column);
//
//                // skip tiles off the map or already visited
//                if (visited.contains(adjacentTileID)) { continue; }
//
//                // If building graph for movement, don't traverse over obstructed tiles
//                Entity adjacentTileEntity = getEntityWithID(adjacentTileID);
//                if (adjacentTileEntity == null) { continue; }
//                TileComponent adjacentTile = adjacentTileEntity.get(TileComponent.class);
//                if (respectfully && adjacentTile.isNotNavigable()) { continue; }
//
//                Set<String> connections = graph.computeIfAbsent(currentTileID, k -> new LinkedHashSet<>());
//                connections.add(adjacentTileID);
//
//                printElevationGrid(new ArrayList<>(graph.keySet()));
//
//                toVisit.add(adjacentTileID);
//                graphMap.put(adjacentTileID, currentTileID);
//                depthMap.put(adjacentTileID, depth + 1);
//            }
//        }
//
//
////        printElevationGrid(new ArrayList<>(graph.keySet()));
//        return graph;
//    }

//    public Map<String, Set<String>> createDirectedGraph2(String startTileID, int range, boolean respectfully) {
//        Map<String, Integer> depthMap = new HashMap<>();
//        depthMap.put(startTileID, 0);
//
//        Map<String, String> graphMap = new HashMap<>();
//        graphMap.put(startTileID, null);
//
////        Map<String, Set<String>> graph = new LinkedHashMap<>();
////        graph.put(startTileID, new LinkedHashSet<>());
//
//        Queue<String> toVisit = new LinkedList<>();
//        toVisit.add(startTileID);
//
//        Set<String> visited = new HashSet<>();
//
//        while (!toVisit.isEmpty()) {
//            // get the tile and its depth
//            String currentTileID = toVisit.poll();
//
//            Entity currentTileEntity = getEntityWithID(currentTileID);
//            TileComponent currentTile = currentTileEntity.get(TileComponent.class);
//            int depth = depthMap.get(currentTileID);
//
//            // check that we have not visited already and is within range
//            if (visited.contains(currentTileID)) { continue; }
//            visited.add(currentTileID);
//
//            // only go the specified range
//            if (range >= 0 && depth > range) { continue; }
//
//            // go through each child tile and set connection
//            for (Direction direction : Direction.cardinal) {
//                int row = currentTile.getRow() + direction.y;
//                int column = currentTile.getColumn() + direction.x;
//                String adjacentTileID = tryFetchingTileEntity(row, column);
//
//                // skip tiles off the map or already visited
//                if (visited.contains(adjacentTileID)) { continue; }
//
//                // If building graph for movement, don't traverse over obstructed tiles
//                Entity adjacentTileEntity = getEntityWithID(adjacentTileID);
//                if (adjacentTileEntity == null) { continue; }
//                TileComponent adjacentTile = adjacentTileEntity.get(TileComponent.class);
//                if (respectfully && adjacentTile.isNotNavigable()) { continue; }
//
////                Set<String> connections = graph.computeIfAbsent(currentTileID, k -> new LinkedHashSet<>());
////                connections.add(adjacentTileID);
////
////                printElevationGrid(new ArrayList<>(graph.keySet()));
//
//                toVisit.add(adjacentTileID);
//                graphMap.put(adjacentTileID, currentTileID);
//                depthMap.put(adjacentTileID, depth + 1);
//            }
//        }
//
//
////        printElevationGrid(new ArrayList<>(graph.keySet()));
//        return graph;
//    }


    public List<String> computeAreaOfSight(String startID, int range, boolean respectfully) {
        Set<String> visibleTiles = new LinkedHashSet<>();

        Entity start = getEntityWithID(startID);
        // Validate the starting entity
        if (start == null) { return new ArrayList<>(); }

        // Retrieve the starting tile
        TileComponent originTile = start.get(TileComponent.class);

        // Ensure the starting tile is valid
        if (originTile == null) { return new ArrayList<>(); };

        // Get the row and column indices for the starting tile
        int originRow = originTile.getRow();
        int originColumn = originTile.getColumn();

        // Iterate over all possible tiles within the diamond-shaped Manhattan range
        for (int rowOffset = -range; rowOffset <= range; rowOffset++) {
            for (int columnOffset = -range + Math.abs(rowOffset); columnOffset <= range - Math.abs(rowOffset); columnOffset++) {
                // Compute the target row and column
                int targetRow = originRow + rowOffset;
                int targetColumn = originColumn + columnOffset;

                // Fetch the entity at the target row and column
//                Entity target = model.tryFetchingEntityAt(targetRow, targetColumn);
                String targetID = tryFetchingTileID(targetRow, targetColumn);
                if (targetID == null) { continue; }

                // Compute the line of sight to the target entity
                List<String> line = computeLineOfSight(startID, targetID, respectfully);

                // Add all tiles from the computed line to the visible set
                visibleTiles.addAll(line);
            }
        }

        return new ArrayList<>(visibleTiles);
    }



    public List<String> computeLineOfSight(String startTileID, String endTileID, boolean respectfully) {
        Set<String> line = new LinkedHashSet<>();

        // Validate input entities
        if (startTileID == null || endTileID == null) {
            return new ArrayList<>();
        }

        // Retrieve the starting and ending tiles
        Entity start = getEntityWithID(startTileID);
        Entity end = getEntityWithID(endTileID);

        TileComponent startTile = start.get(TileComponent.class);
        TileComponent endTile = end.get(TileComponent.class);

        // Get the row and column indices for start and end tiles
        int startRow = startTile.getRow();
        int startColumn = startTile.getColumn();
        int endRow = endTile.getRow();
        int endColumn = endTile.getColumn();

        // Calculate the Manhattan distance differences and the total steps
        int dRow = Math.abs(endRow - startRow);
        int dColumn = Math.abs(endColumn - startColumn);
        int totalSteps = dRow + dColumn;

        // Initialize the current row and column to the starting point
        int currentRow = startRow;
        int currentColumn = startColumn;

        // Determine the step direction for row and column traversal
        int rowStep = (endRow > startRow) ? 1 : -1;
        int columnStep = (endColumn > startColumn) ? 1 : -1;

        // Add the starting tile to the line of sight
        line.add(startTileID);

        // Use a for loop to step from the start to the end tile.
        for (int step = 0; step < totalSteps; step++) {
            // Prioritize movement based on which difference is larger.
            if (dRow >= dColumn && currentRow != endRow) {
                currentRow += rowStep;
                dRow--;
            } else if (currentColumn != endColumn) {
                currentColumn += columnStep;
                dColumn--;
            }

            String tileEntityID = tryFetchingTileID(currentRow, currentColumn);
            Entity tileEntity = getEntityWithID(tileEntityID);
            if (tileEntity == null) {
                continue;
            }

            // Add the current tile to the line of sight.
            line.add(tileEntityID);

            // If "respectfully" is true and the tile is not navigable, stop the traversal.
            TileComponent tile = tileEntity.get(TileComponent.class);
            if (respectfully && tile.isNotNavigable()) {
                break;
            }
        }

        return new ArrayList<>(line);
    }



    public JSONArray computeAreaOfSightJSON(String startID, int range, boolean respectfully) {
        Set<String> visibleTiles = new LinkedHashSet<>();

        Entity start = getEntityWithID(startID);
        // Validate the starting entity
        if (start == null) { return new JSONArray(); }

        // Retrieve the starting tile
        TileComponent originTile = start.get(TileComponent.class);

        // Ensure the starting tile is valid
        if (originTile == null) { return new JSONArray(); };

        // Get the row and column indices for the starting tile
        int originRow = originTile.getRow();
        int originColumn = originTile.getColumn();

        // Iterate over all possible tiles within the diamond-shaped Manhattan range
        for (int rowOffset = -range; rowOffset <= range; rowOffset++) {
            for (int columnOffset = -range + Math.abs(rowOffset); columnOffset <= range - Math.abs(rowOffset); columnOffset++) {
                // Compute the target row and column
                int targetRow = originRow + rowOffset;
                int targetColumn = originColumn + columnOffset;

                // Fetch the entity at the target row and column
//                Entity target = model.tryFetchingEntityAt(targetRow, targetColumn);
                String targetID = tryFetchingTileID(targetRow, targetColumn);
                if (targetID == null) { continue; }

                // Compute the line of sight to the target entity
                JSONArray line = computeLineOfSightJSON(startID, targetID, respectfully);

                // Add all tiles from the computed line to the visible set
                for (int i = 0; i < line.size(); i++) { visibleTiles.add(line.getString(i)); }
            }
        }

        return new JSONArray(visibleTiles);
    }

    public JSONArray computeLineOfSightJSON(String startTileID, String endTileID, boolean respectfully) {
        Set<String> line = new LinkedHashSet<>();

        // Validate input entities
        if (startTileID == null || endTileID == null) {
            return new JSONArray();
        }

        // Retrieve the starting and ending tiles
        Entity start = getEntityWithID(startTileID);
        Entity end = getEntityWithID(endTileID);

        TileComponent startTile = start.get(TileComponent.class);
        TileComponent endTile = end.get(TileComponent.class);

        // Get the row and column indices for start and end tiles
        int startRow = startTile.getRow();
        int startColumn = startTile.getColumn();
        int endRow = endTile.getRow();
        int endColumn = endTile.getColumn();

        // Calculate the Manhattan distance differences and the total steps
        int dRow = Math.abs(endRow - startRow);
        int dColumn = Math.abs(endColumn - startColumn);
        int totalSteps = dRow + dColumn;

        // Initialize the current row and column to the starting point
        int currentRow = startRow;
        int currentColumn = startColumn;

        // Determine the step direction for row and column traversal
        int rowStep = (endRow > startRow) ? 1 : -1;
        int columnStep = (endColumn > startColumn) ? 1 : -1;

        // Add the starting tile to the line of sight
        line.add(startTileID);

        // Use a for loop to step from the start to the end tile.
        for (int step = 0; step < totalSteps; step++) {
            // Prioritize movement based on which difference is larger.
            if (dRow >= dColumn && currentRow != endRow) {
                currentRow += rowStep;
                dRow--;
            } else if (currentColumn != endColumn) {
                currentColumn += columnStep;
                dColumn--;
            }

            String tileEntityID = tryFetchingTileID(currentRow, currentColumn);
            Entity tileEntity = getEntityWithID(tileEntityID);
            if (tileEntity == null) {
                continue;
            }

            // Add the current tile to the line of sight.
            line.add(tileEntityID);

            // If "respectfully" is true and the tile is not navigable, stop the traversal.
            TileComponent tile = tileEntity.get(TileComponent.class);
            if (respectfully && tile.isNotNavigable()) {
                break;
            }
        }

        return new JSONArray(line);
    }
//
//    public Map<String, String> createGraph(String startTileID, int range, boolean respectfully) {
//        Map<String, Integer> depthMap = new HashMap<>();
//        depthMap.put(startTileID, 0);
//
//        Map<String, String> graphMap = new HashMap<>();
//        graphMap.put(startTileID, null);
//
//        Queue<String> tileIDsToVisit = new LinkedList<>();
//        tileIDsToVisit.add(startTileID);
//
//        Set<String> visitedTileIDs = new HashSet<>();
//
//        while (!tileIDsToVisit.isEmpty()) {
//            // get the tile and its depth
//            String currentTileID = tileIDsToVisit.poll();
//            if (currentTileID == null) { continue; }
//
//            Entity currentTileEntity = getEntityWithID(currentTileID);
//            TileComponent currentTile = currentTileEntity.get(TileComponent.class);
//            int depth = depthMap.get(currentTileID);
//
//            // check that we have not visited already and is within range
//            if (visitedTileIDs.contains(currentTileID)) { continue; }
//            visitedTileIDs.add(currentTileID);
//
//            // If building graph for movement, don't traverse over obstructed tiles
//            if (currentTileID.equalsIgnoreCase(startTileID) && (respectfully && currentTile.isNotNavigable())) { continue; }
//
//            // only go the specified range
//            if (range >= 0 && depth >= range) { continue; }
//
//            // go through each child tile and set connection
//            for (Direction direction : Direction.cardinal) {
//                int row = currentTile.getRow() + direction.y;
//                int column = currentTile.getColumn() + direction.x;
//                String adjacentTileID = tryFetchingTileEntity(row, column);
//                Entity adjacentTileEntity = getEntityWithID(adjacentTileID);
//
//                // skip tiles off the map or being occupied or already visited
//                if (adjacentTileEntity == null) { continue; }
//                if (visitedTileIDs.contains(adjacentTileID)) { continue; }
//
//                // ensure the tile isn't obstructed and within jump or move
//                TileComponent adjacentTile = adjacentTileEntity.get(TileComponent.class);
//
//                // If building graph for movement, don't traverse over obstructed tiles
//                if (respectfully && adjacentTile.isNotNavigable()) { continue; }
//
//                tileIDsToVisit.add(adjacentTileID);
//                graphMap.put(adjacentTileID, currentTileID);
//                depthMap.put(adjacentTileID, depth + 1);
//            }
//        }
//
//        return graphMap;
//    }
//


//    public Map<Entity, Map<Entity, Entity>> createTileEntityGraph(String startTileID, int range, boolean respectfully) {
//        Map<String, Integer> depthMap = new HashMap<>();
//        depthMap.put(startTileID, 0);
//
//        Map<String, String> graphMap = new HashMap<>();
//        graphMap.put(startTileID, null);
//
//        Map<String, Map<String, String>> graph = new LinkedHashMap<>();
//        graph.put(startTileID, new HashMap<>());
//
//        Queue<String> tileIDsToVisit = new LinkedList<>();
//        tileIDsToVisit.add(startTileID);
//
//        Set<String> visitedTileIDs = new HashSet<>();
//
//        while (!tileIDsToVisit.isEmpty()) {
//            // get the tile and its depth
//            String currentTileID = tileIDsToVisit.poll();
//            if (currentTileID == null) { continue; }
//
//            Entity currentTileEntity = getEntityWithID(currentTileID);
//            TileComponent currentTile = currentTileEntity.get(TileComponent.class);
//            int depth = depthMap.get(currentTileID);
//
//            // check that we have not visited already and is within range
//            if (visitedTileIDs.contains(currentTileID)) { continue; }
//            visitedTileIDs.add(currentTileID);
//
//            // If building graph for movement, don't traverse over obstructed tiles
//            if (currentTileID.equalsIgnoreCase(startTileID) && (respectfully && currentTile.isNotNavigable())) { continue; }
//
//            // only go the specified range
//            if (range >= 0 && depth >= range) { continue; }
//
//            // go through each child tile and set connection
//            for (Direction direction : Direction.cardinal) {
//                int row = currentTile.getRow() + direction.y;
//                int column = currentTile.getColumn() + direction.x;
//                String adjacentTileID = tryFetchingTileEntity(row, column);
//                Entity adjacentTileEntity = getEntityWithID(adjacentTileID);
//
//                // skip tiles off the map or being occupied or already visited
//                if (adjacentTileEntity == null) { continue; }
//                if (visitedTileIDs.contains(adjacentTileID)) { continue; }
//
//                // ensure the tile isn't obstructed and within jump or move
//                TileComponent adjacentTile = adjacentTileEntity.get(TileComponent.class);
//
//                // If building graph for movement, don't traverse over obstructed tiles
//                if (respectfully && adjacentTile.isNotNavigable()) { continue; }
//
//                Map<String, String> connections = graph.computeIfAbsent(currentTileID, k -> new HashMap<>());
//                connections.put(adjacentTileID, adjacentTileID);
//
//                tileIDsToVisit.add(adjacentTileID);
//                graphMap.put(adjacentTileID, currentTileID);
//                depthMap.put(adjacentTileID, depth + 1);
//            }
//        }
//
//        return graph;
//    }
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
//        // Get the tile to place the entity on
//        String tileEntityID = tryFetchingTileEntity(row, column);
//        Entity tileEntity = EntityStore.getInstance().get(tileEntityID);
//        if (tileEntity == null) { return false; }
//        TileComponent tile = tileEntity.get(TileComponent.class);
//        if (tile.isNotNavigable()) { return false; }
//
//        IdentityComponent identityComponent = entity.get(IdentityComponent.class);
//        String unitID = identityComponent.getID();
//
//        tile.setUnit(unitID);
//
//        MovementComponent movementComponent = entity.get(MovementComponent.class);
//        movementComponent.stageTarget(tileEntityID);
//        movementComponent.commit();
        return true;
    }

//    public void placeByAxis(boolean horizontal, List<Entity> leftOrTopTeam, List<Entity> rightOrBottom, int spawnSize) {
//        // split the map into regions.This will look odd when the map cannot be devided evenly
//        // left most and right most
//        List<Entity> leftOrTopSpawns = new ArrayList<>();
//        List<Entity> rightOrBottomSpawns = new ArrayList<>();
//        for (int row = 0; row < mTileEntityMap.length; row++) {
//            for (int column = 0; column < mTileEntityMap[row].length; column++) {
//                // Left most team
//                Entity tileEntity = mTileEntityMap[row][column];
//                TileComponent tile = tileEntity.get(TileComponent.class);
//                if (horizontal) {
//                    if (column < spawnSize) {
//                        leftOrTopSpawns.add(tileEntity);
//                        tile.setSpawnRegion(1);
//                    } else if (column >= mTileEntityMap[row].length - spawnSize) {
//                        rightOrBottomSpawns.add(tileEntity);
//                        tile.setSpawnRegion(2);
//                    }
//                } else {
//                    if (row < spawnSize) {
//                        leftOrTopSpawns.add(tileEntity);
//                        tile.setSpawnRegion(1);
//                    } else if (row >= mTileEntityMap.length - spawnSize) {
//                        rightOrBottomSpawns.add(tileEntity);
//                        tile.setSpawnRegion(2);
//                    }
//                }
//            }
//        }
//    }
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
        Entity tileEntity = tryFetchingTileEntityRaw(row, column);
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

//    public List<List<List<Entity>>> tryCreatingRegions(int slicesPerAxis, boolean ignoreObstructed) {
//
//        List<List<List<Entity>>> regions = new ArrayList<>();
//
//        if (slicesPerAxis <= 0) { return regions; }
//
//        slicesPerAxis++;
//
//        int tileRowsPerRegion = mTileEntityMap.length / slicesPerAxis;
//        int leftoverRowValue = (mTileEntityMap.length % slicesPerAxis != 0 ? 1 : 0);
//
//        for (int row = 0; row < mTileEntityMap.length; row += tileRowsPerRegion + leftoverRowValue) {
//            List<List<Entity>> rowList = new ArrayList<>();
//            int tileColumnsPerRegion = mTileEntityMap[row].length / slicesPerAxis;
//            int leftoverColumnValue = (mTileEntityMap[row].length % slicesPerAxis != 0 ? 1 : 0);
//
//            for (int column = 0; column < mTileEntityMap[row].length; column += tileColumnsPerRegion + leftoverColumnValue) {
//                int regionEndRow = row + tileRowsPerRegion + leftoverRowValue;
//                int regionEndColumn = column + tileColumnsPerRegion + leftoverColumnValue;
//                List<Entity> area = new ArrayList<>();
//
//                for (int regionRow = row; regionRow < regionEndRow; regionRow++) {
//                    for (int regionColumn = column; regionColumn < regionEndColumn; regionColumn++) {
//                        Entity entity = tryFetchingTileEntityRaw(regionRow, regionColumn);
//                        if (entity == null) { continue; }
//                        TileComponent tile = entity.get(TileComponent.class);
//                        area.add(entity);
//                        if (ignoreObstructed && tile.isNotNavigable()) { continue; }
//                    }
//                }
//                // This means that the regions are way too small
//                if (area.isEmpty()) { return regions; }
//
//                rowList.add(area);
//            }
//            regions.add(rowList);
//        }
//
////        for (List<Entity> spawnRegion : regions) {
////
////        }
//        return regions;
//    }

    public int getRows() { return size(); }
    public int getColumns(int row) { return getJSONArray(row).size(); }
    public int getColumns() { return getRows() > 0 ? getColumns(0) : 0; }

    public boolean isOutOfBounds(int row, int column) {
        boolean isHorizontallyOutOfBounds = row < 0 || row >= size();
        if (isHorizontallyOutOfBounds) { return true; }
        boolean isVerticallyOutOfBounds = column < 0 || column >= getJSONArray(row).size();
        return isVerticallyOutOfBounds;
    }

    public void clear(String layer, int row, int column) {
//        mTileIDMap[row][column].get(TileComponent.class).clear(layer);
    }

    public void fill(String layer, Object filament) {
//        for (int row = 0; row < mTileIDMap.length; row++) {
//            for (int column = 0; column < mTileIDMap[row].length; column++) {
//                set(layer, row, column, filament);
//            }
//        }
    }

    public boolean isUsed(String layer, int row, int column) {
        Entity entity = tryFetchingTileEntityRaw(row, column);
        TileComponent tile = entity.get(TileComponent.class);
        return tile.get(layer) != null;
    }
    public boolean isNotUsed(String layer, int row, int column) {
        return !isUsed(layer, row, column);
    }

    public void set(String layer, int row, int column, Object value) {
//        Entity entity = mTileEntityMap[row][column];
//        TileComponent tile = entity.get(TileComponent.class);
//        tile.set(layer, value);
    }

    public Object get(String layer, int row, int column) {
        Entity entity = tryFetchingTileEntityRaw(row, column);
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

    public void printElevationGrid() {
        int rows = getRows();
        int columns = getColumns();

        // Find the max number of digits in elevation values
        int maxDigits = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                Entity entity = tryFetchingTileEntityRaw(row, col);
                if (entity == null) continue;
                int elevation = entity.get(TileComponent.class).getModifiedElevation();
                maxDigits = Math.max(maxDigits, String.valueOf(elevation).length());
            }
        }

        int cellWidth = maxDigits;
        String formatWithBrackets = "[%" + cellWidth + "s]";
        String formatWithoutBrackets = " %" + cellWidth + "s ";

        // === Column header ===
        System.out.print("    "); // Space for row labels
        for (int col = 0; col < columns; col++) {
            String colLabel = String.valueOf(col);
            System.out.printf(formatWithoutBrackets, colLabel);
        }
        System.out.println();

        // === Rows with elevation ===
        for (int row = 0; row < rows; row++) {
            System.out.printf("%3d ", row); // Row index
            for (int col = 0; col < columns; col++) {
                Entity entity = tryFetchingTileEntityRaw(row, col);
                String cellContent;
                if (entity == null) {
                    cellContent = "??";
                } else {
                    int elevation = entity.get(TileComponent.class).getModifiedElevation();
                    cellContent = String.valueOf(elevation);
                }
                System.out.printf(formatWithBrackets, cellContent);
            }
            System.out.println();
        }
    }


    public void printElevationGrid(List<String> entities) {
        int rows = getRows();
        int columns = getColumns();

        // Find the max number of digits in elevation values
        int maxDigits = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                Entity entity = tryFetchingTileEntityRaw(row, col);
                if (entity == null) continue;
                int elevation = entity.get(TileComponent.class).getModifiedElevation();
                maxDigits = Math.max(maxDigits, String.valueOf(elevation).length());
            }
        }

        int cellWidth = maxDigits;
        String formatWithBrackets = "[%" + cellWidth + "s]";
        String formatWithoutBrackets = " %" + cellWidth + "s ";

        // === Column header ===
        System.out.print("    "); // Space for row labels
        for (int col = 0; col < columns; col++) {
            String colLabel = String.valueOf(col);
            System.out.printf(formatWithoutBrackets, colLabel);
        }
        System.out.println();

        // === Rows with elevation ===
        for (int row = 0; row < rows; row++) {
            System.out.printf("%3d ", row); // Row index
            for (int col = 0; col < columns; col++) {
                Entity entity = tryFetchingTileEntityRaw(row, col);
                IdentityComponent identityComponent = entity.get(IdentityComponent.class);
                String cellContent;
                if (entity == null) {
                    cellContent = "??";
                } else if (entities.contains(identityComponent.getID())) {
                    cellContent = String.valueOf("");
                } else {
                    int elevation = entity.get(TileComponent.class).getTotalElevation();
                    cellContent = String.valueOf(elevation);
                }
                System.out.printf(formatWithBrackets, cellContent);
            }
            System.out.println();
        }
    }

//    public void printTileMapDebugView(String layer) {
//        System.out.println("TileMap Debug View:");
//        System.out.println("-------------------");
//
//        for (int row = 0; row < mTileEntityMap.length; row++) {
//            StringBuilder line = new StringBuilder();
//            for (int column = 0; column < mTileEntityMap[row].length; column++) {
//                Entity entity = tryFetchingTileEntityRaw(row, column);
//                if (entity == null) {
//                    line.append(" ?? ");
//                    continue;
//                }
//
//                TileComponent tile = entity.get(TileComponent.class);
//                Object value = tile.getTotalElevation();
//
//                String symbol;
//                if (value == null) {
//                    symbol = " . ";
//                } else if (value instanceof Number) {
//                    // You can replace this with elevation or any numeric layer
//                    symbol = String.format("%2d ", ((Number) value).intValue());
//                } else if (value instanceof String) {
//                    // Just use the first character for identifiers
//                    symbol = " " + ((String) value).charAt(0) + " ";
//                } else {
//                    symbol = " ? ";
//                }
//
//                line.append(symbol);
//            }
//            System.out.println(line);
//        }
//
//        System.out.println("-------------------");
//    }


    public List<String> getShortestPathAStar(String startTileID, String endTileID) {
        if (startTileID == null || endTileID == null) return Collections.emptyList();

        Entity startEntity = getEntityWithID(startTileID);
        Entity endEntity = getEntityWithID(endTileID);
        if (startEntity == null || endEntity == null) return Collections.emptyList();

        TileComponent startTile = startEntity.get(TileComponent.class);
        TileComponent endTile = endEntity.get(TileComponent.class);

        Map<String, Integer> gScore = new HashMap<>();
        Map<String, Integer> fScore = new HashMap<>();
        Map<String, String> cameFrom = new HashMap<>();
        Set<String> closedSet = new HashSet<>();

        Comparator<String> comparator = Comparator.comparingInt(fScore::get);
        PriorityQueue<String> openSet = new PriorityQueue<>(comparator);

        gScore.put(startTileID, 0);
        fScore.put(startTileID, heuristic(startTileID, endTileID));
        openSet.add(startTileID);

        while (!openSet.isEmpty()) {
            String current = openSet.poll();
            if (current.equals(endTileID)) {
                return reconstructPath(cameFrom, current);
            }

            closedSet.add(current);

            for (String neighborID : getNavigableNeighbors(current)) {
                if (closedSet.contains(neighborID)) continue;

                int tentativeG = gScore.getOrDefault(current, Integer.MAX_VALUE) + 1;
                if (tentativeG < gScore.getOrDefault(neighborID, Integer.MAX_VALUE)) {
                    cameFrom.put(neighborID, current);
                    gScore.put(neighborID, tentativeG);

                    Entity neighborEntity = getEntityWithID(neighborID);
                    TileComponent neighborTile = neighborEntity.get(TileComponent.class);
                    int f = tentativeG + heuristic(neighborID, endTileID);
                    fScore.put(neighborID, f);
                    if (!openSet.contains(neighborID)) {
                        openSet.add(neighborID);
                    }
                }
            }
        }

        return Collections.emptyList(); // No path found
    }

    private int heuristic(String startTileID, String endTileID) {
        Entity startEntity = getEntityWithID(startTileID);
        TileComponent startTile = startEntity.get(TileComponent.class);

        Entity endEntity = getEntityWithID(endTileID);
        TileComponent endTile = endEntity.get(TileComponent.class);

        return Math.abs(startTile.getRow() - endTile.getRow()) + Math.abs(startTile.getColumn() - endTile.getColumn());
    }

    private List<String> reconstructPath(Map<String, String> cameFrom, String current) {
        LinkedList<String> path = new LinkedList<>();
        path.addFirst(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.addFirst(current);
        }
        return path;
    }

    private List<String> getNavigableNeighbors(String tileID) {
        Entity entity = getEntityWithID(tileID);
        TileComponent tile = entity.get(TileComponent.class);
        int row = tile.getRow();
        int col = tile.getColumn();

        List<String> neighbors = new ArrayList<>();
        for (Direction direction : Direction.cardinal) {
            int neighborRow = row + direction.y;
            int neighborColumn = col + direction.x;
            String neighborID = tryFetchingTileID(neighborRow, neighborColumn);
            if (neighborID == null) { continue; }

            Entity neighbor = getEntityWithID(neighborID);
            if (neighbor == null) { continue; }

            TileComponent nTile = neighbor.get(TileComponent.class);
            if (nTile.isNotNavigable()) { continue; }
            neighbors.add(neighborID);
        }
        return neighbors;
    }
}
