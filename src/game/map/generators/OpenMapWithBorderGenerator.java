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

        while (!isCompletelyConnected) {

            initialize(mapConfigs);

            pathMap.fill(1);

            createWallForMap(pathMap);

            if (mapConfigs.liquid > 0) { placeLiquidLevel(heightMap, liquidMap, pathMap, mapConfigs, seaLevel); }

            if (mapConfigs.structure > 0) { placeStructuresSafely(pathMap, structureMap, liquidMap, mapConfigs); }

            isCompletelyConnected = SchemaMapValidation.isValidPath(pathMap);

            System.out.println(pathMap.debug(false));
            System.out.println(pathMap.debug(true));
            if (isCompletelyConnected) {

                System.out.println(pathMap.debug(false));
                System.out.println(pathMap.debug(true));
            }
        }

        mapPathMapToTerrainMap(pathMap, terrainMap, mapConfigs);

//        if (mapConfigs.structure > 0) {
//            placeStructuresSafely(pathMap, structureMap, liquidMap, mapConfigs);
//        }

        return createTileMap(pathMap, heightMap, terrainMap, liquidMap, structureMap);
    }
}
