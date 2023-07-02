package game.map.generators;

import game.map.TileMap;
import game.map.generators.validation.SchemaConfigs;
import game.map.generators.validation.SchemaMapValidation;
import logging.Logger;
import logging.LoggerFactory;

import java.util.SplittableRandom;

public class OpenMapWithBorderGenerator extends TileMapGenerator {
    private final SplittableRandom random = new SplittableRandom();
    private final Logger logger = LoggerFactory.instance().logger(getClass());

    @Override
    public TileMap build(SchemaConfigs mapConfigs) {
        logger.log("Constructing {0}", getClass());

        while (!isPathMapCompletelyConnecting) {

            createSchemaMaps(mapConfigs);

            tilePathMap.fill(1);

            createWallForMap(tilePathMap);

            placeLiquidsSafely(tileHeightMap, tileLiquidMap, tilePathMap, mapConfigs, tileSeaLevelMap);

            placeStructuresSafely(tilePathMap, tileStructureMap, tileLiquidMap, mapConfigs);
            
            isPathMapCompletelyConnecting = SchemaMapValidation.isValidPath(tilePathMap);

            System.out.println(tilePathMap.debug(false));
            System.out.println(tilePathMap.debug(true));
            if (isPathMapCompletelyConnecting) {

                System.out.println(tilePathMap.debug(false));
                System.out.println(tilePathMap.debug(true));
            }
        }

        mapPathMapToTerrainMap(tilePathMap, tileTerrainMap, mapConfigs);

//        if (mapConfigs.structure > 0) {
//            placeStructuresSafely(pathMap, structureMap, liquidMap, mapConfigs);
//        }

        return createTileMap(tilePathMap, tileHeightMap, tileTerrainMap, tileLiquidMap, tileStructureMap);
    }
}
