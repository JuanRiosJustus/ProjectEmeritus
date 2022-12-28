package game.map.generators;


import game.map.SchemaMap;
import game.map.TileMap;
import logging.Logger;
import logging.LoggerFactory;

import java.awt.Point;
import java.util.*;

public class RandomSquareRoomsGenerator extends TileMapGenerator {
    private final SplittableRandom random = new SplittableRandom();
    private final Logger logger = LoggerFactory.instance().logger(getClass());

    @Override
    public TileMap build(int rows, int columns, int mapFlooring, int mapWalling) {
        logger.log("Constructing {0}", getClass());

        while (!isCompletelyConnected) {

            terrainMap = new SchemaMap(rows, columns);
            structureMap = new SchemaMap(rows, columns);

            List<Set<Point>> rooms = tryCreatingRooms(terrainMap);
            List<Set<Point>> halls = tryConnectingRooms(terrainMap, rooms);

            tryConnectingRooms(terrainMap, rooms);

            isCompletelyConnected =  PathMapValidation.isValid(terrainMap);

            if (isCompletelyConnected) {
                System.out.println(terrainMap.debug(false));
                System.out.println(terrainMap.debug(true));
//                floodFillRoom(primaryPathMap, secondaryPathMap, mapRooms, 1, 4);
            }
        }

        return createTileMap(terrainMap, structureMap, mapFlooring, mapWalling);
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
