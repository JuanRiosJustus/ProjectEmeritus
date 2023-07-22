package game.map.generators;

import game.map.TileMap;
import game.map.generators.validation.SchemaConfigs;
import game.map.generators.validation.SchemaMapValidation;
import logging.ELogger;
import logging.ELoggerFactory;

import java.util.SplittableRandom;

public class OpenMapGenerator extends TileMapGenerator {
    private final SplittableRandom random = new SplittableRandom();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    @Override
    public TileMap build(SchemaConfigs configs) {
        logger.info("Constructing {0}", getClass());

        while (!isPathMapCompletelyConnecting) {
            createSchemaMaps(configs);

            pathMap.fill(configs.getFloor());

            placeLiquidsSafely(heightMap, liquidMap, pathMap, configs, liquidLevel);

//            if (mapConfigs.getWalling() > 0) { placeWallingSafely(pathMap); }
            if (configs.getWall()> 0) { tryCreatingRooms(pathMap, true); }

            placeStructuresSafely(pathMap, wallMap, liquidMap, configs);

            isPathMapCompletelyConnecting = SchemaMapValidation.isValidPath(pathMap);

            if (isPathMapCompletelyConnecting) {
                // mapPathMapToTerrainMap(pathMap, floorMap, mapConfigs);
            }
        }

//        mapPathMapToTerrainMap(pathMap, terrainMap, mapConfigs);

//        if (mapConfigs.getLiquid() > 0) {
//            placeLiquidSafely(heightMap, liquidMap, pathMap, mapConfigs);
//        }
//
//        if (mapConfigs.getStructure() > 0) {
//            placeStructuresSafely(pathMap, structureMap, liquidMap, mapConfigs);
//        }

        return createTileMap(pathMap, heightMap, liquidMap, wallMap, configs);
    }
}
