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
    public TileMap build(SchemaConfigs mapConfigs) {
        logger.info("Constructing {0}", getClass());

        while (!isPathMapCompletelyConnecting) {
            createSchemaMaps(mapConfigs);

            tilePathMap.fill(mapConfigs.flooring);

            placeLiquidsSafely(tileHeightMap, tileLiquidMap, tilePathMap, mapConfigs, tileSeaLevelMap);

//            if (mapConfigs.getWalling() > 0) { placeWallingSafely(pathMap); }
            if (mapConfigs.walling > 0) { tryCreatingRooms(tilePathMap, true); }

            placeStructuresSafely(tilePathMap, tileStructureMap, tileLiquidMap, mapConfigs);

            isPathMapCompletelyConnecting = SchemaMapValidation.isValidPath(tilePathMap);

            if (isPathMapCompletelyConnecting) {
                mapPathMapToTerrainMap(tilePathMap, tileTerrainMap, mapConfigs);
                // System.out.println(tilePathMap.debug(false));
                // System.out.println(tilePathMap.debug(true));
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

        return createTileMap(tilePathMap, tileHeightMap, tileTerrainMap, tileLiquidMap, tileStructureMap);
    }
}
