package game.map.generators;

import game.map.TileMap;
import game.map.generators.validation.SchemaConfigs;
import game.map.generators.validation.SchemaMapValidation;
import logging.ELogger;
import logging.ELoggerFactory;

import java.awt.Point;
import java.util.List;
import java.util.Set;

public class BorderedMapWithBorderedRoomsGenerator extends TileMapGenerator {

    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    @Override
    public TileMap build(SchemaConfigs mapConfigs) {
        logger.info("Constructing {0}", getClass());

        while (!isPathMapCompletelyConnecting) {

            createSchemaMaps(mapConfigs);

            // tilePathMap.fill(1);
            pathMap.fill(mapConfigs.getFloor());

            List<Set<Point>> rooms = tryCreatingRooms(pathMap, true);

            Set<Point> mapOutline = createWallForMap(pathMap);

            isPathMapCompletelyConnecting = SchemaMapValidation.isValidPath(pathMap);

            // System.out.println(tilePathMap.debug(false));
            // System.out.println(tilePathMap.debug(true));
            if (isPathMapCompletelyConnecting) {
                // System.out.println(tilePathMap.debug(false));
                // System.out.println(tilePathMap.debug(true));
            }
        }

        mapPathMapToTerrainMap(pathMap, floorMap, mapConfigs);

        placeLiquidsSafely(heightMap, liquidMap, pathMap, mapConfigs, liquidLevel);
        
//        if (mapConfigs.structure > 0) {
//            placeStructuresSafely(pathMap, structureMap, liquidMap, mapConfigs);
//        }

        return createTileMap(pathMap, heightMap, floorMap, liquidMap, wallMap);
    }
}
