package game.map;

import game.map.generators.*;
import game.map.generators.validation.SchemaConfigs;

public class TileMapFactory {

    public static TileMap create(SchemaConfigs mapConfigs) {

        TileMapGenerator generator;

        switch (mapConfigs.getType()) {
            case 1 -> generator = new HauberkDungeonGenerator();
            case 2 -> generator = new IndoorSquareRoomsGenerator();
            case 3 -> generator = new OutdoorSquareRoomsGenerator();
            case 4 -> generator = new BorderedMapWithBorderedRoomsGenerator();
            default -> generator = new OpenMapGenerator();
        }

        return generator.build(mapConfigs);
    }

//    public static TileMap create(Class<? extends TileMapGenerator> type, int mapRows, int mapColumns, int mapFlooring, int mapWalling) {
//
//        TileMapGenerator generator;
//
//        switch (type) {
//            case HauberkDungeonGenerator.class -> generator = new HauberkDungeonGenerator();
//            case IndoorSquareRoomsGenerator.class -> generator = new IndoorSquareRoomsGenerator();
//            case OutdoorSquareRoomsGenerator.class -> generator = new OutdoorSquareRoomsGenerator();
//            case BorderedMapWithBorderedRoomsGenerator.class -> generator = new BorderedMapWithBorderedRoomsGenerator();
//            default -> generator = new OpenMapGenerator();
//        }
//
//        return generator.build(mapRows, mapColumns, mapFlooring, mapWalling);
//    }
}
