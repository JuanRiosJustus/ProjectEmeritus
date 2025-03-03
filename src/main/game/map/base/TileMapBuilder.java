package main.game.map.base;

public class TileMapBuilder {
//
//    public static final String ROWS = "rows", COLUMNS = "columns", FLOOR = "floor", WALL = "wall",
//            LIQUID = Tile.LIQUID, CURRENT_SEED = "current_seed", PREVIOUS_SEED = "previous_seed",
//            ZOOM = "zoom", ALGORITHM = "algorithm", STRUCTURES = "structures",
//            ENTRANCE_STRUCTURE = "entrance_structure", EXIT_STRUCTURE = "exit_structure";
//    private static final Random mRandom = new Random();
//
//    protected static final ELogger mLogger = ELogger.create(TileMapBuilder.class);
//
//    public static Map<String, TileMapAlgorithm> getTileMapBuilderMapping() {
//        Map<String, TileMapAlgorithm> operationsMap = new LinkedHashMap<>();
//        operationsMap.put("HauberkDungeonMap", new HauberkDungeonMap());
//        operationsMap.put("BorderedMapWithBorderedRooms", new BorderedMapWithBorderedRooms());
//        operationsMap.put("LargeContinuousRoom", new LargeContinuousRoom());
//        return operationsMap;
//    }
//
//    static TileMap createRandom(int rows, int columns) {
//
//        SpriteSheet spriteSheet = AssetPool.getInstance().getSpriteMap(Constants.TILES_SPRITEMAP_FILEPATH);
//        Random random = new Random();
//
//        List<String> list = spriteSheet.contains(TileMapBuilder.WALL);
//        String wall = list.get(random.nextInt(list.size()));
//
//        list = spriteSheet.contains(TileMapBuilder.FLOOR);
//        String floor = list.get(random.nextInt(list.size()));
//
//        list = spriteSheet.contains(TileMapBuilder.LIQUID);
//        String liquid = list.get(random.nextInt(list.size()));
//
//        Map<String, Object> configuration = new HashMap<>();
//
//        configuration.put(ALGORITHM, "");
//        configuration.put(ROWS, rows);
//        configuration.put(COLUMNS, columns);
//        configuration.put(WALL, wall);
//        configuration.put(FLOOR, floor);
//        configuration.put(LIQUID, liquid);
//        if (random.nextBoolean()) { configuration.remove(LIQUID); }
//        int randomBounds = 5;
////        randomBounds = randomBounds % 10;
////        configuration.put(MAX_HEIGHT, randomBounds);
////        configuration.put(MIN_HEIGHT, randomBounds * -1);
//
//        return create(configuration);
//    }
//
//    static TileMap create(Map<String, Object> configuration) {
//        sanitize(configuration);
//
//        TileMap newTileMap = new TileMap(configuration);
//
//        String value = (String) configuration.getOrDefault(ALGORITHM, "");
//
////        TileMapAlgorithm operation = getTileMapBuilderMapping().get(value);
//        TileMapAlgorithm operation = null; //getTileMapBuilderMapping().get(value);
//
////        new LargeContinuousRoom().execute(newTileMap);
//        if (operation == null) {
//            new BorderedMapWithBorderedRooms().execute(newTileMap);
//        } else {
////            operation.execute(newTileMap);
//        }
//
//        TileMapAlgorithm.tryPlacingObstruction(newTileMap);
////        Algorithm algorithm = Algorithm.valueOf((String) configuration.get(ALGORITHM));
////        TileMapBuilder builder = null;
////        switch (algorithm) {
//////            case BasicOpenMap -> builder = new BasicOpenMap(configuration);
////            case BorderedOpenMapWithBorderedRooms -> builder = new BorderedMapWithBorderedRooms(configuration);
//////            case LargeBorderedRooms -> builder = new LargeBorderedRoom(configuration);
//////            case LargeContinuousRoom -> builder = new LargeContinuousRoom(configuration);
//////            case NoBorderWithSmallRooms -> builder = new NoBorderWithSmallRooms(configuration);
//////            case HauberkDungeonMap -> builder = new HauberkDungeonMap(configuration);
////        }
//
//        return newTileMap;
//    }
//
//    static void sanitize(Map<String, Object> configuration) {
//        configuration.put(ROWS, configuration.getOrDefault(ROWS, -1));
//        configuration.put(COLUMNS, configuration.getOrDefault(COLUMNS, -1));
//
//        SpriteSheet spriteSheet = AssetPool.getInstance().getSpriteMap(Constants.TILES_SPRITEMAP_FILEPATH);
//        Random random = new Random();
//
//        List<String> list = spriteSheet.contains(TileMapBuilder.WALL);
//        String wall = list.get(random.nextInt(list.size()));
//
//        list = spriteSheet.contains(TileMapBuilder.FLOOR);
//        String floor = list.get(random.nextInt(list.size()));
//
//        list = spriteSheet.contains(TileMapBuilder.LIQUID);
//        String liquid = list.get(random.nextInt(list.size()));
//
//        configuration.put(FLOOR, configuration.getOrDefault(FLOOR, floor));
//        configuration.put(WALL, configuration.getOrDefault(WALL, wall));
//        configuration.put(LIQUID, configuration.getOrDefault(LIQUID, liquid));
//
//        configuration.put(CURRENT_SEED, configuration.getOrDefault(CURRENT_SEED, new Random().nextLong()));
//        configuration.put(ZOOM, configuration.getOrDefault(ZOOM, .75f));
////        configuration.put(MIN_HEIGHT, configuration.getOrDefault(MIN_HEIGHT, 0));
////        configuration.put(MAX_HEIGHT, configuration.getOrDefault(MAX_HEIGHT, 10));
//        configuration.put(ALGORITHM, configuration.getOrDefault(ALGORITHM, ""));
//        configuration.put(STRUCTURES, configuration.getOrDefault(STRUCTURES, null));
//    }
//
////    static void placeTerrain(TileMap tileMap) {
////        TileMapLayer colliderMap = tileMap.getColliderLayer();
////        TileMapLayer terrainMap = tileMap.getTerrainLayer();
////        String wall = (String) tileMap.getConfiguration(WALL);
////        String floor = (String) tileMap.getConfiguration(FLOOR);
////
////        for (int row = 0; row < colliderMap.getRows(); row++) {
////            for (int column = 0; column < colliderMap.getColumns(row); column++) {
////                if (colliderMap.isUsed(row, column)) {
////                    terrainMap.set(row, column, wall);
////                } else {
////                    terrainMap.set(row, column, floor);
////                }
////            }
////        }
////    }
////
////    static void placeLiquids(TileMap tileMap) {
////
////        TileMapLayer heightMap = tileMap.getHeightLayer();
////        TileMapLayer liquidMap = tileMap.getLiquidLayer();
////        TileMapLayer colliderMap = tileMap.getColliderLayer();
////
////        String liquidType = tileMap.getLiquid();
////        int seaLevel = tileMap.getWaterLevel();
////
////        // Don't place liquids if config is not set
////        if (liquidType == null) { return; }
////        // Find the lowest height in the height map to flood
////        Queue<Point> toVisit = new LinkedList<>();
////
////        for (int row = 0; row < heightMap.getRows(); row++) {
////            for (int column = 0; column < heightMap.getColumns(row); column++) {
////
////                // Path must be usable/walkable
////                if (colliderMap.isUsed(row, column)) { continue; }
////
////                int currentHeight = Integer.parseInt(heightMap.get(row, column));
////                if (currentHeight > seaLevel) { continue; }
////
////                toVisit.add(new Point(column, row));
////            }
////        }
////
////        // Fill in the height map at that area with BFS
////        Set<Point> visited = new HashSet<>();
////
////        while (!toVisit.isEmpty()) {
////
////            Point current = toVisit.poll();
////
////            if (visited.contains(current)) { continue; }
////            if (heightMap.isOutOfBounds(current.y, current.x)) { continue; }
////            if (colliderMap.isUsed(current.y, current.x)) { continue; }
////
////            visited.add(current);
////            liquidMap.set(current.y, current.x, liquidType);
////
////            for (Direction direction : Direction.cardinal) {
////                int nextRow = current.y + direction.y;
////                int nextColumn = current.x + direction.x;
////                // Only visit tiles that are pats and the tile is lower or equal height to current
////                if (colliderMap.isOutOfBounds(nextRow, nextColumn)) { continue; }
////                if (colliderMap.isUsed(nextRow, nextColumn)) { continue; }
////                int nextHeight = Integer.parseInt(heightMap.get(nextRow, nextColumn));
////                int currentHeight = Integer.parseInt(heightMap.get(current.y, current.x));
////                if (nextHeight > currentHeight) { continue; }
////                toVisit.add(new Point(nextColumn, nextRow));
////            }
////        }
////    }
//
//    static void placeShadows(TileMap tileMap) {
//        return;
//
////        Entity[][] map = tileMap.getRawTileMap();
////
////        // Go through each tile, first pass
////        for (int row = 0; row < map.length; row++) {
////            for (int column = 0; column < map[row].length; column++) {
////                // get current height
////                Entity entity = map[row][column];
////                Tile tile = entity.get(Tile.class);
////                tile.reset();
////            }
////        }
////
////        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
////        // Go through each tile, first pass
////        for (int row = 0; row < map.length; row++) {
////            for (int column = 0; column < map[row].length; column++) {
////                // get current height
////                Entity entity = map[row][column];
////                Tile tile = entity.get(Tile.class);
////                int height = tile.getHeight();
////                min = Math.min(min, height);
////                max = Math.max(max, height);
////                tryPlacingDirectionalShadows(tileMap, tile, row, column);
////            }
////        }
////
////        tryPlacingDepthShadows(map, min, max);
//    }
//
////    private static void tryPlacingDepthShadows(Entity[][] map, int min, int max) {
////        // map tile heights with shadows
////        for (int row = 0; row < map.length; row++) {
////            for (int column = 0; column < map[row].length; column++) {
////                // get current height
////                Entity entity = map[row][column];
////                Tile tile = entity.get(Tile.class);
////                int height = tile.getHeight();
////                int mapped = (int) MathUtils.map(height, min, max, 5, 0); // lights depth is first
////                String id = AssetPool.getInstance()
////                        .createAsset(AssetPool.MISC_SPRITEMAP, Tile.DEPTH_SHADOWS, mapped, AssetPool.STATIC_ANIMATION);
////                tile.putAsset(Tile.DEPTH_SHADOWS, id);
//////                tile.put(Tile.DEPTH_SHADOWS, mapped);
//////                tile.putProperty(Tile.DEPTH_SHADOWS, mapped);
////            }
////        }
////    }
////
////    private static void tryPlacingDirectionalShadows(TileMap tileMap, Tile currentTile, int row, int column) {
////        if (row == 0 || column == 0 || tileMap.getRows() - 1 == row || tileMap.getColumns(row) - 1 == column) {
////            return;
////        }
////
////        if (currentTile.isWall()) { return; }
////
////        List<String> assetIds = new ArrayList<>();
//////        List<String> directionalShadows = new ArrayList<>();
////
////        // Check all the tiles in all directions
////        for (Direction direction : Direction.values()) {
////
////            int nextRow = row + direction.y;
////            int nextColumn = column + direction.x;
////
////            Entity adjacentEntity = tileMap.tryFetchingTileAt(nextRow, nextColumn);
////            if (adjacentEntity == null) { continue; }
////            Tile adjacentTile = adjacentEntity.get(Tile.class);
////
////            // If the adjacent tile is higher, add a shadow in that direction
////            if (adjacentTile.getHeight() <= currentTile.getHeight() && adjacentTile.isPath()) { continue; }
////            // Enhanced liquid visuals where shadows not showing on them
////            if (adjacentTile.getLiquid() != null) { continue; }
////
////            int index = direction.ordinal();
////
////
////            // TODO this is showing under walls, find a way to remove it
////            String id = AssetPool.getInstance()
////                    .createAsset(AssetPool.MISC_SPRITEMAP, Tile.CARDINAL_SHADOW, index, AssetPool.STATIC_ANIMATION);
////
////            if (row == 1 && column > 1) {
//////                continue;
////            }
////            assetIds.add(id);
//////            currentTile.put(Tile.CARDINAL_SHADOW + " " + direction.name(), id);
//////            directionalShadows.add(direction.name());
////
////        }
////
////        String id = AssetPool.getInstance().mergeAssets(assetIds);
////        currentTile.putAsset(Tile.CARDINAL_SHADOW, id);
////        currentTile.putAsset(SHADOW_COUNT, String.valueOf(assetIds.size()));
////    }
////
//    public static final String SHADOW_COUNT = "SHADOW_COUNT";
}
