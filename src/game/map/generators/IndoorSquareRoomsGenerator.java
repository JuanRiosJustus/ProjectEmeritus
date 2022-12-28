package game.map.generators;


import game.map.TileMap;
import game.map.generators.validation.TileMapGeneratorValidation;
import logging.Logger;
import logging.LoggerFactory;

import java.awt.Point;
import java.util.*;

public class IndoorSquareRoomsGenerator extends TileMapGenerator {
    private final SplittableRandom random = new SplittableRandom();
    private final Logger logger = LoggerFactory.instance().logger(getClass());

    @Override
    public TileMap build(int mapRows, int mapColumns, int mapFlooring, int mapWalling) {
        logger.log("Constructing {0}", getClass());

        while (!isCompletelyConnected) {
            createSchemaMaps(mapRows, mapColumns, mapFlooring, mapWalling);

            List<Set<Point>> rooms = tryCreatingRooms(pathMap, false);
            List<Set<Point>> halls = tryConnectingRooms(pathMap, rooms);

            tryConnectingRooms(pathMap, rooms);

            isCompletelyConnected =  TileMapGeneratorValidation.isValid(pathMap);

            if (isCompletelyConnected) {
                System.out.println(pathMap.debug(false));
                System.out.println(pathMap.debug(true));
            }
        }

        developTerrainMapFromPathMap(pathMap, terrainMap, mapFlooring, mapWalling);
        return createTileMap(pathMap, heightMap, terrainMap, specialMap, structureMap);
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
