package game.map.generators;

import game.map.TileMap;
import game.map.generators.validation.SchemaConfigs;
import game.map.generators.validation.SchemaMapValidation;
import logging.ELogger;
import logging.ELoggerFactory;

import java.util.SplittableRandom;

public class OpenMapWithBorderGenerator extends TileMapGenerator {
    private final SplittableRandom random = new SplittableRandom();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    @Override
    public TileMap build(SchemaConfigs mapConfigs) {
        logger.info("Constructing {0}", getClass());

        while (!isPathMapCompletelyConnecting) {

            createSchemaMaps(mapConfigs);

            // tilePathMap.fill(1);
            pathMap.fill(mapConfigs.getFloor());

            createWallForMap(pathMap);

            placeLiquidsSafely(heightMap, liquidMap, pathMap, mapConfigs, liquidLevel);

            placeStructuresSafely(pathMap, wallMap, liquidMap, mapConfigs);
            
            isPathMapCompletelyConnecting = SchemaMapValidation.isValidPath(pathMap);

            // System.out.println(tilePathMap.debug(false));
            // System.out.println(tilePathMap.debug(true));
            if (isPathMapCompletelyConnecting) {

                // System.out.println(tilePathMap.debug(false));
                // System.out.println(tilePathMap.debug(true));
            }
        }

        mapPathMapToTerrainMap(pathMap, floorMap, mapConfigs);

//        if (mapConfigs.structure > 0) {
//            placeStructuresSafely(pathMap, structureMap, liquidMap, mapConfigs);
//        }

        return createTileMap(pathMap, heightMap, floorMap, liquidMap, wallMap);
    }
}
