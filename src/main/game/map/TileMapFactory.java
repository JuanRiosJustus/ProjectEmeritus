package main.game.map;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.Jsoner;
import main.game.map.base.TileMap;
import main.game.stores.factories.TileFactory;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class TileMapFactory {

    public static final String ROWS = "rows", COLUMNS = "columns", FLOOR = "floor", WALL = "wall",
            LIQUID = "liquid", SEED = "seed", ZOOM = "zoom", ALGORITHM = "algorithm", OBSTRUCTIONS = "obstructions";
    public static Map<String, Object> sanitize(Map<String, Object> settings) {
        Map<String, Object> configMap = new HashMap<>(settings);
        configMap.put(ROWS, settings.getOrDefault(ROWS, -1));
        configMap.put(COLUMNS, settings.getOrDefault(COLUMNS, -1));
        configMap.put(FLOOR, settings.getOrDefault(FLOOR, -1));
        configMap.put(WALL, settings.getOrDefault(WALL, -1));
        configMap.put(LIQUID, settings.getOrDefault(LIQUID, -1));
        configMap.put(SEED, settings.getOrDefault(SEED, random.nextLong()));
        configMap.put(ZOOM, settings.getOrDefault(ZOOM, .75f));
        configMap.put(ALGORITHM, settings.getOrDefault(ALGORITHM, null));
        configMap.put(OBSTRUCTIONS, settings.getOrDefault(OBSTRUCTIONS, null));
        return configMap;
    }

    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(TileFactory.class);
    private static final SplittableRandom random = new SplittableRandom();
    public enum Algorithm {
        BasicOpenMap, BorderedOpenMapWithBorderedRooms, LargeBorderedRooms, LargeContinuousRoom,
        NoBorderWithSmallRooms, HauberkDungeonMap
    }
    public static class TileMapFactoryConfigs {
        public TileMapFactory.Algorithm algorithm;
        public int rows = -1;
        public int columns = -1;
        public int floor = -1;
        public int wall = -1;
        public int greaterStructure = -1;
        public int lesserStructure = -1;
        public float zoom = .75f;
        public long seed = random.nextLong();
        public int liquid = -1;
    }

//    public static TileMap create(TileMapFactoryConfigs configs) {
//        TileMapBuilder builder = null;
//
//        switch (configs.algorithm) {
//            case BasicOpenMap -> builder = new BasicOpenMap();
//            case BorderedOpenMapWithBorderedRooms -> builder = new BorderedMapWithBorderedRooms();
//            case LargeBorderedRooms -> builder = new LargeBorderedRoom();
//            case LargeContinuousRoom -> builder = new LargeContinuousRoom();
//            case NoBorderWithSmallRooms -> builder = new NoBorderWithSmallRooms();
//            case HauberkDungeonMap -> builder = new HauberkDungeonMap();
//        }
//
//        return builder.setRowAndColumn(configs.rows, configs.columns)
//                .setFloor(configs.floor)
//                .setWall(configs.wall)
//                .setLiquid(configs.liquid)
//                .setGreaterStructure(configs.greaterStructure)
//                .setLesserStructure(configs.lesserStructure)
//                .setZoom(configs.zoom)
//                .setSeed(random.nextLong())
//                .build();
//    }

//    public static TileMap create(Map<String, Object> configuration) {
//        configuration = sanitize(configuration);
//
//        Algorithm algorithm = Algorithm.valueOf((String) configuration.get(ALGORITHM));
//        TileMapBuilder builder = null;
//        switch (algorithm) {
//            case BasicOpenMap -> builder = new BasicOpenMap(configuration);
//            case BorderedOpenMapWithBorderedRooms -> builder = new BorderedMapWithBorderedRooms(configuration);
//            case LargeBorderedRooms -> builder = new LargeBorderedRoom(configuration);
//            case LargeContinuousRoom -> builder = new LargeContinuousRoom(configuration);
//            case NoBorderWithSmallRooms -> builder = new NoBorderWithSmallRooms(configuration);
//            case HauberkDungeonMap -> builder = new HauberkDungeonMap(configuration);
//        }
//
//        return builder.build();
//    }


//    public static TileMap random(int rows, int columns) {
//
//        SpriteMap spriteMap = AssetPool.getInstance().getSpriteMap(Constants.TILES_SPRITEMAP_FILEPATH);
//
//        List<String> list = spriteMap.getKeysEndingWith("wall");
//        int wall = spriteMap.indexOf(list.get(random.nextInt(list.size())));
//
//        list = spriteMap.getKeysEndingWith("floor");
//        int floor = spriteMap.indexOf(list.get(random.nextInt(list.size())));
//
//        list = spriteMap.getKeysEndingWith(Tile.GREATER_STRUCTURE);
//        int greaterStructure = spriteMap.indexOf(list.get(random.nextInt(list.size())));
//
//        list = spriteMap.getKeysEndingWith(Tile.LESSER_STRUCTURE);
//        int lesserStructure = spriteMap.indexOf(list.get(random.nextInt(list.size())));
//
//        list = spriteMap.getKeysEndingWith(Tile.LIQUID);
//        int liquid = spriteMap.indexOf(list.get(random.nextInt(list.size())));
//
////        Map<String, Integer> obstructionMap = new HashMap<>();
//
//        TileMapFactoryConfigs configs = new TileMapFactoryConfigs();
//        configs.algorithm = Algorithm.BasicOpenMap;
//        configs.rows = rows;
//        configs.columns = columns;
//        configs.wall = wall;
//        configs.floor = floor;
//        configs.greaterStructure = greaterStructure;
//        configs.lesserStructure = lesserStructure;
//        configs.liquid = liquid;
//
//        return create(configs);
//    }

    public static void save(TileMap map) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("HH-mm");
            String fileName = LocalDate.now() + "-" + formatter.format(new Date()) + ".json";
            PrintWriter out = new PrintWriter(new FileWriter(fileName, false), true);
//            out.write(map.asJson().toJson());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static TileMap load(String path) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(path));
            JsonArray jsonArray = (JsonArray) Jsoner.deserialize(reader);

//            TileMap map = new TileMap(jsonArray);

            logger.info("Finished deserializing tilemap");
//            return map;
            return null;
//            return TileMapBuilder.createTileMap(pathMap, heightMap, terrainMap, liquidMap, structureMap);
        } catch (Exception ex) {
            logger.info("Unable to deserialize Json for tilemap " + ex.getMessage());
        }
        return null;
    }
//    public static TileMap load(String path) {
//        ELogger logger = ELoggerFactory.getInstance().getELogger(TileMapBuilder.class);
//        try {
//            Reader reader = Files.newBufferedReader(Paths.get(path));
//            JsonObject jsonObject = (JsonObject) Jsoner.deserialize(reader);
//            JsonArray tilemap = (JsonArray) jsonObject.get("data");
//
//            JsonArray jsonArrayRow = (JsonArray) tilemap.get(0);
//            TileMapLayer pathMap = new TileMapLayer(tilemap.size(), jsonArrayRow.size());
//            TileMapLayer heightMap = new TileMapLayer(tilemap.size(), jsonArrayRow.size());
//            TileMapLayer terrainMap = new TileMapLayer(tilemap.size(), jsonArrayRow.size());
//            TileMapLayer liquidMap = new TileMapLayer(tilemap.size(), jsonArrayRow.size());
//            TileMapLayer structureMap = new TileMapLayer(tilemap.size(), jsonArrayRow.size());
//
//            Entity[][] raw = new Entity[tilemap.size()][];
//            for (int row = 0; row < tilemap.size(); row++) {
//                jsonArrayRow = (JsonArray) tilemap.get(row);
//                raw[row] = new Entity[jsonArrayRow.size()];
//                for (int column = 0; column < jsonArrayRow.size(); column++) {
//                    JsonArray tileJson = (JsonArray) jsonArrayRow.get(column);
//                    Entity entity = TileFactory.create(row, column);
//                    raw[row][column] = entity;
//                    Tile tile = entity.get(Tile.class);
//                    int[] encoding = new int[tileJson.size()];
//                    for (int i = 0; i < encoding.length; i++) {encoding[i] = tileJson.getInteger(i); }
//                    tile.encode(encoding);
//                    pathMap.set(row, column, tile.getPath());
//                    heightMap.set(row, column, tile.getHeight());
//                    terrainMap.set(row, column, tile.getTerrain());
//                    liquidMap.set(row, column, tile.getLiquid());
//                    structureMap.set(row, column, tile.getStructure());
//                }
//            }
//            logger.info("Finished deserializing tilemap");
//            return null;
////            return TileMapBuilder.createTileMap(pathMap, heightMap, terrainMap, liquidMap, structureMap);
//        } catch (Exception ex) {
//            logger.info("Unable to deserialize Json for tilemap");
//        }
//        return null;
//    }
}
