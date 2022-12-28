package game.map.generators;

import game.map.generators.validation.SchemaMap;
import game.map.TileMap;
import game.map.generators.validation.TileMapGeneratorValidation;
import logging.Logger;
import logging.LoggerFactory;

import java.awt.Point;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

public class OutdoorSquareRoomsGenerator extends TileMapGenerator {
    private final SplittableRandom random = new SplittableRandom();
    private final Logger logger = LoggerFactory.instance().logger(getClass());

    @Override
    public TileMap build(int mapRows, int mapColumns, int mapFlooring, int mapWalling) {
        logger.log("Constructing {0}", getClass());

        while (!isCompletelyConnected) {

            pathMap = new SchemaMap(mapRows, mapColumns);
            structureMap = new SchemaMap(mapRows, mapColumns);
            terrainMap = new SchemaMap(mapRows, mapColumns);

            pathMap.fill(1);

            List<Set<Point>> rooms = tryCreatingRooms(pathMap, true);

            isCompletelyConnected = TileMapGeneratorValidation.isValid(pathMap);

            System.out.println(pathMap.debug(false));
            System.out.println(pathMap.debug(true));
            if (isCompletelyConnected) {
                System.out.println(pathMap.debug(false));
                System.out.println(pathMap.debug(true));
            }
        }

        developTerrainMapFromPathMap(pathMap, terrainMap, mapFlooring, mapWalling);
        return createTileMap(pathMap, heightMap, terrainMap, specialMap, structureMap);
    }
}
