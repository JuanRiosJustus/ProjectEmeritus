package game.map.generators;

import game.map.TileMap;
import game.map.generators.validation.SchemaConfigs;
import game.map.generators.validation.SchemaMapValidation;
import logging.Logger;
import logging.LoggerFactory;

import java.awt.Point;
import java.util.List;
import java.util.Set;

public class BorderedMapWithBorderedRoomsGenerator extends TileMapGenerator {

    private final Logger logger = LoggerFactory.instance().logger(getClass());

    @Override
    public TileMap build(SchemaConfigs mapConfigs) {
        logger.log("Constructing {0}", getClass());

        while (!isCompletelyConnected) {

            initialize(mapConfigs);

            pathMap.fill(1);

            List<Set<Point>> rooms = tryCreatingRooms(pathMap, true);

            Set<Point> mapOutline = createWallForMap(pathMap);

            isCompletelyConnected = SchemaMapValidation.isValidPath(pathMap);

            System.out.println(pathMap.debug(false));
            System.out.println(pathMap.debug(true));
            if (isCompletelyConnected) {
                System.out.println(pathMap.debug(false));
                System.out.println(pathMap.debug(true));
            }
        }

        mapPathMapToTerrainMap(pathMap, terrainMap, mapConfigs);

        placeLiquidsSafely(heightMap, liquidMap, pathMap, mapConfigs, seaLevel);
        
//        if (mapConfigs.structure > 0) {
//            placeStructuresSafely(pathMap, structureMap, liquidMap, mapConfigs);
//        }

        return createTileMap(pathMap, heightMap, terrainMap, liquidMap, structureMap);
    }
}
