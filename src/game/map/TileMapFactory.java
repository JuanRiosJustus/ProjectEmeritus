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
            case 5 -> generator = new OpenMapWithBorderGenerator();
            default -> generator = new OpenMapGenerator();
        }

        return generator.build(mapConfigs);
    }
}
