package game.map.generators;

import game.map.generators.validation.SchemaConfigs;
import game.map.TileMap;
import game.map.generators.validation.SchemaMapValidation;
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
    public TileMap build(SchemaConfigs mapConfigs) {
        logger.log("Constructing {0}", getClass());

        while (!isCompletelyConnected) {

            init(mapConfigs);

            pathMap.fill(1);

            List<Set<Point>> rooms = tryCreatingRooms(pathMap, true);

            isCompletelyConnected = SchemaMapValidation.isValidPath(pathMap);

            System.out.println(pathMap.debug(false));
            System.out.println(pathMap.debug(true));
            if (isCompletelyConnected) {
                System.out.println(pathMap.debug(false));
                System.out.println(pathMap.debug(true));
            }
        }

        developTerrainMapFromPathMap(pathMap, terrainMap, mapConfigs);

        if (mapConfigs.getSpecial() > 0) {
            placeSpecialSafely(heightMap, specialMap, pathMap, mapConfigs);
        }

        if (mapConfigs.getStructure() > 0) {
            placeStructuresSafely(pathMap, structureMap, specialMap, mapConfigs);
        }

        return createTileMap(pathMap, heightMap, terrainMap, specialMap, structureMap);
    }
}
