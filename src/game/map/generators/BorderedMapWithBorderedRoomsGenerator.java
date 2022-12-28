package game.map.generators;

import game.map.TileMap;
import game.map.generators.validation.TileMapGeneratorValidation;
import logging.Logger;
import logging.LoggerFactory;

import java.awt.Point;
import java.util.List;
import java.util.Set;

public class BorderedMapWithBorderedRoomsGenerator extends TileMapGenerator {

    private final Logger logger = LoggerFactory.instance().logger(getClass());

    @Override
    public TileMap build(int mapRows, int mapColumns, int mapFlooring, int mapWalling) {
        logger.log("Constructing {0}", getClass());

        while (!isCompletelyConnected) {

            createSchemaMaps(mapRows, mapColumns, mapFlooring, mapWalling);

            pathMap.fill(1);

            List<Set<Point>> rooms = tryCreatingRooms(pathMap, true);

            Set<Point> mapOutline = createWallForMap(pathMap);

            isCompletelyConnected = TileMapGeneratorValidation.isValid(pathMap);

            System.out.println(pathMap.debug(false));
            System.out.println(pathMap.debug(true));
            if (isCompletelyConnected) {
                System.out.println(pathMap.debug(false));
                System.out.println(pathMap.debug(true));
            }
        }

        developTerrainMapFromPathMap(pathMap, terrainMap, mapFlooring, mapWalling);

        floodLowestHeight(heightMap, specialMap, pathMap);

        return createTileMap(pathMap, heightMap, terrainMap, specialMap, structureMap);
    }
}
