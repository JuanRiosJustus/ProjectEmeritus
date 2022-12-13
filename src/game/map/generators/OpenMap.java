package game.map.generators;

import game.map.MapBuilder;
import game.map.SchemaMap;
import game.map.TileMap;
import game.stores.pools.AssetPool;
import logging.Logger;
import logging.LoggerFactory;

import java.awt.Point;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

public class OpenMap extends MapBuilder {
    private final SplittableRandom random = new SplittableRandom();
    private final Logger logger = LoggerFactory.instance().logger(getClass());
    @Override
    public TileMap build(int rows, int columns) {
        logger.log("Constructing {0}", getClass());

        int baseImageIndex = random.nextInt(AssetPool.instance().tileSprites() - 1);
        while (baseImageIndex % 2 != 0 || baseImageIndex == 0) {
            baseImageIndex = random.nextInt(AssetPool.instance().tileSprites() - 1);
        }

        // TODO, remove 0 index
        int baseWaterIndex = random.nextInt(AssetPool.instance().liquidSprites() - 1);
        while (baseWaterIndex % 2 != 0 || baseWaterIndex == 0) {
            baseWaterIndex = random.nextInt(AssetPool.instance().tileSprites() - 1);
        }

        while (!isCompletelyConnected) {
            floorMap = new SchemaMap(rows, columns);
            structureMap = new SchemaMap(rows, columns);
            List<Set<Point>> rooms = tryCreatingRooms(floorMap);
            List<Set<Point>> halls = tryConnectingRooms(floorMap, rooms);

            isCompletelyConnected =  isCompletelyConnected(floorMap, rooms);
            if (isCompletelyConnected) {
                System.out.println(floorMap.debug(false));
                System.out.println(floorMap.debug(true));
//                floodFillRoom(primaryPathMap, secondaryPathMap, mapRooms, 1, 4);
            }
        }

        return new TileMap(encode(floorMap, structureMap, baseImageIndex));
    }
}
