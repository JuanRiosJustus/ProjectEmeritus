package game.map.generators;

import game.map.SchemaMap;
import game.map.TileMap;
import logging.Logger;
import logging.LoggerFactory;

import java.awt.Point;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

public class BorderedMapWithRoomsGenerator extends TileMapGenerator {

    private final SplittableRandom random = new SplittableRandom();
    private final Logger logger = LoggerFactory.instance().logger(getClass());

    @Override
    public TileMap build(int mapRows, int mapColumns, int mapFlooring, int mapWalling) {
        logger.log("Constructing {0}", getClass());

        while (!isCompletelyConnected) {

            terrainMap = new SchemaMap(mapRows, mapColumns);
            structureMap = new SchemaMap(mapRows, mapColumns);
            specialMap = new SchemaMap(mapRows, mapColumns);

            terrainMap.fill(1);

            List<Set<Point>> rooms = tryCreatingRooms(terrainMap, true);

            Set<Point> mapOutline = createWallForMap(terrainMap);

            isCompletelyConnected = PathMapValidation.isValid(terrainMap);

            System.out.println(terrainMap.debug(false));
            System.out.println(terrainMap.debug(true));
            if (isCompletelyConnected) {
                System.out.println(terrainMap.debug(false));
                System.out.println(terrainMap.debug(true));
            }
        }

        return createTileMap(terrainMap, structureMap, mapFlooring, mapWalling);
    }
}
