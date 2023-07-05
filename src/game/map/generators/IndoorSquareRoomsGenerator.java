package game.map.generators;


import game.map.TileMap;
import game.map.generators.validation.SchemaConfigs;
import game.map.generators.validation.SchemaMapValidation;
import logging.ELogger;
import logging.ELoggerFactory;

import java.awt.Point;
import java.util.*;

public class IndoorSquareRoomsGenerator extends TileMapGenerator {
    private final SplittableRandom random = new SplittableRandom();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    @Override
    public TileMap build(SchemaConfigs mapConfigs) {
        logger.info("Constructing {0}", getClass());

        while (!isPathMapCompletelyConnecting) {
            createSchemaMaps(mapConfigs);

            List<Set<Point>> rooms = tryCreatingRooms(tilePathMap, false);
            List<Set<Point>> halls = tryConnectingRooms(tilePathMap, rooms);

            tryConnectingRooms(tilePathMap, rooms);

            isPathMapCompletelyConnecting =  SchemaMapValidation.isValidPath(tilePathMap);

            if (isPathMapCompletelyConnecting) {
                System.out.println(tilePathMap.debug(false));
                System.out.println(tilePathMap.debug(true));
            }
        }

        mapPathMapToTerrainMap(tilePathMap, tileTerrainMap, mapConfigs);

        placeLiquidsSafely(tileHeightMap, tileLiquidMap, tilePathMap, mapConfigs, tileSeaLevelMap);

//        if (mapConfigs.structure > 0) {
//            placeStructuresSafely(pathMap, structureMap, liquidMap, mapConfigs);
//        }

        return createTileMap(tilePathMap, tileHeightMap, tileTerrainMap, tileLiquidMap, tileStructureMap);
    }

    private void placeWaterwayRandom(int[][] waterMap) {
        for (int row = 1; row < waterMap.length - 1; row++) {
            waterMap[row - 1][waterMap[0].length / 2] = 2;
            waterMap[row][waterMap[0].length / 2] = 2;
            waterMap[row + 1][waterMap[0].length / 2] = 2;
        }
        System.err.println("Placed");
    }
}
