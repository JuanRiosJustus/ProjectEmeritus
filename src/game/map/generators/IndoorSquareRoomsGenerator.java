package game.map.generators;


import game.map.TileMap;
import game.map.generators.validation.SchemaConfigs;
import game.map.generators.validation.SchemaMapValidation;
import logging.Logger;
import logging.LoggerFactory;

import java.awt.Point;
import java.util.*;

public class IndoorSquareRoomsGenerator extends TileMapGenerator {
    private final SplittableRandom random = new SplittableRandom();
    private final Logger logger = LoggerFactory.instance().logger(getClass());

    @Override
    public TileMap build(SchemaConfigs mapConfigs) {
        logger.log("Constructing {0}", getClass());

        while (!isCompletelyConnected) {
            initialize(mapConfigs);

            List<Set<Point>> rooms = tryCreatingRooms(pathMap, false);
            List<Set<Point>> halls = tryConnectingRooms(pathMap, rooms);

            tryConnectingRooms(pathMap, rooms);

            isCompletelyConnected =  SchemaMapValidation.isValidPath(pathMap);

            if (isCompletelyConnected) {
                System.out.println(pathMap.debug(false));
                System.out.println(pathMap.debug(true));
            }
        }

        mapPathMapToTerrainMap(pathMap, terrainMap, mapConfigs);

        if (mapConfigs.liquid > 0) {
            placeLiquidLevel(heightMap, liquidMap, pathMap, mapConfigs, seaLevel);
        }

//        if (mapConfigs.structure > 0) {
//            placeStructuresSafely(pathMap, structureMap, liquidMap, mapConfigs);
//        }

        return createTileMap(pathMap, heightMap, terrainMap, liquidMap, structureMap);
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
